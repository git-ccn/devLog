import { requireInstance } from './requireNew'

export interface TodoRes {
  id: string
  title: string
  status: 'pending' | 'done'
  userId: string
  createdAt: string
  updatedAt: string
}

export interface TodoSaveReq {
  id?: string
  title: string
  status?: 'pending' | 'done'
}

export const getTodosApi = () => {
  return requireInstance.get('/todo/getTodos')
}

export const addTodoApi = (data: TodoSaveReq) => {
  return requireInstance.post('/todo/addTodo', data)
}

export const updateTodoApi = (data: TodoSaveReq) => {
  return requireInstance.post('/todo/updateTodo', data)
}

export const deleteTodoApi = (id: string) => {
  return requireInstance.post('/todo/deleteTodo', { id })
}
