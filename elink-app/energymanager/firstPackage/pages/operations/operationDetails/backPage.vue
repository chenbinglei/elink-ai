<template>
  <view class="content">
    <view class="form-content">
      <view class="title">处理意见：</view>
      <u--textarea v-model="handlingOpinion" placeholder="请输入内容" style="border-radius: 24rpx;"></u--textarea>
    </view>
    <view class="button">
      <u-button type="primary" :disabled="disabled" shape="circle" text="确认退回" @click="goToBackPage()"></u-button>
    </view>
  </view>
</template>

<script setup>
import { updateInspectionTask } from '@/firstPackage/api/inspection.js'
import { onLoad } from '@dcloudio/uni-app'

import { reactive, ref, watch } from 'vue'
const handlingOpinion = ref('')
const disabled = ref(true)
const inspectionTask = ref({})
onLoad((options) => {
  uni.setStorageSync('operationsCurrentIndex', 1)
  inspectionTask.value = JSON.parse(decodeURIComponent(options.data));

})
watch(handlingOpinion, (newValue) => {
  if (newValue.length > 0) {
    disabled.value = false
  } else {
    disabled.value = true
  }
})
const goToBackPage = () => {
  const userId = uni.getStorageSync('USER_ID')
  updateInspectionTask({
    operationType: inspectionTask.value.taskStatus == 2 ? 3 : 7,
    id: inspectionTask.value.id,
    userId: userId
  }).then(res => {
    if (res.success) {
      uni.showToast({
        title: '退回成功',
        icon: 'success',
        duration: 2000
      })

      uni.reLaunch({
        url: '/pages/operations/index',
        success: () => {
        },
        fail: (err) => {
        }
      });
    }
  })


}

</script>

<style lang="scss" scoped>
.content {
  width: 100%;
  height: 100%;
  margin: 0 auto;
  overflow: hidden;
  background-color: #f5f5f5;
  position: relative;

  .form-content {
    width: 88%;
    margin: 0 auto;
  }

  .title {
    margin-top: 50rpx;
    font-weight: 600;
    font-size: 24rpx;
    color: #626365;
  }

  .u-textarea {
    margin-top: 30rpx;
    background-color: #e5e5e5;
  }

  .button {
    position: absolute;
    bottom: 10%;
    width: 60%;
    left: 50%;
    transform: translateX(-50%);
  }
}
</style>