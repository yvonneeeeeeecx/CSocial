import http from './http'

export const listActivitiesApi = (params = {}) => {
  return http.get('/activity', { params })
}

export const publishActivityApi = (params) => {
  return http.post('/activity', null, { params })
}

export const deleteActivityApi = (activityId, operatorUserId) => {
  return http.delete(`/activity/${activityId}`, { params: { operatorUserId } })
}

