import http from './http'

export const sendMessageApi = (params) => {
  return http.post('/message/send', null, { params })
}

export const getConversationApi = (params) => {
  return http.get('/message/conversation', { params })
}

export const getConversationsApi = (params) => {
  return http.get('/message/conversations', { params })
}

