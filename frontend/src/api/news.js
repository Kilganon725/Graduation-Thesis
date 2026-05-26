import request from '../utils/request'

export const fetchNewsApi = (params = {}) => request.get('/public/news', { params })
