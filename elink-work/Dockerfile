FROM openjdk:8-jre-slim

# 锁定基础镜像版本：openjdk 8u422-b05-jre-slim
# 版本信息可通过 docker inspect elink-base | grep -i version 获取
RUN apt-get update && \
    apt-get install -y --no-install-recommends curl libjemalloc2 && \
    rm -rf /var/lib/apt/lists/*

# 添加非root用户运行应用
RUN groupadd -r appuser && useradd -r -g appuser -d /app -s /sbin/nologin appuser

COPY docker-entrypoint.sh /usr/local/bin/docker-entrypoint.sh
RUN chmod +x /usr/local/bin/docker-entrypoint.sh

WORKDIR /app
RUN mkdir -p /app/logs && chown -R appuser:appuser /app
USER appuser
ENTRYPOINT ["docker-entrypoint.sh"]
CMD ["java", "-jar", "app.jar"]
