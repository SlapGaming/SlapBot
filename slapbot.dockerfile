FROM openjdk:8-jdk
COPY SlapBot.jar /SlapBot.jar
CMD ["java", "-jar", "/SlapBot.jar"]