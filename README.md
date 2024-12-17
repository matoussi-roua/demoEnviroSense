# demoEnviroSense :ESP32 Wi-Fi Multisensor Server: MQTT with TLS Configuration

This project integrates a secure MQTT server using TLS for the ESP32 Wi-Fi multisensor system. Below, you'll find instructions for setting up the MQTT server and configuring it with TLS for secure communication.

---

## Table of Contents

1. [MQTT Configuration](#mqtt-configuration)
2. [Generating Keys and Certificates](#generating-keys-and-certificates)
    - [Step 1: Create a Keystore](#step-1-create-a-keystore)
    - [Step 2: Extract Certificates and Keys](#step-2-extract-certificates-and-keys)
    - [Step 3: Create a Certificate Authority (CA)](#step-3-create-a-certificate-authority-ca)
    - [Step 4: Create a Server Certificate](#step-4-create-a-server-certificate)
    - [Step 5: Create a Client Certificate](#step-5-create-a-client-certificate)
3. [MQTT Broker Configuration](#mqtt-broker-configuration)
4. [Testing the MQTT Server](#testing-the-mqtt-server)

---

## MQTT Configuration

The server uses **Mosquitto** as the MQTT broker with SSL/TLS for secure communication. Configuration steps include:

- Generating the required keys and certificates.
- Configuring the Mosquitto broker to use these certificates.
- Setting up clients with secure connections.

---

## Generating Keys and Certificates

### Step 1: Create a Keystore

Generate a keystore containing a private key and self-signed certificate for the server.
#### Generate the Private Key for the CA
    openssl genpkey -algorithm RSA -out ca.key -aes256

#### Generate the Self-Signed Certificate for the CA
    openssl req -new -x509 -key ca.key -out ca.crt -days 3650

#### Generate the Server Private Key
    openssl genpkey -algorithm RSA -out server.key -aes256
#### Generate the Certificate Signing Request (CSR) for the Server
    openssl req -new -key server.key -out server.csr

#### Sign the Server Certificate with the CA
    openssl x509 -req -in server.csr -CA ca.crt -CAkey ca.key -CAcreateserial -out server.crt -d
#### Generate the Client Private Key
    openssl genpkey -algorithm RSA -out client.key -aes256
#### Generate the Client CSR
    openssl req -new -key client.key -out client.csr
#### Sign the Client Certificate with the CA
    openssl x509 -req -in client.csr -CA ca.crt -CAkey ca.key -CAcreateserial -out client.crt -days 365

This will create:

client.key: The client's private key.
client.crt: The signed client certificate.

#### Convert Client Certificate to PKCS12 Format (Optional)
    openssl pkcs12 -export -in client.crt -inkey client.key -out client-cert.p12 -name "mqtt-client" -CAfile ca.crt -caname "CA"
#### Create a Keystore for the Server (Optional for Java-based servers)
    keytool -genkey -keyalg RSA -alias server -keystore server.jks -validity 3650

## MQTT Broker Configuration
Update the Mosquitto broker's configuration file (mosquitto.conf) to enable SSL/TLS.

mosquitto.conf
    ```
        # Enable SSL/TLS
        listener 8883
        
        # Path to the CA certificate
        cafile path/ca.crt
        
        # Path to the server certificate
        certfile path/server.crt
    
        # Path to the server key
        keyfile path/server.key
    
        # Path to password file 
        password_file path/passwdfile

You can create the passwordfile using mosquitto_passwd:
    
    mosquitto_passwd -c C:/path/to/passwordfile username

and then set the password of your broker and replace username by your username

## Testing the MQTT Server
    mosquitto_sub -h localhost -p 8883 --cafile path/ca.crt --cert path/client.crt --key path/client.key -u "your_username" -P "your_password" -t "test/topic"