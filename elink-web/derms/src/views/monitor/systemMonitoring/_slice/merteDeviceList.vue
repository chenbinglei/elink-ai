<template>
  <monitorBlock title="运行数据" icon="runningData">
    <template #content>
      <div class="flex-1 flex flex-col justify-start items-stretch pt-45px">
        <div class="h-60px flex justify-around items-start">
          <div
            class="w-138px relative flex flex-col justify-end items-center pb-10px box-border"
          >
            <img
              :src="`/img/monitor/common/deviceQuotaItemBg.png`"
              class="w-full h-full absolute top-0 left-0 z--2"
            />
            <div
              class="text-16px text-white flex justify-start items-end whitespace-nowrap mt-7px"
            >
              本月平均功率因数
            </div>
            <div class="balance text-18px mt-7px">
              {{ info.averagePowerFactor ?? "--"
              }}<span
                class="text-12px text-white pl-8px"
                v-if="info.averagePowerFactor"
                >%</span
              >
            </div>
          </div>
          <div
            class="w-138px relative flex flex-col justify-end items-center pb-10px box-border"
          >
            <img
              :src="`/img/monitor/common/deviceQuotaItemBg.png`"
              class="w-full h-full absolute top-0 left-0 z--2"
            />
            <div
              class="text-16px text-white flex justify-start items-end whitespace-nowrap mt-7px"
            >
              三相电压不平衡度
            </div>
            <div class="balance text-18px mt-7px">
              {{ info.threePhaseVoltImbalance ?? "--"
              }}<span
                class="text-12px text-white pl-8px"
                v-if="info.threePhaseVoltImbalance"
                >%</span
              >
            </div>
          </div>
          <div
            class="w-138px relative flex flex-col justify-end items-center pb-10px box-border"
          >
            <img
              :src="`/img/monitor/common/deviceQuotaItemBg.png`"
              class="w-full h-full absolute top-0 left-0 z--2"
            />
            <div
              class="text-16px text-white flex justify-start items-end whitespace-nowrap mt-7px"
            >
              三相电流不平衡度
            </div>
            <div class="balance text-18px mt-7px">
              {{ info.threePhaseCurImbalance ?? "--"
              }}<span
                class="text-12px text-white pl-8px"
                v-if="info.threePhaseCurImbalance"
                >%</span
              >
            </div>
          </div>
          <div
            class="w-138px relative flex flex-col justify-end items-center pb-10px box-border"
          >
            <img
              :src="`/img/monitor/common/deviceQuotaItemBg.png`"
              class="w-full h-full absolute top-0 left-0 z--2"
            />
            <div
              class="text-16px text-white flex justify-start items-end whitespace-nowrap mt-7px"
            >
              功率平衡度
            </div>
            <div class="balance text-18px mt-7px">
              {{ info.powerBalance ?? "--"
              }}<span
                class="text-12px text-white pl-8px"
                v-if="info.powerBalance"
                >%</span
              >
            </div>
          </div>
        </div>
      </div>
    </template>
  </monitorBlock>
</template>
<script setup lang="js">
import monitorBlock from '@/views/monitor/_slice/base/monitorBlock.vue';

const props = defineProps({
    info: {
        type: Object,
        default: () => ({})
    }
});



</script>

<style lang="scss" scoped>
.device-name {
  //   background-image: url("/img/monitor/deviceNameBg.png");
  background-repeat: no-repeat;
  background-size: 100% 18px;
  background-position: left bottom;
}
/* styles.css */
.status-item {
  --color: #fff;
  background-color: var(--color);
  box-shadow: 0px 0px 4px 1px var(--color);
}

.txStatus-0 {
  --color: #d3d3d3;
  /* 未注册 */
}

.txStatus-1 {
  --color: #4caf50;
  /* 在线 */
}

.txStatus-2 {
  --color: #ff9800;
  /* 维护 */
}

.txStatus-3 {
  --color: #f44336;
  /* 故障 */
}

.txStatus-88 {
  --color: #9e9e9e;
  /* 离线 */
}

.txStatus-other {
  --color: #607d8b;
  /* 其他 */
}

.runState-0 {
  --color: #2196f3;
  /* 待机 */
}

.runState-1,
.runState-2,
.runState-3 {
  --color: #4caf50;
  /* 运行 */
}

.runState-other {
  --color: #f44336;
  /* 故障 */
}

//电桩工作状态 1-在线 2-维护 3-故障 88-离线
.workStatus-0 {
  --color: #2196f3;
  /* 待机 */
}

.workStatus-1,
.workStatus-2,
.workStatus-3 {
  --color: #4caf50;
  /* 运行 */
}

.workStatus-other {
  --color: #f44336;
  /* 故障 */
}
.balance {
  color: #4caf50;
  text-align: center;
}
.content {
  width: 100%;
}
.left {
  width: 50%;
}
</style>