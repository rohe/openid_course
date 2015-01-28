This document contains information about how to use the provided Python code
(in the directory OIDCRPExample/main/) to build an OpenID Connect Public Client.


1. Setup

  Steps:
    1. Setup a virtual environment: http://docs.python-guide.org/en/latest/dev/virtualenvs/
    2. Activate the new virtual environment.
    3. Install the dependencies: pip install -r requirements.txt
    4. Test that the project runs:
      4.1. Specify the path to the directory containing all necessary files (client.json, index.html, etc.) in runner.py
      4.2. python runner.py
      4.3. Verify the server is running at http://localhost:8090
    5. Start adding to the skeleton code:
      5.1. The missing parts are marked with TODO's in
           <project_path>/OIDCRPExample/main/rp.py.
      5.2. Read https://dirg.org.umu.se/static/pyoidc/howto/rp.html for more
           information about how to use the pyoidv OpenID Connect library.
      5.3. Make sure to delete cookies and cached data in the browser while
           testing to avoid strange results (e.g. due to the browser caching
           redirects, etc.).
