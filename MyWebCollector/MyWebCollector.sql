/*
SQLyog Ultimate v11.24 (32 bit)
MySQL - 5.5.28 : Database - testdb
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`testdb` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `testdb`;

/*Table structure for table `tb_content` */

DROP TABLE IF EXISTS `tb_content`;

CREATE TABLE `tb_content` (
  `C_ID` int(11) NOT NULL AUTO_INCREMENT COMMENT '文章唯一标识',
  `C_Title` varchar(200) DEFAULT NULL COMMENT '文章标题',
  `C_Url` varchar(200) DEFAULT NULL COMMENT '文章来源URL',
  `C_Content` longtext COMMENT '内容',
  `C_Html` longtext COMMENT '网页元素',
  `C_AddTime` datetime DEFAULT NULL COMMENT '文章捉取时间或创建时间',
  `C_LastUpdateTime` datetime DEFAULT NULL COMMENT '最后修改时间',
  PRIMARY KEY (`C_ID`)
) ENGINE=MyISAM AUTO_INCREMENT=2155 DEFAULT CHARSET=utf8;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
