import axios from 'axios'

// 创建axios实例
const request = axios.create({
  baseURL: 'http://localhost:8123/api', // 后端API地址
  timeout: 30000,
  withCredentials: true, // 允许携带 Cookie，用于 Session 认证
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

// 递归函数：将可能的大整数ID字段转换为字符串，避免精度丢失
const convertIdFields = (obj: any): any => {
  if (obj === null || obj === undefined) {
    return obj
  }
  
  if (Array.isArray(obj)) {
    return obj.map(convertIdFields)
  }
  
  if (typeof obj === 'object') {
    const result: any = {}
    for (const key in obj) {
      if (Object.prototype.hasOwnProperty.call(obj, key)) {
        // 将常见的ID字段（id, appId, userId等）转换为字符串，如果是大整数
        if ((key === 'id' || key === 'appId' || key === 'userId' || key.endsWith('Id')) 
            && typeof obj[key] === 'number') {
          // 检查是否可能丢失精度（超过安全整数范围或接近）
          // 为了安全，所有ID字段都转换为字符串
          result[key] = String(obj[key])
        } else {
          result[key] = convertIdFields(obj[key])
        }
      }
    }
    return result
  }
  
  return obj
}

// 响应拦截器
request.interceptors.response.use(
  (response) => {
    // 处理响应数据，将ID字段转换为字符串以避免精度丢失
    if (response.data) {
      response.data = convertIdFields(response.data)
    }
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

