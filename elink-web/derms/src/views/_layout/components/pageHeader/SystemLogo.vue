<template>
  <div :title="platName" class="systemLogo">
    <img v-if="showSysLogo" alt="" class="home_logo" src="/logo.png" />
    <div v-if="isCollapse" class="platName family-zihun">{{ systemDisplayName }}</div>
    <!-- <div class="subPlatName">{{ subPlatName }}</div> -->
    <!-- 使用css+div实现: 两个三角 一个向上一个向下 -->
    <!-- <div class="toggle-icon flex flex-col justify-center items-center cursor-pointer">
      <div class="triangle-up"></div>
      <div class="triangle-down"></div>
    </div> -->
  </div>
</template>

<script lang="ts">
import { useSidebarStore } from '@/stores/index';

import { computed, reactive, toRefs, defineComponent } from "vue";

export default defineComponent({
  name: "SystemLogo",
  setup() {

    const sidebarStore = useSidebarStore();
    const isCollapse = computed(() => {
      return sidebarStore.isCollapse;
    });

    const that = reactive({
    });

    const toggleSideBar = () => {
      sidebarStore.toggleSideBar();
    };

    //
    const showSysLogo = computed(() => {
      return window.IEMSConfig?.showSysLogo ?? true;
    });
    const systemDisplayName = computed(() => {
      return window.IEMSConfig?.systemName ?? '分布式能源聚合管理平台';
    });

    return { ...toRefs(that), isCollapse, toggleSideBar, showSysLogo, systemDisplayName };
  }
});
</script>

<style lang="scss" scoped>
.systemLogo {
  height: 100%;
  display: flex;
  flex-flow: row nowrap;
  justify-content: flex-start;
  align-items: flex-end;
  transition: all 0.28s;

  .home_logo {
    height: 48px;
    margin-right: 12px;
    transition: all 0.28s;
    transform: translateY(9px);
  }

  .platName {
    flex: 1;
    color: #FFFFFF;
    white-space: nowrap;
    font-weight: 400;
    font-size: 30px;
    text-align: left;
    font-style: normal;
    text-transform: none;
    -webkit-text-stroke: 1px rgba(0, 0, 0, 0);
  }

  .subPlatName {
    flex: 1;
    color: #FFFFFF;
    white-space: nowrap;
    font-family: zihun35hao-jindianyahei, zihun35hao-jindianyahei, sans-serif;
    font-weight: 400;
    font-size: 16px;
    text-align: left;
    font-style: normal;
    text-transform: none;
    margin-left: 18px;
  }

  .toggle-icon {
    width: 20px;
    height: 20px;
    display: flex;
    flex-flow: column nowrap;
    justify-content: center;
    align-items: center;
    cursor: pointer;

    .triangle-up {
      width: 0;
      height: 0;
      border-left: 5px solid transparent;
      border-right: 5px solid transparent;
      border-bottom: 5px solid #fff;
      margin-bottom: 2px;
    }

    .triangle-down {
      width: 0;
      height: 0;
      border-left: 5px solid transparent;
      border-right: 5px solid transparent;
      border-top: 5px solid #fff;
    }
  }
}
</style>
