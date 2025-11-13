<template>
  <div class="min-h-screen relative overflow-hidden bg-gradient-to-br from-slate-900 via-purple-900 to-slate-900">
    <!-- 背景特效层 -->
    <BackgroundEffects />
    <ParticleSystem />
    <ScanLines />
    <HolographicGrid />
    
    <!-- 主内容 -->
    <div class="relative z-10">
      <!-- 顶部导航 -->
      <NavBar />
      
      <!-- 主要内容区 -->
      <main class="container mx-auto px-6">
        <!-- 上半部分：AI对话 + 快速提示词 -->
        <section class="min-h-[60vh] flex flex-col items-center justify-center py-12">
          <AIChatSection @send-prompt="handleSendPrompt" />
        </section>

        <!-- 下半部分：项目展示 -->
        <section class="min-h-[40vh] py-12">
          <ProjectShowcase />
        </section>
      </main>

      <!-- 页面底部 -->
      <footer class="relative z-10 text-center py-8 text-purple-200/70 text-sm">
        <p>© 2024 AI Code Platform. Powered by Advanced AI Technology.</p>
      </footer>
    </div>

    <!-- 页面加载动画 -->
    <PageLoader :show="isLoading" @loaded="isLoading = false" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeMount, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import NavBar from './components/NavBar.vue'
import AIChatSection from './components/AIChatSection.vue'
import ProjectShowcase from './components/ProjectShowcase.vue'
import BackgroundEffects from './components/effects/BackgroundEffects.vue'
import ParticleSystem from './components/effects/ParticleSystem.vue'
import ScanLines from './components/effects/ScanLines.vue'
import HolographicGrid from './components/effects/HolographicGrid.vue'
import PageLoader from './components/PageLoader.vue'
import { createApp } from '../../api/appController'
import './style.css'

const router = useRouter()
const isLoading = ref(true)

// 清理其他页面的样式类，确保about页面的样式正确
onBeforeMount(() => {
  // 移除其他页面可能添加的样式类
  document.body.classList.remove('home-page-active')
  document.body.classList.remove('login-page-active')
  document.documentElement.classList.remove('login-page-active')
  
  // 确保body和html的背景样式正确
  document.body.style.background = ''
  document.body.style.backgroundColor = ''
  document.documentElement.style.background = ''
  document.documentElement.style.backgroundColor = ''
})

onMounted(() => {
  // 再次确保样式类已清理
  document.body.classList.remove('home-page-active')
  document.body.classList.remove('login-page-active')
  document.documentElement.classList.remove('login-page-active')
  
  // 确保背景样式正确
  document.body.style.background = ''
  document.body.style.backgroundColor = ''
  document.documentElement.style.background = ''
  document.documentElement.style.backgroundColor = ''
  
  setTimeout(() => {
    isLoading.value = false
  }, 2000)
})

onUnmounted(() => {
  // 清理时也确保样式正确
  document.body.classList.remove('home-page-active')
  document.body.classList.remove('login-page-active')
  document.documentElement.classList.remove('login-page-active')
})

type CodeGenType = 'html' | 'multi_file' | 'vue_project'

const handleSendPrompt = async (prompt: string, codeGenType: CodeGenType) => {
  try {
    // 调用创建应用接口
    const response = await createApp({
      initPrompt: prompt,
      codeGenType: codeGenType,
      appName: prompt.substring(0, 20) || '我的应用', // 使用提示词的前20个字符作为应用名称
    })

    if (response.code === 0 && response.data) {
      // 将Long类型转换为字符串，避免精度丢失
      const appIdStr = String(response.data)
      
      // 将appId和message存储到sessionStorage，不在URL中显示
      sessionStorage.setItem('currentAppId', appIdStr)
      if (prompt) {
        sessionStorage.setItem('currentAppMessage', prompt)
      }
      
      // 跳转到home页面，使用push保留历史记录，方便浏览器回退
      router.push({
        path: '/home',
      })
    } else {
      console.error('创建应用失败:', response.message)
      alert(response.message || '创建应用失败')
    }
  } catch (error: any) {
    console.error('创建应用错误:', error)
    alert(error.message || '创建应用失败，请重试')
  }
}
</script>

<style>
/* About页面全局样式 */
html, body {
  margin: 0 !important;
  padding: 0 !important;
}

/* 确保about页面时body背景正确 */
body:not(.home-page-active):not(.login-page-active) {
  background: linear-gradient(to bottom right, #0f172a, #581c87, #0f172a) !important;
  background-color: #0f172a !important;
}

#app {
  margin: 0 !important;
  padding: 0 !important;
}
</style>
