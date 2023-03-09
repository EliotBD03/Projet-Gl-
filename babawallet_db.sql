-- MariaDB dump 10.19  Distrib 10.9.5-MariaDB, for Linux (x86_64)
--
-- Host: localhost    Database: babawallet_db
-- ------------------------------------------------------
-- Server version	10.9.5-MariaDB

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `client`
--

DROP TABLE IF EXISTS `client`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `client` (
  `client_id` int(10) NOT NULL,
  PRIMARY KEY (`client_id`),
  `latest_consumption_elec` double(6, 2) DEFAULT NULL,
  `latest_consumption_water` double(6, 2) DEFAULT NULL,
  `latest_consumption_gas` double(6, 2) DEFAULT NULL,
  CONSTRAINT `client_ibfk_1` FOREIGN KEY (`client_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `client`
--

LOCK TABLES `client` WRITE;
/*!40000 ALTER TABLE `client` DISABLE KEYS */;
/*!40000 ALTER TABLE `client` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `consumption`
--

DROP TABLE IF EXISTS `consumption`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `consumption` (
  `ean` varchar(18) NOT NULL,
  `date_recorded` date NOT NULL,
  `daily_consumption` double(6,2) DEFAULT 0,
  PRIMARY KEY (`ean`,`date_recorded`),
  CONSTRAINT `consumption_ibfk_1` FOREIGN KEY (`ean`) REFERENCES `counter` (`ean`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `consumption`
--

LOCK TABLES `consumption` WRITE;
/*!40000 ALTER TABLE `consumption` DISABLE KEYS */;
/*!40000 ALTER TABLE `consumption` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `contract`
--

DROP TABLE IF EXISTS `contract`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `contract` (
  `contract_id` int(10) NOT NULL,
  `proposal_name` varchar(10) DEFAULT NULL,
  `ean` varchar(18) DEFAULT NULL,
  `provider_id` int(10) DEFAULT NULL,
  `address` varchar(42) DEFAULT NULL,
  `client_id` varchar(10) DEFAULT NULL,
  `opening_date` date DEFAULT NULL,
  `closing_date` date DEFAULT NULL,
  PRIMARY KEY (`contract_id`),
  KEY `provider_id` (`provider_id`,`contract_id`),
  KEY `proposal_name` (`proposal_name`),
  KEY `address` (`address`,`contract_id`),
  KEY `ean` (`ean`),
  CONSTRAINT `contract_ibfk_1` FOREIGN KEY (`proposal_name`) REFERENCES proposal(`proposal_name`),
  CONSTRAINT `contract_ibfk_2` FOREIGN KEY (`provider_id`) REFERENCES provider_contract(`provider_id`),
  CONSTRAINT `contract_ibfk_3` FOREIGN KEY (`address`) REFERENCES wallet_contract(`address`),
  CONSTRAINT `contract_ibfk_4` FOREIGN KEY (`ean`) REFERENCES `counter`(`ean`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `contract`
--

LOCK TABLES `contract` WRITE;
/*!40000 ALTER TABLE `contract` DISABLE KEYS */;
/*!40000 ALTER TABLE `contract` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `counter`
--

DROP TABLE IF EXISTS `counter`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `counter` (
  `ean` varchar(18) NOT NULL,
  `contract_id` int(10) DEFAULT NULL,
  PRIMARY KEY (`ean`),
  KEY `contract_id` (`contract_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `counter`
--

LOCK TABLES `counter` WRITE;
/*!40000 ALTER TABLE `counter` DISABLE KEYS */;
/*!40000 ALTER TABLE `counter` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `language`
--

DROP TABLE IF EXISTS `language`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `language` (
  `id` int(10) NOT NULL,
  `saved_language` varchar(255) NOT NULL,
  `favourite_language` binary(1) DEFAULT NULL,
  `current_language` binary(1) DEFAULT NULL,
  PRIMARY KEY (`id`,`saved_language`),
  CONSTRAINT `language_ibfk_1` FOREIGN KEY (`id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `language`
--

LOCK TABLES `language` WRITE;
/*!40000 ALTER TABLE `language` DISABLE KEYS */;
/*!40000 ALTER TABLE `language` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notification`
--

DROP TABLE IF EXISTS `notification`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `notification` (
  `notification_id` int(10) NOT NULL AUTO_INCREMENT,
  `sender_id` int(10) DEFAULT NULL,
  `receiver_id` int(10) DEFAULT NULL,
  `linked_contract` varchar(30) DEFAULT NULL,
  `linked_proposal_name` varchar(30) DEFAULT NULL,
  `provider_id_proposal` int(10) DEFAULT NULL,
  `context` varchar(25) DEFAULT NULL,
  `creation_date` timestamp DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`notification_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notification`
--

LOCK TABLES `notification` WRITE;
/*!40000 ALTER TABLE `notification` DISABLE KEYS */;
/*!40000 ALTER TABLE `notification` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `proposal`
--

DROP TABLE IF EXISTS `proposal`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `proposal` (
  `proposal_name` varchar(30) NOT NULL,
  `provider_id` int(10) NOT NULL,
  `water` binary(1) DEFAULT NULL,
  `gas` binary(1) DEFAULT NULL,
  `electricity` binary(1) DEFAULT NULL,
  `fixed_rate` binary(1) DEFAULT NULL,
  `peak_hours` double(6,2) DEFAULT NULL,
  `offpeak_hours` double(6,2) DEFAULT NULL,
  `start_peak_hours` time DEFAULT NULL,
  `end_peak_hours` time DEFAULT NULL,
  `price` double(6,2) DEFAULT NULL,
  `location` binary(3) DEFAULT NULL,
  `duration` int(10) DEFAULT NULL,
  PRIMARY KEY (`proposal_name`,`provider_id`),
  KEY `provider_id` (`provider_id`),
  CONSTRAINT `proposal_ibfk_1` FOREIGN KEY (`provider_id`) REFERENCES `provider` (`provider_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `proposal`
--

LOCK TABLES `proposal` WRITE;
/*!40000 ALTER TABLE `proposal` DISABLE KEYS */;
/*!40000 ALTER TABLE `proposal` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `provider`
--

DROP TABLE IF EXISTS `provider`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `provider` (
  `provider_id` int(10) NOT NULL,
  PRIMARY KEY (`provider_id`),
  CONSTRAINT `provider_ibfk_1` FOREIGN KEY (`provider_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `provider`
--

LOCK TABLES `provider` WRITE;
/*!40000 ALTER TABLE `provider` DISABLE KEYS */;
/*!40000 ALTER TABLE `provider` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `provider_contract`
--

DROP TABLE IF EXISTS `provider_contract`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `provider_contract` (
  `provider_id` int(10) NOT NULL,
  `contract_id` int(10) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`provider_id`,`contract_id`),
  KEY `contract_id` (`contract_id`),
  CONSTRAINT `provider_contract_ibfk_1` FOREIGN KEY (`provider_id`) REFERENCES `provider` (`provider_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `provider_contract`
--

LOCK TABLES `provider_contract` WRITE;
/*!40000 ALTER TABLE `provider_contract` DISABLE KEYS */;
/*!40000 ALTER TABLE `provider_contract` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `mail` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `wallet`
--

DROP TABLE IF EXISTS `wallet`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `wallet` (
  `address` varchar(42) NOT NULL,
  `client_id` int(10) DEFAULT NULL,
  `wallet_name` varchar(30) DEFAULT NULL,
  `latest_consumption_elec` double(6, 2) DEFAULT 0,
  `latest_consumption_water` double(6, 2) DEFAULT 0,
  `latest_consumption_gas` double(6, 2) DEFAULT 0,
  PRIMARY KEY (`address`),
  KEY `client_id` (`client_id`),
  CONSTRAINT `wallet_ibfk_1` FOREIGN KEY (`client_id`) REFERENCES `client` (`client_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `wallet`
--

LOCK TABLES `wallet` WRITE;
/*!40000 ALTER TABLE `wallet` DISABLE KEYS */;
/*!40000 ALTER TABLE `wallet` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `wallet_contract`
--

DROP TABLE IF EXISTS `wallet_contract`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `wallet_contract` (
  `address` varchar(42) NOT NULL,
  `contract_id` int(10) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`address`,`contract_id`),
  KEY `contract_id` (`contract_id`),
  CONSTRAINT `wallet_contract_ibfk_1` FOREIGN KEY (`address`) REFERENCES `wallet` (`address`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `wallet_contract`
--

LOCK TABLES `wallet_contract` WRITE;
/*!40000 ALTER TABLE `wallet_contract` DISABLE KEYS */;
/*!40000 ALTER TABLE `wallet_contract` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2023-02-11 21:37:56
