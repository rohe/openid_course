from setuptools import setup

setup(name='pyOIDCExampleRP',
      version='0.0.1',
      description='An example implementation of an OpenID Connect Relying Party (RP) using pyoidc.',
      license='Apache 2.0',
      author='Rebecka Gulliksson',
      author_email='rebecka.gulliksson@umu.se',
      zip_safe=False,
      url='http://dirg.org.umu.se',
      packages=['main'],
      package_dir={'': 'src'},
)
