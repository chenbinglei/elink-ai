<template>
    <div class="w-full h-full flex flex-col items-center justify-center text-white">
        <i class="iconfont text-56px mb-8px" :class="icon"></i>
        <p class="text-24px">{{ title }}</p>
    </div>
</template>

<script setup>
const props = defineProps({
    title: {
        type: String,
        default: '暂无数据'
    },
    icon: {
        type: String,
        default: 'icon-kongshuju'
    }
})
</script>
