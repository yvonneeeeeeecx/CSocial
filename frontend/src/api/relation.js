import http from './http'

export const likeUserApi = (fromUserId, toUserId) => {
  return http.post(`/relation/like/${toUserId}`, null, { params: { fromUserId } })
}

export const getMatchedUsersApi = (userId) => {
  return http.get('/relation/matches', { params: { userId } })
}

export const followUserApi = (fromUserId, toUserId) => {
  return http.post(`/relation/follow/${toUserId}`, null, { params: { fromUserId } })
}

export const unfollowUserApi = (fromUserId, toUserId) => {
  return http.delete(`/relation/follow/${toUserId}`, { params: { fromUserId } })
}

export const getFollowingUsersApi = (userId) => {
  return http.get('/relation/following', { params: { userId } })
}

export const getFollowersApi = (userId) => {
  return http.get('/relation/followers', { params: { userId } })
}

export const getMutualFollowApi = (userId) => {
  return http.get('/relation/mutual-follow', { params: { userId } })
}

export const blockUserApi = (fromUserId, toUserId) => {
  return http.post(`/relation/block/${toUserId}`, null, { params: { fromUserId } })
}

export const unblockUserApi = (fromUserId, toUserId) => {
  return http.delete(`/relation/block/${toUserId}`, { params: { fromUserId } })
}

export const getBlockedUsersApi = (userId) => {
  return http.get('/relation/blocked', { params: { userId } })
}

