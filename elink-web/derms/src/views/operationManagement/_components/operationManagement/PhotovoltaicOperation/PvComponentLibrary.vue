<template>
  <div class="app-container-right">

    <div class="header-form content_border">
      <el-form :model="formInline" inline>
        <el-row :gutter="16">
          <el-col :xs="24" :sm="12" :md="8" :xl="6">
            <el-form-item label="组件厂家：">
              <el-input v-model="formInline.moduleFactory" placeholder="请输入组件厂家" clearable></el-input>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :xl="6">
            <el-form-item label="组件型号：">
              <el-input v-model="formInline.moduleModel" placeholder="请输入组件型号" clearable></el-input>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :xl="6">
            <el-form-item label="组件类型：">
              <el-select v-model="formInline.moduleType" clearable filterable placeholder="全部">
                <el-option v-for="item in moduleTypeArray" :key="item.id" :label="item.name" :value="item.id"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :xl="6">
            <el-form-item>
              <el-button :icon="Search" type="primary" @click="listArray('refresh')">查询</el-button>
              <el-button :icon="RefreshRight" @click="clickResetForm('resetPage')">重置</el-button>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </div>

    <div class="tableContent content_border" ref="tableContentRef">
      <TableHeaderTitle title="组件库列表">
        <template #content>
          <div class="table_top_content">
            <el-button :icon="Plus" @click="clickOperateBut(1)">新增组件库</el-button>
            <el-button :icon="Delete" @click="clickOperateBut(2)">批量删除组件库</el-button>
            <el-button :icon="Folder" @click="clickOperateBut(5)">导入</el-button>
            <el-button :disabled="exportLoading" :icon="Folder" :loading="exportLoading" @click="listArray('exportTable')">导出</el-button>
          </div>
        </template>
      </TableHeaderTitle>
      <div class="tableCenter" ref="tableCenterRef" v-resize="setTableMaxHeight">
        <el-table ref="multipleTableRef" v-loading="listLoading" :data="list" stripe :max-height="tableMaxHeight" :row-key="getRowKeys" @selection-change="handleSelectionChange">
          <el-table-column align="center" reserve-selection type="selection" width="45"></el-table-column>
          <el-table-column label="序号" type="index" width="80" fixed="left">
            <template #default="{ $index }">{{ $index + 1 + (currentPage - 1) * pageNum }}</template>
          </el-table-column>
          <el-table-column label="组件厂家" fixed="left" show-overflow-tooltip>
            <template #default="{ row }">{{ $filters.moreData(row.moduleFactory) }}</template>
          </el-table-column>
          <el-table-column label="组件型号" show-overflow-tooltip>
            <template #default="{ row }">{{ $filters.moreData(row.moduleModel) }}</template>
          </el-table-column>
          <el-table-column label="组件类型">
            <template #default="{ row }">{{ $filters.moduleType(row.moduleType) }}</template>
          </el-table-column>
          <el-table-column label="组件功率">
            <template #default="{ row }">{{ $filters.moreData(row.maxPower) }}</template>
          </el-table-column>
          <el-table-column label="最后修改时间">
            <template #default="{ row }">{{ $filters.moreData(row.updateTime) }}</template>
          </el-table-column>
          <el-table-column label="创建者">
            <template #default="{ row }">{{ $filters.moreData(row.createName) }}</template>
          </el-table-column>
          <el-table-column label="操作" width="180" fixed="right">
            <template #default="{ row }">
              <div class="table_operate_class">
                <el-link :underline="false" @click="clickOperateBut(3, row)">查看详情</el-link>
                <span class="split_line">|</span>
                <el-link :underline="false" @click="clickOperateBut(4, row)">修改</el-link>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <div class="tablePagination" ref="tablePaginationRef">
        <Pagination v-model:pageSize="pageNum" :totalNumber="totalNumber" v-model:currentPage="currentPage" @pageChange="listArray"/>
      </div>
    </div>

    <ImportComponentLibraryDialog v-if="importComponentLibraryVisible" v-model:isVisible="importComponentLibraryVisible" @changeEvent="listArray('refresh')" />
    <AddComponentLibraryDialog v-if="addComponentLibraryVisible" v-model:isVisible="addComponentLibraryVisible" :titleName="titleName" :activeEditInfo="activeEditInfo" @changeEvent="listArray('refresh')" />
  </div>
</template>

