FROM adoptopenjdk/openjdk11:latest

EXPOSE 8080

WORKDIR /home/avdpool

ARG JAR_FILE
COPY ${JAR_FILE} /home/avdpool/app.jar
RUN chown -R 1001:0 /home/avdpool && \
    chmod -R g=u /home/avdpool

USER 1001

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/home/avdpool/app.jar"]