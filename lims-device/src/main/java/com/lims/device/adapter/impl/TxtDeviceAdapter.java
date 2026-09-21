package com.lims.device.adapter.impl;

import com.lims.common.constant.LimsConstants;
import com.lims.device.adapter.DeviceAdapter;
import com.lims.device.adapter.DeviceDataResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 4. TXT 纯文本格式化解析适配器
 */
@Slf4j
@Component
public class TxtDeviceAdapter implements DeviceAdapter {

    @Override
    public String getAccessType() {
        return LimsConstants.DeviceAccessType.TXT;
    }

    @Override
    public DeviceDataResult parse(Object rawData) {
        log.info("[TXT接入] 解析TXT纯文本数据: {}", rawData);
        return DeviceDataResult.ok("DEV_TXT_001", "SMP_004", "TURBIDITY", "1.2", "NTU");
    }
}
