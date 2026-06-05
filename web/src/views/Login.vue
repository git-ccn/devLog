<template>
  <div class="login-page">
    <div class="background-decor">
      <div class="circle circle-1"></div>
      <div class="circle circle-2"></div>
    </div>
    
    <div class="login-card-wrapper">
      <div class="login-card">
        <!-- Left Section: Illustration -->
        <div class="login-left">
          <div class="illustration">
            <img src="../assets/images/auth/login.avif" alt="illustration" />
          </div>
          <div class="register-link">
            还没有账号？ <el-link type="primary" underline="never" @click="emit('switch', 'register')">立即注册</el-link>
          </div>
        </div>

        <!-- Right Section: Form -->
        <div class="login-right">
          <div class="login-header">
            <h2 class="title">刷题记笔记系统</h2>
          </div>

          <el-form :model="loginForm" :rules="rules" ref="loginFormRef" class="login-form" @keyup.enter="onLogin">
            <el-form-item prop="phone">
              <el-input 
                v-model="loginForm.phone" 
                placeholder="手机号"
                :prefix-icon="Iphone"
              />
            </el-form-item>
            
            <el-form-item prop="password">
              <el-input 
                v-model="loginForm.password" 
                type="password" 
                placeholder="密码"
                :prefix-icon="Lock"
                show-password 
              />
            </el-form-item>

            <div class="form-options">
              <el-link type="info" underline="never" @click="emit('switch', 'forgot-password')">忘记密码</el-link>
            </div>

            <el-form-item>
              <el-button type="primary" :loading="loading" class="login-btn" @click="onLogin">登录</el-button>
            </el-form-item>

            <div class="social-login">
              <el-link type="info" underline="never">使用社交账号登录</el-link>
            </div>
          </el-form>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { Iphone, Lock } from '@element-plus/icons-vue'
import { loginApi } from '@/api/user'

const emit = defineEmits<{
  (e: 'switch', mode: 'login' | 'register' | 'forgot-password'): void
}>()

const router = useRouter()
const loading = ref<boolean>(false)
const loginForm = reactive({
  phone: '',
  password: ''
})

const rules = {
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号格式', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
}

const loginFormRef = ref()

const onLogin = () => {
  if (!loginFormRef.value) return
  loginFormRef.value.validate(async (valid: boolean) => {
    if (!valid) return
    loading.value = true
    try {
      const res = await loginApi({
        phone: loginForm.phone,
        password: loginForm.password
      })
      if (res.code === 200) {
        localStorage.setItem('token', res.data.token)
        ElMessage.success('登录成功')
        router.push('/home')
      } else {
        ElMessage.error(res.msg || '登录失败')
      }
    } catch (error: any) {
      // 错误已在拦截器处理
    } finally {
      loading.value = false
    }
  })
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  width: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f0f4f8;
  position: relative;
  overflow: hidden;
}

/* Background decorations */
.background-decor {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 0;
}

.circle {
  position: absolute;
  border-radius: 50%;
  background: linear-gradient(135deg, #409eff 0%, #79bbff 100%);
  opacity: 0.1;
}

.circle-1 {
  width: 600px;
  height: 600px;
  top: -200px;
  left: -200px;
}

.circle-2 {
  width: 400px;
  height: 400px;
  bottom: -100px;
  right: -100px;
  background: linear-gradient(135deg, #409eff 0%, #337ecc 100%);
}

.login-card-wrapper {
  z-index: 1;
  width: 100%;
  max-width: 1000px;
  padding: 20px;
}

.login-card {
  display: flex;
  background: #ffffff;
  border-radius: 20px;
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  min-height: 550px;
}

/* Left Section */
.login-left {
  flex: 1.2;
  background-color: #f8fbff;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 40px;
  border-right: 1px solid #f0f0f0;
}

.illustration {
  width: 100%;
  max-width: 400px;
  margin-bottom: 40px;
}

.illustration img {
  width: 100%;
  height: auto;
  object-fit: contain;
}

.register-link {
  font-size: 14px;
  color: #666;
}

/* Right Section */
.login-right {
  flex: 1;
  padding: 60px 50px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.login-header {
  margin-bottom: 40px;
  text-align: left;
}

.title {
  font-size: 28px;
  font-weight: 700;
  color: #1a1a1a;
  margin: 0;
}

.login-form {
  width: 100%;
}

:deep(.el-input__wrapper) {
  padding: 12px 15px;
  border-radius: 12px;
  background-color: #f5f7fa;
  box-shadow: none !important;
  border: 1px solid transparent;
  transition: all 0.3s;
}

:deep(.el-input__wrapper:hover) {
  border-color: #409eff;
}

:deep(.el-input__wrapper.is-focus) {
  background-color: #fff;
  border-color: #409eff;
  box-shadow: 0 0 0 1px #409eff !important;
}

:deep(.el-input__inner) {
  height: 24px;
}

.form-options {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 24px;
}

.login-btn {
  width: 100%;
  height: 50px;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  letter-spacing: 1px;
  margin-top: 10px;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
}

.social-login {
  margin-top: 30px;
  display: flex;
  justify-content: space-between;
  font-size: 13px;
}

/* Responsive */
@media (max-width: 850px) {
  .login-left {
    display: none;
  }
  .login-card {
    max-width: 450px;
    margin: 0 auto;
  }
}
</style>
