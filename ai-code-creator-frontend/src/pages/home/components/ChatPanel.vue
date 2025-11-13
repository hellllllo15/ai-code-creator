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
        <div ref="messagesContainer" class="flex-1 overflow-auto pr-4 mb-4">
          <!-- 调试信息 -->
          <div v-if="messages.length === 0" class="text-center text-cyan-400/50 text-sm py-4">
            暂无消息 (messages.length = {{ messages.length }})
          </div>
          <div v-else class="space-y-4">
            <div
              v-for="(message, index) in messages"
              :key="message.id || `msg-${index}`"
              :class="[
                'message-wrapper',
                message.role === 'user' ? 'user-message-wrapper' : 'ai-message-wrapper'
              ]"
            >
              <!-- 头像 -->
              <div
                :class="[
                  'message-avatar',
                  message.role === 'user' ? 'user-avatar' : 'ai-avatar'
                ]"
              >
                <User v-if="message.role === 'user'" class="w-6 h-6" />
                <Bot v-else class="w-6 h-6" />
              </div>
              
              <!-- 消息气泡 -->
              <div
                :class="[
                  'message-bubble',
                  message.role === 'user' ? 'user-message' : 'ai-message'
                ]"
              >
                <template v-if="message.role === 'ai'">
                  <MarkdownRenderer v-if="message.content" :content="message.content" />
                </template>
                <template v-else>
                  <div class="user-message-text">
                    <span v-if="message.content">{{ message.content }}</span>
                  </div>
                </template>
                <div v-if="message.loading" class="flex items-center gap-2 text-cyan-400 mt-2">
                  <div class="w-4 h-4 border-2 border-cyan-400 border-t-transparent rounded-full animate-spin"></div>
                  <span class="text-xs">AI 正在思考...</span>
                </div>
                <div class="flex items-center gap-2 mt-2">
                  <span class="text-xs opacity-60">
                    {{ formatTime(message.timestamp) }}
                  </span>
                  <span 
                    v-if="message.id === currentStreamingMessageId && isStreaming"
                    class="text-xs text-cyan-400 animate-pulse"
                  >
                    ●
                  </span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 选中元素提示 -->
        <div v-if="selectedElementInfo" class="mb-2">
          <AAlert
            type="info"
            closable
            @close="handleRemoveSelectedElement"
            class="custom-alert"
          >
            <template #message>
              <div class="selected-element-info">
                <div class="element-header">
                  <span class="element-tag">
                    选中元素：{{ selectedElementInfo.tagName }}
                  </span>
                  <span v-if="selectedElementInfo.id" class="element-id">
                    #{{ selectedElementInfo.id }}
                  </span>
                  <span v-if="selectedElementInfo.className" class="element-class">
                    .{{ selectedElementInfo.className.split(' ').filter(c => c && !c.startsWith('visual-editor-')).join('.') }}
                  </span>
                </div>
                <div class="element-details">
                  <div v-if="selectedElementInfo.textContent" class="element-item">
                    <strong>内容:</strong> {{ selectedElementInfo.textContent.substring(0, 50) }}
                    {{ selectedElementInfo.textContent.length > 50 ? '...' : '' }}
                  </div>
                  <div v-if="selectedElementInfo.pagePath" class="element-item">
                    <strong>页面路径:</strong> {{ selectedElementInfo.pagePath }}
                  </div>
                  <div class="element-item">
                    <strong>选择器:</strong>
                    <code class="element-selector-code">{{ selectedElementInfo.selector }}</code>
                  </div>
                </div>
              </div>
            </template>
          </AAlert>
        </div>

        <!-- 输入框 -->
        <div class="relative">
          <div class="absolute inset-0 bg-gradient-to-r from-cyan-500/20 to-blue-500/20 rounded-lg blur-xl" />
          <div class="relative flex gap-2">
            <button
              @click="toggleEditMode"
              :class="[
                'px-3 py-2 rounded-lg transition-all',
                isEditMode 
                  ? 'bg-gradient-to-r from-purple-500 to-pink-500 hover:from-purple-400 hover:to-pink-400 text-white shadow-lg shadow-purple-500/20' 
                  : 'bg-slate-900/60 border border-cyan-500/30 text-cyan-400 hover:bg-slate-900/80 hover:border-cyan-400/50'
              ]"
              :title="isEditMode ? '退出编辑模式' : '进入编辑模式'"
            >
              <Edit class="w-4 h-4" />
            </button>
            <input
              v-model="inputValue"
              @keypress="handleKeyPress"
              placeholder="输入你的需求..."
              class="flex-1 px-4 py-2 bg-slate-900/60 border border-cyan-500/30 text-white placeholder:text-slate-500 focus:border-cyan-400 focus:ring-2 focus:ring-cyan-400/20 rounded-lg outline-none"
            />
            <button
              @click="handleSend"
              :disabled="!props.appId || isStreaming || !inputValue.trim()"
              :class="[
                'px-4 py-2 bg-gradient-to-r from-cyan-500 to-blue-500 hover:from-cyan-400 hover:to-blue-400 text-white shadow-lg shadow-cyan-500/20 rounded-lg transition-all',
                (!props.appId || isStreaming || !inputValue.trim()) && 'opacity-50 cursor-not-allowed'
              ]"
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
import { ref, computed, onMounted, watch, nextTick, onUnmounted } from 'vue';
import { Send, History, Trash2, MessageSquare, User, Bot, Edit } from 'lucide-vue-next';
import { Alert as AAlert, message } from 'ant-design-vue';
import request from '../../../request';
import { listAppChatHistory } from '../../../api/chatHistoryController';
// @ts-ignore
import MarkdownRenderer from '../../../components/MarkdownRenderer.vue';
import { injectVisualEditorScript, removeVisualEditorScript, type SelectedElementInfo } from '../../../utils/visualEditor';

