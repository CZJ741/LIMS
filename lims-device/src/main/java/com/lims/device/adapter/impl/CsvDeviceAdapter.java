package com.lims.device.adapter.impl;

import com.lims.common.constant.LimsConstants;
import com.lims.device.adapter.DeviceAdapter;
import com.lims.device.adapter.DeviceDataResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 2. CSV 文件格式设备数据接入适配器
 */
@Slf4j
@Component
public class CsvDeviceAdapter implements DeviceAdapter {

    @Override
    public String getAccessType() {
        return LimsConstants.DeviceAccessType.CSV;
    }

    @Override
    public DeviceDataResult parse(Object rawData) {
        log.info("[CSV接入] 解析CSV设备导出数据: {}", rawData);
        return DeviceDataResult.ok("DEV_CSV_001", "SMP_002", "COD", "24.5", "mg/L");
    }
}
