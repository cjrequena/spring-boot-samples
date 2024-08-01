FROM openjdk:17-jdk-slim

SHELL ["bash", "-c"]

WORKDIR /app

COPY target/*-exec.jar /app/app.jar

RUN touch /app/app.jar

COPY entrypoint.sh /app/entrypoint.sh

RUN chmod +x /app/entrypoint.sh

RUN apt-get update && apt-get install -y curl

RUN apt-get update && apt-get install -y curl jq

#RUN sed -i "s/#networkaddress.cache.ttl=-1/networkaddress.cache.ttl=60/g"  $JAVA_HOME/lib/security/java.security

EXPOSE 8080

ENTRYPOINT ["/app/entrypoint.sh"]
