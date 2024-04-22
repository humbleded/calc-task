FROM ibm-semeru-runtimes:open-8u345-b01-jre

ENV SPRING_OUTPUT_ANSI_ENABLED=ALWAYS \
    JHIPSTER_SLEEP=0 \
    JAVA_OPTS=""

CMD echo "The application will start in ${JHIPSTER_SLEEP}s..." && \
    sleep ${JHIPSTER_SLEEP} && \
    java ${JAVA_OPTS} -Djava.security.egd=file:/dev/./urandom -jar /app.jar

EXPOSE 8150
ARG JAR_FILE
ADD ${JAR_FILE} /app.jar
RUN     mkdir config
COPY config/ /config
