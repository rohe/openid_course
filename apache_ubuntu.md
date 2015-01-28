# Install Apache web server in debian:

```bash
sudo apt-get update
sudo apt-get install apache2
```

## Install mod_auth_openidc:
https://github.com/pingidentity/mod_auth_openidc/releases/download/v1.7.1/libapache2-mod-auth-openidc_1.7.1-1_amd64.deb

## Create new SSL certificate

Note: When entering the certificate information "Common Name” must match the server

```bash
openssl req -x509 -newkey rsa:2048 -keyout key.pem -out cert.pem -days 365 -nodes
```

# Configure Apache with mod_auth_openidc

In this section we will describe how to configure Apache in order to make
mod_auth_openidc work, using static client registration.

apache2.conf path: `/etc/apache2/apache2.conf`

Add to apache2.conf:

```apache
Listen 443
ServerName <IP of your machine>

LoadModule ssl_module /usr/lib/apache2/modules/mod_ssl.so
LoadModule auth_openidc_module /usr/lib/apache2/modules/mod_auth_openidc.so

<VirtualHost _default_:443>

  SSLEngine on
  SSLCertificateFile <path to certificate file>/cert.pem
  SSLCertificateKeyFile <path to certificate file>/key.pem

  OIDCProviderMetadataURL https://<OP issuer URL>/.well-known/openid-configuration
  OIDCClientID <static client id>
  OIDCClientSecret <static client secret>
  OIDCResponseType id_token

  OIDCRedirectURI https://<IP of your machine>/redirect_uri
  OIDCCryptoPassphrase QWEasd123!##

  OIDCScope "openid"
  OIDCIDTokenSignedResponseAlg RS256

  <Location />
    AuthType openid-connect
    Require valid-user
  </Location>

</VirtualHost>
```

Configure

1. `ServerName`
1. `SSLCertificateFile`
1. `SSLCertificateKeyFile`
1. `OIDCProviderMetadataURL`
1. `OIDCClientID`
1. `OIDCClientSecret`
1. `OIDCRedirectURI` 

# Restart Apache:

```bash
sudo service apache2 restart
```

The log file for Apache is located in /var/log/apache2/error.log

# Test mod_auth_openidc module:
https://localhost

Since the module is black box you could see the result by returned by the OP it’s possible to inspect the HTTP requests/responses. If you are using chrome you could use the plugin HTTP trace” in order to see all HTTP request. The bigest advantage from using the built in developer tool in chrome is that ”HTTP trace” shows all request made by the browser. Since the use is redirected to the RP after completing the authorization request the build in developer tool in chrome will only see the http request made after the authorization.



