<template>
  <div class="orderTotalAndExportCom">
    <div class="content_left" v-if="componentName === 'CsTransactionChargingOrder'">
      <template v-for="(item,index) in list" :key="index">
        <div class="content_left_li">
          <div class="iconName">
            <span class="iconfont" :class="item.iconName"></span>
          </div>
          <div class="name">{{ item.name }}：</div>
          <div class="number" :style="{ color: item.color }">
            <span>{{ $filters.moneyTwoNum(return_data_info[item.fieldName]) }}</span>
          </div>
          <div class="unit">元</div>
        </div>
      </template>
    </div>
    <div class="content_right">
      <el-button :disabled="exportLoading" :icon="Folder" :loading="exportLoading" @click="clickExportButFun">导出</el-button>
    </div>
  </div>
</template>
<script lang="ts">
import {getNowDateAll} from "@/utils/dateTime";
import {Folder} from "@element-plus/icons-vue";
import {exportCustomExcel} from "@/common/exportExcel";
import {defineComponent, reactive, toRefs, watch} from "vue";
import {queryDischargeTradeList, queryRechargeTradeList} from "@/api/operationManagement/CsTransactionDetails";

export default defineComponent({
  name: "OrderTotalAndExportCom",
  props:{
    // 导出搜索条件
    formInline:{
      type: Object,
      default: ()=>{
        return { };
      }
    },
    returnDataInfo:{
      type: Object,
      default: ()=>{
        return { };
      }
    },
    componentName:{
      type: String,
      default: ""
    }
  },
  setup(props) {

    const that = reactive({
      Folder,
      searchFormInline: {},
      return_data_info: {},
      exportLoading: false,
      list: [
        {name: "收入", fieldName: "incomeMoney", color: "#F40A0A", iconName: ""},
        {name: "支出", fieldName: "outcomeMoney", color: "#6ECF37", iconName: ""},
        {name: "收支净额", fieldName: "netMoney", color: "#FD944A", iconName: ""}
      ],
      // 充电订单导出列表
      chargingOrderList:[
        {width: 40, key: "orderNum", name: "订单号"},
        {width: 40, key: "flowNum", name: "交易流水号"},
        {width: 15, key: "tradeType", name: "交易类型",filterName:'tradeCoType'},
        {width: 15, key: "tradeMoney", name: "金额（元）"},
        {width: 25, key: "siteName", name: "站点名称"},
        {width: 25, key: "mchName", name: "商户名称"},
        {width: 15, key: "mchId", name: "商户ID"},
        {width: 25, key: "tradeWay", name: "交易方式",filterName:'tradeWay'},
        {width: 40, key: "createTime", name: "支付时间"},
        {width: 40, key: "updateTime", name: "完成时间"},
        {width: 15, key: "tradeStatus", name: "交易状态",filterName:'tradeStatus'},
        {width: 20, key: "phoneNum", name: "手机号"},
      ],
      // V2G钱包交易
      v2GWalletOrderList:[
        {width: 40, key: "orderNum", name: "订单号"},
        {width: 15, key: "tradeType", name: "交易类型",filterName:'tradeV2GType'},
        {width: 25, key: "tradeMoney", name: "金额（元）"},
        {width: 25, key: "siteName", name: "站点名称"},
        {width: 25, key: "mchName", name: "商户名称"},
        {width: 15, key: "mchId", name: "商户ID"},
        {width: 40, key: "createTime", name: "支付时间"},
        {width: 40, key: "updateTime", name: "完成时间"},
        {width: 15, key: "tradeStatus", name: "交易状态",filterName:'tradeStatus'},
        {width: 20, key: "phoneNum", name: "手机号"},
      ]
    });

    // 导出
    const clickExportButFun = ()=>{
      that.exportLoading = true;
      if(props.componentName === 'CsTransactionChargingOrder') findRechargeTradeList();
      if(props.componentName === 'CsTransactionV2GWalletOrder') findDischargeTradeList();
    };

    // 查询充电交易列表
    const findRechargeTradeList = ()=>{
      let formInline = JSON.parse(JSON.stringify(that.searchFormInline));
      if(formInline.startAlsoDate){
        formInline.startTime = formInline.startAlsoDate[0];
        formInline.endTime = formInline.startAlsoDate[1];
        delete formInline.startAlsoDate;
      }
      queryRechargeTradeList({page: 1, size:9999999, ...formInline}).then(res => {
        let returnDataInfo = JSON.parse(JSON.stringify(res.data ? res.data : {}));
        let exportExcelList = returnDataInfo.pageDto.items;
        exportCustomExcel(that.chargingOrderList, exportExcelList, `充电订单交易明细 - ${ getNowDateAll() }`);
        that.exportLoading = false;
      }).catch(() => {
        that.exportLoading = false;
      });
    };

    // 查询V2G钱包交易列表
    const findDischargeTradeList= ()=>{
      let formInline = JSON.parse(JSON.stringify(that.searchFormInline));
      if(formInline.startAlsoDate){
        formInline.startTime = formInline.startAlsoDate[0];
        formInline.endTime = formInline.startAlsoDate[1];
        delete formInline.startAlsoDate;
      }
      queryDischargeTradeList({page: 1, size: 0, ...formInline}).then(res => {
        let returnDataInfo = JSON.parse(JSON.stringify(res.data ? res.data : {}));
        let exportExcelList = returnDataInfo.pageDto.items;
        exportCustomExcel(that.v2GWalletOrderList, exportExcelList, `V2G钱包交易明细 - ${ getNowDateAll() }`);
        that.exportLoading = false;
      }).catch(() => {
        that.exportLoading = false;
      });
    };

    const watchReturnDataInfo = watch([()=>props.returnDataInfo,()=>props.formInline],([newReturnDataInfo,newFormInline])=>{
      that.return_data_info = JSON.parse(JSON.stringify(newReturnDataInfo));
      that.searchFormInline = JSON.parse(JSON.stringify(newFormInline));
    },{ deep: true,immediate: true });

    return {...toRefs(that), watchReturnDataInfo, clickExportButFun, findRechargeTradeList, findDischargeTradeList};
  }
});
</script>
<style lang="scss" scoped>
.orderTotalAndExportCom{
  display: flex;
  align-items: center;

  .content_left{
    display: flex;
    align-items: center;
    margin-right: 14px;

    .content_left_li{
      display: flex;
      align-items: center;
      font-size: 14px;
      color: #FFFFFF;
      margin-right: 16px;

      .number{
        font-size: 16px;
        font-weight: bold;
        margin-right: 2px;
      }

      &:last-child{
        margin-right: 0;
      }
    }
  }
}
</style>