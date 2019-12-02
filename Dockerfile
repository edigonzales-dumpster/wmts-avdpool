FROM adoptopenjdk/openjdk11:latest

RUN apt-get update && \
    apt-get install -y curl

EXPOSE 8080

WORKDIR /home/avdpool

COPY build/libs/wmts-avdpool-*.jar /home/avdpool/app.jar
RUN chown -R 1001:0 /home/avdpool && \
    chmod -R g=u /home/avdpool

USER 1001

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/home/avdpool/app.jar"]

HEALTHCHECK --interval=30s --timeout=30s --start-period=60s CMD curl http://localhost:8080/actuator/health