<script setup>
const props = defineProps({
    id: {
        type: String,
        required: true,
    },
    name: {
        type: String,
        required: true,
    },
    unit: {
        type: String,
        required: false,
    },
    val: {
        type: String,
        required: true,
    }
});
</script>

<template>
    <div class="w-184px h-70px flex items-stretch justify-start overflow-hidden running-quota">
        <!-- 图标 -->
        <div class="w-66px flex-shrink-0 flex flex-col justify-center items-center mr-12px">
            <img :src="`/img/monitor/quota/${id}.png`" loading="lazy" decoding="async"
                class="w-32px h-32px transform-translate-y--10px" />
        </div>
        <!-- 指标名称 数值  单位 -->
        <div class="flex-1 flex-col items-stretch justify-end overflow-hidden">
            <div class="h-27px flex justify-start items-end pr-16px overflow-hidden">
                <el-popover :popper-style="{ padding: 0, width: 'fit-content' }" popper-class="plant-list-popper"
                    placement="top" teleported="true">
                    <span class="p-8px text-24px text-left text-white whitespace-nowrap family-fb">
                        {{ val ?? '--' }}<span class="text-14px color-#00CCFF ml-7px">{{ unit }}</span>
                    </span>
                    <template #reference>
                        <span
                            class="text-24px lh-20px text-right text-white text-ellipsis whitespace-nowrap cursor-help family-fb"
                            :class="unit ? 'overflow-hidden' : 'overflow-visible'">
                            {{ val ?? '--' }}
                        </span>
                    </template>
                </el-popover>
                <span v-if="unit" class="flex-shrink-1 whitespace-nowrap text-14px color-#00CCFF ml-7px">{{
                    unit }}</span>
            </div>
            <!-- <div class="h-33px text-14px max-w-84px text-white flex flex-col justify-center items-start ">
                {{ name }}
            </div> -->
            <el-popover placement="bottom" teleported="true">
                <div>{{ name ?? '--' }}</div>
                <template #reference>
                    <div
                        class="max-w-84px whitespace-nowrap overflow-hidden text-ellipsis text-white text-14px line-height-33px h-33px">
                        {{


                            name ?? '--' }}</div>
                </template>
            </el-popover>
        </div>
    </div>
</template>
<style lang="scss">
.running-quota {
    background: url("/img/monitor/runningQuotaBg.png");
    background-repeat: no-repeat;
    background-size: 100% 100%;
}
.el-popover{
    padding: 5px 12px !important;
}
</style>