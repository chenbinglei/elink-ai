<!-- 逆变器离散率 -->
<template>
  <div class="app-container-right">
    <div class="header-form content_border">
      <el-form :model="formInline" inline>
        <el-row :gutter="16">
          <el-col :xs="24" :sm="12" :md="6" :xl="6">
            <el-form-item label="场站：">
              <el-select v-model="formInline.siteId" clearable filterable max-collapse-tags="1" placeholder="全部">
                <el-option v-for="item in siteArray" :label="item.siteName" :value="item.id" :key="item.id"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="6" :xl="6">
            <el-form-item label="选择时间：">
              <el-date-picker v-model="formInline.dateTime" type="daterange" start-placeholder="开始时间"
                end-placeholder="结束时间" value-format="YYYY-MM-DD" :disabled-date="pickerOptions.disabledDateToday"
                @change="handleDateChange" />
            </el-form-item>
          </el-col>
          <el-col :md="6" :sm="12" :xl="6" :xs="24">
            <div style="display: flex;width: 100%; justify-content: center; align-items: center;">
              <el-button :icon="Search" type="primary" @click="getEnergyStorageStatistics()">查询</el-button>
            </div>
          </el-col>
        </el-row>

      </el-form>
    </div>
    <div class="echart-bar content_border">
      <line-tabs :title="'逆变器离散率趋势'"></line-tabs>

      <div class="echarts-container" ref="chart">

      </div>
      <div class="lenged">
        <div class="lenged-item">
          <div class="lenged-item-color" style="background:#D35E51"></div>
          <div class="lenged-item-text">​严重：>20%</div>
        </div>
        <div class="lenged-item">
          <div class="lenged-item-color" style="background:#C29C1F"></div>
          <div class="lenged-item-text">​异常：10%-20%</div>
        </div>
        <div class="lenged-item">
          <div class="lenged-item-color" style="background:#4A7CD8"></div>
          <div class="lenged-item-text">​良好：5%-10%</div>
        </div>
        <div class="lenged-item">
          <div class="lenged-item-color" style="background:#278E61"></div>
          <div class="lenged-item-text">优秀：0%-5%</div>
        </div>
      </div>

    </div>
    <div class="tableContent content_border" ref="tableContentRef">
      <line-tabs :title="'逆变器偏差详情'" :time="'统计时间：' + time" :buttonText="'导出'" @click="handleButtonClick"
        :icon="'Download'"></line-tabs>
      <div class="tableCenter single" ref="tableCenterRef" v-resize="setTableMaxHeight">
        <el-table v-loading="listLoading" :data="tableData" stripe :max-height="tableMaxHeight">
          <el-table-column label="设备名称" align="center" prop="name"></el-table-column>
          <el-table-column label="SN" align="center" prop="sn"></el-table-column>
          <el-table-column label="额定功率(kW)" align="center" prop="ratedPower"></el-table-column>
          <el-table-column label="组串容量（kWp)" align="center" prop="ratedCapacity"></el-table-column>
          <el-table-column label="日平均功率（kW)" align="center" prop="dailyAveragePower"></el-table-column>
          <el-table-column label="偏差比例（%）" align="center" prop="deviationRatio" sortable>
            <template #header>
              偏差比例（%）

              <el-tooltip placement="right" >

                <template #default>
                  <el-icon>
                    <InfoFilled />
                  </el-icon>
                </template>
                <template #content>
                  <div v-for="item in typeList" :key="item.value" class="type-item">
                    <div class="circle" :style="{ backgroundColor: item.color }"></div>
                    {{ item.label }}
                  </div>
                </template>
              </el-tooltip>
            </template>
          </el-table-column>

          <el-table-column label="组串离散率（%）" align="center" prop="stringDiscreteness" sortable></el-table-column>
          <el-table-column label="操作" align="center"></el-table-column>
        </el-table>
      </div>
      <div class="tablePagination" ref="tablePaginationRef">
        <Pagination v-model:pageSize="pageNum" :totalNumber="totalNumber" v-model:currentPage="currentPage"
          @pageChange="listArray" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, onMounted, onUnmounted, reactive } from "vue";
