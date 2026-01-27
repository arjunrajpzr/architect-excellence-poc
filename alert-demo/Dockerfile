FROM eclipse-temurin:17-jdk-jammy
ARG JAR_FILE=target/alert-demo-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
EXPOSE 9000
ENTRYPOINT ["java","-jar","/app.jar"]
