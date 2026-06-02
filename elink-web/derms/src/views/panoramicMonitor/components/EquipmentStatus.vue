<template>
  <div class="equipment-status">
    <div class="equipment-status-title flex">
      <img src="@/assets/image/panoramic-monitor/p-m-header-icon3.png" alt="" width="20">
      <span class="ml-2">设备状态</span>
    </div>
    <div class="equipment-status-top flex_b_c">
      <div class="top-item flex_s_c">
        <div><img src="@/assets/image/panoramic-monitor/icon-sbzt-1.png" alt="" width="48"></div>
        <p class="flex_s_c ml-2">今日告警<span>{{ data.dayAlarmNum }}</span></p>
      </div>
      <div class="top-item-line"></div>
      <div class="top-item flex_s_c">
        <div><img src="@/assets/image/panoramic-monitor/icon-sbzt-2.png" alt="" width="48"></div>
        <p class="flex_s_c ml-2">已修复<span>{{ $filters.moreZero(data.fixAlarmNum) }}</span></p>

      </div>
    </div>
    <div class="equipment-status-progress">
      <div class="progress-bg">
        <div class="progress-bar" :style="{ width: data.fixAlarmRate + '%' }"></div>
      </div>
      <div class="progress-text">{{ $filters.moreZero(data.fixAlarmRate) }}<span>%</span></div>

    </div>
    <div class="equipment-status-emnu">
      <div class="emnu-status flex_e_c">
        <div class="emnu-status-item flex_s_c" :class="item.className" v-for="(item, index) in warningList"
          :key="index + 'warningList'">{{ item.name }}</div>
      </div>
      <div class="equipment-status-list flex jc-space-between">
        <div class="equipment-status-item" v-for="(item, index) in list" :key="index + 'systemType'">
          <div class="status-number flex_c_c">
            <div class="number-item flex_c_c online special-text">{{ $filters.moreZero(item.normalNum) }}</div>
            <div class="number-item flex_c_c warning special-text">{{ $filters.moreZero(item.errorNum) }}</div>
            <div class="number-item flex_c_c offline special-text">{{ $filters.moreZero(item.offlineNum) }}</div>
          </div>
          <div class="status-icon flex jc-center">
            <img :src="item.img" alt="" width="79">
          </div>
          <div class="status-text">{{ item.label }}</div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import { defineComponent, reactive, toRefs, computed } from "vue";
export default defineComponent({
  name: "equipmentStatus",
  props: {
    // 静态
    data: {
      type: Array,
      required: true,
      default: () => { }
    },
    // 实时数据
    deviceStatus: {
      type: Array,
      required: true,
      default: () => []
    }
  },
  setup (props) {
    const getImageUrl = (name) => {
      return new URL(`/src/assets/image/panoramic-monitor/${name}.png`, import.meta.url).href;
    }
    const state = reactive({
      systemType: [
        { label: "光伏", type: 1, img: getImageUrl('icon-sbzt-6') },
        { label: "储能", type: 2, img: getImageUrl('icon-sbzt-5') },
        { label: "充电", type: 3, img: getImageUrl('icon-sbzt-4') },
        { label: "换电", type: 6, img: getImageUrl('icon-sbzt-7') }],
      warningList: [{ name: '在线', color: '#00FFB1', className: 'online' }, { name: '告警', color: '#FF7D1E', className: 'warning' }, { name: '离线', color: '#FD393A', className: 'offline' }]
    });

    // 合并动态数据
    const list = computed(() => {
      return state.systemType.map(stateItem => ({
        ...stateItem,
        ...props.deviceStatus.find(item => item.type === stateItem.type)
      }))
    })

    return { ...toRefs(state), list };
  },
});
</script>
<style scoped lang="scss">
.flex_s_c {
  display: flex;
  align-items: center;
  justify-content: flex-start;
}

