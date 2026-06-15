<template>
  <div class="power-station-overview">
    <div class="power-station-left">
      <PowerStationGraph></PowerStationGraph>
    </div>
    <div class="power-station-right">
      <div class="right-1 right-item">
        <div class="flex ai-center" style="padding: 0 10px 0 19px;">
          <el-select class="selectRegion" v-model="regionValue" size="mini">
            <el-option v-for="item in options" :key="item.id" :label="item.nodeName" :value="item.id" />
          </el-select>
          <el-date-picker v-model="yearTimeDate" :disabled-date="pickerOptions.disabledDate" :clearable="false"
            format="YYYY-MM-DD" size="mini" style=" width: 240px;" type="date"
            @change="querySystemVarOrFunctionCurveData" />
        </div>
        <div class="echart">
          <el-empty :image="emptyImg" v-if="options.length == '0'" description="暂无数据" />
          <EChartsCategory v-else ref="eChart2" height="100%" width="100%" :data="chartsData1"></EChartsCategory>
        </div>
      </div>
      <div class="right-item right-3" style="z-index: 1000;">
        <station-tab-group :tab-active="tabActive2" :tab-list="tab_list" @tabChangeEvent="tabChangeHandler2">
        </station-tab-group>
        <div class="echart">
          <el-empty :image="emptyImg" v-if="options.length == '0'" description="暂无数据" />
          <EChartsCategory v-else ref="eChart3" width="100%" height="100%" :data="chartsData3"></EChartsCategory>
        </div>
      </div>
    </div>
  </div>
</template>
<script lang="ts">
import { reactive, defineComponent, watch, toRefs, ref, nextTick, onMounted, inject, computed } from "vue";
import { pickerOptionsGthanAcTime } from "@/utils/dateTime";
import { useMonitorStore } from '@/stores/index';

import { TpjdTypeList } from "@/common/enum";
import EChartsCategory from "@/components/echart2/echartsCategory.vue";
import { PowerStationGraph, StationTabGroup, tablist, } from "@/views/stationDetails/components";
import { findSiteTopCurveList, findSiteGateTopBySiteId } from "@/api/monitoringCenter/monitoringCenter";
import { findSiteListByUserId } from "@/api/centralMonitoring/centralMonitoring";
import { useRoute } from 'vue-router';
import emptyImg from "@/assets/image/empty.png";

