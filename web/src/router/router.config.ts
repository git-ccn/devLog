import type { RouteRecordRaw } from 'vue-router'
import Auth from '@/views/Auth.vue'
import Home from '@/views/home/index.vue'

export const routes: Array<RouteRecordRaw> = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: Auth
  },
  {
    path: '/home',
    component: Home,
    redirect: '/home/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/home/dashboard/index.vue')
      },
      {
        path: 'snippets',
        name: 'Snippets',
        component: () => import('@/views/home/snippets/index.vue')
      },
      {
        path: 'notes',
        name: 'Notes',
        component: () => import('@/views/home/notes/index.vue')
      },
      {
        path: 'interview',
        name: 'Interview',
        component: () => import('@/views/home/interview/index.vue')
      },
      {
        path: 'system',
        name: 'System',
        component: () => import('@/views/home/system/index.vue')
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/NotFound.vue')
  }
]
