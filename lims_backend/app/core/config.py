import os
from pydantic_settings import BaseSettings

class Settings(BaseSettings):
    PROJECT_NAME: str = "LIMS 实验室信息管理系统"
    VERSION: str = "v10.0-Snapshot"
    API_V1_STR: str = "/api"

    # 数据库配置 (默认兼容本地已配置的 MySQL；未启动时平滑 fallback 至 sqlite 以确保 demo 顺畅开箱即用)
    MYSQL_HOST: str = os.getenv("MYSQL_HOST", "127.0.0.1")
    MYSQL_PORT: int = int(os.getenv("MYSQL_PORT", 3306))
    MYSQL_USER: str = os.getenv("MYSQL_USER", "root")
    MYSQL_PASSWORD: str = os.getenv("MYSQL_PASSWORD", "2248962829")
    MYSQL_DB: str = os.getenv("MYSQL_DB", "lims_demo")

    SECRET_KEY: str = "09d25e094faa6ca2556c818166b7a9563b93f7099f6f0f4caa6cf63b88e8d3e7"
    ACCESS_TOKEN_EXPIRE_MINUTES: int = 60 * 24

    class Config:
        case_sensitive = True

settings = Settings()
