<template>
  <div class="absolute inset-0 overflow-hidden pointer-events-none">
    <div
      v-for="(stream, i) in streams"
      :key="i"
      class="absolute left-0 right-0 h-px bg-gradient-to-r from-transparent via-emerald-400/30 to-transparent"
      :style="getStreamStyle(i)"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';

const streams = ref([0, 1, 2, 3, 4]);
const animationTime = ref(0);

onMounted(() => {
  const animate = () => {
    animationTime.value = Date.now() / 1000;
    requestAnimationFrame(animate);
  };
  animate();
});

const getStreamStyle = (index: number) => {
  const phase = (animationTime.value - index * 0.6) % 3;
  const x = (phase / 3) * 200 - 100;
  const opacity = Math.sin((phase / 3) * Math.PI);
  
  return {
    top: `${20 + index * 15}%`,
    transform: `translateX(${x}%)`,
    opacity: Math.max(0, opacity),
  };
};
</script>