import { ElMessage } from "element-plus";
import { pickerOptionsGthanAcTime, getDaysFromCurrentTime, getNowDate } from "@/utils/dateTime";

import { findSiteListByUserId } from "@/api/centralMonitoring/centralMonitoring";
const pickerOptions = pickerOptionsGthanAcTime(1);
import * as echarts from 'echarts';
const formInline = ref({
  siteId: "",
  dateTime: [getDaysFromCurrentTime(-6), getDaysFromCurrentTime(0)],
});
const siteArray = ref([]);
const getSiteInfoByUserId = async () => {
  const res = await findSiteListByUserId({});
  siteArray.value = res.data;
};
const handleDateChange = (value) => {
  if (!value || value.length !== 2) return;
  const [startDate, endDate] = value;
  const start = new Date(startDate);
  const end = new Date(endDate);
  const diffDays = Math.ceil((end - start) / (1000 * 60 * 60 * 24));
  if (diffDays < 1 || diffDays > 7) {
    ElMessage.error('请选择 1 至 7 天的时间范围');
    formInline.value.dateTime = [];
  }
};
const typeList = [
  { label: '<-20%  异常，建议整改', value: '01', color: "#F53146" },
  { label: '-20%~-10%  较差', value: '02', color: "#EEBA1A" },
  { label: '-10%~10%  一般 ', value: '03', color: "#5990FE" },
  { label: '10%~20%  良好', value: '04', color: "#6ACA94" },
  { label: '>20%  优秀', value: '05', color: "#2DA76A" },
  { label: '未分析', value: '06', color: "#999999" },
];
// --------------------------echarts与table数据处理--------------------------
const loading = ref(true);
const time = ref(getNowDate());
const pageNum = ref(10);
const currentPage = ref(1);
const totalNumber = ref(0);
const tableData = ref([]);
const tableMaxHeight = ref(300);
// 下载事件
const handleButtonClick = () => {
};
const getlistArray = async () => {
  tableData.value = [
    { name: '1', },
    { name: '1', },
    { name: '1', },
    { name: '1', },
    { name: '1', }, { name: '1', }, { name: '1', }, { name: '1', },
  ]
  loading.value = false;
  totalNumber.value = tableData.value.length;
};
// 计算出表格最大高度
const tableContentRef = ref(null);
const tablePaginationRef = ref(null);
const setTableMaxHeight = () => {
  let tableContentHeight = tableContentRef.value.offsetHeight - 78;
  tableMaxHeight.value = tableContentHeight - tablePaginationRef.value.offsetHeight;
};
// -----------------------------echarts----------------------------------
const chart = ref(null);
let myChart = null;

