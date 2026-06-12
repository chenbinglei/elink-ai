<template>
  <div class="routeMenuListCom flex-jc-ai-center">
    <div class="content_route_list" v-if="routes && routes.length > 1">
      <template v-for="(item, index) in routes" :key="index">
        <!-- 监控菜单打开站点列表下拉 -->
        <el-popover v-if="item.path === '/centralMonitoring/centralMonitoring'" ref="popoverRef"
          :popper-style="{ padding: 0, width: 'fit-content' }" popper-class="plant-list-popper z-1000000!"
          placement="bottom" teleported="true" trigger="click">
          <!-- 场站列表 -->
          <plant-list @station-change="onStationChange" />
          <template #reference>
            <div :class="{ active_route_class: item.path === route.path }" class="content_route_li">
              <span class="route_name">{{ item.meta.title }}</span>
            </div>
          </template>
        </el-popover>
        <!-- 其他菜单直接打开标签页 -->
        <div v-else :class="{ active_route_class: item.path === route.path }" class="content_route_li"
          @mouseover="hoverIndex = index" @mouseleave="clearCascader(index)" @click="handleItemClick(item)">
          <span class="route_name">{{ item.meta.title }}</span>
          <template v-if="item.children?.length">
            <div class="triangle" v-if="hoverIndex == index"></div>
            <div class="panel_list" v-if="hoverIndex == index">
              <!-- <div> -->
              <el-cascader-panel v-if="item.name !== 'operationanalys'" ref="tree"
                style="width: fit-content; height: 150px" :options="item.children"
                :props="{ value: 'path', label: 'title' }" @change="clickSwitchRoute" :key="index + 'tree'" />
              <div v-else style="position: relative; z-index: 1000">
                <!-- 只展示父节点下的子节点，不向下延伸 -->
                <el-cascader-panel ref="tree" style="width: fit-content; height: 150px"
                  :options="filteredChildren(item.children)" :props="{ value: 'path', label: 'title' }"
                  @change="clickSwitchOperation" :key="index + 'tree'" teleported="true" />
                <!-- </div> -->
              </div>
              <plant-list @station-change="onStationChange" v-if="
                activeCascaderPathName ==
                '/centralMonitoring/centralMonitoring' && item.children[1].path == '/centralMonitoring/centralMonitoring'
              " />

              <operationanalys :analysList="analysList" v-if="
                activeCascaderPathName ==
                '/operationManagement/ChargingStationOperation'
              " :routeMenuList="routeMenuList"></operationanalys>
              <operationanalys :analysList="analysList" v-if="
                activeCascaderPathName ==
                '/operationManagement/PhotovoltaicOperation'
              " :routeMenuList="routeMenuList"></operationanalys>
              <operationanalys :analysList="analysList" v-if="
                activeCascaderPathName ==
                '/operationManagement/EnergyStorageOperation'
              " :routeMenuList="routeMenuList"></operationanalys>

            </div>
          </template>
        </div>
      </template>
    </div>
  </div>
</template>
<script>
import { getLeftTreeDataFun } from "@/utils";
import { useRoute, useRouter } from "vue-router";
import { ref, computed, reactive, defineComponent, toRefs, unref } from "vue";
import { useMonitorStore } from '@/stores/index';

import plantList from "./plantList.vue";
import operationanalys from "./operationanalys.vue";

