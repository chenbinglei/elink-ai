package com.sunmax.common.security;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * JWT Token 服务 - 签发、验签、过期检查
 *
 * <p>用于方案B阶段二：UUID token 替换为 JWT（RSA 签名）+ Redis 黑名单</p>
 *
 * <p>RSA 密钥对持久化到文件系统，避免重启后旧 token 失效。</p>
 */
@Component
public class JwtTokenService {

    /** Access Token 有效期（秒）- 3 天 */
    private static final long ACCESS_TOKEN_TTL_SECONDS = 3 * 24 * 60 * 60L;

    /** Refresh Token 有效期（秒）- 30 天 */
    private static final long REFRESH_TOKEN_TTL_SECONDS = 30 * 24 * 60 * 60L;

    /** Access Token 类型标识 */
    public static final String TOKEN_TYPE_ACCESS = "access";

    /** Refresh Token 类型标识 */
    public static final String TOKEN_TYPE_REFRESH = "refresh";

    /** JWT 签发者标识（阶段四安全加固） */
    public static final String ISSUER = "elink-ai-auth";

    /** JWT 受众标识（阶段四安全加固） */
    public static final String AUDIENCE = "elink-ai-services";

    /** 时钟偏移容忍度（秒），防止分布式时钟不同步导致误判过期 */
    private static final long CLOCK_SKEW_SECONDS = 60L;

    /** 算法白名单：仅允许 RS256 */
    private static final JWSAlgorithm ALLOWED_ALGORITHM = JWSAlgorithm.RS256;

    private final RSAKey rsaKey;
    private final JWSSigner signer;
    private final JWSVerifier verifier;
    private final MeterRegistry meterRegistry;

    /**
     * 构造函数 - 加载或生成 RSA 密钥对并持久化
     *
     * @param keyDirPath RSA 密钥文件存储目录（默认 /work/elink-ai/elink-work/keys）
     * @param meterRegistry Micrometer 指标注册表（用于 JWT 验签失败率监控）
     */
    public JwtTokenService(@Value("${jwt.rsa.key-dir:/work/elink-ai/elink-work/keys}") String keyDirPath,
                           MeterRegistry meterRegistry) {
        try {
            this.rsaKey = loadOrCreateRsaKey(keyDirPath);
            this.signer = new RSASSASigner(rsaKey.toPrivateKey());
            this.verifier = new RSASSAVerifier(rsaKey.toRSAPublicKey());
            this.meterRegistry = meterRegistry;
        } catch (IOException | JOSEException e) {
            throw new IllegalStateException("初始化 JwtTokenService 失败: " + e.getMessage(), e);
        }
    }

    /** 记录 JWT 验签结果（用于监控失败率） */
    private void recordVerifyResult(boolean success, String reason) {
        Counter.builder("jwt.verify")
                .tag("result", success ? "success" : "failure")
                .tag("reason", success ? "none" : reason)
                .description("JWT 验签结果计数")
                .register(meterRegistry)
                .increment();
    }

    /** 记录 JWT 生成次数 */
    private void recordGenerate(String type) {
        Counter.builder("jwt.generate")
                .tag("type", type)
                .description("JWT 生成次数")
                .register(meterRegistry)
                .increment();
    }

    /**
     * 签发 Access Token
     *
     * @param userAccount 用户账号
     * @param userId      用户ID
     * @param userRole    用户角色（0-平台管理员 1-管理员 2-普通用户）
     * @param tenantId    租户ID
     * @param clientId    客户端ID（平台标识）
     * @param permsVer    权限版本号
     * @return JWT 字符串
     */
    public String generateAccessToken(String userAccount, String userId, Integer userRole,
                                      String tenantId, String clientId, String permsVer) {
        Instant now = Instant.now();
        JWTClaimsSet.Builder claimsBuilder = new JWTClaimsSet.Builder()
                .subject(userAccount)
                .issuer(ISSUER)
                .audience(AUDIENCE)
                .jwtID(java.util.UUID.randomUUID().toString())
                .claim("id", userId)
                .claim("userRole", userRole)
                .claim("tenantId", tenantId)
                .claim("clientId", clientId)
                .claim("roles", mapToRoles(userRole))
                .claim("permsVer", permsVer)
                .claim("type", TOKEN_TYPE_ACCESS)
                .issueTime(Date.from(now))
                .expirationTime(Date.from(now.plusSeconds(ACCESS_TOKEN_TTL_SECONDS)));
        recordGenerate(TOKEN_TYPE_ACCESS);
        return signJwt(claimsBuilder.build());
    }

