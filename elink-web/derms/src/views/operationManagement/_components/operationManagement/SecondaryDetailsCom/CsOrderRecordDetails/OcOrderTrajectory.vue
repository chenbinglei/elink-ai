<template>
  <div class="content_body">
    <template v-for="(item,index) in activities" :key="index">
      <div class="timeline_item">
        <div class="timeline_item_left">{{ $filters.moreData(returnDataInfo[item.timestamp]) }}</div>
        <div class="timeline_item_center">
          <el-icon size="18">
            <component :is="item.icon" :color="item.color"></component>
          </el-icon>
          <div class="bottom_line"></div>
        </div>
        <div class="timeline_item_right">
          <div class="title">{{ $filters.chargingDisText(item.name, orderType) }}</div>
          <div v-if="item.content" class="content">{{ $filters.moreData(returnDataInfo[item.content]) }}</div>
        </div>
      </div>
    </template>
  </div>
</template>
<script lang="ts">
import {reactive, toRefs, watch} from "vue";
import {CircleCheck, CloseBold, CircleCheckFilled, CircleCloseFilled, Clock} from '@element-plus/icons-vue';

export default {
  name: "OcOrderTrajectory",
  components: {CircleCheck, CloseBold, CircleCheckFilled, CircleCloseFilled, Clock},
  props: {
    orderInfo: {
      type: Object,
      default: () => {
        return {};
      }
    },
  },
  setup(props) {
    const that = reactive({
      returnDataInfo: {},
      // 充电中的数据
      activities: [
        {timestamp: 'createTime', icon: "CircleCheckFilled", color: "#007FEB", name: "创建订单"},
        {timestamp: 'startTime', icon: "Clock", color: "#007FEB", name: "开始计费"},
      ],
      // 在途
      activities1: [
        {timestamp: 'createTime', icon: "CircleCheckFilled", color: "#007FEB", name: "创建订单"},
        {timestamp: 'startTime', icon: "Clock", color: "#007FEB", name: "正在计费"},
      ],
      // 待支付
      activities2: [
        {timestamp: 'createTime', icon: "CircleCheck", color: "#007FEB", name: "创建订单"},
        {timestamp: 'startTime', icon: "CircleCheck", color: "#007FEB", name: "开始计费"},
        {timestamp: 'endTime', icon: "CircleCheck", color: "#007FEB", name: "结束计费"},
        {timestamp: 'payTime', icon: "Clock", color: "#007FEB", name: "待支付"},
      ],
      // 启动失败
      activities3: [
        {timestamp: 'createTime', icon: "CircleCheck", color: "#007FEB", name: "创建订单"},
        {timestamp: 'startTime', icon: "CircleCheck", color: "#007FEB", name: "开始计费"},
        {timestamp: 'endTime', icon: "CircleCheck", color: "#007FEB", name: "结束计费"},
        {timestamp: 'payTime', icon: "CircleCheckFilled", color: "#007FEB", name: "订单支付完成"},
      ],
      // 异常
      activities9: [
        {timestamp: 'createTime', icon: "CircleCheck", color: "#007FEB", name: "创建订单"},
        {timestamp: 'startTime', icon: "CircleCheck", color: "#007FEB", name: "开始计费"},
        {timestamp: 'updateTime', icon: "CircleCloseFilled", color: "#FF1515", name: "订单异常"},
      ],
    });

    const watchOrderInfo = watch(() => props.orderInfo, (newOrderInfo) => {
      that.returnDataInfo = newOrderInfo ? newOrderInfo : {};
      let activities = that['activities' + that.returnDataInfo.occupyState];
      // console.log(activities)
      if (activities && activities.length) that.activities = JSON.parse(JSON.stringify(activities));
    }, {deep: true, immediate: true});

    return {...toRefs(that), watchOrderInfo};
  }
};
</script>
<style lang="scss" scoped>
.content_body {
  box-sizing: border-box;
  padding: 24px 24px 16px 24px;

  .timeline_item {
    display: flex;
    margin-bottom: 4px;

    .timeline_item_left {
      width: 180px;
      color: #121C3F;
      font-size: 14px;
    }

    .timeline_item_center {
      display: flex;
      flex-direction: column;
      align-items: center;
      margin: 0 32px;

      .bottom_line {
        width: 2px;
        height: 58px;
        margin-top: 4px;
        background: rgba(224, 224, 224, 0.8);
      }
    }

    .timeline_item_right {
      color: #121C3F;
      font-size: 14px;
      font-weight: bold;
      padding-left: 32px;

      .content {
        color: #666666;
        font-size: 12px;
        margin-top: 4px;
        font-weight: initial;
      }
    }

    &:last-child {
      margin-bottom: 0;

      .bottom_line {
        display: none;
      }
    }
  }
}
</style>