This document describes how to setup the provided Java code
(in the directory ``src/``) in Eclipse.


# Prerequisites

* Java 1.8
* Eclipse


# Setup using Eclipse

1. Import the project in Eclipse:
  1. File -> Import -> Maven -> Existing Maven Projects
    1. Select the ``java_skeleton/`` directory as 'Root Directory' and
         press Finish.
         
    1. Make sure you're using the Java SE 8 JRE for the project (see [Eclipse settings](http://help.eclipse.org/mars/index.jsp?topic=%2Forg.eclipse.jdt.doc.user%2Ftasks%2Ftask-choose_config_jre.htm)).  

1. Test that the project runs:
  1. Specify the path to the directory containing all necessary files (``client.json``, ``index.html``, etc.) in ``Client::ROOT_PATH`` (in ``src/oidc_rp/Client.java``).
  1. Run ``WebServer.java`` as a Java application.
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
  1. Read the [Java Cookbook](doc/doc.md) for more information
       about how to use the Nimbus OpenID Connect library.
  1. Make sure to delete cookies and cached data in the browser while
       testing to avoid strange results (e.g. due to the browser caching
       redirects, etc.).
