#!/bin/bash

set -e

cd "$(dirname "$0")"

aws --endpoint-url=http://localhost:4566 cloudformation delete-stack \
    --stack-name microservices-practice

aws --endpoint-url=http://localhost:4566 cloudformation deploy \
    --stack-name microservices-practice \
    --template-file "./cdk.out/localstack.template.json"

aws --endpoint-url=http://localhost:4566 elbv2 describe-load-balancers \
    --query "LoadBalancers[0].DNSName" --output text
