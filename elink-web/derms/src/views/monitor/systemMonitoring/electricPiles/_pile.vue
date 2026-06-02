<template>
    <div v-loading="loading"
        class="w-full h-full flex flex-col items-stretch justify-start overflow-x-hidden overflow-y-auto">
        <div class="h-220px flex-shrink-0 flex justify-between items-stretch mb-6px">
            <!-- 设备详情 -->
            <DeviceInfo title="V2G桩" icon="pile" avatar="energyStorageAuxiliaryEquipment" class="w-769px flex-shrink-0"
                :info="deviceInfoData" />
            <!-- 遥控遥调 -->
            <DeviceQuotaGroup class="w-769px  flex-shrink-0" :data="deviceInfoData" title="运行数据" icon="runningData" />
        </div>
        <div class="h-316px flex-shrink-0 flex justify-between items-stretch mb-6px">
            <BlockChart type-id="18" class="w-769px  flex-shrink-0" />
            <BlockChart type-id="19" class="w-769px  flex-shrink-0" />
        </div>
        <PileGunList :info="deviceInfoData" />
    </div>
</template>

<script setup>
// 碎片页引入
import { DeviceInfo, DeviceQuotaGroup, BlockChart } from "../_slice";
import PileGunList from "./_slice/pileGunList.vue";
// hooks
import useDeviceData from "@/views/monitor/systemMonitoring/_hooks/useDeviceData";

const { loading, deviceInfoData } = useDeviceData({ apiName: 'findPileData' });
</script>