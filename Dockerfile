# ==========================================
# 阶段一：构建阶段 (Maven + JDK 17)
# ==========================================
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /build

# 缓存依赖层并拷贝源代码
COPY pom.xml .
COPY lims-common/pom.xml lims-common/
COPY lims-framework/pom.xml lims-framework/
COPY lims-system/pom.xml lims-system/
COPY lims-contract/pom.xml lims-contract/
COPY lims-entrust/pom.xml lims-entrust/
COPY lims-sampling/pom.xml lims-sampling/
COPY lims-detection/pom.xml lims-detection/
COPY lims-report/pom.xml lims-report/
COPY lims-finance/pom.xml lims-finance/
COPY lims-device/pom.xml lims-device/
COPY lims-file/pom.xml lims-file/
COPY lims-app/pom.xml lims-app/

# 拷贝全量源码
COPY lims-common/src lims-common/src
COPY lims-framework/src lims-framework/src
COPY lims-system/src lims-system/src
COPY lims-contract/src lims-contract/src
COPY lims-entrust/src lims-entrust/src
COPY lims-sampling/src lims-sampling/src
COPY lims-detection/src lims-detection/src
COPY lims-report/src lims-report/src
COPY lims-finance/src lims-finance/src
COPY lims-device/src lims-device/src
COPY lims-file/src lims-file/src
COPY lims-app/src lims-app/src

# 执行构建并跳过单元测试
RUN mvn clean package -DskipTests -Dmaven.test.skip=true

# ==========================================
# 阶段二：轻量级运行镜像 (JRE 17)
# ==========================================
FROM eclipse-temurin:17-jre
WORKDIR /app

# 设置时区
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 从构建镜像拷贝产物
COPY --from=builder /build/lims-app/target/lims-app-1.0.0-SNAPSHOT.jar /app/lims-app.jar

# 创建日志目录与卷挂载点
RUN mkdir -p /app/logs
VOLUME /app/logs

EXPOSE 8080

ENTRYPOINT ["java", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-Dfile.encoding=UTF-8", \
  "-Duser.timezone=Asia/Shanghai", \
  "-jar", \
  "/app/lims-app.jar", \
  "--spring.profiles.active=staging"]
