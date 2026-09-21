// LIMS Chrome 扩展 - Background Service Worker
// 职责：处理 Native Messaging 跨进程管道通信、持久化缓存

const NATIVE_HOST = "com.lims.native.host";
let nativePort = null;

function connectNativeHost() {
  try {
    nativePort = chrome.runtime.connectNative(NATIVE_HOST);
    nativePort.onMessage.addListener((msg) => {
      console.log("[LIMS Plugin] 收到 Native Host 响应:", msg);
      // 广播给当前标签页
      chrome.tabs.query({ active: true, currentWindow: true }, (tabs) => {
        if (tabs[0]?.id) {
          chrome.tabs.sendMessage(tabs[0].id, { type: "NATIVE_RESPONSE", data: msg });
        }
      });
    });

    nativePort.onDisconnect.addListener(() => {
      console.warn("[LIMS Plugin] Native Host 已断开连接:", chrome.runtime.lastError?.message);
      nativePort = null;
    });
  } catch (err) {
    console.error("[LIMS Plugin] 连接 Native Host 失败:", err);
  }
}

// 监听 content.js 派发的事件
chrome.runtime.onMessage.addListener((request, sender, sendResponse) => {
  if (request.type === "SEND_TO_NATIVE") {
    if (!nativePort) {
      connectNativeHost();
    }
    if (nativePort) {
      nativePort.postMessage(request.payload);
      sendResponse({ status: "SENT" });
    } else {
      sendResponse({ status: "ERROR", message: "本地 Native Host 未运行或未注册" });
    }
    return true;
  }

  if (request.type === "CHECK_STATUS") {
    sendResponse({ connected: !!nativePort });
  }
});
