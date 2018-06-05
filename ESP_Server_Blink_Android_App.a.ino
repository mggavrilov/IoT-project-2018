#include <ESP8266WiFi.h>          //https://github.com/esp8266/Arduino

//needed for library
#include <DNSServer.h>
#include <ESP8266WebServer.h>
#include <WiFiManager.h>         //https://github.com/tzapu/WiFiManager
#include <ESP8266mDNS.h>

#include <Wire.h>
#include <SPI.h>
//#include <Adafruit_Sensor.h>
//#include <Adafruit_BME280.h>

#define SEALEVELPRESSURE_HPA (1013.25)
//Adafruit_BME280 bme; // I2C
unsigned long delayTime;
ESP8266WebServer server(80);

double temp = 0;
double humi = 0;
double pres = 0;

int touchPin = D7;

int lightPin = D2;

volatile byte state = LOW;

void callback() {
  Serial.println(digitalRead(touchPin));
  state = !state;
  digitalWrite(LED_BUILTIN, state);
}

const int led = 13;

void handleRoot() {
  state = !state;
//  digitalWrite(LED_BUILTIN, state);
digitalWrite(lightPin, state);
  String result = (state == HIGH ? "off" : "on");
  server.send(200, "text/plain", result);
}

void handleNotFound(){
  digitalWrite(led, 1);
  String message = "File Not Found\n\n";
  message += "URI: ";
  message += server.uri();
  message += "\nMethod: ";
  message += (server.method() == HTTP_GET)?"GET":"POST";
  message += "\nArguments: ";
  message += server.args();
  message += "\n";
  for (uint8_t i=0; i<server.args(); i++){
    message += " " + server.argName(i) + ": " + server.arg(i) + "\n";
  }
  server.send(404, "text/plain", message);
  digitalWrite(led, 0);
}

void setup() {
  Serial.begin(115200);
  pinMode(LED_BUILTIN, OUTPUT);
  pinMode(touchPin, INPUT);

  pinMode(lightPin, OUTPUT);


//  attachInterrupt(digitalPinToInterrupt(touchPin), callback, RISING);

//  bool status;
//    Wire.begin(D4, D3);
//    // default settings
//    // (you can also pass in a Wire library object like &Wire2)
//    status = bme.begin(0x76);  
//    if (!status) {
//        Serial.println("Could not find a valid BME280 sensor, check wiring!");
//        while (1);
//    }
//    delayTime = 1000;

  //WiFiManager
  //Local intialization. Once its business is done, there is no need to keep it around
  WiFiManager wifiManager;
  //reset saved settings
  wifiManager.resetSettings();

  WiFi.softAP("probna", "12345678");
  
  //set custom ip for portal
  wifiManager.setAPStaticIPConfig(IPAddress(10,0,1,1), IPAddress(10,0,1,1), IPAddress(255,255,255,0));


  
  //if you get here you have connected to the WiFi
  Serial.println("connected...yeey :)");
  
  Serial.println("");
  Serial.print("Connected to ");
  Serial.println(WiFi.SSID());
  Serial.print("IP address: ");
  Serial.println(WiFi.localIP());

  if (MDNS.begin("esp8266")) {
    Serial.println("MDNS responder started");
  }

  server.on("/", handleRoot);

//  server.on("/temp", []() {
//    scanTemperature();
//    String s = String(temp, 2);
//    s += ",";
//    s += String(pres, 2);
//    s += ",";
//    s += String(humi, 2);    
//    server.send(200, "text/plain", s);
//  });

  server.on("/lamp", [](){
    String result = (state == HIGH ? "off" : "on");
    server.send(200, "text/plain", result);
  });

  server.onNotFound(handleNotFound);

  server.begin();
  Serial.println("HTTP server started");
}

void loop() {
   server.handleClient();
}

//void scanTemperature() {
//  temp = bme.readTemperature();
//  pres = bme.readPressure() / 100.0F;
//  humi = bme.readHumidity();
//}
