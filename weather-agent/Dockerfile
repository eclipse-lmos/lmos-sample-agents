## Stage 1 : unpack fat-jar
FROM registry.access.redhat.com/ubi9/openjdk-21-runtime as builder

USER root
# Define build argument for version
ARG VERSION
# Use the build argument to specify the JAR file
COPY build/libs/weather-agent-$VERSION.jar /deployments/app.jar
WORKDIR /deployments
RUN java -Djarmode=layertools -jar app.jar extract

## Stage 2 : create image
FROM eclipse-temurin
USER root
WORKDIR /deployments
COPY --from=builder /deployments/dependencies/ ./
COPY --from=builder /deployments/spring-boot-loader/ ./
COPY --from=builder /deployments/application/ ./
COPY config ./config
COPY agents ./agents

RUN apt-get update && apt-get install -yq dnsutils

EXPOSE 8080
EXPOSE 9090
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
