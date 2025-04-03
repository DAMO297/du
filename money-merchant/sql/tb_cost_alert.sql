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