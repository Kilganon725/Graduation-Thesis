# 后端接口说明

## 启动

1. 创建 MySQL 数据库 `aifomo`
2. 执行 `sql/schema.sql`
3. 修改 `src/main/resources/application.yml`
4. 启动 Spring Boot

## 核心接口

- `POST /api/auth/register`
- `POST /api/auth/login`
- `GET /api/user/profile`
- `PUT /api/user/profile`
- `POST /api/fomo/submit`
- `GET /api/fomo/score`
- `POST /api/recommendation/generate`
- `GET /api/recommendation/mine`
- `POST /api/chat`
- `GET /api/chat/history`
- `GET /api/stats`
- `GET /api/admin/users`
- `GET /api/admin/recommendations`

## 说明

- 登录后使用 `Authorization: Bearer <token>`
- 管理员默认按用户名 `admin` 判断
- 启动后会自动初始化管理员账号：`admin / admin123`
- 未配置 `AI_API_KEY` 时，聊天模块使用本地兜底回复，项目仍可直接运行
