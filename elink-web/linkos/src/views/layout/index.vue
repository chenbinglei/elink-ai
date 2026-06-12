<template>
  <el-container class="app-container_body" v-resize="setContentMainMaxHeight">

    <el-aside :width="isCollapse?'200px':'36px'" class="app-aside">
      <system-logo></system-logo>
      <side-bar-component class="sidebar-container flex-all"></side-bar-component>
    </el-aside>

    <el-main class="app-content_main">
      <el-header class="app-header">
        <collapse-menu></collapse-menu>
        <PageHeader ref="pageHeaderRef"></PageHeader>
      </el-header>
      <el-container class="app-content-right">
        <el-header class="app-tags-view">
          <tags-view ref="tagsViewRef" @clickEvent="clickEvent"></tags-view>
        </el-header>
        <el-main class="app-main clearPadding" ref="contentMainRef">
          <AppMain ref="appMainRef"></AppMain>
        </el-main>
      </el-container>
    </el-main>

  </el-container>
</template>

<script>
import { useAppStore, useSidebarStore } from '@/stores/index';

import {filterTreeArray} from "@/utils";
import {useRouter, useRoute} from "vue-router";
import {computed, onMounted, reactive, toRefs, ref} from "vue";
import {findControlPermissionListByUserId} from "@/api/tenantManagement/tenantTabulation";
import {SystemLogo, SideBarComponent, CollapseMenu, AppMain, PageHeader, TagsView} from "@/views/layout/components";

export default {
  name: "LayoutPage",
  components: {SystemLogo, SideBarComponent, CollapseMenu, AppMain, PageHeader, TagsView},
  setup() {

    const appStore = useAppStore();
    const sidebarStore = useSidebarStore();
    const route = useRoute();
    const vueRouter = useRouter();

    const userInfo = computed(() => {
      return appStore.userInfo;
    });

    const isCollapse = computed(() => {
      return sidebarStore.isCollapse
    });

    const routes = computed(() => {
      // console.log(JSON.parse(localStorage.getItem("SIDEBAR")))
      return JSON.parse(localStorage.getItem("SIDEBAR"));
    });

    const that = reactive({});

    // 根据用户id查询控件权限列表
    const getControlPermissionList = () => {
      findControlPermissionListByUserId({timer: new Date()}).then(res => {
        appStore.updatePermissionList(res.data);
      })
    }

    const clickEvent = (data) => {
      // 点击回到第一个模块下第一个页面
      if (data.type === "clickHomeBut") {
        jumpIndex();
      }
    };

    const jumpIndex = () => {
      let routesArray = routes.value;
      // 过滤掉隐藏的路由， 默认不进入隐藏的路由
      if (!that.allShowRouterlist) that.allShowRouterlist = filterTreeArray(routesArray);
      if (that.allShowRouterlist && that.allShowRouterlist.length) getItemChildrenName(that.allShowRouterlist[0]);
    };

    // 查看当前路由下的是否有子元素
    const getItemChildrenName = (item, lastTimeName = "") => {
      if (!item.children || !item.children.length) {
        // console.log(!item.hidden ? item.name : lastTimeName);
        vueRouter.push({name: !item.hidden ? item.name : lastTimeName});
      } else {
        lastTimeName = item.name; // 记录父级的权限页面
        getItemChildrenName(item.children[0], lastTimeName);
      }
    };

    // 获取右侧最大高度
    const contentMainRef = ref(null);
    const setContentMainMaxHeight = ()=>{
      let contentMainMaxHeight = contentMainRef.value.$el.offsetHeight;
      appStore.updateContentMainMaxHeight(contentMainMaxHeight - 24);
    }

    onMounted(() => {
      getControlPermissionList();
      // 首次进入系统，进入第一个模块下的第一个页面
      if (route.path === "/") jumpIndex();
    });

    return {...toRefs(that), route, userInfo, isCollapse, clickEvent, getControlPermissionList, setContentMainMaxHeight,contentMainRef};
  }
}
</script>

<style lang="scss" scoped>
.app-container_body {
  height: 100%;

  .app-aside {
    display: flex;
    flex-direction: column;
    background-color: #FFFFFF;
    box-sizing: border-box;
    transition: all 0.28s;
    overflow: initial;
  }

  .app-content_main {
    display: flex;
    flex-direction: column;
    --el-main-padding: 0;

    .app-header {
      display: flex;
      --el-header-padding: 0;
      --el-header-height: 48px;
      background-color: #FFFFFF;
      border-bottom: 1px solid #F2F2F2;
    }

    .app-content-right {
      flex: 1;
      overflow: auto;

      .app-tags-view {
        --el-header-height: 32px;
        background-color: #FFFFFF;
        --el-header-padding: 0 12px 0 0;
      }

      .app-main {
        background-color: #F1F2F6;
        --el-main-padding: 12px 12px 12px 12px;
      }
    }
  }
}
</style>
