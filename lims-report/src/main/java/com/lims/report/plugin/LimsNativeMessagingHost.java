package com.lims.report.plugin;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

/**
 * Chrome Native Messaging Host 进程监听器
 * 遵循 Chrome 规范：输入输出均带有 32-bit (4字节) 整数前缀表示消息长度
 */
@Slf4j
public class LimsNativeMessagingHost {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static void main(String[] args) {
        log.info("LIMS Native Messaging Host 服务启动...");
        try {
            InputStream in = System.in;
            OutputStream out = System.out;

            byte[] lengthBytes = new byte[4];
            while (in.read(lengthBytes) == 4) {
                int length = ByteBuffer.wrap(lengthBytes).order(ByteOrder.LITTLE_ENDIAN).getInt();
                if (length <= 0) break;

                byte[] messageBytes = new byte[length];
                int read = in.read(messageBytes);
                if (read != length) break;

                String jsonStr = new String(messageBytes, StandardCharsets.UTF_8);
                JsonNode request = MAPPER.readTree(jsonStr);

                // 响应客户端
                String responseJson = String.format("{\"status\":\"OK\",\"echoAction\":\"%s\",\"msg\":\"本地服务处理成功\"}",
                        request.has("action") ? request.get("action").asText() : "UNKNOWN");

                byte[] resBytes = responseJson.getBytes(StandardCharsets.UTF_8);
                byte[] resLength = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(resBytes.length).array();

                out.write(resLength);
                out.write(resBytes);
                out.flush();
            }
        } catch (Exception e) {
            log.error("Native Host 运行异常", e);
        }
    }
}
