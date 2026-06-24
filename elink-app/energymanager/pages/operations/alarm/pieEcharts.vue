<template>
  <view style="width: 100%; height: 100%;">
    <qiun-data-charts type="ring" :opts="pieOpts" :chartData="chartData" />
  </view>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue';

const props = defineProps({
  statusTypeList: {
    type: Array,
    default: () => []
  },
  totalSize: {
    type: Number,
    default: 0
  }
});

const chartData = ref({});
const pieOpts = ref({
  rotate: false,
  rotateLock: false,
  padding: [0, 0, 0, 0],
  dataLabel: false,
  enableScroll: false,
  legend: {
    show: false
  },
  title: {
    name: "总数",
    fontSize: 16,
    color: "#1E7CE8"
  },
  subtitle: {
    name: props.totalSize.toString(),
    fontSize: 16,
    color: "#1E7CE8"
  },
  extra: {
    ring: {
      ringWidth:4,
      activeOpacity: 0.5,
      activeRadius: 10,
      offsetAngle: 0,
      labelWidth: 0,
      border: false,
      borderWidth: 3,
      borderColor: "#FFFFFF",
      radius: ['10%', '90%'] // 调整这里来改变饼图大小
    }
  }
});
// 监听 totalSize 变化，更新副标题
watch(() => props.totalSize, (newVal) => {
  
   pieOpts.value.subtitle.name =newVal.toString()>=10000?(newVal / 10000).toFixed(2) + '万' : newVal.toString();
});

watch(() => props.statusTypeList, (newVal) => {
  if (Array.isArray(newVal)) {
    const res = {
      series: [
        {
          data: newVal.map(item => ({
            value: Number(item.value),
            name: item.label,
             color: item.color,
          }))
        }
      ]
    };
     chartData.value = JSON.parse(JSON.stringify(res));
  }
});
</script>