<template>
  <div class="canvasToolbarCom scrollbarStyle">
    <template v-for="(item,index) in list" :key="index">
      <template v-if="(canvasModeType === 'module' && item.fieldName !== 'rightToolbar') || canvasModeType !== 'module'">
        <div :class="item.className" class="content_list">
          <template v-if="item.children && item.children.length">
            <template v-for="(tb_item,tb_index) in item.children" :key="tb_index">
              <template v-if="tb_item.componentName">
                <el-popover :width="200" placement="bottom-start" trigger="hover">
                  <template #reference>
                    <div class="toolbar_list pointer">
                      <span v-if="tb_item.showText" class="list_text">{{ tb_item.text }}</span>
                      <span v-else :class="tb_item.iconName" class="iconfont list_icon"></span>
                    </div>
                  </template>
                  <component :is="tb_item.componentName" @changEvent="setCanvasToolbarValueFun"></component>
                </el-popover>
              </template>
              <el-tooltip v-else effect="dark" placement="top">
                <div class="toolbar_list pointer" @click="clickItemFun(tb_item.fieldName)" @dblclick="dblClickItemFun(tb_item.fieldName)" :draggable="tb_item.fieldName === 'text'"
                     @dragstart="onDragstartFun($event,tb_item.fieldName)" @touchstart="onDragstartFun($event,tb_item.fieldName)">
                  <span v-if="tb_item.showText" class="list_text">{{ tb_item.text }}</span>
                  <span v-else :class="tb_item.iconName" class="iconfont list_icon"></span>
                </div>
                <template #content>{{ tb_item.name }}</template>
              </el-tooltip>
            </template>
          </template>
        </div>
      </template>
    </template>
    <AddDataSourceDialog v-if="addDataSourceVisible" v-model:isVisible="addDataSourceVisible" />
    <AddBindVariableDialog v-if="addBindVariableVisible" v-model:isVisible="addBindVariableVisible" />
    <CanvasMeta2dPublishDialog v-if="canvasMeta2dPublishVisible" v-model:isVisible="canvasMeta2dPublishVisible" />
  </div>
</template>
<script>
import {useStore} from "vuex";
import {deepClone} from "@meta2d/core";
import {useRoute, useRouter} from "vue-router";
import {ElMessage, ElMessageBox} from "element-plus";
import {saveGraph} from "@/api/2DVisualization/2d_drawManagement";
import {reactive, toRefs, defineComponent, computed, watch} from "vue";
import {handleCanvasMeta2dDataFun} from "./CanvasToolbarCom/handleCanvasMeta2dData";
import {CanvasExportCom, ConnectionMethodCom, StartingPointCom, EndingPointCom, AddDataSourceDialog, AddBindVariableDialog, CanvasMeta2dPublishDialog} from "./CanvasToolbarCom/index";

