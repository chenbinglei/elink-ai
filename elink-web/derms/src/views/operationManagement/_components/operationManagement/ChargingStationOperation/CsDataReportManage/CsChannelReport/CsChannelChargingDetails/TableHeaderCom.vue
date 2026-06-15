<template>
  <TableHeaderTitle title="渠道充电明细">
    <template #content>
      <el-button :disabled="exportLoading" :icon="Folder" :loading="exportLoading" @click="clickExportButFun">导出
      </el-button>
    </template>
  </TableHeaderTitle>
</template>

<script lang="ts">
import {Folder} from "@element-plus/icons-vue";
import {exportCustomExcel} from "@/common/exportExcel";
import {reactive, toRefs, defineComponent, watch} from "vue";
import {findPlatformDetailsList} from "@/api/operationManagement/CsDataReportManage";
import {getCurrentMonthFirstDay, getCurrentMonthLastDay, getDaysFromCurrentTime, getNowDateAll, isMonth} from "@/utils/dateTime";
import {ElMessage} from "element-plus";

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
  },
  setup(props) {

    const that = reactive({
      Folder,
      searchFormInline: {},
      exportLoading: false,

      // 导出列表
      tableExportList: [
        {width: 25, key: "platformName", name: "订单来源"},
        {width: 25, key: "siteName", name: "站点名称"},
        {width: 15, key: "chargeOrderNum", name: "充电订单数（笔）"},
        {width: 20, key: "chargeQt", name: "充电电量（度）"},
        {width: 20, key: "chargeDuration", name: "充电时长（时）"},
        {width: 20, key: "orderNumRatio", name: "订单量占比"},
        {width: 20, key: "orderAmount", name: "订单总金额（元）"},
        {width: 20, key: "chargeElecMony", name: "充电电费（元）"},
        {width: 20, key: "chargeServiceMony", name: "充电服务费（元）"},
        {width: 20, key: "countDate", name: "统计时间"}
      ],
    });

    const clickExportButFun = () => {
      that.exportLoading = true;
      let formInline = JSON.parse(JSON.stringify(that.searchFormInline));

      if(formInline.timeType === 1 &&formInline.startAlsoDate){
        formInline.startTime = formInline.startAlsoDate[0] + ' 00:00:00';
        formInline.endTime = formInline.startAlsoDate[1] + ' 23:59:59';
      }

      if(formInline.timeType === 2 && formInline.monthTimeDate){
        formInline.startTime = getCurrentMonthFirstDay(formInline.monthTimeDate[0]) + ' 00:00:00';

        let endTime = getCurrentMonthLastDay(formInline.monthTimeDate[1]);
        let active_end_time = getDaysFromCurrentTime(-1) + ' 23:59:59';
        formInline.endTime = isMonth(endTime) ? active_end_time : endTime + " 23:59:59";
      }

      if(!formInline.siteIds || !formInline.siteIds.length){
        if(!props.siteAllIds || !props.siteAllIds.length){
          that.exportLoading = false;
          ElMessage({type: "error", showClose: true, message: "请先选择查询的站点！"});
          return;
        }
        formInline.siteIds = JSON.parse(JSON.stringify(props.siteAllIds));
      }

      findPlatformDetailsList({page: 1, size: 0, ...formInline}).then(res => {
        that.exportLoading = false;
        let exportExcelList = JSON.parse(JSON.stringify(res.data ? res.data : {}));
        exportCustomExcel(that.tableExportList, exportExcelList, `渠道充电明细_${ formInline.startTime }~${ formInline.endTime }区间数据 - ${ getNowDateAll() }`);
      }).catch(() => {
        that.exportLoading = false;
      });
    };

    const watchReturnDataInfo = watch([() => props.formInline], ([newFormInline]) => {
      that.searchFormInline = JSON.parse(JSON.stringify(newFormInline));
    }, {deep: true, immediate: true});

    return {...toRefs(that), clickExportButFun, watchReturnDataInfo};
  }
});
</script>

<style lang="scss" scoped>
</style>