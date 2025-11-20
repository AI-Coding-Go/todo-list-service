#!/bin/bash

# 启动后端和前端的脚本

echo "🚀 启动待办清单应用..."

# 检查并创建数据目录
if [ ! -d "data" ]; then
    echo "📁 创建数据目录..."
    mkdir -p data
fi

# 启动后端
echo "🔧 启动后端服务..."
cd "$(dirname "$0")"
./mvnw spring-boot:run -Dmaven.test.skip=true > app.log 2>&1 &
BACKEND_PID=$!

# 等待后端启动
echo "⏳ 等待后端启动..."
sleep 30

# 检查后端是否启动成功
if curl -s http://localhost:8080/api/tasks > /dev/null; then
    echo "✅ 后端启动成功！"
else
    echo "❌ 后端启动失败，请检查日志："
    tail -20 app.log
    exit 1
fi

# 启动前端
echo "🎨 启动前端服务..."
cd frontend
npm start > ../frontend.log 2>&1 &
FRONTEND_PID=$!

# 等待前端启动
echo "⏳ 等待前端启动..."
sleep 20

# 检查前端是否启动成功
if curl -s http://localhost:3000 > /dev/null; then
    echo "✅ 前端启动成功！"
else
    echo "❌ 前端启动失败，请检查日志："
    tail -20 ../frontend.log
    exit 1
fi

echo ""
echo "🎉 应用启动完成！"
echo "📱 前端地址: http://localhost:3000"
echo "🔧 后端地址: http://localhost:8080"
echo "📊 API文档: http://localhost:8080/doc.html"
echo ""
echo "🛑 停止应用请运行: ./stop.sh"
echo ""
echo "📝 进程ID:"
echo "   后端: $BACKEND_PID"
echo "   前端: $FRONTEND_PID"