FROM maven:3-jdk-8 as base 
COPY . /
RUN mvn package

FROM openjdk:8-jdk
COPY --from=base /target/SlapBot.jar /SlapBot.jar
CMD ["java", "-jar", "/SlapBot.jar"]