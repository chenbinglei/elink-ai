<template>
  <div class="content_table" v-resize="setTableMaxHeight">

    <div class="header-form" ref="headerFormRef">
      <el-form :model="formInline" inline>
        <el-form-item label="关键字：">
          <el-input v-model="formInline.functionName" clearable placeholder="请输入关键词搜索">
            <template #append>
              <el-button :icon="Search" @click="listArray('resetPage')"></el-button>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item style="float: right">
          <el-button :icon="CirclePlus" class="whiteFontButtons" @click="clickAddBut">添加标准功能</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="tableContent" v-loading="listLoading">
      <div class="tableCenter">
        <el-table :data="list" :max-height="tableMaxHeight" border stripe @selection-change="handleSelectionChange">
          <el-table-column align="center" type="selection" width="55"></el-table-column>
          <el-table-column align="center" label="序号" type="index" width="80">
            <template #default="scope">{{ scope.$index + 1 + (currentPage - 1) * pageNum }}</template>
          </el-table-column>
          <el-table-column align="center" label="功能名称">
            <template #default="{ row }">{{ $filters.moreData(row.functionName) }}</template>
          </el-table-column>
          <el-table-column align="center" label="标识符">
            <template #default="{ row }">{{ $filters.moreData(row.functionLogo) }}</template>
          </el-table-column>
          <el-table-column align="center" label="数据类型">
            <template #default="{ row }">{{ $filters.dataType(row.dataType)}}</template>
          </el-table-column>
<!--          <el-table-column align="center" label="数据值定义" show-overflow-tooltip>-->
<!--            <template #default="{ row }">-->
<!--              <data-value-definition :dataType="row.dataType" :valueRange="row.valueRange" :dataObject="row.dataObject"></data-value-definition>-->
<!--            </template>-->
<!--          </el-table-column>-->
          <el-table-column align="center" label="功能类型">
            <template #default="{ row }">{{ $filters.functionType(row.functionType)}}</template>
          </el-table-column>
          <el-table-column align="center" label="操作">
            <template #default="{ row }">
              <el-link :underline="false" @click="clickOperateBut(1, row)">查看</el-link>
              <span class="split_line">|</span>
              <el-link :underline="false" @click="clickOperateBut(3, row)">数据定义</el-link>
              <span class="split_line">|</span>
              <el-link :underline="false" type="danger" @click="clickOperateBut(2, row)">删除</el-link>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <div class="tablePagination">
        <Pagination v-model:pageSize="pageNum" :totalNumber="totalNumber" v-model:currentPage="currentPage" :paginationButArray="paginationButArray" @pageChange="listArray" @paginationFunction="paginationFunction" />
      </div>
    </div>

    <add-standard-features v-if="addStandardFeaturesVisible" v-model:isVisible="addStandardFeaturesVisible" :activeModelId="activeModelId" @changeEvent="listArray('resetPage')"></add-standard-features>
    <AddStandardFunDialog v-if="addStandardFunVisible" v-model:isVisible="addStandardFunVisible" :titleName="titleName" :isFunctionType="isFunctionType"
                          :activeFunctionInfo="activeFunctionInfo" @changeEvent="listArray"></AddStandardFunDialog>
  </div>
</template>

<script lang="ts">
import { AddStandardFeatures } from "./component";
import {ElMessage, ElMessageBox} from "element-plus";
import {onMounted, reactive, ref, toRefs, defineComponent} from "vue";
import {CirclePlus, Delete, Refresh, Search} from "@element-plus/icons-vue";
import {findModelFunctionListByModelId,modelBindFunctionData} from "@/api/modelCenter/modelManagement";
import AddStandardFunDialog from "@/views/modelCenter/component/masterDataManagement/component/StandardFunction/AddStandardFunDialog.vue";

