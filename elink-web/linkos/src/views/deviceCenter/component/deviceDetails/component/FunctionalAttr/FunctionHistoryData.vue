<template>
  <Dialog v-model:isVisible="dialog_visible" :manualEnterClose="false" :title="titleName" confirmText="导出数据" width="980" @confirm="clickExportDataFun">
    <template v-slot:content>
      <div class="dialog-main">

        <div class="dialog-main_top">
          <div class="top_list">
            <div class="top_list_left">功能名称：</div>
            <div class="top_list_right">{{ $filters.moreData(cardInfo.functionName) }}</div>
          </div>
          <div class="top_list">
            <div class="top_list_left">功能标识：</div>
            <div class="top_list_right">{{ $filters.moreData(cardInfo.functionLogo) }}</div>
          </div>
          <div class="top_list">
            <div class="top_list_left">数据类型：</div>
            <div class="top_list_right">{{ $filters.dataType(cardInfo.dataType) }}</div>
          </div>
        </div>

        <el-form :model="formInline" inline>
          <el-form-item label="时间范围：">
            <el-date-picker v-model="formInline.pickerDate" :clearable="false" :disabled-date="pickerOptions.disabledDate" placeholder="开始时间"
                            type="Date" value-format="YYYY-MM-DD"/>
          </el-form-item>
          <el-form-item label="数据间隔：">
            <div class="flex ai-center timeInterval">
              <el-input-number v-model="formInline.timeNumber" :min="1" controls-position="right" style="width: 120px"></el-input-number>
              <el-select v-model="formInline.timeType" placeholder="请选择" style="width: 90px">
                <el-option v-for="item in timeTypeArray" :key="item.id" :label="item.name" :value="item.id"></el-option>
              </el-select>
            </div>
          </el-form-item>
          <el-form-item>
            <el-button :icon="Search" class="whiteFontButtons" @click="findDeviceFunctionValueList">查询</el-button>
          </el-form-item>
          <el-form-item style="float: right">
            <TabBackground v-model:tabsIndex="componentName" :tabsArray="tabsArray"></TabBackground>
          </el-form-item>
        </el-form>
        <div v-loading="listLoading" class="content_body">
          <component :is="componentName" :cardInfo="cardInfo" :returnDataInfo="returnDataInfo"></component>
        </div>
      </div>
    </template>
  </Dialog>
</template>

<script>
import {useRoute} from "vue-router";
import {ElMessage} from "element-plus";
import HistoryDataChart from "./HistoryDataChart";
import HistoryDataTable from "./HistoryDataTable";
import {RefreshRight, Search} from '@element-plus/icons-vue';
import {exportCustomExcel} from "@/common/common/exportExcel";
import {getNowDate, pickerOptionsSixMonth} from "@/utils/dateTime";
import {queryDeviceFunctionValueList} from "@/api/deviceCenter/deviceList";
import {getCurrentInstance, reactive, toRefs, watch, onMounted, defineComponent} from "vue";

