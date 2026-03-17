import http from './http'

export const registerApi = (params) => {
  return http.post('/user/register', null, { params })
}

export const loginApi = (params) => {
  return http.post('/user/login', null, { params })
}

export const getUserApi = (userId) => {
  return http.get(`/user/${userId}`)
}

export const updateUserApi = (userId, params) => {
  return http.put(`/user/${userId}`, null, { params })
}

export const updateAlarmSettingApi = (userId, params) => {
  return http.put(`/user/${userId}/alarm-settings`, null, { params })
}

export const listCampusUsersApi = (params) => {
  return http.get('/user/campus', { params })
}



