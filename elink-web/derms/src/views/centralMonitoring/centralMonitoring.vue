<template>
  <div
    class="w-full h-full flex flex-col justify-start items-stretch relative box-border page-container"
  >
    <div class="app-container">
      <div
        class="w-full h-full overflow-hidden position-relative"
        :class="isFullscreen ? 'full_screen_class' : ''"
      >
        <!-- 加载遮罩：只在 iframe 加载中显示 -->
        <div v-if="isLoading" class="loading-overlay">
          <!-- <el-icon class="is-loading"><Loading /></el-icon>
          <span>加载中...</span> -->
          <div v-loading="isShowLoading"></div>

        </div>

        <iframe
          v-if="frameSrc"
          :src="frameSrc"
          class="w-full h-full border-none m-0 p-0"
          @load="handleIframeLoad"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, onUnmounted, computed, ref, watch } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useMonitorStore, useAppStore } from '@/stores/index';

import { removeToken } from "@/utils/auth";
import { ElMessage } from "element-plus";
import { Loading } from "@element-plus/icons-vue";

const route = useRoute();
const router = useRouter();
const monitorStore = useMonitorStore();
    const appStore = useAppStore();
const isShowLoading=ref(false)
const isFullscreen = computed(() => monitorStore.isFullscreen);
const frameSrc = computed(() => {
  const { siteId } = route.query;
  return siteId ? `/monitor/${siteId}` : "";
});

const isLoading = ref(false); // 初始不加载，等 src 有效且 iframe 开始加载时再显示

// 监听 frameSrc 变化，开始加载时显示遮罩
watch(frameSrc, (newSrc) => {
  if (newSrc) {
    isLoading.value = true;
    isShowLoading.value = true;

    // 超时兜底：5秒后强制隐藏，避免因网络或错误导致一直显示
    setTimeout(() => {
      if (isLoading.value) {
        isLoading.value = false;
        isShowLoading.value = false;

      }
    }, 5000);
  } else {
    isLoading.value = false;
  }
}, { immediate: true });

const handleIframeLoad = () => {
  // iframe 加载完成（即使内容空白也会触发），隐藏遮罩
  isLoading.value = false;
};

const switchFullscreen = () => {
  monitorStore.updateIsFullscreen(false);
};

const onMessage = ({ data }) => {
  const { action, payload } = data;
  switch (action) {
    case "unAuth":
      removeToken();
      ElMessage({
        message: "登录已失效，请重新登录！",
        showClose: true,
        type: "error",
      });
      router.replace(`/login?id=${route.query.siteId}`);
      break;
    case "fullScreenChange":
      monitorStore.updateIsFullscreen(payload);
      break;
    case "exitSystem":
      appStore.exitSystem({ noReload: true });
      router.replace(`/login?id=${route.query.siteId}`);
      break;
    default:
      return;
  }
};

onMounted(() => {
  window.addEventListener("message", onMessage);
});

onUnmounted(() => {
  window.removeEventListener("message", onMessage);
});
</script>

<style lang="scss" scoped>
/* 原有样式保持不变，仅添加 loading 遮罩样式 */
.page-container {
  padding: 7px 54px 16px 54px;
}

.app-container {
  position: relative;
  padding: 9px 75px 0 75px;
  background-image: url("/img/monitor/monitorBg.webp");
  background-size: 100% 100%;
  background-repeat: no-repeat;
}

.full_screen_class {
  width: 100vw;
  height: 100vh;
  z-index: 1001;
  transition: all 0.28s;
  background-size: 100% 100%;
  background-repeat: no-repeat;
  background-image: url("@/assets/image/bg_image.webp");
  background-color: #000000;
  position: fixed;
  left: 0;
  top: 0;
}

/* 新增加载遮罩样式 */
.loading-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  color: #fff;
  font-size: 16px;
  backdrop-filter: blur(2px);
}

.loading-overlay .is-loading {
  font-size: 48px;
  margin-bottom: 12px;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}
</style>