<template>
  <div class="notes-page">
    <div class="notes-layout">
      <aside class="sidebar">
        <el-card class="side-card" shadow="never">
          <div class="side-title">分类</div>
          <div class="side-list">
            <div class="side-item" :class="{ active: query.categoryId === '' }" @click="query.categoryId = ''">
              <span>全部分类</span>
              <span class="side-count">{{ allCategoryCount }}</span>
            </div>
            <div
              v-for="category in categoryItems"
              :key="category.id || category.name"
              class="side-item"
              :class="{ active: query.categoryId === (category.id || category.name) }"
              @click="query.categoryId = category.id || category.name"
            >
              <span>{{ category.name }}</span>
              <span class="side-count">{{ category.count ?? 0 }}</span>
            </div>
          </div>
        </el-card>

        <el-card class="side-card" shadow="never">
          <div class="side-title">状态</div>
          <div class="status-list">
            <el-tag
              v-for="status in statusOptions"
              :key="status.value"
              class="status-chip"
              :effect="query.status === status.value ? 'dark' : 'plain'"
              :type="status.type"
              @click="query.status = status.value"
            >
              {{ status.label }}
            </el-tag>
          </div>
        </el-card>

        <el-card class="side-card" shadow="never">
          <div class="side-title">学习轨迹</div>
          <div class="timeline">
            <div v-for="item in timelineItems" :key="item.label" class="timeline-item">
              <div class="timeline-dot" />
              <div class="timeline-content">
                <div class="timeline-label">{{ item.label }}</div>
                <div class="timeline-value">{{ item.value }}</div>
              </div>
            </div>
          </div>
        </el-card>
      </aside>

      <section class="content">
        <div class="stats-grid">
          <el-card v-for="item in statsCards" :key="item.label" class="stat-card" shadow="never">
            <div class="stat-top">
              <div class="stat-icon" :style="{ background: item.bgColor, color: item.color }">
                <el-icon><component :is="item.icon" /></el-icon>
              </div>
              <div class="stat-trend" :style="{ color: item.color }">{{ item.tip }}</div>
            </div>
            <div class="stat-value">{{ item.value }}</div>
            <div class="stat-label">{{ item.label }}</div>
          </el-card>
        </div>

        <el-card class="main-card" shadow="never">
          <div class="toolbar">
            <div class="toolbar-left">
              <el-input v-model="query.keyword" placeholder="搜索标题" class="search" clearable>
                <template #prefix>
                  <el-icon><Search /></el-icon>
                </template>
              </el-input>

              <div class="tag-filter-area">
                <el-popover placement="bottom-start" trigger="click" :width="320">
                  <template #reference>
                    <el-button class="tag-filter-btn" plain>
                      <el-icon><CollectionTag /></el-icon>
                      标签
                      <span v-if="query.tagIds.length" class="tag-filter-count">({{ query.tagIds.length }})</span>
                    </el-button>
                  </template>

                  <div class="tag-pop">
                    <el-input v-model="tagSearch" size="small" placeholder="搜索标签" clearable />
                    <el-scrollbar max-height="240" class="tag-pop-scroll">
                      <el-checkbox-group v-model="query.tagIds" class="tag-check-group">
                        <el-checkbox v-for="tag in filteredTagItems" :key="tag.id" :label="tag.id">{{ tag.name }}</el-checkbox>
                      </el-checkbox-group>
                    </el-scrollbar>
                    <div class="tag-pop-actions">
                      <el-button link type="primary" @click="query.tagIds = []">清空</el-button>
                    </div>
                  </div>
                </el-popover>

                <div v-if="query.tagIds.length" class="tag-selected">
                  <el-tag
                    v-for="tag in visibleSelectedTagItems"
                    :key="tag.id"
                    size="small"
                    effect="plain"
                    closable
                    class="tag-selected-item"
                    @close="removeTag(tag.id)"
                  >
                    {{ tag.name }}
                  </el-tag>
                  <el-tag v-if="hiddenSelectedTagCount > 0" size="small" effect="plain" class="tag-selected-more">
                    +{{ hiddenSelectedTagCount }}
                  </el-tag>
                </div>
              </div>
            </div>

            <div class="toolbar-right">
              <el-button @click="resetQuery">重置筛选</el-button>
              <el-button type="primary" @click="startCreate()">
                <el-icon><DocumentAdd /></el-icon>
                写笔记
              </el-button>
            </div>
          </div>

          <div class="main-body">
            <div class="note-list">
              <div class="list-header">
                <div class="list-title">笔记列表</div>
                <div class="list-subtitle">共 {{ filteredNotes.length }} 条</div>
              </div>

              <div v-if="filteredNotes.length" ref="listRef" class="list-scroll">
                <VirtualList :items="filteredNotes" :height="listHeight" :item-size="noteItemSize">
                  <template #default="{ item: note }">
                    <div class="note-item-wrap">
                      <el-card
                        :key="note.id"
                        shadow="hover"
                        class="note-card"
                        :class="{ active: activeNote?.id === note.id }"
                        @click="selectNote(note)"
                      >
                        <div class="note-card-head">
                          <div class="note-main">
                            <div class="note-title-row">
                              <div class="note-title">{{ note.title }}</div>
                                <el-tag size="small" :type="statusMap[note.status as NoteStatus].type">{{ statusMap[note.status as NoteStatus].label }}</el-tag>
                            </div>
                            <div class="note-summary">{{ note.summary }}</div>
                          </div>
                          <div class="note-side">
                            <div class="note-read">{{ note.readTime }} min</div>
                            <el-popconfirm
                              title="确认删除这条笔记？"
                              width="220"
                              confirm-button-text="删除"
                              cancel-button-text="取消"
                              @confirm="deleteNoteItem(note)"
                            >
                              <template #reference>
                                <el-button link type="danger" class="note-delete-btn" @click.stop>
                                  <el-icon><Delete /></el-icon>
                                </el-button>
                              </template>
                            </el-popconfirm>
                          </div>
                        </div>

                        <div class="note-meta">
                          <span>{{ note.category }}</span>
                          <span>{{ note.updatedAt }}</span>
                        </div>

                        <div class="note-tags">
                          <el-tag v-for="tag in note.tags" :key="tag" size="small" effect="plain" class="note-tag">
                            {{ tag }}
                          </el-tag>
                        </div>
                      </el-card>
                    </div>
                  </template>
                </VirtualList>
              </div>

              <el-empty v-else description="当前筛选条件下暂无笔记" />
            </div>

            <aside class="detail-pane">
              <div v-if="paneMode === 'view' && activeNote" class="detail">
                <div class="detail-header">
                  <div>
                    <div class="detail-title">{{ activeNote.title }}</div>
                    <div class="detail-meta">
                      <span>{{ activeNote.category }}</span>
                      <span>{{ activeNote.updatedAt }}</span>
                      <span>预计阅读 {{ activeNote.readTime }} 分钟</span>
                    </div>
                  </div>
                  <div class="detail-actions">
                    <el-button link type="primary" @click="startEdit(activeNote)">编辑</el-button>
                    <el-button link type="info" @click="startCreate(activeNote)">复制为草稿</el-button>
                  </div>
                </div>

                <div class="detail-tags">
                  <el-tag v-for="tag in activeNote.tags" :key="tag" effect="light">{{ tag }}</el-tag>
                </div>

                <div class="detail-summary">{{ activeNote.summary }}</div>

                <MarkdownEditor readonly :model-value="activeNote.content" />
              </div>

              <div v-else-if="paneMode !== 'view'" class="editor-pane">
                <div class="detail-header">
                  <div>
                    <div class="detail-title">{{ paneMode === 'create' ? '新增笔记' : '编辑笔记' }}</div>
                    <div class="detail-meta">支持 Markdown 编辑和预览，保存后会回到查看模式。</div>
                  </div>
                  <div class="detail-actions">
                    <el-button @click="cancelEdit">取消</el-button>
                    <el-button type="primary" @click="saveNote">保存</el-button>
                  </div>
                </div>

                <el-form class="note-form" label-position="top">
                  <div class="note-form-grid">
                    <el-form-item label="标题">
                      <el-input v-model="noteForm.title" maxlength="80" show-word-limit placeholder="请输入笔记标题" />
                    </el-form-item>

                    <el-form-item label="分类">
                      <el-select v-model="noteForm.categoryId" filterable placeholder="请选择分类">
                        <el-option
                          v-for="category in formCategoryItems"
                          :key="category.id || category.name"
                          :label="category.name"
                          :value="String(category.id || category.name)"
                        />
                      </el-select>
                    </el-form-item>
                  </div>

                  <div class="note-form-grid">
                    <el-form-item label="状态">
                      <el-select v-model="noteForm.status" placeholder="请选择状态">
                        <el-option v-for="status in editableStatusOptions" :key="status.value" :label="status.label" :value="status.value" />
                      </el-select>
                    </el-form-item>

                    <el-form-item label="标签">
                      <el-select
                        v-model="noteForm.tags"
                        multiple
                        filterable
                        allow-create
                        default-first-option
                        placeholder="输入并回车创建标签"
                      >
                        <el-option v-for="tag in tagOptions" :key="tag" :label="tag" :value="tag" />
                      </el-select>
                    </el-form-item>
                  </div>

                  <el-form-item label="摘要">
                    <el-input
                      v-model="noteForm.summary"
                      type="textarea"
                      :rows="3"
                      maxlength="180"
                      show-word-limit
                      placeholder="列表卡片和详情摘要会使用这里的内容，不填则自动根据正文生成。"
                    />
                  </el-form-item>

                  <el-form-item label="正文">
                    <MarkdownEditor v-model="noteForm.content" placeholder="支持 Markdown 语法，建议用标题、列表、代码块组织内容。" />
                  </el-form-item>
                </el-form>
              </div>

              <div v-else class="detail-empty">
                <el-icon class="detail-empty-icon"><Notebook /></el-icon>
                <div class="detail-empty-title">选择一篇笔记</div>
                <div class="detail-empty-text">点击左侧笔记查看内容，或创建一篇新的学习草稿。</div>
              </div>
            </aside>
          </div>
        </el-card>
      </section>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { computed, nextTick, onActivated, onBeforeUnmount, onMounted, reactive, ref, watch } from 'vue'
