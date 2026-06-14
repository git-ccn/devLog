import { requireInstance } from './requireNew'

export interface InterviewRes {
  id: string
  title: string
  category: string
  categoryId: string
  status: 'todo' | 'learning' | 'mastered'
  difficulty: 'easy' | 'medium' | 'hard'
  question: string
  myAnswer: string | null
  accuracy: number | null
  solution: string | null
  createdAt: string
  updatedAt: string
}

export interface InterviewAnsReq {
  interviewId: string
  answer: string
}

export interface InterviewCategoryRes {
  id: string
  name: string
}

export interface InterviewQueryReq {
  keyword?: string
  categoryId?: string
  status?: string
  difficulty?: string
}

export interface ExamGradeReq {
  items: {
    id: string
    title: string
    question: string
    answer: string
  }[]
}

export interface ExamGradeRes {
  id: string
  accuracy: number | null
  solution: string | null
}

export const getInterviewApi = (data?: InterviewQueryReq) => {
  return requireInstance.post('/interview/getInterview', data ?? {})
}

export const getCategoryApi = () => {
  return requireInstance.get('/interview/getCategory')
}

export const addAnsApi = (data: InterviewAnsReq) => {
  return requireInstance.post('/interview/addAns', data)
}

export const updateAnsApi = (data: InterviewAnsReq) => {
  return requireInstance.post('/interview/updateAns', data)
}

export const examApi = () => {
  return requireInstance.get('/interview/exam')
}

export const gradeApi = (data: ExamGradeReq) => {
  return requireInstance.post('/interview/grade', data)
}
