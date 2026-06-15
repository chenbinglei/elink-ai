<template>
  <div class="app-container-right">
    <div class="header-form">
      <el-form :model="formInline" inline>
        <el-row :gutter="16">
          <el-col :xs="24" :sm="12" :md="8" :xl="6">
            <el-form-item label="配置状态：">
              <el-select v-model="formInline.configStatus" clearable filterable placeholder="全部">
                <el-option v-for="item in configStatusArray" :key="item.id" :label="item.name" :value="item.id"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :xl="6">
            <el-form-item label="设备型号：">
              <el-select v-model="formInline.equipmentModel" clearable filterable placeholder="全部">
                <el-option v-for="item in equipmentModelArray" :key="item" :label="item" :value="item"></el-option>
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

    <div class="tableContent" ref="tableContentRef">
      <div class="content_table_header">
        <div class="table_header_left">已选择{{ checkDeviceList.length }}台</div>
        <el-button :icon="Delete" @click="clickOperateBut(3)">清除配置</el-button>
      </div>
      <div class="tableCenter" ref="tableCenterRef" v-resize="setTableMaxHeight">
        <el-table ref="multipleTableRef" v-loading="listLoading" :data="list" stripe :max-height="tableMaxHeight" :row-key="getRowKeys"
          @selection-change="handleSelectionChange">
          <el-table-column align="center" reserve-selection type="selection" width="45"></el-table-column>
          <el-table-column label="序号" type="index" width="80" fixed="left"></el-table-column>
          <el-table-column label="设备名称" fixed="left" show-overflow-tooltip>
            <template #default="{ row }">{{ $filters.moreData(row.deviceName) }}</template>
          </el-table-column>
          <el-table-column label="SN号" show-overflow-tooltip>
            <template #default="{ row }">{{ $filters.moreData(row.deviceNumber) }}</template>
          </el-table-column>
          <el-table-column label="设备类型">
            <template #default="{ row }">{{ $filters.moreData(row.typeName) }}</template>
          </el-table-column>
          <el-table-column label="设备型号">
            <template #default="{ row }">{{ $filters.moreData(row.equipmentModel) }}</template>
          </el-table-column>
          <el-table-column label="配置状态">
            <template #default="{ row }">{{ $filters.configStatus(row.configStatus) }}</template>
          </el-table-column>
          <el-table-column label="操作" fixed="right">
            <template #default="{ row }">
              <div class="table_operate_class">
                <el-link :underline="false" @click="clickOperateBut(1, row)">配置</el-link>
                <!--                <el-link :underline="false" @click="clickOperateBut(2, row)">修改</el-link>-->
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>

    <PvStringConfigDialog v-if="pvStringConfigVisible" v-model:isVisible="pvStringConfigVisible" :activeEditInfo="activeEditInfo"
      @changeEvent="listArray('refresh')" />
  </div>
</template>

<script lang="ts">
import { ElMessage, ElMessageBox } from "element-plus";
import PvStringConfigDialog from "./PvStringConfigDialog.vue";
import { RefreshRight, Search, Delete } from "@element-plus/icons-vue";
import { onMounted, reactive, toRefs, defineComponent, ref, nextTick, watch } from "vue";
import { findInverterDeviceModelList, findInverterDeviceList, purgeSeriesConfigById } from "@/api/operationManagement/PvComponentLibrary";

