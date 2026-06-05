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
            <img src="../assets/images/auth/forgot-password.avif" alt="illustration" />
          </div>
          <div class="register-link">
            想起密码了？ <el-link type="primary" underline="never" @click="emit('switch', 'login')">立即登录</el-link>
          </div>
        </div>

        <!-- Right Section: Form -->
        <div class="login-right">
          <div class="login-header">
            <h2 class="title">重置密码</h2>
          </div>

          <el-form :model="forgotForm" :rules="rules" ref="forgotFormRef" class="login-form" @keyup.enter="onReset">
            <el-form-item prop="phone">
              <el-input 
                v-model="forgotForm.phone" 
                placeholder="手机号"
                :prefix-icon="Iphone"
              />
            </el-form-item>
            
            <el-form-item prop="code">
              <div class="code-input-wrapper">
                <el-input 
                  v-model="forgotForm.code" 
                  placeholder="验证码"
                  :prefix-icon="Message"
                />
                <div class="captcha-img-wrapper" @click="refreshCaptcha">
                  <img :src="captchaUrl" alt="验证码" title="点击刷新" />
                </div>
              </div>
            </el-form-item>

            <el-form-item prop="password">
              <el-input 
                v-model="forgotForm.password" 
                type="password" 
                placeholder="新密码"
                :prefix-icon="Lock"
                show-password 
              />
            </el-form-item>

            <el-form-item prop="confirmPassword">
              <el-input 
                v-model="forgotForm.confirmPassword" 
                type="password" 
                placeholder="确认新密码"
                :prefix-icon="Lock"
                show-password 
              />
            </el-form-item>

            <el-form-item>
              <el-button type="primary" :loading="loading" class="login-btn" @click="onReset">重置密码</el-button>
            </el-form-item>
          </el-form>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Iphone, Lock, Message } from '@element-plus/icons-vue'
import { forgotPasswordApi, sendCodeApi } from '@/api/user'
import { v4 as uuidv4 } from 'uuid'

const emit = defineEmits<{
  (e: 'switch', mode: 'login' | 'register' | 'forgot-password'): void
}>()

const loading = ref<boolean>(false)
const captchaUrl = ref<string>('')
const captchaUuid = ref<string>('')
const forgotFormRef = ref()

const forgotForm = reactive({
  phone: '',
  code: '',
  password: '',
  confirmPassword: ''
})

const refreshCaptcha = async () => {
  try {
    // 生成新的 UUID 标识本次验证码
    captchaUuid.value = uuidv4()
    const blob = await sendCodeApi(captchaUuid.value)
    if (captchaUrl.value) {
      URL.revokeObjectURL(captchaUrl.value) // 释放旧内存
    }
    // blob 此时已被 TS 识别为正确的 Blob 类型
    captchaUrl.value = URL.createObjectURL(blob)
  } catch (error) {
    console.error('获取验证码失败', error)
  }
}

onMounted(() => {
  refreshCaptcha()
})

const validateConfirmPassword = (rule: any, value: string, callback: any) => {
  if (value === '') {
    callback(new Error('请再次输入新密码'))
  } else if (value !== forgotForm.password) {
    callback(new Error('两次输入密码不一致!'))
  } else {
    callback()
  }
}

const rules = {
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号格式', trigger: 'blur' }
  ],
  code: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { len: 4, message: '验证码为4位字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

const onReset = () => {
  if (!forgotFormRef.value) return
  forgotFormRef.value.validate(async (valid: boolean) => {
    if (!valid) return
    loading.value = true
    try {
      const res = await forgotPasswordApi({
        phone: forgotForm.phone,
        code: forgotForm.code,
        password: forgotForm.password,
        uuid: captchaUuid.value // 携带 UUID 进行后端校验
      })
      if (res.code === 200) {
        ElMessage.success('密码重置成功')
        emit('switch', 'login')
      } else {
        ElMessage.error(res.msg || '重置失败')
        refreshCaptcha() // 失败自动刷新验证码
      }
    } catch (error: any) {
      // 错误已在拦截器处理
      refreshCaptcha()
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

.code-input-wrapper {
  display: flex;
  gap: 10px;
  width: 100%;
}

.get-code-btn {
  white-space: nowrap;
  border-radius: 12px;
  height: 50px;
}

.captcha-img-wrapper {
  cursor: pointer;
  height: 50px;
  width: 150px; /* 固定宽度，与后端一致 */
  flex-shrink: 0; /* 防止被输入框挤压 */
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
  border-radius: 12px;
  overflow: hidden;
  border: 1px solid transparent;
  transition: all 0.3s;
}

.captcha-img-wrapper:hover {
  border-color: #409eff;
}

.captcha-img-wrapper img {
  height: 100%;
  width: auto;
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
