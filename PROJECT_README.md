# 待办清单项目

## 📋 项目概述

这是一个跨终端（Web/iOS/Android）轻量型个人待办清单工具，聚焦「任务管理效率」与「极简交互体验」，通过核心功能闭环满足用户日常待办记录、进度追踪、时间提醒需求。

## 🎯 核心功能

### 任务基础管理
- ✅ **任务新增**：支持标题、描述、截止时间、优先级设置
- ✏️ **任务编辑**：修改任务各项信息
- 🗑️ **任务删除**：支持二次确认删除
- ✅ **状态切换**：标记完成/未完成状态
- 📋 **列表查看**：多种排序和筛选方式

### 数据统计
- 📊 **完成率统计**：近7天任务完成率
- 📈 **趋势分析**：每日新增/完成任务趋势
- 🥧 **状态分布**：任务完成状态饼图
- 🎯 **优先级分析**：各优先级任务分布

### 智能提醒
- ⏰ **截止时间提醒**：截止前30分钟提醒
- 🔔 **整点提醒**：整点时间提醒
- ⚠️ **逾期提醒**：逾期任务每日提醒（最多3次）

## 🏗️ 技术架构

### 后端技术栈
- **框架**: Spring Boot 3.2.3
- **数据库**: SQLite (轻量级持久化数据库)
- **ORM**: Spring Data JPA
- **API文档**: Knife4j (Swagger)
- **定时任务**: Spring Scheduling

### 前端技术栈
- **框架**: React 18
- **UI组件**: Ant Design 5
- **路由**: React Router 6
- **HTTP客户端**: Axios
- **图表**: Recharts
- **构建工具**: Create React App

## 🚀 快速开始

### 环境要求
- Java 17+
- Node.js 16+
- Maven 3.6+

### 方法一：使用启动脚本（推荐）
```bash
# 启动所有服务
./start-all.sh

# 停止所有服务
./stop.sh
```

### 方法二：手动启动

#### 启动后端
```bash
# 确保数据目录存在
mkdir -p data

# 启动 Spring Boot 应用
./mvnw spring-boot:run -Dmaven.test.skip=true
```

#### 启动前端
```bash
cd frontend
npm start
```

## 📱 访问地址

- **前端应用**: http://localhost:3000
- **后端API**: http://localhost:8080
- **API文档**: http://localhost:8080/doc.html

## 📚 API文档

### 任务管理接口

| 方法 | 路径 | 描述 |
|------|------|------|
| GET | `/api/tasks` | 获取任务列表 |
| POST | `/api/tasks` | 创建新任务 |
| GET | `/api/tasks/{id}` | 获取任务详情 |
| PUT | `/api/tasks/{id}` | 更新任务 |
| DELETE | `/api/tasks/{id}` | 删除任务 |
| PATCH | `/api/tasks/{id}/toggle` | 切换任务状态 |
| GET | `/api/tasks/statistics` | 获取统计数据 |
| GET | `/api/tasks/overdue` | 获取逾期任务 |

### 提醒接口

| 方法 | 路径 | 描述 |
|------|------|------|
| GET | `/api/reminders/current` | 获取当前提醒 |

## 🗂️ 项目结构

```
todo-list-service/
├── data/                    # SQLite 数据库文件目录
│   └── todo.db             # SQLite 数据库文件
├── src/main/java/org/example/todolistservice/
│   ├── controller/          # 控制器层
│   ├── service/             # 服务层
│   ├── repository/          # 数据访问层
│   ├── entity/              # 实体类
│   ├── dto/                 # 数据传输对象
│   ├── config/              # 配置类
│   └── exception/           # 异常处理
├── src/main/resources/      # 配置文件
│   └── application.properties
├── src/test/                # 测试代码
├── frontend/                # 前端代码
│   ├── src/
│   │   ├── components/      # React组件
│   │   ├── services/        # API服务
│   │   └── App.js           # 主应用组件
│   └── package.json         # 包含代理配置
├── start-all.sh            # 一键启动脚本
├── stop.sh                 # 停止脚本
└── pom.xml                  # Maven配置
```

## 💾 数据库说明

### SQLite 配置
- **数据库文件**: `./data/todo.db`
- **数据库类型**: SQLite
- **持久化**: ✅ 数据会持久保存
- **自动更新**: ✅ 表结构会自动更新

### 数据库优势
1. **轻量级**: 无需额外的数据库服务器
2. **持久化**: 重启应用后数据不会丢失
3. **跨平台**: 支持所有操作系统
4. **零配置**: 开箱即用

### 应用配置
- **服务端口**: 8080
- **前端端口**: 3000
- **API前缀**: `/api`

### 配置文件详情

