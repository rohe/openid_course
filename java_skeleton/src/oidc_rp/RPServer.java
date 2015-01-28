package oidc_rp;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.nimbusds.jwt.ReadOnlyJWTClaimsSet;
import com.nimbusds.oauth2.sdk.AuthorizationCode;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.SerializeException;
import com.nimbusds.oauth2.sdk.http.CommonContentTypes;
import com.nimbusds.oauth2.sdk.token.AccessToken;
import com.nimbusds.oauth2.sdk.util.JSONObjectUtils;
import com.nimbusds.openid.connect.sdk.UserInfoSuccessResponse;
import com.nimbusds.openid.connect.sdk.rp.OIDCClientMetadata;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.ServerRunner;

/**
 * Skeleton code for building an OpenID Connect Public Client.
 *
 * Using the nanohttpd library (https://github.com/NanoHttpd/nanohttpd) as the
 * webserver, and Nimbus OAauth
 * (http://connect2id.com/products/nimbus-oauth-openid-connect-sdk) for OpenID
 * Connect support.
 *
 * @author Rebecka Gulliksson, rebecka.gulliksson@umu.se
 *
 */
public class RPServer extends NanoHTTPD {
	/**
	 * Which port (on localhost) the RP listens to for the redirect URI.
	 */
	public static int SERVER_PORT = 8090;

	/**
	 * Logger instance.
	 */
	private static Logger logger = Logger.getLogger(RPServer.class.getName());

	private URI issuerURI;
	private Path fileDir;

	/**
	 * Constructor for the RP server.
	 *
	 * Loads the client metadata from file.
	 *
	 * @throws IOException
	 * @throws URISyntaxException
	 * @throws ParseException
	 * @throws SerializeException
	 */
	public RPServer() throws IOException, ParseException, URISyntaxException,
			SerializeException {
		super(SERVER_PORT);
		fileDir = Paths.get("."); // TODO specify the correct path

		issuerURI = new URI("https://dirg.org.umu.se:8092");

		String jsonMetadata = readFromFile("client.json");
		OIDCClientMetadata metadata = OIDCClientMetadata.parse(JSONObjectUtils
				.parseJSONObject(jsonMetadata));

		// TODO get the provider configuration information
		// TODO register with the provider using the jsonMetadata
	}

	/**
	 * Main method to start running the web server.
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		ServerRunner.run(RPServer.class);
	}

	/**
	 * Callback for the web server when receiving a request. Currently has
	 * support for the following paths:
	 *
	 * '/': displays the main page
	 *
	 * '/implicit_flow_callback': where the authentication response from the
	 * provider is received when using implicit or hybrid flow
	 * 
	 * '/authenticate': starts authentication using the OpenID Connect code flow
	 * 
	 * '/code_flow_callback': where the authentication response from the
	 * provider is received when using code flow
	 * 
	 * '/repost_fragment': where the fragment identifier is received after being
	 * parsed by the client (using Javascript)
	 * 
	 * @param session
	 *            the incoming request
	 *
	 * @return the response to the request.
	 */
	@Override
	public Response serve(IHTTPSession session) {
		UserInfoSuccessResponse userInfoClaims;
		if (session.getUri().equals("/")) {
			return loadPageFromFile("index.html");
		} else if (session.getUri().endsWith("implicit_flow_callback")) {
			return loadPageFromFile("repost_fragment.html");
		} else if (session.getUri().endsWith("authenticate")) {
			// TODO make authentication request

			String login_url = null;
			return redirect(login_url); // TODO insert the redirect URL
		} else if (session.getUri().endsWith("code_flow_callback")) {
			// Callback redirect URI
			String url = session.getUri() + "?"
					+ session.getQueryParameterString();

			// TODO parse authentication response from url
			// TODO make token request
			// TODO verify the id token
			// TODO make userinfo request

			// TODO set the appropriate values
			AuthorizationCode authCode = null;
			AccessToken accessToken = null;
			String parsedIdToken = null;
			ReadOnlyJWTClaimsSet idTokenClaims = null;
			UserInfoSuccessResponse userInfoResponse = null;
			return successPage(authCode, accessToken, parsedIdToken,
					idTokenClaims, userInfoResponse);
		} else if (session.getUri().endsWith("repost_fragment")) {
			try {
				// Read POST data
				session.parseBody(new HashMap<String, String>());
				Map<String, String> postParams = session.getParms();

				// Callback redirect URI
				String url = session.getUri() + "#"
						+ postParams.get("url_fragment");

				// TODO parse authentication response from url

				// TODO set the appropriate values
				AccessToken accessToken = null;
				String parsedIdToken = null;
				ReadOnlyJWTClaimsSet idTokenClaims = null;
				return successPage(null, accessToken, parsedIdToken,
						idTokenClaims, null);
			} catch (IOException | ResponseException e) {
				// TODO proper error handling
			}
		}

		return notFound();
	}

