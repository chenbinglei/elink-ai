<template>
  <title-icon-view isContentHeight title="电桩充放电量分析">
    <template #icon>
      <span class="iconfont icon-gongshuai"></span>
    </template>
    <template v-loading="listLoading" #content>
      <div v-loading="listLoading" class="content_body">
        <v-chart ref="chartComponentRef" :option="chartOption" autoresize></v-chart>
      </div>
    </template>
  </title-icon-view>
</template>

<script>
import { useStore } from "vuex";
import cloneDeep from "lodash/cloneDeep";
import { findElecCountCurveData } from "@/api/assetManagement/assetManagement";
import { computed, defineComponent, reactive, ref, toRefs, watch } from "vue";
import { getCurrentMonthFirstDay, getDaysFromCurrentTime, getNowDateAll } from "@/utils/dateTime";

export default defineComponent({
  name: "EpDisAndChargeAnalysisCom",
  setup () {

    const store = useStore();
    const siteAllIds = computed(() => {
      return store.state.assetManagement.siteAllIds;
    });

    const updateTimeNum = computed(() => {
      return store.state.assetManagement.updateTimeNum;
    });

    const that = reactive({
      listLoading: false,
      chartOption: {
        color: ["#49E9FF", "#FFE700"],
        legend: {
          bottom: 5,
          left: 'center',
          icon: 'rect',
          itemGap: 12,
          itemWidth: 10,
          itemHeight: 10,
          textStyle: { color: '#9EA9B5' },
        },
        tooltip: {
          confine: true,
          trigger: 'axis',
          borderColor: "#072948",
          backgroundColor: "#072948",
          axisPointer: { type: 'shadow' },
          textStyle: { color: "#FFFFFFCC" },
          valueFormatter: (value) => `${value ?? '-'} kWh`,
        },
        grid: {
          top: '1%',
          left: '2%',
          right: "15%",
          bottom: '5%',
          containLabel: true
        },
        xAxis: {
          name: "kWh",
          type: 'value',
          splitLine: {
            lineStyle: {
              type: 'dashed',//y轴分割线类型
              color: "rgba(255,255,255,0.3)",
            }
          },
          position: 'top',
          axisLabel: { color: "#9EA9B5" },
        },
        yAxis: {
          type: 'category',
          boundaryGap: true,
          axisLine: {
            lineStyle: {
              color: "#9EA9B5"
            }
          },
          axisLabel: {
            color: "#9EA9B5",
          },
          axisTick: { show: false },
          data: [],
        },
        series: [
          {
            data: [],
            type: 'bar',
            name: '汽车充电量',
            barMaxWidth: 12,
            fieldName: 'chargeQtList'
          },
          {
            data: [],
            type: 'bar',
            name: '汽车V2G电量',
            barMaxWidth: 12,
            fieldName: 'dischargeQtList'
          }
        ]
      }
    });


    // 查询光伏发电量分析曲线数据
    const chartComponentRef = ref(null);
    const queryElecCountCurveData = () => {
      let data = {};
      that.listLoading = true;
      if (!siteAllIds.value || !siteAllIds.value.length) {
        chartComponentRef.value?.clear();
        that.listLoading = false;
        return;
      }

      data['dateType'] = 2; //查询类型(1-日 2-月 3-年)
      data['endTime'] = getNowDateAll();
      let startTime = getDaysFromCurrentTime(-325);
      data['startTime'] = getCurrentMonthFirstDay(startTime) + " 00:00:00";
      findElecCountCurveData({ ...data, siteIds: siteAllIds.value, timer: new Date() }).then(res => {
        let returnDataInfo = res.data ? res.data : {};
        let chartOption = cloneDeep(that.chartOption);
        chartOption.yAxis.data = returnDataInfo.xAxisList ?? [];
        for (let i = 0; i < chartOption.series.length; i++) {
          chartOption.series[i].data = returnDataInfo[chartOption.series[i].fieldName];
        }
        that.chartOption = cloneDeep(chartOption);
        that.listLoading = false;
      }).catch(() => {
        that.listLoading = false;
        chartComponentRef.value?.clear();
      });
    };

    // 监听 updateTimeNum 重新获取资产数据
    const watchUpdateTimeNum = watch(() => updateTimeNum, () => {
      queryElecCountCurveData(); // 查询光伏发电量分析曲线数据
    }, { deep: true });

    return { ...toRefs(that), queryElecCountCurveData, watchUpdateTimeNum, chartComponentRef, siteAllIds, updateTimeNum };
  }
});
</script>

<style lang="scss" scoped>
:deep(.collapse_content) {
  overflow-y: initial !important;

  .content_body {
    height: 100%;
    padding: 6px 8px;
    background: #ffffff08;
    box-sizing: border-box;
  }
}
</style>