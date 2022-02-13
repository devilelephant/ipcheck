AWS_ACCESS_KEY_ID=$(aws --profile gc configure get aws_access_key_id)
AWS_SECRET_ACCESS_KEY=$(aws --profile gc configure get aws_secret_access_key)

#docker build -t my_app .
#docker run -it --rm \
#   -e AWS_ACCESS_KEY_ID=$AWS_ACCESS_KEY_ID \
#   -e AWS_SECRET_ACCESS_KEY=$AWS_SECRET_ACCESS_KEY

docker run -e AWS_ACCESS_KEY_ID=$AWS_ACCESS_KEY_ID -e AWS_SECRET_ACCESS_KEY=$AWS_SECRET_ACCESS_KEY devilelephant/ipcheck

