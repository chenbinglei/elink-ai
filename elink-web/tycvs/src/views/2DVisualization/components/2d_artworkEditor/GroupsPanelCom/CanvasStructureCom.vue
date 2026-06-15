<template>
  <div class="canvasStructureCom">
    <div class="content_top">
      <template v-for="(item,index) in top_icon_list" :key="index">
        <el-tooltip :content="item.name" effect="dark" placement="top">
          <div class="icon_class flex-jc-ai-center" @click="clickTopItemBut(item.fieldName)">
            <span :class="item.iconName" class="iconfont"></span>
          </div>
        </el-tooltip>
      </template>
    </div>
    <div ref="tableCenterRef" v-resize="setTreeMaxHeight" class="content_bottom flex-all">
      <el-tree-v2 ref="treeV2Ref" :data="handleMenuArray" :height="treeMaxHeight" :props="treePropConfig" highlight-current @node-click="clickActiveNodeFun">
        <template #default="{ node,data }">
          <div class="content_list" :class="{ active_class: lockActivePelCLassFun(data) }">
            <div class="content_list_left">
              <span :class="[data.isPel ? 'icon-lifangti' : 'icon-wenjianjia']" class="prefix_icon iconfont"></span>
              <span class="label">{{ node.label }}</span>
            </div>
            <div class="content_list_right">

              <div @click="clickThreeItemFun('visible',data)" style="margin-right: 6px">
                <span class="iconfont yanjing " :class="[data.visible ? 'icon-yanjing_xianshi' : 'icon-yanjing_yincang']" ></span>
              </div>

              <el-tooltip effect="dark" placement="top">
                <template v-if="data.isPel">
                  <div class="flex-ai-center" @click="clickThreeItemFun('locked',data)">
                    <span class="iconfont icon-kebianji" v-if="!data.locked"></span>
                    <span class="iconfont icon-lock" v-if="data.locked === 1"></span>
                    <span class="iconfont icon-wufayidong" v-if="data.locked === 2"></span>
                    <span class="iconfont icon-jinyong" v-if="data.locked === 10"></span>
                  </div>
                </template>
                <template #content>
                  <span v-if="!data.locked">可编辑</span>
                  <span v-if="data.locked === 1">禁止编辑</span>
                  <span v-if="data.locked === 2">禁止编辑和移动</span>
                  <span v-if="data.locked === 10">禁止所有事件</span>
                </template>
              </el-tooltip>
            </div>
          </div>
        </template>
      </el-tree-v2>
    </div>
  </div>
</template>

<script lang="ts">
import { useMeta2dStore } from '@/stores/index';

import {setTreeData, treeToArray} from "@/utils";
import {reactive, toRefs, defineComponent, ref, computed, watch} from "vue";
import {ElMessage} from "element-plus";

