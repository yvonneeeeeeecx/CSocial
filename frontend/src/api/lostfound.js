import http from './http'

export const listLostFoundApi = (params = {}) => {
  return http.get('/lostfound', { params })
}

export const publishLostFoundApi = (params) => {
  return http.post('/lostfound', null, { params })
}

export const deleteLostFoundApi = (itemId, operatorUserId) => {
  return http.delete(`/lostfound/${itemId}`, { params: { operatorUserId } })
}

