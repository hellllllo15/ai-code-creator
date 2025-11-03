<template>
  <div class="h-screen w-screen overflow-hidden relative" style="background: transparent">
    <!-- 科幻背景 - 确保在最底层 -->
    <SciFiBackground />

    <!-- 顶部导航栏 -->
    <header
      class="relative z-10 h-16 border-b border-cyan-500/30 backdrop-blur-md"
      :style="{ ...headerStyle, backgroundColor: `rgba(2, 6, 23, ${headerOpacityValue})` }"
    >
      <div class="h-full px-6 flex items-center justify-between">
        <div class="flex items-center gap-3">
          <div
            class="w-10 h-10 rounded-lg bg-gradient-to-br from-cyan-500 via-purple-500 to-pink-500 flex items-center justify-center"
            :style="logoGlow"
          >
            <Sparkles class="w-6 h-6 text-white" />
          </div>
          <div>
            <h1 class="text-xl bg-gradient-to-r from-cyan-400 via-purple-400 to-pink-400 bg-clip-text text-transparent">
              代码生成平台
            </h1>
            <p class="text-xs text-slate-400">版本 v2.0</p>
          </div>
        </div>

        <div class="flex items-center gap-4">
          <div
            class="text-cyan-400"
            :style="zapRotationStyle"
          >
            <Zap class="w-5 h-5" />
          </div>
          <div class="text-right">
            <p class="text-xs text-slate-400">版本</p>
            <p class="text-sm text-cyan-400">v2.0.1</p>
          </div>
        </div>
      </div>
    </header>

    <!-- 主内容区域 -->
    <div 
      class="relative z-10 h-[calc(100vh-4rem)] flex" 
      style="background: transparent"
    >
      <!-- 左侧面板 -->
      <div
        :style="{ 
          width: `${leftWidth}%`, 
          ...leftPanelStyle
        }"
        class="relative border-r border-gray-700"
      >
        <ChatPanel 
          :opacity="chatPanelOpacityValue" 
          :app-id="appId"
          :initial-message="initialMessage"
          @generate-code="handleGenerateCode"
          @code-generated="handleCodeGenerated" 
        />
      </div>

      <!-- 可调节分隔栏 -->
      <ResizableDivider 
        :initial-left-width="leftWidth"
        @resize="setLeftWidth" 
      />

      <!-- 右侧面板 -->
      <div
        :style="{ width: `${100 - leftWidth}%`, ...rightPanelStyle }"
        class="relative border-l border-gray-700"
      >
        <PreviewPanel 
          ref="previewPanelRef"
          :opacity="previewPanelOpacityValue" 
          :generated-code="generatedCode"
          :preview-url="previewUrl"
          :app-id="appId"
          :code-gen-type="appInfo?.codeGenType"
          :no-preview-mode="noPreviewMode"
          :app-info="appInfo"
          @toggle-no-preview="handleToggleNoPreview"
          @refresh-app-info="fetchAppInfo"
        />
      </div>
    </div>

    <!-- 装饰性元素 - 增强可见度和动画效果 -->
    <div class="fixed top-20 left-10 w-64 h-64 bg-cyan-500/25 rounded-full blur-3xl pointer-events-none" style="z-index: 0;" />
    <div class="fixed bottom-20 right-10 w-96 h-96 bg-purple-500/25 rounded-full blur-3xl pointer-events-none animate-pulse-slow" style="z-index: 0; animation-delay: 1s" />
    <div class="fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-96 h-96 bg-pink-500/15 rounded-full blur-3xl pointer-events-none animate-pulse-slow" style="z-index: 0; animation-delay: 2s" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, computed, watch } from 'vue';
import { useRoute } from 'vue-router';
import { Sparkles, Zap } from 'lucide-vue-next';
import SciFiBackground from './components/SciFiBackground.vue';
import ChatPanel from './components/ChatPanel.vue';
import PreviewPanel from './components/PreviewPanel.vue';
import ResizableDivider from './components/ResizableDivider.vue';
import { getMyById } from '../../api/appController';
import { getStaticPreviewUrl } from '../../config/env';
import { CodeGenTypeEnum } from '../../utils/codeGenTypes';
import * as API from '../../api/typings';
import type { ComponentPublicInstance } from 'vue';
import './index.css';

