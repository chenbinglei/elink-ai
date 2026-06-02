<template>
  <div
    class="status-container"
    style="
      display: flex;
      gap: 10px;
      align-items: center;
      justify-content: center;
    "
  >
    <!-- 无异常 -->
    <div
      v-if="!showError && !showUnregistered && !showOffline"
      class="status-group normal"
      style="display: flex; align-items: center"
    >
      <img
        src="@/assets/images/组 118652.png"
        style="width: 53px; height: 45px"
      />
      <div
        style="
          display: flex;
          flex-direction: column;
          align-items: center;
          gap: 4px;
        "
      >
        <span class="normal-text">无异常</span>
        <img
          src="@/assets/images/11111.png"
          style="width: 124px; height: 14px"
        />
      </div>
    </div>

    <!-- 故障 -->
    <div
      v-if="showError"
      class="status-group normal"
      style="display: flex; align-items: center"
    >
      <img
        src="@/assets/images/组 118653.png"
        style="width: 53px; height: 45px"
      />
      <div
        style="
          display: flex;
          flex-direction: column;
          align-items: center;
          gap: 4px;
        "
      >
        <span class="normal-text">故障</span>
        <img
          src="@/assets/images/11111.png"
          style="width: 124px; height: 14px"
        />
      </div>
      <span class="number" style="color: #ff0000ff">{{
        props.errorCount
      }}</span>
    </div>

    <!-- 离线 -->
    <div
      v-if="showOffline"
      class="status-group normal"
      style="display: flex; align-items: center"
    >
      <img
        src="@/assets/images/组 118654.png"
        style="width: 53px; height: 45px"
      />
      <div
        style="
          display: flex;
          flex-direction: column;
          align-items: center;
          gap: 4px;
        "
      >
        <span class="normal-text">离线</span>
        <img
          src="@/assets/images/11111.png"
          style="width: 124px; height: 14px"
        />
      </div>
      <span class="number" style="color: #aaaaaaff">{{
        props.offlineCount
      }}</span>
    </div>

    <!-- 未注册 -->
    <div
      v-if="showUnregistered"
      class="status-group normal"
      style="display: flex; align-items: center"
    >
      <img
        src="@/assets/images/组 118654.png"
        style="width: 53px; height: 45px"
      />
      <div
        style="
          display: flex;
          flex-direction: column;
          align-items: center;
          gap: 4px;
        "
      >
        <span class="normal-text">未注册</span>
        <img
          src="@/assets/images/11111.png"
          style="width: 124px; height: 14px"
        />
      </div>
      <!-- 推荐使用标准十六进制格式 -->
      <span class="number" style="color: #aaaaaaff">{{
        props.unregisteredCount
      }}</span>
    </div>
  </div>
</template>

<script setup>
import { defineProps, computed } from "vue";

const props = defineProps({
  normalCount: {
    type: [Number, String],
    default: 0,
  },
  errorCount: {
    type: [Number, String],
    default: 0,
  },
  offlineCount: {
    type: [Number, String],
    default: 0,
  },
  unregisteredCount: {
    type: [Number, String],
    default: 0,
  },
});
// 只有非 0 才显示对应状态模块
const showError = computed(() => props.errorCount > 0);
const showOffline = computed(() => props.offlineCount > 0);
const showUnregistered = computed(() => props.unregisteredCount > 0);
</script>

<style scoped>
.status-container {
  display: flex;
}
.number {
  font-family: Agency FB, Agency FB;
  font-weight: 400;
  font-size: 24px;

  text-align: left;
  font-style: normal;
  text-transform: none;
}
.normal-text {
  color: #ffffffff;
  font-size: 14px;
}
</style>
