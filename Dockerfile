FROM openjdk:17-jdk
COPY build/libs/find-my-meme-0.0.1-SNAPSHOT.jar /usr/app/find-my-meme-0.0.1-SNAPSHOT.jar
WORKDIR /usr/app
EXPOSE 8080
CMD ["java", "-jar", "find-my-meme-0.0.1-SNAPSHOT.jar", "--spring.profiles.active=prod"]