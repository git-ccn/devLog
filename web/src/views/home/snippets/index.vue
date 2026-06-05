<template>
  <div class="snippets-page">
    <div class="snippets-layout">
      <aside class="sidebar">
        <el-card class="side-card" shadow="never">
          <div class="side-header">
            <div class="side-title">语言筛选</div>
            <el-button v-if="query.language" link type="primary" class="side-clear" @click="setLanguage('')">
              清除
            </el-button>
          </div>

          <div class="lang-list">
            <div class="lang-item" :class="{ active: !query.language }" @click="setLanguage('')">
              <span class="lang-name">全部</span>
              <span class="lang-count">{{ filteredNoLang.length }}</span>
            </div>
            <div
              v-for="lang in languages"
              :key="lang"
              class="lang-item"
              :class="{ active: query.language === lang }"
              @click="setLanguage(lang)"
            >
              <span class="lang-name">{{ lang }}</span>
              <span class="lang-count">{{ languageCountMap[lang] }}</span>
            </div>
          </div>
        </el-card>

        <el-card v-if="allTags.length" class="side-card" shadow="never">
          <div class="side-header">
            <div class="side-title">标签筛选</div>
            <el-button v-if="query.tag" link type="primary" class="side-clear" @click="query.tag = ''">
              清除
            </el-button>
          </div>

          <div class="tag-grid">
            <el-tag
              v-for="tag in allTags"
              :key="tag"
              class="tag-chip"
              :effect="query.tag === tag ? 'dark' : 'plain'"
              :type="query.tag === tag ? 'primary' : 'info'"
              @click="toggleTag(tag)"
            >
              {{ tag }}
            </el-tag>
          </div>
        </el-card>

        <el-card class="side-card" shadow="never">
          <div class="side-header">
            <div class="side-title">其它</div>
          </div>
          <div class="side-controls">
            <el-switch v-model="query.showDeleted" active-text="显示已删除" />
          </div>
        </el-card>
      </aside>

      <section class="content">
        <el-card class="panel" shadow="never">
          <div class="toolbar toolbar-fixed">
            <div class="toolbar-left">
              <el-input v-model="query.keyword" placeholder="点击内容输入关键词搜索" class="search" clearable>
                <template #prefix>
                  <el-icon><Search /></el-icon>
                </template>
              </el-input>

              <el-select v-model="query.language" placeholder="语言筛选" class="lang" clearable>
                <el-option v-for="lang in languages" :key="lang" :label="lang" :value="lang" />
              </el-select>
            </div>

            <div class="toolbar-right">
              <div class="toolbar-pager">
                <span class="muted total">{{ filteredItems.length }} 条</span>
                <el-pagination
                  v-model:current-page="pagination.page"
                  :page-size="pagination.pageSize"
                  layout="prev, pager, next"
                  :total="filteredItems.length"
                  small
                  background
                />
              </div>
              <el-button type="primary" class="create-btn" @click="startCreate">
                <el-icon><Plus /></el-icon>
                新增片段
              </el-button>
            </div>
          </div>

          <div class="body">
            <div class="list">
              <el-card
                v-for="row in pagedItems"
                :key="row.id"
                shadow="hover"
                class="snippet-card"
                :class="{ 'is-deleted': row.deleted, active: pane.id === row.id }"
                @click="selectRow(row)"
              >
                <div class="snippet-head">
                  <div class="snippet-title">{{ row.title }}</div>
                  <div class="snippet-actions" @click.stop>
                    <el-button class="copy-btn" round @click="copyToClipboard(row.content)">复制</el-button>
                  </div>
                </div>

                <div class="snippet-meta">
                  <el-tag size="small" effect="light" type="primary">{{ row.language }}</el-tag>
                  <div class="tags-cell">
                    <el-tag
                      v-for="tag in row.tags"
                      :key="tag"
                      size="small"
                      effect="plain"
                      class="row-tag"
                      @click.stop="toggleTag(tag)"
                    >
                      {{ tag }}
                    </el-tag>
                    <span v-if="!row.tags.length" class="muted">-</span>
                  </div>
                  <span class="meta-time muted">更新 {{ formatDate(row.updatedAt) }}</span>
                </div>

                <div class="snippet-code">
                  <MonacoEditor
                    :key="`${row.id}-${row.updatedAt}`"
                    :model-value="row.content"
                    :language="toMonacoLang(row.language)"
                    :read-only="true"
                    height="128px"
                    :options="monacoPreviewOptions"
                  />
                </div>

                <div class="snippet-foot" @click.stop>
                  <el-button size="small" round plain @click="selectRow(row)">详情</el-button>
                  <el-button size="small" round plain :disabled="row.deleted" @click="startEdit(row)">编辑</el-button>
                  <el-button v-if="!row.deleted" size="small" round plain type="danger" @click="softDelete(row)">
                    删除
                  </el-button>
                  <el-button v-else size="small" round plain type="success" @click="restore(row)">恢复</el-button>
                </div>
              </el-card>

              <div v-if="!filteredItems.length" class="empty">
                <div class="empty-title">暂无片段</div>
                <div class="muted">试试调整筛选条件，或点击右上角新增片段</div>
              </div>
            </div>

            <aside class="pane">
              <el-card class="pane-card" shadow="never">
                <div class="pane-header">
                  <div class="pane-title">{{ paneTitle }}</div>
                  <div class="pane-header-actions">
                    <el-button v-if="pane.mode === 'view'" link type="primary" @click="startEdit()">
                      编辑
                    </el-button>
                    <el-button v-if="pane.mode === 'view'" link type="info" @click="copyActive">
                      复制
                    </el-button>
                    <el-button v-if="pane.mode === 'view' && activeItem && !activeItem.deleted" link type="danger" @click="softDelete(activeItem)">
                      删除
                    </el-button>
                    <el-button v-if="pane.mode === 'view' && activeItem && activeItem.deleted" link type="success" @click="restore(activeItem)">
                      恢复
                    </el-button>
                  </div>
                </div>

                <div v-if="pane.mode === 'empty'" class="pane-empty">
                  <div class="pane-empty-title">选择一个片段</div>
                  <div class="muted">点击左侧卡片查看/编辑，或创建新的代码片段</div>
                  <el-button type="primary" class="pane-empty-btn" @click="startCreate">
                    <el-icon><Plus /></el-icon>
                    新增片段
                  </el-button>
                </div>

                <el-form
                  v-else
                  ref="formRef"
                  class="pane-form"
                  :model="pane.form"
                  :rules="rules"
                  label-width="64px"
                >
                  <el-form-item label="标题" prop="title">
                    <el-input v-model="pane.form.title" :disabled="pane.mode === 'view'" maxlength="50" show-word-limit />
                  </el-form-item>

                  <el-form-item label="语言" prop="language">
                    <el-select
                      v-model="pane.form.language"
                      :disabled="pane.mode === 'view'"
                      style="width: 100%"
                      @change="onPaneLanguageChange"
                    >
                      <el-option v-for="lang in languages" :key="lang" :label="lang" :value="lang" />
                    </el-select>
                  </el-form-item>

                  <el-form-item label="标签" prop="tags">
                    <el-select
                      v-model="pane.form.tags"
                      :disabled="pane.mode === 'view'"
                      multiple
                      filterable
                      allow-create
                      default-first-option
                      placeholder="输入并回车创建标签"
                      style="width: 100%"
                    >
                      <el-option v-for="tag in allTags" :key="tag" :label="tag" :value="tag" />
                    </el-select>
                  </el-form-item>

                  <el-form-item label="代码" prop="content">
                    <MonacoEditor
                      ref="paneEditorRef"
                      v-model="pane.form.content"
                      :language="toMonacoLang(pane.form.language)"
                      :read-only="pane.mode === 'view'"
                      height="420px"
                      :options="monacoEditOptions"
                    />
                  </el-form-item>

                  <div class="pane-footer">
                    <template v-if="pane.mode === 'view'">
                      <div class="muted" v-if="activeItem">更新 {{ formatDate(activeItem.updatedAt) }}</div>
                      <div class="pane-footer-actions">
                        <el-button :disabled="activeItem?.deleted" @click="startEdit()">编辑</el-button>
                        <el-button type="primary" @click="copyActive">复制</el-button>
                      </div>
                    </template>
                    <template v-else>
                      <div class="pane-footer-actions">
                        <el-button @click="cancelPane">取消</el-button>
                        <el-button type="primary" @click="savePane">保存</el-button>
                      </div>
                    </template>
                  </div>
                </el-form>
              </el-card>
            </aside>
          </div>
        </el-card>
      </section>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { computed, nextTick, reactive, ref, watch } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search } from '@element-plus/icons-vue'
