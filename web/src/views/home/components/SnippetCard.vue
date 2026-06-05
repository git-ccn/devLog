<template>
  <el-card class="snippet-card" shadow="hover">
    <template #header>
      <div class="card-header">
        <span class="title">
          <el-icon class="title-icon"><Document /></el-icon>
          代码片段
        </span>
        <div class="view-more">
          查看更多<el-icon><ArrowRight /></el-icon>
        </div>
      </div>
    </template>
    <div class="snippet-list">
      <div v-for="(item, index) in snippets" :key="index" class="snippet-item">
        <div class="item-left">
          <span class="snippet-title">{{ item.title }}</span>
        </div>
        <div class="item-right">
          <span class="lang">{{ item.lang }}</span>
          <el-icon class="copy-icon" @click="handleCopy(item.title)"><CopyDocument /></el-icon>
        </div>
      </div>
    </div>
  </el-card>
</template>

<script lang="ts" setup>
import { ref } from 'vue'
import { Document, ArrowRight, CopyDocument } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const snippets = ref([
  { title: '二分查找算法', lang: 'Java' },
  { title: '深拷贝函数', lang: 'JavaScript' },
  { title: 'JWT Token验证', lang: 'Java' },
  { title: '防抖函数', lang: 'JavaScript' },
  { title: 'MySQL分页查询', lang: 'SQL' }
])

const handleCopy = (title: string) => {
  ElMessage.success(`已复制: ${title}`)
}
</script>

<style scoped>
.snippet-card {
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
  font-weight: bold;
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

.snippet-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.snippet-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 14px 14px;
  border-radius: 14px;
  background: linear-gradient(90deg, rgba(222, 238, 255, 0.9) 0%, rgba(245, 250, 255, 0.9) 100%);
  box-shadow: 0 8px 18px rgba(64, 158, 255, 0.08);
  transition: all 0.3s;
}

.snippet-item:hover {
  background: linear-gradient(90deg, rgba(210, 231, 255, 0.95) 0%, rgba(242, 248, 255, 0.95) 100%);
  box-shadow: 0 10px 22px rgba(64, 158, 255, 0.12);
}

.item-left {
  display: flex;
  align-items: center;
  gap: 8px;
}

.snippet-title {
  font-size: 14px;
  color: #303133;
}

.item-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.lang {
  font-size: 12px;
  color: #606266;
  background: rgba(194, 220, 255, 0.75);
  padding: 6px 14px;
  border-radius: 10px;
  min-width: 60px;
  text-align: center;
}

.copy-icon {
  font-size: 16px;
  color: #409eff;
  cursor: pointer;
  transition: color 0.3s;
}

.copy-icon:hover {
  color: #409eff;
}

:deep(.el-card__header) {
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
  padding: 15px 20px;
}
</style>
