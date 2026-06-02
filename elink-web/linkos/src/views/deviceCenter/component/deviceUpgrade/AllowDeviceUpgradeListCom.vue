<template>
  <div class="allowDeviceUpgradeListCom">
    <div class="content_top flex ai-center jc-space-between">
      <div class="content_title">已选择条数：{{ checkDeviceList.length }}</div>
      <el-button :icon="Refresh" class="blackFontButtons" @click="searchInputFun">刷新</el-button>
    </div>
    <div class="content_table">
      <el-table ref="multipleTableRef" v-loading="listLoading" :data="list" :max-height="tableMaxHeight"
        :row-key="getRowKeys" @selection-change="handleSelectionChange">
        <template #empty>
          <null-data words="暂无数据">
            <img alt="" class="table_null_img" src="@/assets/image/table_null.png" />
          </null-data>
        </template>
        <el-table-column :selectable="selectable" align="center" reserve-selection type="selection"
          width="45"></el-table-column>
        <el-table-column fixed="left" label="序号" type="index" width="80">
          <template #default="{ $index }">{{ $index + 1 + (currentPage - 1) * pageNum }}</template>
        </el-table-column>
        <el-table-column fixed="left" show-overflow-tooltip>
          <template #header>
            <TableFilterPopCom v-model:value="formInline.siteName" fieldName="siteName" placeholder="请输入电站名称"
              tableName="电站名称" @changEvent="searchInputFun" />
          </template>
          <template #default="{ row }">{{ $filters.moreData(row.siteName) }}</template>
        </el-table-column>
        <el-table-column show-overflow-tooltip>
          <template #header>
            <TableFilterPopCom v-model:value="formInline.deviceName" fieldName="deviceName" placeholder="请输入设备名称"
              tableName="设备名称" @changEvent="searchInputFun" />
          </template>
          <template #default="{ row }">{{ $filters.moreData(row.deviceName) }}</template>
        </el-table-column>
        <el-table-column show-overflow-tooltip>
          <template #header>
            <TableFilterPopCom v-model:value="formInline.deviceNumber" fieldName="deviceNumber" placeholder="请输入SN号"
              tableName="SN号" @changEvent="searchInputFun" />
          </template>
          <template #default="{ row }">{{ $filters.moreData(row.deviceNumber) }}</template>
        </el-table-column>
        <el-table-column>
          <template #header>
            <TableFilterPopCom v-model:value="formInline.currentVersions" :dataList="versionList"
              fieldName="currentVersions" filterType="checkbox" placeholder="全部" tableName="当前版本"
              @changEvent="searchInputFun" />
          </template>
          <template #default="{ row }">{{ $filters.moreData(row.currentVersion) }}</template>
        </el-table-column>
        <el-table-column>
          <template #header>
            <TableFilterPopCom v-model:value="formInline.status" :dataList="statusArray" fieldName="status"
              filterType="select" placeholder="全部" tableName="状态" @changEvent="searchInputFun" />
          </template>
          <template #default="{ row }">
            <span :class="'status' + row.status" class="status">{{ $filters.deviceUpdateStatus(row.status) }}</span>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <div class="tablePagination">
      <Pagination v-model:currentPage="currentPage" v-model:pageSize="pageNum" :totalNumber="totalNumber"
        @pageChange="listArray" />
    </div>
  </div>
</template>

<script>
import { Refresh } from "@element-plus/icons-vue";
import TableFilterPopCom from "@/components/FromFilterComponent/TableFilterPopCom.vue";
import { getDeviceVersionList, queryDeviceUpdateList } from "@/api/deviceCenter/deviceUpgrade";
import { getCurrentInstance, nextTick, reactive, ref, toRefs, watch, defineComponent } from "vue";

