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
import { ref, onMounted } from 'vue'
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

onMounted(() => {
  setTimeout(() => {
    isLoading.value = false
  }, 2000)
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
      
      // 跳转到home页面，传递应用ID作为查询参数（字符串形式）
      router.push({
        path: '/home',
        query: {
          appId: appIdStr,
          message: prompt, // 同时传递初始消息
        },
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

#app {
  margin: 0 !important;
  padding: 0 !important;
}
</style>
