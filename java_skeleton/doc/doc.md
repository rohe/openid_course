# Java Cookbook for OpenID Connect Public Client

This document describes how to implement an OpenID Connect (OIDC) Public Client
using this library, [Nimbus OAuth 2.0 SDK with OpenID Connect extensions](https://bitbucket.org/connect2id/oauth-2.0-sdk-with-openid-connect-extensions).
Full javadoc can be found [here](http://www.javadoc.io/doc/com.nimbusds/oauth2-oidc-sdk/), and
for the accompanying JOSE library [Nimbus JOSE + JWT](http://www.javadoc.io/doc/com.nimbusds/nimbus-jose-jwt/).

The basic authentication flow in OpenID Connect consists of the following steps:

  1. Optional: OpenID Provider Issuer Discovery using WebFinger
  1. Optional: Obtaining OpenID Provider Configuration Information
  1. Optional: Dynamic client registration
  1. Authentication (using one of the defined flows)
  1. Optional: Token request
  1. Optional: UserInfo request


## Issuer discovery
The WebFinger protocol is used to find the OpenID Provider (OP). The library
does not have any out-of-the box support for WebFinger, so in the following
example we assume you already have acquired the issuer url of the OP (possibly
from developer documentation).


## Provider configuration information
Obtaining the provider configuration information can be done either out-of-band
or using the optional [discovery process](http://openid.net/specs/openid-connect-discovery-1_0.html#ProviderConfig):

```java
URI issuerURI = new URI("http://provider.example.com/");
URL providerConfigurationURL = issuerURI.resolve("/.well-known/openid-configuration").toURL();
InputStream stream = providerConfigurationURL.openStream();
// Read all data from URL
String providerInfo = null;
try (java.util.Scanner s = new java.util.Scanner(stream)) {
  providerInfo = s.useDelimiter("\\A").hasNext() ? s.next() : "";
}
OIDCProviderMetadata providerMetadata = OIDCProviderMetadata.parse(providerInfo);
```

## Client registration
If the provider supports dynamic registration, a new client can be registered
using the [client registration](http://openid.net/specs/openid-connect-registration-1_0.html#RegistrationRequest) process:

```java
String jsonMetadata = "{\"application_type\": \"web\",\"redirect_uris\": [\"http://client.example.com/auth_callback\"],\"response_types\": [\"code\"]}";
OIDCClientMetadata metadata = OIDCClientMetadata.parse(JSONObjectUtils.parse(jsonMetadata));

// Make registration request
OIDCClientRegistrationRequest registrationRequest = new OIDCClientRegistrationRequest(providerMetadata.getRegistrationEndpointURI(), metadata, null);
HTTPResponse regHTTPResponse = registrationRequest.toHTTPRequest().send();

// Parse and check response
ClientRegistrationResponse registrationResponse = OIDCClientRegistrationResponseParser.parse(regHTTPResponse);

if (registrationResponse instanceof ClientRegistrationErrorResponse) {
  ErrorObject error = ((ClientRegistrationErrorResponse) registrationResponse)
  .getErrorObject();
  // TODO error handling
}

// Store client information from OP
OIDCClientInformation clientInformation = ((OIDCClientInformationResponse)registrationResponse).getOIDCClientInformation();
```


## Authentication request
The authentication request is done by redirecting the end user to the provider,
for more details see the [OIDC specification](http://openid.net/specs/openid-connect-core-1_0.html#AuthRequest).
The redirect URL is built as follows, using the Authorization Code Flow:

```java
// Generate random state string for pairing the response to the request
State state = new State();
// Generate nonce
Nonce nonce = new Nonce();
// Specify scope
Scope scope = Scope.parse("openid");

// Compose the request
AuthenticationRequest authenticationRequest = new AuthenticationRequest(
  providerMetadata.getAuthorizationEndpointURI(),
  new ResponseType(ResponseType.Value.CODE),
  scope, clientInformation.getID(), redirectURI, state, nonce);

  URI authReqURI = authenticationRequest.toURI();
  return authReqURI;
```

Note:
  * If the provider does not support the discovery protocol, replace ``providerMetadata.getAuthorizationEndpointURI()`` with the authorization endpoint URL received out-of-band.
  * If the provider does not support dynamic client registration, replace ``clientInformation.getID()`` with the client id received out-of-band.
  * Make sure ``redirectURI`` matches a URI known by the provider.
  * The ``state`` and ``nonce`` should be stored so they can be retrieved later.
  * If you want to specify additional parameters in the authentication request, use 
    [``AuthenticationRequest.Builder``](http://static.javadoc.io/com.nimbusds/oauth2-oidc-sdk/4.15/com/nimbusds/openid/connect/sdk/AuthenticationRequest.Builder.html).

### Receive the Authentication Response
The authentication response is sent from the provider by redirecting the end
user to the redirect URI specified in the initial authentication request from the client.

#### Code flow
The Authorization Code can be extracted from the query part of the redirect URL:
```java
AuthenticationResponse authResp = null;
try {
  authResp = AuthenticationResponseParser.parse(new URI(requestURL));
} catch (ParseException | URISyntaxException e) {
  // TODO error handling
}

if (authResp instanceof AuthenticationErrorResponse) {
  ErrorObject error = ((AuthenticationErrorResponse) authResp)
  .getErrorObject();
  // TODO error handling
}

AuthenticationSuccessResponse successResponse = (AuthenticationSuccessResponse) authResp;

/* Don't forget to check the state!
 * The state in the received authentication response must match the state
 * specified in the previous outgoing authentication request.
*/
if (!verifyState(successResponse.getState())) {
  // TODO proper error handling
}

AuthorizationCode authCode = successResponse.getAuthorizationCode();
```

#### Implicit/Hybrid flow
When using either implicit or hybrid flow the authentication response is encoded
in the fragment part of the URL. This requires additional handling, e.g.
using Javascript, see [Implementation Notes](http://openid.net/specs/openid-connect-core-1_0.html#FragmentNotes).

After receiving the response back in the client, it can be parsed in the same way as when using
Code flow.

## Token Request

When an authorization code (using code or hybrid flow) has been obtained, a
[token request](http://openid.net/specs/openid-connect-core-1_0.html#TokenRequest)
can made to get the access token and the id token:

```java
TokenRequest tokenReq = new TokenRequest(
  providerMetadata.getTokenEndpointURI(),
  new ClientSecretBasic(clientInformation.getID(),
						clientInformation.getSecret()),
  new AuthorizationCodeGrant(authCode, redirectURI));

HTTPResponse tokenHTTPResp = null;
try {
  tokenHTTPResp = tokenReq.toHTTPRequest().send();
} catch (SerializeException | IOException e) {
  // TODO proper error handling
}

// Parse and check response
TokenResponse tokenResponse = null;
try {
  tokenResponse = OIDCTokenResponseParser.parse(tokenHTTPResp);
} catch (ParseException e) {
  // TODO proper error handling
}

if (tokenResponse instanceof TokenErrorResponse) {
  ErrorObject error = ((TokenErrorResponse) tokenResponse).getErrorObject();
  // TODO error handling
}

OIDCAccessTokenResponse accessTokenResponse = (OIDCAccessTokenResponse) tokenResponse;
accessTokenResponse.getAccessToken();
accessTokenResponse.getIDToken();
```

### Validate the ID token

The id token obtained from the token request must be validated, see [ID token validation](http://openid.net/specs/openid-connect-core-1_0.html#IDTokenValidation):

```java
private ReadOnlyJWTClaimsSet verifyIdToken(JWT idToken, OIDCProviderMetadata providerMetadata) {
  RSAPublicKey providerKey = null;
  try {
    JSONObject key = getProviderRSAJWK(providerMetadata.getJWKSetURI().toURL().openStream());
    providerKey = RSAKey.parse(key).toRSAPublicKey();
  } catch (NoSuchAlgorithmException | InvalidKeySpecException
  | IOException | java.text.ParseException e) {
    // TODO error handling
  }

  DefaultJWTDecoder jwtDecoder = new DefaultJWTDecoder();
  jwtDecoder.addJWSVerifier(new RSASSAVerifier(providerKey));
  ReadOnlyJWTClaimsSet claims = null;
  try {
    claims = jwtDecoder.decodeJWT(idToken);
  } catch (JOSEException | java.text.ParseException e) {
    // TODO error handling
  }

  return claims;
}

private JSONObject getProviderRSAJWK(InputStream is) {
  // Read all data from stream
  StringBuilder sb = new StringBuilder();
  try (Scanner scanner = new Scanner(is);) {
    while (scanner.hasNext()) {
      sb.append(scanner.next());
    }
  }

  // Parse the data as json
  String jsonString = sb.toString();
  JSONObject json = JSONObjectUtils.parse(jsonString);

  // Find the RSA signing key
  JSONArray keyList = (JSONArray) json.get("keys");
  for (Object key : keyList) {
    JSONObject k = (JSONObject) key;
    if (k.get("use").equals("sig") && k.get("kty").equals("RSA")) {
      return k;
    }
  }
  return null;
}
```

## UserInfo Request

Using the access token, information about the end user can be obtained by making
a [user info request](http://openid.net/specs/openid-connect-core-1_0.html#UserInfoRequest):

```java
UserInfoRequest userInfoReq = new UserInfoRequest(
  providerMetadata.getUserInfoEndpointURI(),
  (BearerAccessToken) accessToken);

HTTPResponse userInfoHTTPResp = null;
try {
  userInfoHTTPResp = userInfoReq.toHTTPRequest().send();
} catch (SerializeException | IOException e) {
  // TODO proper error handling
}

UserInfoResponse userInfoResponse = null;
try {
  userInfoResponse = UserInfoResponse.parse(userInfoHTTPResp);
} catch (ParseException e) {
  // TODO proper error handling
}

if (userInfoResponse instanceof UserInfoErrorResponse) {
  ErrorObject error = ((UserInfoErrorResponse) userInfoResponse).getErrorObject();
  // TODO error handling
}

UserInfoSuccessResponse successResponse = (UserInfoSuccessResponse) userInfoResponse;
JSONObject claims = successResponse.getUserInfo().toJSONObject();
```
