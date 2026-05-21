import request from '../utils/request'

export const adminDashboardApi = () => request.get('/admin/dashboard')
export const adminUsersApi = (page = 1, size = 10, keyword = '') => request.get('/admin/users', { params: { page, size, keyword } })
export const adminFomoTestsApi = (page = 1, size = 10) => request.get('/admin/fomo-tests', { params: { page, size } })
export const adminChatsApi = (page = 1, size = 10) => request.get('/admin/chats', { params: { page, size } })
export const adminRecommendationsApi = (page = 1, size = 10) => request.get('/admin/recommendations', { params: { page, size } })
export const saveAdminRecommendationApi = (data) => request.post('/admin/recommendations', data)
export const deleteAdminRecommendationApi = (id) => request.delete(`/admin/recommendations/${id}`)
export const deleteAdminUserApi = (id) => request.delete(`/admin/users/${id}`)
