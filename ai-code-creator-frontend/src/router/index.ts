import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '../stores/user'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'login',
      component: () => import('../pages/user/login/App.vue'),
      meta: { requiresAuth: false },
    },
    {
      path: '/home',
      name: 'home',
      component: () => import('../pages/home/App.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/about',
      name: 'about',
      component: () => import('../pages/about/App.vue'),
      meta: { requiresAuth: true },
    },
  ],
})

// 路由守卫
router.beforeEach(async (to, from, next) => {
  const userStore = useUserStore()
  
  // 清理页面切换时可能残留的样式类
  if (to.path === '/about') {
    // 进入about页面时，清理其他页面的样式类
    document.body.classList.remove('home-page-active')
    document.body.classList.remove('login-page-active')
    document.documentElement.classList.remove('login-page-active')
    // 清理内联样式
    document.body.style.background = ''
    document.body.style.backgroundColor = ''
    document.documentElement.style.background = ''
    document.documentElement.style.backgroundColor = ''
    // 清理sessionStorage（用户通过浏览器回退或直接访问about时）
    sessionStorage.removeItem('currentAppId')
    sessionStorage.removeItem('currentAppMessage')
  } else if (to.path === '/home') {
    // 进入home页面时，清理其他页面的样式类
    document.body.classList.remove('login-page-active')
    document.documentElement.classList.remove('login-page-active')
    // 检查是否有appId（从sessionStorage或query参数），如果没有则重定向
    const hasAppId = sessionStorage.getItem('currentAppId') || to.query.appId
    if (!hasAppId) {
      next({ path: '/about' })
      return
    }
  } else if (to.path === '/') {
    // 进入登录页面时，清理其他页面的样式类
    document.body.classList.remove('home-page-active')
    // 清理sessionStorage
    sessionStorage.removeItem('currentAppId')
    sessionStorage.removeItem('currentAppMessage')
  }
  
  // 如果路由需要认证
  if (to.meta.requiresAuth) {
    // 如果还没有检查过登录状态，先检查
    if (!userStore.hasCheckedLogin) {
      const isLoggedIn = await userStore.checkLoginStatus()
      if (!isLoggedIn) {
        // 未登录，跳转到登录页
        next({ path: '/', query: { redirect: to.fullPath } })
        return
      }
    } else if (!userStore.isLoggedIn) {
      // 已检查过但未登录，跳转到登录页
      next({ path: '/', query: { redirect: to.fullPath } })
      return
    }
  }
  
  // 如果访问登录页且已登录，跳转到 about 页
  if (to.path === '/' && userStore.isLoggedIn) {
    next({ path: '/about' })
    return
  }
  
  next()
})

export default router
