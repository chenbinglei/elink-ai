<template>
  <div class="PanoramicMonitorBottom flex_b_c">
    <!-- <div class="title-text flex_c_c">
      <p>{{ data?.siteName }}<span v-if="data?.siteType == 0">一体化</span></p>
    </div>
    <div class="carousel-list">
      <el-carousel height="50px" indicator-position="none">
        <el-carousel-item v-for="(group, index) in optionList" :key="index + 'list'">
          <div class="carousel-list-item flex_c_c" style="justify-content: space-around;">
            <template v-for="(item, ind) in group" :key="item.prop">
              <div class="item flex_s_c">
                <div><img :src="item.img" alt="" width="49"></div>
                <div class="item-right ml-2">
                  <div class="item-text">{{ item.title }}</div>
                  <div class="item-value">
                    <span class="special-text">{{ data ? data[item.prop] : 0 }}</span>{{ item.unit }}
                  </div>
                </div>
              </div>
              <div class="carousel-line" v-if="ind < 3"></div>
            </template>
          </div>
        </el-carousel-item>
      </el-carousel>
    </div> -->
    <el-carousel :interval="5000" arrow="always">
      <el-carousel-item v-for="item in optionList" :key="item">
        <h3>{{ item }}</h3>
      </el-carousel-item>
    </el-carousel>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue';

const props = defineProps({
  data: {
    type: Array,
    default: () => []
  }
  ,
  mainMapName: {
    type: Array,
    default: () => []
  }
});

// 图片路径生成函数
function getImageUrl (name) {
  return new URL(`/src/assets/image/panoramic-monitor/${name}.png`, import.meta.url).href;
}

const optionList = ref([]);

// watch(() => props.data, (newValue) => {
//   if (newValue) {
//     const list = [
//       { title: '光伏装机容量', prop: 'pvCapacity', unit: 'kWp', img: getImageUrl('icon-bottom-1'), value: 1 },
//       { title: '光伏日发电量', prop: 'pvDayQt', unit: 'kWh', img: getImageUrl('icon-bottom-2'), value: 1 },
//       { title: '储能装机容量', prop: 'storageCapacity', unit: 'kWh', img: getImageUrl('icon-bottom-3'), value: 2 },
//       { title: '储能充/放电量', prop: 'storageChargeQt', unit: 'kWh', img: getImageUrl('icon-bottom-4'), value: 2 },
//       { title: '电桩装机容量', prop: 'pileCapacity', unit: 'kW', img: getImageUrl('icon-bottom-5'), value: 3 },
//       { title: '电桩充/放电量', prop: 'pileChargeQt', unit: 'kWh', img: getImageUrl('icon-bottom-6'), value: 3 },
//       { title: '换电站装机容量', prop: 'changeCapacity', unit: 'kW', img: getImageUrl('icon-bottom-7'), value: 6 },
//       { title: '换电站充/耗电量', prop: 'changeChargeQt', unit: 'kWh', img: getImageUrl('icon-bottom-8'), value: 6 }
//     ];

//     const arr = list.filter(item => props.data?.scenarioTypes?.includes(item.value));
//     console.log(arr, '-----------------')
//     const groups = [];
//     for (let i = 0; i < arr.length; i += 4) {
//       groups.push(arr.slice(i, i + 4));
//     }
//     optionList.value = groups;
//     console.log(optionList.value, 'optionList.value')
//   }
// });
watch(() => props.mainMapName, (newValue) => {
  const groupedData = [];

  // 定义 list 数据
  const list = [
    { title: '光伏装机容量', prop: 'pvCapacity', unit: 'kWp', img: getImageUrl('icon-bottom-1'), value: 1 },
    { title: '光伏日发电量', prop: 'pvDayQt', unit: 'kWh', img: getImageUrl('icon-bottom-2'), value: 1 },
    { title: '储能装机容量', prop: 'storageCapacity', unit: 'kWh', img: getImageUrl('icon-bottom-3'), value: 2 },
    { title: '储能充/放电量', prop: 'storageChargeQt', unit: 'kWh', img: getImageUrl('icon-bottom-4'), value: 2 },
    { title: '电桩装机容量', prop: 'pileCapacity', unit: 'kW', img: getImageUrl('icon-bottom-5'), value: 3 },
    { title: '电桩充/放电量', prop: 'pileChargeQt', unit: 'kWh', img: getImageUrl('icon-bottom-6'), value: 3 },
    { title: '换电站装机容量', prop: 'changeCapacity', unit: 'kW', img: getImageUrl('icon-bottom-7'), value: 6 },
    { title: '换电站充/耗电量', prop: 'changeChargeQt', unit: 'kWh', img: getImageUrl('icon-bottom-8'), value: 6 }
  ];

  if (newValue && newValue.points) {
    newValue.points.forEach(site => {
      const scenarioTypes = site.scenarioTypes;

      // 将 scenarioTypes 转为数组
      const typesArray = typeof scenarioTypes === 'string'
        ? scenarioTypes.split(',').map(Number)
        : scenarioTypes;

      const items = [];

      list.forEach(item => {
        if (typesArray.includes(item.value)) {
          items.push({
            title: item.title,
            unit: item.unit,
            prop: item.prop,
            img: item.img
          });
        }
      });

      if (items.length > 0) {
        groupedData.push({
          ...site,
          items: items,
        });
      }
    });
  }
  optionList.value = groupedData

  console.log(groupedData, 'groupedDatagroupedDatagroupedData');
});
</script>
<style scoped lang="scss">
@import "./style.scss";
.el-carousel__item h3 {
    color: #475669;
    font-size: 18px;
    opacity: 0.75;
    line-height: 300px;
    margin: 0;
  }
  
  .el-carousel__item:nth-child(2n) {
    background-color: #99a9bf;
  }
  
  .el-carousel__item:nth-child(2n+1) {
    background-color: #d3dce6;
  }
</style>