# 后端接口说明

当前版本：`v1.0.11`

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
- `GET /api/fomo/latest`
- `GET /api/fomo/score`
- `POST /api/intervention/{id}/complete`
- `POST /api/recommendation/generate`
- `GET /api/recommendation/mine`
- `POST /api/chat`
- `GET /api/chat/history`
- `GET /api/stats`
- `GET /api/admin/dashboard`
- `GET /api/admin/users`
- `GET /api/admin/fomo-tests`
- `GET /api/admin/chats`
- `GET /api/admin/recommendations`
- `POST /api/admin/recommendations`
- `DELETE /api/admin/users/{id}`
- `DELETE /api/admin/recommendations/{id}`

## 主要说明

- `/api/fomo/submit` 提交测评后会自动生成干预计划
- `/api/fomo/latest` 返回最新测评结果、干预计划和复测对比信息
- FOMO 测评新增补充画像项：睡眠时长、专注程度、通知干扰、目标清晰度，这些字段用于干预建议和结果分析，不参与总分计算
- `/api/intervention/{id}/complete` 用于标记干预计划已执行
- `GET /api/public/news` 获取 AI 与科技新闻栏目数据，支持分类和关键词，默认接入 TechCrunch RSS，抓取失败时自动回退到 mock 数据
- `/api/admin/dashboard` 返回后台总览数据，包括干预完成率
- 管理员默认按用户名 `admin` 判断
- 启动后会自动初始化管理员账号：`admin / admin123`
- 未配置 `AI_API_KEY` 时，聊天模块使用本地兜底回复，项目仍可直接运行
- 新闻栏目默认通过 `NEWS_FEED_URL` 接入 TechCrunch RSS，环境变量可通过 `NEWS_SOURCE_NAME` 调整来源展示名称

## 说明

- 登录后使用 `Authorization: Bearer <token>`
