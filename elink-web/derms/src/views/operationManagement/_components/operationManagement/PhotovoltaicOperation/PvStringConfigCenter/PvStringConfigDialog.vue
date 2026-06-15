<template>
  <Dialog v-model:isVisible="dialog_visible" :listLoading="listLoading" :manualEnterClose="false" :title="titleName" disabledLoading width="75%"
    @confirm="saveDialog">
    <template v-slot:content>
      <div class="dialog-main">
        <div class="content_header">
          <div class="content_header_left">
            <el-checkbox v-model="isBatchConfig" label="批量配置"></el-checkbox>
            <span class="mpptNum">组串数：{{ formDialog.mppt }}</span>
          </div>
          <el-button type="primary" :icon="Document" @click="clickDocumentButFun">参数说明</el-button>
        </div>
        <template v-if="configTableList && configTableList.length" >
          <el-table border :data="configTableList" :row-key="getRowKeys" :header-cell-class-name="headerCellClassName" :max-height="'700'">

            <el-table-column type="expand" fixed="left">
              <template #default="{ row }">
                <ComponentParameCom :componentParame="row"></ComponentParameCom>
              </template>
            </el-table-column>
            <el-table-column label="序号" type="index" fixed="left" width="80"></el-table-column>
            <el-table-column label="组件厂家" fixed="left" >
              <template #default="{ row,$index }">
                <el-select v-model="row.moduleFactory" filterable placeholder="请选择组件厂家" @change="changeModuleLibraryListFun(row,$index,'moduleFactory')">
                  <el-option v-for="item in moduleFactoryArray" :key="item.moduleFactory" :label="item.moduleFactory" :value="item.moduleFactory"></el-option>
                </el-select>
              </template>
            </el-table-column>
            <el-table-column label="组件型号" >
              <template #default="{ row,$index }">
                <el-select v-model="row.moduleModel" filterable placeholder="请选择组件型号" :disabled="!row.moduleFactory"
                  @change="changeModuleLibraryListFun(row,$index,'moduleModel')">
                  <template v-for="item in moduleLibraryList" :key="item.moduleModel">
                    <el-option v-if="item.moduleFactory === row.moduleFactory" :label="item.moduleModel" :value="item.moduleModel"></el-option>
                  </template>
                </el-select>
              </template>
            </el-table-column>
            <el-table-column label="组串名称" >
              <template #default="{ row,$index}">
                <el-input v-model="row.seriesName" placeholder="请输入组串名称" @change="changeModuleLibraryListFun(row,$index,'seriesName')"></el-input>
              </template>
            </el-table-column>
            <el-table-column label="组件数量(块)">
              <template #default="{ row,$index}">
                <el-input-number v-model="row.moduleNum" controls-position="right" min="0" placeholder="请输入组件数量"
                  @change="changeModuleLibraryListFun(row,$index,'moduleNum')"></el-input-number>
              </template>
            </el-table-column>
            <el-table-column label="组件最大功率(Pmax)(W)">
              <template #default="{ row }">
                <el-input v-model="row.maxPower" disabled placeholder="请输入组件最大功率"></el-input>
              </template>
            </el-table-column>
            <el-table-column label="组件容量(Wp)">
              <template #default="{ row }">
                <el-input v-model="row.seriesCapacity" disabled placeholder="请输入组件容量"></el-input>
              </template>
            </el-table-column>
          </el-table>
        </template>
        <null-data v-else words="暂未配置组串数"></null-data>
        <ParameterDescriptionDialog v-if="parameterDescriptionVisible" v-model:isVisible="parameterDescriptionVisible"></ParameterDescriptionDialog>
      </div>
    </template>
  </Dialog>
</template>

<script lang="ts">
import { ElMessage } from "element-plus";
import { Document } from "@element-plus/icons-vue";
import { calcNumberFun, generateUUID } from "@/utils";
import ComponentParameCom from "./ComponentParameCom.vue";
import { getCurrentInstance, onMounted, reactive, toRefs, watch, defineComponent } from "vue";
import ParameterDescriptionDialog from "../PvComponentLibrary/ParameterDescriptionDialog.vue";
import { findSeriesConfigInfo, saveSeriesConfigList, findModuleLibraryList } from "@/api/operationManagement/PvComponentLibrary";

