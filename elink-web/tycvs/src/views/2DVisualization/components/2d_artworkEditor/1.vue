<template>
  <div class="meta2dEditorCom">
    <GroupsPanelCom ref="groupsPanelComRef" />
    <Meta2dCanvasCom ref="meta2dCanvasComRef" v-loading="loading" @canvasMeta2dFun="canvasMeta2dFun" />
    <CanvasInteractionCom ref="canvasInteractionComRef" />
  </div>
</template>

<script lang="ts">
import { useMeta2dStore } from '@/stores/index';

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
      default: "normal"  //normal:正常开发   module： 组件开发
    }
  },
  components: { GroupsPanelCom, Meta2dCanvasCom, CanvasInteractionCom },
  setup (props) {

    const meta2dStore = useMeta2dStore();
    const canvasMeta2d = computed(() => {
      return meta2dStore.canvasMeta2d;
    });

    const canvasMeta2dData = computed(() => {
      return meta2dStore.canvasMeta2dData;
    });

    const customCanvasOptionsList = computed(() => {
      return meta2dStore.customCanvasOptionsList;
    });

    const that = reactive({
      loading: false
    })

    // 图模组件回调
    const canvasMeta2dFun = (data) => {
      // 画布初始化成功
      if (data.type === "initMeta2d") canvasMeta2dOpenFun(); // 需要打开新的文件
    }

    // 打开新的画布
    const canvasMeta2dOpenFun = () => {
      let canvasData = JSON.parse(JSON.stringify(canvasMeta2dData.value));
      // console.log(canvasMeta2d);

      // 正常打开模式
      if (props.modeType === "normal") {
        if (!canvasData.filePath) {
          meta2dCanvasLoadSFun(); // 无图纸执行
          return
        }

        that.loading = true;
        const fileName = getFileNameFromPath(canvasData.filePath);
        readOSSFile({ fileName: fileName }).then(result_file => {
          let fileData = result_file.data ? JSON.parse(result_file.data) : {};
          if (JSON.stringify(fileData) !== "{}") {
            let canvasDrawingOptions = {};
            for (let i = 0; i < customCanvasOptionsList.value.length; i++) {
              if (fileData[customCanvasOptionsList.value[i].fieldName]) {
                canvasDrawingOptions[customCanvasOptionsList.value[i].fieldName] = fileData[customCanvasOptionsList.value[i].fieldName];
              }
              delete fileData[customCanvasOptionsList.value[i].fieldName]; // 删除对应的数据对象
            }
            fileData.name = canvasData.name;
            canvasMeta2d.value.setOptions(canvasDrawingOptions); // 设置画布配置
            canvasMeta2d.value.open(fileData);
          }
          meta2dCanvasLoadSFun(); // 画布全部加载完成
        }).catch(() => {
          meta2dCanvasLoadSFun();
        })
      }

      // 如果打开的是自定义图元
      if (props.modeType === "module") {
        let fileData = canvasData.fileData ? JSON.parse(canvasData.fileData) : {};
        if (fileData && fileData.length) canvasMeta2d.value.addPens(fileData);
        meta2dCanvasLoadSFun();   // 画布全部加载完成
      }
    }

    // 画布全部加载完成
    const meta2dCanvasLoadSFun = () => {
      meta2dStore.updateCanvasMeta2dAllLoad(true);
      console.log("画布资源全部加载完成！！！");
      that.loading = false;
    }

    // 手动注册画布
    const meta2dCanvasComRef = ref(null);
    const activeRegistrationMeta2dFun = () => {
      meta2dCanvasComRef.value.initMeta2dFun();
    }

    // 生成组件
    const canvasMeta2dToComponentFun = () => {
      const pens = canvasMeta2d.value.toComponent();
      return JSON.stringify(pens)
    }

    onMounted(() => {
      meta2dStore.updateCanvasModeType(props.modeType); // 全局更新类型
    })

    return {
      ...toRefs(that), canvasMeta2dData, canvasMeta2dOpenFun, canvasMeta2d, canvasMeta2dToComponentFun, meta2dCanvasComRef, activeRegistrationMeta2dFun,
      canvasMeta2dFun, customCanvasOptionsList, meta2dCanvasLoadSFun
    }
  }
})
</script>

<style lang="scss" scoped>
.meta2dEditorCom {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
}
</style>
