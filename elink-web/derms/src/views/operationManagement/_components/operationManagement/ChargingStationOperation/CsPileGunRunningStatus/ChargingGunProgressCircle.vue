<template>
  <div class="chargingGunProgressCircle">
    <v-chart ref="chartComponentRef" :option="chartOption" autoresize></v-chart>
  </div>
</template>

<script>
import { defineComponent, reactive, toRefs, watch } from "vue";

export default defineComponent({
  name: "ChargingGunProgressCircle",
  props: {
    gunWorkState: {
      type: [Number, String],
      default: 1
    },
    batterySOC: {
      type: [Number, String],
      default: 0
    },
  },
  setup(props) {
    // 创建响应式状态，每个组件实例独立
    const state = reactive({
      // 直接初始化 chartOption 为响应式对象
      chartOption: {
        title: [
          {
            text: props.gunWorkState === 1 ? '充电' : '放电',
            x: 'center',
            top: '50%',
            textStyle: {
              fontSize: 14,
              color: props.gunWorkState === 1 ? '#00f0ff' : '#05FF00',
            }
          },
          {
            text: props.batterySOC + '%',
            x: 'center',
            top: '30%',
            textStyle: {
              fontSize: 14,
              color: props.gunWorkState === 1 ? '#00f0ff' : '#05FF00',
            },
          }
        ],
        series: {
          type: 'pie',
          name: 'circle',
          clockWise: true,
          radius: ['90%', '78%'],
          itemStyle: {
            normal: {
              label: { show: false },
              labelLine: { show: false }
            }
          },
          hoverAnimation: false,
          data: [
            {
              value: props.batterySOC,
              name: '占比',
              itemStyle: {
                normal: {
                  color: {
                    colorStops: [
                      {
                        offset: 0,
                        color: props.gunWorkState === 1 ? '#00f0ff' : '#05FF00',
                      },
                      {
                        offset: 1,
                        color: props.gunWorkState === 1 ? '#35FBFA' : '#05FF00',
                      }
                    ]
                  }
                }
              }
            },
            {
              name: '剩余',
              value: 100 - props.batterySOC,
              itemStyle: {
                normal: { color: '#E1E8EE' }
              }
            }
          ]
        }
      }
    });

    // 监听 prop 变化并更新响应式状态
    watch(
      [() => props.batterySOC, () => props.gunWorkState],
      ([newBatterySOC, newGunWorkState]) => {
        const batterySOC = newBatterySOC || 0;
        const isCharging = newGunWorkState === 1;
        const color = isCharging ? '#00f0ff' : '#05FF00';
        const gradient = isCharging 
          ? [{ offset: 0, color: '#00f0ff' }, { offset: 1, color: '#35FBFA' }]
          : [{ offset: 0, color: '#05FF00' }, { offset: 1, color: '#05FF00' }];

        // 更新图表选项
        state.chartOption.title[0].text = isCharging ? '充电' : '放电';
        state.chartOption.title[0].textStyle.color = color;
        state.chartOption.title[1].text = batterySOC + '%';
        state.chartOption.title[1].textStyle.color = color;
        
        // 更新系列数据
        state.chartOption.series.data[0].value = batterySOC;
        state.chartOption.series.data[1].value = 100 - batterySOC;
        state.chartOption.series.data[0].itemStyle.normal.color = {
          colorStops: gradient
        };
      },
      { immediate: true }
    );

    return { ...toRefs(state) };
  }
});
</script>

<style lang="scss" scoped>
.chargingGunProgressCircle {
  width: 100%;
  height: 100%;
}
</style>