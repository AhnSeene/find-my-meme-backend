FROM gradle:7.6-jdk17 AS build
WORKDIR /app
COPY gradle /app/gradle
COPY gradle.properties /app/
COPY settings.gradle /app/
COPY build.gradle /app/
COPY src /app/src
RUN gradle build --no-daemon

FROM openjdk:17-jdk
COPY --from=build --chown=gradle:gradle /app/build/libs/find-my-meme-0.0.1-SNAPSHOT.jar /usr/app/find-my-meme-0.0.1-SNAPSHOT.jar
WORKDIR /usr/app
EXPOSE 8080
CMD ["java", "-jar", "find-my-meme-0.0.1-SNAPSHOT.jar"]
