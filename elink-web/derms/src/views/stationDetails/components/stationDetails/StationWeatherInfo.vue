<template>
  <div class="station-weather-info">
    <!-- <svg-icon class="station-weather-icon" :name="info.iconDay"
      :iconName="info.period === 1 ? info.iconDay : info.iconNight" />

    <div class="station-day">{{ info.fxDate }}</div>
    <div class="station-weather-temp">{{ info.textDay }}</div>
    <div class="gap" v-if="index !== 0"></div> -->
    <!-- <div class="station-weather-icon" :class="info.icon"></div> -->
    <svg-icon class="station-weather-icon" :name="info.iconDay" :iconName="info.iconDay" />
    <div class="station-day">{{ info.day }}</div>
    <div class="station-weather-temp">{{ info.tempMin }} ~ {{ info.tempMax }}°C</div>
    <div class="gap" v-if="index !== 0"></div>
  </div>
</template>
<script lang="ts">
import {
  reactive,
  defineComponent,
  toRefs,
  getCurrentInstance,
  watch,
} from "vue";
export default defineComponent({
  name: "StationWeatherInfo",
  props: {
    info: {
      type: Object,
      default: () => {
        return {
          icon: "weather-icon1",
          day: "今天",
          temp: "24~34°C",
        };
      },
    },
    index: {
      type: Number,
      default: 0,
    },
  },
  emits: ["clickHandler"],
  setup (props, { emit }) {
    console.log(props.info);
    const clickHandler = () => {
      emit("clickHandler", props.info);
    };
    const state = reactive({});

    return { ...toRefs(state), clickHandler, };
  },
});
</script>


<style scoped lang="scss">
.station-weather-info {
  width: 146px;
  height: 52px;
  margin-left: 72px;
  background: url("@/assets/image/station-details/top_weather_bg.png") no-repeat;
  background-size: contain;
  position: relative;

  .gap {
    position: absolute;
    width: 1px;
    height: 52px;
    left: -36px;
    background: linear-gradient(180deg,
        rgba(0, 204, 255, 0) 0%,
        rgba(0, 204, 255, 0.6) 53%,
        rgba(0, 204, 255, 0) 100%);
    border-radius: 0px 0px 0px 0px;
  }

  .station-weather-icon {
    width: 24px;
    height: 24px;
    font-size: 24px;
    color: #ffffff;
    position: absolute;
    top: 15px;
    left: 7px;
    filter: brightness(0) saturate(100%) invert(100%) sepia(100%) hue-rotate(0deg);

    &.weather-icon1 {
      background: url("@/assets/image/station-details/weather-icon1.png") no-repeat;
      background-size: 100% 100%;
    }

    &.weather-icon2 {
      background: url("@/assets/image/station-details/weather-icon2.png") no-repeat;
      background-size: contain;
    }

    &.weather-icon3 {
      background: url("@/assets/image/station-details/weather-icon3.png") no-repeat;
      background-size: contain;
    }

    &.weather-icon4 {
      background: url("@/assets/image/station-details/weather-icon4.png") no-repeat;
      background-size: contain;
    }

    &.weather-icon5 {
      background: url("@/assets/image/station-details/weather-icon5.png") no-repeat;
      background-size: contain;
    }

    &.weather-icon6 {
      background: url("@/assets/image/station-details/weather-icon6.png") no-repeat;
      background-size: contain;
    }

    &.weather-icon7 {
      background: url("@/assets/image/station-details/weather-icon7.png") no-repeat;
      background-size: contain;
    }

    &.weather-icon8 {
      background: url("@/assets/image/station-details/weather-icon8.png") no-repeat;
      background-size: contain;
    }

    &.weather-icon9 {
      background: url("@/assets/image/station-details/weather-icon9.png") no-repeat;
      background-size: contain;
    }
  }

  .station-day {
    position: absolute;
    top: 10px;
    left: 59px;
    font-family: Microsoft YaHei, Microsoft YaHei;
    font-weight: 400;
    font-size: 12px;
    color: #ffffff;
    text-align: left;
    font-style: normal;
  }

  .station-weather-temp {
    position: absolute;
    top: 34px;
    left: 59px;
    font-family: Agency FB, Agency FB;
    font-weight: bold;
    font-size: 18px;
    color: #ffffff;
    text-align: right;
    font-style: normal;
    white-space: nowrap;
  }
}
</style>