export default defineComponent({
  name: "RouteMenuListCom",
  components: {
    plantList,
    operationanalys,
  },
  setup () {
    const route = useRoute();
    const vueRouter = useRouter();
    const monitorStore = useMonitorStore();
    const routes = computed(() => {
      return JSON.parse(localStorage.getItem("SIDEBAR"));
    });

    const onStationChange = (query) => {
      localStorage.setItem("StationRoulist", query.siteSetUpDto.readwriteObject);
      // 触发事件，传递选中的场站ID subTitle
      const { id, siteName } = query;
      vueRouter.push({
        path: "/centralMonitoring/centralMonitoring",
        query: {
          siteId: id,
          siteName,
        },
      });
      monitorStore.updateSiteInfo({
        siteId: id,
        siteName: siteName,
      });
    };
    const handleItemClick = (item) => {
      if (item.children?.length) {
        // 有子节点，不跳转，只展示下拉面板
        return;
      }
      // 目前只调试首页页面
      const path = item.path;
      vueRouter.push({ path });
    };
    const activeCascaderPathName = ref();
    const hoverIndex = ref(9);
    const that = reactive({
      routeMenuList: [],
      analysListSum: [],
      analysList: [],
    });
    const clearCascader = (index) => {
      hoverIndex.value = 9;
      activeCascaderPathName.value = "";
    };
    const clickSwitchOperation = (data) => {
      const matchedItem = that.analysListSum.filter(
        (item) => item.path == data
      )[0];
      that.analysList = matchedItem ? matchedItem.children : [];
      activeCascaderPathName.value = data;
      findRouteMenuListFun(data);
    };
    // 获取左侧树结构的数据
    const findRouteMenuListFun = (data) => {
      that.routeMenuList = getLeftTreeDataFun(data[0], 0);
      console.log(that.routeMenuList);
    };
    const clickSwitchRoute = (data) => {
      let path = data[data.length - 1];
      activeCascaderPathName.value = path;
      if (path == "/centralMonitoring/centralMonitoring") return;
      if (path !== route.path || path != "/centralMonitoring/centralMonitoring")
        vueRouter.push({ path });
    };

    const filteredChildren = (children) => {
      that.analysListSum = children;
      return children.map((child) => ({
        ...child,
        children: [], // 清空子节点，防止继续展开
      }));
    };

    return {
      ...toRefs(that),
      routes,
      route,
      filteredChildren,
      clickSwitchRoute,
      onStationChange,
      activeCascaderPathName,
      hoverIndex,
      clearCascader,
      clickSwitchOperation,
      findRouteMenuListFun,
      handleItemClick,
      
    };
  },
});
</script>
<style lang="scss" scoped>
.plant-list-popper {
  --el-bg-color-overlay: rgba(0, 84, 128, 0.9);
  --el-border-color-light: rgba(3, 165, 255, 0.25);
}

.routeMenuListCom {
  width: 100%;
  box-sizing: border-box;
  flex-flow: row nowrap;
  align-items: flex-end;
  // justify-content:center;
  padding: 0 52px 0 173px;
  transform: translateY(8px);

  .content_route_list {
    display: flex;
    align-items: center;
    justify-content: center;
    height: 36px;
    background: rgba(0, 0, 0, 0.3);
    border: 1px solid #0079cb;
    border-radius: 18px 18px 18px 18px;

    .content_route_li {
      position: relative;
      display: flex;
      align-items: center;
      justify-content: center;
      height: 100%;
      width: fit-content;
      border-radius: 4px;
      padding: 7px 33px 8px 35px;
      box-sizing: border-box;
      cursor: pointer;

      .route_name {
        font-family: Microsoft YaHei, Microsoft YaHei;
        font-weight: bold;
        font-size: 16px;
        color: #ffffff;
        text-align: left;
        font-style: normal;
        text-transform: none;
        white-space: nowrap;
        overflow: hidden;
      }

      .panel_list {
        display: flex;
        position: absolute;

        top: 38px;
        color: #fff;
        background: rgba(0, 84, 128, 0.9);
        box-shadow: inset 0px 0px 6px 1px #3baaf5;
        border-radius: 3px 3px 3px 3px;
        border: 1px solid rgba(3, 165, 255, 0.25);
      }

      .triangle {
        position: absolute;
        top: 20px;
        border: 10px solid transparent;
        border-bottom-color: rgba(0, 84, 128, 0.9);
      }
    }

    .active_route_class {
      background: linear-gradient(180deg, #003447 0%, #0088cf 100%);
      box-shadow: inset 0px -3px 6px 1px #00ccff;
      border-radius: 18px 18px 18px 18px;
    }
  }
}
</style>