import { Search, CollectionTag, EditPen, Calendar, DocumentAdd, Notebook, Delete } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import MarkdownEditor from '@/components/MarkdownEditor.vue'
import VirtualList from '@/components/VirtualList.vue'
import {
  addNoteApi,
  deleteNoteApi,
  getNoteCategoriesApi,
  getNoteTagsApi,
  getNotesApi,
  getNoteStatsApi,
  updateNoteApi,
  type NoteRes,
  type NoteStatsRes
} from '@/api/notes'

type NoteStatus = 'draft' | 'updating' | 'done'
type PaneMode = 'view' | 'create' | 'edit'

interface NoteItem {
  id: string
  title: string
  summary: string
  category: string
  status: NoteStatus
  updatedAt: string
  readTime: number
  tags: string[]
  content: string
}

interface NoteForm {
  title: string
  summary: string
  categoryId: string
  status: NoteStatus
  tags: string[]
  content: string
}

type NoteOption = {
  id?: string
  name: string
  count?: number
}


const notes = ref<NoteItem[]>([])

const query = ref({
  keyword: '',
  categoryId: '',
  status: '',
  tagIds: [] as string[]
})

const statusMap: Record<NoteStatus, { label: string; type: 'info' | 'warning' | 'success' }> = {
  draft: { label: '草稿', type: 'info' },
  updating: { label: '更新中', type: 'warning' },
  done: { label: '已完结', type: 'success' }
}

