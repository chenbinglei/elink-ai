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
import {onMounted, reactive, toRefs, defineComponent, ref, watch} from "vue";

export default defineComponent({
  name: "PublicCardChartCom",
  props: {
    titleName: {
      type: String,
      default: ""
    },
    unit: {
      type: String,
      default: ""
    },
    boundaryGap: {
      type: Boolean,
      default: false
    },
    seriesListArray: {
      type: Array,
      default: () => []
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
        legend: {
          top: 0,
          left: 'center',
          textStyle: {color: '#9EA9B5'},
        },
        tooltip: {
          confine: true,
          trigger: 'axis',
          borderColor: "#072948",
          backgroundColor: "#072948",
          textStyle: {color: "#FFFFFFCC"},
          valueFormatter: (value) => `${ $filters.reserveTwoNum(value,4,'-') } ${props.unit}`,
        },
        grid: {
          top: '12%',
          left: '2%',
          right: '2%',
          bottom: '1%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          splitNumber: 2,
          boundaryGap: false,
          axisLine: {
            lineStyle: {
              color: "#9EA9B5"
            }
          },
          axisLabel: {
            color: "#9EA9B5",
            hideOverlap: true,
            // showMinLabel: true,
            // showMaxLabel: true,
            // alignMinLabel: "left",
            // alignMaxLabel: "right"
          },
          axisTick: {show: false},
          data: []
        },
        dataZoom: [{type: "inside", start: 0, end: 100}],
        yAxis: {
          type: 'value',
          splitLine: {
            show: true,
            lineStyle: {
              type: 'dashed',// y轴分割线类型
              color: "rgba(255,255,255,0.1)",
            }
          },
          axisLabel: {color: "#9EA9B5"},
        },
        series: []
      },
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
        let tableHeader = [], exportExcelListData = [];

        if (props.seriesListArray && props.seriesListArray.length) {
          for (let j = 0; j < props.seriesListArray.length; j++) {
            if (props.seriesListArray[j].fieldName) {
              tableHeader.push({
                width: 25,
                name: props.seriesListArray[j].name,
                key: props.seriesListArray[j].fieldName,
              });
            }
          }
        }

        if (that.return_data_info.dateList && that.return_data_info.dateList.length) {
          for (let i = 0; i < that.return_data_info.dateList.length; i++) {
            exportExcelListData[i] = { dateTime: that.return_data_info.dateList[i] };
            for (let j = 0; j < tableHeader.length; j++) {
              let active_value = "";
              if(that.return_data_info[tableHeader[j].key] && that.return_data_info[tableHeader[j].key].length){
                active_value = that.return_data_info[tableHeader[j].key][i];
              }
              exportExcelListData[i][tableHeader[j].key] = active_value;
            }
          }
        }

        let newTableHeader = [{width: 25, key: "dateTime", name: "时间"}, ...tableHeader];
        exportCustomExcel(newTableHeader, exportExcelListData, `光伏运营_${props.titleName}_趋势数据_${getNowDateAll()}`);
        that.exportLoading = false;
      }
    };

    const setChartOptionFun = () => {
      let chartOption = cloneDeep(that.chartOption);
      chartOption.yAxis.name = props.unit;
      chartOption.xAxis.boundaryGap = props.boundaryGap;
      // chartOption.xAxis.data = JSON.parse(JSON.stringify(props.dateList));
      chartOption.series = JSON.parse(JSON.stringify(props.seriesListArray));
      that.chartOption = cloneDeep(chartOption);
    };

    const watchReturnDataInfo = watch(() => props.returnDataInfo, (newReturnDataInfo) => {
      let chartOption = cloneDeep(that.chartOption);
      let seriesList = JSON.parse(JSON.stringify(props.seriesListArray ?? []));
      that.return_data_info = JSON.parse(JSON.stringify(newReturnDataInfo ?? {}));
      chartOption.xAxis.data = JSON.parse(JSON.stringify(that.return_data_info?.dateList ?? []));
      for(let i = 0;i < seriesList.length;i++) seriesList[i].data = that.return_data_info[seriesList[i].fieldName] ?? [];

      chartOption.series = JSON.parse(JSON.stringify(seriesList));
      that.chartOption = cloneDeep(chartOption);
    }, {deep: true});

    onMounted(() => {
      setChartOptionFun();
    });

    return {...toRefs(that), chartComponentRef, downloadChartDataFun, setChartOptionFun, watchReturnDataInfo};
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