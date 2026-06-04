// 提供方法由外部设置响应处理函数
import axios, { AxiosInstance, AxiosRequestConfig, AxiosResponse } from 'axios'

class Require {
  private timeOut: number
  private path: string
  private instance: AxiosInstance
  private requestInterceptorHandler?: <T = any, R = any>(config: AxiosRequestConfig<T>) => R | Promise<R>
  private responseInterceptorHandler?: <T = any, R = any>(response: AxiosResponse<T>) => R | Promise<R>

  constructor({ timeOut = 10000, path = '' }: { timeOut?: number; path?: string } = {}) {
    this.timeOut = timeOut
    this.path = path
    this.instance = axios.create({
      baseURL: this.path,
      timeout: this.timeOut,
      withCredentials: true // 允许跨域携带 cookie
    })

    // 添加请求前拦截器，调用外部自定义的处理逻辑
    this.instance.interceptors.request.use(
      (config: any) => {
        if (this.requestInterceptorHandler) {
          return this.requestInterceptorHandler(config)
        }
        return config
      },
      (error) => {
        return Promise.reject(error)
      }
    )

    // 添加响应拦截器，可由外部方法覆写
    this.instance.interceptors.response.use(
      (response) => {
        if (this.responseInterceptorHandler) {
          return this.responseInterceptorHandler(response)
        }
        return response
      },
      (error) => {
        return Promise.reject(error)
      }
    )
  }

  // 提供方法由外部设置请求前处理函数
  // 之所以报红，是因为 requestInterceptorHandler 和 responseInterceptorHandler 的类型定义为泛型方法，而下面赋值只是直接赋 handler，类型未能完全匹配。
  // 解决方式是将这两个属性的类型去掉方法泛型，直接定义为普通函数类型，这样就不会有类型不兼容报错。
  setRequestInterceptorHandler(
    handler: (config: AxiosRequestConfig) => any
  ) {
    this.requestInterceptorHandler = handler
  }

  setResponseInterceptorHandler(
    handler: (response: AxiosResponse) => any
  ) {
    this.responseInterceptorHandler = handler
  }

  // 如果需要公开 timeout 和 path，可以直接通过方法传参设置
  setConfig({ timeOut, path }: { timeOut?: number; path?: string }) {
    if (timeOut !== undefined) {
      this.timeOut = timeOut
      this.instance.defaults.timeout = timeOut
    }
    if (path !== undefined) {
      this.path = path
      this.instance.defaults.baseURL = path
    }
  }

  // 封装请求方法
  request<RequestType = any, ResponseType = any>(
    config: AxiosRequestConfig<RequestType>
  ): Promise<ResponseType> {
    return this.instance.request<any, ResponseType, RequestType>(config)
  }

  // 新增 get 方法
  get<RequestType = any, ResponseType = any>(
    url: string,
    config?: AxiosRequestConfig<RequestType>
  ): Promise<ResponseType> {
    return this.instance.get<any, ResponseType, RequestType>(url, config)
  }

  // 新增 post 方法
  post<RequestType = any, ResponseType = any>(
    url: string,
    data?: RequestType,
    config?: AxiosRequestConfig<RequestType>
  ): Promise<ResponseType> {
    return this.instance.post<any, ResponseType, RequestType>(url, data, config)
  }
}

export default Require