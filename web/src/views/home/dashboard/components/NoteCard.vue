<template>
  <el-card class="note-card" shadow="hover">
    <template #header>
      <div class="card-header">
        <span class="title">
          <el-icon class="title-icon"><Notebook /></el-icon>
          技术笔记
        </span>
        <div class="view-more" @click="goNotes()">
          查看更多<el-icon><ArrowRight /></el-icon>
        </div>
      </div>
    </template>
    <div class="note-list">
      <div v-for="note in notes" :key="note.id" class="note-item" @click="goNotes(note.title)">
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
import { markRaw, onMounted, ref } from 'vue'
import { Notebook, ArrowRight, Pointer, Share, Collection } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import { getNotesApi, type NoteRes } from '@/api/notes'

const router = useRouter()

type NoteStatus = 'draft' | 'updating' | 'done'
type TagType = 'info' | 'warning' | 'success' | 'primary' | 'danger'

type NoteCardItem = {
  id: string
  title: string
  date: string
  category: string
  icon: any
  color: string
  tag: string
  tagType: TagType
}

const goNotes = (keyword?: string) => {
  const value = String(keyword ?? '').trim()
  void router.push(value ? { name: 'Notes', state: { keyword: value } } : '/home/notes')
}

const statusLabelMap: Record<NoteStatus, { label: string; tagType: TagType }> = {
  draft: { label: '草稿', tagType: 'info' },
  updating: { label: '更新中', tagType: 'warning' },
  done: { label: '已完结', tagType: 'success' }
}

const iconList = [markRaw(Pointer), markRaw(Share), markRaw(Collection)]
const colorList = ['#ff9c6e', '#69c0ff', '#b37feb']

const normalizeDate = (v: any) => {
  const s = String(v ?? '').trim()
  if (!s) return '-'
  return s.length >= 10 ? s.slice(0, 10) : s
}

const mapNote = (note: NoteRes, index: number): NoteCardItem => {
  const status = (String(note?.status ?? 'draft') as NoteStatus) || 'draft'
  const meta = statusLabelMap[status] || statusLabelMap.draft
  return {
    id: String(note?.id ?? ''),
    title: String(note?.title ?? ''),
    date: normalizeDate(note?.updatedAt),
    category: String(note?.category ?? ''),
    icon: iconList[index % iconList.length],
    color: colorList[index % colorList.length],
    tag: meta.label,
    tagType: meta.tagType
  }
}

const notes = ref<NoteCardItem[]>([])

const loadNotes = async () => {
  try {
    const res = await getNotesApi({ pageNum: 1, pageSize: 3 })
    if (res.code !== 200) return
    const list = Array.isArray(res.data) ? (res.data as NoteRes[]) : []
    notes.value = list.map(mapNote)
  } catch {}
}

onMounted(() => {
  void loadNotes()
})
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
