<template>
  <div class="not-found-page">
    <div class="content-card">
      <div class="code">404</div>
      <div class="title">页面不存在</div>
      <div class="desc">抱歉，你访问的页面可能已被删除、名称已更改，或暂时不可用。</div>
      <div class="actions">
        <el-button type="primary" @click="router.push('/login')">返回登录页</el-button>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { useRouter } from 'vue-router'

const router = useRouter()
</script>

<style scoped>
.not-found-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
  background: linear-gradient(180deg, #f5f9ff 0%, #eef4ff 100%);
}

.content-card {
  width: 100%;
  max-width: 520px;
  padding: 56px 40px;
  text-align: center;
  background: #fff;
  border-radius: 24px;
  box-shadow: 0 16px 40px rgba(64, 158, 255, 0.12);
}

.code {
  font-size: 88px;
  line-height: 1;
  font-weight: 700;
  color: #409eff;
  margin-bottom: 16px;
}

.title {
  font-size: 28px;
  font-weight: 600;
  color: #1f2937;
  margin-bottom: 12px;
}

.desc {
  font-size: 15px;
  line-height: 1.8;
  color: #6b7280;
  margin-bottom: 28px;
}

.actions {
  display: flex;
  justify-content: center;
}

@media (max-width: 768px) {
  .content-card {
    padding: 40px 24px;
    border-radius: 20px;
  }

  .code {
    font-size: 72px;
  }

  .title {
    font-size: 24px;
  }
}
</style>
