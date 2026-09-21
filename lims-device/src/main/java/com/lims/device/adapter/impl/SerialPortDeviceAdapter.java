package com.lims.device.adapter.impl;

import com.lims.common.constant.LimsConstants;
import com.lims.device.adapter.DeviceAdapter;
import com.lims.device.adapter.DeviceDataResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 5. RS232/RS485 串口直连适配器
 */
@Slf4j
@Component
public class SerialPortDeviceAdapter implements DeviceAdapter {

    @Override
    public String getAccessType() {
        return LimsConstants.DeviceAccessType.SERIAL_PORT;
    }

    @Override
    public DeviceDataResult parse(Object rawData) {
        log.info("[串口接入] 解析串口字节数据帧: {}", rawData);
        return DeviceDataResult.ok("DEV_COM_001", "SMP_005", "BALANCE_WEIGHT", "10.0024", "g");
    }
}
