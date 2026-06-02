<template>
  <div class="real-time-power">
    <div class="w-full flex jc-space-between">
      <div class="real-time-power-title flex-ai-center">
        <img src="@/assets/image/panoramic-monitor/p-m-header-icon2.png" alt="" width="20">
        <span class="ml-2">实时功率</span>
      </div>
      <TabGroup class="right-tab" :tab-active="activeTab" :tab-list="tabList" @tabChangeEvent="tabChangeHandler"></TabGroup>
    </div>
    <div class="real-time-power-info flex">
      <template  v-for="(item,index) in list[activeTab]"> 
        <div class="info-item">
          <div class="info-item-value"><span>{{ $filters.moreZero(data[item.prop]) }}</span>{{ item.unit }}</div>
          <div class="info-item-text">{{ item.name }}</div>
          <div class="flex-jc-ai-center mt-2"><img :src="item.icon" alt="" width="49" height="51"></div>
        </div>
        <div class="info-item-line" v-if="index < (list[activeTab].length - 1)" :key="item.name"></div>
      </template>
    </div>
    <div class="chart-content">
      <EChartsLine
        ref="echart1"
        width="100%"
        height="100%"
        font-size="12"
        :data="chartData"
      ></EChartsLine>
    </div>
  </div>
</template>
<script>
import { computed, defineComponent, watchEffect, reactive, ref, toRefs, watch } from "vue";
import { TabGroup, EChartsLine } from "./index";

export default defineComponent({
  name: "realTimePower",
  components: { EChartsLine, TabGroup },
  props: {
    data: {
      type: Array,
      default: () => {},
    },
  },
  setup(props) {
    const getImageUrl = (name) => {
      return new URL(`/src/assets/image/panoramic-monitor/icon-${name}.png`, import.meta.url).href;
    }
    const state = reactive({
      tabList: ["光伏", "储能", "充电", "换电"],
      activeTab: 0,
      list: [
        [
          {
            name: "当前功率",
            unit: "kW",
            icon: getImageUrl('dqgl'),
            prop: 'pvTotalPower'  
          },
          {
            name: "发电量",
            unit: "kWh",
            icon: getImageUrl('fdl'),
            prop: 'pvQt'  
          },{
            name: "上网电量",
            unit: "kWh",
            icon: getImageUrl('swdl'),
            prop: 'pvNetQt'  
          },{
            name: "消纳电量",
            unit: "kWh",
            icon: getImageUrl('xndl'),
            prop: 'pvConsumeQt'  
          }
        ],[
          {
            name: "当前功率",
            unit: "kW",
            icon: getImageUrl('dqgl'),
            prop: 'storageTotalPower'    
          },
          {
            name: "充电量",
            unit: "kWh",
            icon: getImageUrl('cdl'),
            prop: 'storageChargeQt'  
          },{
            name: "放电量",
            unit: "kWh",
            icon: getImageUrl('fadl'),
            prop: 'storageDischargeQt'  
          }
        ],[
          {
            name: "当前功率",
            unit: "kW",
            icon: getImageUrl('dqgl') ,
            prop: 'pileTotalPower'  
          },
          {
            name: "充电量",
            unit: "kWh",
            icon: getImageUrl('cdl'),
            prop: 'pileChargeQt' 
          },{
            name: "充电次数",
            unit: "次",
            icon: getImageUrl('cdcs'),
            prop: 'pileChargeNum' 
          }
        ],[
          {
            name: "当前功率",
            unit: "kW",
            icon: getImageUrl('dqgl'),
            prop: 'totalChangePower'   
          },
          {
            name: "充电量",
            unit: "kWh",
            icon: getImageUrl('cdl'),
            prop: 'changeChargeQt' 
          },{
            name: "耗电量",
            unit: "kWh",
            icon: getImageUrl('hdl'),
            prop: 'changeUsePower' 
          }
        ]
      ],
      echartData: {
        0: { todayEchartProp: 'pvDayPowerList',yesterdayEchartProp: 'pvYestdayPowerList'},
        1: { todayEchartProp: 'storageDayPowerList',yesterdayEchartProp: 'storageYestdayPowerList'},
        2: { todayEchartProp: 'pileDayPowerList',yesterdayEchartProp: 'pileYestdayPowerList'},
        3: { todayEchartProp: 'changeDayPowerList',yesterdayEchartProp: 'changeYestdayPowerList'}
      },
      chartData: { names: [], datas: [] },
    });
    const tabChangeHandler = (index) => {
      state.activeTab = index;
    };
    const loadChartData = (serverData) => {
      let names = [].concat(serverData[0]);
      let data2 = {
        color: "#00CCFF99",
        name: "昨日",
        needArea: false,
        smooth: false,
        datas: [],
      };
      let data1 = {
        color: "#34E80099",
        name: "今日",
        needArea: true,
        smooth: false,
        datas: [],
      };
      //渐变色定义
      data1.areaColor = {
        linear: [0, 0, 0, 1],
        colors: [
          { color: data1.color, offset: 0, alpha: 0.4 },
          { color: data1.color, offset: 1, alpha: 0 },
        ]
      };

      data1.datas = [].concat(serverData[1]);
      data2.datas = [].concat(serverData[2]);
      let datalist = [data1, data2];
      return {
        names: names,
        datas: datalist,
      };
    }
    watchEffect(() => {
      let data = [
        props.data?.dateList,
        props.data[state.echartData[state.activeTab].todayEchartProp],
        props.data[state.echartData[state.activeTab].yesterdayEchartProp],
      ]
      state.chartData = loadChartData(data)
    })
    return { ...toRefs(state), tabChangeHandler }
  },
});
</script>
<style scoped lang="scss">
@import "./style.scss";
// .real-time-power {
//   width: 100%;
//   height: 100%;
//   background: url("@/assets/image/panoramic-monitor/p-m-header-bg.png") no-repeat;
//   background-size: 100% 100%;
//   .real-time-power-title {
//     font-size: 18px;
//     color: #fff;
//     font-family: zihun35hao-jindianyahei, zihun35hao-jindianyahei;
//   }
//   .right-tab {
//     width: 65%;
//     height: 28px;
//   }
//   .real-time-power-info {
//     padding: 23px 20px 0;
//     justify-content: space-around;
//     .info-item-value {
//       margin-bottom: 6px;
//       text-align: center;
//       color: #fff;
//       font-size: 12px;
//       word-break: keep-all;
//       font-family: Microsoft YaHei, Microsoft YaHei;
//       span {
//         font-family: Agency FB, Agency FB;
//         font-weight: 400;
//         margin-right: 5px;
//         font-size: 18px;
//         color: #00ffb1;
//       }
//     }
//     .info-item-text {
//       text-align: center;
//       color: #fff;
//       word-break: keep-all;
//     }
//   }

//   .chart-content {
//     width: 100%;
//     height: 50%;
//   }
//   .info-item-line {
//     width: 3px;
//     height: 103px;
//     background: url("@/assets/image/panoramic-monitor/vertica-ldivider-line-1.png") no-repeat;
//     background-size: 100% 100%;
//   }
// }
</style>