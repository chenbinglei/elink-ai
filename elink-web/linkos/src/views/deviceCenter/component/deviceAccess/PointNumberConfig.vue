<template>
  <div class="pointNumberConfig" ref="pointNumberConfigRef">
    <div class="content_top flex jc-space-between">
      <div class="content_top_left">
        <el-button class="whiteFontButtons" :icon="Edit" @click="clickEditChannelBut">编辑通道</el-button>
        <el-button class="blackFontButtons" @click="clickImportPointTable">导入点表</el-button>
        <el-button class="blackFontButtons" @click="clickExportPointTable">导出点表</el-button>
      </div>
      <div class="content_top_right" v-if="isShowCancelAndEnter">
        <el-button class="blackFontButtons" :disabled="listLoading" @click="clickCancelBut">取消</el-button>
        <el-button class="whiteFontButtons" :disabled="listLoading" :loading="listLoading"
          @click="clickSaveBut">保存</el-button>
      </div>
    </div>
    <div class="content_bottom" v-loading="listLoading">
      <el-table :data="list" border :height="tableMaxHeight" :header-cell-class-name="headerClass"
        ref="multipleTableRef" row-key="id" :row-class-name="tableRowClassName" @row-click="handleRowClickFun"
        @selection-change="handleSelectionChange">
        <el-table-column align="center" width="100">
          <template #header>
            <span class="addIconBut iconfont icon-treeAdd" @click="clickAddBut()"></span>
          </template>
          <template #default="{ row, $index }">
            <span class="scIconBut iconfont icon-shanchuxiajimokuai" @click="clickReduceBut(row, $index)"></span>
          </template>
        </el-table-column>
        <el-table-column type="selection" width="55"></el-table-column>
        <el-table-column label="序号" type="index" width="100"></el-table-column>
        <el-table-column align="center" label="设备名称" show-overflow-tooltip>
          <template #default="{ row, $index }">
            <template v-if="tableActiveEditIndex === $index">
              <el-select v-model="row.deviceId" placeholder="请选择设备" placement="top"
                @change="tableChange(row, 'deviceId')">
                <el-option v-for="item in deviceArray" :key="item.deviceId" :label="item.deviceName"
                  :value="item.deviceId"></el-option>
              </el-select>
            </template>
            <template v-else>{{ $filters.moreData(row.deviceName) }}</template>
          </template>
        </el-table-column>
        <el-table-column align="center" label="功能点" show-overflow-tooltip>
          <template #default="{ row, $index }">
            <template v-if="tableActiveEditIndex === $index">
              <el-select v-model="row.functionId" placeholder="请选择功能点" :disabled="!row.deviceId"
                @focus="queryFunctionListByDeviceId(row)" placement="top" @change="tableChange(row, 'functionId')">
                <el-option v-for="item in row.functionIdArray" :key="item.functionId" :label="item.functionName"
                  :value="item.functionId"></el-option>
              </el-select>
            </template>
            <template v-else>{{ $filters.moreData(row.functionName) }}</template>
          </template>
        </el-table-column>
        <el-table-column align="center" label="功能点下标">
          <template #default="{ row, $index }">
            <template v-if="tableActiveEditIndex === $index && (row.dataType === 8 || dataTypeSelect === 8)">
              <el-input type="text" v-model="row.functionIndex" placeholder="请输入下标"
                :disabled="dataTypeSelect !== 8" @change="tableChange(row, 'functionIndex')"></el-input>

            </template>
            <template v-else>{{ $filters.moreData(row.functionIndex) }}</template>

          </template>
        </el-table-column>
        <el-table-column align="center" label="功能类型">
          <template #default="{ row }">{{ $filters.functionType(row.functionType) }}</template>
        </el-table-column>
        <el-table-column align="center" label="点号">
          <template #default="{ row, $index }">
            <template v-if="tableActiveEditIndex === $index">
              <el-input type="text" v-model="row.dataId" placeholder="请输入数据ID"
                @change="tableChange(row, 'dataId')"></el-input>
            </template>
            <template v-else>{{ $filters.moreData(row.dataId) }}</template>
          </template>
        </el-table-column>
        <el-table-column align="center" label="系数">
          <template #default="{ row, $index }">
            <template v-if="tableActiveEditIndex === $index">
              <el-input-number v-model="row.coefficient" controls-position="right" placeholder="请输入系数"
                @change="tableChange(row)" style="width: 100%;" />
            </template>
            <template v-else>{{ $filters.moreData(row.coefficient) }}</template>
          </template>
        </el-table-column>
        <el-table-column align="center" label="偏移量">
          <template #default="{ row, $index }">
            <template v-if="tableActiveEditIndex === $index">
              <el-input type="text" v-model="row.offset" placeholder="请输入偏移量"
                @change="tableChange(row, 'offset')"></el-input>
            </template>
            <template v-else>{{ $filters.moreData(row.offset) }}</template>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <ImportPointTableDialog v-if="importPointTableVisible" v-model:isVisible="importPointTableVisible"
      :activeDeviceId="activeDeviceId" :activeChannelId="activeChannelId" :activeChannelName="activeChannelName"
      :activeDeviceName="activeDeviceName" @changeEvent="queryPointTableListByChannelId" />
  </div>
