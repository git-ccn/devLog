<template>
  <div class="markdown-editor" :class="{ readonly }">
    <MdPreview
      v-if="readonly"
      class="markdown-preview-only"
      :model-value="modelValue"
      :theme="theme"
      language="zh-CN"
      preview-theme="default"
      code-theme="atom"
    />

    <div v-else class="markdown-tabs">
      <el-tabs v-if="!hidePreview" v-model="activeTab" class="tabs">
        <el-tab-pane label="编辑" name="edit" />
        <el-tab-pane label="预览" name="preview" />
      </el-tabs>

      <MdEditor
        v-if="hidePreview || activeTab === 'edit'"
        :model-value="modelValue"
        class="markdown-editor-inner"
        :style="editorStyle"
        :theme="theme"
        language="zh-CN"
        preview-theme="default"
        code-theme="atom"
        :preview="false"
        :toolbarsExclude="toolbarsExclude"
        :placeholder="placeholder"
        @update:model-value="updateValue"
      />

      <MdPreview
        v-else
        class="markdown-preview-only"
        :model-value="modelValue"
        :style="previewStyle"
        :theme="theme"
        language="zh-CN"
        preview-theme="default"
        code-theme="atom"
      />
    </div>
  </div>
</template>

<script lang="ts" setup>
import { MdEditor, MdPreview } from 'md-editor-v3'
import { computed, ref } from 'vue'
import 'md-editor-v3/lib/style.css'

const props = withDefaults(
  defineProps<{
    modelValue: string
    readonly?: boolean
    placeholder?: string
    hidePreview?: boolean
  }>(),
  {
    readonly: false,
    placeholder: '请输入 Markdown 内容',
    hidePreview: false
  }
)

const emit = defineEmits<{
  (e: 'update:modelValue', value: string): void
}>()

const activeTab = ref<'edit' | 'preview'>('edit')
const theme = computed<'light' | 'dark'>(() => 'light')
const editorStyle = {
  height: '520px'
}
const previewStyle = {
  height: '520px'
}

const toolbarsExclude = ['preview', 'preview-only', 'preview-html', 'fullscreen', 'catalog', 'save', 'github'] as any

const updateValue = (value: string) => {
  emit('update:modelValue', value)
}
</script>

<style scoped>
.markdown-editor {
  width: 100%;
  min-height: 0;
}

.markdown-tabs {
  width: 100%;
  min-height: 0;
  display: flex;
  flex-direction: column;
}

.tabs {
  margin-bottom: 12px;
}

.markdown-editor-inner,
.markdown-preview-only {
  width: 100%;
}

.markdown-editor-inner :deep(.md-editor) {
  height: 520px;
  border-radius: 18px;
  overflow: hidden;
}

.markdown-editor-inner :deep(.md-editor-toolbar) {
  border-radius: 18px 18px 0 0;
}

.markdown-editor-inner :deep(.md-editor-content),
.markdown-editor-inner :deep(.md-editor-input-wrapper),
.markdown-editor-inner :deep(.md-editor-preview-wrapper) {
  min-height: 0;
}

.markdown-preview-only :deep(.md-editor) {
  height: 520px;
  border-radius: 18px;
  overflow: hidden;
}

.markdown-preview-only :deep(.md-editor-preview-wrapper) {
  height: 100%;
  min-height: 0;
}

.markdown-preview-only :deep(.md-editor-preview) {
  height: 100%;
  overflow: auto;
  scrollbar-width: thin;
  scrollbar-color: rgba(148, 163, 184, 0.65) transparent;
}

.markdown-editor-inner :deep(.md-editor-custom-scrollbar__track) {
  background: transparent;
  opacity: 0;
  transition: opacity 0.2s ease;
  width: 6px;
}

.markdown-editor-inner :deep(.md-editor-custom-scrollbar__thumb) {
  width: 6px;
  border-radius: 999px;
}

.markdown-editor-inner :deep(.md-editor-custom-scrollbar:hover .md-editor-custom-scrollbar__track) {
  opacity: 1;
}

.markdown-preview-only :deep(.md-editor-preview::-webkit-scrollbar) {
  width: 6px;
  height: 6px;
}

.markdown-preview-only :deep(.md-editor-preview::-webkit-scrollbar-thumb) {
  background: rgba(148, 163, 184, 0.65);
  border-radius: 999px;
}

.markdown-preview-only :deep(.md-editor-preview::-webkit-scrollbar-track) {
  background: transparent;
}

.readonly .markdown-preview-only :deep(.md-editor-preview-wrapper) {
  padding: 0;
}

.readonly .markdown-preview-only :deep(.md-editor-preview) {
  background: transparent;
}
</style>
