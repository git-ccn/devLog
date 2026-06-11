<template>
  <div class="practice-page">
    <header class="practice-header">
      <div class="header-left">
        <div class="header-title">刷题</div>
        <div class="header-meta">
          <span>共 {{ totalCount }} 题</span>
          <span>已掌握 {{ masteredCount }}</span>
          <span>学习中 {{ learningCount }}</span>
          <span>待整理 {{ todoCount }}</span>
        </div>
        <el-progress :percentage="progressPercent" :stroke-width="10" :show-text="false" class="header-progress" />
      </div>
      <div class="header-categories">
        <div class="category-pill" :class="{ active: query.categoryId === '' }" @click="query.categoryId = ''; applyFilters()">
          <span class="pill-dot"></span>
          全部
        </div>
        <div
          v-for="(c, idx) in categoryOptions"
          :key="c.id"
          class="category-pill"
          :class="{ active: query.categoryId === c.id }"
          :style="{ '--pill-hue': idx * 45 + 200 }"
          @click="query.categoryId = c.id; applyFilters()"
        >
          <span class="pill-dot" :style="{ background: `hsl(${idx * 45 + 200}, 65%, 58%)` }"></span>
          {{ c.name }}
        </div>
      </div>
      <div class="header-right">
        <el-button @click="pickRandom">随机一题</el-button>
        <el-button :disabled="!activeItem" @click="prevItem">上一题</el-button>
        <el-button :disabled="!activeItem" @click="nextItem">下一题</el-button>
      </div>
    </header>

    <section class="practice-body">
      <aside class="list-pane">
        <div class="list-toolbar">
          <el-input v-model="query.keyword" clearable placeholder="搜索题目" class="toolbar-input" @clear="applyFilters" @keyup.enter="applyFilters">
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
          <el-select v-model="query.status" placeholder="状态" class="toolbar-select" @change="applyFilters">
            <el-option v-for="s in statusOptions" :key="s.value" :label="s.label" :value="s.value" />
          </el-select>
          <el-select v-model="query.difficulty" placeholder="难度" class="toolbar-select" @change="applyFilters">
            <el-option v-for="d in difficultyOptions" :key="d.value" :label="d.label" :value="d.value" />
          </el-select>
        </div>

        <div class="list-meta">共 {{ items.length }} 题</div>

        <div ref="listRef" class="list-scroll">
          <VirtualList :items="items" :height="listHeight" :item-size="itemSize">
            <template #default="{ item }">
              <div class="problem-row" :class="{ active: item.id === activeId }" @click="selectItem(item)">
                <div class="problem-left">
                  <div class="problem-title">{{ item.title }}</div>
                  <div class="problem-sub">
                    <span>{{ item.category }}</span>
                    <span>{{ item.updatedAt }}</span>
                  </div>
                </div>
                <div class="problem-right">
                  <el-tag size="small" :type="statusMap[(item as InterviewItem).status].type">{{ statusMap[(item as InterviewItem).status].label }}</el-tag>
                  <el-tag size="small" effect="plain" :type="difficultyMap[(item as InterviewItem).difficulty].type">
                    {{ difficultyMap[(item as InterviewItem).difficulty].label }}
                  </el-tag>
                </div>
              </div>
            </template>
          </VirtualList>
        </div>
      </aside>

      <main class="work-pane">
        <el-card v-if="activeItem" class="work-card" shadow="never">
          <div class="work-top">
            <div class="work-title-row">
              <div class="work-title">{{ activeItem.title }}</div>
              <el-tag size="small" :type="statusMap[activeItem.status].type">{{ statusMap[activeItem.status].label }}</el-tag>
              <el-tag size="small" effect="plain" :type="difficultyMap[activeItem.difficulty].type">
                {{ difficultyMap[activeItem.difficulty].label }}
              </el-tag>
            </div>
            <div class="work-meta">
              <span>{{ activeItem.category }}</span>
              <span>{{ activeItem.updatedAt }}</span>
              <span v-if="activeItem.accuracy != null" class="work-accuracy">
                正确率 {{ activeItem.accuracy }}%
              </span>
              <span class="work-index">第 {{ activeIndex + 1 }} / {{ items.length }} 题</span>
            </div>
            <div class="work-actions">
              <el-button size="small" type="success" @click="setStatus(activeItem, 'mastered')">已掌握</el-button>
              <el-button size="small" type="warning" @click="setStatus(activeItem, 'learning')">学习中</el-button>
              <el-button size="small" @click="setStatus(activeItem, 'todo')">待整理</el-button>
              <el-button size="small" type="primary" plain @click="showAnswer = !showAnswer">
                {{ showAnswer ? '隐藏解析' : '显示解析' }}
              </el-button>
            </div>
          </div>

          <el-tabs v-model="activeTab" class="work-tabs">
            <el-tab-pane label="题目" name="question">
              <MarkdownEditor readonly :model-value="activeItem.question" />
              <div class="answer-section">
                <div class="answer-toolbar">
                  <el-button size="small" type="primary" :loading="submitting" @click="saveMyAnswer">
                    {{ submitting ? '评分中...' : '提交答案' }}
                  </el-button>
                  <div class="answer-tip">保存后会同步到服务器</div>
                </div>
                <MarkdownEditor v-model="myAnswerDraft" hide-preview placeholder="在这里写你的思路和答案（支持 Markdown）" />
              </div>
            </el-tab-pane>
            <el-tab-pane label="解析" name="solution">
              <div v-if="showAnswer">
                <MarkdownEditor readonly :model-value="activeItem.solution" />
              </div>
              <el-empty v-else description="点击上方“显示解析”查看参考答案" />
            </el-tab-pane>
          </el-tabs>
        </el-card>

        <div v-else class="work-empty">
          <el-icon class="work-empty-icon"><Notebook /></el-icon>
          <div class="work-empty-title">没有可刷的题目</div>
          <div class="work-empty-text">调整筛选条件，或清空搜索关键词</div>
        </div>
      </main>
    </section>
  </div>
