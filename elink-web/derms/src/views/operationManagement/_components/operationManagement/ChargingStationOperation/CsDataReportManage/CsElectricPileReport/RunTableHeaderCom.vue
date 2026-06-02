<template>
  <TableHeaderTitle title="运行报表">
    <template #content>
      <el-button :disabled="exportLoading" :icon="Folder" :loading="exportLoading" @click="clickExportButFun">导出
      </el-button>
    </template>
  </TableHeaderTitle>
</template>

<script>
import {ElMessage} from "element-plus";
import {Folder} from "@element-plus/icons-vue";
import {getNowDateAll} from "@/utils/dateTime";
import {exportCustomExcel} from "@/common/exportExcel";
import {reactive, toRefs, defineComponent, watch} from "vue";
import {findPileRunReport} from "@/api/operationManagement/CsDataReportManage";

export default defineComponent({
  name: "RunTableHeaderCom",
  props: {
    // 导出搜索条件
    formInline: {
      type: Object,
      default: () => {
        return {};
      }
    },
    siteAllIds: {
      type: Array,
      default: () => []
    },
    returnDataInfo: {
      type: Object,
      default: () => {
        return {};
      }
    },
  },
  setup(props) {

    const that = reactive({
      Folder,
      searchFormInline: {},
      return_data_info: {},
      exportLoading: false,

      // 导出列表
      tableExportList: [
        {width: 25, key: "siteName", name: "站点名称"},
        {width: 25, key: "pileCode", name: "电桩编号"},
        {width: 10, key: "gunCode", name: "枪编号"},
        {width: 15, key: "typeTd", name: "桩类型",filterName: "pileType"},
        {width: 20, key: "ratedPower", name: "额定功率（kW）"},
        {width: 20, key: "chargeDuration", name: "充电时长（时）"},
        {width: 20, key: "dischargeDuration", name: "V2G时长（时）"},
        {width: 20, key: "timeUtilize", name: "时间利用率（%）"},
        {width: 20, key: "dayAvgQt", name: "日均充电量"},
        {width: 20, key: "onlineDuration", name: "在线时长"},
        {width: 20, key: "onlineRate", name: "设备在线率"},
        {width: 20, key: "alarmNum", name: "告警次数"},
        {width: 20, key: "startFailNum", name: "启动失败次数"},
        {width: 20, key: "abnormalOrderNum", name: "异常订单数量"},
        {width: 20, key: "abnormalOrderRate", name: "订单异常率"},
      ],
    });

    const clickExportButFun = () => {
      that.exportLoading = true;
      let formInline = JSON.parse(JSON.stringify(that.searchFormInline));
      if(formInline.startAlsoDate){
        formInline.startTime = formInline.startAlsoDate[0] + ' 00:00:00';
        formInline.endTime = formInline.startAlsoDate[1] + ' 23:59:59';
        delete formInline.startAlsoDate;
      }

      if(!formInline.siteIds || !formInline.siteIds.length){
        if(!props.siteAllIds || !props.siteAllIds.length){
          that.exportLoading = false;
          ElMessage({type: "error", showClose: true, message: "请先选择查询的站点！"});
          return;
        }
        formInline.siteIds = JSON.parse(JSON.stringify(props.siteAllIds));
      }

      findPileRunReport({page: 1, size: 0, ...formInline}).then(res => {
        let exportExcelList = JSON.parse(JSON.stringify(res.data ? res.data : {}));
        exportCustomExcel(that.tableExportList, exportExcelList, `电桩_运行报表_${ formInline.startTime }~${ formInline.endTime }区间数据 - ${getNowDateAll()}`);
        that.exportLoading = false;
      }).catch(()=>{
        that.exportLoading = false;
      });
    };

    const watchReturnDataInfo = watch([() => props.returnDataInfo, () => props.formInline], ([newReturnDataInfo, newFormInline]) => {
      that.return_data_info = JSON.parse(JSON.stringify(newReturnDataInfo));
      that.searchFormInline = JSON.parse(JSON.stringify(newFormInline));
    }, {deep: true, immediate: true});

    return {...toRefs(that), clickExportButFun, watchReturnDataInfo};
  }
});
</script>

<style lang="scss" scoped>
</style>