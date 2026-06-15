<template>
  <div v-loading="loading" class="webCanvasCom">
    <template v-if="webViewLink">
      <web-view v-model:loading="loading" :src="webViewLink"></web-view>
    </template>
    <Empty title="暂无图纸" />
  </div>
</template>

<script lang="ts">
import { getActiveTimeStampFun } from "@/utils/dateTime";
import WebView from "@/components/component/WebView.vue";
import { reactive, defineComponent, toRefs, watch, getCurrentInstance } from "vue";
import { findGraphRelevancyListByDeviceId } from "@/api/centralMonitoring/centralMonitoring";
import Empty from "@/views/monitor/_slice/base/empty.vue";

export default defineComponent({
  name: "WebCanvasCom",
  components: { WebView, Empty },
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
    });

    // 根据设备id查询图形关联列表
    const queryGraphRelevancyListByDeviceId = () => {
      that.loading = true;
      findGraphRelevancyListByDeviceId({ deviceId: props.siteId, graphTypeCode: props.code }).then(res => {
        let graphicList = res.data ? res.data : [];
        graphicList.forEach(item => {
          if (item.graphUrl) item.graphUrl = `${item.graphUrl}?v=${getActiveTimeStampFun()}&theme=dark`;
        });
        let findItem = graphicList.find(item => item.isDefault === 1);
        that.webViewLink = findItem?.graphUrl ?? ""; // 获取默认图纸
        that.graphicList = JSON.parse(JSON.stringify(graphicList));
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