export default defineComponent({
  name: "PowerStationOverview",
  components: { PowerStationGraph, StationTabGroup, EChartsCategory },
  setup () {
    const siteId = inject('siteId')
    const monitorStore = useMonitorStore();
    const route = useRoute();
    const options = ref([]);
    const tab_list = ref([]);
    const state = reactive({
      regionValue: "",
      yearTimeDate: '',
      chartsData1: { names: [], datas: [] },
      chartsData3: { names: [], datas: [] },
      tabActive1: 0,
      tabList2: [
        { id: '1', label: '光伏', },
        { id: '2', label: '储能', },
        { id: '3', label: '电桩', },
        { id: '4', label: '换电', },
        { id: '5', label: '负荷', }
      ],
      stationList: [],
      tabActive2: 0,
      pickerOptions: pickerOptionsGthanAcTime(),
      SiteTopCurveList: [],
      testList1: [],
      testList3: [],
      otherList: [],

    });

    const querySystemVarOrFunctionCurveData = (value) => {
      const date = new Date(value);
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, '0');
      const day = String(date.getDate()).padStart(2, '0');
      const formattedDate = `${year}-${month}-${day}`;
      state.yearTimeDate = formattedDate
      state.tabActive2 = 0
      getLineListOne()
    };
    const tabChangeHandler1 = (index) => {
      state.tabActive1 = index;
    };
    const tabChangeHandler2 = (index) => {
      state.tabActive2 = index
      const matchedItem = state.otherList.find(item => {
        if (Array.isArray(item) && item.length > 0) {
          return item[0].dataType == tab_list.value[index].id;
        }
        return false;
      });
      const lineCharts = [state.SiteTopCurveList.dateList, ...matchedItem]
      state.chartsData3 = loadChartData1(lineCharts);

    };
    // 初始化日期为今天
    const formatDate = (date) => {
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, '0');
      const day = String(date.getDate()).padStart(2, '0');
      return `${year}-${month}-${day}`;
    };
    state.yearTimeDate = formatDate(new Date());
    const loadChartData1 = (serverData) => {
      const arrLineList = serverData.slice(1);
      let names = [].concat(serverData[0]);
      const legendNameMap = {};
      const colors = ["#00CCFF99", "#34e800", "#FF6666", "#FFCC66"]; // 不同颜色数组
      const datalistlist = arrLineList.map((item, index) => {

        const originalName = item?.curveName || `曲线 ${index}`;

        // 如果名称已存在，添加索引
        const name = legendNameMap[originalName]
          ? `${originalName}(${legendNameMap[originalName]})`
          : originalName;

        legendNameMap[originalName] = (legendNameMap[originalName] || 0) + 1;
        return {
          color: colors[index % colors.length], // 不同颜色
          // name: item?.curveName,
          name: name, // 唯一名称
          lineStyle: { width: 2 },
          needArea: index === 0 ? true : false, // 只有第一条线需要填充
          datas: item?.curveList,
          lineType: name === "变压器安全容量" ? "dashed" : undefined, // 第二条线为虚线
          // 只有第一条线添加渐变色
          ...(index === 0 && {
            areaColor: {
              linear: [0, 0, 0, 1],
              colors: [
                { color: "#00CCFF99", offset: 0, alpha: 0.4 },
                { color: "#00CCFF99", offset: 1, alpha: 0 },
              ],
            },
          }),
        };
      });
      return {
        legend: {
          itemHeight: 2,
          top: "5%",
          right: "5%",
          show: true, // 强制显示所有图例
        },
        names: names,
        datas: datalistlist,
        tooltip: {
          className: "custom-tooltip-box",
          formatter: formatter,
        },
      };
    };
    const eChart2 = ref(null);
    const eChart3 = ref(null);




    const formatter = (params) => {
      // 循环处理数据，展示数据
      let str = getValueStr(params);
      let htmlText = `<div class='custom-tooltip-style'>
      <div class="custom-title">${params[0].name}</div>
      ${str}</div>`;
      return htmlText;
    }
    const getValueStr = (data) => {
      let str = "";
      for (let i = 0; i < data.length; i++) {
        let temp = data[i];
        str += `<div class="custom-tooltip-item">
        <div class="custom-tooltip-name">${temp.seriesName}</div>
        <div class="custom-tooltip-value">${temp.value ?? "--"}
           <span> ${temp.seriesName.includes('储能SOC') ? '%' : temp.seriesName == '变压器安全容量' ? 'kWp' : "kW"}</span></div>
      </div>`;
      }
      return str;
    };
    const getLineListOne = () => {
      let obj = {
        queryDate: state.yearTimeDate,
        siteId: siteId.value,
        nodeId: state.regionValue
      }
      findSiteTopCurveList(obj).then(res => {
        state.SiteTopCurveList = res.data
        state.testList1 = [state.SiteTopCurveList.dateList, ...state.SiteTopCurveList.curveDataMap[0]]


        state.chartsData1 = loadChartData1(state.testList1);
        // 第二个不同类型显示的数据
        setTimeout(() => {
          // if (!tab_list.value[0]) {
          //   let list = TpjdTypeList.filter(item =>
          //     state.stationList.scenarioTypes.includes(item.id)
          //   );

          //   let newList = list.map(item => ({
          //     id: item.id,
          //     label: item.name
          //   }));

          //   tab_list.value = newList;
          // }
        
          if (!tab_list.value[0]) {
            // 获取场景类型数组
            let scenarioTypes = state.stationList?.scenarioTypes;
            if (!scenarioTypes || scenarioTypes.length === 0) {
              // 从路由获取
              const queryTypes = route.query.scenarioTypes;
              if (typeof queryTypes === 'string') {
                scenarioTypes = queryTypes.split(',');
              } else if (Array.isArray(queryTypes)) {
                scenarioTypes = queryTypes;
              } else {
                scenarioTypes = [];
              }
            }

            let list = TpjdTypeList.filter(item => scenarioTypes.includes(item.id));
            let newList = list.map(item => ({
              id: item.id,
              label: item.name
            }));
            tab_list.value = newList;
          }

          state.testList3 = [state.SiteTopCurveList.dateList, ...state.SiteTopCurveList.curveDataMap[tab_list.value[0].id]];
          state.chartsData3 = loadChartData1(state.testList3);

          const filtered = Object.values(state.SiteTopCurveList?.curveDataMap).filter((_, i) => i !== 0);
          state.otherList = [...filtered];
        }, 200); // 延迟 500 毫秒

      })
    }

    // 查询场站列表
    const querySiteListByUserId = () => {
      // 在 querySiteListByUserId 中赋值


      findSiteListByUserId({}).then((res) => {
        let arr = res.data ? res.data : [];
        state.stationList = arr.find(item => item.id == siteId.value);
        let list = TpjdTypeList.filter(item =>
          state.stationList.scenarioTypes.includes(item.id)
        );

        let newList = list.map(item => ({
          id: item.id,
          label: item.name
        }));

        tab_list.value = newList;
        getOptionsList()
      }).catch(e => {
        let list = TpjdTypeList.filter(item =>
          route.query.scenarioTypes.includes(item.id)
        );

        let newList = list.map(item => ({
          id: item.id,
          label: item.name
        }));
        tab_list.value = newList;
      });
    };
    const getOptionsList = () => {
      findSiteGateTopBySiteId({ siteId: siteId.value }).then(res => {

        options.value = res.data
        if (options.value.length > 0) {
          state.regionValue = options.value[0].id;
          getLineListOne()
        } else {
          state.chartsData1 = loadChartData1([]);
          state.chartsData3 = loadChartData1([]);
          state.SiteTopCurveList = []
        }

      }).catch(e => { })
    }
    // 监听 stationList 变化
    watch(
      () => siteId.value,
      (newVal) => {
        if (newVal) {
          querySiteListByUserId()
        }
      },
      { immediate: true }
    );
    onMounted(() => {
      getOptionsList()
    })
    return {
      ...toRefs(state),
      options,
      eChart2,
      eChart3,
      tab_list,
      emptyImg,
      querySiteListByUserId,
      tabChangeHandler1,
      tabChangeHandler2,
      querySystemVarOrFunctionCurveData,
      getOptionsList,
      getLineListOne,
    };
  },
});
</script>

