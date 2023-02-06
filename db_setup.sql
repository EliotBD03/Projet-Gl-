/*
All the queries initialized at the database set up.
Please make sure you have already set the PK before setting up the FK.
NOTE: all query use "ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci" at the end.
*/


CREATE TABLE `client` (
  `client_id` varchar(10) NOT NULL,
  PRIMARY KEY (`client_id`)
)

CREATE TABLE `consumption` (
  `ean` varchar(18) NOT NULL,
  `date` date NOT NULL,
  `daily_consumption` double(6,2) DEFAULT NULL,
  PRIMARY KEY (`ean`,`date`),
  CONSTRAINT `consumption_ibfk_1` FOREIGN KEY (`ean`) REFERENCES `counter` (`ean`)
)

CREATE TABLE `contract` (
  `contract_id` varchar(10) NOT NULL,
  `proposal_name` varchar(10) DEFAULT NULL,
  `ean` varchar(18) DEFAULT NULL,
  `provider_id` varchar(10) DEFAULT NULL,
  `address` varchar(42) DEFAULT NULL,
  `client_id` varchar(10) DEFAULT NULL,
  `opening_date` date DEFAULT NULL,
  `closing_date` date DEFAULT NULL,
  PRIMARY KEY (`contract_id`),
  KEY `provider_id` (`provider_id`,`contract_id`),
  KEY `proposal_name` (`proposal_name`),
  KEY `address` (`address`,`contract_id`),
  KEY `ean` (`ean`),
  CONSTRAINT `contract_ibfk_1` FOREIGN KEY (`provider_id`, `contract_id`) REFERENCES `provider_contract` (`provider_id`, `contract_id`),
  CONSTRAINT `contract_ibfk_2` FOREIGN KEY (`proposal_name`) REFERENCES `proposal` (`proposal_name`),
  CONSTRAINT `contract_ibfk_3` FOREIGN KEY (`address`, `contract_id`) REFERENCES `wallet_contract` (`address`, `contract_id`),
  CONSTRAINT `contract_ibfk_4` FOREIGN KEY (`ean`) REFERENCES `counter` (`ean`)
)

CREATE TABLE `counter` (
  `ean` varchar(18) NOT NULL,
  `contract_id` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`ean`)
)

CREATE TABLE `language` (
  `id` varchar(10) NOT NULL,
  `saved_language` varchar(255) NOT NULL,
  `favourite_language` binary(1) DEFAULT NULL,
  `current_language` binary(1) DEFAULT NULL,
  PRIMARY KEY (`id`,`saved_language`),
  CONSTRAINT `language_ibfk_1` FOREIGN KEY (`id`) REFERENCES `user` (`id`)
)

CREATE TABLE `notification` (
  `notification_id` varchar(10) NOT NULL,
  `sender_id` varchar(10) DEFAULT NULL,
  `receiver_id` varchar(10) DEFAULT NULL,
  `linked_contract` varchar(30) DEFAULT NULL,
  `context` varchar(25) DEFAULT NULL,
  PRIMARY KEY (`notification_id`)
)

CREATE TABLE `proposal` (
  `proposal_name` varchar(30) NOT NULL,
  `provider_id` varchar(10) NOT NULL,
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
  PRIMARY KEY (`proposal_name`,`provider_id`),
  KEY `provider_id` (`provider_id`),
  CONSTRAINT `proposal_ibfk_1` FOREIGN KEY (`provider_id`) REFERENCES `provider` (`provider_id`),
  CONSTRAINT `proposal_ibfk_2` FOREIGN KEY (`provider_id`) REFERENCES `provider` (`provider_id`)
)

CREATE TABLE `provider` (
  `provider_id` varchar(10) NOT NULL,
  PRIMARY KEY (`provider_id`)
)

CREATE TABLE `provider_contract` (
  `provider_id` varchar(10) NOT NULL,
  `contract_id` varchar(10) NOT NULL,
  PRIMARY KEY (`provider_id`,`contract_id`)
)

CREATE TABLE `user` (
  `id` varchar(10) NOT NULL,
  `mail` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `user_ibfk_1` FOREIGN KEY (`id`) REFERENCES `language` (`id`),
  CONSTRAINT `user_ibfk_2` FOREIGN KEY (`id`) REFERENCES `provider` (`provider_id`),
  CONSTRAINT `user_ibfk_3` FOREIGN KEY (`id`) REFERENCES `client` (`client_id`)
)

CREATE TABLE `wallet` (
  `address` varchar(42) NOT NULL,
  `client_id` varchar(10) DEFAULT NULL,
  `wallet_name` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`address`),
  KEY `client_id` (`client_id`),
  CONSTRAINT `wallet_ibfk_1` FOREIGN KEY (`client_id`) REFERENCES `client` (`client_id`)
)

CREATE TABLE `wallet_contract` (
  `address` varchar(42) NOT NULL,
  `contract_id` varchar(10) NOT NULL,
  PRIMARY KEY (`address`,`contract_id`),
  CONSTRAINT `wallet_contract_ibfk_1` FOREIGN KEY (`address`) REFERENCES `wallet` (`address`)
)