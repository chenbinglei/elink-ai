<template>
    <div class="w-full h-full px-10px pb-20px box-border">
        <OperationOverview :site-id="siteId" :code="code" />
    </div>
</template>

<script setup>
import { computed } from 'vue';
import { useRoute } from 'vue-router';
import { OperationOverview } from "@/views/centralMonitoring/_components";

const route = useRoute();
const siteId = computed(() => route.params.id || '');
const code = 'OperationOverview';
</script>

<style scoped>
.home {
    padding: 20px;
    font-family: Arial, sans-serif;
}
</style>