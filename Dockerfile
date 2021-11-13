FROM openjdk:17
ARG JAR_FILE=target/*.jar
COPY build/libs/odysejapka.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]