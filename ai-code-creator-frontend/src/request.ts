import axios from 'axios'

// 创建axios实例
const request = axios.create({
  baseURL: 'http://localhost:8123/api', // 后端API地址
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json',
  },
})

// 请求拦截器
request.interceptors.request.use(
  (config) => {
    // 可以在这里添加token等认证信息
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  (response) => {
    return response.data
  },
  (error) => {
    // 处理错误
    if (error.response) {
      console.error('API Error:', error.response.data)
    }
    return Promise.reject(error)
  }
)

export default request