// 定义聊天历史类型（注意：ID字段可能是字符串，因为响应拦截器转换过）
interface ChatHistoryItem {
  id?: number | string;
  message?: string;
  messageType?: string;
  appId?: number | string;
  userId?: number | string;
  parentId?: number | string | null;
  createTime?: string;
  updateTime?: string;
  isDelete?: number;
}

interface Props {
  opacity?: number;
  appId?: string | null;
  initialMessage?: string | null;
  previewPanelRef?: any; // PreviewPanel 组件的引用
}

const props = withDefaults(defineProps<Props>(), {
  opacity: 0.15,  // 降低默认透明度，让背景更容易显示
  appId: null,
  initialMessage: null,
});

interface Message {
  id: string;
  role: 'user' | 'ai';
  content: string;
  loading?: boolean;
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
  (e: 'code-generated'): void;
}>();

// 状态
const activeTab = ref<'chat' | 'history'>('chat');
const messages = ref<Message[]>([]);
const hasSentInitialMessage = ref(false); // 标记是否已发送初始消息
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

// SSE流式响应相关
const isStreaming = ref(false);
const currentStreamingMessageId = ref<string | null>(null);
let currentAbortController: AbortController | null = null;

// 可视化编辑相关
const isEditMode = ref(false);
const selectedElementInfo = ref<SelectedElementInfo | null>(null);
let cleanupVisualEditor: (() => void) | null = null;
let messageHandler: ((event: MessageEvent) => void) | null = null;

