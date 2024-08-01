#!/bin/bash
echo "Starting service"

#DOCUMENT_DB_USER_WITH_QUOTES=$(echo $DOCUMENTDB_SECRETS | jq '.username')
#DOCUMENT_DB_PASSWORD_WITH_QUOTES=$(echo $DOCUMENTDB_SECRETS | jq '.password')

#export DOCUMENT_DB_USER=$(echo $DOCUMENT_DB_USER_WITH_QUOTES | tr -d '"')
#export DOCUMENT_DB_PASSWORD=$(echo $DOCUMENT_DB_PASSWORD_WITH_QUOTES | tr -d '"')

#export EC2_HOST_IP=`curl -s http://169.254.169.254/latest/meta-data/local-ipv4`

sleep 3

java $JVM_OPTS --add-opens java.base/java.lang=ALL-UNNAMED -jar /app/app.jar
