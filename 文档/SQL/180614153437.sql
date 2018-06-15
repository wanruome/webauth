/*
MySQL Backup
Source Server Version: 5.5.25
Source Database: webauth
Date: 2018/6/14 15:34:37
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
--  Table structure for `tbl_login_user`
-- ----------------------------
DROP TABLE IF EXISTS `tbl_login_user`;
CREATE TABLE `tbl_login_user` (
  `LOGIN_ID` varchar(64) NOT NULL,
  `LOGIN_NAME` varchar(64) DEFAULT NULL,
  `LOGIN_MOBILE` varchar(16) DEFAULT NULL,
  `LOGIN_EMAIL` varchar(64) DEFAULT NULL,
  `LOGIN_PWD` varchar(64) DEFAULT NULL,
  `STATUS` int(1) DEFAULT NULL,
  `VERSION` int(11) DEFAULT NULL,
  PRIMARY KEY (`LOGIN_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `tbl_msg_auth_info`
-- ----------------------------
DROP TABLE IF EXISTS `tbl_msg_auth_info`;
CREATE TABLE `tbl_msg_auth_info` (
  `UUID` varchar(64) NOT NULL DEFAULT '',
  `MSG_ADDR` varchar(64) NOT NULL DEFAULT '',
  `FUNCTION_ID` varchar(20) NOT NULL DEFAULT '',
  `SESSION_TOKEN` varchar(64) DEFAULT NULL,
  `MSG_CODE` varchar(16) DEFAULT NULL,
  `MSG_VALID_TIME` varchar(19) DEFAULT NULL,
  `MSG_TOKEN` varchar(64) DEFAULT NULL,
  `VERISON` int(11) DEFAULT NULL,
  PRIMARY KEY (`UUID`,`MSG_ADDR`,`FUNCTION_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `tbl_msg_funtion_info`
-- ----------------------------
DROP TABLE IF EXISTS `tbl_msg_funtion_info`;
CREATE TABLE `tbl_msg_funtion_info` (
  `FUNCTION_ID` varchar(16) NOT NULL DEFAULT '0',
  `FUNCTION_NAME` varchar(32) DEFAULT NULL,
  `AUTH_TYPE` int(1) DEFAULT NULL,
  `MAPPING` varchar(64) DEFAULT NULL,
  `TEMPLATE` varchar(512) DEFAULT NULL,
  `STATUS` int(1) DEFAULT NULL,
  PRIMARY KEY (`FUNCTION_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `tbl_msg_send_info`
-- ----------------------------
DROP TABLE IF EXISTS `tbl_msg_send_info`;
CREATE TABLE `tbl_msg_send_info` (
  `MSG_ID` varchar(11) NOT NULL DEFAULT '',
  `USER_ID` varchar(11) DEFAULT NULL,
  `FUNCTION_ID` varchar(16) DEFAULT NULL,
  `MSG_TYPE` int(11) DEFAULT NULL,
  `MSG_TOKEN` varchar(64) DEFAULT NULL,
  `MSG_ADDR` varchar(64) DEFAULT NULL,
  `MSG_CODE` varchar(16) DEFAULT NULL,
  `MSG_CONTENT` varchar(256) DEFAULT NULL,
  `MSG_VALID_TIME` varchar(19) DEFAULT NULL,
  `MSG_STATUS` int(1) DEFAULT NULL,
  `CREATE_DATE` varchar(12) DEFAULT NULL,
  `CREATE_TIME` varchar(19) DEFAULT NULL,
  PRIMARY KEY (`MSG_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `tbl_util_seq`
-- ----------------------------
DROP TABLE IF EXISTS `tbl_util_seq`;
CREATE TABLE `tbl_util_seq` (
  `SEQ_NAME` varchar(32) NOT NULL DEFAULT '',
  `SEQ_VALUE` int(11) DEFAULT NULL,
  PRIMARY KEY (`SEQ_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `tbl_uuid_keypair`
-- ----------------------------
DROP TABLE IF EXISTS `tbl_uuid_keypair`;
CREATE TABLE `tbl_uuid_keypair` (
  `UUID` varchar(64) NOT NULL DEFAULT '',
  `KEY_TYPE` varchar(10) NOT NULL DEFAULT '',
  `PUBLIC_KEY` varchar(512) DEFAULT NULL,
  `PRIVATE_KEY` varchar(2048) DEFAULT NULL,
  `KEY_VERSION` varchar(20) DEFAULT NULL,
  `VERSION` int(11) DEFAULT NULL,
  PRIMARY KEY (`UUID`,`KEY_TYPE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Records 
-- ----------------------------
INSERT INTO `tbl_login_user` VALUES ('100000','zhangmi','13355667799','zhangmi@163.com','14e1b600b1fd579f47433b88e8d85291','1','20'), ('100001',NULL,'13355667788',NULL,'c30fe4d9f98c7f1736f501bfd5cb3d13','1','1'), ('100002',NULL,'13355667789',NULL,'14e1b600b1fd579f47433b88e8d85291','1','1');
INSERT INTO `tbl_msg_auth_info` VALUES ('66778899','13738085782','1','tokentest','34836455','20180614135646','029331349341650','8');
INSERT INTO `tbl_msg_funtion_info` VALUES ('1','注册用户','0','/userInfo/doRegister','亲爱的用户，{appName}欢迎你注册。您本次的验证码为:{msgCode}，验证码有效期为{msgValidTime}。','1'), ('2','修改密码','0','/userInfo/doFindPwd','{appName}提醒您正在{msgType}找回密码。您本次的验证码为:{msgCode}，验证码有效期为{msgValidTime}。','1'), ('3','修改手机号','1','/userInfo/doModifyMobie','{appName}提醒您正在{msgTypeEmail}修改手机号。您本次的验证码为:{msgCode}，验证码有效期为{msgValidTime}。','1'), ('4','修改邮箱','1','/userInfo/doModifyEmail','{appName}提醒您正在{msgTypeMobile}修改邮箱。您本次的验证码为:{msgCode}，验证码有效期为{msgValidTime}。','1'), ('5','修改用户名','1','/userInfo/doModifyName','{appName}提醒您正在通过{msgType}修改用户名。您本次的验证码为:{msgCode}，验证码有效期为{msgValidTime}。','1'), ('6','执行变更操作','1','/userInfo/doGetModifyToken','{appName}提醒您正在执行{msgFunction}操作。您本次的验证码为:{msgCode}，验证码有效期为{msgValidTime}。','1');
INSERT INTO `tbl_msg_send_info` VALUES ('100000','123213','1','1','029331349341650','13738085782','34836455','亲爱的用户，浙江盛炬支付欢迎你注册。您本次的验证码为:34836455，验证码有效期为16分钟。','20180614135646','0','20180614','20180614134046'), ('100001','123213','1','1','82250511423332','13738085782','76144499','亲爱的用户，浙江盛炬支付欢迎你注册。您本次的验证码为:76144499，验证码有效期为16分钟。','20180614135728','0','20180614','20180614134128'), ('100002','123213','1','1','1846013518932','13738085782','05933410','亲爱的用户，浙江盛炬支付欢迎你注册。您本次的验证码为:05933410，验证码有效期为16分钟。','20180614140724','0','20180614','20180614135124'), ('100003','123213','1','1','1222420928367','13738085782','10987369','亲爱的用户，浙江盛炬支付欢迎你注册。您本次的验证码为:10987369，验证码有效期为16分钟。','20180614140840','0','20180614','20180614135240'), ('100004','123213','1','1','44092313161819','13738085782','55120053','亲爱的用户，浙江盛炬支付欢迎你注册。您本次的验证码为:55120053，验证码有效期为16分钟。','20180614141235','0','20180614','20180614135635'), ('100005','123213','1','1','314135104683619','13738085782','25611039','亲爱的用户，浙江盛炬支付欢迎你注册。您本次的验证码为:25611039，验证码有效期为16分钟。','20180614141306','0','20180614','20180614135706'), ('100006','123213','1','1','20417453164219','13738085782','18988463','亲爱的用户，浙江盛炬支付欢迎你注册。您本次的验证码为:18988463，验证码有效期为16分钟。','20180614141355','0','20180614','20180614135755'), ('100007','123213','1','1','10741234304736','13738085782','87031849','亲爱的用户，浙江盛炬支付欢迎你注册。您本次的验证码为:87031849，验证码有效期为16分钟。','20180614141942','0','20180614','20180614140342'), ('100008','123213','1','1','4473204451734','13738085782','86927528','亲爱的用户，浙江盛炬支付欢迎你注册。您本次的验证码为:86927528，验证码有效期为16分钟。','20180614142108','0','20180614','20180614140508'), ('229','123213','1','1','66778899','13738085782','943254','亲爱的用户，浙江盛炬支付欢迎你注册。您本次的验证码为:943254，验证码有效期为15分钟。','20180614092137','0','20180614','20180614090637'), ('239','123213','1','1','66778899','13738085782','43388537','亲爱的用户，浙江盛炬支付欢迎你注册。您本次的验证码为:43388537，验证码有效期为16分钟。','20180614102423','0','20180614','20180614100823');
INSERT INTO `tbl_util_seq` VALUES ('SEQ_LOGIN_USER_NEW_PK','100008'), ('tbl_login_user_new_pk','100002');
INSERT INTO `tbl_uuid_keypair` VALUES ('66778899','3DES','kXywNN83L8jIp5h640ZR0OMCcyrTUldn','kXywNN83L8jIp5h640ZR0OMCcyrTUldn','20180614092606','5'), ('66778899','RSA','MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCPEl44/utLAJ1hwl0h7XcU5tOqHCb6Yd3iWYx9LujG5xSWXwwzfK3/vi9US3Z2nueJno8FMlRTQOixgMBUvBXl6l/gGziu7KSXgNFvuQLe8C2Qvck+djHStc2QVKxztbJx51zijBzNZPd7so84G+oPvn89fauX63/k2zjxPjUG8wIDAQAB','MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAI8SXjj+60sAnWHCXSHtdxTm06ocJvph3eJZjH0u6MbnFJZfDDN8rf++L1RLdnae54mejwUyVFNA6LGAwFS8FeXqX+AbOK7spJeA0W+5At7wLZC9yT52MdK1zZBUrHO1snHnXOKMHM1k93uyjzgb6g++fz19q5frf+TbOPE+NQbzAgMBAAECgYA3zJoi8Ikj/2FiN0PLb6n55Oy4c7JgBLtdBD/esuTmbkmBPCCuE/pou+wJnETfjqoO6mAtTqvErJQUZnqckZ6YL2M0JZHRsFXT8fKEoLl0SJ4E1ylUO9bnkHlYRK9WEnBsyRgXzA7ezwqa26umUkWbgkJJqHWH91WM2oJKe4lRwQJBAM041HAbZeTSIxoK5A7MdDf8lorWHks8FXx46UdUdB3X7yr4qnxAuZxFKZj3F0C0qScteG6cpPiPdg+ulZKnUp8CQQCyeNFo3wkRrq0NXwm/3ZaG+Ky/BuHwW2JJv0qwAHldnhqjV/Nu5skMTt3GhPVefpPIdU5dq1lhYGfnWjukTd8tAkEArpQErCWyhXh5oeLX5PrqduIqEotZk/jV8mBNWn+uk+Cota56IV9SRmzTmSvBnSrSpuBYjgg1krk/3tFOrkYm0wJALU8a6uLkrIaKDID6OqmI049CW+Tla13n4A0eavxT036WCK4FcTxKAkPrbcDHgkvkPNn7ITClKmGNP2DbD2VtqQJAX80BNz0yBGZJUj/8oQJGAVzu87bONzcfxaNXmO6AChjFQD3hXOi/YRuPgJmxgr17AqY87rz0gUyMbTpcoqazLw==','20180614094117','5');
