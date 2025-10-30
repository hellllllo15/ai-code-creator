<template>
  <div
    ref="dividerRef"
    @mousedown="handleMouseDown"
    :class="[
      'relative bg-gradient-to-b from-cyan-500/30 via-purple-500/30 to-pink-500/30 cursor-col-resize group hover:w-2 transition-all',
      isDragging ? 'w-2' : 'w-1'
    ]"
  >
    <!-- 发光效果 -->
    <div
      :class="[
        'absolute inset-0 bg-gradient-to-b from-cyan-500 via-purple-500 to-pink-500 transition-opacity',
        isDragging ? 'opacity-60' : 'opacity-0 group-hover:opacity-40'
      ]"
    />

    <!-- 拖动手柄 -->
    <div
      class="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-8 h-16 bg-gradient-to-br from-cyan-500 to-purple-500 rounded-lg flex items-center justify-center shadow-lg shadow-purple-500/50 border border-white/20 transition-all"
      :style="handleStyle"
    >
      <GripVertical class="w-5 h-5 text-white" />
    </div>

    <!-- 粒子效果 -->
    <div v-if="isDragging" class="absolute inset-0 overflow-hidden">
      <div
        v-for="i in 5"
        :key="i"
        class="absolute left-1/2 w-1 h-1 bg-cyan-400 rounded-full"
        :style="getParticleStyle(i)"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue';
import { GripVertical } from 'lucide-vue-next';

interface Props {
  initialLeftWidth?: number;
}

const props = withDefaults(defineProps<Props>(), {
  initialLeftWidth: 40,
});

const emit = defineEmits<{
  (e: 'resize', width: number): void;
}>();

const isDragging = ref(false);
const dividerRef = ref<HTMLDivElement | null>(null);

const handleStyle = computed(() => ({
  opacity: isDragging.value ? 1 : 0,
  transform: `translate(-50%, -50%) scale(${isDragging.value ? 1 : 0.8})`,
  transition: 'opacity 0.2s, transform 0.2s',
}));

const getParticleStyle = (index: number) => ({
  top: `${Math.random() * 100}%`,
  animation: `particleRise 1s ease-out infinite`,
  animationDelay: `${index * 0.2}s`,
});

const handleMouseMove = (e: MouseEvent) => {
  if (!isDragging.value) return;

  const containerWidth = window.innerWidth;
  const newLeftWidth = (e.clientX / containerWidth) * 100;

  // 限制最小和最大宽度
  if (newLeftWidth > 20 && newLeftWidth < 80) {
    emit('resize', newLeftWidth);
  }
};

const handleMouseUp = () => {
  isDragging.value = false;
  document.body.style.cursor = '';
  document.body.style.userSelect = '';
};

const handleMouseDown = () => {
  isDragging.value = true;
  document.body.style.cursor = 'col-resize';
  document.body.style.userSelect = 'none';
};

onMounted(() => {
  document.addEventListener('mousemove', handleMouseMove);
  document.addEventListener('mouseup', handleMouseUp);
});

onUnmounted(() => {
  document.removeEventListener('mousemove', handleMouseMove);
  document.removeEventListener('mouseup', handleMouseUp);
  // 清理样式
  document.body.style.cursor = '';
  document.body.style.userSelect = '';
});
</script>

<style scoped>
@keyframes particleRise {
  0% {
    transform: translateY(0);
    opacity: 1;
  }
  100% {
    transform: translateY(-100px);
    opacity: 0;
  }
}

/* 手柄悬停效果 */
.group:hover div {
  opacity: 1;
  transform: translate(-50%, -50%) scale(1);
}
</style>
