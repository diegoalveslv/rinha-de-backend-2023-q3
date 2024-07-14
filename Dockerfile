FROM eclipse-temurin:21-alpine

#RUN mkdir -p /usr/local/newrelic
#ADD ./newrelic/newrelic.jar /usr/local/newrelic/newrelic.jar
#ADD ./newrelic/newrelic.yml /usr/local/newrelic/newrelic.yml

ENV JAVA_TOOL_OPTIONS=""

WORKDIR /app

COPY build/libs/rinha-de-backend-2023-q3-0.0.1-SNAPSHOT.jar /app/rinha-de-backend-2023-q3-0.0.1-SNAPSHOT.jar

EXPOSE 8080

# Run the Spring Boot application
#ENTRYPOINT ["java","-javaagent:/usr/local/newrelic/newrelic.jar","-jar","rinha-de-backend-2023-q3-0.0.1-SNAPSHOT.jar"]
ENTRYPOINT ["java","-jar","rinha-de-backend-2023-q3-0.0.1-SNAPSHOT.jar"]