export default defineComponent({
  name: "FunctionHistoryData",
  components: {HistoryDataChart, HistoryDataTable},
  props: {
    isVisible: {
      type: Boolean,
      default: false
    },
    cardInfo: {
      type: Object,
      default: () => {
        return {}
      }
    }
  },
  setup(props) {
    const route = useRoute();
    const {emit} = getCurrentInstance();

    const that = reactive({
      Search,
      RefreshRight,
      returnDataInfo: {},
      listLoading: false,
      titleName: "功能历史数据",
      activeDeviceId: route.query.id, // 当前设备模型id
      dialog_visible: props.isVisible,

      oldFormInline: {},
      componentName: "HistoryDataChart",
      pickerOptions: pickerOptionsSixMonth(),
      formInline: {pickerDate: getNowDate(), timeNumber: 1, timeType: 'm'},
      tabsArray: [{id: "HistoryDataChart", name: "图表"}, {id: "HistoryDataTable", name: "表格"}],
      timeTypeArray: [{id: "y", name: "年"}, {id: "n", name: "月"}, {id: "d", name: "天"}, {id: "h", name: "时"}, {id: "m", name: "分"}],
    })

    const findDeviceFunctionValueList = () => {
      that.listLoading = true;
      let formInline = JSON.parse(JSON.stringify(that.formInline));
      formInline.startTime = formInline.pickerDate + " 00:00:00";
      formInline.endTime = formInline.pickerDate + " 23:59:59";
      formInline.timeInterval = formInline.timeNumber + formInline.timeType;
      queryDeviceFunctionValueList({...formInline, functionId: props.cardInfo.functionId, deviceId: that.activeDeviceId}).then(res => {
        let valueListNum = 1;
        let returnDataInfo = res.data ? res.data : {};
        // 先查看需要生成几条数据线
        if (returnDataInfo.valueList && returnDataInfo.valueList.length) {
          for (let i = 0; i < returnDataInfo.valueList.length; i++) {
            try {
              let item_arr = JSON.parse(returnDataInfo.valueList[i]);
              if (Array.isArray(item_arr) && valueListNum < item_arr.length) valueListNum = item_arr.length;
            } catch (e) {
              // console.log(returnDataInfo.valueList);
            }
          }
        }

        for (let i = 0; i < valueListNum; i++) {
          if (!returnDataInfo['seriesList' + i]) returnDataInfo['seriesList' + i] = [];
          if (returnDataInfo.dateList && returnDataInfo.dateList.length) {
            for (let j = 0; j < returnDataInfo.dateList.length; j++) {
              // console.log(returnDataInfo.dateList[i]);
              let active_item_arr = null;
              try {
                active_item_arr = JSON.parse(returnDataInfo.valueList[j]);
              } catch (e) {}
              // console.log(active_item_arr);
              let active_value = Array.isArray(active_item_arr) ? active_item_arr[i] : active_item_arr;
              returnDataInfo['seriesList' + i].push(active_value);
            }
          }
        }
        returnDataInfo.valueListNum = valueListNum;
        // console.log(returnDataInfo);
        that.returnDataInfo = JSON.parse(JSON.stringify(returnDataInfo));
        that.listLoading = false;
      }).catch(() => {
        that.listLoading = false;
      })
    }

    const clickExportDataFun = () => {
      let dateList = [];
      let tableHeader = [];
      if (!that.returnDataInfo.dateList || !that.returnDataInfo.dateList.length) {
        ElMessage({type: "warning", showClose: true, message: `暂无数据！`});
        return
      }

      for (let i = 0; i < that.returnDataInfo.dateList.length; i++) {
        if (i === 0) tableHeader.push({width: 20, key: "dataTime", name: "时间"});
        dateList.push({dataTime: that.returnDataInfo.dateList[i]});
        for (let j = 0; j < that.returnDataInfo.valueListNum; j++) {
          if (i === 0) tableHeader.push({width: 15, key: `value${j}`, name: `第${j + 1}个值`});
          dateList[i]['value' + j] = that.returnDataInfo['seriesList' + j][i]
        }
      }

      let endTime = that.formInline.pickerDate[1];
      let startTime = that.formInline.pickerDate[0];
      exportCustomExcel(tableHeader, dateList, `功能历史数据 - ${props.cardInfo.functionName}，${startTime}~${endTime}`, "Sheet1");
    }

    const watchVisible = watch([() => props.isVisible], ([newVisible]) => {
      that.dialog_visible = newVisible;
    });

    const watchDialogVisible = watch([() => that.dialog_visible], ([newDialogVisible]) => {
      emit("update:isVisible", newDialogVisible);
    });

    onMounted(() => {
      that.oldFormInline = JSON.parse(JSON.stringify(that.formInline));
      findDeviceFunctionValueList();
    })

    return {...toRefs(that), watchVisible, watchDialogVisible, findDeviceFunctionValueList, clickExportDataFun}
  }
})
</script>

<style lang="scss" scoped>
.dialog-main_top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;

  .top_list {
    display: flex;
    align-items: center;

    .top_list_left {
      color: #BBBBBB;
      font-size: 14px;
    }

    .top_list_right {
      color: #242424;
      font-size: 14px;
    }
  }
}

.timeInterval {
  border: var(--el-border);
  border-radius: var(--el-input-border-radius, var(--el-border-radius-base));

  :deep(.el-input__wrapper) {
    border-radius: 0;
  }
}

.content_body {
  height: 380px;
}
</style>
