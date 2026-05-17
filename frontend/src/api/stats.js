import request from '../utils/request'

export const statsApi = () => request.get('/stats')
