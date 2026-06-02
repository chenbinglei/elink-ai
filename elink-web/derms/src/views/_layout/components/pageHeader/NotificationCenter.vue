<template>
  <div class="notificationCenter">
    <!-- 综合分析 -->
    <!-- <div class="w-119px h-36px flex justify-center items-center mr-18px rounded-18px cursor-pointer comprehensive-analysis">
      <img src="/img/layout/chart.png" class="w-18px h-18px mr-8px" />
      <span class="text-16px text-white">综合分析</span>
    </div> -->
    <!-- <RouterLink to="/panoramicMonitor" class="PanoramicMonitorCenter">
      <img
        src="@/assets/image/icon-big-screen.png"
        class="w-30px h-30px mr-20px"
      />
    </RouterLink> -->
    <el-dropdown @command="changeSiteStation" v-if="routeIshow" class="custom-trigger">
      <div class="custom-trigger PanoramicMonitorCenter" style="width: 50px; height: 50px">
        <img src="@/assets/image/icon-big-screen.png" class="w-30px h-30px mr-20px" />
      </div>
      <template #dropdown>
        <!-- 用 command 传值，不要用 to -->
        <el-dropdown-item command="/panoramicMonitor">
          <div style="padding: 20px 20px 10px 20px">综合能源聚合管理平台</div>
        </el-dropdown-item>
        <el-dropdown-item command="/customized">
          <div style="padding:10px 20px 20px 20px">海天光储柔性直流微电系统</div>
        </el-dropdown-item>
      </template>

    </el-dropdown>
    <RouterLink to="/panoramicMonitor" class="PanoramicMonitorCenter" target="_blank" rel="noopener noreferrer"
      v-if="!routeIshow">
      <img src="@/assets/image/icon-big-screen.png" class="w-30px h-30px mr-20px" />
    </RouterLink>
    <!--  <RouterLink to="customized" class="PanoramicMonitorCenter" target="_blank" rel="noopener noreferrer">
      <img src="@/assets/image/icon-progress.png" class="w-30px h-30px mr-20px" />
    </RouterLink> -->
    <!-- 工具icon -->
    <!-- <div class="circle" v-for="icon in iconList" :key="icon.icon">
      <span class="iconfont" :class="icon.icon"></span>
    </div> -->
  </div>
</template>

<script>
import { defineComponent, reactive, toRefs, computed } from "vue";
import { useRouter, useRoute } from "vue-router";
import { useStore } from "vuex";


export default defineComponent({
  name: "NotificationCenter",
  setup () {
    const that = reactive({
      iconList: [
        { icon: "icon-alarm", text: "Alarm" },
        { icon: "icon-question", text: "Question" },
        { icon: "icon-description", text: "Description" },
      ],
      routeIshow: false,
    });
    const isFullscreen = computed(() => store.state.fullscreen.isFullscreen);
    const store = useStore();

    const vueRouter = useRouter();

    const list = JSON.parse(localStorage.getItem('AUTH_ROUTER') || '[]');
    that.routeIshow = list.some(item => item.url === 'customized');



    console.log(that.routeIshow, '获取道德数据')

    const changeSiteStation = (command) => {
      if (command) {
        window.open(command, '_blank')
      }
    }


    return { ...toRefs(that), changeSiteStation };

  },
});
</script>
<style>
.el-dropdown,
.el-dropdown * {
  outline: none;
}

/* 强制移除 el-dropdown 悬停和聚焦时的边框 */
.el-dropdown__inner:hover,
.el-dropdown__inner:focus {
  outline: none !important;
  border: none !important;
  box-shadow: none !important;
}
</style>
<style lang="scss" scoped>
.notificationCenter {
  height: 100%;
  display: flex;
  flex-flow: row nowrap;
  justify-content: flex-end;
  align-items: center;
  transform: translateY(8px);
  z-index: 99;

  .comprehensive-analysis {
    background: linear-gradient(180deg, #006091 0%, #00416d 100%);
  }

  .circle {
    width: 30px;
    height: 30px;
    display: flex;
    justify-content: center;
    align-items: center;
    border-radius: 50%;
    margin-right: 18px;
    box-shadow: inset 0 0 8px 3px #34adffb8;
    // background: linear-gradient(180deg, rgba(52, 173, 255, 0.72) 0%, rgba(52, 173, 255, 0) 50%, rgba(52, 173, 255, 0.72) 100%);
  }

  .iconfont {
    color: #ffffff;
    font-size: 12px;
  }

  .text {
    color: #ffffff;
    font-size: 16px;
    font-family: Inter, serif;
  }
}

.custom-trigger {
  display: flex;
  justify-content: center;
  align-items: center;

}
</style>