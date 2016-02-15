import json

import cherrypy

from .client import Client, read_from_file


class RPServer(object):
    def __init__(self):
        self.client = Client(json.loads(read_from_file("client.json")))

    @cherrypy.expose
    def index(self):
        return "Python " + read_from_file("index.html")

    @cherrypy.expose
    def authenticate(self):
        redirect_url = self.client.authenticate(cherrypy.session)
        raise cherrypy.HTTPRedirect(redirect_url, 302)

    @cherrypy.expose
    def repost_fragment(self, **kwargs):
        return self.client.implicit_flow_callback(kwargs["url_fragment"], cherrypy.session)

    @cherrypy.expose
    def code_flow_callback(self, **kwargs):
        if "error" in kwargs:
            raise cherrypy.HTTPError(500,
                                     "{}: {}".format(kwargs["error"], kwargs["error_description"]))
        return self.client.code_flow_callback(cherrypy.request.query_string, cherrypy.session)

    @cherrypy.expose
    def implicit_flow_callback(self, **kwargs):
        return read_from_file("repost_fragment.html")


def main():
    cherrypy.server.socket_host = "0.0.0.0"
    cherrypy.server.socket_port = 8090

    conf = {
        '/': {
            'tools.sessions.on': True
        }
    }
    cherrypy.quickstart(RPServer(), "/", conf)