const statusOptions = [
  { label: '全部状态', value: '', type: 'info' as const },
  { label: '草稿', value: 'draft', type: 'info' as const },
  { label: '更新中', value: 'updating', type: 'warning' as const },
  { label: '已完结', value: 'done', type: 'success' as const }
]

const editableStatusOptions = statusOptions.filter((item) => item.value)
const paneMode = ref<PaneMode>('view')
const editingNoteId = ref('')
const activeNoteId = ref(notes.value[0]?.id || '')
const listRef = ref<HTMLElement | null>(null)
const listHeight = ref(0)
const noteItemSize = 208

const createEmptyForm = (): NoteForm => ({
  title: '',
  summary: '',
  categoryId: '',
  status: 'draft',
  tags: [],
  content: ''
})

const noteForm = reactive<NoteForm>(createEmptyForm())

const remoteCategories = ref<NoteOption[]>([])
const remoteTags = ref<NoteOption[]>([])

const localCategoryCountMap = computed<Record<string, number>>(() => {
  return notes.value.reduce<Record<string, number>>((acc, item) => {
    const key = item.category
    if (!key) return acc
    acc[key] = (acc[key] || 0) + 1
    return acc
  }, {})
})

const categoryItems = computed<NoteOption[]>(() => {
  if (remoteCategories.value.length) return remoteCategories.value
  const names = Array.from(new Set(notes.value.map((item) => item.category).filter(Boolean)))
  return names.map((name) => ({ id: name, name, count: localCategoryCountMap.value[name] || 0 }))
})

