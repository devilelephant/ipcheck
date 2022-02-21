Retrieve an authentication token and authenticate your Docker client to your registry.
Use the AWS CLI:

    aws ecr --profile ipcheck get-login-password --region us-east-1 | docker login --username AWS --password-stdin 037352143869.dkr.ecr.us-east-1.amazonaws.com

Build your Docker image using the following command. For information on building a Docker file from scratch see the instructions here . You can skip this step if your image is already built:

    docker build -t ipcheck .

After the build completes, tag your image so you can push the image to this repository:

    docker tag ipcheck:latest 037352143869.dkr.ecr.us-east-1.amazonaws.com/ipcheck:latest

Run the following command to push this image to your newly created AWS repository:

    docker push 037352143869.dkr.ecr.us-east-1.amazonaws.com/ipcheck:latest
