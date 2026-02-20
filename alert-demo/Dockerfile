# Stage 1: Build/Extract Stage
FROM eclipse-temurin:17-jdk-jammy as builder
WORKDIR /workspace
ARG JAR_FILE=target/alert-demo-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} application.jar
RUN java -Djarmode=tools -jar application.jar extract --layers --launcher                    

# Stage 2: Final Image Stage
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Create a non-root user and group then switch to it
# RUN groupadd --gid 1001 appuser && useradd --uid 1001 --gid 1001 -m appuser
# USER appuser

# Copy extracted layers from the builder stage
COPY --from=builder /workspace/dependencies/ ./
COPY --from=builder /workspace/spring-boot-loader/ ./
COPY --from=builder /workspace/snapshot-dependencies/ ./
COPY --from=builder /workspace/application/ ./

EXPOSE 9000
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