</template>

<script lang="ts" setup>
import { computed, nextTick, onBeforeUnmount, onMounted, reactive, ref, watch } from 'vue'
import VirtualList from '@/components/VirtualList.vue'
import MarkdownEditor from '@/components/MarkdownEditor.vue'
import { Notebook, Search } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { getInterviewApi, addAnsApi, updateAnsApi, getCategoryApi, type InterviewCategoryRes, type InterviewQueryReq } from '@/api/interview'

type InterviewStatus = 'todo' | 'learning' | 'mastered'
type InterviewDifficulty = 'easy' | 'medium' | 'hard'
type TabKey = 'question' | 'solution'

type InterviewItem = {
  id: string
  title: string
  category: string
  status: InterviewStatus
  difficulty: InterviewDifficulty
  tags: string[]
  question: string
  solution: string
  myAnswer: string
  accuracy: number | null
  hasAns: boolean
  updatedAt: string
  createdAt: string
}

const normalizeDate = (d: Date) => {
  const y = d.getFullYear()
  const m = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  return `${y}-${m}-${day}`
}

const items = ref<InterviewItem[]>([])
const activeId = ref('')
const activeTab = ref<TabKey>('question')
const showAnswer = ref(false)
const myAnswerDraft = ref('')
const submitting = ref(false)

const query = reactive({
  keyword: '',
  status: '' as '' | InterviewStatus,
  difficulty: '' as '' | InterviewDifficulty,
  categoryId: ''
})

const statusMap: Record<InterviewStatus, { label: string; type: 'info' | 'warning' | 'success' }> = {
  todo: { label: '待整理', type: 'info' },
  learning: { label: '学习中', type: 'warning' },
  mastered: { label: '已掌握', type: 'success' }
}

const statusOptions = [
  { label: '全部', value: '' },
  { label: '待整理', value: 'todo' },
  { label: '学习中', value: 'learning' },
  { label: '已掌握', value: 'mastered' }
]

const difficultyMap: Record<InterviewDifficulty, { label: string; type: 'info' | 'warning' | 'danger' }> = {
  easy: { label: '简单', type: 'info' },
  medium: { label: '中等', type: 'warning' },
  hard: { label: '困难', type: 'danger' }
}

const difficultyOptions = [
  { label: '全部', value: '' },
  { label: '简单', value: 'easy' },
  { label: '中等', value: 'medium' },
  { label: '困难', value: 'hard' }
]

const totalCount = computed(() => items.value.length)
const masteredCount = computed(() => items.value.filter((i) => i.status === 'mastered').length)
const learningCount = computed(() => items.value.filter((i) => i.status === 'learning').length)
const todoCount = computed(() => items.value.filter((i) => i.status === 'todo').length)
const progressPercent = computed(() => (totalCount.value ? Math.round((masteredCount.value / totalCount.value) * 100) : 0))

const categoryOptions = ref<{ id: string; name: string }[]>([])

