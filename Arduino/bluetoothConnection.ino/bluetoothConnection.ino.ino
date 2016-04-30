 // arduino>>bluetooth
 // D11   >>>  Rx
 // D10   >>>  Tx

 #include <SoftwareSerial.h>

 //int inPin = 7;   // choose the input pin (for a pushbutton)
 //int val = 0;     // variable for reading the pin status
 SoftwareSerial Genotronex(10, 11); // RX, TX
 char BluetoothData; // the data given from Computer
 boolean flag = false;
 
 void setup() {
   // put your setup code here, to run once:
   Genotronex.begin(9600);
   Serial.begin(9600);
   Serial.println("Bluetooth telah menyala");
   //pinMode(inPin, INPUT);    // declare pushbutton as input 
 }

 void loop() {
  //Program untuk menggunakan button
//  val = digitalRead(inPin);  // read input value
//  if (val == HIGH) {         // check if the input is HIGH (button released)
//    Serial.println("tombol dilepas setelah ditekan");
//  } 
//  else {
//    Serial.println("tombol belum ditekan");
//  }
//  
    if (Genotronex.available()){
      BluetoothData=Genotronex.read();
      Serial.println("Data masuk");
      Serial.print(BluetoothData);
      flag=true;
    }
  delay(100);
 }

