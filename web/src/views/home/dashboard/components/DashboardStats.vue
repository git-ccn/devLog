<template>
  <el-card class="dashboard-stats" shadow="hover">
    <template #header>
      <div class="card-header">
        <span class="title">
          <el-icon class="title-icon"><Odometer /></el-icon>
          仪表盘
        </span>
      </div>
    </template>
    <div class="stats-content">
      <!-- 学习进度 -->
      <div class="stat-section progress-section">
        <div class="section-title">学习度</div>
        <div class="chart-container">
          <div class="circular-progress" :style="`background: conic-gradient(#409eff ${progressPercent}%, #f0f2f5 0)`">
            <div class="progress-inner">
              <span class="percentage">{{ progressPercent }}%</span>
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
          <el-icon class="add-icon" @click="openTodoDialog()"><Plus /></el-icon>
        </div>
        <div class="todo-list">
          <div v-for="item in todoItems" :key="item.id" class="todo-item">
            <el-icon class="todo-status-icon" :class="item.status" @click="toggleTodoStatus(item)">
              <component :is="item.status === 'done' ? CircleCheck : CircleCheck" />
            </el-icon>
            <span class="todo-text" :class="{ done: item.status === 'done' }" @click="openTodoDialog(item)">{{ item.title }}</span>
            <el-icon class="todo-del-icon" @click="handleDeleteTodo(item)"><Delete /></el-icon>
          </div>
          <div v-if="!todoItems.length" class="todo-empty">暂无待办</div>
        </div>
      </div>
    </div>
  </el-card>

  <!-- 待办事项新增/编辑弹窗 -->
  <el-dialog v-model="todoDialogVisible" :title="editingTodo ? '编辑待办' : '新增待办'" width="420px" destroy-on-close>
    <el-input v-model="todoForm.title" placeholder="输入待办事项" maxlength="200" show-word-limit @keyup.enter="handleSaveTodo" />
    <template #footer>
      <el-button @click="todoDialogVisible = false">取消</el-button>
      <el-button type="primary" :disabled="!todoForm.title.trim()" @click="handleSaveTodo">保存</el-button>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import { markRaw, nextTick, onBeforeUnmount, onMounted, ref } from 'vue'
import * as echarts from 'echarts'
import { Odometer, CircleCheck, RefreshRight, StarFilled, Plus, Delete } from '@element-plus/icons-vue'
import { getDashboardStatsApi, type DashboardWeeklyItem } from '@/api/dashboard'
import { getTodosApi, addTodoApi, updateTodoApi, deleteTodoApi, type TodoRes } from '@/api/todo'
import { ElMessage, ElMessageBox } from 'element-plus'

const barChartRef = ref<HTMLDivElement | null>(null)
let barChart: echarts.ECharts | null = null


