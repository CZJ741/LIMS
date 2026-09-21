package com.lims.device.factory;

import com.lims.common.exception.BizException;
import com.lims.device.adapter.DeviceAdapter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 设备适配器工厂
 */
@Component
public class DeviceAdapterFactory {

    private final Map<String, DeviceAdapter> adapterMap = new ConcurrentHashMap<>();

    public DeviceAdapterFactory(List<DeviceAdapter> adapters) {
        for (DeviceAdapter adapter : adapters) {
            adapterMap.put(adapter.getAccessType(), adapter);
        }
    }

    /**
     * 根据设备接入协议类型获取对应适配器
     */
    public DeviceAdapter getAdapter(String accessType) {
        DeviceAdapter adapter = adapterMap.get(accessType);
        if (adapter == null) {
            throw new BizException("不支持的设备接入方式: " + accessType);
        }
        return adapter;
    }
}
