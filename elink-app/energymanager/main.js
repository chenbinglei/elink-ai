import { createSSRApp } from 'vue'
import App from './App.vue'
import store from './store' // 引入 Vuex 4 store
import '@/utils/serverDomainName.js'; // 设置请求环境
import common from '@/common/common.js' // 配置公共方法
import filters from "@/common/filters.js" // 全局过滤器

export function createApp() {
  const app = createSSRApp(App)
  
  // 使用 Vuex 4
  app.use(store)
  
  // 设置平台标识
  let platform = ''
  // #ifdef MP-WEIXIN
  platform = 'wechat'
  // #endif
  // #ifdef MP-ALIPAY
  platform = 'alipay'
  // #endif
  // #ifdef APP-VUE || H5
  platform = 'app'
  // #endif
  
  // 设置全局属性
  app.config.globalProperties.$platform = platform
  app.config.globalProperties.$store = store
  
  // 服务器静态资源路径方法
  app.config.globalProperties.getStaticFilePath = (url) => {
    let static_path = "/image/app/"
    const baseUrl = uni.getStorageSync('BASE_URL') || ''
    if (baseUrl.indexOf('jshqjk.com') !== -1) static_path = "/imageApp/app/"
    const staticPath = uni.getStorageSync('STATIC_PATH') || ''
    return staticPath + static_path + url
  }
  
  // 注册全局过滤器（Vue 3 中已移除过滤器，改为全局方法）
  // 方案1：创建 $filters 对象集中管理
  app.config.globalProperties.$filters = {}
  for (let key in filters) {
    app.config.globalProperties.$filters[key] = filters[key]
  }
  
  // 挂载公共方法
  for (let key in common) {
    app.config.globalProperties[key] = common[key]
  }
  
  // 如果您需要在应用启动时设置请求环境，可以在这里执行
  // 或者移到 App.vue 的 onLaunch 生命周期中
  // import('@/utils/serverDomainName.js').then(module => {
  //   module.init()
  // })
  
  return {
    app
  }
}