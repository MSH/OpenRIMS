-- MySQL dump 10.13  Distrib 8.0.21, for Win64 (x86_64)
--
-- Host: 212.115.235.249    Database: pdx2
-- ------------------------------------------------------
-- Server version	5.7.35-0ubuntu0.18.04.1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `activity`
--

DROP TABLE IF EXISTS `activity`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `activity` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `conceptID2` bigint(20) NOT NULL,
  `conceptID` bigint(20) NOT NULL,
  `CreatedAt` date DEFAULT NULL,
  `ChangedAt` date DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FKactivity686761` (`conceptID`),
  KEY `FKactivity744662` (`conceptID2`),
  CONSTRAINT `FKactivity686761` FOREIGN KEY (`conceptID`) REFERENCES `concept` (`ID`),
  CONSTRAINT `FKactivity744662` FOREIGN KEY (`conceptID2`) REFERENCES `concept` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Temporary view structure for view `activity_data`
--

DROP TABLE IF EXISTS `activity_data`;
/*!50001 DROP VIEW IF EXISTS `activity_data`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `activity_data` AS SELECT 
 1 AS `ID`,
 1 AS `come`,
 1 AS `go`,
 1 AS `days`,
 1 AS `applNodeId`,
 1 AS `actConfigID`,
 1 AS `applicant`,
 1 AS `applurl`,
 1 AS `executive`,
 1 AS `activityurl`,
 1 AS `pref`,
 1 AS `lang`*/;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `activitydict`
--

DROP TABLE IF EXISTS `activitydict`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `activitydict` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `activityID` bigint(20) DEFAULT NULL,
  `conceptID` bigint(20) NOT NULL,
  `Url` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FKactivitydi565601` (`conceptID`),
  KEY `FKactivitydi939134` (`activityID`),
  CONSTRAINT `FKactivitydi565601` FOREIGN KEY (`conceptID`) REFERENCES `concept` (`ID`),
  CONSTRAINT `FKactivitydi939134` FOREIGN KEY (`activityID`) REFERENCES `activity` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `activitydoc`
--

DROP TABLE IF EXISTS `activitydoc`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `activitydoc` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `conceptID` bigint(20) NOT NULL,
  `activityID` bigint(20) DEFAULT NULL,
  `Url` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FKactivitydo619183` (`activityID`),
  KEY `FKactivitydo992716` (`conceptID`),
  CONSTRAINT `FKactivitydo619183` FOREIGN KEY (`activityID`) REFERENCES `activity` (`ID`),
  CONSTRAINT `FKactivitydo992716` FOREIGN KEY (`conceptID`) REFERENCES `concept` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `activitything`
--

DROP TABLE IF EXISTS `activitything`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `activitything` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `conceptID` bigint(20) NOT NULL,
  `activityID` bigint(20) DEFAULT NULL,
  `Url` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FKactivityth455325` (`activityID`),
  KEY `FKactivityth828858` (`conceptID`),
  CONSTRAINT `FKactivityth455325` FOREIGN KEY (`activityID`) REFERENCES `activity` (`ID`),
  CONSTRAINT `FKactivityth828858` FOREIGN KEY (`conceptID`) REFERENCES `concept` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `address`
--

DROP TABLE IF EXISTS `address`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `address` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `spatialID` bigint(20) NOT NULL,
  `conceptID` bigint(20) DEFAULT NULL,
  `PoBox` varchar(255) DEFAULT NULL,
  `PostCode` varchar(255) DEFAULT NULL,
  `AddressId` varchar(255) DEFAULT NULL,
  `spatialdataID` bigint(20) NOT NULL,
  `conceptID2` bigint(20) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `FKaddress411336` (`conceptID`),
  KEY `FKaddress427008` (`spatialdataID`),
  KEY `FKaddress951502` (`conceptID2`),
  CONSTRAINT `FKaddress411336` FOREIGN KEY (`conceptID`) REFERENCES `concept` (`ID`),
  CONSTRAINT `FKaddress427008` FOREIGN KEY (`spatialdataID`) REFERENCES `spatialdata` (`ID`),
  CONSTRAINT `FKaddress951502` FOREIGN KEY (`conceptID2`) REFERENCES `concept` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `admin_route`
--

DROP TABLE IF EXISTS `admin_route`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `admin_route` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `code` varchar(500) DEFAULT NULL,
  `Name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=120 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `administrativeunit`
--

DROP TABLE IF EXISTS `administrativeunit`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `administrativeunit` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdDate` datetime DEFAULT NULL,
  `updatedDate` datetime DEFAULT NULL,
  `code` varchar(15) NOT NULL,
  `legacyId` varchar(50) DEFAULT NULL,
  `name1` varchar(255) DEFAULT NULL,
  `name2` varchar(255) DEFAULT NULL,
  `unitsCount` int(11) NOT NULL,
  `createdBy_userId` bigint(20) DEFAULT NULL,
  `updatedBy_userId` bigint(20) DEFAULT NULL,
  `COUNTRYSTRUCTURE_ID` bigint(20) DEFAULT NULL,
  `PARENT_ID` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_62r9or1nr1w2yp03g7npoqjrk` (`id`,`code`),
  KEY `FK_o9xlw2fxs5v97odf2gblkyuk9` (`createdBy_userId`),
  KEY `FK_iqofnqqh69v2p2hfx28egpt9o` (`updatedBy_userId`),
  KEY `FK_s7gpdt3awwsgf3aqyxj08hfsx` (`COUNTRYSTRUCTURE_ID`),
  KEY `FK_dahrjeel2xeban5v6sn1at0c9` (`PARENT_ID`),
  CONSTRAINT `FK_dahrjeel2xeban5v6sn1at0c9` FOREIGN KEY (`PARENT_ID`) REFERENCES `administrativeunit` (`id`),
  CONSTRAINT `FK_iqofnqqh69v2p2hfx28egpt9o` FOREIGN KEY (`updatedBy_userId`) REFERENCES `user` (`userId`),
  CONSTRAINT `FK_o9xlw2fxs5v97odf2gblkyuk9` FOREIGN KEY (`createdBy_userId`) REFERENCES `user` (`userId`),
  CONSTRAINT `FK_s7gpdt3awwsgf3aqyxj08hfsx` FOREIGN KEY (`COUNTRYSTRUCTURE_ID`) REFERENCES `countrystructure` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `amdmt_category`
--

DROP TABLE IF EXISTS `amdmt_category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `amdmt_category` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `createdDate` datetime DEFAULT NULL,
  `updatedDate` datetime DEFAULT NULL,
  `amdmt_type` varchar(255) DEFAULT NULL,
  `cat_code` int(11) DEFAULT NULL,
  `eForm` bit(1) DEFAULT NULL,
  `full_desc` varchar(1000) DEFAULT NULL,
  `short_desc` varchar(255) DEFAULT NULL,
  `createdBy_userId` bigint(20) DEFAULT NULL,
  `updatedBy_userId` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_ough5r9micgar2gq7svxqs6m9` (`createdBy_userId`),
  KEY `FK_9m4a0hnttyvuw1dk9ajsask0q` (`updatedBy_userId`),
  CONSTRAINT `FK_9m4a0hnttyvuw1dk9ajsask0q` FOREIGN KEY (`updatedBy_userId`) REFERENCES `user` (`userId`),
  CONSTRAINT `FK_ough5r9micgar2gq7svxqs6m9` FOREIGN KEY (`createdBy_userId`) REFERENCES `user` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `amended`
--

DROP TABLE IF EXISTS `amended`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `amended` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `assemblyID` bigint(20) NOT NULL,
  `amendmentID` bigint(20) DEFAULT NULL,
  `OldValue` varchar(255) DEFAULT NULL,
  `NewValue` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FKamended511026` (`amendmentID`),
  KEY `FKamended264173` (`assemblyID`),
  CONSTRAINT `FKamended264173` FOREIGN KEY (`assemblyID`) REFERENCES `assembly` (`ID`),
  CONSTRAINT `FKamended511026` FOREIGN KEY (`amendmentID`) REFERENCES `amendment` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `amendment`
--

DROP TABLE IF EXISTS `amendment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `amendment` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `amendedThingID` bigint(20) NOT NULL,
  `applicationDataID` bigint(20) NOT NULL,
  `conceptID` bigint(20) NOT NULL,
  `OldThing` mediumtext,
  `NewThing` mediumtext,
  `CreatedAt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`ID`),
  KEY `FKamendment121508` (`conceptID`),
  KEY `FKamendment712597` (`applicationDataID`),
  KEY `FKamendment307312` (`amendedThingID`),
  CONSTRAINT `FKamendment121508` FOREIGN KEY (`conceptID`) REFERENCES `concept` (`ID`),
  CONSTRAINT `FKamendment307312` FOREIGN KEY (`amendedThingID`) REFERENCES `concept` (`ID`),
  CONSTRAINT `FKamendment712597` FOREIGN KEY (`applicationDataID`) REFERENCES `concept` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `answerquestion`
--

DROP TABLE IF EXISTS `answerquestion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `answerquestion` (
  `Id` bigint(20) NOT NULL AUTO_INCREMENT,
  `authorId` bigint(19) DEFAULT NULL,
  `Yes` tinyint(1) NOT NULL,
  `No` tinyint(1) NOT NULL,
  `NotApplicable` tinyint(1) NOT NULL,
  `expertId` bigint(19) DEFAULT NULL,
  `Ask` tinyint(1) NOT NULL,
  `Answered` tinyint(1) NOT NULL,
  `AskedAt` date DEFAULT NULL,
  `AnsweredAt` date DEFAULT NULL,
  `EQuestion` mediumtext CHARACTER SET latin1,
  `Note` mediumtext,
  PRIMARY KEY (`Id`),
  KEY `FKanswerques22688` (`authorId`),
  KEY `FKanswerques645533` (`expertId`),
  KEY `FK_AUTHOR` (`authorId`),
  KEY `FK_EXPERT` (`expertId`),
  CONSTRAINT `FK_AUTHOR` FOREIGN KEY (`authorId`) REFERENCES `user` (`userId`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_EXPERT` FOREIGN KEY (`expertId`) REFERENCES `user` (`userId`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=979 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `appfile`
--

DROP TABLE IF EXISTS `appfile`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `appfile` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `conceptID` bigint(20) NOT NULL,
  `applicationID` bigint(20) DEFAULT NULL,
  `Url` varchar(255) DEFAULT NULL,
  `Predicate` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FKappfile511623` (`applicationID`),
  KEY `FKappfile132797` (`conceptID`),
  CONSTRAINT `FKappfile132797` FOREIGN KEY (`conceptID`) REFERENCES `concept` (`ID`),
  CONSTRAINT `FKappfile511623` FOREIGN KEY (`applicationID`) REFERENCES `application` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `appldict`
--

DROP TABLE IF EXISTS `appldict`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `appldict` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `conceptID` bigint(20) NOT NULL,
  `applicationID` bigint(20) DEFAULT NULL,
  `Url` varchar(255) DEFAULT NULL,
  `Predicate` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FKappldict646523` (`conceptID`),
  KEY `FKappldict267697` (`applicationID`),
  CONSTRAINT `FKappldict267697` FOREIGN KEY (`applicationID`) REFERENCES `application` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FKappldict646523` FOREIGN KEY (`conceptID`) REFERENCES `concept` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `applicant`
--

DROP TABLE IF EXISTS `applicant`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `applicant` (
  `applcntId` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdDate` datetime DEFAULT NULL,
  `updatedDate` datetime DEFAULT NULL,
  `address1` varchar(500) DEFAULT NULL,
  `address2` varchar(500) DEFAULT NULL,
  `zipcode` varchar(500) DEFAULT NULL,
  `AppName` varchar(255) DEFAULT NULL,
  `comment` varchar(500) DEFAULT NULL,
  `contactName` varchar(500) DEFAULT NULL,
  `email` varchar(500) DEFAULT NULL,
  `faxNo` varchar(500) DEFAULT NULL,
  `fileNumber` varchar(500) DEFAULT NULL,
  `licNo` varchar(500) DEFAULT NULL,
  `phoneNo` varchar(500) DEFAULT NULL,
  `regExpiryDate` date DEFAULT NULL,
  `registrationDate` date DEFAULT NULL,
  `state` varchar(255) DEFAULT NULL,
  `submitDate` date DEFAULT NULL,
  `website` varchar(500) DEFAULT NULL,
  `createdBy_userId` bigint(20) DEFAULT NULL,
  `updatedBy_userId` bigint(20) DEFAULT NULL,
  `CNTRY_ID` bigint(20) DEFAULT NULL,
  `applicantType_id` bigint(20) DEFAULT NULL,
  `zipaddress` varchar(32) DEFAULT NULL,
  `applicant_typeId` bigint(20) DEFAULT NULL,
  `countryId` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`applcntId`),
  KEY `FK_h1ljqh4uhcumv2c9ol88bc16l` (`createdBy_userId`),
  KEY `FK_79g0qko63tuaef9dg3ib6m006` (`updatedBy_userId`),
  KEY `FK_ja0psyeo9rkgvaqsnv50ni6r8` (`CNTRY_ID`),
  KEY `FK_1eoy018w3v8jryvw5va2okqq4` (`applicantType_id`),
  CONSTRAINT `FK_1eoy018w3v8jryvw5va2okqq4` FOREIGN KEY (`applicantType_id`) REFERENCES `applicant_type` (`id`),
  CONSTRAINT `FK_79g0qko63tuaef9dg3ib6m006` FOREIGN KEY (`updatedBy_userId`) REFERENCES `user` (`userId`),
  CONSTRAINT `FK_h1ljqh4uhcumv2c9ol88bc16l` FOREIGN KEY (`createdBy_userId`) REFERENCES `user` (`userId`),
  CONSTRAINT `FK_ja0psyeo9rkgvaqsnv50ni6r8` FOREIGN KEY (`CNTRY_ID`) REFERENCES `country` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=59 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `applicant_type`
--

DROP TABLE IF EXISTS `applicant_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `applicant_type` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdDate` datetime DEFAULT NULL,
  `updatedDate` datetime DEFAULT NULL,
  `name` varchar(500) DEFAULT NULL,
  `createdBy_userId` bigint(20) DEFAULT NULL,
  `updatedBy_userId` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_o945o5oxbtatjj4r92sgss43r` (`createdBy_userId`),
  KEY `FK_d2qkybwmpe1wahsqa1g7s9rq4` (`updatedBy_userId`),
  CONSTRAINT `FK_d2qkybwmpe1wahsqa1g7s9rq4` FOREIGN KEY (`updatedBy_userId`) REFERENCES `user` (`userId`),
  CONSTRAINT `FK_o945o5oxbtatjj4r92sgss43r` FOREIGN KEY (`createdBy_userId`) REFERENCES `user` (`userId`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `application`
--

DROP TABLE IF EXISTS `application`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `application` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `conceptID` bigint(20) NOT NULL,
  `lastupdate` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`ID`),
  KEY `FKapplicatio536545` (`conceptID`),
  CONSTRAINT `FKapplicatio536545` FOREIGN KEY (`conceptID`) REFERENCES `concept` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Temporary view structure for view `applications_active`
--

DROP TABLE IF EXISTS `applications_active`;
/*!50001 DROP VIEW IF EXISTS `applications_active`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `applications_active` AS SELECT 
 1 AS `dataID`,
 1 AS `url`,
 1 AS `come`*/;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `appointment`
--

DROP TABLE IF EXISTS `appointment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `appointment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdDate` datetime DEFAULT NULL,
  `updatedDate` datetime DEFAULT NULL,
  `allday` bit(1) NOT NULL,
  `end` date DEFAULT NULL,
  `start` date DEFAULT NULL,
  `tile` varchar(500) DEFAULT NULL,
  `createdBy_userId` bigint(20) DEFAULT NULL,
  `updatedBy_userId` bigint(20) DEFAULT NULL,
  `prodApplications_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_d71wloos0wl6w5llf5ci8jir2` (`createdBy_userId`),
  KEY `FK_7c8xsfkaknwo002cxnitnyjvj` (`updatedBy_userId`),
  KEY `FK_pxnwqwgmayowlqp7xgrxe8enx` (`prodApplications_id`),
  CONSTRAINT `FK_7c8xsfkaknwo002cxnitnyjvj` FOREIGN KEY (`updatedBy_userId`) REFERENCES `user` (`userId`),
  CONSTRAINT `FK_d71wloos0wl6w5llf5ci8jir2` FOREIGN KEY (`createdBy_userId`) REFERENCES `user` (`userId`),
  CONSTRAINT `FK_pxnwqwgmayowlqp7xgrxe8enx` FOREIGN KEY (`prodApplications_id`) REFERENCES `prodapplications` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `appperson`
--

DROP TABLE IF EXISTS `appperson`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `appperson` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `conceptID` bigint(20) NOT NULL,
  `applicationID` bigint(20) DEFAULT NULL,
  `Url` varchar(255) DEFAULT NULL,
  `Predicate` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FKappperson812879` (`applicationID`),
  KEY `FKappperson434053` (`conceptID`),
  CONSTRAINT `FKappperson434053` FOREIGN KEY (`conceptID`) REFERENCES `concept` (`ID`),
  CONSTRAINT `FKappperson812879` FOREIGN KEY (`applicationID`) REFERENCES `application` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `assembly`
--

DROP TABLE IF EXISTS `assembly`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `assembly` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `conceptID` bigint(20) NOT NULL,
  `Required` tinyint(1) NOT NULL,
  `Mult` tinyint(1) NOT NULL,
  `ReadOnly` tinyint(1) NOT NULL,
  `Url` varchar(255) DEFAULT NULL,
  `DictUrl` varchar(255) DEFAULT NULL,
  `Min` bigint(20) NOT NULL,
  `Max` bigint(20) NOT NULL,
  `FileTypes` varchar(255) DEFAULT NULL,
  `Discriminator` varchar(255) NOT NULL,
  `Row` int(11) NOT NULL,
  `Col` int(11) NOT NULL,
  `Ord` int(11) NOT NULL,
  `Clazz` varchar(255) DEFAULT NULL,
  `AuxDataUrl` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FKassembly126800` (`conceptID`),
  CONSTRAINT `FKassembly126800` FOREIGN KEY (`conceptID`) REFERENCES `concept` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=318 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Temporary view structure for view `assm_var`
--

DROP TABLE IF EXISTS `assm_var`;
/*!50001 DROP VIEW IF EXISTS `assm_var`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `assm_var` AS SELECT 
 1 AS `ID`,
 1 AS `assemblyID`,
 1 AS `Required`,
 1 AS `Mult`,
 1 AS `ReadOnly`,
 1 AS `Url`,
 1 AS `DictUrl`,
 1 AS `Min`,
 1 AS `Max`,
 1 AS `FileTypes`,
 1 AS `Discriminator`,
 1 AS `Row`,
 1 AS `Col`,
 1 AS `Ord`,
 1 AS `Clazz`,
 1 AS `nodeID`,
 1 AS `varNodeId`,
 1 AS `Active`,
 1 AS `propertyName`,
 1 AS `pref`,
 1 AS `lang`*/;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `atc`
--

DROP TABLE IF EXISTS `atc`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `atc` (
  `AtcCode` varchar(255) NOT NULL,
  `createdDate` datetime DEFAULT NULL,
  `updatedDate` datetime DEFAULT NULL,
  `AtcName` varchar(255) DEFAULT NULL,
  `legacyId` varchar(50) DEFAULT NULL,
  `level` int(11) NOT NULL,
  `createdBy_userId` bigint(20) DEFAULT NULL,
  `updatedBy_userId` bigint(20) DEFAULT NULL,
  `PARENT_ID` varchar(255) DEFAULT NULL,
  `atcAtcCode` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`AtcCode`),
  KEY `FK_pk0asis6bdscs56k927r4b151` (`createdBy_userId`),
  KEY `FK_6hv2rme8kmleatcde5jf2trbv` (`updatedBy_userId`),
  KEY `FK_od01qoevpot4acox9shsix6h3` (`PARENT_ID`),
  KEY `FKatc891905` (`atcAtcCode`),
  CONSTRAINT `FK_6hv2rme8kmleatcde5jf2trbv` FOREIGN KEY (`updatedBy_userId`) REFERENCES `user` (`userId`),
  CONSTRAINT `FK_od01qoevpot4acox9shsix6h3` FOREIGN KEY (`PARENT_ID`) REFERENCES `atc` (`AtcCode`),
  CONSTRAINT `FK_pk0asis6bdscs56k927r4b151` FOREIGN KEY (`createdBy_userId`) REFERENCES `user` (`userId`),
  CONSTRAINT `FKatc891905` FOREIGN KEY (`atcAtcCode`) REFERENCES `atc` (`AtcCode`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `attachment`
--

DROP TABLE IF EXISTS `attachment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `attachment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdDate` datetime DEFAULT NULL,
  `updatedDate` datetime DEFAULT NULL,
  `comment` varchar(500) DEFAULT NULL,
  `contentType` varchar(500) NOT NULL,
  `file` longblob NOT NULL,
  `fileName` varchar(500) NOT NULL,
  `regState` varchar(255) DEFAULT NULL,
  `title` varchar(500) NOT NULL,
  `createdBy_userId` bigint(20) DEFAULT NULL,
  `updatedBy_userId` bigint(20) DEFAULT NULL,
  `prodApplications_id` bigint(20) DEFAULT NULL,
  `reviewInfo_id` bigint(20) DEFAULT NULL,
  `uploadedBy_userId` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_83lmb65lp5ddn8shr0q2v8xco` (`createdBy_userId`),
  KEY `FK_cq1g6lieiasn56jxhl5mox6t4` (`updatedBy_userId`),
  KEY `FK_nql5sake9vw21xy4wk808714i` (`prodApplications_id`),
  KEY `FK_b946gi9pjvwvlnmwsvav33lgy` (`reviewInfo_id`),
  KEY `FK_7k9bj5xvwhmcvx3qbkal4jej4` (`uploadedBy_userId`),
  CONSTRAINT `FK_7k9bj5xvwhmcvx3qbkal4jej4` FOREIGN KEY (`uploadedBy_userId`) REFERENCES `user` (`userId`),
  CONSTRAINT `FK_83lmb65lp5ddn8shr0q2v8xco` FOREIGN KEY (`createdBy_userId`) REFERENCES `user` (`userId`),
  CONSTRAINT `FK_b946gi9pjvwvlnmwsvav33lgy` FOREIGN KEY (`reviewInfo_id`) REFERENCES `review_info` (`id`),
  CONSTRAINT `FK_cq1g6lieiasn56jxhl5mox6t4` FOREIGN KEY (`updatedBy_userId`) REFERENCES `user` (`userId`),
  CONSTRAINT `FK_nql5sake9vw21xy4wk808714i` FOREIGN KEY (`prodApplications_id`) REFERENCES `prodapplications` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `bill`
--

DROP TABLE IF EXISTS `bill`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bill` (
  `Id` bigint(20) NOT NULL AUTO_INCREMENT,
  `import_permitId` bigint(20) DEFAULT NULL,
  `optionsId2` bigint(20) DEFAULT NULL,
  `optionsId` bigint(20) DEFAULT NULL,
  `Amount` decimal(19,0) DEFAULT NULL,
  `Issued` int(11) DEFAULT NULL,
  `Discriminator` varchar(255) NOT NULL,
  `Number` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`Id`),
  KEY `FKbill4816` (`optionsId`),
  KEY `FKbill16217` (`optionsId2`),
  KEY `FKbill213603` (`import_permitId`),
  CONSTRAINT `FKbill16217` FOREIGN KEY (`optionsId2`) REFERENCES `options` (`Id`),
  CONSTRAINT `FKbill213603` FOREIGN KEY (`import_permitId`) REFERENCES `import_permit` (`Id`),
  CONSTRAINT `FKbill4816` FOREIGN KEY (`optionsId`) REFERENCES `options` (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `checklist`
--

DROP TABLE IF EXISTS `checklist`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `checklist` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `ord` int(11) DEFAULT NULL,
  `createdDate` datetime DEFAULT NULL,
  `updatedDate` datetime DEFAULT NULL,
  `gen_med` bit(1) DEFAULT NULL,
  `header` bit(1) DEFAULT NULL,
  `module` varchar(255) DEFAULT NULL,
  `moduleNo` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `new_med` bit(1) DEFAULT NULL,
  `recognized_med` bit(1) DEFAULT NULL,
  `renewal` bit(1) DEFAULT NULL,
  `createdBy_userId` bigint(20) DEFAULT NULL,
  `updatedBy_userId` bigint(20) DEFAULT NULL,
  `variation` bit(1) DEFAULT NULL,
  `majvar` bit(1) DEFAULT NULL,
  `Import_permitid` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_5sjtp7nsned1q2q9awuprr7y0` (`createdBy_userId`),
  KEY `FK_awlsw8coewod2pak5s7hx8h02` (`updatedBy_userId`),
  KEY `byRealOrder` (`ord`),
  CONSTRAINT `FK_5sjtp7nsned1q2q9awuprr7y0` FOREIGN KEY (`createdBy_userId`) REFERENCES `user` (`userId`),
  CONSTRAINT `FK_awlsw8coewod2pak5s7hx8h02` FOREIGN KEY (`updatedBy_userId`) REFERENCES `user` (`userId`)
) ENGINE=InnoDB AUTO_INCREMENT=759 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `checklistr2`
--