export default defineComponent({
  name: "CanvasStructureCom",
  setup() {

    const meta2dStore = useMeta2dStore();
    const canvasMeta2d = computed(() => {
      return meta2dStore.canvasMeta2d;
    });

    // 代表画布初始化成功
    const canvasMeta2dAllLoad = computed(() => {
      return meta2dStore.canvasMeta2dAllLoad;
    });

    // 当前选中的图元
    const current_active_pel_list = computed(() => {
      return meta2dStore.current_active_pel_list;
    });

    const that = reactive({
      top_icon_list: [
        {id: 1, name: '置顶', iconName: 'icon-zhiding',fieldName: "top"},
        {id: 1, name: '置底', iconName: 'icon-zhidi',fieldName: "bottom"},
        {id: 1, name: '上一层', iconName: 'icon-shangjiantou',fieldName: "up"},
        {id: 1, name: '下一层', iconName: 'icon-xiajiantou',fieldName: "down"},
        {id: 1, name: '展开', iconName: 'icon-zhankai',fieldName: "expand"},
      ],
      treeMaxHeight: 380,
      handleMenuArray: [],
      defaultExpandAll: false, // 树形结构是否展开
      treePropConfig: {value: 'id', label: 'name', children: 'children'},
      defaultMenuArray: [
        {id: 4, name: "上层图片层", visible: true},
        {id: 3, name: "主画布层", visible: true},
        {id: 2, name: "底层图片层", visible: true},
        {id: 1, name: "模版层", visible: true},
      ],
    })

    const queryCanvasMeta2dPensFun = () => {
      try {
        let handleMenuArray = [];
        let {pens} = canvasMeta2d.value?.store;
        // console.log(pens);
        for (let key in pens) {
          if(pens[key]){
            handleMenuArray.push({
              id: key,
              locked: pens[key]?.locked,
              isPel: pens[key]?.name !== "combine",
              parentId: setPelParentIdFun(pens[key]),
              name: pens[key]?.chineseName || pens[key]?.name,
              visible: pens[key]?.visible === undefined ? true : pens[key]?.visible,
            })
          }
        }
        let newHandleMenuArray = setTreeData([...handleMenuArray, ...that.defaultMenuArray]);
        that.handleMenuArray = JSON.parse(JSON.stringify(newHandleMenuArray));
        // console.log(that.handleMenuArray);
      } catch (e) {
        console.log(e);
      }
    }

    // 设置图元父级id
    const setPelParentIdFun = (data = {}) => {
      let parentId = data.parentId;
      if(!data.externElement){
        if (!data.parentId && data.name !== 'gif') {
          parentId = data.canvasLayer ? data.canvasLayer : 3;
        }
      }
      return parentId
    }

    const treeV2Ref = ref(null);
    const clickTopItemBut = (fieldName)=>{

      if(fieldName !== "expand" && (!current_active_pel_list.value || !current_active_pel_list.value.length)){
        ElMessage({type: "warning", showClose: true, message: "请先选择图元！"});
        return
      }

      if(fieldName === "expand"){
        let defaultExpandedKeys = [];
        that.defaultExpandAll = !that.defaultExpandAll;
        if(that.defaultExpandAll){
         let handleMenuArray = treeToArray(that.handleMenuArray);
          handleMenuArray.forEach(item=>{defaultExpandedKeys.push(item.id);});
        }
        let findIndex = that.top_icon_list.findIndex(item=> item.fieldName === fieldName);
        that.top_icon_list[findIndex].name = that.defaultExpandAll ? '折叠' :'展开';
        that.top_icon_list[findIndex].iconName = that.defaultExpandAll ? 'icon-zhedie' :'icon-zhankai';
        treeV2Ref.value.setExpandedKeys(defaultExpandedKeys);
      } else {
        for (let i = 0;i < current_active_pel_list.value.length;i++){
          let pen = canvasMeta2d.value.findOne(current_active_pel_list.value[i]);
          canvasMeta2d.value[fieldName](pen);
        }
      }
    }

    const clickThreeItemFun = (operateType,data)=>{
      if(operateType === "visible"){
        data.visible = !data.visible;
        let pen = canvasMeta2d.value.findOne(data.id);
        canvasMeta2d.value.setVisible(pen,data.visible);
      }

      if(operateType === "locked"){
        let locked = 0;
        if(!data.locked)locked = 1;
        if(data.locked === 1)locked = 2;
        if(data.locked === 2)locked = 10;
        if(data.locked === 10)locked = 0;
        data.locked = locked;
        canvasMeta2d.value.setValue({id: data.id, locked: locked });
      }
    }

    // 设置选中图元高亮_样式
    const lockActivePelCLassFun = (data)=>{
      let activeStatus = false;
      let findIndex = current_active_pel_list.value.findIndex(item => item === data.id);
      if(findIndex !== -1) activeStatus = true;
      return activeStatus
    }

    // 点击某个图元高亮
    const clickActiveNodeFun = (data)=>{
      let activePelDate = canvasMeta2d.value.findOne(data.id);
      if(activePelDate) canvasMeta2d.value.active([activePelDate]);
    }

    const tableCenterRef = ref(null);
    const setTreeMaxHeight = () => {
      that.treeMaxHeight = tableCenterRef.value.offsetHeight;
    }

    // 监听画布是否初始化成功
    const watchCanvasMeta2dAllLoad = watch([() => canvasMeta2dAllLoad,()=>current_active_pel_list], ([newCanvasMeta2dAllLoad,newCurrentActivePelList]) => {
      if (newCanvasMeta2dAllLoad.value) queryCanvasMeta2dPensFun();
    }, {deep: true, immediate: true})

    return {...toRefs(that), canvasMeta2d, queryCanvasMeta2dPensFun, watchCanvasMeta2dAllLoad, setTreeMaxHeight, tableCenterRef, setPelParentIdFun, clickTopItemBut,
      treeV2Ref, clickThreeItemFun, current_active_pel_list, lockActivePelCLassFun, clickActiveNodeFun}
  }
})
</script>

<style lang="scss" scoped>
.canvasStructureCom {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  padding: 12px 8px 10px 8px;
  box-sizing: border-box;

  .content_top {
    display: flex;
    align-items: center;
    justify-content: flex-end;
    margin-bottom: 16px;

    .icon_class {
      width: 24px;
      height: 24px;
      border-radius: 4px;
      margin-right: 4px;

      .iconfont {
        color: #242424;
        font-size: 18px;
      }

      &:last-child {
        margin-right: 0;
      }

      &:hover {
        cursor: pointer;
        background-color: #e5e5e5;
      }
    }
  }

  :deep(.el-tree) {
    --el-tree-node-content-height: 38px;

    .el-tree-node__content {
      width: 100%;
      box-sizing: border-box;

      .content_list {
        flex: 1;
        display: flex;
        align-items: center;
        justify-content: space-between;
        padding-right: 4px;
        box-sizing: border-box;

        .content_list_left {
          .prefix_icon {
            font-size: 14px;
            margin-right: 4px;
          }
        }

        .content_list_right {
          display: flex;
          align-items: center;

          .iconfont {
            display: none;
          }
        }
      }

      .active_class{
        color: #007FEB;
      }

      &:hover {
        color: #007FEB;

        .content_list_right {
          .iconfont {
            display: block;
          }
        }
      }
    }
  }
}
</style>