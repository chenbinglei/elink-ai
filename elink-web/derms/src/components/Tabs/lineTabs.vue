<template>
  <div class="lineTabs">
    <div class="lineTabs-title-box">
      <div class="lines"></div>
      <div class="lineTabs-title">{{ title }}</div>
      <div class="time">{{ time }}</div>
    </div>
    <el-button :icon="icon" type="primary" v-if="buttonText" @click="handleClick">{{ buttonText }}</el-button>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { RefreshRight, Search, Download } from "@element-plus/icons-vue";
const props = defineProps({
  title: {
    type: String,
    default: '标题'
  },
  time: {
    type: String,
    default: ''
  },
  buttonText: {
    type: String,
    default: ''
  },
  icon: {
    type: String,
    default: ''
  }
});

const emit = defineEmits(['click']);

const handleClick = () => {
  emit('click');
};
</script>

<style lang="scss" scoped>
.lineTabs {
  height: 40px;
  color: #fff;
  padding: 10px 20px;
  font-size: 16px;
  box-sizing: border-box;
  font-family: "Regular", sans-serif;
  display: flex;
  align-items: center;
  justify-content: space-between;

  .lineTabs-title-box {
    display: flex;
    align-items: center;
    justify-content: space-between;
    height: 100%;
  }

  .lines {
    width: 3px;
    height: 100%;
    background: rgba(0, 144, 255, 1);
    margin-right: 12px;
  }

  .time {
    margin-left: 32px;
    /* 时间靠右 */
    font-size: 14px;
    color: #ccc;
  }

  .custom-button {
    margin-left: 10px;
    padding: 5px 10px;
    background-color: #0090ff;
    color: white;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    font-size: 14px;
  }

  .custom-button:hover {
    background-color: #007acc;
  }
}
</style>