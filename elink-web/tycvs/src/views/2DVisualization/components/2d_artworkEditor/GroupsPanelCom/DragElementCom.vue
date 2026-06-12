<template>
  <div :draggable="draggable" class="content_li dragClass" @click="onTouchstartFun($event)" @dragstart="onDragstartFun($event)" @touchstart="onDragstartFun($event)">
   <div class="content_image flex-jc-ai-center">
     <img class="image_class" v-if="graph_info.fileType" :src="graph_info.filePath" alt="" />
     <span v-else :class="graph_info.icon" class="icon_name"></span>
   </div>
    <div class="icon_text textTwo">{{ graph_info.name }}</div>
  </div>
</template>

<script>
import { useMeta2dStore } from '@/stores/index';

// import {parseSvg} from "@meta2d/svg";
import {deepClone} from "@meta2d/core";
import {computed, defineComponent, reactive, toRefs, watch} from "vue";

export default defineComponent({
  name: 'DragElementCom',
  props: {
    graphInfo: {
      type: Object,
      default: () => {
        return {}
      }
    }
  },
  setup(props) {

    const meta2dStore = useMeta2dStore();
    const canvasMeta2d = computed(() => {
      return meta2dStore.canvasMeta2d;
    });

    const that = reactive({
      draggable: true,
      graph_info: {}
    })

    // 监听控件拖动事件
    const onDragstartFun = (event) => {
      // console.log(that.graph_info);
      event.dataTransfer.setData('Text', JSON.stringify(that.graph_info.data));
    };

    //点击选择图元
    const onTouchstartFun = () => {
      // console.log(that.graph_info);
      canvasMeta2d.value.canvas.addCaches = deepClone(that.graph_info.data);
    };

    const setPenCanvasDataFun = ()=>{
      let dataData = {};
      if(that.graph_info.dataData) dataData = JSON.parse(that.graph_info.dataData);
      that.graph_info.dataData = JSON.parse(JSON.stringify(dataData));
      // console.log(that.graph_info);
      if(that.graph_info.filePath){
        let fileType = "png";
        fileType = that.graph_info.filePath.indexOf('.svg') !== -1 ? 'svg' : fileType;
        fileType = that.graph_info.filePath.indexOf('.gif') !== -1 ? 'gif' : fileType;
        that.graph_info.fileType = fileType;
      }

      // 文件图元
      if(that.graph_info.pelType === 1){
        // if(that.graph_info.fileType === "svg"){
          // that.graph_info.data = parseSvg(that.graph_info.fileData);
        // }

        // if(that.graph_info.fileType === "png" || that.graph_info.fileType === "svg"){
          that.graph_info.data = [{
            crossOrigin: "undefined",
            ...that.graph_info.dataData,
            image: that.graph_info.filePath,
            chineseName: that.graph_info.name
          }]
        // }
        return;
      }

      // 组件图元
      if(that.graph_info.pelType === 2){
        that.graph_info.data = JSON.parse(JSON.stringify(that.graph_info.dataData));
        return
      }

      that.graph_info.data.chineseName = that.graph_info.name; // 设置  图元名称
      that.graph_info.data = JSON.parse(JSON.stringify([that.graph_info.data]));
    }

    const watchGraphInfo = watch(() => props.graphInfo, (newGraphInfo) => {
      that.graph_info = JSON.parse(JSON.stringify(newGraphInfo));
      setPenCanvasDataFun();
    }, {deep: true,immediate: true})

    return {...toRefs(that), canvasMeta2d, onDragstartFun, onTouchstartFun, watchGraphInfo, setPenCanvasDataFun}
  }
})
</script>

<style lang="scss" scoped>
.content_li {
  padding: 10px 2px;
  border-radius: 4px;
  box-sizing: border-box;
  border: 1px solid transparent;

  .content_image{
    width: 100%;
    height: 55px;
    padding: 2px 10px;
    box-sizing: border-box;

    .image_class{
      min-width: 25px;
      max-width: 55px;
      max-height: 55px;
    }

    .icon_name {
      font-size: 32px;
      font-weight: 500;
      color: var(--color-title);
    }
  }

  .font-weight-bold {
    font-weight: bolder;
  }

  .icon_text {
    font-size: 12px;
    margin-top: 4px;
    text-align: center;
    color: var(--color-title);
    -webkit-line-clamp: 1;
  }

  &:hover {
    border-color: var(--color-primary);
  }
}
</style>