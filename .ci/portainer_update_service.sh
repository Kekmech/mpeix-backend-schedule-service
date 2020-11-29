#!/bin/bash

##################################
## Portainer API service update ##
##################################
# Tool for updating service with portainer api
#
# Usage:
#  $ ./portainer_update_service.sh login password host endpoint_id service_name image_name
# Requirements
# * curl
# * jq

USERNAME=$1
PASSWORD=$2
HOST=$3
ENDPOINT_ID=$4
SERVICE_NAME=$5
IMAGE_NAME=$6

LOGIN_TOKEN=$(curl -s -H "Content-Type: application/json" -d "{\"username\":\"$USERNAME\",\"password\":\"$PASSWORD\"}" -X POST $HOST/api/auth | jq -r .jwt)
if [ $? -eq 1 ]; then
    echo "Something went wrong during get jwt token"
    exit 1
fi

#ENDPOINT_ID=$(curl -s -H "Authorization: Bearer $LOGIN_TOKEN" $HOST/api/endpoints | jq ."[].Id")

SERVICE=$(curl -s -H "Authorization: Bearer $LOGIN_TOKEN" $HOST/api/endpoints/${ENDPOINT_ID}/docker/services | jq -c ".[] | select( .Spec.Name==(\"$SERVICE_NAME\"))")
if [ $? -eq 1 ]; then
    echo "Something went wrong during get service spec"
    exit 1
fi

ID=$(echo "$SERVICE" | jq  -r .ID)
SPEC=$(echo "$SERVICE" | jq .Spec)
VERSION=$(echo "$SERVICE" | jq .Version.Index)
UPDATE=$(echo "$SPEC" | jq ".TaskTemplate.ContainerSpec.Image |= \"$IMAGE_NAME\" " | jq ".TaskTemplate.ForceUpdate |= 1 ")

curl -s -H "Content-Type: text/json; charset=utf-8" \
-H "Authorization: Bearer $LOGIN_TOKEN" -X POST -d "${UPDATE}" \
"$HOST/api/endpoints/${ENDPOINT_ID}/docker/services/$ID/update?version=$VERSION"
if [ $? -eq 1 ]; then
    echo "Something went wrong during sending request"
    exit 1
fi