DROP TABLE IF EXISTS `checklistr2`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `checklistr2` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `activityID` bigint(20) NOT NULL,
  `applicationID` bigint(20) NOT NULL,
  `Question` varchar(255) DEFAULT NULL,
  `Answer` int(11) NOT NULL,
  `Comment` varchar(255) DEFAULT NULL,
  `dictItemID` bigint(20) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `FKchecklistr910937` (`applicationID`),
  KEY `FKchecklistr915013` (`activityID`),
  KEY `FKchecklistr541205` (`dictItemID`),
  CONSTRAINT `FKchecklistr541205` FOREIGN KEY (`dictItemID`) REFERENCES `concept` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FKchecklistr910937` FOREIGN KEY (`applicationID`) REFERENCES `concept` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FKchecklistr915013` FOREIGN KEY (`activityID`) REFERENCES `concept` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=7200 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `classification`
--

DROP TABLE IF EXISTS `classification`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `classification` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `conceptID2` bigint(20) NOT NULL,
  `conceptID` bigint(20) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `FKclassifica632547` (`conceptID`),
  KEY `FKclassifica907618` (`conceptID2`),
  CONSTRAINT `FKclassifica632547` FOREIGN KEY (`conceptID`) REFERENCES `concept` (`ID`),
  CONSTRAINT `FKclassifica907618` FOREIGN KEY (`conceptID2`) REFERENCES `concept` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `closure`
--

DROP TABLE IF EXISTS `closure`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `closure` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `childID` bigint(20) DEFAULT NULL,
  `parentID` bigint(20) DEFAULT NULL,
  `Level` int(11) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `FKclosure226963` (`parentID`),
  KEY `FKclosure121614` (`childID`),
  CONSTRAINT `FKclosure121614` FOREIGN KEY (`childID`) REFERENCES `concept` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FKclosure226963` FOREIGN KEY (`parentID`) REFERENCES `concept` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=302676 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `comment`
--

DROP TABLE IF EXISTS `comment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `comment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `comment` varchar(500) DEFAULT NULL,
  `comment_date` datetime DEFAULT NULL,
  `internal` bit(1) NOT NULL,
  `PROD_ID` bigint(20) NOT NULL,
  `userId` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_ord3tvf4dkynnvkbevpj0v029` (`PROD_ID`),
  KEY `FK_ivdqik6ejvf6mmp0i12rkkyx4` (`userId`),
  CONSTRAINT `FK_ivdqik6ejvf6mmp0i12rkkyx4` FOREIGN KEY (`userId`) REFERENCES `user` (`userId`),
  CONSTRAINT `FK_ord3tvf4dkynnvkbevpj0v029` FOREIGN KEY (`PROD_ID`) REFERENCES `prodapplications` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `company`
--

DROP TABLE IF EXISTS `company`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `company` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdDate` datetime DEFAULT NULL,
  `updatedDate` datetime DEFAULT NULL,
  `address1` varchar(500) DEFAULT NULL,
  `address2` varchar(500) DEFAULT NULL,
  `zipcode` varchar(500) DEFAULT NULL,
  `CompanyName` varchar(255) DEFAULT NULL,
  `contactName` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `faxNo` varchar(255) DEFAULT NULL,
  `gmp_cert_no` varchar(500) DEFAULT NULL,
  `gmpInsp` bit(1) NOT NULL,
  `gmp_insp_date` datetime DEFAULT NULL,
  `phoneNo` varchar(255) DEFAULT NULL,
  `siteNumber` varchar(255) DEFAULT NULL,
  `createdBy_userId` bigint(20) DEFAULT NULL,
  `updatedBy_userId` bigint(20) DEFAULT NULL,
  `CNTRY_ID` bigint(20) DEFAULT NULL,
  `zipaddress` varchar(32) DEFAULT NULL,
  `countryId` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_h7w1mkrsh1wcg5dkv6wrvam5m` (`CompanyName`),
  KEY `FK_p7rkb7t4fwke05w28j5fslof2` (`createdBy_userId`),
  KEY `FK_atwnvueify56bf3oienhjn7e4` (`updatedBy_userId`),
  KEY `FK_l5u1c6uj55wfp1cl3yr8839uf` (`CNTRY_ID`),
  KEY `FKcompany999210` (`countryId`),
  CONSTRAINT `FK_atwnvueify56bf3oienhjn7e4` FOREIGN KEY (`updatedBy_userId`) REFERENCES `user` (`userId`),
  CONSTRAINT `FK_l5u1c6uj55wfp1cl3yr8839uf` FOREIGN KEY (`CNTRY_ID`) REFERENCES `country` (`id`),
  CONSTRAINT `FK_p7rkb7t4fwke05w28j5fslof2` FOREIGN KEY (`createdBy_userId`) REFERENCES `user` (`userId`),
  CONSTRAINT `FKcompany999210` FOREIGN KEY (`countryId`) REFERENCES `country` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1361 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `concept`
--

DROP TABLE IF EXISTS `concept`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `concept` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Identifier` varchar(255) DEFAULT NULL,
  `Label` mediumtext,
  `Active` tinyint(1) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `identifier` (`Identifier`)
) ENGINE=InnoDB AUTO_INCREMENT=58483 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `contactpoint`
--

DROP TABLE IF EXISTS `contactpoint`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `contactpoint` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `spatialID` bigint(20) NOT NULL,
  `publicorganizationID` bigint(20) DEFAULT NULL,
  `webresourceID` bigint(20) DEFAULT NULL,
  `spatialdataID` bigint(20) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `FKcontactpoi663651` (`webresourceID`),
  KEY `FKcontactpoi140022` (`publicorganizationID`),
  KEY `FKcontactpoi736516` (`spatialdataID`),
  CONSTRAINT `FKcontactpoi140022` FOREIGN KEY (`publicorganizationID`) REFERENCES `publicorganization` (`ID`),
  CONSTRAINT `FKcontactpoi663651` FOREIGN KEY (`webresourceID`) REFERENCES `webresource` (`ID`),
  CONSTRAINT `FKcontactpoi736516` FOREIGN KEY (`spatialdataID`) REFERENCES `spatialdata` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `context`
--

DROP TABLE IF EXISTS `context`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `context` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=342 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `country`
--

DROP TABLE IF EXISTS `country`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `country` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdDate` datetime DEFAULT NULL,
  `updatedDate` datetime DEFAULT NULL,
  `CountryCD` varchar(255) DEFAULT NULL,
  `CountryName` varchar(255) DEFAULT NULL,
  `createdBy_userId` bigint(20) DEFAULT NULL,
  `updatedBy_userId` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_bj5mdwheiqd454439n6smwlrs` (`createdBy_userId`),
  KEY `FK_gp7gvu5amnv8ie6vhvu2pd3xg` (`updatedBy_userId`),
  CONSTRAINT `FK_bj5mdwheiqd454439n6smwlrs` FOREIGN KEY (`createdBy_userId`) REFERENCES `user` (`userId`),
  CONSTRAINT `FK_gp7gvu5amnv8ie6vhvu2pd3xg` FOREIGN KEY (`updatedBy_userId`) REFERENCES `user` (`userId`)
) ENGINE=InnoDB AUTO_INCREMENT=246 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `countrystructure`
--

DROP TABLE IF EXISTS `countrystructure`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `countrystructure` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdDate` datetime DEFAULT NULL,
  `updatedDate` datetime DEFAULT NULL,
  `STRUCTURE_LEVEL` int(11) DEFAULT NULL,
  `name1` varchar(255) DEFAULT NULL,
  `name2` varchar(255) DEFAULT NULL,
  `createdBy_userId` bigint(20) DEFAULT NULL,
  `updatedBy_userId` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_bgque5nkkpmftor35oaa71vva` (`createdBy_userId`),
  KEY `FK_2a17g2lia6i71fuwtfjms7dor` (`updatedBy_userId`),
  CONSTRAINT `FK_2a17g2lia6i71fuwtfjms7dor` FOREIGN KEY (`updatedBy_userId`) REFERENCES `user` (`userId`),
  CONSTRAINT `FK_bgque5nkkpmftor35oaa71vva` FOREIGN KEY (`createdBy_userId`) REFERENCES `user` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `criteria`
--

DROP TABLE IF EXISTS `criteria`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `criteria` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `ContextID` bigint(20) DEFAULT NULL,
  `Name` varchar(255) DEFAULT NULL,
  `Criteria` longtext,
  PRIMARY KEY (`ID`),
  KEY `FKCriteria69346` (`ContextID`),
  KEY `FKcriteria199763` (`ContextID`),
  CONSTRAINT `FKCriteria69346` FOREIGN KEY (`ContextID`) REFERENCES `context` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FKcriteria199763` FOREIGN KEY (`ContextID`) REFERENCES `context` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `currency`
--

DROP TABLE IF EXISTS `currency`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `currency` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdDate` datetime DEFAULT NULL,
  `updatedDate` datetime DEFAULT NULL,
  `currCD` varchar(30) NOT NULL,
  `currName` varchar(255) NOT NULL,
  `currSym` varchar(10) NOT NULL,
  `createdBy_userId` bigint(20) DEFAULT NULL,
  `updatedBy_userId` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK224BF011538C3791` (`createdBy_userId`),
  KEY `FK224BF0119A39331E` (`updatedBy_userId`),
  CONSTRAINT `FK224BF011538C3791` FOREIGN KEY (`createdBy_userId`) REFERENCES `user` (`userId`),
  CONSTRAINT `FK224BF0119A39331E` FOREIGN KEY (`updatedBy_userId`) REFERENCES `user` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `description`
--

DROP TABLE IF EXISTS `description`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `description` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Discriminator` varchar(255) NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `document`
--

DROP TABLE IF EXISTS `document`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `document` (
  `Id` bigint(20) NOT NULL AUTO_INCREMENT,
  `docTypeId` bigint(20) NOT NULL,
  `applicantapplcntId` bigint(19) DEFAULT NULL,
  `import_permitId` bigint(20) DEFAULT NULL,
  `SignNo` varchar(255) DEFAULT NULL,
  `SignDate` date DEFAULT NULL,
  `SingPerson` varchar(255) DEFAULT NULL,
  `Attachment` longblob NOT NULL,
  `Discriminator` varchar(255) NOT NULL,
  `Annotation` mediumtext,
  `ContentType` varchar(255) DEFAULT NULL,
  `FileSize` bigint(20) NOT NULL,
  `ContentEncoding` varchar(255) DEFAULT NULL,
  `FileName` varchar(255) DEFAULT NULL,
  `authorId` bigint(19) DEFAULT NULL,
  `Modified` datetime DEFAULT NULL,
  PRIMARY KEY (`Id`),
  KEY `FKdocument571128` (`docTypeId`),
  KEY `FKdocument60149` (`import_permitId`),
  KEY `FKdocument999664` (`applicantapplcntId`),
  KEY `FKdocument463464` (`authorId`),
  KEY `FKdocument363368` (`authorId`),
  CONSTRAINT `FKdocument363368` FOREIGN KEY (`authorId`) REFERENCES `user` (`userId`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FKdocument463464` FOREIGN KEY (`authorId`) REFERENCES `user` (`userId`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FKdocument571128` FOREIGN KEY (`docTypeId`) REFERENCES `options` (`Id`),
  CONSTRAINT `FKdocument60149` FOREIGN KEY (`import_permitId`) REFERENCES `import_permit` (`Id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FKdocument999664` FOREIGN KEY (`applicantapplcntId`) REFERENCES `applicant` (`applcntId`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `dosform`
--

DROP TABLE IF EXISTS `dosform`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `dosform` (
  `uid` bigint(20) NOT NULL AUTO_INCREMENT,
  `Dosageform` varchar(255) DEFAULT NULL,
  `Discontinued` bit(1) DEFAULT NULL,
  `sampleSize` int(11) DEFAULT NULL,
  PRIMARY KEY (`uid`)
) ENGINE=InnoDB AUTO_INCREMENT=300 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `dosuom`
--

DROP TABLE IF EXISTS `dosuom`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `dosuom` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `Discontinued` bit(1) DEFAULT NULL,
  `UOM` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `drug_price`
--

DROP TABLE IF EXISTS `drug_price`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `drug_price` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdDate` datetime DEFAULT NULL,
  `updatedDate` datetime DEFAULT NULL,
  `drugName` varchar(255) NOT NULL,
  `msrp` varchar(255) NOT NULL,
  `createdBy_userId` bigint(20) DEFAULT NULL,
  `updatedBy_userId` bigint(20) DEFAULT NULL,
  `pricing_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_cnhwj3ahdf6iwki2e76i4yqoh` (`createdBy_userId`),
  KEY `FK_hu3l08be32966womycy3kt25r` (`updatedBy_userId`),
  KEY `FK_5q1l08ucxpm8dcm9ra86d7gua` (`pricing_id`),
  CONSTRAINT `FK_5q1l08ucxpm8dcm9ra86d7gua` FOREIGN KEY (`pricing_id`) REFERENCES `pricing` (`id`) ON DELETE CASCADE,
  CONSTRAINT `FK_cnhwj3ahdf6iwki2e76i4yqoh` FOREIGN KEY (`createdBy_userId`) REFERENCES `user` (`userId`),
  CONSTRAINT `FK_hu3l08be32966womycy3kt25r` FOREIGN KEY (`updatedBy_userId`) REFERENCES `user` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `error_log`
--

DROP TABLE IF EXISTS `error_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `error_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `ajaxRequest` varchar(500) DEFAULT NULL,
  `browserName` varchar(500) DEFAULT NULL,
  `errorDate` date NOT NULL,
  `exceptionMessage` varchar(1000) DEFAULT NULL,
  `exceptionType` varchar(500) DEFAULT NULL,
  `requestURI` varchar(500) DEFAULT NULL,
  `stackTrace` varchar(255) DEFAULT NULL,
  `statusCode` varchar(500) DEFAULT NULL,
  `userIP` varchar(500) DEFAULT NULL,
  `USER_ID` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_3mk1pi6vykcq2uigkj9iulk13` (`USER_ID`),
  CONSTRAINT `FK_3mk1pi6vykcq2uigkj9iulk13` FOREIGN KEY (`USER_ID`) REFERENCES `user` (`userId`)
) ENGINE=InnoDB AUTO_INCREMENT=3868 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `eventlog`
--

DROP TABLE IF EXISTS `eventlog`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `eventlog` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Email` varchar(255) DEFAULT NULL,
  `Source` varchar(255) DEFAULT NULL,
  `Message` varchar(255) DEFAULT NULL,
  `ConceptId` bigint(20) NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `excipient`
--

DROP TABLE IF EXISTS `excipient`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `excipient` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdDate` datetime DEFAULT NULL,
  `updatedDate` datetime DEFAULT NULL,
  `Name` varchar(255) DEFAULT NULL,
  `createdBy_userId` bigint(20) DEFAULT NULL,
  `updatedBy_userId` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_fps1j0q1c7667wmusv5cie67u` (`createdBy_userId`),
  KEY `FK_7pbcqwgeihwib70guabeklxc2` (`updatedBy_userId`),
  CONSTRAINT `FK_7pbcqwgeihwib70guabeklxc2` FOREIGN KEY (`updatedBy_userId`) REFERENCES `user` (`userId`),
  CONSTRAINT `FK_fps1j0q1c7667wmusv5cie67u` FOREIGN KEY (`createdBy_userId`) REFERENCES `user` (`userId`)
) ENGINE=InnoDB AUTO_INCREMENT=1040 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `fee_sch`
--

DROP TABLE IF EXISTS `fee_sch`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `fee_sch` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdDate` datetime DEFAULT NULL,
  `updatedDate` datetime DEFAULT NULL,
  `AppType` varchar(255) NOT NULL,
  `endDate` date DEFAULT NULL,
  `fee` varchar(255) NOT NULL,
  `preScreenFee` varchar(255) NOT NULL,
  `startDate` date DEFAULT NULL,
  `totalFee` varchar(255) NOT NULL,
  `createdBy_userId` bigint(20) DEFAULT NULL,
  `updatedBy_userId` bigint(20) DEFAULT NULL,
  `labFee` varchar(45) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_iu5ktjc25bns87w99msajcfy7` (`createdBy_userId`),
  KEY `FK_i5eb94vrvm7ukf9tb6sjxeyk` (`updatedBy_userId`),
  CONSTRAINT `FK_i5eb94vrvm7ukf9tb6sjxeyk` FOREIGN KEY (`updatedBy_userId`) REFERENCES `user` (`userId`),
  CONSTRAINT `FK_iu5ktjc25bns87w99msajcfy7` FOREIGN KEY (`createdBy_userId`) REFERENCES `user` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `filelinks`
--

DROP TABLE IF EXISTS `filelinks`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `filelinks` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `conceptID` bigint(20) NOT NULL,
  `fileresourceID` bigint(20) DEFAULT NULL,
  `Url` varchar(255) DEFAULT NULL,
  `Predicate` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FKfilelinks383303` (`fileresourceID`),
  KEY `FKfilelinks318044` (`conceptID`),
  CONSTRAINT `FKfilelinks318044` FOREIGN KEY (`conceptID`) REFERENCES `concept` (`ID`),
  CONSTRAINT `FKfilelinks383303` FOREIGN KEY (`fileresourceID`) REFERENCES `fileresource` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `fileresource`
--

DROP TABLE IF EXISTS `fileresource`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `fileresource` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `conceptID` bigint(20) NOT NULL,
  `FileSize` bigint(20) NOT NULL,
  `File` longblob NOT NULL,
  `ChangedAt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `dictconceptID` bigint(20) NOT NULL,
  `Mediatype` varchar(255) DEFAULT NULL,
  `activityDataID` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FKfileresour477120` (`conceptID`),
  KEY `FKfileresour491212` (`dictconceptID`),
  KEY `FKfileresour258199` (`activityDataID`),
  CONSTRAINT `FKfileresour258199` FOREIGN KEY (`activityDataID`) REFERENCES `concept` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FKfileresour477120` FOREIGN KEY (`conceptID`) REFERENCES `concept` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FKfileresour491212` FOREIGN KEY (`dictconceptID`) REFERENCES `concept` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=937 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `filetemplate`
--

DROP TABLE IF EXISTS `filetemplate`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `filetemplate` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `contentType` varchar(500) DEFAULT NULL,
  `file` longblob,
  `fileName` varchar(500) DEFAULT NULL,
  `templateType` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `foreign_appl_status`
--

DROP TABLE IF EXISTS `foreign_appl_status`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `foreign_appl_status` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdDate` datetime DEFAULT NULL,
  `updatedDate` datetime DEFAULT NULL,
  `foreignAppStatusType` varchar(255) DEFAULT NULL,
  `mktAuthCert` varchar(255) DEFAULT NULL,
  `mktAuthDate` date DEFAULT NULL,
  `mktAuthHolder` varchar(255) DEFAULT NULL,
  `prodName` varchar(255) DEFAULT NULL,
  `createdBy_userId` bigint(20) DEFAULT NULL,
  `updatedBy_userId` bigint(20) DEFAULT NULL,
  `CNTRY_ID` bigint(20) DEFAULT NULL,
  `PROD_APP_ID` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_o5dclt67frj1i7uwuj4u9fm5j` (`createdBy_userId`),
  KEY `FK_6nje6lquqkihnmxdbn4elfy9q` (`updatedBy_userId`),
  KEY `FK_b20dgllkwmxqtfhsr8n38lm4t` (`CNTRY_ID`),
  KEY `FK_6hvcbw9utev7uiruy592lt5l6` (`PROD_APP_ID`),
  CONSTRAINT `FK_6hvcbw9utev7uiruy592lt5l6` FOREIGN KEY (`PROD_APP_ID`) REFERENCES `prodapplications` (`id`) ON DELETE CASCADE,
  CONSTRAINT `FK_6nje6lquqkihnmxdbn4elfy9q` FOREIGN KEY (`updatedBy_userId`) REFERENCES `user` (`userId`),
  CONSTRAINT `FK_b20dgllkwmxqtfhsr8n38lm4t` FOREIGN KEY (`CNTRY_ID`) REFERENCES `country` (`id`),
  CONSTRAINT `FK_o5dclt67frj1i7uwuj4u9fm5j` FOREIGN KEY (`createdBy_userId`) REFERENCES `user` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `history`
--

DROP TABLE IF EXISTS `history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `history` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `applicationID` bigint(20) NOT NULL,
  `activityID` bigint(20) DEFAULT NULL,
  `Come` datetime DEFAULT NULL,
  `Go` datetime DEFAULT NULL,
  `activityDataID` bigint(20) DEFAULT NULL,
  `applDataID` bigint(20) NOT NULL,
  `applConfigID` bigint(20) NOT NULL,
  `applDictID` bigint(20) NOT NULL,
  `actConfigID` bigint(20) DEFAULT NULL,
  `DataUrl` varchar(255) DEFAULT NULL,
  `PrevNotes` mediumtext,
  `Cancelled` tinyint(1) NOT NULL,
  `Expire` date DEFAULT NULL,
  `CreatedAt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`ID`),
  KEY `FKhistory387311` (`activityID`),
  KEY `FKhistory608612` (`applicationID`),
  KEY `FKhistory19325` (`activityDataID`),
  KEY `FKhistory877482` (`applDataID`),
  KEY `FKhistory777469` (`applDictID`),
  KEY `FKhistory733279` (`applConfigID`),
  KEY `FKhistory555751` (`actConfigID`),
  CONSTRAINT `FKhistory19325` FOREIGN KEY (`activityDataID`) REFERENCES `concept` (`ID`) ON DELETE SET NULL ON UPDATE SET NULL,
  CONSTRAINT `FKhistory387311` FOREIGN KEY (`activityID`) REFERENCES `concept` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FKhistory555751` FOREIGN KEY (`actConfigID`) REFERENCES `concept` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FKhistory608612` FOREIGN KEY (`applicationID`) REFERENCES `concept` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FKhistory733279` FOREIGN KEY (`applConfigID`) REFERENCES `concept` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FKhistory777469` FOREIGN KEY (`applDictID`) REFERENCES `concept` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FKhistory877482` FOREIGN KEY (`applDataID`) REFERENCES `concept` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1519 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `import_permit`
--

