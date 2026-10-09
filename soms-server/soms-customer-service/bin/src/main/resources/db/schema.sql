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
    `gmt_create`     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_modified`   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`     TINYINT         NOT NULL DEFAULT 0      COMMENT '逻辑删除：0未删除，1已删除',
    PRIMARY KEY (`customer_id`),
    UNIQUE KEY `uk_mobile` (`mobile`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员客户信息表';
