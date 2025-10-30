<template>
  <div class="absolute inset-0 overflow-hidden pointer-events-none">
    <!-- 柔和的渐变光晕 -->
    <div class="absolute top-1/4 -left-1/4 w-1/2 h-1/2 bg-purple-600/30 rounded-full blur-3xl animate-pulse-slow" />
    <div class="absolute bottom-1/4 -right-1/4 w-1/2 h-1/2 bg-blue-600/30 rounded-full blur-3xl animate-pulse-slow" style="animation-delay: 1s" />
    <div class="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-1/3 h-1/3 bg-pink-600/30 rounded-full blur-3xl animate-pulse-slow" style="animation-delay: 2s" />
  </div>
</template>
