<template>
  <div class="min-h-screen relative bg-black overflow-hidden">
    <!-- 复杂背景层 -->
    <div class="absolute inset-0 bg-gradient-to-br from-slate-950 via-emerald-950/30 to-slate-950" />
    <div class="absolute inset-0 bg-[radial-gradient(ellipse_at_center,_var(--tw-gradient-stops))] from-emerald-900/20 via-transparent to-transparent" />
    
    <!-- 网格背景 -->
    <div class="absolute inset-0 opacity-20">
      <div class="absolute inset-0 bg-[linear-gradient(to_right,#10b98120_1px,transparent_1px),linear-gradient(to_bottom,#10b98120_1px,transparent_1px)] bg-[size:4rem_4rem]" />
    </div>

    <!-- 动态效果层 -->
    <CodeRain />
    <AIParticles />
    <NeuralNetwork />
    <DataStream />

    <!-- 主内容区域 -->
    <div class="relative z-10 flex min-h-screen">
      <!-- 左侧信息面板 -->
      <div
        class="hidden lg:flex lg:w-2/5 relative"
        :style="leftPanelStyle"
      >
        <div class="flex-1 p-12 flex flex-col justify-between">
          <!-- 顶部 Logo 和标题 -->
          <div>
            <div class="inline-block mb-6" :style="logoGlowStyle">
              <div class="relative">
                <div class="absolute inset-0 bg-gradient-to-r from-emerald-400 to-blue-600 rounded-2xl blur-xl opacity-50" />
                <div class="relative w-16 h-16 bg-gradient-to-br from-emerald-500 to-blue-600 rounded-2xl flex items-center justify-center border-2 border-emerald-300/50">
                  <Code2 class="w-8 h-8 text-white" />
                </div>
              </div>
            </div>
            
            <h1 class="text-5xl mb-4 bg-gradient-to-r from-emerald-400 via-blue-400 to-purple-400 text-transparent bg-clip-text">
              AI Code Studio
            </h1>
            <p class="text-xl text-emerald-200/70 mb-8">
              Transform ideas into code<br />
              Build faster with AI
            </p>

            <!-- AI 模型展示 -->
            <div class="space-y-3 mb-8">
              <h3 class="text-sm text-emerald-300/50 uppercase tracking-wider flex items-center gap-2">
                <Cpu class="w-4 h-4" />
                支持的AI模型
              </h3>
              <div
                v-for="(model, index) in models"
                :key="model.name"
                class="group relative"
                :style="getModelStyle(index)"
              >
                <div class="flex items-center gap-3 p-3 rounded-lg bg-slate-900/30 border border-emerald-500/20 hover:border-emerald-500/40 transition-all">
                  <div :class="`w-2 h-2 rounded-full bg-gradient-to-r ${model.color}`" />
                  <div class="flex-1">
                    <div class="text-emerald-100/90">{{ model.name }}</div>
                    <div class="text-xs text-emerald-300/50">{{ model.desc }}</div>
                  </div>
                  <Sparkles class="w-4 h-4 text-emerald-400/50 group-hover:text-emerald-400 transition-colors" />
                </div>
              </div>
            </div>

            <!-- 特性列表 -->
            <div class="space-y-2">
              <div
                v-for="(feature, index) in features"
                :key="feature.text"
                class="flex items-center gap-3 text-emerald-300/70"
                :style="getFeatureStyle(index)"
              >
                <component :is="feature.icon" class="w-4 h-4" />
                <span class="text-sm">{{ feature.text }}</span>
              </div>
            </div>
          </div>

          <!-- 底部统计 -->
          <div class="grid grid-cols-3 gap-4">
            <div
              v-for="(stat, index) in stats"
              :key="stat.label"
              class="relative group"
              :style="getStatStyle(index)"
            >
              <div class="absolute inset-0 bg-gradient-to-t from-emerald-500/10 to-transparent rounded-lg blur-sm group-hover:from-emerald-500/20 transition-all" />
              <div class="relative p-3 border border-emerald-500/20 rounded-lg backdrop-blur-sm hover:border-emerald-500/40 transition-all">
                <div class="text-2xl text-emerald-400 mb-1">{{ stat.value }}</div>
                <div class="text-xs text-emerald-300/50">{{ stat.label }}</div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧表单区域 -->
      <div class="flex-1 flex items-center justify-center p-8">
        <div class="w-full max-w-2xl" :style="formContainerStyle">
          <!-- 主卡片 -->
          <div class="relative">
            <!-- 外层光环 -->
            <div
              class="absolute -inset-1 bg-gradient-to-r from-emerald-600 via-blue-600 to-purple-600 rounded-3xl blur-xl opacity-30"
              :style="glowStyle"
            />

            <!-- 角装饰 - 代码风格 -->
            <div class="absolute -top-3 -left-3 text-emerald-400/30 font-mono text-xs"><div></div>
            <div class="absolute -top-3 -right-3 text-emerald-400/30 font-mono text-xs"></div></div>
            <div class="absolute -bottom-3 -left-3 text-emerald-400/30 font-mono text-xs">{{ '{ }' }}</div>
            <div class="absolute -bottom-3 -right-3 text-emerald-400/30 font-mono text-xs">[ ]</div>

            <!-- 主体内容 -->
            <div class="relative bg-slate-950/90 backdrop-blur-2xl border border-emerald-500/30 rounded-2xl p-10 shadow-2xl">
              <!-- 顶部装饰 -->
              <div class="flex items-center justify-center mb-8">
                <div class="flex items-center gap-4">
                  <div class="h-px w-12 bg-gradient-to-r from-transparent to-emerald-500" :style="lineStyle1" />
                  <Terminal class="w-8 h-8 text-emerald-400" />
                  <div class="h-px w-12 bg-gradient-to-l from-transparent to-emerald-500" :style="lineStyle2" />
                </div>
              </div>

              <!-- 切换按钮 -->
              <div class="flex gap-3 mb-8">
                <button
                  @click="mode = 'login'; errorMessage = ''"
                  :class="['flex-1 relative py-4 px-6 rounded-xl transition-all duration-500 overflow-hidden group', mode === 'login' ? 'text-white' : 'text-emerald-300/50']"
                >
                  <div v-if="mode === 'login'" class="absolute inset-0 bg-gradient-to-r from-emerald-600 to-blue-600" />
                  <span class="relative flex items-center justify-center gap-2 z-10">
                    <Terminal class="w-5 h-5" />
                    <span>登录</span>
                  </span>
                  <div v-if="mode !== 'login'" class="absolute inset-0 bg-slate-800/30 group-hover:bg-slate-800/50 transition-colors" />
                </button>

                <button
                  @click="mode = 'register'; errorMessage = ''"
                  :class="['flex-1 relative py-4 px-6 rounded-xl transition-all duration-500 overflow-hidden group', mode === 'register' ? 'text-white' : 'text-emerald-300/50']"
                >
                  <div v-if="mode === 'register'" class="absolute inset-0 bg-gradient-to-r from-emerald-600 to-blue-600" />
                  <span class="relative flex items-center justify-center gap-2 z-10">
                    <Code2 class="w-5 h-5" />
                    <span>注册</span>
                  </span>
                  <div v-if="mode !== 'register'" class="absolute inset-0 bg-slate-800/30 group-hover:bg-slate-800/50 transition-colors" />
                </button>
              </div>

              <!-- 错误提示 -->
              <div v-if="errorMessage" class="mb-6 p-3 rounded-lg bg-red-500/10 border border-red-500/30 text-red-400 text-sm text-center">
                    {{ errorMessage }}
                  </div>

              <!-- 表单内容 -->
              <Transition
                mode="out-in"
                enter-active-class="transition-all duration-400 ease-out"
                enter-from-class="opacity-0 -translate-x-5"
                enter-to-class="opacity-100 translate-x-0"
                leave-active-class="transition-all duration-400 ease-in"
                leave-from-class="opacity-100 translate-x-0"
                leave-to-class="opacity-0 translate-x-5"
              >
                <!-- 登录表单 -->
                <form v-if="mode === 'login'" class="space-y-6" @submit.prevent="handleLogin">
                  <div class="space-y-2">
                    <label class="text-emerald-200 flex items-center gap-2">
                      <Mail class="w-4 h-4" />
                      账号
                    </label>
                    <input
                      v-model="loginForm.email"
                      type="text"
                      placeholder="请输入账号"
                      class="w-full bg-slate-900/50 border border-emerald-500/30 text-white placeholder:text-emerald-300/30 focus:border-emerald-400 focus:ring-emerald-400/20 h-12 font-mono rounded-md px-3 outline-none focus:ring-2"
                    />
                  </div>

                  <div class="space-y-2">
                    <label class="text-emerald-200 flex items-center gap-2">
                      <Lock class="w-4 h-4" />
                      Password
                    </label>
                    <input
                      v-model="loginForm.password"
                      type="password"
                      placeholder="••••••••"
                      class="w-full bg-slate-900/50 border border-emerald-500/30 text-white placeholder:text-emerald-300/30 focus:border-emerald-400 focus:ring-emerald-400/20 h-12 font-mono rounded-md px-3 outline-none focus:ring-2"
                    />
                  </div>

                  <div class="flex items-center justify-between text-sm">
                    <label class="flex items-center gap-2 text-emerald-300/70 cursor-pointer">
                      <input v-model="loginForm.remember" type="checkbox" class="rounded border-emerald-500/30 bg-slate-900/50" />
                      记住登录
                    </label>
                    <button type="button" class="text-emerald-400 hover:text-emerald-300 transition-colors">
                      忘记密码？
                    </button>
                  </div>

                  <button
                    type="submit"
                    :disabled="isLoading"
                    class="w-full relative group overflow-hidden rounded-xl py-4 disabled:opacity-50 disabled:cursor-not-allowed"
                  >
                    <div class="absolute inset-0 bg-gradient-to-r from-emerald-600 via-blue-600 to-purple-600 bg-[length:200%_100%] animate-[shimmer_3s_linear_infinite]" />
                    <div class="absolute inset-0 bg-gradient-to-r from-emerald-600 to-blue-600 opacity-0 group-hover:opacity-100 transition-opacity" />
                    <span class="relative flex items-center justify-center gap-2 text-white">
                      <Zap class="w-5 h-5" />
                      {{ isLoading ? '登录中...' : '开始编码' }}
                    </span>
                  </button>

                  <!-- 社交登录 -->
                  <div class="relative">
                    <div class="absolute inset-0 flex items-center">
                      <div class="w-full border-t border-emerald-500/20" />
                    </div>
                    <div class="relative flex justify-center text-xs uppercase">
                      <span class="bg-slate-950 px-2 text-emerald-300/50">或使用</span>
                    </div>
                  </div>

                  <div class="grid grid-cols-2 gap-3">
                    <button
                      type="button"
                      class="flex items-center justify-center gap-2 py-3 px-4 bg-slate-900/50 border border-emerald-500/30 rounded-lg text-emerald-300 hover:border-emerald-500/50 hover:bg-slate-900/70 transition-all"
                    >
                      <Github class="w-5 h-5" />
                      GitHub
                    </button>
                    <button
                      type="button"
                      class="flex items-center justify-center gap-2 py-3 px-4 bg-slate-900/50 border border-emerald-500/30 rounded-lg text-emerald-300 hover:border-emerald-500/50 hover:bg-slate-900/70 transition-all"
                    >
                      <Chrome class="w-5 h-5" />
                      Google
                    </button>
                  </div>
                </form>

                <!-- 注册表单 -->
                <form v-else class="space-y-5" @submit.prevent="handleRegister">
                  <div class="grid grid-cols-2 gap-4">
                    <div class="space-y-2">
                      <label class="text-emerald-200 flex items-center gap-2">
                        <User class="w-4 h-4" />
                        用户名
                      </label>
                      <input
                        v-model="registerForm.username"
                        type="text"
                        placeholder="developer"
                        class="w-full bg-slate-900/50 border border-emerald-500/30 text-white placeholder:text-emerald-300/30 focus:border-emerald-400 focus:ring-emerald-400/20 font-mono rounded-md px-3 py-2 outline-none focus:ring-2"
                      />
                    </div>

                    <div class="space-y-2">
                      <label class="text-emerald-200 flex items-center gap-2">
                        <Cpu class="w-4 h-4" />
                        AI模型
                      </label>
                      <select
                        v-model="registerForm.aiModel"
                        class="w-full bg-slate-900/50 border border-emerald-500/30 text-white focus:border-emerald-400 focus:ring-emerald-400/20 rounded-md px-3 py-2 outline-none focus:ring-2"
                      >
                        <option value="gpt4">GPT-4</option>
                        <option value="claude">Claude 3</option>
                        <option value="gemini">Gemini Pro</option>
                        <option value="llama">LLaMA 3</option>
                      </select>
                    </div>
                  </div>

                  <div class="space-y-2">
                    <label class="text-emerald-200 flex items-center gap-2">
                      <Code2 class="w-4 h-4" />
                      首选框架
                    </label>
                    <div class="grid grid-cols-5 gap-2">
                      <button
                        v-for="(framework, index) in frameworks"
                        :key="framework.name"
                        type="button"
                        @click="selectedFramework = index"
                        :class="['relative p-3 rounded-lg border transition-all', selectedFramework === index ? 'border-emerald-400 bg-emerald-500/20' : 'border-emerald-500/30 bg-slate-900/50 hover:border-emerald-500/50']"
                      >
                        <component :is="framework.icon" :class="`w-6 h-6 mx-auto ${framework.color}`" />
                        <span class="text-xs text-emerald-300/70 block mt-1">{{ framework.name }}</span>
                        <div v-if="selectedFramework === index" class="absolute inset-0 border-2 border-emerald-400 rounded-lg" />
                      </button>
                    </div>
                  </div>

                  <div class="space-y-2">
                    <label class="text-emerald-200 flex items-center gap-2">
                      <Mail class="w-4 h-4" />
                     账号
                    </label>
                    <input
                      v-model="registerForm.email"
                      type="text"
                      placeholder="请输入账号"
                      class="w-full bg-slate-900/50 border border-emerald-500/30 text-white placeholder:text-emerald-300/30 focus:border-emerald-400 focus:ring-emerald-400/20 font-mono rounded-md px-3 py-2 outline-none focus:ring-2"
                    />
                  </div>

                  <div class="grid grid-cols-2 gap-4">
                    <div class="space-y-2">
                      <label class="text-emerald-200 flex items-center gap-2">
                        <Lock class="w-4 h-4" />
                        密码
                      </label>
                      <input
                        v-model="registerForm.password"
                        type="password"
                        placeholder="••••••••"
                        class="w-full bg-slate-900/50 border border-emerald-500/30 text-white placeholder:text-emerald-300/30 focus:border-emerald-400 focus:ring-emerald-400/20 font-mono rounded-md px-3 py-2 outline-none focus:ring-2"
                      />
                    </div>

                    <div class="space-y-2">
                      <label class="text-emerald-200 flex items-center gap-2">
                        <Lock class="w-4 h-4" />
                        确认密码
                      </label>
                      <input
                        v-model="registerForm.confirmPassword"
                        type="password"
                        placeholder="••••••••"
                        class="w-full bg-slate-900/50 border border-emerald-500/30 text-white placeholder:text-emerald-300/30 focus:border-emerald-400 focus:ring-emerald-400/20 font-mono rounded-md px-3 py-2 outline-none focus:ring-2"
                      />
                    </div>
                  </div>

                  <label class="flex items-start gap-2 text-sm text-emerald-300/70 cursor-pointer">
                    <input v-model="registerForm.agree" type="checkbox" class="rounded border-emerald-500/30 bg-slate-900/50 mt-0.5" />
                    <span>
                      我同意
                      <button type="button" class="text-emerald-400 hover:text-emerald-300 mx-1">服务条款</button>
                      和
                      <button type="button" class="text-emerald-400 hover:text-emerald-300 mx-1">隐私政策</button>
                    </span>
                  </label>

                  <button
                    type="submit"
                    :disabled="isLoading"
                    class="w-full relative group overflow-hidden rounded-xl py-4 disabled:opacity-50 disabled:cursor-not-allowed"
                  >
                    <div class="absolute inset-0 bg-gradient-to-r from-emerald-600 via-blue-600 to-purple-600 bg-[length:200%_100%] animate-[shimmer_3s_linear_infinite]" />
                    <div class="absolute inset-0 bg-gradient-to-r from-emerald-600 to-blue-600 opacity-0 group-hover:opacity-100 transition-opacity" />
                    <span class="relative flex items-center justify-center gap-2 text-white">
                      <Sparkles class="w-5 h-5" />
                      {{ isLoading ? '注册中...' : '开启AI编码之旅' }}
                    </span>
                  </button>
                </form>
              </Transition>

              <!-- 底部切换 -->
              <div class="mt-8 pt-6 border-t border-emerald-500/20">
                <p class="text-center text-emerald-300/50 text-sm">
                  {{ mode === 'login' ? '还没有账号？' : '已有账号？' }}
                  <button
                    type="button"
                    @click="mode = mode === 'login' ? 'register' : 'login'; errorMessage = ''"
                    class="text-emerald-400 hover:text-emerald-300 ml-2 transition-colors"
                  >
                    {{ mode === 'login' ? '立即注册' : '直接登录' }}
                  </button>
                </p>
              </div>

              <!-- 装饰性代码片段 -->
              <div class="absolute top-4 right-4 text-emerald-400/20 font-mono text-xs">
                const isActive = true;
              </div>
              <div class="absolute bottom-4 left-4 text-emerald-400/20 font-mono text-xs">
                // powered by code
              </div>
            </div>
          </div>

          <!-- 底部提示 -->
          <div class="mt-8 text-center space-y-2" :style="bottomTextStyle">
            <p class="text-emerald-300/40 text-sm font-mono">
              {{ '< Build the future with code />' }}
            </p>
            <div class="flex items-center justify-center gap-4 text-emerald-400/30 text-xs">
              <span>v2.0.0</span>
              <span>•</span>
              <span>API Status: Online</span>
              <span>•</span>
              <span>99.9% Uptime</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 装饰性扫描线 -->
    <div class="absolute left-0 right-0 h-px bg-gradient-to-r from-transparent via-emerald-400/50 to-transparent" :style="scanLineStyle" />

    <!-- 底部装饰光柱 -->
    <div
      v-for="i in 8"
      :key="i"
      class="absolute bottom-0 w-px bg-gradient-to-t from-emerald-400/30 via-emerald-400/10 to-transparent"
      :style="getLightColumnStyle(i)"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue';
