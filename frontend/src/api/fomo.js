import request from '../utils/request'

export const submitFomoApi = (data) => request.post('/fomo/submit', data)
export const latestFomoApi = () => request.get('/fomo/latest')
export const scoreApi = () => request.get('/fomo/score')
