<template>
  <div :style="{ height: contentMaxHeight + 'px' }" class="app-container-right" v-loading="listLoading">
    <div class="content_body_left">
      <!-- 只有当 topologyNodeList 为空时才显示按钮 -->
      <div class="null_class flex-jc-ai-center" v-if="!topologyNodeList.length">
        <el-button type="text" :icon="Plus" @click="clickAddTopologyNodeBut">添加拓扑节点</el-button>
      </div>
      <!-- 子组件始终渲染 -->
      <TopologyNodeGraphCom ref="dialogRef" :nodes="topologyNodeList" :siteRecordsId="siteRecordsId"
        v-model:activeNodeId="activeNodeId" @changeEvent="queryTopoNodeListBySiteId" @handleClickNode="handleClickNode">
      </TopologyNodeGraphCom>
    </div>
    <TopologyNodeInfoCom :activeNodeId="activeNodeId" :siteRecordsId="siteRecordsId" @infoUpdata="infoUpdata">
    </TopologyNodeInfoCom>
  </div>
</template>
<script>
import { Plus } from "@element-plus/icons-vue";
import { onMounted, reactive, toRefs, ref, defineComponent } from "vue";
import { findTopoNodeListBySiteId, findTopNodeListBySiteId } from "@/api/siteCenter/stationDetails";
import TopologyNodeInfoCom from "./TopologicalNodeCom/TopologyNodeInfoCom.vue";
import TopologyNodeGraphCom from "./TopologicalNodeCom/TopologyNodeGraphComVue.vue";
import AddTopologyNodeDialog from "./TopologicalNodeCom/AddTopologyNodeDialogVue.vue";

export default defineComponent({
  name: "TopologicalNodeCom",
  components: { TopologyNodeInfoCom, AddTopologyNodeDialog, TopologyNodeGraphCom },
  props: {
    siteRecordsId: {
      type: [String, Number],
      default: ""
    },
    contentMaxHeight: {
      type: Number,
      default: 520
    }
  },

  setup (props) {
    const that = reactive({
      Plus,
      activeNodeId: '',
      listLoading: false,
      titleName: '添加节点',
      topologyNodeList: [],
      activeParentNodeInfo: {},
      addTopologyNodeVisible: false,
      siteRecordsId: '',
    })
    const dialogRef = ref(null);
    const clickAddTopologyNodeBut = () => {
      const parentNode = {
        siteId: props.siteRecordsId,
      }
      
      
      dialogRef.value.addNode(parentNode, 'initial', null, null);
    }
    const handleClickNode = (node) => {
      that.activeNodeId = node.id
    }
    const infoUpdata = () => {
      queryTopoNodeListBySiteId()
    }

    // 根据站点id查询拓扑节点列表
    const queryTopoNodeListBySiteId = () => {
      that.listLoading = true;
      findTopNodeListBySiteId({ siteId: props.siteRecordsId }).then(res => {
        let topologyNodeList = res.data ?? [];
        let findActiveNodeInfo = topologyNodeList.find(item => item.id === that.activeNodeId);
        if (!findActiveNodeInfo) that.activeNodeId = "";
        that.topologyNodeList = JSON.parse(JSON.stringify(topologyNodeList));
        that.listLoading = false;
      }).catch(() => {
        that.listLoading = false;
      })
    }
    onMounted(() => {
      queryTopoNodeListBySiteId();
    })

    return { ...toRefs(that), dialogRef, infoUpdata, clickAddTopologyNodeBut, queryTopoNodeListBySiteId, handleClickNode }
  }
})
</script>

<style lang="scss" scoped>
.app-container-right {
  flex-direction: initial;

  .content_body_left {
    flex: 1;
    height: 100%;

    .null_class {
      width: 100%;
      height: 100%;
    }
  }
}
</style>