import { Code2, Sparkles, Cpu, Zap, Mail, Lock, User, Github, Chrome, Database, Terminal, Braces, Box, FileCode } from 'lucide-vue-next';
import CodeRain from './components/CodeRain.vue';
import AIParticles from './components/AIParticles.vue';
import NeuralNetwork from './components/NeuralNetwork.vue';
import DataStream from './components/DataStream.vue';
import { userLogin, userRegister } from '../../../api/userController';
import { useRouter } from 'vue-router';

const router = useRouter();

// 防止全局样式污染
onMounted(() => {
  document.body.classList.add('login-page-active');
  document.documentElement.classList.add('login-page-active');
  
  // 启动动画
  const animate = () => {
    animationTime.value += 0.016;
    if (animationTime.value > 2) {
      animationTime.value = 2; // 限制动画时间，防止无限增长
    }
    requestAnimationFrame(animate);
  };
  animate();
});

onUnmounted(() => {
  document.body.classList.remove('login-page-active');
  document.documentElement.classList.remove('login-page-active');
});

// 表单状态
const mode = ref<'login' | 'register'>('login');
const selectedFramework = ref(0);

const loginForm = ref({
  email: '',
  password: '',
  remember: false,
});

const registerForm = ref({
  username: '',
  aiModel: 'gpt4',
  email: '',
  password: '',
  confirmPassword: '',
  agree: false,
});

