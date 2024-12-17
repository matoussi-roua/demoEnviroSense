package org.demoenvirosense.demoenvirosense.security.config;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateFactory;

@Configuration
public class MqttConfig {
    private static final String MQTT_BROKER_URL = "ssl://localhost:8883"; // Use "ssl://" for secure connections
    private static final String MQTT_USERNAME = "localhost";
    private static final char[] MQTT_PASSWORD = "extra".toCharArray();
    private static final String CLIENT_CERT_PATH = "utility/client-cert.p12"; // Path to client certificate
    private static final String CLIENT_CERT_PASSWORD = "extra"; // Password for the client certificate
    private static final String CA_CERT_PATH = "utility/ca.crt"; // Path to CA certificate


    @Bean
    public MqttClient mqttClient() throws Exception {
        MqttClient client = new MqttClient(MQTT_BROKER_URL, MqttClient.generateClientId(), new MemoryPersistence());

        // Configure connection options
        MqttConnectOptions options = configureConnectionOptions();

        // Connect to the broker
        connectBroker(client, options);

        return client;
    }

    private MqttConnectOptions configureConnectionOptions() throws Exception {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(true); // Ensures no stale session is retained
        options.setSocketFactory(createSSLSocketFactory()); // Set up SSL/TLS security
        options.setUserName(MQTT_USERNAME); // Provide authentication
        options.setPassword(MQTT_PASSWORD);
        return options;
    }

    private void connectBroker(MqttClient client, MqttConnectOptions options) {
        try {
            client.connect(options);
            System.out.println("Connected to MQTT broker: " + MQTT_BROKER_URL);
        } catch (MqttException e) {
            System.err.println("Failed to connect to MQTT broker: " + e.getMessage());
            throw new RuntimeException("Unable to connect to MQTT broker", e); // Throw as unchecked exception
        }
    }

    private SSLSocketFactory createSSLSocketFactory() throws Exception {
        // Load client certificate and private key from PKCS12 file
        KeyStore keyStore = loadKeyStore(CLIENT_CERT_PATH, CLIENT_CERT_PASSWORD);

        // Initialize KeyManagerFactory with the client key store
        KeyManagerFactory keyManagerFactory = initializeKeyManagerFactory(keyStore, CLIENT_CERT_PASSWORD);

        // Load CA certificate for server trust
        KeyStore trustStore = loadTrustStore(CA_CERT_PATH);

        // Initialize TrustManagerFactory with the CA trust store
        TrustManagerFactory trustManagerFactory = initializeTrustManagerFactory(trustStore);

        // Set up SSL context with key and trust managers
        return initializeSSLContext(keyManagerFactory, trustManagerFactory).getSocketFactory();
    }

    private KeyStore loadKeyStore(String certPath, String certPassword) throws Exception {
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        try (InputStream certInputStream = getClass().getClassLoader().getResourceAsStream(certPath)) {
            if (certInputStream == null) {
                throw new IOException("Client certificate file not found at path: " + certPath);
            }
            keyStore.load(certInputStream, certPassword.toCharArray());
        }
        return keyStore;
    }

    private KeyManagerFactory initializeKeyManagerFactory(KeyStore keyStore, String certPassword) throws Exception {
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, certPassword.toCharArray());
        return keyManagerFactory;
    }

    private KeyStore loadTrustStore(String caCertPath) throws Exception {
        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        try (InputStream caCertInputStream = getClass().getClassLoader().getResourceAsStream(caCertPath)) {
            if (caCertInputStream == null) {
                throw new IOException("CA certificate file not found at path: " + caCertPath);
            }
            trustStore.load(null); // Initialize an empty trust store
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            trustStore.setCertificateEntry("ca-cert", certificateFactory.generateCertificate(caCertInputStream));
        }
        return trustStore;
    }

    private TrustManagerFactory initializeTrustManagerFactory(KeyStore trustStore) throws Exception {
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(trustStore);
        return trustManagerFactory;
    }

    private SSLContext initializeSSLContext(KeyManagerFactory keyManagerFactory, TrustManagerFactory trustManagerFactory) throws Exception {
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), new SecureRandom());
        return sslContext;
    }

}
