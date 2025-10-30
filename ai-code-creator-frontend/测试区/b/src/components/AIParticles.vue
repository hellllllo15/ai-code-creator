<template>
  <div class="absolute inset-0 overflow-hidden pointer-events-none">
    <div
      v-for="particle in particles"
      :key="particle.id"
      class="absolute rounded-full"
      :style="getParticleStyle(particle)"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';

interface Particle {
  id: number;
  x: number;
  y: number;
  size: number;
  duration: number;
  delay: number;
  phase: number;
}

const particles = ref<Particle[]>([]);

onMounted(() => {
  const newParticles = Array.from({ length: 60 }, (_, i) => ({
    id: i,
    x: Math.random() * 100,
    y: Math.random() * 100,
    size: Math.random() * 3 + 1,
    duration: Math.random() * 3 + 2,
    delay: Math.random() * 2,
    phase: 0,
  }));
  particles.value = newParticles;

  // Animation loop
  const animate = () => {
    const time = Date.now() / 1000;
    particles.value.forEach(particle => {
      particle.phase = ((time - particle.delay) / particle.duration) % 1;
    });
    requestAnimationFrame(animate);
  };
  animate();
});

const getParticleStyle = (particle: Particle) => {
  const opacity = Math.sin(particle.phase * Math.PI) * 0.8;
  const scale = Math.sin(particle.phase * Math.PI) * 1.5;
  
  return {
    left: `${particle.x}%`,
    top: `${particle.y}%`,
    width: `${particle.size}px`,
    height: `${particle.size}px`,
    background: 'linear-gradient(45deg, #10b981, #3b82f6)',
    opacity: Math.max(0, opacity),
    transform: `scale(${Math.max(0, scale)})`,
  };
};
</script>
