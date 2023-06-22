FROM openjdk:17
ARG JAR_FILE=target/*.jar
COPY build/libs/Odysejapka.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]