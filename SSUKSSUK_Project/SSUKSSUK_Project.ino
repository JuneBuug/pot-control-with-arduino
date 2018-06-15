#include <dht11.h>
#include "WiFiEsp.h"

#ifndef HAVE_HWSERIAL1
#include "SoftwareSerial.h"
SoftwareSerial Serial1(6, 7);
#endif

dht11 DHT11; 
int pin_DHT11 = 9;
int pin_soil = A0;
int pin_LED = 5;
int pin_Light = A4;

char ssid[] = "윤소영의 iPhone";
char pass[] = "yoonsso0315";
int status = WL_IDLE_STATUS;

char server[] = "13.125.92.56";

WiFiEspClient client;

String data;

void setup()
{  
  // UART setup baud 115200, data bit 8, parity None, stop bit 1
  Serial.begin(115200);  // same Serial.begin(115200, SERIAL_8N1)
  Serial1.begin(9600); 
  pinMode(pin_soil, INPUT);
  pinMode(pin_LED, OUTPUT);
  pinMode(pin_Light, INPUT);
  WiFi.init(&Serial1);

  // check for the presence of the shield
  if (WiFi.status() == WL_NO_SHIELD) {
    Serial.println("WiFi shield not present");
    // don't continue
    while (true);
  }

  // attempt to connect to WiFi network
  while ( status != WL_CONNECTED) {
    Serial.print("Attempting to connect to WPA SSID: ");
    Serial.println(ssid);
    // Connect to WPA/WPA2 network
    status = WiFi.begin(ssid, pass);
  }

  // you're connected now, so print out the data
  Serial.println("You're connected to the network");
  
  printWifiStatus();

  Serial.println();
  Serial.println("Starting connection to server...");
  
}
void loop()
{  
  
  // temp & humi Read
  int chk = DHT11.read(pin_DHT11);
  switch (chk)
  {
    //normal operation
    case DHTLIB_OK: 
      Serial.print("Temperature : ");
      Serial.print(DHT11.temperature);
      Serial.print("[C] Humidity : ");
      Serial.print(DHT11.humidity);
      Serial.println("[%]");
      break;
    //error message 
    case DHTLIB_ERROR_CHECKSUM: 
      Serial.println("Checksum error"); 
      break;
    case DHTLIB_ERROR_TIMEOUT: 
      Serial.println("Time out error"); 
      break;
    default: 
      Serial.println("Unknown error"); 
      break;
  }

  int temperature = DHT11.temperature;

  int humidity = DHT11.humidity;

  int soil_value = analogRead(pin_soil);
  Serial.print("soil : ");
  Serial.println(soil_value);

  int light_value = analogRead(pin_Light);
  Serial.print("Light : ");
  Serial.println(light_value);
  //"{\"temperature\":\""+String(value)+"\"}";
  data = "id=f2f423e8-467f-4ff1-9dfa-1674c615ef33";
  data += "&temperature=";
  data += String(temperature);
  data += "&humidity=";
  data += String(humidity);
  data += "&brightness=";
  data += String(light_value);
  data += "&soil_water=";
  data += (String) soil_value;

  delay(500);
  Serial.println(data);
  // if you get a connection, report back via serial
  if (client.connect(server, 80)) {
    Serial.println("Connected to server");
    // Make a HTTP request
//    client.println("GET /api/log HTTP/1.1");
//    client.println("Host: 13.125.92.56");
//    client.println();
    
    client.println("POST /api/log HTTP/1.1 ");
    client.println("Host: 13.125.92.56 ");
    client.println("Content-Type: application/x-www-form-urlencoded");
    client.println("Content-Length: " + String(data.length()));
    client.println();
    client.println(data);
  }
    
  // if there are incoming bytes available
  // from the server, read them and print them
  String str = "";
  while (client.available()) {
    //Serial.print("enter");
    char c = client.read();
    str += c;
    Serial.write(c);
  }
  Serial.println(str);
  // if the server's disconnected, stop the client
  if (!client.connected()) {
    Serial.println();
    Serial.println("Disconnecting from server...");
    client.stop();

    // do nothing forevermore
    while (true);
  }
  // digitalWrite(pin_LED, HIGH);
  delay(1000);
  if(str.indexOf("False") != -1){
    digitalWrite(pin_LED, LOW);
  }
  if(str.indexOf("True") !=  -1){
    digitalWrite(pin_LED, HIGH);
  }
  
  delay(10000);
}

void printWifiStatus()
{
  // print the SSID of the network you're attached to
  Serial.print("SSID: ");
  Serial.println(WiFi.SSID());

  // print your WiFi shield's IP address
  IPAddress ip = WiFi.localIP();
  Serial.print("IP Address: ");
  Serial.println(ip);

  // print the received signal strength
  long rssi = WiFi.RSSI();
  Serial.print("Signal strength (RSSI):");
  Serial.print(rssi);
  Serial.println(" dBm");
}
