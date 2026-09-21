// LIMS Chrome 扩展 - Content Script
// 职责：注入前端页面，监听报告生成、外设扫码枪与仪器通讯触发事件

console.log("[LIMS Plugin] Content Script 已就绪并注入页面");

// 注入全局事件桥接
window.addEventListener("message", (event) => {
  if (event.source !== window) return;
  if (event.data?.type === "LIMS_GENERATE_REPORT") {
    console.log("[LIMS Plugin] 捕获页面生成报告指令:", event.data.payload);
    chrome.runtime.sendMessage({
      type: "SEND_TO_NATIVE",
      payload: {
        action: "GENERATE_PDF",
        reportId: event.data.payload?.reportId,
        templateContent: event.data.payload?.templateContent
      }
    }, (res) => {
      window.postMessage({ type: "LIMS_GENERATE_RESULT", response: res }, "*");
    });
  }
});

// 监听 Background Service Worker 回传结果
chrome.runtime.onMessage.addListener((message) => {
  if (message.type === "NATIVE_RESPONSE") {
    window.postMessage({ type: "LIMS_NATIVE_CALLBACK", payload: message.data }, "*");
  }
});
