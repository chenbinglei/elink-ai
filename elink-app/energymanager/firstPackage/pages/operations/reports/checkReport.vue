<template>
  <view class="content">
    <view class="form-content">
      <view class="form-item">
        <view class="form-item-title">巡检项目</view>
        <view v-for="(section, index) in sections" :key="index"
          :class="{ 'form-item-content': true, line: index !== sections?.length - 1 }">
          <view class="label">{{ section.name }}</view>
          <view class="list">
            <view v-for="item in siteStatus" :key="item.value"
              :class="['item', { 'selected': selectedItems[section.id] === item.value }]">
              {{ item.label }}
            </view>
          </view>
        </view>
      </view>
      <view class="form-item" style="padding-bottom: 30rpx;">
        <view class="form-item-title">备注</view>
        <u--textarea v-model="handlingOpinion" placeholder="请输入内容" disabled
          style="border-radius: 24rpx;background-color: #F1F1F1;margin-top: 30rpx;"></u--textarea>
      </view>
      <view class="form-item" style="padding-bottom: 38rpx;">
        <view class="form-item-title">附件</view>

        <view class="upload-file-list">
          <view v-for="(file, index) in fileList" :key="index" class="file-preview" @click="imageAction(file)">
            <image :src="file" mode="aspectFill" class="file-image"></image>
          </view>

        </view>
      </view>

    </view>
  </view>
</template>
<script setup>
import { onLoad } from '@dcloudio/uni-app'
import { ref, reactive, toRefs, computed, watch, nextTick } from 'vue'
import { findInspectionSiteListById } from '@/firstPackage/api/inspection.js'
onLoad((options) => {
  const data = JSON.parse(decodeURIComponent(options.siteName));
  inspectionTask.value = data
  const siteName = options.siteName ? decodeURIComponent(data.siteName) : '默认标题';
  uni.setNavigationBarTitle({
    title: siteName
  });
  siteId.value = data.id
  getfindInspectionSiteListById(data.id)

})
const siteId = ref('')
// 传递过来的巡检任务
const inspectionTask = ref({})
const sections = ref([])
const selectedItems = ref({});
const handlingOpinion = ref('');
const fileList = ref([]);

const siteStatus = ref([{
  label: '未检查',
  value: 1
}, {
  label: '正常',
  value: 2
}, {
  label: '异常',
  value: 3

}])
const imageAction = (filePath) => {
  uni.showActionSheet({
    itemList: ['放大'],
    success: (res) => {
      // 放大
      uni.previewImage({
        urls: [filePath],
      });
    }
  });
}
const getfindInspectionSiteListById = (val) => {
  findInspectionSiteListById({ inspectionSiteId: val }).then(res => {
    if (res.success) {
      handlingOpinion.value = res.data.remark ? res.data.remark : ''
      fileList.value = res.data.annexPath ? res.data.annexPath.split(',') : [];

      sections.value = res.data.itemStateList.map(item => {
        return {
          name: item.itemName,
          id: item.itemId,
        }
      })
      const newSelectedItems = {};
      selectedItems.value = res.data.itemStateList.forEach(item => {
        newSelectedItems[item.itemId] = item.itemState

      },)
      selectedItems.value = newSelectedItems;

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
  position: relative;
  background-color: #F5F5F5;

  .form-content {
    width: 92%;
    margin: 0 auto;
    height: 90%;
    overflow: auto;
  }

  .form-item {
    background-color: #fff;
    border-radius: 28rpx;
    padding: 33rpx 28rpx 0 28rpx;
    margin: 19rpx 0;

    .form-item-title {
      font-weight: 600;
      font-size: 28rpx;
      color: #1B1B1B;
    }

    .line {
      border-bottom: 1rpx solid #CCCCCC;
    }

    .form-item-content {
      width: 100%;
      padding-bottom: 33rpx;
    }

    .label {
      font-weight: 500;
      font-size: 26rpx;
      color: #6C6C6C;
      margin: 32rpx 0 13rpx 0;
    }

    .list {
      display: flex;
      justify-content: space-between;
      font-weight: 500;
      font-size: 22rpx;
      color: #1A1A1A;
      margin-top: 13rpx;

      .item {
        background: #F3F3F3;
        border-radius: 26rpx;
        padding: 13rpx 0;
        width: 30%;
        text-align: center;
      }

      .selected {
        background: #E7EEFC;
      }


    }
  }

  .upload-file-list {
    display: flex;
    margin-top: 24rpx;
    gap: 20rpx;

    .file-preview {
      display: flex;
    }

    .file-image {
      width: 100rpx;
      height: 100rpx;
    }
  }

  .upload-file {
    width: 100rpx;
    height: 100rpx;
    background: #F1F1F1;
    border-radius: 12rpx;
    border: 1rpx solid #CBCBCB;
    display: flex;
    justify-content: center;
    align-items: center;
  }

  .upload-file image {
    width: 60%;
    height: 60%;
  }

  .button {
    margin: 0 auto;
    width: 60%;
    position: fixed;
    bottom: 30rpx;
    left: 50%;
    transform: translateX(-50%);
  }
}
</style>