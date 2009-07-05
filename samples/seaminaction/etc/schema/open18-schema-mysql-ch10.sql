/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

DROP TABLE IF EXISTS `FACILITY`;
CREATE TABLE `FACILITY` (
  `ID` bigint(20) NOT NULL auto_increment,
  `ADDRESS` varchar(50) default NULL,
  `NAME` varchar(50) NOT NULL,
  `STATE` varchar(2) default NULL,
  `TYPE` varchar(15) NOT NULL,
  `COUNTRY` varchar(30) default NULL,
  `DESCRIPTION` text,
  `URI` varchar(255) default NULL,
  `CITY` varchar(30) default NULL,
  `ZIP` varchar(5) default NULL,
  `COUNTY` varchar(30) default NULL,
  `PHONE` varchar(10) default NULL,
  `PRICE_RANGE` int(11) default NULL,
  PRIMARY KEY  (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `COURSE`;
CREATE TABLE `COURSE` (
  `ID` bigint(20) NOT NULL auto_increment,
  `NAME` varchar(50) default NULL,
  `DESCRIPTION` text,
  `GREENS` enum('UNKNOWN','BENT','BERMUDA') NOT NULL,
  `DESIGNER` varchar(50) default NULL,
  `FAIRWAYS` enum('UNKNOWN','BENT','BERMUDA','ZOYSIA','BLUEGRASS','RYE','FESCUE') NOT NULL,
  `YEAR_BUILT` int(4) default NULL,
  `NUM_HOLES` int(2) NOT NULL default 18,
  `SIGNATURE_HOLE` bigint(20) default NULL,
  `FACILITY_ID` bigint(20) NOT NULL,
  PRIMARY KEY  (`ID`),
  KEY `FACILITY_ID` (`FACILITY_ID`),
  CONSTRAINT `FK_COURSE_REF_FACILITY` FOREIGN KEY (`FACILITY_ID`) REFERENCES `FACILITY` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `HOLE`;
CREATE TABLE `HOLE` (
  `ID` bigint(20) NOT NULL auto_increment,
  `NAME` varchar(25) default NULL,
  `NUMBER` int(2) NOT NULL,
  `M_PAR` int(1) NOT NULL,
  `L_PAR` int(1) NOT NULL,
  `L_HANDICAP` int(2) default NULL,
  `M_HANDICAP` int(2) default NULL,
  `COURSE_ID` bigint(20) NOT NULL,
  PRIMARY KEY  (`ID`),
  UNIQUE KEY `UK_HOLE_NUMBER` (`NUMBER`,`COURSE_ID`),
  KEY `COURSE_ID` (`COURSE_ID`),
  CONSTRAINT `FK_HOLE_REF_COURSE` FOREIGN KEY (`COURSE_ID`) REFERENCES `COURSE` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `TEE`;
CREATE TABLE `TEE` (
  `HOLE_ID` bigint(20) NOT NULL,
  `TEE_SET_ID` bigint(20) NOT NULL,
  `DISTANCE` int(4) NOT NULL,
  PRIMARY KEY  (`HOLE_ID`,`TEE_SET_ID`),
  KEY `HOLE_ID` (`HOLE_ID`),
  KEY `TEE_SET_ID` (`TEE_SET_ID`),
  CONSTRAINT `FK_TEE_REF_HOLE` FOREIGN KEY (`HOLE_ID`) REFERENCES `HOLE` (`ID`),
  CONSTRAINT `FK_TEE_REF_TEE_SET` FOREIGN KEY (`TEE_SET_ID`) REFERENCES `TEE_SET` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `TEE_SET`;
CREATE TABLE `TEE_SET` (
  `ID` bigint(20) NOT NULL auto_increment,
  `NAME` varchar(25) default NULL,
  `POS` int(1) default NULL,
  `COLOR` varchar(10) NOT NULL,
  `L_COURSE_RATING` double default NULL,
  `L_SLOPE_RATING` double default NULL,
  `M_COURSE_RATING` double default NULL,
  `M_SLOPE_RATING` double default NULL,
  `COURSE_ID` bigint(20) NOT NULL,
  PRIMARY KEY  (`ID`),
  UNIQUE KEY `UK_TEE_SET_COLOR` (`COLOR`,`COURSE_ID`),
  UNIQUE KEY `UK_TEE_SET_POS` (`POS`,`COURSE_ID`),
  KEY `COURSE_ID` (`COURSE_ID`),
  CONSTRAINT `FK_TEE_SET_REF_COURSE` FOREIGN KEY (`COURSE_ID`) REFERENCES `COURSE` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- New entities for chapter 04
DROP TABLE IF EXISTS `MEMBER`;
CREATE TABLE `MEMBER` (
  `ID` bigint(20) NOT NULL auto_increment,
	`USERNAME` varchar(255) not null,
	`PASSWORD_HASH` varchar(255) not null,
	`EMAIL_ADDRESS` varchar(255) not null,
	PRIMARY KEY (`ID`),
	UNIQUE KEY (`USERNAME`),
	UNIQUE KEY (`EMAIL_ADDRESS`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `GOLFER`;
CREATE TABLE `GOLFER` (
	`MEMBER_ID` bigint(20) not null,
	`LAST_NAME` varchar(40) not null,
	`FIRST_NAME` varchar(40) not null,
	`LOCATION` varchar(255),
	`GENDER` varchar(255),
	`JOINED` timestamp not null,
	`DOB` date,
	`SPECIALTY` VARCHAR(255),
	`PRO_STATUS` VARCHAR(255),
	PRIMARY KEY (`MEMBER_ID`),
  CONSTRAINT `FK_GOLFER_REF_MEMBER` FOREIGN KEY (`MEMBER_ID`) REFERENCES `MEMBER` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- New entities for chapter 10
DROP TABLE IF EXISTS `ROUND`;
CREATE TABLE `ROUND` (
	`ID` bigint(20) not null auto_increment unique,
	`DATE` date not null,
	`NOTES` longtext,
	`TOTAL_SCORE` integer not null,
	`VERSION` integer,
	`WEATHER` varchar(255) not null,
	`GOLFER_ID` bigint(20) not null,
	`TEE_SET_ID` bigint(20) not null,
	PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

alter table ROUND
	add index IDX_ROUND_TEE_SET (TEE_SET_ID),
	add constraint FK_ROUND_REF_TEE_SET
		foreign key (TEE_SET_ID)
		references TEE_SET (ID);

alter table ROUND
	add index IDX_ROUND_GOLFER (GOLFER_ID),
	add constraint FK_ROUND_REF_GOLFER
		foreign key (GOLFER_ID)
		references GOLFER (MEMBER_ID);

DROP TABLE IF EXISTS `SCORE`;
CREATE TABLE SCORE (
	`ID` bigint(20) not null auto_increment unique,
	`FAIRWAY` bit,
	`GIR` bit,
	`PUTTS` integer,
	`STROKES` integer not null check (STROKES>=1),
	`HOLE_ID` bigint(20) not null,
	`ROUND_ID` bigint(20) not null,
	primary key (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

alter table SCORE 
	add index IDX_SCORE_ROUND (ROUND_ID), 
	add constraint FK_SCORE_REF_ROUND 
		foreign key (ROUND_ID) 
		references ROUND (id);

alter table SCORE 
	add index IDX_SCORE_HOLE (HOLE_ID), 
	add constraint FK_SCORE_REF_HOLE 
		foreign key (HOLE_ID) 
		references HOLE (ID);

-- Increment for chapter 11
ALTER TABLE FACILITY 
	ADD COLUMN AGENT_ID BIGINT(20);
	
ALTER TABLE FACILITY 
	ADD CONSTRAINT FK_FACILITY_REF_MEMBER 
		FOREIGN KEY(AGENT_ID) 
		REFERENCES MEMBER(ID);


/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;