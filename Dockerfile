FROM eclipse-temurin:26-jre-alpine
RUN mkdir /opt/app
COPY target/zamszyk-0.15.0.jar /opt/app/zamszyk.jar
CMD ["java", "-jar", "/opt/app/zamszyk.jar"]
EXPOSE 8080