// 生成代码 - 使用 fetch + ReadableStream 处理流式响应（支持携带Cookie）
const generateCode = async (appId: string, userMessage: string, aiMessageIndex: number) => {
  let streamCompleted = false;
  let reader: ReadableStreamDefaultReader<Uint8Array> | null = null;

  try {
    // 获取 axios 配置的 baseURL
    const baseURL = request.defaults.baseURL || 'http://localhost:8123/api';

    // 手动构建URL，确保参数名正确（不使用URLSearchParams，避免appId被转为小写appid）
    const url = `${baseURL}/app/chat/gen/code?appId=${encodeURIComponent(appId)}&message=${encodeURIComponent(userMessage)}`;
    console.log('创建SSE连接:', url);

    // 创建AbortController用于取消请求
    const abortController = new AbortController();
    currentAbortController = abortController;

    // 使用 fetch 来处理 SSE，这样可以携带 Cookie
    const response = await fetch(url, {
      method: 'GET',
      credentials: 'include', // 携带 Cookie，用于 Session 认证
      headers: {
        'Accept': 'text/event-stream', // 明确指定接受 SSE 格式
      },
      signal: abortController.signal,
    });

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    if (!response.body) {
      throw new Error('响应体为空');
    }

    reader = response.body.getReader();
    const decoder = new TextDecoder();
    let buffer = '';
    let fullContent = '';
    let pendingEventType: string | null = null;

    // 读取流数据
    while (true) {
      const { done, value } = await reader.read();
      
      if (done) {
        break;
      }

      // 解码数据块
      buffer += decoder.decode(value, { stream: true });
      
      // 按行处理 SSE 数据
      const lines = buffer.split('\n');
      buffer = lines.pop() || ''; // 保留最后不完整的行

      for (let i = 0; i < lines.length; i++) {
        const line = lines[i];
        const trimmedLine = line.trim();
        
        // 跳过空行
        if (!trimmedLine) continue;

        // 处理 data: 行（支持 data: 和 data: 两种格式）
        if (trimmedLine.startsWith('data:')) {
          // 获取 data: 后面的内容（可能是 data: 或 data:）
          const colonIndex = trimmedLine.indexOf(':');
          const data = trimmedLine.slice(colonIndex + 1).trim();

          // 处理与事件关联的数据
          if (pendingEventType === 'business-error') {
            if (data) {
              try {
                const errorData = JSON.parse(data);
                const errorMessage = errorData.message || '生成过程中出现错误';
                if (messages.value[aiMessageIndex]) {
                  messages.value[aiMessageIndex].content = `❌ ${errorMessage}`;
                  messages.value[aiMessageIndex].loading = false;
                  scrollToBottom();
                }
                message.error(errorMessage);
              } catch (parseError) {
                console.error('解析错误事件失败:', parseError, '原始数据:', data);
                handleError(new Error('服务器返回错误'), aiMessageIndex);
              }
            }
            streamCompleted = true;
            isStreaming.value = false;
            currentStreamingMessageId.value = null;
            currentAbortController = null;
            pendingEventType = null;
            return;
          }

          // 空数据行（如最后的 data:）忽略
          if (!data || data === '') {
            pendingEventType = null;
            continue;
          }

          try {
            // 解析JSON包装的数据
            const parsed = JSON.parse(data);
            const content = parsed.d;

            // 拼接内容
            if (content !== undefined && content !== null) {
              fullContent += content;
              if (messages.value[aiMessageIndex]) {
                messages.value[aiMessageIndex].content = fullContent;
                messages.value[aiMessageIndex].loading = false;
                scrollToBottom();
              }
            }
          } catch (error) {
            console.error('解析SSE数据失败:', error, '原始数据:', data);
          }
          pendingEventType = null;
        } 
        // 处理 event: 行（支持 event: 和 event: 两种格式）
        else if (trimmedLine.startsWith('event:')) {
          const colonIndex = trimmedLine.indexOf(':');
          const eventType = trimmedLine.slice(colonIndex + 1).trim();

          pendingEventType = eventType;
          
          if (eventType === 'done') {
            // 确保内容已更新
            if (messages.value[aiMessageIndex]) {
              messages.value[aiMessageIndex].content = fullContent;
              messages.value[aiMessageIndex].loading = false;
              scrollToBottom();
            }
            streamCompleted = true;
            isStreaming.value = false;
            currentStreamingMessageId.value = null;
            currentAbortController = null;
            pendingEventType = null;
            // 触发代码生成完成事件，通知父组件刷新预览
            emit('code-generated');
            return;
          }
        }
      }
    }

    // 流正常结束
    streamCompleted = true;
    isStreaming.value = false;
    currentStreamingMessageId.value = null;
    currentAbortController = null;
    
    // 触发代码生成完成事件，通知父组件更新预览
    emit('code-generated');
  } catch (error: any) {
    // 如果是取消请求，不显示错误
    if (error.name === 'AbortError') {
      console.log('SSE连接已取消');
      return;
    }
    console.error('创建 SSE 连接失败：', error);
    handleError(error, aiMessageIndex);
  } finally {
    if (reader) {
      try {
        await reader.cancel();
      } catch (e) {
        // 忽略取消时的错误
      }
    }
    currentAbortController = null;
  }
};

