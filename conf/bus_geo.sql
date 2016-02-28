/*
 Navicat MySQL Data Transfer

 Source Server         : localhost
 Source Server Version : 50618
 Source Host           : localhost
 Source Database       : CMS

 Target Server Version : 50618
 File Encoding         : utf-8

 Date: 01/29/2016 18:26:17 PM
*/

SET NAMES utf8;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
--  Table structure for `bus_geo`
-- ----------------------------
DROP TABLE IF EXISTS `bus_geo`;
CREATE TABLE `bus_geo` (
  `bus` bigint(20) NOT NULL,
  `longitude` decimal(8,5) NOT NULL,
  `latitude` decimal(8,5) NOT NULL,
  `direction` int(11) NOT NULL,
  `hourSpeed` int(11) NOT NULL,
  `dateTime` datetime NOT NULL,
  `timestamp` bigint(20) NOT NULL,
  PRIMARY KEY (`bus`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

SET FOREIGN_KEY_CHECKS = 1;

-- ----------------------------
--  Table structure for `bus_info`
-- ----------------------------
DROP TABLE IF EXISTS `bus_info`;
CREATE TABLE `bus_info` (
  `busNum` varchar(32) NOT NULL,
  `geoDevID` bigint(20) NOT NULL,
  `targetCity` varchar(64) NOT NULL,
  PRIMARY KEY (`busNum`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

SET FOREIGN_KEY_CHECKS = 1;
