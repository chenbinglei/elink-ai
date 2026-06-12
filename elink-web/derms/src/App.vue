<template>
  <router-view/>
</template>
<script setup>
import { useAppStore, useTagsViewStore } from '@/stores/index';

import {onMounted, reactive} from "vue";

const appStore = useAppStore();
    const tagsViewStore = useTagsViewStore();
const that = reactive({
  userInfo: JSON.parse(localStorage.getItem('USER_INFO'))
});

const updateUserInfo = () => {
  let userId = that.userInfo && that.userInfo?.userId;
  appStore.updateUserInfo(that.userInfo);
  appStore.updateOldUserId(userId);
};

// 重置带有返回按钮的页面
const updateTagsViewBackArray = () => {
  let backButArray = localStorage.getItem("TAGS_VIEW_BACK_ARRAY");
  if (backButArray) tagsViewStore.updateBackButViews(JSON.parse(backButArray));
};

const debounce = (fn, delay) => {
  let timer = null;
  return function () {
    let context = this;
    let args = arguments;
    clearTimeout(timer);
    timer = setTimeout(function () {
      fn.apply(context, args);
    }, delay);
  };
};

const _ResizeObserver = window.ResizeObserver;
window.ResizeObserver = class ResizeObserver extends _ResizeObserver {
  constructor(callback) {
    callback = debounce(callback, 16);
    super(callback);
  }
};

onMounted(() => {
  updateUserInfo();
  updateTagsViewBackArray();
});
</script>
<style lang="scss"></style>
