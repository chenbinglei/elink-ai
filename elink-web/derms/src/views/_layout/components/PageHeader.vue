<template>
  <div class="pageHeader flex-ai-center">
    <SystemLogo ref="SystemLogoRef"></SystemLogo>
    <div class="flex-all content_right">
      <div class="flex-all content_route" v-if="!isFullscreen">
        <RouteMenuListCom></RouteMenuListCom>
      </div>
      <NotificationCenter v-if="!isFullscreen"></NotificationCenter>
      <UserOperationMenu v-if="!isFullscreen"></UserOperationMenu>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue';
import { useMonitorStore } from '@/stores/index';

import SystemLogo from "./pageHeader/SystemLogo.vue";
import RouteMenuListCom from "./pageHeader/RouteMenuListCom.vue";
import UserOperationMenu from "./pageHeader/UserOperationMenu.vue";
const monitorStore = useMonitorStore();
import NotificationCenter from "./pageHeader/NotificationCenter.vue";
const isFullscreen = computed(() => monitorStore.isFullscreen);

</script>

<style scoped lang="scss">
.pageHeader {
  height: 100%;
  padding: 0 16px 32px 16px;
  box-sizing: border-box;

  .content_right {
    height: 100%;
    padding-top: 6px;
    box-sizing: border-box;
    display: flex;
    justify-content: flex-start;
  }

  .content_route {
    height: 100%;
    display: flex;
    flex-flow: row nowrap;
    justify-content: flex-end;
    align-items: flex-end;
    z-index: 9;
  }
}
</style>
