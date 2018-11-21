FROM maven:3.6.0-jdk-10-slim
WORKDIR /usr/src/java-code
COPY . /usr/src/java-code/
RUN mvn install:install-file -Dfile=./lib/NotTheServiceClient.jar \
    -DgroupId=stroam.notification -DartifactId=not-the-service-client \
    -Dversion=1.0-SNAPSHOT -Dpackaging=jar
RUN mvn package -DskipTests
 
WORKDIR /usr/src/java-app
RUN cp /usr/src/java-code/target/*.jar ./app.jar
EXPOSE 4000
CMD ["java", "-jar", "app.jar"]