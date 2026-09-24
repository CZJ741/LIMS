import uvicorn

if __name__ == "__main__":
    print("[*] 正在启动 LIMS 实验室信息管理系统后端服务...")
    print("[*] 访问地址: http://127.0.0.1:8000")
    print("[*] Swagger API 文档: http://127.0.0.1:8000/docs")
    print("[*] 管理员前端页面: http://127.0.0.1:8000/web/index.html")
    uvicorn.run("app.main:app", host="0.0.0.0", port=8000, reload=True)
