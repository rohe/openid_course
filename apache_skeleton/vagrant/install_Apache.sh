#!/usr/bin/env bash
PKG=~/mod_auth_openidc.deb

# Fetch mod_auth_openidc
wget https://github.com/pingidentity/mod_auth_openidc/releases/download/v1.8.4/libapache2-mod-auth-openidc_1.8.4-1ubuntu1.trusty.1_amd64.deb -O $PKG

# Install Apache and mod_auth_openidc
sudo apt-get update
sudo apt-get install -y apache2 gdebi
sudo gdebi -n $PKG