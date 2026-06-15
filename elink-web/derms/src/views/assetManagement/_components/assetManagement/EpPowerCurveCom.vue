<template>
  <title-icon-view isContentHeight title="电桩功率曲线">
    <template #icon>
      <span class="iconfont icon-gongshuai"></span>
    </template>
    <template #content>
      <div v-loading="listLoading" class="content_body">
        <v-chart ref="chartComponentRef" :option="chartOption" autoresize></v-chart>
      </div>
    </template>
  </title-icon-view>
</template>

<script lang="ts">
import { useAssetManagementStore } from '@/stores/index';

import cloneDeep from "lodash/cloneDeep";
import {getNowDate, getNowDateAll} from "@/utils/dateTime";
import {computed, defineComponent, reactive, ref, toRefs, watch} from "vue";
import {findChargeSitePowerCurve} from "@/api/assetManagement/assetManagement";

export default defineComponent({
  name: "EpPowerCurveCom",
  setup() {

    const assetManagementStore = useAssetManagementStore();
    const siteAllIds = computed(() => {
      return assetManagementStore.siteAllIds;
    });

    const updateTimeNum = computed(() => {
      return assetManagementStore.updateTimeNum;
    });

    const that = reactive({
      listLoading: false,
      chartOption: {
        color: ["#49E9FF", "#FFE700"],
        legend: {
          bottom: 0,
          left: 'center',
          textStyle: {
            color: '#9EA9B5',
          },
        },
        tooltip: {
          confine: true,
          trigger: 'axis',
          borderColor: "#072948",
          backgroundColor: "#072948",
          textStyle: {color: "#FFFFFFCC"},
          valueFormatter: (value) => `${ value ?? '-' } kW`,
        },
        grid: {
          top: '15%',
          left: '2%',
          right: '1%',
          bottom: '12%',
          containLabel: true
        },
        dataZoom: [{type: "inside", start: 0, end: 100}],
        xAxis: {
          type: 'category',
          boundaryGap: false,
          axisLine: {
            lineStyle: {
              color: "#9EA9B5"
            }
          },
          axisLabel: {
            color: "#9EA9B5",
            hideOverlap: true,
            showMinLabel: true,
            showMaxLabel: true,
            alignMinLabel: "left",
            alignMaxLabel: "right",
          },
          axisTick: { //y轴刻度线
            show: false
          },
          data: [],
        },
        yAxis: {
          name: "kW",
          type: 'value',
          splitLine: {
            lineStyle: {
              type: 'dashed',//y轴分割线类型
              color: "rgba(255,255,255,0.3)",
            }
          },
          axisLabel: {
            color: "#9EA9B5"
          },
        },
        series: [
          {
            data: [],
            type: 'line',
            name: '汽车充电功率',
            showSymbol: false,
            areaStyle: { opacity: 0.2 },
            fieldName: 'chargePowerList'
          },
          {
            data: [],
            type: 'line',
            name: '汽车放电功率',
            showSymbol: false,
            areaStyle: { opacity: 0.2 },
            fieldName: 'dischargePowerList'
          }
        ]
      }
    });

    // 查询充放电功率曲线数据
    const chartComponentRef = ref(null);
    const queryChargeSitePowerCurve = () => {
      let data = {};
      that.listLoading = true;
      if (!siteAllIds.value || !siteAllIds.value.length) {
        chartComponentRef.value?.clear();
        that.listLoading = false;
        return;
      }

      data['endTime'] = getNowDateAll();
      data['startTime'] = getNowDate() + " 00:00:00";
      findChargeSitePowerCurve({...data, siteIds: siteAllIds.value, timer: new Date()}).then(res => {
        let returnDataInfo = res.data ? res.data : {};
        let chartOption = cloneDeep(that.chartOption);
        chartOption.xAxis.data = returnDataInfo.xAxisList ?? [];
        for (let i = 0; i < chartOption.series.length; i++) {
          let seriesDataList = [];
          if (returnDataInfo.dataInfoList && returnDataInfo.dataInfoList.length) {
            let findItem = returnDataInfo.dataInfoList.find(item => item.ename === chartOption.series[i].fieldName);
            if (findItem) seriesDataList = findItem?.dataList;
          }
          chartOption.series[i].data = JSON.parse(JSON.stringify(seriesDataList));
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
      queryChargeSitePowerCurve();
    }, {deep: true});

    return {...toRefs(that), updateTimeNum, siteAllIds, queryChargeSitePowerCurve, watchUpdateTimeNum};
  }
});
</script>

<style lang="scss" scoped>
.content_body {
  width: 100%;
  height: 100%;
  padding: 6px 8px;
  background: #ffffff08;
  box-sizing: border-box;
}

:deep(.collapse_content) {
  overflow-y: initial !important;
}
</style>