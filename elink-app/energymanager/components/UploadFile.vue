<template>
  <view class="upload-container">
    <u-upload :url="uploadUrl" :max-size="maxSize" :max-count="maxCount" @success="handleSuccess" @fail="handleFail"
      @delete="handleDelete" @after-read="handleAfterRead" :fileList="fileList"></u-upload>
  </view>
</template>

<script setup>

import { defineProps,  } from 'vue';

const props = defineProps({
  uploadUrl: {
    type: String,
    required: true,
    default: ''
  },
  headers: {
    type: Object,
    default: () => ({})
  },
  formData: {
    type: Object,
    default: () => ({})
  },
  maxSize: {
    type: Number,
    default: 10 * 1024 * 1024 // 默认 10MB
  },
  maxCount: {
    type: Number,
    default: 1
  }, 
  fileList: {
    type: Array,
    default: () => []
  }
});

const emit = defineEmits(['success', 'fail', 'delete', 'after-read']);

const handleSuccess = (res) => {
  emit('success', res);
};
const handleAfterRead = (file) => {
  emit('after-read', file);
};

const handleFail = (err) => {
  emit('fail', err);
};

const handleDelete = (index, file) => {
  emit('delete', index, file);
};
</script>

<style scoped>
.upload-container {
  padding: 20rpx;
}
</style>