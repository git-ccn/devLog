import { requireInstance } from './requireNew'

/**
 * 登录请求数据
 */
export interface LoginData {
  phone: string
  password: string
}

/**
 * 注册请求数据
 */
export interface RegisterData {
  phone: string
  password: string
}

/**
 * 找回密码请求数据
 */
export interface ForgotPasswordData {
  phone: string
  code: string
  password: string // 新密码
}

/**
 * 用户登录
 */
export const loginApi = (data: LoginData) => {
  return requireInstance.post('/user/login', data)
}

/**
 * 用户注册
 */
export const registerApi = (data: RegisterData) => {
  return requireInstance.post('/user/register', data)
}

/**
 * 忘记密码/找回密码
 */
export const forgotPasswordApi = (data: ForgotPasswordData) => {
  return requireInstance.post('/user/forgot-password', data)
}

/**
 * 退出登录
 */
export const logoutApi = () => {
  return requireInstance.post('/user/logout')
}

/**
 * 获取图形验证码图片
 */
export const sendCodeApi = () => {
  return requireInstance.get<any, Blob>('/user/send-code', { responseType: 'blob' })
}
