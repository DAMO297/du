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