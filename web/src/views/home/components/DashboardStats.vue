<template>
  <el-card class="dashboard-stats" shadow="hover">
    <template #header>
      <div class="card-header">
        <span class="title">
          <el-icon class="title-icon"><Odometer /></el-icon>
          仪表盘
        </span>
        <div class="view-more">
          查看更多<el-icon><ArrowRight /></el-icon>
        </div>
      </div>
    </template>
    <div class="stats-content">
      <!-- 学习进度 -->
      <div class="stat-section progress-section">
        <div class="section-title">学习度</div>
        <div class="chart-container">
          <div class="circular-progress">
            <div class="progress-inner">
              <span class="percentage">75%</span>
              <span class="label">完成进度</span>
            </div>
          </div>
          <div ref="barChartRef" class="echart-bar"></div>
        </div>
      </div>

      <div class="stat-section mastery-section">
        <div class="section-title">掌握状态</div>
        <div class="record-list">
          <div v-for="(item, index) in masteryStats" :key="index" class="record-item">
            <el-icon class="mastery-icon" :class="item.status">
              <component :is="statusIconMap[item.status]" />
            </el-icon>
            <span class="record-name">
              <span class="mastery-title">{{ item.label }}</span>
              <span v-if="item.desc" class="mastery-desc">{{ item.desc }}</span>
            </span>
            <span class="record-time">{{ item.count }}</span>
          </div>
        </div>
      </div>

      <!-- 待办事项 -->
      <div class="stat-section todo-section">
        <div class="section-title">
          待办事项
          <el-icon class="arrow-icon"><ArrowRight /></el-icon>
        </div>
        <div class="todo-list">
          <div v-for="(todo, index) in todos" :key="index" class="todo-item">
            <el-icon class="todo-icon"><component :is="todo.icon" /></el-icon>
            <span class="todo-text">{{ todo.text }}</span>
          </div>
        </div>
      </div>
    </div>
  </el-card>
</template>

<script lang="ts" setup>
import { markRaw, onBeforeUnmount, onMounted, ref } from 'vue'
import * as echarts from 'echarts'
import { Odometer, CircleCheck, Memo, ArrowRight, RefreshRight, StarFilled } from '@element-plus/icons-vue'

const barChartRef = ref<HTMLDivElement | null>(null)
let barChart: echarts.ECharts | null = null

const weekLabels = ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
const snippetAdds = [3, 5, 2, 6, 4, 2, 5]
const noteAdds = [1, 2, 1, 3, 2, 1, 3]

const renderBarChart = () => {
  if (!barChartRef.value) return

  if (!barChart) {
    barChart = echarts.init(barChartRef.value)
  }

  barChart.setOption(
    {
      tooltip: {
        trigger: 'axis',
        axisPointer: { type: 'shadow' }
      },
      legend: {
        data: ['代码片段新增数', '技术笔记新增数'],
        top: 0,
        right: 0,
        icon: 'roundRect',
        itemWidth: 10,
        itemHeight: 10,
        textStyle: {
          fontSize: 10,
          color: '#606266'
        }
      },
      grid: {
        top: 30,
        left: 0,
        right: 0,
        bottom: 0,
        containLabel: true
      },
      xAxis: {
        type: 'category',
        data: weekLabels,
        axisTick: { show: false },
        axisLine: { show: false },
        axisLabel: { fontSize: 10, color: '#909399' }
      },
      yAxis: {
        type: 'value',
        axisLabel: { show: false },
        axisTick: { show: false },
        axisLine: { show: false },
        splitLine: { show: false }
      },
      series: [
        {
          name: '代码片段新增数',
          type: 'bar',
          data: snippetAdds,
          barWidth: 10,
          itemStyle: { color: '#409eff', borderRadius: [6, 6, 0, 0] }
        },
        {
          name: '技术笔记新增数',
          type: 'bar',
          data: noteAdds,
          barWidth: 10,
          itemStyle: { color: '#67c23a', borderRadius: [6, 6, 0, 0] }
        }
      ]
    },
    { notMerge: true }
  )
}

const handleResize = () => {
  barChart?.resize()
}

onMounted(() => {
  renderBarChart()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  barChart?.dispose()
  barChart = null
})

type MasteryStatus = 'unknown' | 'reviewing' | 'mastered'

const statusIconMap: Record<MasteryStatus, unknown> = {
  unknown: markRaw(StarFilled),
  reviewing: markRaw(RefreshRight),
  mastered: markRaw(CircleCheck)
}

const masteryStats = ref([
  { label: '未掌握', desc: '需要重点复习', count: 12, status: 'unknown' as const },
  { label: '复习中', desc: '看过但还不熟', count: 7, status: 'reviewing' as const },
  { label: '已掌握', desc: '', count: 25, status: 'mastered' as const }
])

const todos = ref([
  { text: '复习 Spring AOP', icon: markRaw(Memo) },
  { text: '整理 MySQL 索引笔记', icon: markRaw(Memo) },
  { text: '准备明天的面试', icon: markRaw(Memo) }
])
</script>

<style scoped>
.dashboard-stats {
  height: 100%;
  border-radius: 16px;
  border: none;
  background: rgba(255, 255, 255, 0.7);
  backdrop-filter: blur(10px);
  box-shadow: 0 8px 32px 0 rgba(31, 38, 135, 0.07);
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 15px 20px;
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

.stats-content {
  display: grid;
  grid-template-columns: 1.5fr 1fr 1fr;
  gap: 30px;
}

.section-title {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.chart-container {
  display: flex;
  align-items: center;
  gap: 20px;
  /* padding-bottom: 30px; */
}

.circular-progress {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  background: conic-gradient(#409eff 75%, #f0f2f5 0);
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}

.circular-progress::after {
  content: '';
  position: absolute;
  width: 80px;
  height: 80px;
  background: #fff;
  border-radius: 50%;
}

.progress-inner {
  position: relative;
  z-index: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.percentage {
  font-size: 18px;
  font-weight: 700;
  color: #303133;
}

.label {
  font-size: 10px;
  color: #909399;
}

.echart-bar {
  flex: 1;
  height: 150px;
}

.record-list, .todo-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.record-item, .todo-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px 12px;
  background: rgba(255, 255, 255, 0.5);
  border-radius: 8px;
  font-size: 13px;
}

.check-icon {
  color: #67c23a;
}

.record-name {
  flex: 1;
  color: #606266;
}

.mastery-title {
  color: #303133;
}

.mastery-desc {
  margin-left: 10px;
  color: #909399;
  font-size: 12px;
}

.mastery-icon {
  font-size: 16px;
}

.mastery-icon.unknown {
  color: #e6a23c;
}

.mastery-icon.reviewing {
  color: #409eff;
}

.mastery-icon.mastered {
  color: #67c23a;
}

.record-time {
  color: #909399;
  font-size: 11px;
}

.todo-icon {
  color: #409eff;
}

.todo-text {
  color: #606266;
}

.arrow-icon {
  font-size: 12px;
  color: #c0c4cc;
}

:deep(.el-card__header) {
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
  padding: 15px 20px;
}
</style>