// 错误处理函数
const handleError = (error: unknown, aiMessageIndex: number) => {
  console.error('生成代码失败：', error);
  if (messages.value[aiMessageIndex]) {
    messages.value[aiMessageIndex].content = '抱歉，生成过程中出现了错误，请重试。';
    messages.value[aiMessageIndex].loading = false;
  }
  isStreaming.value = false;
  currentStreamingMessageId.value = null;
  if (currentAbortController) {
    currentAbortController.abort();
    currentAbortController = null;
  }
};

// 方法：处理SSE流式响应
const handleStreamResponse = async (appId: string, message: string) => {
  if (!appId || isStreaming.value) {
    console.warn('无法发送消息:', { appId, isStreaming: isStreaming.value });
    return;
  }

  console.log('开始发送消息:', { appId, message });

  // 确保在chat标签页显示
  activeTab.value = 'chat';

  // 添加用户消息
  const userMessage: Message = {
    id: `user-${Date.now()}`,
    role: 'user',
    content: message.trim(),
    timestamp: new Date(),
  };
  messages.value.push(userMessage);

  // 创建AI响应消息（初始为空，流式填充）
  const aiMessageId = `ai-${Date.now()}`;
  const aiMessage: Message = {
    id: aiMessageId,
    role: 'ai',
    content: '',
    loading: true,
    timestamp: new Date(),
  };
  messages.value.push(aiMessage);
  currentStreamingMessageId.value = aiMessageId;
  isStreaming.value = true;

  await nextTick();
  scrollToBottom();

  // 获取AI消息的索引
  const aiMessageIndex = messages.value.findIndex(msg => msg.id === aiMessageId);
  if (aiMessageIndex !== -1) {
    // 开始生成代码（流式输出）
    await generateCode(appId, message.trim(), aiMessageIndex);
  } else {
    console.error('找不到AI消息索引');
    isStreaming.value = false;
    currentStreamingMessageId.value = null;
  }
};