export default defineComponent({
  name: "FunctionDefine",
  components:{AddStandardFeatures,AddStandardFunDialog},
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
      pageNum: 20,
      formInline: {},
      currentPage: 1,
      totalNumber: 0,
      listLoading: false, // 表格加载
      tableMaxHeight: 300,
      CirclePlus, Delete, Refresh, Search,

      titleName: "",
      activeFunctionInfo: {},
      selectableArrayIds: [],

      isFunctionType: 1, // 1: 查看详情  2:  修改数据定义
      addStandardFunVisible: false,
      addStandardFeaturesVisible: false,
      paginationButArray: [
        {buttonName: "批量删除", buttonType: "batchDelete", buttonIcon: "Delete"},
      ],
    })


    const listArray = (reset)=>{
      that.listLoading = true;
      if (reset === "resetPage") that.currentPage = 1;
      findModelFunctionListByModelId({ modelId: props.activeModelId, page: that.currentPage, size: that.pageNum, ...that.formInline }).then(res=>{
        that.totalNumber = res.data.totalSize;
        that.list = res.data.items;
        that.listLoading = false;
      }).catch((err) => {
        that.listLoading = false;
        if (err && err.code === 88886)return
        that.totalNumber = 0;
        that.list = [];
      });
    }

    const clickAddBut = ()=>{
      that.addStandardFeaturesVisible = true;
    }

    const clickOperateBut = (operate,row)=>{

      if(operate === 1){
        that.titleName = "详情";
        that.isFunctionType = 3;
        that.activeFunctionInfo = JSON.parse(JSON.stringify(row));
        that.addStandardFunVisible = true;
      }

      if(operate === 2){
        ElMessageBox.confirm(`确定删除（<span class="deleteName">${ row.functionName }</span>）吗？`, "删除提示", {
          dangerouslyUseHTMLString: true,confirmButtonText: '确定', cancelButtonText: '取消',
          customClass: "deleteMsgBoxClass", showClose:false,type: 'warning',closeOnClickModal: false,
          beforeClose: (action, instance, done)=>{
            if (action === 'confirm') {
              instance.confirmButtonLoading = true;
              instance.confirmButtonText = '正在删除...';

              let functionMap = {};
              functionMap[row.functionId] = 0;
              modelBindFunctionData({ functionMap: functionMap,type: 2,modelId: props.activeModelId}).then(() => {
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
          listArray("resetPage");
          ElMessage({ type: "success", message: "删除成功!", showClose: true });
        }).catch(() => {
          console.log("取消删除！");
        });
      }

      if(operate === 3){
        console.log('shuju',row);
        that.isFunctionType = 4;
        that.titleName = "修改数据定义";
        that.activeFunctionInfo = JSON.parse(JSON.stringify(row));
        that.addStandardFunVisible = true;
      }
    }

    const handleSelectionChange = (val)=> {
      that.selectableArrayIds = val;
    }

    const paginationFunction = (data)=>{
      if (data.type === "batchDelete") {
        if (!that.selectableArrayIds.length) {
          ElMessage({ type: "error", message: "请选择需要操作的数据!", showClose: true });
          return
        }

        ElMessageBox.confirm(`您确定要删除设备数据吗？`, "删除提示", {
          dangerouslyUseHTMLString: true,confirmButtonText: '确定', cancelButtonText: '取消',
          customClass: "elMessageBoxWarning", showClose:false,type: 'warning',
          beforeClose: (action, instance, done)=>{
            if (action === 'confirm') {
              instance.confirmButtonLoading = true;
              instance.confirmButtonText = '正在删除...';

              let functionMap = {};
              for (let i = 0; i < that.selectableArrayIds.length; i++) functionMap[that.selectableArrayIds[i].id] = i;
              modelBindFunctionData({functionMap: functionMap,type: 2,modelId: props.activeModelId}).then(() => {
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
          listArray("resetPage");
          ElMessage({ type: "success", message: "删除成功!", showClose: true });
        }).catch(() => {
          console.log("取消操作！");
        });
      }
    }

    const clickDisplaySet = ()=>{
      that.displaySettingVisible = true;
    }

    // 初始化表格高度
    const headerFormRef = ref(null);
    const setTableMaxHeight = () => {
      let headerFormHeight = headerFormRef.value.offsetHeight;
      that.tableMaxHeight = props.componentMaxHeight - headerFormHeight - 115;
    }

    onMounted(()=>{
      listArray();
    })

    return {...toRefs(that), headerFormRef, setTableMaxHeight, listArray, clickDisplaySet, paginationFunction, handleSelectionChange, clickAddBut, clickOperateBut}
  }
})
</script>


<style scoped lang="scss">
.content_table{
  height: 100%;
  display: flex;
  flex-direction: column;

  .header-form,.tableContent{
    padding: 0;
    border-top: none;
  }
}
</style>
