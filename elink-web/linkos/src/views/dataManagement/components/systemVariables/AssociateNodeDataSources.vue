<template>
  <div class="associateDataSources">

    <div class="header-form">
      <el-form :model="formInline" inline>
        <el-form-item>
          <el-input v-model="formInline.keyword" :suffix-icon="Search" clearable placeholder="请输入关键字">
            <template #prepend>
              <el-select v-model="formInline.keywordType" placeholder="请选择" style="width: 100px">
                <el-option v-for="item in keyTypeArray" :key="item.id" :label="item.name" :value="item.id"></el-option>
              </el-select>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item label="存储策略：">
          <el-select v-model="formInline.strategyType" clearable placeholder="请选择存储策略">
            <el-option v-for="item in strategyTypeArray" :key="item.id" :label="item.name" :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button class="whiteFontButtons" @click="listArray('refresh')">查询</el-button>
          <el-button class="blackFontButtons" @click="resetForm('resetPage')">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div ref="tableCenterRef"  v-resize="setTableMaxHeight" class="tableCenter">
      <el-table :data="list" :max-height="tableMaxHeight" v-loading="listLoading">
        <el-table-column align="center" label="关联">
          <template #default="{ row }">
            <el-radio-group v-model="activeBindNodeId" @change="changeNodeId">
              <el-radio :label="row.id" size="large"><em></em></el-radio>
            </el-radio-group>
          </template>
        </el-table-column>
        <el-table-column align="center" label="序号" width="60">
          <template #default="scope">{{ scope.$index + 1 + (currentPage - 1) * pageNum }}</template>
        </el-table-column>
        <el-table-column align="center" label="节点名称" show-overflow-tooltip>
          <template #default="{ row }">{{ $filters.moreData(row.nodeName) }}</template>
        </el-table-column>
        <el-table-column align="center" label="节点标识" show-overflow-tooltip>
          <template #default="{ row }">{{ $filters.moreData(row.nodeCode) }}</template>
        </el-table-column>
        <el-table-column align="center" label="计算周期">
          <template #default="{ row }">{{ $filters.moreData(row.countPeriod) }}</template>
        </el-table-column>
        <el-table-column align="center" label="单位">
          <template #default="{ row }">{{ $filters.moreData(row.unit) }}</template>
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
import {findNotComputeNodeByPage} from "@/api/dataManagement/systemVariables";
import {getCurrentInstance, reactive, toRefs, onMounted, ref, defineComponent} from "vue";

export default defineComponent({
  name: "AssociateNodeDataSources",
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
      oldFormInline: {},
      formInline: {keywordType: 1},

      list: [],
      pageNum: 20,
      totalNumber: 0,
      currentPage: 1,
      listLoading: false,
      tableMaxHeight: 300,
      activeBindNodeId: "",

      keyTypeArray: [{id: 1, name: "名称"}, {id: 2, name: "编码"}],
      strategyTypeArray: [{id: 1, name: "每次存储"}, {id: 2, name: "变化存储"}, {id: 3, name: "不存储"}],
    })

    const listArray = (operateType) => {
      that.listLoading = true;
      if (operateType === "resetPage" || operateType === "refresh") that.currentPage = 1;
      findNotComputeNodeByPage({...props.formDialog, ...that.formInline, page: that.currentPage, size: that.pageNum}).then(res => {
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
      let findItem = that.list.find(item => item.id === that.activeBindNodeId);
      if (findItem) {
        emit("changEvent", {type: "selNodeId", nodeId: findItem.id, storageId: findItem.storageId});
      }
    }

    const resetForm = () => {
      that.formInline = JSON.parse(JSON.stringify(that.oldFormInline));
      listArray("refresh");
    }

    // 初始化表格高度
    const tableCenterRef = ref(null);
    const setTableMaxHeight = () => {
      that.tableMaxHeight = tableCenterRef.value.offsetHeight;
    };

    onMounted(() => {
      that.oldFormInline = JSON.parse(JSON.stringify(that.formInline));
      listArray();
    })

    return {...toRefs(that), listArray, changeNodeId, tableCenterRef, setTableMaxHeight, resetForm}
  }
})
</script>

<style lang="scss" scoped>
.associateDataSources {
  height: 100%;
  display: flex;
  flex-direction: column;
  padding-top: 12px;
  box-sizing: border-box;

  .header-form {
    padding: 0;
  }

  .tableCenter {
    flex: 1;
    height: 2px;
    margin-bottom: 12px;
  }
}
</style>