// 加载对话历史
const loadChatHistory = async (appId: string) => {
  try {
    // 关键修复：直接传递字符串类型的appId
    // listAppChatHistory函数内部会将参数转换为字符串放入URL路径，避免精度丢失
    // 使用类型断言，因为API类型定义要求number，但实际URL需要字符串
    const response = await listAppChatHistory({
      appId: appId as any, // 传递字符串，API函数内部会转换为字符串
      pageSize: 50, // 加载最近50条消息
    });

    console.log('对话历史API响应:', response);
    console.log('响应类型:', typeof response);
    console.log('响应键:', response ? Object.keys(response) : 'null');

    // 响应拦截器返回的是 response.data，所以这里的 response 就是完整的 BaseResponse 结构
    // 数据结构应该是：{ code: 0, data: { records: [...], ... }, message: "ok" }
    // 但需要检查实际情况，因为响应拦截器可能已经提取了 data
    
    // 先检查是否是直接的 BaseResponse 结构
    let records: ChatHistoryItem[] = [];
    
    if (response && typeof response === 'object') {
      // 情况1：完整的 BaseResponse 结构 { code, data, message }
      if ('code' in response && 'data' in response) {
        const baseResponse = response as any;
        if (baseResponse.code === 0 && baseResponse.data && baseResponse.data.records) {
          records = baseResponse.data.records;
          console.log('情况1：BaseResponse结构，找到records:', records.length);
        }
      }
      // 情况2：已经是 data 部分（响应拦截器已提取）{ records, pageNumber, ... }
      else if ('records' in response) {
        records = (response as any).records || [];
        console.log('情况2：直接是PageChatHistory结构，找到records:', records.length);
      }
    }
    
    if (records.length > 0) {
      const historyMessages: Message[] = [];
      
      // 将历史记录转换为消息格式
      console.log('对话历史记录数量:', records.length);
      
      records.forEach((history: ChatHistoryItem, index: number) => {
        console.log(`处理第 ${index + 1} 条记录:`, history);
        if (history && history.message && history.messageType) {
          historyMessages.push({
            id: String(history.id || `${Date.now()}-${index}`),
            role: history.messageType === 'user' ? 'user' : 'ai',
            content: history.message,
            timestamp: history.createTime ? new Date(history.createTime) : new Date(),
          });
        }
      });

      console.log('转换后的消息数量:', historyMessages.length);

      // 按时间排序（最早的在前）
      historyMessages.sort((a, b) => a.timestamp.getTime() - b.timestamp.getTime());

      // 如果有历史消息，显示历史消息，否则显示欢迎消息
      if (historyMessages.length > 0) {
        console.log('设置消息列表:', historyMessages);
        console.log('当前messages.value长度:', messages.value.length);
        console.log('当前activeTab:', activeTab.value);
        // 使用响应式更新，确保Vue能检测到变化
        // 使用 reactive 包装或者直接赋值新数组
        messages.value.splice(0, messages.value.length, ...historyMessages);
        console.log('更新后messages.value长度:', messages.value.length);
        console.log('更新后messages.value内容:', messages.value);
        // 确保在chat标签页
        activeTab.value = 'chat';
        // 确保滚动到底部
        await nextTick();
        scrollToBottom();
      } else {
        console.log('没有历史消息，显示欢迎消息');
        messages.value = [
          {
            id: '1',
            role: 'ai',
            content: '你好！我可以帮你生成页面代码。请告诉我你想创建什么样的页面？',
            timestamp: new Date(),
          },
        ];
      }
    } else {
      console.warn('响应格式不正确或没有数据:', response);
      // 响应格式不正确，显示欢迎消息
      messages.value = [
        {
          id: '1',
          role: 'ai',
          content: '你好！我可以帮你生成页面代码。请告诉我你想创建什么样的页面？',
          timestamp: new Date(),
        },
      ];
    }
  } catch (error: any) {
    console.error('加载对话历史失败:', error);
    // 加载失败时显示欢迎消息
    messages.value = [
      {
        id: '1',
        role: 'ai',
        content: '你好！我可以帮你生成页面代码。请告诉我你想创建什么样的页面？',
        timestamp: new Date(),
      },
    ];
  }
};

// 监听props变化，处理两种情况：
// 1. 有appId和initialMessage：发送新消息
// 2. 只有appId没有initialMessage：加载对话历史
watch(
  () => [props.appId, props.initialMessage],
  ([appId, initialMessage]) => {
    if (!appId || hasSentInitialMessage.value || isStreaming.value) return;

    if (initialMessage) {
      // 情况1：有初始消息，发送新消息
      hasSentInitialMessage.value = true;
      handleStreamResponse(appId, initialMessage);
    } else {
      // 情况2：没有初始消息，加载对话历史
      hasSentInitialMessage.value = true; // 标记已处理，避免重复加载
      loadChatHistory(appId);
    }
  },
  { immediate: true }
);

onMounted(() => {
  // 组件挂载时再次检查（防止props在onMounted之后才传递的情况）
  if (!props.appId || hasSentInitialMessage.value || isStreaming.value) {
    if (!props.appId) {
      // 如果没有appId，显示欢迎消息
      messages.value = [
        {
          id: '1',
          role: 'ai',
          content: '你好！我可以帮你生成页面代码。请告诉我你想创建什么样的页面？',
          timestamp: new Date(),
        },
      ];
    }
    return;
  }

  if (props.initialMessage) {
    // 情况1：有初始消息，发送新消息
    hasSentInitialMessage.value = true;
    handleStreamResponse(props.appId, props.initialMessage);
  } else {
    // 情况2：没有初始消息，加载对话历史
    hasSentInitialMessage.value = true;
    loadChatHistory(props.appId);
  }
});

