DROP DATABASE IF EXISTS data_sources;
CREATE DATABASE data_sources CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
SET FOREIGN_KEY_CHECKS = 0;


CREATE TABLE data_sources.connection_configurations
(
    connectionConfigurationID BIGINT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
    dataSourceId TEXT NOT NULL,
    dataSourceType TEXT NOT NULL,
    apiUrl TEXT,
    apiKey TEXT,
    httpMethod TEXT,
    brokerUrl TEXT,
    topic TEXT,
    clientId TEXT,
    username TEXT,
    password TEXT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE utf8mb4_unicode_ci;



DROP DATABASE IF EXISTS remote_data;
CREATE DATABASE remote_data CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
SET FOREIGN_KEY_CHECKS = 0;


CREATE TABLE remote_data.data_packets
(
    dataPacketID BIGINT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
    clientId TEXT,
    topic TEXT,
    content TEXT,
    qualityOfServiceLevel INT,
    isDeliveredToClient BIT(1),
    publicationDateTime DATETIME
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE utf8mb4_unicode_ci;


CREATE TABLE remote_data.topic_subscribers
(
    topicSubscriberID BIGINT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
    clientId TEXT,
    topic TEXT,
    subscriptionDateTime DATETIME
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE utf8mb4_unicode_ci;