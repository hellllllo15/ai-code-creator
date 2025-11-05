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
          <button
            @click="handleDownload"
            v-if="appId"
            :disabled="!isPreviewValid || downloading"
            class="ml-2 px-3 py-1 text-xs text-purple-400 hover:text-purple-300 hover:bg-purple-500/10 rounded transition-all disabled:opacity-50 disabled:cursor-not-allowed flex items-center gap-1"
            title="下载应用代码（需要预览页面正常显示）"
          >
            <Download class="w-3 h-3" />
            <span v-if="downloading">下载中...</span>
            <span v-else>下载</span>
          </button>
          <button
            @click="handleDeploy"
            v-if="appId && !isDeployed"
            :disabled="deploying"
            class="ml-2 px-3 py-1 text-xs text-cyan-400 hover:text-cyan-300 hover:bg-cyan-500/10 rounded transition-all disabled:opacity-50 disabled:cursor-not-allowed flex items-center gap-1"
          >
            <span v-if="deploying">部署中...</span>
            <span v-else>部署</span>
          </button>
          <div
            v-if="appId && isDeployed"
            class="ml-2 px-3 py-1 text-xs text-green-400 flex items-center gap-1"
          >
            <Check class="w-3 h-3" />
            <span>已部署</span>
          </div>
        </div>
        <!-- 部署地址显示区域 -->
        <div v-if="deployUrl || isDeployed" class="px-4 py-2 bg-gradient-to-r from-cyan-900/50 to-purple-900/50 border-b border-cyan-500/30 flex items-center gap-2">
          <div class="flex-1 text-xs">
            <span class="text-cyan-400">部署地址:</span>
            <span class="text-cyan-200 ml-2 font-mono break-all">{{ currentDeployUrl }}</span>
          </div>
          <button
            @click="handleOpenDeployUrl"
            class="px-2 py-1 text-xs text-cyan-400 hover:text-cyan-300 hover:bg-cyan-500/10 rounded transition-all"
          >
            打开
          </button>
          <button
            @click="handleCopyDeployUrl"
            class="px-2 py-1 text-xs text-cyan-400 hover:text-cyan-300 hover:bg-cyan-500/10 rounded transition-all flex items-center gap-1"
          >
            <Check v-if="deployUrlCopied" class="w-3 h-3" />
            <Copy v-else class="w-3 h-3" />
            <span>{{ deployUrlCopied ? '已复制' : '复制' }}</span>
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
import { deployApp } from '../../../api/appController';
import { STATIC_BASE_URL } from '../../../config/env';

interface Props {
  generatedCode?: string;
  previewUrl?: string;
  appId?: string | null;
  opacity?: number;
  codeGenType?: string;
  debug?: boolean;
  noPreviewMode?: boolean; // 新增 props
  appInfo?: any; // 应用信息，包含 deployKey 和 deployedTime
}
const props = withDefaults(defineProps<Props>(), {
  opacity: 0.15,
  noPreviewMode: false, // 默认值
});
const noPreviewMode = computed(() => props.noPreviewMode);

// 定义事件
const emit = defineEmits<{
  refreshAppInfo: [];
  'toggle-no-preview': [];
}>();

const copied = ref(false);
const activeView = ref<'preview' | 'code'>('preview');
const diagnosticsOpen = ref(true);

// 部署相关状态
const deploying = ref(false);
const deployUrl = ref<string | null>(null);
const deployUrlCopied = ref(false);

// 下载相关状态
const downloading = ref(false);

// 判断应用是否已部署
const isDeployed = computed(() => {
  return !!(props.appInfo?.deployKey && props.appInfo?.deployedTime);
});

// 判断预览页面是否正常显示
const isPreviewValid = computed(() => {
  return !!(
    props.previewUrl && 
    !noPreviewMode.value && 
    iframeLoaded.value && 
    !iframeLoadError.value
  );
});