</template>

<script>
import { Edit } from '@element-plus/icons-vue';
import { ElMessage, ElMessageBox } from "element-plus";
import { integer1tox, num0to9999999 } from "@/utils/validate";
import ImportPointTableDialog from "./ImportPointTableDialog";
import { exportPointTableExcel } from "@/views/deviceCenter/component/generateExcel";
import { getCurrentInstance, nextTick, onMounted, reactive, ref, toRefs, watch, defineComponent } from "vue";
import { findPointTableListByChannelId, findSubDeviceFunctionListByDeviceId, savePointTable } from "@/api/deviceCenter/deviceAccess";

export default defineComponent({
  name: "PointNumberConfig",
  components: { ImportPointTableDialog },
  props: {
    activeDeviceId: {
      type: [Number, String],
      default: ""
    },
    activeChannelId: {
      type: [Number, String],
      default: ""
    },
    activeDeviceName: {
      type: String,
      default: ""
    },
    activeChannelName: {
      type: String,
      default: ""
    },
  },
  setup (props) {
    const { emit } = getCurrentInstance();
    const pointNumberConfigRef = ref(null);

    const that = reactive({
      Edit,
      list: [],
      deviceArray: [],
      listLoading: false,
      tableMaxHeight: 320,
      deleteTableArray: [],
      selectableArrayIds: [],
      isShowCancelAndEnter: false,
      importPointTableVisible: false,
      dataTypeSelect: null,

      tableActiveEditIndex: null, // 当前表格编辑的index
    })

    // 根据通道id查询点表数据列表
    const queryPointTableListByChannelId = () => {
      that.listLoading = true;
      findPointTableListByChannelId({ channelId: props.activeChannelId }).then(res => {
        let list = res.data ? res.data : [];
        list.forEach((element, index) => {
          // 解决表格操作， 变量数据下拉框 展示的数字
          element.functionIdArray = [{ functionId: element.functionId, functionName: element.functionName }];
        });

        that.list = JSON.parse(JSON.stringify(list));
        console.log(that.list,'8888');
         
        that.tableActiveEditIndex = null;
        that.listLoading = false;
      }).catch(() => {
        that.listLoading = false;
      })
    }

    const clickSaveBut = () => {
      that.listLoading = true;
      let dataIds = [], dataRecordId = []; // 设备 ， 变量数据
      let list = JSON.parse(JSON.stringify(that.list));

      for (let i = 0; i < list.length; i++) {
        delete list[i].functionIdArray; // 删除功能点数据

        if (!list[i].deviceId) {
          that.listLoading = false;
          ElMessage({ type: "error", showClose: true, message: `请选择设备名称（第${i + 1}行）` });
          return
        }

        if (!list[i].functionId) {
          that.listLoading = false;
          ElMessage({ type: "error", showClose: true, message: `请选择功能点（第${i + 1}行）` });
          return
        }

        if (!integer1tox(list[i].dataId, 8)) {
          that.listLoading = false;
          ElMessage({ type: "error", showClose: true, message: `请填写正确的点号（第${i + 1}行）` });
          return
        }

        if (!num0to9999999(list[i].coefficient)) {
          that.listLoading = false;
          ElMessage({ type: "error", showClose: true, message: `请填写正确的系数（第${i + 1}行）` });
          return
        }

        if (!list[i].offset && list[i].offset !== 0) {
          that.listLoading = false;
          ElMessage({ type: "error", showClose: true, message: `请填写偏移量（第${i + 1}行）` });
          return
        }

        let dataRecordIdStr = list[i].deviceId + ',' + list[i].functionId;
        if (JSON.stringify(dataRecordId).indexOf(dataRecordIdStr) > -1) {
          that.listLoading = false;
          ElMessage({ type: "error", showClose: true, message: `此设备下的变量，数据名称已存在，请更改（第${i + 1}行）` });
          return
        } else {
          dataRecordId.push(dataRecordIdStr);
        }

        let dataIdsStatus = dataIds.find(item => item.dataId == list[i].dataId &&
          item.deviceName === list[i].deviceName);
        if (dataIdsStatus) {
          that.listLoading = false;
          ElMessage({ type: "error", showClose: true, message: `此点号ID已存在，请更改（第${i + 1}行）` });
          return
        } else {
          dataIds.push({
            dataId: list[i].dataId,
            deviceName: list[i].deviceName
          });
        }
      }

      list = list.filter(item => {
        return item.updateType
      });

      savePointTable({ pointTableVos: [...that.deleteTableArray, ...list] }).then(() => {
        that.listLoading = false;
        queryPointTableListByChannelId();
        that.isShowCancelAndEnter = false;
        ElMessage({ type: "success", showClose: true, message: "保存成功！" });
      }).catch(() => {
        that.listLoading = false;
      });
    }

    const clickCancelBut = () => {
      ElMessageBox.confirm(`确定要取消吗`, "提示", {
        dangerouslyUseHTMLString: true, confirmButtonText: '确定', cancelButtonText: '取消',
        customClass: "deleteMsgBoxClass", showClose: false, type: 'warning',
      }).then(() => {
        queryPointTableListByChannelId();
        that.isShowCancelAndEnter = false;
        ElMessage({ type: "success", showClose: true, message: "取消成功！" });
      }).catch(() => {
        console.log("取消删除！");
      });
    }

    // 新增表格数据
    const clickAddBut = () => {
      that.isShowCancelAndEnter = true;
      that.tableActiveEditIndex = that.list.length;
      that.list.push({ channelId: props.activeChannelId, updateType: 1 });

      // 每次新加都会滚动到相应位置
      nextTick(() => {
        let cloudAccessInfo = pointNumberConfigRef.value;
        let offsetHeight = cloudAccessInfo.getElementsByClassName("el-scrollbar__view")[0].offsetHeight;
        cloudAccessInfo.getElementsByClassName("el-scrollbar__wrap")[0].scrollTop = offsetHeight;
      })
    }

    // 删除表格数据
    const clickReduceBut = (row, index) => {
      if (row.updateType !== 1) {
        row.updateType = 3; // 删除
        that.deleteTableArray.push(row);
      }
      that.isShowCancelAndEnter = true;
      that.list.splice(index, 1);
    }

    // 值发生改变执行
    const tableChange = (row, type) => {
      that.isShowCancelAndEnter = true;

      if (type === "deviceId") {
        row.functionId = "";
        row.functionName = "";
        row.functionIdArray = [];
        let findItem = that.deviceArray.find(item => item.deviceId === row.deviceId);

        row.deviceName = findItem?.deviceName || findItem?.deviceId;
      }

      if (type === "functionId") {
        let findItem = row.functionIdArray.find(item => item.functionId === row.functionId);
        that.dataTypeSelect = findItem?.dataType;
        row.functionType = findItem?.functionType;
        row.functionName = findItem?.functionName;
      }
      // 当 dataTypeSelect 不等于 8 时，清空 functionIndex
      if (that.dataTypeSelect !== 8) {
        row.functionIndex = '';
      }
      if (row.updateType === 1) return
      row.updateType = 2; // 编辑



    }

    // 根据设备id查询网关子设备功能点列表
    const querySubDeviceFunctionListByDeviceId = () => {
      findSubDeviceFunctionListByDeviceId({ type: 2, deviceId: props.activeDeviceId }).then(res => {
        that.deviceArray = res.data ? res.data : [];
      })
    }

    // 根据设备id查询功能点
    const queryFunctionListByDeviceId = (row) => {
      findSubDeviceFunctionListByDeviceId({ type: 3, deviceId: row.deviceId, timer: new Date() }).then(res => {
        row.functionIdArray = res.data ? res.data : [];
      })
    }

    // 表格 头部中那些带有红色 *
    const headerClass = (obj) => {
      if (obj.columnIndex > 1 && obj.columnIndex !== 4) return 'headerClass';
    }

    // 给列表每一项增加索引
    const tableRowClassName = ({ row, rowIndex }) => {
      // console.log(row);
      // console.log(rowIndex);
      row.rowIndex = rowIndex;
      if (row.updateType <= 2) return 'tableRowEditClass'
    }

    const handleRowClickFun = (row) => {
      that.tableActiveEditIndex = row.rowIndex;
      that.dataTypeSelect = row?.dataType;
    }

    const clickImportPointTable = () => {
      that.importPointTableVisible = true;
    }

    // 导出点表
    const multipleTableRef = ref(null);
    const clickExportPointTable = () => {
      if (that.isShowCancelAndEnter) {
        ElMessage({ type: "error", showClose: true, message: "请先结束编辑点表！" });
        return
      }

      if (!that.selectableArrayIds.length) {
        ElMessage({ type: "error", message: "请选择需要操作的数据!", showClose: true });
        return
      }

      // console.log(that.selectableArrayIds);
      let selectableArrayIds = JSON.parse(JSON.stringify(that.selectableArrayIds));

      let tableHeader = [
        { width: 18, header: "设备名称", key: "deviceName" },
        { width: 35, header: "设备名ID", key: "deviceId" },
        { width: 18, header: "功能点名称", key: "functionName" },
        { width: 35, header: "功能点ID", key: "functionId" },
        { width: 18, header: "点号", key: "dataId" },
        { width: 18, header: "系数", key: "coefficient" },
        { width: 18, header: "偏移量", key: "offset" },
      ]; // 表头

      multipleTableRef.value.clearSelection();
      exportPointTableExcel(tableHeader, selectableArrayIds, `${props.activeDeviceName}-${props.activeChannelName}-点表信息`);
    }

    const handleSelectionChange = (val) => {
      that.selectableArrayIds = val;
    }

    const clickEditChannelBut = () => {
      emit("changeEvent", { type: 'channelEdit' });
    }

    const watchDeviceAndChannelId = watch([() => props.activeDeviceId, () => props.activeChannelId], ([newActiveDeviceId, newActiveChannelId]) => {
      queryPointTableListByChannelId();
    }, { deep: true })

    onMounted(() => {
      querySubDeviceFunctionListByDeviceId();
    })

    return {
      ...toRefs(that), headerClass, watchDeviceAndChannelId, queryPointTableListByChannelId, clickAddBut, pointNumberConfigRef, querySubDeviceFunctionListByDeviceId,
      tableChange, queryFunctionListByDeviceId, clickCancelBut, clickSaveBut, clickReduceBut, clickImportPointTable, clickExportPointTable, handleSelectionChange,
      multipleTableRef, clickEditChannelBut, handleRowClickFun, tableRowClassName
    }
  }
})
</script>

<style scoped lang="scss">
.content_bottom {
  margin-top: 12px;

  .addIconBut {
    font-size: 21px;
    cursor: pointer;
    color: #1F74E2;
  }

  .scIconBut {
    font-size: 22px;
    cursor: pointer;
    color: #FF2626;
  }
}

:deep(.el-table) {
  .tableRowEditClass {
    --el-table-tr-bg-color: #f5fafa;
  }
}
</style>
