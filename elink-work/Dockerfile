FROM openjdk:8-jre

# 安装依赖: curl + libjemalloc (TDengine JNI 依赖)
RUN apt-get update && \
    apt-get install -y --no-install-recommends curl libjemalloc2 && \
    rm -rf /var/lib/apt/lists/*

COPY docker-entrypoint.sh /usr/local/bin/docker-entrypoint.sh
RUN chmod +x /usr/local/bin/docker-entrypoint.sh

WORKDIR /app
ENTRYPOINT ["docker-entrypoint.sh"]
CMD ["java", "-jar", "app.jar"]
