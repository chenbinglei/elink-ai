<template>
  <div class="content_body">

    <div class="content_top_card" :class="'content_top_card' + orderType ">
      <div class="content_top_card_h">
        <span>{{ $filters.chargingDisText('充/放电电量（度）', orderType) }}</span>
        <span class="number">{{ $filters.moreData(returnDataInfo.totalQt) }}</span>
        <div class="orderStatus" :class="'orderStatus' + returnDataInfo.orderStatus">
          <span class="text">{{$filters.chargingDisText($filters.orderStatus(returnDataInfo.orderStatus),orderType) }}</span>
        </div>
      </div>
      <div class="content_top_card_c">
        <div class="card_c_li">
          <div class="card_c_li_title">金额（元）</div>
          <div class="card_c_li_num">{{ $filters.moneyTwoNum(returnDataInfo.totalCost) }}</div>
        </div>
        <div class="card_c_li">=</div>
        <div class="card_c_li">
          <div class="card_c_li_title">电费（元）</div>
          <div class="card_c_li_num">{{ $filters.moneyTwoNum(returnDataInfo.totalElect) }}</div>
        </div>
        <template v-if="orderType === 1">
          <div class="card_c_li">+</div>
          <div class="card_c_li">
            <div class="card_c_li_title">服务费（元）</div>
            <div class="card_c_li_num">{{ $filters.moneyTwoNum(returnDataInfo.totalFee) }}</div>
          </div>
        </template>
      </div>
    </div>

    <template v-for="(item,index) in list" :key="index">
      <!--      v-if="orderType !== 2 || orderType === 2 && item.fieldName !== 'repairOrderRecord' "-->
      <TitleView :title="item.name">
        <template #content>
          <div class="content_list">
            <el-row :gutter="12" class="content_list">
              <template v-for="(child,i) in item.children" :key="i">
                <template v-if="orderType !== 2 || orderType === 2 && child.fieldName !== 'totalFee'">
                  <el-col :sm="12" :lg="8" class="info_li">
                    <div class="flex_li_left">{{ $filters.chargingDisText(child.name, orderType) }}：</div>
                      <template v-if="item.fieldName">
                        <div class="flex_li_right" :class="[child.className ? child.className : '', child.className ? child.className + returnDataInfo[item.fieldName][child.fieldName] : '' ]">
                          <span v-if="child.filterName">{{ $filters.chargingDisText($filters[child.filterName](returnDataInfo[item.fieldName][child.fieldName]),orderType) }}</span>
                          <span v-else>{{ $filters.chargingDisText($filters.moreData(returnDataInfo[item.fieldName][child.fieldName]),orderType) }}</span>
                        </div>
                      </template>
                      <template v-else>
                        <div class="flex_li_right" :class="[child.className ? child.className : '', child.className ? child.className + returnDataInfo[child.fieldName] : '' ]">
                          <span v-if="child.filterName">{{ $filters.chargingDisText($filters[child.filterName](returnDataInfo[child.fieldName]),orderType) }}</span>
                          <span v-else>{{ $filters.chargingDisText($filters.moreData(returnDataInfo[child.fieldName]),orderType) }}</span>
                        </div>
                      </template>
                  </el-col>
                </template>
              </template>
            </el-row>
          </div>
        </template>
      </TitleView>
    </template>
  </div>
</template>

<script>
import {reactive, toRefs, defineComponent, watch} from "vue";

