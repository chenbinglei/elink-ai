<template>
  <div class="asset-overview">
    <div class="asset-overview-title flex">
      <img src="@/assets/image/panoramic-monitor/p-m-header-icon1.png" alt="" width="20">
      <!-- <span class="ml-2">资产总览</span> -->
       <span>资产总览</span>
    </div>
    <div :class="item.className" v-for="(item,index) in pieData" :key="index + 'pieLegendList'">
      <div :class="(index % 2) == 0 ? 'title flex_s_c' : 'title flex_e_c'">{{item.name}}</div>
      <div class="line"></div>
      <div :class="(index % 2) == 0 ? 'value flex_s_c' : 'value flex_e_c'" >
        <span class="special-text">{{  $filters.moreZero(item.value)}}</span>{{ item.unit }}
      </div>
    </div>
    <div class="asset-overview-zzjrl flex_c_c"><span>总装机容量</span></div>
    <div class="echarts-content">
      <echarts-two width="100%" height="100%" ref="chart1" :data="chartData"></echarts-two>
    </div>
    <div class="asset-overview-total">
      <div class="value">{{ $filters.moreZero(data.totalCapacity) }}</div>
      <div class="unit">kW</div>
    </div>
  </div>
</template>
<script>
import { defineComponent, nextTick, ref, reactive, toRefs, computed } from "vue";
import EchartsTwo from "@/components/echart2/echartsTwo.vue";
export default defineComponent({
  name: "assetOverview",
  components: { EchartsTwo },
  props: {
    data: {
      type: Array,
      required: true,
      default: () => {}
    }
  },
  setup(props) {
    // 基础数据
    const state = reactive({
      pieLegendList: [
        { name: "光伏电站", unit: "kW",className: "asset-overview-gfdz",prop: 'pvCapacity'},
        { name: "储能电站", unit: "kW",className: "asset-overview-cndz",prop: 'storageCapacity'},
        { name: "充电站", unit: "kW",className: "asset-overview-cdz",prop: 'pileCapacity'},
        { name: "换电站", unit: "kW",className: "asset-overview-hdz",prop: 'changeCapacity'}
      ],
      chartData: {},
    });
    // 饼图legend
    const pieData = computed(() => { 
      return state.pieLegendList.map(item => {
        item.value = props.data[item.prop]
        return item
      })
    });
    // 获取图表数据
    const initChartData = () => {
      const chart1 = ref();
      let chartData = {
        name: "资产总览",
        tooltip: {
          show: false,
        },
        legend: {
          show: false,
        },
        color: ["#00FFB1", "#FFA700", "#F56C6C", "#00CCFF"],
        series: [
          {
            name: "Access From",
            type: "pie",
            radius: ["80%", "90%"],
            avoidLabelOverlap: false,
            startAngle: 180,
            label: {
              show: false,
            },
            emphasis: {
              show: false,
            },
            labelLine: {
              show: false,
            },
            data: pieData,
          },
        ],
      };
      return chartData;
    };
    nextTick(() => {
      state.chartData = initChartData();
    });
    return { ...toRefs(state),pieData };
  },
});
</script>

<style scoped lang="scss">
@import "./style.scss";
// .asset-overview {
//   position: relative;
//   width: 100%;
//   height: 100%;
//   background: url("/src/assets/image/panoramic-monitor/p-m-header-bg.png") no-repeat;
//   background-size: 100% 100%;
//   .asset-overview-title {
//     font-size: 18px;
//     color: #fff;
//     font-family: zihun35hao-jindianyahei, zihun35hao-jindianyahei;
//   }

//   .asset-overview-echart {
//     position: absolute;
//     top: 90px;
//     left: 53.5%;
//     transform: translateX(-50%);
//     width: 132px;
//     height: 119px;
//     background: url("@/assets/image/panoramic-monitor/asset-overview-echart-bg.png")
//       no-repeat;
//     background-size: 100% 100%;
//   }

//   .asset-overview-gfdz {
//     position: absolute;
//     top: 57px;
//     left: 20px;

//     .title {
//       color: #ffffff;
//       font-size: 14px;

//       &::before {
//         display: inline-block;
//         margin-right: 8px;
//         content: "";
//         width: 0px;
//         height: 10px;
//         border: 2px solid #00ffb1;
//       }
//     }

//     .line {
//       margin-top: 5px;
//       width: 134px;
//       border-top: 1px solid #0097d1;
//       height: 10px;
//       // background: linear-gradient( 180deg, #0097D1 0%, rgba(48,183,183,0) 100%);
//       opacity: 0.45;
//     }

