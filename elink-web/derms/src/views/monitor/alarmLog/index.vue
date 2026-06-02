<template>
    <div class="w-full h-full pl-40px pr-22px pb-120px box-border relative">
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
