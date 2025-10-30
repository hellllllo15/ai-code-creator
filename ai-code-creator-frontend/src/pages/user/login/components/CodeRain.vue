<template>
  <div class="absolute inset-0 overflow-hidden pointer-events-none">
    <div
      v-for="drop in drops"
      :key="drop.id"
      class="absolute flex flex-col gap-1"
      :style="getDropStyle(drop)"
    >
      <span
        v-for="(char, index) in drop.chars"
        :key="index"
        class="text-emerald-400/40 text-sm font-mono"
        :style="getCharStyle(index)"
      >
        {{ char }}
      </span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue';

interface CodeDrop {
  id: number;
  x: number;
  chars: string[];
  speed: number;
  delay: number;
  progress: number;
}

const drops = ref<CodeDrop[]>([]);
const codeChars = ['0', '1', '{', '}', '<', '>', '/', '*', '+', '=', ';', '(', ')', '[', ']', 'AI', 'ML', 'GPU'];

onMounted(() => {
  const newDrops = Array.from({ length: 30 }, (_, i) => ({
    id: i,
    x: Math.random() * 100,
    chars: Array.from({ length: 8 }, () => codeChars[Math.floor(Math.random() * codeChars.length)]),
    speed: 3 + Math.random() * 2,
    delay: Math.random() * 3,
    progress: 0,
  }));
  drops.value = newDrops;

  // Animation loop
  const animate = () => {
    drops.value.forEach(drop => {
      drop.progress += 0.016 / drop.speed;
      if (drop.progress > 1 + drop.delay / drop.speed) {
        drop.progress = -drop.delay / drop.speed;
      }
    });
    requestAnimationFrame(animate);
  };
  animate();
});

const getDropStyle = (drop: CodeDrop) => ({
  left: `${drop.x}%`,
  transform: `translateY(${drop.progress * 120}vh)`,
});

const getCharStyle = (index: number) => {
  const phase = (Date.now() / 1000) % 1;
  const opacity = Math.sin((phase + index * 0.1) * Math.PI * 2) * 0.5 + 0.5;
  return {
    opacity: opacity * 0.4,
  };
};
</script>
