AWS_REGION=$(aws --profile ipcheck configure get aws_region)
AWS_ACCESS_KEY_ID=$(aws --profile ipcheck configure get aws_access_key_id)
AWS_SECRET_ACCESS_KEY=$(aws --profile ipcheck configure get aws_secret_access_key)

echo $AWS_REGION

docker volume create firehol-repo

# I don't like the -u root but need to figure it out with buildpacks
docker run -it \
  -e AWS_REGION=$AWS_REGION \
  -e AWS_ACCESS_KEY_ID=$AWS_ACCESS_KEY_ID \
  -e AWS_SECRET_ACCESS_KEY=$AWS_SECRET_ACCESS_KEY \
  -p 8080:8080 \
  -u root \
  -m 2gb \
  --mount source=firehol-repo,target=/var/repo \
  ipcheck

