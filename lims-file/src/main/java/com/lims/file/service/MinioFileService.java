package com.lims.file.service;

import com.lims.common.exception.BizException;
import com.lims.file.config.MinioConfig;
import io.minio.BucketExistsArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

/**
 * MinIO 文件存储与全生命周期服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MinioFileService {

    private final MinioClient minioClient;
    private final MinioConfig minioConfig;

    /**
     * 业务分桶规范
     */
    public enum StorageBucket {
        CONTRACT("lims-contract"),
        SAMPLE("lims-sample"),
        DETECTION("lims-detection"),
        REPORT("lims-report"),
        ARCHIVE("lims-archive");

        private final String bucketName;

        StorageBucket(String bucketName) {
            this.bucketName = bucketName;
        }

        public String getBucketName() {
            return bucketName;
        }
    }

    /**
     * 确保存储桶存在
     */
    public void ensureBucketExists(String bucketName) {
        try {
            boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!exists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                log.info("成功创建 MinIO 存储桶: {}", bucketName);
            }
        } catch (Exception e) {
            log.error("检查/创建 MinIO Bucket 失败: {}", bucketName, e);
            throw new BizException("初始化存储桶失败: " + e.getMessage());
        }
    }

    /**
     * 上传流文件到指定业务桶
     */
    public String uploadFile(String bucketName, String objectName, InputStream inputStream, long size, String contentType) {
        try {
            ensureBucketExists(bucketName);
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(inputStream, size, -1)
                            .contentType(contentType)
                            .build()
            );
            log.info("文件上传成功: bucket={}, objectName={}", bucketName, objectName);
            return objectName;
        } catch (Exception e) {
            log.error("MinIO 上传文件失败", e);
            throw new BizException("文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 获取带签名的限时下载/预览 URL
     */
    public String getPresignedUrl(String bucketName, String objectName, int durationMinutes) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .object(objectName)
                            .expiry(durationMinutes, TimeUnit.MINUTES)
                            .build()
            );
        } catch (Exception e) {
            log.error("生成预签名 URL 失败: bucket={}, object={}", bucketName, objectName, e);
            throw new BizException("获取下载凭证失败: " + e.getMessage());
        }
    }
}