// 加载状态
const isLoading = ref(false);
const errorMessage = ref('');

// 登录处理
const handleLogin = async () => {
  if (!loginForm.value.email || !loginForm.value.password) {
    errorMessage.value = '请填写账号和密码';
    return;
  }

  isLoading.value = true;
  errorMessage.value = '';

  try {
    const response = await userLogin({
      userAccount: loginForm.value.email,
      userPassword: loginForm.value.password,
    });

    if (response.code === 0 && response.data) {
      // 登录成功，跳转到about页面
      router.push('/about');
    } else {
      errorMessage.value = response.message || '登录失败';
    }
  } catch (error: any) {
    errorMessage.value = error.response?.data?.message || error.message || '登录失败';
    console.error('Login error:', error);
  } finally {
    isLoading.value = false;
  }
};

// 注册处理
const handleRegister = async () => {
  // 验证必填项
  if (!registerForm.value.email || !registerForm.value.password || !registerForm.value.confirmPassword) {
    errorMessage.value = '请填写所有必填项';
    return;
  }

  // 验证密码一致性
  if (registerForm.value.password !== registerForm.value.confirmPassword) {
    errorMessage.value = '两次输入的密码不一致';
    return;
  }

  // 验证密码长度
  if (registerForm.value.password.length < 8) {
    errorMessage.value = '密码长度至少为8位';
    return;
  }

  if (!registerForm.value.agree) {
    errorMessage.value = '请同意服务条款和隐私政策';
    return;
  }

  isLoading.value = true;
  errorMessage.value = '';

  try {
    const response = await userRegister({
      userAccount: registerForm.value.email,
      userPassword: registerForm.value.password,
      checkPassword: registerForm.value.confirmPassword,
    });

    if (response.code === 0) {
      // 注册成功，切换到登录模式
      mode.value = 'login';
      errorMessage.value = '';
      alert('注册成功！请登录');
    } else {
      errorMessage.value = response.message || '注册失败';
    }
  } catch (error: any) {
    errorMessage.value = error.response?.data?.message || error.message || '注册失败';
    console.error('Register error:', error);
  } finally {
    isLoading.value = false;
  }
};

