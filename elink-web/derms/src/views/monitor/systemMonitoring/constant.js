import { FieldUnitMap } from "@/common/enum";

export const chartIconMap = {
  lineCircle:
    "path://M908.49 562.043H754.526c-23.01 114.216-123.222 200.183-243.363 200.183S290.81 676.259 267.8 562.043H113.836c-27.431 0-49.667-22.405-49.667-50.044 0-27.64 22.236-50.044 49.667-50.044H267.8c23.01-114.216 123.222-200.183 243.363-200.183s220.353 85.967 243.363 200.183H908.49c27.431 0 49.666 22.404 49.666 50.044 0 27.64-22.236 50.044-49.666 50.044zM511.163 361.865c-82.291 0-148.998 67.218-148.998 150.135s66.707 150.135 148.998 150.135S660.16 594.917 660.16 512s-66.706-150.135-148.997-150.135z",
};

const createColor = (param) => {
  if (typeof param.color === "string") {
    return `background-color: ${param.color};`;
  } else {
    const [startColor, endColor] = param.color.colorStops;
    return `background:  linear-gradient(180deg, ${startColor.color}, ${endColor.color})`;
  }
};

export const tooltipFormatter =
  ({ isRounded = true, unit, prefixMap }) =>
  (params) => {
    const legendStyle = isRounded ? "border-radius: 50%;" : "";
    return `<div class="flex flex-col justify-start items-stretch">
      <div class="text-white text-18px mb-6px family-fb">${
        params[0]?.axisValueLabel || "--"
      }</div>
      ${params
        .map((param,index) => {
          const unitById = unit ?? FieldUnitMap[param.seriesId];
          const namePrefix = prefixMap ? prefixMap[param.seriesId] : "";
          return `
          <div class="flex justify-start items-center text-white text-14px">
              <span class="flex mr-4px w-8px h-8px" style="${legendStyle} ${createColor(
            param
          )}"></span>
              <span class="mr-8px">
              ${
                namePrefix + param.seriesName ?? "--"
              }</span>
              <span class="text-16px flex-1 text-right family-fb">${
                param.value ?? "--"
              }</span>
              <span class="ml-4px">${unitById ?? ""}</span>
              </div>
          `;
        })
        .join("")}
      </div>`;
  };

export const commonChartOption = {
  grid: {
    top: 70,
    left: "8%",
    right: "5%",
    bottom: "10%",
  },
  tooltip: {
    trigger: "axis",
    axisPointer: {
      type: "line",
    },
    appendToBody: true,
    formatter: tooltipFormatter({}),
    className: "fancy-echart-tooltip",
    textStyle: {
      color: "#ffffff",
      fontSize: 14,
    },
  },
  axisPointer: {
    lineStyle: {
      color: "#00CCFF",
    },
  },
  legend: {
    top: "3%",
    left: "8%",
    data: [],
    height: 16,
    itemHeight: 8,
    textStyle: {
      color: "#ffffff",
      fontSize: 14,
      height: 20,
      lineHeight: 14,
    },
    icon: chartIconMap.lineCircle,
  },
  xAxis: {
    type: "category",
    boundaryGap: true,
    axisTick: {
      alignWithLabel: true,
    },
    axisLine: {
      show: true,
      lineStyle: {
        color: "#00CCFF",
      },
    },
    axisLabel: {
      color: "#00CCFF",
      showMaxLabel: true,
    },
    data: [],
    splitNumber: 7,
  },
  yAxis: {
    type: "value",
    axisLine: {
      show: false,
    },
    axisLabel: {
      color: "#00CCFF",
    },
    splitLine: {
      show: true,
      lineStyle: {
        type: "dashed",
        color: "#005B8B",
      },
    },
    name: "kW",
    nameLocation: "end",
    nameGap: 15,
    nameTextStyle: {
      color: "#00CCFF",
      padding: [0, 0, 0, -25],
    },
  },
  dataZoom: [
    {
      type: "inside",
      xAxisIndex: 0,
      show: false,
    },
  ],
};

export const commonVChartProps = {
  autoresize: true,
  updateOptions: {
    lazyUpdate: true,
    notMerge: true,
    silent: true,
  },
};

export const INJECT_KEY_DEVICE_INFO = Symbol("deviceInfo");

export const airStateList = [
  {
    id: "coolingState",
    name: "制冷",
  },
  {
    id: "heatingState",
    name: "制热",
  },
  {
    id: "internalState",
    name: "除湿",
  },
  {
    id: "externalState",
    name: "通风",
  },
];

export const dimIntervalMap = {
  date: 119,
  month: 6,
  year: 2,
};