// 清理资源
onUnmounted(() => {
  // 退出编辑模式
  exitEditMode();
  
  // 组件卸载时取消正在进行的请求
  if (currentAbortController) {
    currentAbortController.abort();
    currentAbortController = null;
  }
  isStreaming.value = false;
  currentStreamingMessageId.value = null;
});

// 滚动到底部
const messagesContainer = ref<HTMLElement | null>(null);

const scrollToBottom = () => {
  nextTick(() => {
    if (messagesContainer.value) {
      messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight;
    }
  });
};

// 切换编辑模式
const toggleEditMode = () => {
  if (isEditMode.value) {
    // 退出编辑模式
    exitEditMode();
  } else {
    // 进入编辑模式
    enterEditMode();
  }
};

// 进入编辑模式
const enterEditMode = () => {
  if (!props.previewPanelRef) {
    console.warn('PreviewPanel 引用不可用');
    return;
  }

  // 获取 iframe
  const iframe = props.previewPanelRef.getIframe?.();
  if (!iframe) {
    console.warn('无法获取 iframe 引用');
    return;
  }

  // 等待 iframe 加载完成
  if (iframe.contentDocument && iframe.contentDocument.readyState === 'complete') {
    injectEditor();
  } else {
    iframe.addEventListener('load', injectEditor, { once: true });
  }
};

// 注入编辑器
const injectEditor = () => {
  if (!props.previewPanelRef) return;

  const iframe = props.previewPanelRef.getIframe?.();
  if (!iframe || !iframe.contentDocument) return;

  try {
    // 注入可视化编辑脚本
    cleanupVisualEditor = injectVisualEditorScript(iframe);
    isEditMode.value = true;

    // 监听 postMessage
    messageHandler = (event: MessageEvent) => {
      if (event.data && typeof event.data === 'object' && event.data.type === 'element-selected') {
        selectedElementInfo.value = event.data.payload as SelectedElementInfo;
        console.log('接收到选中的元素信息:', selectedElementInfo.value);
      }
    };

    window.addEventListener('message', messageHandler);
  } catch (error) {
    console.error('注入可视化编辑脚本失败:', error);
    alert('无法进入编辑模式，请确保预览页面已加载且与主页面同源');
  }
};

// 退出编辑模式
const exitEditMode = () => {
  // 先通知 iframe 禁用编辑模式
  if (props.previewPanelRef) {
    const iframe = props.previewPanelRef.getIframe?.();
    if (iframe && iframe.contentWindow) {
      try {
        iframe.contentWindow.postMessage({
          type: 'visual-editor-toggle',
          active: false
        }, '*');
      } catch (e) {
        console.warn('发送禁用编辑模式消息失败:', e);
      }
    }
  }

  // 清理可视化编辑脚本
  if (cleanupVisualEditor) {
    cleanupVisualEditor();
    cleanupVisualEditor = null;
  }

  // 移除消息监听器
  if (messageHandler) {
    window.removeEventListener('message', messageHandler);
    messageHandler = null;
  }

  isEditMode.value = false;
  selectedElementInfo.value = null;
};

// 移除选中的元素
const handleRemoveSelectedElement = () => {
  selectedElementInfo.value = null;
};

