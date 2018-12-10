//GSM module
#include <sms.h>

//Program constants
#define NUM_SIZE 20
#define SMS_SIZE 160
SMSGSM sms;

//Program variables
char phoneNumber[NUM_SIZE];
char messageText[SMS_SIZE];

void startGsmModule() {
  while(!gsm.begin(4800))
    delay(5000);
}

/*This sketch use low speed in order to allow arduino to process the
  the input before it reach the maximum size. That allows sending
  bigger messages without exceeds the bounds of memory */

void setup() {
  Serial.begin(1200);
  while(!Serial);
  
  startGsmModule();
}

void loop() {
  while(Serial.available() == 0);

  //read the phone number
  int i = 0;
  char character;
  while((character = Serial.read()) != char(',') && i < NUM_SIZE) {
    if(character == char(-1))
      continue;
    
    phoneNumber[i++] = character;
  }
  //Serial.println(phoneNumber); //# debug #

  i = 0; //reset array index
  while((character = Serial.read()) != char('\n') && i < SMS_SIZE) {
    if(character == char(-1))
      continue;

    messageText[++i] = character;
    //Serial.print(character); //# debug #
  }
  //Serial.println(); //sends "\n" to indicate the end of the string

  //starts the gsm module if not started or powered off
  if(gsm.CheckRegistration() != REG_REGISTERED)
    startGsmModule();

  if(sms.SendSMS(phoneNumber, messageText))
    Serial.print("OK");
  else Serial.print("ERROR");
  Serial.flush();

  //clean arrays data
  for(i = 0; i < NUM_SIZE; i++)
    phoneNumber[i] = '\0';

  for(i = 0; i < SMS_SIZE; i++)
    messageText[i] = '\0';
}
