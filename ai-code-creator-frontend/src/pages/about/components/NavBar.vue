<template>
  <nav class="relative z-20 border-b border-purple-500/30 backdrop-blur-xl bg-black/30">
    <div class="container mx-auto px-6 py-4">
      <div class="flex items-center justify-between">
        <!-- Logo -->
        <div class="flex items-center gap-3">
          <div class="relative">
            <div class="absolute inset-0 bg-gradient-to-r from-purple-600 to-blue-600 rounded-lg blur-lg opacity-75 animate-pulse" />
            <div class="relative w-12 h-12 bg-gradient-to-br from-purple-500 to-blue-500 rounded-lg flex items-center justify-center border border-purple-400/40">
              <Sparkles class="w-6 h-6 text-white" />
            </div>
          </div>
          <h1 class="text-2xl font-bold bg-gradient-to-r from-purple-400 via-pink-400 to-blue-400 text-transparent bg-clip-text">
            AI CODE PLATFORM
          </h1>
        </div>

        <!-- 导航菜单 -->
        <div class="hidden md:flex items-center gap-8">
          <a
            v-for="item in menuItems"
            :key="item.name"
            href="#"
            class="text-purple-200 hover:text-white transition-colors relative group"
          >
            {{ item.name }}
            <span class="absolute bottom-0 left-0 w-0 h-0.5 bg-gradient-to-r from-purple-400 to-blue-400 group-hover:w-full transition-all duration-300" />
          </a>
        </div>

        <!-- 登录按钮 -->
        <button class="relative px-6 py-2 bg-gradient-to-r from-purple-500 to-blue-500 rounded-lg text-white overflow-hidden group">
          <div class="absolute inset-0 bg-gradient-to-r from-blue-500 to-purple-500 opacity-0 group-hover:opacity-100 transition-opacity" />
          <span class="relative flex items-center gap-2">
            <Zap class="w-4 h-4" />
            开始使用
          </span>
        </button>
      </div>
    </div>
  </nav>
</template>

<script setup lang="ts">
import { Sparkles, Zap } from 'lucide-vue-next'

const menuItems = [
  { name: '首页', href: '#' },
  { name: '模板', href: '#templates' },
  { name: '文档', href: '#docs' },
  { name: '关于', href: '#about' },
]
</script>
