# 接口说明

## 认证

- `POST /api/auth/register`
- `POST /api/auth/login`

返回 `token` 后，在请求头加入：

`Authorization: Bearer <token>`

## 用户

- `GET /api/user/profile`
- `PUT /api/user/profile`

## FOMO

- `POST /api/fomo/submit`
- `GET /api/fomo/latest`
- `GET /api/fomo/score`
- `POST /api/intervention/{id}/complete`

## 推荐

- `POST /api/recommendation/generate`
- `GET /api/recommendation/mine`

## AI 聊天

- `POST /api/chat`
- `GET /api/chat/history?limit=10`

## 统计

- `GET /api/stats`

## 管理员

- `GET /api/admin/dashboard`
- `GET /api/admin/users`
- `DELETE /api/admin/users/{id}`
- `GET /api/admin/fomo-tests`
- `GET /api/admin/chats`
- `GET /api/admin/recommendations`
- `POST /api/admin/recommendations`
- `DELETE /api/admin/recommendations/{id}`
