<template>
  <div :class="['batteryCartoon', 'battery-null', 'battery-' + gunWorkState, direction + '_class']">
    <div class="battery_body">
      <template v-if="gunWorkState === 1 || gunWorkState === 2">
        <div class="battery_top"></div>
        <div class="battery_bottom">
          <div class="battery_bg">
            <div v-for="(item, index) in battery_split" :key="item" class="battery_list">
              <div v-for="(battery_item, battery_index) in battery_split1" :key="battery_item"
                :class="['battery_list_li', index < itemNum || (index === itemNum && battery_index < itamNum) ? 'battery_li_bg' : '']">
              </div>
            </div>
          </div>
        </div>
      </template>
      <div v-else :class="'battery_stop_' + gunWorkState" class="battery_stop_class">
        <div class="battery_top"></div>
        <div class="battery_bottom flex-jc-ai-center">
          <span v-if="gunWorkState === 3" class="iconfont icon-kongxian"></span>
          <span v-else-if="gunWorkState === 4" class="iconfont icon-zhanyong"></span>
          <span v-else-if="gunWorkState === 5" class="iconfont icon-guzhang"></span>
          <span v-else-if="gunWorkState === 6" class="iconfont icon-lixian"></span>
          <span v-else-if="gunWorkState === 8" class="iconfont icon-yuyue"></span>
          <span v-else class="iconfont icon-qita"></span>
        </div>
      </div>
    </div>
    <div v-if="direction === 'vertical'" class="bottom_title">
      <span class="text">{{ $filters.gunWorkState(gunWorkState) }}</span>
      <span v-if="gunWorkState && gunWorkState <= 2" class="batteryNum">{{ $filters.numberNull(batteryNum) }}%</span>
    </div>
  </div>
</template>

<script>
import { onMounted, reactive, toRefs, watch, onUnmounted, defineComponent } from "vue";

export default defineComponent({
  name: "BatteryCartoon",
  props: {
    batteryNum: {
      type: [Number, String],
      default: 0
    },
    gunWorkState: {
      type: [Number, String],
      default: ""
    },
    //  horizontal
    direction: {
      type: String,
      default: "vertical"
    }
  },
  setup(props) {
    const that = reactive({
      itemNum: 0,
      itamNum: 0,

      battery_split: 10,
      battery_split1: 10,
      requestAnimationFrame: null, // 做动画
      gunWorkState: props.gunWorkState, //枪工作状态
      battery_num: props.batteryNum, // 电池soc百分比
    });

    // 监听枪状态
    const watchGunWorkState = watch([() => props.gunWorkState, () => props.batteryNum], ([newGunWorkState, newBatteryNum]) => {
      that.gunWorkState = newGunWorkState;
      that.battery_num = newBatteryNum;
      if (that.gunWorkState === 1 || that.gunWorkState === 2) {
        initBatteryFun();
      } else {
        cancelAnimationFrame(that.requestAnimationFrame); // 清除动画
      }
    });

    const animationFrameFun = () => {
      // 充电动画
      if (that.gunWorkState === 1) {
        that.battery_num += 1;
        if (that.battery_num >= 100) that.battery_num = props.batteryNum;
      }

      // 放电动画
      if (that.gunWorkState === 2) {
        that.battery_num -= 1;
        if (that.battery_num <= 0) that.battery_num = props.batteryNum;
      }

      let battery_num = that.battery_num ? that.battery_num : 0;
      that.itemNum = Math.trunc(battery_num / that.battery_split1);
      that.itamNum = battery_num % that.battery_split1;

      if (that.gunWorkState === 1 || that.gunWorkState === 2) {
        setTimeout(() => initBatteryFun(), 100);
      }
    };

    // 初始化充电动画
    const initBatteryFun = () => {
      that.requestAnimationFrame = window.requestAnimationFrame(animationFrameFun);
    };

    onMounted(() => {
      initBatteryFun();
    });

    onUnmounted(() => {
      cancelAnimationFrame(that.requestAnimationFrame); // 清除动画
    });

    return { ...toRefs(that), initBatteryFun, watchGunWorkState, animationFrameFun };
  }
});
</script>

