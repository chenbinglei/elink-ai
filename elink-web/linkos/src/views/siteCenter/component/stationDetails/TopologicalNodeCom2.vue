<template>
  <div :style="{height: contentMaxHeight + 'px'}" class="app-container-right" v-loading="listLoading">
    <div class="content_body_left">
      <template v-if="topologyNodeList && topologyNodeList.length">
        <TopologyNodeGraphCom :topologyNodeList="topologyNodeList" :siteRecordsId="siteRecordsId" v-model:activeNodeId="activeNodeId"
                              @changeEvent="queryTopoNodeListBySiteId"></TopologyNodeGraphCom>
      </template>
      <div class="null_class flex-jc-ai-center" v-else>
        <el-button type="text" :icon="Plus" @click="clickAddTopologyNodeBut">添加拓扑节点</el-button>
      </div>
    </div>
    <TopologyNodeInfoCom :activeNodeId="activeNodeId" :siteRecordsId="siteRecordsId"></TopologyNodeInfoCom>
    <AddTopologyNodeDialog v-if="addTopologyNodeVisible" v-model:isVisible="addTopologyNodeVisible" :siteRecordsId="siteRecordsId" :titleName="titleName"
                           :activeParentNodeInfo="activeParentNodeInfo" @changeEvent="queryTopoNodeListBySiteId"></AddTopologyNodeDialog>
  </div>
</template>

<script lang="ts">
import {Plus} from "@element-plus/icons-vue";
import {onMounted, reactive, toRefs, defineComponent} from "vue";
import {findTopoNodeListBySiteId} from "@/api/siteCenter/stationDetails";
import TopologyNodeInfoCom from "./TopologicalNodeCom/TopologyNodeInfoCom.vue";
import TopologyNodeGraphCom from "./TopologicalNodeCom/TopologyNodeGraphCom.vue";
import AddTopologyNodeDialog from "./TopologicalNodeCom/AddTopologyNodeDialog.vue";

export default defineComponent({
  name: "TopologicalNodeCom",
  components: {TopologyNodeInfoCom, AddTopologyNodeDialog, TopologyNodeGraphCom},
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
  setup(props) {
    const that = reactive({
      Plus,
      activeNodeId: '',
      listLoading: false,
      titleName: '添加节点',
      topologyNodeList: [],
      activeParentNodeInfo: {},
      addTopologyNodeVisible: false,
    })

    const clickAddTopologyNodeBut = () => {
      that.titleName = '添加节点';
      that.activeParentNodeInfo = { parentName: '/' };
      that.addTopologyNodeVisible = true;
    }

    // 根据站点id查询拓扑节点列表
    const queryTopoNodeListBySiteId = ()=>{
      that.listLoading = true;
      findTopoNodeListBySiteId({siteId: props.siteRecordsId}).then(res=>{
        let topologyNodeList = res.data ?? [];
        let findActiveNodeInfo = topologyNodeList.find(item => item.id === that.activeNodeId);
        if(!findActiveNodeInfo) that.activeNodeId = "";
        that.topologyNodeList = JSON.parse(JSON.stringify(topologyNodeList));
        that.listLoading = false;
      }).catch(()=>{
        // console.log(err);
        that.listLoading = false;
      })
    }

    onMounted(() => {
      queryTopoNodeListBySiteId();
    })

    return {...toRefs(that), clickAddTopologyNodeBut, queryTopoNodeListBySiteId}
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