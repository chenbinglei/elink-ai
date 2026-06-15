<template>
  <div class="tableHeaderCom">
    <TableHeaderTitle :title="titleName">
      <template #content>
        <el-button :disabled="exportLoading" :icon="Folder" :loading="exportLoading" @click="clickExportButFun">导出</el-button>
      </template>
    </TableHeaderTitle>
    <el-row class="table_header_bottom" :gutter="12">
      <template v-for="(item,index) in list" :key="index">
        <el-col :lg="3" :md="8" :sm="12" :xs="24">
          <TableTopCardCom :cardInfo="item" :fieldValue="return_data_info[item.fieldName]" :chargingType="chargingType"></TableTopCardCom>
        </el-col>
      </template>
    </el-row>
  </div>
</template>

<script lang="ts">
import $filters from "@/common/filters";
import {Folder} from "@element-plus/icons-vue";
import {getNowDateAll} from "@/utils/dateTime";
import {exportCustomExcel} from "@/common/exportExcel";
import {reactive, toRefs, defineComponent, watch} from "vue";
import {findPileChargeReport} from "@/api/operationManagement/CsDataReportManage";
import {TableTopCardCom} from "@/views/operationManagement/_components/operationManagement/PublicComponents";
import {ElMessage} from "element-plus";

export default defineComponent({
  name: "TableHeaderCom",
  components:{TableTopCardCom},
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
    componentName:{
      type: String,
      default: "CsPileChargingReport"
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
      titleName: "",
      chargingType: 1,
      searchFormInline: {},
      return_data_info: {},
      exportLoading: false,
      list: [
        {name: "订单总数量（笔）", fieldName: "orderTotalCount"},
        {name: "总充/放电电量（度）", fieldName: "totalQt"},
        {name: "尖时充/放电量（度）", fieldName: "jtotalQt"},
        {name: "峰时充/放电量（度）", fieldName: "ftotalQt"},
        {name: "平时充/放电量（度）", fieldName: "ptotalQt"},
        {name: "谷时充/放电量（度）", fieldName: "gtotalQt"},
        {name: "深谷充/放电量（度）", fieldName: "fukayaTotalQt"},
      ],

      // 导出列表
      tableExportList: [
        {width: 25, key: "siteName", name: "站点名称"},
        {width: 25, key: "pileCode", name: "电桩编号"},
        {width: 10, key: "gunCode", name: "枪编号"},
        {width: 15, key: "typeTd", name: "桩类型",filterName: "pileType"},
        {width: 20, key: "ratedPower", name: "额定功率（kW）"},
        {width: 20, key: "totalQt", name: "总充/放电量（度）"},
        {width: 20, key: "jqt", name: "尖时充/放电量（度）"},
        {width: 20, key: "fqt", name: "峰时充/放电量（度）"},
        {width: 20, key: "pqt", name: "平时充/放电量（度）"},
        {width: 20, key: "gqt", name: "谷时充/放电量（度）"},
        {width: 20, key: "fukayaQt", name: "深谷充/放电量（度）"},
        {width: 20, key: "totalDuration", name: "充/放电时长（时）"},
        {width: 20, key: "orderCount", name: "订单数（笔）"},
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

      formInline.runMode = props.componentName === 'CsPileV2GReport' ? 1 : 0;
      findPileChargeReport({page: 1, size: 0, ...formInline}).then(res => {
        let returnDataInfo = JSON.parse(JSON.stringify(res.data ? res.data : {}));
        let exportExcelList = returnDataInfo.dataReportInfoList ?? [];

        that.exportLoading = false;
        let tableExportList = JSON.parse(JSON.stringify(that.tableExportList));
        tableExportList.forEach(item=>{
          item.name = $filters.chargingDisText(item.name,props.componentName === 'CsPileV2GReport' ? 2 : 1);
        });
        exportCustomExcel(tableExportList, exportExcelList, `电桩_${ that.titleName }_${ formInline.startTime }~${ formInline.endTime }区间数据 - ${ getNowDateAll() }`);
      }).catch(() => {
        that.exportLoading = false;
      });
    };

    const watchReturnDataInfo = watch([() => props.returnDataInfo, () => props.formInline], ([newReturnDataInfo, newFormInline]) => {
      that.return_data_info = JSON.parse(JSON.stringify(newReturnDataInfo));
      that.searchFormInline = JSON.parse(JSON.stringify(newFormInline));
    }, {deep: true, immediate: true});

    const watchComponentName = watch(()=>props.componentName,(newComponentName)=>{
      that.chargingType = newComponentName === 'CsPileV2GReport' ? 2 : 1;
      that.titleName = that.chargingType === 2 ? 'V2G报表' : '充电报表';
    },{deep: true, immediate: true});

    return {...toRefs(that), clickExportButFun, watchReturnDataInfo, watchComponentName};
  }
});
</script>

<style lang="scss" scoped>
@media screen and (min-width: 1200px) {
  .el-col-lg-3 {
    flex: 1;
    max-width: none;
  }
}
</style>