package oidc_rp;

import static spark.Spark.exception;
import static spark.Spark.get;
import static spark.Spark.post;
import static spark.SparkBase.port;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.logging.Logger;

import spark.Request;
import spark.Response;
import spark.Session;

import com.nimbusds.jwt.ReadOnlyJWTClaimsSet;
import com.nimbusds.oauth2.sdk.AuthorizationCode;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.SerializeException;
import com.nimbusds.oauth2.sdk.http.CommonContentTypes;
import com.nimbusds.oauth2.sdk.token.AccessToken;
import com.nimbusds.oauth2.sdk.util.JSONObjectUtils;
import com.nimbusds.openid.connect.sdk.UserInfoSuccessResponse;
import com.nimbusds.openid.connect.sdk.rp.OIDCClientMetadata;

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
public class RPServer {
	/**
	 * Which port (on localhost) the RP listens to for the redirect URI.
	 */
	public static int SERVER_PORT = 8090;

	/**
	 * Logger instance.
	 */
	private static Logger logger = Logger.getLogger(RPServer.class.getName());

	// TODO specify the correct path
	private static Path FILE_DIR = Paths.get(".");

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
	public static void main(String[] args) throws ParseException, IOException,
			URISyntaxException {
		connectToProvider("https://dirg.org.umu.se:8092");

		/* Spark webserver setup */

		port(SERVER_PORT);

		/* Spark webserver routes */

		/* displays the main page */
		get("/", (req, res) -> loadPageFromFile("index.html"));

		/*
		 * where the authentication response from the provider is received when
		 * using implicit or hybrid flow
		 */
		get("/implicit_flow_callback",
				(req, res) -> loadPageFromFile("repost_fragment.html"));

		/*
		 * starts authentication using the OpenID Connect code flow
		 */
		get("/authenticate", RPServer::authenticate);

		/*
		 * where the authentication response from the provider is received when
		 * using code flow
		 */
		get("/code_flow_callback", RPServer::codeFlowCallback);

		/*
		 * where the fragment identifier is received after being parsed by the
		 * client (using Javascript)
		 */
		post("/repost_fragment", RPServer::repostFragment);

		/* default handling if a file a requested file can not be found */
		exception(IOException.class, (e, request, response) -> {
			response.status(404);
			response.body("Resource not found: " + e);
		});
	}

	private static void connectToProvider(String issuerURI) throws IOException,
			ParseException, URISyntaxException {
		String jsonMetadata = readFromFile("client.json");
		OIDCClientMetadata metadata = OIDCClientMetadata.parse(JSONObjectUtils
				.parse(jsonMetadata));

		URI issuer = new URI(issuerURI);

		// TODO get the provider configuration information
		// TODO register with the provider using the jsonMetadata
	}

	private static String authenticate(Request req, Response res) {
		// session object that can be used to store store state between requests
		Session session = req.session();
		
		// TODO make authentication request

		String login_url = null; // TODO insert the redirect URL
		res.redirect(login_url); // Redirect the user to the provider
		return null;
	}

	private static String codeFlowCallback(Request req, Response res)
			throws IOException {
		// Callback redirect URI
		String url = req.url(); // TODO Rebecka: does this contain query params

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
		return successPage(authCode, accessToken, parsedIdToken, idTokenClaims,
				userInfoResponse);
	}

	private static String repostFragment(Request req, Response res)
			throws IOException {
		// TODO Rebecka: req.body(); vs. req.attributes(); for POST body

		// Callback redirect URI
		String url = req.url() + "#" + req.attribute("url_fragment");

		// TODO parse authentication response from url

		// TODO set the appropriate values
		AccessToken accessToken = null;
		String parsedIdToken = null;
		ReadOnlyJWTClaimsSet idTokenClaims = null;
		return successPage(null, accessToken, parsedIdToken, idTokenClaims,
				null);
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
	private static String successPage(AuthorizationCode authCode,
			AccessToken accessToken, String idToken,
			ReadOnlyJWTClaimsSet idTokenClaims,
			UserInfoSuccessResponse userInfoResponse) throws IOException {

		StringBuilder idTokenString = new StringBuilder();
		idTokenString.append(idTokenClaims.toJSONObject().toJSONString());
		idTokenString.append("\n");
		idTokenString.append(idToken);

		StringBuilder userInfoString = new StringBuilder();
		if (userInfoResponse != null) {
			userInfoString.append(userInfoResponse.getUserInfo().toJSONObject()
					.toJSONString());
			if (userInfoResponse.getContentType().equals(
					CommonContentTypes.APPLICATION_JWT)) {
				userInfoString.append("\n");
				userInfoString.append(userInfoResponse.getUserInfoJWT()
						.getParsedString());
			}
		}
		String successPage = readFromFile("success_page.html");
		return MessageFormat.format(successPage, authCode, accessToken,
				idTokenString, userInfoString);
	}

	/**
	 * Read all data from a file.
	 *
	 * @param path
	 *            path of the file
	 * @return All data from the file.
	 * @throws IOException
	 */
	private static String readFromFile(String path) throws IOException {
		return new String(
				Files.readAllBytes(FILE_DIR.resolve(Paths.get(path))),
				StandardCharsets.UTF_8);
	}

	/**
	 * Load a page from file.
	 *
	 * @return response containing the formatted HTML page.
	 * @throws IOException
	 */
	private static String loadPageFromFile(String file) throws IOException {
		return readFromFile(file);
	}
}
