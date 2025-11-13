<template>
  <div class="space-y-8">
    <!-- 标题 -->
    <h2 class="text-4xl text-center font-bold bg-gradient-to-r from-purple-400 via-pink-400 to-blue-400 text-transparent bg-clip-text">
      我的项目
    </h2>

    <!-- 加载状态 -->
    <div v-if="loading" class="flex justify-center items-center py-12">
      <div class="w-8 h-8 border-4 border-purple-500 border-t-transparent rounded-full animate-spin"></div>
    </div>

    <!-- 空状态 -->
    <div v-else-if="!projects || projects.length === 0" class="text-center py-12">
      <p class="text-purple-300/70 text-lg">暂无项目，快去创建一个吧！</p>
    </div>

    <!-- 项目网格 -->
    <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
      <div
        v-for="(project, index) in projects"
        :key="project.id"
        class="project-card cursor-pointer"
        :style="{ animationDelay: `${index * 0.15}s` }"
        @mouseenter="hoveredProject = project.id"
        @mouseleave="hoveredProject = null"
        @click="handleProjectClick(project.id)"
      >
        <!-- 能量连接线 -->
        <div v-if="hoveredProject === project.id" class="energy-beam" />
        
        <!-- 卡片主体 -->
        <div class="relative h-full glass rounded-xl overflow-hidden border border-purple-500/30 group hover:border-purple-400/50 transition-all duration-500">
          <!-- 霓虹边框效果 -->
          <div class="absolute inset-0 opacity-0 group-hover:opacity-100 transition-opacity duration-500">
            <div class="absolute inset-0 border border-purple-400/40" />
          </div>

          <!-- 项目图片 -->
          <div class="relative h-48 overflow-hidden bg-gradient-to-br from-purple-900/50 to-pink-900/50">
            <img
              v-if="project.cover"
              :src="project.cover"
              :alt="project.appName"
              class="w-full h-full object-cover transition-transform duration-500 group-hover:scale-110"
              @error="handleImageError"
            />
            <div v-else class="w-full h-full flex items-center justify-center">
              <div class="text-purple-300/50 text-4xl">🚀</div>
            </div>
            <div class="absolute inset-0 bg-gradient-to-t from-black via-transparent to-transparent" />
            
            <!-- 悬停覆盖层 -->
            <div class="absolute inset-0 bg-purple-900/80 backdrop-blur-sm opacity-0 group-hover:opacity-100 transition-opacity duration-500 flex items-center justify-center">
              <div class="text-center p-4">
                <p class="text-white text-sm line-clamp-3">{{ project.initPrompt || '点击查看详情' }}</p>
              </div>
            </div>
          </div>

          <!-- 项目信息 -->
          <div class="p-6">
            <h3 class="text-xl mb-3 text-white group-hover:text-glow transition-all">
              {{ project.appName || '未命名项目' }}
            </h3>
            
            <!-- 技术标签 -->
            <div class="flex flex-wrap gap-2">
              <span
                v-if="project.codeGenType"
                class="px-3 py-1 text-xs rounded-full bg-purple-500/20 text-purple-300 border border-purple-500/30"
              >
                {{ getCodeGenTypeLabel(project.codeGenType) }}
              </span>
              <span
                v-if="project.createTime"
                class="px-3 py-1 text-xs rounded-full bg-purple-500/20 text-purple-300 border border-purple-500/30"
              >
                {{ formatDate(project.createTime) }}
              </span>
            </div>
          </div>

          <!-- 装饰性光点 -->
          <div class="absolute top-4 right-4 w-2 h-2 bg-purple-400/50 rounded-full animate-pulse" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { listMy } from '../../../api/appController'

// 定义项目接口类型
interface Project {
  id?: number | string // ID可能是字符串（经过响应拦截器转换）
  appName?: string
  cover?: string
  initPrompt?: string
  codeGenType?: string
  createTime?: string
}

const router = useRouter()
const hoveredProject = ref<number | null>(null)
const projects = ref<Project[]>([])
const loading = ref(true)

// 获取项目列表
const loadProjects = async () => {
  try {
    loading.value = true
    const response = await listMy({
      pageNum: 1,
      pageSize: 20,
      sortField: 'createTime',
      sortOrder: 'descend',
    })
    
    if (response.code === 0 && response.data) {
      // 处理项目列表，确保ID以字符串形式保存，避免精度丢失
      const records = response.data.records || []
      projects.value = records.map((project: any) => ({
        ...project,
        // 保持id为原值，但确保在传递时转换为字符串
        id: project.id !== undefined ? project.id : undefined,
      }))
    } else {
      console.error('获取项目列表失败:', response.message)
    }
  } catch (error: any) {
    console.error('获取项目列表错误:', error)
  } finally {
    loading.value = false
  }
}

// 点击项目跳转
const handleProjectClick = (appId: number | string | undefined) => {
  if (appId === undefined || appId === null) return
  
  // 确保appId是字符串格式（响应拦截器已将ID字段转换为字符串）
  const appIdStr = typeof appId === 'string' ? appId : String(appId)
  
  // 将 appId 写入 sessionStorage，避免出现在 URL 中
  sessionStorage.setItem('currentAppId', appIdStr)
  // 清理可能残留的 message
  sessionStorage.removeItem('currentAppMessage')
  
  // 跳转到干净的 /home 路径
  router.push({ path: '/home' })
}

// 图片加载错误处理
const handleImageError = (e: Event) => {
  const img = e.target as HTMLImageElement
  img.style.display = 'none'
}

// 获取代码生成类型标签
const getCodeGenTypeLabel = (codeGenType?: string) => {
  const map: Record<string, string> = {
    html: 'HTML',
    multi_file: '多文件',
    vue_project: 'Vue项目',
  }
  return map[codeGenType || ''] || codeGenType || '未知'
}

// 格式化日期
const formatDate = (dateStr?: string) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN')
}

onMounted(() => {
  loadProjects()
})
</script>

<style scoped>
.project-card {
  opacity: 0;
  animation: floatIn 0.8s ease-out forwards;
  transition: transform 0.5s ease;
}

.project-card:hover {
  transform: translateY(-10px);
}

.energy-beam {
  position: absolute;
  top: -100px;
  left: 50%;
  width: 2px;
  height: 100px;
  background: linear-gradient(to bottom, transparent, #a855f7, transparent);
  animation: beamPulse 1s ease-in-out infinite;
  z-index: -1;
}

@keyframes floatIn {
  from {
    opacity: 0;
    transform: translateY(50px) scale(0.95);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

@keyframes beamPulse {
  0%, 100% {
    opacity: 0.3;
  }
  50% {
    opacity: 1;
  }
}
</style>
