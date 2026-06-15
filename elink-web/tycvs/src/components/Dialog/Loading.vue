<template>
  <div class="loading">
    <div class="loadingContent">
      <LoadingSvg class="loadingImage"></LoadingSvg>
      <span class="number">{{ number }}%</span>
      <span class="title">{{ title }}</span>
    </div>
  </div>
</template>

<script lang="ts">
import LoadingSvg from "./LoadingSvg.vue";
import {getCurrentInstance, defineComponent, onMounted, onUnmounted, toRefs, ref, reactive, watch} from "vue";

export default defineComponent({
  name: "Loading",
  components: {LoadingSvg},
  props: {
    title: {
      type: String,
      default: ""
    }
  },
  emits: ["update:loading"],
  setup(props) {
    const {emit} = getCurrentInstance();
    const that = reactive({
      number: 0,
      timer: null
    });

    const setNumberFun = () => {
      that.timer = setInterval(() => {
        let randNum = Math.floor(Math.random() * 10) + 1;
        let number = that.number + randNum;
        if (number < 99) that.number = number;
        if (number > 99) {
          that.number = 99;
          clearInterval(that.timer);
        }
      }, 1000);
    };


    // Loading.value.setloadingFun();  父组件中调用此方法进行关闭组件
    const setloadingFun = (data) => {
      that.number = 100;
      setTimeout(() => {
        emit("loadingEvent", data);
      }, 500);
    };

    onMounted(() => {
      setNumberFun();
    });

    return {
      ...toRefs(that), setNumberFun, setloadingFun
    };
  }
});
</script>

<style lang="scss" scoped>
.loading {
  width: 100vw;
  height: 100vh;
  position: fixed;
  left: 0;
  top: 0;
  background: rgba(0, 26, 43, .8);
  z-index: 999999;

  display: flex;
  align-items: center;
  justify-content: center;


  .loadingContent {
    width: 310px;
    height: 310px;
    color: #FFFFFF;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    position: relative;

    .loadingImage {
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
    }

    .number {
      z-index: 100;
      font-size: 48px;
      font-weight: bold;
    }

    .title {
      z-index: 100;
      font-size: 14px;
      margin-top: 8px;
    }
  }
}
</style>