#### application.properties
```properties
# SQLite数据库配置
spring.datasource.url=jdbc:sqlite:./data/todo.db
spring.datasource.driverClassName=org.sqlite.JDBC
spring.jpa.database-platform=org.hibernate.community.dialect.SQLiteDialect
spring.jpa.hibernate.ddl-auto=update
```

#### Maven 依赖
```xml
<!-- SQLite JDBC 驱动 -->
<dependency>
    <groupId>org.xerial</groupId>
    <artifactId>sqlite-jdbc</artifactId>
    <version>3.45.1.0</version>
</dependency>

<!-- SQLite 方言支持 -->
<dependency>
    <groupId>org.hibernate.orm</groupId>
    <artifactId>hibernate-community-dialects</artifactId>
</dependency>
```

## 🧪 测试

### 运行后端测试
```bash
./mvnw test
```

### 运行前端测试
```bash
cd frontend
npm test
```

## 📝 开发指南

### 添加新功能
1. 在`entity`包中创建实体类
2. 在`repository`包中创建数据访问接口
3. 在`service`包中实现业务逻辑
4. 在`controller`包中创建REST接口
5. 在前端`components`中创建UI组件
6. 在`services/api.js`中添加API调用

### 数据库操作
- 使用Spring Data JPA进行数据访问
- 支持自动建表和数据迁移
- SQLite数据库文件存储在`data/`目录，可直接访问

### 数据库迁移说明
从 H2 迁移到 SQLite 的主要变更：

1. **依赖替换**: `h2` → `sqlite-jdbc` + `hibernate-community-dialects`
2. **配置更新**: 数据库连接字符串和方言
3. **SQL 适配**: 日期函数从 `FORMATDATETIME` 改为 `DATE`
4. **空值处理**: 添加了 null 值过滤

### 注意事项
- SQLite 不支持并发写入，但适合单用户应用
- 数据文件存储在项目根目录的 `data/` 文件夹
- 使用 `hibernate.ddl-auto=update` 自动管理表结构

## 🚀 部署

## 🛠️ 开发说明

### 故障排除

#### 常见问题

1. **数据库文件权限问题**
   ```bash
   chmod 755 data/
   chmod 644 data/todo.db
   ```

2. **端口被占用**
   ```bash
   # 查看端口占用
   lsof -i :8080
   lsof -i :3000
   
   # 停止占用进程
   ./stop.sh
   ```

3. **前端代理问题**
   - 确保 `package.json` 中有 `"proxy": "http://localhost:8080"`
   - 重启前端服务

4. **统计功能异常**
   - 检查数据库文件是否存在
   - 查看后端日志中的 SQL 错误

#### 日志查看
```bash
# 后端日志
tail -f app.log

# 前端日志
tail -f frontend.log
```

## 🚀 部署

### 生产环境部署
1. 备份SQLite数据库文件：`cp data/todo.db data/todo.db.backup`
2. 构建生产版本：`./mvnw clean package`
3. 部署到服务器并运行
4. 确保生产环境有适当的数据库文件权限

### Docker部署
```dockerfile
# Dockerfile示例
FROM openjdk:17-jdk-slim
COPY target/todo-list-service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]
```

## 📈 功能特性

- ✅ **任务管理**: 创建、编辑、删除、完成任务
- ✅ **数据统计**: 完成率、优先级分布、趋势图表
- ✅ **智能提醒**: 截止时间提醒、逾期提醒
- ✅ **数据持久化**: SQLite 数据库存储
- ✅ **跨平台**: 支持 Web/iOS/Android 终端
- ✅ **API文档**: Knife4j 自动生成文档

## 🎯 下一步

1. **数据备份**: 定期备份 `data/todo.db` 文件
2. **性能优化**: 考虑添加索引优化查询
3. **功能扩展**: 添加任务分类、标签等功能
4. **部署**: 考虑使用 Docker 容器化部署

## 🤝 贡献指南

1. Fork项目
2. 创建功能分支
3. 提交更改
4. 推送到分支
5. 创建Pull Request

## 📄 许可证

本项目采用Apache 2.0许可证。

## 🆘 常见问题

### Q: 如何修改数据库？
A: 修改`application.properties`中的数据库配置，重启应用即可。

### Q: 如何添加新的提醒方式？
A: 在`ReminderService`中的`sendReminder`方法中添加新的提醒逻辑。

### Q: 前端如何适配移动端？
A: 项目已使用响应式设计，支持移动端访问。如需原生App，可使用React Native或Flutter重新实现前端。

### Q: 数据持久化问题？
A: 当前使用H2内存数据库，重启后数据会丢失。生产环境建议使用MySQL等持久化数据库。