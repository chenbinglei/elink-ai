-- ============================================================================
-- Elink-AI Auth Service OAuth2 表结构脚本
-- 数据库: sunos-system (与system-service共享)
-- 字符集: utf8mb4
-- 排序规则: utf8mb4_general_ci
-- 创建日期: 2026-06-09
-- 说明: 本脚本仅包含OAuth2 Token登录相关的3张标准表
--       oauth2_registered_client  - 客户端注册配置
--       oauth2_authorization      - 授权码与令牌存储
--       oauth2_authorization_consent - 用户授权同意记录
-- ============================================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ============================================================================
-- 1. OAuth2已注册客户端表 oauth2_registered_client
-- 用途: Spring Authorization Server标准表, 存储OAuth2客户端配置
-- 替代旧版oauth_client_details表
-- 注意: client_secret必须使用BCrypt加密存储
-- ============================================================================
DROP TABLE IF EXISTS `oauth2_registered_client`;
CREATE TABLE `oauth2_registered_client` (
    `id`                            VARCHAR(100)    NOT NULL                    COMMENT '主键',
    `client_id`                     VARCHAR(100)    NOT NULL                    COMMENT '客户端ID',
    `client_id_issued_at`           TIMESTAMP       DEFAULT CURRENT_TIMESTAMP   COMMENT '客户端ID签发时间',
    `client_secret`                 VARCHAR(200)    DEFAULT NULL                COMMENT '客户端密钥(BCrypt加密)',
    `client_secret_expires_at`      TIMESTAMP       DEFAULT NULL                COMMENT '客户端密钥过期时间',
    `client_name`                   VARCHAR(200)    NOT NULL                    COMMENT '客户端名称',
    `client_authentication_methods` VARCHAR(1000)   NOT NULL                    COMMENT '客户端认证方法',
    `authorization_grant_types`     VARCHAR(1000)   NOT NULL                    COMMENT '授权类型',
    `redirect_uris`                 VARCHAR(1000)   DEFAULT NULL                COMMENT '重定向URI',
    `post_logout_redirect_uris`     VARCHAR(1000)   DEFAULT NULL                COMMENT '登出后重定向URI',
    `scopes`                        VARCHAR(1000)   NOT NULL                    COMMENT '授权范围',
    `client_settings`               VARCHAR(2000)   NOT NULL                    COMMENT '客户端设置(JSON)',
    `token_settings`                VARCHAR(2000)   NOT NULL                    COMMENT 'Token设置(JSON)',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_client_id` (`client_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='OAuth2已注册客户端表';

-- ============================================================================
-- 2. OAuth2授权信息表 oauth2_authorization
-- 用途: Spring Authorization Server标准表, 存储授权码和令牌信息
-- ============================================================================
DROP TABLE IF EXISTS `oauth2_authorization`;
CREATE TABLE `oauth2_authorization` (
    `id`                            VARCHAR(100)    NOT NULL                    COMMENT '主键',
    `registered_client_id`          VARCHAR(100)    NOT NULL                    COMMENT '关联客户端ID',
    `principal_name`                VARCHAR(200)    NOT NULL                    COMMENT '主体名称',
    `authorization_grant_type`      VARCHAR(100)    NOT NULL                    COMMENT '授权类型',
    `authorized_scopes`             VARCHAR(1000)   DEFAULT NULL                COMMENT '授权范围',
    `attributes`                    TEXT            DEFAULT NULL                COMMENT '属性(JSON)',
    `state`                         VARCHAR(500)    DEFAULT NULL                COMMENT '状态',
    `authorization_code_value`      TEXT            DEFAULT NULL                COMMENT '授权码值',
    `authorization_code_issued_at`  TIMESTAMP       DEFAULT NULL                COMMENT '授权码签发时间',
    `authorization_code_expires_at` TIMESTAMP       DEFAULT NULL                COMMENT '授权码过期时间',
    `authorization_code_metadata`   TEXT            DEFAULT NULL                COMMENT '授权码元数据(JSON)',
    `access_token_value`            TEXT            DEFAULT NULL                COMMENT '访问令牌值',
    `access_token_issued_at`        TIMESTAMP       DEFAULT NULL                COMMENT '访问令牌签发时间',
    `access_token_expires_at`       TIMESTAMP       DEFAULT NULL                COMMENT '访问令牌过期时间',
    `access_token_metadata`         TEXT            DEFAULT NULL                COMMENT '访问令牌元数据(JSON)',
    `access_token_type`             VARCHAR(100)    DEFAULT NULL                COMMENT '访问令牌类型',
    `access_token_scopes`           VARCHAR(1000)   DEFAULT NULL                COMMENT '访问令牌范围',
    `oidc_id_token_value`           TEXT            DEFAULT NULL                COMMENT 'OIDC ID令牌值',
    `oidc_id_token_issued_at`       TIMESTAMP       DEFAULT NULL                COMMENT 'OIDC ID令牌签发时间',
    `oidc_id_token_expires_at`      TIMESTAMP       DEFAULT NULL                COMMENT 'OIDC ID令牌过期时间',
    `oidc_id_token_metadata`        TEXT            DEFAULT NULL                COMMENT 'OIDC ID令牌元数据(JSON)',
    `refresh_token_value`           TEXT            DEFAULT NULL                COMMENT '刷新令牌值',
    `refresh_token_issued_at`       TIMESTAMP       DEFAULT NULL                COMMENT '刷新令牌签发时间',
    `refresh_token_expires_at`      TIMESTAMP       DEFAULT NULL                COMMENT '刷新令牌过期时间',
    `refresh_token_metadata`        TEXT            DEFAULT NULL                COMMENT '刷新令牌元数据(JSON)',
    `user_code_value`               TEXT            DEFAULT NULL                COMMENT '用户码值',
    `user_code_issued_at`           TIMESTAMP       DEFAULT NULL                COMMENT '用户码签发时间',
    `user_code_expires_at`          TIMESTAMP       DEFAULT NULL                COMMENT '用户码过期时间',
    `user_code_metadata`            TEXT            DEFAULT NULL                COMMENT '用户码元数据(JSON)',
    `device_code_value`             TEXT            DEFAULT NULL                COMMENT '设备码值',
    `device_code_issued_at`         TIMESTAMP       DEFAULT NULL                COMMENT '设备码签发时间',
    `device_code_expires_at`        TIMESTAMP       DEFAULT NULL                COMMENT '设备码过期时间',
    `device_code_metadata`          TEXT            DEFAULT NULL                COMMENT '设备码元数据(JSON)',
    PRIMARY KEY (`id`),
    KEY `idx_registered_client_id` (`registered_client_id`),
    KEY `idx_principal_name` (`principal_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='OAuth2授权信息表';

-- ============================================================================
-- 3. OAuth2授权同意表 oauth2_authorization_consent
-- 用途: Spring Authorization Server标准表, 存储用户授权同意信息
-- ============================================================================
DROP TABLE IF EXISTS `oauth2_authorization_consent`;
CREATE TABLE `oauth2_authorization_consent` (
    `registered_client_id`          VARCHAR(100)    NOT NULL                    COMMENT '关联客户端ID',
    `principal_name`                VARCHAR(200)    NOT NULL                    COMMENT '主体名称',
    `authorities`                   VARCHAR(1000)   NOT NULL                    COMMENT '授权权限(JSON)',
    PRIMARY KEY (`registered_client_id`, `principal_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='OAuth2授权同意表';

SET FOREIGN_KEY_CHECKS = 1;