const formCategoryItems = computed(() => remoteCategories.value.filter((i) => Boolean(i?.id) && Boolean(i?.name)))

const findCategoryIdByName = (name: string) => {
  const key = String(name ?? '').trim()
  if (!key) return ''
  if (!remoteCategories.value.length) return ''
  const found = remoteCategories.value.find((i) => i.name === key)
  return found?.id ? String(found.id) : ''
}

const tagSearch = ref('')
const localTagItems = computed<Required<Pick<NoteOption, 'id' | 'name'>>[]>(() => {
  const names = Array.from(new Set(notes.value.flatMap((item) => item.tags))).filter(Boolean)
  return names.map((name) => ({ id: name, name }))
})

const tagOptionItems = computed<Required<Pick<NoteOption, 'id' | 'name'>>[]>(() => {
  if (remoteTags.value.length) {
    return remoteTags.value
      .filter((v) => Boolean(v?.id) && Boolean(v?.name))
      .map((v) => ({ id: String(v.id), name: v.name }))
  }
  return localTagItems.value
})

const tagOptions = computed(() => {
  const list = new Set(tagOptionItems.value.map((i) => i.name).filter(Boolean))
  noteForm.tags.forEach((t) => {
    const v = String(t ?? '').trim()
    if (v) list.add(v)
  })
  return Array.from(list)
})

const tagNameMap = computed(() => {
  const map = new Map<string, string>()
  tagOptionItems.value.forEach((item) => map.set(item.id, item.name))
  return map
})

const filteredTagItems = computed(() => {
  const keyword = tagSearch.value.trim().toLowerCase()
  if (!keyword) return tagOptionItems.value
  return tagOptionItems.value.filter((tag) => tag.name.toLowerCase().includes(keyword))
})

const selectedTagItems = computed<Required<Pick<NoteOption, 'id' | 'name'>>[]>(() => {
  return (query.value.tagIds || []).map((id) => ({ id, name: tagNameMap.value.get(id) || id }))
})

const visibleSelectedTagItems = computed(() => selectedTagItems.value.slice(0, 3))
const hiddenSelectedTagCount = computed(() => Math.max(0, selectedTagItems.value.length - visibleSelectedTagItems.value.length))
const removeTag = (id: string) => {
  query.value.tagIds = (query.value.tagIds || []).filter((v) => v !== id)
}

const allCategoryCount = computed(() => {
  return categoryItems.value.reduce((sum, item) => sum + (Number.isFinite(item?.count as number) ? Number(item.count) : 0), 0)
})

const filteredNotes = computed(() => notes.value)

const activeNote = computed(() => filteredNotes.value.find((item) => item.id === activeNoteId.value) || filteredNotes.value[0] || null)

const stats = ref<Required<NoteStatsRes>>({
  total: 0,
  done: 0,
  updating: 0,
  month: 0
})

const statsCards = computed(() => {
  return [
    { label: '总笔记数', value: stats.value.total, tip: '持续积累', icon: Notebook, color: '#409eff', bgColor: 'rgba(64, 158, 255, 0.12)' },
    { label: '已完结', value: stats.value.done, tip: '可复盘', icon: CollectionTag, color: '#67c23a', bgColor: 'rgba(103, 194, 58, 0.12)' },
    { label: '更新中', value: stats.value.updating, tip: '持续迭代', icon: EditPen, color: '#e6a23c', bgColor: 'rgba(230, 162, 60, 0.12)' },
    { label: '本月记录', value: stats.value.month, tip: '保持输出', icon: Calendar, color: '#9254de', bgColor: 'rgba(146, 84, 222, 0.12)' }
  ]
})

