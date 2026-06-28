<template>
  <div class="app-container" v-resize="setTableMaxHeight">
    <div class="app-container-right">
      <div class="header-form" ref="headerFormRef">
        <el-form inline :model="formInline">
          <el-form-item label="关键词：">
            <el-input v-model="formInline.keyword" :suffix-icon="Search" placeholder="请输入关键字" @keyup.enter="listArray">
              <template #prepend>
                <el-select v-model="formInline.keywordType" placeholder="请选择" style="width: 100px">
                  <el-option v-for="item in keyTypeArray" :key="item.id" :label="item.name" :value="item.id"></el-option>
                </el-select>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item label="变量类型：">
            <el-select v-model="formInline.varType" clearable placeholder="请选择变量类型">
              <el-option v-for="item in varTypeArray" :key="item.id" :label="item.name" :value="item.id"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button class="whiteFontButtons" @click="listArray">查询</el-button>
            <el-button class="blackFontButtons" @click="resetForm">重置</el-button>
          </el-form-item>
          <el-form-item style="float: right">
            <el-button :icon="CirclePlus" class="whiteFontButtons" :disabled="isAddButtonClick" @click="clickAddBut">新建变量</el-button>
          </el-form-item>
        </el-form>
      </div>
      <div class="tableContent">
        <div class="tableCenter" v-loading="listLoading">
          <el-table ref="multipleTableRef" :data="list" :max-height="tableMaxHeight" stripe>
            <el-table-column fixed="left" label="序号" width="100">
              <template #default="scope">{{ scope.$index + 1 + (currentPage - 1) * pageNum }}</template>
            </el-table-column>
            <el-table-column label="变量名称" show-overflow-tooltip>
              <template #default="{ row }">{{ $filters.moreData(row.varName) }}</template>
            </el-table-column>
            <el-table-column label="变量标识">
              <template #default="{ row }">{{ $filters.moreData(row.varCode) }}</template>
            </el-table-column>
            <el-table-column label="类型">
              <template #default="{ row }">{{ $filters.variableType(row.varType) }}</template>
            </el-table-column>
            <el-table-column label="数据类型">
              <template #default="{ row }">{{ $filters.varDataSource(row.dataSource) }}</template>
            </el-table-column>
            <el-table-column label="数据映射">
              <template #default="{ row }">
                <div class="pointer">
                  <el-tag @click="clickOperate(4,row)">{{ $filters.numberNull(row.exampleNumber) }}</el-tag>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="描述" show-overflow-tooltip>
              <template #default="{ row }">{{ $filters.moreData(row.remark) }}</template>
            </el-table-column>
            <el-table-column label="最后更新" show-overflow-tooltip>
              <template #default="{ row }">
                <span>{{ $filters.moreData(row.upadteUserName) }}</span>
                <span>,</span>
                <span>{{ $filters.moreData(row.upadteTime) }}</span>
              </template>
            </el-table-column>
            <el-table-column align="center" label="操作">
              <template #default="{ row }">
                <el-link :underline="false" @click="clickOperate(1, row)">编辑</el-link>
                <span class="split_line">|</span>
                <el-link :underline="false" @click="clickOperate(2, row)">数据映射</el-link>
                <span class="split_line">|</span>
                <el-link :underline="false" type="danger" @click="clickOperate(3, row)">删除</el-link>
              </template>
            </el-table-column>
          </el-table>
        </div>
        <div class="tablePagination">
          <Pagination v-model:currentPage="currentPage" v-model:pageSize="pageNum" :totalNumber="totalNumber" @pageChange="listArray"/>
        </div>
      </div>

      <AddVariableDialog v-if="addVariableVisible" v-model:isVisible="addVariableVisible" :activeVarInfo="activeVarInfo" :titleName="titleName" @changEvent="listArray('refresh')" />
      <AddVarInstanceDialog v-if="addVarInstanceVisible" v-model:isVisible="addVarInstanceVisible" :activeVarInfo="activeVarInfo" :titleName="titleName" @changEvent="listArray('refresh')" />
      <LookVarAssociatedInstance v-if="associatedInstanceVisible" v-model:isVisible="associatedInstanceVisible" :variableExampleInfoList="variableExampleInfoList" @changEvent="listArray" />
    </div>
  </div>
</template>

<script lang="ts">
import {operateButtonIsClick} from "@/utils";
import {ElMessage, ElMessageBox} from "element-plus";
import {CirclePlus, Search} from '@element-plus/icons-vue';
import {computed, onMounted, reactive, ref, toRefs} from "vue";
import {deleteSystemVariableById, findSystemVariableListByPage} from "@/api/dataManagement/systemVariables";
import {AddVariableDialog, AddVarInstanceDialog, LookVarAssociatedInstance} from "@/views/dataManagement/components";

