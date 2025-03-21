#!/bin/bash -eux

mkdir -p target/install
cd target/install
unzip -o ../authn.delegating-*-distro.zip
cd authn.delegating

cat > setup.properties <<EOF
secure    = true
container = Glassfish
home      = $CONTAINER_HOME
port      = 4848
EOF

cat > run.properties <<EOF
secretToken = AAAAAA
ip = localhost
EOF

./setup -v install
