<template>
  <TitleView :title="titleName" isContentHeight>
    <template #headerRight>
      <el-dropdown :disabled="exportLoading" placement="bottom-start">
        <span class="iconfont icon-xiazai exportIcon"></span>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item @click="downloadChartDataFun(1)">导出图片</el-dropdown-item>
            <el-dropdown-item @click="downloadChartDataFun(2)">导出Excel</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </template>
    <template #content>
      <div class="content_body">
        <v-chart ref="chartComponentRef" :option="chartOption" autoresize></v-chart>
      </div>
    </template>
  </TitleView>
</template>

<script>
import cloneDeep from "lodash/cloneDeep";
import {downloadFiles} from "@/utils";
import {ElMessage} from "element-plus";
import $filters from "@/common/filters";
import {getNowDateAll} from "@/utils/dateTime";
import {exportCustomExcel} from "@/common/exportExcel";
import {defineComponent, reactive, ref, toRefs, watch} from "vue";

export default defineComponent({
  name: "PowerStationRankingCom",
  props: {
    titleName: {
      type: String,
      default: ""
    },
    returnDataInfo: {
      type: Object,
      default: () => {
        return {};
      }
    }
  },
  setup(props) {
    const that = reactive({
      return_data_info: {},
      exportLoading: false,
      chartOption: {
        tooltip: {
          confine: true,
          trigger: 'axis',
          borderColor: "#072948",
          backgroundColor: "#072948",
          textStyle: {color: "#FFFFFFCC"},
          valueFormatter: (value) => `${ value ?? '-' } kWh/kWp`,
        },
        grid: {
          top: "2%",
          left: '5%',
          right: '10%',
          bottom: '1%',
          containLabel: true
        },
        dataZoom: [
          {
            type: 'inside',
            yAxisIndex: [0],
            xAxisIndex: false,
          }
        ],
        xAxis: {
          type: 'value',
          name: "kWh/kWp",
          splitLine: {
            show: false,
            lineStyle: {
              type: 'dashed',//y轴分割线类型
              color: "rgba(255,255,255,0.1)",
            }
          },
          axisLabel: {color: "#9EA9B5"},
        },
        yAxis: {
          type: 'category',
          axisLine: {
            lineStyle: {
              color: "#9EA9B5"
            }
          },
          axisLabel: {
            color: "#9EA9B5",
            hideOverlap: true,
          },
          axisTick: {show: false},
          data: []
        },
        series: {
          type: 'bar',
          barMaxWidth: 18,
          name: '等效发电时长',
          itemStyle: {
            borderRadius: [0, 6, 6, 0],
            color: {
              type: 'linear',
              x: 1,
              y: 0,
              x2: 0,
              y2: 0,
              colorStops: [
                {
                  offset: 0,
                  color: '#00b7ffff', // 0% 处的颜色
                },
                {
                  offset: 1,
                  color: '#005288cc', // 100% 处的颜色
                },
              ]
            },
          },
          label: {
            show: true,
            precision: 2,
            color: "#9EA9B5",
            position: 'right',
            valueAnimation: true
          },
          data: []
        }
      }
    });

    const chartComponentRef = ref(null);
    const downloadChartDataFun = (operateType) => {
      that.exportLoading = true;

      if (operateType === 1) {
        const fileUrl = chartComponentRef.value.getDataURL({pixelRatio: 2, backgroundColor: '#FFFFFF'});
        downloadFiles(fileUrl, `光伏运营_${props.titleName}_趋势数据_${getNowDateAll()}`);
        ElMessage({type: 'success', showClose: true, message: '下载成功！'});
        that.exportLoading = false;
      }

      if (operateType === 2) {
        let tableHeader = [
          {width: 30, key: "siteName", name: "站点名称"},
          {width: 30, key: "totalGenerationTime", name: "总发电时长",filterName: "reserveTwoNum"}
        ];

        let exportExcelList = [];
        if(that.return_data_info.rankingList && that.return_data_info.rankingList.length){
          exportExcelList = JSON.parse(JSON.stringify(that.return_data_info.rankingList));
        }
        // console.log(exportExcelList);
        exportCustomExcel(tableHeader, exportExcelList, `光伏运营_${props.titleName}_趋势数据_${getNowDateAll()}`);
        that.exportLoading = false;
      }
    };

    const watchReturnDataInfo = watch(() => props.returnDataInfo, (newReturnDataInfo) => {
      let siteNameList = [],generationTime = [];
      let chartOption = cloneDeep(that.chartOption);
      that.return_data_info = JSON.parse(JSON.stringify(newReturnDataInfo ?? {}));
      if(that.return_data_info.rankingList && that.return_data_info.rankingList.length){
        for(let i = that.return_data_info.rankingList.length - 1;i >= 0;i--){
          siteNameList.push(that.return_data_info.rankingList[i].siteName);
          let totalGenerationTime = $filters.reserveTwoNum(that.return_data_info.rankingList[i].totalGenerationTime,4);
          generationTime.push(totalGenerationTime);
        }
      }
      chartOption.yAxis.data = JSON.parse(JSON.stringify(siteNameList));
      chartOption.series.data = JSON.parse(JSON.stringify(generationTime));
      that.chartOption = cloneDeep(chartOption);
    }, {deep: true});

    return {...toRefs(that), chartComponentRef, downloadChartDataFun, watchReturnDataInfo};
  }
});
</script>

<style lang="scss" scoped>
.content_body {
  width: 100%;
  height: 100%;

  .exportIcon {
    cursor: pointer;
    font-size: 24px;
    color: #ffffffcc;
    margin-left: 12px;
  }
}

:deep(.titleView){
  .collapse_content_slot{
    overflow: initial;
  }
}
</style>