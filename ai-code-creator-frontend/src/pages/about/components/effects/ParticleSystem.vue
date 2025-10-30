<template>
  <div class="absolute inset-0 overflow-hidden pointer-events-none">
    <div
      v-for="particle in particles"
      :key="particle.id"
      class="absolute text-purple-400/50 font-mono text-sm font-bold"
      :style="getParticleStyle(particle)"
    >
      {{ particle.char }}
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'

interface Particle {
  id: number
  x: number
  y: number
  char: string
  speed: number
  opacity: number
}

const particles = ref<Particle[]>([])
const codeChars = ['<', '>', '{', '}', '/', '*', '0', '1', 'AI', 'ML', 'GPU', '(', ')', '[', ']', ';']

let animationFrame: number

onMounted(() => {
  // 创建粒子
  particles.value = Array.from({ length: 50 }, (_, i) => ({
    id: i,
    x: Math.random() * 100,
    y: Math.random() * 100,
    char: codeChars[Math.floor(Math.random() * codeChars.length)],
    speed: 0.1 + Math.random() * 0.3,
    opacity: Math.random() * 0.3 + 0.4
  }))

  const animate = () => {
    particles.value.forEach(p => {
      p.y += p.speed
      if (p.y > 100) {
        p.y = -5
        p.x = Math.random() * 100
        p.char = codeChars[Math.floor(Math.random() * codeChars.length)]
      }
    })
    animationFrame = requestAnimationFrame(animate)
  }

  animate()
})

onUnmounted(() => {
  if (animationFrame) {
    cancelAnimationFrame(animationFrame)
  }
})

const getParticleStyle = (particle: Particle) => ({
  left: `${particle.x}%`,
  top: `${particle.y}%`,
  opacity: particle.opacity
})
</script>
