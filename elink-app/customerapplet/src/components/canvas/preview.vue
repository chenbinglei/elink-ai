<template>
  <div v-resize="meta2dResize" class="preview">
    <div id="meta2dCanvas" class="canvas"></div>
    <div v-if="!isShowGraph" class="null_data flex align-center jc-center"><null-data words="无数据"></null-data></div>
  </div>
</template>

<script>
import {Toast} from 'vant';
import {Meta2d} from '@meta2d/core';
import { canvasRegister } from "/packages/register"; //引入自定义图源库
import {onUnmounted, onMounted, reactive, toRefs, getCurrentInstance, watch, nextTick} from "vue";

export default {
  name: "preview",
  props: {
    // 图形文件字符串
    graphFileStr: {
      type: String,
      default: ""
    }
  },
  setup(props) {
    const {emit} = getCurrentInstance();

    const that = reactive({
      meta2dCanvas: null,  // 画布实列
      canvasOptions: {
        scroll: false, // 是否显示滚动条
      },
      meta2dChartOption: {},
      isShowGraph: true,
    })

    // 初始化 画布实例
    const initMeta2dFun = () => {
      that.meta2dCanvas = new Meta2d('meta2dCanvas', that.canvasOptions);
      that.meta2dCanvas.on('*', bindMeta2dAllFun);
      that.meta2dCanvas.lock(1); // 默认锁定画布
      canvasRegister();  // 注册自定义图形库
    }

    const bindMeta2dAllFun = (event, data)=>{
      if(event === "opened"){
        emit("meta2dFun",{ action: "loadCanvas",isShowGraph: that.isShowGraph });
      }
    }

    const renderingMeta2dFun = () => {
      try {
        // 查看画布是否有数据
        let isShowGraph = !(!that.meta2dChartOption || that.meta2dChartOption === "{}");
        // console.log(isShowGraph);
        if (isShowGraph) {
          // 打开画布
          // console.log(that.meta2dCanvas)
          that.meta2dCanvas.open(that.meta2dChartOption,true);
          if (!that.meta2dCanvas.hasView())isShowGraph = false; //重新查看画布是否有图源
          that.meta2dCanvas.topView();
        } else {
          that.meta2dCanvas.open({});
        }

        that.isShowGraph = isShowGraph;
        that.meta2dCanvas.lock(1);
      } catch (e) {
        console.log(e);
        that.isShowGraph = false;
        that.meta2dCanvas.open({});
        that.meta2dCanvas.lock(1);
        Toast({message: '图形打开失败！', position: 'bottom'});
      }
    }

    const clickDownloadFile = (data) => {
      if (!that.meta2dCanvas || !that.isShowGraph) {
        Toast({message: '模板暂无数据！', position: 'bottom'});
        return
      }

      if (data.action === 'exportPng') {
        // that.meta2dCanvas.downloadPng(data.graphName, 10);
        let base64Data = that.meta2dCanvas.toPng(12);
        uni.postMessage({ data: { type: "downloadPng", base64Data: base64Data } });
        uni.navigateTo({ url: "/fourthPackage/pages/reportDownload" });
      }

      if (data.action === 'exportPdf') {
        // console.log(base64Data);
        // window.URL.createObjectURL(new Blob([base64Data], { type: 'application/pdf' }));
      }
    }

    const meta2dResize = () => {
      if(that.meta2dCanvas)that.meta2dCanvas.resize();
    }

    // 销毁画布
    const destroyCanvas = () => {
      that.meta2dCanvas && that.meta2dCanvas.destroy();
      console.log("画布销毁成功~！！！");
    }

    const setMeta2dChartOption = ()=>{
      that.meta2dChartOption = props.graphFileStr ? JSON.parse(props.graphFileStr) : props.graphFileStr;
      renderingMeta2dFun();
    }

    const watchGraphFileStr = watch(() => props.graphFileStr, (newGraphFileStr) => {
      // console.log(newGraphFileStr);
      setMeta2dChartOption();
    }, { deep: true })

    onMounted(() => {
      initMeta2dFun();
    })

    onUnmounted(() => {
      destroyCanvas();
    });

    return {...toRefs(that), initMeta2dFun, watchGraphFileStr, renderingMeta2dFun, meta2dResize, clickDownloadFile, setMeta2dChartOption}
  }
}
</script>

<style lang="scss" scoped>
.preview {
  width: 100%;
  height: 100%;
  position: relative;

  .canvas{
    min-width: 10px;
    min-height: 10px;
  }

  .canvas, .null_data {
    width: 100%;
    height: 100%;
    overflow: hidden;
  }

  .null_data {
    z-index: 1000;
    position: absolute;
    left: 0;
    top: 0;
  }
}
</style>
