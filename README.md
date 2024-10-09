# Projeto de Controle de Acesso via Bluetooth com Arduino e Android

Este repositório contém o código para um sistema de controle de acesso via Bluetooth utilizando um **Arduino** e um aplicativo Android desenvolvido em **Kotlin**. O projeto simula o controle de abertura de uma porta utilizando um LED, onde o aplicativo Android pode ligar e desligar o LED conectado ao Arduino por meio de um módulo Bluetooth.

## Visão Geral

O projeto consiste em duas partes principais:

1. **Aplicativo Android (Kotlin)**: O aplicativo Android se conecta ao Arduino via Bluetooth e controla o estado do LED, podendo ligá-lo e desligá-lo através de um botão no app. Além disso, o LED é automaticamente aceso quando o aplicativo é iniciado.
2. **Código do Arduino (C/C++)**: O código do Arduino recebe comandos do aplicativo Android via Bluetooth e altera o estado do LED de acordo com os comandos enviados.

## Requisitos

### Hardware

- **Arduino Uno** ou similar
- Módulo **Bluetooth HC-05** ou **HC-06**
- LED
- Resistores (220Ω para o LED)
- Jumpers e uma protoboard

### Software

- IDE **Arduino** para carregar o código no Arduino
- IDE **Android Studio** para rodar e modificar o aplicativo Android
- **Módulo Bluetooth HC-05/HC-06** emparelhado com o dispositivo Android

## Instruções de Configuração

### Configuração do Arduino

1. Monte o circuito:
   - Conecte o LED no pino digital 13 (ou outro de sua escolha) do Arduino.
   - Conecte o módulo Bluetooth (HC-05/HC-06) ao Arduino:
     - **RX** do HC-05 no **TX** do Arduino
     - **TX** do HC-05 no **RX** do Arduino
     - **VCC** e **GND** nos pinos de alimentação.
2. Abra o arquivo `arduino/arduino.ino` na IDE Arduino.
3. Carregue o código no Arduino.

### Configuração do Aplicativo Android

1. Abra a pasta `app/` no **Android Studio**.
2. Compile e execute o aplicativo no seu dispositivo Android.
3. Certifique-se de que o Bluetooth está ativado no dispositivo.
4. Conecte o dispositivo Android ao módulo Bluetooth (HC-05/HC-06). O emparelhamento geralmente requer a senha padrão **1234** ou **0000**.
5. Inicie o aplicativo, e o LED será aceso automaticamente. Utilize o botão no app para alternar o estado do LED (ligar/desligar).

## Funcionamento do Projeto

- Ao abrir o aplicativo, o LED conectado ao Arduino é automaticamente ligado.
- O botão no aplicativo permite alternar o estado do LED: ligando ou desligando de acordo com a interação do usuário.
- A comunicação entre o aplicativo e o Arduino é feita via Bluetooth. O aplicativo envia comandos simples como `"AUTORIZADO"` para ligar o LED e `"DESLIGAR"` para apagá-lo.

## Personalização

- **Alterar o pino do LED**: Caso deseje alterar o pino do LED, modifique a constante no código do Arduino:

```cpp
int ledPin = 13; // Modifique aqui para o pino desejado
```

- **Comandos Bluetooth**: O aplicativo envia strings simples para controlar o Arduino. Você pode modificar os comandos para ações diferentes, como abrir um portão ou outro dispositivo.
