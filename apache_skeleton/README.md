This document describes how to setup the Apache module [``mod_auth_openidc``](https://github.com/pingidentity/mod_auth_openidc).

# Setup

Either use the accompanying Vagrant box or set it up locally.

## Vagrant

1. Install Vagrant: https://www.vagrantup.com/downloads.html
1. Install VirtualBox: https://www.virtualbox.org/
1. Start the Vagrant box and ssh into it:
  
   ```bash
   cd vagrant/
   vagrant up --provider=virtualbox
   vagrant ssh
   ```
  
## Local setup (Ubuntu)

1. Refer to the install script in [``vagrant/install_Apache.sh``](vagrant/install_Apache.sh).

# Apache configuration
Refer to the [mod_auth_openidc wiki](https://github.com/pingidentity/mod_auth_openidc/wiki) or the
[configuration option overview](https://github.com/pingidentity/mod_auth_openidc/blob/master/auth_openidc.conf)
for more information.

1. Generate a new (self-signed) SSL certificate:

   ```bash
   openssl req -x509 -newkey rsa:2048 -keyout /etc/ssl/localhost.key -out /etc/ssl/localhost.crt -days 1 -nodes
   ```
       
   **Note: When entering the certificate information, "Common Name" must match the server name, specify ``localhost``.**
       
1. Create a directory for the module and make sure it's writeable:
       
   ```bash
   mkdir /var/lib/apache2/mod_auth_openidc
   chmod 777 /var/lib/apache2/mod_auth_openidc
   ```

1. Setup CGI script displaying result after authentication:

   ```bash
   mkdir /usr/lib/protected-cgi-bin
   cp /apache_skeleton/openidc_params.cgi /usr/lib/protected-cgi-bin/
   chmod 755 /usr/lib/protected-cgi-bin/openidc_params.cgi
   ```
       
1. Make a new configuration for Apache in ``/etc/apache2/sites-available/oidc.conf``:

    ```apache
    Listen 8090
    LogLevel debug
    ServerName localhost
    
    LoadModule auth_openidc_module /usr/lib/apache2/modules/mod_auth_openidc.so
    LoadModule ssl_module /usr/lib/apache2/modules/mod_ssl.so
    LoadModule cgi_module /usr/lib/apache2/modules/mod_cgi.so
    
    <VirtualHost _default_:8090>
    OIDCMetadataDir /var/lib/apache2/mod_auth_openidc
    
    OIDCCryptoPassphrase py7h0n_rul35!
    OIDCRedirectURI https://localhost:8090/protected/redirect_uri
    
    ScriptAlias "/protected" "/usr/lib/protected-cgi-bin/openidc_params.cgi"
    <Location /protected>
      AuthType openid-connect
      Require valid-user
    </Location>
     
    SSLEngine on
    SSLCertificateFile /etc/ssl/localhost.crt
    SSLCertificateKeyFile /etc/ssl/localhost.key
    </VirtualHost>
    ```

1. Remove the default config and link the new one:
       
   ```bash
   rm /etc/apache2/sites-enabled/000-default.conf 
   ln -s /etc/apache2/sites-available/oidc.conf /etc/apache2/sites-enabled/oidc.conf
   ```
       
1. Restart Apache: ``service apache2 restart``

1. Verify it works by browsing to [https://localhost:8090/protected](https://localhost:8090/protected).
   
   **Note: you may get a warning in your browser about the certificate for ``localhost``, due to
   it being self-signed. Proceed to the site anyway (since this is expected).**

1. If any errors occur, view the log at ``/var/log/apache2/error.log`` and use your browsers tools
   to view network traffic and examine the requests/responses.
