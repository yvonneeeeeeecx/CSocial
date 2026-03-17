import http from './http'

export const listCommunitiesApi = (params = {}) => {
  return http.get('/community', { params })
}

export const listCreatedCommunitiesApi = (userId) => {
  return http.get(`/community/user/${userId}/created`)
}

export const listJoinedCommunitiesApi = (userId) => {
  return http.get(`/community/user/${userId}/joined`)
}

export const createCommunityApi = (params) => {
  return http.post('/community', null, { params })
}

export const joinCommunityApi = (communityId, userId) => {
  return http.post(`/community/${communityId}/join`, null, { params: { userId } })
}

export const leaveCommunityApi = (communityId, userId) => {
  return http.post(`/community/${communityId}/leave`, null, { params: { userId } })
}

export const listCommunityMembersApi = (communityId) => {
  return http.get(`/community/${communityId}/members`)
}

export const listCommunityPendingMembersApi = (communityId, operatorId) => {
  return http.get(`/community/${communityId}/members/pending`, { params: { operatorId } })
}

export const listCommunityAnnouncementsApi = (communityId) => {
  return http.get(`/community/${communityId}/announcements`)
}

export const publishCommunityAnnouncementApi = (communityId, params) => {
  return http.post(`/community/${communityId}/announcements`, null, { params })
}

export const approveCommunityMemberApi = (communityId, userId, operatorId) => {
  return http.post(`/community/${communityId}/members/${userId}/approve`, null, { params: { operatorId } })
}

export const rejectCommunityMemberApi = (communityId, userId, operatorId) => {
  return http.post(`/community/${communityId}/members/${userId}/reject`, null, { params: { operatorId } })
}

