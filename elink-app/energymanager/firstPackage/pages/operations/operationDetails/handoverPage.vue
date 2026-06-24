<template>
  <view class="content">
    <view class="form-content">
      <u--form labelPosition="left" :model="handoverForm" :rules="rules" ref="uForm">
        <u-form-item label="接收人" :labelWidth="270" prop="handoverForm.recipient" ref="recipient"
          @click="selectrecipient">
          <u--input v-model="handoverForm.recipient" disabled disabledColor="#ffffff" placeholder="请选择" border="none"
            suffixIcon="arrow-right" style="background: #F5F5F5;"></u--input>
        </u-form-item>

        <u-form-item label="处理意见" :labelWidth="270">
        </u-form-item>
        <u-form-item label="" :labelWidth="0" prop="handoverForm.operationOpinion" ref="operationOpinion"
          :labelStyle="{ display: 'block' }">
          <u--textarea v-model="handoverForm.operationOpinion" disabledColor="#E5E5E5" placeholder="请输入内容" border="none"
            :style="{ width: '100%' }"></u--textarea>
        </u-form-item>
      </u--form>
    </view>
    <view class="button">
      <u-button type="primary" :disabled="disabled" shape="circle" text="确认交接" @click="goToBackPage()"></u-button>
    </view>
  </view>
</template>

<script setup>
import { ref, watch } from "vue";
import { updateInspectionTask } from '@/firstPackage/api/inspection.js'
import { onLoad } from '@dcloudio/uni-app'
const handoverForm = ref({
  recipient: "",
  operationOpinion: "",
});
const disabled = ref(true)
const rules = {
  recipient: [
    { required: true, message: "请选择接收人", trigger: "blur" },
  ],
  operationOpinion: [
    { required: true, message: "请输入处理意见", trigger: "blur" },
  ],
};
const userInfo = ref([])
const inspectionTask = ref({})
onLoad((options) => {
  uni.setStorageSync('operationsCurrentIndex', 1)
  inspectionTask.value = JSON.parse(decodeURIComponent(options.data));
  uni.$on('backData', (data) => {
    userInfo.value = data
    handoverForm.value.recipient = data.userName
  });

})
// 监听表单数据变化
watch(
  () => [handoverForm.value.recipient, handoverForm.value.operationOpinion],
  ([recipient, operationOpinion]) => {
    // 当两个字段都不为空时，启用按钮
    disabled.value = !(recipient && operationOpinion);
  }
);
// 选择交接人
const selectrecipient = () => {

  uni.navigateTo({
    url: "/firstPackage/pages/operations/operationDetails/selectRecipient?taskStuts=" + inspectionTask.value.taskStatus + "&taskId=" + userInfo.value.userId,

  });
};
const goToBackPage = () => {
  console.log(inspectionTask.value.taskStatus,inspectionTask.value.taskStatus == 1 ? 2 : 8,)
  const userId = uni.getStorageSync('USER_ID')
  let obj = {
    operationType: inspectionTask.value.taskStatus == 2 ? 2 : 8,
    id: inspectionTask.value.id,
    userId: userId,
    operationOpinion: handoverForm.value.operationOpinion,
    operationUserId: userInfo.value.userId
  }
  updateInspectionTask(obj).then(res => {

    if (res.success) {
      uni.showToast({
        title: '交接成功',
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