#!/usr/bin/env python

import os

print "Content-type: text/html\n\n"

print "<html><head>OIDC Parameters</head><body>"
print "<h1>mod_auth_openidc parameters</h1>"

for k in sorted(os.environ.keys()):
    if item.startswith("OIDC"):
        print "<p><strong>{param}</strong>: {value} </p>".format(param=k, value=os.environ[k])
print "</body></html>"