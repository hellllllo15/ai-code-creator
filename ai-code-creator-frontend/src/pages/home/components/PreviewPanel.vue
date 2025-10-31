<template>
  <div class="h-full w-full flex flex-col">
    <div class="flex items-center justify-end p-2">
      <button class="bg-pink-500 text-white px-3 py-1 rounded" @click="$emit('toggle-no-preview')">
        {{ noPreviewMode ? '恢复预览页面' : '模拟无预览页面' }}
      </button>
    </div>
    <div class="flex-1 w-full flex flex-col">
      <div v-if="!noPreviewMode && previewUrl" class="flex-1 min-h-[150px] flex flex-col rounded-lg bg-white shadow-2xl shadow-purple-500/20 overflow-hidden">
        <!-- 顶部工具栏 -->
        <div class="h-10 min-h-[2.5rem] bg-slate-900 border-b border-purple-500/30 flex items-center px-4 gap-2">
          <div class="flex gap-2">
            <div class="w-3 h-3 rounded-full bg-red-500" />
            <div class="w-3 h-3 rounded-full bg-yellow-500" />
            <div class="w-3 h-3 rounded-full bg-green-500" />
          </div>
          <div class="flex-1 ml-4 h-6 bg-slate-800 rounded px-3 flex items-center text-xs text-slate-400 truncate">
            {{ previewUrl || 'localhost:3000' }}
          </div>
          <button
            @click="handleOpenInNewTab"
            v-if="previewUrl"
            class="ml-2 px-3 py-1 text-xs text-purple-400 hover:text-purple-300 hover:bg-purple-500/10 rounded transition-all"
          >
            新窗口打开
          </button>
        </div>
        <div class="flex-1 min-h-[100px] bg-white overflow-hidden flex flex-col">
          <iframe
            ref="previewIframe"
            :src="iframeSrc"
            class="w-full h-full min-h-[100px] border border-green-300"
            style="display:block; background:#fff"
            frameborder="0"
            @load="onIframeLoad"
            @error="onIframeError"
          ></iframe>
        </div>
      </div>
      <div v-else class="flex-1 w-full flex items-center justify-center bg-transparent min-h-[320px]">
        <span class="text-2xl text-gray-400 font-bold">正在加载...</span>
      </div>
    </div>

    <!-- 源码tab表现原样 -->
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
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, shallowRef } from 'vue';
import { Code, Eye, Download, Copy, Check } from 'lucide-vue-next';
import request from '../../../request';

interface Props {
  generatedCode?: string;
  previewUrl?: string;
  appId?: string | null;
  opacity?: number;
  codeGenType?: string;
  debug?: boolean;
  noPreviewMode?: boolean; // 新增 props
}
const props = withDefaults(defineProps<Props>(), {
  opacity: 0.15,
  noPreviewMode: false, // 默认值
});
const noPreviewMode = computed(() => props.noPreviewMode);

const copied = ref(false);
const activeView = ref<'preview' | 'code'>('preview');
const diagnosticsOpen = ref(true);

// 调试用：模拟无预览页面模式
const prevPreviewUrl = shallowRef<string|undefined|null>(undefined);
const toggleNoPreview = () => {
  if (!noPreviewMode.value) {
    prevPreviewUrl.value = props.previewUrl;
    // 注意：双向props不可直接改，采用响应式变量控制实际渲染
    // noPreviewMode.value = true; // 移除此行，由父组件控制
  } else {
    // noPreviewMode.value = false; // 移除此行，由父组件控制
  }
}

// iframe引用
const previewIframe = ref<HTMLIFrameElement | null>(null);

// iframe src（带时间戳参数，用于强制刷新）
const iframeTimestamp = ref<number>(Date.now());
const iframeSrc = computed(() => {
  if (!props.previewUrl) return '';
  // 添加时间戳参数强制刷新，避免缓存
  try {
    // 如果是完整URL（包含协议），直接使用
    if (props.previewUrl.startsWith('http://') || props.previewUrl.startsWith('https://')) {
      const url = new URL(props.previewUrl);
      url.searchParams.set('_t', String(iframeTimestamp.value));
      return url.toString();
    } else {
      // 如果是相对路径，使用window.location作为base
      const url = new URL(props.previewUrl, window.location.origin);
      url.searchParams.set('_t', String(iframeTimestamp.value));
      return url.toString();
    }
  } catch (e) {
    // 如果URL解析失败，直接添加时间戳参数（简单方式）
    const separator = props.previewUrl.includes('?') ? '&' : '?';
    return `${props.previewUrl}${separator}_t=${iframeTimestamp.value}`;
  }
});

