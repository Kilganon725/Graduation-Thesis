import request from '../utils/request'

export const sendChatApi = (data) => request.post('/chat', data)
export const chatHistoryApi = (limit = 10) => request.get('/chat/history', { params: { limit } })
