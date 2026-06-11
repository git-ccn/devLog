import { requireInstance } from './requireNew'

export interface NoteQueryReq {
  keyword?: string
  categoryId?: string
  status?: string
  tagIds?: string[]
  pageNum?: number
  pageSize?: number
}

export interface NoteRes {
  id: string
  title?: string
  summary?: string
  content?: string
  category?: string
  status?: string
  readTime?: number
  updatedAt?: string
  tags?: string[]
}

export interface AddNoteReq {
  id?: string
  title: string
  summary?: string
  content: string
  categoryId: string
  status?: string
  tags?: string[]
}

export interface UpdateNoteReq {
  id: string
  title: string
  summary?: string
  content: string
  categoryId: string
  status?: string
  tags?: string[]
}

export interface NoteStatsRes {
  total?: number
  done?: number
  updating?: number
  month?: number
}

export const getNotesApi = (data?: NoteQueryReq) => {
  return requireInstance.post('/note/getNote', data ?? {})
}

export const getNoteTagsApi = (name?: string) => {
  return requireInstance.get(`/note/getTag${name ? `?name=${encodeURIComponent(name)}` : ''}`)
}

export const getNoteCategoriesApi = () => {
  return requireInstance.get('/note/getCategory')
}

export const getNoteStatsApi = () => {
  return requireInstance.get('/note/getStats')
}

export const addNoteApi = (data: AddNoteReq) => {
  return requireInstance.post('/note/addNote', data)
}

export const updateNoteApi = (data: UpdateNoteReq) => {
  return requireInstance.post('/note/updateNote', data)
}

export const deleteNoteApi = (data: { id: string }) => {
  return requireInstance.post('/note/deleteNote', data)
}