DROP TABLE IF EXISTS `import_permit`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `import_permit` (
  `Id` bigint(20) NOT NULL AUTO_INCREMENT,
  `currency` bigint(20) DEFAULT NULL,
  `paymentmode` bigint(20) DEFAULT NULL,
  `consignee` bigint(20) DEFAULT NULL,
  `supplier` bigint(20) DEFAULT NULL,
  `statusId` bigint(20) DEFAULT NULL,
  `moduleId` bigint(20) DEFAULT NULL,
  `transportId` bigint(20) DEFAULT NULL,
  `typeId` bigint(20) DEFAULT NULL,
  `portId` bigint(20) DEFAULT NULL,
  `incotermsId` bigint(20) DEFAULT NULL,
  `applicantapplcntId` bigint(19) DEFAULT NULL,
  `Expiry_date` date DEFAULT NULL,
  `Freight_cost` decimal(19,0) DEFAULT NULL,
  `PipNumber` varchar(255) DEFAULT NULL,
  `ProformaNumber` varchar(255) DEFAULT NULL,
  `Remark` longtext,
  `Insurance` decimal(19,0) DEFAULT NULL,
  `Requested_date` date DEFAULT NULL,
  `OtherExpences` decimal(19,0) DEFAULT NULL,
  `ProformaFile` longblob,
  `payerId` bigint(20) DEFAULT NULL,
  `orderTypeId` bigint(20) DEFAULT NULL,
  `inspectorId` bigint(19) DEFAULT NULL,
  `Auth_date` date DEFAULT NULL,
  `Validation_date` date DEFAULT NULL,
  `optionsId` bigint(20) DEFAULT NULL,
  `approverId` bigint(19) DEFAULT NULL,
  `ApprovalDate` date DEFAULT NULL,
  PRIMARY KEY (`Id`),
  KEY `FKimport_per505376` (`applicantapplcntId`),
  KEY `FKimport_per256381` (`incotermsId`),
  KEY `FKimport_per270439` (`portId`),
  KEY `FKimport_per948379` (`typeId`),
  KEY `FKimport_per500555` (`transportId`),
  KEY `FKimport_per495244` (`moduleId`),
  KEY `FKimport_per525297` (`statusId`),
  KEY `FKimport_per553969` (`supplier`),
  KEY `FKimport_per495280` (`consignee`),
  KEY `FKimport_per112295` (`paymentmode`),
  KEY `FKimport_per816052` (`currency`),
  KEY `FKimport_per843838` (`payerId`),
  KEY `FKimport_per457890` (`orderTypeId`),
  KEY `FKimport_per632597` (`inspectorId`),
  KEY `FKimport_per763894` (`optionsId`),
  KEY `FKimport_per188374` (`approverId`),
  CONSTRAINT `FKimport_per112295` FOREIGN KEY (`paymentmode`) REFERENCES `options` (`Id`),
  CONSTRAINT `FKimport_per188374` FOREIGN KEY (`approverId`) REFERENCES `user` (`userId`),
  CONSTRAINT `FKimport_per256381` FOREIGN KEY (`incotermsId`) REFERENCES `options` (`Id`),
  CONSTRAINT `FKimport_per270439` FOREIGN KEY (`portId`) REFERENCES `options` (`Id`),
  CONSTRAINT `FKimport_per457890` FOREIGN KEY (`orderTypeId`) REFERENCES `options` (`Id`),
  CONSTRAINT `FKimport_per495244` FOREIGN KEY (`moduleId`) REFERENCES `options` (`Id`),
  CONSTRAINT `FKimport_per495280` FOREIGN KEY (`consignee`) REFERENCES `options` (`Id`),
  CONSTRAINT `FKimport_per500555` FOREIGN KEY (`transportId`) REFERENCES `options` (`Id`),
  CONSTRAINT `FKimport_per505376` FOREIGN KEY (`applicantapplcntId`) REFERENCES `applicant` (`applcntId`),
  CONSTRAINT `FKimport_per525297` FOREIGN KEY (`statusId`) REFERENCES `options` (`Id`),
  CONSTRAINT `FKimport_per553969` FOREIGN KEY (`supplier`) REFERENCES `options` (`Id`),
  CONSTRAINT `FKimport_per632597` FOREIGN KEY (`inspectorId`) REFERENCES `user` (`userId`),
  CONSTRAINT `FKimport_per763894` FOREIGN KEY (`optionsId`) REFERENCES `options` (`Id`),
  CONSTRAINT `FKimport_per816052` FOREIGN KEY (`currency`) REFERENCES `options` (`Id`),
  CONSTRAINT `FKimport_per843838` FOREIGN KEY (`payerId`) REFERENCES `options` (`Id`),
  CONSTRAINT `FKimport_per948379` FOREIGN KEY (`typeId`) REFERENCES `options` (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `import_permit_detail`
--

DROP TABLE IF EXISTS `import_permit_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `import_permit_detail` (
  `Id` bigint(20) NOT NULL AUTO_INCREMENT,
  `import_permitId` bigint(20) DEFAULT NULL,
  `productid` bigint(19) NOT NULL,
  `Units` decimal(19,0) DEFAULT NULL,
  `Price` decimal(19,0) DEFAULT NULL,
  PRIMARY KEY (`Id`),
  KEY `FKimport_per362669` (`productid`),
  KEY `FKimport_per117443` (`import_permitId`),
  CONSTRAINT `FKimport_per117443` FOREIGN KEY (`import_permitId`) REFERENCES `import_permit` (`Id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FKimport_per362669` FOREIGN KEY (`productid`) REFERENCES `product` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `import_permit_user`
--

DROP TABLE IF EXISTS `import_permit_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `import_permit_user` (
  `import_permitId` bigint(20) NOT NULL,
  `useruserId` bigint(19) NOT NULL,
  PRIMARY KEY (`import_permitId`,`useruserId`),
  KEY `FKimport_per394279` (`useruserId`),
  CONSTRAINT `FKimport_per394279` FOREIGN KEY (`useruserId`) REFERENCES `user` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `inn`
--

DROP TABLE IF EXISTS `inn`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `inn` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdDate` datetime DEFAULT NULL,
  `updatedDate` datetime DEFAULT NULL,
  `Name` varchar(255) DEFAULT NULL,
  `createdBy_userId` bigint(20) DEFAULT NULL,
  `updatedBy_userId` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_hpqx64sggl2jtywduqm67vqto` (`createdBy_userId`),
  KEY `FK_2d70y17plo1wyv7ehjppesk4h` (`updatedBy_userId`),
  CONSTRAINT `FK_2d70y17plo1wyv7ehjppesk4h` FOREIGN KEY (`updatedBy_userId`) REFERENCES `user` (`userId`),
  CONSTRAINT `FK_hpqx64sggl2jtywduqm67vqto` FOREIGN KEY (`createdBy_userId`) REFERENCES `user` (`userId`)
) ENGINE=InnoDB AUTO_INCREMENT=10332 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `invoice`
--

DROP TABLE IF EXISTS `invoice`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `invoice` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdDate` datetime DEFAULT NULL,
  `updatedDate` datetime DEFAULT NULL,
  `curr_expiry_date` date NOT NULL,
  `invoice_amt` varchar(100) NOT NULL,
  `invoiceFile` longblob,
  `invoice_number` varchar(255) DEFAULT NULL,
  `invoice_type` varchar(255) NOT NULL,
  `issue_date` date NOT NULL,
  `new_expiry_date` date NOT NULL,
  `payment_status` varchar(255) NOT NULL,
  `renewal_date` date DEFAULT NULL,
  `createdBy_userId` bigint(20) DEFAULT NULL,
  `updatedBy_userId` bigint(20) DEFAULT NULL,
  `payment_id` bigint(20) DEFAULT NULL,
  `prod_app_id` bigint(20) NOT NULL,
  `import_permitId` bigint(20) DEFAULT NULL,
  `import_permitId4` bigint(20) DEFAULT NULL,
  `import_permitId3` bigint(20) DEFAULT NULL,
  `import_permitId2` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_j84g8qevritq3n2lckpv7ven0` (`createdBy_userId`),
  KEY `FK_jtmqv7tbef2ch5bqmnx5ry67f` (`updatedBy_userId`),
  KEY `FK_5vvlr4mmb6jbwiu4dyqwevd0d` (`payment_id`),
  KEY `FK_miop6lod9r2ohkvtxcgduoa4p` (`prod_app_id`),
  KEY `FKinvoice559266` (`payment_id`),
  KEY `FKinvoice905066` (`import_permitId4`),
  CONSTRAINT `FK_5vvlr4mmb6jbwiu4dyqwevd0d` FOREIGN KEY (`payment_id`) REFERENCES `payment` (`id`),
  CONSTRAINT `FK_j84g8qevritq3n2lckpv7ven0` FOREIGN KEY (`createdBy_userId`) REFERENCES `user` (`userId`),
  CONSTRAINT `FK_jtmqv7tbef2ch5bqmnx5ry67f` FOREIGN KEY (`updatedBy_userId`) REFERENCES `user` (`userId`),
  CONSTRAINT `FK_miop6lod9r2ohkvtxcgduoa4p` FOREIGN KEY (`prod_app_id`) REFERENCES `prodapplications` (`id`),
  CONSTRAINT `FKinvoice559266` FOREIGN KEY (`payment_id`) REFERENCES `payment` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FKinvoice905066` FOREIGN KEY (`import_permitId4`) REFERENCES `import_permit` (`Id`) ON DELETE SET NULL ON UPDATE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `letter`
--

DROP TABLE IF EXISTS `letter`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `letter` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdDate` datetime DEFAULT NULL,
  `updatedDate` datetime DEFAULT NULL,
  `body` varchar(2000) NOT NULL,
  `letter_type` varchar(255) DEFAULT NULL,
  `subject` varchar(255) NOT NULL,
  `title` varchar(255) NOT NULL,
  `createdBy_userId` bigint(20) DEFAULT NULL,
  `updatedBy_userId` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_45k67hv1xygkl1sreu51l6wp6` (`letter_type`),
  KEY `FK_1hoaxabw3ifkbmsaib571ywus` (`createdBy_userId`),
  KEY `FK_qjnms2uv07jytvn2py12w30dx` (`updatedBy_userId`),
  CONSTRAINT `FK_1hoaxabw3ifkbmsaib571ywus` FOREIGN KEY (`createdBy_userId`) REFERENCES `user` (`userId`),
  CONSTRAINT `FK_qjnms2uv07jytvn2py12w30dx` FOREIGN KEY (`updatedBy_userId`) REFERENCES `user` (`userId`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `literal`
--

DROP TABLE IF EXISTS `literal`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `literal` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `contactpointID2` bigint(20) DEFAULT NULL,
  `postNameID` bigint(20) DEFAULT NULL,
  `fullAddrID` bigint(20) DEFAULT NULL,
  `locNameID` bigint(20) DEFAULT NULL,
  `conceptPrefLblID` bigint(20) DEFAULT NULL,
  `thrID` bigint(20) DEFAULT NULL,
  `resource_bundleId` bigint(20) NOT NULL,
  `addrAreaID` bigint(20) DEFAULT NULL,
  `fileresourceID` bigint(20) DEFAULT NULL,
  `addressID` bigint(20) DEFAULT NULL,
  `contactpointID` bigint(20) DEFAULT NULL,
  `descriptionID` bigint(20) DEFAULT NULL,
  `conceptAltLblID` bigint(20) DEFAULT NULL,
  `Value` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FKliteral35906` (`conceptAltLblID`),
  KEY `FKliteral461488` (`descriptionID`),
  KEY `FKliteral686093` (`addressID`),
  KEY `FKliteral677369` (`fileresourceID`),
  KEY `FKliteral163609` (`addrAreaID`),
  KEY `FKliteral261160` (`resource_bundleId`),
  KEY `FKliteral891391` (`thrID`),
  KEY `FKliteral206510` (`conceptPrefLblID`),
  KEY `FKliteral158055` (`locNameID`),
  KEY `FKliteral867281` (`fullAddrID`),
  KEY `FKliteral52241` (`postNameID`),
  KEY `FKliteral595966` (`contactpointID2`),
  CONSTRAINT `FKliteral158055` FOREIGN KEY (`locNameID`) REFERENCES `address` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FKliteral163609` FOREIGN KEY (`addrAreaID`) REFERENCES `address` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FKliteral206510` FOREIGN KEY (`conceptPrefLblID`) REFERENCES `concept` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FKliteral261160` FOREIGN KEY (`resource_bundleId`) REFERENCES `resource_bundle` (`id`),
  CONSTRAINT `FKliteral35906` FOREIGN KEY (`conceptAltLblID`) REFERENCES `concept` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FKliteral461488` FOREIGN KEY (`descriptionID`) REFERENCES `description` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FKliteral52241` FOREIGN KEY (`postNameID`) REFERENCES `address` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FKliteral595966` FOREIGN KEY (`contactpointID2`) REFERENCES `contactpoint` (`ID`),
  CONSTRAINT `FKliteral677369` FOREIGN KEY (`fileresourceID`) REFERENCES `fileresource` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FKliteral686093` FOREIGN KEY (`addressID`) REFERENCES `address` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FKliteral867281` FOREIGN KEY (`fullAddrID`) REFERENCES `address` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FKliteral891391` FOREIGN KEY (`thrID`) REFERENCES `address` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `mail`
--

DROP TABLE IF EXISTS `mail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `mail` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `mail_date` datetime NOT NULL,
  `mailto` varchar(255) DEFAULT NULL,
  `message` varchar(500) DEFAULT NULL,
  `subject` varchar(255) DEFAULT NULL,
  `PROD_APP_ID` bigint(20) NOT NULL,
  `USER_ID` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_bl5wluqlfbxd6ynp828nkeu25` (`PROD_APP_ID`),
  KEY `FK_5wjnm9x4n2nyh3xhawrr09su5` (`USER_ID`),
  CONSTRAINT `FK_5wjnm9x4n2nyh3xhawrr09su5` FOREIGN KEY (`USER_ID`) REFERENCES `user` (`userId`),
  CONSTRAINT `FK_bl5wluqlfbxd6ynp828nkeu25` FOREIGN KEY (`PROD_APP_ID`) REFERENCES `prodapplications` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `notification`
--

DROP TABLE IF EXISTS `notification`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notification` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `userTo` bigint(19) NOT NULL,
  `userFrom` bigint(19) NOT NULL,
  `PipId` bigint(20) NOT NULL,
  `Created` date DEFAULT NULL,
  `Subject` varchar(255) DEFAULT NULL,
  `Discriminator` varchar(255) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `FKnotificati87783` (`userFrom`),
  KEY `FKnotificati230223` (`userTo`),
  KEY `FKnotificati389060` (`PipId`),
  CONSTRAINT `FKnotificati230223` FOREIGN KEY (`userTo`) REFERENCES `user` (`userId`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FKnotificati389060` FOREIGN KEY (`PipId`) REFERENCES `import_permit` (`Id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FKnotificati87783` FOREIGN KEY (`userFrom`) REFERENCES `user` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `openinghoursspec`
--

DROP TABLE IF EXISTS `openinghoursspec`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `openinghoursspec` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `contactpointID` bigint(20) DEFAULT NULL,
  `Dow` int(11) NOT NULL,
  `Closes` int(11) NOT NULL,
  `Opens` int(11) NOT NULL,
  `ValidFrom` date DEFAULT NULL,
  `ValidThrough` date DEFAULT NULL,
  `publicorganizationID` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FKopeninghou195842` (`contactpointID`),
  KEY `FKopeninghou161452` (`publicorganizationID`),
  CONSTRAINT `FKopeninghou161452` FOREIGN KEY (`publicorganizationID`) REFERENCES `publicorganization` (`ID`),
  CONSTRAINT `FKopeninghou195842` FOREIGN KEY (`contactpointID`) REFERENCES `contactpoint` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `options`
--

DROP TABLE IF EXISTS `options`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `options` (
  `Id` bigint(20) NOT NULL AUTO_INCREMENT,
  `Description` mediumtext CHARACTER SET latin1,
  `Code` varchar(255) CHARACTER SET latin1 DEFAULT NULL,
  `Discriminator` varchar(255) CHARACTER SET latin1 NOT NULL,
  `Active` tinyint(1) NOT NULL,
  `applicantapplcntId3` bigint(19) DEFAULT NULL,
  `applicantapplcntId2` bigint(19) DEFAULT NULL,
  `companyid` bigint(19) DEFAULT NULL,
  `applicantapplcntId` bigint(19) DEFAULT NULL,
  `AttachToPIP` tinyint(1) DEFAULT NULL,
  `AttachToApplicant` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`Id`),
  KEY `FKoptions774041` (`applicantapplcntId`),
  KEY `FKoptions806497` (`companyid`),
  KEY `FKoptions470316` (`applicantapplcntId2`),
  KEY `FKoptions470317` (`applicantapplcntId3`),
  CONSTRAINT `FKoptions470316` FOREIGN KEY (`applicantapplcntId2`) REFERENCES `applicant` (`applcntId`),
  CONSTRAINT `FKoptions470317` FOREIGN KEY (`applicantapplcntId3`) REFERENCES `applicant` (`applcntId`),
  CONSTRAINT `FKoptions774041` FOREIGN KEY (`applicantapplcntId`) REFERENCES `applicant` (`applcntId`),
  CONSTRAINT `FKoptions806497` FOREIGN KEY (`companyid`) REFERENCES `company` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=277 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `orgrole`
--

DROP TABLE IF EXISTS `orgrole`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orgrole` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `classificationID` bigint(20) NOT NULL,
  `publicorganizationID` bigint(20) DEFAULT NULL,
  `conceptID` bigint(20) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `FKorgrole285857` (`publicorganizationID`),
  KEY `FKorgrole673690` (`conceptID`),
  CONSTRAINT `FKorgrole285857` FOREIGN KEY (`publicorganizationID`) REFERENCES `publicorganization` (`ID`),
  CONSTRAINT `FKorgrole673690` FOREIGN KEY (`conceptID`) REFERENCES `concept` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `payment`
--

DROP TABLE IF EXISTS `payment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `payment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdDate` datetime DEFAULT NULL,
  `updatedDate` datetime DEFAULT NULL,
  `payment_amt` varchar(255) DEFAULT NULL,
  `payment_date` date DEFAULT NULL,
  `receipt_no` varchar(255) DEFAULT NULL,
  `tracking_no` varchar(255) DEFAULT NULL,
  `createdBy_userId` bigint(20) DEFAULT NULL,
  `updatedBy_userId` bigint(20) DEFAULT NULL,
  `inv_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_pdb25v3bd8ao1ah9hlbki2ijy` (`createdBy_userId`),
  KEY `FK_efj357ctqyai171q6bxygs4h8` (`updatedBy_userId`),
  KEY `FK_rqsxpoduqypoaojwikm2h0lu2` (`inv_id`),
  KEY `FKpayment188179` (`inv_id`),
  CONSTRAINT `FK_efj357ctqyai171q6bxygs4h8` FOREIGN KEY (`updatedBy_userId`) REFERENCES `user` (`userId`),
  CONSTRAINT `FK_pdb25v3bd8ao1ah9hlbki2ijy` FOREIGN KEY (`createdBy_userId`) REFERENCES `user` (`userId`),
  CONSTRAINT `FK_rqsxpoduqypoaojwikm2h0lu2` FOREIGN KEY (`inv_id`) REFERENCES `invoice` (`id`),
  CONSTRAINT `FKpayment188179` FOREIGN KEY (`inv_id`) REFERENCES `invoice` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Temporary view structure for view `personlist`
--

DROP TABLE IF EXISTS `personlist`;
/*!50001 DROP VIEW IF EXISTS `personlist`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `personlist` AS SELECT 
 1 AS `ID`,
 1 AS `appldataid`,
 1 AS `personrooturl`,
 1 AS `lang`,
 1 AS `pref`*/;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `pharmacy_checklist`
--

DROP TABLE IF EXISTS `pharmacy_checklist`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pharmacy_checklist` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdDate` datetime DEFAULT NULL,
  `updatedDate` datetime DEFAULT NULL,
  `staffComment` varchar(500) DEFAULT NULL,
  `staffValue` bit(1) NOT NULL,
  `value` bit(1) NOT NULL,
  `createdBy_userId` bigint(20) DEFAULT NULL,
  `updatedBy_userId` bigint(20) DEFAULT NULL,
  `pharmacysite_id` bigint(20) NOT NULL,
  `sitechecklist_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_6tyfd8s14elv0jk0c4o3kdhb7` (`createdBy_userId`),
  KEY `FK_gnuqp8a0pkfigans5r9eel0j7` (`updatedBy_userId`),
  KEY `FK_vo7oth9n9louxqf4wj8bpfvd` (`pharmacysite_id`),
  KEY `FK_dqxx8kihyd3nthb6224rtgd6p` (`sitechecklist_id`),
  CONSTRAINT `FK_6tyfd8s14elv0jk0c4o3kdhb7` FOREIGN KEY (`createdBy_userId`) REFERENCES `user` (`userId`),
  CONSTRAINT `FK_dqxx8kihyd3nthb6224rtgd6p` FOREIGN KEY (`sitechecklist_id`) REFERENCES `sitechecklist` (`id`),
  CONSTRAINT `FK_gnuqp8a0pkfigans5r9eel0j7` FOREIGN KEY (`updatedBy_userId`) REFERENCES `user` (`userId`),
  CONSTRAINT `FK_vo7oth9n9louxqf4wj8bpfvd` FOREIGN KEY (`pharmacysite_id`) REFERENCES `pharmacy_site` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pharmacy_site`
--

DROP TABLE IF EXISTS `pharmacy_site`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pharmacy_site` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdDate` datetime DEFAULT NULL,
  `updatedDate` datetime DEFAULT NULL,
  `applicantName` varchar(255) NOT NULL,
  `applicantQualif` varchar(255) NOT NULL,
  `comment` varchar(500) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `estPopulation` varchar(255) NOT NULL,
  `faxNo` varchar(255) DEFAULT NULL,
  `fileNumber` varchar(255) DEFAULT NULL,
  `pharmacyName` varchar(255) NOT NULL,
  `phoneNo` varchar(255) DEFAULT NULL,
  `registrationDate` date DEFAULT NULL,
  `site_address1` varchar(255) DEFAULT NULL,
  `site_address2` varchar(255) DEFAULT NULL,
  `site_zipcode` varchar(255) DEFAULT NULL,
  `state` varchar(255) DEFAULT NULL,
  `submitDate` date DEFAULT NULL,
  `targetArea` varchar(255) DEFAULT NULL,
  `website` varchar(255) DEFAULT NULL,
  `createdBy_userId` bigint(20) DEFAULT NULL,
  `updatedBy_userId` bigint(20) DEFAULT NULL,
  `CNTRY_ID` bigint(20) DEFAULT NULL,
  `zipaddress` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_pjqa7qvg0tqngltvxd1e0yuym` (`createdBy_userId`),
  KEY `FK_pab587sd724oaihvg02idwh5u` (`updatedBy_userId`),
  KEY `FK_bf3dxrk8ph7arg54sjdm0sslr` (`CNTRY_ID`),
  CONSTRAINT `FK_bf3dxrk8ph7arg54sjdm0sslr` FOREIGN KEY (`CNTRY_ID`) REFERENCES `country` (`id`),
  CONSTRAINT `FK_pab587sd724oaihvg02idwh5u` FOREIGN KEY (`updatedBy_userId`) REFERENCES `user` (`userId`),
  CONSTRAINT `FK_pjqa7qvg0tqngltvxd1e0yuym` FOREIGN KEY (`createdBy_userId`) REFERENCES `user` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pharmacy_site_user`
--

DROP TABLE IF EXISTS `pharmacy_site_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pharmacy_site_user` (
  `userId` bigint(20) NOT NULL,
  `site_id` bigint(20) NOT NULL,
  `useruserId` bigint(19) NOT NULL,
  KEY `FK_fl8lgho38dugdyckj3wx12199` (`site_id`),
  KEY `FK_cal60yih9479msltmr8h5yasm` (`userId`),
  KEY `FK_cal60yih9479msltmr8h5yasm2` (`useruserId`),
  CONSTRAINT `FK_cal60yih9479msltmr8h5yasm` FOREIGN KEY (`userId`) REFERENCES `user` (`userId`),
  CONSTRAINT `FK_cal60yih9479msltmr8h5yasm2` FOREIGN KEY (`useruserId`) REFERENCES `user` (`userId`),
  CONSTRAINT `FK_fl8lgho38dugdyckj3wx12199` FOREIGN KEY (`site_id`) REFERENCES `pharmacy_site` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pharmclassif`
--

DROP TABLE IF EXISTS `pharmclassif`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pharmclassif` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `createdDate` datetime DEFAULT NULL,
  `updatedDate` datetime DEFAULT NULL,
  `Discontinued` varchar(255) DEFAULT NULL,
  `PharmaClass` varchar(255) DEFAULT NULL,
  `No` varchar(255) DEFAULT NULL,
  `createdBy_userId` bigint(20) DEFAULT NULL,
  `updatedBy_userId` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_4rbfalv68sfycmqv3qa7d0wvk` (`createdBy_userId`),
  KEY `FK_jk6df9ut6v677ry8ecb3a5xtj` (`updatedBy_userId`),
  CONSTRAINT `FK_4rbfalv68sfycmqv3qa7d0wvk` FOREIGN KEY (`createdBy_userId`) REFERENCES `user` (`userId`),
  CONSTRAINT `FK_jk6df9ut6v677ry8ecb3a5xtj` FOREIGN KEY (`updatedBy_userId`) REFERENCES `user` (`userId`)
) ENGINE=InnoDB AUTO_INCREMENT=488 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `piptracktodo`
--

DROP TABLE IF EXISTS `piptracktodo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `piptracktodo` (
  `Id` bigint(20) NOT NULL AUTO_INCREMENT,
  `trackId` bigint(20) DEFAULT NULL,
  `import_permitId` bigint(20) DEFAULT NULL,
  `Estimation` int(11) DEFAULT NULL,
  PRIMARY KEY (`Id`),
  KEY `FKpiptrackto650368` (`import_permitId`),
  KEY `FKpiptrackto357756` (`trackId`),
  CONSTRAINT `FKpiptrackto357756` FOREIGN KEY (`trackId`) REFERENCES `track` (`Id`),
  CONSTRAINT `FKpiptrackto650368` FOREIGN KEY (`import_permitId`) REFERENCES `import_permit` (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pricing`
--

DROP TABLE IF EXISTS `pricing`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pricing` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdDate` datetime DEFAULT NULL,
  `updatedDate` datetime DEFAULT NULL,
  `msrp` varchar(255) DEFAULT NULL,
  `pricePerDay` varchar(255) DEFAULT NULL,
  `pricePerDose` varchar(255) DEFAULT NULL,
  `treatCost` varchar(255) DEFAULT NULL,
  `createdBy_userId` bigint(20) DEFAULT NULL,
  `updatedBy_userId` bigint(20) DEFAULT NULL,
  `PROD_ID` bigint(20) DEFAULT NULL,
  `productId` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_itlru955epsejmb6o2gues6pt` (`createdBy_userId`),
  KEY `FK_eeb2sddj6ggjt67mf02ek9c3` (`updatedBy_userId`),
  KEY `FK_r21sluba088qooddiruykukwm` (`PROD_ID`),
  KEY `FKpricing360242` (`productId`),
  CONSTRAINT `FK_eeb2sddj6ggjt67mf02ek9c3` FOREIGN KEY (`updatedBy_userId`) REFERENCES `user` (`userId`),
  CONSTRAINT `FK_itlru955epsejmb6o2gues6pt` FOREIGN KEY (`createdBy_userId`) REFERENCES `user` (`userId`),
  CONSTRAINT `FK_r21sluba088qooddiruykukwm` FOREIGN KEY (`PROD_ID`) REFERENCES `product` (`id`) ON DELETE CASCADE,
  CONSTRAINT `FKpricing360242` FOREIGN KEY (`productId`) REFERENCES `product` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `prod_atc`
--

DROP TABLE IF EXISTS `prod_atc`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `prod_atc` (
  `atc_id` varchar(255) NOT NULL,
  `prod_id` bigint(20) NOT NULL,
  `atcatcCode` varchar(255) DEFAULT NULL,
  `productId` bigint(20) NOT NULL,
  KEY `FK_qr3emhpe8fjp8uvy5c3uw2udk` (`prod_id`),
  KEY `FK_l29nhil8ofmhna900ament9vx` (`atc_id`),
  KEY `FKprod_atc375341` (`atcatcCode`),
  KEY `FKprod_atc248197` (`productId`),
  CONSTRAINT `FK_l29nhil8ofmhna900ament9vx` FOREIGN KEY (`atc_id`) REFERENCES `atc` (`AtcCode`),
  CONSTRAINT `FK_qr3emhpe8fjp8uvy5c3uw2udk` FOREIGN KEY (`prod_id`) REFERENCES `product` (`id`) ON DELETE CASCADE,
  CONSTRAINT `FKprod_atc248197` FOREIGN KEY (`productId`) REFERENCES `product` (`id`),
  CONSTRAINT `FKprod_atc375341` FOREIGN KEY (`atcatcCode`) REFERENCES `atc` (`AtcCode`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `prod_company`
--

DROP TABLE IF EXISTS `prod_company`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `prod_company` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdDate` datetime DEFAULT NULL,
  `updatedDate` datetime DEFAULT NULL,
  `companyType` varchar(255) DEFAULT NULL,
  `createdBy_userId` bigint(20) DEFAULT NULL,
  `updatedBy_userId` bigint(20) DEFAULT NULL,
  `company_id` bigint(20) NOT NULL,
  `prod_id` bigint(20) NOT NULL,
  `productId` bigint(20) DEFAULT NULL,
  `companyId` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_gex5tggcwx8af70xsp1d4cg1e` (`createdBy_userId`),
  KEY `FK_ofqvide41wuluvfar0jbrvbd4` (`updatedBy_userId`),
  KEY `FK_f2x968kr7kqtjjtur6rk9a6du` (`company_id`),
  KEY `FK_ad7vmosx1exb8u9gbvt9gfk49` (`prod_id`),
  KEY `FKprod_compa765731` (`companyId`),
  KEY `FKprod_compa205691` (`productId`),
  KEY `FKprod_compa767715` (`companyId`),
  CONSTRAINT `FK_ad7vmosx1exb8u9gbvt9gfk49` FOREIGN KEY (`prod_id`) REFERENCES `product` (`id`) ON DELETE CASCADE,
  CONSTRAINT `FK_f2x968kr7kqtjjtur6rk9a6du` FOREIGN KEY (`company_id`) REFERENCES `company` (`id`),
  CONSTRAINT `FK_gex5tggcwx8af70xsp1d4cg1e` FOREIGN KEY (`createdBy_userId`) REFERENCES `user` (`userId`),
  CONSTRAINT `FK_ofqvide41wuluvfar0jbrvbd4` FOREIGN KEY (`updatedBy_userId`) REFERENCES `user` (`userId`),
  CONSTRAINT `FKprod_compa205691` FOREIGN KEY (`productId`) REFERENCES `product` (`id`),
  CONSTRAINT `FKprod_compa765731` FOREIGN KEY (`companyId`) REFERENCES `company` (`id`),
  CONSTRAINT `FKprod_compa767715` FOREIGN KEY (`companyId`) REFERENCES `company` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `prodapp_amdmt`
--

DROP TABLE IF EXISTS `prodapp_amdmt`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `prodapp_amdmt` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdDate` datetime DEFAULT NULL,
  `updatedDate` datetime DEFAULT NULL,
  `amdmtDesc` varchar(500) DEFAULT NULL,
  `amdmt_state` varchar(255) DEFAULT NULL,
  `approved` bit(1) DEFAULT NULL,
  `contentType` varchar(100) DEFAULT NULL,
  `file` longblob,
  `fileName` varchar(100) DEFAULT NULL,
  `fileUploaded` bit(1) NOT NULL,
  `staff_comment` varchar(500) DEFAULT NULL,
  `createdBy_userId` bigint(20) DEFAULT NULL,
  `updatedBy_userId` bigint(20) DEFAULT NULL,
  `amdmt_category` int(11) DEFAULT NULL,
  `approvedBy_userId` bigint(20) DEFAULT NULL,
  `prod_app_id` bigint(20) NOT NULL,
  `submittedBy_userId` bigint(20) DEFAULT NULL,
  `uploadedBy_userId` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_7p49qynd67kk7s1n7syfr8otc` (`createdBy_userId`),
  KEY `FK_pi4fkxpwf5lkc0y1a50unsl6p` (`updatedBy_userId`),
  KEY `FK_44yld8gphapcifprif894m5f0` (`amdmt_category`),
  KEY `FK_6ryjro5n3cjbv8di9ecnsvqxl` (`approvedBy_userId`),
  KEY `FK_9yq0lam42le9cl5w22ce7v2od` (`prod_app_id`),
  KEY `FK_qiyw8hv13d62bym1w507ixexg` (`submittedBy_userId`),
  KEY `FK_ppt4rly3bydq65t22cehfjw6s` (`uploadedBy_userId`),
  CONSTRAINT `FK_44yld8gphapcifprif894m5f0` FOREIGN KEY (`amdmt_category`) REFERENCES `amdmt_category` (`id`),
  CONSTRAINT `FK_6ryjro5n3cjbv8di9ecnsvqxl` FOREIGN KEY (`approvedBy_userId`) REFERENCES `user` (`userId`),
  CONSTRAINT `FK_7p49qynd67kk7s1n7syfr8otc` FOREIGN KEY (`createdBy_userId`) REFERENCES `user` (`userId`),
  CONSTRAINT `FK_9yq0lam42le9cl5w22ce7v2od` FOREIGN KEY (`prod_app_id`) REFERENCES `prodapplications` (`id`),
  CONSTRAINT `FK_pi4fkxpwf5lkc0y1a50unsl6p` FOREIGN KEY (`updatedBy_userId`) REFERENCES `user` (`userId`),
  CONSTRAINT `FK_ppt4rly3bydq65t22cehfjw6s` FOREIGN KEY (`uploadedBy_userId`) REFERENCES `user` (`userId`),
  CONSTRAINT `FK_qiyw8hv13d62bym1w507ixexg` FOREIGN KEY (`submittedBy_userId`) REFERENCES `user` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `prodapp_letter`
--

DROP TABLE IF EXISTS `prodapp_letter`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `prodapp_letter` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdDate` datetime DEFAULT NULL,
  `updatedDate` datetime DEFAULT NULL,
  `comment` varchar(500) DEFAULT NULL,
  `contentType` varchar(500) NOT NULL,
  `file` longblob NOT NULL,
  `fileName` varchar(500) NOT NULL,
  `letterType` varchar(255) DEFAULT NULL,
  `regState` varchar(255) DEFAULT NULL,
  `title` varchar(500) NOT NULL,
  `createdBy_userId` bigint(20) DEFAULT NULL,
  `updatedBy_userId` bigint(20) DEFAULT NULL,
  `prodApplications_id` bigint(20) DEFAULT NULL,
  `reviewInfo_id` bigint(20) DEFAULT NULL,
  `sampleTest_id` bigint(20) DEFAULT NULL,
  `suspDetail_id` bigint(20) DEFAULT NULL,
  `uploadedBy_userId` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_gw20q224eo1prdqog95890xbu` (`createdBy_userId`),
  KEY `FK_qod9jl7y18d232jn10i1rrpib` (`updatedBy_userId`),
  KEY `FK_cikeg8vict937qobqwwg0h4eg` (`prodApplications_id`),
  KEY `FK_mj2n9cnybueb8b5yts69l3xg0` (`reviewInfo_id`),
  KEY `FK_tg41xxpe5jss20liqpdy3aty2` (`sampleTest_id`),
  KEY `FK_l9th0c3j3a0rnm6e8q9rfhtdm` (`suspDetail_id`),
  KEY `FK_fvkyy3u4o449db7283stlt9nm` (`uploadedBy_userId`),
  CONSTRAINT `FK_cikeg8vict937qobqwwg0h4eg` FOREIGN KEY (`prodApplications_id`) REFERENCES `prodapplications` (`id`) ON DELETE CASCADE,
  CONSTRAINT `FK_fvkyy3u4o449db7283stlt9nm` FOREIGN KEY (`uploadedBy_userId`) REFERENCES `user` (`userId`),
  CONSTRAINT `FK_gw20q224eo1prdqog95890xbu` FOREIGN KEY (`createdBy_userId`) REFERENCES `user` (`userId`),
  CONSTRAINT `FK_l9th0c3j3a0rnm6e8q9rfhtdm` FOREIGN KEY (`suspDetail_id`) REFERENCES `susp_detail` (`id`),
  CONSTRAINT `FK_mj2n9cnybueb8b5yts69l3xg0` FOREIGN KEY (`reviewInfo_id`) REFERENCES `review_info` (`id`) ON DELETE CASCADE,
  CONSTRAINT `FK_qod9jl7y18d232jn10i1rrpib` FOREIGN KEY (`updatedBy_userId`) REFERENCES `user` (`userId`),
  CONSTRAINT `FK_tg41xxpe5jss20liqpdy3aty2` FOREIGN KEY (`sampleTest_id`) REFERENCES `sample_test` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `prodappchecklist`
--

DROP TABLE IF EXISTS `prodappchecklist`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `prodappchecklist` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdDate` datetime DEFAULT NULL,
  `updatedDate` datetime DEFAULT NULL,
  `appRemark` varchar(500) DEFAULT NULL,
  `contentType` varchar(255) DEFAULT NULL,
  `file` longblob,
  `fileName` varchar(255) DEFAULT NULL,
  `fileUploaded` bit(1) NOT NULL,
  `sendToApp` bit(1) NOT NULL,
  `staffComment` varchar(500) DEFAULT NULL,
  `staffValue` varchar(255) DEFAULT NULL,
  `value` varchar(255) DEFAULT NULL,
  `createdBy_userId` bigint(20) DEFAULT NULL,
  `updatedBy_userId` bigint(20) DEFAULT NULL,
  `checklist_id` bigint(20) NOT NULL,
  `prod_app_id` bigint(20) NOT NULL,
  `uploadedBy_userId` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_iqi2mphigvhm2sug0din1pler` (`createdBy_userId`),
  KEY `FK_dmhfe21qytweognjgm3b1kic3` (`updatedBy_userId`),
  KEY `FK_hsgikle82r6chedprcmmbc7jl` (`checklist_id`),
  KEY `FK_6vbem94vb0p87sh8vxjg5utcu` (`prod_app_id`),
  KEY `FK_krd7y4i4dtffjwe660r5pihqq` (`uploadedBy_userId`),
  CONSTRAINT `FK_6vbem94vb0p87sh8vxjg5utcu` FOREIGN KEY (`prod_app_id`) REFERENCES `prodapplications` (`id`) ON DELETE CASCADE,
  CONSTRAINT `FK_dmhfe21qytweognjgm3b1kic3` FOREIGN KEY (`updatedBy_userId`) REFERENCES `user` (`userId`),
  CONSTRAINT `FK_hsgikle82r6chedprcmmbc7jl` FOREIGN KEY (`checklist_id`) REFERENCES `checklist` (`id`),
  CONSTRAINT `FK_iqi2mphigvhm2sug0din1pler` FOREIGN KEY (`createdBy_userId`) REFERENCES `user` (`userId`),
  CONSTRAINT `FK_krd7y4i4dtffjwe660r5pihqq` FOREIGN KEY (`uploadedBy_userId`) REFERENCES `user` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `prodapplications`
--

DROP TABLE IF EXISTS `prodapplications`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `prodapplications` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdDate` datetime DEFAULT NULL,
  `updatedDate` datetime DEFAULT NULL,
  `active` bit(1) NOT NULL DEFAULT b'1',
  `appComment` varchar(500) DEFAULT NULL,
  `applicantVerified` bit(1) NOT NULL DEFAULT b'0',
  `bankName` varchar(500) DEFAULT NULL,
  `dosRecDate` date DEFAULT NULL,
  `dossLoc` varchar(500) DEFAULT NULL,
  `dossierReceived` bit(1) NOT NULL DEFAULT b'0',
  `exec_summary` longtext,
  `fastrack` bit(1) NOT NULL DEFAULT b'0',
  `feeAmt` varchar(255) DEFAULT NULL,
  `feeReceipt` longblob,
  `feeReceived` bit(1) NOT NULL DEFAULT b'0',
  `feeSubmittedDt` date DEFAULT NULL,
  `lastStatusDate` date DEFAULT NULL,
  `position` varchar(255) DEFAULT NULL,
  `prescreenBankName` varchar(500) DEFAULT NULL,
  `prescreenReceiptNo` varchar(500) DEFAULT NULL,
  `prescreenfeeAmt` varchar(255) DEFAULT NULL,
  `prescreenfeeReceived` bit(1) NOT NULL DEFAULT b'0',
  `prescreenfeeSubmittedDt` date DEFAULT NULL,
  `prodAppNo` varchar(255) DEFAULT NULL,
  `prodAppType` varchar(255) DEFAULT NULL,
  `prodRegNo` varchar(255) DEFAULT NULL,
  `productVerified` bit(1) NOT NULL,
  `receiptNo` varchar(500) DEFAULT NULL,
  `regCert` longblob,
  `regExpiryDate` date DEFAULT NULL,
  `regState` varchar(255) DEFAULT NULL,
  `registrationDate` date DEFAULT NULL,
  `rejCert` longblob,
  `sendToGazette` bit(1) NOT NULL DEFAULT b'0',
  `sra` bit(1) NOT NULL DEFAULT b'0',
  `submitDate` date DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  `createdBy_userId` bigint(20) DEFAULT NULL,
  `updatedBy_userId` bigint(20) DEFAULT NULL,
  `APP_ID` bigint(20) NOT NULL,
  `applicantUser` bigint(20) DEFAULT NULL,
  `appointment_id` bigint(20) DEFAULT NULL,
  `MODERATOR_ID` bigint(20) DEFAULT NULL,
  `PROD_ID` bigint(20) DEFAULT NULL,
  `clinicalRevReceived` bit(1) NOT NULL DEFAULT b'0',
  `clinicalRevVerified` bit(1) NOT NULL DEFAULT b'0',
  `clinicalReview` longblob,
  `dccApproval` bit(1) NOT NULL DEFAULT b'0',
  `priorityDate` datetime DEFAULT NULL,
  `priorityNo` varchar(255) DEFAULT NULL,
  `sampleTestRecieved` bit(1) DEFAULT NULL,
  `cRevAttach_id` bigint(20) DEFAULT NULL,
  `papp_ID` bigint(20) DEFAULT NULL,
  `archivingDate` date DEFAULT NULL,
  `mjVarQnt` int(11) DEFAULT '0',
  `mnVarQnt` int(11) DEFAULT '0',
  `prodAppDetails` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_oltnrevfb7q8281mk5ihxf9rw` (`createdBy_userId`),
  KEY `FK_4d3swiip45l5huy782hh699d1` (`updatedBy_userId`),
  KEY `FK_32l4y9y0qd1djydd7u3paxbmn` (`APP_ID`),
  KEY `FK_7qk6jsiyxy5mifn4wuwt0my6k` (`applicantUser`),
  KEY `FK_1vs2a589883gbhcjd0cxc1xi` (`appointment_id`),
  KEY `FK_edxf76byhvjvqulk9ydn56yty` (`MODERATOR_ID`),
  KEY `FK_r7lut1w8do6qai0bxw9o697kp` (`PROD_ID`),
  KEY `FKDD4B963ADAD577B8` (`cRevAttach_id`),
  KEY `FKDD4B963AB912A923` (`papp_ID`),
  CONSTRAINT `FKDD4B963AB912A923` FOREIGN KEY (`papp_ID`) REFERENCES `prodapplications` (`id`),
  CONSTRAINT `FKDD4B963ADAD577B8` FOREIGN KEY (`cRevAttach_id`) REFERENCES `attachment` (`id`),
  CONSTRAINT `FK_1vs2a589883gbhcjd0cxc1xi` FOREIGN KEY (`appointment_id`) REFERENCES `appointment` (`id`),
  CONSTRAINT `FK_32l4y9y0qd1djydd7u3paxbmn` FOREIGN KEY (`APP_ID`) REFERENCES `applicant` (`applcntId`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_4d3swiip45l5huy782hh699d1` FOREIGN KEY (`updatedBy_userId`) REFERENCES `user` (`userId`),
  CONSTRAINT `FK_7qk6jsiyxy5mifn4wuwt0my6k` FOREIGN KEY (`applicantUser`) REFERENCES `user` (`userId`),
  CONSTRAINT `FK_edxf76byhvjvqulk9ydn56yty` FOREIGN KEY (`MODERATOR_ID`) REFERENCES `user` (`userId`),
  CONSTRAINT `FK_oltnrevfb7q8281mk5ihxf9rw` FOREIGN KEY (`createdBy_userId`) REFERENCES `user` (`userId`),
  CONSTRAINT `FK_r7lut1w8do6qai0bxw9o697kp` FOREIGN KEY (`PROD_ID`) REFERENCES `product` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `prodexcipient`
--

DROP TABLE IF EXISTS `prodexcipient`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `prodexcipient` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdDate` datetime DEFAULT NULL,
  `updatedDate` datetime DEFAULT NULL,
  `RefStd` varchar(255) DEFAULT NULL,
  `dosage_strength` varchar(255) DEFAULT NULL,
  `function` varchar(255) DEFAULT NULL,
  `createdBy_userId` bigint(20) DEFAULT NULL,
  `updatedBy_userId` bigint(20) DEFAULT NULL,
  `DOSUNIT_ID` int(11) DEFAULT NULL,
  `EXPNT_ID` bigint(20) DEFAULT NULL,
  `prod_id` bigint(20) NOT NULL,
  `productId` bigint(20) DEFAULT NULL,
  `excipientId` bigint(20) DEFAULT NULL,
  `dosuomId` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_jisxtn5m82ygjyjyahqy0bnw8` (`createdBy_userId`),
  KEY `FK_7fs3k50mnf6kxc81ppxrfguo6` (`updatedBy_userId`),
  KEY `FK_hau8atoexq46k1w52vl3iaq92` (`DOSUNIT_ID`),
  KEY `FK_q1r8kunyah7facxbh5aaypnc2` (`EXPNT_ID`),
  KEY `FK_badaadg88hjebng2ilqu8b5gu` (`prod_id`),
  KEY `FKprodexcipi210712` (`dosuomId`),
  KEY `FKprodexcipi29378` (`excipientId`),
  KEY `FKprodexcipi949354` (`productId`),
  CONSTRAINT `FK_7fs3k50mnf6kxc81ppxrfguo6` FOREIGN KEY (`updatedBy_userId`) REFERENCES `user` (`userId`),
  CONSTRAINT `FK_badaadg88hjebng2ilqu8b5gu` FOREIGN KEY (`prod_id`) REFERENCES `product` (`id`) ON DELETE CASCADE,
  CONSTRAINT `FK_hau8atoexq46k1w52vl3iaq92` FOREIGN KEY (`DOSUNIT_ID`) REFERENCES `dosuom` (`id`),
  CONSTRAINT `FK_jisxtn5m82ygjyjyahqy0bnw8` FOREIGN KEY (`createdBy_userId`) REFERENCES `user` (`userId`),
  CONSTRAINT `FK_q1r8kunyah7facxbh5aaypnc2` FOREIGN KEY (`EXPNT_ID`) REFERENCES `excipient` (`id`),
  CONSTRAINT `FKprodexcipi210712` FOREIGN KEY (`dosuomId`) REFERENCES `dosuom` (`id`),
  CONSTRAINT `FKprodexcipi29378` FOREIGN KEY (`excipientId`) REFERENCES `excipient` (`id`),
  CONSTRAINT `FKprodexcipi949354` FOREIGN KEY (`productId`) REFERENCES `product` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `prodinn`
--

DROP TABLE IF EXISTS `prodinn`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `prodinn` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdDate` datetime DEFAULT NULL,
  `updatedDate` datetime DEFAULT NULL,
  `RefStd` varchar(255) DEFAULT NULL,
  `dosage_strength` varchar(255) DEFAULT NULL,
  `function` varchar(255) DEFAULT NULL,
  `createdBy_userId` bigint(20) DEFAULT NULL,
  `updatedBy_userId` bigint(20) DEFAULT NULL,
  `DOSUNIT_ID` int(11) DEFAULT NULL,
  `INN_ID` bigint(20) DEFAULT NULL,
  `prod_id` bigint(20) NOT NULL,
  `productId` bigint(20) DEFAULT NULL,
  `innId` bigint(20) DEFAULT NULL,
  `dosuomId` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_6tcdscm60m6qkmb1kb80s95qk` (`createdBy_userId`),
  KEY `FK_1usvry8s7qdswt54rxsq5j9pa` (`updatedBy_userId`),
  KEY `FK_53kylymjvismeaio3ijb608nt` (`DOSUNIT_ID`),
  KEY `FK_13mgiudmqvcuh9ye3k1vxwsk4` (`INN_ID`),
  KEY `FK_jha7nj3djvqe39mw17cf7cafj` (`prod_id`),
  KEY `FKprodinn629245` (`dosuomId`),
  KEY `FKprodinn23895` (`innId`),
  KEY `FKprodinn789312` (`productId`),
  CONSTRAINT `FK_13mgiudmqvcuh9ye3k1vxwsk4` FOREIGN KEY (`INN_ID`) REFERENCES `inn` (`id`),
  CONSTRAINT `FK_1usvry8s7qdswt54rxsq5j9pa` FOREIGN KEY (`updatedBy_userId`) REFERENCES `user` (`userId`),
  CONSTRAINT `FK_53kylymjvismeaio3ijb608nt` FOREIGN KEY (`DOSUNIT_ID`) REFERENCES `dosuom` (`id`),
  CONSTRAINT `FK_6tcdscm60m6qkmb1kb80s95qk` FOREIGN KEY (`createdBy_userId`) REFERENCES `user` (`userId`),
  CONSTRAINT `FK_jha7nj3djvqe39mw17cf7cafj` FOREIGN KEY (`prod_id`) REFERENCES `product` (`id`) ON DELETE CASCADE,
  CONSTRAINT `FKprodinn23895` FOREIGN KEY (`innId`) REFERENCES `inn` (`id`),
  CONSTRAINT `FKprodinn629245` FOREIGN KEY (`dosuomId`) REFERENCES `dosuom` (`id`),
  CONSTRAINT `FKprodinn789312` FOREIGN KEY (`productId`) REFERENCES `product` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `product`
--

DROP TABLE IF EXISTS `product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdDate` datetime DEFAULT NULL,
  `updatedDate` datetime DEFAULT NULL,
  `age_group` varchar(255) DEFAULT NULL,
  `apprvd_name` varchar(500) DEFAULT NULL,
  `contType` varchar(500) DEFAULT NULL,
  `dosage_strength` varchar(255) DEFAULT NULL,
  `drugType` varchar(255) DEFAULT NULL,
  `fnm` varchar(500) DEFAULT NULL,
  `gen_name` varchar(500) DEFAULT NULL,
  `indications` varchar(4096) DEFAULT NULL,
  `ingrdStatment` varchar(500) DEFAULT NULL,
  `new_chemical_entity` bit(1) DEFAULT NULL,
  `new_chem_name` varchar(500) DEFAULT NULL,
  `noAtc` bit(1) NOT NULL,
  `packSize` varchar(500) DEFAULT NULL,
  `pharmacopeiaStds` varchar(500) DEFAULT NULL,
  `posology` varchar(4096) DEFAULT NULL,
  `prod_cat` varchar(255) DEFAULT NULL,
  `prod_desc` varchar(4096) DEFAULT NULL,
  `prod_name` varchar(500) DEFAULT NULL,
  `prod_type` varchar(255) DEFAULT NULL,
  `shelfLife` varchar(500) DEFAULT NULL,
  `storageCndtn` varchar(500) DEFAULT NULL,
  `createdBy_userId` bigint(20) DEFAULT NULL,
  `updatedBy_userId` bigint(20) DEFAULT NULL,
  `ADMIN_ROUTE_ID` int(11) DEFAULT NULL,
  `DOSFORM_ID` bigint(20) DEFAULT NULL,
  `DOSUNIT_ID` int(11) DEFAULT NULL,
  `PHARM_CLASSIF_ID` int(11) DEFAULT NULL,
  `pricing_id` bigint(20) DEFAULT NULL,
  `manuf_name` varchar(1000) DEFAULT NULL,
  `pricingId` bigint(20) DEFAULT NULL,
  `pharmclassifId` int(11) DEFAULT NULL,
  `dosuomId` int(11) DEFAULT NULL,
  `dosformUid` bigint(20) DEFAULT NULL,
  `admin_routeId` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_eplbe2yapj8k4p6g9mneq5758` (`createdBy_userId`),
  KEY `FK_qom0noib7xkm5hcpapqvc65lf` (`updatedBy_userId`),
  KEY `FK_nh370c0kxdg2rirhns6539ekw` (`ADMIN_ROUTE_ID`),
  KEY `FK_7xnxrspd13f7apsqex6b59404` (`DOSFORM_ID`),
  KEY `FK_pv4wimod0f4c5xb8l08nomo3c` (`DOSUNIT_ID`),
  KEY `FK_nvava2tq1a7ijk6253qr9tuen` (`PHARM_CLASSIF_ID`),
  KEY `FK_jhjd12ybnf2033fdlnox4qcqi` (`pricing_id`),
  KEY `FKproduct682319` (`admin_routeId`),
  KEY `FKproduct340108` (`dosformUid`),
  KEY `FKproduct618048` (`dosuomId`),
  KEY `FKproduct642710` (`pharmclassifId`),
  KEY `FKproduct159294` (`pricingId`),
  CONSTRAINT `FK_7xnxrspd13f7apsqex6b59404` FOREIGN KEY (`DOSFORM_ID`) REFERENCES `dosform` (`uid`),
  CONSTRAINT `FK_eplbe2yapj8k4p6g9mneq5758` FOREIGN KEY (`createdBy_userId`) REFERENCES `user` (`userId`),
  CONSTRAINT `FK_jhjd12ybnf2033fdlnox4qcqi` FOREIGN KEY (`pricing_id`) REFERENCES `pricing` (`id`) ON DELETE SET NULL,
  CONSTRAINT `FK_nh370c0kxdg2rirhns6539ekw` FOREIGN KEY (`ADMIN_ROUTE_ID`) REFERENCES `admin_route` (`id`),
  CONSTRAINT `FK_nvava2tq1a7ijk6253qr9tuen` FOREIGN KEY (`PHARM_CLASSIF_ID`) REFERENCES `pharmclassif` (`id`),
  CONSTRAINT `FK_pv4wimod0f4c5xb8l08nomo3c` FOREIGN KEY (`DOSUNIT_ID`) REFERENCES `dosuom` (`id`),
  CONSTRAINT `FK_qom0noib7xkm5hcpapqvc65lf` FOREIGN KEY (`updatedBy_userId`) REFERENCES `user` (`userId`),
  CONSTRAINT `FKproduct159294` FOREIGN KEY (`pricingId`) REFERENCES `pricing` (`id`),
  CONSTRAINT `FKproduct340108` FOREIGN KEY (`dosformUid`) REFERENCES `dosform` (`uid`),
  CONSTRAINT `FKproduct618048` FOREIGN KEY (`dosuomId`) REFERENCES `dosuom` (`id`),
  CONSTRAINT `FKproduct642710` FOREIGN KEY (`pharmclassifId`) REFERENCES `pharmclassif` (`id`),
  CONSTRAINT `FKproduct682319` FOREIGN KEY (`admin_routeId`) REFERENCES `admin_route` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `product_aud`
--

DROP TABLE IF EXISTS `product_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product_aud` (
  `id` bigint(20) NOT NULL,
  `REV` int(11) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `age_group` varchar(255) DEFAULT NULL,
  `apprvd_name` varchar(500) DEFAULT NULL,
  `contType` varchar(500) DEFAULT NULL,
  `dosage_strength` varchar(255) DEFAULT NULL,
  `drugType` varchar(255) DEFAULT NULL,
  `fnm` varchar(500) DEFAULT NULL,
  `gen_name` varchar(500) DEFAULT NULL,
  `indications` varchar(1500) DEFAULT NULL,
  `ingrdStatment` varchar(500) DEFAULT NULL,
  `new_chemical_entity` bit(1) DEFAULT NULL,
  `new_chem_name` varchar(500) DEFAULT NULL,
  `noAtc` bit(1) DEFAULT NULL,
  `packSize` varchar(500) DEFAULT NULL,
  `pharmacopeiaStds` varchar(500) DEFAULT NULL,
  `posology` varchar(500) DEFAULT NULL,
  `prod_cat` varchar(255) DEFAULT NULL,
  `prod_desc` varchar(500) DEFAULT NULL,
  `prod_name` varchar(500) DEFAULT NULL,
  `shelfLife` varchar(500) DEFAULT NULL,
  `storageCndtn` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`id`,`REV`),
  KEY `FK_ahohsquktbbkggtspwpqra8i6` (`REV`),
  CONSTRAINT `FK_ahohsquktbbkggtspwpqra8i6` FOREIGN KEY (`REV`) REFERENCES `revinfo` (`REV`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `publicorganization`
--

DROP TABLE IF EXISTS `publicorganization`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `publicorganization` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `webresourceID2` bigint(20) DEFAULT NULL,
  `conceptID` bigint(20) NOT NULL,
  `webresourceID` bigint(20) DEFAULT NULL,
  `spatialID` bigint(20) DEFAULT NULL,
  `addressID` bigint(20) DEFAULT NULL,
  `spatialdataID` bigint(20) DEFAULT NULL,
  `orgroleID` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FKpublicorga189004` (`addressID`),
  KEY `FKpublicorga529189` (`webresourceID`),
  KEY `FKpublicorga548173` (`conceptID`),
  KEY `FKpublicorga595638` (`webresourceID2`),
  KEY `FKpublicorga602054` (`spatialdataID`),
  KEY `FKpublicorga536704` (`orgroleID`),
  KEY `FKpublicorga617726` (`conceptID`),
  CONSTRAINT `FKpublicorga189004` FOREIGN KEY (`addressID`) REFERENCES `address` (`ID`),
  CONSTRAINT `FKpublicorga529189` FOREIGN KEY (`webresourceID`) REFERENCES `webresource` (`ID`),
  CONSTRAINT `FKpublicorga536704` FOREIGN KEY (`orgroleID`) REFERENCES `orgrole` (`ID`),
  CONSTRAINT `FKpublicorga595638` FOREIGN KEY (`webresourceID2`) REFERENCES `webresource` (`ID`),
  CONSTRAINT `FKpublicorga602054` FOREIGN KEY (`spatialdataID`) REFERENCES `spatialdata` (`ID`),
  CONSTRAINT `FKpublicorga617726` FOREIGN KEY (`conceptID`) REFERENCES `concept` (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `publicorgsubject`
--

DROP TABLE IF EXISTS `publicorgsubject`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `publicorgsubject` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `conceptID` bigint(20) NOT NULL,
  `publicorganizationID` bigint(20) DEFAULT NULL,
  `Url` varchar(255) DEFAULT NULL,
  `Predicate` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FKpublicorgs935498` (`publicorganizationID`),
  KEY `FKpublicorgs323332` (`conceptID`),
  CONSTRAINT `FKpublicorgs323332` FOREIGN KEY (`conceptID`) REFERENCES `concept` (`ID`),
  CONSTRAINT `FKpublicorgs935498` FOREIGN KEY (`publicorganizationID`) REFERENCES `publicorganization` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=99 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `purpose`
--

DROP TABLE IF EXISTS `purpose`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `purpose` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `resposibilityID` bigint(20) NOT NULL,
  `publicorganizationID` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FKpurpose794373` (`publicorganizationID`),
  KEY `FKpurpose929287` (`resposibilityID`),
  CONSTRAINT `FKpurpose794373` FOREIGN KEY (`publicorganizationID`) REFERENCES `publicorganization` (`ID`),
  CONSTRAINT `FKpurpose929287` FOREIGN KEY (`resposibilityID`) REFERENCES `resposibility` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `query`
--

DROP TABLE IF EXISTS `query`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `query` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Key` varchar(255) DEFAULT NULL,
  `Sql` varchar(8198) DEFAULT NULL,
  `Comment` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `Key` (`Key`)
) ENGINE=InnoDB AUTO_INCREMENT=1010 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `question`
--

DROP TABLE IF EXISTS `question`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `question` (
  `Id` bigint(20) NOT NULL AUTO_INCREMENT,
  `pipStatusInstanceId` bigint(20) DEFAULT NULL,
  `pipStatusId` bigint(20) NOT NULL,
  `import_permitId` bigint(20) DEFAULT NULL,
  `answerquestionId` bigint(20) DEFAULT NULL,
  `Question` varchar(255) DEFAULT NULL,
  `Header` tinyint(1) NOT NULL,
  `Order` int(11) NOT NULL,
  `Discriminator` varchar(255) NOT NULL,
  PRIMARY KEY (`Id`),
  KEY `FKquestion86354` (`answerquestionId`),
  KEY `FKquestion318449` (`import_permitId`),
  KEY `FKquestion614458` (`pipStatusId`),
  KEY `FKquestion43795` (`pipStatusInstanceId`),
  KEY `FK_Questions` (`import_permitId`),
  CONSTRAINT `FK_Questions` FOREIGN KEY (`import_permitId`) REFERENCES `import_permit` (`Id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FKquestion43795` FOREIGN KEY (`pipStatusInstanceId`) REFERENCES `options` (`Id`),
  CONSTRAINT `FKquestion614458` FOREIGN KEY (`pipStatusId`) REFERENCES `options` (`Id`),
  CONSTRAINT `FKquestion86354` FOREIGN KEY (`answerquestionId`) REFERENCES `answerquestion` (`Id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1794 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `register`
--

DROP TABLE IF EXISTS `register`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `register` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `appdataID` bigint(20) NOT NULL,
  `conceptID` bigint(20) NOT NULL,
  `RegisteredAt` date DEFAULT NULL,
  `Register` varchar(255) DEFAULT NULL,
  `ValidTo` date DEFAULT NULL,
  `CreatedAt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `activityDataID` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FKregister932047` (`conceptID`),
  KEY `FKregister105477` (`appdataID`),
  KEY `FKregister803271` (`activityDataID`),
  CONSTRAINT `FKregister105477` FOREIGN KEY (`appdataID`) REFERENCES `concept` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FKregister803271` FOREIGN KEY (`activityDataID`) REFERENCES `concept` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FKregister932047` FOREIGN KEY (`conceptID`) REFERENCES `concept` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `reminder`
--

DROP TABLE IF EXISTS `reminder`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reminder` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `reminderType` varchar(255) DEFAULT NULL,
  `sent_date` date DEFAULT NULL,
  `invoice_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_7cpfi664523eul3s65f4sm716` (`invoice_id`),
  CONSTRAINT `FK_7cpfi664523eul3s65f4sm716` FOREIGN KEY (`invoice_id`) REFERENCES `invoice` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `resource_bundle`
--

DROP TABLE IF EXISTS `resource_bundle`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `resource_bundle` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `sortOrder` tinyint(4) DEFAULT '0',
  `basename` varchar(255) DEFAULT NULL,
  `locale` varchar(255) DEFAULT NULL,
  `displayName` varchar(45) DEFAULT NULL,
  `svgFlag` mediumtext,
  `updatedDate` datetime DEFAULT NULL,
  `createdDate` datetime DEFAULT NULL,
  `UsaidLogo` longtext NOT NULL,
  `NmraLogo` longtext NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `resource_message`
--

DROP TABLE IF EXISTS `resource_message`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `resource_message` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `message_key` varchar(255) DEFAULT NULL,
  `message_value` mediumtext,
  `key_bundle` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKresource_m315789` (`key_bundle`),
  KEY `key_bundle` (`key_bundle`),
  CONSTRAINT `bundle` FOREIGN KEY (`key_bundle`) REFERENCES `resource_bundle` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `key_bundle` FOREIGN KEY (`key_bundle`) REFERENCES `resource_bundle` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=34503 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `resourceclassifier`
--

DROP TABLE IF EXISTS `resourceclassifier`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `resourceclassifier` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `labelsID` bigint(20) DEFAULT NULL,
  `classifierID` bigint(20) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `FKresourcecl915659` (`classifierID`),
  KEY `FKresourcecl13807` (`labelsID`),
  CONSTRAINT `FKresourcecl13807` FOREIGN KEY (`labelsID`) REFERENCES `concept` (`ID`),
  CONSTRAINT `FKresourcecl915659` FOREIGN KEY (`classifierID`) REFERENCES `concept` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `resourcepurpose`
--

DROP TABLE IF EXISTS `resourcepurpose`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `resourcepurpose` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `resourceclassifierID` bigint(20) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `FKresourcepu750177` (`resourceclassifierID`),
  CONSTRAINT `FKresourcepu750177` FOREIGN KEY (`resourceclassifierID`) REFERENCES `resourceclassifier` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Temporary view structure for view `resources`
--

DROP TABLE IF EXISTS `resources`;
/*!50001 DROP VIEW IF EXISTS `resources`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `resources` AS SELECT 
 1 AS `ID`,
 1 AS `url`,
 1 AS `lang`,
 1 AS `description`*/;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `resposibility`
--

DROP TABLE IF EXISTS `resposibility`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `resposibility` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `conceptID2` bigint(20) NOT NULL,
  `conceptID` bigint(20) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `FKresposibil177116` (`conceptID`),
  KEY `FKresposibil636949` (`conceptID2`),
  CONSTRAINT `FKresposibil177116` FOREIGN KEY (`conceptID`) REFERENCES `concept` (`ID`),
  CONSTRAINT `FKresposibil636949` FOREIGN KEY (`conceptID2`) REFERENCES `concept` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `revdeficiency`
--

DROP TABLE IF EXISTS `revdeficiency`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `revdeficiency` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `ack_date` datetime DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `due_date` datetime DEFAULT NULL,
  `resolved` bit(1) NOT NULL,
  `ackComment_id` bigint(20) DEFAULT NULL,
  `prodAppLetter_id` bigint(20) DEFAULT NULL,
  `reviewinfo_ID` bigint(20) DEFAULT NULL,
  `sentComment_id` bigint(20) DEFAULT NULL,
  `userId` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_t0flfuchl0dbftov1thdo5d6h` (`ackComment_id`),
  KEY `FK_nn7xqk3xwnl3ndyokbv60pdoc` (`prodAppLetter_id`),
  KEY `FK_hb2skqfqj7e6ccg9cs724231a` (`reviewinfo_ID`),
  KEY `FK_73h0xoos91r4hatnfk5rpxpuw` (`sentComment_id`),
  KEY `FK_suosop3vcfylq30twbfx9sl7v` (`userId`),
  CONSTRAINT `FK_73h0xoos91r4hatnfk5rpxpuw` FOREIGN KEY (`sentComment_id`) REFERENCES `reviewcomment` (`id`) ON DELETE CASCADE,
  CONSTRAINT `FK_hb2skqfqj7e6ccg9cs724231a` FOREIGN KEY (`reviewinfo_ID`) REFERENCES `review_info` (`id`) ON DELETE CASCADE,
  CONSTRAINT `FK_nn7xqk3xwnl3ndyokbv60pdoc` FOREIGN KEY (`prodAppLetter_id`) REFERENCES `prodapp_letter` (`id`),
  CONSTRAINT `FK_suosop3vcfylq30twbfx9sl7v` FOREIGN KEY (`userId`) REFERENCES `user` (`userId`),
  CONSTRAINT `FK_t0flfuchl0dbftov1thdo5d6h` FOREIGN KEY (`ackComment_id`) REFERENCES `reviewcomment` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `review`
--

DROP TABLE IF EXISTS `review`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `review` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `assignDate` date DEFAULT NULL,
  `dueDate` date DEFAULT NULL,
  `file` longblob,
  `modComment` varchar(255) DEFAULT NULL,
  `moderatorSummary` varchar(1000) DEFAULT NULL,
  `recomendType` varchar(255) DEFAULT NULL,
  `reviewStatus` varchar(255) DEFAULT NULL,
  `reviewerSummary` varchar(1000) DEFAULT NULL,
  `submitDate` date DEFAULT NULL,
  `prod_app_id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_lucbhdlp1yxwvxvbdawreniem` (`prod_app_id`),
  KEY `FK_jdnb3cnup5vq6opj19kbm8w4d` (`user_id`),
  CONSTRAINT `FK_jdnb3cnup5vq6opj19kbm8w4d` FOREIGN KEY (`user_id`) REFERENCES `user` (`userId`),
  CONSTRAINT `FK_lucbhdlp1yxwvxvbdawreniem` FOREIGN KEY (`prod_app_id`) REFERENCES `prodapplications` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `review_checklist`
--

DROP TABLE IF EXISTS `review_checklist`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `review_checklist` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdDate` datetime DEFAULT NULL,
  `updatedDate` datetime DEFAULT NULL,
  `comments` varchar(1000) DEFAULT NULL,
  `concerns` varchar(1000) DEFAULT NULL,
  `createdBy_userId` bigint(20) DEFAULT NULL,
  `updatedBy_userId` bigint(20) DEFAULT NULL,
  `checklist_id` bigint(20) DEFAULT NULL,
  `review_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_83vvhm0l4h2kqq3pgmaynsr5d` (`createdBy_userId`),
  KEY `FK_5imiclnrtysde8ypxme1p86vu` (`updatedBy_userId`),
  KEY `FK_jwnr9nuachhy7fua07bp9v67d` (`checklist_id`),
  KEY `FK_no7huvfmnwq7n4ri3ty292tbb` (`review_id`),
  CONSTRAINT `FK_5imiclnrtysde8ypxme1p86vu` FOREIGN KEY (`updatedBy_userId`) REFERENCES `user` (`userId`),
  CONSTRAINT `FK_83vvhm0l4h2kqq3pgmaynsr5d` FOREIGN KEY (`createdBy_userId`) REFERENCES `user` (`userId`),
  CONSTRAINT `FK_jwnr9nuachhy7fua07bp9v67d` FOREIGN KEY (`checklist_id`) REFERENCES `checklist` (`id`),
  CONSTRAINT `FK_no7huvfmnwq7n4ri3ty292tbb` FOREIGN KEY (`review_id`) REFERENCES `review` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `review_detail`
--

DROP TABLE IF EXISTS `review_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `review_detail` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdDate` datetime DEFAULT NULL,
  `updatedDate` datetime DEFAULT NULL,
  `answered` bit(1) DEFAULT NULL,
  `no_reason` longtext,
  `other_comment` longtext,
  `satisfactory` bit(1) DEFAULT NULL,
  `sec_comment` longtext,
  `volume` varchar(255) DEFAULT NULL,
  `createdBy_userId` bigint(20) DEFAULT NULL,
  `updatedBy_userId` bigint(20) DEFAULT NULL,
  `review_info_id` bigint(20) NOT NULL,
  `reviewquest_id` bigint(20) NOT NULL,
  `file` longblob,
  `filename` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_pbvjjn3ibl12x7rwabb6c9m0s` (`createdBy_userId`),
  KEY `FK_bqppyxaov6iscy14dayjvwkyq` (`updatedBy_userId`),
  KEY `FK_am5ofd7xeclriws1i6yud82qo` (`review_info_id`),
  KEY `FK_l0e2b4r74p702mk5qugwxiy8t` (`reviewquest_id`),
  CONSTRAINT `FK_am5ofd7xeclriws1i6yud82qo` FOREIGN KEY (`review_info_id`) REFERENCES `review_info` (`id`) ON DELETE CASCADE,
  CONSTRAINT `FK_bqppyxaov6iscy14dayjvwkyq` FOREIGN KEY (`updatedBy_userId`) REFERENCES `user` (`userId`),
  CONSTRAINT `FK_l0e2b4r74p702mk5qugwxiy8t` FOREIGN KEY (`reviewquest_id`) REFERENCES `review_question` (`id`),
  CONSTRAINT `FK_pbvjjn3ibl12x7rwabb6c9m0s` FOREIGN KEY (`createdBy_userId`) REFERENCES `user` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `review_info`
--

DROP TABLE IF EXISTS `review_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `review_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdDate` datetime DEFAULT NULL,
  `updatedDate` datetime DEFAULT NULL,
  `assignDate` date DEFAULT NULL,
  `comment` longtext,
  `ctdModule` varchar(255) DEFAULT NULL,
  `dueDate` date DEFAULT NULL,
  `exec_summary` longtext,
  `file` longblob,
  `modcomment` longtext,
  `recomendType` varchar(255) DEFAULT NULL,
  `reviewStatus` varchar(255) DEFAULT NULL,
  `submitDate` date DEFAULT NULL,
  `submitted` bit(1) NOT NULL,
  `createdBy_userId` bigint(20) DEFAULT NULL,
  `updatedBy_userId` bigint(20) DEFAULT NULL,
  `prod_app_id` bigint(20) NOT NULL,
  `reviewer_id` bigint(20) NOT NULL,
  `sec_reviewer_id` bigint(20) DEFAULT NULL,
  `secreview` bit(1) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_2k2lnbct6jvg0fepi9v0fosg0` (`createdBy_userId`),
  KEY `FK_ac8uq2bkai038o1ame1j32rcn` (`updatedBy_userId`),
  KEY `FK_7ijtl0s11i5p74slyibhitmb4` (`prod_app_id`),
  KEY `FK_1owpyqqnf2sxx39q9qu7esley` (`reviewer_id`),
  KEY `FK_20u8wu78kcu13u17giorxu59g` (`sec_reviewer_id`),
  CONSTRAINT `FK_1owpyqqnf2sxx39q9qu7esley` FOREIGN KEY (`reviewer_id`) REFERENCES `user` (`userId`),
  CONSTRAINT `FK_20u8wu78kcu13u17giorxu59g` FOREIGN KEY (`sec_reviewer_id`) REFERENCES `user` (`userId`),
  CONSTRAINT `FK_2k2lnbct6jvg0fepi9v0fosg0` FOREIGN KEY (`createdBy_userId`) REFERENCES `user` (`userId`),
  CONSTRAINT `FK_7ijtl0s11i5p74slyibhitmb4` FOREIGN KEY (`prod_app_id`) REFERENCES `prodapplications` (`id`) ON DELETE CASCADE,
  CONSTRAINT `FK_ac8uq2bkai038o1ame1j32rcn` FOREIGN KEY (`updatedBy_userId`) REFERENCES `user` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `review_question`
--

DROP TABLE IF EXISTS `review_question`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `review_question` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdDate` datetime DEFAULT NULL,
  `updatedDate` datetime DEFAULT NULL,
  `ctdModule` varchar(255) DEFAULT NULL,
  `gen_med` bit(1) DEFAULT NULL,
  `header1` varchar(255) DEFAULT NULL,
  `header2` varchar(255) DEFAULT NULL,
  `new_med` bit(1) DEFAULT NULL,
  `question` longtext,
  `renewal` bit(1) DEFAULT NULL,
  `sra` bit(1) NOT NULL,
  `createdBy_userId` bigint(20) DEFAULT NULL,
  `updatedBy_userId` bigint(20) DEFAULT NULL,
  `majVariation` bit(1) DEFAULT NULL,
  `variation` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_pvny0met4dn0v924wvwehrcp` (`createdBy_userId`),
  KEY `FK_tcxswqyla7gl4i2tpihj99kgq` (`updatedBy_userId`),
  CONSTRAINT `FK_pvny0met4dn0v924wvwehrcp` FOREIGN KEY (`createdBy_userId`) REFERENCES `user` (`userId`),
  CONSTRAINT `FK_tcxswqyla7gl4i2tpihj99kgq` FOREIGN KEY (`updatedBy_userId`) REFERENCES `user` (`userId`)
) ENGINE=InnoDB AUTO_INCREMENT=126 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `reviewcomment`
--

DROP TABLE IF EXISTS `reviewcomment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reviewcomment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `comment` longtext,
  `comment_date` datetime DEFAULT NULL,
  `finalSummary` bit(1) NOT NULL,
  `recomendType` varchar(255) DEFAULT NULL,
  `reviewinfo_ID` bigint(20) DEFAULT NULL,
  `userId` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_rn3k34tctgwfucouc7lw23jge` (`reviewinfo_ID`),
  KEY `FK_lxtup80i1ypyaga30kcjxdr54` (`userId`),
  CONSTRAINT `FK_lxtup80i1ypyaga30kcjxdr54` FOREIGN KEY (`userId`) REFERENCES `user` (`userId`),
  CONSTRAINT `FK_rn3k34tctgwfucouc7lw23jge` FOREIGN KEY (`reviewinfo_ID`) REFERENCES `review_info` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `revinfo`
--

DROP TABLE IF EXISTS `revinfo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `revinfo` (
  `REV` int(11) NOT NULL AUTO_INCREMENT,
  `REVTSTMP` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`REV`)
) ENGINE=InnoDB AUTO_INCREMENT=118 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `role`
--

DROP TABLE IF EXISTS `role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `role` (
  `roleId` int(11) NOT NULL AUTO_INCREMENT,
  `createdDate` datetime DEFAULT NULL,
  `updatedDate` datetime DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `Displayname` varchar(255) DEFAULT NULL,
  `Rolename` varchar(255) DEFAULT NULL,
  `epermit` tinyint(4) DEFAULT '0',
  `createdBy_userId` bigint(20) DEFAULT NULL,
  `updatedBy_userId` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`roleId`),
  UNIQUE KEY `UK_nctmxadhieiw7aduxjy4dfglt` (`Rolename`),
  KEY `FK_dcn7ksfpo4176hplt8xfbw7ea` (`createdBy_userId`),
  KEY `FK_1mucr9ahcswn53ij3ytoemk60` (`updatedBy_userId`),
  CONSTRAINT `FK_1mucr9ahcswn53ij3ytoemk60` FOREIGN KEY (`updatedBy_userId`) REFERENCES `user` (`userId`),
  CONSTRAINT `FK_dcn7ksfpo4176hplt8xfbw7ea` FOREIGN KEY (`createdBy_userId`) REFERENCES `user` (`userId`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sample_med`
--

DROP TABLE IF EXISTS `sample_med`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sample_med` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdDate` datetime DEFAULT NULL,
  `updatedDate` datetime DEFAULT NULL,
  `batchNo` varchar(255) DEFAULT NULL,
  `comment` varchar(255) DEFAULT NULL,
  `exp_date` date DEFAULT NULL,
  `manuf_date` date DEFAULT NULL,
  `manuf_name` varchar(255) DEFAULT NULL,
  `quantity` varchar(255) DEFAULT NULL,
  `createdBy_userId` bigint(20) DEFAULT NULL,
  `updatedBy_userId` bigint(20) DEFAULT NULL,
  `country_id` bigint(20) DEFAULT NULL,
  `product_id` bigint(20) DEFAULT NULL,
  `sampleTest_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`),
  KEY `FK88D65B725141B5A` (`country_id`),
  KEY `FK88D65B7538C3791` (`createdBy_userId`),
  KEY `FK88D65B79A39331E` (`updatedBy_userId`),
  KEY `FK88D65B77EAC70BA` (`product_id`),
  KEY `FK88D65B783B5E9FB` (`sampleTest_id`),
  CONSTRAINT `FK88D65B725141B5A` FOREIGN KEY (`country_id`) REFERENCES `country` (`id`),
  CONSTRAINT `FK88D65B7538C3791` FOREIGN KEY (`createdBy_userId`) REFERENCES `user` (`userId`),
  CONSTRAINT `FK88D65B77EAC70BA` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`),
  CONSTRAINT `FK88D65B783B5E9FB` FOREIGN KEY (`sampleTest_id`) REFERENCES `sample_test` (`id`),
  CONSTRAINT `FK88D65B79A39331E` FOREIGN KEY (`updatedBy_userId`) REFERENCES `user` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sample_std`
--

DROP TABLE IF EXISTS `sample_std`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sample_std` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdDate` datetime DEFAULT NULL,
  `updatedDate` datetime DEFAULT NULL,
  `batchNo` varchar(255) DEFAULT NULL,
  `comment` varchar(255) DEFAULT NULL,
  `exp_date` date DEFAULT NULL,
  `manuf_date` date DEFAULT NULL,
  `manuf_name` varchar(255) DEFAULT NULL,
  `potency` varchar(255) DEFAULT NULL,
  `quantity` varchar(255) DEFAULT NULL,
  `std_name` varchar(255) DEFAULT NULL,
  `std_type` varchar(255) DEFAULT NULL,
  `createdBy_userId` bigint(20) DEFAULT NULL,
  `updatedBy_userId` bigint(20) DEFAULT NULL,
  `country_id` bigint(20) DEFAULT NULL,
  `sampleTest_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`),
  KEY `FK88D7E0E25141B5A` (`country_id`),
  KEY `FK88D7E0E538C3791` (`createdBy_userId`),
  KEY `FK88D7E0E9A39331E` (`updatedBy_userId`),
  KEY `FK88D7E0E83B5E9FB` (`sampleTest_id`),
  CONSTRAINT `FK88D7E0E25141B5A` FOREIGN KEY (`country_id`) REFERENCES `country` (`id`),
  CONSTRAINT `FK88D7E0E538C3791` FOREIGN KEY (`createdBy_userId`) REFERENCES `user` (`userId`),
  CONSTRAINT `FK88D7E0E83B5E9FB` FOREIGN KEY (`sampleTest_id`) REFERENCES `sample_test` (`id`),
  CONSTRAINT `FK88D7E0E9A39331E` FOREIGN KEY (`updatedBy_userId`) REFERENCES `user` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sample_test`
--

DROP TABLE IF EXISTS `sample_test`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sample_test` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdDate` datetime DEFAULT NULL,
  `updatedDate` datetime DEFAULT NULL,
  `letterGenerated` bit(1) DEFAULT NULL,
  `letterSent` bit(1) DEFAULT NULL,
  `quantity` varchar(255) DEFAULT NULL,
  `recievedDt` date DEFAULT NULL,
  `reqDt` date DEFAULT NULL,
  `resultDt` date DEFAULT NULL,
  `sampleRecieved` bit(1) DEFAULT NULL,
  `sampleTestStatus` varchar(255) DEFAULT NULL,
  `submitDate` date DEFAULT NULL,
  `createdBy_userId` bigint(20) DEFAULT NULL,
  `updatedBy_userId` bigint(20) DEFAULT NULL,
  `prodApplications_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_9bm92c5qdqifo2w86120fk4tx` (`createdBy_userId`),
  KEY `FK_dgdq71vqs5r6m1kpw7taufpn8` (`updatedBy_userId`),
  KEY `FK_bxbw6pqwth95naaht8mib323f` (`prodApplications_id`),
  CONSTRAINT `FK_9bm92c5qdqifo2w86120fk4tx` FOREIGN KEY (`createdBy_userId`) REFERENCES `user` (`userId`),
  CONSTRAINT `FK_bxbw6pqwth95naaht8mib323f` FOREIGN KEY (`prodApplications_id`) REFERENCES `prodapplications` (`id`) ON DELETE CASCADE,
  CONSTRAINT `FK_dgdq71vqs5r6m1kpw7taufpn8` FOREIGN KEY (`updatedBy_userId`) REFERENCES `user` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `samplecomment`
--

DROP TABLE IF EXISTS `samplecomment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `samplecomment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `comment` varchar(500) DEFAULT NULL,
  `comment_date` datetime DEFAULT NULL,
  `sampleTestStatus` varchar(255) DEFAULT NULL,
  `sampletest_ID` bigint(20) DEFAULT NULL,
  `userId` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_99omi6cppjy7gwpk34rir57pu` (`sampletest_ID`),
  KEY `FK_6erometoi93ft0racbxq7olem` (`userId`),
  CONSTRAINT `FK_6erometoi93ft0racbxq7olem` FOREIGN KEY (`userId`) REFERENCES `user` (`userId`),
  CONSTRAINT `FK_99omi6cppjy7gwpk34rir57pu` FOREIGN KEY (`sampletest_ID`) REFERENCES `sample_test` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `scheduler`