const timelineItems = computed(() => [
  { label: '最近更新', value: notes.value[0]?.updatedAt || '-' },
  { label: '当前分类', value: selectedCategoryName.value },
  { label: '当前状态', value: query.value.status ? statusMap[query.value.status as NoteStatus].label : '全部' }
])

const syncForm = (note?: Partial<NoteItem>) => {
  Object.assign(noteForm, createEmptyForm(), {
    title: note?.title || '',
    summary: note?.summary || '',
    categoryId: note?.category ? findCategoryIdByName(note.category) : '',
    status: note?.status || 'draft',
    tags: [...(note?.tags || [])],
    content: note?.content || ''
  })
}

const selectNote = (note: NoteItem) => {
  activeNoteId.value = note.id
  paneMode.value = 'view'
}

const startCreate = (base?: NoteItem | null) => {
  paneMode.value = 'create'
  editingNoteId.value = ''
  syncForm(base ? {
    ...base,
    title: `${base.title} - 草稿`,
    status: 'draft'
  } : undefined)
  if (!noteForm.categoryId && query.value.categoryId) {
    noteForm.categoryId = query.value.categoryId
  }
}

const startEdit = (note?: NoteItem | null) => {
  if (!note) return
  paneMode.value = 'edit'
  editingNoteId.value = note.id
  syncForm(note)
}

const cancelEdit = () => {
  paneMode.value = 'view'
  syncForm(activeNote.value || undefined)
}