import MonacoEditor from './MonacoEditor.vue'

type Language = 'Java' | 'JavaScript' | 'Python' | 'SQL'
type MonacoLanguage = 'plaintext' | 'java' | 'javascript' | 'sql'

type MonacoEditorExpose = {
  setValue: (v: string, opts?: { preserveUndo?: boolean }) => boolean
  getValue: () => string
  setLanguage: (lang: MonacoLanguage) => boolean
  setReadOnly: (ro: boolean) => void
  setTheme: (t: 'vs' | 'vs-dark') => void
  updateOptions: (opts: any) => void
  layout: () => void
  focus: () => void
}

interface SnippetItem {
  id: string
  title: string
  content: string
  language: Language
  tags: string[]
  createdAt: number
  updatedAt: number
  deleted: boolean
}

const languages: Language[] = ['Java', 'JavaScript', 'Python', 'SQL']

const seedNow = Date.now()
const items = ref<SnippetItem[]>([
  {
    id: 's1',
    title: '二分查找算法',
    language: 'Java',
    tags: ['算法', '数组'],
    content:
      'public int binarySearch(int[] nums, int target) {\n' +
      '  int l = 0, r = nums.length - 1;\n' +
      '  while (l <= r) {\n' +
      '    int mid = l + (r - l) / 2;\n' +
      '    if (nums[mid] == target) return mid;\n' +
      '    if (nums[mid] < target) l = mid + 1;\n' +
      '    else r = mid - 1;\n' +
      '  }\n' +
      '  return -1;\n' +
      '}',
    createdAt: seedNow - 1000 * 60 * 60 * 24 * 4,
    updatedAt: seedNow - 1000 * 60 * 60 * 7,
    deleted: false
  },
  {
    id: 's2',
    title: '深拷贝函数',
    language: 'JavaScript',
    tags: ['工具', '对象'],
    content:
      'export function deepClone(obj) {\n' +
      '  if (obj === null || typeof obj !== \"object\") return obj;\n' +
      '  if (Array.isArray(obj)) return obj.map(deepClone);\n' +
      '  const res = {};\n' +
      '  for (const k in obj) res[k] = deepClone(obj[k]);\n' +
      '  return res;\n' +
      '}',
    createdAt: seedNow - 1000 * 60 * 60 * 24 * 3,
    updatedAt: seedNow - 1000 * 60 * 60 * 18,
    deleted: false
  },
  {
    id: 's3',
    title: 'JWT Token验证',
    language: 'Java',
    tags: ['安全', '认证'],
    content:
      'public boolean verify(String token) {\n' +
      '  try {\n' +
      '    Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);\n' +
      '    return true;\n' +
      '  } catch (Exception e) {\n' +
      '    return false;\n' +
      '  }\n' +
      '}',
    createdAt: seedNow - 1000 * 60 * 60 * 24 * 2,
    updatedAt: seedNow - 1000 * 60 * 60 * 4,
    deleted: false
  },
  {
    id: 's4',
    title: '防抖函数',
    language: 'JavaScript',
    tags: ['工具', '性能'],
    content:
      'export function debounce(fn, delay = 300) {\n' +
      '  let timer;\n' +
      '  return (...args) => {\n' +
      '    clearTimeout(timer);\n' +
      '    timer = setTimeout(() => fn(...args), delay);\n' +
      '  };\n' +
      '}',
    createdAt: seedNow - 1000 * 60 * 60 * 24,
    updatedAt: seedNow - 1000 * 60 * 60 * 3,
    deleted: false
  },
  {
    id: 's5',
    title: 'MySQL分页查询',
    language: 'SQL',
    tags: ['数据库', '分页'],
    content:
      'SELECT *\n' +
      'FROM sys_user\n' +
      'ORDER BY create_time DESC\n' +
      'LIMIT 10 OFFSET 0;',
    createdAt: seedNow - 1000 * 60 * 60 * 12,
    updatedAt: seedNow - 1000 * 60 * 60 * 2,
    deleted: false
  }
])

