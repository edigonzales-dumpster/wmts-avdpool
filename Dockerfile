FROM adoptopenjdk/openjdk11:latest

EXPOSE 8080

WORKDIR /home/avdpool

COPY build/libs/wmts-avdpool-*.jar /home/avdpool/app.jar
RUN chown -R 1001:0 /home/avdpool && \
    chmod -R g=u /home/avdpool

USER 1001

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/home/avdpool/app.jar"]