    /**
     * 签发 Refresh Token
     *
     * @param userAccount 用户账号
     * @param userId      用户ID
     * @param userRole    用户角色（0-平台管理员 1-管理员 2-普通用户）
     * @param tenantId    租户ID
     * @param clientId    客户端ID（平台标识）
     * @return JWT 字符串
     */
    public String generateRefreshToken(String userAccount, String userId, Integer userRole,
                                       String tenantId, String clientId) {
        Instant now = Instant.now();
        JWTClaimsSet.Builder claimsBuilder = new JWTClaimsSet.Builder()
                .subject(userAccount)
                .issuer(ISSUER)
                .audience(AUDIENCE)
                .jwtID(java.util.UUID.randomUUID().toString())
                .claim("id", userId)
                .claim("userRole", userRole)
                .claim("tenantId", tenantId)
                .claim("clientId", clientId)
                .claim("roles", mapToRoles(userRole))
                .claim("type", TOKEN_TYPE_REFRESH)
                .issueTime(Date.from(now))
                .expirationTime(Date.from(now.plusSeconds(REFRESH_TOKEN_TTL_SECONDS)));
        recordGenerate(TOKEN_TYPE_REFRESH);
        return signJwt(claimsBuilder.build());
    }

    /**
     * 验签并返回 claims
     *
     * <p>阶段四安全加固内容：
     * <ul>
     *     <li>算法白名单：仅允许 RS256，防止 alg=none 攻击和算法降级攻击</li>
     *     <li>issuer 校验：防止跨域 token 伪造</li>
     *     <li>audience 校验：确保 token 颁发给本系统使用</li>
     * </ul>
     *
     * @param token JWT 字符串
     * @return JWTClaimsSet
     * @throws JOSEException 验签失败或解析异常
     */
    public JWTClaimsSet parseAndVerify(String token) throws JOSEException {
        try {
            SignedJWT signedJwt = SignedJWT.parse(token);

            // 1. 算法白名单校验（防止 alg=none 攻击和算法降级攻击）
            JWSAlgorithm algorithm = signedJwt.getHeader().getAlgorithm();
            if (!ALLOWED_ALGORITHM.equals(algorithm)) {
                recordVerifyResult(false, "algorithm_mismatch");
                throw new JOSEException("JWT 算法不被允许: " + algorithm + "，仅支持 " + ALLOWED_ALGORITHM);
            }

            // 2. 签名验证
            if (!signedJwt.verify(verifier)) {
                recordVerifyResult(false, "signature_invalid");
                throw new JOSEException("JWT 验签失败: 签名不匹配");
            }

            JWTClaimsSet claims = signedJwt.getJWTClaimsSet();

            // 3. issuer 校验
            String tokenIssuer = claims.getIssuer();
            if (!ISSUER.equals(tokenIssuer)) {
                recordVerifyResult(false, "issuer_mismatch");
                throw new JOSEException("JWT issuer 校验失败: 期望=" + ISSUER + "，实际=" + tokenIssuer);
            }

            // 4. audience 校验
            List<String> audience = claims.getAudience();
            if (audience == null || !audience.contains(AUDIENCE)) {
                recordVerifyResult(false, "audience_mismatch");
                throw new JOSEException("JWT audience 校验失败: 期望包含=" + AUDIENCE);
            }

            recordVerifyResult(true, "none");
            return claims;
        } catch (java.text.ParseException e) {
            recordVerifyResult(false, "parse_error");
            throw new JOSEException("JWT 解析失败: " + e.getMessage(), e);
        }
    }

    /**
     * 检查 token 是否过期
     *
     * <p>阶段四安全加固：增加 CLOCK_SKEW_SECONDS 时钟偏移容忍度，
     * 防止分布式环境下服务端时钟不同步导致 token 在临界点被误判过期。</p>
     *
     * @param claims JWT claims
     * @return true-已过期，false-未过期
     */
    public boolean isExpired(JWTClaimsSet claims) {
        Date expirationTime = claims.getExpirationTime();
        if (expirationTime == null) {
            return true;
        }
        // 允许 CLOCK_SKEW_SECONDS 秒的时钟偏移：token 过期时间在 (now - skew) 之前才算过期
        Date nowMinusSkew = Date.from(Instant.now().minusSeconds(CLOCK_SKEW_SECONDS));
        return expirationTime.before(nowMinusSkew);
    }

