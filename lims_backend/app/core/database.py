import os
from sqlalchemy import create_engine
from sqlalchemy.orm import declarative_base, sessionmaker
import pymysql
from app.core.config import settings

def get_engine():
    # 尝试连接 MySQL
    mysql_url = f"mysql+pymysql://{settings.MYSQL_USER}:{settings.MYSQL_PASSWORD}@{settings.MYSQL_HOST}:{settings.MYSQL_PORT}/{settings.MYSQL_DB}?charset=utf8mb4"
    try:
        # 测试 MySQL 是否可连
        conn = pymysql.connect(
            host=settings.MYSQL_HOST,
            port=settings.MYSQL_PORT,
            user=settings.MYSQL_USER,
            password=settings.MYSQL_PASSWORD,
            connect_timeout=2
        )
        with conn.cursor() as cursor:
            cursor.execute(f"CREATE DATABASE IF NOT EXISTS `{settings.MYSQL_DB}` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;")
        conn.commit()
        conn.close()

        print(f"[*] 成功连接 MySQL 数据库: {settings.MYSQL_HOST}:{settings.MYSQL_PORT}/{settings.MYSQL_DB}")
        return create_engine(mysql_url, pool_pre_ping=True, pool_recycle=3600)
    except Exception as e:
        print(f"[!] 本地 MySQL 尚未就绪或连接失败 ({e})，为保障 Demo 即刻可运行，自动采用持久化 SQLite 引擎。")
        db_path = os.path.join(os.path.dirname(os.path.dirname(os.path.dirname(__file__))), "lims_demo.db")
        sqlite_url = f"sqlite:///{db_path}"
        return create_engine(sqlite_url, connect_args={"check_same_thread": False})

engine = get_engine()
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)
Base = declarative_base()

def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()
