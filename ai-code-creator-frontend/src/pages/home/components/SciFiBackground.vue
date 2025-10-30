<template>
  <div 
    class="fixed inset-0 pointer-events-none" 
    style="z-index: -1; background: linear-gradient(to bottom, #030712, #0a1628); position: fixed;"
  >
    <canvas
      ref="canvasRef"
      class="absolute inset-0 w-full h-full"
      style="display: block; position: absolute;"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue';

const canvasRef = ref<HTMLCanvasElement | null>(null);

// 粒子类
class Particle {
  x: number;
  y: number;
  size: number;
  speedX: number;
  speedY: number;
  opacity: number;
  canvasWidth: number;
  canvasHeight: number;

  constructor(canvasWidth: number, canvasHeight: number) {
    this.canvasWidth = canvasWidth;
    this.canvasHeight = canvasHeight;
    this.x = Math.random() * canvasWidth;
    this.y = Math.random() * canvasHeight;
    this.size = Math.random() * 2 + 0.5;
    this.speedX = Math.random() * 0.5 - 0.25;
    this.speedY = Math.random() * 0.5 - 0.25;
    this.opacity = Math.random() * 0.5 + 0.3;
  }

  update() {
    this.x += this.speedX;
    this.y += this.speedY;

    if (this.x > this.canvasWidth) this.x = 0;
    if (this.x < 0) this.x = this.canvasWidth;
    if (this.y > this.canvasHeight) this.y = 0;
    if (this.y < 0) this.y = this.canvasHeight;
  }

  draw(ctx: CanvasRenderingContext2D) {
    // 添加粒子光晕效果
    ctx.shadowBlur = 8;
    ctx.shadowColor = 'rgba(99, 179, 237, 0.6)';
    ctx.fillStyle = `rgba(99, 179, 237, ${this.opacity})`;
    ctx.beginPath();
    ctx.arc(this.x, this.y, this.size, 0, Math.PI * 2);
    ctx.fill();
    ctx.shadowBlur = 0;
  }
}

onMounted(() => {
  const canvas = canvasRef.value;
  if (!canvas) return;

  const ctx = canvas.getContext('2d');
  if (!ctx) return;

  // 设置画布尺寸
  const resizeCanvas = () => {
    canvas.width = window.innerWidth;
    canvas.height = window.innerHeight;
  };
  resizeCanvas();
  window.addEventListener('resize', resizeCanvas);

  // 创建粒子 - 增加粒子数量以增强视觉效果
  const particles: Particle[] = [];
  for (let i = 0; i < 150; i++) {
    particles.push(new Particle(canvas.width, canvas.height));
  }

  // 网格线绘制函数
  const drawGrid = () => {
    ctx.strokeStyle = 'rgba(99, 179, 237, 0.15)'; // 增加网格线可见度
    ctx.lineWidth = 1;

    // 垂直线
    for (let x = 0; x < canvas.width; x += 50) {
      ctx.beginPath();
      ctx.moveTo(x, 0);
      ctx.lineTo(x, canvas.height);
      ctx.stroke();
    }

    // 水平线
    for (let y = 0; y < canvas.height; y += 50) {
      ctx.beginPath();
      ctx.moveTo(0, y);
      ctx.lineTo(canvas.width, y);
      ctx.stroke();
    }
  };

  // 动画循环
  let animationFrameId: number;
  const animate = () => {
    // 使用透明填充而不是完全不透明，让背景渐变可见
    ctx.clearRect(0, 0, canvas.width, canvas.height);

    drawGrid();

    particles.forEach((particle) => {
      particle.update();
      particle.draw(ctx);
    });

    // 连接附近的粒子
    particles.forEach((a, i) => {
      particles.slice(i + 1).forEach((b) => {
        const dx = a.x - b.x;
        const dy = a.y - b.y;
        const distance = Math.sqrt(dx * dx + dy * dy);

        if (distance < 120) {
          const opacity = 0.3 * (1 - distance / 120);
          ctx.strokeStyle = `rgba(99, 179, 237, ${opacity})`;
          ctx.lineWidth = 0.8;
          ctx.beginPath();
          ctx.moveTo(a.x, a.y);
          ctx.lineTo(b.x, b.y);
          ctx.stroke();
        }
      });
    });

    animationFrameId = requestAnimationFrame(animate);
  };

  animate();

  // 清理函数
  const cleanup = () => {
    window.removeEventListener('resize', resizeCanvas);
    if (animationFrameId) {
      cancelAnimationFrame(animationFrameId);
    }
  };

  onUnmounted(() => {
    cleanup();
  });
});
</script>

<style scoped>
/* Canvas样式已在template中定义 */
</style>
