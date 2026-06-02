<template>
  <div class="PanoramicMonitorBottom flex_b_c">
    <div class="title-text flex_c_c">
      <p>{{data?.siteName}}<span v-if="data?.siteType == 0">一体化</span></p>
    </div>
    <div class="carousel-list">
      <el-carousel height="50px" indicator-position="none" :interval="2000" :autoplay="true" :loop="true" v-if="optionList.length > 0">
        <el-carousel-item v-for="(group,index) in optionList" :key="index + 'list'">
          <div class="carousel-list-item flex_c_c" style="justify-content: space-around;">
            <template v-for="(item,ind) in group" :key="item.prop">
              <div class="item flex_s_c">
                <div><img :src="item.img" alt="" width="49"></div>
                <div class="item-right ml-2">
                  <div class="item-text">{{item.title}}</div>
                  <div class="item-value">
                    <span class="special-text">{{data ? data[item.prop] : 0}}</span>{{item.unit}}
                  </div>
                </div>
              </div>
              <div class="carousel-line" v-if="ind < 3"></div>
            </template>
          </div>
        </el-carousel-item>
      </el-carousel>
       <div v-else class="empty-state" style="height: 100%; display: flex; align-items: center; justify-content: center; color: #5999C4;">
        暂无数据
      </div>
    </div>
  </div>
</template>
<script>
import { defineComponent, ref, watch, nextTick } from "vue";
export default defineComponent({
  name: "panoramicMonitorBottom",
  props: {
    data: {
      type: Array,
      default: () => { }
    }
  },
  setup (props) {
    function getImageUrl (name) {
      return new URL(`/src/assets/image/panoramic-monitor/${name}.png`, import.meta.url).href
    }

    const optionList = ref([])

    // 初始化数据
    const initData = () => {
      if (!props.data || !props.data.scenarioTypes) return;

      let list = [{
        title: '光伏装机容量', prop: 'pvCapacity', unit: 'kWp', img: getImageUrl('icon-bottom-1'), value: 1
      }, {
        title: '光伏日发电量', prop: 'pvDayQt', unit: 'kWh', img: getImageUrl('icon-bottom-2'), value: 1
      }, {
        title: '储能装机容量', prop: 'storageCapacity', unit: 'kWh', img: getImageUrl('icon-bottom-3'), value: 2
      }, {
        title: '储能充/放电量', prop: 'storageChargeQt', unit: 'kWh', img: getImageUrl('icon-bottom-4'), value: 2
      }, {
        title: '电桩装机容量', prop: 'pileCapacity', unit: 'kW', img: getImageUrl('icon-bottom-5'), value: 3
      }, {
        title: '电桩充/放电量', prop: 'pileChargeQt', unit: 'kWh', img: getImageUrl('icon-bottom-6'), value: 3
      }, {
        title: '换电站装机容量', prop: 'changeCapacity', unit: 'kW', img: getImageUrl('icon-bottom-7'), value: 6
      }, {
        title: '换电站充/耗电量', prop: 'changeChargeQt', unit: 'kWh', img: getImageUrl('icon-bottom-8'), value: 6
      }]

      // 根据 scenarioTypes 过滤
      let arr = list.filter(item => props.data.scenarioTypes.includes(item.value))

      // 每4个一组
      const groups = []
      for (let i = 0; i < arr.length; i += 4) {
        groups.push(arr.slice(i, i + 4));
      }

      optionList.value = groups

      // 强制重新渲染轮播图
      nextTick(() => {
        // 触发轮播图重新计算
        window.dispatchEvent(new Event('resize'));
      });
    }

    // 监听数据变化
    watch(() => props.data, (newVal) => {
      if (newVal && Object.keys(newVal).length > 0) {
        initData();
      }
    }, { immediate: true, deep: true });

    return { optionList };
  },
});
</script>
<style scoped lang="scss">
@import "./style.scss";
</style>