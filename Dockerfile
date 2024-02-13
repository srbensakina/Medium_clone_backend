FROM eclipse-temurin:17-jre-focal

WORKDIR /app

COPY target/*.jar app.jar
RUN sh -c 'touch /app.jar'

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java -jar /app/app.jar"]
