# AI时代大学生信息焦虑分析与个性化学习推荐系统

## 目录

- `backend/` Spring Boot 后端
- `frontend/` Vue3 前端
- `sql/schema.sql` 数据库脚本
- `docs/api.md` 接口说明

## 启动顺序

1. 创建 MySQL 数据库并执行 `sql/schema.sql`
2. 修改 `backend/src/main/resources/application.yml`
3. 启动后端
4. 进入 `frontend/` 执行 `npm install`
5. 启动前端 `npm run dev`

## 默认管理员

- 用户名：`admin`
- 密码：`admin123`

## 说明

- AI 聊天接口支持 OpenAI 兼容接口
- 如果未配置 `AI_API_KEY`，聊天页会使用本地兜底回复，系统仍可完整运行
