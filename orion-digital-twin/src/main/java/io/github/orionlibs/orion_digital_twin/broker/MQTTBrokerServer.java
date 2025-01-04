package io.github.orionlibs.orion_digital_twin.broker;

import io.moquette.broker.ClientDescriptor;
import io.moquette.broker.Server;
import io.moquette.broker.config.IConfig;
import io.moquette.broker.config.MemoryConfig;
import java.util.Properties;
import org.eclipse.paho.mqttv5.common.MqttMessage;

public class MQTTBrokerServer
{
    private BrokerConnectionConfig config;
    private Server mqttServer;
    private boolean isRunning;
    private MQTTBroker broker;


    public MQTTBrokerServer(BrokerConnectionConfig config)
    {
        this.config = config;
        this.broker = new MQTTBroker();
    }


    public void startBroker()
    {
        // Use config to start the broker, for example:
        if(config.isSecure())
        {
            startSecureBroker(config.getHost(), config.getPort(), config.getUsername(), config.getPassword());
        }
        else
        {
            startInsecureBroker(config.getHost(), config.getPort());
        }
    }


    private void startSecureBroker(String host, int port, String username, String password)
    {
        try
        {
            // Create broker configuration
            Properties configProps = new Properties();
            configProps.setProperty(IConfig.HOST_PROPERTY_NAME, host);
            configProps.setProperty(IConfig.PORT_PROPERTY_NAME, String.valueOf(port));
            configProps.setProperty(IConfig.ENABLE_TELEMETRY_NAME, "false");
            //configProps.setProperty(IConfig.PERSISTENCE_ENABLED_PROPERTY_NAME, "false");
            //configProps.setProperty(IConfig.DATA_PATH_PROPERTY_NAME, "/currentDir/data");
            configProps.setProperty("password_file", "password.conf"); // File defining users/passwords
            configProps.setProperty("ssl_port", String.valueOf(port));
            configProps.setProperty("jks_path", "keystore.jks"); // Path to your Java keystore
            configProps.setProperty("key_store_password", "keystorePassword");
            configProps.setProperty("key_manager_password", "keyManagerPassword");
            IConfig brokerConfig = new MemoryConfig(configProps);
            // Start the broker
            mqttServer = new Server();
            mqttServer.startServer(brokerConfig);
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                if(mqttServer != null)
                {
                    mqttServer.stopServer();
                }
            }));
            System.out.println("Secure MQTT Broker started on " + host + ":" + port);
            this.isRunning = true;
        }
        catch(Exception e)
        {
            this.isRunning = false;
            throw new RuntimeException("Failed to start secure MQTT broker: " + e.getMessage(), e);
        }
    }


    private void startInsecureBroker(String host, int port)
    {
        try
        {
            Properties configProps = new Properties();
            configProps.setProperty(IConfig.HOST_PROPERTY_NAME, host);
            configProps.setProperty(IConfig.PORT_PROPERTY_NAME, String.valueOf(port));
            configProps.setProperty(IConfig.ENABLE_TELEMETRY_NAME, "false");
            //configProps.setProperty(IConfig.PERSISTENCE_ENABLED_PROPERTY_NAME, "false");
            //configProps.setProperty(IConfig.DATA_PATH_PROPERTY_NAME, "/currentDir/data");
            IConfig brokerConfig = new MemoryConfig(configProps);
            mqttServer = new Server();
            mqttServer.startServer(brokerConfig);
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                if(mqttServer != null)
                {
                    mqttServer.stopServer();
                }
            }));
            System.out.println("Insecure MQTT Broker started on " + host + ":" + port);
            for(ClientDescriptor client : mqttServer.listConnectedClients())
            {
                System.out.println("--------" + client.getClientID());
            }
            this.isRunning = true;
        }
        catch(Exception e)
        {
            this.isRunning = false;
            throw new RuntimeException("Failed to start insecure MQTT broker: " + e.getMessage(), e);
        }
    }


    public void stopBroker()
    {
        if(mqttServer != null)
        {
            try
            {
                mqttServer.stopServer();
                mqttServer = null;
                isRunning = false;
                System.out.println("MQTT Broker stopped successfully.");
            }
            catch(Exception e)
            {
                System.err.println("Error stopping MQTT Broker: " + e.getMessage());
            }
        }
        else
        {
            System.out.println("MQTT Broker cannot stop, because it is already stopped.");
        }
    }


    public void subscribe(String topic, String clientId) throws MQTTServerNotRunningException
    {
        if(mqttServer != null && isRunning)
        {
            broker.subscribe(topic, clientId);
        }
        else
        {
            throw new MQTTServerNotRunningException();
        }
    }


    public void unsubscribe(String topic, String clientId) throws MQTTServerNotRunningException
    {
        if(mqttServer != null && isRunning)
        {
            broker.unsubscribe(topic, clientId);
        }
        else
        {
            throw new MQTTServerNotRunningException();
        }
    }


    public void publish(String topic, MqttMessage message) throws MQTTServerNotRunningException
    {
        if(mqttServer != null && isRunning)
        {
            broker.publish(topic, message);
        }
        else
        {
            throw new MQTTServerNotRunningException();
        }
    }


    public boolean isRunning()
    {
        return isRunning;
    }
}
