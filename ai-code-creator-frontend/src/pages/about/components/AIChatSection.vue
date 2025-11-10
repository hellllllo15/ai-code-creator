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
                <p v-if="msg.content" class="text-purple-100 typing-effect">{{ msg.content }}</p>
                <div v-if="msg.loading" class="flex gap-2">
                  <div class="w-2 h-2 bg-purple-400 rounded-full animate-bounce" style="animation-delay: 0s" />
                  <div class="w-2 h-2 bg-purple-400 rounded-full animate-bounce" style="animation-delay: 0.2s" />
                  <div class="w-2 h-2 bg-purple-400 rounded-full animate-bounce" style="animation-delay: 0.4s" />
                </div>
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
import { ref, nextTick, onUnmounted } from 'vue'
import { Bot, User, Send } from 'lucide-vue-next'
import QuickPrompts from './QuickPrompts.vue'
import CodeGenTypeSelector from './CodeGenTypeSelector.vue'
import { createApp } from '../../../api/appController'
import request from '../../../request'

type CodeGenType = 'html' | 'multi_file' | 'vue_project'

interface Message {
  role: 'user' | 'ai'
  content: string
  loading?: boolean
}

const emit = defineEmits<{
  (e: 'send-prompt', prompt: string, codeGenType: CodeGenType): void
}>()

const inputText = ref('')
const isTyping = ref(false)
const isStreaming = ref(false)
const messagesContainer = ref<HTMLElement | null>(null)
const selectedCodeGenType = ref<CodeGenType>('multi_file')
const currentAppId = ref<string | null>(null)
let currentEventSource: EventSource | null = null

const messages = ref<Message[]>([
  {
    role: 'ai',
    content: '你好！我是AI代码助手，我可以帮你快速生成各种前端项目。告诉我你想要什么？'
  }
])

const sendMessage = async () => {
  if (!inputText.value.trim() || isStreaming.value) return

  const prompt = inputText.value.trim()
  const userMessage: Message = {
    role: 'user',
    content: prompt
  }
  messages.value.push(userMessage)
  inputText.value = ''

  scrollToBottom()

  try {
    // 如果还没有应用ID，只触发父组件事件，让父组件统一处理创建应用和跳转
    if (!currentAppId.value) {
      isTyping.value = true
      
      // 触发父组件事件，让父组件创建应用并跳转
      // 不要在子组件中创建应用，避免重复创建
      emit('send-prompt', prompt, selectedCodeGenType.value)
      
      isTyping.value = false
    } else {
      // 如果已有应用ID，直接开始流式对话
      await startStreamChat(prompt)
    }
  } catch (error: any) {
    isTyping.value = false
    console.error('发送消息失败:', error)
    messages.value.push({
      role: 'ai',
      content: `❌ 错误: ${error.message || '发送消息失败，请重试'}`
    })
    scrollToBottom()
  }
}

const startStreamChat = async (userMessage: string) => {
  if (!currentAppId.value || isStreaming.value) return

  // 创建AI响应消息（初始为空，流式填充）
  const aiMessage: Message = {
    role: 'ai',
    content: '',
    loading: true
  }
  messages.value.push(aiMessage)
  isStreaming.value = true
  
  await nextTick()
  scrollToBottom()

  const aiMessageIndex = messages.value.length - 1
  let eventSource: EventSource | null = null
  let streamCompleted = false

  try {
    // 使用 request 中的 baseURL 配置（与 appController 一致）
    const baseURL = request.defaults.baseURL || 'http://localhost:8123/api'

    // 构建URL参数
    const params = new URLSearchParams({
      appId: currentAppId.value,
      message: userMessage,
    })

    const url = `${baseURL}/app/chat/gen/code?${params.toString()}`

    // 创建 EventSource 连接（注意：标准 EventSource 不支持配置对象参数）
    eventSource = new EventSource(url)
    currentEventSource = eventSource

    let fullContent = ''

    // 处理接收到的消息
    eventSource.onmessage = function (event) {
      if (streamCompleted) return

      try {
        // 解析JSON包装的数据
        const parsed = JSON.parse(event.data)
        const content = parsed.d

        // 拼接内容
        if (content !== undefined && content !== null) {
          fullContent += content
          messages.value[aiMessageIndex].content = fullContent
          messages.value[aiMessageIndex].loading = false
          scrollToBottom()
        }
      } catch (error) {
        console.error('解析消息失败:', error)
        handleStreamError(error, aiMessageIndex)
      }
    }

    // 处理done事件
    eventSource.addEventListener('done', function () {
      if (streamCompleted) return

      streamCompleted = true
      isStreaming.value = false
      messages.value[aiMessageIndex].loading = false
      eventSource?.close()
      currentEventSource = null
    })

    // 处理business-error事件（后端限流等错误）
    eventSource.addEventListener('business-error', function (event: MessageEvent) {
      if (streamCompleted) return

      try {
        const errorData = JSON.parse(event.data)
        console.error('SSE业务错误事件:', errorData)

        const errorMessage = errorData.message || '生成过程中出现错误'
        messages.value[aiMessageIndex].content = `❌ ${errorMessage}`
        messages.value[aiMessageIndex].loading = false

        streamCompleted = true
        isStreaming.value = false
        eventSource?.close()
        currentEventSource = null
      } catch (parseError) {
        console.error('解析错误事件失败:', parseError, '原始数据:', event.data)
        handleStreamError(new Error('服务器返回错误'), aiMessageIndex)
      }
    })

    // 处理错误
    eventSource.onerror = function () {
      if (streamCompleted || !isStreaming.value) return
      
      // 检查是否是正常的连接关闭
      if (eventSource?.readyState === EventSource.CLOSED || 
          eventSource?.readyState === EventSource.CONNECTING) {
        // 连接关闭或正在连接中，可能是正常结束
        if (messages.value[aiMessageIndex].content.trim() === '') {
          // 如果没有接收到任何内容就关闭了，可能是错误
          handleStreamError(new Error('连接意外关闭'), aiMessageIndex)
        } else {
          streamCompleted = true
          isStreaming.value = false
          messages.value[aiMessageIndex].loading = false
          eventSource?.close()
          currentEventSource = null
        }
      } else {
        handleStreamError(new Error('SSE连接错误'), aiMessageIndex)
      }
    }
  } catch (error) {
    console.error('创建 EventSource 失败：', error)
    handleStreamError(error, aiMessageIndex)
  }
}

const handleStreamError = (error: unknown, aiMessageIndex: number) => {
  console.error('流式响应错误：', error)
  if (messages.value[aiMessageIndex]) {
    if (messages.value[aiMessageIndex].content.trim() === '') {
      messages.value[aiMessageIndex].content = '抱歉，生成过程中出现了错误，请重试。'
    }
    messages.value[aiMessageIndex].loading = false
  }
  isStreaming.value = false
  if (currentEventSource) {
    currentEventSource.close()
    currentEventSource = null
  }
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

// 组件卸载时清理 EventSource
onUnmounted(() => {
  if (currentEventSource) {
    currentEventSource.close()
    currentEventSource = null
  }
})
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
