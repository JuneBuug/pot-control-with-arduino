#include <Debug.h>
#include <JSN270.h>
#include <Arduino.h>
#include <SoftwareSerial.h>
#include <math.h>

#define SSID      "준킴"    // your wifi network SSID
#define KEY       "junekim0613"    // your wifi network password
#define AUTH       "WPA2"     // your wifi network security (NONE, WEP, WPA, WPA2)

#define USE_DHCP_IP 1

#if !USE_DHCP_IP
#define MY_IP          "10.16.131.245"
#define SUBNET         "255.255.192.0"
#define GATEWAY        "10.16.128.1"
#endif

#define HOST_IP        "74,125,232,128" // www.google.com
#define REMOTE_PORT    80
#define PROTOCOL       "TCP"

SoftwareSerial mySerial(3, 2); // RX, TX
SoftwareSerial sensorSerial(5, 4);
JSN270 JSN270(&mySerial);

int sensorPin = A0; // select the input pin for the potentiometer
String demo_key = "f2f423e8-467f-4ff1-9dfa-1674c615ef33";
String data = "id=f2f423e8-467f-4ff1-9dfa-1674c615ef33";
String response = "";
String sensor_res = ""; // 온습조도센서 값 
int ledPin = 7;
String temperature = "";
String humidity = "";
String lux = "";

void setup() {
  char c;
  String hostname;
  char hostip[32];

  mySerial.begin(9600);
  
  Serial.begin(9600);

  pinMode(ledPin,OUTPUT);
  digitalWrite(ledPin,HIGH);
  pinMode(sensorPin,INPUT);

  Serial.println("--------- JSN270 Web Client Test --------");
  
  // wait for initilization of JSN270
  delay(5000);
  //JSN270.reset();
  delay(1000);

  //JSN270.prompt();
  JSN270.sendCommand("at+ver\r");
  delay(5);
  while(JSN270.receive((uint8_t *)&c, 1, 1000) > 0) {
    Serial.print((char)c);
  }
  delay(1000);

#if USE_DHCP_IP
  JSN270.dynamicIP();
#else
  JSN270.staticIP(MY_IP, SUBNET, GATEWAY);
#endif    
    
  if (JSN270.join(SSID, KEY, AUTH)) {
    Serial.println("WiFi connect to " SSID);
  }
  else {
    Serial.println("Failed WiFi connect to " SSID);
    Serial.println("Restart System");

    return;
  }
  delay(1000);

  JSN270.sendCommand("at+wstat\r");
  delay(5);
  while(JSN270.receive((uint8_t *)&c, 1, 1000) > 0) {
    Serial.print((char)c);
  }
  delay(1000);        

  JSN270.sendCommand("at+nstat\r");
  delay(5);
  while(JSN270.receive((uint8_t *)&c, 1, 1000) > 0) {
    Serial.print((char)c);
  }
  delay(1000);

  hostname = "13.125.92.56";
  
  Serial.print("Host IP is ");
  Serial.println(hostname);
  hostname.toCharArray(hostip, hostname.length()+1);  // convert string to char array
  
  if (!JSN270.client(hostip, REMOTE_PORT, PROTOCOL)) {
    Serial.print("Failed connect to ");
    Serial.println(hostip);
    Serial.println("Restart System");
  } else {
    Serial.print("Socket connect to ");
    Serial.println(hostip);
    //delay(2000);
    
    // Enter data mode
    JSN270.sendCommand("at+exit\r");
    delay(100);

    Serial.println("connected to server");
  }

  sensorSerial.begin(9600);
}

unsigned long prev_time=0;

void loop() {

 sensorSerial.listen();
 delay(500);


  if (sensorSerial.isListening()) {
   Serial.println("sensorSerial is listening!"); 
  }
  
 while(sensorSerial.available())
 {
    //Serial.write(sensorSerial.read());
    if(sensorSerial.readString().startsWith("@T154")){
      sensor_res = sensorSerial.readString();
    }
 }

 unsigned long current_time = millis();
 int readVal=analogRead(sensorPin); // 토양 습도값
 Serial.print("토양센서: ");
 Serial.println(readVal);
 Serial.print("센서패킷 : ");
 Serial.println(sensor_res);
 
data = "id=f2f423e8-467f-4ff1-9dfa-1674c615ef33";

 if(sensor_res.length()>0){
   String str = sensor_res.substring(6);
   if(str.indexOf(",") != -1){
      str = str.substring(str.indexOf(",")+1);
      int idx = str.indexOf(",");
      temperature = str.substring(0,idx);
      str = str.substring(idx+1);
      idx = str.indexOf(",");
      humidity = str.substring(0,idx);
      lux = str.substring(idx+1);
      
     
//      Serial.println(temperature);
//      Serial.println(humidity);
//      Serial.println(lux);
  }
   
 }
  data += "&temperature=";
  data += temperature;
  data += "&humidity=";
  data += humidity;
  data += "&brightness=";
  data += lux;
  data += "&soil_water=";
  data += (String) readVal; 
 Serial.println(data);

 delay(500);
 
 mySerial.listen();
  if (mySerial.isListening()) {
   Serial.println("mySerial is listening!"); 
  }
 
 delay(500);
// if(current_time - prev_time > 40000){
  JSN270.print("GET /api/test");
  JSN270.print("?id=");
  JSN270.print(demo_key);
  JSN270.print(" ");
  JSN270.println("HTTP/1.1");
  JSN270.println("Host: 13.125.92.56");
  JSN270.println();
  
  JSN270.print("POST /api/log ");
  JSN270.println("HTTP/1.1");
  JSN270.println("Host: 13.125.92.56");
  JSN270.println("User-Agent: Arduino/1.0");
  JSN270.println("Connection: close");
  JSN270.println("Content-Type: application/x-www-form-urlencoded;");
  JSN270.print("Content-Length: ");
  JSN270.println(data.length());
  JSN270.println();
  JSN270.println(data);
//  prev_time = current_time;
//  }
  delay(200);
  int idx = 0;
  while(JSN270.available()){
    response = JSN270.readString();
  if(response.indexOf("True") != -1){
    digitalWrite(ledPin,HIGH);
  }

  if(response.indexOf("False") != -1){
    digitalWrite(ledPin,LOW);
  }
}

//  if(response.indexOf("Fa") != -1 || response.indexOf("Tr")!=-1){  
//    if(response.indexOf("Fa") > response.indexOf("Tr")){
//      Serial.println("LOW");
//      response = "";
//      digitalWrite(ledPin, LOW);
//    } else if(response.indexOf("Fa") < response.indexOf("Tr")){
//      Serial.println("HIGH");
//      digitalWrite(ledPin, HIGH);
//    }

//  }
}
