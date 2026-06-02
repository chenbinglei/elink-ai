<template>
  <div class="content_table" v-resize="setTableMaxHeight">

    <div class="header-form" ref="headerFormRef">
      <el-form :model="formInline" inline>
        <el-form-item style="float: right">
          <el-button :icon="Folder" class="blackFontButtons" @click="clickAddBut(3)">导出故障码</el-button>
          <el-button :icon="Folder" class="whiteFontButtons" @click="clickAddBut(2)">批量导入故障码</el-button>
          <el-button :icon="CirclePlus" class="whiteFontButtons" @click="clickAddBut(1)">添加故障码</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div v-loading="listLoading" class="tableContent">
      <div class="tableCenter">
        <el-table :data="list" :max-height="tableMaxHeight" border stripe>
          <el-table-column align="center" label="序号" type="index" width="80"></el-table-column>
          <el-table-column align="center" label="故障码">
            <template #default="{ row }">{{ $filters.moreData(row.faultCode) }}</template>
          </el-table-column>
          <el-table-column align="center" label="事件名称">
            <template #default="{ row }">{{ $filters.moreData(row.eventName) }}</template>
          </el-table-column>
          <el-table-column align="center" label="事件级别">
            <template #default="{ row }">{{ $filters.eventLevel(row.eventLevel) }}</template>
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

    <AddFaultDefinitionDialog v-if="addFaultDefinitionVisible" v-model:isVisible="addFaultDefinitionVisible" :titleName="titleName" :formDialog="formDialog" @changeEvent="queryPileFaultListByModelId" />
    <BatchAddFaultDefinitionDialog v-if="batchAddFaultDefinitionVisible" v-model:isVisible="batchAddFaultDefinitionVisible" :titleName="titleName" :formDialog="formDialog" @changeEvent="queryPileFaultListByModelId" />
  </div>
</template>

<script>
import {getNowDateAll} from "@/utils/dateTime";
import {ElMessage, ElMessageBox} from "element-plus";
import {onMounted, reactive, ref, toRefs} from "vue";
import {Folder,CirclePlus} from "@element-plus/icons-vue";
import {exportCustomExcel} from "@/common/common/exportExcel";
import {AddFaultDefinitionDialog,BatchAddFaultDefinitionDialog} from "./component";
import {deletePileFaultById, findPileFaultListByModelId} from "@/api/modelCenter/modelManagement";

export default {
  name: "FaultDefinition",
  components:{AddFaultDefinitionDialog,BatchAddFaultDefinitionDialog},
  props: {
    activeModelId: {
      type: [String, Number],
      default: ''
    },
    activeModelName: {
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
      Folder,
      CirclePlus,
      formInline: {},
      listLoading: false, // 表格加载
      tableMaxHeight: 300,

      formDialog: {},
      titleName: "添加故障码",
      addFaultDefinitionVisible: false,
      batchAddFaultDefinitionVisible: false,
    })

    // 根据模型id查询模型拓扑节点列表
    const queryPileFaultListByModelId = ()=>{
      that.listLoading = true;
      findPileFaultListByModelId({ modelId:props.activeModelId,...that.formInline }).then(res=>{
        that.list = res.data;
        that.listLoading = false;
      }).catch(()=>{
        that.listLoading = false;
      })
    }

    const clickAddBut = (tabsIndex) => {
      if(tabsIndex === 1){
        that.titleName = "添加故障码";
        that.formDialog = { modelId: props.activeModelId };
        that.addFaultDefinitionVisible = true;
      }

      if(tabsIndex === 2){
        that.titleName = "批量添加故障码";
        that.formDialog = { modelId: props.activeModelId };
        that.batchAddFaultDefinitionVisible = true;
      }

      if(tabsIndex === 3){
        let tableHeader = [
          {width: 20, key: "faultCode", name: "故障码"},
          {width: 20, key: "eventName", name: "事件名称"},
          {width: 20, key: "eventLevel", name: "事件级别",filterName: "eventLevel"},
        ];
        exportCustomExcel(tableHeader,that.list,`${ props.activeModelName }-故障定义_${ getNowDateAll() }`);
      }

    }

    const clickOperateBut = (operate,row)=>{

      if(operate === 1){
        that.titleName = "编辑故障码";
        that.formDialog = JSON.parse(JSON.stringify(row));
        that.addFaultDefinitionVisible = true;
      }

      if(operate === 2){
        ElMessageBox.confirm(`确定删除（<span class="deleteName">${ row.eventName }</span>）吗？`, "删除提示", {
          dangerouslyUseHTMLString: true,confirmButtonText: '确定', cancelButtonText: '取消',
          customClass: "deleteMsgBoxClass", showClose:false,type: 'warning',closeOnClickModal: false,
          beforeClose: (action, instance, done)=>{
            if (action === 'confirm') {
              instance.confirmButtonLoading = true;
              instance.confirmButtonText = '正在删除...';
              deletePileFaultById({ id: row.id,modelId: props.activeModelId }).then(() => {
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
          queryPileFaultListByModelId();
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
      queryPileFaultListByModelId();
    })

    return {...toRefs(that), clickAddBut, headerFormRef, setTableMaxHeight, queryPileFaultListByModelId,clickOperateBut}
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
