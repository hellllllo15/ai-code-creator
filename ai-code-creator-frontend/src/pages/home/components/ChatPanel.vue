<template>
  <div 
    class="h-full flex flex-col backdrop-blur-md border-r border-cyan-500/30"
    :style="{ 
      background: `linear-gradient(to bottom right, rgba(2, 6, 23, ${props.opacity}), rgba(30, 58, 138, ${props.opacity}))`
    }"
  >
    <!-- 头部 -->
    <div class="p-4 border-b border-cyan-500/30 bg-gradient-to-r from-cyan-500/10 to-blue-500/10">
      <div class="flex items-center gap-3">
        <div class="relative">
          <div class="w-10 h-10 rounded-lg bg-gradient-to-br from-cyan-500 to-blue-500 flex items-center justify-center">
            <MessageSquare class="w-5 h-5 text-white" />
          </div>
          <div class="absolute -top-1 -right-1 w-3 h-3 bg-green-500 rounded-full border-2 border-slate-950 animate-pulse" />
        </div>
        <div>
          <h2 class="font-semibold text-cyan-400">代码助手</h2>
          <p class="text-xs text-slate-400">在线</p>
        </div>
      </div>
    </div>

    <!-- 标签页 -->
    <div class="flex-1 flex flex-col overflow-hidden">
      <div class="mx-4 mt-4 bg-slate-900/50 border border-cyan-500/20 rounded-lg p-1 flex gap-1">
        <button
          @click="activeTab = 'chat'"
          :class="[
            'flex-1 px-4 py-2 rounded-md transition-all',
            activeTab === 'chat' 
              ? 'bg-cyan-500/20 text-cyan-400' 
              : 'text-slate-400 hover:text-slate-300'
          ]"
        >
          对话
        </button>
        <button
          @click="activeTab = 'history'"
          :class="[
            'flex-1 px-4 py-2 rounded-md transition-all',
            activeTab === 'history' 
              ? 'bg-cyan-500/20 text-cyan-400' 
              : 'text-slate-400 hover:text-slate-300'
          ]"
        >
          历史
        </button>
      </div>

      <!-- 对话标签内容 -->
      <div v-show="activeTab === 'chat'" class="flex-1 flex flex-col overflow-hidden p-4">
        <!-- 消息列表 -->
        <div class="flex-1 overflow-auto pr-4 mb-4">
          <div class="space-y-4">
            <div
              v-for="(message, index) in messages"
              :key="message.id"
              :class="['flex', message.role === 'user' ? 'justify-end' : 'justify-start']"
              :style="getMessageStyle(index)"
            >
              <div
                :class="[
                  'max-w-[80%] rounded-lg p-3',
                  message.role === 'user'
                    ? 'bg-gradient-to-br from-cyan-500 to-blue-500 text-white shadow-lg shadow-cyan-500/20'
                    : 'bg-slate-900/60 text-slate-200 border border-cyan-500/20'
                ]"
              >
                <p class="text-sm">{{ message.content }}</p>
                <span class="text-xs opacity-60 mt-1 block">
                  {{ formatTime(message.timestamp) }}
                </span>
              </div>
            </div>
          </div>
        </div>

        <!-- 输入框 -->
        <div class="relative">
          <div class="absolute inset-0 bg-gradient-to-r from-cyan-500/20 to-blue-500/20 rounded-lg blur-xl" />
          <div class="relative flex gap-2">
            <input
              v-model="inputValue"
              @keypress="handleKeyPress"
              placeholder="输入你的需求..."
              class="flex-1 px-4 py-2 bg-slate-900/60 border border-cyan-500/30 text-white placeholder:text-slate-500 focus:border-cyan-400 focus:ring-2 focus:ring-cyan-400/20 rounded-lg outline-none"
            />
            <button
              @click="handleSend"
              class="px-4 py-2 bg-gradient-to-r from-cyan-500 to-blue-500 hover:from-cyan-400 hover:to-blue-400 text-white shadow-lg shadow-cyan-500/20 rounded-lg transition-all"
            >
              <Send class="w-4 h-4" />
            </button>
          </div>
        </div>
      </div>

      <!-- 历史标签内容 -->
      <div v-show="activeTab === 'history'" class="flex-1 flex flex-col overflow-hidden p-4">
        <div class="flex items-center justify-between mb-4">
          <div class="flex items-center gap-2">
            <History class="w-4 h-4 text-cyan-400" />
            <h3 class="text-cyan-400">对话历史</h3>
          </div>
          <button
            @click="clearHistory"
            class="p-2 text-red-400 hover:text-red-300 hover:bg-red-500/10 rounded-lg transition-all"
          >
            <Trash2 class="w-4 h-4" />
          </button>
        </div>

        <div class="flex-1 overflow-auto">
          <div class="space-y-2">
            <div
              v-for="chat in chatHistory"
              :key="chat.id"
              @click="loadHistoryChat(chat.id)"
              class="p-3 rounded-lg bg-slate-900/40 border border-cyan-500/20 cursor-pointer hover:bg-slate-900/60 hover:border-cyan-400/40 transition-all hover:scale-[1.02]"
            >
              <h4 class="text-sm text-slate-200 mb-1">{{ chat.title }}</h4>
              <p class="text-xs text-slate-500 line-clamp-2 mb-2">{{ chat.lastMessage }}</p>
              <span class="text-xs text-cyan-400">
                {{ formatDate(chat.timestamp) }}
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import { Send, History, Trash2, MessageSquare } from 'lucide-vue-next';

