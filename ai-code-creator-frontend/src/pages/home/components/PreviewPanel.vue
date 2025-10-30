<template>
  <div 
    class="h-full flex flex-col backdrop-blur-md"
    :style="{ 
      background: `linear-gradient(to bottom right, rgba(2, 6, 23, ${props.opacity}), rgba(55, 48, 163, ${props.opacity}))`
    }"
  >
    <!-- 头部工具栏 -->
    <div class="p-4 border-b border-purple-500/30 bg-gradient-to-r from-purple-500/10 to-pink-500/10">
      <div class="flex items-center justify-between">
        <div class="flex items-center gap-2">
          <div class="w-8 h-8 rounded-lg bg-gradient-to-br from-purple-500 to-pink-500 flex items-center justify-center">
            <Eye class="w-4 h-4 text-white" />
          </div>
          <h2 class="font-semibold text-purple-400">预览面板</h2>
        </div>

        <div class="flex items-center gap-2">
          <button
            @click="handleCopyCode"
            class="p-2 text-purple-400 hover:text-purple-300 hover:bg-purple-500/10 rounded-lg transition-all"
          >
            <Check v-if="copied" class="w-4 h-4" />
            <Copy v-else class="w-4 h-4" />
          </button>
          <button
            @click="handleDownload"
            class="p-2 text-purple-400 hover:text-purple-300 hover:bg-purple-500/10 rounded-lg transition-all"
          >
            <Download class="w-4 h-4" />
          </button>
        </div>
      </div>
    </div>

    <!-- 视图切换 -->
    <div class="flex-1 flex flex-col overflow-hidden">
      <div class="mx-4 mt-4 bg-slate-900/50 border border-purple-500/20 rounded-lg p-1 flex gap-1">
        <button
          @click="activeView = 'preview'"
          :class="[
            'flex-1 px-4 py-2 rounded-md transition-all flex items-center justify-center gap-2',
            activeView === 'preview' 
              ? 'bg-purple-500/20 text-purple-400' 
              : 'text-slate-400 hover:text-slate-300'
          ]"
        >
          <Eye class="w-4 h-4" />
          预览
        </button>
        <button
          @click="activeView = 'code'"
          :class="[
            'flex-1 px-4 py-2 rounded-md transition-all flex items-center justify-center gap-2',
            activeView === 'code' 
              ? 'bg-purple-500/20 text-purple-400' 
              : 'text-slate-400 hover:text-slate-300'
          ]"
        >
          <Code class="w-4 h-4" />
          源码
        </button>
      </div>

      <!-- 预览视图 -->
      <div v-show="activeView === 'preview'" class="flex-1 m-4 mt-4 overflow-hidden">
        <div
          class="h-full rounded-lg border border-purple-500/30 bg-white/95 shadow-2xl shadow-purple-500/20 overflow-hidden"
          :style="previewStyle"
        >
          <!-- 浏览器工具栏模拟 -->
          <div class="h-10 bg-slate-900 border-b border-purple-500/30 flex items-center px-4 gap-2">
            <div class="flex gap-2">
              <div class="w-3 h-3 rounded-full bg-red-500" />
              <div class="w-3 h-3 rounded-full bg-yellow-500" />
              <div class="w-3 h-3 rounded-full bg-green-500" />
            </div>
            <div class="flex-1 ml-4 h-6 bg-slate-800 rounded px-3 flex items-center text-xs text-slate-400">
              localhost:3000
            </div>
          </div>

          <!-- 预览内容 -->
          <div class="h-[calc(100%-2.5rem)] bg-white p-8 overflow-auto">
            <div v-if="generatedCode" class="space-y-4">
              <div class="text-center space-y-2">
                <div class="inline-block px-4 py-2 bg-gradient-to-r from-purple-500 to-pink-500 text-white rounded-lg shadow-lg">
                  生成的页面预览
                </div>
                <p class="text-slate-600">这是根据你的需求生成的页面</p>
              </div>

              <div class="grid grid-cols-3 gap-4 mt-8">
                <div
                  v-for="i in 3"
                  :key="i"
                  class="aspect-video bg-gradient-to-br from-purple-100 to-pink-100 rounded-lg flex items-center justify-center text-purple-600"
                >
                  组件 {{ i }}
                </div>
              </div>

              <div class="mt-8 p-6 bg-slate-50 rounded-lg">
                <h3 class="text-lg mb-2 text-slate-800">示例内容</h3>
                <p class="text-slate-600">
                  这是一个演示预览。实际应用中，这里会显示根据AI生成的代码渲染的真实页面。
                </p>
              </div>
            </div>

            <div v-else class="h-full flex items-center justify-center">
              <div class="text-center space-y-4">
                <div class="w-20 h-20 mx-auto bg-gradient-to-br from-purple-500/20 to-pink-500/20 rounded-full flex items-center justify-center">
                  <Eye class="w-10 h-10 text-purple-400" />
                </div>
                <div>
                  <h3 class="text-lg text-slate-800 mb-2">暂无内容</h3>
                  <p class="text-slate-500 text-sm">
                    在左侧对话框中输入需求，AI将为你生成页面
                  </p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 代码视图 -->
      <div v-show="activeView === 'code'" class="flex-1 m-4 mt-4 overflow-hidden">
        <div
          class="h-full rounded-lg border border-purple-500/30 bg-slate-950/70 shadow-2xl shadow-purple-500/20 overflow-hidden"
          :style="codeStyle"
        >
          <div class="h-full overflow-auto">
            <pre v-if="generatedCode" class="p-6 text-sm"><code class="text-green-400 font-mono">{{ generatedCode }}</code></pre>
            
            <div v-else class="h-full flex items-center justify-center p-8">
              <div class="text-center space-y-4">
                <div class="w-20 h-20 mx-auto bg-gradient-to-br from-purple-500/20 to-pink-500/20 rounded-full flex items-center justify-center">
                  <Code class="w-10 h-10 text-purple-400" />
                </div>
                <div>
                  <h3 class="text-lg text-slate-200 mb-2">暂无代码</h3>
                  <p class="text-slate-400 text-sm">
                    在左侧对话框中输入需求，AI将为你生成代码
                  </p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 底部状态栏 -->
    <div class="p-3 border-t border-purple-500/30 bg-slate-950/50">
      <div class="flex items-center justify-between text-xs">
        <div class="flex items-center gap-4">
          <span class="text-slate-400">
            状态: <span class="text-green-400">就绪</span>
          </span>
          <span class="text-slate-400">
            语言: <span class="text-purple-400">TypeScript</span>
          </span>
        </div>
        <div class="flex items-center gap-2">
          <div class="w-2 h-2 bg-green-500 rounded-full animate-pulse" />
          <span class="text-slate-400">实时预览</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import { Code, Eye, Download, Copy, Check } from 'lucide-vue-next';