const renderBarChart = () => {
  if (!barChartRef.value) return

  const items = weeklyItems.value
  const labels = items.length > 0 ? items.map((i) => i.day) : ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
  const snippetData = items.length > 0 ? items.map((i) => i.snippetCount) : [0, 0, 0, 0, 0, 0, 0]
  const noteData = items.length > 0 ? items.map((i) => i.noteCount) : [0, 0, 0, 0, 0, 0, 0]

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
        data: labels,
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
          data: snippetData,
          barWidth: 10,
          itemStyle: { color: '#409eff', borderRadius: [6, 6, 0, 0] }
        },
        {
          name: '技术笔记新增数',
          type: 'bar',
          data: noteData,
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

const progressPercent = ref(0)
const weeklyItems = ref<DashboardWeeklyItem[]>([])
const masteryStats = ref([
  { label: '未掌握', desc: '需要重点复习', count: 0, status: 'unknown' as const },
  { label: '复习中', desc: '看过但还不熟', count: 0, status: 'reviewing' as const },
  { label: '已掌握', desc: '', count: 0, status: 'mastered' as const }
])

const loadStats = async () => {
  try {
    const res = await getDashboardStatsApi()
    if ((res as any)?.code === 200) {
      const data = (res as any).data
      // 学习度
      progressPercent.value = data.progress ?? 0
      // 周活跃
      if (Array.isArray(data.weekly)) {
        weeklyItems.value = data.weekly
      }
      // 掌握状态
      if (data.mastery) {
        masteryStats.value[0].count = data.mastery.todo ?? 0
        masteryStats.value[1].count = data.mastery.learning ?? 0
        masteryStats.value[2].count = data.mastery.mastered ?? 0
      }
      // 重新渲染图表
      nextTick(() => renderBarChart())
    }
  } catch {}
}

onMounted(() => {
  void loadStats()
  void loadTodos()
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

// ====== 待办事项 ======
const todoItems = ref<TodoRes[]>([])
const todoDialogVisible = ref(false)
const editingTodo = ref<TodoRes | null>(null)
const todoForm = ref({ title: '' })

const loadTodos = async () => {
  try {
    const res = await getTodosApi()
    if ((res as any)?.code === 200) {
      todoItems.value = Array.isArray((res as any).data) ? (res as any).data : []
    }
  } catch {}
}

const openTodoDialog = (todo?: TodoRes) => {
  editingTodo.value = todo || null
  todoForm.value.title = todo?.title || ''
  todoDialogVisible.value = true
}

const handleSaveTodo = async () => {
  const title = todoForm.value.title.trim()
  if (!title) return
  try {
    if (editingTodo.value) {
      await updateTodoApi({ id: editingTodo.value.id, title })
      ElMessage.success('已更新')
    } else {
      await addTodoApi({ title })
      ElMessage.success('已添加')
    }
    todoDialogVisible.value = false
    await loadTodos()
  } catch {
    ElMessage.error('操作失败')
  }
}

const toggleTodoStatus = async (item: TodoRes) => {
  const newStatus = item.status === 'done' ? 'pending' : 'done'
  try {
    await updateTodoApi({ id: item.id, title: item.title, status: newStatus })
    await loadTodos()
  } catch {
    ElMessage.error('操作失败')
  }
}

const handleDeleteTodo = async (item: TodoRes) => {
  try {
    await ElMessageBox.confirm('确定删除该待办？', '提示', { type: 'warning' })
    await deleteTodoApi(item.id)
    ElMessage.success('已删除')
    await loadTodos()
  } catch {}
}
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

.todo-section {
  min-height: 0;
  display: flex;
  flex-direction: column;
  overflow: hidden;
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
  overflow-y: auto;
  min-height: 0;
  max-height: 130px;
}

.record-item, .todo-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px 12px;
  background: rgba(255, 255, 255, 0.5);
  border-radius: 8px;
  font-size: 13px;
  text-align: left;
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

.todo-status-icon {
  font-size: 18px;
  color: #909399;
  cursor: pointer;
  transition: color 0.2s;
  flex-shrink: 0;
}

.todo-status-icon.done {
  color: #67c23a;
}

.todo-status-icon:hover {
  color: #67c23a;
}

.todo-text {
  color: #606266;
  flex: 1;
  cursor: pointer;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.todo-text.done {
  color: #c0c4cc;
  text-decoration: line-through;
}

.todo-del-icon {
  font-size: 14px;
  color: #c0c4cc;
  cursor: pointer;
  flex-shrink: 0;
  transition: color 0.2s;
}

.todo-del-icon:hover {
  color: #f56c6c;
}

.todo-empty {
  font-size: 13px;
  color: #c0c4cc;
  text-align: center;
  padding: 12px 0;
}

.add-icon {
  font-size: 16px;
  color: #409eff;
  cursor: pointer;
  transition: color 0.2s;
}

.add-icon:hover {
  color: #66b1ff;
}

:deep(.el-card__header) {
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
  padding: 15px 20px;
}
</style>
