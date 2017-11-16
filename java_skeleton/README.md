This document describes how to setup the provided Java code
(in the directory ``java_skeleton/src``) in IntellyJ.


# Prerequisites

* Java 1.8
* IntellyJ Community Edition: https://www.jetbrains.com/idea/download


# Setup using IntellyJ
1. Install java 1.8: `apt-get install openjdk-8-jdk`

1. Import the project in IntellyJ:
   1. Click on the `Import project` option in the welcome window.
   1. Select the ``java_skeleton/pom.xml`` file and press OK.
   1. Click next on the following windows, and select the installed JDK (located in `/usr/lib/jvm/java-1.8.0-openjdk-amd64`)
   1. Wait for the project to load
   
1. Test that the project runs:
   1. Specify the path to the directory containing all necessary files (``client.json``, ``index.html``, etc.) in ``Client::ROOT_PATH`` (in ``src/oidc_rp/Client.java``).
   1. Run ``WebServer.java`` (right-click and Run).
   1. The application should output something like:

         ```
         [Thread-0] INFO spark.webserver.SparkServer - == Spark has ignited ...
         [Thread-0] INFO spark.webserver.SparkServer - >> Listening on 0.0.0.0:8090
         [Thread-0] INFO org.eclipse.jetty.server.Server - jetty-9.0.2.v20130417
         [Thread-0] INFO org.eclipse.jetty.server.ServerConnector - Started ServerConnector@5967e514{HTTP/1.1}{0.0.0.0:8090}
         ```

    1. Verify the Relying Party (RP) is running at [http://localhost:8090](http://localhost:8090)

1. Start adding to the skeleton code:
   1. The missing parts are marked with ``TODO`` in
       ``src/oidc_rp/Client.java``.
   1. Read the [Java Cookbook](http://connect2id.com/products/nimbus-oauth-openid-connect-sdk/guides/java-cookbook-for-openid-connect-public-clients) for more information
       about how to use the Nimbus OpenID Connect library.
   1. Make sure to delete cookies and cached data in the browser while
       testing to avoid strange results (e.g. due to the browser caching redirects, etc.).
