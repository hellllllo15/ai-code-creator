<template>
  <div v-html="renderedMarkdown" class="markdown-body"></div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { marked } from 'marked'
import hljs from 'highlight.js'
import 'highlight.js/styles/github.css'

const props = defineProps<{ content?: string }>()

marked.setOptions({
  highlight: (code, lang) => {
    if (lang && hljs.getLanguage(lang)) {
      return hljs.highlight(code, { language: lang }).value
    }
    return hljs.highlightAuto(code).value
  }
})

const renderedMarkdown = computed(() =>
  props.content ? marked.parse(props.content) : ''
)
</script>

<style scoped>
.markdown-body {
  color: inherit;
  font-size: 15px;
  line-height: 1.75;
  background: transparent;
}

/* 确保所有文本元素都有足够的对比度 */
.markdown-body p,
.markdown-body span,
.markdown-body div {
  color: inherit;
}

.markdown-body pre {
  background: rgba(2, 6, 23, 0.9);
  border: 1px solid rgba(6, 182, 212, 0.4);
  border-left: 4px solid rgba(6, 182, 212, 0.8);
  padding: 16px;
  border-radius: 8px;
  overflow-x: auto;
  font-size: 14px;
  color: #e2e8f0;
  margin: 12px 0;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
}

.markdown-body code {
  background: rgba(6, 182, 212, 0.2);
  border: 1px solid rgba(6, 182, 212, 0.3);
  border-radius: 4px;
  padding: 3px 8px;
  font-size: 92%;
  font-family: Menlo, Monaco, Consolas, 'Courier New', monospace;
  color: #06b6d4;
}

.markdown-body pre code {
  background: transparent;
  border: none;
  padding: 0;
  color: #e2e8f0;
}

.markdown-body h1,
.markdown-body h2,
.markdown-body h3,
.markdown-body h4,
.markdown-body h5,
.markdown-body h6 {
  color: inherit;
  font-weight: 600;
}

.markdown-body strong,
.markdown-body b {
  color: inherit;
  font-weight: 600;
}

.markdown-body a {
  color: #06b6d4;
  text-decoration: underline;
}

.markdown-body a:hover {
  color: #22d3ee;
}

.markdown-body ul,
.markdown-body ol {
  color: inherit;
}

.markdown-body li {
  color: inherit;
}

.markdown-body blockquote {
  color: inherit;
  border-left: 3px solid rgba(6, 182, 212, 0.5);
  padding-left: 16px;
  margin-left: 0;
}
</style>