// 诊断相关状态
type UrlCheckState = 'idle' | 'checking' | 'ok' | 'fail';
const urlCheckState = ref<UrlCheckState>('idle');
const urlCheckDetail = ref<string>('');
const iframeLoaded = ref(false);
const iframeLoading = ref(false);
const lastIframeLoadTime = ref<string>('');
const lastError = ref<string>('');
const iframeLoadError = ref(false);

const handleCopyCode = () => {
  if (props.generatedCode) {
    navigator.clipboard.writeText(props.generatedCode);
    copied.value = true;
    setTimeout(() => {
      copied.value = false;
    }, 2000);
  }
};

const handleDownload = async () => {
  if (!props.appId) {
    console.error('无法下载：应用ID不存在');
    return;
  }

  try {
    const API_BASE_URL = request.defaults.baseURL || 'http://localhost:8123/api';
    const url = `${API_BASE_URL}/app/download/${props.appId}`;
    const response = await fetch(url, {
      method: 'GET',
      credentials: 'include',
    });

    if (!response.ok) {
      throw new Error(`下载失败: ${response.status}`);
    }

    // 获取文件名
    const contentDisposition = response.headers.get('Content-Disposition');
    const fileName = contentDisposition?.match(/filename="(.+)"/)?.[1] || `app-${props.appId}.zip`;

    // 下载文件
    const blob = await response.blob();
    const downloadUrl = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = downloadUrl;
    link.download = fileName;
    link.click();
    URL.revokeObjectURL(downloadUrl);
  } catch (error) {
    console.error('下载失败：', error);
  }
};

const onIframeLoad = () => {
  // iframe 加载完成
  iframeLoading.value = false;
  iframeLoaded.value = true;
  iframeLoadError.value = false;
  lastIframeLoadTime.value = new Date().toLocaleTimeString();
  console.log('预览页面加载完成');
};

const onIframeError = () => {
  // iframe 加载错误
  iframeLoading.value = false;
  iframeLoaded.value = false;
  iframeLoadError.value = true;
  lastError.value = 'iframe加载失败';
  console.error('预览页面加载失败');
};

const handleOpenInNewTab = () => {
  if (props.previewUrl) {
    window.open(props.previewUrl, '_blank');
  }
};

// 执行 URL 可达性检查
const checkPreviewUrl = async () => {
  urlCheckDetail.value = '';
  lastError.value = '';
  iframeLoaded.value = false;
  iframeLoading.value = false;

  if (!props.previewUrl || !props.appId || !props.codeGenType) {
    urlCheckState.value = 'fail';
    urlCheckDetail.value = '缺少必要参数(appId / codeGenType / previewUrl)';
    return;
  }
  try {
    urlCheckState.value = 'checking';
    iframeLoading.value = true;
    const res = await fetch(props.previewUrl, {
      method: 'GET',
      credentials: 'include',
    });
    if (res.ok) {
      urlCheckState.value = 'ok';
      urlCheckDetail.value = `${res.status}`;
    } else {
      urlCheckState.value = 'fail';
      urlCheckDetail.value = `${res.status}`;
    }
  } catch (e: any) {
    urlCheckState.value = 'fail';
    urlCheckDetail.value = '网络或跨域错误';
    lastError.value = e?.message || String(e);
  }
};