const handleSend = () => {
  if (!inputValue.value.trim() || !props.appId || isStreaming.value) return;

  let message = inputValue.value.trim();
  
  // 如果有选中的元素，将元素信息添加到提示词中
  if (selectedElementInfo.value) {
    const elementInfo = selectedElementInfo.value;
    let elementContext = `\n\n选中元素信息：`;
    
    // 页面路径（如果有）
    if (elementInfo.pagePath) {
      elementContext += `\n- 页面路径: ${elementInfo.pagePath}`;
    }
    
    // 标签和选择器
    elementContext += `\n- 标签: ${elementInfo.tagName.toLowerCase()}\n- 选择器: ${elementInfo.selector}`;
    
    // 当前内容（如果有，截取100字符）
    if (elementInfo.textContent) {
      elementContext += `\n- 当前内容: ${elementInfo.textContent.substring(0, 100)}`;
    }
    
    message += elementContext;
    
    // 清除选中元素并退出编辑模式
    selectedElementInfo.value = null;
    exitEditMode();
  }

  inputValue.value = '';
  
  // 调用流式接口
  handleStreamResponse(props.appId, message);
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
  // 移除动画，直接显示，避免动画问题导致消息不可见
  // 如果确实需要动画效果，可以在mounted后再添加
  return {};
};
</script>

<style scoped>
@keyframes fadeInUp {
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 消息容器布局 - 左右对话形式 */
.message-wrapper {
  display: flex;
  width: 100%;
  align-items: flex-start;
  gap: 12px;
}

.user-message-wrapper {
  justify-content: flex-end; /* 用户消息靠右 */
  flex-direction: row-reverse; /* 反转顺序：头像在右，气泡在左 */
}

.ai-message-wrapper {
  justify-content: flex-start; /* AI消息靠左 */
  flex-direction: row; /* 正常顺序：头像在左，气泡在右 */
}

/* 头像样式 */
.message-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
}

.user-avatar {
  background: linear-gradient(135deg, rgba(99, 102, 241, 0.9) 0%, rgba(139, 92, 246, 0.9) 100%);
  border: 2px solid rgba(99, 102, 241, 0.6);
  color: #ffffff;
}

.ai-avatar {
  background: linear-gradient(135deg, rgba(6, 182, 212, 0.9) 0%, rgba(139, 92, 246, 0.9) 100%);
  border: 2px solid rgba(6, 182, 212, 0.6);
  color: #ffffff;
}

/* 消息气泡样式 */
.message-bubble {
  max-width: 75%;
  padding: 12px 16px;
  border-radius: 12px;
  word-wrap: break-word;
  position: relative;
}

/* 用户消息样式 - 右侧，蓝紫色渐变 */
.user-message {
  background: linear-gradient(135deg, rgba(99, 102, 241, 0.3) 0%, rgba(139, 92, 246, 0.3) 100%);
  border: 1px solid rgba(99, 102, 241, 0.4);
  color: #e2e8f0;
  margin-left: auto;
}

.user-message-text {
  color: #e2e8f0;
  line-height: 1.6;
}

/* AI消息样式 - 左侧，深色背景 */
.ai-message {
  background: rgba(15, 23, 42, 0.6);
  border: 1px solid rgba(6, 182, 212, 0.3);
  color: #f1f5f9;
  margin-right: auto;
}

/* AI消息中的普通文字区域 - 有背景区分 */
.ai-message :deep(.markdown-body) {
  color: #f1f5f9;
}

/* 普通文字段落 - 有背景区分（直接子元素，不包括代码块内） */
.ai-message :deep(.markdown-body > p) {
  color: #f1f5f9;
  background: rgba(30, 41, 59, 0.4);
  padding: 8px 12px;
  border-radius: 6px;
  margin: 8px 0;
  border-left: 3px solid rgba(6, 182, 212, 0.5);
}

.ai-message :deep(.markdown-body > p:first-child) {
  margin-top: 0;
}

.ai-message :deep(.markdown-body > p:last-child) {
  margin-bottom: 0;
}

/* 标题样式 */
.ai-message :deep(.markdown-body h1),
.ai-message :deep(.markdown-body h2),
.ai-message :deep(.markdown-body h3),
.ai-message :deep(.markdown-body h4),
.ai-message :deep(.markdown-body h5),
.ai-message :deep(.markdown-body h6) {
  color: #e2e8f0;
  background: rgba(30, 41, 59, 0.5);
  padding: 8px 12px;
  border-radius: 6px;
  margin: 12px 0 8px 0;
  border-left: 3px solid rgba(6, 182, 212, 0.7);
}

