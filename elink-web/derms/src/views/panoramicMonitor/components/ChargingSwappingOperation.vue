<template>
  <div class="charging-swapping-operation">
    <div class="charging-swapping-operation-title w-full flex jc-space-between">
      <div class="real-time-power-title flex-ai-center">
        <img src="@/assets/image/panoramic-monitor/p-m-header-icon4.png" alt="" width="20">
        <span class="ml-2">充换电运营</span>
      </div>
      <TabGroup class="right-tab" :tab-active="activeTab" :tab-list="tabList" @tabChangeEvent="tabChangeHandler"></TabGroup>
    </div>
    <div class="dzzcdl flex">
      <div class="dzzcdl-icon">
        <img src="@/assets/image/panoramic-monitor/icon-chdyy.png" alt="" width="95" v-if="activeTab == 0">
        <img src="@/assets/image/panoramic-monitor/icon-chdyy2.png" alt="" width="95" v-else>
      </div>
      <div class="dzzcdl-text flex-ai-center">
        <p>{{activeTab == 0 ? '电桩' : '换电'}}总充电量</p>
        <div class="dzzcdl-value"><span class="special-text ml-5">{{activeTab == 0 ? $filters.moreZero(pileOperation.totalChargeQt) : $filters.moreZero(changeOperation.totalChargeQt)}}</span>kWh</div>
      </div>
    </div>
    <div class="details" v-if="activeTab == 0">
      <div class="details-item flex" style="width: calc((100% - 20px) / 2);">
        <div><img src="@/assets/image/panoramic-monitor/icon-chdyy-1.png" alt="" width="30"></div>
        <div class="ml-2 pt-2">
          <div class="details-text">本月充电次数</div>
          <div class="details-value flex"><span>{{$filters.moreZero(pileOperation.monthChargeNum)}}</span>次</div>
          <div class="details-state flex mt-3" :style="backgroundStyle(pileOperation.monthNumCompare)">
            较上月<span :class="getClass(pileOperation.monthNumCompare)">{{$filters.moreZero(pileOperation.monthNumCompare)}}</span>
          </div>
        </div>
      </div>
      <div class="details-item-line"></div>
      <div class="details-item flex" style="width: calc((100% - 20px) / 2);padding-left: 20px;">
        <div><img src="@/assets/image/panoramic-monitor/icon-chdyy-2.png" alt="" width="30"></div>
        <div class="ml-2 pt-2">
          <div class="details-text">日均枪效</div>
          <div class="details-value flex"><span>{{$filters.moreZero(pileOperation.dayChargeEfficiency)}}</span>kWh/枪/天</div>
          <div class="details-state flex mt-3" :style="backgroundStyle(pileOperation.dayEfficiencyCompare)">
            较上月<span :class="getClass(pileOperation.dayEfficiencyCompare)">{{$filters.moreZero(pileOperation.dayEfficiencyCompare)}}</span>
          </div>
        </div>
      </div> 
    </div>
    <div class="details" v-else>
      <div class="details-item flex" style="width: calc((100% - 20px) / 2);">
        <div><img src="@/assets/image/panoramic-monitor/icon-chdyy-1.png" alt="" width="30"></div>
        <div class="ml-2 pt-2">
          <div class="details-text">本月充电次数</div>
          <div class="details-value flex"><span>{{$filters.moreZero(changeOperation.monthChargeNum)}}</span>次</div>
          <div class="details-state flex mt-3" :style="backgroundStyle(changeOperation.monthNumCompare)">
            较上月<span :class="getClass(changeOperation.monthNumCompare)">{{$filters.moreZero(changeOperation.monthNumCompare)}}</span>
          </div>
        </div>
      </div>
      <div class="details-item-line"></div>
      <div class="details-item flex" style="width: calc((100% - 20px) / 2);padding-left: 20px;">
        <div><img src="@/assets/image/panoramic-monitor/icon-chdyy-2.png" alt="" width="30"></div>
        <div class="ml-2 pt-2">
          <div class="details-text">日均换电里程</div>
          <div class="details-value flex"><span>{{$filters.moreZero(changeOperation.dayKm)}}</span>km</div>
          <div class="details-state flex mt-3" :style="backgroundStyle(changeOperation.dayKmCompare)">
            较上月<span :class="getClass(changeOperation.dayKmCompare)">{{$filters.moreZero(changeOperation.dayKmCompare)}}</span>
          </div>
        </div>
      </div>
    </div>
    <!-- <div class="segmentation-line"></div> -->
    <!-- 图表 -->
    <div class="chart-content">
      <EchartsBar
        ref="echart1"
        width="100%"
        height="100%"
        :data="chartData"
      ></EchartsBar>
    </div>
  </div>
