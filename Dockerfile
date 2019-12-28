FROM adoptopenjdk/openjdk11:latest

RUN apt-get update && \
    apt-get install -y curl

EXPOSE 8080

WORKDIR /home/avdpool

ARG DEPENDENCY=build/dependency
COPY ${DEPENDENCY}/BOOT-INF/lib /home/avdpool/app/lib
COPY ${DEPENDENCY}/META-INF /home/avdpool/app/META-INF
COPY ${DEPENDENCY}/BOOT-INF/classes /home/avdpool/app
RUN chown -R 1001:0 /home/avdpool && \
    chmod -R g=u /home/avdpool

USER 1001

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/home/avdpool/app.jar"]

HEALTHCHECK --interval=30s --timeout=30s --start-period=60s CMD curl http://localhost:8080/actuator/health