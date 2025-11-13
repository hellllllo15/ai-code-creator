import { ref, computed } from 'vue'
import { defineStore } from 'pinia'
import { getLoginUser, userLogout } from '@/api/userController'
import type { LoginUserVO } from '@/api/typings'

export const useUserStore = defineStore('user', () => {
  // 用户信息
  const userInfo = ref<LoginUserVO | null>(null)
  // 是否已检查登录状态
  const hasCheckedLogin = ref(false)
  // 是否正在检查登录状态
  const isCheckingLogin = ref(false)

  // 是否已登录
  const isLoggedIn = computed(() => {
    return userInfo.value !== null && userInfo.value.id !== undefined
  })

  // 设置用户信息
  function setUserInfo(user: LoginUserVO | null) {
    userInfo.value = user
  }

  // 清除用户信息
  function clearUserInfo() {
    userInfo.value = null
  }

  // 检查登录状态
  async function checkLoginStatus() {
    // 如果正在检查，避免重复请求
    if (isCheckingLogin.value) {
      return
    }

    // 如果已经检查过且已登录，直接返回
    if (hasCheckedLogin.value && isLoggedIn.value) {
      return true
    }

    isCheckingLogin.value = true
    try {
      const response = await getLoginUser()
      // 后端返回的响应包含 code、data、message
      // code === 0 表示成功，code === 40100 表示未登录
      if (response.code === 0 && response.data) {
        setUserInfo(response.data)
        hasCheckedLogin.value = true
        return true
      } else {
        // 如果返回码不是0（可能是 40100 未登录或其他错误），清除用户信息
        clearUserInfo()
        hasCheckedLogin.value = true
        return false
      }
    } catch (error: any) {
      // 处理网络错误或其他异常
      // 如果 error.response 存在，说明是 HTTP 错误响应
      if (error.response) {
        const errorData = error.response.data
        // 检查错误码是否为 40100 (NOT_LOGIN_ERROR)
        if (errorData?.code === 40100 || error.response.status === 401) {
          clearUserInfo()
          hasCheckedLogin.value = true
          return false
        }
      }
      // 其他错误也清除用户信息
      clearUserInfo()
      hasCheckedLogin.value = true
      return false
    } finally {
      isCheckingLogin.value = false
    }
  }

  // 退出登录
  async function logout() {
    try {
      await userLogout()
    } catch (error) {
      console.error('Logout error:', error)
    } finally {
      clearUserInfo()
      hasCheckedLogin.value = false
    }
  }

  return {
    userInfo,
    isLoggedIn,
    hasCheckedLogin,
    isCheckingLogin,
    setUserInfo,
    clearUserInfo,
    checkLoginStatus,
    logout,
  }
})