</template>
<script lang="ts">
import { defineComponent,reactive, watchEffect, toRefs } from "vue";
import {EchartsBar,TabGroup,} from "@/views/panoramicMonitor/components/index";
import arrowLine1 from '@/assets/image/panoramic-monitor/arrow-line-1.png'
import arrowLine2 from '@/assets/image/panoramic-monitor/arrow-line-2.png'
export default defineComponent({
  name: "chargingSwappingOperation",
  components: { EchartsBar, TabGroup },
   props: {
    // 换电
    changeOperation: {
      type: Object,
      required: true,
      default: () => {}
    },
    // 充电
    pileOperation: {
      type: Object,
      required: true,
      default: () => {}
    },
    // 日期项
    operationDateList: {
      type: Array,
      required: true,
      default: () => []
    }
  },
  setup(props) {
    // 基础数据
    const state = reactive({
      tabList: ["充电", "换电"],
      activeTab: 0,
      chartData: { names: [], datas: [] },
    });
    const tabChangeHandler = (index) => {
      state.activeTab = index;
    };
    // 自定义样式
    const backgroundStyle = (num) => {
      let obj = {}
      if(Number(num) > 0 ) {
        obj = { 
          background: `url(${arrowLine1}) no-repeat`,
          backgroundSize: '100% auto',
          backgroundPosition: 'bottom'
        }
      }else if( Number(num) < 0){
        obj = { 
          background: `url(${arrowLine2}) no-repeat`,
          backgroundSize: '100% auto',
          backgroundPosition: 'top'
        }
      }
      return obj
    }
    // 自定义类
    const getClass = (num) => { 
      return Number(num) > 0 ? 'green-num' : 'yellow-num'
    }
    // echarts加载数据
    const loadChartData = (serverData) => {
      let names = [].concat(serverData[0]);
      let data = {
        color: "#00CCFF",
        name: "充电量",
        showBackground: false,
        datas: [],
      };

      data.datas = [].concat(serverData[1]);
      let datalist = [data];
      return {
        names: names,
        datas: datalist,
      }
    }
    // 监听echarts数据
    watchEffect(() => {
      let data = [
        props.operationDateList,
        state.activeTab == 0 ? props.pileOperation?.chargeQtList : props.changeOperation?.chargeQtList,
      ]
      state.chartData = loadChartData(data)
    })
   
    return { ...toRefs(state), tabChangeHandler,backgroundStyle, getClass };
  },
});
</script>
<style scoped lang="scss">
@import "./style.scss";
// .charging-swapping-operation {
//   box-sizing: border-box;
//   padding: 0 20px;
//   font-family: Agency FB, Agency FB;
//   width: 100%;
//   height: 100%;
//   background: url("@/assets/image/panoramic-monitor/p-m-header-bg.png") no-repeat;
//   background-size: 100% 100%;
//   .charging-swapping-operation-title {
//     font-size: 18px;
//     color: #fff;
//     font-family: zihun35hao-jindianyahei, zihun35hao-jindianyahei;
//   }
//   .dzzcdl {
//     padding: 18px 0 8px 0;
//     color: #fff;
//     .dzzcdl-icon {
//       position: relative;
//       top: -10px;
//     }
//     .dzzcdl-text {
//       padding-left: 23px;
//       width: calc(100% - 95px);
//       height: 44px;
//       background: url("@/assets/image/panoramic-monitor/info-bg.png") no-repeat;
//       background-size: 100% 100%;
//       .dzzcdl-value {
//         font-size: 14px;
//         font-family: Microsoft YaHei, Microsoft YaHei;
//         span {
//           margin-right: 15px;
//           font-size: 24px;
//         }
//       }
//     }
//   }
//   .details {
//     display: flex;
//     color: #fff;
//     font-size: 14px;
//     .details-value {
//       font-size: 12px;
//       line-height: 30px;
//       span {
//         font-size: 18px;
//         color: #00ffb1;
//         font-family: Agency FB, Agency FB;
//         font-weight: 400;
//         margin-right: 30px;
//       }
//     }
//     .details-state {
//       width: 95px;
//       padding-top: 10px;
//       padding-bottom: 5px;
//       font-size: 12px;
//       color: #00ccff;
//       flex: 1;
//       span {
//         font-size: 18px;
//         font-family: Agency FB, Agency FB;
//         font-weight: 400;
//         color: #00FFB1;
//         margin-left: 20px;
//       }
//     }
//     .details-item-line {
//       width: 20px;
//       height: 118px;
//       background: url("@/assets/image/panoramic-monitor/vertica-ldivider-line-1.png") no-repeat;
//       background-size: 100% 100%;
//     }
//   }
//   .segmentation-line {
//     width: 90%;
//     height: 7px;
//     margin:0 auto 16px;
//     background: linear-gradient(
//       180deg,
//       rgba(78, 186, 255, 0) 0%,
//       rgba(78, 186, 255, 0.83) 100%
//     );
//     opacity: 0.81;
//   }
//   .chart-content {
//     width: 100%;
//     height: 40%;
//   }
// }
</style>