-- =============================================
-- SOMS 客户服务数据库建表脚本
-- Database: soms_customer
-- =============================================

CREATE DATABASE IF NOT EXISTS `soms_customer` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE `soms_customer`;

-- 客户表
CREATE TABLE IF NOT EXISTS `customer` (
    `customer_id`    BIGINT          NOT NULL AUTO_INCREMENT COMMENT '会员ID',
    `customer_name`  VARCHAR(64)     NOT NULL                COMMENT '会员姓名',
    `mobile`         VARCHAR(11)     NOT NULL                COMMENT '手机号',
    `customer_level` TINYINT         DEFAULT 1               COMMENT '会员等级：1普通会员，2银卡，3金卡',
    `total_consume`  DECIMAL(10,2)   DEFAULT 0.00            COMMENT '累计消费金额',
    `balance`        DECIMAL(10,2)   DEFAULT 0.00            COMMENT '账户余额',
    `points`         INT             NOT NULL DEFAULT 0      COMMENT '积分余额',
    `status`         TINYINT         NOT NULL DEFAULT 1      COMMENT '状态：0禁用，1正常',
    `gmt_create`     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_modified`   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`     TINYINT         NOT NULL DEFAULT 0      COMMENT '逻辑删除：0未删除，1已删除',
    PRIMARY KEY (`customer_id`),
    UNIQUE KEY `uk_mobile` (`mobile`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员客户信息表';

-- 会员余额流水表（充值/消费/退款/调整，biz_type+biz_id 唯一用于幂等）
CREATE TABLE IF NOT EXISTS `customer_balance_log` (
    `log_id`         BIGINT          NOT NULL AUTO_INCREMENT COMMENT '流水ID',
    `customer_id`    BIGINT          NOT NULL                COMMENT '会员ID',
    `change_type`    TINYINT         NOT NULL                COMMENT '变动类型：1充值，2消费扣减，3退款，4人工调整',
    `change_amount`  DECIMAL(10,2)   NOT NULL                COMMENT '变动金额（正数为增加，负数为扣减）',
    `balance_before` DECIMAL(10,2)   NOT NULL                COMMENT '变动前余额',
    `balance_after`  DECIMAL(10,2)   NOT NULL                COMMENT '变动后余额',
    `biz_type`       VARCHAR(32)                              COMMENT '业务类型，如 RECHARGE/ORDER/REFUND/ADJUST',
    `biz_id`         VARCHAR(64)                              COMMENT '业务单号（幂等键）',
    `remark`         VARCHAR(255)                             COMMENT '备注',
    `gmt_create`     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_modified`   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`log_id`),
    KEY `idx_customer` (`customer_id`),
    UNIQUE KEY `uk_biz` (`biz_type`, `biz_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员余额流水表';

-- 会员积分流水表（消费得积分/积分使用/调整，biz_type+biz_id 唯一用于幂等）
CREATE TABLE IF NOT EXISTS `customer_points_log` (
    `log_id`         BIGINT          NOT NULL AUTO_INCREMENT COMMENT '流水ID',
    `customer_id`    BIGINT          NOT NULL                COMMENT '会员ID',
    `change_type`    TINYINT         NOT NULL                COMMENT '变动类型：1消费获得，2积分使用，3人工调整',
    `change_points`  INT             NOT NULL                COMMENT '变动积分（正数为增加，负数为扣减）',
    `points_before`  INT             NOT NULL                COMMENT '变动前积分',
    `points_after`   INT             NOT NULL                COMMENT '变动后积分',
    `biz_type`       VARCHAR(32)                              COMMENT '业务类型，如 CONSUME/USE/ADJUST',
    `biz_id`         VARCHAR(64)                              COMMENT '业务单号（幂等键）',
    `remark`         VARCHAR(255)                             COMMENT '备注',
    `gmt_create`     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_modified`   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`log_id`),
    KEY `idx_customer` (`customer_id`),
    UNIQUE KEY `uk_biz` (`biz_type`, `biz_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员积分流水表';

-- =============================================
-- 存量库升级脚本（customer 表已存在的环境执行）
-- =============================================
-- ALTER TABLE `customer`
--     ADD COLUMN `points` INT NOT NULL DEFAULT 0 COMMENT '积分余额' AFTER `balance`,
--     ADD COLUMN `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0禁用，1正常' AFTER `points`;
