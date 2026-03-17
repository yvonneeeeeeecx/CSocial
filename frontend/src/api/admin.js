import http from './http'

export const adminDashboardApi = (adminUserId) => {
  return http.get('/admin/dashboard', { params: { adminUserId } })
}

export const adminUsersApi = (adminUserId, params = {}) => {
  return http.get('/admin/users', { params: { adminUserId, ...params } })
}


export const adminUpdateUserStatusApi = (adminUserId, targetUserId, status) => {
  return http.put(`/admin/users/${targetUserId}/status`, null, { params: { adminUserId, status } })
}

export const adminPostsApi = (adminUserId, params = {}) => {
  return http.get('/admin/posts', { params: { adminUserId, ...params } })
}


export const adminDeletePostApi = (adminUserId, postId) => {
  return http.delete(`/admin/posts/${postId}`, { params: { adminUserId } })
}

export const adminPublishAnnouncementApi = (params) => {
  return http.post('/admin/announcements', null, { params })
}

export const adminDeleteAnnouncementApi = (adminUserId, announcementId) => {
  return http.delete(`/admin/announcements/${announcementId}`, { params: { adminUserId } })
}

export const adminTrendReportApi = (adminUserId, days = 7) => {
  return http.get('/admin/reports/trend', { params: { adminUserId, days } })
}

