import request from '../utils/request'

export const loginApi = (data) => request.post('/auth/login', data)
export const registerApi = (data) => request.post('/auth/register', data)
export const getProfileApi = () => request.get('/user/profile')
export const updateProfileApi = (data) => request.put('/user/profile', data)