--

DROP TABLE IF EXISTS `scheduler`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `scheduler` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `appdataID` bigint(20) NOT NULL,
  `conceptID` bigint(20) NOT NULL,
  `Scheduled` date DEFAULT NULL,
  `ProcessUrl` varchar(255) DEFAULT NULL,
  `CreatedAt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `ChangedAt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `activityDataID` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FKscheduler602203` (`appdataID`),
  KEY `FKscheduler428774` (`conceptID`),
  KEY `FKscheduler665045` (`activityDataID`),
  CONSTRAINT `FKscheduler428774` FOREIGN KEY (`conceptID`) REFERENCES `concept` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FKscheduler602203` FOREIGN KEY (`appdataID`) REFERENCES `concept` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FKscheduler665045` FOREIGN KEY (`activityDataID`) REFERENCES `concept` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=50 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `shipment`
--

DROP TABLE IF EXISTS `shipment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shipment` (
  `Id` bigint(20) NOT NULL AUTO_INCREMENT,
  `import_permitId` bigint(20) DEFAULT NULL,
  `Departure` date DEFAULT NULL,
  `Arrival` date DEFAULT NULL,
  `PreShipment` tinyint(1) NOT NULL,
  `PostShipment` tinyint(1) NOT NULL,
  PRIMARY KEY (`Id`),
  KEY `FKshipment860142` (`import_permitId`),
  CONSTRAINT `FKshipment860142` FOREIGN KEY (`import_permitId`) REFERENCES `import_permit` (`Id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `shipmentdetail`
