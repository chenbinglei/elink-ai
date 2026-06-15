<template>
  <div class="totalOrderData">
    <template v-for="(item,index) in list" :key="index">
      <div class="total_list" v-if="componentName === item.componentName || !item.componentName">
        <div class="total_list_left flex-jc-ai-center">
          <span :class="item.iconName" class="iconfont"></span>
        </div>
        <div class="total_list_right">
          <template v-for="(childItem,childIndex) in item.children" :key="childIndex">
            <div class="total_list_li" v-if="componentName === childItem.componentName || !childItem.componentName">
              <div class="title">{{ childItem.name }}</div>
              <div class="number">
                <span v-if="childItem.filterName">{{ $filters[childItem.filterName](returnDataInfo[childItem.fieldName]) }}</span>
                <span v-else>{{ $filters.moreData(returnDataInfo[childItem.fieldName]) }}</span>
              </div>
            </div>
          </template>
        </div>
      </div>
    </template>
  </div>
</template>

<script lang="ts">
import {reactive, toRefs, watch, defineComponent} from "vue";

export default defineComponent({
  name: "TotalOrderData",
  props: {
    orderTotalData: {
      type: Object,
      default: () => {
        return {};
      }
    },
    componentName: {
      type: String,
      default: ""
    },
  },
  setup(props) {
    const that = reactive({
      returnDataInfo: {},
      list: [
        {
          iconName: "icon-cishu",
          children: [
            {name: '订单数量（笔）', fieldName: 'orderNumber'},
          ]
        },
        {
          iconName: "icon-jinezhi",
          children: [
            {name: '订单金额（元）', fieldName: 'sumCost',filterName:"moneyTwoNum"},
          ]
        },
        {
          iconName: "icon-jinezhi",
          componentName: 'CsChargingRecord',
          children: [
            {name: '实付金额（元）', fieldName: 'actualTotalCost',filterName:"moneyTwoNum"},
            {name: '实付电费（元）', fieldName: 'actualTotalElect',filterName:"moneyTwoNum"},
            {name: '实付服务费（元）', fieldName: 'actualTotalFee',filterName:"moneyTwoNum"},
          ]
        },
        {
          iconName: "icon-dianliang",
          children: [
            {name: '总充电电量（度）', fieldName: 'sumQt',componentName: 'CsChargingRecord'},
            {name: '总放电电量（度）', fieldName: 'sumQt',componentName: 'CsDisChargingRecord'},
          ]
        },
      ]
    });

    const watchOrderTotalData = watch(() => props.orderTotalData, (newOrderTotalData) => {
      that.returnDataInfo = newOrderTotalData;
    }, {deep: true, immediate: true});

    return {...toRefs(that), watchOrderTotalData};
  }
});
</script>

<style lang="scss" scoped>
.totalOrderData {
  border-radius: 4px;
  box-sizing: border-box;
  padding: 16px 12px;
  margin-bottom: 12px;
  background: #0094ff1a;
  border: 1px solid #4ab3ff4d;
  display: flex;
  align-items: center;
  justify-content: space-between;

  .total_list{
    display: flex;
    align-items: center;

    .total_list_left{
      width: 48px;
      height: 48px;
      border-radius: 50%;
      background: #0094ff1a;

      .iconfont{
        color: #1790FF;
        font-size: 21px;
      }
    }

    .total_list_right{
      display: flex;
      align-items: center;
      margin-left: 16px;

      .total_list_li{
        font-size: 14px;
        color: #ffffffcc;
        text-align: center;
        margin-right: 32px;

        .number{
          color: #30f3ff;
          font-size: 20px;
          margin-top: 12px;
        }

        &:last-child{
          margin-right: 0;
        }
      }
    }
  }
}
</style>