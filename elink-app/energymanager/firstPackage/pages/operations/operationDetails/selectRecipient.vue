<template>
  <view class="content">
    <view class="form-content">
      <u--input v-model="userName" placeholder="请输入内容" prefixIcon="search"
        prefixIconStyle="font-size: 22px;color: #909399"
        style="background-color: #ffffff;border-radius: 100rpx;"></u--input>
      <view class="user-list">
        <u-radio-group v-model="selectUser" placement="column" iconPlacement="right">
          <u-radio
            :customStyle="{ padding: index === 0 ? '30rpx 30rpx 30rpx' : '0rpx 30rpx 30rpx', borderBottom: index === userInfo.length - 1 ? 'none' : '1rpx solid #DFDFDF', color: '#000000' }"
            v-for="item in userInfo" :key="item.userId" :label="item.userName" :name="item.userId"
            @change="radioChange(item)">
          </u-radio>
        </u-radio-group>
      </view>
    </view>


  </view>
</template>

<script setup>
import { reactive, ref, watch } from 'vue'
import { findInspectionUserList } from '@/firstPackage/api/inspection.js'
const userName = ref('')
const userInfo = ref([])
const selectUser = ref('')
import { onLoad } from '@dcloudio/uni-app'
onLoad((options) => {
   uni.setStorageSync('operationsCurrentIndex', 1)
  getUserInfo(options.taskStuts)
  if (options.taskId) {
    selectUser.value = options.taskId
  }
})
const getUserInfo = (type) => {
  const userId = uni.getStorageSync('USER_ID')
  findInspectionUserList({
    type: type == 3 ? 2 : type == 4 ? 3 : 1,
    userId: userId
  }).then(res => {
    userInfo.value = res.data
  })
}
watch(userName, (newValue) => {

})
const radioChange = (val) => {
   
  uni.navigateBack({
    delta: 1, // 返回上一级页面，默认为1
    success: () => {
      // 可选：在返回前传递数据（需配合 onUnload 或 onUnload 页面监听）
      uni.$emit('backData', val); // 发送数据
    }
  });
};



</script>

<style lang="scss" scoped>
.content {
  width: 100%;
  height: 100%;
  margin: 0 auto;
  overflow: hidden;
  background-color: #F5F5F5;

  .form-content {
    width: 94%;
    margin: 50rpx auto;
  }

  .title {
    margin-top: 50rpx;
  }

  .u-textarea {
    margin-top: 30rpx;
    background-color: #E5E5E5;
  }

  .user-list {
    background-color: #fff;
    border-radius: 36rpx;
    margin-top: 34rpx;
    box-shadow: 0rpx 0rpx 2rpx 0rpx rgba(0, 0, 0, 0.05);
  }

}
</style>