const models = [
  { name: 'GPT-4', desc: '最强大的语言模型', color: 'from-emerald-400 to-emerald-600' },
  { name: 'Claude 3', desc: '高级推理能力', color: 'from-blue-400 to-blue-600' },
  { name: 'Gemini Pro', desc: '多模态AI模型', color: 'from-purple-400 to-purple-600' },
  { name: 'LLaMA 3', desc: '开源大语言模型', color: 'from-orange-400 to-orange-600' },
];

const frameworks = [
  { name: 'React', icon: Code2, color: 'text-cyan-400' },
  { name: 'Vue', icon: Box, color: 'text-emerald-400' },
  { name: 'Angular', icon: Braces, color: 'text-red-400' },
  { name: 'Svelte', icon: FileCode, color: 'text-orange-400' },
  { name: 'Next.js', icon: Terminal, color: 'text-white' },
];

const features = [
  { icon: Zap, text: '实时代码生成' },
  { icon: Terminal, text: '智能补全建议' },
  { icon: Database, text: '项目模板库' },
];

const stats = [
  { label: '活跃开发者', value: '50K+' },
  { label: '生成项目', value: '1M+' },
  { label: '代码行数', value: '100M+' },
];

// 动画相关
const animationTime = ref(0);

const leftPanelStyle = computed(() => ({
  opacity: Math.min(animationTime.value / 1, 1),
  transform: `translateX(${Math.max(0, (1 - animationTime.value) * -100)}px)`,
  transition: 'all 1s ease-out',
}));

