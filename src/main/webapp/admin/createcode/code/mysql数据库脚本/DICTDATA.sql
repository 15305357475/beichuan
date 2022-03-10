
SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `DICTDATA`
-- ----------------------------
DROP TABLE IF EXISTS `DICTDATA`;
CREATE TABLE `DICTDATA` (
 		`DICTDATA_ID` varchar(100) NOT NULL,
		`DICT_CODE` varchar(64) DEFAULT NULL COMMENT '字典编码',
		`PARENT_CODE` varchar(64) DEFAULT NULL COMMENT '父级编号',
		`PARENT_CODES` varchar(1000) DEFAULT NULL COMMENT '所有父级编号',
		`DICT_LABEL` varchar(100) DEFAULT NULL COMMENT '字典标签',
		`DICT_VALUE` varchar(100) DEFAULT NULL COMMENT '字典键值',
		`DICT_TYPE` varchar(100) DEFAULT NULL COMMENT '字典类型',
		`DESCRIPTION` varchar(500) DEFAULT NULL COMMENT '字典描述',
		`STATUS` varchar(1) DEFAULT NULL COMMENT '状态（0正常 1删除 2停用）',
		`CREATE_BY` varchar(64) DEFAULT NULL COMMENT '创建者',
		`CREATE_DATE` varchar(0) DEFAULT NULL COMMENT '创建时间',
		`UPDATE_BY` varchar(64) DEFAULT NULL COMMENT '更新者',
		`UPDATE_DATE` varchar(0) DEFAULT NULL COMMENT '更新时间',
		`REMARKS` varchar(500) DEFAULT NULL COMMENT '备注信息',
  		PRIMARY KEY (`DICTDATA_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
