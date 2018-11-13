FROM openjdk:10-jre-slim
COPY ./target/catalog-0.0.1-SNAPSHOT.jar /usr/src/catalog/
WORKDIR /usr/src/catalog
CMD ["java", "-jar", "catalog-0.0.1-SNAPSHOT.jar"]