<script setup>
import { onLoad } from '@dcloudio/uni-app'
import { useStore } from 'vuex'
import ourLoading from '@/components/our-loading/our-loading.vue'
import { getTenantSiteList } from '@/api/homePage.js'

const store = useStore()
let hasRedirected = false

// 初始化存储信息的方法
const initStorageSync = () => {
  // 进入App 重新赋值登录信息
  let userInfo = uni.getStorageSync('USER_INFO')
  store.dispatch('alterUserInfo', userInfo)

  // 防止重复跳转
  if (!hasRedirected) {
    uni.reLaunch({ url: '/pages/homePage/index' })
    hasRedirected = true
  }
}

// 使用onLoad生命周期
onLoad(() => {
  const userId = uni.getStorageSync('USER_ID')
  if (!userId) {
    // 未登录，跳转到登录页
    uni.showToast({
      icon: 'none',
      title: '请先登录'
    });
    uni.reLaunch({ url: '/thirdPackage/login/login' });
    return;
  }
  initStorageSync()
})
</script>