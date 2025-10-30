<template>
  <div class="space-y-8">
    <!-- 标题 -->
    <h2 class="text-4xl text-center font-bold bg-gradient-to-r from-purple-400 via-pink-400 to-blue-400 text-transparent bg-clip-text">
      精选 AI 生成项目
    </h2>

    <!-- 项目网格 -->
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
      <div
        v-for="(project, index) in projects"
        :key="project.id"
        class="project-card"
        :style="{ animationDelay: `${index * 0.15}s` }"
        @mouseenter="hoveredProject = project.id"
        @mouseleave="hoveredProject = null"
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
          <div class="relative h-48 overflow-hidden">
            <img
              :src="project.image"
              :alt="project.name"
              class="w-full h-full object-cover transition-transform duration-500 group-hover:scale-110"
            />
            <div class="absolute inset-0 bg-gradient-to-t from-black via-transparent to-transparent" />
            
            <!-- 悬停覆盖层 -->
            <div class="absolute inset-0 bg-purple-900/80 backdrop-blur-sm opacity-0 group-hover:opacity-100 transition-opacity duration-500 flex items-center justify-center">
              <div class="text-center p-4">
                <p class="text-white text-sm">{{ project.description }}</p>
                <button class="mt-4 px-4 py-2 bg-gradient-to-r from-purple-500 to-pink-500 rounded-lg text-white text-sm hover:scale-105 transition-transform">
                  查看详情
                </button>
              </div>
            </div>
          </div>

          <!-- 项目信息 -->
          <div class="p-6">
            <h3 class="text-xl mb-3 text-white group-hover:text-glow transition-all">
              {{ project.name }}
            </h3>
            
            <!-- 技术标签 -->
            <div class="flex flex-wrap gap-2">
              <span
                v-for="tech in project.techs"
                :key="tech"
                class="px-3 py-1 text-xs rounded-full bg-purple-500/20 text-purple-300 border border-purple-500/30"
              >
                {{ tech }}
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
import { ref } from 'vue'

interface Project {
  id: number
  name: string
  description: string
  image: string
  techs: string[]
}

const hoveredProject = ref<number | null>(null)

const projects: Project[] = [
  {
    id: 1,
    name: '现代登录界面',
    description: '具有渐变背景和动画效果的登录页面',
    image: 'https://images.unsplash.com/photo-1633356122544-f134324a6cee?w=400&h=300&fit=crop',
    techs: ['Vue 3', 'Tailwind', 'TypeScript']
  },
  {
    id: 2,
    name: '数据仪表板',
    description: '实时数据可视化管理后台',
    image: 'https://images.unsplash.com/photo-1551288049-bebda4e38f71?w=400&h=300&fit=crop',
    techs: ['React', 'Chart.js', 'Redux']
  },
  {
    id: 3,
    name: '电商平台',
    description: '功能完整的在线购物网站',
    image: 'https://images.unsplash.com/photo-1472851294608-062f824d29cc?w=400&h=300&fit=crop',
    techs: ['Next.js', 'Stripe', 'MongoDB']
  },
  {
    id: 4,
    name: '博客系统',
    description: '支持Markdown的现代博客平台',
    image: 'https://images.unsplash.com/photo-1499750310107-5fef28a66643?w=400&h=300&fit=crop',
    techs: ['Vue 3', 'Nuxt', 'Markdown']
  },
  {
    id: 5,
    name: '聊天应用',
    description: '实时消息传递和视频通话',
    image: 'https://images.unsplash.com/photo-1611606063065-ee7946f0787a?w=400&h=300&fit=crop',
    techs: ['React', 'Socket.io', 'WebRTC']
  },
  {
    id: 6,
    name: '个人作品集',
    description: '展示你的项目和技能',
    image: 'https://images.unsplash.com/photo-1467232004584-a241de8bcf5d?w=400&h=300&fit=crop',
    techs: ['Vue 3', 'GSAP', 'Three.js']
  }
]
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