// 从应用信息中获取部署地址（如果已部署）
const getDeployUrlFromAppInfo = () => {
  if (isDeployed.value && props.appInfo?.deployKey) {
    return `${STATIC_BASE_URL}/${props.appInfo.deployKey}/`;
  }
  return null;
};

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

  // 检查预览是否正常
  if (!isPreviewValid.value) {
    alert('请等待预览页面正常显示后再下载');
    return;
  }

  try {
    downloading.value = true;
    
    // 使用 fetch 下载文件（因为返回的是文件流，不是 JSON）
    const API_BASE_URL = request.defaults.baseURL || 'http://localhost:8123/api';
    const url = `${API_BASE_URL}/app/download/${props.appId}`;
    const response = await fetch(url, {
      method: 'GET',
      credentials: 'include',
    });

    if (!response.ok) {
      // 尝试解析错误信息
      const errorData = await response.json().catch(() => null);
      throw new Error(errorData?.message || `下载失败: ${response.status}`);
    }

    // 获取文件名
    const contentDisposition = response.headers.get('Content-Disposition');
    const fileName = contentDisposition?.match(/filename="?(.+?)"?$/)?.[1] || 
                     contentDisposition?.match(/filename\*=(?:UTF-8'')?([^;]+)/)?.[1] ||
                     `app-${props.appId}.zip`;

    // 下载文件
    const blob = await response.blob();
    const downloadUrl = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = downloadUrl;
    link.download = decodeURIComponent(fileName);
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    URL.revokeObjectURL(downloadUrl);
    
    console.log('下载成功');
  } catch (error: any) {
    console.error('下载失败：', error);
    alert(`下载失败: ${error?.message || '未知错误'}`);
  } finally {
    downloading.value = false;
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

// 部署应用
const handleDeploy = async () => {
  if (!props.appId) {
    console.error('无法部署：应用ID不存在');
    return;
  }

  try {
    deploying.value = true;
    deployUrl.value = null;

    // 直接传递字符串，避免大整数精度丢失
    // Spring Boot 会自动将字符串转换为 Long
    const response = await deployApp({
      appId: props.appId,
    });

    if (response && response.code === 0 && response.data) {
      // 后端返回的格式可能是 http://localhost/{deployKey}/
      // 需要转换为正确的静态资源访问地址: http://localhost:8123/api/static/{deployKey}/
      const backendUrl = response.data;
      let finalDeployUrl = backendUrl;
      
      try {
        // 尝试从后端URL中提取 deployKey
        // 支持格式：http://localhost/{deployKey}/ 或 http://localhost:8123/{deployKey}/
        // 正则匹配：协议://域名/部署密钥/
        const urlMatch = backendUrl.match(/https?:\/\/[^\/]+\/([^\/\s]+)\/?$/);
        if (urlMatch && urlMatch[1]) {
          const deployKey = urlMatch[1];
          // 转换为正确的静态资源访问地址
          // STATIC_BASE_URL 格式: http://localhost:8123/api/static
          finalDeployUrl = `${STATIC_BASE_URL}/${deployKey}/`;
          console.log('提取的deployKey:', deployKey, '转换后的URL:', finalDeployUrl);
        } else {
          console.warn('无法从后端URL中提取deployKey:', backendUrl);
        }
      } catch (error) {
        console.warn('解析部署URL失败，使用原始URL:', error);
      }
      
      deployUrl.value = finalDeployUrl;
      console.log('部署成功，原始地址:', backendUrl, '转换后地址:', finalDeployUrl);
      
      // 部署成功后，触发父组件重新获取应用信息
      setTimeout(() => {
        emit('refreshAppInfo');
      }, 500);
    } else {
      console.error('部署失败:', response?.message || '未知错误');
      alert(`部署失败: ${response?.message || '未知错误'}`);
    }
  } catch (error: any) {
    console.error('部署失败：', error);
    alert(`部署失败: ${error?.message || '网络错误'}`);
  } finally {
    deploying.value = false;
  }
};

// 获取当前部署URL（优先使用本地状态，否则从appInfo获取）
const currentDeployUrl = computed(() => {
  return deployUrl.value || getDeployUrlFromAppInfo();
});

// 打开部署地址
const handleOpenDeployUrl = () => {
  const url = currentDeployUrl.value;
  if (url) {
    window.open(url, '_blank');
  }
};

// 复制部署地址
const handleCopyDeployUrl = async () => {
  const url = currentDeployUrl.value;
  if (url) {
    try {
      await navigator.clipboard.writeText(url);
      deployUrlCopied.value = true;
      setTimeout(() => {
        deployUrlCopied.value = false;
      }, 2000);
    } catch (error) {
      console.error('复制失败：', error);
      alert('复制失败，请手动复制');
    }
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
  getIframe: () => previewIframe.value, // 暴露 iframe 引用
});

// 监听应用信息变化，自动更新部署地址
watch(() => props.appInfo, (newAppInfo) => {
  if (newAppInfo && isDeployed.value && !deployUrl.value) {
    // 如果应用已部署但没有本地部署URL，从appInfo获取
    const url = getDeployUrlFromAppInfo();
    if (url) {
      deployUrl.value = url;
    }
  }
}, { immediate: true });

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
