<template>
  <div class="panoramic-monitor-title flex_b_c">
    <div class="title-left flex_b_c">
      <div class="title-weather flex_s_c">{{ weatherInfo?.weather }}</div>
      <div class="title-humidity">
        <span>{{ weatherInfo?.curTemp }}</span
        >℃
      </div>
      <div class="title-temperature">{{ weatherInfo?.dayTemp }}℃</div>
    </div>
    <div class="title-text">
      综合能源聚合管理平台
    </div>
    <div class="title-right">
      <div class="title-date">{{ dayTime }}</div>
    </div>
  </div>
</template>
<script>
import { useRouter } from "vue-router";
import { defineComponent, onMounted, onUnmounted, reactive, toRefs } from "vue";
import { formatDateTime } from "@/utils/dateTime";

export default defineComponent({
  name: "panoramicMonitorTitle",
  setup() {
    const vueRouter = useRouter();
    let intervalId = 0;
    const state = reactive({
      dayTime: "2025/06/18 12:00:00",
      weatherInfo: {
        weather: "晴天",
        curTemp: "15",
        dayTemp: "16/28",
      },
    });
   
    onMounted(() => {
      intervalId = setInterval(() => {
        state.dayTime = formatDateTime(new Date(), "YYYY-MM-DD HH:mm:ss");
        // count.value++;
      }, 1000); // 1000毫秒，即1秒
    });

    onUnmounted(() => {
      clearInterval(intervalId);
    });
    return { ...toRefs(state) };
  },
});
</script>
<style scoped lang="scss">
.flex_s_c {
  display: flex;
  align-items: center;
  justify-content: flex-start;
}

.flex_b_c {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.panoramic-monitor-title {
  width: 100%;
  // min-width: 1800px;
  height: 84px;
  position: absolute;
  top: 0;
  left: 0;
  background: url("@/assets/image/panoramic-monitor/panoramic-monitor-title-bg.png")
    top left no-repeat;
  background-size: 100% 100%;
  .title-right {
    width: 200px;
    height: 40px;
    // padding-right: 24px;
    margin-top: -5px;
    font-size: 20px;
    color: #ffffff;
    font-family: Agency FB, Agency FB;
  }

  .title-left {
    padding-left: 40px;
    margin-top: -15px;
    height: 40px;
    font-family: Agency FB, Agency FB;
  }

  .title-humidity {
    margin-top: -5px;
    margin-left: 15px;
    margin-right: 10px;
    color: #fff;
    font-size: 14px;

    span {
      margin-right: 4px;
      font-size: 24px;
    }
  }

  .title-temperature {
    font-size: 20px;
    color: #fff;
  }

  .title-text {
    position: absolute;
    left: 50%;
    top: 12px;
    font-family: zihun35hao-jindianyahei;
    transform: translateX(-50%);
    margin-left: -30px;
    width: 600px;
    text-align: center;
    font-size: 32px;
    color: #fff;
    cursor: pointer;
  }

  .title-weather {
    font-size: 14px;
    color: #ffffff;

    &::before {
      display: block;
      margin-right: 15px;
      width: 24px;
      height: 24px;
      content: "";
      background: url("@/assets/image/panoramic-monitor/weather-icon1.png") top
        left no-repeat;
    }
  }
}
</style>