const activeItem = computed(() => items.value.find((i) => i.id === activeId.value) || items.value[0] || null)
const activeIndex = computed(() => items.value.findIndex((i) => i.id === (activeItem.value?.id || '')))

const loadFromServer = async () => {
  try {
    const req: InterviewQueryReq = {
      categoryId: query.categoryId || undefined,
      status: query.status || undefined,
      difficulty: query.difficulty || undefined,
      keyword: query.keyword || undefined
    }
    const res = await getInterviewApi(req)
    const list = (res as any)?.data
    if (Array.isArray(list)) {
      items.value = list.map((x: any) => ({
        id: String(x?.id ?? ''),
        title: String(x?.title ?? ''),
        category: String(x?.category ?? ''),
        categoryId: String(x?.categoryId ?? ''),
        status: (String(x?.status ?? 'todo') as InterviewStatus) || 'todo',
        difficulty: (String(x?.difficulty ?? 'easy') as InterviewDifficulty) || 'easy',
        tags: [],
        question: String(x?.question ?? ''),
        solution: String(x?.solution ?? ''),
        myAnswer: String(x?.myAnswer ?? ''),
        accuracy: x?.accuracy != null ? Number(x.accuracy) : null,
        hasAns: x?.myAnswer != null,
        updatedAt: String(x?.updatedAt ?? normalizeDate(new Date())),
        createdAt: String(x?.createdAt ?? normalizeDate(new Date()))
      }))
    } else {
      items.value = []
    }
  } catch {
    items.value = []
  }
}

const applyFilters = () => {
  loadFromServer().then(() => {
    activeId.value = items.value[0]?.id || ''
  })
}

const loadCategories = async () => {
  try {
    const res = await getCategoryApi()
    const list = (res as any)?.data as InterviewCategoryRes[] | undefined
    if (Array.isArray(list)) {
      categoryOptions.value = list
    }
  } catch {
    // 忽略
  }
}

const syncDraft = () => {
  myAnswerDraft.value = activeItem.value?.myAnswer || ''
  showAnswer.value = false
  activeTab.value = 'question'
}

const selectItem = (item: InterviewItem) => {
  activeId.value = item.id
  syncDraft()
}

const moveToIndex = (idx: number) => {
  const safe = Math.max(0, Math.min(items.value.length - 1, idx))
  const next = items.value[safe]
  if (!next) return
  activeId.value = next.id
  syncDraft()
}

const prevItem = () => {
  if (!items.value.length) return
  moveToIndex(Math.max(0, activeIndex.value - 1))
}

const nextItem = () => {
  if (!items.value.length) return
  moveToIndex(Math.min(items.value.length - 1, activeIndex.value + 1))
}

const pickRandom = () => {
  const n = items.value.length
  if (!n) return
  const idx = Math.floor(Math.random() * n)
  moveToIndex(idx)
}

const setStatus = (item: InterviewItem, status: InterviewStatus) => {
  const idx = items.value.findIndex((i) => i.id === item.id)
  if (idx < 0) return
  items.value[idx] = { ...items.value[idx], status, updatedAt: normalizeDate(new Date()) }
}

const saveMyAnswer = async () => {
  if (!activeItem.value || submitting.value) return
  const item = activeItem.value
  const idx = items.value.findIndex((i) => i.id === item.id)
  submitting.value = true
  try {
    let res: any
    if (item.hasAns) {
      res = await updateAnsApi({ interviewId: item.id, answer: myAnswerDraft.value })
    } else {
      res = await addAnsApi({ interviewId: item.id, answer: myAnswerDraft.value })
    }
    const evalData = (res as any)?.data
    if (idx >= 0) {
      items.value[idx] = {
        ...items.value[idx],
        myAnswer: myAnswerDraft.value,
        hasAns: true,
        accuracy: evalData?.accuracy ?? items.value[idx].accuracy,
        solution: evalData?.solution ?? items.value[idx].solution,
        updatedAt: normalizeDate(new Date())
      }
    }
    ElMessage.success('已保存作答')
  } catch {
    ElMessage.error('保存失败，请重试')
  } finally {
    submitting.value = false
  }
}

const listRef = ref<HTMLElement | null>(null)
const listHeight = ref(0)
const itemSize = 72

const updateListHeight = () => {
  listHeight.value = Math.max(0, listRef.value?.clientHeight || 0)
}

onMounted(async () => {
  await loadFromServer()
  await loadCategories()
  activeId.value = items.value[0]?.id || ''
  syncDraft()
  nextTick(updateListHeight)
  window.addEventListener('resize', updateListHeight)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', updateListHeight)
})

