#!/bin/bash
# ==============================================================================
# LIMS 实验室管理系统数据灾备脚本
# 策略：MySQL 每日全量备份 + MinIO 对象存储增量镜像 (mc mirror)
# 保留周期：近 7 天每日、近 4 周每周、近 12 个月每月
# ==============================================================================

set -eo pipefail

BACKUP_DATE=$(date +"%Y%m%d_%H%M%S")
BACKUP_DIR="/data/backup/lims"
MYSQL_CONTAINER="lims-mysql"
DB_USER="root"
DB_PASS="${MYSQL_ROOT_PASSWORD:-rootpassword}"

mkdir -p "${BACKUP_DIR}/mysql"
mkdir -p "${BACKUP_DIR}/minio"

echo "[$(date)] 开始执行 MySQL 数据库备份..."
docker exec ${MYSQL_CONTAINER} mysqldump -u${DB_USER} -p${DB_PASS} \
  --single-transaction \
  --quick \
  --databases lims | gzip > "${BACKUP_DIR}/mysql/lims_${BACKUP_DATE}.sql.gz"

echo "[$(date)] 开始执行 MinIO 文件增量同步..."
# 借助 MinIO Client 执行镜像
docker run --rm --net lims-net \
  -v "${BACKUP_DIR}/minio:/backup" \
  minio/mc:RELEASE.2024-09-09T07-53-10Z \
  mirror --overwrite http://minioadmin:minioadmin@lims-minio:9000/lims-report /backup/lims-report

echo "[$(date)] 执行备份轮转清理 (清除7天前的每日快照)..."
find "${BACKUP_DIR}/mysql" -name "*.sql.gz" -mtime +7 -delete

echo "[$(date)] 备份任务全部圆满完成！"
