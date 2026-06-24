<template>
  <view class="content">
    <image class="bg-image" src="/thirdPackage/image/bgcLogin.png" mode="widthFix"></image>

    <view class="content-containers">
      <view class="tabs">
        <view v-for="tab in tabsArray" :key="tab.label" :class="['tab-item', { active: tabsIndex === tab.label }]"
          @click="changeTab(tab.label)">
          {{ tab.name }}
        </view>
      </view>

      <view class="login-containers" v-if="tabsIndex === 'login'">
        <view class="description">
          登录账号密码与“能源管家后台”一致，若没有账号可向公司管理者申请开通
        </view>
        <view class="content-title">
          <u--input placeholder="请输入账号" class="input" v-model="userName" clearable suffixIconStyle="color: #AAAEB0"
            style="background-color: #F4F8FB;"></u--input>
          <u--input placeholder="请输入密码" class="input" v-model="password" type="password" clearable
            suffixIconStyle="color: #AAAEB0" style="background-color: #F4F8FB;"></u--input>
        </view>
        <!-- <view class="tips"> xxx用户不存在 </view> -->
      </view>
      <view class="login-containers" v-else>
        <view class="description">
          首次登陆需使用账号密码登录，绑定手机号后可使用手机号便捷登录
        </view>
        <view class="content-title">
          <u--input placeholder="请输入你的手机号" class="input" v-model="phone" clearable suffixIconStyle="color: #AAAEB0"
            style="background-color: #F4F8FB;"></u--input>
          <u--input v-model="nlCode" class="input" placeholder="请输入验证码" :border="false"
            style="background-color: #F4F8FB;">
            <template #suffix>
              <u-code ref="uCode" @change="codeChange" seconds="20" changeText="X秒重新获取哈哈哈"></u-code>
              <view @tap="getCode" class="u-ml-sm" :style="{ color: phone ? '#007AFF' : '#BFBFBF' }">
                {{ tips }}
              </view>
            </template>
          </u--input>
        </view>
        <!-- <view class="tips"> xxx用户不存在 </view> -->
      </view>
      <view class="btn-box">
        <u-button type="primary" @click="loginBtn" style="border-radius: 49rpx;">登录</u-button>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, reactive, watch, computed, onMounted, onBeforeMount } from 'vue'
import { getTenantSiteList } from '@/api/homePage.js'
import { login } from '@/thirdPackage/api/login.js'
import { onShow } from '@dcloudio/uni-app'
import Crypto from './crypto.js'
const tabsArray = [
  {
    name: '密码登录',
    label: 'login'
  },
  // {
  //   name: '手机号注册',
  //   label: 'register'
  // }
]
const uCode = ref(null);
const nlCode = ref('');
const tabsIndex = ref('login');
const userName = ref('');
const password = ref('');
const tips = ref('获取验证码')
const phone = ref('');
const loginType = ref('')
const code = ref('')
const changeTab = (label) => {
  tabsIndex.value = label
}

const loginBtn = () => {
  if (tabsIndex.value === 'login') {
    // if (!username.value || !password.value) {
    //   uni.showToast({
    //     title: '请输入账号密码',
    //     icon: 'none'
    //   })
    //   return
    // }
    login({
      userAccount: userName.value,
      password: Crypto.CBC_encrypt(password.value), // 密码加密
      grant_type: 'sys_pwd',
      client_id: 'enlink-client',
      client_secret: 'enlink-client'

    }).then(res => {
      if (res.success) {
        let loginData = res.data ? res.data : {};
        console.log('loginData', loginData)
        // uni.setStorage({ key: 'USER_INFO', data: loginData });
        // uni.setStorage({ key: 'USER_ID', data: res.data.id });

        uni.setStorageSync('USER_ID', res.data.id);
         uni.setStorageSync('tenantId', loginData.tenantId);
        uni.setStorageSync('USER_INFO', loginData);
       


        querygetTenantSiteList(res.data.id)
        uni.showToast({
          title: '登录成功',
          icon: 'none'
        })
        setTimeout(() => {
          uni.navigateBack({
            delta: 1,
            fail: () => {
              uni.reLaunch({ url: '/pages/welcome/index' });
            }
          });
        }, 150); // 150ms 足够
      } else {
        uni.showToast({
          title: res.msg,
          icon: 'none'
        })
      }
    })
  } else {
    if (!phone.value || !nlCode.value) {
      uni.showToast({ title: '请输入手机号和验证码', icon: 'none' })
      return
    }
  }

}
const codeChange = (e) => {
  // 验证码变化时的处理逻辑
}

