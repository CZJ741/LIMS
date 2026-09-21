package com.lims.device.adapter.impl;

import com.lims.common.constant.LimsConstants;
import com.lims.device.adapter.DeviceAdapter;
import com.lims.device.adapter.DeviceDataResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 3. PDF 图谱与测试报告文本解析适配器
 */
@Slf4j
@Component
public class PdfDeviceAdapter implements DeviceAdapter {

    @Override
    public String getAccessType() {
        return LimsConstants.DeviceAccessType.PDF;
    }

    @Override
    public DeviceDataResult parse(Object rawData) {
        log.info("[PDF接入] 解析PDF图谱报告内容: {}", rawData);
        return DeviceDataResult.ok("DEV_PDF_001", "SMP_003", "HEAVY_METAL_PB", "0.002", "mg/kg");
    }
}
