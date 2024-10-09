#include <SoftwareSerial.h>

SoftwareSerial bluetooth(10, 11); 
int ledPin = 13;
String comandoAutorizado = "AUTORIZADO";

void setup() {
  bluetooth.begin(9600);
  Serial.begin(9600);
  
  pinMode(ledPin, OUTPUT);
  digitalWrite(ledPin, LOW);
}

void loop() {
  if (bluetooth.available()) {
    String comando = bluetooth.readStringUntil('\n');
    comando.trim(); // Remove espaços em branco ou quebras de linha

    Serial.print("Comando recebido: ");
    Serial.println(comando);
    
    if (comando == comandoAutorizado) {
      Serial.println("Acesso autorizado! Acendendo o LED...");
      digitalWrite(ledPin, HIGH);
    } else {
      Serial.println("Acesso negado. Apagando o LED...");
      digitalWrite(ledPin, LOW);
    }
  }
}
