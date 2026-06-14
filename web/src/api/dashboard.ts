import { requireInstance } from './requireNew'

export interface DashboardWeeklyItem {
  day: string
  snippetCount: number
  noteCount: number
}

export interface DashboardMastery {
  todo: number
  learning: number
  mastered: number
}

export interface DashboardStatsRes {
  mastery: DashboardMastery
  weekly: DashboardWeeklyItem[]
  progress: number
}

export const getDashboardStatsApi = () => {
  return requireInstance.get('/dashboard/getStats')
}
