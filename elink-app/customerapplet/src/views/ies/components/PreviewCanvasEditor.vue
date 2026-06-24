<template>
  <div class="previewCanvasEditor">
    <div ref="canvasEditorRef" class="canvas-editor"></div>

    <van-overlay :show="loading">
      <van-loading vertical>加载中...</van-loading>
    </van-overlay>
  </div>
</template>

<script>
import JsPDF from "jspdf";
import {Toast} from 'vant';
import Editor, {EditorMode} from '@hufe921/canvas-editor';
import {onMounted, reactive, toRefs, ref, onUnmounted, getCurrentInstance, nextTick} from "vue";

export default {
  name: "PreviewCanvasEditor",
  props: {
    listLoading: {
      type: Boolean,
      default: false
    }
  },
  emits: ["update:listLoading"],
  setup() {
    const {emit} = getCurrentInstance();
    const canvasEditorRef = ref(null);

    const that = reactive({
      loading: false,
      canvasEditor: null,
      options: {
        pageNumber: {
          format: '第{pageNo}页/共{pageCount}页'
        },
        defaultBasicRowMarginHeight:0,
        placeholder: {
          data: "请输入..."
        }
      },
    })

    // 设置整个画布数据
    const setCanvasEditorHTML = (canvas) => {
      that.loading = true;
      let newCanvasData = JSON.parse(JSON.stringify(canvas));
      // console.log(newCanvasData);

      try {
        that.canvasEditor = new Editor(canvasEditorRef.value, newCanvasData.data, {
          ...that.options,
          mode: EditorMode.READONLY, // 只读模式
          width: newCanvasData.width, // 纸张宽度。默认：794
          height: newCanvasData.height, // 纸张高度。默认：1123
          margins: newCanvasData.margins, // 页面边距。默认：[100, 120, 100, 120]
          pageMode: newCanvasData.pageMode, // 纸张模式：连页、分页。默认：分页
          watermark: newCanvasData.watermark, // 水印信息。
          paperDirection: newCanvasData.paperDirection, // 纸张方向：纵向、横向
        });

        // that.canvasEditor.command.executePageScaleMinus();
        // that.canvasEditor.command.executePageScaleMinus();
        // that.canvasEditor.command.executePageScaleMinus();
        that.loading = false;
      } catch (e) {
        console.log(e);
        that.loading = false;
        Toast({message: '加载错误！', position: 'bottom'});
      }
    }

    // 打开打印页面
    const canvasEditorPrintFun = () => {
      that.canvasEditor.command.executePrint();
    }

    const setPageScaleFun = (operateType)=>{
      if(operateType === "scaleMinus")that.canvasEditor.command.executePageScaleMinus();
      if(operateType === "scaleAdd")that.canvasEditor.command.executePageScaleAdd();
    }

    // 保存下载pdf文件
    const downloadPdfFileFun = (data) => {
      try {
        const editorOption = that.canvasEditor.command.getOptions();
        const direction = editorOption.paperDirection === "vertical" ? 'p' : 'l';
        let pdf = new JsPDF(direction, 'pt', [editorOption.width, editorOption.height]);

        that.canvasEditor.command.getImage({type: 'png'}).then(res => {
          let base64StringList = res && res.length ? res : [];
          base64StringList.forEach((img, i) => {
            pdf.addImage(img,'JPEG',0,0,editorOption.width,editorOption.height);
            if (i !== base64StringList.length - 1) {
              pdf.addPage();
            }
          });

          // pdf.save(`${ data.pdfName }.pdf`);
          emit("update:listLoading", false);
          let base64Data = pdf.output('dataurlstring');
          uni.postMessage({ data: { type: "downloadPng", base64Data: base64Data } });
          uni.navigateTo({ url: "/fourthPackage/pages/reportDownload" });
        }).catch(() => {
          emit("update:listLoading", false);
          Toast({message: '下载失败！', position: 'bottom'});
        })
      } catch (e) {
        emit("update:listLoading", false);
        Toast({message: '下载失败！', position: 'bottom'});
      }
    }

    onMounted(() => {})

    onUnmounted(() => {
      if (that.canvasEditor) that.canvasEditor.destroy();
    })

    return {...toRefs(that), setCanvasEditorHTML, canvasEditorRef, canvasEditorPrintFun, downloadPdfFileFun,setPageScaleFun}

  }
}
</script>

<style lang="scss" scoped>
.previewCanvasEditor {
  width: 100%;
  height: 100%;
  overflow: auto;

  :deep(.ce-inputarea){
    height: 0 !important;
    display: none;
  }
}
</style>