<style scoped lang="scss">
.power-station-overview {
  color: white;
  font-size: 46px;
  display: flex;
  height: calc(100vh - 330px);

  .power-station-left {
    width: 50%;
    flex-shrink: 1;
    height: 100%;
    background: rgba(0, 23, 39, 0.5);
    border: 1px solid #1c4a87;
  }

  .power-station-right {
    width: 50%;
    flex-shrink: 1;
    height: 100%;
    display: flex;
    flex-direction: column;
    justify-content: space-between;

    .right-item {
      width: 100%;
      flex: 1;

      .echart {
        width: 100%;
        height: calc(100% - 24px);
      }
    }

    .right-1 {
      .selectRegion {
        width: 240px;
        height: 32px;
      }
    }

    // 给父盒子清除默认已有样式
    :deep(.custom-tooltip-box) {
      padding: 0 !important;
      border: none !important;
      background-color: transparent !important;

      // 给子盒子自定义样式
      .custom-tooltip-style {
        width: 202px;
        height: 100%;
        background: rgba(0, 47, 78, 0.9);
        box-shadow: inset 0px 0px 8px 1px #00ccff;
        border-radius: 3px 3px 3px 3px;
        border: 0px solid rgba(3, 165, 255, 0.25);
        padding: 5px;

        .custom-title {
          font-family: Microsoft YaHei, Microsoft YaHei;
          font-weight: 400;
          font-size: 14px;
          color: #ffffff;
          text-align: left;
          font-style: normal;
          margin: 8px 10px 8px 12px;
        }

        .custom-tooltip-item {
          display: flex;
          margin: 8px 10px 8px 12px;

          .custom-tooltip-name {
            width: 100px;
            height: 19px;
            font-family: Microsoft YaHei, Microsoft YaHei;
            font-weight: 400;
            font-size: 14px;
            color: #ffffff;
            text-align: left;
            font-style: normal;
            text-transform: none;
          }

          .custom-tooltip-value {
            text-align: right;
            width: 90px;
            font-family: Agency FB, Agency FB;
            font-weight: 400;
            font-size: 24px;
            color: #34e800;
            font-style: normal;
            text-transform: none;

            span {
              font-weight: 400;
              font-size: 14px;
              color: #ffffff;
            }
          }
        }
      }
    }
  }
}
</style>