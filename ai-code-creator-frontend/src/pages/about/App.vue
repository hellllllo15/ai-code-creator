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
import NavBar from './components/NavBar.vue'
import AIChatSection from './components/AIChatSection.vue'
import ProjectShowcase from './components/ProjectShowcase.vue'
import BackgroundEffects from './components/effects/BackgroundEffects.vue'
import ParticleSystem from './components/effects/ParticleSystem.vue'
import ScanLines from './components/effects/ScanLines.vue'
import HolographicGrid from './components/effects/HolographicGrid.vue'
import PageLoader from './components/PageLoader.vue'
import './style.css'

const isLoading = ref(true)

onMounted(() => {
  setTimeout(() => {
    isLoading.value = false
  }, 2000)
})

const handleSendPrompt = (prompt: string) => {
  console.log('发送提示:', prompt)
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
