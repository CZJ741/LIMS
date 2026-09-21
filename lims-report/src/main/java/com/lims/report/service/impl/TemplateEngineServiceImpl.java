package com.lims.report.service.impl;

import com.lims.common.exception.BizException;
import com.lims.report.service.TemplateEngineService;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;

@Slf4j
@Service
public class TemplateEngineServiceImpl implements TemplateEngineService {

    private final Configuration freemarkerConfig;

    public TemplateEngineServiceImpl() {
        this.freemarkerConfig = new Configuration(Configuration.VERSION_2_3_32);
        this.freemarkerConfig.setDefaultEncoding("UTF-8");
        this.freemarkerConfig.setNumberFormat("0.##");
        this.freemarkerConfig.setDateFormat("yyyy-MM-dd");
        this.freemarkerConfig.setDateTimeFormat("yyyy-MM-dd HH:mm:ss");
    }

    @Override
    public String renderHtml(String templateContent, Map<String, Object> model) {
        try {
            Template template = new Template("dynamic_report", new StringReader(templateContent), freemarkerConfig);
            StringWriter writer = new StringWriter();
            template.process(model, writer);
            return writer.toString();
        } catch (Exception e) {
            log.error("FreeMarker 模板渲染失败", e);
            throw new BizException("报告模板渲染异常: " + e.getMessage());
        }
    }

    @Override
    public void convertHtmlToPdf(String htmlContent, OutputStream outputStream) {
        try {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(htmlContent, null);
            builder.toStream(outputStream);
            builder.run();
        } catch (Exception e) {
            log.error("HTML 转换 PDF 失败", e);
            throw new BizException("生成 PDF 报告失败: " + e.getMessage());
        }
    }

    @Override
    public byte[] generateReportPdf(String templateContent, Map<String, Object> model) {
        String html = renderHtml(templateContent, model);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        convertHtmlToPdf(html, bos);
        return bos.toByteArray();
    }
}
