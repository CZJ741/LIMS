package com.lims.device.adapter.impl;

import com.lims.common.constant.LimsConstants;
import com.lims.device.adapter.DeviceAdapter;
import com.lims.device.adapter.DeviceDataResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 1. API/工作站软件直连适配器
 */
@Slf4j
@Component
public class ApiWorkstationAdapter implements DeviceAdapter {

    @Override
    public String getAccessType() {
        return LimsConstants.DeviceAccessType.API_WORKSTATION;
    }

    @Override
    public DeviceDataResult parse(Object rawData) {
        log.info("[API/工作站接入] 接收到原始数据: {}", rawData);
        // 生产实现将解析工作站 HTTP/Socket 报文
        return DeviceDataResult.ok("DEV_WS_001", "SMP_001", "PH", "7.35", "");
    }
}
