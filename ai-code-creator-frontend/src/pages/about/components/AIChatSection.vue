<template>
  <div class="w-full max-w-5xl space-y-8">
    <!-- AI 对话框 -->
    <div class="relative group">
      <!-- 3D效果背景 - 柔和的发光 -->
      <div class="absolute -inset-1 bg-gradient-to-r from-purple-600/30 via-pink-600/30 to-blue-600/30 rounded-2xl blur-2xl opacity-30 group-hover:opacity-50 transition-opacity animate-glow" />
      
      <!-- 主对话框 -->
      <div class="relative glass rounded-2xl p-8 border border-purple-500/20 shadow-lg">
        <!-- 装饰性扫描线 -->
        <div class="absolute top-0 left-0 right-0 h-1 bg-gradient-to-r from-transparent via-purple-400 to-transparent opacity-50 animate-scan" />
        
        <!-- 消息列表 -->
        <div ref="messagesContainer" class="h-80 overflow-y-auto space-y-4 mb-6 pr-2">
          <div
            v-for="(msg, index) in messages"
            :key="index"
            :class="['flex gap-4 animate-in', msg.role === 'user' ? 'justify-end' : 'justify-start']"
            :style="{ animationDelay: `${index * 0.1}s` }"
          >
            <!-- AI 消息 -->
            <div v-if="msg.role === 'ai'" class="flex gap-3 max-w-[80%]">
              <div class="w-10 h-10 rounded-full bg-gradient-to-br from-purple-500 to-pink-500 flex items-center justify-center flex-shrink-0 border border-purple-400/30">
                <Bot class="w-5 h-5 text-white" />
              </div>
              <div class="glass rounded-2xl p-4 border border-purple-500/20">
                <p class="text-purple-100 typing-effect">{{ msg.content }}</p>
              </div>
            </div>

            <!-- 用户消息 -->
            <div v-else class="flex gap-3 max-w-[80%]">
              <div class="glass rounded-2xl p-4 border border-blue-500/20 bg-gradient-to-br from-blue-900/30 to-purple-900/30">
                <p class="text-blue-100">{{ msg.content }}</p>
              </div>
              <div class="w-10 h-10 rounded-full bg-gradient-to-br from-blue-500 to-cyan-500 flex items-center justify-center flex-shrink-0 border border-blue-400/30">
                <User class="w-5 h-5 text-white" />
              </div>
            </div>
          </div>

          <!-- AI 输入中 -->
          <div v-if="isTyping" class="flex gap-3">
            <div class="w-10 h-10 rounded-full bg-gradient-to-br from-purple-500 to-pink-500 flex items-center justify-center animate-pulse">
              <Bot class="w-5 h-5 text-white" />
            </div>
            <div class="glass rounded-2xl p-4 border border-purple-500/30">
              <div class="flex gap-2">
                <div class="w-2 h-2 bg-purple-400 rounded-full animate-bounce" style="animation-delay: 0s" />
                <div class="w-2 h-2 bg-purple-400 rounded-full animate-bounce" style="animation-delay: 0.2s" />
                <div class="w-2 h-2 bg-purple-400 rounded-full animate-bounce" style="animation-delay: 0.4s" />
              </div>
            </div>
          </div>
        </div>

        <!-- 输入框 -->
        <div class="relative">
          <input
            v-model="inputText"
            @keydown.enter="sendMessage"
            type="text"
            placeholder="描述你想创建的项目..."
            class="w-full px-6 py-4 glass rounded-xl border border-purple-500/20 focus:border-purple-400 focus:border-opacity-50 outline-none text-white placeholder:text-purple-300/60 transition-all"
          />
          <button
            @click="sendMessage"
            class="absolute right-2 top-1/2 -translate-y-1/2 w-10 h-10 bg-gradient-to-r from-purple-500 to-pink-500 rounded-lg flex items-center justify-center hover:scale-110 transition-transform border border-purple-400/30"
          >
            <Send class="w-5 h-5 text-white" />
          </button>
        </div>
      </div>
    </div>

    <!-- 生成模式选择 -->
    <CodeGenTypeSelector v-model="selectedCodeGenType" />

    <!-- 快速提示词 -->
    <QuickPrompts @select="handlePromptSelect" />
  </div>
</template>

<script setup lang="ts">
import { ref, nextTick } from 'vue'
import { Bot, User, Send } from 'lucide-vue-next'
import QuickPrompts from './QuickPrompts.vue'
import CodeGenTypeSelector from './CodeGenTypeSelector.vue'

type CodeGenType = 'html' | 'multi_file' | 'vue_project'

interface Message {
  role: 'user' | 'ai'
  content: string
}

const emit = defineEmits<{
  (e: 'send-prompt', prompt: string): void
}>()

const inputText = ref('')
const isTyping = ref(false)
const messagesContainer = ref<HTMLElement | null>(null)
const selectedCodeGenType = ref<CodeGenType>('multi_file')

const messages = ref<Message[]>([
  {
    role: 'ai',
    content: '你好！我是AI代码助手，我可以帮你快速生成各种前端项目。告诉我你想要什么？'
  }
])

const sendMessage = () => {
  if (!inputText.value.trim()) return

  messages.value.push({
    role: 'user',
    content: inputText.value
  })

  emit('send-prompt', inputText.value)
  inputText.value = ''

  isTyping.value = true
  setTimeout(() => {
    isTyping.value = false
    messages.value.push({
      role: 'ai',
      content: '好的！我正在为你生成代码，请稍等...'
    })
    scrollToBottom()
  }, 1500)

  scrollToBottom()
}

const handlePromptSelect = (prompt: string) => {
  inputText.value = prompt
  // 只填充输入框，不发送消息
}

const scrollToBottom = () => {
  nextTick(() => {
    if (messagesContainer.value) {
      messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
    }
  })
}
</script>

<style scoped>
@keyframes animate-in {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.animate-in {
  animation: animate-in 0.5s ease-out forwards;
}
</style>
