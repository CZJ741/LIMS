package com.lims.device.adapter;

/**
 * 设备数据采集结果封装
 */
public record DeviceDataResult(
        String deviceCode,
        String sampleCode,
        String itemCode,
        String resultValue,
        String unit,
        long timestamp,
        boolean success,
        String errorMessage
) {
    public static DeviceDataResult ok(String deviceCode, String sampleCode, String itemCode, String resultValue, String unit) {
        return new DeviceDataResult(deviceCode, sampleCode, itemCode, resultValue, unit, System.currentTimeMillis(), true, null);
    }

    public static DeviceDataResult fail(String deviceCode, String errorMessage) {
        return new DeviceDataResult(deviceCode, null, null, null, null, System.currentTimeMillis(), false, errorMessage);
    }
}