export default defineComponent({
  name: "DcOrderingInfo",
  props:{
    orderInfo:{
      type: Object,
      default:()=>{
        return { };
      }
    },
    // 1: 充 2： 放
    orderType: {
      type: Number,
      default: 1
    },
  },
  setup(props) {
    const that = reactive({
      returnDataInfo: {},
      list: [{
        name: "基本信息",
        children: [
          {name: "订单号", fieldName: "orderNum"},
          {name: "订单状态", fieldName: "orderStatus", filterName: "orderStatus",className:"orderStatus"},
          {name: "充/放电电量（度）", fieldName: "totalQt"},
          {name: "订单金额（元）", fieldName: "totalCost"},
          {name: "电费（元）", fieldName: "totalElect", filterName: "moneyTwoNum"},
          {name: "服务费（元）", fieldName: "totalFee", filterName: "moneyTwoNum"},
          {name: "充/放电前Soc", fieldName: "startSoc"},
          {name: "充/放电后Soc", fieldName: "endSoc"},
          {name: "创建订单时间", fieldName: "createTime"},
          {name: "开始充/放电时间", fieldName: "startTime"},
          {name: "结束充/放电时间", fieldName: "endTime"},
          {name: "充/放电时长", fieldName: "chargeDuration"},
          {name: "停止码", fieldName: "stopReason"},
          {name: "结束原因", fieldName: "stopDetailReason"},
          {name: "是否有序充/放电", fieldName: "isOrderly", filterName: "whetherOrNot"},
        ]
      }, {
        name: "设备信息",
        fieldName: "pileInfoData",
        children: [
          {name: "电站名称", fieldName: "siteName"},
          {name: "电站ID", fieldName: "siteId"},
          {name: "运营商名称", fieldName: "operatorName"},
          {name: "所在城市", fieldName: "city"},
          {name: "具体地址", fieldName: "address"},
          {name: "设备出厂编码", fieldName: "factoryCode"},
          {name: "电桩类型", fieldName: "pileType", filterName: "pileType"},
          {name: "桩编号", fieldName: "pileCode"},
          {name: "枪编号", fieldName: "gunCode"},
        ]
      },  {
        name: "补单信息",
        fieldName: "repairOrderRecord",
        children: [
          {name: "挂单时间", fieldName: "createTime"},
          {name: "补单时间", fieldName: "updateTime"},
          {name: "补单状态", fieldName: "repairStatus", filterName: "repairStatus",className:"repairStatus"},
          {name: "异常时长", fieldName: "exceptionTime"},
          {name: "操作人", fieldName: "repairOperator"},
        ]
      }, {
        name: "用户信息",
        fieldName: "userRecordDto",
        children: [
          {name: "账号类型", fieldName: "accountType", filterName: "accountType"},
          {name: "手机号", fieldName: "accountData"},
          {name: "启动方式", fieldName: "starter", filterName: "pileRunMode"},
          {name: "平台", fieldName: "platformName"},
          {name: "车牌号", fieldName: "plateNumber"},
          {name: "VIN码", fieldName: "busVin"},
          // {name: "电卡ID", fieldName: "cardNumber"},
          {name: "电卡卡号", fieldName: "cardNumber"},
          {name: "企业账户", fieldName: "enterpriseAccount"},
          {name: "车队名称", fieldName: "fleetName"},
          {name: "开票状态", fieldName: "invoicingState", filterName: "invoicingState",className:"invoicingState"},
        ]
      }]
    });

    const watchOrderInfo = watch(()=>props.orderInfo,(newOrderInfo)=>{
      let returnDataInfo = newOrderInfo ? newOrderInfo : {};
      // 设备信息
      returnDataInfo.pileInfoData = returnDataInfo.pileInfoData ?? {};
      returnDataInfo.pileInfoData.pileCode = returnDataInfo.pileCode;
      returnDataInfo.pileInfoData.gunCode = returnDataInfo.gunCode;

      // 补单信息
      returnDataInfo.repairOrderRecord = returnDataInfo.repairOrderRecord ?? {};
      if(!returnDataInfo.repairOrderRecord.repairStatus) returnDataInfo.repairOrderRecord.updateTime = "";

      // 用户信息
      returnDataInfo.userRecordDto = returnDataInfo.userRecordDto ?? {};
      returnDataInfo.userRecordDto.platformName = returnDataInfo.platformName;
      returnDataInfo.userRecordDto.accountType = returnDataInfo.accountType;
      returnDataInfo.userRecordDto.accountData = returnDataInfo.accountData;
      returnDataInfo.userRecordDto.cardNumber = returnDataInfo.cardNumber;
      returnDataInfo.userRecordDto.starter = returnDataInfo.starter;
      returnDataInfo.userRecordDto.busVin = returnDataInfo.busVin;

      that.returnDataInfo = JSON.parse(JSON.stringify(returnDataInfo));
    },{ deep: true,immediate:true });

    return { ...toRefs(that),watchOrderInfo };
  }
});
</script>

<style lang="scss" scoped>
.content_top_card{
  border-radius: 8px;
  margin-bottom: 24px;
  background: #ffffff08;
  box-sizing: border-box;
  border: 1px solid #4ab3ff4d;

  .content_top_card_h{
    padding: 16px 24px;
    box-sizing: border-box;
    border-radius: 8px 8px 0 0;
    background: #0094ff1a;
    font-size: 14px;
    color: #ffffffcc;
    position: relative;

    .number{
      color: #30f3ff;
      font-size: 21px;
      font-weight: bold;
      margin-left: 46px;
    }

    .orderStatus{
      position: absolute;
      right: 0;
      top: 0;
      padding: 6px 18px;
      border-radius: 0 8px 0 8px;
      background: rgba(65,203,74,0.5);

      .text{
        color: #FFFFFF;
        font-size: 14px;
      }
    }

    .orderStatus0{
      background: rgba(102, 102, 102, .1);
      .text{
        color: rgba(255, 255, 255, .6);
      }
    }

    .orderStatus1{
      background: #EDA300;
    }

    .orderStatus2{
      background: #41CB4A;
    }

    .orderStatus3,.orderStatus4{
      background: #FF1515;
    }

    .orderStatus6{
      background: #00B3EB;
    }
  }

  .content_top_card_c{
    padding: 20px 24px;
    box-sizing: border-box;
    display: flex;
    align-items: center;

    .card_c_li{
      color: #ffffffcc;
      margin-right: 64px;
      text-align: center;

      .card_c_li_title{
        font-size: 14px;
        margin-bottom: 8px;
      }

      .card_c_li_num{
        color: #30f3ff;
        font-size: 21px;
      }

      &:last-child{
        margin-right: 0;
      }
    }
  }
}

.content_top_card2{
  //border: 1px solid #FF9C02;

  .content_top_card_h{
    //color: #FF9C02;
    //background: rgba(255,156,2,0.1);

    .orderStatus{
      background: rgba(255,156,2,0.5);
    }
  }
  //.content_top_card_c{
  //  .card_c_li{
  //    color: #FF9C02;
  //  }
  //}
}

.info_li {
  display: flex;
  align-items: center;
  margin-bottom: 24px;

  .flex_li_left {
    color: #d3ecfb;
    font-size: 14px;
    white-space: nowrap;
  }

  .flex_li_right {
    color: #d3ecfb;
    font-size: 14px;
    display: flex;
    align-items: center;
  }

  .orderStatus1,.invoicingState1,.repairStatus2{
    color: #EDA300;
  }

  .orderStatus2,.invoicingState2,.repairStatus1{
    color: #41CB4A;
  }

  .orderStatus3,.orderStatus4,.repairStatus0{
    color: #FF1515;
  }

  .orderStatus6{
    color: #00B3EB;
  }
}
</style>