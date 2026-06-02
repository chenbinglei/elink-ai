<template>
  <el-container class="app-container_body">
    <el-aside class="app-aside" width="200px">
      <system-logo></system-logo>
      <side-bar-component class="sidebar-container"></side-bar-component>
    </el-aside>
    <el-main class="app-main">
      <AppMain ref="appMainRef"></AppMain>
    </el-main>
  </el-container>
</template>

<script setup>
import {useStore} from "vuex";
import {filterTreeArray} from "@/utils";
import {useRouter, useRoute} from "vue-router";
import {computed, onMounted, reactive} from "vue";
import {SystemLogo, SideBarComponent, AppMain} from "@/views/layout/components";

const store = useStore();
const route = useRoute();
const vueRouter = useRouter();

const routes = computed(() => {
  // console.log(JSON.parse(localStorage.getItem("SIDEBAR")))
  return JSON.parse(localStorage.getItem("SIDEBAR"));
});

const that = reactive({});

// 点击回到第一个模块下第一个页面
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

onMounted(() => {
  // 首次进入系统，进入第一个模块下的第一个页面
  if (route.path === "/") jumpIndex();
});
</script>

<style lang="scss" scoped>
.app-aside {
  display: flex;
  flex-direction: column;
  background-color: #FFFFFF;

  .sidebar-container{
    flex: 1;
    height: 2px;
    box-shadow: 0 1px 4px 0 rgba(0, 0, 0, 0.2);
  }
}

.app-main {
  background: #F1F3FA;
  --el-main-padding: 12px;
}
</style>