export default defineComponent({
  name: 'PvStringConfigTable',
  components: { PvStringConfigDialog },
  props: {
    activeSiteId: {
      type: [String, Number],
      default: ""
    }
  },
  setup (props) {

    const that = reactive({
      Delete,
      Search,
      RefreshRight,
      formInline: {},
      oldFormInline: {},
      equipmentModelArray: [],
      configStatusArray: [{ id: 1, name: "未配置" }, { id: 2, name: "已配置" }],

      list: [],
      listLoading: false,
      tableMaxHeight: 300,
      tablePaginationRef: null,

      activeEditInfo: {},
      checkDeviceList: [],
      pvStringConfigVisible: false,
    });

    const listArray = (operateType) => {
      that.listLoading = true;
      let formInline = JSON.parse(JSON.stringify(that.formInline));
      if (operateType === "resetPage" || operateType === "refresh") that.currentPage = 1;
      findInverterDeviceList({ page: that.currentPage, size: that.pageNum, siteId: props.activeSiteId, ...formInline }).then(res => {
        that.list = res.data;
        that.listLoading = false;
        if (operateType === "resetPage") ElMessage({ type: "success", message: "重置成功", showClose: true });
      }).catch((error) => {
        that.listLoading = false;
        if (error && error.code === 88886) return;
        that.list = [];
      });
    };

    const multipleTableRef = ref(null);
    const clickResetForm = (operateType = "resetPage") => {
      multipleTableRef.value.clearSelection();
      that.formInline = JSON.parse(JSON.stringify(that.oldFormInline));
      listArray(operateType);
    };

    const clickOperateBut = (operateType, row) => {

      if (operateType === 1 || operateType === 2) {
        that.activeEditInfo = JSON.parse(JSON.stringify(row));
        that.pvStringConfigVisible = true;
      }

      if (operateType === 3) {
        if (!that.checkDeviceList || !that.checkDeviceList.length) {
          ElMessage({ type: "error", showClose: true, message: "请选择需要清除除的设备！" });
          return;
        }

        let deviceIds = [];
        for (let i = 0; i < that.checkDeviceList.length; i++) deviceIds.push(that.checkDeviceList[i].id);
        ElMessageBox.confirm(`清除配置可能会影响相关指标计算结果，请确认是否清除配置？`, "清除提示", {
          customClass: "deleteMsgBoxClass", dangerouslyUseHTMLString: true, confirmButtonText: '确定',
          cancelButtonText: '取消', type: 'warning', closeOnClickModal: false,
          beforeClose: (action, instance, done) => {
            if (action === 'confirm') {
              instance.confirmButtonLoading = true;
              instance.confirmButtonText = '正在清除...';
              purgeSeriesConfigById({ deviceIds: deviceIds }).then(() => {
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
          multipleTableRef.value.clearSelection();
          ElMessage({ type: "success", showClose: true, message: "操作成功！" });
        }).catch(() => {
          console.log("取消清除");
        });
      }
    };

    // 选中触发
    const handleSelectionChange = (selection = []) => {
      that.checkDeviceList = selection ? selection : []; // 当前页面里面所有选中的
    };

    const getRowKeys = (row) => {
      return row.id;
    };

    // 根据站点id查询逆变器设备型号列表
    const queryInverterDeviceModelList = () => {
      findInverterDeviceModelList({ siteId: props.activeSiteId, timer: new Date() }).then(res => {
        that.equipmentModelArray = res.data ?? [];
      });
    };
    // 计算出表格最大高度
    // 计算出表格最大高度
    const tableContentRef = ref(null);
    const setTableMaxHeight = () => {
      if (!tableContentRef.value) return;

      // if (that.list.length) {

      //   that.tableMaxHeight = tableContentRef.value.offsetHeight - 48;
      // }
      let tableContentHeight = tableContentRef.value.offsetHeight - 78;
      that.tableMaxHeight = tableContentHeight - that.tablePaginationRef?.offsetHeight ?? 0;
      console.log(tableContentHeight, that.tableMaxHeight);
    };

    const watchActiveSiteId = watch(() => props.activeSiteId, (newActiveSiteId) => {
      if (newActiveSiteId) {
        that.formInline = JSON.parse(JSON.stringify(that.oldFormInline));
        queryInverterDeviceModelList();
        listArray('refresh');
      }
    }, { deep: true, immediate: true });

    onMounted(() => {
      that.oldFormInline = JSON.parse(JSON.stringify(that.formInline));
    });

    return {
      ...toRefs(that), listArray, clickResetForm, tableContentRef, setTableMaxHeight, clickOperateBut, getRowKeys, multipleTableRef, handleSelectionChange,
      queryInverterDeviceModelList, watchActiveSiteId
    };
  }
});
</script>

<style lang="scss" scoped>
.app-container-right {
  width: 100%;
  flex-direction: column !important;

  .content_table_header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 4px;

    .table_header_left {
      font-size: 12px;
      color: #ffffffcc;
    }
  }

  .tableContent {
    padding-bottom: 10px;
    box-sizing: border-box;
  }
}
</style>