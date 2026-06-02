<template>
  <div class="meta2dEditorCom">
    <GroupsPanelCom ref="groupsPanelComRef" />
    <Meta2dCanvasCom ref="meta2dCanvasComRef" v-loading="loading" @canvasMeta2dFun="canvasMeta2dFun" />
    <CanvasInteractionCom ref="canvasInteractionComRef" />
  </div>
</template>

<script>
import { useStore } from "vuex";
import { getFileNameFromPath } from "@/utils";
import { readOSSFile } from "@/common/readOSSFile";
import GroupsPanelCom from "./GroupsPanelCom.vue";
import Meta2dCanvasCom from "./Meta2dCanvasCom.vue";
import CanvasInteractionCom from "./CanvasInteractionCom.vue";
import { reactive, toRefs, defineComponent, computed, ref, onMounted } from "vue";

export default defineComponent({
  name: 'Meta2dEditorCom',
  props: {
    modeType: {
      type: String,
      default: "normal"
    }
  },
  components: { GroupsPanelCom, Meta2dCanvasCom, CanvasInteractionCom },
  setup (props) {
    const store = useStore();
    const canvasMeta2d = computed(() => store.state.meta2d.canvasMeta2d);
    const canvasMeta2dData = computed(() => store.state.meta2d.canvasMeta2dData);
    const customCanvasOptionsList = computed(() => store.state.meta2d.customCanvasOptionsList);

    const that = reactive({ loading: false });

    const canvasMeta2dFun = (data) => {
      if (data.type === "initMeta2d") canvasMeta2dOpenFun();
    };

    const canvasMeta2dOpenFun = () => {
      let canvasData = JSON.parse(JSON.stringify(canvasMeta2dData.value));
      if (props.modeType === "normal") {
        if (!canvasData.filePath) { meta2dCanvasLoadSFun(); return; }
        that.loading = true;

        if (canvasData.filePath.indexOf("oss-cn-hangzhou") == -1) {
          fetchHttpFileWithProxy(canvasData);
        } else {
          const fileName = getFileNameFromPath(canvasData.filePath);
          readOSSFile({ fileName }).then(result_file => {
            // let fileData = result_file.data ? JSON.parse(result_file.data) : {};
            let fileData = result_file.data ? JSON.parse(result_file.data) : {};
            if (fileData.canvasMeta2dFileData) {
              fileData = fileData.canvasMeta2dFileData;
            }

            if (JSON.stringify(fileData) !== "{}") {
              let canvasDrawingOptions = {};
              customCanvasOptionsList.value.forEach(item => {
                if (fileData[item.fieldName]) {
                  canvasDrawingOptions[item.fieldName] = fileData[item.fieldName];
                  delete fileData[item.fieldName];
                }
              });
              fileData.name = canvasData.name;
              canvasMeta2d.value.setOptions(canvasDrawingOptions);
              canvasMeta2d.value.open(fileData);
            }
            meta2dCanvasLoadSFun();
          }).catch(() => meta2dCanvasLoadSFun());
        }
      }

      if (props.modeType === "module") {
        let fileData = canvasData.fileData ? JSON.parse(canvasData.fileData) : {};
        if (fileData?.length) canvasMeta2d.value.addPens(fileData);
        meta2dCanvasLoadSFun();
      }
    };

    const fetchHttpFileWithProxy = async (canvasData) => {
      try {
        const proxyUrl = convertToProxyUrl(canvasData.filePath);
        const res = await fetch(proxyUrl);
        const fileData = await res.json();
        applyCanvasData(fileData.canvasMeta2dFileData || fileData, canvasData);
      } catch (e) { console.error("代理失败", e); }
      finally { meta2dCanvasLoadSFun(); }
    };

    const convertToProxyUrl = (url) => {
      if (url.startsWith('/configure/')) return url;
      try { return new URL(url).pathname; } catch (e) { return url; }
    };

    const applyCanvasData = (fileData, canvasData) => {
      try {
        let canvasDrawingOptions = {};
        customCanvasOptionsList.value.forEach(item => {
          if (fileData[item.fieldName]) {
            canvasDrawingOptions[item.fieldName] = fileData[item.fieldName];
            delete fileData[item.fieldName];
          }
        });
        fileData.name = canvasData.name;
        canvasMeta2d.value.setOptions(canvasDrawingOptions);
        canvasMeta2d.value.open(fileData);
      } catch (e) { console.error("应用数据失败", e); }
    };

    const meta2dCanvasLoadSFun = () => {
      store.dispatch("updateCanvasMeta2dAllLoad", true);
      that.loading = false;
    };

    const meta2dCanvasComRef = ref(null);
    const activeRegistrationMeta2dFun = () => meta2dCanvasComRef.value.initMeta2dFun();
    const canvasMeta2dToComponentFun = () => JSON.stringify(canvasMeta2d.value.toComponent());

    onMounted(() => {
      store.dispatch("updateCanvasModeType", props.modeType);
    });

    return {
      ...toRefs(that),
      canvasMeta2dData,
      canvasMeta2dOpenFun,
      canvasMeta2d,
      canvasMeta2dToComponentFun,
      meta2dCanvasComRef,
      activeRegistrationMeta2dFun,
      canvasMeta2dFun,
      customCanvasOptionsList,
      meta2dCanvasLoadSFun
    };
  }
});
</script>

<style lang="scss" scoped>
.meta2dEditorCom {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
}
</style>