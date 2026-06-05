<template>
  <el-card class="system-card" shadow="hover">
    <template #header>
      <div class="card-header">
        <span class="title">
          <el-icon class="title-icon"><Setting /></el-icon>
          系统功能
        </span>
        <div class="view-more">
          查看更多<el-icon><ArrowRight /></el-icon>
        </div>
      </div>
    </template>
    <div class="system-grid">
      <div v-for="(item, index) in systems" :key="index" class="system-item">
        <div class="item-icon" :style="{ color: item.color }">
          <el-icon><component :is="item.icon" /></el-icon>
        </div>
        <span class="item-name">{{ item.name }}</span>
      </div>
    </div>
  </el-card>
</template>

<script lang="ts" setup>
import { markRaw, ref } from 'vue'
import { Setting, ArrowRight, User, Bell, Connection, PieChart } from '@element-plus/icons-vue'

const systems = ref([
  { name: '用户中心', icon: markRaw(User), color: '#409eff' },
  { name: '通知提醒', icon: markRaw(Bell), color: '#f56c6c' },
  { name: '插件管理', icon: markRaw(Connection), color: '#67c23a' },
  { name: '统计报表', icon: markRaw(PieChart), color: '#e6a23c' }
])
</script>

<style scoped>
.system-card {
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

.system-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
  padding: 10px 0;
}

.system-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  padding: 16px;
  border-radius: 16px;
  transition: all 0.3s;
}

.system-item:hover {
  background: rgba(255, 255, 255, 0.8);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  transform: translateY(-4px);
}

.item-icon {
  font-size: 28px;
  padding: 12px;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 12px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.04);
}

.item-name {
  font-size: 14px;
  color: #606266;
  font-weight: 500;
}

:deep(.el-card__header) {
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
  padding: 15px 20px;
}
</style>