--

DROP TABLE IF EXISTS `shipmentdetail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shipmentdetail` (
  `Id` bigint(20) NOT NULL AUTO_INCREMENT,
  `import_permit_detailId` bigint(20) DEFAULT NULL,
  `shipmentId` bigint(20) DEFAULT NULL,
  `Units` decimal(19,0) DEFAULT NULL,
  PRIMARY KEY (`Id`),
  KEY `FKshipmentde308186` (`shipmentId`),
  KEY `FKshipmentde876551` (`import_permit_detailId`),
  CONSTRAINT `FKshipmentde308186` FOREIGN KEY (`shipmentId`) REFERENCES `shipment` (`Id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FKshipmentde876551` FOREIGN KEY (`import_permit_detailId`) REFERENCES `import_permit_detail` (`Id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sitechecklist`
--

DROP TABLE IF EXISTS `sitechecklist`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sitechecklist` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdDate` datetime DEFAULT NULL,
  `updatedDate` datetime DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `createdBy_userId` bigint(20) DEFAULT NULL,
  `updatedBy_userId` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_8m70y9d6o9ngpk63fhvyl4993` (`createdBy_userId`),
  KEY `FK_s3c55884kp8o5pihtjs68xso3` (`updatedBy_userId`),
  CONSTRAINT `FK_8m70y9d6o9ngpk63fhvyl4993` FOREIGN KEY (`createdBy_userId`) REFERENCES `user` (`userId`),
  CONSTRAINT `FK_s3c55884kp8o5pihtjs68xso3` FOREIGN KEY (`updatedBy_userId`) REFERENCES `user` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `spatialdata`
--

DROP TABLE IF EXISTS `spatialdata`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `spatialdata` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `labelsID` bigint(20) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `FKspatialdat698804` (`labelsID`),
  CONSTRAINT `FKspatialdat698804` FOREIGN KEY (`labelsID`) REFERENCES `concept` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sra`
--

DROP TABLE IF EXISTS `sra`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sra` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdDate` datetime DEFAULT NULL,
  `updatedDate` datetime DEFAULT NULL,
  `code` varchar(255) DEFAULT NULL,
  `country` varchar(255) DEFAULT NULL,
  `sraType` varchar(255) DEFAULT NULL,
  `createdBy_userId` bigint(20) DEFAULT NULL,
  `updatedBy_userId` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_a372sqtiyby6aslwxo9mme0r2` (`createdBy_userId`),
  KEY `FK_p3mk97oicp381jk8cfgjaq39w` (`updatedBy_userId`),
  CONSTRAINT `FK_a372sqtiyby6aslwxo9mme0r2` FOREIGN KEY (`createdBy_userId`) REFERENCES `user` (`userId`),
  CONSTRAINT `FK_p3mk97oicp381jk8cfgjaq39w` FOREIGN KEY (`updatedBy_userId`) REFERENCES `user` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `status_user`
--

DROP TABLE IF EXISTS `status_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `status_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `assignDate` date DEFAULT NULL,
  `complete` bit(1) NOT NULL,
  `module1SubmitDt` date DEFAULT NULL,
  `module2SubmitDt` date DEFAULT NULL,
  `module3SubmitDt` date DEFAULT NULL,
  `module4SubmitDt` date DEFAULT NULL,
  `review1` varchar(5000) DEFAULT NULL,
  `review2` varchar(5000) DEFAULT NULL,
  `review3` varchar(5000) DEFAULT NULL,
  `review4` varchar(5000) DEFAULT NULL,
  `module1` bigint(20) DEFAULT NULL,
  `module2` bigint(20) DEFAULT NULL,
  `module3` bigint(20) DEFAULT NULL,
  `module4` bigint(20) DEFAULT NULL,
  `prod_app_id` bigint(20) NOT NULL,
  `uploadedBy_userId` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_8334m4pdurbs9wvcge6icilee` (`module1`),
  KEY `FK_pqgkj9hlbr5d3vlst99xeqx` (`module2`),
  KEY `FK_fpaxmh0l86g1alyle1wr18ebu` (`module3`),
  KEY `FK_78oc9nat5fgbxj6sburmc9yfh` (`module4`),
  KEY `FK_4vs6ipw5aahap4fx6q6mdhelb` (`prod_app_id`),
  KEY `FK_4oqncum0m1rmmq9jrvd9y65mn` (`uploadedBy_userId`),
  CONSTRAINT `FK_4oqncum0m1rmmq9jrvd9y65mn` FOREIGN KEY (`uploadedBy_userId`) REFERENCES `user` (`userId`),
  CONSTRAINT `FK_4vs6ipw5aahap4fx6q6mdhelb` FOREIGN KEY (`prod_app_id`) REFERENCES `prodapplications` (`id`),
  CONSTRAINT `FK_78oc9nat5fgbxj6sburmc9yfh` FOREIGN KEY (`module4`) REFERENCES `user` (`userId`),
  CONSTRAINT `FK_8334m4pdurbs9wvcge6icilee` FOREIGN KEY (`module1`) REFERENCES `user` (`userId`),
  CONSTRAINT `FK_fpaxmh0l86g1alyle1wr18ebu` FOREIGN KEY (`module3`) REFERENCES `user` (`userId`),
  CONSTRAINT `FK_pqgkj9hlbr5d3vlst99xeqx` FOREIGN KEY (`module2`) REFERENCES `user` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `susp_comment`
--

DROP TABLE IF EXISTS `susp_comment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `susp_comment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `comment` varchar(500) DEFAULT NULL,
  `comment_date` datetime DEFAULT NULL,
  `suspdetail_id` bigint(20) NOT NULL,
  `userId` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_2f3g7cv5dxt0kkf452msm11l1` (`suspdetail_id`),
  KEY `FK_m7qoobf4b75n0kky9dj0axksx` (`userId`),
  CONSTRAINT `FK_2f3g7cv5dxt0kkf452msm11l1` FOREIGN KEY (`suspdetail_id`) REFERENCES `susp_detail` (`id`),
  CONSTRAINT `FK_m7qoobf4b75n0kky9dj0axksx` FOREIGN KEY (`userId`) REFERENCES `user` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `susp_detail`
--

DROP TABLE IF EXISTS `susp_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `susp_detail` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdDate` datetime DEFAULT NULL,
  `updatedDate` datetime DEFAULT NULL,
  `canceled` bit(1) DEFAULT NULL,
  `complete` bit(1) DEFAULT NULL,
  `decision` varchar(255) DEFAULT NULL,
  `decision_date` datetime DEFAULT NULL,
  `due_date` datetime DEFAULT NULL,
  `final_summ` longtext,
  `request_date` datetime DEFAULT NULL,
  `susp_end_date` datetime DEFAULT NULL,
  `susp_no` varchar(255) NOT NULL,
  `susp_st_date` datetime DEFAULT NULL,
  `suspensionStatus` varchar(255) DEFAULT NULL,
  `createdBy_userId` bigint(20) DEFAULT NULL,
  `updatedBy_userId` bigint(20) DEFAULT NULL,
  `moderator_userId` bigint(20) DEFAULT NULL,
  `prodApplications_id` bigint(20) DEFAULT NULL,
  `reviewer_userId` bigint(20) DEFAULT NULL,
  `batchNo` varchar(500) DEFAULT NULL,
  `head_summ` longtext,
  `moderator_summ` longtext,
  `notifRecieveDt` datetime DEFAULT NULL,
  `orgReported` varchar(500) DEFAULT NULL,
  `reason` varchar(500) DEFAULT NULL,
  `parentId` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_2shuw8kwc6y5s4s8le123o312` (`createdBy_userId`),
  KEY `FK_7x14r6gft5mgvn0wu0w06ns8u` (`updatedBy_userId`),
  KEY `FK_pf5m58dtw8mxtlnwn4c81clns` (`moderator_userId`),
  KEY `FK_osh6bfwlln8pyv2ew86ysfwkn` (`prodApplications_id`),
  KEY `FK_rhs7vs407iud8xsbansm3nblr` (`reviewer_userId`),
  CONSTRAINT `FK_2shuw8kwc6y5s4s8le123o312` FOREIGN KEY (`createdBy_userId`) REFERENCES `user` (`userId`),
  CONSTRAINT `FK_7x14r6gft5mgvn0wu0w06ns8u` FOREIGN KEY (`updatedBy_userId`) REFERENCES `user` (`userId`),
  CONSTRAINT `FK_osh6bfwlln8pyv2ew86ysfwkn` FOREIGN KEY (`prodApplications_id`) REFERENCES `prodapplications` (`id`) ON DELETE CASCADE,
  CONSTRAINT `FK_pf5m58dtw8mxtlnwn4c81clns` FOREIGN KEY (`moderator_userId`) REFERENCES `user` (`userId`),
  CONSTRAINT `FK_rhs7vs407iud8xsbansm3nblr` FOREIGN KEY (`reviewer_userId`) REFERENCES `user` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `task`
--

DROP TABLE IF EXISTS `task`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `task` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `description` varchar(48) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblsampletypes`
--

DROP TABLE IF EXISTS `tblsampletypes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tblsampletypes` (
  `sampleTestID` bigint(20) NOT NULL,
  `sample_type` varchar(255) NOT NULL,
  KEY `FK_4d9xmjfdyf0ipos7vgd5maky` (`sampleTestID`),
  CONSTRAINT `FK_4d9xmjfdyf0ipos7vgd5maky` FOREIGN KEY (`sampleTestID`) REFERENCES `sample_test` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblusecategories`
--

DROP TABLE IF EXISTS `tblusecategories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tblusecategories` (
  `prodID` bigint(20) NOT NULL,
  `useCategory` varchar(255) NOT NULL,
  KEY `FK_er8pia189hp3vemtmlo8cw2gv` (`prodID`),
  CONSTRAINT `FK_er8pia189hp3vemtmlo8cw2gv` FOREIGN KEY (`prodID`) REFERENCES `product` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblusecategories_aud`
--

DROP TABLE IF EXISTS `tblusecategories_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tblusecategories_aud` (
  `REV` int(11) NOT NULL,
  `prodID` bigint(20) NOT NULL,
  `useCategory` varchar(255) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`REV`,`prodID`,`useCategory`),
  CONSTRAINT `FK_ofmrn5e7l2ic7j4od2c86axlw` FOREIGN KEY (`REV`) REFERENCES `revinfo` (`REV`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `thing`
--

DROP TABLE IF EXISTS `thing`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `thing` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `conceptID` bigint(20) NOT NULL,
  `CreatedAt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `ChangedAt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `Url` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FKthing613083` (`conceptID`),
  CONSTRAINT `FKthing613083` FOREIGN KEY (`conceptID`) REFERENCES `concept` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=2183 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `thingarchive`
--

DROP TABLE IF EXISTS `thingarchive`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `thingarchive` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `filecopyID` bigint(20) NOT NULL,
  `conceptID` bigint(20) NOT NULL,
  `thingID` bigint(20) DEFAULT NULL,
  `CreatedAt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `amendedID` bigint(20) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `FKthingarchi831668` (`thingID`),
  KEY `FKthingarchi214609` (`conceptID`),
  KEY `FKthingarchi600388` (`filecopyID`),
  KEY `FKthingarchi869781` (`amendedID`),
  CONSTRAINT `FKthingarchi214609` FOREIGN KEY (`conceptID`) REFERENCES `concept` (`ID`),
  CONSTRAINT `FKthingarchi600388` FOREIGN KEY (`filecopyID`) REFERENCES `concept` (`ID`),
  CONSTRAINT `FKthingarchi831668` FOREIGN KEY (`thingID`) REFERENCES `thing` (`ID`),
  CONSTRAINT `FKthingarchi869781` FOREIGN KEY (`amendedID`) REFERENCES `concept` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `thingdict`
--

DROP TABLE IF EXISTS `thingdict`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `thingdict` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `thingID` bigint(20) DEFAULT NULL,
  `conceptID` bigint(20) NOT NULL,
  `Url` varchar(255) DEFAULT NULL,
  `Varname` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FKthingdict339477` (`conceptID`),
  KEY `FKthingdict264790` (`thingID`),
  CONSTRAINT `FKthingdict264790` FOREIGN KEY (`thingID`) REFERENCES `thing` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FKthingdict339477` FOREIGN KEY (`conceptID`) REFERENCES `concept` (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=8258 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `thingdoc`
--

DROP TABLE IF EXISTS `thingdoc`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `thingdoc` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `dictNodeID` bigint(20) NOT NULL,
  `thingID` bigint(20) DEFAULT NULL,
  `conceptID` bigint(20) NOT NULL,
  `DictUrl` varchar(255) DEFAULT NULL,
  `VarName` varchar(255) DEFAULT NULL,
  `DocUrl` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FKthingdoc737617` (`conceptID`),
  KEY `FKthingdoc187695` (`thingID`),
  KEY `FKthingdoc250361` (`dictNodeID`),
  CONSTRAINT `FKthingdoc187695` FOREIGN KEY (`thingID`) REFERENCES `thing` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FKthingdoc250361` FOREIGN KEY (`dictNodeID`) REFERENCES `concept` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FKthingdoc737617` FOREIGN KEY (`conceptID`) REFERENCES `concept` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=850 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `thingperson`
--

DROP TABLE IF EXISTS `thingperson`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `thingperson` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `thingID` bigint(20) DEFAULT NULL,
  `conceptID` bigint(20) NOT NULL,
  `VarName` varchar(255) DEFAULT NULL,
  `PersonUrl` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FKthingperso54921` (`conceptID`),
  KEY `FKthingperso870391` (`thingID`),
  CONSTRAINT `FKthingperso54921` FOREIGN KEY (`conceptID`) REFERENCES `concept` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FKthingperso870391` FOREIGN KEY (`thingID`) REFERENCES `thing` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=190 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `thingregister`
--

DROP TABLE IF EXISTS `thingregister`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `thingregister` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `conceptID` bigint(20) NOT NULL,
  `thingID` bigint(20) DEFAULT NULL,
  `Url` varchar(255) DEFAULT NULL,
  `VarName` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FKthingregis607509` (`thingID`),
  KEY `FKthingregis438768` (`conceptID`),
  CONSTRAINT `FKthingregis438768` FOREIGN KEY (`conceptID`) REFERENCES `concept` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FKthingregis607509` FOREIGN KEY (`thingID`) REFERENCES `thing` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `thingscheduler`
--

DROP TABLE IF EXISTS `thingscheduler`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `thingscheduler` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `conceptID` bigint(20) NOT NULL,
  `thingID` bigint(20) DEFAULT NULL,
  `Url` varchar(255) DEFAULT NULL,
  `VarName` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FKthingsched823940` (`thingID`),
  KEY `FKthingsched222337` (`conceptID`),
  CONSTRAINT `FKthingsched222337` FOREIGN KEY (`conceptID`) REFERENCES `concept` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FKthingsched823940` FOREIGN KEY (`thingID`) REFERENCES `thing` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=80 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `thingthing`
--

DROP TABLE IF EXISTS `thingthing`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `thingthing` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `conceptID` bigint(20) NOT NULL,
  `thingID` bigint(20) DEFAULT NULL,
  `Url` varchar(255) DEFAULT NULL,
  `Varname` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FKthingthing294571` (`thingID`),
  KEY `FKthingthing369258` (`conceptID`),
  CONSTRAINT `FKthingthing294571` FOREIGN KEY (`thingID`) REFERENCES `thing` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FKthingthing369258` FOREIGN KEY (`conceptID`) REFERENCES `concept` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1804 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `timeline`
--

DROP TABLE IF EXISTS `timeline`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `timeline` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `comment` varchar(500) DEFAULT NULL,
  `RegState` varchar(255) DEFAULT NULL,
  `StatusDate` date DEFAULT NULL,
  `PROD_APP_ID` bigint(19) NOT NULL,
  `USER_ID` bigint(20) NOT NULL,
  `shipmentId` bigint(20) DEFAULT NULL,
  `import_permitId` bigint(20) DEFAULT NULL,
  `prodapplicationsid` bigint(19) DEFAULT NULL,
  `shipmentId2` bigint(20) DEFAULT NULL,
  `import_permitId4` bigint(20) DEFAULT NULL,
  `import_permitId3` bigint(20) DEFAULT NULL,
  `import_permitId2` bigint(20) DEFAULT NULL,
  `useruserId` bigint(19) DEFAULT NULL,
  `Discriminator` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_t2c39mhswj72cf2uvdgerc9fs` (`PROD_APP_ID`),
  KEY `FK_mjwet3drc6om2gqyecnrnvix0` (`USER_ID`),
  KEY `FK_PIP_TIMELINE` (`import_permitId`),
  KEY `FKtimeline537213` (`import_permitId`),
  KEY `FKtimeline864423` (`shipmentId2`),
  KEY `FKtimeline786900` (`import_permitId4`),
  CONSTRAINT `FK_PIP_TIMELINE` FOREIGN KEY (`import_permitId`) REFERENCES `import_permit` (`Id`),
  CONSTRAINT `FK_mjwet3drc6om2gqyecnrnvix0` FOREIGN KEY (`USER_ID`) REFERENCES `user` (`userId`),
  CONSTRAINT `FK_t2c39mhswj72cf2uvdgerc9fs` FOREIGN KEY (`PROD_APP_ID`) REFERENCES `prodapplications` (`id`) ON DELETE CASCADE,
  CONSTRAINT `FKtimeline537213` FOREIGN KEY (`import_permitId`) REFERENCES `import_permit` (`Id`),
  CONSTRAINT `FKtimeline786900` FOREIGN KEY (`import_permitId4`) REFERENCES `import_permit` (`Id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FKtimeline864423` FOREIGN KEY (`shipmentId2`) REFERENCES `shipment` (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `track`
--

DROP TABLE IF EXISTS `track`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `track` (
  `Id` bigint(20) NOT NULL AUTO_INCREMENT,
  `roleExecutorId` int(10) DEFAULT NULL,
  `doneIpId` bigint(20) DEFAULT NULL,
  `Discriminator` varchar(255) NOT NULL,
  `Order` int(11) NOT NULL,
  `JobCode` varchar(255) DEFAULT NULL,
  `Completed` date DEFAULT NULL,
  `userExecutorId` bigint(19) DEFAULT NULL,
  `newStatusId` bigint(20) DEFAULT NULL,
  `prevStatusId` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`Id`),
  KEY `FKtrack318075` (`doneIpId`),
  KEY `FKtrack339993` (`roleExecutorId`),
  KEY `FKtrack386577` (`userExecutorId`),
  KEY `FKtrack567783` (`prevStatusId`),
  KEY `FKtrack588333` (`newStatusId`),
  CONSTRAINT `FKtrack318075` FOREIGN KEY (`doneIpId`) REFERENCES `import_permit` (`Id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FKtrack339993` FOREIGN KEY (`roleExecutorId`) REFERENCES `role` (`roleId`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FKtrack386577` FOREIGN KEY (`userExecutorId`) REFERENCES `user` (`userId`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FKtrack567783` FOREIGN KEY (`prevStatusId`) REFERENCES `options` (`Id`),
  CONSTRAINT `FKtrack588333` FOREIGN KEY (`newStatusId`) REFERENCES `options` (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `transaction`
--

DROP TABLE IF EXISTS `transaction`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `transaction` (
  `Id` bigint(20) NOT NULL AUTO_INCREMENT,
  `billId` bigint(20) DEFAULT NULL,
  `PaymentDate` date DEFAULT NULL,
  `Document` longblob,
  PRIMARY KEY (`Id`),
  KEY `FKtransactio255957` (`billId`),
  CONSTRAINT `FKtransactio255957` FOREIGN KEY (`billId`) REFERENCES `bill` (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Temporary view structure for view `tree_roots`
--

DROP TABLE IF EXISTS `tree_roots`;
/*!50001 DROP VIEW IF EXISTS `tree_roots`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `tree_roots` AS SELECT 
 1 AS `ID`,
 1 AS `Identifier`,
 1 AS `Label`,
 1 AS `Active`*/;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `userId` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdDate` datetime DEFAULT NULL,
  `updatedDate` datetime DEFAULT NULL,
  `address1` varchar(500) DEFAULT NULL,
  `address2` varchar(500) DEFAULT NULL,
  `zipcode` varchar(500) DEFAULT NULL,
  `comments` varchar(255) DEFAULT NULL,
  `companyName` varchar(255) DEFAULT NULL,
  `Email` varchar(255) DEFAULT NULL,
  `enabled` bit(1) NOT NULL,
  `faxNo` varchar(255) DEFAULT NULL,
  `language` varchar(255) DEFAULT NULL,
  `Name` varchar(255) DEFAULT NULL,
  `Password` varchar(255) DEFAULT NULL,
  `phoneNo` varchar(255) DEFAULT NULL,
  `registrationDate` datetime DEFAULT NULL,
  `timeZone` varchar(255) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  `Username` varchar(255) DEFAULT NULL,
  `createdBy_userId` bigint(20) DEFAULT NULL,
  `updatedBy_userId` bigint(20) DEFAULT NULL,
  `CNTRY_ID` bigint(20) DEFAULT NULL,
  `applcntId` bigint(20) DEFAULT NULL,
  `zipaddress` varchar(32) DEFAULT NULL,
  `applicantApplcntId` bigint(20) DEFAULT NULL,
  `user_roleID` int(11) DEFAULT NULL,
  `organizationID` bigint(20) DEFAULT NULL,
  `auxDataID` bigint(20) DEFAULT NULL,
  `conceptID` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`userId`),
  UNIQUE KEY `UK_sb8bbouer5wak8vyiiy4pf2bx` (`Username`),
  UNIQUE KEY `UK_f9dvvibvpfsldnu8wh3enop4i` (`Username`,`Email`),
  KEY `FK_2iyp5gq82l6c0hat9l8lfch9t` (`createdBy_userId`),
  KEY `FK_5u9syr9j560vpfhokcei9ivpk` (`updatedBy_userId`),
  KEY `FK_f9hwt21pbk5a23tmxpuibudb9` (`CNTRY_ID`),
  KEY `FK_pmlp2vri7s57jdmeqnsm6kyst` (`applcntId`),
  KEY `FKuser610100` (`user_roleID`),
  KEY `FKuser756152` (`auxDataID`),
  KEY `FKuser583735` (`organizationID`),
  KEY `FKuser881165` (`conceptID`),
  CONSTRAINT `FK_2iyp5gq82l6c0hat9l8lfch9t` FOREIGN KEY (`createdBy_userId`) REFERENCES `user` (`userId`),
  CONSTRAINT `FK_5u9syr9j560vpfhokcei9ivpk` FOREIGN KEY (`updatedBy_userId`) REFERENCES `user` (`userId`),
  CONSTRAINT `FK_f9hwt21pbk5a23tmxpuibudb9` FOREIGN KEY (`CNTRY_ID`) REFERENCES `country` (`id`),
  CONSTRAINT `FK_pmlp2vri7s57jdmeqnsm6kyst` FOREIGN KEY (`applcntId`) REFERENCES `applicant` (`applcntId`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FKuser583735` FOREIGN KEY (`organizationID`) REFERENCES `concept` (`ID`),
  CONSTRAINT `FKuser610100` FOREIGN KEY (`user_roleID`) REFERENCES `user_role` (`ID`),
  CONSTRAINT `FKuser756152` FOREIGN KEY (`auxDataID`) REFERENCES `concept` (`ID`),
  CONSTRAINT `FKuser881165` FOREIGN KEY (`conceptID`) REFERENCES `concept` (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=45 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_role`
--

DROP TABLE IF EXISTS `user_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_role` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `userId` bigint(20) DEFAULT NULL,
  `roleId` int(11) NOT NULL,
  `Active` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`ID`),
  KEY `FKuser_role847522` (`userId`),
  KEY `FKuser_role983344` (`roleId`),
  KEY `FKuser_role990319` (`userId`),
  CONSTRAINT `FKuser_role847522` FOREIGN KEY (`userId`) REFERENCES `user` (`userId`),
  CONSTRAINT `FKuser_role983344` FOREIGN KEY (`roleId`) REFERENCES `role` (`roleId`),
  CONSTRAINT `FKuser_role990319` FOREIGN KEY (`userId`) REFERENCES `user` (`userId`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `useraccess`
--

DROP TABLE IF EXISTS `useraccess`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `useraccess` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `Application` varchar(200) DEFAULT NULL,
  `IpAddress` varchar(1000) DEFAULT NULL,
  `loginDate` datetime DEFAULT NULL,
  `logoutDate` datetime DEFAULT NULL,
  `userId` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_s2asuvu0fqsbf8muqh4w0lr6m` (`userId`),
  CONSTRAINT `FK_s2asuvu0fqsbf8muqh4w0lr6m` FOREIGN KEY (`userId`) REFERENCES `user` (`userId`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=258 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `userdict`
--

DROP TABLE IF EXISTS `userdict`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `userdict` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `conceptID` bigint(20) NOT NULL,
  `useruserId` bigint(19) DEFAULT NULL,
  `Url` varchar(255) DEFAULT NULL,
  `Predicate` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FKuserdict782172` (`useruserId`),
  KEY `FKuserdict722377` (`conceptID`),
  CONSTRAINT `FKuserdict722377` FOREIGN KEY (`conceptID`) REFERENCES `concept` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FKuserdict782172` FOREIGN KEY (`useruserId`) REFERENCES `user` (`userId`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=349 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `validation`
--

DROP TABLE IF EXISTS `validation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `validation` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `ClassName` varchar(255) DEFAULT NULL,
  `FieldName` varchar(255) DEFAULT NULL,
  `Criteria` int(11) NOT NULL,
  `MessageKey` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `webresource`
--

DROP TABLE IF EXISTS `webresource`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `webresource` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Url` varchar(255) DEFAULT NULL,
  `File` longblob,
  `MediaType` varchar(255) DEFAULT NULL,
  `FileName` varchar(255) DEFAULT NULL,
  `FileSize` bigint(20) NOT NULL,
  `ApiUpload` varchar(255) DEFAULT NULL,
  `descriptionID` bigint(20) DEFAULT NULL,
  `prenameID` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FKwebresourc381888` (`prenameID`),
  KEY `FKwebresourc137107` (`descriptionID`),
  CONSTRAINT `FKwebresourc137107` FOREIGN KEY (`descriptionID`) REFERENCES `concept` (`ID`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `FKwebresourc381888` FOREIGN KEY (`prenameID`) REFERENCES `concept` (`ID`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `workspace`
--

DROP TABLE IF EXISTS `workspace`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `workspace` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdDate` datetime DEFAULT NULL,
  `updatedDate` datetime DEFAULT NULL,
  `datePattern` varchar(255) DEFAULT NULL,
  `defaultLocale` varchar(10) DEFAULT NULL,
  `detailReview` bit(1) DEFAULT NULL,
  `displatPricing` bit(1) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `pipRegDuration` int(11) DEFAULT NULL,
  `prodRegDuration` int(11) DEFAULT NULL,
  `secReview` bit(1) DEFAULT NULL,
  `createdBy_userId` bigint(20) DEFAULT NULL,
  `updatedBy_userId` bigint(20) DEFAULT NULL,
  `registrarName` varchar(255) DEFAULT NULL,
  `registraremail` varchar(255) DEFAULT NULL,
  `contentType` varchar(500) DEFAULT NULL,
  `file` longblob,
  `fileName` varchar(500) DEFAULT NULL,
  `emblemSvg` mediumtext,
  `title` varchar(255) DEFAULT NULL,
  `subtitle` varchar(255) DEFAULT NULL,
  `address1` varchar(255) DEFAULT NULL,
  `address2` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_dxsxla677kb5butgy0iv5fc9b` (`createdBy_userId`),
  KEY `FK_1qk3lbp8185prkm9wbn9vss31` (`updatedBy_userId`),
  CONSTRAINT `FK_1qk3lbp8185prkm9wbn9vss31` FOREIGN KEY (`updatedBy_userId`) REFERENCES `user` (`userId`),
  CONSTRAINT `FK_dxsxla677kb5butgy0iv5fc9b` FOREIGN KEY (`createdBy_userId`) REFERENCES `user` (`userId`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping events for database 'pdx2'
--

--
-- Dumping routines for database 'pdx2'
--
/*!50003 DROP PROCEDURE IF EXISTS `activities` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `activities`(appl bigint(20), go bool, exec varchar(80), lang varchar(20))
BEGIN
/*
	Extract all non-monitoring - non trace activities
    @return table _activities
    @params
    appl - application ID or null for all applications
    go - true - completed and incompleted. false - only completed
    exec - email of the executor. null - all executors
    lang - language in uppercase
    @example
    call activities(null,false,'oleksiik@unops.org','EN_US');
	select * from _activities;
*/
declare execo bool default true;
declare ex varchar(80) default 'nobodynever';
declare goo boolean default false;
declare appo bool default true;
declare app bigint(20) default 0;
/*
	executor defined or all executors
*/
if exec is not null then
	set ex=exec;
    set execo=false;
end if;
/*
only active or all
*/
if go then
	set goo=true;
end if;
/*
only for an application
*/
if appl is not null then
	set app=appl;
    set appo=false;
end if;

drop temporary table if exists _activities;
create temporary table _activities
SELECT his.*, exec.Identifier as 'email',val.Label as 'activity', aval.Label as 'workflow',v.Label as 'pref', act.Identifier as 'applicant'
FROM history his
join closure execo on execo.childID=his.activityID and execo.Level=1
join concept exec on exec.ID=execo.parentID
join closure varclo on varclo.parentID=his.actConfigID and varclo.Level=2
join concept var on var.ID=varclo.childID and var.Identifier='prefLabel'
join closure valclo on valclo.parentID=var.ID and valclo.Level=1
join concept val on val.ID=valclo.childID and val.Identifier=lang
join closure aclo on aclo.parentID=his.applDictID and aclo.Level=2
join concept acl on acl.ID = aclo.childID and acl.Identifier='prefLabel'
join closure avalo on avalo.parentID=acl.ID and avalo.Level=1
join concept aval on aval.ID=avalo.childID and aval.Identifier=lang
join closure oclo on oclo.parentID=his.applDataID and oclo.Level=2
join concept o on o.ID=oclo.childID and o.Identifier='prefLabel'
join closure vo on vo.parentID=o.ID and vo.Level=1
join concept v on v.ID=vo.childID and v.Identifier=lang
join closure acto on acto.childID=his.applDataID and acto.Level=1
join concept act on act.ID=acto.parentID
where (exec.Identifier=ex or execo) and (his.Go is null or goo) and (his.applicationID=app or appo);
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `dictlevel` */;
ALTER DATABASE `pdx2` CHARACTER SET latin1 COLLATE latin1_swedish_ci ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `dictlevel`(parent bigint(20), lang varchar(20))
    COMMENT 'get the next level of the dictionary to _dictlevel table'
BEGIN
call tempdiclevelselection(parent);
drop temporary table if exists _tempdiclevelselection1;
create temporary table _tempdiclevelselection1 select * from _tempdiclevelselection;
drop temporary table if exists _dictlevel;
create temporary table _dictlevel
select pref.ID as ID, pref.lang as lang, pref.val as pref, descr.val as description, pref.`active` as `active`, pref.identifier as 'identifier' 
from _tempdiclevelselection pref
join _tempdiclevelselection1 descr on descr.ID=pref.ID and descr.lang=pref.lang and pref.var='prefLabel' and descr.var='description'
where pref.lang=lang;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
ALTER DATABASE `pdx2` CHARACTER SET utf8 COLLATE utf8_general_ci ;
/*!50003 DROP PROCEDURE IF EXISTS `dictvariables` */;
ALTER DATABASE `pdx2` CHARACTER SET latin1 COLLATE latin1_swedish_ci ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `dictvariables`(rootid bigint(20), varname varchar(20))
    COMMENT 'get list of values of variable varname from a dictioanry with root Id rootid'
BEGIN
select distinct val.Label
from
(SELECT conc.* 
FROM concept conc
join closure clos on clos.parentID=rootid
where conc.ID=clos.childID and conc.Identifier=varname and conc.Active
) var
join closure clos on clos.parentID=var.ID and clos.Level=1
join concept val on val.ID=clos.childID and val.Active
join closure itemclo on itemclo.childID=val.ID and itemclo.Level=3
join concept item on item.ID=itemclo.parentID and item.Active;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
ALTER DATABASE `pdx2` CHARACTER SET utf8 COLLATE utf8_general_ci ;
/*!50003 DROP PROCEDURE IF EXISTS `executors_activity` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `executors_activity`(actconfigid bigint(20), lang varchar(20))
BEGIN
drop temporary table if exists _executi;
create temporary table _executi
select uconc.ID as 'ID', uconc.Identifier as 'email', uconc.ID as 'userconceptid', orgconc.ID as 'organizationid'
from concept conc
join thing th on th.conceptID=conc.ID
join thingdict tdr on tdr.thingID=th.ID and tdr.Url='dictionary.system.roles'
join userdict ud on ud.conceptID=tdr.conceptID 
join `user` usr on usr.userid=ud.useruserId
join concept uconc on uconc.ID=usr.auxDataID
join concept orgconc on orgconc.ID=usr.organizationID
join userdict compet on compet.useruserId=usr.userid and compet.Url='dictionary.guest.applications'
join concept appl on appl.ID=compet.conceptID
where conc.ID=actconfigid;

drop temporary table if exists _uname;
create temporary table _uname
select ex.*, val.Label as 'uname' 
from _executi ex
join closure varclo on varclo.parentID= ex.userconceptid and varclo.Level=2
join concept var on var.ID=varclo.childID and var.Identifier='prefLabel'
join closure valclo on valclo.parentID=var.ID and valclo.Level=1
join concept val on val.ID=valclo.childID and val.Identifier=lang;

drop temporary table if exists executors_activity;
create temporary table executors_activity
select distinct una.*, val.Label as 'orgname'
from _uname una
join closure varclo on varclo.parentID= una.organizationid and varclo.Level=2
join concept var on var.ID=varclo.childID and var.Identifier='prefLabel'
join closure valclo on valclo.parentID=var.ID and valclo.Level=1
join concept val on val.ID=valclo.childID and val.Identifier=lang;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `filelist` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `filelist`(dictrootid bigint(20), thingid bigint(20), lang varchar(20))
BEGIN
call dictlevel(dictrootid, lang);
drop temporary table if exists __dictlevel;
drop temporary table if exists _filelist;
create temporary table __dictlevel select * from _dictlevel;
create temporary table _filelist
select dict.ID as 'ID', dict.pref, dict.description, ifnull(att.filename,'upload_file') as 'filename'
from __dictlevel dict
left join (
	select dict.ID as 'ID', dict.pref as 'pref', dict.description as 'description', conc.Label as filename
	from _dictlevel dict
	join fileresource fres on fres.dictconceptID=dict.ID
	join thingdoc td on td.conceptID=fres.conceptID
	join concept conc on conc.ID=fres.conceptID
	where td.thingID=thingid) att on att.ID=dict.ID
    where dict.Active = true;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `list_activities` */;
ALTER DATABASE `pdx2` CHARACTER SET latin1 COLLATE latin1_swedish_ci ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `list_activities`(url varchar(100), executor varchar(100), lang varchar(20))
BEGIN
drop temporary table if exists _list_activities;
create temporary table _list_activities
select act.ID as 'ID', act.Label as 'activity',  own.Identifier as 'owner', val.Label as 'label', thing.CreatedAt as 'createdAt', thing.ChangedAt as 'changedAt'
from concept root
join closure ownclo on ownclo.parentID=root.ID and ownclo.Level=1
join concept own on own.ID=ownclo.childID
join closure execlo on execlo.parentID=root.ID and execlo.Level in (1,3)
join concept exec on exec.ID=execlo.childID and exec.Identifier=executor
join closure actclo on actclo.parentID=exec.ID and actclo.Level=1
join concept act on act.ID=actclo.childID and act.Active
join thing thing on thing.conceptID=act.ID
join closure prefclo on prefclo.parentID=act.ID and prefclo.Level=2
join concept pref on pref.ID=prefclo.childID and pref.Identifier='prefLabel'
join closure valclo on valclo.parentID=pref.ID and valclo.Level=1
join concept val on val.ID=valclo.childID and val.Identifier=lang
where root.Identifier=url;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
ALTER DATABASE `pdx2` CHARACTER SET utf8 COLLATE utf8_general_ci ;
/*!50003 DROP PROCEDURE IF EXISTS `monitoring` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `monitoring`(exec varchar(80), lang varchar(20))
BEGIN
/*
Seelct all monitoring (trace) activiities for a user given
@returns 
table _monitoring
@params
exec - executor's email
lang - language in upper case
@example
call monitoring('oleksiik@unops.org','EN_US');
select * from _monitoring;
*/
drop temporary table if exists _monitoring;
create temporary table _monitoring
SELECT his.*, exec.Identifier as 'email', aval.Label as 'workflow', v.Label as 'pref', act.Identifier as 'applicant'
FROM history his
join closure execo on execo.childID=his.activityID and execo.Level=1
join concept exec on exec.ID=execo.parentID
join closure aclo on aclo.parentID=his.applDictID and aclo.Level=2
join concept acl on acl.ID = aclo.childID and acl.Identifier='prefLabel'
join closure avalo on avalo.parentID=acl.ID and avalo.Level=1
join concept aval on aval.ID=avalo.childID and aval.Identifier=lang
join closure oclo on oclo.parentID=his.applDataID and oclo.Level=2
join concept o on o.ID=oclo.childID and o.Identifier='prefLabel'
join closure vo on vo.parentID=o.ID and vo.Level=1
join concept v on v.ID=vo.childID and v.Identifier=lang
join closure acto on acto.childID=his.applDataID and acto.Level=1
join concept act on act.ID=acto.parentID
where his.actConfigID is null and his.Go is null and exec.Identifier=exec;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `moveSubTree` */;
ALTER DATABASE `pdx2` CHARACTER SET latin1 COLLATE latin1_swedish_ci ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `moveSubTree`(rootNode bigint(20), newParent bigint(20))
    COMMENT 'Move subtree from rootNode undr the newParent. Idea is from https://www.percona.com/blog/2011/02/14/moving-subtrees-in-closure-table/'
BEGIN
delete a FROM closure AS a
JOIN closure AS d ON a.childID = d.childID and d.parentID=rootNode
LEFT JOIN closure AS x ON x.parentID = d.parentID AND x.childID = a.parentID
WHERE x.ID IS NULL;

INSERT INTO closure (parentID, childID, Level)
SELECT supertree.parentID, subtree.ChildID,
supertree.Level+subtree.Level+1
FROM closure AS supertree
JOIN closure AS subtree
WHERE subtree.parentID = rootNode
AND supertree.childID = newParent;

END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
ALTER DATABASE `pdx2` CHARACTER SET utf8 COLLATE utf8_general_ci ;
/*!50003 DROP PROCEDURE IF EXISTS `onlyRoot` */;
ALTER DATABASE `pdx2` CHARACTER SET latin1 COLLATE latin1_swedish_ci ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `onlyRoot`()
BEGIN
SELECT concept.ID, concept.Identifier, concept.Label, concept.`Active`
FROM concept concept
left join closure clos on clos.childID=concept.ID and clos.level!=0
where clos.ID is null;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
ALTER DATABASE `pdx2` CHARACTER SET utf8 COLLATE utf8_general_ci ;
/*!50003 DROP PROCEDURE IF EXISTS `persons` */;
ALTER DATABASE `pdx2` CHARACTER SET latin1 COLLATE latin1_swedish_ci ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `persons`(parent bigint(20), lang varchar(20))
BEGIN
drop temporary table if exists _persons;
create temporary table _persons
SELECT node.ID as 'ID', mnode.ID as 'mnodeID', val.Label as 'pref' 
FROM thingperson thp
join concept node on node.ID=thp.conceptID
join closure litclo on litclo.parentID=node.ID and level=1
join concept lit on lit.ID=litclo.childID and lit.Identifier='_LITERALS_'
join closure prefclo on prefclo.parentID=lit.ID and prefclo.Level=1
join concept pref on pref.ID=prefclo.childID and pref.Identifier='prefLabel'
join closure valclo on valclo.parentID=pref.ID and valclo.Level=1
join concept val on val.ID=valclo.childID and val.Identifier=lang
join thing th on th.ID=thp.thingID
join concept mnode on mnode.ID=th.conceptID and mnode.ID=parent;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
ALTER DATABASE `pdx2` CHARACTER SET utf8 COLLATE utf8_general_ci ;
/*!50003 DROP PROCEDURE IF EXISTS `prefdescription` */;
ALTER DATABASE `pdx2` CHARACTER SET latin1 COLLATE latin1_swedish_ci ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `prefdescription`(lang varchar(20))
    COMMENT 'get prefLabel and description variables for a language given'
BEGIN
call tempvarmap(lang);
drop temporary table if exists _tempvarmap1;
create temporary table _tempvarmap1 select * from _tempvarmap;
drop temporary table if exists _prefdescription;
create temporary table _prefdescription
select pref.mapId as ID, pref.lang as lang, pref.val as pref, descr.val as description, pref.mapActive as active 
from _tempvarmap pref
join _tempvarmap1 descr on descr.mapId=pref.mapId and descr.lang=pref.lang and pref.var='prefLabel' and descr.var='description';
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
ALTER DATABASE `pdx2` CHARACTER SET utf8 COLLATE utf8_general_ci ;
/*!50003 DROP PROCEDURE IF EXISTS `prepremovetrees` */;
ALTER DATABASE `pdx2` CHARACTER SET latin1 COLLATE latin1_swedish_ci ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `prepremovetrees`(rootid bigint(20))
    COMMENT 'prepare queries to remove trees from rootid. For development only'
BEGIN
SELECT concat('call remove_branch(',ID,');') FROM pdx2.tree_roots where ID>=rootid and Identifier not like 'dictionary%' and Identifier not like '%configu%';
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
ALTER DATABASE `pdx2` CHARACTER SET utf8 COLLATE utf8_general_ci ;
/*!50003 DROP PROCEDURE IF EXISTS `print_tree` */;
ALTER DATABASE `pdx2` CHARACTER SET latin1 COLLATE latin1_swedish_ci ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `print_tree`(parent bigint(20))
BEGIN
SELECT d.`iD`, d.`Identifier`,
      d.Label,
      d.Active,
       p.Level,
       GROUP_CONCAT(crumbs.parentID order by crumbs.parentID) AS breadcrumbs
FROM concept AS d
JOIN closure AS p ON d.`iD` = p.childID
JOIN closure AS crumbs ON crumbs.childID = p.childID
WHERE p.parentID = parent
GROUP BY d.`iD`,d.`Identifier`,d.Label, d.Active, p.Level
ORDER BY breadcrumbs;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
ALTER DATABASE `pdx2` CHARACTER SET utf8 COLLATE utf8_general_ci ;
/*!50003 DROP PROCEDURE IF EXISTS `registered` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `registered`(email varchar(80), url varchar(120), lang varchar(20))
BEGIN
/**
	Get only registered objects
    @params
		email- user's email, null means all
        url - root url of appication data, null means all
		lang - language
	@returns
    ID - application data ID
    regdate	- registration date
    expdate	- registration expiration date
    pref - name or prefLabel
    email	- owner's eMail
    url - root url for data, e.g. ws.site
	@example
		call registered('EN_US');
		select * from _registered;
**/
declare emailo bool default true;
declare em varchar(80) default 'nobodynowhere';
declare urlo bool default true;
declare ur varchar(120) default "neverland.nev";
if(email is not null) then
	set em=email;
    set emailo=false;
end if;

if(url is not null) then
	set ur=url;
    set urlo=false;
end if;

drop temporary table if exists _registered;
create temporary table _registered
SELECT reg.appdataID as 'ID', reg.register as 'regno', reg.RegisteredAt as 'regdate', reg.ValidTo as 'expdate', val.Label as 'pref', us.Identifier as 'email', url.Identifier as 'url'
FROM register reg
join closure varo on varo.parentID=reg.appdataID and varo.Level=2
join concept var on var.ID=varo.childID and var.Identifier='prefLabel'
join closure valo on valo.parentID=var.ID and valo.Level=1
join concept val on val.ID=valo.childID and val.Identifier=lang
join closure uso on uso.childID=reg.appdataID and uso.Level=1
join concept us on us.ID=uso.parentID
join closure urlo on urlo.childID=us.ID and urlo.Level=1
join concept url on url.ID=urlo.parentID
where reg.ValidTo>curdate() and (us.Identifier=em or emailo) and (url.Identifier=ur or urlo);
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `remove_branch` */;
ALTER DATABASE `pdx2` CHARACTER SET latin1 COLLATE latin1_swedish_ci ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `remove_branch`(root bigint(20))
    COMMENT 'Remove all branch starts with root node. Parameter is root node id'
BEGIN
drop temporary table if exists _tmp;
create temporary table _tmp
	select clos.childID as 'ID'
	from closure clos
	where clos.parentID=root;
delete from concept
 where ID in (
	select ID from _tmp
);
drop temporary table if exists _tmp;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
ALTER DATABASE `pdx2` CHARACTER SET utf8 COLLATE utf8_general_ci ;
/*!50003 DROP PROCEDURE IF EXISTS `report_sites` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `report_sites`(site_url varchar(80), dict_stage_url varchar(80), 
																		addr_url varchar(80), owner_url varchar(80), 
																		appl_inspection_url varchar(80), appl_renew_url varchar(80),
                                                                        lang varchar(80))
BEGIN
/*
@example
call report_sites('pharmacy.site','dictionary.host.applications','pharamcy.site.address',
							'pharmacy.site.owners','application.pharmacy.inspection',
                            'application.pharmacy.renew', 'EN_US');
select * from report_sites
*/
call sites_data(site_url,dict_stage_url,lang);
call site_addr(addr_url,lang);
call site_owners(owner_url,lang);
drop temporary table if exists report_sites;
create temporary table report_sites
select d.ID as 'ID', d.pref as 'pref', d.registrar as 'email', a.aus as 'address',a.gis as 'gis', o.owners as 'owners', 
reg.Register as 'regno', reg.RegisteredAt as 'regdate', reg.ValidTo as 'expdate',
 insp.come as 'inspdate', ren.come as 'renewvaldate'
from sites_data d
join site_addr a on a.ID=d.ID 
join site_owners o on o.ID=d.ID
left join register reg on reg.appdataID=d.ID
left join applications_active insp on insp.dataID=d.ID and insp.url=appl_inspection_url
left join applications_active ren on ren.dataID=d.ID and ren.url=appl_renew_url;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sites_data` */;
ALTER DATABASE `pdx2` CHARACTER SET latin1 COLLATE latin1_swedish_ci ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sites_data`(url varchar(80), statedic varchar(80), lang varchar(20))
BEGIN
/*
Get ID of sites data as well as sites names, states and applicant's emails
@parameters:
url - root url for site data
statedic - dictionary url for lifecycle stage
lang - language, upper case
@returns
ID is application data ID
state is name of lifecycle dictionary
transitat - date and time of the currently opened transition to this dictionary. Not the first one!
pref - name of the site
registrar - eMail of an applicant
clazz - root url of the data
@example
call sites_data('pharmacy.site','dictionary.host.applications','EN_US');
*/
drop temporary table if exists _transitions;
create temporary table _transitions
SELECT ad.ID as 'ID', root.Identifier as 'state', min(h.CreatedAt) as 'transitat'
FROM history h
join concept dc on dc.ID=h.applDictID
join closure clo on clo.childID=dc.ID and clo.Level=1
join concept root on root.ID=clo.parentID and root.Identifier=statedic
join concept ad on ad.ID=h.applDataID
where h.Go is null
group by ad.ID, root.Identifier;

drop temporary table if exists sites_data;
create temporary table sites_data 
select tr.ID as 'ID', tr.state as 'state', tr.transitat as 'transitat', val.Label as 'pref', ow.Identifier as 'registrar', cla.Identifier as 'clazz' 
from _transitions tr
join closure clo on clo.parentID=tr.ID and clo.Level=2
join concept var on var.ID=clo.childID and var.Identifier='prefLabel'
join closure valclo on valclo.parentID=var.ID and valclo.Level=1
join concept val on val.ID=valclo.childID and val.Identifier=lang
join closure oclo on oclo.childID=tr.ID and oclo.Level=1
join concept ow on ow.ID=oclo.parentID
join closure claclo on claclo.childID=ow.ID and claclo.Level=1
join concept cla on cla.ID=claclo.parentID and cla.Identifier=url;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
ALTER DATABASE `pdx2` CHARACTER SET utf8 COLLATE utf8_general_ci ;
/*!50003 DROP PROCEDURE IF EXISTS `site_addr` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `site_addr`( url varchar(100), lang varchar(20))
BEGIN
/*
Get admin units linked to concepts
@parameters
url - addresses tree url
lang - language
@returns
ID of concept
aus list of admin units
@example
call site_addr('pharamcy.site.address','EN_US');
*/
drop temporary table if exists _siteau;
create temporary table _siteau (INDEX my (ID))
SELECT t.conceptID as ID, au.ID as dictId, concept.Label as 'gis' 
FROM concept
join thingthing tt on tt.conceptID=concept.ID and tt.Url=url
join thing t on t.ID=tt.thingID
join thing ta on ta.conceptID=concept.ID
join thingdict tau on tau.thingID=ta.ID
join closure tauclo on tauclo.childID=tau.conceptID
join concept au on au.ID=tauclo.parentID;

drop temporary table if exists site_addr;
create temporary table site_addr  (INDEX my (ID))
select au.ID as 'ID', group_concat(val.Label) as 'aus' , au.gis as 'gis'
from _siteau au
join closure clo on clo.parentID=au.dictid and clo.Level=2
join concept var on var.ID=clo.childID and var.Identifier='prefLabel'
join closure valclo on valclo.parentID=var.ID and valclo.Level=1
join concept val on val.ID=valclo.childID and val.Identifier=lang
group by au.ID,au.gis;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `site_owners` */;
ALTER DATABASE `pdx2` CHARACTER SET latin1 COLLATE latin1_swedish_ci ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `site_owners`(url varchar(100), lang varchar(20))
BEGIN
drop temporary table if exists _siteowners;
create temporary table _siteowners
SELECT siteco.ID as 'ID', own.ID as 'ownerid'
FROM concept siteco
join thing ts on ts.conceptID=siteco.ID
join thingthing tt on tt.thingID=ts.ID and tt.Url=url
join concept oco on oco.ID=tt.conceptID 
join thing tot on tot.conceptID=oco.ID
join thingperson tpo on tpo.thingID=tot.ID
join concept own on own.ID=tpo.conceptID;

drop temporary table if exists site_owners;
create temporary table site_owners
select so.ID as ID, group_concat(val.Label) as 'owners'
from _siteowners so
join closure oclo on oclo.parentID= so.ownerid and oclo.Level=2
join concept var on var.ID=oclo.childID and var.Identifier='prefLabel'
join closure valclo on valclo.parentID=var.ID and valclo.Level=1
join concept val on val.ID=valclo.childID and val.Identifier=lang
group by so.ID;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
ALTER DATABASE `pdx2` CHARACTER SET utf8 COLLATE utf8_general_ci ;
/*!50003 DROP PROCEDURE IF EXISTS `site_registers` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `site_registers`(url varchar(100))
BEGIN
/*
Get data from registers ordered by register date, asc
@params 
url - url of register
@example
call site_registers('pharmacy.site.certificates');
select * from site_registers;
@Result
ID - application data ID
registeredat - registration date
regno - registration no
validto - valid until (otional)
createdat - date when this record has been created
*/
drop temporary table if exists site_registers;
create temporary table site_registers
SELECT appdt.ID as ID, reg.RegisteredAt as 'registeredat', reg.Register as 'regno', reg.ValidTo as 'validto' , reg.createdAt as 'createdat'
FROM register reg
join thingregister tr on tr.conceptID=reg.conceptID and tr.Url=url
join concept appdt on appdt.ID=reg.appdataID
order by reg.RegisteredAt;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `tempdiclevelselection` */;
ALTER DATABASE `pdx2` CHARACTER SET latin1 COLLATE latin1_swedish_ci ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `tempdiclevelselection`(parent bigint(20))
    COMMENT 'creates an intermediate data for the next dictionary level'
BEGIN
drop temporary table if exists _tempdiclevelselection;
create temporary table _tempdiclevelselection
SELECT clos.childID as ID, concept.ID as dictID, labels.Identifier as 'identifier', v.`active` as `active`, labels.var as var, labels.lang as lang, labels.val as val 
		FROM concept concept
		join closure clos on clos.parentID=concept.ID and clos.Level=1 and concept.ID=parent
		join concept v on v.ID=clos.childID
		join (
			SELECT concept.ID as nodeId, concept.Identifier as 'identifier', convar.Identifier as var,conval.Identifier as lang, conval.Label as val
			FROM concept concept
			join closure clos on clos.parentID=concept.ID and level=1
			join concept literal on literal.Identifier='_LITERALS_' and literal.ID=clos.childID
			join closure clovar on clovar.parentID=literal.ID and clovar.Level=1
			join concept convar on convar.ID=clovar.childID
			join closure cloval on cloval.parentID=convar.ID and cloval.Level=1
			join concept conval on conval.ID=cloval.childID
			) labels on labels.nodeID=clos.childID;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
ALTER DATABASE `pdx2` CHARACTER SET utf8 COLLATE utf8_general_ci ;
/*!50003 DROP PROCEDURE IF EXISTS `tempvarmap` */;
ALTER DATABASE `pdx2` CHARACTER SET latin1 COLLATE latin1_swedish_ci ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `tempvarmap`(lang varchar(20))
    COMMENT 'temporary table  _tempvarmap - all variables map on a language given'
BEGIN
drop temporary table if exists _tempvarmap;
create temporary table _tempvarmap
SELECT root.ID as mapId, root.Identifier as mapUrl, root.active as mapActive, convar.Identifier as var, conval.Identifier as lang,  conval.Label as val
FROM concept root
join closure clos on clos.parentID=root.ID and clos.level=1
join concept literal on literal.ID=clos.childID and literal.Identifier='_LITERALS_'
join closure clovar on clovar.parentID=literal.ID and clovar.Level=1
join concept convar on convar.id=clovar.childID
join closure cloval on cloval.parentID=convar.ID and cloval.Level=1
join concept conval on conval.ID=cloval.childID and conval.Identifier=lang;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
ALTER DATABASE `pdx2` CHARACTER SET utf8 COLLATE utf8_general_ci ;
/*!50003 DROP PROCEDURE IF EXISTS `workflow_activities` */;
ALTER DATABASE `pdx2` CHARACTER SET latin1 COLLATE latin1_swedish_ci ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `workflow_activities`(rootid bigint(20),  lang varchar(20))
    COMMENT 'get brief description on workflow activities. Activitiea are in right order. Result will be in temporary table workflow_data'
BEGIN
call workflow_configuration(rootid, 'prefLabel',lang);
drop temporary table if exists _pref;
create temporary table _pref
SELECT * from workflow_configuration;

call workflow_configuration(rootid, 'description',lang);
drop temporary table if exists _descr;
create temporary table _descr
SELECT * from workflow_configuration;

call workflow_configuration(rootid, 'background',lang);
drop temporary table if exists _back;
create temporary table _back
SELECT * from workflow_configuration;

drop temporary table if exists workflow_activities;
create temporary table workflow_activities
SELECT p.ID, p.Label as 'pref', d.Label as 'descr', b.Label as 'bg' 
from _pref p
join _descr d on d.ID=p.ID
join _back b on b.ID=p.ID;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
ALTER DATABASE `pdx2` CHARACTER SET utf8 COLLATE utf8_general_ci ;
/*!50003 DROP PROCEDURE IF EXISTS `workflow_configuration` */;
ALTER DATABASE `pdx2` CHARACTER SET latin1 COLLATE latin1_swedish_ci ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `workflow_configuration`(rootid bigint(20), varname varchar(20), lang varchar(20))
    COMMENT 'get single literal (varible) from all activities in workflow description. Activities will be in a right order. Used in workflow_activities'
BEGIN
drop temporary table if exists workflow_configuration;
create temporary table workflow_configuration
SELECT conc.ID, val.Label, val.Identifier 
FROM concept root
join closure clo on clo.parentID=root.ID
join concept conc on clo.childID=conc.ID and (conc.Identifier=concat(conc.ID,'') or conc.Identifier=root.Identifier) and conc.Active

join closure prefclo on prefclo.parentID=conc.ID and prefclo.level=2
join concept var on var.ID=prefclo.childID and var.Identifier=varname
join closure valclo on valclo.parentID=var.ID and valclo.Level=1
join concept val on val.ID=valclo.childID and val.Identifier=lang

where root.ID=rootid
order by clo.Level;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
ALTER DATABASE `pdx2` CHARACTER SET utf8 COLLATE utf8_general_ci ;

--
-- Final view structure for view `activity_data`
--

/*!50001 DROP VIEW IF EXISTS `activity_data`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `activity_data` AS select `his`.`ID` AS `ID`,`his`.`Come` AS `come`,`his`.`Go` AS `go`,(to_days(ifnull(`his`.`Go`,now())) - to_days(`his`.`Come`)) AS `days`,`his`.`applicationID` AS `applNodeId`,`his`.`actConfigID` AS `actConfigID`,`applicant`.`Identifier` AS `applicant`,`applurl`.`Identifier` AS `applurl`,`exec`.`Identifier` AS `executive`,`acturl`.`Identifier` AS `activityurl`,`val`.`Label` AS `pref`,`val`.`Identifier` AS `lang` from ((((((((((((`history` `his` join `closure` `aplclo` on(((`aplclo`.`childID` = `his`.`applicationID`) and (`aplclo`.`Level` = 1)))) join `concept` `applicant` on((`applicant`.`ID` = `aplclo`.`parentID`))) join `closure` `aurlclo` on(((`aurlclo`.`childID` = `applicant`.`ID`) and (`aurlclo`.`Level` = 1)))) join `concept` `applurl` on((`applurl`.`ID` = `aurlclo`.`parentID`))) join `closure` `execlo` on(((`execlo`.`childID` = `his`.`activityID`) and (`execlo`.`Level` = 1)))) join `concept` `exec` on((`exec`.`ID` = `execlo`.`parentID`))) join `closure` `acturlclo` on(((`acturlclo`.`childID` = `exec`.`ID`) and (`acturlclo`.`Level` = 1)))) join `concept` `acturl` on((`acturl`.`ID` = `acturlclo`.`parentID`))) join `closure` `prefclo` on(((`prefclo`.`parentID` = `his`.`applDataID`) and (`prefclo`.`Level` = 2)))) join `concept` `pref` on(((`pref`.`ID` = `prefclo`.`childID`) and (`pref`.`Identifier` = 'prefLabel')))) join `closure` `valclo` on(((`valclo`.`parentID` = `pref`.`ID`) and (`valclo`.`Level` = 1)))) join `concept` `val` on((`val`.`ID` = `valclo`.`childID`))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `applications_active`
--

/*!50001 DROP VIEW IF EXISTS `applications_active`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `applications_active` AS select `his`.`applDataID` AS `dataID`,`root`.`Identifier` AS `url`,min(`his`.`Come`) AS `come` from (((`history` `his` join `concept` `appl` on((`appl`.`ID` = `his`.`applicationID`))) join `closure` `clo` on(((`clo`.`childID` = `appl`.`ID`) and (`clo`.`Level` = 2)))) join `concept` `root` on((`root`.`ID` = `clo`.`parentID`))) where isnull(`his`.`Go`) group by `his`.`applDataID`,`root`.`Identifier` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `assm_var`
--

/*!50001 DROP VIEW IF EXISTS `assm_var`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `assm_var` AS select `assembly`.`conceptID` AS `ID`,`assembly`.`ID` AS `assemblyID`,`assembly`.`Required` AS `Required`,`assembly`.`Mult` AS `Mult`,`assembly`.`ReadOnly` AS `ReadOnly`,`assembly`.`Url` AS `Url`,`assembly`.`DictUrl` AS `DictUrl`,`assembly`.`Min` AS `Min`,`assembly`.`Max` AS `Max`,`assembly`.`FileTypes` AS `FileTypes`,`assembly`.`Discriminator` AS `Discriminator`,`assembly`.`Row` AS `Row`,`assembly`.`Col` AS `Col`,`assembly`.`Ord` AS `Ord`,`assembly`.`Clazz` AS `Clazz`,`par`.`ID` AS `nodeID`,`conc`.`ID` AS `varNodeId`,`conc`.`Active` AS `Active`,`conc`.`Identifier` AS `propertyName`,`val`.`Label` AS `pref`,`val`.`Identifier` AS `lang` from (((((((`assembly` join `concept` `conc` on((`conc`.`ID` = `assembly`.`conceptID`))) join `closure` `clo` on(((`clo`.`parentID` = `conc`.`ID`) and (`clo`.`Level` = 2)))) join `concept` `var` on(((`var`.`ID` = `clo`.`childID`) and (`var`.`Identifier` = 'prefLabel')))) join `closure` `valclo` on(((`valclo`.`parentID` = `var`.`ID`) and (`valclo`.`Level` = 1)))) join `concept` `val` on((`val`.`ID` = `valclo`.`childID`))) join `closure` `parclo` on(((`parclo`.`childID` = `conc`.`ID`) and (`parclo`.`Level` = 1)))) join `concept` `par` on((`par`.`ID` = `parclo`.`parentID`))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `personlist`
--

/*!50001 DROP VIEW IF EXISTS `personlist`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `personlist` AS select `pconc`.`ID` AS `ID`,`adata`.`ID` AS `appldataid`,`tp`.`PersonUrl` AS `personrooturl`,`val`.`Identifier` AS `lang`,`val`.`Label` AS `pref` from ((((((((((`thing` join `concept` `adata` on((`adata`.`ID` = `thing`.`conceptID`))) join `thingthing` `tt` on((`tt`.`thingID` = `thing`.`ID`))) join `concept` `pers` on((`pers`.`ID` = `tt`.`conceptID`))) join `thing` `perst` on((`perst`.`conceptID` = `pers`.`ID`))) join `thingperson` `tp` on((`tp`.`thingID` = `perst`.`ID`))) join `concept` `pconc` on((`pconc`.`ID` = `tp`.`conceptID`))) join `closure` `clo` on(((`clo`.`parentID` = `pconc`.`ID`) and (`clo`.`Level` = 2)))) join `concept` `var` on(((`var`.`ID` = `clo`.`childID`) and (`var`.`ID` = `clo`.`childID`) and (`var`.`Identifier` = 'prefLabel')))) join `closure` `cloval` on(((`cloval`.`parentID` = `var`.`ID`) and (`cloval`.`Level` = 1)))) join `concept` `val` on((`val`.`ID` = `cloval`.`childID`))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `resources`
--

/*!50001 DROP VIEW IF EXISTS `resources`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `resources` AS select `con`.`ID` AS `ID`,`con`.`Identifier` AS `url`,`lang`.`Identifier` AS `lang`,`val`.`Label` AS `description` from ((((((((`tree_roots` `tr` join `closure` `lanclo` on(((`lanclo`.`parentID` = `tr`.`ID`) and (`lanclo`.`Level` = 1)))) join `concept` `lang` on((`lang`.`ID` = `lanclo`.`childID`))) join `closure` `conclo` on(((`conclo`.`parentID` = `lang`.`ID`) and (`conclo`.`Level` = 1)))) join `concept` `con` on((`con`.`ID` = `conclo`.`childID`))) join `closure` `varclo` on(((`varclo`.`parentID` = `con`.`ID`) and (`varclo`.`Level` = 2)))) join `concept` `var` on(((`var`.`ID` = `varclo`.`childID`) and (`var`.`Identifier` = 'description')))) join `closure` `valclo` on(((`valclo`.`parentID` = `var`.`ID`) and (`valclo`.`Level` = 1)))) join `concept` `val` on(((`val`.`ID` = `valclo`.`childID`) and (`val`.`Identifier` = `lang`.`Identifier`)))) where ((`tr`.`Identifier` = 'configuration.resources') and `con`.`Active`) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `tree_roots`
--

/*!50001 DROP VIEW IF EXISTS `tree_roots`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `tree_roots` AS select `concept`.`ID` AS `ID`,`concept`.`Identifier` AS `Identifier`,`concept`.`Label` AS `Label`,`concept`.`Active` AS `Active` from (`concept` left join `closure` `clos` on(((`clos`.`childID` = `concept`.`ID`) and (`clos`.`Level` <> 0)))) where isnull(`clos`.`ID`) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2021-09-16 11:14:10
