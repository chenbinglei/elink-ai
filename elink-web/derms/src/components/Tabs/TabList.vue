<template>
  <div class="station-top-tab-list">
    <div
      class="station-top-tab"
      v-for="item in list"
      :key="item.type"
      :class="modelValue == item.type ? 'active' : ''"
      @click="handleInput(item.type)"
    >
      {{ item.name }}
    </div>
  </div>
</template>
<script setup>
import { defineProps } from "vue";
// 接收父组件的 v-model 值
const props = defineProps({
  modelValue: { type: String, default: 1 },
  list: {
    type: Array,
    default: () => [],
  }
});
// 定义 emit 事件
const emit = defineEmits(['update:modelValue']);
// 更新值时触发 emit
const handleInput = (type) => {
    console.log(type)
  emit('update:modelValue', type);
};

</script>
<style scoped lang="scss">
.station-top-tab-list {
    display: flex;
    flex-direction: row;
    .station-top-tab {
      align-items: center;
      width: 148px;
      height: 32px;
      background: url("@/assets/image/station-details/tab_normal.png");
      background-size: 100% 100%;
      font-family: Microsoft YaHei, Microsoft YaHei;
      font-weight: 400;
      font-size: 16px;
      line-height: 32px;
      color: #7dcbff;
      text-align: center;
      font-style: normal;
      text-transform: none;
      margin-right: -20px;
      cursor: pointer;
      &.active,&:hover {
        background: url("@/assets/image/station-details/tab_over.png");
        background-size: 100% 100%;
        font-weight: bold;
        font-size: 16px;
        color: #ffffff;
        text-stroke: 1px rgba(0, 0, 0, 0);
        text-align: center;
        font-style: normal;
        text-transform: none;
        -webkit-text-stroke: 1px rgba(0, 0, 0, 0);
      }
    }
  }
</style>