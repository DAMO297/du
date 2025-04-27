-- 商品预警配置表
create table tb_good_alert_config (
    id              bigint(20)      not null auto_increment    comment '配置ID',
    good_id         bigint(20)      not null                   comment '商品ID',
    price_change_threshold decimal(10,2)                       comment '价格波动预警阈值(%)',
    profit_threshold decimal(10,2)                             comment '利润预警阈值(%)',
    sales_abnormal_threshold int(11)                           comment '销售异常预警阈值(天)',
    enabled         char(1)         default '1'                comment '是否启用（1是 0否）',
    create_time     datetime                                   comment '创建时间',
    update_time     datetime                                   comment '更新时间',
    remark          varchar(500)    default null               comment '备注',
    primary key (id),
    key idx_good_id (good_id)
) engine=innodb auto_increment=100 comment = '商品预警配置表';

-- 商品预警记录表
create table tb_good_alert_record (
    id              bigint(20)      not null auto_increment    comment '记录ID',
    good_id         bigint(20)      not null                   comment '商品ID',
    alert_type      varchar(20)     not null                   comment '预警类型(PRICE-价格波动,PROFIT-利润异常,SALES-销售异常)',
    alert_content   varchar(500)    not null                   comment '预警内容',
    alert_value     decimal(10,2)   not null                   comment '预警值',
    threshold       decimal(10,2)   not null                   comment '预警阈值',
    alert_time      datetime        not null                   comment '预警时间',
    status          char(1)         default '0'                comment '处理状态（0未处理 1已处理）',
    handler         varchar(64)     default null               comment '处理人',
    handle_time     datetime                                   comment '处理时间',
    handle_remark   varchar(500)    default null               comment '处理备注',
    create_time     datetime                                   comment '创建时间',
    update_time     datetime                                   comment '更新时间',
    remark          varchar(500)    default null               comment '备注',
    primary key (id),
    key idx_good_id (good_id),
    key idx_alert_time (alert_time)
) engine=innodb auto_increment=100 comment = '商品预警记录表';

-- 创建触发器，在插入商品后自动创建预警配置
DELIMITER //
CREATE TRIGGER after_good_insert
AFTER INSERT ON tb_good
FOR EACH ROW
BEGIN
    -- 只有当商品状态为returned时才创建预警配置
    IF NEW.status = 'returned' THEN
        INSERT INTO tb_good_alert_config (
            good_id,
            price_change_threshold,
            profit_threshold,
            sales_abnormal_threshold,
            enabled,
            create_time
        ) VALUES (
            NEW.id,
            20.00,  -- 默认价格波动阈值20%
            5.00,   -- 默认利润率阈值5%
            30,     -- 默认销售异常阈值30天
            '1',    -- 默认启用
            NOW()   -- 当前时间
        );
    END IF;
END//
DELIMITER ; 