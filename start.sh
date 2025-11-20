#!/bin/bash

echo "🚀 启动待办清单项目..."

# 检查Java版本
echo "📋 检查Java环境..."
if ! command -v java &> /dev/null; then
    echo "❌ Java未安装，请先安装Java 17或更高版本"
    exit 1
fi

JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 17 ]; then
    echo "❌ Java版本过低，需要Java 17或更高版本，当前版本: $JAVA_VERSION"
    exit 1
fi

# 检查Maven
echo "📦 检查Maven环境..."
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven未安装，请先安装Maven"
    exit 1
fi

# 检查Node.js
echo "🌐 检查Node.js环境..."
if ! command -v node &> /dev/null; then
    echo "❌ Node.js未安装，请先安装Node.js"
    exit 1
fi

# 启动后端
echo "🔧 启动后端服务..."
cd "$(dirname "$0")"
echo "当前目录: $(pwd)"

# 编译并启动Spring Boot应用
echo "📦 编译后端项目..."
./mvnw clean package -DskipTests

if [ $? -ne 0 ]; then
    echo "❌ 后端编译失败"
    exit 1
fi

echo "🚀 启动后端服务..."
nohup java -jar target/todo-list-service-leo-0.0.1-SNAPSHOT.jar > backend.log 2>&1 &
BACKEND_PID=$!
echo "✅ 后端服务已启动，PID: $BACKEND_PID"

# 等待后端启动
echo "⏳ 等待后端服务启动..."
sleep 10

# 检查后端是否启动成功
if curl -s http://localhost:8080/api/tasks > /dev/null; then
    echo "✅ 后端服务启动成功"
else
    echo "❌ 后端服务启动失败，请检查日志: backend.log"
    kill $BACKEND_PID 2>/dev/null
    exit 1
fi

# 启动前端
echo "🎨 启动前端服务..."
cd frontend

# 检查package.json是否存在
if [ ! -f "package.json" ]; then
    echo "❌ 前端package.json文件不存在"
    kill $BACKEND_PID 2>/dev/null
    exit 1
fi

# 安装依赖
echo "📦 安装前端依赖..."
npm install

if [ $? -ne 0 ]; then
    echo "❌ 前端依赖安装失败"
    kill $BACKEND_PID 2>/dev/null
    exit 1
fi

# 启动前端开发服务器
echo "🚀 启动前端开发服务器..."
npm start &
FRONTEND_PID=$!

echo ""
echo "🎉 项目启动完成！"
echo ""
echo "📱 访问地址："
echo "   前端应用: http://localhost:3000"
echo "   后端API: http://localhost:8080"
echo "   API文档: http://localhost:8080/doc.html"
echo "   H2控制台: http://localhost:8080/h2-console"
echo ""
echo "📋 进程信息："
echo "   后端PID: $BACKEND_PID"
echo "   前端PID: $FRONTEND_PID"
echo ""
echo "🛑 停止服务："
echo "   kill $BACKEND_PID $FRONTEND_PID"
echo ""
echo "📝 日志文件："
echo "   后端日志: backend.log"
echo ""

# 保存PID到文件
echo "$BACKEND_PID" > backend.pid
echo "$FRONTEND_PID" > frontend.pid

echo "✅ 进程ID已保存到 backend.pid 和 frontend.pid 文件"