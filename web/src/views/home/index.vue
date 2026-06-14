<template>
  <div class="home-layout">
    <!-- 顶部导航栏 -->
    <header class="top-header">
      <div class="header-left">
        <div class="logo-box">
          <img :src="logoUrl" alt="Logo" class="logo-img" v-if="logoUrl" />
          <div class="logo-placeholder" v-else>
            <el-icon><Monitor /></el-icon>
          </div>
        </div>
        <h1 class="page-title">DevLog</h1>
      </div>

      <nav class="header-nav">
        <div 
          v-for="item in navItems" 
          :key="item.path"
          class="nav-item"
          :class="{ active: activeNav === item.path }"
          @click="go(item.path)"
        >
          <el-icon class="nav-icon"><component :is="item.icon" /></el-icon>
          <span>{{ item.name }}</span>
        </div>
      </nav>

      <div class="header-right">
        <div class="search-box">
          <el-icon><Search /></el-icon>
        </div>
        <el-dropdown trigger="click" @command="handleCommand">
          <div class="user-profile">
            <el-avatar :size="32" src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" />
            <span class="username">{{ nickname }}</span>
          </div>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="profile">个人中心</el-dropdown-item>
              <el-dropdown-item command="settings">设置</el-dropdown-item>
              <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </header>

    <div class="main-container">
      <!-- 侧边工具栏 -->
      <aside class="side-toolbar">
        <div class="toolbar-top">
          <div
            v-for="item in toolbarTopItems"
            :key="item.path"
            class="tool-item"
            :class="{ active: activeNav === item.path }"
            @click="go(item.path)"
          >
            <el-icon><component :is="item.icon" /></el-icon>
          </div>
        </div>
      </aside>

      <!-- 主内容区域 - 仪表盘网格 -->
      <main class="main-content">
        <router-view v-slot="{ Component, route: r }">
          <keep-alive :exclude="noCacheNames">
            <component :is="Component" :key="r.path" />
          </keep-alive>
        </router-view>
      </main>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { markRaw, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { 
  Monitor, Search, Notebook, ChatLineSquare,
  HomeFilled, Document
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const logoUrl = ref('')
const activeNav = ref('/home/dashboard')
const nickname = ref('Admin')

onMounted(() => {
  try {
    const rawUser = localStorage.getItem('user')
    if (rawUser) {
      const user = JSON.parse(rawUser)
      if (user?.nickname) nickname.value = user.nickname
    }
  } catch {}
})

const navItems = [
  { name: '首页', path: '/home/dashboard', icon: markRaw(HomeFilled), group: 'top' as const },
  { name: '代码片段管理器', path: '/home/snippets', icon: markRaw(Document), group: 'top' as const },
  { name: '技术笔记', path: '/home/notes', icon: markRaw(Notebook), group: 'top' as const },
  { name: '面试题库', path: '/home/interview', icon: markRaw(ChatLineSquare), group: 'top' as const },
]

const toolbarTopItems = navItems.filter((i) => i.group === 'top').map(({ path, icon }) => ({ path, icon }))

// 根据路由 meta.noCache 决定哪些组件不缓存
const noCacheNames = router.getRoutes()
  .filter((r) => r.meta?.noCache && r.name)
  .map((r) => r.name as string)

watch(
  () => route.name,
  (name) => {
    if (name) {
      activeNav.value = router.resolve({ name: String(name) }).path
    }
  },
  { immediate: true }
)

const go = (path: string) => {
  if (route.path !== path) router.push(path)
}

const handleCommand = (command: string) => {
  if (command === 'logout') {
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    nickname.value = 'Admin'
    ElMessage.success('已退出登录')
    router.push('/login')
  }
}
</script>

<style scoped>
.home-layout {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: #f5f7fa;
  color: #333;
  overflow: hidden;
}

/* 顶部导航栏样式 */
.top-header {
  height: 64px;
  background: #fff;
  display: flex;
  align-items: center;
  padding: 0 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  z-index: 10;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
  width: 300px;
}

.logo-box {
  width: 40px;
  height: 40px;
  background: #409eff;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 24px;
}

.page-title {
  font-size: 18px;
  font-weight: 700;
  color: #1a1a1a;
  margin: 0;
}

.header-nav {
  flex: 1;
  display: flex;
  justify-content: center;
  gap: 40px;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 15px;
  color: #606266;
  cursor: pointer;
  padding: 8px 0;
  position: relative;
  transition: all 0.3s;
}

.nav-item:hover {
  color: #409eff;
}

.nav-item.active {
  color: #409eff;
  font-weight: 600;
}

.nav-item.active::after {
  content: '';
  position: absolute;
  bottom: -18px;
  left: 0;
  width: 100%;
  height: 3px;
  background: #409eff;
  border-radius: 3px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
  width: 300px;
  justify-content: flex-end;
}

.search-box {
  font-size: 20px;
  color: #909399;
  cursor: pointer;
}

.user-profile {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
}

.username {
  font-size: 14px;
  font-weight: 500;
}

/* 主容器样式 */
.main-container {
  flex: 1;
  display: flex;
  overflow: hidden;
}

/* 侧边工具栏样式 */
.side-toolbar {
  width: 72px;
  background: #fff;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  padding: 20px 0;
  border-right: 1px solid #f0f0f0;
}

.toolbar-top, .toolbar-bottom {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 24px;
}

.tool-item {
  width: 44px;
  height: 44px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 12px;
  font-size: 22px;
  color: #909399;
  cursor: pointer;
  transition: all 0.3s;
}

.tool-item:hover {
  background: #f0f7ff;
  color: #409eff;
}

.tool-item.active {
  background: #409eff;
  color: #fff;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.4);
}

/* 内容区（承载子路由页面） */
.main-content {
  flex: 1;
  overflow: auto;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e7ed 100%);
}

.route-loading {
  min-height: 260px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
  gap: 10px;
  color: #909399;
}

.route-loading-icon {
  font-size: 22px;
  animation: route-spin 1s linear infinite;
}

.route-loading-text {
  font-size: 13px;
}

@keyframes route-spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

/* 响应式调整 */
@media (max-width: 1400px) {
  .header-left,
  .header-right {
    width: 240px;
  }
}
</style>