const formContainerStyle = computed(() => ({
  opacity: Math.min((animationTime.value - 0.3) / 0.8, 1),
  transform: `scale(${Math.min(0.9 + (animationTime.value - 0.3) * 0.125, 1)})`,
  transition: 'all 0.8s ease-out',
}));

const logoGlowStyle = computed(() => ({
  boxShadow: `0 0 ${20 + Math.sin(animationTime.value * 2) * 10}px rgba(16, 185, 129, ${0.3 + Math.sin(animationTime.value * 2) * 0.1})`,
}));

const glowStyle = computed(() => ({
  opacity: 0.3 + Math.sin(animationTime.value) * 0.1,
}));

const lineStyle1 = computed(() => ({
  width: `${3 + Math.sin(animationTime.value * 2) * 0.5}rem`,
}));

const lineStyle2 = computed(() => ({
  width: `${3 + Math.sin(animationTime.value * 2 + Math.PI) * 0.5}rem`,
}));

const scanLineStyle = computed(() => ({
  top: `${(animationTime.value * 12.5) % 100}%`,
}));

const bottomTextStyle = computed(() => ({
  opacity: Math.min((animationTime.value - 1.2) / 0.5, 1),
  transform: `translateY(${Math.max(0, (1.2 - animationTime.value) * 20)}px)`,
}));

const getModelStyle = (index: number) => ({
  opacity: Math.min((animationTime.value - (index * 0.1 + 0.5)) / 0.3, 1),
  transform: `translateX(${Math.max(0, (index * 0.1 + 0.5 - animationTime.value) * -50)}px)`,
});

const getFeatureStyle = (index: number) => ({
  opacity: Math.min((animationTime.value - (1 + index * 0.1)) / 0.3, 1),
  transform: `translateX(${Math.max(0, (1 + index * 0.1 - animationTime.value) * -30)}px)`,
});

const getStatStyle = (index: number) => ({
  opacity: Math.min((animationTime.value - (1.2 + index * 0.1)) / 0.3, 1),
  transform: `translateY(${Math.max(0, (1.2 + index * 0.1 - animationTime.value) * 20)}px)`,
});

const getLightColumnStyle = (i: number) => {
  const phase = (animationTime.value * (3 + i * 0.3)) % (2 * Math.PI);
  return {
    left: `${12.5 * i}%`,
    height: `${20 + Math.sin(phase) * 10}%`,
    opacity: 0.2 + Math.sin(phase) * 0.2,
  };
};
</script>


