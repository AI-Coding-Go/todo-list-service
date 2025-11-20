#!/bin/bash

echo "🎬 待办清单项目演示"
echo "===================="

# 检查后端是否运行
echo "📋 检查后端服务状态..."
if curl -s http://localhost:8080/api/tasks > /dev/null; then
    echo "✅ 后端服务正在运行"
else
    echo "❌ 后端服务未运行，请先执行 ./start.sh"
    exit 1
fi

echo ""
echo "🔧 演示API功能..."
echo "=================="

# 创建示例任务
echo "📝 创建示例任务..."
TASK1=$(curl -s -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -d '{
    "title": "完成项目文档",
    "description": "编写项目的技术文档和用户手册",
    "priority": "HIGH",
    "deadline": "'$(date -d '+1 day' -Iseconds)'"
  }')

TASK1_ID=$(echo $TASK1 | grep -o '"id":[0-9]*' | cut -d':' -f2)
echo "✅ 创建任务1: 完成项目文档 (ID: $TASK1_ID)"

TASK2=$(curl -s -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -d '{
    "title": "代码审查",
    "description": "审查团队成员提交的代码",
    "priority": "MEDIUM",
    "deadline": "'$(date -d '+2 days' -Iseconds)'"
  }')

TASK2_ID=$(echo $TASK2 | grep -o '"id":[0-9]*' | cut -d':' -f2)
echo "✅ 创建任务2: 代码审查 (ID: $TASK2_ID)"

TASK3=$(curl -s -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -d '{
    "title": "学习新技术",
    "description": "学习React和Spring Boot的最新特性",
    "priority": "LOW"
  }')

TASK3_ID=$(echo $TASK3 | grep -o '"id":[0-9]*' | cut -d':' -f2)
echo "✅ 创建任务3: 学习新技术 (ID: $TASK3_ID)"

echo ""
echo "📋 获取任务列表..."
curl -s http://localhost:8080/api/tasks | jq '.[] | {id, title, priority, status}' 2>/dev/null || echo "请安装jq以格式化JSON输出"

echo ""
echo "✅ 标记任务1为已完成..."
curl -s -X PATCH http://localhost:8080/api/tasks/$TASK1_ID/toggle > /dev/null
echo "任务1状态已更新"

echo ""
echo "📊 获取统计数据..."
STATS=$(curl -s http://localhost:8080/api/tasks/statistics)
echo "总任务数: $(echo $STATS | grep -o '"totalTasks":[0-9]*' | cut -d':' -f2)"
echo "已完成: $(echo $STATS | grep -o '"completedTasks":[0-9]*' | cut -d':' -f2)"
echo "完成率: $(echo $STATS | grep -o '"completionRate":[0-9.]*' | cut -d':' -f2)%"

echo ""
echo "🔔 检查提醒任务..."
REMINDERS=$(curl -s http://localhost:8080/api/reminders/current)
REMINDER_COUNT=$(echo $REMINDERS | jq '. | length' 2>/dev/null || echo "0")
echo "当前需要提醒的任务数: $REMINDER_COUNT"

echo ""
echo "🎯 演示完成！"
echo "=============="
echo ""
echo "📱 访问地址："
echo "   前端应用: http://localhost:3000"
echo "   后端API: http://localhost:8080"
echo "   API文档: http://localhost:8080/doc.html"
echo ""
echo "💡 提示："
echo "   - 在浏览器中打开前端应用查看完整界面"
echo "   - 访问API文档查看所有接口"
echo "   - 使用 ./stop.sh 停止所有服务"