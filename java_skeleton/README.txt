This document contains information about how to use the provided Java code
(in the directory java_skeleton/) to build an OpenID Connect Public Client.

        1. Setup using Eclipse


1. Setup using Eclipse

  Steps:
    1. Import the project in Eclipse:
      1.1. File -> Import -> Maven -> Existing Maven Projects
      1.2. Select the folder java_skeleton directory as 'Root Directory' and
           press Finish.
      1.3. Do Maven install:
        1.3.1. In the main menu: Run -> Run Configurations...
        1.3.2. Create new 'Maven Build' configuration.
        1.3.3. Base directory: 'Browse Workspace...' and select the skeleton project.
        1.3.4. Specify 'clean compile' as 'Goals'.
        1.3.5. Apply
    2. Test that the project runs:
      2.1. Specify the path to the directory containing all necessary files (client.json, index.html, etc.)
      	   in RPServer.py:RPServer().
      2.2. Run RPServer as a Java application.
      2.3. The application should output 'Server started, Hit Enter to stop.'.
      2.4. Verify the server is running at http://localhost:8090
    3. Start adding to the skeleton code:
      3.1. The missing parts are marked with TODO's in
           <project_path>/src/oidc_rp/RPServer.java.
      3.2. Read java_skeleton/java_openid_cookbook.html for more information
           about how to use the Nimbus OpenID Connect library.
      3.3. Make sure to delete cookies and cached data in the browser while
           testing to avoid strange results (e.g. due to the browser caching
           redirects, etc.).
