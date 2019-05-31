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

 Date: 05/30/2019 18:02:17 PM
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
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- ----------------------------
--  Records of `ADDRESS`
-- ----------------------------
BEGIN;
INSERT INTO `ADDRESS` VALUES ('1', '1', 'chemin de la brasserie', 'Yverdon-les-Bains', '1400', 'Switzerland', '1.1234', '2.3456'), ('2', '1', 'Route du succès', 'Yverdon-les-Bains', '1400', 'Switzerland', '1.789', '2.6486');
COMMIT;

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
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

-- ----------------------------
--  Records of `BEER`
-- ----------------------------
BEGIN;
INSERT INTO `BEER` VALUES ('1', 'Santé', 'Pu soif', '5.5', null), ('2', 'T bouré', 'Alcolo', '15.2', null), ('3', 'Soft one', 'Brasserie du coin', '2.5', null);
COMMIT;

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
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- ----------------------------
--  Records of `COMPANY`
-- ----------------------------
BEGIN;
INSERT INTO `COMPANY` VALUES ('1', 'Brasserie', 'description', '1', null), ('2', 'Bar', 'Vient boire un coup!', '2', null);
COMMIT;

-- ----------------------------
--  Table structure for `DAILY_SCHEDULE`
-- ----------------------------
DROP TABLE IF EXISTS `DAILY_SCHEDULE`;
CREATE TABLE `DAILY_SCHEDULE` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `DAY` enum('MONDAY','TUESDAY','WEDNESDAY','THURSDAY','FRIDAY','SATURDAY','SUNDAY') NOT NULL,
  `H_OPEN` text NOT NULL,
  `H_CLOSE_AM` text,
  `H_OPEN_PM` text,
  `H_CLOSE` text NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- ----------------------------
--  Records of `DAILY_SCHEDULE`
-- ----------------------------
BEGIN;
INSERT INTO `DAILY_SCHEDULE` VALUES ('1', 'MONDAY', '8h00', null, null, '18h00'), ('2', 'TUESDAY', '8h00', '11h30', '13h00', '18h00');
COMMIT;

