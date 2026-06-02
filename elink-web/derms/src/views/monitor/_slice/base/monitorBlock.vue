<template>
    <div class="h-full flex flex-col items-stretch justify-start" :class="{
        'w-full': attrs.class?.indexOf('w-') + 1 === 0
    }">
        <!-- 展示块头部 -->
        <div class="w-full h-40px px-24px flex items-center justify-start box-border block-header">
            <img v-if="icon" class="mr-13px w-20px h-20px" :src="`/img/monitor/common/blockIcon/${icon}.svg`" />
            <div class="flex-1 flex items-center justify-start">
                <div class="text-18px text-white flex justify-start items-center">
                    {{ title }}
                    <slot name="sub" />
                </div>
            </div>
            <slot name="extra" />
        </div>
        <!-- 展示块主体 -->
        <div class="flex-1 overflow-hidden box-border block-body">
            <slot name="content" />
        </div>
    </div>
</template>

<script setup>
import { useAttrs } from 'vue';

const props = defineProps({
    icon: {
        type: String,
        required: false
    },
    title: {
        type: String,
        required: true,
        default: ''
    }
});
const attrs = useAttrs();
</script>

<style lang="scss" scoped>
.block-header {
    background-image: url("/img/monitor/blockHeader.png");
    background-repeat: no-repeat;
    background-size: 100% 100%;
}

.block-body {
    border-style: solid;
    border-width: 0 2px 0 2px;
    border-image: linear-gradient(180deg, rgba(230, 246, 255, 0), #0379B7, rgba(230, 246, 255, 0) 90%, transparent) 30;
}
</style>