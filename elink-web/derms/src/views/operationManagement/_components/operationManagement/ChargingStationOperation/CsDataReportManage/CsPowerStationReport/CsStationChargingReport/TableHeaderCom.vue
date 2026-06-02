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
          <TableTopCardCom :cardInfo="item" :fieldValue="return_data_info[item.fieldName]" :chargingType="componentName === 'CsStationV2GReport' ? 2 : 1" />
        </el-col>
      </template>
    </el-row>
  </div>
</template>

<script>
import {Folder} from "@element-plus/icons-vue";
import {getNowDateAll} from "@/utils/dateTime";
import {exportCustomExcel} from "@/common/exportExcel";
import {reactive, toRefs, defineComponent, onMounted, watch} from "vue";
import {TableTopCardCom} from "@/views/operationManagement/_components/operationManagement/PublicComponents";
import {findSiteChargeReport} from "@/api/operationManagement/CsDataReportManage";
import {calcNumberFun, isNumber} from "@/utils";
import {ElMessage} from "element-plus";

export default defineComponent({
  name: "ChargingTableHeaderCom",
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
      default: "CsStationChargingReport"
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
      chargingType: 1, // 1: 充  2：放
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
      chargingTableList: [
        {width: 25, key: "siteName", name: "站点名称"},
        {width: 15, key: "province", name: "所在省"},
        {width: 15, key: "city", name: "所在市"},
        {width: 25, key: "accountName", name: "商户"},
        {width: 15, key: "dcPileNum", name: "直流桩数"},
        {width: 15, key: "dcGunNum", name: "直流枪数"},
        {width: 15, key: "acPileNum", name: "交流桩数"},
        {width: 15, key: "acGunNum", name: "交流枪数"},
        {width: 20, key: "dcRatedPower", name: "直流额定功率（kW）"},
        {width: 20, key: "acRatedPower", name: "交流额定功率（kW）"},
        {width: 20, key: "dcTotalQt", name: "直流充电量（度）"},
        {width: 20, key: "acTotalQt", name: "交流充电量（度）"},

        {width: 20, key: "totalQt", name: "总充电量（度）"},
        {width: 20, key: "jqt", name: "尖时充电量（度）"},
        {width: 20, key: "fqt", name: "峰时充电量（度）"},
        {width: 20, key: "pqt", name: "平时充电量（度）"},
        {width: 20, key: "gqt", name: "谷时充电量（度）"},
        {width: 20, key: "totalDuration", name: "总充电时长（时）"},
        {width: 20, key: "orderCount", name: "订单数量（笔）"},
      ],
      dischargingTableList:[
        {width: 25, key: "siteName", name: "站点名称"},
        {width: 15, key: "province", name: "所在省"},
        {width: 15, key: "city", name: "所在市"},
        {width: 25, key: "accountName", name: "商户"},
        {width: 15, key: "v2gPileNum", name: "V2G桩数"},
        {width: 15, key: "v2gGunNum", name: "V2G枪数"},
        {width: 20, key: "v2gRatedPower", name: "V2G额定功率（kW）"},
        {width: 20, key: "v2gTotalQt", name: "总放电量（度）"},
        {width: 20, key: "jqt", name: "尖时放电量（度）"},
        {width: 20, key: "fqt", name: "峰时放电量（度）"},
        {width: 20, key: "pqt", name: "平时放电量（度）"},
        {width: 20, key: "gqt", name: "谷时放电量（度）"},
        {width: 20, key: "totalDuration", name: "总放电时长（时）"},
        {width: 20, key: "orderCount", name: "订单数量（笔）"},
      ]
    });


    // 查询电站充放电报表
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

      formInline.runMode = props.componentName === 'CsStationV2GReport' ? 1 : 0;
      findSiteChargeReport({page: 1, size: 0, ...formInline}).then(res => {
        let returnDataInfo = JSON.parse(JSON.stringify(res.data ? res.data : {}));
        let exportExcelList = returnDataInfo.dataReportInfoList ?? [];

        // 充电报表 需要把直流数据跟 V2G数据相加
        if(props.componentName === 'CsStationChargingReport'){
          if(exportExcelList && exportExcelList.length){
            exportExcelList.forEach(item=>{
              item.dcGunNum = calculationNumFun(item.dcGunNum,item.v2gGunNum);
              item.dcPileNum = calculationNumFun(item.dcPileNum,item.v2gPileNum);
              item.dcTotalQt = calculationNumFun(item.dcTotalQt,item.v2gTotalQt);
              item.dcRatedPower = calculationNumFun(item.dcRatedPower,item.v2gRatedPower);
            });
          }
        }

        that.exportLoading = false;
        let tableExportList = JSON.parse(JSON.stringify(that.chargingTableList));
        if(props.componentName === 'CsStationV2GReport') tableExportList = JSON.parse(JSON.stringify(that.dischargingTableList));
        exportCustomExcel(tableExportList, exportExcelList, `电站_${ that.titleName }_${ formInline.startTime }~${ formInline.endTime }区间数据 - ${ getNowDateAll() }`);
      }).catch(() => {
        that.exportLoading = false;
      });
    };

    const calculationNumFun = (num1,num2)=>{
      if(isNumber(num1) || isNumber(num2)){
        return calcNumberFun(num1 ?? 0,num2 ?? 0,'+');
      }
      return '';
    };

    const watchReturnDataInfo = watch([() => props.returnDataInfo, () => props.formInline], ([newReturnDataInfo, newFormInline]) => {
      that.return_data_info = JSON.parse(JSON.stringify(newReturnDataInfo));
      that.searchFormInline = JSON.parse(JSON.stringify(newFormInline));
    }, {deep: true, immediate: true});


    const watchComponentName = watch(()=>props.componentName,(newComponentName)=>{
      that.chargingType = newComponentName === 'CsStationV2GReport' ? 2 : 1;
      that.titleName = that.chargingType === 2 ? 'V2G报表' : '充电报表';
    },{deep: true, immediate: true});

    return {...toRefs(that), clickExportButFun, watchReturnDataInfo, calculationNumFun, watchComponentName};
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