package com.lims.report.service;

import java.io.OutputStream;
import java.util.Map;

/**
 * 报告模板引擎与 HTML-PDF 生成服务
 */
public interface TemplateEngineService {

    /**
     * 根据 FreeMarker 模板与数据渲染生成合规 HTML
     *
     * @param templateContent FreeMarker 模板文本
     * @param model 数据上下文
     * @return 渲染后的标准 HTML 字符串
     */
    String renderHtml(String templateContent, Map<String, Object> model);

    /**
     * 将 HTML 内容转换为 PDF 并输出到流中
     *
     * @param htmlContent HTML 文本
     * @param outputStream 输出流
     */
    void convertHtmlToPdf(String htmlContent, OutputStream outputStream);

    /**
     * 一键生成 PDF 字节数据
     *
     * @param templateContent FreeMarker 模板文本
     * @param model 数据上下文
     * @return PDF 二进制数据
     */
    byte[] generateReportPdf(String templateContent, Map<String, Object> model);
}