const query = reactive({
  keyword: '',
  language: '' as '' | Language,
  tag: '',
  showDeleted: false
})

const pagination = reactive({
  page: 1,
  pageSize: 10
})

const allTags = computed(() => {
  const set = new Set<string>()
  for (const item of items.value) {
    for (const t of item.tags) set.add(t)
  }
  return Array.from(set).sort((a, b) => a.localeCompare(b, 'zh-CN'))
})

const filteredItems = computed(() => {
  const kw = query.keyword.trim().toLowerCase()
  return items.value
    .filter((i) => (query.showDeleted ? true : !i.deleted))
    .filter((i) => (query.language ? i.language === query.language : true))
    .filter((i) => (query.tag ? i.tags.includes(query.tag) : true))
    .filter((i) => {
      if (!kw) return true
      return i.title.toLowerCase().includes(kw) || i.content.toLowerCase().includes(kw)
    })
    .sort((a, b) => b.updatedAt - a.updatedAt)
})

const filteredNoLang = computed(() => {
  const kw = query.keyword.trim().toLowerCase()
  return items.value
    .filter((i) => (query.showDeleted ? true : !i.deleted))
    .filter((i) => (query.tag ? i.tags.includes(query.tag) : true))
    .filter((i) => {
      if (!kw) return true
      return i.title.toLowerCase().includes(kw) || i.content.toLowerCase().includes(kw)
    })
    .sort((a, b) => b.updatedAt - a.updatedAt)
})

