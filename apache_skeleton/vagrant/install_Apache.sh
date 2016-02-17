#!/usr/bin/env bash

MOD_AUTH_OPENIDC_COMMIT=4673a64

# Install Apache
sudo apt-get update
sudo apt-get install -y apache2

# Install dependencies for building mod_auth_openidc
sudo apt-get install -y apache2-dev libssl-dev libcurl4-openssl-dev libjansson-dev libpcre3-dev autoconf

# Fetch mod_auth_openidc
wget https://github.com/pingidentity/mod_auth_openidc/archive/${MOD_AUTH_OPENIDC_COMMIT}.tar.gz -O mod_auth_openidc.tar.gz
mkdir mod_auth_openidc && tar zxvf mod_auth_openidc.tar.gz -C mod_auth_openidc --strip-components 1

# Compile and install mod_auth_openidc
cd mod_auth_openidc/
./autogen.sh
./configure --with-apxs2=/usr/bin/apxs2
make
sudo make install
