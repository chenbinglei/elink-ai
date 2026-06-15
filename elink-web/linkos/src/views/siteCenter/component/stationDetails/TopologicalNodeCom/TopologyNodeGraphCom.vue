<template>
  <div class="topologyNodeGraphCom">
    <vue3-tree-org :center="center" :clone-node-drag="cloneNodeDrag" :collapsable="collapsable" :data="treeOrgData" :default-expand-level="defaultExpandLevel"
                   :define-menus="defineMenus" :horizontal="horizontal" :label-style=style :node-draggable="nodeDraggable" :only-one-node="onlyOneNode"
                   :scalable="scalable" :toolBar="toolBarConfig" :props="treeOrgProps" @on-node-click="clickTopologyNodeFun">
      <!-- 自定义节点内容 -->
      <template v-slot="{node}">
        <div class="tree-org-node_class node-label">
          <span v-if="node.$$data.nodeType === 0" class="iconfont icon-jiliangjiedian"></span>
          <template v-if="node.$$data.nodeType === 1">
            <span v-if="node.$$data.deviceType === 1" class="iconfont icon-guangfuxitong"></span>
            <span v-if="node.$$data.deviceType === 2" class="iconfont icon-chunengxitong"></span>
            <span v-if="node.$$data.deviceType === 3" class="iconfont icon-dianzhuangxitong"></span>
            <span v-if="node.$$data.deviceType === 4" class="iconfont icon-qita"></span>
          </template>
          <div class="node_text" :class="{active_node_class: node.id === activeNodeId}">{{ node.label }}</div>

          <div class="content_node_operate pointer">
            <el-dropdown placement="bottom-start" v-if="node.$$data.nodeType === 0">
              <el-icon size="16" color="#108ffe"><Setting /></el-icon>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="clickOperateButFun(1, node)">添加节点</el-dropdown-item>
                  <el-dropdown-item @click="clickOperateButFun(2, node)">删除节点</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
            <template v-if="node.$$data.nodeType === 1">
              <el-icon size="16" color="#108ffe" @click="clickOperateButFun(2, node)"><Delete /></el-icon>
            </template>
          </div>
        </div>
      </template>
      <!-- 自定义展开按钮 -->
      <template v-slot:expand="{node}">
        <div>{{ node.children.length }}</div>
      </template>
    </vue3-tree-org>

    <AddTopologyNodeDialog v-if="addTopologyNodeVisible" v-model:isVisible="addTopologyNodeVisible" :siteRecordsId="siteRecordsId" :titleName="titleName"
                           :activeParentNodeInfo="activeParentNodeInfo" @changeEvent="changeEvent"></AddTopologyNodeDialog>
  </div>
</template>

<script lang="ts">
// https://sangtian152.github.io/vue3-tree-org/demo/#attributes  官网
import {setTreeData} from "@/utils";
import {ElMessage, ElMessageBox} from "element-plus";
import {Setting, Delete} from '@element-plus/icons-vue';
import AddTopologyNodeDialog from "./AddTopologyNodeDialog.vue";
import {deleteTopoNodeInfoById} from "@/api/siteCenter/stationDetails";
import {reactive, toRefs, defineComponent, watch, getCurrentInstance} from "vue";

export default defineComponent({
  name: "TopologyNodeInfoCom",
  components:{AddTopologyNodeDialog, Setting, Delete},
  props: {
    siteRecordsId: {
      type: [String, Number],
      default: ""
    },
    activeNodeId: {
      type: [String, Number],
      default: ""
    },
    topologyNodeList: {
      type: Array,
      default: () => []
    }
  },
  emits: ["update:activeNodeId"],
  setup(props) {
    const {emit} = getCurrentInstance();

    const that = reactive({
      treeOrgData: {},
      center: true, // 是否水平居中
      scalable: true,  // 架构图是否可缩放
      defineMenus: [],  // 阻止右键出发事件
      horizontal: false, // 是否是横向
      onlyOneNode: true,
      collapsable: true,  // 是否可以展开收起节点
      defaultExpandLevel: 10, // 默认展开层级
      nodeDraggable: false, // 节点是否可拖拽
      cloneNodeDrag: false, //是否拷贝节点拖拽
      style: {background: "#fff", color: "#5e6d82"},
      treeOrgProps: {label: "nodeName", pid: "parentId",children:"children",id:'id'},
      toolBarConfig: {scale: true, restore: true, expand: true, zoom: false, fullscreen: false},

      titleName: '添加节点',
      activeParentNodeInfo: {},
      addTopologyNodeVisible: false,
    })

    const clickOperateButFun = (operateType,node)=>{
      if(operateType === 1){
        that.titleName = '添加节点';
        that.activeParentNodeInfo = { parentName: node.label,parentId: node.id,activeDeviceType: node.$$data.deviceType };
        that.addTopologyNodeVisible = true;
      }

      if(operateType === 2){
        ElMessageBox.confirm(`您确定要删除所选节点（<span class="deleteName">${node.label}</span>）及其子节点吗？`, "删除提示", {
          customClass: "deleteMsgBoxClass", dangerouslyUseHTMLString: true, confirmButtonText: '确定',
          cancelButtonText: '取消', type: 'warning',closeOnClickModal: false,
          beforeClose: (action, instance, done)=>{
            if (action === 'confirm') {
              instance.confirmButtonLoading = true;
              instance.confirmButtonText = '正在删除...';
              deleteTopoNodeInfoById({ topoId: node.id }).then(()=> {
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
          emit("changeEvent");
          ElMessage({ type: "success",showClose: true,message: "删除成功！" });
        }).catch(() => {
          console.log("取消删除");
        });
      }
    }

    // 点击节点执行
    const clickTopologyNodeFun = (e,data)=>{
      // console.log(data);
      emit("update:activeNodeId",data.id);
    }

    const changeEvent = ()=>{
      emit("changeEvent");
    }

    const watchTopologyNodeList = watch(()=> props.topologyNodeList,(newTopologyNodeList)=>{
      let topologyNodeTreeList = setTreeData(newTopologyNodeList);
      // console.log(topologyNodeTreeList);
      that.treeOrgData = JSON.parse(JSON.stringify(topologyNodeTreeList[0] ?? null));
      // console.log(that.treeOrgData);
    },{ deep: true,immediate: true });

    return {...toRefs(that), watchTopologyNodeList, clickOperateButFun, changeEvent, clickTopologyNodeFun}
  }
})
</script>

<style lang="scss" scoped>
.topologyNodeGraphCom {
  width: 100%;
  height: 100%;

  .tree-org-node_class{
    min-width: 96px;
    padding: 12px 14px;
    box-sizing: border-box;
    position: relative;

    .iconfont{
      font-size: 24px;
      color: #3378FBFF;
    }

    .node_text{
      font-size: 16px;
      margin-top: 10px;
    }

    .active_node_class{
      color: #3378FBFF;
      font-weight: bold;
    }

    .content_node_operate{
      z-index: 100;
      position: absolute;
      right: 4px;
      top: 4px;

      .operate_text{
        font-size: 12px;
        color: #3378FBFF;
      }
    }
  }
}
</style>