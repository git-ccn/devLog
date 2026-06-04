import { createRouter, createWebHistory } from 'vue-router'
import { routes } from './router.config'

const router = createRouter({
  history: createWebHistory(),
  routes
})


// 路由拦截（导航守卫）
router.beforeEach((to, from, next) => {
  // 这里可以添加你的拦截逻辑
  // 例如：判断是否需要登录
  const isAuthenticated = Boolean(localStorage.getItem('token'))
  if (to.meta.requiresAuth && !isAuthenticated) {
    // 未认证，重定向到登录页
    next({ path: '/login' })
  } else {
    // 通过
    next()
  }
})


export default router