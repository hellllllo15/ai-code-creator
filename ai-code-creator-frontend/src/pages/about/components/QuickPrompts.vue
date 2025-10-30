<template>
  <div class="flex flex-wrap justify-center gap-4">
    <div
      v-for="(prompt, index) in prompts"
      :key="index"
      class="prompt-card"
      :style="{ animationDelay: `${index * 0.1}s` }"
      @click="selectPrompt(prompt)"
    >
      <div class="prompt-inner">
        <!-- 正面 -->
        <div class="prompt-front">
          <component :is="prompt.icon" class="w-8 h-8 mb-2 text-purple-200" />
          <p class="text-sm text-center">{{ prompt.text }}</p>
        </div>
        
        <!-- 背面 -->
        <div class="prompt-back">
          <Sparkles class="w-8 h-8 mb-2 text-white" />
          <p class="text-sm text-center">点击使用</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { LogIn, LayoutDashboard, FileText, ShoppingCart, MessageSquare, User, Sparkles } from 'lucide-vue-next'

const emit = defineEmits<{
  (e: 'select', prompt: string): void
}>()

const prompts = [
  { text: '创建登录页', icon: LogIn },
  { text: '生成仪表板', icon: LayoutDashboard },
  { text: '设计博客', icon: FileText },
  { text: '开发电商页面', icon: ShoppingCart },
  { text: '构建聊天应用', icon: MessageSquare },
  { text: '制作个人主页', icon: User },
]

const selectPrompt = (prompt: typeof prompts[0]) => {
  emit('select', prompt.text)
}
</script>

<style scoped>
.prompt-card {
  width: 150px;
  height: 120px;
  perspective: 1000px;
  cursor: pointer;
  opacity: 0;
  animation: slideUp 0.6s ease-out forwards;
}

.prompt-inner {
  position: relative;
  width: 100%;
  height: 100%;
  transition: transform 0.6s;
  transform-style: preserve-3d;
}

.prompt-card:hover .prompt-inner {
  transform: rotateY(180deg);
}

.prompt-front,
.prompt-back {
  position: absolute;
  width: 100%;
  height: 100%;
  backface-visibility: hidden;
  border-radius: 12px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 16px;
}

.prompt-front {
  background: rgba(139, 92, 246, 0.12);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(168, 85, 247, 0.3);
}

.prompt-back {
  background: linear-gradient(135deg, #a855f7, #ec4899);
  transform: rotateY(180deg);
  box-shadow: 0 0 20px rgba(168, 85, 247, 0.3);
}

.prompt-card:hover {
  transform: translateY(-5px);
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
