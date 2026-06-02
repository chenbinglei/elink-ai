// options.js
// 在 methods 或 computed 中定义
function truncateDecimal(num, digits = 4) {
  const factor = Math.pow(10, digits)
  return Math.trunc(num * factor) / factor
}
export const changeoptionsProfit = (data) => {
  return {
    tooltip: {
      trigger: 'axis',
      className: "custom-tooltip-box",
      axisPointer: {
        type: 'shadow'
      },
      formatter: (params) => {
        console.log(params);
        const xAxisValue = params[0]?.name || ""; // 获取 x 轴值

        let html = `<strong style="color:#fff;font-weight:bold;">${xAxisValue}</strong><br/>`;

        params.forEach((p) => {
          
          const color = p.color;
          html += `
       <div><span style="
            display:inline-block;
            margin:2px 8px 2px 0;
            border-radius:50%;
            width:6px;
            height:6px;
            background:${color};
          "></span>
          <span style="color:#fff;font-size:14px;">${p.seriesName}： ${ truncateDecimal(p.value, 4)}</span></div>
        `;
        });
        let htmlText = `<div class='custom-tooltip-style'><div class="custom-tooltip-title">${html}</div>`;
        return htmlText;

      },
    },
    // legend: {
    //   data: ['充电成本', '放电收入', '收益']
    // },
    xAxis: {
      type: 'category',
      axisLabel: {
        color: 'rgba(255, 255, 255, 1)'
      },
      axisLine: {
        lineStyle: {
          color: 'rgba(0, 85, 153, 1)'
        }
      },
      axisTick: { show: false },
      data: data.dateList
    },
    legend: {
      top: "5%",

      // icon: 'rect',
      itemWidth: 12,
      itemHeight: 12,
      itemStyle: {
        borderColor: "inherit"
      },
      textStyle: {
        color: '#ffffff'
      }
    },
    grid: {
      left: '12%',
      right: '6%',
      top: "20%",
      bottom: "10%",
      containLabel: true // 关键：防止标签被截断
    },
    yAxis: {
      type: 'value',
      axisLabel: {
        color: 'rgba(255, 255, 255, 1)'
      },
      axisLine: {
        lineStyle: {
          color: 'rgba(0, 85, 153, 1)'
        }
      },
      splitLine: {
        lineStyle: {
          color: '#005599'
        }
      }
    },
    series: [
      {
        name: '充电成本',
        type: 'bar',
        data: data.chargeMoneyList,
        itemStyle: {
          color: "rgba(0, 232, 131, 0.30)",
          borderColor: "rgba(0, 232, 131, 1)",
          borderWidth: 1,
          borderRadius: 1
        },
        backgroundStyle: {
          color: 'rgba(16, 45, 78, 1)'
        }
      },
      {
        name: '放电收入',
        type: 'bar',
        data: data.dischargeMoneyList,
        itemStyle: {
          color: "rgba(255, 187, 0, 0.30)",
          borderColor: "rgba(255, 187, 0, 1)",
          borderWidth: 1,
          borderRadius: 1
        },
        backgroundStyle: {
          color: 'rgba(16, 45, 78, 1)'
        }
      },
      {
        name: '收益',
        type: 'line',
        data: data.incomeList,
        // smooth: true,
        itemStyle: {
          color: "rgba(0, 155, 255, 1)",

        },
      }
    ]
  }
}
export const disChangeoptionsProfit = (data) => {
  return {
      tooltip: {
      trigger: 'axis',
      className: "custom-tooltip-box",
      axisPointer: {
        type: 'shadow'
      },
      formatter: (params) => {
        console.log(params);
        const xAxisValue = params[0]?.name || ""; // 获取 x 轴值

        let html = `<strong style="color:#fff;font-weight:bold;">${xAxisValue}</strong><br/>`;

        params.forEach((p) => {
          
          const color = p.color;
          html += `
       <div><span style="
            display:inline-block;
            margin:2px 8px 2px 0;
            border-radius:50%;
            width:6px;
            height:6px;
            background:${color};
          "></span>
          <span style="color:#fff;font-size:14px;">${p.seriesName}： ${ truncateDecimal(p.value, 4)}</span></div>
        `;
        });
        let htmlText = `<div class='custom-tooltip-style'><div class="custom-tooltip-title">${html}</div>`;
        return htmlText;

      },
    },
    xAxis: {
      type: 'category',
      axisLabel: {
        color: 'rgba(255, 255, 255, 1)'
      },
      axisLine: {
        lineStyle: {
          color: 'rgba(0, 85, 153, 1)'
        }
      },
      axisTick: { show: false },
      data: data.dateList
    },
    legend: {
      top: "5%",

      // icon: 'rect',
      itemWidth: 12,
      itemHeight: 12,
      itemStyle: {
        borderColor: "inherit"
      },
      textStyle: {
        color: '#ffffff'
      }
    },
    grid: {
      left: '12%',
      right: '6%',
      top: "20%",
      bottom: "10%",
      containLabel: true // 关键：防止标签被截断
    },
    yAxis: {
      type: 'value',
      axisLabel: {
        color: 'rgba(255, 255, 255, 1)'
      },
      axisLine: {
        lineStyle: {
          color: 'rgba(0, 85, 153, 1)'
        }
      },
      splitLine: {
        lineStyle: {
          color: '#005599'
        }
      }
    },
    series: [
      {
        name: '累计收益',
        type: 'line',
        data: data.totalIncomeList,
        itemStyle: {
          color: "rgba(0, 232, 131, 0.30)",

        },
      },
    ]
  }
}