# Build stage
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn clean package

# Run stage
ENV JAVA_TOOL_OPTIONS "-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=*:5005"
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
VOLUME /tmp
COPY --from=build /app/target/fibonacci-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
EXPOSE 5005
ENTRYPOINT ["java", "-Xmx512m", "-Xms256m", "-jar", "app.jar"]
