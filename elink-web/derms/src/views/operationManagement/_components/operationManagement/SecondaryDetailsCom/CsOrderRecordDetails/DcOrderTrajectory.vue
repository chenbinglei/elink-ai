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
import {reactive, toRefs, watch, defineComponent} from "vue";
import {CircleCheck, CloseBold, CircleCheckFilled, CircleCloseFilled, Clock} from '@element-plus/icons-vue';

export default defineComponent({
  name: "DcOrderTrajectory",
  components: {CircleCheck, CloseBold, CircleCheckFilled, CircleCloseFilled, Clock},
  props: {
    orderInfo: {
      type: Object,
      default: () => {
        return {};
      }
    },
    // 1: 充 2： 放
    orderType: {
      type: [Number, String],
      default: 1
    },
  },
  setup(props) {
    const that = reactive({
      returnDataInfo: {},
      // 充电中的数据
      activities: [{timestamp: 'createTime', icon: "CircleCheckFilled", color: "#30F3FF", name: "创建订单"}],
      activities1: [
        {timestamp: 'createTime', icon: "CircleCheck", color: "#30F3FF", name: "创建订单"},
        {timestamp: 'startTime', icon: "CircleCheck", color: "#30F3FF", name: "开始充/放电"},
        {timestamp: 'updateTime', icon: "Clock", color: "#30F3FF", name: "正在充/放电"},
      ],
      // 充电完成
      activities2: [
        {timestamp: 'createTime', icon: "CircleCheck", color: "#30F3FF", name: "创建订单"},
        {timestamp: 'startTime', icon: "CircleCheck", color: "#30F3FF", name: "开始充/放电"},
        {timestamp: 'endTime', icon: "CircleCheck", color: "#30F3FF", name: "结束充/放电", content: "stopDetailReason"},
        {timestamp: 'updateTime', icon: "CircleCheckFilled", color: "#30F3FF", name: "结算完成"},
      ],
      // 启动失败
      activities3: [
        {timestamp: 'createTime', icon: "CircleCheck", color: "#30F3FF", name: "创建订单"},
        {timestamp: 'updateTime', icon: "CircleCloseFilled", color: "#FF1515", name: "启动失败", content: "stopDetailReason"},
      ],
      // 异常
      activities4: [
        {timestamp: 'createTime', icon: "CircleCheck", color: "#30F3FF", name: "创建订单"},
        {timestamp: 'updateTime', icon: "CircleCloseFilled", color: "#FF1515", name: "异常中断-订单完成", content: "stopDetailReason"},
      ],
      // 异常
      activities5: [
        {timestamp: 'createTime', icon: "CircleCheck", color: "#30F3FF", name: "创建订单"},
        {timestamp: 'updateTime', icon: "CircleCloseFilled", color: "#666666", name: "订单取消"},
      ],
      // 预约
      activities6: [
        {timestamp: 'createTime', icon: "CircleCheck", color: "#30F3FF", name: "创建订单"},
        {timestamp: 'clockingTime', icon: "Clock", color: "#30F3FF", name: "预约时间"},
      ],
    });

    const watchOrderInfo = watch(() => props.orderInfo, (newOrderInfo) => {
      that.returnDataInfo = newOrderInfo ? newOrderInfo : {};
      let activities = that['activities' + that.returnDataInfo.orderStatus];
      // console.log(activities)
      if (activities && activities.length) that.activities = JSON.parse(JSON.stringify(activities));
    }, {deep: true, immediate: true});

    return {...toRefs(that), watchOrderInfo};
  }
});
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
      color: #d3ecfb;
      font-size: 14px;
    }

    .timeline_item_center {
      display: flex;
      flex-direction: column;
      align-items: center;
      margin: 0 32px;

      .bottom_line {
        width: 1px;
        height: 58px;
        margin-top: 4px;
        background: #98defc80;
      }
    }

    .timeline_item_right {
      color: #d3ecfb;
      font-size: 14px;
      font-weight: bold;
      padding-left: 32px;

      .content {
        color: #ffffff99;
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