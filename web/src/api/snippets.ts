import { requireInstance } from './requireNew'

export interface SnippetQueryReq {
  title?: string
  content?: string
  keyword?: string
  language?: string
  showDeleted?: boolean
  tagNames?: string[]
  pageNum?: number
  pageSize?: number
}

export interface SnippetSaveReq {
  id?: string
  title: string
  content: string
  language: string
  deleted?: number
  tags?: Array<{ id?: string; name: string }>
}

export interface SnippetRes {
  id: string
  title: string
  content: string
  language: string
  deleted: number
  createdAt?: string
  updatedAt?: string
  tags?: TagRes[]
}

export interface TagRes {
  id: string
  name: string
  createdAt?: string
}

export const getSnippetApi = (data?: SnippetQueryReq) => {
  return requireInstance.post('/snippet/getSnippet', data ?? {})
}

export const addSnippetApi = (data: SnippetSaveReq) => {
  return requireInstance.post('/snippet/addSnippet', data)
}

export const updateSnippetApi = (data: SnippetSaveReq) => {
  return requireInstance.post('/snippet/updateSnippet', data)
}

export const getTagsApi = (name?: string) => {
  return requireInstance.get(`/snippet/getTags${name ? `?name=${encodeURIComponent(name)}` : ''}`)
}

export const getLanguagesApi = () => {
  return requireInstance.get('/snippet/getLanguages')
}

export const deleteSnippetApi = (id: string) => {
  return requireInstance.post('/snippet/deleteSnippet', { id })
}

export const hardDeleteSnippetApi = (id: string) => {
  return requireInstance.post('/snippet/hardDeleteSnippet', { id })
}

export const deleteTagApi = (id: string) => {
  return requireInstance.post('/snippet/deleteTag', { id })
}