-- ----------------------------
--  Table structure for `LINK_COMPANY_BEER`
-- ----------------------------
DROP TABLE IF EXISTS `LINK_COMPANY_BEER`;
CREATE TABLE `LINK_COMPANY_BEER` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `COMPANY_ID` bigint(20) NOT NULL,
  `BEER_ID` bigint(20) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `COMPANY_ID_IDX` (`COMPANY_ID`),
  KEY `BEER_ID_IDX` (`BEER_ID`) USING BTREE,
  CONSTRAINT `FK_LINK_COMPANY_BEER_BEER` FOREIGN KEY (`BEER_ID`) REFERENCES `BEER` (`ID`),
  CONSTRAINT `FK_LINK_COMPANY_BEER_COMPANY` FOREIGN KEY (`COMPANY_ID`) REFERENCES `COMPANY` (`ID`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;

-- ----------------------------
--  Records of `LINK_COMPANY_BEER`
-- ----------------------------
BEGIN;
INSERT INTO `LINK_COMPANY_BEER` VALUES ('2', '1', '1'), ('3', '1', '2'), ('4', '2', '2'), ('5', '2', '3');
COMMIT;

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
  CONSTRAINT `FK_LINK_DAILY_SCHEDULE_COMPANY_COMPANY` FOREIGN KEY (`COMPANY_ID`) REFERENCES `COMPANY` (`ID`) ON DELETE CASCADE,
  CONSTRAINT `FK_LINK_DAILY_SCHEDULE_COMPANY_DAILY_SCHEDULE` FOREIGN KEY (`DAILY_SCHEDULE_ID`) REFERENCES `DAILY_SCHEDULE` (`ID`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- ----------------------------
--  Records of `LINK_DAILY_SCHEDULE_COMPANY`
-- ----------------------------
BEGIN;
INSERT INTO `LINK_DAILY_SCHEDULE_COMPANY` VALUES ('1', '2', '1'), ('2', '2', '2');
COMMIT;

-- ----------------------------
--  Table structure for `OFFER`
-- ----------------------------
DROP TABLE IF EXISTS `OFFER`;
CREATE TABLE `OFFER` (
  `COMPANY_ID` bigint(20) NOT NULL,
  `CLIENT_ID` bigint(20) NOT NULL,
  `BEER_ID` bigint(20) DEFAULT NULL,
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`ID`,`COMPANY_ID`,`CLIENT_ID`),
  KEY `COMPANY_ID_IDX` (`COMPANY_ID`),
  KEY `CLIENT_ID_IDX` (`CLIENT_ID`),
  KEY `BEER_ID_IDX` (`BEER_ID`),
  CONSTRAINT `FK_OFFER_BEER` FOREIGN KEY (`BEER_ID`) REFERENCES `BEER` (`ID`),
  CONSTRAINT `FK_OFFER_CLIENT` FOREIGN KEY (`CLIENT_ID`) REFERENCES `USER` (`ID`),
  CONSTRAINT `FK_OFFER_COMPANY` FOREIGN KEY (`COMPANY_ID`) REFERENCES `COMPANY` (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;

-- ----------------------------
--  Records of `OFFER`
-- ----------------------------
BEGIN;
INSERT INTO `OFFER` VALUES ('1', '2', null, '1'), ('2', '2', null, '2'), ('1', '3', null, '3'), ('2', '3', null, '4'), ('1', '4', null, '5'), ('2', '4', null, '6');
COMMIT;

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
  `USER_TYPE` enum('CLIENT','EMPLOYEE') NOT NULL,
  `COMPANY_ID` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `COMPANY_ID_IDX` (`COMPANY_ID`),
  CONSTRAINT `FK_USER_COMPANY` FOREIGN KEY (`COMPANY_ID`) REFERENCES `COMPANY` (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

-- ----------------------------
--  Records of `USER`
-- ----------------------------
BEGIN;
INSERT INTO `USER` VALUES ('1', 'Benoît', 'Schöpfli', 'ben@barman.ch', '$2a$10$VWpdIS/ffa1BX.YfBTj1LuriUWgeIijyV7CPDFSw7ceMpbJaroD3S', 'EMPLOYEE', '1'), ('2', 'Antoine', 'Rochaille', 'tonio@boiiiire.ch', '$2a$10$u1bN0voVK6wfLLpM7jG8buHMQwghkfR5NI4DWg1PJgMKgoYtLeVpW', 'CLIENT', null), ('3', 'James', 'South', 'james@boiiiire.ch', '$2a$10$GidjtL1.EBEYgFWM5T/r1eY4nEi4CDE1GloF.q5oxhs9J7YnjoSjm', 'CLIENT', null), ('4', 'Jerem', 'chaton', 'jerem@boiiiire.ch', '$2a$10$0Z6QVbOfLDP75G/.Q9LUCetOTHZ2gb3C8NM7o.l.AGFiVk3frOMxe', 'CLIENT', null);
COMMIT;

-- ----------------------------
--  Triggers structure for table COMPANY
-- ----------------------------
DROP TRIGGER IF EXISTS `REMOVE ADDRESS BEFORE DELETE`;
delimiter ;;
CREATE TRIGGER `REMOVE ADDRESS BEFORE DELETE` BEFORE DELETE ON `COMPANY` FOR EACH ROW DELETE FROM ADDRESS WHERE ADDRESS.ID = OLD.ADDRESS_ID
 ;;
delimiter ;

delimiter ;;
-- ----------------------------
--  Triggers structure for table LINK_DAILY_SCHEDULE_COMPANY
-- ----------------------------
 ;;
delimiter ;
DROP TRIGGER IF EXISTS `REMOVE DAILY_SCHEDULE BEFORE DELETE`;
delimiter ;;
CREATE TRIGGER `REMOVE DAILY_SCHEDULE BEFORE DELETE` BEFORE DELETE ON `LINK_DAILY_SCHEDULE_COMPANY` FOR EACH ROW DELETE FROM DAILY_SCHEDULE WHERE DAILY_SCHEDULE.ID = OLD.DAILY_SCHEDULE_ID
 ;;
delimiter ;

delimiter ;;
-- ----------------------------
--  Triggers structure for table USER
-- ----------------------------
 ;;
delimiter ;
DROP TRIGGER IF EXISTS `CHECK COMPANY_ID IF EMPLOYEE ON INSERT`;
delimiter ;;
CREATE TRIGGER `CHECK COMPANY_ID IF EMPLOYEE ON INSERT` BEFORE INSERT ON `USER` FOR EACH ROW BEGIN
	IF NEW.USER_TYPE = 'EMPLOYEE' THEN
		IF NEW.COMPANY_ID IS NULL THEN
			SIGNAL SQLSTATE '45000' set message_text = 'Error : an employee must have a companyId';
		END IF;
	END IF;
END
 ;;
delimiter ;
DROP TRIGGER IF EXISTS `CHECK USERTYPE IF COMPANY_ID ON INSERT`;
delimiter ;;
CREATE TRIGGER `CHECK USERTYPE IF COMPANY_ID ON INSERT` BEFORE INSERT ON `USER` FOR EACH ROW BEGIN
	IF NEW.COMPANY_ID IS NOT NULL THEN
		SET NEW.USER_TYPE = 'EMPLOYEE';
	END IF;
END
 ;;
delimiter ;
DROP TRIGGER IF EXISTS `CHECK COMPANY_ID IF EMPLOYEE ON UPDATE`;
delimiter ;;
CREATE TRIGGER `CHECK COMPANY_ID IF EMPLOYEE ON UPDATE` BEFORE UPDATE ON `USER` FOR EACH ROW BEGIN
	IF NEW.USER_TYPE = 'EMPLOYEE' THEN
		IF NEW.COMPANY_ID IS NULL THEN
			SIGNAL SQLSTATE '45000' set message_text = 'Error : an employee must have a companyId';
		END IF;
	END IF;
END
 ;;
delimiter ;
DROP TRIGGER IF EXISTS `CHECK USERTYPE IF COMPANY_ID ON UPDATE`;
delimiter ;;
CREATE TRIGGER `CHECK USERTYPE IF COMPANY_ID ON UPDATE` BEFORE UPDATE ON `USER` FOR EACH ROW BEGIN
	IF NEW.COMPANY_ID IS NOT NULL THEN
		SET NEW.USER_TYPE = 'EMPLOYEE';
	END IF;
END
 ;;
delimiter ;

SET FOREIGN_KEY_CHECKS = 1;
