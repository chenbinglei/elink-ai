<template>
  <div class="content_list">
    <div class="content_li_top">
      <span class="title">订单号：</span>
      <span class="number">{{ $filters.moreData(orderInfo.orderNum) }}</span>
    </div>
    <div class="content_li_bottom">
      <div class="table_li">
        <span>{{ $filters.moreData(orderInfo.siteName) }}</span>
<!--        <div class="table_li_top">{{ $filters.moreData(orderInfo.operateUnitName) }}</div>-->
<!--        <div class="table_li_bottom">{{ $filters.moreData(orderInfo.siteName) }}</div>-->
      </div>
      <div class="table_li">
        <div class="table_li_top">{{ $filters.moreData(orderInfo.duration) }}</div>
        <el-timeline>
          <template v-for="(activity, index) in activities" :key="index">
            <el-timeline-item :color="activity.color" :hollow="activity.hollow" :type="activity.type">
              <span class="timestamp">{{ $filters.moreData(orderInfo[activity.timestamp]) }}</span>
            </el-timeline-item>
          </template>
        </el-timeline>
      </div>
      <div class="table_li">
        <div :class="'order_status' + orderInfo.occupyState" class="text order_status">
          <span>{{ $filters.occupyState(orderInfo.occupyState) }}</span>
        </div>
      </div>
      <div class="table_li">
        <span class="money">¥</span>
        <span class="text">{{ $filters.moneyTwoNum(orderInfo.orderMoney) }}</span>
      </div>
      <div class="table_li">
        <span class="money">¥</span>
        <span class="text">{{ $filters.moneyTwoNum(orderInfo.paidMoney) }}</span>
      </div>
      <div class="table_li">
        <span class="text">{{ $filters.moreData(orderInfo.plateNumber) }}</span>
      </div>
      <div class="table_li">
        <el-link :underline="false" type="primary" @click="clickOperateBut">
          <span class="iconfont icon-dingdanguanli"></span>
          <span>详情</span>
        </el-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { useOperationManagementStore } from '@/stores/index';

import {ElMessage} from "element-plus";
import {queryUserAuthorityIsHaveFun} from "@/utils";
import {reactive, defineComponent, toRefs} from "vue";

export default defineComponent({
  name: "DcOrderListCard",
  props: {
    orderInfo: {
      type: Object,
      default: () => {
        return {};
      }
    }
  },
  setup(props) {

    const operationManagementStore = useOperationManagementStore();
    const that = reactive({
      activities: [{timestamp: 'startTime', color: '#41CB4A'}, {timestamp: 'endTime', type: 'primary', hollow: true}]
    });

    const clickOperateBut = ()=>{
      const routeName = "/operationManagement/CsOrderRecordDetails";
      const isAuthority = queryUserAuthorityIsHaveFun(routeName);
      if(!isAuthority){
        ElMessage({type: "warning", showClose: true, message: "请联系管理员打开对应权限！"});
        return;
      }

      operationManagementStore.updateSecondaryInfo({
        type: 3,
        subTitle: "占用订单详情",
        id: props.orderInfo.id,
        componentName: "CsOrderRecordDetails",
        backComponentName: "CsPileOccupationRecord"
      });
      operationManagementStore.updateSecondaryVisible(true);
    };

    return {...toRefs(that),clickOperateBut};
  }
});
</script>

<style lang="scss" scoped>
.content_list {
  width: 100%;
  border-radius: 8px;
  background: #003d6033;
  box-sizing: border-box;

  .content_li_top{
    color: #d3ecfb;
    text-align: left;
    background: #00a3ff4d;
    box-sizing: border-box;
    border-radius: 8px 8px 0 0;
    padding: 10px 16px 9px 16px;
  }

  .content_li_bottom{
    padding: 24px 0 15px 0;
    box-sizing: border-box;
    display: flex;

    .table_li{
      flex: 1;
      color: #d3ecfb;
      padding: 0 12px;
      box-sizing: border-box;

      .order_status{
        width: fit-content;
        font-size: 14px;
        padding: 5px 20px;
        border-radius: 8px;
        background: #0094ff1a;
        box-sizing: border-box;
      }

      .order_status1 {
        color: #FF9C02;
        background: rgba(255, 156, 2, .1);
      }

      .order_status2 {
        color: #00B3EB;
        background: rgba(0, 179, 235, .1);
      }

      .order_status3 {
        color: #41CB4A;
        background: rgba(65, 203, 74, .1);
      }

      .order_status9 {
        color: #FF1515;
        background: rgba(255, 21, 21, .1);
      }

      .unit{
        font-size: 14px;
      }

      .iconfont{
        color: #007FEB;
        margin-right: 6px;
      }

      :deep(.el-timeline){
        margin-top: 10px;
        --el-color-white: transparent;

        .timestamp{
          color: #d3ecfb;
          white-space: nowrap;
        }
      }
    }
  }
}
</style>