const languageCountMap = computed(() => {
  const res: Record<Language, number> = {
    Java: 0,
    JavaScript: 0,
    Python: 0,
    SQL: 0
  }
  for (const i of filteredNoLang.value) res[i.language]++
  return res
})

const pagedItems = computed(() => {
  const start = (pagination.page - 1) * pagination.pageSize
  return filteredItems.value.slice(start, start + pagination.pageSize)
})

watch(
  () => [query.keyword, query.language, query.tag, query.showDeleted],
  () => {
    pagination.page = 1
  }
)

const setLanguage = (lang: '' | Language) => {
  query.language = lang
}

const toggleTag = (tag: string) => {
  query.tag = query.tag === tag ? '' : tag
}

const toMonacoLang = (lang: Language): MonacoLanguage => {
  if (lang === 'Java') return 'java'
  if (lang === 'JavaScript') return 'javascript'
  if (lang === 'SQL') return 'sql'
  return 'plaintext'
}

const monacoPreviewOptions = {
  fontSize: 12,
  lineNumbers: 'on',
  wordWrap: 'on',
  glyphMargin: false,
  contextmenu: false,
  scrollbar: {
    vertical: 'hidden',
    horizontal: 'hidden',
    handleMouseWheel: false
  }
} as const

const formRef = ref<FormInstance>()

