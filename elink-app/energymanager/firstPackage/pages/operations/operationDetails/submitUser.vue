<template>
  <view class="content">
    <view class="form-content">
      <u--form labelPosition="left" :model="handoverForm" :rules="rules" ref="uForm">
        <u-form-item label="接收人" :labelWidth="270" prop="handoverForm.recipient" ref="recipient" @click="selectrecipient">
          <u--input v-model="handoverForm.recipient" disabled disabledColor="#ffffff" placeholder="请选择" border="none" suffixIcon="arrow-right"
            style="background: #F5F5F5;"></u--input>
        </u-form-item>
      </u--form>
    </view>
    <view class="button">
      <u-button type="primary" :disabled="disabled" shape="circle" text="确认提交" @click="goToBackPage()"></u-button>
    </view>
  </view>
</template>

<script setup>
import { ref, watch, onMounted } from "vue";
import { onLoad } from '@dcloudio/uni-app'
import { updateInspectionTask } from '@/firstPackage/api/inspection.js'
const disabled = ref(true)
const handoverForm = ref({
  recipient: "",
});
const rules = {
  recipient: [
    { required: true, message: "请选择接收人", trigger: "blur" },
  ],
};
const userInfo = ref([])
// 传递过来的巡检任务
const inspectionTask = ref({})
onLoad((options) => {
  uni.setStorageSync('operationsCurrentIndex', 1)
  const data = JSON.parse(decodeURIComponent(options.data));
  inspectionTask.value = data
  uni.$on('backData', (data) => {

    userInfo.value = data
    handoverForm.value.recipient = data.userName
    if (handoverForm.value.recipient) {
      disabled.value = false
    }
  });

})

// 选择交接人
const selectrecipient = () => {
  uni.navigateTo({
    url: "/firstPackage/pages/operations/operationDetails/selectRecipient?taskStuts=" + inspectionTask.value.taskStatus + "&taskId=" + userInfo.value.userId
  });
};
const goToBackPage = () => {
  let obj = {
    operationType: 5,
    id: inspectionTask.value.id,
    userId: userInfo.value.userId, //用户id
    operationUserId: userInfo.value.userId

  }
  updateInspectionTask(obj).then(res => {

    if (res.success) {
      uni.showToast({
        title: '退回成功',
        icon: 'success',
        duration: 2000
      })
      
      uni.reLaunch({
        url: '/pages/operations/index',
        success: () => {
          console.log('跳转成功');
        },
        fail: (err) => {
          console.error('跳转失败:', err);
        }
      });
    }
  })
}
// 上一级页面的 onShow 或 onLoad 中监听
// onMounted(() => {
//   uni.$on('backData', (data) => {
//     handoverForm.value.recipient = data
//     if (handoverForm.value.recipient) {
//       disabled.value = false
//     }
//     // 处理数据
//   });
// });
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
    margin: 30rpx auto;
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