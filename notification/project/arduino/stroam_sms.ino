//Ethernet and SD card
#include <SPI.h>
#include <Ethernet.h>

//GSM module
#include <SIM900.h>
#include <sms.h>

//MQTT client
#include <MQTT.h>

//Constants
//#define CONF_FILE_NAME "data.conf"
#define SMS_REQUEST "/sms"
#define SMS_REPONSE "/sms/response"

//Hardware variables
const PROGMEM byte mac[] = { 0x00, 0xAA, 0xBB, 0xCC, 0xDE, 0x02 };
EthernetClient ethernetClient;
MQTTClient mqttClient;
SMSGSM sms;

//Logic functions
boolean mqttConnect() {
  if(mqttClient.connect("a")) { //client id
    mqttClient.subscribe(SMS_REQUEST);
    return true;
  } return false;
}

void messageReceived(String &topic, String &payload) {
  int separator = payload.indexOf(",");

  char phoneNumber[separator + 1];
  payload.substring(0, separator).toCharArray(phoneNumber, sizeof(phoneNumber));

  char body[payload.length() - separator];
  payload.substring(separator + 1, payload.length()).toCharArray(body, sizeof(body));
  
  if(sms.SendSMS(phoneNumber, body))
    mqttClient.publish(SMS_REPONSE, "OK"); //don't work sometimes
  else mqttClient.publish(SMS_REPONSE, "ERROR"); //don't work sometimes
}

void setup() {
  Serial.begin(9600);
  delay(1000); //wait for serial start

  /*if(!SD.begin(4)) { //must have an if
    Serial.println("1");
    while(1);  
  }
  
  if(File confFile = SD.open(CONF_FILE_NAME)) {
    char temp; int i, readValue = 0;
    while(confFile.available()) {
      while((temp = confFile.read()) != '\n') {
        switch(readValue) {
          case 0: serverAddress[i++] = temp; break;
          case 1: port += (int)temp; break;
        }
        readValue++;
      }
    }
    confFile.close();
  } else {
    Serial.println("3"); //Conf. file not found. Init. failed!
    while(1);
  }*/

  if(Ethernet.begin(mac) == 0 && Ethernet.linkStatus() == LinkOFF) {
    Serial.println("1"); //Ethernet not connected. Init. failed!
    while(1);
  } else Serial.println(Ethernet.localIP());

  //Starts the mqtt client
  while(!mqttClient.connected()) {
    Serial.println("?");
    while(Serial.available() == 0) //wait for user input
      delay(1000);
    
    //try to connect with the given address
    String input = Serial.readString();
    char serverAddress[input.length()];
    input.toCharArray(serverAddress, sizeof(serverAddress));
    
    mqttClient.begin(serverAddress, 1883, ethernetClient);
    mqttConnect();
  }
  mqttClient.onMessage(messageReceived);
  mqttClient.publish(SMS_REPONSE, "READY");
  Serial.println("!");
}

void loop() {
  delay(1000); //wait in each new cicle
  mqttClient.loop();
  
  if(gsm.CheckRegistration() != REG_REGISTERED) {
    while(!gsm.begin(4800))
      delay(5000);
  }

  if(!mqttClient.connected())
    while(!mqttConnect())
      delay(5000);
}
