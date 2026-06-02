<script setup lang="js">
import MonitorBlock from '@/views/monitor/_slice/base/monitorBlock.vue';
import EmptyView from "@/views/monitor/_slice/base/empty.vue";

const props = defineProps({
  data: {
    type: Object,
    required: true
  }
});
</script>

<template>
  <MonitorBlock icon="telemetryData" title="遥测数据">
    <template #content>
      <div class="w-full h-full box-border px-20px py-17px box-border overflow-auto">
        <EmptyView v-if="
          !data?.telemetryDataList || data?.telemetryDataList.length === 0
        " />
        <div v-else class="w-full h-full grid grid-cols-4 gap-22px">
          <div v-for="item in data?.telemetryDataList" :key="item.functionId"
            class="w-166px h-60px flex flex-col justify-center items-center telemetry-data-unit">
            <div class="w-full flex justify-center items-center">
              <span v-if="item.unit === '%'" class="text-24px text-white family-fb">{{ Number(item.value).toFixed(2)
              }}</span>
              <span v-else class="text-24px text-white family-fb">
               {{ item.value ?? '--' }}
              </span>
              <span v-if="item.unit && item.value" class="text-14px text-[#00CCFF] ml-4px">
                {{ item.unit }}
              </span>
            </div>

            <el-popover placement="top" :open-delay="200" trigger="hover">
              <span>{{ item.functionName }}</span>
              <template #reference>
                <span class="text-14px text-white w-full text-center overflow-hidden text-ellipsis whitespace-nowrap">
                  {{ item.functionName }}
                </span>
              </template>
            </el-popover>
            <!-- <span class="text-14px text-white">{{ item.functionName }}</span> -->

          </div>
        </div>
      </div>
    </template>
  </MonitorBlock>
</template>

<style lang="scss" scoped>
.telemetry-data-unit {
  background-image: url("/img/monitor/common/telemetryDataUnitBg.png");
  background-repeat: no-repeat;
  background-size: 100% 100%;
}
</style>