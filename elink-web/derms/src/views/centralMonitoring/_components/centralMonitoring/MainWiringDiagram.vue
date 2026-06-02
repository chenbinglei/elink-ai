<template>
  <div v-loading="loading" class="webCanvasCom">
    <template v-if="graphicList && graphicList.length">
      <div class="w-full h-full flex flex-col justify-start items-stretch">
        <div class="h-32px flex justify-start items-center pl-12px overflow-x-auto overflow-y-hidden">
          <FancyTab :tabs="graphicTabs" v-model:activeTab="webViewLink" :just-text="true" />
        </div>
        <div class="flex-1 overflow-hidden">
          <web-view v-if="webViewLink" v-model:loading="loading" :src="webViewLink"></web-view>
        </div>
      </div>
    </template>
    <Empty title="暂无图纸" />
  </div>
</template>

<script>
import { getActiveTimeStampFun } from "@/utils/dateTime";
import WebView from "@/components/component/WebView.vue";
import { reactive, ref, defineComponent, toRefs, watch, getCurrentInstance } from "vue";
import { findGraphRelevancyListByDeviceId } from "@/api/centralMonitoring/centralMonitoring";
import FancyTab from "@/components/Tabs/FancyTab.vue";
import Empty from "@/views/monitor/_slice/base/empty.vue";

export default defineComponent({
  name: "WebCanvasCom",
  components: { WebView, FancyTab, Empty },
  props: {
    siteId: {
      type: [Number, String],
      default: ""
    },
    code: {
      type: [Number, String],
      default: ""
    },
  },
  setup(props) {

    const { emit } = getCurrentInstance();
    const that = reactive({
      loading: false,
      webViewLink: "", // 当前展示的路径
      graphicList: [],
      graphicTabs: []
    });

    // 根据设备id查询图形关联列表
    const queryGraphRelevancyListByDeviceId = () => {
      that.loading = true;
      findGraphRelevancyListByDeviceId({ deviceId: props.siteId, graphTypeCode: '' }).then(res => {
        let graphicList = res.data ? res.data : [];
        //过滤非主页默认的接线图
        graphicList = graphicList.filter((item) => item.isDefault !== 1);
        graphicList.forEach(item => {
          if (item.graphUrl) item.graphUrl = `${item.graphUrl}?v=${getActiveTimeStampFun()}&theme=dark`;
        });
        const findItem = graphicList[0];
        that.webViewLink = findItem?.graphUrl ?? ""; // 获取默认图纸
        that.graphicList = JSON.parse(JSON.stringify(graphicList));
        that.graphicTabs = that.graphicList.map((item) => {
          return {
            id: item.graphUrl,
            name: item.graphName
          }
        })
        emit("changeEvent", { type: "graphicList", graphicList: that.graphicList });
        if (!that.webViewLink) that.loading = false;
      }).catch(() => {
        that.loading = false;
      });
    };

    const watchSiteIdAndCode = watch([() => props.siteId, () => props.code], ([newSiteId, newCode]) => {
      if (newSiteId) queryGraphRelevancyListByDeviceId();
    }, { deep: true, immediate: true });

    return { ...toRefs(that), queryGraphRelevancyListByDeviceId, watchSiteIdAndCode };
  }
});
</script>

<style lang="scss" scoped>
.webCanvasCom {
  width: 100%;
  height: 100%;
  position: relative;
  box-sizing: border-box;

  .select_class {
    width: 260px;
    z-index: 10;
    position: absolute;
    left: 16px;
    top: 12px;
  }
}
</style>