<template>
  <monitorBlock :title="info.typeId == '39' ? '关口表' : '电能表'" icon="pile">
    <template #content>
      <div
        class="w-full content h-full pl-15px pr-19px flex justify-start items-center box-border"
      >
        <!-- 左侧设备模型图 -->
        <div
          class="w-125px overflow-hidden mr-17px flex justify-center items-center relative"
        >
          <img
            :src="`/img/monitor/device/${avatar}.png`"
            loading="lazy"
            decoding="async"
            class="w-full"
            alt="设备模型图"
          />
        </div>
        <div class="flex-1 flex flex-col justify-start items-stretch">
          <div class="h-30px flex justify-center items-start device-name">
            <span class="text-white text-16px family-fb">{{
              info?.deviceName || "--"
            }}</span>
          </div>
          <div class="h-16px mb-16px text-center">
            <span class="text-[#C7E8FF] text-12px"
              >SN:{{ info?.deviceNumber || "--" }}</span
            >
          </div>

          <div class="h-60px flex justify-between items-start">
            <div
              class="w-102px h-60px pl-32px pt-9px bg-no-repeat bg-cover box-border flex flex-col justify-start items-stretch relative"
            >
              <img
                :src="`/img/monitor/common/infoQuotaBg/txStatus.png`"
                class="absolute w-full h-full z--1 top-0 left-0"
              />
              <div class="text-14px text-white">通信状态</div>
              <div
                class="w-full text-12px text-white flex justify-start items-center mt-7px"
              >
                <i
                  class="flex w-8px h-8px mr-8px rounded-50% status-item"
                  :class="`txStatus-${infoList.txStatus ?? 'other'}`"
                />
                {{ TxStatusEnum[infoList.txStatus] || TxStatusEnum.other }}
              </div>
            </div>
            <div
              class="w-102px h-60px pl-32px pt-9px bg-no-repeat bg-cover box-border flex flex-col justify-start items-stretch relative"
            >
              <img
                data-v-609d5c18=""
                src="/img/monitor/common/infoQuotaBg/model.png"
                class="absolute w-full h-full z--1 top-0 left-0"
              />
              <div class="text-14px text-white">设备型号</div>
              <div
                class="w-full text-12px text-white flex justify-start items-center mt-7px"
              >
                <el-popover placement="bottom" teleported="true">
                  <div>{{ infoList.model ?? "--" }}</div>
                  <template #reference>
                    <div
                      class="w-80px whitespace-nowrap overflow-hidden text-ellipsis"
                    >
                      {{ infoList.model ?? "--" }}
                    </div>
                  </template>
                </el-popover>
              </div>
            </div>
            <div
              class="w-102px h-60px pl-32px pt-9px bg-no-repeat bg-cover box-border flex flex-col justify-start items-stretch relative"
            >
              <img
                data-v-609d5c18=""
                src="/img/monitor/common/infoQuotaBg/manufacturerName.png"
                class="absolute w-full h-full z--1 top-0 left-0"
              />
              <div class="text-14px text-white">生产厂家</div>
              <div
                class="w-full text-12px text-white flex justify-start items-center mt-7px"
              >
                <el-popover placement="bottom" teleported="true">
                  <div>{{ infoList.manufacturerName ?? "--" }}</div>
                  <template #reference>
                    <div
                      class="w-80px whitespace-nowrap overflow-hidden text-ellipsis"
                    >
                      {{ infoList.manufacturerName ?? "--" }}
                    </div>
                  </template>
                </el-popover>
              </div>
            </div>
          </div>
        </div>
      </div>
    </template>
  </monitorBlock>
</template>
<script setup lang="js">
import { unref, inject,watch,ref,defineEmits} from 'vue';
import monitorBlock from '@/views/monitor/_slice/base/monitorBlock.vue';
import { TxStatusEnum, RunStateEnum, WorkStatusEnum } from "@/common/enum";
import { INJECT_KEY_DEVICE_INFO, airStateList } from "../constant";
import  SystemMonitorController from "@/api/together/systemMonitor";


const props = defineProps({
    title: {
        type: String,
        default: ''
    },
    icon: {
        type: String,
        default: ''
    },
    avatar: {
        type: String,
        default: ''
    },
    info: {
        type: Object,
        default: () => ({})
    }
});
const infoList=ref([])
// 监听 info 变化
const emit = defineEmits(['infoList']);

watch(() => props.info, async (newVal) => {
  console.log('jiantingshijian');
  if (!newVal) {
    console.warn('info is null or undefined');
    return;
  }
  try {
    const response = await SystemMonitorController.findSystemMeterData(newVal.id);
    infoList.value = response.data;
    
    emit('infoList', infoList.value); // 正确调用 emit
  } catch (error) {
    console.error('请求失败:', error);
  }
});


</script>

<style lang="scss" scoped>
.device-name {
    background-image: url("/img/monitor/deviceNameBg.png");
  background-repeat: no-repeat;
  background-size: 100% 18px;
  background-position: left bottom;
}
/* styles.css */
.half-width {
  width: 50%;
}
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