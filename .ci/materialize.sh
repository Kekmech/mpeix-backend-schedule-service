#!/bin/sh

##################
## Materialize ##
#################
# Tool for decoding base64 string from param into a file
#
# Usage:
#  $ ./materialize inputString filePath
# * inputString: base64 encoded string
# * filePath: path to the file to write to
# Example:
#  $ ./materialize $SECRET_FILE app/secrets/secret.txt

echo "$1" | base64 --decode >"$2"
if [ $? -eq 1 ]; then
    echo "Something went wrong during materialization"
    exit 1
fi