const rules: FormRules = {
  title: [
    { required: true, message: '请输入标题', trigger: 'blur' },
    { min: 2, max: 50, message: '标题长度为 2-50 个字符', trigger: 'blur' }
  ],
  language: [{ required: true, message: '请选择语言', trigger: 'change' }],
  content: [{ required: true, message: '请输入代码内容', trigger: 'blur' }],
  tags: [{ type: 'array', required: true, message: '请至少填写一个标签', trigger: 'change' }]
}

const monacoEditOptions = {
  fontSize: 13,
  lineNumbers: 'on',
  wordWrap: 'on',
  minimap: { enabled: false },
  scrollBeyondLastLine: false,
  renderLineHighlight: 'line',
  contextmenu: true
} as const

const paneEditorRef = ref<MonacoEditorExpose | null>(null)

const pane = reactive({
  mode: 'empty' as 'empty' | 'create' | 'view' | 'edit',
  id: '' as string,
  form: {
    title: '',
    content: '',
    language: 'Java' as Language,
    tags: [] as string[]
  }
})

const activeItem = computed(() => items.value.find((i) => i.id === pane.id) || null)

const paneTitle = computed(() => {
  if (pane.mode === 'create') return '新增片段'
  if (pane.mode === 'edit') return '编辑片段'
  if (pane.mode === 'view') return '片段详情'
  return '片段'
})

const syncPaneEditor = async () => {
  await nextTick()
  const inst = paneEditorRef.value
  if (!inst) return
  inst.setReadOnly(pane.mode === 'view')
  inst.setLanguage(toMonacoLang(pane.form.language))
  inst.updateOptions(monacoEditOptions)
  inst.setValue(pane.form.content ?? '')
  inst.layout()
}

const fillPane = (row: SnippetItem) => {
  pane.form.title = row.title
  pane.form.content = row.content
  pane.form.language = row.language
  pane.form.tags = [...row.tags]
  formRef.value?.clearValidate()
  void syncPaneEditor()
}

const resetPaneForm = () => {
  pane.form.title = ''
  pane.form.content = ''
  pane.form.language = 'Java'
  pane.form.tags = []
  formRef.value?.clearValidate()
  void syncPaneEditor()
}

const selectRow = (row: SnippetItem) => {
  pane.id = row.id
  pane.mode = 'view'
  fillPane(row)
}

const startCreate = () => {
  pane.id = ''
  pane.mode = 'create'
  resetPaneForm()
}

const startEdit = (row?: SnippetItem) => {
  const target = row ?? activeItem.value
  if (!target) return
  pane.id = target.id
  pane.mode = 'edit'
  fillPane(target)
}

const cancelPane = () => {
  if (pane.mode === 'create') {
    pane.mode = 'empty'
    pane.id = ''
    resetPaneForm()
    return
  }
  const target = activeItem.value
  if (target) {
    pane.mode = 'view'
    fillPane(target)
  } else {
    pane.mode = 'empty'
    pane.id = ''
    resetPaneForm()
  }
}

const onPaneLanguageChange = () => {
  const inst = paneEditorRef.value
  if (!inst) return
  inst.setLanguage(toMonacoLang(pane.form.language))
}

const savePane = async () => {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
  } catch {
    return
  }

  const now = Date.now()
  const title = pane.form.title.trim()
  const content = pane.form.content
  const language = pane.form.language
  const tags = pane.form.tags.map((t) => t.trim()).filter(Boolean)
  console.log('content====',content);

  if (pane.mode === 'create') {
    const id = `s_${Math.random().toString(16).slice(2)}`
    const newItem: SnippetItem = {
      id,
      title,
      content,
      language,
      tags,
      createdAt: now,
      updatedAt: now,
      deleted: false
    }
    items.value.unshift(newItem)
    ElMessage.success('新增成功')
    selectRow(newItem)
    return
  }

  if (pane.mode === 'edit' && pane.id) {
    const idx = items.value.findIndex((i) => i.id === pane.id)
    if (idx >= 0) {
      const old = items.value[idx]
      items.value[idx] = {
        ...old,
        title,
        content,
        language,
        tags,
        updatedAt: now
      }
      ElMessage.success('保存成功')
      pane.mode = 'view'
      fillPane(items.value[idx])
    }
  }
}

