import request from '../utils/request'

export const completeInterventionApi = (id) => request.post(`/intervention/${id}/complete`)
