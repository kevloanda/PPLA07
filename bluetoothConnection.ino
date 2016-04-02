// This program shown how to control arduino from PC Via Bluetooth
// Connect ...
 // arduino>>bluetooth
 // D11   >>>  Rx
 // D10   >>>  Tx
 //Written By Mohannad Rawashdeh
 //for http://www.genotronex.com/

 // you will need arduino 1.0.1 or higher to run this sketch

 #include <SoftwareSerial.h>// import the serial library

 SoftwareSerial Genotronex(10, 11); // RX, TX
 int ledpin=13; // led on D13 will show blink on / off
 char BluetoothData; // the data given from Computer
 boolean flag = false;

 void setup() {
   // put your setup code here, to run once:
   Genotronex.begin(9600);
   Serial.begin(9600);
   Genotronex.println("Halo Christian Halim....");
   pinMode(ledpin,OUTPUT);
 }

 void loop() {
   // put your main code here, to run repeatedly:
    //Serial.println("Bluetooth nyala");
    
    if (Genotronex.available()){
    BluetoothData=Genotronex.read();
    //Serial.println("Data masuk");
    Serial.print(BluetoothData);
     flag=true;
    }
 delay(100);// prepare for next data ...
 }