const getCode = () => {
  // 获取验证码逻辑
  if (!phone.value) {
    uni.showToast({ title: '请输入手机号', icon: 'none' })
    return
  }

  // // 调用 uCode 的 start 方法开始倒计时
  // uCode.value.start()
  // tips.value = '发送中...'
}
const querygetTenantSiteList = (val) => {
  getTenantSiteList({ userId: val }).then(res => {
    if (res.message) {
      const arr = res.data
        .filter(item => item.type === 2)
        .map(item => item.id)
        .join(','); // 转换为逗号分隔的字符串
      const ids = arr.split(',')
      const company = res.data
        .filter(item => item.type === 1)
        .map(item => item.name);
      const allScenarioTypes = res.data
        .filter(item => item.scenarioTypes && typeof item.scenarioTypes === 'string')
        .flatMap(item => {
          // 如果是逗号分隔的字符串，拆分成数字数组
          return item.scenarioTypes.split(',').map(Number);
        });
      // 去重
      const uniqueScenarioTypes = Array.from(new Set(allScenarioTypes));
      uni.setStorageSync('COMPANY', company)
      uni.setStorageSync('SITE_LIST', ids)
      uni.setStorageSync('SCENARIO_TYPES', uniqueScenarioTypes)

      // 获取最早 createTime
      const earliestCreateTime = getEarliestCreateTime(res.data)
      uni.setStorageSync('EARLIEST_CREATE_TIME', earliestCreateTime)
    }
  })
}
// 获取最早 createTime 的方法
const getEarliestCreateTime = (data) => {
  // 假设 data 是一个数组，每个元素有 createTime 字段
  if (!data || data.length === 0) return null

  // 过滤掉没有 createTime 的项
  const validData = data.filter(item => item.createTime)

  if (validData.length === 0) return null

  // 找出最早的时间
  return validData.reduce((earliest, current) => {
    return new Date(current.createTime) < new Date(earliest.createTime)
      ? current : earliest
  }).createTime
}
</script>

<style lang="scss" scoped>
.content {
  height: 100%;
  width: 100%;
  position: relative;
}

.bg-image {
  width: 100%;
  height: 30%;
}

.content-containers {
  height: 75%;
  width: 100%;
  border-radius: 36rpx;
  position: absolute;
  top: 25%;
  background-color: #fff;
  padding: 63rpx;

  .tabs {
    font-weight: 400;
    font-size: 32rpx;
    color: #000000;
    display: flex;
    gap: 40rpx;
  }

  .active {
    font-weight: 600;
    font-size: 38rpx;
    color: #000000;
    transform: translateY(-10rpx);
    padding-bottom: 13rpx;
  }

  .tab-item.active::after {
    content: "";
    position: absolute;
    left: 0;
    bottom: 0;
    width: 100%;
    height: 15rpx;
    background: linear-gradient(to right,
        #fff,
        rgba(56, 139, 255, 1) 20%,
        rgba(56, 139, 255, 0.3) 60%,
        rgba(56, 139, 255, 0.3) 70%);
  }
}

.login-containers {
  margin-top: 40rpx;

  .description {
    font-weight: 400;
    font-size: 26rpx;
    color: #8c8c8c;
  }

  .input {
    margin-top: 23rpx;
  }
}

.btn-box {
  margin-top: 28rpx;
  width: 100%;
}

.tips {
  margin-top: 20rpx;
  font-weight: 400;
  font-size: 28rpx;
  color: #ff3a32;
}
</style>