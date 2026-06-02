<template>
    <div class="w-full h-full flex items-start justify-start">
        <router-view v-slot="{ Component }">
            <keep-alive :max="3">
                <component :is="Component" />
            </keep-alive>
            <template v-if="!Component">
                <Empty title="无权限,请联系管理员开通" />
            </template>
        </router-view>
    </div>
</template>

<script setup>
import Empty from '../_slice/base/empty.vue';
</script>

<style scoped></style>