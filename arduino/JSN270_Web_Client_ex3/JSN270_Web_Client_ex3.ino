#include <Debug.h>
#include <JSN270.h>
#include <Arduino.h>
#include <SoftwareSerial.h>
#include <math.h>
#include <ArduinoJson.h>

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
 
JSN270 JSN270(&mySerial);

int sensorPin = A0; // select the input pin for the potentiometer
String demo_key = "f2f423e8-467f-4ff1-9dfa-1674c615ef33";
String response = "";
int ledPin = 7;


double Thermistor(int RawADC) {
  double Temp;
  Temp = log(10000.0*((1024.0/RawADC-1))); 
  Temp = 1 / (0.001129148 + (0.000234125 + (0.0000000876741 * Temp * Temp ))* Temp );
  Temp = Temp - 273.15;            // Convert Kelvin to Celcius
   //Temp = (Temp * 9.0)/ 5.0 + 32.0; // Convert Celcius to Fahrenheit
   return Temp;
}

void setup() {
  char c;
  String hostname;
  char hostip[32];

  
  
  mySerial.begin(9600);
  Serial.begin(9600);

  pinMode(ledPin,OUTPUT);
  digitalWrite(ledPin,HIGH);

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
}

unsigned long prev_time=0;

void loop() {

 unsigned long current_time = millis();
 int readVal=analogRead(sensorPin);


if (Serial.available())
  {
    Serial.write(Serial.read());
  }

 
 String data = "id=f2f423e8-467f-4ff1-9dfa-1674c615ef33&is_led_active=false";
 if(current_time - prev_time >20000){
  JSN270.print("GET /api/test");
  JSN270.print("?id=");
  JSN270.print(demo_key);
  JSN270.print(" ");
  JSN270.println("HTTP/1.1");
  JSN270.println("Host: 13.125.92.56");
  JSN270.println();
//  JSN270.print("POST /api/led ");
//  JSN270.println("HTTP/1.1");
//  JSN270.println("Host: 13.125.92.56");
//  JSN270.println("User-Agent: Arduino/1.0");
//  JSN270.println("Connection: close");
//  JSN270.println("Content-Type: application/x-www-form-urlencoded;");
//  JSN270.print("Content-Length: ");
//  JSN270.println(data.length());
//  JSN270.println();
//  JSN270.println(data);
  prev_time = current_time;
  }

  int idx = 0;
  while(JSN270.available()){
    response += (char)JSN270.read();
  }

//  Serial.print(response);
  //if(text.startsWith("\"F")){ // 만약 받아온 정보가 False이면 led를 끈다.
  if(response.indexOf("Fa") != -1 || response.indexOf("Tr")!=-1){  
    if(response.indexOf("Fa") > response.indexOf("Tr")){
      Serial.println("LOW");
      digitalWrite(ledPin, LOW);
    } else {
      Serial.println("HIGH");
      digitalWrite(ledPin, HIGH);
    }
  
  
  }
  


  

  
// Serial.println(json);
//  StaticJsonBuffer<200> jsonBuffer;
  //Serial.println(json);
//  JsonObject& array = jsonBuffer.parseObject(json);
//  const char* plant_id = array["id"];
  //Serial.println(plant_id);
  //Serial.println("출력");
//  for (auto& arr : array) {
//   const char* plant_id = arr["id"];
//   const char* name = arr["name"];
//   Serial.println(plant_id);
//   Serial.println("출력");
  
//  String str;
//  str = String(json);
//  Serial.println(str);
//  
//  int id_idx = str.indexOf("id");
//  int next_id_idx = str.indexOf(",", id_idx+1);
//  int led_idx = str.indexOf("is_led_active");
//  int next_led_idx = str.indexOf(",", led_idx+1);

  //Serial.println(str.substring(id_idx, next_id_idx));
  //Serial.println(str.substring(led_idx, next_led_idx));

  
  
  
}
