FROM openjdk:27-ea-slim-trixie
# FROM openjdk:17-jdk-slim
WORKDIR /app
COPY gradle-wrapper.jar gradle-wrapper.properties gradle /app/gradle/
COPY build.gradle settings.gradle /app/
RUN ./gradlew build --no-daemon
COPY src /app/src
EXPOSE 8080
RUN ./gradlew bootJar --no-daemon
CMD ["java", "-jar", "build/libs/your-app.jar"]