export default defineComponent({
  name: "CanvasToolbarCom",
  components: {CanvasExportCom,ConnectionMethodCom,StartingPointCom,EndingPointCom,AddDataSourceDialog,AddBindVariableDialog, CanvasMeta2dPublishDialog},
  setup() {

    const store = useStore();
    const route = useRoute();
    const vueRouter = useRouter();
    const canvasMeta2d = computed(() => {
      return store.state.meta2d.canvasMeta2d;
    });

    //normal:正常开发   module： 组件开发
    const canvasModeType = computed(() => {
      return store.state.meta2d.canvasModeType;
    });

    const canvas_scale = computed(() => {
      return store.state.meta2d.canvas_scale;
    });

    const canvasEyeMap = computed(() => {
      return store.state.meta2d.canvasEyeMap;
    });

    const disableScale = computed(() => {
      return store.state.meta2d.disableScale;
    });

    const canvasMeta2dData = computed(() => {
      return store.state.meta2d.canvasMeta2dData;
    });

    const canvasLocked = computed(() => {
      return store.state.meta2d.canvasLocked;
    });

    const that = reactive({
      addDataSourceVisible: false,
      addBindVariableVisible: false,
      canvasMeta2dPublishVisible: false,
      list: [
        {
          name: "画布左侧工具栏",
          fieldName: "leftToolbar",
          className: 'toolbar_list',
          children: [
            {name: "钢笔（双击可连续使用）", fieldName: "pen", iconName: "icon-gangbi"},
            {name: "铅笔", fieldName: "pencil", iconName: "icon-qianbi"},
            {name: "文字", fieldName: "text", iconName: "icon-wenzi"},
            {name: "连线方式", fieldName: "connection—method", iconName: "icon-quxian", componentName: "ConnectionMethodCom"},
            {name: "起点", fieldName: "starting-point", iconName: "icon-line", componentName: "StartingPointCom"},
            {name: "终点", fieldName: "ending-point", iconName: "icon-line", componentName: "EndingPointCom"},
            {name: "视图大小", fieldName: "view-size", text: "55%", showText: true},
            {name: "100%视图", fieldName: "bfb-view", iconName: "icon-xuexiao"},
            {name: "窗口大小", fieldName: "window-size", iconName: "icon-suofang"},
            {name: "禁止缩放", fieldName: "ban-scale", iconName: "icon-jinzhisuofang"},
            // {name: "地图", fieldName: "eye-map", iconName: "icon-dingwei"},
            {name: "清空所有辅助标尺线", fieldName: "clear-rule", iconName: "icon-qingkong"},
          ]
        },
        {
          name: "画布右侧工具栏",
          fieldName: "rightToolbar",
          className: 'toolbar_list',
          children: [
            {name: "编辑模式", fieldName: "lock-mode", iconName: "icon-suoding"},
            {name: "数据源", fieldName: "data-source", iconName: "icon-shujuyuan"},
            {name: "变量", fieldName: "data-var", iconName: "icon-bianliang"},
            {name: "导出", fieldName: "export", iconName: "icon-daochu", componentName: "CanvasExportCom"},
            // {name: "预览", fieldName: "preview", iconName: "icon-yulanwenjian"},
            {name: "保存", fieldName: "save-file", iconName: "icon-baocun"},
            {name: "发布", fieldName: "publish", iconName: "icon-yunduanshangchuan"},
          ]
        }
      ],
      // 文本图元属性
      textPenData:[{width: 120, height: 30, name: "text", text: "Text", chineseName: "文本", textAutoAdjust: true, whiteSpace: "break-all"}]
    })

    const clickItemFun = (fieldName) => {

      // 钢笔
      if(fieldName === "pen"){
        store.dispatch('updateDrawLineStatus',false);
        canvasMeta2d.value.drawLine('curve');
      }
      // 铅笔
      if(fieldName === "pencil") canvasMeta2d.value.drawingPencil();


      if(fieldName === "text"){
        canvasMeta2d.value.canvas.addCaches = deepClone(that.textPenData);
      }

      // 100%视图
      if(fieldName === "bfb-view"){
        canvasMeta2d.value.scale(1);
        canvasMeta2d.value.screenView(10); //大屏范围居中视图
      }

      // 窗口大小
      if(fieldName === "window-size"){
        canvasMeta2d.value.fitSizeView(true, 10);
      }

      // 禁止缩放
      if(fieldName === "ban-scale"){
        const disableScaleValue = !disableScale.value;
        const name = disableScaleValue ? "允许缩放" : "禁止缩放";
        const iconName = disableScaleValue ? "icon-yunxusuofang" : "icon-jinzhisuofang";
        setCanvasToolbarValueFun({ name: name, fieldName: "ban-scale", iconName: iconName });
        canvasMeta2d.value.setOptions({ disableScale: disableScaleValue });
        store.dispatch("updateDisableScale", disableScaleValue);
      }


      if(fieldName === "eye-map"){
        const canvasEyeMapValue = !canvasEyeMap.value;
        store.dispatch("updateCanvasEyeMap", canvasEyeMapValue);
        canvasEyeMapValue ? canvasMeta2d.value.hideMap() : canvasMeta2d.value.showMap();
      }

      // 清空所有标尺辅助线
      if(fieldName === "clear-rule") canvasMeta2d.value.clearRuleLines();

      if(fieldName === "data-source") that.addDataSourceVisible = true;

      if(fieldName === "data-var") that.addBindVariableVisible = true;

      if(fieldName === "lock-mode"){
        const canvasLockedValue = canvasLocked.value ? 0 : 1;
        const name = canvasLockedValue ? "浏览模式" :"编辑模式";
        const iconName = canvasLockedValue ? "icon-jiesuo" : "icon-suoding";
        setCanvasToolbarValueFun({ name: name, fieldName: "lock-mode", iconName: iconName });
        canvasMeta2d.value.lock(canvasLockedValue);
        store.dispatch("updateCanvasLocked", canvasLockedValue);
        // console.log(canvasLockedValue);
      }

      if(fieldName === "save-file"){
        let canvasData = JSON.parse(JSON.stringify(canvasMeta2dData.value));
        delete canvasData.filePath
        delete canvasData.fileData

        ElMessageBox.confirm(`确定保存（<span class="highlightText">${ canvasData.name }</span>）吗？`, "提示", {
          dangerouslyUseHTMLString: true, confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning',
          customClass: "deleteMsgBoxClass", showClose: false, closeOnClickModal: false,
          beforeClose: (action, instance, done) => {
            if (action === 'confirm') {
              let formData = new FormData();
              instance.confirmButtonLoading = true;
              instance.confirmButtonText = `正在保存...`;

              // 基础字段
              canvasData.updateType = 5; // 编辑类型 5-保存
              let returnDataInfo = handleCanvasMeta2dDataFun();
              for(let key in canvasData) if(canvasData[key])formData.append(key, canvasData[key]);
              // console.log(returnDataInfo);

              let meta2dBlob = new Blob([JSON.stringify(returnDataInfo.canvasMeta2dFileData)], {type: 'application/json'});
              let meta2dFile = new File([meta2dBlob], `${ canvasData.name }.json`, {type: 'application/json'});
              formData.append("deviceVariables", JSON.stringify(returnDataInfo.deviceVariables));
              formData.append("file", meta2dFile);
              saveGraph(formData).then(()=>{
                done();
                instance.confirmButtonLoading = false;
              }).catch(() => {
                instance.confirmButtonText = '确定';
                instance.confirmButtonLoading = false;
              });
            } else {
              done();
            }
          }
        }).then(() => {
          clickBackButFun();
          ElMessage({type: "success", showClose: true, message: "保存成功！"});
        }).catch(() => {
          console.log("取消删除！");
        });
      }

      // 发布
      if(fieldName === "publish") that.canvasMeta2dPublishVisible = true;
    }

    // 后退一页  回去
    const clickBackButFun = () => {
      vueRouter.go(-1);
      setTimeout(() => {
        let activeRoutePath = route.path;
        // 检测当前页的路由是否还是原来的，如果是原来的则重新回退
        if (activeRoutePath === that.activeRoutePath) {
          clickBackButFun();
        }
      }, 20)
    }

    // 监听控件拖动事件
    const onDragstartFun = (event,fieldName) => {
      if(fieldName === "text"){
        event.dataTransfer.setData('Text', JSON.stringify(that.textPenData));
      }
    };

    // 双击执行
    const dblClickItemFun = (fieldName)=>{
      // 钢笔 连续绘画
      if(fieldName === "pen"){
        store.dispatch('updateDrawLineStatus',true);
        canvasMeta2d.value.drawLine('curve');
      }
    }

    // 更新列表数据
    const setCanvasToolbarValueFun = (data = {})=>{
      // console.log(data)
      if(data.fieldName){
        let list = JSON.parse(JSON.stringify(that.list));
        for(let i = 0;i < list.length;i++){
          if(that.list[i].children && that.list[i].children.length){
            let findIndex = list[i].children.findIndex(item => item.fieldName === data.fieldName);
            if(findIndex !== -1){
              // 视图大小
              if(data.fieldName === "view-size") that.list[i].children[findIndex].text = data.text;

              // 连线方式  连线开始点  连线结束点
              if(data.fieldName === "connection—method" || data.fieldName ===  "starting-point" || data.fieldName ===  "ending-point") {
                that.list[i].children[findIndex].iconName = data.iconName;
              }

              // 禁止缩放  编辑模式
              if(data.fieldName ===  "ban-scale" || data.fieldName === "lock-mode")that.list[i].children[findIndex] = data;

            }
          }
        }
      }
    }

    // 监听画布缩放 级别发生改变执行
    const watch_canvas_scale = watch(()=>canvas_scale,(new_canvas_scale)=>{
      let scale_value = parseInt((new_canvas_scale.value).toFixed(2) * 100);
      setCanvasToolbarValueFun({ fieldName: "view-size",text: `${ scale_value }%` });
    },{ deep: true })

    return {...toRefs(that), clickItemFun, canvasMeta2d, dblClickItemFun, canvas_scale, watch_canvas_scale, setCanvasToolbarValueFun, disableScale,
      canvasMeta2dData, canvasLocked, onDragstartFun, clickBackButFun, canvasModeType, canvasEyeMap}
  }
})
</script>
<style lang="scss" scoped>
.canvasToolbarCom {
  width: 100%;
  height: 40px;
  display: flex;
  padding: 0 12px;
  overflow-x: auto;
  box-sizing: border-box;
  justify-content: space-between;
  background-color: var(--color-background);

  .content_list {
    height: 100%;
    display: flex;
    align-items: center;
    margin-right: 210px;

    .toolbar_list {
      padding: 6px;
      margin: 4px;
      border-radius: 4px;
      box-sizing: border-box;
      color: var(--color);

      .list_text {
        font-size: 12px;
      }

      .list_icon {
        font-size: 16px;
        font-weight: bold;
      }

      &:hover {
        background-color: #e5e5e5;
      }
    }

    &:last-child {
      margin-right: 0;
    }
  }

  &::-webkit-scrollbar {
    width: 10px;
    height: 4px;
  }

  &::-webkit-scrollbar-thumb {
    background-color: rgba(18, 28, 63, 0.8);
  }
}
</style>