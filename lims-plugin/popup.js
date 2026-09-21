// LIMS Chrome 扩展 - Popup 脚本

document.addEventListener("DOMContentLoaded", () => {
  const statusEl = document.getElementById("connStatus");
  const logEl = document.getElementById("logBox");
  const btnConnect = document.getElementById("btnConnect");

  function checkStatus() {
    chrome.runtime.sendMessage({ type: "CHECK_STATUS" }, (res) => {
      if (res?.connected) {
        statusEl.textContent = "已连接 Native";
        statusEl.className = "status-badge connected";
      } else {
        statusEl.textContent = "未连接 Host";
        statusEl.className = "status-badge";
      }
    });
  }

  btnConnect.addEventListener("click", () => {
    chrome.runtime.sendMessage({ type: "SEND_TO_NATIVE", payload: { action: "PING" } }, (res) => {
      logEl.innerHTML += `<div>[${new Date().toLocaleTimeString()}] 发送 PING 指令: ${res?.status}</div>`;
      checkStatus();
    });
  });

  checkStatus();
});
