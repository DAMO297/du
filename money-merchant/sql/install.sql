-- 邮费管理模块数据库结构
-- 版本: 1.0
-- 日期: 2023-04-03
-- 作者: DM

-- ----------------------------
-- 1. 创建邮费记录表
-- ----------------------------
DROP TABLE IF EXISTS `tb_cost`;
CREATE TABLE `tb_cost` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '邮费主键ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `dept_id` bigint(20) DEFAULT NULL COMMENT '部门ID',
  `logistics_company` varchar(100) NOT NULL COMMENT '物流公司名称',
  `last_four_digits` varchar(4) DEFAULT NULL,
  `full_tracking_number` varchar(50) DEFAULT NULL COMMENT '完整运单号',
  `cost_amount` decimal(10,2) NOT NULL COMMENT '花费金额',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `date_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `shipping_date` datetime DEFAULT NULL COMMENT '发货日期',
  `recipient_province` varchar(50) DEFAULT NULL COMMENT '收件省份',
  `recipient_city` varchar(50) DEFAULT NULL COMMENT '收件城市',
  `recipient_district` varchar(50) DEFAULT NULL COMMENT '收件区县',
  `recipient_address` text COMMENT '收件详细地址',
  `weight` decimal(10,2) DEFAULT NULL COMMENT '包裹重量(kg)',
  `receipt_image_url` varchar(255) DEFAULT NULL COMMENT '运单图片存储路径',
  `image_upload_time` datetime DEFAULT NULL COMMENT '图片上传时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_dept_id` (`dept_id`),
  KEY `idx_full_tracking_number` (`full_tracking_number`),
  KEY `idx_shipping_date` (`shipping_date`),
  KEY `idx_recipient_province` (`recipient_province`),
  KEY `idx_recipient_city` (`recipient_city`),
  KEY `idx_recipient_district` (`recipient_district`),
  CONSTRAINT `fk_cost_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_cost_dept` FOREIGN KEY (`dept_id`) REFERENCES `sys_dept` (`dept_id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8 COMMENT='邮费表';

-- ----------------------------
-- 2. 创建邮费预算表
-- ----------------------------
DROP TABLE IF EXISTS `tb_cost_budget`;
CREATE TABLE `tb_cost_budget` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '预算ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `dept_id` bigint(20) DEFAULT NULL COMMENT '部门ID',
  `budget_type` int(4) DEFAULT NULL COMMENT '预算类型(1:月度, 2:季度, 3:年度)',
  `budget_period` varchar(20) DEFAULT NULL COMMENT '预算期间(如2023-01, 2023-Q1, 2023)',
  `budget_amount` decimal(10,2) DEFAULT 0.00 COMMENT '预算金额',
  `used_amount` decimal(10,2) DEFAULT 0.00 COMMENT '已使用金额',
  `start_date` datetime DEFAULT NULL COMMENT '起始日期',
  `end_date` datetime DEFAULT NULL COMMENT '结束日期',
  `status` int(4) DEFAULT 1 COMMENT '状态(0:未启用, 1:进行中, 2:已结束, 3:已超出)',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  INDEX `idx_user_dept` (`user_id`, `dept_id`),
  INDEX `idx_period` (`budget_type`, `budget_period`),
  INDEX `idx_date_range` (`start_date`, `end_date`),
  INDEX `idx_status` (`status`),
  CONSTRAINT `fk_cost_budget_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_cost_budget_dept` FOREIGN KEY (`dept_id`) REFERENCES `sys_dept` (`dept_id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='邮费预算表';

-- ----------------------------
-- 3. 创建邮费异常提醒表
-- ----------------------------
DROP TABLE IF EXISTS `tb_cost_alert`;
CREATE TABLE `tb_cost_alert` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '提醒ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `dept_id` bigint(20) DEFAULT NULL COMMENT '部门ID',
  `threshold_type` int(4) DEFAULT 1 COMMENT '阈值类型(1:金额, 2:百分比)',
  `threshold_amount` decimal(10,2) DEFAULT 0.00 COMMENT '阈值金额',
  `alert_method` varchar(20) DEFAULT 'system' COMMENT '提醒方式(system:系统提醒, email:邮件, sms:短信)',
  `status` int(4) DEFAULT 1 COMMENT '状态(0:未启用, 1:已启用)',
  `last_triggered_time` datetime DEFAULT NULL COMMENT '上次触发时间',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  INDEX `idx_user_dept` (`user_id`, `dept_id`),
  INDEX `idx_status` (`status`),
  CONSTRAINT `fk_cost_alert_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_cost_alert_dept` FOREIGN KEY (`dept_id`) REFERENCES `sys_dept` (`dept_id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='邮费异常提醒表';

-- ----------------------------
-- 4. 创建邮费报告表
-- ----------------------------
DROP TABLE IF EXISTS `tb_cost_report`;
CREATE TABLE `tb_cost_report` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '报告ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `dept_id` bigint(20) DEFAULT NULL COMMENT '部门ID',
  `report_type` int(4) DEFAULT NULL COMMENT '报告类型(1:周报, 2:月报, 3:季报, 4:年报)',
  `report_name` varchar(100) DEFAULT NULL COMMENT '报告名称',
  `report_year` int(4) DEFAULT NULL COMMENT '报告年份',
  `report_period` int(4) DEFAULT NULL COMMENT '报告期间(周:1-53, 月:1-12, 季:1-4)',
  `start_date` datetime DEFAULT NULL COMMENT '起始日期',
  `end_date` datetime DEFAULT NULL COMMENT '结束日期',
  `total_cost` decimal(10,2) DEFAULT 0.00 COMMENT '总费用',
  `average_cost` decimal(10,2) DEFAULT 0.00 COMMENT '平均单笔费用',
  `item_count` int(11) DEFAULT 0 COMMENT '项目数量',
  `report_status` int(4) DEFAULT 0 COMMENT '报告状态(0:草稿, 1:已生成, 2:已发送)',
  `report_content` text COMMENT '报告内容(JSON格式)',
  `optimization_suggestions` text COMMENT '优化建议',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  INDEX `idx_user_dept` (`user_id`, `dept_id`),
  INDEX `idx_report_period` (`report_type`, `report_year`, `report_period`),
  INDEX `idx_date_range` (`start_date`, `end_date`),
  CONSTRAINT `fk_cost_report_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_cost_report_dept` FOREIGN KEY (`dept_id`) REFERENCES `sys_dept` (`dept_id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='邮费报告表';

-- ----------------------------
-- 5. 插入初始数据（可根据需要调整）
-- ----------------------------
-- 插入一条测试数据到邮费记录表
INSERT INTO `tb_cost` (`user_id`, `dept_id`, `full_tracking_number`, `logistics_company`, `cost_amount`, 
                      `shipping_date`, `create_time`) 
VALUES (1, 100, 'SF1234567890', '顺丰速运', 25.50, NOW(), NOW());

-- 插入一条测试预算数据
INSERT INTO `tb_cost_budget` (`user_id`, `dept_id`, `budget_type`, `budget_period`, `budget_amount`, 
                             `used_amount`, `start_date`, `end_date`, `status`, `create_time`) 
VALUES (1, 100, 1, '2023-04', 5000.00, 25.50, '2023-04-01 00:00:00', '2023-04-30 23:59:59', 1, NOW());

-- 插入一条测试提醒数据
INSERT INTO `tb_cost_alert` (`user_id`, `dept_id`, `threshold_type`, `threshold_amount`, 
                            `alert_method`, `status`, `create_time`) 
VALUES (1, 100, 1, 100.00, 'system', 1, NOW()); 