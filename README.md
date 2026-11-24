# Cube_Timer · 魔方计时器

一个记录、管理与统计魔方成绩的全栈项目。**后端为主线**（Spring Boot 多模块），前端为辅助（Vue 3）。

![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.8-brightgreen?logo=springboot)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?logo=mysql)
![Redis](https://img.shields.io/badge/Redis-6.0-red?logo=redis)
![Vue](https://img.shields.io/badge/Vue-3.5-42b883?logo=vuedotjs)

> 项目目标：通过从零实现一个完整业务系统，系统掌握企业级后端的常见机制
> （事务 / 鉴权 / AOP / 缓存 / 分页 / 定时任务 / WebSocket / 文件 / 报表 / 多模块）。

## 功能特性

### 业务功能

- **成绩管理**：新增 / 修改 / 删除 / 分页查询 / 按耗时区间筛选 / 详情查看
- **打乱公式**：一条成绩可关联多条打乱公式（1:N），删除成绩时级联删除
- **数据统计**：总次数、最佳、最差、AVG5 / AVG12（WCA「去头尾」规则）、今日最佳
- **每日统计**：定时任务凌晨 2 点把前一天的数据聚合落库（`daily_stat`）
- **成绩导出**：一键导出 Excel（当前用户的全部成绩）
- **实时推送**：新增成绩时通过 WebSocket 广播给所有在线客户端
- **图片上传**：支持上传图片（白名单 + 大小限制 + UUID 重命名）

### 工程能力

| 能力 | 实现 |
|---|---|
| 分层架构 | Controller → Service(接口+实现) → Mapper → MySQL |
| Maven 多模块 | `common`（通用） / `pojo`（数据对象） / `server`（业务服务） |
| 统一返回 | `Result{code, msg, data}`、`PageResult{total, records}` |
| 全局异常 | 业务异常（warn）/ 参数校验失败（warn）/ 系统异常（error，不泄漏内部实现） |
| 参数校验 | `@Valid` + JSR-303，Service 层零手写校验 |
| 认证鉴权 | BCrypt 密码 + JWT 签发 + 拦截器校验 + ThreadLocal 上下文 |
| 数据隔离 | 所有业务 SQL 带 `user_id` 过滤，越权访问统一返回「记录不存在」 |
| 事务 | `@Transactional(rollbackFor = Exception.class)`，多表写入原子性 |
| AOP | `@AutoFill` 自动填充 `create_time / create_user / update_time / update_user` |
| 缓存 | 统计接口 Redis 缓存（TTL 60s + 写操作主动失效 + 按用户隔离 key） |
| 分页 | PageHelper（`reasonable=true`，页码越界自动回到最后一页） |
| 日志 | 拦截器统一记录请求方法 / 路径 / 状态码 / 耗时 |
| 接口文档 | springdoc（OpenAPI 3.1），仅 dev 环境开启 |
| 测试 | JUnit 5（核心统计算法 + 上下文冒烟测试） |

## 技术栈

### 后端

| 项 | 版本 / 说明 |
|---|---|
| JDK | 21 |
| Spring Boot | 4.0.8 |
| MyBatis | mybatis-spring-boot-starter 4.0.1 |
| 数据库 | MySQL 8.0 |
| 缓存 | Redis 6.0 |
| 分页 | PageHelper 2.1.0 |
| JWT | jjwt 0.12.6 |
| 接口文档 | springdoc-openapi 3.0.0 |
| Excel | Apache POI 5.2.5 |
| 构建 | Maven（多模块） |

### 前端

| 项 | 版本 |
|---|---|
| Vue | 3.5.44 |
| Vue Router | 4.6.4 |
| Vite | 5.4.21 |

## 项目结构

```
Cube_Timer/
├── backend/                          # 后端（Maven 多模块）
│   ├── pom.xml                       # 父 pom（packaging=pom，聚合三模块）
│   │
│   ├── common/                       # 通用能力（不依赖 Web）
│   │   └── com/example/backend/
│   │       ├── common/               # Result / PageResult
│   │       ├── exception/            # BusinessException
│   │       ├── context/              # BaseContext（ThreadLocal 存当前用户）
│   │       ├── properties/           # JwtProperties（@ConfigurationProperties）
│   │       ├── util/                 # JwtUtil / StatsUtils
│   │       ├── annotation/           # @AutoFill
│   │       └── enums/                # OperationType
│   │
│   ├── pojo/                         # 数据对象（不依赖 Spring Web）
│   │   └── com/example/backend/
│   │       ├── entity/               # CubeRecord / CubeScramble / User / DailyStat
│   │       ├── dto/                  # 入参对象
│   │       └── vo/                   # 出参对象
│   │
│   ├── server/                       # 业务服务（Spring Boot 可执行模块）
│   │   ├── src/main/java/com/example/backend/
│   │   │   ├── BackendApplication.java
│   │   │   ├── controller/           # 成绩 / 用户 / 文件
│   │   │   ├── service/              # 接口 + impl
│   │   │   ├── mapper/               # MyBatis Mapper 接口
│   │   │   ├── config/               # MVC / OpenAPI / WebSocket 配置
│   │   │   ├── interceptor/          # 请求日志 / JWT 校验
│   │   │   ├── aspect/               # 公共字段自动填充切面
│   │   │   ├── handler/              # 全局异常处理
│   │   │   ├── task/                 # 定时任务
│   │   │   └── websocket/            # WebSocket 服务端
│   │   ├── src/main/resources/
│   │   │   ├── application.yml       # 通用配置（提交入库）
│   │   │   ├── application-dev.yml   # 本地配置（含密码，已被 .gitignore 排除）
│   │   │   ├── application-dev.yml.example
│   │   │   ├── mapper/               # MyBatis XML
│   │   │   └── static/ws-test.html   # WebSocket 测试页
│   │   └── src/test/java/            # 单元测试
│   │
│   └── docs/springboot-learning/     # 开发文档（学习计划 / 进度记录 / 课程卡）
│
├── frontend/                         # 前端（Vue 3 + Vite）
├── docs/                             # 项目文档
│   └── 面试话术-项目讲解.md          # 面试讲解准备（30 秒 / 2 分钟 / 深挖）
├── scripts/                          # 一键启停脚本（Linux）
├── cube_timer.sql                    # 建表脚本
└── README.md
```

## 快速开始

### 前置要求

- JDK 21
- MySQL 8
- Redis 6
- Maven 3.6+
- Node.js 18+（仅前端需要）

### 1. 初始化数据库

```bash
mysql -u root -p < cube_timer.sql
```

### 2. 配置数据库连接

```bash
cd backend/server/src/main/resources
cp application-dev.yml.example application-dev.yml
```

编辑 `application-dev.yml`，填入你的数据库账号密码：

```yaml
spring:
  datasource:
    username: your-username
    password: your-password
```

### 3. 启动依赖服务

```bash
sudo service mysql start
redis-server --daemonize yes
```

### 4. 构建并运行后端

```bash
cd backend

# 方式 A：打包后运行（推荐，最接近生产）
mvn clean package -DskipTests
java -jar server/target/server-0.0.1-SNAPSHOT.jar

# 方式 B：直接用 Maven 运行（-am 会连带构建 common/pojo）
mvn -pl server -am spring-boot:run
```

后端地址：http://localhost:8081

> **多模块注意**：单独 `cd server && mvn spring-boot:run` 会失败（common/pojo 未安装）。
> 要么加 `-am`，要么先 `mvn install`。

### 5. 启动前端（可选）

```bash
cd frontend
npm install
npm run dev
```

### 6. 接口文档（仅 dev）

```
http://localhost:8081/swagger-ui/index.html
```

## API 概览

所有接口统一返回：

```json
{ "code": 0, "msg": null, "data": null }
```

| `code` | 含义 |
|---|---|
| `0` | 成功 |
| `1` | 业务 / 校验失败，`msg` 为可读提示 |

### 用户（无需登录）

| 方法 | 路径 | 说明 |
|---|---|---|
| POST | `/user/register` | 注册，请求体 `{username, password, nickname?}` |
| POST | `/user/login` | 登录，返回 `{id, username, nickname, token}` |

### 成绩（**需请求头** `Authorization: Bearer <token>`）

| 方法 | 路径 | 说明 |
|---|---|---|
| POST | `/record` | 新增成绩 `{timeMs}` |
| POST | `/record/full` | 新增成绩 + 多条打乱（**事务**）`{timeMs, scrambles[]}` |
| GET | `/record/list` | 当前用户全部成绩 |
| GET | `/record/page` | 分页 + 条件（`page`/`size`/`minMs`/`maxMs`） |
| GET | `/record/search` | 按耗时区间筛选 |
| GET | `/record/{id}` | 详情（含打乱列表） |
| PUT | `/record/{id}` | 修改成绩 `{timeMs}` |
| DELETE | `/record/{id}` | 删除（打乱级联删除） |
| POST | `/record/{id}/scramble` | 追加打乱公式 `?scramble=...` |
| GET | `/record/stats` | 统计（Redis 缓存 60s） |
| GET | `/record/export` | **导出 Excel**（当前用户全部成绩） |

### 文件（**需登录**）

| 方法 | 路径 | 说明 |
|---|---|---|
| POST | `/file/upload` | 上传图片，返回 `/uploads/xxx.png` |
| GET | `/uploads/**` | 静态访问已上传图片（无需登录） |

### 实时推送

```
WebSocket: ws://localhost:8081/ws/{任意唯一 sid}

服务端推送消息：
  {"type":"NEW_RECORD","timeMs":7777}
```

浏览器测试页：`http://localhost:8081/ws-test.html`

## 核心技术点

### 1. 认证与数据隔离

```
登录 → BCrypt 校验 → 签发 JWT（payload 含 userId）
     → 前端存 token，后续请求带 Authorization: Bearer <token>
     → 拦截器 parseJWT → BaseContext.setCurrentId(userId)（ThreadLocal）
     → Service 取当前 userId → Mapper SQL 全部带 where user_id = ?
     → 请求结束 afterCompletion 清理 ThreadLocal
```

- **越权访问统一返回「记录不存在」**（而非「无权限」），避免泄露「这条数据存在」
- **缓存 key 带用户维度**：`cube:stats:{userId}`，防止 A 用户读到 B 用户的统计

### 2. 事务

```java
@Transactional(rollbackFor = Exception.class)
public void addFull(CubeRecordFullDTO dto) { ... }
```

成绩与多条打乱要么全成功、要么全回滚。`rollbackFor` 保证受检异常也回滚。

### 3. AOP 自动填充

```java
@AutoFill(OperationType.INSERT)
void insert(CubeRecord record);
```

切面在 Mapper 方法执行前，用反射填充 `create_time / create_user / update_time / update_user`。
INSERT 填 4 个，UPDATE 只填 2 个。

### 4. 缓存策略

- key：`cube:stats:{userId}`，TTL 60 秒
- 读：先查 Redis，命中直接返回
- 写：增 / 删 / 改成功后删除缓存（Cache Invalidation）
- **缓存访问失败时降级查库**，不影响主流程

### 5. 安全细节

| 场景 | 措施 |
|---|---|
| 密码存储 | BCrypt（自带盐，故意慢），绝不明文/MD5 |
| 登录失败提示 | 统一「用户名或密码错误」，防用户名枚举 |
| 用户名重复 | 应用层先查 + **数据库唯一索引兜底**（并发安全） |
| 文件上传 | UUID 重命名（防路径穿越）+ 扩展名 & Content-Type 双白名单 + 5MB 限制 |
| SQL 注入 | MyBatis `#{}` 预编译，不使用 `${}` |
| 接口文档 | 生产环境 `springdoc.api-docs.enabled=false` |

### 6. 多模块依赖方向

```
server ──> common
       └─> pojo
common ⟷ pojo （互不依赖）
```

`pojo` 模块里调用 `Service` 会**编译失败** —— 依赖边界由编译器强制。

## 测试

```bash
cd backend
mvn test
```

覆盖：
- `StatsUtilsTest`：AVG 去头尾算法、边界（数据不足 / 空 / null）、时间格式化
- `BackendApplicationTests`：Spring 上下文冒烟测试（`RANDOM_PORT`，因为 WebSocket 需要真实容器）

## 部署

### Linux 一键启停（需 root）

```bash
sudo bash scripts/start.sh
sudo bash scripts/stop.sh
```

### Windows

```bat
一键启动.bat
一键停止.bat
```

详见 `使用说明.md`。

## 开发文档

学习与设计过程记录：

- **`docs/项目导读.md` —— 读代码专用：模块地图 / 调用链 / 机制索引 / 自测清单**
- `backend/docs/springboot-learning/学习计划与进度.md` —— 导航 + 现状 + 契约 + 环境手册
- `backend/docs/springboot-learning/阶段一-课程卡-已完成.md` —— D1–D15 教案
- `backend/docs/springboot-learning/阶段一-进度记录.md` —— D1–D15 实测与踩坑
- `backend/docs/springboot-learning/阶段二-课程卡-对标寒页外卖.md` —— D16–D26 教案
- `backend/docs/springboot-learning/阶段二-进度记录.md` —— D16–D26 实测与踩坑
- `frontend/docs/vue-learning/` —— 前端学习记录
- `docs/面试话术-项目讲解.md` —— **面试怎么讲这个项目**（亮点 / 追问 / 简历写法）

## License

[MIT](LICENSE) —— 可自由使用、修改、分发。

> 个人学习项目，用于学习与作品展示。