    /**
     * 加载或生成 RSA 密钥对，并持久化到文件系统
     *
     * <p>密钥文件路径：
     * <ul>
     *     <li>私钥：{keyDir}/jwt-private.pem（PKCS8 Base64）</li>
     *     <li>公钥：{keyDir}/jwt-public.pem（X.509 Base64）</li>
     * </ul>
     * 文件权限 600（仅 owner 可读写）</p>
     */
    private RSAKey loadOrCreateRsaKey(String keyDirPath) throws IOException {
        Path keyDir = Paths.get(keyDirPath);
        Path privateKeyPath = keyDir.resolve("jwt-private.pem");
        Path publicKeyPath = keyDir.resolve("jwt-public.pem");

        // 目录不存在则创建
        if (!Files.exists(keyDir)) {
            Files.createDirectories(keyDir);
        }

        // 尝试加载已有密钥
        if (Files.exists(privateKeyPath) && Files.exists(publicKeyPath)) {
            RSAPrivateKey privateKey = loadPrivateKey(privateKeyPath);
            RSAPublicKey publicKey = loadPublicKey(publicKeyPath);
            return new RSAKey.Builder(publicKey).privateKey(privateKey).keyID("elink-jwt-key").build();
        }

        // 生成新密钥对
        try {
            java.security.KeyPairGenerator keyPairGenerator = java.security.KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            java.security.KeyPair keyPair = keyPairGenerator.generateKeyPair();

            RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
            RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

            // 持久化到文件
            savePrivateKey(privateKeyPath, privateKey);
            savePublicKey(publicKeyPath, publicKey);

            return new RSAKey.Builder(publicKey).privateKey(privateKey).keyID("elink-jwt-key").build();
        } catch (NoSuchAlgorithmException e) {
            throw new IOException("RSA 密钥对生成失败: " + e.getMessage(), e);
        }
    }

    private RSAPrivateKey loadPrivateKey(Path path) throws IOException {
        try {
            String content = Files.readString(path).replaceAll("-----.*-----", "").replaceAll("\\s", "");
            byte[] keyBytes = Base64.getDecoder().decode(content);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            return (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(spec);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            throw new IOException("加载私钥失败: " + e.getMessage(), e);
        }
    }

    private RSAPublicKey loadPublicKey(Path path) throws IOException {
        try {
            String content = Files.readString(path).replaceAll("-----.*-----", "").replaceAll("\\s", "");
            byte[] keyBytes = Base64.getDecoder().decode(content);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            return (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(spec);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            throw new IOException("加载公钥失败: " + e.getMessage(), e);
        }
    }

    private void savePrivateKey(Path path, RSAPrivateKey privateKey) throws IOException {
        byte[] keyBytes = privateKey.getEncoded();
        String base64 = Base64.getEncoder().encodeToString(keyBytes);
        String pem = "-----BEGIN PRIVATE KEY-----\n" + base64 + "\n-----END PRIVATE KEY-----\n";
        Files.writeString(path, pem);
        // 设置文件权限 600（仅 owner 可读写）
        File file = path.toFile();
        file.setReadable(false, false);
        file.setReadable(true, true);
        file.setWritable(false, false);
        file.setWritable(true, true);
    }

    private void savePublicKey(Path path, RSAPublicKey publicKey) throws IOException {
        byte[] keyBytes = publicKey.getEncoded();
        String base64 = Base64.getEncoder().encodeToString(keyBytes);
        String pem = "-----BEGIN PUBLIC KEY-----\n" + base64 + "\n-----END PUBLIC KEY-----\n";
        Files.writeString(path, pem);
    }

    private String signJwt(JWTClaimsSet claims) {
        try {
            SignedJWT signedJwt = new SignedJWT(new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(rsaKey.getKeyID()).build(), claims);
            signedJwt.sign(signer);
            return signedJwt.serialize();
        } catch (JOSEException e) {
            throw new IllegalStateException("JWT 签名失败: " + e.getMessage(), e);
        }
    }

    /**
     * 根据 userRole 映射为角色列表
     *
     * @param userRole 0-平台管理员 1-管理员 2-普通用户
     * @return 角色列表
     */
    private List<String> mapToRoles(Integer userRole) {
        if (userRole == null) {
            return Collections.singletonList("ROLE_USER");
        }
        switch (userRole) {
            case 0:
                return Collections.singletonList("ROLE_PLATFORM_ADMIN");
            case 1:
                return Collections.singletonList("ROLE_ADMIN");
            case 2:
            default:
                return Collections.singletonList("ROLE_USER");
        }
    }
}
