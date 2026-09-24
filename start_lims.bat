@echo off
chcp 65001 >nul
echo ========================================================
echo        启动 LIMS 实验室信息管理系统 (Python + Web)
echo ========================================================
echo.
cd lims_backend
python run.py
pause
