This document describes how to setup the provided Python code
(in the directory python_skeleton/).

# Prerequisites

* Python 3.4.3
* Homebrew if using Mac

# Setup

## System setup
On Mac:

    brew install libffi
    
On Ubuntu:

    apt-get install libffi-dev libsasl2-dev
        
## Project setup

### Optional pre-setup
1. Setup a [virtual environment](http://docs.python-guide.org/en/latest/dev/virtualenvs/)
2. Activate the new virtual environment.

### Code setup
1. Install the dependencies: ``pip install -r requirements.txt``

1. Test that the project runs:
  1. Specify the path to the directory containing all necessary files (client.json, index.html, etc.) in Client.ROOT_PATH (in client.py).
  1. ``python -m oidc_rp``
  1. Verify the server is running at [http://localhost:8090](http://localhost:8090).
  
1. Start adding to the skeleton code:
  1. The missing parts are marked with TODO's in \<project_path>/oidc_rp/client.py.
  1. Read the [Python Cookbook](https://dirg.org.umu.se/static/pyoidc/howto/rp.html) for more
       information about how to use the pyOIDC OpenID Connect library.
  1. Make sure to delete cookies and cached data in the browser while
       testing to avoid strange results (e.g. due to the browser caching
       redirects, etc.).
