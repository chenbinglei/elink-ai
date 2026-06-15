<template>
  <div class="sideBarComponent scrollbarStyle">
    <el-menu :active-text-color="activeTextColor" :collapse="!isCollapse" :default-active="$route.name" :text-color="textColor" mode="vertical" unique-opened>
      <sidebar-item :routes="routes"></sidebar-item>
    </el-menu>
  </div>
</template>

<script lang="ts">
import { useSidebarStore } from '@/stores/index';

import SidebarItem from "./SidebarItem";
import {computed, onMounted, reactive, toRefs} from "vue";

export default {
  name: "SideBarComponent",
  components: {SidebarItem},
  setup() {

    const sidebarStore = useSidebarStore();
    const routes = computed(() => {
      return JSON.parse(localStorage.getItem("SIDEBAR"));
    });

    const isCollapse = computed(() => {
      return sidebarStore.isCollapse
    });

    const that = reactive({
      activeTextColor: "#242424",
      textColor: "rgba(0,0,0,.7)",
    });

    onMounted(() => {});

    return {...toRefs(that), routes, isCollapse};
  }
}
</script>

<style lang="scss" scoped>
.sideBarComponent {
  width: 100%;
  height: 100%;
  overflow-y: auto;
  transition: width 0.28s;
  border-right: 1px solid #F2F2F2;
  box-sizing: border-box;

  :deep(.el-menu) {
    width: 100%;
    border: none;

    .router-link-active {
      width: 100%;
      outline: none;
      color: inherit;
      text-decoration: none;
      display: inline-block;
    }

    .is-active {
      .el-sub-menu__title {
        --el-menu-text-color: #242424;
      }
    }

    .el-sub-menu {
      --el-menu-item-height: 44px;

      .el-menu-item {
        --el-menu-sub-item-height: 40px;
      }
    }
  }

  :deep(.el-menu--collapse) {

    .el-menu-item {
      padding: 0 !important;

      .el-tooltip__trigger {
        padding: 0 !important;
        text-align: center;
        justify-content: center;
      }
    }

    .el-sub-menu {
      .el-sub-menu__title {
        padding: 0 !important;
        justify-content: center;

        .el-icon, span {
          display: none;
        }
      }
    }

    .iconfont {
      margin-right: 0 !important;
    }
  }
}
</style>
