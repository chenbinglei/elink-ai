<template>
  <view class="alarm-search">
    <view class="alarm-search-type" @click="showTypePicker" v-if="isHowSelect">
      <view class="alarm-search-type-text"> {{ selectedType }}</view>
      <u-icon name="arrow-down-fill" color="#000000" size="12"></u-icon>
      <view class="alarm-search-type-list" v-if="showDropdown">
        <view class="alarm-search-type-list-item" v-for="(item, index) in typeList" :key="index" @click.stop="selectType(item)"
          :class="{'active': item.name == selectedType}">
          {{ item.name }}
        </view>
      </view>
    </view>

    <view class="alarm-search-input" :class="{ active: !isHowSelect }">
      <u--input :placeholder="placeholder" border="surround" style="background-color: #ffffff;border-radius: 100rpx;" v-model="searchValue" @change="change"
        prefixIcon="search" prefixIconStyle="font-size: 22px;color: #909399">
        <template #suffix v-if="isIconImage">
          <image class="image" src="/static/image/alarm/selecttp.png" mode="" @click="goSelectSite()">
          </image>
        </template>
      </u--input>
    </view>
    <image class="alarm-search-more"  v-if="props.isMore" src="/static/image/alarm/moreselect.png" @click="show = true"></image>
  </view>
</template>

<script setup>
import { ref, onBeforeUnmount, watch, onMounted } from 'vue'
const emit = defineEmits(['change', 'selectTypeValue', 'goSelectSite'])
const props = defineProps({
  typeList: {
    type: Array,
    default: () => []
  },
  placeholder: {
    type: String,
    default: '请输入'
  },
  isHowSelect: {
    type: Boolean,
    default: false
  },
  isIconImage: {
    type: Boolean,
    default: false
  },
  isMore: {
    type: Boolean,
    default: true
  }

})
let timer = null
const showDropdown = ref(false)
const selectedType = ref()
watch(() => props.isMore, (newVal, oldVal) => { 
  console.log('newVal', newVal)
})

// 输入变化时触发
const change = (val) => {
  // 清除之前的定时器
  if (timer) {
    clearTimeout(timer)
  }
  // 设置新的定时器（1秒后触发）
  timer = setTimeout(() => {
    emit('change', val)
  }, 1000)
}

const goSelectSite = (item) => {
  emit('goSelectSite', item)

}
const selectType = (item) => {
  showDropdown.value = false
  selectedType.value = item.name
  emit('selectTypeValue', item.value)
}
const showTypePicker = () => {
  showDropdown.value = !showDropdown.value
}
onMounted(() => {
  if (props.typeList.length > 0) {
    selectedType.value = props.typeList[0].name
  }
})
// 组件卸载前清除定时器
onBeforeUnmount(() => {
  if (timer) {
    clearTimeout(timer)
  }
})
</script>

<style lang="scss" scoped>
.alarm-search {
  height: 10%;
  width: 100%;
  display: flex;
  align-items: center;
  // justify-content: space-between;
  justify-content: space-around;

  .alarm-search-type {
    display: flex;
    align-items: center;
    position: relative;

    .alarm-search-type-text {
      margin-right: 14rpx;
      color: #000000;
      font-weight: 500;
      white-space: nowrap;
      /* 禁止换行 */
    }

    .alarm-search-type-list {
      position: absolute;
      background: rgba(255, 255, 255, 1);
      box-shadow: 0rpx 0rpx 17rpx 0rpx rgba(0, 0, 0, 0.25);
      border-radius: 30rpx;
      padding: 20rpx 30rpx;
      top: 74rpx;
      left: 5rpx;
      z-index: 1;
      /* 确保在最上层 */
    }

    .alarm-search-type-list-item {
      white-space: nowrap;
      /* 禁止换行 */
      padding: 20rpx 30rpx;
    }
  }

  .image {
    width: 38rpx;
    height: 38rpx;
  }

  .alarm-search-more {
    width: 48rpx;
    height: 48rpx;
  }

  .alarm-search-input {
    width: 70%;
  }

  .active {
    width: 88% !important;
  }
}
.active {
  color: rgba(24, 119, 255, 1);
}
</style>