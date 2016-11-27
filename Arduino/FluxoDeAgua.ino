#include <WiFi.h>

WiFiClient client;
char ssid[] = "asus_zoad";
char pass[] = "ff3dff3d";

char thingSpeakAddress[] = "api.thingspeak.com";
String writeAPIKey = "LNIQLZAUWCAW33VP";

#define MILLIS_ENTRE_SERVER_UPDATE 20000 //20 segundos
long lastConnectionTime = 0; 
boolean lastConnected = false;
int failedCounter = 0;


#define pin_SENSOR_FLUXO_DE_AGUA     8
float calibrationFactor = 4.5; // The hall-effect flow sensor outputs approximately 4.5 pulses per second per litre/minute of flow.
volatile byte pulseCount;
float flowRate;
unsigned int flowMilliLitres;
unsigned long totalMilliLitres;
unsigned long totalMilliLitresEnviadoAoServidor;
unsigned long oldTime;
unsigned long oldTimeLed = 0;

void setup() {
  delay(10000);
  pinMode(LED_BUILTIN, OUTPUT);
  pinMode(pin_SENSOR_FLUXO_DE_AGUA, INPUT);

  digitalWrite(LED_BUILTIN, HIGH);

  Serial.begin(115200); //Configura a porta de comunicação serial com baudrate de 115200bps
  Serial.println("Inicializando!");

  if (WiFi.status() == WL_NO_SHIELD) {
    Serial.println("Modulo WIFI nao encontrado!");
    while(true);
  } 

  int statusDaConexaoWIFI = WL_IDLE_STATUS;
  while (statusDaConexaoWIFI != WL_CONNECTED) { //Loop de tentativa de conxão
    Serial.print("Tentando conexao com o SSID: ");
    Serial.println(ssid);
  
    statusDaConexaoWIFI = WiFi.begin(ssid, pass); //Iniciar conexão WPA/WPA2

    delay(10000); //Aguarda 10 segundos, 
  }
  Serial.print("Conexao WIFI estabelecida");
  printCurrentNet();
  printWifiData();

  lastConnectionTime = millis();

  pulseCount = 0;
  flowRate = 0.0;
  flowMilliLitres = 0;
  totalMilliLitres = 0;
  oldTime = 0;

  attachInterrupt(pin_SENSOR_FLUXO_DE_AGUA, pulseCounter, FALLING); //configura o pino de interrupção e o metodo que será chamdo a cada interrupção
}

void loop() {
  if((millis() - oldTime) > 1000) { //Entra aqui a cada segundo
    detachInterrupt(pin_SENSOR_FLUXO_DE_AGUA); //A interrupção é desabilitada durante o calculo da vazão do liquido
    flowRate = ((1000.0 / (millis() - oldTime)) * pulseCount) / calibrationFactor; //Formula retirada do datashet do sensor, retorna o valor vazão em litros por minuto
    oldTime = millis();
    flowMilliLitres = (flowRate / 60) * 1000; //Dividindo a vazão por 60, resulta na quantidade de litros que se passarma durante o ultimo segundo, multiplicando por 1000 temos o valor em mililitros
  
    totalMilliLitres += flowMilliLitres;
  
    if(flowRate > 0) {
      Serial.print("Vazao(L/min): ");
      Serial.print(flowRate, 2); //Duas casas decimais
  
      Serial.print(" | Vazao(mL/Sec): ");
      Serial.print(flowMilliLitres);
  
      Serial.print(" | Total Acumulado(mL): "); 
      Serial.println(totalMilliLitres);
    }
    pulseCount = 0;
    attachInterrupt(pin_SENSOR_FLUXO_DE_AGUA, pulseCounter, FALLING); //Habilitamos a interrupção novamente, após o calculo do fluxo de agua
  }
  if((flowRate > 0 && (millis() - oldTimeLed) > 200) || (flowRate == 0 && (millis() - oldTimeLed) > 1000)){
    digitalWrite(LED_BUILTIN, !digitalRead(LED_BUILTIN)); //Pisca mais rapido quando ha vazão
    oldTimeLed = millis();
  }

  //Caso exista resposta de alguma requisição
  if(verificarRequestResponse())
     totalMilliLitres -= totalMilliLitresEnviadoAoServidor;

  // Força desconexão do ThingSpeak apos envio de requisição
  if (client.connected() && lastConnected) {
    Serial.println("POST FIM");
    client.stop();
  }

  // Update ThingSpeak
  if(!client.connected() && (millis() - lastConnectionTime > MILLIS_ENTRE_SERVER_UPDATE)) {
     updateThingSpeak("api_key="+writeAPIKey+ "&field1="+ totalMilliLitres); //Parametros a serem enviados
     totalMilliLitresEnviadoAoServidor = totalMilliLitres;
  }
  lastConnected = client.connected();
}

