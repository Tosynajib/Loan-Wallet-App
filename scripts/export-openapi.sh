#!/bin/bash

mkdir -p postman

curl http://localhost:8080/v3/api-docs -o postman/openapi.json

echo "OpenAPI spec exported to postman/openapi.json"