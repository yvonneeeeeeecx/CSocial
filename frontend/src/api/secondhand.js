import http from './http'

export const listSecondHandApi = (params = {}) => {
  return http.get('/secondhand', { params })
}

export const publishSecondHandApi = (params) => {
  return http.post('/secondhand', null, { params })
}

export const deleteSecondHandApi = (itemId, operatorUserId) => {
  return http.delete(`/secondhand/${itemId}`, { params: { operatorUserId } })
}

