FROM eclipse-temurin:27-jre-alpine
RUN mkdir /opt/app
COPY target/zamszyk-0.17.0-SNAPSHOT.jar /opt/app/zamszyk.jar
CMD ["java", "-jar", "/opt/app/zamszyk.jar"]
EXPOSE 8080
