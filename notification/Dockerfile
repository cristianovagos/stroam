FROM maven:3.6.0-jdk-10
COPY ./project/notification /usr/src/notification
COPY ./project/client /usr/src/notification/client
WORKDIR /usr/src/notification
RUN mvn clean install -DskipTests

RUN cp ./server/target/notTheService.jar .
RUN rm nottheservice.conf #avoid conflits
EXPOSE 1884
CMD ["java", "--illegal-access=deny", "-jar", "notTheService.jar"]

#docker run -it -v .../nottheservice.conf:/usr/src/notification/notheservice.conf <image>