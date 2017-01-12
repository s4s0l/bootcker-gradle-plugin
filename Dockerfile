# This is Dockerfile that defines build environment.
FROM java:8-jdk
MAINTAINER augustyn@avast.com

# install Docker
#RUN curl -sSL https://get.docker.com/ | sh
RUN curl -fsSLO https://get.docker.com/builds/Linux/x86_64/docker-1.12.6.tgz && tar --strip-components=1 -xvzf docker-1.12.6.tgz -C /usr/local/bin

# install docker-compose
ENV COMPOSE_VERSION 1.9.0
RUN curl -o /usr/local/bin/docker-compose -L "https://github.com/docker/compose/releases/download/${COMPOSE_VERSION}/docker-compose-Linux-x86_64" \
	&& chmod +x /usr/local/bin/docker-compose

# allow to bind local Docker to the outer Docker
VOLUME /var/run/docker.sock

VOLUME /build
WORKDIR /build

ENTRYPOINT ["/build/gradlew"]
CMD ["test"]