export default defineComponent({
  name: "PvStringConfigDialog",
  components: { ParameterDescriptionDialog, ComponentParameCom },
  props: {
    isVisible: {
      type: Boolean,
      default: false
    },
    activeEditInfo: {
      type: Object,
      default: () => {
        return {};
      }
    },
  },
  setup (props) {
    const { emit } = getCurrentInstance();

    const that = reactive({
      Document,
      formDialog: {},
      listLoading: false,
      // tableMaxHeight: 520,
      moduleLibraryList: [],
      moduleFactoryArray: [],
      titleName: "组串详情配置",
      dialog_visible: props.isVisible,
      parameterDescriptionVisible: false,

      configTableList: [],

      isBatchConfig: false,
    });

    const saveDialog = () => {
      that.listLoading = true;
      let seriesConfigChangeVos = JSON.parse(JSON.stringify(that.configTableList));

      for (let i = 0; i < seriesConfigChangeVos.length; i++) {
        if (!seriesConfigChangeVos[i].moduleFactory) {
          ElMessage({ type: "error", showClose: true, message: `请选择组件厂家（第${i + 1}行）！` });
          that.listLoading = false;
          return;
        }

        if (!seriesConfigChangeVos[i].moduleModel) {
          ElMessage({ type: "error", showClose: true, message: `请选择组件型号（第${i + 1}行）！` });
          that.listLoading = false;
          return;
        }

        if (!seriesConfigChangeVos[i].seriesName) {
          ElMessage({ type: "error", showClose: true, message: `请输入组串名称（第${i + 1}行）！` });
          that.listLoading = false;
          return;
        }

        if (!seriesConfigChangeVos[i].moduleNum) {
          ElMessage({ type: "error", showClose: true, message: `请输入组件数量（第${i + 1}行）！` });
          that.listLoading = false;
          return;
        }

        seriesConfigChangeVos[i].deviceId = props.activeEditInfo.id;
      }

      saveSeriesConfigList({ deviceId: props.activeEditInfo.id, seriesConfigChangeVos: seriesConfigChangeVos }).then(() => {
        emit("changeEvent");
        that.dialog_visible = false;
        ElMessage({ type: "success", showClose: true, message: "操作成功！" });
      }).catch(() => {
        that.listLoading = false;
      });
    };

    // 根据组件厂家 跟 组件型号获取对应的组件信息
    const changeModuleLibraryListFun = (row, index, fieldName) => {
      // let newModuleConfig = JSON.parse(JSON.stringify(row));
      // let findModuleFactoryList = that.moduleLibraryList.filter(item => item.moduleFactory === row.moduleFactory);
      // // console.log(findModuleFactoryList);
      // if(findModuleFactoryList && findModuleFactoryList.length){
      //   let findItem = findModuleFactoryList.find(item => item.moduleModel === row.moduleModel);
      //   if(findItem){
      //     delete findItem.id;
      //     for(let key in findItem) newModuleConfig[key] = findItem[key];
      //   }
      // }
      // // 组件容量
      // newModuleConfig.seriesCapacity = calcNumberFun(newModuleConfig.moduleNum, newModuleConfig.maxPower, '*');
      // that.configTableList[index] = JSON.parse(JSON.stringify(newModuleConfig));

      let configTableList = JSON.parse(JSON.stringify(that.configTableList));
      if (fieldName === 'moduleFactory') delete configTableList[index].moduleModel; // 厂家改变，删除当前型号

      for (let i = 0; i < configTableList.length; i++) {
        // 批量配置
        if (that.isBatchConfig) {
          if (!configTableList[i][fieldName] && configTableList[i][fieldName] !== 0) {
            configTableList[i][fieldName] = row[fieldName];
          }

          if (fieldName === 'moduleFactory' && !configTableList[i].moduleModel) {
            delete configTableList[i].moduleModel; // 厂家改变，删除型号
          }
        }

        // 根据组件厂家 跟 组件型号获取对应的组件信息
        let findModuleFactoryList = that.moduleLibraryList.filter(item => item.moduleFactory === configTableList[i].moduleFactory);
        // console.log(findModuleFactoryList);
        if (findModuleFactoryList && findModuleFactoryList.length) {
          let findItem = findModuleFactoryList.find(item => item.moduleModel === configTableList[i].moduleModel);
          if (findItem) {
            delete findItem.id;
            for (let key in findItem) configTableList[i][key] = findItem[key];
          }
        } else {
          delete configTableList[i].moduleModel; // 如果厂家没有选择 ，型号不赋值
        }
        // 组件容量
        configTableList[i].seriesCapacity = calcNumberFun(configTableList[i].moduleNum, configTableList[i].maxPower, '*');
      }
      that.configTableList = JSON.parse(JSON.stringify(configTableList));
      // that.configTableList = [{},{},{},{},{},{},{},{},{},{},{}];

    };

    const clickDocumentButFun = () => {
      that.parameterDescriptionVisible = true;
    };

    const initParamConfigFun = () => {
      that.formDialog = Object.assign({}, props.activeEditInfo, that.formDialog);

      // 未配置组串配置
      if (props.activeEditInfo.configStatus === 1) {
        let mpptNum = props.activeEditInfo.mppt || 0;
        for (let i = 0; i < mpptNum; i++) that.configTableList[i] = { newId: generateUUID() };
        return;
      };
      // 根据设备id查询组串配置信息
      findSeriesConfigInfo({ deviceId: props.activeEditInfo.id }).then(res => {
        let returnDataList = res.data ?? [];
        for (let i = 0; i < returnDataList.length; i++) returnDataList[i].newId = returnDataList[i].id;
        that.configTableList = JSON.parse(JSON.stringify(returnDataList));
        // that.configTableList = [{},{},{},{},{},{},{},{},{},{},{}];

      });
    };

    const getRowKeys = (row) => {
      return row.newId;
    };

    // 查询组件库列表
    const queryModuleLibraryList = () => {
      findModuleLibraryList({ page: 1, size: 0, timer: new Date() }).then(res => {
        let moduleFactoryArray = [];
        let returnDataList = res.data ?? [];
        if (returnDataList && returnDataList.length) {
          for (let i = 0; i < returnDataList.length; i++) {
            if (JSON.stringify(moduleFactoryArray).indexOf(returnDataList[i].moduleFactory) === -1) {
              moduleFactoryArray.push(returnDataList[i]);
            }
          }
        }

        that.moduleLibraryList = JSON.parse(JSON.stringify(returnDataList));
        that.moduleFactoryArray = JSON.parse(JSON.stringify(moduleFactoryArray));
        console.log(that.moduleLibraryList, that.moduleFactoryArray, 'that.moduleLibraryList');
      });
    };

    const headerCellClassName = ({ row, column, rowIndex, columnIndex }) => {
      if (columnIndex >= 2) return 'el-table-header-cell-bg';
    };

    const watchVisible = watch([() => props.isVisible], ([newVisible]) => {
      that.dialog_visible = newVisible;
    });

    const watchDialogVisible = watch([() => that.dialog_visible], ([newDialogVisible]) => {
      emit("update:isVisible", newDialogVisible);
    });

    onMounted(() => {
      initParamConfigFun();
      queryModuleLibraryList();
    });

    return {
      ...toRefs(that), watchVisible, watchDialogVisible, saveDialog, initParamConfigFun, clickDocumentButFun, queryModuleLibraryList,
      headerCellClassName, changeModuleLibraryListFun, getRowKeys
    };
  }
});
</script>

<style scoped lang="scss">
.content_header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 10px;

  .content_header_left {
    display: flex;
    align-items: center;

    .mpptNum {
      margin-left: 12px;
    }
  }
}
</style>