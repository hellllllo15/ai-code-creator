<template>
  <div class="absolute inset-0 overflow-hidden pointer-events-none opacity-60">
    <!-- 垂直扫描线 -->
    <div class="absolute top-0 bottom-0 w-0.5 bg-gradient-to-b from-transparent via-purple-400 to-transparent animate-scan shadow-[0_0_10px_rgba(168,85,247,0.5)]" />
    
    <!-- 水平扫描线 -->
    <div
      v-for="i in 3"
      :key="i"
      class="absolute left-0 right-0 h-0.5 bg-gradient-to-r from-transparent via-blue-400 to-transparent shadow-[0_0_10px_rgba(59,130,246,0.5)]"
      :style="{
        top: `${i * 33}%`,
        animation: `scanHorizontal ${4 + i}s linear infinite`,
        animationDelay: `${i * 0.5}s`
      }"
    />
  </div>
</template>

<style scoped>
@keyframes scanHorizontal {
  0% {
    transform: translateX(-100%);
  }
  100% {
    transform: translateX(100%);
  }
}
</style>
