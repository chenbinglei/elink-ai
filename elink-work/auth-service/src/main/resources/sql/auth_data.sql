-- ============================================================================
-- Elink-AI Auth Service OAuth2 初始数据脚本
-- 数据库: sunos-system
-- 创建日期: 2026-06-09
-- 说明: 本脚本仅包含OAuth2客户端注册数据
--       每个客户端的client_secret为对应client_id的BCrypt加密值
-- ============================================================================

SET NAMES utf8mb4;

-- ============================================================================
-- OAuth2客户端注册数据
-- 说明: Spring Authorization Server客户端配置
--       每个前端平台对应一个client, 用于validateClient()校验
--       client_secret = BCrypt(client_id)
-- ============================================================================

-- derms-client: 运营管理平台客户端 (derms, port:9001)
-- secret明文: derms-client
INSERT INTO `oauth2_registered_client` (
    `id`, `client_id`, `client_id_issued_at`, `client_secret`, `client_secret_expires_at`,
    `client_name`, `client_authentication_methods`, `authorization_grant_types`,
    `redirect_uris`, `post_logout_redirect_uris`, `scopes`,
    `client_settings`, `token_settings`
) VALUES (
    'derms-client-1', 'derms-client', NOW(),
    '$2a$10$fDbtPvwrTCaLF5sUwpFUou4do9kyVJPIdWCGJPhNIzNxlDLH/um5i',
    NULL,
    '运营管理平台客户端',
    'client_secret_basic,client_secret_post',
    'authorization_code,refresh_token,client_credentials,password',
    'http://localhost:9001',
    'http://localhost:9001',
    'read,write',
    '{"@class":"java.util.Collections$UnmodifiableMap","settings.client.require-proof-key":false,"settings.client.require-authorization-consent":true}',
    '{"@class":"java.util.Collections$UnmodifiableMap","settings.token.reuse-refresh-tokens":true,"settings.token.id-token-signature-algorithm":["org.springframework.security.oauth2.jose.jws.SignatureAlgorithm","RS256"],"settings.token.access-token-time-to-live":["java.time.Duration",259200.000000000],"settings.token.access-token-format":{"@class":"org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat","value":"self-contained"},"settings.token.refresh-token-time-to-live":["java.time.Duration",2592000.000000000]}'
);

-- enlink-client: Elink综合平台客户端
-- secret明文: enlink-client
INSERT INTO `oauth2_registered_client` (
    `id`, `client_id`, `client_id_issued_at`, `client_secret`, `client_secret_expires_at`,
    `client_name`, `client_authentication_methods`, `authorization_grant_types`,
    `redirect_uris`, `post_logout_redirect_uris`, `scopes`,
    `client_settings`, `token_settings`
) VALUES (
    'enlink-client-1', 'enlink-client', NOW(),
    '$2a$10$yhTtcwxFRSDcy0WkcV0RD.zJD2ssfkItJSyAfiIf05uzqTSq4S6cO',
    NULL,
    'Elink综合平台客户端',
    'client_secret_basic,client_secret_post',
    'authorization_code,refresh_token,client_credentials,password',
    'http://localhost:9000',
    'http://localhost:9000',
    'read,write,trust',
    '{"@class":"java.util.Collections$UnmodifiableMap","settings.client.require-proof-key":false,"settings.client.require-authorization-consent":true}',
    '{"@class":"java.util.Collections$UnmodifiableMap","settings.token.reuse-refresh-tokens":true,"settings.token.id-token-signature-algorithm":["org.springframework.security.oauth2.jose.jws.SignatureAlgorithm","RS256"],"settings.token.access-token-time-to-live":["java.time.Duration",259200.000000000],"settings.token.access-token-format":{"@class":"org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat","value":"self-contained"},"settings.token.refresh-token-time-to-live":["java.time.Duration",2592000.000000000]}'
);

-- evcos-client: EV充电运营平台客户端
-- secret明文: evcos-client
INSERT INTO `oauth2_registered_client` (
    `id`, `client_id`, `client_id_issued_at`, `client_secret`, `client_secret_expires_at`,
    `client_name`, `client_authentication_methods`, `authorization_grant_types`,
    `redirect_uris`, `post_logout_redirect_uris`, `scopes`,
    `client_settings`, `token_settings`
) VALUES (
    'evcos-client-1', 'evcos-client', NOW(),
    '$2a$10$KA85HTk3sIYw4cFl2tq4W.8.hI6CyN7/kz5y9MgPBYxFDRUw.BEei',
    NULL,
    'EV充电运营平台客户端',
    'client_secret_basic,client_secret_post',
    'authorization_code,refresh_token,client_credentials,password',
    'http://localhost:9003',
    'http://localhost:9003',
    'read,write',
    '{"@class":"java.util.Collections$UnmodifiableMap","settings.client.require-proof-key":false,"settings.client.require-authorization-consent":true}',
    '{"@class":"java.util.Collections$UnmodifiableMap","settings.token.reuse-refresh-tokens":true,"settings.token.id-token-signature-algorithm":["org.springframework.security.oauth2.jose.jws.SignatureAlgorithm","RS256"],"settings.token.access-token-time-to-live":["java.time.Duration",259200.000000000],"settings.token.access-token-format":{"@class":"org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat","value":"self-contained"},"settings.token.refresh-token-time-to-live":["java.time.Duration",2592000.000000000]}'
);

