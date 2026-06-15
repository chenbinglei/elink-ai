<template>
  <div class="content_table">

    <div class="header-form">
      <el-form :model="formInline" inline @submit.native.prevent>
        <el-input v-model="formInline.functionName" clearable placeholder="请输入关键词搜索" style="width: 320px">
          <template #append>
            <el-button :icon="Search" @click="listArray('refresh')"></el-button>
          </template>
        </el-input>
      </el-form>
    </div>

    <div ref="tableCenterRef"  v-resize="setTableMaxHeight" class="tableCenter">
      <el-table :data="list" :max-height="tableMaxHeight" v-loading="listLoading">
        <el-table-column align="center" label="关联">
          <template #default="{ row }">
            <el-radio-group v-model="activeBindFunctionId" @change="changeNodeId">
              <el-radio :label="row.functionId" size="large"><em></em></el-radio>
            </el-radio-group>
          </template>
        </el-table-column>
        <el-table-column align="center" label="序号" width="60">
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
      </el-table>
    </div>
    <div class="tablePagination">
      <Pagination v-model:currentPage="currentPage" v-model:pageSize="pageNum" :totalNumber="totalNumber" @pageChange="listArray"/>
    </div>
  </div>
</template>

<script lang="ts">
import {ElMessage} from "element-plus";
import {Search} from '@element-plus/icons-vue';
import {findModelFunctionListByPage} from "@/api/dataManagement/systemVariables";
import {getCurrentInstance, reactive, toRefs, onMounted, ref, defineComponent} from "vue";

export default defineComponent({
  name: "AssociateFunctionDataSources",
  props: {
    formDialog: {
      type: Object,
      default: () => {
        return {}
      }
    }
  },
  setup(props) {
    const {emit} = getCurrentInstance();

    const that = reactive({
      Search,
      formInline: {},

      list: [],
      pageNum: 20,
      totalNumber: 0,
      currentPage: 1,
      listLoading: false,
      tableMaxHeight: 300,
      activeBindFunctionId: "",
    })

    // 分页查询未被关联的模型功能点
    const listArray = (operateType) => {
      that.listLoading = true;
      if (operateType === "resetPage" || operateType === "refresh") that.currentPage = 1;
      let formDialog = JSON.parse(JSON.stringify(props.formDialog));
      formDialog.modelId = formDialog.deviceId;
      findModelFunctionListByPage({...formDialog, ...that.formInline, page: that.currentPage, size: that.pageNum}).then(res => {
        that.listLoading = false;
        that.list = res.data.items;
        that.totalNumber = res.data.totalSize;
        if (operateType === "resetPage") ElMessage({type: "success", message: "重置成功", showClose: true});
      }).catch((error) => {
        that.listLoading = false;
        if (error && error.code === 88886) return
        that.totalNumber = 0;
        that.list = [];
      });
    }

    const changeNodeId = () => {
      emit("changEvent", {type: "selFunctionId", functionId: that.activeBindFunctionId });
    }

    // 初始化表格高度
    const tableCenterRef = ref(null);
    const setTableMaxHeight = () => {
      that.tableMaxHeight = tableCenterRef.value.offsetHeight;
    };

    onMounted(() => {
      listArray();
    })

    return {...toRefs(that), listArray, changeNodeId, tableCenterRef, setTableMaxHeight}
  }
})
</script>

<style lang="scss" scoped>
.content_table {
  height: 100%;
  display: flex;
  flex-direction: column;
  padding-top: 12px;
  box-sizing: border-box;

  .header-form {
    padding: 0;
    margin-bottom: 12px;

    .el-input{
      width: fit-content;
    }
  }

  .tableCenter {
    flex: 1;
    height: 2px;
    margin-bottom: 12px;
  }
}
</style>
