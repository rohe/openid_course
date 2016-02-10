This document describes how to setup the provided Python code
(in the directory ``OIDCRPExample/``).

# Prerequisites

* Python 2.7 or 3.4.3
* Homebrew if using Mac
* PyCharm Community Edition: https://www.jetbrains.com/pycharm/download/

# Setup

## System setup
On Mac:

    brew install libffi
    
On Ubuntu:

    apt-get install libffi-dev libsasl2-dev
        
## Project setup

### Optional pre-setup
1. Setup a [virtual environment](http://docs.python-guide.org/en/latest/dev/virtualenvs/).
2. Activate the new virtual environment.

### Code setup
1. Install the dependencies: ``pip install -r requirements.txt``

1. Test that the project runs:
  1. Specify the path to the root directory containing all necessary files (``client.json``, 
     ``index.html``, etc.) in ``Client.ROOT_PATH`` (in ``oidc_rp/client.py``).
  1. Run the Relying Party (RP): ``python runner.py`` (from the ``OIDCRPExample/`` directory).
  1. Verify the RP is running at [http://localhost:8090](http://localhost:8090) **using Firefox**
     (Google Chrome has some issues with CherryPy's sessions).
  
1. Start adding to the skeleton code:
  1. The missing parts are marked with ``TODO`` in ``oidc_rp/client.py``.
  1. Read the [Python Cookbook](https://dirg.org.umu.se/static/pyoidc/howto/rp.html) for more
     information about how to use the pyOIDC OpenID Connect library.
  1. Make sure to delete cookies and cached data in the browser while
     testing to avoid strange results (e.g. due to the browser caching
     redirects, etc.).
