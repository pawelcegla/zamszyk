FROM eclipse-temurin:26-jre-alpine
RUN mkdir /opt/app
COPY target/zamszyk-0.16.0-SNAPSHOT.jar /opt/app/zamszyk.jar
CMD ["java", "-jar", "/opt/app/zamszyk.jar"]
EXPOSE 8080