const route = useRoute();
const leftWidth = ref(40);
const generatedCode = ref('');
const previewUrl = ref<string | null>(null);
const appInfo = ref<any>(null);
const debugEnabled = ref(route.query.debug === '1');
const previewPanelRef = ref<ComponentPublicInstance<{ checkPreviewUrlStatus: (url: string) => Promise<boolean>; refreshPreview: () => void }> | null>(null);

// 从路由参数获取appId和初始消息（同步获取，确保ChatPanel能立即收到props）
const appId = ref<string | null>((route.query.appId as string) || null);
const initialMessage = ref<string | null>((route.query.message as string) || null);

// 透明度控制值
const headerOpacityValue = ref(0.4);
const chatPanelOpacityValue = ref(0.15);  // ChatPanel 背景透明度（rgba alpha值）
const previewPanelOpacityValue = ref(0.15);  // PreviewPanel 背景透明度（rgba alpha值）
const mainContainerOpacityValue = ref(1.0);  // 保留但不使用（主容器不需要）
const panelWrapperOpacityValue = ref(0.85);   // 面板包装器整体透明度（CSS opacity），默认稍低以便看到背景

// 动画状态
const headerOpacity = ref(0);
const headerY = ref(-100);
const leftPanelX = ref(-100);
const leftPanelOpacity = ref(0);
const rightPanelX = ref(100);
const rightPanelOpacity = ref(0);
const logoGlowPhase = ref(0);
const zapRotation = ref(0);

// 计算样式
const headerStyle = computed(() => ({
  opacity: headerOpacity.value,
  transform: `translateY(${headerY.value}px)`,
  transition: 'opacity 0.6s, transform 0.6s',
}));

const leftPanelStyle = computed(() => ({
  opacity: leftPanelOpacity.value * panelWrapperOpacityValue.value,  // 动画 opacity * 用户控制透明度
  transform: `translateX(${leftPanelX.value}px)`,
  transition: 'opacity 0.6s, transform 0.6s',
  'background-color': 'transparent',
}));

const rightPanelStyle = computed(() => ({
  opacity: rightPanelOpacity.value * panelWrapperOpacityValue.value,  // 动画 opacity * 用户控制透明度
  transform: `translateX(${rightPanelX.value}px)`,
  transition: 'opacity 0.6s, transform 0.6s',
  'background-color': 'transparent',
}));

const logoGlow = computed(() => {
  const phase = logoGlowPhase.value;
  const shadow = phase === 0 
    ? '0 0 20px rgba(6, 182, 212, 0.5)'
    : phase === 1 
    ? '0 0 40px rgba(168, 85, 247, 0.5)'
    : '0 0 20px rgba(6, 182, 212, 0.5)';
  return { boxShadow: shadow, transition: 'box-shadow 1s' };
});

const zapRotationStyle = computed(() => ({
  transform: `rotate(${zapRotation.value}deg)`,
  transition: 'transform 0.1s linear',
}));

// 获取应用信息并更新预览
const fetchAppInfo = async () => {
  if (!appId.value) return;

  try {
    const response = await getMyById({
      id: appId.value as any,
    });

    if (response && response.code === 0 && response.data) {
      appInfo.value = response.data;
      updatePreview();
    }
  } catch (error) {
    console.error('获取应用信息失败:', error);
  }
};

// 更新预览URL
const updatePreview = () => {
  if (appId.value && appInfo.value?.codeGenType) {
    previewUrl.value = getStaticPreviewUrl(appInfo.value.codeGenType, appId.value);
  } else {
    previewUrl.value = null;
  }
};

// 监听 appId 变化，重新获取应用信息
watch(appId, (newAppId) => {
  if (newAppId) {
    fetchAppInfo();
  } else {
    previewUrl.value = null;
    appInfo.value = null;
  }
});