.flex_b_c {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

@import "./style.scss";
// .equipment-status {
//   width: 100%;
//   height: 100%;
//   background: url("@/assets/image/panoramic-monitor/p-m-header-bg.png") no-repeat;
//   background-size: 100% 100%;
//   .equipment-status-title {
//     font-size: 18px;
//     color: #fff;
//     font-family: zihun35hao-jindianyahei, zihun35hao-jindianyahei;
//   }
//   .equipment-status-top {
//     padding: 20px 20px 10px;
//     .top-item {
//       color: #fff;
//       font-size: 13px;
//       span{
//         font-family: Agency FB, Agency FB;
//         font-weight: bold;
//         font-size: 24px;
//         color: #F56C6C;
//         margin-left: 10px;
//       }
//     }
//     .top-item-line {
//       width: 2px;
//       height: 54px;
//       background: url("@/assets/image/panoramic-monitor/vertica-ldivider-line-2.png") no-repeat;
//       background-size: 100% 100%;
//     }
//   }
//   .equipment-status-progress {
//     box-sizing: border-box;
//     padding: 10px 0 36px;
//     width: 100%;
//     height: 30px;
//     display: flex;
//     align-items: center;
//     justify-content: center;
//     .progress-bg {
//       width: 294px;
//       height: 11px;
//       background: linear-gradient(
//         180deg,
//         #002f5a 0%,
//         rgba(0, 64, 123, 0.5) 44%,
//         #00274b 83%,
//         #002f5a 100%
//       );
//       border-radius: 9px 9px 9px 9px;
//       border: 1px solid #00539c;
//       opacity: 0.92;
//       .progress-bar {
//         width: 290px;
//         margin: 2px;
//         height: 7px;
//         background: #0099ff;
//         border-radius: 8px 8px 8px 8px;
//       }
//     }
//     .progress-text {
//       margin-left: 10px;
//       height: 27px;
//       font-family: Agency FB, Agency FB;
//       font-weight: 400;
//       font-size: 24px;
//       color: #00ccff;
//       text-align: center;
//       font-style: normal;
//       span {
//         font-size: 12px;
//       }
//     }
//   }
//   .equipment-status-emnu {
//     box-sizing: border-box;
//     padding: 10px;
//     width: 100%;
//     .emnu-status {
//       .emnu-status-item {
//         margin-left: 24px;
//         font-size: 14px;
//         color: #ffffff;
//         &::before {
//           content: "";
//           width: 10px;
//           height: 10px;
//           margin-right: 8px;
//           border-radius: 100%;
//         }
//         &.online {
//           &::before {
//             background: #01ffcb;
//             box-shadow: 0px 0px 6px 1px #01ffcb;
//           }
//         }
//         &.warning {
//           &::before {
//             background: #f56c6c;
//             box-shadow: 0px 0px 6px 1px #f56c6c;
//           }
//         }
//         &.offline {
//           &::before {
//             background: #cdcbce;
//             box-shadow: 0px 0px 6px 1px #cdcbce;
//           }
//         }
//       }
//     }
//   }
//   .equipment-status-list {
//     margin-top: 20px;
//     width: 100%;
//     box-sizing: border-box;
//     flex: 1;
//     .status-number {
//       .number-item {
//         font-size: 18px;
//         width: 28px;
//         height: 39px;
//         position: relative;
//         &::before {
//           position: absolute;
//           top: 0;
//           left: 50%;
//           transform: translateX(-50%);
//           width: 8px;
//           height: 0px;
//           content: "";
//         }
//         &.online {
//           color: #00ffb1;
//           background: linear-gradient(
//             180deg,
//             rgba(1, 255, 203, 0.1) 0%,
//             rgba(1, 255, 203, 0) 100%
//           );
//           border-radius: 0px 0px 0px 0px;
//           &::before {
//             border: 1px solid #00ffb1;
//           }
//         }
//         &.warning {
//           color: #f56c6c;
//           background: linear-gradient(
//             180deg,
//             rgba(245, 108, 108, 0.1) 0%,
//             rgba(254, 94, 0, 0) 100%
//           );
//           border-radius: 0px 0px 0px 0px;
//           &::before {
//             border: 1px solid #f56c6c;
//           }
//         }
//         &.offline {
//           margin-right: 9px;
//           color: #cdcbce;
//           background: linear-gradient(
//             180deg,
//             rgba(205, 203, 206, 0.1) 0%,
//             rgba(205, 203, 206, 0) 100%
//           );
//           border-radius: 0px 0px 0px 0px;
//           &::before {
//             border: 1px solid #cdcbce;
//           }
//         }
//       }
//     }
//     .status-text {
//       margin-top: 8px;
//       color: #fff;
//       font-size: 14px;
//       text-align: center;
//     }
//   }
// }</style>