<template>
  <el-container class="app-container_body">
    <el-header class="app-header">
      <PageHeader ref="pageHeaderRef"></PageHeader>
    </el-header>
    <el-main class="app-main">
      <AppMain ref="appMainRef"></AppMain>
    </el-main>
    <el-footer class="app-footer">
      <TagsView ref="tagsViewRef"></TagsView>
    </el-footer>
  </el-container>
</template>

<script setup>
import { filterTreeArray } from "@/utils";
import { useRouter, useRoute } from "vue-router";
import { computed, onMounted, reactive } from "vue";
import { AppMain, PageHeader, TagsView } from "./components";

const route = useRoute();
const vueRouter = useRouter();

const routes = computed(() => {
  return JSON.parse(localStorage.getItem("SIDEBAR"));
});

const that = reactive({});

const jumpIndex = () => {
  let routesArray = routes.value;
  // 过滤掉隐藏的路由， 默认不进入隐藏的路由
  if (!that.allShowRouterlist)
    that.allShowRouterlist = filterTreeArray(routesArray);
  if (that.allShowRouterlist && that.allShowRouterlist.length)
    getItemChildrenName(that.allShowRouterlist[0]);
};

// 查看当前路由下的是否有子元素
const getItemChildrenName = (item, lastTimeName = "") => {
  if (!item.children || !item.children.length) {
    vueRouter.push({ name: !item.hidden ? item.name : lastTimeName });
  } else {
    lastTimeName = item.name; // 记录父级的权限页面
    let active_item = JSON.parse(JSON.stringify(item));
    delete active_item.children;
    getItemChildrenName(
      item.isLayout ? item.children[0] : active_item,
      lastTimeName
    );
  }
};

onMounted(() => {
  // 首次进入系统，进入第一个模块下的第一个页面
  if (route.path === "/") jumpIndex();
});
</script>

<style lang="scss" scoped>
.app-container_body {
  position: relative;
  height: 100vh;

  .app-header {
    --el-header-padding: 0;
    --el-header-height: 80px;
    background-size: 100% 100%;
    background-repeat: no-repeat;
    background-image: url("@/assets/image/header_bg.webp");
  }

  .app-main {
    overflow: initial;
    --el-main-padding:0px 0 14px 0;
    width: 100%;
    overflow-x: hidden;
    padding-bottom: 50px;
    background: url(/src/assets/image/station-details/sta-detail-bg.png)
      no-repeat;
       background-size: 100% 100%;
  }

  .app-footer {
    --el-footer-padding: 0;
    --el-footer-height: 46px;
    background-size: 100%;
    background-repeat: no-repeat;
    background: linear-gradient(to bottom, #004e77 0%, #000e17 100%);
    position: fixed;
    bottom: 0;
    width: 100%;
  }
}
</style>
