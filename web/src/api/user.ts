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
  uuid: string // 图形验证码唯一标识
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
 * @param uuid 唯一标识
 */
export const sendCodeApi = (uuid: string) => {
  return requireInstance.get<any, Blob>(`/user/send-code?uuid=${uuid}`, { responseType: 'blob' })
}
