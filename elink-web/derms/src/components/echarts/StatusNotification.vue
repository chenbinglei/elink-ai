<template>
  <div class="notification-container">
    <!-- 循环渲染每个状态项 -->
    <div
      v-for="item in items"
      :key="item.id || item.time"
      class="status-card"
      :class="{
        emergency: item.eventLevel === 3,
        important: item.eventLevel === 2,
        secondary: item.eventLevel === 1,
      }"
    >
      <div class="status-icon">
        <img
          v-if="item.eventLevel === 3"
          src="@/assets/images/组 118712.png"
          alt="紧急"
          class="status-img"
        />
        <img
          v-else-if="item.eventLevel === 2"
          src="@/assets/images/组 118711.png"
          alt="重要"
          class="status-img"
        />
        <img
          v-else-if="item.eventLevel === 1"
          src="@/assets/images/组 118650.png"
          alt="次要"
          class="status-img"
        />
      </div>
      <div class="status-info">
        <div class="status-title">
          <!-- 根据状态显示不同标题 -->
          <span v-if="item.eventLevel === 3" style="color: #ff0000">紧急</span>
          <span v-else-if="item.eventLevel === 2" style="color: #ffbb00"
            >重要</span
          >
          <span v-else-if="item.eventLevel === 2" style="color: #3baaf5"
            >次要</span
          >
        </div>
        <div class="device-name" :class="statusClass(item.eventLevel)">
          {{ item.deviceName + "" + item.eventName }}
        </div>
      </div>
      <div class="status-time">{{ item.createTime }}</div>
    </div>
  </div>
</template>

<script setup>
import { defineProps, onMounted, ref, watch } from "vue";
import { warningGetEventList } from "@/api/monitoringCenter/monitoringCenter.js";
const props = defineProps({
  systemId: {
    type: String,
    required: true,
  },
});
watch(
  () => props.systemId,
  async (newVal, oldVal) => {
    await getEventList();
  },
  { immediate: true } // 立即执行一次
);
const items = ref([]);
async function getEventList() {
  let res = await warningGetEventList({ systemId: props.systemId });
  items.value = res.data;
}
onMounted(async () => {
  await getEventList();
});
const statusClass = (status) => {
  switch (status) {
    case 3:
      return "emergency-text";
    case 2:
      return "important-text";
    case 1:
      return "secondary-text";
    default:
      return "";
  }
};
</script>

<style scoped>
.notification-container {
  width: 100%;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.status-card {
  margin-bottom: 20px;
  margin-bottom: 10px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 48px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.status-card:hover {
  transform: translateY(-1px);
}

.status-icon {
  flex-shrink: 0;
  margin: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
}

.status-img {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.status-info {
  flex: 1;
  display: flex;
  align-items: center;
  overflow: hidden;
}

.status-title {
  font-family: Microsoft YaHei, Microsoft YaHei;
  font-weight: 400;
  font-size: 16px;
  text-align: left;
  font-style: normal;
  text-transform: none;
  margin-right: 15px;
}

.device-name {
  font-family: Microsoft YaHei, Microsoft YaHei;
  font-weight: 400;
  font-size: 14px;
  color: #ffffff;
  text-align: left;
  font-style: normal;
  text-transform: none;
}

.alert-message {
  font-family: Microsoft YaHei, Microsoft YaHei;
  font-weight: 400;
  font-size: 14px;
  color: #ffffff;
  text-align: left;
  font-style: normal;
  text-transform: none;
}

.status-time {
  margin-right: 10px;
  font-family: Microsoft YaHei, Microsoft YaHei;
  font-weight: 400;
  font-size: 14px;
  color: #ffffff;
  text-align: left;
  font-style: normal;
  text-transform: none;
  -webkit-text-stroke: 1px rgba(0, 0, 0, 0);
  white-space: nowrap;
  overflow: hidden;
}

/* 修改后的背景图片样式 */
.emergency {
  width: 508px;
  background: url("@/assets/images/组 118292.png") no-repeat center/cover;
}

.important {
  width: 508px;
  background: url("@/assets/images/组 118288.png") no-repeat center/cover;
}

.secondary {
  width: 508px;
  background: url("@/assets/images/组 118713.png") no-repeat center/cover;
}

.info {
  width: 508px;
  background: url("@/assets/images/组 118715.png") no-repeat center/cover;
}

.offline {
  width: 508px;
  background: url("@/assets/images/组 118717.png") no-repeat center/cover;
}

.emergency-text {
  color: #ff0000;
}
.important-text {
  color: #ffbb00;
}
.secondary-text {
  color: #3baaf5;
}
.info-text {
  color: #34e800ff;
}
.offline-text {
  color: #aaaaaaff;
}
</style>