<style lang="scss" scoped>
.batteryCartoon {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;

  .battery_body {
    flex: 1;
    width: 54px;
    display: flex;
    align-items: center;
    flex-direction: column;

    .battery_top {
      width: 50%;
      height: 11px;
      z-index: 1;
      background: #0C325E;
      border-radius: 4px 4px 0 0;
      //border: 1px solid #079CEB;
      border-bottom: none !important;
      box-sizing: border-box;
      margin-bottom: -1px;
    }

    .battery_bottom {
      flex: 1;
      width: 100%;
      padding: 4px;
      border-radius: 4px;
      background: #0C325E;
      //border: 1px solid #079CEB;
      box-sizing: border-box;
      position: relative;

      .battery_bg {
        height: 100%;
        padding: 4px;
        border-radius: 4px;
        background: #0C2747;
        box-sizing: border-box;
        display: flex;
        flex-direction: column;
        transform: rotate(180deg);

        .battery_list {
          flex: 1;
          border-radius: 2px;
          background: #0C325E;
          margin-bottom: 1px;
          display: flex;
          flex-direction: column;

          .battery_list_li {
            flex: 1;
            transition: all .28s;
          }
        }

        .battery_list:last-child {
          margin-bottom: 0;
        }
      }
    }
  }

  .battery_stop_class {
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-direction: column;

    .battery_top,
    .battery_bottom {
      color: #ffffff66;
      background: #869ba199;
      border: 1px solid #ffffff66;
    }

    .iconfont {
      font-size: 24px;
    }
  }

  .battery_stop_3 {

    .battery_top,
    .battery_bottom {
      color: #00D1FFFF;
      background: #00D1FF1A;
      border: 1px solid #00D1FFFF !important;
    }
  }

  .battery_stop_7 {

    .battery_top,
    .battery_bottom {
      background: #00D1FF0B;
      color: rgba(255, 255, 255, .25);
      border: 1px solid rgba(255, 255, 255, .25);
    }
  }

  .battery_stop_8 {

    .battery_top,
    .battery_bottom {
      color: #ad00ff;
      background: #ad00ff1a;
      border: 1px solid #ad00ff;
    }
  }

  .battery_stop_4 {

    .battery_top,
    .battery_bottom {
      color: #ffb800;
      background: #ffb8001a;
      border: 1px solid #ffb800;
    }
  }

  .battery_stop_5 {

    .battery_top,
    .battery_bottom {
      color: #ff1818;
      background: #ff18181a;
      border: 1px solid #ff1818;
    }
  }

  .bottom_title {
    font-size: 12px;
    margin-top: 12px;
    white-space: nowrap;
    color: rgba(255, 255, 255, 0.8);
  }
}

.horizontal_class {
  flex-direction: initial;

  .battery_body {
    width: 100%;
    height: 100%;
    position: relative;
    justify-content: center;

    .battery_top {
      width: 8px;
      height: 50%;
      border-bottom: 1px solid !important;
      border-left: none !important;
      border-radius: 0 4px 4px 0;
      position: absolute;
      right: -8px;
    }

    .battery_bottom {

      .battery_bg {
        flex-direction: initial;
        transform: rotate(360deg);

        .battery_list {
          margin-bottom: 0;
          margin-right: 1px;
          flex-direction: initial;

          &:last-child {
            margin-right: 0;
          }
        }
      }
    }
  }
}

.battery-1 {
  .battery_body {

    .battery_top,
    .battery_bottom {
      border: 1px solid #00f0ff;
      box-shadow: 0 0 5px 1px #35fbfa47 inset;
    }

    .battery_li_bg {
      background-color: #00f0ff;
    }
  }

  .bottom_title {
    color: #00f0ff;
  }
}

.battery-2 {
  .battery_body {

    .battery_top,
    .battery_bottom {
      border: 1px solid rgba(255, 248, 134, 1);
      box-shadow: 0 0 5px 1px rgba(255, 248, 134, .5) inset;
    }

    .battery_li_bg {
      background-color: rgba(255, 248, 134, 1);
    }
  }

  .bottom_title {
    color: rgba(255, 248, 134, 1);
  }
}

.battery-3 {
  .battery_body {

    .battery_top,
    .battery_bottom {
      border: 1px solid rgba(19, 82, 120, 1);
      box-shadow: 0 0 5px 1px rgba(19, 82, 120, .5) inset;
    }
  }

  .bottom_title {
    color: rgba(19, 82, 120, 1);
  }
}

.battery-4 {
  .battery_body {

    .battery_top,
    .battery_bottom {
      border: 1px solid rgba(255, 125, 30, 1);
      box-shadow: 0 0 5px 1px rgba(255, 125, 30, .5) inset;
    }

    .battery_li_bg {
      background-color: rgba(255, 125, 30, 1);
    }
  }

  .bottom_title {
    color: rgba(255, 125, 30, 1);
  }
}

.battery-5 {
  .battery_body {

    .battery_top,
    .battery_bottom {
      border: 1px solid rgba(253, 57, 58, 1);
      box-shadow: 0 0 5px 1px rgba(253, 57, 58, .5) inset;
    }

    .battery_li_bg {
      background-color: rgba(253, 57, 58, 1);
    }
  }

  .bottom_title {
    color: rgba(253, 57, 58, 1);
  }
}

.battery-null {
  color: #647A94;
  font-weight: bold;

  .battery_top,
  .battery_bottom {
    border: 1px solid rgba(109, 125, 145, 1);
    box-shadow: 0 0 5px 1px rgba(109, 125, 145, .5) inset;
  }

  .battery_li_bg {
    background-color: rgba(109, 125, 145, 1);
  }
}
</style>