/*
Interrupção por entrada, será executada quando houver mudança de nivel logico no pino de  interrupção configurado
 */
void pulseCounter() {
  pulseCount++; //Incrementa o contador de pulsos
}

bool verificarRequestResponse() {
  int i = 0;
  int result = -1;
  
  char bufferRX[100] = "\0";
  char OK_200[] = "Status: 200 OK";
  bool bodyStart = false;
  String body;
  
  while (client.available() && i < 100) {
    bufferRX[i++] = client.read();
    //Serial.print(bufferRX[i-1]);
    
    if(bodyStart)
      body += bufferRX[i-1];
    if(result == -1 && strstr(bufferRX, OK_200))
      result = 200;
      
    if(i==100 || bufferRX[i-1] == '\n') {
      if(i==2 && bufferRX[i-2] == '\r')
        bodyStart=true;
      i=0;
      memset(bufferRX,0,100);
    }
  }
  if(result != -1) {
    Serial.print("Result:");
    Serial.println(result);
    Serial.print("Body:");
    Serial.println(body);
    client.flush();
  }
  return result == 200 && body.length() > 0 && !body.equals("0");
}
void updateThingSpeak(String  stringVal) {
  if(client.connect(thingSpeakAddress, 80)) {
    client.print("POST /update HTTP/1.0\n");
    client.print("Host: api.thingspeak.com\n");
    client.print("Connection: close\n");
    //client.print("X-THINGSPEAKAPIKEY: "+writeAPIKey+"\n");
    client.print("Content-Type: application/x-www-form-urlencoded\n");
    client.print("Content-Length: ");
    client.print(stringVal.length());
    client.print("\n\n");

    client.print(stringVal);

    lastConnectionTime = millis();

    if (client.connected()) {
      Serial.print("POST: api.thingspeak.com/update/");
      Serial.println(stringVal);
    } else {
      Serial.println("ERRO durante POST");
    }
  } else {
    Serial.println("ERRO na conexao com ThingSpeak!");
    //resetFunc();

    lastConnectionTime = millis();
  }
}

void printWifiData() {
  // print your WiFi shield's IP address:
  IPAddress ip = WiFi.localIP();
  Serial.print("IP Address: ");
  Serial.println(ip);
  Serial.println(ip);

  // print your MAC address:
  byte mac[6];  
  WiFi.macAddress(mac);
  Serial.print("MAC address: ");
  Serial.print(mac[5],HEX);
  Serial.print(":");
  Serial.print(mac[4],HEX);
  Serial.print(":");
  Serial.print(mac[3],HEX);
  Serial.print(":");
  Serial.print(mac[2],HEX);
  Serial.print(":");
  Serial.print(mac[1],HEX);
  Serial.print(":");
  Serial.println(mac[0],HEX);
 
}

void printCurrentNet() {
  // print the SSID of the network you're attached to:
  Serial.print("SSID: ");
  Serial.println(WiFi.SSID());

  // print the MAC address of the router you're attached to:
  byte bssid[6];
  WiFi.BSSID(bssid);    
  Serial.print("BSSID: ");
  Serial.print(bssid[5],HEX);
  Serial.print(":");
  Serial.print(bssid[4],HEX);
  Serial.print(":");
  Serial.print(bssid[3],HEX);
  Serial.print(":");
  Serial.print(bssid[2],HEX);
  Serial.print(":");
  Serial.print(bssid[1],HEX);
  Serial.print(":");
  Serial.println(bssid[0],HEX);

  // print the received signal strength:
  long rssi = WiFi.RSSI();
  Serial.print("signal strength (RSSI):");
  Serial.println(rssi);

  // print the encryption type:
  byte encryption = WiFi.encryptionType();
  Serial.print("Encryption Type:");
  Serial.println(encryption,HEX);
  Serial.println();
}

void(* resetFunc) (void) = 0;//declare reset function at address 0
