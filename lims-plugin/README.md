# LIMS Chrome 浏览器扩展安装指南

### ⚠️ 安装前必读（强制约束）
**在向系统注册 Native Messaging Host 之前，请务必先完全关闭并退出 Chrome 浏览器（包括后台进程）！** 否则 Chrome 进程锁定注册表，将无法识别宿主清单。

---

### 1. 扩展程序加载步骤
1. 打开 Google Chrome 浏览器；
2. 在地址栏输入 `chrome://extensions/` 进入扩展管理界面；
3. 打开右上角的 **「开发者模式」** 开关；
4. 点击左上角 **「加载已解压的扩展程序」**；
5. 选择当前项目的 `lims-plugin` 目录，加载成功后记录生成的 **扩展ID（Extension ID）**。

---

### 2. Windows 宿主清单配置（Native Messaging Host）
1. 在 `lims-plugin` 目录下创建 `com.lims.native.host.json`：
```json
{
  "name": "com.lims.native.host",
  "description": "LIMS Native Messaging Host for Chrome",
  "path": "C:\\LIMS\\bin\\lims-native-host.bat",
  "type": "stdio",
  "allowed_origins": [
    "chrome-extension://<YOUR_EXTENSION_ID>/"
  ]
}
```
2. 向 Windows 注册表写入键值：
```reg
HKEY_CURRENT_USER\Software\Google\Chrome\NativeMessagingHosts\com.lims.native.host
(默认值) = "C:\LIMS\bin\com.lims.native.host.json"
```

---

### 3. 一键卸载与日志排查
- **一键卸载**：在 `chrome://extensions/` 界面找到「LIMS 协同助手」，点击「移除」；删除上述注册表键即可。
- **日志排查**：右键点击扩展图标选择「审查弹出内容」，在 Console 中即可实时查看 Native Messaging 交互帧。
