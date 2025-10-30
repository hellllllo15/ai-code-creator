<template>
  <div class="code-gen-selector">
    <div class="flex items-center gap-3 flex-wrap">
      <span class="text-purple-200 text-sm font-medium">生成模式：</span>
      <div class="flex gap-2">
        <button
          v-for="option in options"
          :key="option.value"
          @click="selectType(option.value)"
          :class="[
            'px-4 py-2 rounded-lg border transition-all text-sm font-medium',
            selectedValue === option.value
              ? 'bg-gradient-to-r from-purple-500 to-pink-500 border-purple-400 text-white shadow-lg shadow-purple-500/50'
              : 'bg-slate-900/50 border-purple-500/30 text-purple-200 hover:border-purple-400/50 hover:bg-slate-900/70'
          ]"
        >
          {{ option.label }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

export type CodeGenType = 'html' | 'multi_file' | 'vue_project'

interface CodeGenOption {
  label: string
  value: CodeGenType
  description: string
}

const props = defineProps<{
  modelValue?: CodeGenType
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', value: CodeGenType): void
}>()

const options: CodeGenOption[] = [
  { label: 'HTML', value: 'html', description: '生成单文件 HTML 代码' },
  { label: '多文件', value: 'multi_file', description: '生成多个文件的代码结构' },
  { label: 'Vue 项目', value: 'vue_project', description: '生成完整的 Vue 项目结构' },
]

const selectedValue = ref<CodeGenType>(props.modelValue || 'multi_file')

const selectType = (value: CodeGenType) => {
  selectedValue.value = value
  emit('update:modelValue', value)
}
</script>

<style scoped>
.code-gen-selector {
  padding: 1rem 1.5rem;
  background: rgba(30, 41, 59, 0.3);
  backdrop-filter: blur(10px);
  border-radius: 0.75rem;
  border: 1px solid rgba(168, 85, 247, 0.2);
}
</style>