interface Props {
  generatedCode: string;
  opacity?: number;
}

const props = withDefaults(defineProps<Props>(), {
  opacity: 0.15,  // 降低默认透明度，让背景更容易显示
});

const copied = ref(false);
const activeView = ref<'preview' | 'code'>('preview');

const handleCopyCode = () => {
  navigator.clipboard.writeText(props.generatedCode);
  copied.value = true;
  setTimeout(() => {
    copied.value = false;
  }, 2000);
};

const handleDownload = () => {
  const blob = new Blob([props.generatedCode], { type: 'text/plain' });
  const url = URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.download = 'generated-code.vue';
  a.click();
  URL.revokeObjectURL(url);
};

// 动画样式
const previewStyle = computed(() => ({
  opacity: 0,
  transform: 'scale(0.95)',
  animation: 'fadeInScale 0.5s ease-out forwards',
}));

const codeStyle = computed(() => ({
  opacity: 0,
  transform: 'scale(0.95)',
  animation: 'fadeInScale 0.5s ease-out forwards',
}));
</script>

<style scoped>
@keyframes fadeInScale {
  to {
    opacity: 1;
    transform: scale(1);
  }
}

/* 滚动条样式 */
:deep(.overflow-auto::-webkit-scrollbar) {
  width: 8px;
  height: 8px;
}

:deep(.overflow-auto::-webkit-scrollbar-track) {
  background: rgba(15, 23, 42, 0.3);
  border-radius: 4px;
}

:deep(.overflow-auto::-webkit-scrollbar-thumb) {
  background: rgba(168, 85, 247, 0.3);
  border-radius: 4px;
}

:deep(.overflow-auto::-webkit-scrollbar-thumb:hover) {
  background: rgba(168, 85, 247, 0.5);
}

/* 代码块样式 */
pre {
  margin: 0;
  white-space: pre-wrap;
  word-wrap: break-word;
}

code {
  font-family: 'Courier New', Courier, monospace;
}
</style>
