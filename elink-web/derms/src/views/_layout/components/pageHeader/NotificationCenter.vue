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

<script lang="ts">
import { defineComponent, reactive, toRefs } from "vue";
import { useRouter } from "vue-router";

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

    const vueRouter = useRouter();

    const list = JSON.parse(localStorage.getItem('AUTH_ROUTER') || '[]');
    that.routeIshow = list.some(item => item.url === 'customized');

    const changeSiteStation = (command) => {
      if (command) {
        window.open(command, '_blank')
      }
    }

    return { ...toRefs(that), changeSiteStation };
  },
});
</script>

<style lang="scss" scoped>
.notificationCenter {
  height: 100%;
  display: flex;
  flex-flow: row nowrap;
  align-items: center;
}

.PanoramicMonitorCenter {
  position: relative;
  height: 100%;
  display: flex;
  flex-flow: row nowrap;
  align-items: center;

  img {
    position: absolute;
    top: 50%;
    transform: translateY(-50%);
    left: 10px;
    cursor: pointer;
  }
}

.comprehensive-analysis {
  background-color: rgba(63, 140, 255, 0.4);
}

.circle {
  width: 30px;
  height: 30px;
  background-color: rgba(63, 140, 255, 0.4);
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  margin-right: 10px;
  cursor: pointer;
}
</style>
