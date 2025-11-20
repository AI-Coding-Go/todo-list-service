# 待办清单应用 - SQLite 版本

## 🚀 快速启动

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

## 📊 访问地址

- **前端应用**: http://localhost:3000
- **后端API**: http://localhost:8080
- **API文档**: http://localhost:8080/doc.html

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

## 🔧 配置文件

### application.properties
```properties
# SQLite数据库配置
spring.datasource.url=jdbc:sqlite:./data/todo.db
spring.datasource.driverClassName=org.sqlite.JDBC
spring.jpa.database-platform=org.hibernate.community.dialect.SQLiteDialect
spring.jpa.hibernate.ddl-auto=update
```

### Maven 依赖
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

## 📁 项目结构

```
todo-list-service-leo/
├── data/                    # SQLite 数据库文件目录
│   └── todo.db             # SQLite 数据库文件
├── src/main/resources/      # 配置文件
│   └── application.properties
├── frontend/               # React 前端
│   ├── src/
│   └── package.json       # 包含代理配置
├── start-all.sh           # 一键启动脚本
├── stop.sh               # 停止脚本
└── README-SQLite.md      # 本文档
```

## 🛠️ 开发说明

### 数据库迁移
从 H2 迁移到 SQLite 的主要变更：

1. **依赖替换**: `h2` → `sqlite-jdbc` + `hibernate-community-dialects`
2. **配置更新**: 数据库连接字符串和方言
3. **SQL 适配**: 日期函数从 `FORMATDATETIME` 改为 `DATE`
4. **空值处理**: 添加了 null 值过滤

### 注意事项
- SQLite 不支持并发写入，但适合单用户应用
- 数据文件存储在项目根目录的 `data/` 文件夹
- 使用 `hibernate.ddl-auto=update` 自动管理表结构

## 🐛 故障排除

### 常见问题

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

### 日志查看
```bash
# 后端日志
tail -f app.log

# 前端日志
tail -f frontend.log
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