import json

import cherrypy
import os


__author__ = 'regu0004'


class RPServer(object):
    ISSUER_URI = "https://dirg.org.umu.se:8092"

    def __init__(self, file_dir):
        self.file_dir = file_dir

        self.client_metadata = json.loads(self._read_file_content("client.json"))

        # TODO get the provider configuration information
        # TODO register with the provider using the self.client_metadata

    @cherrypy.expose
    def index(self):
        """Displays the main page."""
        return "Python " + self._read_file_content("index.html")

    @cherrypy.expose
    def authenticate(self):
        """Starts authentication using the OpenID Connect code flow."""
        # TODO make authentication request

        redirect_url = None # TODO insert the redirect URL
        raise cherrypy.HTTPRedirect(redirect_url, 302)

    @cherrypy.expose
    def repost_fragment(self, **kwargs):
        """
        Where the fragment identifier is received after being parsed by
        the client (using Javascript).
        """
        response = self.rp.parse_authentication_response(kwargs["url_fragment"])

        html_page = self._read_file_content("success_page.html")
        return html_page.format(None, response["access_token"], response["id_token"], None)

    @cherrypy.expose
    def code_flow_callback(self, **kwargs):
        """
        Where the authentication response from the provider is
        received when using code flow.
        """
        if "error" in kwargs:
            raise cherrypy.HTTPError(500, "{}: {}".format(kwargs["error"], kwargs["error_description"]))

        # TODO parse authentication response from url
        # TODO make token request
        # TODO verify the id token
        # TODO make userinfo request

        auth_code = None
        access_token = None
        id_token = None
        userinfo = None

        html_page = self._read_file_content("success_page.html")
        return html_page.format(auth_code, access_token, id_token, userinfo)

    @cherrypy.expose
    def implicit_flow_callback(self, **kwargs):
        """
        Where the authentication response from the provider is
        received when using implicit or hybrid flow.
        """
        return self._read_file_content("repost_fragment.html")

    def _read_file_content(self, path):
        """
        Load the contents of a file.
        """
        with open(os.path.join(self.file_dir, path), "r") as f:
            return f.read()


def main(file_dir):
    cherrypy.server.socket_host = "0.0.0.0"
    cherrypy.server.socket_port = 8090

    cherrypy.quickstart(RPServer(file_dir))

if __name__ == "__main__":
    main(os.getcwd())