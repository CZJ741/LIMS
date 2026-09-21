package com.lims.device.adapter;

/**
 * 设备数据接入适配器统一接口
 */
public interface DeviceAdapter {

    /**
     * 获取支持的接入协议类型（参见 LimsConstants.DeviceAccessType）
     */
    String getAccessType();

    /**
     * 解析/采集设备原始输入数据
     *
     * @param rawData 原始输入（可以是字节流、报文字符串、文件二进制等）
     * @return 解析后的设备数据结构
     */
    DeviceDataResult parse(Object rawData);
}
