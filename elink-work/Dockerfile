FROM docker.1panel.live/library/openjdk:8-jre

# 安装 Java 17 (Eclipse Temurin) + curl + libjemalloc (TDengine JNI 依赖)
RUN apt-get update && \
    apt-get install -y --no-install-recommends curl wget libjemalloc2 && \
    wget -q "https://mirrors.huaweicloud.com/openjdk/17.0.2/openjdk-17.0.2_linux-x64_bin.tar.gz" -O /tmp/jdk17.tar.gz && \
    mkdir -p /usr/local/java/17 && \
    tar xzf /tmp/jdk17.tar.gz -C /usr/local/java/17 --strip-components=1 && \
    rm /tmp/jdk17.tar.gz && \
    rm -rf /var/lib/apt/lists/*

# 设置 Java 17 为默认运行时
ENV JAVA_HOME=/usr/local/java/17
ENV PATH="${JAVA_HOME}/bin:${PATH}"

# Java 17 JPMS 兼容性：旧版 JAXB/OAuth2/FST(Redisson) 库需要反射访问 java.base 内部类
# 注意：必须使用 JDK_JAVA_OPTIONS 而非 JAVA_TOOL_OPTIONS，后者不支持 --add-opens
# FST 序列化库深度反射访问 java.base 多个子包，需全面开放
ENV JDK_JAVA_OPTIONS="\
--add-opens java.base/java.lang=ALL-UNNAMED \
--add-opens java.base/java.lang.annotation=ALL-UNNAMED \
--add-opens java.base/java.lang.invoke=ALL-UNNAMED \
--add-opens java.base/java.lang.ref=ALL-UNNAMED \
--add-opens java.base/java.lang.reflect=ALL-UNNAMED \
--add-opens java.base/java.io=ALL-UNNAMED \
--add-opens java.base/java.math=ALL-UNNAMED \
--add-opens java.base/java.net=ALL-UNNAMED \
--add-opens java.base/java.nio=ALL-UNNAMED \
--add-opens java.base/java.nio.channels=ALL-UNNAMED \
--add-opens java.base/java.nio.charset=ALL-UNNAMED \
--add-opens java.base/java.nio.file=ALL-UNNAMED \
--add-opens java.base/java.security=ALL-UNNAMED \
--add-opens java.base/java.security.cert=ALL-UNNAMED \
--add-opens java.base/java.text=ALL-UNNAMED \
--add-opens java.base/java.time=ALL-UNNAMED \
--add-opens java.base/java.util=ALL-UNNAMED \
--add-opens java.base/java.util.concurrent=ALL-UNNAMED \
--add-opens java.base/java.util.concurrent.atomic=ALL-UNNAMED \
--add-opens java.base/java.util.concurrent.locks=ALL-UNNAMED \
--add-opens java.base/java.util.jar=ALL-UNNAMED \
--add-opens java.base/java.util.zip=ALL-UNNAMED \
--add-opens java.base/javax.crypto=ALL-UNNAMED \
--add-opens java.base/javax.net.ssl=ALL-UNNAMED \
--add-opens java.base/sun.nio.ch=ALL-UNNAMED \
--add-opens java.base/sun.nio.cs=ALL-UNNAMED \
--add-opens java.base/sun.security.action=ALL-UNNAMED \
--add-opens java.base/sun.security.provider=ALL-UNNAMED \
--add-opens java.base/sun.security.util=ALL-UNNAMED \
--add-opens java.base/sun.security.x509=ALL-UNNAMED \
--add-opens java.base/sun.util.calendar=ALL-UNNAMED \
--add-opens java.base/sun.util.locale=ALL-UNNAMED"

COPY docker-entrypoint.sh /usr/local/bin/docker-entrypoint.sh
RUN chmod +x /usr/local/bin/docker-entrypoint.sh

WORKDIR /app
ENTRYPOINT ["docker-entrypoint.sh"]
CMD ["java", "-jar", "app.jar"]
