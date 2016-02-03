package oidc_rp;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import spark.Request;
import spark.Response;
import spark.Session;

import com.nimbusds.jwt.ReadOnlyJWTClaimsSet;
import com.nimbusds.oauth2.sdk.AuthorizationCode;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.SerializeException;
import com.nimbusds.oauth2.sdk.token.AccessToken;
import com.nimbusds.oauth2.sdk.util.JSONObjectUtils;
import com.nimbusds.openid.connect.sdk.UserInfoSuccessResponse;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import com.nimbusds.openid.connect.sdk.rp.OIDCClientInformation;
import com.nimbusds.openid.connect.sdk.rp.OIDCClientMetadata;

public class Client {
	// TODO specify the correct path
	public static Path ROOT_PATH = Paths.get(".");
	// TODO specify the correct URL
	public static String ISSUER = "https://example.com";

	private OIDCClientInformation clientInformation;
	private OIDCProviderMetadata providerMetadata;

	public Client(String clientMetadataString)
			throws ParseException, URISyntaxException, IOException,
			SerializeException {
		OIDCClientMetadata clientMetadata = OIDCClientMetadata
				.parse(JSONObjectUtils.parse(clientMetadataString));
	}

	public String authenticate(Request req, Response res)
			throws URISyntaxException, SerializeException {
		// session object that can be used to store state between requests
		Session session = req.session();

		// TODO make authentication request

		String login_url = null; // TODO insert the redirect URL

		res.redirect(login_url);
		return null;
	}

	public String codeFlowCallback(Request req, Response res)
			throws IOException {
		// Callback redirect URI
		String url = req.url() + "?" + req.raw().getQueryString();

		// TODO parse authentication response from url
		// TODO validate the 'state' parameter

		// TODO make token request
		// TODO validate the ID Token according to the OpenID Connect spec (sec
		// 3.1.3.7.)
		// TODO make userinfo request

		// TODO set the appropriate values
		AuthorizationCode authCode = null;
		AccessToken accessToken = null;
		String parsedIdToken = null;
		ReadOnlyJWTClaimsSet idTokenClaims = null;
		UserInfoSuccessResponse userInfoResponse = null;

		return WebServer.successPage(authCode, accessToken, parsedIdToken,
				idTokenClaims, userInfoResponse);
	}

	public String implicitFlowCallback(Request req, Response res)
			throws IOException {
		// Callback redirect URI
		String url = req.url() + "#" + req.queryParams("url_fragment");

		// TODO parse authentication response from url
		// TODO validate the 'state' parameter

		// TODO validate the ID Token according to the OpenID Connect spec (sec
		// 3.2.2.11.)

		// TODO set the appropriate values
		AuthorizationCode authCode = null;
		AccessToken accessToken = null;
		String parsedIdToken = null;
		ReadOnlyJWTClaimsSet idTokenClaims = null;

		return WebServer.successPage(authCode, accessToken, parsedIdToken,
				idTokenClaims, null);
	}
}
