# Cube_Timer · 魔方计时器

一个用于记录、管理和统计魔方成绩的个人全栈项目。后端为主线，前端为辅助。

## 功能

- **成绩管理**：新增 / 修改 / 删除 / 分页查询 / 按时间区间筛选
- **打乱公式**：一条成绩可关联多条打乱公式（1:N），删除成绩时级联删除
- **数据统计**：总次数、最佳、最差、AVG5、AVG12（WCA 去头尾规则）、今日最佳
- **统一返回**：全局 `Result{code, msg, data}`，分页封装 `PageResult{total, records}`
- **全局异常**：业务异常（warn）与系统异常（error）分级处理，错误信息不泄漏内部实现
- **参数校验**：`@Valid` + JSR-303 注解，Service 层零手写校验
- **缓存**：统计接口 Redis 缓存（TTL 60s），写操作主动失效
- **请求日志**：拦截器统一记录请求方法、路径、状态码、耗时

## 技术栈

### 后端

| 项 | 版本 / 说明 |
|---|---|
| JDK | 21 |
| Spring Boot | 4.0.8 |
| MyBatis | mybatis-spring-boot-starter 4.0.1 |
| 数据库 | MySQL 8.0 |
| 缓存 | Redis 6.0 |
| 构建 | Maven |

### 前端

| 项 | 版本 |
|---|---|
| Vue | 3.5.44 |
| Vue Router | 4.6.4 |
| Vite | 5.4.21 |

## 项目结构

```
Cube_Timer/
├── backend/                        # 后端（Spring Boot）
│   ├── src/main/java/com/example/backend/
│   │   ├── controller/             # 接口层
│   │   ├── service/                # 业务层（接口 + impl）
│   │   ├── mapper/                 # MyBatis Mapper 接口
│   │   ├── entity/                 # 数据库实体
│   │   ├── dto/                    # 入参对象
│   │   ├── vo/                     # 出参对象
│   │   ├── interceptor/            # 拦截器（请求日志）
│   │   ├── handler/                # 全局异常处理
│   │   ├── config/                 # 配置（MVC 拦截器注册）
│   │   ├── common/                 # Result / PageResult
│   │   ├── exception/              # BusinessException
│   │   └── util/                   # 工具类（统计算法）
│   ├── src/main/resources/
│   │   ├── application.yml         # 通用配置（提交入库）
│   │   ├── application-dev.yml     # 本地配置（含密码，已被 .gitignore 排除）
│   │   ├── application-dev.yml.example
│   │   └── mapper/                 # MyBatis XML
│   └── src/test/java/              # 单元测试
├── frontend/                       # 前端（Vue 3 + Vite）
├── cube_timer.sql                  # 建表脚本
└── README.md
```

## 一键启动（Windows 本机 · 推荐）

> 适合日常使用（不需要开终端）。双击项目根目录的 **`一键启动.bat`** 即可：
> 自动启动 MySQL / Redis / Nginx / 后端 jar，并打开 `http://127.0.0.1/`。
> 用完双击 **`一键停止.bat`** 关闭全部服务。
>
> 详细说明见 [`使用说明.md`](./使用说明.md)；底层脚本在 `scripts/start.sh` / `scripts/stop.sh`。

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
cd backend/src/main/resources
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

### 4. 启动后端

```bash
cd backend
mvn spring-boot:run
```

或用打包产物：

```bash
mvn -DskipTests package
java -jar server/target/server-0.0.1-SNAPSHOT.jar（多模块后产物在 server 模块）
```

后端地址：http://localhost:8081

### 5. 启动前端（可选）

```bash
cd frontend
npm install
npm run dev
```

## API 文档

所有接口返回统一格式：

```json
{ "code": 0, "msg": null, "data": null }
```

| `code` | 含义 |
|---|---|
| `0` | 成功 |
| `1` | 业务 / 校验失败，`msg` 为可读提示 |

### 成绩

| 方法 | 路径 | 说明 |
|---|---|---|
| POST | `/record` | 新增成绩，请求体 `{"timeMs": 1234}` |
| GET | `/record/list` | 全部成绩 |
| GET | `/record/page?page=1&size=10` | 分页查询 |
| GET | `/record/search?minMs=1000&maxMs=5000` | 按时间区间筛选（参数均可选） |
| GET | `/record/{id}` | 成绩详情（含打乱公式列表） |
| PUT | `/record/{id}` | 修改成绩，请求体 `{"timeMs": 8888}` |
| DELETE | `/record/{id}` | 删除成绩（打乱公式级联删除） |

### 打乱公式

| 方法 | 路径 | 说明 |
|---|---|---|
| POST | `/record/{id}/scramble` | 为指定成绩新增打乱公式，参数 `scramble` |

### 统计

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/record/stats` | 总次数 / 最佳 / 最差 / AVG5 / AVG12 / 今日最佳（Redis 缓存 60s） |

响应示例：

```json
{
  "total": 22,
  "bestMs": 592,
  "bestText": "0.592",
  "worstMs": 600000,
  "worstText": "600.000",
  "avg5Text": "4.115",
  "avg12Text": "3.269",
  "todayBestMs": null,
  "todayBestText": null
}
```

## 核心设计说明

### 统一返回与异常分级

- `Result<T>` 统一包装所有响应
- `BusinessException`：业务失败（如记录不存在），日志 `warn`，返回可读提示
- 未预期异常：日志 `error`，对外只返回「系统繁忙」，不泄漏类名 / 方法签名

### 参数校验

- 入参对象（DTO）上使用 `@NotNull` / `@Min` / `@Max`
- Controller 加 `@Valid`，校验在进入 Service 前完成
- 校验失败由 `MethodArgumentNotValidException` 处理器统一转成可读提示

### 统计规则（WCA 去头尾）

AVG5 / AVG12 采用「去头去尾」算法：

1. 取最近 n 次成绩
2. 去掉一个最好、一个最差
3. 剩下 n-2 个求平均

### 缓存策略

- key：`cube:stats`，TTL 60 秒
- 读：先查 Redis，命中直接返回
- 写：增 / 删 / 改成功后立即删除缓存（Cache Invalidation）
- 缓存访问失败时降级查库，不影响主流程

## 测试

```bash
cd backend
mvn test
```

覆盖：

- `StatsUtilsTest`：去头尾平均算法、边界情况（数据不足 / 空列表 / null）、时间格式化
- `BackendApplicationTests`：Spring 上下文能否正常启动

## 开发文档

学习与设计过程记录在：

- `backend/docs/springboot-learning/学习计划与进度.md`
- `frontend/docs/vue-learning/学习计划与进度.md`
