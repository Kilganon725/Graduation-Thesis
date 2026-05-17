import request from '../utils/request'

export const generateRecommendationApi = () => request.post('/recommendation/generate')
export const myRecommendationApi = () => request.get('/recommendation/mine')
