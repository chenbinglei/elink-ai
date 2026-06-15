<template>
  <div class="canvasElementList">
    <div class="content_top_input">
      <div class="content_top_input_left">
        <el-input v-model="keyWords" placeholder="搜索" @input="clickSearchInput">
          <template #prefix>
            <div class="search_icon flex-jc-ai-center">
              <el-icon><Search/></el-icon>
            </div>
          </template>
        </el-input>
      </div>
      <el-tooltip content="折叠/展开" effect="dark" placement="top-start">
        <div class="content_top_input_right" @click="clickCollapseFun">
          <span :class="collapseStatus ? 'icon-zhedie' : 'icon-zhankai'" class="iconfont pointer"></span>
        </div>
      </el-tooltip>
    </div>
    <div class="content_bottom" v-loading="listLoading">
      <div class="scrollbarStyle">
        <ElementDragCom ref="elementDragComRef" :list="graphical_list"></ElementDragCom>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import {Search} from '@element-plus/icons-vue';
import ElementDragCom from "./ElementDragCom.vue";
import {selectTreeData, setTreeData} from "@/utils";
import {findPelList} from "@/api/2DVisualization/2d_artworkEditor";
import {basicGraphicsCom, customChartCom, customControlCom} from "./config/gallery";
import {reactive, toRefs, defineComponent, watch, ref, getCurrentInstance} from "vue";

export default defineComponent({
  name: "CanvasElementList",
  components: {Search,ElementDragCom},
  props: {
    // 默认 获取第一个 (数组id)
    tabsIndex: {
      type: String,
      default: ""
    }
  },
  setup(props) {
    const {emit} = getCurrentInstance();

    const that = reactive({
      keyWords: "",
      listLoading: false,
      graphical_list: [],
      old_graphical_list: [],
      collapseStatus: true, // 折叠 / 展开状态
    })

    // 设置组件显示的图形列表数据
    const queryGraphicalFun = () => {
      that.listLoading = true;
      emit("update:loading",that.listLoading);

      // 图形库
      if (props.tabsIndex === "BasicGraphicsCom") {
        that.graphical_list = JSON.parse(JSON.stringify(basicGraphicsCom));
        that.old_graphical_list = JSON.parse(JSON.stringify(that.graphical_list));
        that.listLoading = false;
        emit("update:loading",that.listLoading);
      }

      // 控件
      if (props.tabsIndex === "CustomControlCom") {
        that.graphical_list = JSON.parse(JSON.stringify(customControlCom));
        that.old_graphical_list = JSON.parse(JSON.stringify(that.graphical_list));
        that.listLoading = false;
        emit("update:loading",that.listLoading);
      }

      // 图元
      if (props.tabsIndex === "CustomElementCom") {
        // 查询图元管理数据
        findPelList({ timer: new Date() }).then(res=>{
          let isQiTaStatus = false;
          let list = res.data ? res.data : [];
          list.forEach(item=>{
            // 文件夹
            if(item.type === 1){
              item.fieldNameEn = item.id;
              item.fieldNameCn = item.name;
            }

            // 图元
            if(item.type === 2) {
              if(!item.parentId) {
                isQiTaStatus = true;
                item.parentId = "qiTaTuYuan-id";
              }
            }
          })

          if(isQiTaStatus) list.push({ id: "qiTaTuYuan-id",fieldNameCn: "其他",fieldNameEn: "qiTaTuYuan-id",type: 1 });
          that.graphical_list = setTreeData(list);
          that.old_graphical_list = JSON.parse(JSON.stringify(that.graphical_list));
          // console.log(that.graphical_list);
          that.listLoading = false;
          emit("update:loading",that.listLoading);
        }).catch(()=>{
          that.graphical_list = [];
          that.old_graphical_list = JSON.parse(JSON.stringify(that.graphical_list));
          that.listLoading = false;
          emit("update:loading",that.listLoading);
        })
      }

      // 图表
      if (props.tabsIndex === "CustomChartCom") {
        that.graphical_list = JSON.parse(JSON.stringify(customChartCom));
        that.old_graphical_list = JSON.parse(JSON.stringify(that.graphical_list));
        that.listLoading = false;
        emit("update:loading",that.listLoading);
      }
    }

    // 折叠打开列表
    const elementDragComRef = ref(null);
    const clickCollapseFun = () => {
      that.collapseStatus = !that.collapseStatus;
      elementDragComRef.value.clickCollapseFun();
    }

    const clickSearchInput = ()=>{
      that.listLoading = true;
      let graphical_list = JSON.parse(JSON.stringify(that.old_graphical_list));
      if(that.keyWords) graphical_list = selectTreeData(that.keyWords,"name",that.graphical_list);
      that.graphical_list = JSON.parse(JSON.stringify(graphical_list));
      that.listLoading = false;
    }

    const watchTabsIndex = watch(() => props.tabsIndex, (newTabsIndex) => {
      queryGraphicalFun();
    }, {deep: true,immediate: true})

    return {...toRefs(that), queryGraphicalFun, watchTabsIndex, clickCollapseFun, clickSearchInput, elementDragComRef}
  }
})
</script>

<style lang="scss" scoped>
.canvasElementList {
  height: 100%;
  padding-bottom: 12px;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;

  .content_top_input {
    padding: 16px;
    display: flex;
    align-items: center;
    box-sizing: border-box;

    .content_top_input_left {
      flex: 1;
    }

    .content_top_input_right {
      margin-left: 4px;
      box-sizing: border-box;

      .iconfont {
        font-size: 21px;
        transition: all .25s;
      }
    }
  }

  .content_bottom {
    flex: 1;
    height: 2px;

    .scrollbarStyle{
      height: 100%;
      overflow-y: auto;
    }
  }
}
</style>