const stripMarkdown = (content: string) => {
  return content
    .replace(/```[\s\S]*?```/g, ' ')
    .replace(/`([^`]+)`/g, '$1')
    .replace(/!\[[^\]]*\]\([^)]*\)/g, ' ')
    .replace(/\[([^\]]*)\]\([^)]*\)/g, '$1')
    .replace(/[>#*_~\-|]/g, ' ')
    .replace(/\n+/g, ' ')
    .replace(/\s+/g, ' ')
    .trim()
}

const buildSummary = (summary: string, content: string) => {
  const value = summary.trim()
  if (value) return value
  const plain = stripMarkdown(content)
  return plain ? plain.slice(0, 90) : '暂无摘要'
}

const estimateReadTime = (content: string) => {
  return Math.max(1, Math.ceil(stripMarkdown(content).length / 220))
}

const resetQuery = () => {
  query.value.keyword = ''
  query.value.categoryId = ''
  query.value.status = ''
  query.value.tagIds = []
  tagSearch.value = ''
}

const selectedCategoryName = computed(() => {
  const id = query.value.categoryId
  if (!id) return '全部'
  const found = categoryItems.value.find((i) => (i.id || i.name) === id)
  return found?.name || '全部'
})

const normalizeDate = (v: any) => {
  const s = String(v ?? '').trim()
  if (!s) return ''
  return s.length >= 10 ? s.slice(0, 10) : s
}

const mapNoteRes = (n: NoteRes): NoteItem => {
  return {
    id: String(n?.id ?? ''),
    title: String(n?.title ?? ''),
    summary: String(n?.summary ?? ''),
    category: String(n?.category ?? ''),
    status: (String(n?.status ?? 'draft') as NoteStatus) || 'draft',
    updatedAt: normalizeDate(n?.updatedAt),
    readTime: Number.isFinite(n?.readTime as number) ? Number(n.readTime) : 0,
    tags: Array.isArray(n?.tags) ? n.tags.map((t: any) => String(t ?? '').trim()).filter(Boolean) : [],
    content: String(n?.content ?? '')
  }
}

const loadNotes = async () => {
  try {
    const req: any = {}
    if (query.value.keyword.trim()) req.keyword = query.value.keyword.trim()
    if (query.value.categoryId) req.categoryId = query.value.categoryId
    if (query.value.status) req.status = query.value.status
    if (query.value.tagIds.length) req.tagIds = query.value.tagIds
    const res = await getNotesApi(req)
    if (res.code !== 200) return
    const list = Array.isArray(res.data) ? (res.data as NoteRes[]) : []
    notes.value = list.map(mapNoteRes)
    if (paneMode.value === 'view') {
      if (!notes.value.find((i) => i.id === activeNoteId.value)) {
        activeNoteId.value = notes.value[0]?.id || ''
      }
    }
  } catch {}
}

const loadNoteStats = async () => {
  try {
    const res = await getNoteStatsApi()
    if (res.code !== 200) return
    const data: any = res.data || {}
    stats.value = {
      total: Number.isFinite(data?.total) ? Number(data.total) : 0,
      done: Number.isFinite(data?.done) ? Number(data.done) : 0,
      updating: Number.isFinite(data?.updating) ? Number(data.updating) : 0,
      month: Number.isFinite(data?.month) ? Number(data.month) : 0
    }
  } catch {}
}

const loadNoteTags = async () => {
  try {
    const res = await getNoteTagsApi()
    if (res.code !== 200) return
    const list = Array.isArray(res.data) ? res.data : []
    remoteTags.value = list
      .map((v: any) => ({ id: v?.id ? String(v.id) : undefined, name: String(v?.name ?? '').trim() }))
      .filter((v: NoteOption) => Boolean(v.name))
      .sort((a: NoteOption, b: NoteOption) => a.name.localeCompare(b.name, 'zh-CN'))
  } catch {}
}

const loadNoteCategories = async () => {
  try {
    const res = await getNoteCategoriesApi()
    if (res.code !== 200) return
    const list = Array.isArray(res.data) ? res.data : []
    remoteCategories.value = list
      .map((v: any) => ({
        id: v?.id ? String(v.id) : undefined,
        name: String(v?.name ?? '').trim(),
        count: Number.isFinite(v?.count) ? Number(v.count) : 0
      }))
      .filter((v: NoteOption) => Boolean(v.name))
      .sort((a: NoteOption, b: NoteOption) => a.name.localeCompare(b.name, 'zh-CN'))
  } catch {}
}

const deleteNoteItem = async (note: NoteItem) => {
  const id = String(note?.id ?? '').trim()
  if (!id) return
  try {
    const res = await deleteNoteApi({ id })
    if (res.code !== 200) {
      ElMessage.error(res.msg || '删除失败')
      return
    }
    await loadNoteCategories()
    await loadNoteTags()
    await loadNoteStats()
    await loadNotes()
    if (activeNoteId.value === id) {
      activeNoteId.value = notes.value[0]?.id || ''
    }
    ElMessage.success('笔记已删除')
  } catch {
    ElMessage.error('删除失败')
  }
}

const saveNote = async () => {
  if (!noteForm.title.trim()) {
    ElMessage.warning('请输入标题')
    return
  }
  if (!noteForm.categoryId) {
    ElMessage.warning('请输入分类')
    return
  }
  if (!noteForm.content.trim()) {
    ElMessage.warning('请输入正文')
    return
  }

  const payload = {
    title: noteForm.title.trim(),
    summary: buildSummary(noteForm.summary, noteForm.content),
    content: noteForm.content.trim(),
    categoryId: noteForm.categoryId,
    status: noteForm.status,
    tags: noteForm.tags.map((tag) => String(tag ?? '').trim()).filter(Boolean)
  }

  try {
    const editing = paneMode.value === 'edit'
    const currentId = editing ? editingNoteId.value : ''
    const res = editing ? await updateNoteApi({ id: currentId, ...payload }) : await addNoteApi(payload)
    if (res.code !== 200) {
      ElMessage.error(res.msg || '保存失败')
      return
    }

    await loadNoteCategories()
    await loadNoteTags()
    await loadNoteStats()
    await loadNotes()

    if (currentId && notes.value.some((i) => i.id === currentId)) {
      activeNoteId.value = currentId
    } else {
      activeNoteId.value = notes.value[0]?.id || ''
    }
    paneMode.value = 'view'
    ElMessage.success('笔记已保存')
  } catch {
    ElMessage.error('保存失败')
  }
}

const updateListHeight = () => {
  listHeight.value = Math.max(0, listRef.value?.clientHeight || 0)
}

const applyKeywordFromState = () => {
  const state = history.state as any
  const keyword = (state?.keyword || '').toString().trim()
  if (keyword && keyword !== query.value.keyword) {
    query.value.keyword = keyword
  }
}

// setup 阶段设置 keyword，watcher 或 onMounted 会触发查询
applyKeywordFromState()

onMounted(() => {
  nextTick(updateListHeight)
  window.addEventListener('resize', updateListHeight)
  void loadNoteTags()
  void loadNoteCategories()
  void loadNotes()
  void loadNoteStats()
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', updateListHeight)
})

// keep-alive 重新激活时读取 state 中的 keyword
onActivated(() => {
  applyKeywordFromState()
})

watch(
  () => notes.value.length,
  () => {
    if (paneMode.value === 'view' && !activeNote.value) {
      activeNoteId.value = filteredNotes.value[0]?.id || ''
    }
    nextTick(updateListHeight)
  }
)

watch(
  () => [query.value.categoryId, query.value.status, query.value.tagIds],
  () => {
    void loadNotes()
  }
)

let keywordTimer: ReturnType<typeof setTimeout> | undefined
watch(
  () => query.value.keyword,
  () => {
    if (keywordTimer) clearTimeout(keywordTimer)
    keywordTimer = setTimeout(() => {
      void loadNotes()
    }, 300)
  }
)

</script>

<style scoped>
.notes-page {
  height: calc(100% - 48px);
  padding: 24px;
}

.notes-layout {
  height: 100%;
  display: grid;
  grid-template-columns: 260px 1fr;
  gap: 24px;
  min-height: 0;
}

.sidebar,
.content,
.main-card,
.main-body,
.note-list,
.detail-pane {
  min-height: 0;
}

.sidebar {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.side-card,
.main-card,
.stat-card {
  border: none;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.82);
  box-shadow: 0 10px 30px rgba(15, 23, 42, 0.06);
}

.side-title,
.list-title,
.detail-title {
  font-weight: 600;
  color: #1f2937;
}

.side-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
  max-height: calc(4 * 40px + 3 * 8px);
  overflow-y: auto;
  padding-right: 4px;
  scrollbar-width: thin;
  scrollbar-color: rgba(148, 163, 184, 0.65) transparent;
}

.side-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 40px;
  box-sizing: border-box;
  padding: 6px 10px;
  border-radius: 12px;
  cursor: pointer;
  color: #4b5563;
  transition: all 0.2s ease;
}

.side-list::-webkit-scrollbar {
  width: 6px;
}

.side-list::-webkit-scrollbar-thumb {
  background: rgba(148, 163, 184, 0.65);
  border-radius: 999px;
}

.side-list::-webkit-scrollbar-track {
  background: transparent;
}

.side-item:hover,
.side-item.active {
  background: #eff6ff;
  color: #2563eb;
}

.side-count {
  font-size: 12px;
  color: #9ca3af;
}

.status-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.status-chip {
  cursor: pointer;
}

.timeline {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.timeline-item {
  display: flex;
  gap: 12px;
}

.timeline-dot {
  width: 10px;
  height: 10px;
  margin-top: 6px;
  border-radius: 50%;
  background: linear-gradient(135deg, #409eff, #7c3aed);
  flex-shrink: 0;
}

.timeline-label {
  font-size: 12px;
  color: #9ca3af;
}

.timeline-value {
  margin-top: 2px;
  color: #374151;
}

.content {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 16px;
}

.stat-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.stat-icon {
  width: 44px;
  height: 44px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
}

.stat-trend {
  font-size: 12px;
  font-weight: 500;
}

.stat-value {
  margin-top: 20px;
  font-size: 28px;
  line-height: 1;
  font-weight: 700;
  color: #111827;
}

.stat-label {
  margin-top: 8px;
  color: #6b7280;
}

.main-card {
  flex: 1;
}

.main-card :deep(.el-card__body) {
  height: 100%;
  display: flex;
  flex-direction: column;
  min-height: 0;
}

.toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding-bottom: 16px;
  border-bottom: 1px solid #eef2f7;
}

.toolbar-left {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
  min-width: 0;
}

.search {
  width: 320px;
}

.tag-filter-area {
  display: flex;
  align-items: center;
  gap: 10px;
  min-width: 0;
}

.tag-filter-btn {
  border-radius: 12px;
}

.tag-filter-count {
  margin-left: 4px;
  color: #6b7280;
}

.tag-selected {
  display: flex;
  align-items: center;
  gap: 6px;
  overflow: hidden;
  white-space: nowrap;
  max-width: 360px;
}

.tag-selected-item,
.tag-selected-more {
  border-radius: 999px;
}

.tag-pop {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.tag-check-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.tag-pop-actions {
  display: flex;
  justify-content: flex-end;
}

.toolbar-right {
  display: flex;
  gap: 12px;
}

.main-body {
  flex: 1;
  min-height: 0;
  display: grid;
  grid-template-columns: minmax(360px, 460px) 1fr;
  gap: 20px;
  padding-top: 18px;
}

.note-list {
  min-height: 0;
  display: flex;
  flex-direction: column;
}

.list-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 14px;
}

.list-subtitle,
.detail-meta {
  color: #9ca3af;
  font-size: 13px;
}

.list-scroll {
  flex: 1;
  min-height: 0;
  padding-right: 4px;
}

.note-item-wrap {
  height: 100%;
  padding-bottom: 12px;
  box-sizing: border-box;
}

.note-card {
  height: 100%;
  border-radius: 16px;
  border: 1px solid #edf2f7;
  cursor: pointer;
  transition: all 0.2s ease;
}

.note-card:hover,
.note-card.active {
  border-color: rgba(64, 158, 255, 0.45);
  box-shadow: 0 12px 30px rgba(64, 158, 255, 0.12);
}

.note-card-head {
  display: flex;
  gap: 12px;
  align-items: flex-start;
  justify-content: space-between;
}

.note-main {
  min-width: 0;
}

.note-title-row {
  display: flex;
  align-items: center;
  gap: 10px;
}

.note-title {
  font-size: 16px;
  font-weight: 600;
  color: #111827;
}

.note-summary {
  margin-top: 10px;
  color: #4b5563;
  line-height: 1.7;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.note-read {
  flex-shrink: 0;
  color: #9ca3af;
  font-size: 13px;
}

.note-side {
  display: flex;
  align-items: center;
  gap: 6px;
  flex-shrink: 0;
}

.note-delete-btn {
  padding: 0;
  min-height: auto;
}

.note-meta {
  margin-top: 14px;
  display: flex;
  gap: 14px;
  font-size: 12px;
  color: #9ca3af;
}

.note-tags {
  margin-top: 14px;
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  max-height: 56px;
  overflow: hidden;
}

.note-tag {
  border-radius: 999px;
}

.detail-pane {
  min-height: 0;
  overflow: auto;
  border-radius: 18px;
  background: linear-gradient(180deg, rgba(248, 250, 252, 0.95), rgba(255, 255, 255, 0.95));
  border: 1px solid #edf2f7;
  padding: 24px;
  text-align: left;
}

.detail,
.editor-pane {
  display: flex;
  flex-direction: column;
  min-height: 0;
  width: 100%;
  align-items: stretch;
}

.detail-header {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: flex-start;
}

.detail-actions {
  display: flex;
  gap: 4px;
  flex-shrink: 0;
}

.detail-title {
  font-size: 26px;
}

.detail-meta {
  margin-top: 8px;
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.detail-tags {
  margin-top: 16px;
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.detail-summary {
  margin: 20px 0;
  padding: 14px 16px;
  border-radius: 14px;
  background: #f8fafc;
  color: #475569;
  line-height: 1.8;
  text-align: left;
}

.note-form {
  margin-top: 20px;
}

.note-form-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
}

.detail-empty {
  height: 100%;
  min-height: 320px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #94a3b8;
  text-align: center;
}

.detail-empty-icon {
  font-size: 42px;
}

.detail-empty-title {
  margin-top: 16px;
  font-size: 18px;
  font-weight: 600;
  color: #475569;
}

.detail-empty-text {
  margin-top: 8px;
  max-width: 320px;
  line-height: 1.7;
}

@media (max-width: 1440px) {
  .notes-layout {
    grid-template-columns: 220px 1fr;
  }

  .stats-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .main-body {
    grid-template-columns: 380px 1fr;
  }
}

@media (max-width: 1100px) {
  .notes-page {
    height: auto;
  }

  .notes-layout {
    grid-template-columns: 1fr;
  }

  .main-body,
  .note-form-grid {
    grid-template-columns: 1fr;
  }

  .toolbar-left {
    flex-direction: column;
    align-items: stretch;
  }

  .search {
    width: 100%;
  }

  .tag-selected {
    max-width: 100%;
    white-space: normal;
    flex-wrap: wrap;
  }

  .tag-filter-area {
    width: 100%;
  }

  .toolbar,
  .detail-header {
    flex-direction: column;
    align-items: stretch;
  }

  .toolbar-right,
  .detail-actions {
    justify-content: flex-end;
  }
}
</style>
