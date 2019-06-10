/*
 Navicat Premium Data Transfer

 Source Server         : Amazon RDS
 Source Server Type    : MySQL
 Source Server Version : 50725
 Source Host           : db-scala.crrausiusiwg.eu-central-1.rds.amazonaws.com:3306
 Source Schema         : beerPass

 Target Server Type    : MySQL
 Target Server Version : 50725
 File Encoding         : 65001

 Date: 10/06/2019 14:18:22
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for ADDRESS
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
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of ADDRESS
-- ----------------------------
BEGIN;
INSERT INTO `ADDRESS` VALUES (1, '1', 'chemin de la brasserie', 'Yverdon-les-Bains', 1400, 'Switzerland', 6.6030356, 46.7752969);
INSERT INTO `ADDRESS` VALUES (2, '13', 'Route du succès', 'Yverdon-les-Bains', 1400, 'Switzerland', 6.3030356, 46.8752969);
INSERT INTO `ADDRESS` VALUES (3, '1', 'Route de la migraine', 'Yverdon-les-Bains', 1400, 'Switzerland', 6.4030356, 46.4752969);
INSERT INTO `ADDRESS` VALUES (4, '1', 'Route de la victoire', 'Yverdon-les-Bains', 1400, 'Switzerland', 6.7330356, 46.2552969);
INSERT INTO `ADDRESS` VALUES (5, '1', 'Route de la bière', 'Yverdon-les-Bains', 1400, 'Switzerland', 6.3530356, 46.552969);
INSERT INTO `ADDRESS` VALUES (6, '1', 'Route de la mine', 'Yverdon-les-Bains', 1400, 'Switzerland', 6.5030356, 46.4352969);
INSERT INTO `ADDRESS` VALUES (7, '1', 'Route du bistrot', 'Yverdon-les-Bains', 1400, 'Switzerland', 6.4530356, 46.5452969);
INSERT INTO `ADDRESS` VALUES (8, '1', 'Route du bar', 'Yverdon-les-Bains', 1400, 'Switzerland', 6.6230356, 46.4952969);
INSERT INTO `ADDRESS` VALUES (9, '1', 'Route du village', 'Yverdon-les-Bains', 1400, 'Switzerland', 6.4830356, 46.3352969);
INSERT INTO `ADDRESS` VALUES (10, '1', 'Route de Cully', 'Yverdon-les-Bains', 1400, 'Switzerland', 6.8530356, 46.8552969);
COMMIT;

-- ----------------------------
-- Table structure for BEER
-- ----------------------------
DROP TABLE IF EXISTS `BEER`;
CREATE TABLE `BEER` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `NAME` text NOT NULL,
  `BRAND` text NOT NULL,
  `DEGREE_ALCOHOL` double DEFAULT NULL,
  `IMAGE` text,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of BEER
-- ----------------------------
BEGIN;
INSERT INTO `BEER` VALUES (1, 'La Chouffe', 'Brasserie Achouffe', 8, 'https://i.goopics.net/ZbNQl.jpg');
INSERT INTO `BEER` VALUES (2, 'Heineken', 'Heineken', 5, 'https://i.goopics.net/ynXvg.png');
INSERT INTO `BEER` VALUES (3, 'Mythos', 'Mythos', 4.7, 'https://i.goopics.net/YjdKA.jpg');
INSERT INTO `BEER` VALUES (4, 'Alpha', 'Athenian Brewery', 5, 'https://i.goopics.net/JgLZJ.jpg');
INSERT INTO `BEER` VALUES (5, 'Boxer', 'Bière du Boxer', 5.2, 'https://i.goopics.net/pYjON.jpg');
INSERT INTO `BEER` VALUES (6, 'Fix', 'Fix brewery', 4.8, 'https://i.goopics.net/Rj3AV.png');
INSERT INTO `BEER` VALUES (7, 'La Houleuse', 'Docteur Gabs', 5, 'https://i.goopics.net/vV9J9.png');
INSERT INTO `BEER` VALUES (8, 'Blue Moon', 'Blue Moon Brewing Co', 5.4, 'https://i.goopics.net/AqZNo.png');
INSERT INTO `BEER` VALUES (9, 'Jorat - La Blanche', 'Brasserie du Jorat', 5.5, 'https://i.goopics.net/LyrYA.jpg');
INSERT INTO `BEER` VALUES (10, 'Bintang', 'Bintang', 4.7, 'https://i.goopics.net/5DXW2.jpg');
INSERT INTO `BEER` VALUES (11, 'Tsingtao', 'Tsingtao Brewery', 4.7, 'https://i.goopics.net/rYVx0.png');
INSERT INTO `BEER` VALUES (12, 'Punk IPA', 'Brewdog', 5.6, 'https://i.goopics.net/1onQN.png');
COMMIT;

-- ----------------------------
-- Table structure for COMPANY
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
  CONSTRAINT `FK_COMPANY_ADDRESS` FOREIGN KEY (`ADDRESS_ID`) REFERENCES `address` (`ID`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of COMPANY
-- ----------------------------
BEGIN;
INSERT INTO `COMPANY` VALUES (1, 'Café du Mont', 'Venez découvrir nos plus de 100 sortes différents de bière sur notre terrasse ensoleillée', 1, 'https://i.goopics.net/koAEe.jpg');
INSERT INTO `COMPANY` VALUES (2, 'Bar du Deven', 'Venez découvrir nos plus de 100 sortes différents de bière sur notre terrasse ensoleillée', 2, 'https://i.goopics.net/gdpgJ.jpg');
INSERT INTO `COMPANY` VALUES (3, 'Café des Artistes', 'Venez découvrir nos plus de 100 sortes différents de bière sur notre terrasse ensoleillée', 3, 'https://i.goopics.net/VKeO8.jpg');
INSERT INTO `COMPANY` VALUES (4, 'Blue Dark', 'Venez découvrir nos plus de 100 sortes différents de bière sur notre terrasse ensoleillée', 4, 'https://i.goopics.net/an0Ay.jpg');
INSERT INTO `COMPANY` VALUES (5, 'Etoile Blanche', 'Venez découvrir nos plus de 100 sortes différents de bière sur notre terrasse ensoleillée', 5, 'https://i.goopics.net/30GpJ.jpg');
INSERT INTO `COMPANY` VALUES (6, 'Les Boucaniers', 'Venez découvrir nos plus de 100 sortes différents de bière sur notre terrasse ensoleillée', 6, 'https://i.goopics.net/nQrDQ.jpg');
INSERT INTO `COMPANY` VALUES (7, 'La Poste', 'Venez découvrir nos plus de 100 sortes différents de bière sur notre terrasse ensoleillée', 7, 'https://i.goopics.net/92P5q.jpg');
INSERT INTO `COMPANY` VALUES (8, 'Brasserie du Château', 'Venez découvrir nos plus de 100 sortes différents de bière sur notre terrasse ensoleillée', 8, 'https://i.goopics.net/Gxvak.jpg');
INSERT INTO `COMPANY` VALUES (9, 'Le Punk', 'Venez découvrir nos plus de 100 sortes différents de bière sur notre terrasse ensoleillée', 9, 'https://i.goopics.net/eXY10.jpg');
INSERT INTO `COMPANY` VALUES (10, 'Café du Peuple', 'Venez découvrir nos plus de 100 sortes différents de bière sur notre terrasse ensoleillée', 10, 'https://i.goopics.net/XjwxX.jpg');
COMMIT;

-- ----------------------------
-- Table structure for DAILY_SCHEDULE
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
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of DAILY_SCHEDULE
-- ----------------------------
BEGIN;
INSERT INTO `DAILY_SCHEDULE` VALUES (1, 'MONDAY', '8h00', NULL, NULL, '18h00');
INSERT INTO `DAILY_SCHEDULE` VALUES (2, 'TUESDAY', '8h00', '11h30', '13h00', '18h00');
INSERT INTO `DAILY_SCHEDULE` VALUES (3, 'WEDNESDAY', '8h00', NULL, NULL, '18h00');
INSERT INTO `DAILY_SCHEDULE` VALUES (4, 'THURSDAY', '8h00', NULL, NULL, '18h00');
INSERT INTO `DAILY_SCHEDULE` VALUES (5, 'FRIDAY', '8h00', '11h30', '13h00', '18h00');
INSERT INTO `DAILY_SCHEDULE` VALUES (6, 'MONDAY', '8h00', NULL, NULL, '18h00');
INSERT INTO `DAILY_SCHEDULE` VALUES (7, 'SATURDAY', '8h00', NULL, NULL, '18h00');
INSERT INTO `DAILY_SCHEDULE` VALUES (8, 'SUNDAY', '8h00', NULL, NULL, '18h00');
COMMIT;

-- ----------------------------
-- Table structure for LINK_COMPANY_BEER
-- ----------------------------
DROP TABLE IF EXISTS `LINK_COMPANY_BEER`;
CREATE TABLE `LINK_COMPANY_BEER` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `COMPANY_ID` bigint(20) NOT NULL,
  `BEER_ID` bigint(20) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `COMPANY_ID_IDX` (`COMPANY_ID`),
  KEY `BEER_ID_IDX` (`BEER_ID`) USING BTREE,
  CONSTRAINT `FK_LINK_COMPANY_BEER_BEER` FOREIGN KEY (`BEER_ID`) REFERENCES `beer` (`ID`),
  CONSTRAINT `FK_LINK_COMPANY_BEER_COMPANY` FOREIGN KEY (`COMPANY_ID`) REFERENCES `company` (`ID`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=45 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of LINK_COMPANY_BEER
-- ----------------------------
BEGIN;
INSERT INTO `LINK_COMPANY_BEER` VALUES (1, 1, 1);
INSERT INTO `LINK_COMPANY_BEER` VALUES (2, 1, 2);
INSERT INTO `LINK_COMPANY_BEER` VALUES (3, 1, 3);
INSERT INTO `LINK_COMPANY_BEER` VALUES (4, 1, 5);
INSERT INTO `LINK_COMPANY_BEER` VALUES (5, 1, 6);
INSERT INTO `LINK_COMPANY_BEER` VALUES (6, 1, 7);
INSERT INTO `LINK_COMPANY_BEER` VALUES (7, 1, 9);
INSERT INTO `LINK_COMPANY_BEER` VALUES (8, 1, 12);
INSERT INTO `LINK_COMPANY_BEER` VALUES (9, 2, 5);
INSERT INTO `LINK_COMPANY_BEER` VALUES (10, 2, 7);
INSERT INTO `LINK_COMPANY_BEER` VALUES (11, 2, 9);
INSERT INTO `LINK_COMPANY_BEER` VALUES (12, 2, 12);
INSERT INTO `LINK_COMPANY_BEER` VALUES (13, 3, 10);
INSERT INTO `LINK_COMPANY_BEER` VALUES (14, 3, 11);
INSERT INTO `LINK_COMPANY_BEER` VALUES (15, 3, 9);
INSERT INTO `LINK_COMPANY_BEER` VALUES (16, 3, 12);
INSERT INTO `LINK_COMPANY_BEER` VALUES (17, 4, 7);
INSERT INTO `LINK_COMPANY_BEER` VALUES (18, 4, 8);
INSERT INTO `LINK_COMPANY_BEER` VALUES (19, 4, 9);
INSERT INTO `LINK_COMPANY_BEER` VALUES (20, 4, 12);
INSERT INTO `LINK_COMPANY_BEER` VALUES (21, 5, 5);
INSERT INTO `LINK_COMPANY_BEER` VALUES (22, 5, 11);
INSERT INTO `LINK_COMPANY_BEER` VALUES (23, 5, 6);
INSERT INTO `LINK_COMPANY_BEER` VALUES (24, 5, 12);
INSERT INTO `LINK_COMPANY_BEER` VALUES (25, 6, 5);
INSERT INTO `LINK_COMPANY_BEER` VALUES (26, 6, 2);
INSERT INTO `LINK_COMPANY_BEER` VALUES (27, 6, 1);
INSERT INTO `LINK_COMPANY_BEER` VALUES (28, 6, 12);
INSERT INTO `LINK_COMPANY_BEER` VALUES (29, 7, 5);
INSERT INTO `LINK_COMPANY_BEER` VALUES (30, 7, 7);
INSERT INTO `LINK_COMPANY_BEER` VALUES (31, 7, 9);
INSERT INTO `LINK_COMPANY_BEER` VALUES (32, 7, 12);
INSERT INTO `LINK_COMPANY_BEER` VALUES (33, 8, 5);
INSERT INTO `LINK_COMPANY_BEER` VALUES (34, 8, 3);
INSERT INTO `LINK_COMPANY_BEER` VALUES (35, 8, 7);
INSERT INTO `LINK_COMPANY_BEER` VALUES (36, 8, 12);
INSERT INTO `LINK_COMPANY_BEER` VALUES (37, 9, 5);
INSERT INTO `LINK_COMPANY_BEER` VALUES (38, 9, 6);
INSERT INTO `LINK_COMPANY_BEER` VALUES (39, 9, 9);
INSERT INTO `LINK_COMPANY_BEER` VALUES (40, 9, 4);
INSERT INTO `LINK_COMPANY_BEER` VALUES (41, 10, 2);
INSERT INTO `LINK_COMPANY_BEER` VALUES (42, 10, 3);
INSERT INTO `LINK_COMPANY_BEER` VALUES (43, 10, 4);
INSERT INTO `LINK_COMPANY_BEER` VALUES (44, 10, 12);
COMMIT;

-- ----------------------------
-- Table structure for LINK_DAILY_SCHEDULE_COMPANY
-- ----------------------------
DROP TABLE IF EXISTS `LINK_DAILY_SCHEDULE_COMPANY`;
CREATE TABLE `LINK_DAILY_SCHEDULE_COMPANY` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `COMPANY_ID` bigint(20) NOT NULL,
  `DAILY_SCHEDULE_ID` bigint(20) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `COMPANY_ID_IDX` (`COMPANY_ID`),
  KEY `DAILY_SCHEDULE_ID_IDX` (`DAILY_SCHEDULE_ID`),
  CONSTRAINT `FK_LINK_DAILY_SCHEDULE_COMPANY_COMPANY` FOREIGN KEY (`COMPANY_ID`) REFERENCES `company` (`ID`) ON DELETE CASCADE,
  CONSTRAINT `FK_LINK_DAILY_SCHEDULE_COMPANY_DAILY_SCHEDULE` FOREIGN KEY (`DAILY_SCHEDULE_ID`) REFERENCES `daily_schedule` (`ID`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=42 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of LINK_DAILY_SCHEDULE_COMPANY
-- ----------------------------
BEGIN;
INSERT INTO `LINK_DAILY_SCHEDULE_COMPANY` VALUES (1, 1, 1);
INSERT INTO `LINK_DAILY_SCHEDULE_COMPANY` VALUES (2, 1, 2);
INSERT INTO `LINK_DAILY_SCHEDULE_COMPANY` VALUES (3, 1, 3);
INSERT INTO `LINK_DAILY_SCHEDULE_COMPANY` VALUES (4, 1, 4);
INSERT INTO `LINK_DAILY_SCHEDULE_COMPANY` VALUES (5, 1, 5);
INSERT INTO `LINK_DAILY_SCHEDULE_COMPANY` VALUES (6, 1, 6);
INSERT INTO `LINK_DAILY_SCHEDULE_COMPANY` VALUES (7, 2, 1);
INSERT INTO `LINK_DAILY_SCHEDULE_COMPANY` VALUES (8, 2, 2);
INSERT INTO `LINK_DAILY_SCHEDULE_COMPANY` VALUES (9, 2, 3);
INSERT INTO `LINK_DAILY_SCHEDULE_COMPANY` VALUES (10, 3, 3);
INSERT INTO `LINK_DAILY_SCHEDULE_COMPANY` VALUES (11, 3, 4);
INSERT INTO `LINK_DAILY_SCHEDULE_COMPANY` VALUES (12, 4, 1);
INSERT INTO `LINK_DAILY_SCHEDULE_COMPANY` VALUES (13, 4, 2);
INSERT INTO `LINK_DAILY_SCHEDULE_COMPANY` VALUES (14, 4, 4);
INSERT INTO `LINK_DAILY_SCHEDULE_COMPANY` VALUES (15, 4, 5);
INSERT INTO `LINK_DAILY_SCHEDULE_COMPANY` VALUES (16, 5, 2);
INSERT INTO `LINK_DAILY_SCHEDULE_COMPANY` VALUES (17, 5, 4);
INSERT INTO `LINK_DAILY_SCHEDULE_COMPANY` VALUES (18, 5, 5);
INSERT INTO `LINK_DAILY_SCHEDULE_COMPANY` VALUES (19, 5, 6);
INSERT INTO `LINK_DAILY_SCHEDULE_COMPANY` VALUES (20, 6, 1);
INSERT INTO `LINK_DAILY_SCHEDULE_COMPANY` VALUES (21, 6, 2);
INSERT INTO `LINK_DAILY_SCHEDULE_COMPANY` VALUES (22, 6, 3);
INSERT INTO `LINK_DAILY_SCHEDULE_COMPANY` VALUES (23, 6, 7);
INSERT INTO `LINK_DAILY_SCHEDULE_COMPANY` VALUES (24, 7, 1);
INSERT INTO `LINK_DAILY_SCHEDULE_COMPANY` VALUES (25, 7, 2);
INSERT INTO `LINK_DAILY_SCHEDULE_COMPANY` VALUES (26, 7, 3);
INSERT INTO `LINK_DAILY_SCHEDULE_COMPANY` VALUES (27, 7, 5);
INSERT INTO `LINK_DAILY_SCHEDULE_COMPANY` VALUES (28, 7, 6);
INSERT INTO `LINK_DAILY_SCHEDULE_COMPANY` VALUES (29, 7, 7);
INSERT INTO `LINK_DAILY_SCHEDULE_COMPANY` VALUES (30, 8, 1);
INSERT INTO `LINK_DAILY_SCHEDULE_COMPANY` VALUES (31, 8, 4);
INSERT INTO `LINK_DAILY_SCHEDULE_COMPANY` VALUES (32, 8, 6);
INSERT INTO `LINK_DAILY_SCHEDULE_COMPANY` VALUES (33, 9, 1);
INSERT INTO `LINK_DAILY_SCHEDULE_COMPANY` VALUES (34, 9, 2);
INSERT INTO `LINK_DAILY_SCHEDULE_COMPANY` VALUES (35, 9, 4);
INSERT INTO `LINK_DAILY_SCHEDULE_COMPANY` VALUES (36, 9, 5);
INSERT INTO `LINK_DAILY_SCHEDULE_COMPANY` VALUES (37, 9, 6);
INSERT INTO `LINK_DAILY_SCHEDULE_COMPANY` VALUES (38, 10, 2);
INSERT INTO `LINK_DAILY_SCHEDULE_COMPANY` VALUES (39, 10, 4);
INSERT INTO `LINK_DAILY_SCHEDULE_COMPANY` VALUES (40, 10, 6);
INSERT INTO `LINK_DAILY_SCHEDULE_COMPANY` VALUES (41, 10, 7);
COMMIT;

-- ----------------------------
-- Table structure for OFFER
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
  CONSTRAINT `FK_OFFER_BEER` FOREIGN KEY (`BEER_ID`) REFERENCES `beer` (`ID`) ON DELETE CASCADE,
  CONSTRAINT `FK_OFFER_CLIENT` FOREIGN KEY (`CLIENT_ID`) REFERENCES `user` (`ID`) ON DELETE CASCADE,
  CONSTRAINT `FK_OFFER_COMPANY` FOREIGN KEY (`COMPANY_ID`) REFERENCES `company` (`ID`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of OFFER
-- ----------------------------
BEGIN;
INSERT INTO `OFFER` VALUES (1, 1, NULL, 1);
INSERT INTO `OFFER` VALUES (2, 1, NULL, 2);
INSERT INTO `OFFER` VALUES (3, 1, NULL, 3);
INSERT INTO `OFFER` VALUES (4, 1, NULL, 4);
INSERT INTO `OFFER` VALUES (8, 1, NULL, 8);
INSERT INTO `OFFER` VALUES (10, 1, NULL, 10);
INSERT INTO `OFFER` VALUES (1, 2, NULL, 11);
INSERT INTO `OFFER` VALUES (2, 2, NULL, 12);
INSERT INTO `OFFER` VALUES (3, 2, NULL, 13);
INSERT INTO `OFFER` VALUES (5, 2, NULL, 15);
INSERT INTO `OFFER` VALUES (8, 2, NULL, 18);
INSERT INTO `OFFER` VALUES (9, 2, NULL, 19);
INSERT INTO `OFFER` VALUES (1, 3, NULL, 21);
INSERT INTO `OFFER` VALUES (2, 3, NULL, 22);
INSERT INTO `OFFER` VALUES (3, 3, NULL, 23);
INSERT INTO `OFFER` VALUES (5, 3, NULL, 25);
INSERT INTO `OFFER` VALUES (8, 3, NULL, 28);
INSERT INTO `OFFER` VALUES (9, 3, NULL, 29);
INSERT INTO `OFFER` VALUES (4, 3, 2, 24);
INSERT INTO `OFFER` VALUES (6, 3, 3, 26);
INSERT INTO `OFFER` VALUES (6, 1, 4, 6);
INSERT INTO `OFFER` VALUES (9, 1, 5, 9);
INSERT INTO `OFFER` VALUES (6, 2, 7, 16);
INSERT INTO `OFFER` VALUES (7, 2, 9, 17);
INSERT INTO `OFFER` VALUES (10, 2, 9, 20);
INSERT INTO `OFFER` VALUES (5, 1, 12, 5);
INSERT INTO `OFFER` VALUES (7, 1, 12, 7);
INSERT INTO `OFFER` VALUES (4, 2, 12, 14);
INSERT INTO `OFFER` VALUES (7, 3, 12, 27);
INSERT INTO `OFFER` VALUES (10, 3, 12, 30);
INSERT INTO `OFFER` VALUES (6, 4, 12, 31);
INSERT INTO `OFFER` VALUES (6, 5, 12, 32);
INSERT INTO `OFFER` VALUES (6, 6, 12, 33);
COMMIT;

-- ----------------------------
-- Table structure for USER
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
  CONSTRAINT `FK_USER_COMPANY` FOREIGN KEY (`COMPANY_ID`) REFERENCES `company` (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of USER
-- ----------------------------
BEGIN;
INSERT INTO `USER` VALUES (1, 'Antoine', 'Rochaille', 'user1@beerpass.ch', '$2a$10$u1bN0voVK6wfLLpM7jG8buHMQwghkfR5NI4DWg1PJgMKgoYtLeVpW', 'CLIENT', NULL);
INSERT INTO `USER` VALUES (2, 'James', 'Smithy', 'user2@beerpass.ch', '$2a$10$GidjtL1.EBEYgFWM5T/r1eY4nEi4CDE1GloF.q5oxhs9J7YnjoSjm', 'CLIENT', NULL);
INSERT INTO `USER` VALUES (3, 'Jerem', 'Chaton', 'user3@beerpass.ch', '$2a$10$0Z6QVbOfLDP75G/.Q9LUCetOTHZ2gb3C8NM7o.l.AGFiVk3frOMxe', 'CLIENT', NULL);
INSERT INTO `USER` VALUES (4, 'Benoît', 'Schöpfli', 'bar1@beerpass.ch', '$2a$10$VWpdIS/ffa1BX.YfBTj1LuriUWgeIijyV7CPDFSw7ceMpbJaroD3S', 'EMPLOYEE', 1);
INSERT INTO `USER` VALUES (5, 'Jon', 'Athan', 'bar2@beerpass.ch', '$2a$10$VWpdIS/ffa1BX.YfBTj1LuriUWgeIijyV7CPDFSw7ceMpbJaroD3S', 'EMPLOYEE', 2);
INSERT INTO `USER` VALUES (6, 'John', 'Doe', 'bar3@beerpass.ch', '$2a$10$VWpdIS/ffa1BX.YfBTj1LuriUWgeIijyV7CPDFSw7ceMpbJaroD3S', 'EMPLOYEE', 3);
INSERT INTO `USER` VALUES (7, 'Bar', 'Man', 'bar4@beerpass.ch', '$2a$10$VWpdIS/ffa1BX.YfBTj1LuriUWgeIijyV7CPDFSw7ceMpbJaroD3S', 'EMPLOYEE', 4);
INSERT INTO `USER` VALUES (8, 'Tonio', 'Wisky', 'bar5@beerpass.ch', '$2a$10$VWpdIS/ffa1BX.YfBTj1LuriUWgeIijyV7CPDFSw7ceMpbJaroD3S', 'EMPLOYEE', 5);
INSERT INTO `USER` VALUES (9, 'Alfred', 'Barry', 'bar6@beerpass.ch', '$2a$10$VWpdIS/ffa1BX.YfBTj1LuriUWgeIijyV7CPDFSw7ceMpbJaroD3S', 'EMPLOYEE', 6);
INSERT INTO `USER` VALUES (10, 'Arthur', 'Jucy', 'bar7@beerpass.ch', '$2a$10$VWpdIS/ffa1BX.YfBTj1LuriUWgeIijyV7CPDFSw7ceMpbJaroD3S', 'EMPLOYEE', 7);
INSERT INTO `USER` VALUES (11, 'Juliette', 'Auby', 'bar8@beerpass.ch', '$2a$10$VWpdIS/ffa1BX.YfBTj1LuriUWgeIijyV7CPDFSw7ceMpbJaroD3S', 'EMPLOYEE', 8);
INSERT INTO `USER` VALUES (12, 'Victoria', 'Stale', 'bar9@beerpass.ch', '$2a$10$VWpdIS/ffa1BX.YfBTj1LuriUWgeIijyV7CPDFSw7ceMpbJaroD3S', 'EMPLOYEE', 9);
INSERT INTO `USER` VALUES (13, 'Greg', 'Ory', 'bar10@beerpass.ch', '$2a$10$VWpdIS/ffa1BX.YfBTj1LuriUWgeIijyV7CPDFSw7ceMpbJaroD3S', 'EMPLOYEE', 10);
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
