![alt text](http://code.ua.pt/projects/es1819-stroam/repository/revisions/master/raw/notification/static/nottheservicelogo.png)

## About
NotTheService is a notification service based in MQTT protocol.
This service aims to process and forward the various notifications to all its users with great simplicity accross the diferent services and by email.

## API Specification
Below are specified all the necessary requirements for the correct use of the service. These requirements must be fulfilled by users in order to ensure a satisfactory use of the service.

The service is distributed in topics where users can subscribe to receive all the notifications published in them. These topics must respect the following requirements:
- All topics of the service must start with **/notTheService**, otherwise the messages will not be processed by the service.
- Topics with the hierarchy **/email** and **/phone** (hereinafter referred as &quot;service topics&quot;) are reserved for this type of notifications and should be only used for this purpose, otherwise may produce incorrect or unexpected service responses.
- With the exception of the service topics, all the other topics can carry any kind of data up to 250 Megabytes and do not need to respect any struture or format.
- With the exception of the service topics, all the other user topics will not receive any type of result about the request/operation made, and it is up to the user implement this type of guarantees over the existing service.
- The topics should not contain spaces or other null characters, punctuation, accentuation or special characters of any kind. To learn more about using the topics correctly read the information in the &quot;Topics/Subscriptions&quot; chapter in: [https://mosquitto.org/man/mqtt-7.html](https://mosquitto.org/man/mqtt-7.html).

### Send email notification
It is possible to send a notification of type email by publishing on any topic where the hierarchy ends in **/email/&lt;recipient_email address&gt;** where the message must contain fields and data formatted in JSON respecting the all conventions of this language according to the following standards:

* **requestId**: can contain any data of type string - used to distinguish to what request refers the result received after executing the operation.
* **subject**: can contain any data of type string in **base64** - used to specify the subject of the email.
* **body**: can contain any data of type string in **base64** - used to specify the body of the email (supports HTML).

The execution of this type of message in the service will produce a response with one of the following results codes:
100 - email send success
101 - email send error (normally, server error)
102 - email address null or empty
103 - email subject decode error
104 - email body null or empty
105 - email body decode error
106 - email decoded body null or empty
199 - email unknown error (invalid email addresses or email server error, for example, result in this code)

### Send phone notification
It is possible to send a notification of type phone (SMS) by publishing on any topic where the hierarchy ends in **/phone/&lt;recipient_phone_number&gt;** where the message must contain fields and data formatted in JSON respecting the all conventions of this language according to the following standards:

* **requestId**: can contain any data of type string - used to distinguish to what request refers the result received after executing the operation.
* **body**: can contain any data of type string in **base64** - used to specify the body of the phone message.

The execution of this type of message in the service will produce a response with one of the following results codes:
200 - phone SMS send success
201 - phone SMS send error (normally, server error)
202 - phone number null or empty
203 - phone SMS body null or empty
204 - phone SMS body decode error
205 - phone decoded SMS body null or empty
206 - phone SMS body too long (more than 160 characters)
299 - phone SMS unknown error (normally, server error)

### Service responses
All request made to the service (except push notifications) receive a reponse formatted in JSON respecting the all conventions of this language according to the standards specified below.
**Note:** To receive the response to the requests is mandatory to subscribe to the service topic **/requestResponse**, otherwise the service responses will not be received.

The execution of requests will produce a JSON formated response according to the following fields:

* **requestId**: contains the id, assigned when the request was made, which indicates to which request the response refers.
* **resultCode**: contains the result code of the request made, as specified in **Send email notification** if the response refers to a email request and in **Send phone notification** if the response refers to a phone SMS request.
* **resultCode**: contains a string that justifies the result obtained for the request made.

### Examples
You can find the libraries, examples of how to use them and more information about the service and how data is processed in [here](http://code.ua.pt/projects/es1819-stroam/repository/revisions/master/show/notification/project/client).

## What is done
- All base server logic
- All messages processors (handlers)
- All response processors (handlers)
- All scripts to run the service demonstration local level
- All logic (in hardware) to receive and process the SMS notifcations using capable hardware

## What is left
- More service tests
- Logging improvement

## Deployment

**Requirements**: 
- last version of the container platform Docker.
- git client
- MQTT client. You can use the JavaScript example in [here](http://code.ua.pt/projects/es1819-stroam/repository/revisions/master/show/notification/project/client/JavaScript)

To run the service demonstration local level you just need to download the project source files in [git repository](http://code.ua.pt/projects/es1819-stroam/repository/revisions/master/show/notification) and, in terminal do:
**cd** to the directory where is located the project folder download from git
**docker-compose up**

**Note:**
You can add email users in the email server using the script setup.sh located in ./config/setup.sh
with the command:
**sh setup.sh -c &lt;docker_email_container_id&gt; email add &lt;email_address&gt;**

To more information of how to use setup.sh do:
**sh setup.sh --help**