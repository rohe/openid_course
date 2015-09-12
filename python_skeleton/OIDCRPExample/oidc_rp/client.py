import os

from oic.oauth2 import rndstr
from oic.oic import Client as OIDCClient
from oic.oic.message import AuthorizationResponse, RegistrationResponse

from oic.utils.authn.client import CLIENT_AUTHN_METHOD

from .defaults import SCOPE_BEHAVIOR

__author__ = 'regu0004'


class Client(object):
    # TODO specify the correct path
    ROOT_PATH = "."
    # TODO specify the correct URL
    ISSUER = "http://localhost:8000"

    def __init__(self, client_metadata):
        self.client = OIDCClient(client_authn_method=CLIENT_AUTHN_METHOD)

        self.provider_info = self.client.provider_config(Client.ISSUER)

        static = False
        if static:
            reg_info = RegistrationResponse(**{"client_id": "TODO", "client_secret": "TODO"})
            self.client.store_registration_info(reg_info)
        else:
            self.client.register(self.provider_info["registration_endpoint"], **client_metadata)

    def authenticate(self, session):
        # Use the session object to store state between requests

        # TODO make authentication request

        login_url = None  # TODO insert the redirect URL
        return login_url

    def code_flow_callback(self, auth_response, session):
        # TODO parse the authentication response
        # TODO make token request
        # TODO validate the ID Token according to the OpenID Connect spec (sec 3.1.3.7.)
        # TODO make userinfo request

        # TODO set the appropriate values
        access_code = None
        access_token = None
        id_token_claims = None
        userinfo = None
        return success_page(access_code, access_token, id_token_claims, userinfo)

    def implicit_flow_callback(self, auth_response, session):
        # TODO parse the authentication response
        # TODO validate the ID Token according to the OpenID Connect spec (sec 3.2.2.11.)

        # TODO set the appropriate values
        access_code = None
        access_token = None
        id_token_claims = None
        return success_page(access_code, access_token, id_token_claims, None)


def success_page(auth_code, access_token, id_token_claims, userinfo):
    html_page = read_from_file("success_page.html")
    return html_page.format(auth_code, access_token, id_token_claims, userinfo)


def read_from_file(path):
    full_path = os.path.join(Client.ROOT_PATH, path)
    with open(full_path, "r") as f:
        return f.read()
