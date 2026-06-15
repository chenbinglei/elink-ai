<template>
  <div class="routeMenuListCom flex-jc-ai-center">
    <div class="content_route_list" v-if="routes && routes.length > 1">
      <template v-for="(item, index) in routes" :key="index">
        <!-- 监控菜单打开站点列表下拉 -->
        <el-popover
          v-if="item.path === '/centralMonitoring/centralMonitoring'"
          ref="popoverRef"
          :popper-style="{ padding: 0, width: 'fit-content' }"
          popper-class="plant-list-popper z-1000000!"
          placement="bottom"
          teleported="true"
          trigger="click"
        >
          <!-- 场站列表 -->
          <plant-list @station-change="onStationChange" />
          <template #reference>
            <div
              :class="{ active_route_class: item.path === route.path }"
              class="content_route_li"
            >
              <span class="route_name">{{ item.meta.title }}</span>
            </div>
          </template>
        </el-popover>
        <!-- 其他菜单直接打开标签页 -->
        <div
          v-else
          :class="{ active_route_class: item.path === route.path }"
          class="content_route_li"
          @mouseover="hoverIndex = index"
          @mouseleave="clearCascader(index)"
        >
          <span class="route_name">{{ item.meta.title }}</span>
          <template v-if="item.children?.length">
            <div class="triangle" v-if="hoverIndex == index"></div>
            <div class="panel_list" v-if="hoverIndex == index">
              <el-cascader-panel
                ref="tree"
                style="width: fit-content; height: 150px"
                :options="item.children"
                :props="{ value: 'path', label: 'title' }"
                @change="clickSwitchRoute"
                :key="index + 'tree'"
              />
              <plant-list
                @station-change="onStationChange"
                v-if="
                  activeCascaderPathName ==
                    '/centralMonitoring/centralMonitoring' && hoverIndex == 0
                "
              />
            </div>
          </template>
        </div>
      </template>
    </div>
  </div>
</template>
<script lang="ts">
import { useRoute, useRouter } from "vue-router";
import { ref, computed, reactive, defineComponent, toRefs, unref } from "vue";
import { useMonitorStore } from '@/stores/index';

import plantList from "./plantList.vue";

export default defineComponent({
  name: "RouteMenuListCom",
  components: {
    plantList,
  },
  setup() {
    const route = useRoute();
    const vueRouter = useRouter();
    const monitorStore = useMonitorStore();
    const routes = computed(() => {
      console.log(route);
      console.log(JSON.parse(localStorage.getItem("SIDEBAR")));
      return JSON.parse(localStorage.getItem("SIDEBAR"));
    });
    const onStationChange = (query) => {
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
    const activeCascaderPathName = ref();
    const hoverIndex = ref(9);
    const that = reactive({});
    const clearCascader = (index) => {
      hoverIndex.value = 9;
      activeCascaderPathName.value = "";
    };
    const clickSwitchRoute = (data) => {
      let path = data[data.length - 1];
      activeCascaderPathName.value = path;
      if (path == "/centralMonitoring/centralMonitoring") return;
      if (path !== route.path || path != "/centralMonitoring/centralMonitoring")
        vueRouter.push({ path });
    };

    return {
      ...toRefs(that),
      routes,
      route,
      clickSwitchRoute,
      onStationChange,
      activeCascaderPathName,
      hoverIndex,
      clearCascader,
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
  justify-content: flex-start;
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