const copyActive = async () => {
  if (pane.mode === 'empty') return
  await copyToClipboard(pane.form.content)
}

const softDelete = async (row: SnippetItem) => {
  await ElMessageBox.confirm(`确认删除「${row.title}」？（可在“显示已删除”中恢复）`, '提示', {
    type: 'warning',
    confirmButtonText: '删除',
    cancelButtonText: '取消'
  })
  row.deleted = true
  row.updatedAt = Date.now()
  ElMessage.success('已删除')
}

const restore = (row: SnippetItem) => {
  row.deleted = false
  row.updatedAt = Date.now()
  ElMessage.success('已恢复')
}

const copyToClipboard = async (text: string) => {
  try {
    await navigator.clipboard.writeText(text)
    ElMessage.success('已复制到剪贴板')
  } catch {
    ElMessage.error('复制失败')
  }
}

const formatDate = (ts: number) => {
  const d = new Date(ts)
  const pad = (n: number) => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(
    d.getMinutes()
  )}`
}
</script>

<style scoped>
.snippets-page {
  height: calc(100vh - 64px);
  padding: 18px 20px;
  box-sizing: border-box;
}

.snippets-layout {
  display: grid;
  grid-template-columns: 240px 1fr;
  gap: 18px;
  align-items: start;
  height: 100%;
  min-height: 0;
}

.sidebar {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.side-card {
  border-radius: 16px;
  border: none;
  background: rgba(255, 255, 255, 0.78);
  backdrop-filter: blur(10px);
  box-shadow: 0 8px 32px 0 rgba(31, 38, 135, 0.06);
}

.side-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 10px;
}

.side-title {
  font-size: 14px;
  font-weight: 700;
  color: #1f2329;
}

.side-clear {
  padding: 0;
}

.lang-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.lang-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 12px;
  border-radius: 12px;
  cursor: pointer;
  transition: background 0.2s, color 0.2s, box-shadow 0.2s;
  color: #606266;
}

.lang-item:hover {
  background: rgba(64, 158, 255, 0.06);
}

.lang-item.active {
  background: rgba(64, 158, 255, 0.12);
  color: #303133;
  box-shadow: 0 8px 18px rgba(64, 158, 255, 0.08);
}

.lang-name {
  font-weight: 600;
}

.lang-count {
  font-size: 12px;
  color: #909399;
  background: rgba(244, 246, 248, 0.9);
  padding: 2px 8px;
  border-radius: 10px;
}

.tag-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.tag-chip {
  cursor: pointer;
}

.content {
  min-width: 0;
  /* min-height: 0; */
  height: 100%;
  overflow: auto;
}

.panel {
  border-radius: 16px;
  border: none;
  background: rgba(255, 255, 255, 0.78);
  backdrop-filter: blur(10px);
  box-shadow: 0 8px 32px 0 rgba(31, 38, 135, 0.06);
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
  margin-bottom: 14px;
}

.toolbar-fixed {
  position: sticky;
  top: 0;
  z-index: 10;
  padding: 12px 12px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.06);
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
}

.toolbar-left {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
}

.toolbar-right {
  display: flex;
  align-items: center;
  gap: 10px;
}

.toolbar-pager {
  display: flex;
  align-items: center;
  gap: 10px;
}

.total {
  white-space: nowrap;
}

.search {
  max-width: 420px;
}

.lang {
  width: 180px;
}

.side-controls {
  padding-top: 4px;
}

.body {
  display: flex;
  gap: 16px;
  flex: 1;
  min-height: 0;
}

.list {
  display: flex;
  flex-direction: column;
  gap: 14px;
  flex: 1;
  min-height: 0;
  overflow: auto;
  padding-right: 2px;
}

.pane {
  width: 420px;
  flex: 0 0 420px;
  min-height: 0;
}

.pane-card {
  height: 100%;
  border-radius: 16px;
  border: none;
  background: rgba(255, 255, 255, 0.86);
  box-shadow: 0 10px 30px rgba(31, 38, 135, 0.06);
  display: flex;
  flex-direction: column;
  min-height: 0;
  overflow: auto;
  padding-right: 2px;
}

.pane-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 10px;
}

.pane-title {
  font-size: 16px;
  font-weight: 800;
  color: #1f2329;
}

.pane-header-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.pane-empty {
  flex: 1;
  min-height: 0;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  justify-content: center;
  gap: 10px;
  padding: 12px 0;
}

.pane-empty-title {
  font-size: 16px;
  font-weight: 800;
  color: #1f2329;
}

.pane-empty-btn {
  border-radius: 12px;
}

.pane-form {
  flex: 1;
  min-height: 0;
  display: flex;
  flex-direction: column;
}

.pane-form :deep(.el-form-item__content) {
  min-width: 0;
}

.pane-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-top: 10px;
}

.pane-footer-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.snippet-card {
  border-radius: 16px;
  border: none;
  background: rgba(255, 255, 255, 0.86);
  box-shadow: 0 10px 30px rgba(31, 38, 135, 0.06);
  cursor: pointer;
}

.snippet-card.active {
  outline: 2px solid rgba(64, 158, 255, 0.35);
}

.snippet-card.is-deleted {
  opacity: 0.6;
}

.snippet-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 10px;
}

.snippet-title {
  font-size: 16px;
  font-weight: 700;
  color: #1f2329;
}

.snippet-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.copy-btn {
  border-radius: 12px;
}

.snippet-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.tags-cell {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  flex: 1;
  min-width: 0;
}

.row-tag {
  cursor: pointer;
}

.meta-time {
  white-space: nowrap;
}

.snippet-code {
  border-radius: 14px;
  background: linear-gradient(180deg, rgba(248, 250, 252, 0.95) 0%, rgba(241, 245, 249, 0.95) 100%);
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.6);
  padding: 0;
  overflow: hidden;
}

.snippet-foot {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-top: 12px;
}

.empty {
  border-radius: 16px;
  padding: 34px 20px;
  background: rgba(255, 255, 255, 0.65);
  text-align: center;
}

.empty-title {
  font-size: 16px;
  font-weight: 700;
  color: #1f2329;
  margin-bottom: 6px;
}

.muted {
  color: #909399;
}

.detail {
  padding: 10px 2px 24px;
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 12px;
  margin-bottom: 10px;
}

.detail-title {
  font-size: 18px;
  font-weight: 700;
  color: #1f2329;
}

.detail-meta {
  display: flex;
  align-items: center;
  gap: 8px;
}

.detail-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 12px;
}

.detail-actions {
  display: flex;
  gap: 10px;
  margin-bottom: 12px;
}

.code-block {
  border-radius: 14px;
  background: linear-gradient(180deg, rgba(248, 250, 252, 0.95) 0%, rgba(241, 245, 249, 0.95) 100%);
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.6);
  overflow: hidden;
}

.pre {
  margin: 0;
  padding: 14px 16px;
  overflow: auto;
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, 'Liberation Mono', 'Courier New', monospace;
  font-size: 12px;
  line-height: 1.65;
  color: #111827;
}

.code {
  white-space: pre;
}

.hl-kw {
  color: #2563eb;
  font-weight: 600;
}

.hl-str {
  color: #0f766e;
}

.hl-num {
  color: #7c3aed;
}

.hl-cmt {
  color: #9ca3af;
}

.detail-footer {
  margin-top: 14px;
}
</style>
