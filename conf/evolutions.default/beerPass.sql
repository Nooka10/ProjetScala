/*
 Navicat Premium Data Transfer

 Source Server         : Localhost
 Source Server Type    : MySQL
 Source Server Version : 50719
 Source Host           : localhost
 Source Database       : beerPass

 Target Server Type    : MySQL
 Target Server Version : 50719
 File Encoding         : utf-8

 Date: 05/28/2019 15:13:42 PM
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
--  Table structure for `ADDRESS`
-- ----------------------------
DROP TABLE IF EXISTS `ADDRESS`;
CREATE TABLE `ADDRESS` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `NO` text,
  `ROAD` text NOT NULL,
  `CITY` text NOT NULL,
  `POSTAL_CODE` int(11) NOT NULL,
  `COUNTRY` text NOT NULL,
  `LNG` double NOT NULL,
  `LAT` double NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `BEER`
-- ----------------------------
DROP TABLE IF EXISTS `BEER`;
CREATE TABLE `BEER` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `NAME` text NOT NULL,
  `BRAND` text NOT NULL,
  `DEGREE_ALCOHOL` double DEFAULT NULL,
  `IMAGE` text,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `COMPANY`
-- ----------------------------
DROP TABLE IF EXISTS `COMPANY`;
CREATE TABLE `COMPANY` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `NAME` text NOT NULL,
  `DESCRIPTION` text,
  `ADDRESS_ID` bigint(20) NOT NULL,
  `IMAGE` text,
  PRIMARY KEY (`ID`),
  KEY `ADDRES_ID_IDX` (`ADDRESS_ID`),
  CONSTRAINT `FK_COMPANY_ADDRESS` FOREIGN KEY (`ADDRESS_ID`) REFERENCES `ADDRESS` (`ID`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `DAILY_SCHEDULE`
-- ----------------------------
DROP TABLE IF EXISTS `DAILY_SCHEDULE`;
CREATE TABLE `DAILY_SCHEDULE` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `DAY` text NOT NULL,
  `H_OPEN` text NOT NULL,
  `H_CLOSE_AM` text,
  `H_OPEN_PM` text,
  `H_CLOSE` text NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `LINK_DAILY_SCHEDULE_COMPANY`
-- ----------------------------
DROP TABLE IF EXISTS `LINK_DAILY_SCHEDULE_COMPANY`;
CREATE TABLE `LINK_DAILY_SCHEDULE_COMPANY` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `COMPANY_ID` bigint(20) NOT NULL,
  `DAILY_SCHEDULE_ID` bigint(20) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `COMPANY_ID_IDX` (`COMPANY_ID`),
  KEY `DAILY_SCHEDULE_ID_IDX` (`DAILY_SCHEDULE_ID`),
  CONSTRAINT `FK_LINK_DAILY_SCHEDULE_COMPANY_COMPANY` FOREIGN KEY (`COMPANY_ID`) REFERENCES `COMPANY` (`ID`),
  CONSTRAINT `FK_LINK_DAILY_SCHEDULE_COMPANY_DAILY_SCHEDULE` FOREIGN KEY (`DAILY_SCHEDULE_ID`) REFERENCES `DAILY_SCHEDULE` (`ID`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `OFFER`
-- ----------------------------
DROP TABLE IF EXISTS `OFFER`;
CREATE TABLE `OFFER` (
  `COMPANY_ID` bigint(20) NOT NULL,
  `CLIENT_ID` bigint(20) NOT NULL,
  `BEER_ID` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`COMPANY_ID`,`CLIENT_ID`),
  KEY `COMPANY_ID_IDX` (`COMPANY_ID`),
  KEY `CLIENT_ID_IDX` (`CLIENT_ID`),
  KEY `BEER_ID_IDX` (`BEER_ID`),
  CONSTRAINT `FK_OFFER_BEER` FOREIGN KEY (`BEER_ID`) REFERENCES `BEER` (`ID`),
  CONSTRAINT `FK_OFFER_CLIENT` FOREIGN KEY (`CLIENT_ID`) REFERENCES `USER` (`ID`),
  CONSTRAINT `FK_OFFER_COMPANY` FOREIGN KEY (`COMPANY_ID`) REFERENCES `COMPANY` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `USER`
-- ----------------------------
DROP TABLE IF EXISTS `USER`;
CREATE TABLE `USER` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `FIRSTNAME` text NOT NULL,
  `LASTNAME` text NOT NULL,
  `EMAIL` text NOT NULL,
  `PASSWORD` text NOT NULL,
  `USER_TYPE` text NOT NULL,
  `COMPANY_ID` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `COMPANY_ID_IDX` (`COMPANY_ID`),
  CONSTRAINT `FK_USER_COMPANY` FOREIGN KEY (`COMPANY_ID`) REFERENCES `COMPANY` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

SET FOREIGN_KEY_CHECKS = 1;