// 初始化图表
const initChart = () => {
  if (!chart.value) return;

  // 使用 SVG 渲染器以支持 tooltip.className
  myChart = echarts.init(chart.value, 'svg');

  const option = getOption();
  myChart.setOption(option);
  // echarts添加点击事件
  myChart.on('click', (params) => {
    // 你可以在这里做你想做的事，比如跳转页面、弹窗等
  });
  window.addEventListener('resize', onResize);
};
const getOption = () => {
  return {
    grid: {
      left: '1%',
      right: '1%',
      bottom: '2%',
      top: '15%',
      containLabel: true
    },
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow',
        style: {
          color: 'rgba(0, 0, 0, 0.1)'
        }
      },
      className: "custom-tooltip-box",
      formatter: function (params) {
        let result = params[0].name + '<br/>';
        params.forEach(item => {
          result += item.marker + item.seriesName + ': ' + item.value.toFixed(2) + '<br/>';
        });
        let htmlText = `<div class='custom-tooltip-style'><div class="custom-tooltip-title">${result}</div>`;
        return htmlText;
      }
    },

    xAxis: {
      type: 'category',
      data: ['A', 'B', 'C', 'D', 'E', 'F', 'G']
    },
    yAxis: {
      type: 'value',
      name: '离散率: %',
      splitLine: {
        lineStyle: {
          color: 'rgba(255, 255, 255, 0.3)',
          width: 1,
          type: 'dashed'
        }
      }
    },
    series: [
      {
        name: '类型', // 这个名字必须和 legend.data 中的某个值一致
        type: 'bar',
        data: [
          { value: 10, type: '1' },
          { value: 5, type: '2' },
          { value: 8, type: '3' },
          { value: 6, type: '4' },
          { value: 4, type: '1' },
          { value: 5, type: '2' },
          { value: 6, type: '1' },
        ],
        encode: {
          x: 'name', // 假设你的数据中有 name 字段
          y: 'value'
        },
        itemStyle: {
          color: (params) => {
            const colorMap = {
              '1': '#D35E51', // 严重
              '2': '#C29C1F', // 异常
              '3': '#4A7CD8', // 良好
              '4': '#278E61' // 优秀
            };
            return colorMap[params.data.type];
          }
        },
        barWidth: 25,
      }
    ]
  };
};
// 窗口大小变化时调整图表
const onResize = () => {
  if (myChart) {
    myChart.resize();
  }
};
//---------------------------echarts结束----------------------------------------
onMounted(() => {
  getSiteInfoByUserId();
  initChart();
  getlistArray();
});
</script>

<style lang="scss" scoped>
.app-container-right {
  .echart-bar {
    height: 40%;
  }

  .echarts-container {
    height: calc(100% - 42px);
    width: 100%;


  }

  .echart-bar {
    position: relative;

    .lenged {
      color: rgba(255, 255, 255, 0.8);
      position: absolute;
      font-size: 12px;
      top: 10%;
      left: 38%;
      display: flex;
      align-items: center;

      .lenged-item {
        margin-right: 28px;
        display: flex;
        align-items: center;
      }

      .lenged-item-color {
        width: 8px;
        height: 8px;
        margin-right: 8px;
      }
    }
  }

  .tableContent {
    margin-top: 10px;

    .tableCenter {
      padding: 12px;
      box-sizing: border-box;
    }
  }



}

// ::v-deep .custom-tooltip-box {
//   padding: 0 !important;
//   border: none !important;
//   background-color: transparent !important;

//   // 给子盒子自定义样式
//   .custom-tooltip-style {

//     background: rgba(0, 47, 78, 0.9);
//     box-shadow: inset 0px 0px 8px 1px #00ccff;
//     border-radius: 3px 3px 3px 3px;
//     padding: 5px 15px;

//     .custom-tooltip-title {
//       font-family: Microsoft YaHei, Microsoft YaHei;
//       font-weight: 400;
//       font-size: 14px;
//       color: #ffffff;
//       text-align: left;
//       font-style: normal;
//     }

//     .custom-tooltip-content {
//       display: flex;
//       align-items: center;
//       justify-content: space-between;
//     }


//     .custom-radio {
//       width: 10px;
//       height: 10px;
//       border-radius: 50%;
//       display: inline-block;
//       margin-right: 5px;
//       box-sizing: border-box;

//     }



//     .custom-tooltip-value {
//       font-family: Microsoft YaHei, Microsoft YaHei;
//       font-weight: 400;
//       margin-left: 20px;
//       font-size: 14px;
//       color: #ffffff;
//       text-align: left;
//       font-style: normal;
//     }
//   }
// }

.type-item {
  padding: 5px 10px;
  display: flex;
  align-items: center;
  font-size: 16px;

  .circle {
    width: 8px;
    height: 8px;
    border-radius: 50%;
    margin-right: 20px;
    box-sizing: border-box;
  }
}
::v-deep .ui-design .el-popper{
    background: rgba(0, 47, 78, 0.9) !important;
    box-shadow: inset 0px 0px 8px 1px #00ccff !important;
}

</style>
