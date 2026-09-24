import os
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from fastapi.staticfiles import StaticFiles
from app.core.config import settings
from app.api.endpoints import router as api_router
from app.core.init_db import init_db

app = FastAPI(
    title=settings.PROJECT_NAME,
    version=settings.VERSION,
    description="现代化 LIMS 实验室信息管理系统 API (基于 Python FastAPI + SQLAlchemy + MySQL/SQLite)"
)

# 允许跨域
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# 注册 API 路由：既挂载 /api 规范前缀，又支持兼容原有路径直接请求
app.include_router(api_router, prefix=settings.API_V1_STR)
app.include_router(api_router)

# 挂载前端静态文件目录
frontend_dist = os.path.join(os.path.dirname(os.path.dirname(os.path.dirname(__file__))), "lims_frontend")
if os.path.exists(frontend_dist):
    app.mount("/web", StaticFiles(directory=frontend_dist, html=True), name="frontend")

@app.on_event("startup")
def on_startup():
    init_db()

@app.get("/")
def root():
    return {
        "system": settings.PROJECT_NAME,
        "version": settings.VERSION,
        "status": "online",
        "docs_url": "/docs",
        "frontend_url": "/web/index.html"
    }
