/**
 * 环境变量配置
 */
import { CodeGenTypeEnum } from '@/utils/codeGenTypes'

// API 基础地址
export const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8123/api'

// 静态资源地址
export const STATIC_BASE_URL = `${API_BASE_URL}/static`

// 获取静态资源预览URL
export const getStaticPreviewUrl = (codeGenType: string, appId: string) => {
  // 在开发环境下，使用相对路径通过 Vite 代理访问，实现同源
  const isDev = import.meta.env.DEV
  const basePath = `/${codeGenType}_${appId}/`
  
  if (isDev) {
    // 开发环境：使用相对路径，通过 Vite 代理
    const baseUrl = `/api/static${basePath}`
    // 如果是 Vue 项目，浏览地址需要添加 dist 后缀
    if (codeGenType === CodeGenTypeEnum.VUE_PROJECT) {
      return `${baseUrl}dist/index.html`
    }
    return baseUrl
  } else {
    // 生产环境：使用完整 URL
    const baseUrl = `${STATIC_BASE_URL}${basePath}`
    // 如果是 Vue 项目，浏览地址需要添加 dist 后缀
    if (codeGenType === CodeGenTypeEnum.VUE_PROJECT) {
      return `${baseUrl}dist/index.html`
    }
    return baseUrl
  }
}

