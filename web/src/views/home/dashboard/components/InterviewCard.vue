<template>
  <el-card class="interview-card" shadow="hover">
    <template #header>
      <div class="card-header">
        <span class="title">
          <el-icon class="title-icon"><ChatLineSquare /></el-icon>
          面试题库
        </span>
        <div class="view-more">
          查看更多<el-icon><ArrowRight /></el-icon>
        </div>
      </div>
    </template>
    <div class="interview-content">
      <div class="category-list">
        <div v-for="(q, index) in questions" :key="index" class="category-item">
          <span class="dot"></span>
          <span class="name">
            <span class="question-title">{{ q.title }}</span>
            <span class="question-meta">
              （{{ q.topic }}，
              <el-icon class="status-icon" :class="q.status"><component :is="statusIconMap[q.status]" /></el-icon>
              ）
            </span>
          </span>
          <el-icon class="arrow"><ArrowRight /></el-icon>
        </div>
      </div>
      <div class="actions">
        <el-button type="primary" class="random-btn">
          <el-icon><Aim /></el-icon>
          随机抽一道
        </el-button>
      </div>
    </div>
  </el-card>
</template>

<script lang="ts" setup>
import { markRaw, onMounted, ref } from 'vue'
import { ChatLineSquare, ArrowRight, Aim, StarFilled, RefreshRight, CircleCheck } from '@element-plus/icons-vue'
import { getInterviewApi, type InterviewRes } from '@/api/interview'

type QuestionStatus = 'unknown' | 'reviewing' | 'mastered'

interface QuestionItem {
  title: string
  topic: string
  status: QuestionStatus
}

const questions = ref<QuestionItem[]>([])

const statusMap: Record<string, QuestionStatus> = {
  todo: 'unknown',
  learning: 'reviewing',
  mastered: 'mastered'
}

const statusIconMap: Record<QuestionStatus, unknown> = {
  unknown: markRaw(StarFilled),
  reviewing: markRaw(RefreshRight),
  mastered: markRaw(CircleCheck)
}

onMounted(async () => {
  try {
    const res = await getInterviewApi()
    const list = (res as any)?.data as InterviewRes[] | undefined
    if (Array.isArray(list) && list.length > 0) {
      questions.value = list.slice(0, 5).map((q) => ({
        title: q.title,
        topic: q.category || '',
        status: statusMap[q.status] || 'unknown'
      }))
    }
  } catch {
    // 接口不可用时保持为空
  }
})
</script>

<style scoped>
.interview-card {
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

.interview-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.category-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.category-item {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 14px;
  color: #606266;
  cursor: pointer;
  padding: 4px 0;
  transition: color 0.2s;
}

.category-item:hover {
  color: #409eff;
}

.dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #dcdfe6;
}

.category-item:hover .dot {
  background: #409eff;
}

.name {
  flex: 1;
}

.question-title {
  color: #303133;
}

.question-meta {
  color: #606266;
  margin-left: 6px;
}

.status-icon {
  font-size: 14px;
  vertical-align: -2px;
}

.status-icon.unknown {
  color: #e6a23c;
}

.status-icon.reviewing {
  color: #409eff;
}

.status-icon.mastered {
  color: #67c23a;
}

.arrow {
  font-size: 12px;
  color: #c0c4cc;
}

.actions {
  display: flex;
  gap: 12px;
  align-items: center;
}

.random-btn {
  flex: 1;
  border-radius: 12px;
  height: 40px;
  font-weight: 600;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
}

.more-btn {
  width: 40px;
  height: 40px;
  border-radius: 12px;
}

:deep(.el-card__header) {
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
  padding: 15px 20px;
}
</style>
