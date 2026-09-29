FROM maven:3.9.16-eclipse-temurin-21-noble AS build
WORKDIR /workspace

COPY pom.xml .
RUN mvn -B -ntp dependency:go-offline

COPY src ./src
RUN mvn -B -ntp -DskipTests package

FROM eclipse-temurin:21-jre-noble AS runtime
WORKDIR /app

COPY --from=build --chown=10001:10001 /workspace/target/*.jar /app/app.jar
USER 10001:10001

EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
