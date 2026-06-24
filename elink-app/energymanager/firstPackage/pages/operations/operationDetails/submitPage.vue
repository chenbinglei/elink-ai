<template>
  <view class="content">
    <view class="form-content">
      <view class="title">处理意见：</view>
      <u--textarea v-model="handlingOpinion" placeholder="请输入内容" style="border-radius: 24rpx;"></u--textarea>
    </view>
    <view class="button">
      <u-button type="primary" :disabled="disabled" shape="circle" text="确认提交" @click="goToBackPage()"></u-button>
    </view>
  </view>
</template>

<script setup>
import { onLoad } from '@dcloudio/uni-app'
import { reactive, ref, watch } from 'vue'
import { updateInspectionTask } from '@/firstPackage/api/inspection.js'
const handlingOpinion = ref('')
const disabled = ref(true)
const inspectionTask = ref('')
watch(handlingOpinion, (newValue) => {
  if (newValue.length > 0) {
    disabled.value = false
  } else {
    disabled.value = true
  }
})
onLoad((options) => {
  // 这里可以放置页面加载时的逻辑
  const data = JSON.parse(decodeURIComponent(options.data));
  inspectionTask.value = data
    uni.setStorageSync('operationsCurrentIndex', 1)

})
const goToBackPage = () => {
  const userId = uni.getStorageSync('USER_ID')
  let obj = {
    id: inspectionTask.value.id,
    operationOpinion: handlingOpinion.value,
    operationType: 6,
    userId: userId


  }
  updateInspectionTask(obj).then(res => {
    if (res.code) {
      uni.showToast({
        title: '提交成功',
        icon: 'success',
        duration: 2000
      })
     
      uni.reLaunch({
        url: '/pages/operations/index',
        success: () => {
        },
        fail: (err) => {
          console.error('跳转失败:', err);
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
  background-color: #F5F5F5;
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
    background-color: #E5E5E5;
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