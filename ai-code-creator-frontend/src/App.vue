<script setup lang="ts">
import { RouterView } from 'vue-router'
import { onMounted } from 'vue'
import { useUserStore } from './stores/user'

const userStore = useUserStore()

// 应用启动时检查登录状态
onMounted(async () => {
  // 如果还没有检查过登录状态，则检查
  if (!userStore.hasCheckedLogin) {
    await userStore.checkLoginStatus()
  }
})
</script>

<template>
  <RouterView />
</template>

<style>
/* 完全清空，让登录页自己控制样式 */
</style>
