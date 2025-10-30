<template>
  <div class="absolute inset-0 overflow-hidden pointer-events-none opacity-20">
    <svg class="w-full h-full" xmlns="http://www.w3.org/2000/svg">
      <line
        v-for="(conn, index) in connections"
        :key="`line-${index}`"
        :x1="`${nodes[conn[0]].x}%`"
        :y1="`${nodes[conn[0]].y}%`"
        :x2="`${nodes[conn[1]].x}%`"
        :y2="`${nodes[conn[1]].y}%`"
        stroke="url(#line-gradient)"
        stroke-width="1"
        :style="getLineStyle(index)"
      />
      <circle
        v-for="(node, index) in nodes"
        :key="`node-${index}`"
        :cx="`${node.x}%`"
        :cy="`${node.y}%`"
        r="4"
        fill="#10b981"
        :style="getNodeStyle(index)"
      />
      <defs>
        <linearGradient id="line-gradient" x1="0%" y1="0%" x2="100%" y2="0%">
          <stop offset="0%" stop-color="#10b981" stop-opacity="0.2" />
          <stop offset="50%" stop-color="#3b82f6" stop-opacity="0.6" />
          <stop offset="100%" stop-color="#10b981" stop-opacity="0.2" />
        </linearGradient>
      </defs>
    </svg>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';

const nodes = [
  { x: 20, y: 20 },
  { x: 20, y: 50 },
  { x: 20, y: 80 },
  { x: 50, y: 30 },
  { x: 50, y: 50 },
  { x: 50, y: 70 },
  { x: 80, y: 40 },
  { x: 80, y: 60 },
];

const connections = [
  [0, 3], [0, 4], [0, 5],
  [1, 3], [1, 4], [1, 5],
  [2, 3], [2, 4], [2, 5],
  [3, 6], [3, 7],
  [4, 6], [4, 7],
  [5, 6], [5, 7],
];

const animationPhase = ref(0);

onMounted(() => {
  const animate = () => {
    animationPhase.value = (Date.now() / 1000) % 3;
    requestAnimationFrame(animate);
  };
  animate();
});

const getLineStyle = (index: number) => {
  const progress = Math.max(0, Math.min(1, (animationPhase.value - index * 0.1) / 2));
  return {
    strokeDasharray: '100',
    strokeDashoffset: 100 - progress * 100,
    opacity: Math.min(progress, 0.4),
  };
};

const getNodeStyle = (index: number) => {
  const delay = index * 0.1;
  const phase = (animationPhase.value - delay) % 3;
  let scale = 0;
  
  if (phase < 0.5) {
    scale = phase * 2.4; // 0 to 1.2
  } else if (phase < 1) {
    scale = 1.2 - (phase - 0.5) * 0.4; // 1.2 to 1
  } else {
    scale = 1;
  }
  
  return {
    transform: `scale(${Math.max(0, scale)})`,
    transformOrigin: 'center',
  };
};
</script>
