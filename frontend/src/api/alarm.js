import http from './http'

export const ringApi = (params) => {
  return http.post('/alarm/ring', null, { params })
}