-- microgrid-client: 微电网管理平台客户端
-- secret明文: microgrid-client
INSERT INTO `oauth2_registered_client` (
    `id`, `client_id`, `client_id_issued_at`, `client_secret`, `client_secret_expires_at`,
    `client_name`, `client_authentication_methods`, `authorization_grant_types`,
    `redirect_uris`, `post_logout_redirect_uris`, `scopes`,
    `client_settings`, `token_settings`
) VALUES (
    'microgrid-client-1', 'microgrid-client', NOW(),
    '$2a$10$/49Jlx9hVmgdJJ5zbsJ4HuJ9Dwmo4OkQPoMIXISO4xasMZSnAtrdS',
    NULL,
    '微电网管理平台客户端',
    'client_secret_basic,client_secret_post',
    'authorization_code,refresh_token,client_credentials,password',
    'http://localhost:9004',
    'http://localhost:9004',
    'read,write',
    '{"@class":"java.util.Collections$UnmodifiableMap","settings.client.require-proof-key":false,"settings.client.require-authorization-consent":true}',
    '{"@class":"java.util.Collections$UnmodifiableMap","settings.token.reuse-refresh-tokens":true,"settings.token.id-token-signature-algorithm":["org.springframework.security.oauth2.jose.jws.SignatureAlgorithm","RS256"],"settings.token.access-token-time-to-live":["java.time.Duration",259200.000000000],"settings.token.access-token-format":{"@class":"org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat","value":"self-contained"},"settings.token.refresh-token-time-to-live":["java.time.Duration",2592000.000000000]}'
);

-- sunos-client: 物联网管理平台Web客户端 (linkos, port:9000)
-- secret明文: sunos-client
INSERT INTO `oauth2_registered_client` (
    `id`, `client_id`, `client_id_issued_at`, `client_secret`, `client_secret_expires_at`,
    `client_name`, `client_authentication_methods`, `authorization_grant_types`,
    `redirect_uris`, `post_logout_redirect_uris`, `scopes`,
    `client_settings`, `token_settings`
) VALUES (
    'sunos-client-1', 'sunos-client', NOW(),
    '$2a$10$Wn8oA8zGKF5krvEStx9d.eKdZfiCB.m/GpByN/LqSZqeTw5nJPKH6',
    NULL,
    '物联网管理平台Web客户端',
    'client_secret_basic,client_secret_post',
    'authorization_code,refresh_token,client_credentials,password',
    'http://localhost:9000',
    'http://localhost:9000',
    'read,write,trust',
    '{"@class":"java.util.Collections$UnmodifiableMap","settings.client.require-proof-key":false,"settings.client.require-authorization-consent":true}',
    '{"@class":"java.util.Collections$UnmodifiableMap","settings.token.reuse-refresh-tokens":true,"settings.token.id-token-signature-algorithm":["org.springframework.security.oauth2.jose.jws.SignatureAlgorithm","RS256"],"settings.token.access-token-time-to-live":["java.time.Duration",259200.000000000],"settings.token.access-token-format":{"@class":"org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat","value":"self-contained"},"settings.token.refresh-token-time-to-live":["java.time.Duration",2592000.000000000]}'
);

-- tycvs-client: 可视化组态平台客户端 (tycvs, port:9002)
-- secret明文: tycvs-client
INSERT INTO `oauth2_registered_client` (
    `id`, `client_id`, `client_id_issued_at`, `client_secret`, `client_secret_expires_at`,
    `client_name`, `client_authentication_methods`, `authorization_grant_types`,
    `redirect_uris`, `post_logout_redirect_uris`, `scopes`,
    `client_settings`, `token_settings`
) VALUES (
    'tycvs-client-1', 'tycvs-client', NOW(),
    '$2a$10$8.mgSQLKJXym8yo4llhMjOxQxQJCIHHM0sdZpm12Ol0AcP/VQ.7UK',
    NULL,
    '可视化组态平台客户端',
    'client_secret_basic,client_secret_post',
    'authorization_code,refresh_token,client_credentials,password',
    'http://localhost:9002',
    'http://localhost:9002',
    'read,write',
    '{"@class":"java.util.Collections$UnmodifiableMap","settings.client.require-proof-key":false,"settings.client.require-authorization-consent":true}',
    '{"@class":"java.util.Collections$UnmodifiableMap","settings.token.reuse-refresh-tokens":true,"settings.token.id-token-signature-algorithm":["org.springframework.security.oauth2.jose.jws.SignatureAlgorithm","RS256"],"settings.token.access-token-time-to-live":["java.time.Duration",259200.000000000],"settings.token.access-token-format":{"@class":"org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat","value":"self-contained"},"settings.token.refresh-token-time-to-live":["java.time.Duration",2592000.000000000]}'
);