// 检查预览URL是否正常（用于自动更新预览）
const checkPreviewUrlStatus = async (url: string): Promise<boolean> => {
  if (!url) {
    return false;
  }
  
  // 使用Promise和iframe加载来检查URL是否可用
  return new Promise((resolve) => {
    const timeoutId = setTimeout(() => {
      // 超时认为URL不可用
      console.warn('预览URL检查超时');
      resolve(false);
    }, 8000); // 8秒超时
    
    // 使用临时iframe检查URL是否可加载
    const checkWithIframe = () => {
      const testIframe = document.createElement('iframe');
      testIframe.style.display = 'none';
      testIframe.style.width = '1px';
      testIframe.style.height = '1px';
      testIframe.src = url;
      
      let resolved = false;
      
      const cleanup = () => {
        if (!resolved) {
          resolved = true;
          clearTimeout(timeoutId);
          try {
            if (testIframe.parentNode) {
              document.body.removeChild(testIframe);
            }
          } catch (e) {
            // 忽略清理错误
          }
        }
      };
      
      testIframe.onload = () => {
        console.log('预览URL检查成功（iframe）');
        cleanup();
        resolve(true);
      };
      
      testIframe.onerror = () => {
        console.warn('预览URL检查失败（iframe error）');
        cleanup();
        resolve(false);
      };
      
      // 如果iframe在5秒内没有触发load或error，认为超时
      setTimeout(() => {
        if (!resolved) {
          console.warn('预览URL检查超时（iframe）');
          cleanup();
          resolve(false);
        }
      }, 5000);
      
      document.body.appendChild(testIframe);
    };
    
    // 首先尝试fetch检查（对于同源或允许CORS的资源）
    const controller = new AbortController();
    const fetchTimeoutId = setTimeout(() => {
      controller.abort();
    }, 3000); // fetch 3秒超时
    
    fetch(url, {
      method: 'GET',
      credentials: 'include',
      signal: controller.signal,
    })
      .then((res) => {
        clearTimeout(fetchTimeoutId);
        // fetch成功且返回ok，说明URL可用
        if (res.ok) {
          clearTimeout(timeoutId);
          console.log('预览URL检查成功（fetch）');
          resolve(true);
        } else {
          console.warn('预览URL返回非200状态:', res.status);
          // 状态码不是200，但可能是302重定向等，我们仍然尝试用iframe加载
          checkWithIframe();
        }
      })
      .catch((e: any) => {
        clearTimeout(fetchTimeoutId);
        // fetch失败可能是CORS错误，但这不代表URL不可用
        // 继续用iframe检查
        console.log('fetch检查失败（可能是CORS），尝试iframe检查:', e.message);
        checkWithIframe();
      });
  });
};

// 刷新iframe预览
const refreshPreview = () => {
  if (!props.previewUrl) {
    console.warn('预览URL为空，无法刷新');
    return;
  }
  
  console.log('开始刷新预览页面，当前URL:', props.previewUrl);
  
  // 更新时间戳，强制iframe重新加载
  const oldTimestamp = iframeTimestamp.value;
  iframeTimestamp.value = Date.now();
  iframeLoaded.value = false;
  iframeLoading.value = true;
  iframeLoadError.value = false;
  console.log('更新时间戳:', { oldTimestamp, newTimestamp: iframeTimestamp.value });
  
  // 等待Vue更新DOM后，手动刷新iframe以确保刷新
  setTimeout(() => {
    if (previewIframe.value && props.previewUrl) {
      // 获取新的带时间戳的URL（iframeSrc computed会自动计算）
      const newSrc = iframeSrc.value;
      
      // 强制重新加载iframe：先清空src，再设置新的src
      previewIframe.value.src = '';
      
      setTimeout(() => {
        if (previewIframe.value) {
          previewIframe.value.src = newSrc;
          console.log('手动刷新iframe，新src:', newSrc);
        }
      }, 50);
    }
  }, 150);
};

// 暴露方法给父组件
defineExpose({
  checkPreviewUrlStatus,
  refreshPreview,
});

watch(() => props.previewUrl, (newUrl, oldUrl) => {
  // 当预览URL变化时，自动刷新iframe
  if (newUrl && newUrl !== oldUrl) {
    console.log('预览URL变化，自动刷新:', { oldUrl, newUrl });
    // 延迟一点刷新，确保URL已更新
    setTimeout(() => {
      refreshPreview();
    }, 100);
  }
  
  if (props.debug) {
    if (newUrl) {
      checkPreviewUrl();
    } else {
      urlCheckState.value = 'idle';
      urlCheckDetail.value = '';
      iframeLoaded.value = false;
      iframeLoading.value = false;
    }
  }
});

onMounted(() => {
  if (props.debug && props.previewUrl) {
    checkPreviewUrl();
  }
});

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
