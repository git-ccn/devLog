<template>
  <el-card class="note-card" shadow="hover">
    <template #header>
      <div class="card-header">
        <span class="title">
          <el-icon class="title-icon"><Notebook /></el-icon>
          技术笔记
        </span>
        <div class="view-more">
          查看更多<el-icon><ArrowRight /></el-icon>
        </div>
      </div>
    </template>
    <div class="note-list">
      <div v-for="(note, index) in notes" :key="index" class="note-item">
        <div class="note-icon" :style="{ background: note.color }">
          <el-icon><component :is="note.icon" /></el-icon>
        </div>
        <div class="note-info">
          <div class="note-title">{{ note.title }}</div>
          <div class="note-meta">{{ note.date }} · {{ note.category }}</div>
        </div>
        <el-tag :type="note.tagType" size="small" effect="light" class="note-tag">
          {{ note.tag }}
        </el-tag>
      </div>
    </div>
  </el-card>
</template>

<script lang="ts" setup>
import { markRaw, ref } from 'vue'
import { Notebook, ArrowRight, Pointer, Share, Collection } from '@element-plus/icons-vue'

const notes = ref([
  {
    title: 'Spring Cloud 微服务组件详解',
    date: '2024-03-05',
    category: '后端',
    icon: markRaw(Pointer),
    color: '#ff9c6e',
    tag: '已完结',
    tagType: 'success'
  },
  {
    title: 'Vue3 + TypeScript 最佳实践总结',
    date: '2024-03-02',
    category: '前端',
    icon: markRaw(Share),
    color: '#69c0ff',
    tag: '更新中',
    tagType: 'warning'
  },
  {
    title: 'MySQL 深度优化指南',
    date: '2024-02-28',
    category: '数据库',
    icon: markRaw(Collection),
    color: '#b37feb',
    tag: '草稿箱',
    tagType: 'info'
  }
])
</script>

<style scoped>
.note-card {
  height: 100%;
  border-radius: 16px;
  border: none;
  background: rgba(255, 255, 255, 0.7);
  backdrop-filter: blur(10px);
  box-shadow: 0 8px 32px 0 rgba(31, 38, 135, 0.07);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.title {
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 8px;
  color: #303133;
}

.title-icon {
  color: #409eff;
}

.view-more {
  font-size: 13px;
  color: #909399;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 2px;
  transition: color 0.3s;
}

.view-more:hover {
  color: #409eff;
}

.note-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.note-item {
  display: flex;
  align-items: center;
  padding: 12px;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.5);
  transition: all 0.3s;
  cursor: pointer;
}

.note-item:hover {
  background: #fff;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.note-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 20px;
  margin-right: 12px;
}

.note-info {
  flex: 1;
}

.note-title {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 4px;
}

.note-meta {
  font-size: 12px;
  color: #909399;
}

.note-tag {
  border-radius: 6px;
}

:deep(.el-card__header) {
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
  padding: 15px 20px;
}
</style>
