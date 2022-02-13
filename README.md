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

Test
====
Spring actuator endpoints
```
http://localhost:8080/actuator
http://localhost:8080/actuator/info
http://localhost:8080/actuator/health
http://localhost:8080/actuator/metrics
```

IP Check endpoint
```text
http://localhost:8080/ipcheck/<IPv4 ip>
http://localhost:8080/ipcheck/0.0.0.0
```