#!/bin/bash

set -e
#set -x

cd "$(dirname "$0")"

export AWS_ENDPOINT_URL=http://localhost:4566

BUCKET_NAME="ls-bucket"
TEMPLATE_FILE="localstack.template.json"
STACK_NAME="microservices-practice"

BUCKET_LIST=$(aws s3 ls)

if [ -z "$BUCKET_LIST" ]; then
  echo "No buckets found in LocalStack."
else
  echo "Bucket list from LocalStack: '$BUCKET_LIST'"
fi

BUCKET_COUNT=$(echo "$BUCKET_LIST" | grep -c "$BUCKET_NAME" || [[ $? == 1 ]])

if [ "$BUCKET_COUNT" -eq 0 ]; then
  echo "Bucket '$BUCKET_NAME' does not exist. Creating the bucket..."
  aws s3 mb s3://$BUCKET_NAME
fi

aws s3 cp cdk.out/$TEMPLATE_FILE s3://$BUCKET_NAME/$TEMPLATE_FILE

aws s3 ls s3://$BUCKET_NAME

#echo $AWS_ENDPOINT_URL/$BUCKET_NAME/$TEMPLATE_FILE

#STACK_EXISTS=$(aws cloudformation list-stacks | grep -c "\"StackName\": \"$STACK_NAME\"" || [[ $? == 1 ]])

#if [ "$STACK_EXISTS" -eq 0 ]; then
  aws cloudformation delete-stack \
      --stack-name $STACK_NAME

  echo "Creating stack $STACK_NAME"
  aws cloudformation create-stack \
      --stack-name $STACK_NAME \
      --template-url $AWS_ENDPOINT_URL/$BUCKET_NAME/$TEMPLATE_FILE
#else
#  echo "Updating stack $STACK_NAME"
#  aws cloudformation update-stack \
#      --stack-name $STACK_NAME \
#      --template-url $AWS_ENDPOINT_URL/$BUCKET_NAME/$TEMPLATE_FILE
#fi

aws elbv2 describe-load-balancers --query "LoadBalancers[0].DNSName" --output text
