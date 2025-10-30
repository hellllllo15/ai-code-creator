<template>
  <div class="absolute inset-0 overflow-hidden pointer-events-none opacity-40">
    <!-- 3D网格 -->
    <div class="absolute inset-0 cyber-grid" style="transform: perspective(1000px) rotateX(60deg) translateZ(-100px)" />
    
    <!-- 旋转几何图形 -->
    <div class="absolute top-1/4 right-1/4 w-32 h-32">
      <div class="relative w-full h-full animate-spin" style="animation-duration: 20s">
        <div class="absolute inset-0 border border-purple-500/30 rounded-lg" style="transform: rotateY(0deg) translateZ(20px)" />
        <div class="absolute inset-0 border border-purple-500/30 rounded-lg" style="transform: rotateY(90deg) translateZ(20px)" />
        <div class="absolute inset-0 border border-purple-500/30 rounded-lg" style="transform: rotateX(90deg) translateZ(20px)" />
      </div>
    </div>
  </div>
</template>
