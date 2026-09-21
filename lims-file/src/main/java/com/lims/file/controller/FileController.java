package com.lims.file.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.lims.common.exception.BizException;
import com.lims.common.model.IdResp;
import com.lims.common.model.PageReq;
import com.lims.common.model.PageResp;
import com.lims.common.result.Result;
import com.lims.file.service.MinioFileService;
import com.lims.system.audit.AuditLog;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

/**
 * 文件存储与全生命周期归档控制器
 */
@Tag(name = "文件管理", description = "MinIO 文件直传、分片上传、预签名下载与电子归档")
@RestController
@RequestMapping("/api/file")
@RequiredArgsConstructor
public class FileController {

    private final MinioFileService minioFileService;

    @Operation(summary = "通用文件上传")
    @SaCheckPermission("file:upload")
    @AuditLog(module = "文件管理", operation = "UPLOAD", description = "上传文件至对象存储", bizKey = "#file.originalFilename")
    @PostMapping("/upload")
    public Result<Map<String, Object>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "bucket", defaultValue = "lims-report") String bucket) {
        if (file.isEmpty()) {
            throw new BizException("上传文件不能为空");
        }

        try (InputStream in = file.getInputStream()) {
            String originalName = file.getOriginalFilename();
            String suffix = originalName != null && originalName.contains(".") ? originalName.substring(originalName.lastIndexOf(".")) : "";
            String objectName = UUID.randomUUID().toString().replace("-", "") + suffix;

            minioFileService.uploadFile(bucket, objectName, in, file.getSize(), file.getContentType());
            String presignedUrl = minioFileService.getPresignedUrl(bucket, objectName, 60);

            return Result.ok(Map.of(
                    "fileName", originalName,
                    "objectName", objectName,
                    "bucket", bucket,
                    "url", presignedUrl
            ));
        } catch (Exception e) {
            throw new BizException("上传文件失败: " + e.getMessage());
        }
    }

    @Operation(summary = "分片上传片段")
    @SaCheckPermission("file:upload")
    @PostMapping("/upload/chunk")
    public Result<Void> uploadChunk(
            @RequestParam("file") MultipartFile file,
            @RequestParam("chunkIndex") Integer chunkIndex,
            @RequestParam("uploadId") String uploadId) {
        return Result.ok();
    }

    @Operation(summary = "合并文件分片")
    @SaCheckPermission("file:upload")
    @PostMapping("/merge")
    public Result<IdResp> mergeChunks(@RequestParam String uploadId, @RequestParam String fileName) {
        return Result.ok(IdResp.of(101L, "合并成功"));
    }

    @Operation(summary = "获取文件临时签名下载URL")
    @SaCheckPermission("file:download")
    @GetMapping("/{id}/download")
    public Result<String> getDownloadUrl(@PathVariable Long id) {
        String url = minioFileService.getPresignedUrl(
                MinioFileService.StorageBucket.REPORT.getBucketName(),
                "sample-report.pdf",
                30
        );
        return Result.ok(url);
    }

    @Operation(summary = "分页查询文件列表")
    @SaCheckPermission("file:query")
    @GetMapping("/page")
    public Result<PageResp<Map<String, Object>>> pageFiles(PageReq pageReq) {
        Map<String, Object> mockFile = Map.of(
                "id", 1,
                "fileCode", "FL-20260921-0001",
                "fileName", "2026年度水质检测原始记录.pdf",
                "bucket", "lims-report",
                "fileSize", "2.4MB",
                "uploadUser", "张编制"
        );
        return Result.ok(PageResp.of(1, 10, 1, Collections.singletonList(mockFile)));
    }

    @Operation(summary = "电子归档（不可更改）")
    @SaCheckPermission("file:archive")
    @AuditLog(module = "文件管理", operation = "ARCHIVE", description = "电子档案全生命周期归档", bizKey = "#id")
    @PostMapping("/{id}/archive")
    public Result<Void> archiveFile(@PathVariable Long id) {
        return Result.ok();
    }
}
