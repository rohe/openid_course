# Test different authentication flows

1. [Code flow](http://openid.net/specs/openid-connect-core-1_0.html#CodeFlowAuth)
    1. In the authentication request:
        * Specify ``response_type = code``.
        * Specify ``redirect_uri = http://localhost:8090/code_flow_callback``
        
1. [Implicit flow](http://openid.net/specs/openid-connect-core-1_0.html#ImplicitFlowAuth)
    1. In the authentication request:
        * Specify ``response_type = id_token token``.
        * Specify ``redirect_uri = http://localhost:8090/implicit_flow_callback``
        
1. [Hybrid flow](http://openid.net/specs/openid-connect-core-1_0.html#HybridFlowAuth)
    1. In the authentication request:
        * Specify ``response_type = code id_token``.
        * Specify``redirect_uri = http://localhost:8090/implicit_flow_callback``

    
# Test static and dynamic client registration

1. Static client registration
    1. Refer to each implementation's documentation for instructions.
    
1. Dynamic client registration (**note: this can not be done with the Apache module**)
    1. Refer to each implementation's documentation for instructions.
 
    
# Test requesting additional claims and special behavior
*Note: this can not be done with the Apache module.**

1. [Request claims using the ``scope`` parameter](http://openid.net/specs/openid-connect-core-1_0.html#ScopeClaims)
    1. In the authentication request:
        * Specify ``scope = openid profile``
    1. Observe which claims are returned.
    1. Try logging in as a another user and again observe which claims are returned.
    
1. [Request claims using the ``claims`` parameter](http://openid.net/specs/openid-connect-core-1_0.html#ClaimsParameter)
    1. In the authentication request
        * Specify the following in the claims request (pseudo-code):
        
            {  
               "userinfo":{  
                  "given_name":{  
                     "essential":true
                  },
                  "family_name":{  
                     "essential":true
                  },
                  "nickname":null
               },
               "id_token":{  
                  "email":{  
                     "essential":true
                  },
                  "phone_number":null
               }
            }
            
    1. Observere which claims are returned and how they are returned (in the user info or in the ID token).
    
1. Request a certain behavior by the OP using ``scope`` values
    1. In the authentication request
        * Specify ``scope = openid who_am_i``
        
    1. Observe what claims are returned and their values.
    1. Try logging in as a another user and again observe which claims are returned.
     
# Signing and encryption

1. Request a signed UserInfo Response
    1. In the registration request:
        * Specify ``userinfo_signed_response_alg = RS256``
    1. Refer to each implementation's documentation for instructions on how to
       verify the signature.

1. Request an encrypted UserInfo Response
    1. In the registration request:
        * Specify ``id_token_encrypted_response_enc = RSA1_5``
        * Specify ``userinfo_encrypted_response_alg = A128CBC-HS256``
    1. Refer to each implementation's documentation for instructions on how to
       decrypt the response.
    
        
1. Request a signed AND encrypted UserInfo Response
    1. In the registration request:
        * Specify ``userinfo_signed_response_alg = RS256``
        * Specify ``id_token_encrypted_response_enc = RSA1_5``
        * Specify ``userinfo_encrypted_response_alg = A128CBC-HS256``
    1. Refer to each implementation's documentation for instructions on how to
       verify the signature and decrypt the response.
