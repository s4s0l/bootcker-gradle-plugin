FROM frolvlad/alpine-oraclejdk8:slim
ADD ${application_jar} /service/service.jar
ADD entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh
WORKDIR /service
EXPOSE 8080
ENV JAVA_OPTS=-Djava.security.egd=file:/dev/./urandom
ENTRYPOINT ["/entrypoint.sh"]
CMD ["--server.port=8080"]