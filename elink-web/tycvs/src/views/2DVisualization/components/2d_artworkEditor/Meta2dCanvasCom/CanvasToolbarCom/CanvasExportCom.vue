<template>
  <div class="canvasExportCom">
    <template v-for="(item,index) in list" :key="index">
      <div class="content_list_li" @click="clickItemFun(item.fieldName)">
        <div class="content_list_li_left">{{ item.name }}</div>
        <div class="content_list_li_right"></div>
      </div>
    </template>
  </div>
</template>
<script>
import { useMeta2dStore } from '@/stores/index';

import FileSaver from 'file-saver';
import {ElLoading, ElMessage} from 'element-plus';
import {reactive, toRefs, defineComponent, computed} from "vue";
import {handleCanvasMeta2dDataFun} from "./handleCanvasMeta2dData";

export default defineComponent({
  name: 'CanvasExportCom',
  setup() {

    const meta2dStore = useMeta2dStore();
    const canvasMeta2d = computed(() => {
      return meta2dStore.canvasMeta2d;
    });

    const canvasMeta2dData = computed(() => {
      return meta2dStore.canvasMeta2dData;
    });

    const that = reactive({
      list: [
        {name: "导出为 png", fieldName: "png"},
        {name: "导出为 svg", fieldName: "svg"},
        {name: "导出为 json", fieldName: "json"},
      ]
    })

    const clickItemFun = (fieldName) => {
      const loading = ElLoading.service({lock: true, text: '正在下载中...', background: 'rgba(0, 0, 0, 0.7)'});

      try {
        if(fieldName === "png"){
          canvasMeta2d.value.downloadPng(canvasMeta2dData.value.name,12);
          // let canvasImgBlob = canvasMeta2d.value.toPng(12,undefined,true);
          // FileSaver.saveAs(canvasImgBlob, `${ canvasMeta2dData.value.name }.png`);
        }

        if(fieldName === "svg"){
          canvasMeta2d.value.downloadSvg(canvasMeta2dData.value.name,12);
        }

        if(fieldName === "json"){
          let blob = new Blob([JSON.stringify(handleCanvasMeta2dDataFun())], {type: 'application/json'});
          FileSaver.saveAs(blob, `${ canvasMeta2dData.value.name }.json`);
        }

        setTimeout(() => {
          ElMessage({type: 'success', showClose: true, message: '下载成功！'});
          loading.close();
        }, 500);
      } catch (e) {
        setTimeout(() => {
          loading.close();
          ElMessage({type: 'error', showClose: true, message: '导出失败！'});
        }, 500);
      }
    }

    return {...toRefs(that), clickItemFun, canvasMeta2d, canvasMeta2dData}
  }
})
</script>
<style lang="scss" scoped>
.canvasExportCom {
  width: 100%;

  .content_list_li {
    height: 35px;
    padding: 0 12px;
    box-sizing: border-box;
    display: flex;
    align-items: center;
    justify-content: space-between;
    color: var(--color);
    font-size: 12px;

    &:hover {
      cursor: pointer;
      background: var(--el-menu-hover-bg-color);
    }
  }
}
</style>