interface Props {
  opacity?: number;
}

const props = withDefaults(defineProps<Props>(), {
  opacity: 0.15,  // 降低默认透明度，让背景更容易显示
});

interface Message {
  id: string;
  role: 'user' | 'ai';
  content: string;
  timestamp: Date;
}

interface ChatHistory {
  id: string;
  title: string;
  lastMessage: string;
  timestamp: Date;
}

// Props
const emit = defineEmits<{
  (e: 'generate-code', prompt: string): void;
}>();

// 状态
const activeTab = ref<'chat' | 'history'>('chat');
const messages = ref<Message[]>([
  {
    id: '1',
    role: 'ai',
    content: '你好！我可以帮你生成页面代码。请告诉我你想创建什么样的页面？',
    timestamp: new Date(),
  },
]);
const inputValue = ref('');
const chatHistory = ref<ChatHistory[]>([
  {
    id: '1',
    title: '科幻登录页面',
    lastMessage: '创建一个具有霓虹效果的登录页面...',
    timestamp: new Date(Date.now() - 3600000),
  },
  {
    id: '2',
    title: '仪表板设计',
    lastMessage: '设计一个数据可视化仪表板...',
    timestamp: new Date(Date.now() - 7200000),
  },
]);

// 方法
const handleSend = () => {
  if (!inputValue.value.trim()) return;

  const userMessage: Message = {
    id: Date.now().toString(),
    role: 'user',
    content: inputValue.value,
    timestamp: new Date(),
  };

  messages.value.push(userMessage);

  // 生成代码
  setTimeout(() => {
    const aiMessage: Message = {
      id: (Date.now() + 1).toString(),
      role: 'ai',
      content: '正在为你生成代码...',
      timestamp: new Date(),
    };
    messages.value.push(aiMessage);
    emit('generate-code', inputValue.value);
  }, 1000);

  inputValue.value = '';
};

const handleKeyPress = (e: KeyboardEvent) => {
  if (e.key === 'Enter' && !e.shiftKey) {
    e.preventDefault();
    handleSend();
  }
};

const clearHistory = () => {
  chatHistory.value = [];
};

const loadHistoryChat = (historyId: string) => {
  console.log('Loading chat:', historyId);
};

const formatTime = (date: Date) => {
  return date.toLocaleTimeString();
};

const formatDate = (date: Date) => {
  return date.toLocaleDateString();
};

const getMessageStyle = (index: number) => {
  return {
    opacity: 0,
    transform: 'translateY(20px)',
    animation: `fadeInUp 0.5s ease-out ${index * 0.1}s forwards`,
  };
};
</script>

<style scoped>
@keyframes fadeInUp {
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 滚动条样式 */
:deep(.overflow-auto::-webkit-scrollbar) {
  width: 6px;
}

:deep(.overflow-auto::-webkit-scrollbar-track) {
  background: rgba(15, 23, 42, 0.3);
  border-radius: 3px;
}

:deep(.overflow-auto::-webkit-scrollbar-thumb) {
  background: rgba(6, 182, 212, 0.3);
  border-radius: 3px;
}

:deep(.overflow-auto::-webkit-scrollbar-thumb:hover) {
  background: rgba(6, 182, 212, 0.5);
}
</style>
