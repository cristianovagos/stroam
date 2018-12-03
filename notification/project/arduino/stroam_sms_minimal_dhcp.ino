//Ethernet and SD card
#include <SPI.h>
#include <Ethernet.h>

//GSM module
#include <SIM900.h>
#include <sms.h>

//MQTT client
#include <MQTT.h>

//Constants
#define SMS_REQUEST "/s"
#define SMS_REPONSE "/s/r"

//Hardware variables
const PROGMEM byte mac[] = { 0x00, 0xAA, 0xBB, 0xCC, 0xDE, 0x02 };
//const PROGMEM IPAddress ip(192,168,70,1);
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

  if(Ethernet.begin(mac) == 0 && Ethernet.linkStatus() == LinkOFF) {
    //Serial.println("1"); //Ethernet not connected. Init. failed!
    while(1);
  }

  //Case DHCP cannot give ip address
  /*if(!Ethernet.localIP())
    Ethernet.setLocalIP(ip);*/
  Serial.println(Ethernet.localIP());

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
  //mqttClient.publish(SMS_REPONSE, "READY");
  //Serial.println("!");
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
      delay(2000);
}
