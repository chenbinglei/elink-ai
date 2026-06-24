<template>
  <view class="screen-containers">
    <view class="screen-left">
      <!-- <lineArea style="width: 100%; height: 100%;" :lendList="lendList" :selectType="selectType" :selectDeviceType="selectDeviceType"></lineArea> -->
    </view>
    <view class="screen-right">

    </view>
  </view>
</template>


<script setup name="setup">
import { ref } from 'vue'
import lineArea from '../../../../pages/homePage/components/lineArea.vue'
import { onLoad, onShow, onPullDownRefresh } from '@dcloudio/uni-app'
const lendList = ref([])
const selectType = ref(null)
const selectDeviceType = ref(null)
onLoad((options) => {
  const option = JSON.parse(decodeURIComponent(options))
  console.log(option,'jieshoudao ')
  // lendList.value = option.lendList
  // selectType.value = option.selectType
  // selectDeviceType.value = option.selectDeviceType
})

</script>

<style scoped lang="less">
.screen-containers {
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: space-between;
}
.screen-left {
  width:80%;
  height: 100%;
}
.screen-right {
  width: 10%;
  height: 100%;
}
</style>