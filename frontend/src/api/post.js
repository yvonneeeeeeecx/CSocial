import http from './http'

export const createPostApi = (params) => {
  return http.post('/post', null, { params })
}

export const deletePostApi = (postId, operatorUserId) => {
  return http.delete(`/post/${postId}`, { params: { operatorUserId } })
}



export const listPostApi = () => {
  return http.get('/post')
}

export const pagePostApi = (params) => {
  return http.get('/post/page', { params })
}

export const likePostApi = (postId, userId) => {
  return http.post(`/post/${postId}/like`, null, { params: { userId } })
}

export const commentPostApi = (postId, params) => {
  return http.post(`/post/${postId}/comment`, null, { params })
}

export const listCommentsApi = (postId) => {
  return http.get(`/post/${postId}/comments`)
}

export const listLikesApi = (postId) => {
  return http.get(`/post/${postId}/likes`)
}

