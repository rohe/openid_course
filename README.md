# Assignment instructions
Download this repository as a [zip file](https://github.com/its-dirg/openid_course/archive/master.zip).
Choose one of the following assignments and follow the linked instructions:

1. Implement a Relying Party in Python: [instructions](python_skeleton/README.md)
1. Implement a Relying Party in Java: [instructions](java_skeleton/README.md)
1. Use the Apache module ``mod_auth_openidc`` as a black-box Relying Party: [instructions](apache_skeleton/README.md)
 
After completing the assignments, experiment with your setup by applying the
suggested tweaks in [OpenID Connect Parameter options](parameter_exercises.md). 

All OpenID Connect specifications can be found at http://openid.net/developers/specs/.

# Provider information

A custom OpenID Connect Provider with the issuer URI `https://op1.test.inacademia.org` can be used to test your Relying
Party against.

Static client registration can be performed through the web interface at `https://op1.test.inacademia.org/client_registration/`.

It has the following username-password pairs configured:
```
diana - krall
babs - howes
upper - crust
```