	/**
	 * Build HTML summary of a successful authentication.
	 *
	 * @param authCode
	 *            authorization code obtained from authentication response
	 * @param tokenResponse
	 *            response to the token request
	 * @param idTokenClaims
	 *            claims from the id token
	 * @param userInfoResponse
	 *            response to the user info request
	 * @return response containing HTML formatted summary.
	 */
	private Response successPage(AuthorizationCode authCode,
			AccessToken accessToken, String idToken,
			ReadOnlyJWTClaimsSet idTokenClaims,
			UserInfoSuccessResponse userInfoResponse) {

		try {
			StringBuilder idTokenString = new StringBuilder();
			idTokenString.append(idTokenClaims.toJSONObject().toJSONString());
			idTokenString.append("\n");
			idTokenString.append(idToken);

			StringBuilder userInfoString = new StringBuilder();
			if (userInfoResponse != null) {
				userInfoString.append(userInfoResponse.getUserInfo()
						.toJSONObject().toJSONString());
				if (userInfoResponse.getContentType().equals(
						CommonContentTypes.APPLICATION_JWT)) {
					userInfoString.append("\n");
					userInfoString.append(userInfoResponse.getUserInfoJWT()
							.getParsedString());
				}
			}
			String successPage = readFromFile("success_page.html");
			return new Response(MessageFormat.format(successPage, authCode,
					accessToken, idTokenString, userInfoString));
		} catch (IOException e) {
			logger.severe("Could not read success page from file: " + e);
			return new Response(Response.Status.NOT_FOUND, MIME_PLAINTEXT,
					"Page not found.");
		}
	}

	/**
	 * Read all data from a file.
	 *
	 * @param path
	 *            path of the file
	 * @return All data from the file.
	 * @throws IOException
	 */
	private String readFromFile(String path) throws IOException {
		return new String(Files.readAllBytes(fileDir.resolve(Paths.get(path))),
				StandardCharsets.UTF_8);
	}

	/**
	 * Load a page from file.
	 *
	 * @return response containing the formatted HTML page.
	 */
	private Response loadPageFromFile(String file) {
		try {
			String index = readFromFile(file);
			return new Response(index);
		} catch (IOException e) {
			logger.severe("Could not read index page from file: " + e);
			return new Response(Response.Status.NOT_FOUND, MIME_PLAINTEXT,
					"Page not found.");
		}
	}

	/**
	 * Build 404 Not Found response.
	 *
	 * @return response with HTTP status code 404.
	 */
	private Response notFound() {
		return new Response(Response.Status.NOT_FOUND, MIME_PLAINTEXT,
				"Page not found.");
	}

	/**
	 * Build 301 Redirect response.
	 *
	 * @param redirectURL
	 *            url to redirect to
	 * @return response with HTTP status 301 Redirect.
	 */
	private Response redirect(String redirectURL) {
		Response response = new Response(Response.Status.REDIRECT,
				MIME_PLAINTEXT, "");
		response.addHeader("Location", redirectURL);

		return response;
	}
}