.ai-message :deep(.markdown-body strong),
.ai-message :deep(.markdown-body b) {
  color: #ffffff;
  font-weight: 600;
}

/* 代码样式 - 更明显的背景区分 */
.ai-message :deep(.markdown-body code) {
  color: #06b6d4;
  background: rgba(6, 182, 212, 0.2);
  padding: 3px 8px;
  border-radius: 4px;
  font-family: 'Menlo', 'Monaco', 'Consolas', 'Courier New', monospace;
  border: 1px solid rgba(6, 182, 212, 0.3);
}

/* 代码块样式 - 非常明显的背景，与文字区分 */
.ai-message :deep(.markdown-body pre) {
  background: rgba(2, 6, 23, 0.9) !important;
  border: 1px solid rgba(6, 182, 212, 0.4);
  border-left: 4px solid rgba(6, 182, 212, 0.8);
  color: #e2e8f0;
  padding: 16px;
  border-radius: 8px;
  overflow-x: auto;
  font-size: 14px;
  margin: 12px 0;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
}

.ai-message :deep(.markdown-body pre code) {
  color: #e2e8f0;
  background: transparent !important;
  padding: 0;
  border: none;
}

/* 链接样式 */
.ai-message :deep(.markdown-body a) {
  color: #06b6d4;
  text-decoration: underline;
}

.ai-message :deep(.markdown-body a:hover) {
  color: #22d3ee;
}

/* 列表样式 */
.ai-message :deep(.markdown-body ul),
.ai-message :deep(.markdown-body ol) {
  color: #f1f5f9;
  background: rgba(30, 41, 59, 0.4);
  padding: 12px 12px 12px 32px;
  border-radius: 6px;
  margin: 8px 0;
  border-left: 3px solid rgba(6, 182, 212, 0.5);
}

.ai-message :deep(.markdown-body li) {
  color: #f1f5f9;
  margin: 4px 0;
}

/* 引用样式 */
.ai-message :deep(.markdown-body blockquote) {
  color: #cbd5e1;
  background: rgba(30, 41, 59, 0.4);
  padding: 12px 16px;
  border-radius: 6px;
  border-left: 4px solid rgba(6, 182, 212, 0.7);
  margin: 12px 0;
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

/* Ant Design Vue Alert 样式调整 */
:deep(.custom-alert) {
  background: rgba(15, 23, 42, 0.8);
  border: 1px solid rgba(6, 182, 212, 0.3);
}

:deep(.custom-alert .ant-alert-message) {
  color: #06b6d4;
}

:deep(.custom-alert .ant-alert-description) {
  color: #cbd5e1;
}

/* 选中元素信息样式 */
.selected-element-info {
  line-height: 1.6;
}

.element-header {
  margin-bottom: 8px;
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.element-tag {
  font-family: 'Monaco', 'Menlo', 'Consolas', monospace;
  font-size: 14px;
  font-weight: 600;
  color: #06b6d4;
}

.element-id {
  color: #22c55e;
  font-family: 'Monaco', 'Menlo', 'Consolas', monospace;
  font-size: 13px;
}

.element-class {
  color: #fbbf24;
  font-family: 'Monaco', 'Menlo', 'Consolas', monospace;
  font-size: 13px;
}

.element-details {
  margin-top: 8px;
  font-size: 12px;
}

.element-item {
  margin-bottom: 4px;
  color: #cbd5e1;
}

.element-item:last-child {
  margin-bottom: 0;
}

.element-item strong {
  color: #94a3b8;
  margin-right: 4px;
}

.element-selector-code {
  font-family: 'Monaco', 'Menlo', 'Consolas', monospace;
  background: rgba(15, 23, 42, 0.6);
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 11px;
  color: #fbbf24;
  border: 1px solid rgba(251, 191, 36, 0.3);
  word-break: break-all;
  display: inline-block;
  margin-left: 4px;
}
</style>
