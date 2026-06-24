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
              :class="['item', { 'selected': selectedItems[section.id] === item.value }]"
              @click="toggleSelect(section.id, item.value)">
              {{ item.label }}
            </view>
          </view>
        </view>
      </view>
      <view class="form-item" style="padding-bottom: 30rpx;">
        <view class="form-item-title">备注</view>
        <u--textarea v-model="handlingOpinion" placeholder="请输入内容"
          style="border-radius: 24rpx;background-color: #F1F1F1;margin-top: 30rpx;"></u--textarea>
      </view>
      <view class="form-item" style="padding-bottom: 38rpx;">
        <view class="form-item-title">附件</view>

        <view class="upload-file-list">
          <view v-for="(file, index) in fileList" :key="index" class="file-preview"
            @click="imageAction(file.uri ? file.uri : file, index)">
            <image :src="file.uri ? file.uri : file" mode="aspectFill" class="file-image"></image>
          </view>
          <view class="upload-file" @click="chooseImage" v-if="fileList.length < 5">
            <image src="../../../static/upload.png" mode="aspectFill"></image>
          </view>
        </view>
      </view>
      <view class="button">
        <u-button type="primary" :disabled="disabled" shape="circle" text="保存报告" @click="goToBackPage()"></u-button>
      </view>
    </view>
  </view>
</template>

<script setup>
import { onLoad } from '@dcloudio/uni-app'
import { ref, reactive, toRefs, computed, watch, nextTick } from 'vue'
import { findInspectionItemListBySiteId, uploadInspectSiteFile, updateInspectSite, findInspectionSiteListById } from '@/firstPackage/api/inspection.js'


onLoad((options) => {
  const data = JSON.parse(decodeURIComponent(options.siteName));
  inspectionTask.value = data
  const siteName = options.siteName ? decodeURIComponent(data.siteName) : '默认标题';
  uni.setNavigationBarTitle({
    title: siteName
  });
  siteId.value = data.id
  getItemListBySiteId(data.siteId)
  if (data.status == 3) {
    getfindInspectionSiteListById(data.id)
  }

})
const deleteInspectionList = ref([])
const siteId = ref('')
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
// 传递过来的巡检任务
const inspectionTask = ref({})
const sections = ref([])
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
const getItemListBySiteId = (siteId) => {
  findInspectionItemListBySiteId({
    siteId: siteId
  }).then(res => {
    sections.value = res.data
    // 动态生成 selectedItems
    const newSelectedItems = {};
    res.data.forEach(section => {
      newSelectedItems[section.id] = 1; // 默认选中 '未检查'
    });
    selectedItems.value = newSelectedItems;
  })

}

// 存储每个分类的选中项
const selectedItems = ref({});
const handlingOpinion = ref('');
const fileList = ref([]);

const toggleSelect = (label, item) => {
  selectedItems.value[label] = selectedItems.value[label] === item ? '' : item;
};

// -------------------

const imageAction = (filePath, value) => {
  uni.showActionSheet({
    itemList: ['删除', '替换', '放大'],
    success: (res) => {
      const index = res.tapIndex;
      if (index === 0) {
        // 删除
        deleteFile(filePath, value);
      } else if (index === 1) {
        // 替换
        replaceImage(filePath, value);
      } else if (index === 2) {
        // 放大
        uni.previewImage({
          urls: [filePath],
        });

      }
    }
  });
}
const replaceImage = (filePath, index) => {
  uni.showActionSheet({
    itemList: ['拍照', '从相册选择'],
    success: (res) => {
      const tapIndex = res.tapIndex;
      let sourceType = [];
      if (tapIndex === 0) {
        sourceType = ['camera'];
      } else if (tapIndex === 1) {
        sourceType = ['album'];
      }
      uni.chooseImage({
        count: 1,
        sourceType: sourceType,
        sizeType: ['compressed', 'original'],
        success: (chooseRes) => {
          deleteInspectionList.value.push(filePath);
          const tempFilePath = chooseRes.tempFilePaths[0];
          const fileName = `image_${Date.now()}.jpg`;
          let obj = {
            uri: tempFilePath,
            name: fileName,
          };

          adduploadInspectSiteFile([obj], index);
        },
        fail: (err) => {
          console.error('选择图片失败:', err);
          uni.showToast({
            title: '选择图片失败',
            icon: 'none'
          });
        }
      });
    },
    fail: (err) => {
      console.error('操作失败:', err);
    }
  });
};

const adduploadInspectSiteFile = (val, index) => {
  Promise.all(val.map(item => {
    return uploadInspectSiteFile({ id: siteId.value }, item);
  })).then(res => {
    if (index !== undefined) {
      // 替换指定索引的文件
      fileList.value.splice(index, 1, val[0]);
    } else {
      // 添加新文件
      fileList.value = [...fileList.value, ...val];
    }
  }).catch(err => {
    console.error('上传失败:', err);
  });
};
const chooseImage = () => {
  // 计算还可以选择多少张图片
  const maxCount = 5 - fileList.value.length;

  if (maxCount <= 0) {
    uni.showToast({
      title: '最多只能上传5个文件',
      icon: 'none'
    });
    return;
  }

  uni.showActionSheet({
    itemList: ['拍照', '从相册选择'],
    success: (res) => {
      const index = res.tapIndex;
      let sourceType = [];

      if (index === 0) {
        sourceType = ['camera'];
      } else if (index === 1) {
        sourceType = ['album'];
      }

      // 选择图片，可以多选
      uni.chooseImage({
        count: maxCount, // 最多选择maxCount张
        sourceType: sourceType,
        sizeType: ['compressed', 'original'], // 可以指定是原图还是压缩图
        success: (chooseRes) => {
          const obj = chooseRes.tempFilePaths.map((tempFilePath, i) => {
            const fileName = `image_${Date.now()}_${i}.jpg`; // 创建文件名
            return {
              uri: tempFilePath,
              name: fileName,
            };
          });
          adduploadInspectSiteFile(obj)
        },
        fail: (err) => {
          console.error('选择图片失败:', err);
          uni.showToast({
            title: '选择图片失败',
            icon: 'none'
          });
        }
      });
    },
    fail: (err) => {
      console.error('操作失败:', err);
    }
  });
};
const deleteFile = (filePath, index) => {
  uni.showModal({
    title: '提示',
    content: '确认删除当前照片？',
    success: (res) => {
      if (res.confirm) {
        fileList.value.splice(index, 1);
        deleteInspectionList.value.push(filePath)
        uni.showToast({
          title: '已删除',
          icon: 'success'
        });
      }
    }
  });
};


// 保存报告
const goToBackPage = () => {
  const value = uni.getStorageSync('listItem');
  // 处理 deletePaths
  const deletePathsStr = deleteInspectionList.value.join(',');
  let obj = {
    itemStates: JSON.stringify(selectedItems.value),
    remark: handlingOpinion.value,
    id: siteId.value,
    status: 3,
    deletePaths: deletePathsStr,
  }
  if (obj.remark == '') delete obj.remark;
  updateInspectSite(obj).then(res => {
    if (res.success) {
      uni.showToast({
        title: '保存报告成功',
        icon: 'success',
        duration: 2000
      })
    }
    uni.reLaunch({
      url: `/firstPackage/pages/operations/operationDetails/operationDetails?data=${encodeURIComponent(JSON.stringify(value))}`,
      success: () => {
        console.log('跳转成功');
      },
      fail: (err) => {
        console.error('跳转失败:', err);
      }
    });
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