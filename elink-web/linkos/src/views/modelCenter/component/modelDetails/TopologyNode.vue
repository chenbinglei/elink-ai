<template>
  <div class="content_table" v-resize="setTableMaxHeight">

    <div class="header-form" ref="headerFormRef">
      <el-form :model="formInline" inline>
        <el-form-item style="float: right">
          <el-button :icon="CirclePlus" class="whiteFontButtons" @click="clickAddBut">添加拓扑节点</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div v-loading="listLoading" class="tableContent">
      <div class="tableCenter">
        <el-table :data="list" :max-height="tableMaxHeight" border stripe>
          <el-table-column align="center" label="序号" type="index" width="80"></el-table-column>
          <el-table-column align="center" label="ID">
            <template #default="{ row }">{{ $filters.moreData(row.id) }}</template>
          </el-table-column>
          <el-table-column align="center" label="节点名称">
            <template #default="{ row }">{{ $filters.moreData(row.nodeName) }}</template>
          </el-table-column>
          <el-table-column align="center" label="操作">
            <template #default="{ row }">
              <el-link :underline="false" @click="clickOperateBut(1, row)">编辑</el-link>
              <span class="split_line">|</span>
              <el-link :underline="false" type="danger" @click="clickOperateBut(2, row)">删除</el-link>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>

    <add-model-topology-node v-if="addModelTopologyNodeVisible" v-model:isVisible="addModelTopologyNodeVisible" :titleName="titleName"
                             :formDialog="formDialog" @changeEvent="queryModelTopologyListByModelId"></add-model-topology-node>

  </div>
</template>

<script lang="ts">
import {AddModelTopologyNode} from "./component";
import {CirclePlus} from "@element-plus/icons-vue";
import {ElMessage, ElMessageBox} from "element-plus";
import {onMounted, reactive, ref, toRefs} from "vue";
import {deleteModelTopologyById, findModelTopologyListByModelId,} from "@/api/modelCenter/modelManagement";

export default {
  name: "TopologyNode",
  components:{AddModelTopologyNode},
  props: {
    activeModelId: {
      type: [String, Number],
      default: ''
    },
    componentMaxHeight: {
      type: [String, Number],
      default: 320
    }
  },
  setup(props) {

    const that = reactive({
      list: [],
      CirclePlus,
      formInline: {},
      listLoading: false, // 表格加载
      tableMaxHeight: 300,

      formDialog: {},
      titleName: "添加节点",
      addModelTopologyNodeVisible: false,
    })

    // 根据模型id查询模型拓扑节点列表
    const queryModelTopologyListByModelId = ()=>{
      that.listLoading = true;
      findModelTopologyListByModelId({ modelId:props.activeModelId,...that.formInline }).then(res=>{
        that.list = res.data;
        that.listLoading = false;
      }).catch(()=>{
        that.listLoading = false;
      })
    }

    const clickAddBut = () => {
      that.titleName = "添加节点";
      that.formDialog = { nodeName: "", modelId: props.activeModelId };
      that.addModelTopologyNodeVisible = true;
    }

    const clickOperateBut = (operate,row)=>{

      if(operate === 1){
        that.formDialog = row;
        that.titleName = "编辑节点";
        that.addModelTopologyNodeVisible = true;
      }

      if(operate === 2){
        ElMessageBox.confirm(`确定删除（<span class="deleteName">${ row.nodeName }</span>）吗？`, "删除提示", {
          dangerouslyUseHTMLString: true,confirmButtonText: '确定', cancelButtonText: '取消',
          customClass: "deleteMsgBoxClass", showClose:false,type: 'warning',closeOnClickModal: false,
          beforeClose: (action, instance, done)=>{
            if (action === 'confirm') {
              instance.confirmButtonLoading = true;
              instance.confirmButtonText = '正在删除...';
              deleteModelTopologyById({ id: row.id,modelId: props.activeModelId }).then(() => {
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
          queryModelTopologyListByModelId();
          ElMessage({ type: "success", message: "删除成功!", showClose: true });
        }).catch(() => {
          console.log("取消删除！");
        });
      }
    }

    // 初始化表格高度
    const headerFormRef = ref(null);
    const setTableMaxHeight = () => {
      let headerFormHeight = headerFormRef.value.offsetHeight;
      that.tableMaxHeight = props.componentMaxHeight - headerFormHeight - 100;
    }

    onMounted(()=>{
      queryModelTopologyListByModelId();
    })

    return {...toRefs(that), clickAddBut, headerFormRef, setTableMaxHeight, queryModelTopologyListByModelId,clickOperateBut}
  }
}
</script>

<style lang="scss" scoped>
.content_table{
  height: 100%;
  display: flex;
  flex-direction: column;

  .header-form,.tableContent{
    padding: 0;
    border-top: none;
  }

  .tableCenter{
    max-height: 100% !important;
  }
}
</style>