export default {
  name: "systemVariables",
  components: {AddVariableDialog, AddVarInstanceDialog, LookVarAssociatedInstance},
  props: {
    contentMaxHeight:{
      type:Number,
      default: 520
    }
  },
  setup(props) {

    const isAddButtonClick = computed(()=>{
      return operateButtonIsClick('/crontab/systemVariable/saveOrUpdateSystemVariable')
    })

    const that = reactive({
      Search,
      CirclePlus,
      oldFormInline: {},
      formInline: {keywordType: 1},

      list: [],
      pageNum: 20,
      totalNumber: 0,
      currentPage: 1,
      listLoading: false,
      tableMaxHeight: 300,

      activeVarInfo: {},
      titleName: "新建系统变量",
      addVariableVisible: false, // 新建变量弹窗显示
      addVarInstanceVisible: false,

      variableExampleInfoList: [],
      associatedInstanceVisible: false,
      keyTypeArray: [{id: 1, name: "变量名称"}, {id: 2, name: "变量标识"}],
      varTypeArray: [{id: undefined, name: "全部"}, {id: 2, name: "场站变量"}, {id: 1, name: "设备变量"}],
    })

    const listArray = (operateType) => {
      that.listLoading = true;
      if (operateType === "resetPage" || operateType === "refresh") that.currentPage = 1;
      findSystemVariableListByPage({...that.formInline, page: that.currentPage, size: that.pageNum}).then(res => {
        that.listLoading = false;
        that.list = res.data.items;
        that.totalNumber = res.data.totalSize;
        if (operateType === "resetPage") ElMessage({type: "success", message: "重置成功！", showClose: true});
      }).catch((error) => {
        that.listLoading = false;
        if (error && error.code === 88886) return
        that.totalNumber = 0;
        that.list = [];
      });
    }

    // 新增变量
    const clickAddBut = () => {
      that.activeVarInfo = {};
      that.titleName = "新建系统变量";
      that.addVariableVisible = true;
    }

    // 重置
    const resetForm = () => {
      that.formInline = JSON.parse(JSON.stringify(that.oldFormInline));
      listArray("resetPage");
    }

    // 操作
    const clickOperate = (operateType, row) => {

      if (operateType === 1) {
        that.titleName = "编辑系统变量";
        that.activeVarInfo = JSON.parse(JSON.stringify(row));
        that.addVariableVisible = true;
      }

      if (operateType === 2) {
        that.titleName = "关联映射";
        that.activeVarInfo = {varId: row.id, varType: row.varType, dataSource: row.dataSource};
        that.addVarInstanceVisible = true;
      }

      if (operateType === 3) {
        ElMessageBox.confirm(`确定删除（${row.varName}）吗？`, "提示", {
          dangerouslyUseHTMLString: true, confirmButtonText: '确定', cancelButtonText: '取消',
          customClass: "elMessageBoxWarning", showClose: false, type: 'warning',closeOnClickModal: false,
          beforeClose: (action, instance, done)=>{
            if (action === 'confirm') {
              instance.confirmButtonLoading = true;
              instance.confirmButtonText = '正在删除...';
              deleteSystemVariableById({id: row.id}).then(() => {
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
          listArray("refresh");
          ElMessage({type: "success", message: "删除成功", showClose: true});
        }).catch(() => {
          console.log("取消操作！");
        })
      }

      if (operateType === 4) {
        that.variableExampleInfoList = row.variableExampleInfoList;
        that.associatedInstanceVisible = true;
      }
    }

    // 初始化表格高度
    const headerFormRef = ref(null);
    const setTableMaxHeight = () => {
      let headerFormHeight = headerFormRef.value.offsetHeight;
      that.tableMaxHeight = props.contentMaxHeight - headerFormHeight - 110;
    }

    onMounted(() => {
      that.oldFormInline = JSON.parse(JSON.stringify(that.formInline));
      listArray();
    })

    return {...toRefs(that), isAddButtonClick, headerFormRef, setTableMaxHeight, clickAddBut, listArray, resetForm, clickOperate}
  }
}
</script>

<style lang="scss" scoped>
.header-form {
  :deep(.el-select) {
    min-width: 160px;
    .el-select__wrapper {
      width: 180px;
      min-height: 32px;
    }
    .el-select__selected-item.el-select__placeholder {
      z-index: 1 !important;
      position: absolute !important;
      opacity: 1 !important;
      &.is-transparent { opacity: 1 !important; }
      span { color: #a8abb2 !important; font-size: 14px !important; }
    }
  }
  .el-input {
    :deep(.el-select) {
      min-width: 120px;
      .el-select__wrapper { width: 140px; }
    }
  }
}
</style>
