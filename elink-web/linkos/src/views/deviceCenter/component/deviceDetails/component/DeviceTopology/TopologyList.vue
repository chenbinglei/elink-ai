<template>
  <div class="topologyList scrollbarStyle">
    <template v-if="list && list.length">
      <template v-for="(item,index) in list" :key="index">
        <TitleView :title="item.nodeName">
          <template #headerRight>
            <el-button :icon="CirclePlus" class="whiteFontButtons" @click="clickAddBut(item.nodeId)">添加设备</el-button>
          </template>
          <template #content>
            <div class="table_list">
              <el-table :data="item.otherNodeList" :max-height="tableMaxHeight" border stripe>
                <el-table-column align="center" label="序号" type="index" width="80"></el-table-column>
                <el-table-column align="center" label="节点ID">
                  <template #default="{ row }">{{ $filters.moreData(row.id) }}</template>
                </el-table-column>
                <el-table-column align="center" label="节点名称">
                  <template #default="{ row }">{{ $filters.moreData(row.nodeName) }}</template>
                </el-table-column>
                <el-table-column align="center" label="设备名称">
                  <template #default="{ row }">{{ $filters.moreData(row.deviceName) }}</template>
                </el-table-column>
                <el-table-column align="center" label="操作" width="220">
                  <template #default="{ row }">
                    <el-link :underline="false" type="danger" @click="clickOperateBut(1, row)">删除</el-link>
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </template>
        </TitleView>
      </template>
    </template>
    <template v-else><null-data></null-data></template>

    <AddTopologyDialog v-if="addTopologyVisible" v-model:isVisible="addTopologyVisible" :activeNodeId="activeNodeId" @changeEvent="changeEvent" />
  </div>
</template>

<script>
import AddTopologyDialog from "./AddTopologyDialog";
import {ElMessage, ElMessageBox} from "element-plus";
import {getCurrentInstance, reactive, ref, toRefs} from "vue";
import {deleteDeviceTopologyById} from "@/api/deviceCenter/deviceList";
import {CirclePlus, Delete, Refresh, Search} from "@element-plus/icons-vue";

export default {
  name: "TopologyList",
  components:{AddTopologyDialog},
  props: {
    list: {
      type: Array,
      default: () => {
        return []
      }
    }
  },
  setup(props) {

    const {emit} = getCurrentInstance();
    const that = reactive({
      activeNodeId: "",
      tableMaxHeight: 280,
      addTopologyVisible: false,
      CirclePlus, Delete, Refresh, Search,
    })

    const clickAddBut = (nodeId)=>{
      that.activeNodeId = nodeId;
      that.addTopologyVisible= true;
    }

    const clickOperateBut = (operate,row)=> {
      if(operate === 1){
        ElMessageBox.confirm(`确定删除（<span class="deleteName">${ row.nodeName }</span>）吗？`, "删除提示", {
          dangerouslyUseHTMLString: true,confirmButtonText: '确定', cancelButtonText: '取消',
          customClass: "deleteMsgBoxClass", showClose:false,type: 'warning',
        }).then(() => {
          deleteDeviceTopologyById({ id: row.id }).then(()=>{
            emit("changeEvent");
            ElMessage({ type: "success", showClose: true, message: "删除成功！" });
          })
        }).catch(() => {
          console.log("取消删除！");
        });
      }
    }

    const changeEvent = ()=>{
      emit("changeEvent");
    }

    return {...toRefs(that), clickOperateBut, clickAddBut, changeEvent }
  }
}
</script>

<style scoped lang="scss">
.topologyList{
  height: 100%;
  overflow-y: auto;

  .table_list{
    margin-bottom: 16px;
  }
}
</style>
