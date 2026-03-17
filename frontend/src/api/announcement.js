import http from './http'

export const listAnnouncementsApi = () => {
  return http.get('/announcement')
}