watch(
  () => items.value.length,
  () => {
    if (!items.value.length) {
      activeId.value = ''
      syncDraft()
      return
    }
    if (activeItem.value) return
    activeId.value = items.value[0]?.id || ''
    syncDraft()
  }
)
</script>

<style scoped>
.practice-page {
  height: calc(100% - 48px);
  padding: 20px 24px;
  display: flex;
  flex-direction: column;
  gap: 14px;
  min-height: 0;
}

.practice-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
}

.header-title {
  font-size: 18px;
  font-weight: 800;
  color: #111827;
}

.header-meta {
  margin-top: 6px;
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  color: #6b7280;
  font-size: 12px;
}

.header-categories {
  margin-top: 10px;
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.category-pill {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 5px 14px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 500;
  color: #6b7280;
  background: #f3f4f6;
  cursor: pointer;
  user-select: none;
  transition: all 0.22s ease;
  border: 1px solid transparent;
}

.category-pill:hover {
  color: #374151;
  background: #e5e7eb;
}

.category-pill.active {
  color: #fff;
  background: #409eff;
  border-color: #409eff;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.35);
}

.category-pill.active .pill-dot {
  background: #fff !important;
}

.pill-dot {
  width: 7px;
  height: 7px;
  border-radius: 50%;
  background: #9ca3af;
  flex-shrink: 0;
  transition: background 0.22s ease;
}

.header-progress {
  max-width: 520px;
  margin-top: 10px;
}

.practice-body {
  flex: 1;
  min-height: 0;
  display: grid;
  grid-template-columns: 360px 1fr;
  gap: 14px;
}

.list-pane {
  min-height: 0;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.9);
  box-shadow: 0 2px 14px rgba(17, 24, 39, 0.06);
  padding: 14px;
  display: flex;
  flex-direction: column;
}

.list-toolbar {
  display: grid;
  grid-template-columns: 1fr 110px;
  gap: 10px;
}

.toolbar-input {
  grid-column: 1 / span 2;
}

.toolbar-select {
  width: 100%;
}

.list-meta {
  margin-top: 10px;
  font-size: 12px;
  color: #9ca3af;
}

.list-scroll {
  margin-top: 10px;
  flex: 1;
  min-height: 0;
}

.problem-row {
  height: 72px;
  border-radius: 14px;
  padding: 10px 12px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  cursor: pointer;
  border: 1px solid transparent;
  transition: background 0.2s ease, border-color 0.2s ease;
}

.problem-row:hover {
  background: #f8fafc;
}

.problem-row.active {
  background: rgba(64, 158, 255, 0.12);
  border-color: rgba(64, 158, 255, 0.35);
}

.problem-left {
  min-width: 0;
}

.problem-title {
  font-size: 13px;
  font-weight: 600;
  color: #111827;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.problem-sub {
  margin-top: 6px;
  display: flex;
  gap: 10px;
  color: #9ca3af;
  font-size: 12px;
}

.problem-right {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-shrink: 0;
}

.work-pane {
  min-height: 0;
}

.work-card {
  height: 100%;
  border-radius: 18px;
}

.work-card :deep(.el-card__body) {
  text-align: left;
}

.work-card :deep(.el-tabs__content) {
  text-align: left;
}

.work-top {
  padding-bottom: 12px;
  border-bottom: 1px solid rgba(17, 24, 39, 0.06);
}

.work-title-row {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 10px;
}

.work-title {
  font-size: 18px;
  font-weight: 800;
  color: #111827;
}

.work-meta {
  margin-top: 10px;
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  color: #9ca3af;
  font-size: 12px;
}

.work-index {
  margin-left: auto;
}

.work-actions {
  margin-top: 12px;
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.work-tabs {
  margin-top: 12px;
}

.answer-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 10px;
}

.answer-section {
  margin-top: 24px;
  padding-top: 24px;
  border-top: 1px solid rgba(17, 24, 39, 0.06);
}

.answer-tip {
  color: #9ca3af;
  font-size: 12px;
}

.work-empty {
  height: 100%;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.9);
  box-shadow: 0 2px 14px rgba(17, 24, 39, 0.06);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
  gap: 8px;
  color: #9ca3af;
}

.work-empty-icon {
  font-size: 28px;
}

.work-empty-title {
  font-weight: 700;
  color: #374151;
}

.work-empty-text {
  font-size: 13px;
}

@media (max-width: 1400px) {
  .practice-body {
    grid-template-columns: 1fr;
  }
}
</style>
