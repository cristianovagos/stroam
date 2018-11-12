FROM java:8
WORKDIR /app
RUN ./mvnw clean package
COPY target/catalog-0.0.1-SNAPSHOT.jar /app/stroam-catalog.jar
ENTRYPOINT ["java","-jar","stroam-catalog.jar"]