import { computed,onMounted } from "vue";

import { CurveColorMap } from "@/common/enum";
import {
  commonChartOption,
  tooltipFormatter,
} from "@/views/monitor/systemMonitoring/constant";

export default function useChart ({ rawData }) {
  console.log('useChart')
  const options = computed(() => {
    const { dateList = [], dataInfoList = [] } = rawData.value;
    const { legend, xAxis, yAxis, tooltip, ...rest } = commonChartOption;
    const prefixMap = dataInfoList.reduce((map, item,index) => {
       const key = `${item.fieldCode}_${index}`;
         map[key] = `${item.deviceName}/`;
      // map[item.fieldCode] = `${item.deviceName}/`;
      return map;
    }, {});
    return {
      ...rest,
      tooltip: {
        ...tooltip,
        formatter: tooltipFormatter({
          unit: "",
          prefixMap,
        }),
      },
      legend: {
        ...legend,
        data: dataInfoList?.map((item) => item.fieldName),
        top: "3%",
        left: "3%",
      },
      xAxis: {
        ...xAxis,
        data: dateList,
      },
      yAxis: {
        ...yAxis,
        name: "",
      },
      series: dataInfoList?.map(({ deviceName, fieldName, dataList, fieldCode }, index) => ({
        dataGroupId: deviceName,
        // id: fieldCode ,
        id: `${fieldCode}_${index}`, // 唯一 ID
        name: fieldName,
        type: "line",
        data: dataList,
        emphasis: {
          focus: "series",
        },
        itemStyle: {
          color: CurveColorMap[fieldCode],
        },
        lineStyle: {
          color: CurveColorMap[fieldCode],
        },
      })
      ),

    };
  });
  

  return {
    options,
  };
}