export default defineComponent({
  name: "AllowDeviceUpgradeListCom",
  components: { TableFilterPopCom },
  props: {
    // 设备类型
    typeId: {
      type: [String, Number],
      default: ""
    },
    // 设备型号
    equipmentModel: {
      type: [String, Number],
      default: ""
    },
    // 固件类型
    firmwareType: {
      type: [String, Number],
      default: ""
    },
    deviceCheckList: {
      type: Array,
      default: () => []
    },
  },
  emits: ["update:deviceCheckList"],
  setup (props) {
    const { emit } = getCurrentInstance();

    const that = reactive({
      Refresh,
      formInline: {},
      versionList: [],
      statusArray: [{ id: 1, name: "可用" }, { id: 2, name: "不可用" }],

      list: [],
      pageNum: 20,
      currentPage: 1,
      totalNumber: 0,
      listLoading: false,
      tableMaxHeight: 320,

      isLock: false,
      maxCheckLength: 2,
      checkDeviceList: [],
    })

    const listArray = (operateType) => {
      that.listLoading = true;
      if (operateType === "resetPage") that.currentPage = 1;
      let formInline = JSON.parse(JSON.stringify(that.formInline));
      formInline.equipmentModel = props.equipmentModel;
      formInline.firmwareType = props.firmwareType;
      formInline.typeId = props.typeId;

      queryDeviceUpdateList({ page: that.currentPage, size: that.pageNum, ...formInline }).then(res => {
        that.totalNumber = res.data.totalSize;
        that.list = res.data.items;
        that.listLoading = false;
        setSelectTableTrue();
      }).catch((error) => {
        that.listLoading = false;
        if (error && error.code === 88886) return
        that.totalNumber = 0;
        that.list = [];
      })
    }

    // 选中触发
    const multipleTableRef = ref(null);
    const handleSelectionChange = (selection = []) => {
      if (that.isLock) return
      // let multipleSelection = selection ? selection : []; // 当前页面里面所有选中的

      // // // 查看本页所有数据
      // for (let i = 0; i < that.list.length; i++) {
      //   let findNoIndex = multipleSelection.findIndex(item => item.id === that.list[i].id);       // 查看当前页是否选中

      //   if (findNoIndex === -1) {
      //     let findIndex = that.checkDeviceList.findIndex(item => item.id === that.list[i].id);
      //     if (findIndex !== -1) that.checkDeviceList.splice(findIndex, 1); // 不存在初始选择的删除
      //   }

      //   if (findNoIndex !== -1) {
      //     let findIndex = that.checkDeviceList.findIndex(item => item.id === that.list[i].id);
      //     if (findIndex === -1) {
      //       if (that.checkDeviceList.length < that.maxCheckLength) {
      //         that.checkDeviceList.push(that.list[i]);
      //       } else {
      //         multipleTableRef.value.toggleRowSelection(that.list[i], false);
      //       }
      //     }
      //   }
      // }
      //    emit("update:deviceCheckList", JSON.parse(JSON.stringify(that.checkDeviceList)));
      // 直接将当前页选中的数据赋值给 checkDeviceList
      that.checkDeviceList = [...selection];
      emit("update:deviceCheckList", JSON.parse(JSON.stringify(that.checkDeviceList)));


    }

    const selectable = (row) => {
      // let findItem = that.checkDeviceList.find(item => item.id === row.id);
      // return row.status === 1 && (that.checkDeviceList.length < that.maxCheckLength || findItem)
      return row.status === 1;
    }

    const getRowKeys = (row) => {
      return row.id
    }

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

    const searchInputFun = (data) => {
      // console.log(data);
      if (props.equipmentModel && props.firmwareType) {
        listArray('resetPage');
      }
    }

    // 获取设备版本列表
    const queryDeviceVersionList = () => {
      getDeviceVersionList({ typeId: props.typeId, equipmentModel: props.equipmentModel, firmwareType: props.firmwareType }).then(res => {
        let versionList = [];
        let returnDataList = res.data ?? [];
        for (let i = 0; i < returnDataList.length; i++) versionList.push({ id: returnDataList[i], name: returnDataList[i] });
        that.versionList = JSON.parse(JSON.stringify(versionList));
      }).catch(() => {
        that.versionList = [];
      })
    }

    const watchModelAndType = watch([() => props.equipmentModel, () => props.firmwareType], ([newEquipmentModel, newFirmwareType]) => {
      if (!newEquipmentModel || !newFirmwareType) {
        that.list = [];
        that.totalNumber = 0;
        that.versionList = [];
        return
      }

      queryDeviceVersionList();  // 获取设备版本列表
      listArray('resetPage');
    }, { deep: true })

    return {
      ...toRefs(that), listArray, watchModelAndType, selectable, handleSelectionChange, multipleTableRef, setSelectTableTrue, getRowKeys, searchInputFun,
      queryDeviceVersionList
    }
  }
})
</script>

<style lang="scss" scoped>
.allowDeviceUpgradeListCom {
  padding-top: 12px;
  box-sizing: border-box;

  .content_top {
    margin-bottom: 12px;

    .content_title {
      font-size: 14px;
    }
  }

  .status {
    color: #b9b9b9ff;
  }

  .status1 {
    color: #2b66feff;
  }

  .table_null_img {
    width: 96px;
    height: 96px;
  }

  .tablePagination {
    margin-top: 12px;

    :deep(.el-select) {
      max-width: 100px !important;
    }
  }
}
</style>