This document describes how to setup the provided Python code
(in the directory ``python_skeleton/OIDCRPExample/``).

# Prerequisites

* Python 2.7 or 3.4.3
* Homebrew if using Mac
* PyCharm Community Edition: https://www.jetbrains.com/pycharm/download/ (optional)

# Setup

## System setup

On Mac:

* Install pip: https://pip.pypa.io/en/stable/installing/
* Install libffi:

  ```
  brew install libffi
  ```  
    
On CentOS:

    yum install libffi-devel
        
On Ubuntu:

    apt-get install python-dev python-pip libssl-dev libffi-dev libsasl2-dev
        
## Project setup

### Optional pre-setup
1. Setup a [virtual environment](http://docs.python-guide.org/en/latest/dev/virtualenvs/). (You can do it directly from pyCharm)
2. Activate the new virtual environment.

### Code setup
1. Install the dependencies: ``pip install -r requirements.txt``

1. Test that the project runs:
   1. Specify the path to the root directory containing all necessary files (``client.json``, 
     ``index.html``, etc.) in ``Client.ROOT_PATH`` (in ``oidc_rp/client.py``).
   1. Run the Relying Party (RP): ``python runner.py`` (from the ``OIDCRPExample/`` directory).
   1. Verify the RP is running at [http://localhost:8090](http://localhost:8090) **using Firefox**
     (Google Chrome has some issues with CherryPy's sessions). With recent versions of Chrome there seem not to be a problem.
  
1. Start adding to the skeleton code:
   1. The missing parts are marked with ``TODO`` in ``oidc_rp/client.py``.
   1. Read the [Python Cookbook](https://github.com/OpenIDC/pyoidc/blob/master/doc/examples/rp.rst) for more
     information about how to use the pyOIDC OpenID Connect library.
   1. Make sure to delete cookies and cached data in the browser while
     testing to avoid strange results (e.g. due to the browser caching
     redirects, etc.).
