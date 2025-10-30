import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'login',
      component: () => import('../pages/user/login/App.vue'),
    },
    {
      path: '/home',
      name: 'home',
      component: () => import('../pages/home/App.vue'),
    },
    {
      path: '/about',
      name: 'about',
      component: () => import('../pages/about/App.vue'),
    },
  ],
})

export default router
