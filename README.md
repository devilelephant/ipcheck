Setup
=====
Requires JDK 17

**Credentials**

add an `[ipcheck]` section to your ~/.aws/credentials file

```text
[ipcheck]
aws_access_key_id = <KEY>
aws_secret_access_key = <SECRET>
```

Build
=====

**Docker**

Build and create image, then run in local docker

```bash
./gradlew bootBuildImage --imageName=devilelephant/ipcheck
./docker_run.sh
```
