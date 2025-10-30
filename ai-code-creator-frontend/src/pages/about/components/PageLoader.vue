<template>
  <Transition name="loader">
    <div v-if="show" class="fixed inset-0 z-50 bg-black flex items-center justify-center">
      <!-- Logo 展开动画 -->
      <div class="relative">
        <!-- 外圈旋转 -->
        <div class="absolute inset-0 w-32 h-32 border-4 border-transparent border-t-purple-500 border-r-pink-500 rounded-full animate-spin" />
        <div class="absolute inset-2 w-28 h-28 border-4 border-transparent border-b-blue-500 border-l-cyan-500 rounded-full animate-spin" style="animation-duration: 1.5s; animation-direction: reverse" />
        
        <!-- 中心Logo -->
        <div class="relative w-32 h-32 flex items-center justify-center">
          <div class="absolute inset-0 bg-gradient-to-r from-purple-600 to-blue-600 rounded-full blur-xl opacity-75 animate-pulse" />
          <div class="relative w-20 h-20 bg-gradient-to-br from-purple-500 to-blue-500 rounded-full flex items-center justify-center neon-border border-purple-400">
            <Sparkles class="w-10 h-10 text-white animate-pulse" />
          </div>
        </div>

        <!-- 加载文字 -->
        <div class="absolute -bottom-16 left-1/2 -translate-x-1/2 whitespace-nowrap">
          <p class="text-purple-400 text-xl font-bold animate-pulse">
            初始化 AI 引擎...
          </p>
        </div>
      </div>

      <!-- 背景粒子 -->
      <div class="absolute inset-0 overflow-hidden">
        <div
          v-for="i in 20"
          :key="i"
          class="absolute w-1 h-1 bg-purple-400 rounded-full animate-float"
          :style="{
            left: `${Math.random() * 100}%`,
            top: `${Math.random() * 100}%`,
            animationDelay: `${Math.random() * 2}s`,
            animationDuration: `${3 + Math.random() * 3}s`
          }"
        />
      </div>
    </div>
  </Transition>
</template>

<script setup lang="ts">
import { Sparkles } from 'lucide-vue-next'

interface Props {
  show: boolean
}

defineProps<Props>()

const emit = defineEmits<{
  (e: 'loaded'): void
}>()
</script>

<style scoped>
.loader-leave-active {
  transition: opacity 0.8s ease-out;
}

.loader-leave-to {
  opacity: 0;
}
</style>