//     .value {
//       margin-left: 8px;
//       color: #ffffff;
//       font-size: 12px;
//       font-family: Microsoft YaHei, Microsoft YaHei;
//       span {
//         margin-right: 12px;
//         font-size: 20px;
//       }
//     }
//   }

//   .asset-overview-cndz {
//     position: absolute;
//     top: 57px;
//     right: 20px;

//     .title {
//       text-align: right;
//       color: #ffffff;
//       font-size: 14px;

//       &::after {
//         display: inline-block;
//         margin-left: 8px;
//         content: "";
//         width: 0px;
//         height: 10px;
//         border: 2px solid #ffa700;
//       }
//     }

//     .line {
//       margin-top: 5px;
//       width: 134px;
//       border-top: 1px solid #0097d1;
//       height: 10px;
//       // background: linear-gradient( 180deg, #0097D1 0%, rgba(48,183,183,0) 100%);
//       opacity: 0.45;
//     }

//     .value {
//       margin-right: 19px;
//       font-size: 12px;
//       color: #ffffff;

//       span {
//         margin-right: 12px;
//         font-size: 20px;
//         color: #ffa700;
//       }
//     }
//   }

//   .asset-overview-cdz {
//     position: absolute;
//     top: 180px;
//     left: 20px;

//     .title {
//       color: #ffffff;
//       font-size: 14px;

//       &::before {
//         display: inline-block;
//         margin-right: 8px;
//         content: "";
//         width: 0px;
//         height: 10px;
//         border: 2px solid #f56c6c;
//       }
//     }

//     .line {
//       margin-top: 5px;
//       width: 134px;
//       border-top: 1px solid #0097d1;
//       height: 10px;
//       // background: linear-gradient( 180deg, #0097D1 0%, rgba(48,183,183,0) 100%);
//       opacity: 0.45;
//     }

//     .value {
//       margin-left: 8px;
//       font-size: 12px;
//       color: #ffffff;
//       font-family: Microsoft YaHei, Microsoft YaHei;
//       span {
//         margin-right: 12px;
//         font-size: 20px;
//         color: #f56c6c;
//       }
//     }
//   }

//   .asset-overview-hdz {
//     position: absolute;
//     top: 180px;
//     right: 20px;

//     .title {
//       text-align: right;
//       color: #ffffff;
//       font-size: 14px;

//       &::after {
//         display: inline-block;
//         margin-left: 8px;
//         content: "";
//         width: 0px;
//         height: 10px;
//         border: 2px solid #00ccff;
//       }
//     }

//     .line {
//       margin-top: 5px;
//       width: 134px;
//       border-top: 1px solid #0097d1;
//       height: 10px;
//       // background: linear-gradient( 180deg, #0097D1 0%, rgba(48,183,183,0) 100%);
//       opacity: 0.45;
//     }

//     .value {
//       margin-right: 19px;
//       font-size: 12px;
//       color: #ffffff;

//       span {
//         margin-right: 12px;
//         font-size: 20px;
//         color: #00ccff;
//       }
//     }
//   }
//   .asset-overview-total {
//     position: absolute;
//     top: 120px;
//     width: 120px;
//     left: 50%;
//     transform: translateX(-50%);
//     text-align: center;
//     .value {
//       font-family: Agency FB, Agency FB;
//       font-weight: 400;
//       font-size: 22px;
//       color: #ffffff;
//     }
//     .unit {
//       font-size: 12px;
//       color: #00ccff;
//     }
//   }

//   .asset-overview-zzjrl {
//     position: absolute;
//     top: 90%;
//     left: 50%;
//     transform: translateX(-50%);
//     width: 208px;
//     height: 14px;
//     font-size: 14px;
//     color: #fff;
//     background: linear-gradient(
//       180deg,
//       rgba(3, 165, 255, 0) 0%,
//       rgba(3, 165, 255, 0.25) 100%
//     );
//     span {
//       margin-top: -12px;
//     }
//   }

//   .echarts-content {
//     position: absolute;
//     width: 120px;
//     height: 120px;
//     //display: flex;
//     display: block;
//     //border: 1px solid #0097D1;
//     top: 75px;
//     left: 50%;
//     transform: translateX(-50%);
//     background: url("@/assets/image/panoramic-monitor/asset-overview-echart-bg.png")
//       no-repeat;
//     background-size: 100% 100%;
//   }
// }
</style>