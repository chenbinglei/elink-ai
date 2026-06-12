<template>
  <div class="appMain" :style="{ maxHeight: contentMainMaxHeight + 'px'}">
    <router-view v-slot="{ Component, route }">
      <transition name="fade-route" mode="out-in">
        <keep-alive :include="cachedViews">
          <component :is="Component" :key="route.fullPath" :contentMaxHeight="contentMainMaxHeight" />
        </keep-alive>
      </transition>
    </router-view>
  </div>
</template>

<script>
import { useAppStore, useTagsViewStore } from '@/stores/index';

import {computed, reactive, toRefs} from "vue";

export default {
  name: "AppMain",
  setup() {
    const appStore = useAppStore();
    const tagsViewStore = useTagsViewStore();

    const cachedViews = computed(() => {
      return tagsViewStore.cachedViews;
    });

    const contentMainMaxHeight = computed(() => {
      return appStore.contentMainMaxHeight;
    });

    const that = reactive({});

    return {...toRefs(that), cachedViews, contentMainMaxHeight};
  }
};
</script>
<style lang="scss" scoped>
.appMain {
  width: 100%;
  display: flex;
  padding: 12px 14px;
  box-sizing: border-box;
  background-color: #F8F8F8;
}

/* 路由切换淡入淡出，消除黑/白闪 */
.fade-route-enter-active,
.fade-route-leave-active {
  transition: opacity 0.2s ease;
}
.fade-route-enter-from,
.fade-route-leave-to {
  opacity: 0;
}
.fade-route-enter-to,
.fade-route-leave-from {
  opacity: 1;
}
</style>