// 监听代码生成完成，刷新预览
const handleCodeGenerated = async () => {
  console.log('代码生成完成，准备刷新预览页面');
  
  // 延迟刷新，确保后端已完成文件保存和处理
  setTimeout(() => {
    // 直接刷新预览iframe，因为URL通常不会改变，只是内容更新了
    if (previewPanelRef.value && typeof previewPanelRef.value.refreshPreview === 'function') {
      console.log('刷新预览iframe');
      previewPanelRef.value.refreshPreview();
    } else {
      console.warn('预览面板引用不可用，无法刷新');
    }
    
    // 可选：如果需要更新应用信息，可以异步获取（不阻塞刷新）
    // 延迟更新应用信息，避免影响刷新速度
    setTimeout(async () => {
      if (!appId.value) return;
      
      try {
        const response = await getMyById({
          id: appId.value as any,
        });

        if (response && response.code === 0 && response.data) {
          const newAppInfo = response.data;
          const newCodeGenType = newAppInfo.codeGenType;
          
          // 生成新的预览URL
          const newPreviewUrl = newCodeGenType && appId.value 
            ? getStaticPreviewUrl(newCodeGenType, appId.value)
            : null;
          
          // 如果预览URL变化了，检查新的预览URL是否正常后再更新
          if (newPreviewUrl && newPreviewUrl !== previewUrl.value) {
            console.log('预览URL发生变化，检查新URL:', newPreviewUrl);
            
            if (previewPanelRef.value && typeof previewPanelRef.value.checkPreviewUrlStatus === 'function') {
              const isUrlValid = await previewPanelRef.value.checkPreviewUrlStatus(newPreviewUrl);
              
              if (isUrlValid) {
                console.log('新预览URL正常，更新预览URL并刷新');
                appInfo.value = newAppInfo;
                previewUrl.value = newPreviewUrl;
                // URL变化会自动触发watch刷新，所以不需要手动刷新
              } else {
                console.warn('新预览URL异常，保持原预览页面');
              }
            } else {
              // 没有检查方法，直接更新
              appInfo.value = newAppInfo;
              previewUrl.value = newPreviewUrl;
            }
          }
        }
      } catch (error) {
        console.error('获取应用信息失败:', error);
      }
    }, 2000); // 延迟2秒异步更新，不影响预览刷新
  }, 2000); // 延迟2秒刷新，确保后端文件已保存
};

// 动画循环
let logoGlowInterval: number;
let zapRotationInterval: number;

const noPreviewMode = ref(false);
const showPreviewPanel = computed(() => previewUrl.value && !noPreviewMode.value);

function handleToggleNoPreview() {
  noPreviewMode.value = !noPreviewMode.value;
}

onMounted(() => {
  // 添加home页面特定类到body
  document.body.classList.add('home-page-active');
  
  // 如果已有appId，获取应用信息
  if (appId.value) {
    fetchAppInfo();
  }
  
  // 入场动画
  setTimeout(() => {
    headerOpacity.value = 1;
    headerY.value = 0;
  }, 0);

  setTimeout(() => {
    leftPanelOpacity.value = 1;
    leftPanelX.value = 0;
    rightPanelOpacity.value = 1;
    rightPanelX.value = 0;
  }, 200);

  // Logo发光动画
  logoGlowInterval = window.setInterval(() => {
    logoGlowPhase.value = (logoGlowPhase.value + 1) % 3;
  }, 1000);

  // 闪电旋转动画
  zapRotationInterval = window.setInterval(() => {
    zapRotation.value += 4.5; // 360度 / 8秒 / 10fps = 4.5度
  }, 100);
});

onUnmounted(() => {
  document.body.classList.remove('home-page-active');
  clearInterval(logoGlowInterval);
  clearInterval(zapRotationInterval);
});

const handleGenerateCode = (prompt: string) => {
  // 生成代码
  const code = `// 根据需求生成的代码: ${prompt}
import { ref } from 'vue';

export default {
  setup() {
    return () => (
      <div class="container mx-auto p-8">
        <h1 class="text-3xl mb-4">生成的页面</h1>
        <p>这是根据你的需求生成的代码。</p>
        
        <div class="grid grid-cols-3 gap-4 mt-8">
          {[1, 2, 3].map((item) => (
            <div 
              key={item}
              class="p-6 bg-gradient-to-br from-purple-500 to-pink-500 
                     rounded-lg text-white shadow-xl"
            >
              <h3>组件 {item}</h3>
              <p>示例组件</p>
            </div>
          ))}
        </div>
      </div>
    );
  }
};`;

  generatedCode.value = code;
};

const setLeftWidth = (width: number) => {
  leftWidth.value = width;
};
</script>

<style>
/* Home页面全局样式 */
html, body {
  margin: 0 !important;
  padding: 0 !important;
  background: transparent !important;
}

#app {
  margin: 0 !important;
  padding: 0 !important;
  background: transparent !important;
}

/* 确保 body 在 home 页面时背景透明 */
body.home-page-active {
  background: transparent !important;
  background-color: transparent !important;
}
</style>
