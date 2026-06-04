import Require from '@/utils/require'
import { ElMessage, ElMessageBox } from 'element-plus'

// 示例化 require，设置 baseURL 为 '/devLog'
const requireInstance = new Require({
  path: '/devLog'
})



// request 拦截器：给请求头加 token 校验
requireInstance.setRequestInterceptorHandler((config) => {
  // 示例：假设 token 存储在 localStorage 中
  const token = localStorage.getItem('token')
  if (token) {
    config.headers = config.headers || {}
    config.headers['Authorization'] = `Bearer ${token}`
  }
  return config
})

requireInstance.setResponseInterceptorHandler((response) => {
  // 1. 如果返回的是 Blob 对象（图形验证码等二进制流），直接返回原始数据
  if (response.data instanceof Blob || response.config.responseType === 'blob') {
    return response.data
  }

  // 2. 正常的 JSON 响应处理
  const { code, data, msg } = response.data

  if (code === 200) {
    return Promise.resolve({ code, data, msg })
  }

  // 3. 业务错误处理
  if (code === 404) {
    ElMessageBox.alert(msg || '未找到资源（code: 404）', '提示', {
      confirmButtonText: '确定',
      callback: () => {
        localStorage.removeItem('token')
        window.location.href = '/login'
      }
    })
  } else if (code === 500) {
    ElMessage.error(msg || '服务器内部错误（code: 500）')
  } else {
    ElMessage.error(msg || '请求失败')
  }

  return Promise.reject(new Error(msg || '请求失败'))
})

export { requireInstance }