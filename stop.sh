#!/bin/bash

# 停止后端和前端的脚本

echo "🛑 停止待办清单应用..."

# 停止后端
echo "🔧 停止后端服务..."
pkill -f "spring-boot:run" 2>/dev/null || echo "后端服务未运行"

# 停止前端
echo "🎨 停止前端服务..."
pkill -f "react-scripts" 2>/dev/null || echo "前端服务未运行"

# 清理端口
echo "🧹 清理端口..."
lsof -ti:8080 | xargs kill -9 2>/dev/null || echo "端口8080未被占用"
lsof -ti:3000 | xargs kill -9 2>/dev/null || echo "端口3000未被占用"

echo "✅ 应用已停止！"