<script>
import {getNowDateAll} from "@/utils/dateTime";
import {module_type_array} from "@/utils/setVariate";
import {ElMessage, ElMessageBox} from "element-plus";
import {exportCustomExcel} from "@/common/exportExcel";
import {onMounted, reactive, toRefs, defineComponent, ref, nextTick} from "vue";
import {Plus,RefreshRight, Search, Delete, Folder} from "@element-plus/icons-vue";
import AddComponentLibraryDialog from "./PvComponentLibrary/AddComponentLibraryDialog.vue";
import ImportComponentLibraryDialog from "./PvComponentLibrary/ImportComponentLibraryDialog.vue";
import {batchDeleteModuleLibrary, findModuleLibraryList} from "@/api/operationManagement/PvComponentLibrary";

export default defineComponent({
  name: 'PvComponentLibrary',
  components:{AddComponentLibraryDialog,ImportComponentLibraryDialog},
  setup() {

    const that = reactive({
      Plus,
      Delete,
      Search,
      Folder,
      RefreshRight,
      formInline: {},
      oldFormInline: {},
      moduleTypeArray: module_type_array,

      list: [],
      pageNum: 20,
      currentPage: 1,
      totalNumber: 0,
      listLoading: false,
      tableMaxHeight: 300,
      exportLoading: false,

      titleName: "",
      isLock: false,
      activeEditInfo: {},
      checkDeviceList: [],
      addComponentLibraryVisible: false,
      importComponentLibraryVisible: false,

      tableExportList:[
        {width: 20, key: "maxPower", name: "组件最大功率(Pmax)(W)"},
        {width: 20, key: "bestWorkVoltage", name: "组件最佳工作电压(Vmp)"},
        {width: 20, key: "bestWorkCurrent", name: "组件最佳工作电流(Imp) (A)"},
        {width: 20, key: "openVoltage", name: "组件开路电压(Voc)(V)"},
        {width: 20, key: "shortCurrent", name: "组件短路电流(Isc)(A)"},
        {width: 20, key: "maxPowerTempCoeff", name: "最大功率(Pmax)的温度系数 (%/℃)"},
        {width: 20, key: "openVoltTempCoeff", name: "开路电压(Voc)的温度系数 (%/℃)"},
        {width: 20, key: "shortCurrTempCoeff", name: "短路电流(Isc)的温度系数 (%/℃)"},
        {width: 20, key: "moduleType", name: "组件类型",filterName: 'moduleType'},
        {width: 35, key: "moduleFactory", name: "组件厂家"},
        {width: 20, key: "batteryPieces", name: "组件电池片数(片/组件)"},
        {width: 20, key: "firstDecayRate", name: "组件首年衰减率(%/y)"},
        {width: 20, key: "passingDecayRate", name: "组件逐年衰减率(%/y)"},
        {width: 35, key: "moduleModel", name: "组件型号"},
        {width: 20, key: "fillFactor", name: "填充因子(%)"},
        {width: 20, key: "convertEffi", name: "标称组件转换效率(%)"},
      ]
    });

    const listArray = (operateType) => {
      // console.log(operateType);
      let formInline = JSON.parse(JSON.stringify(that.formInline));
      operateType === "exportTable" ? that.exportLoading = true : that.listLoading = true;

      let size = operateType === "exportTable" ? 0 : that.pageNum;
      if (operateType === "resetPage" || operateType === "refresh") that.currentPage = 1;
      findModuleLibraryList({page: that.currentPage, size, ...formInline}).then(async res => {
        if(operateType === "exportTable"){
          let returnDataInfo = JSON.parse(JSON.stringify(res.data ? res.data : []));
          exportCustomExcel(that.tableExportList, returnDataInfo, `光伏_组件库列表_${getNowDateAll()}`);
          setTimeout(()=> {that.exportLoading = false;},500);
        } else {
          if (operateType === "resetPage") ElMessage({type: "success", message: "重置成功", showClose: true});
          let returnDataInfo = JSON.parse(JSON.stringify(res.data ? res.data : {}));
          that.totalNumber = returnDataInfo.totalSize;
          that.list = returnDataInfo.items;
          that.listLoading = false;
          setSelectTableTrue();
        }
        await nextTick();
        if (multipleTableRef.value) {
          multipleTableRef.value.setScrollTop(0);
        }
      }).catch((error) => {
        that.listLoading = false;
        that.exportLoading = false;
        if (error && error.code === 88886) return;
        if(operateType !== "exportTable"){
          that.totalNumber = 0;
          that.list = [];
        }
      });
    };

    const clickResetForm = (operateType = "resetPage") => {
      multipleTableRef.value.clearSelection();
      that.formInline = JSON.parse(JSON.stringify(that.oldFormInline));
      listArray(operateType);
    };

    const clickOperateBut = (operateType,row) => {
      if (operateType === 1) {
        that.activeEditInfo = {};
        that.titleName = "新增组件库";
        that.addComponentLibraryVisible = true;
      }

      if (operateType === 2) {
        if(!that.checkDeviceList || !that.checkDeviceList.length){
          ElMessage({ type: "error",showClose: true,message: "请选择需要删除的组件库列表！" });
          return;
        }

        let ids = [];
        for(let i = 0; i < that.checkDeviceList.length;i++ ) ids.push(that.checkDeviceList[i].id);
        ElMessageBox.confirm(`您确定要删除当前所选中的组件吗？`, "删除提示", {
          customClass: "deleteMsgBoxClass", dangerouslyUseHTMLString: true, confirmButtonText: '确定',
          cancelButtonText: '取消', type: 'warning',closeOnClickModal: false,
          beforeClose: (action, instance, done)=>{
            if (action === 'confirm') {
              instance.confirmButtonLoading = true;
              instance.confirmButtonText = '正在删除...';
              batchDeleteModuleLibrary({ ids: ids }).then(()=> {
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
          listArray('refresh');
          ElMessage({ type:"success",showClose: true,message:"操作成功！" });
        }).catch(() => {
          console.log("取消删除");
        });
      }

      if (operateType === 3) {
        that.titleName = "组件库详情";
        that.activeEditInfo = JSON.parse(JSON.stringify({...row, previewCom: true}));
        that.addComponentLibraryVisible = true;
      }

      if (operateType === 4) {
        that.titleName = "修改组件库";
        that.activeEditInfo = JSON.parse(JSON.stringify(row));
        that.addComponentLibraryVisible = true;
      }

      if (operateType === 5) {
        that.importComponentLibraryVisible = true;
      }
    };

    // 选中触发
    const multipleTableRef = ref(null);
    const handleSelectionChange = (selection = []) => {
      if (that.isLock) return;
      let multipleSelection = selection ? selection : []; // 当前页面里面所有选中的

      // 查看本页所有数据
      for (let i = 0; i < that.list.length; i++) {
        let findNoIndex = multipleSelection.findIndex(item => item.id === that.list[i].id);       // 查看当前页是否选中

        if (findNoIndex === -1) {
          let findIndex = that.checkDeviceList.findIndex(item => item.id === that.list[i].id);
          if (findIndex !== -1) that.checkDeviceList.splice(findIndex, 1); // 不存在初始选择的删除
        }

        if (findNoIndex !== -1) {
          let findIndex = that.checkDeviceList.findIndex(item => item.id === that.list[i].id);
          if (findIndex === -1) that.checkDeviceList.push(that.list[i]);
        }
      }
    };

    const getRowKeys = (row) => {
      return row.id;
    };

    //设置表格默认选中
    const setSelectTableTrue = () => {
      that.isLock = true;
      if (that.checkDeviceList && that.checkDeviceList.length) {
        for (let i = 0; i < that.checkDeviceList.length; i++) {
          multipleTableRef.value.toggleRowSelection(that.checkDeviceList[i], true);
        }
      }
      nextTick(() => that.isLock = false);
    };

    // 计算出表格最大高度
    const tableContentRef = ref(null);
    const tablePaginationRef = ref(null);
    const setTableMaxHeight = () => {
      let tableContentHeight = tableContentRef.value.offsetHeight - 78;
      that.tableMaxHeight = tableContentHeight - tablePaginationRef.value.offsetHeight;
    };

    onMounted(() => {
      that.oldFormInline = JSON.parse(JSON.stringify(that.formInline));
      listArray();
    });

    return {...toRefs(that), listArray, clickResetForm, tableContentRef, tablePaginationRef, setTableMaxHeight, clickOperateBut, getRowKeys, multipleTableRef,
      handleSelectionChange, setSelectTableTrue};
  }
});
</script>

<style lang="scss" scoped>
.tableContent {
  padding: 12px;
  box-sizing: border-box;
}
</style>