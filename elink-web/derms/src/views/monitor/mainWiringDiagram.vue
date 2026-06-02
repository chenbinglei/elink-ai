<template>
    <div class="w-full h-full">
        <MainWiringDiagram :site-id="siteId" :code="code" />
    </div>
</template>

<script setup>
import { computed } from 'vue';
import { useRoute } from 'vue-router';
import { MainWiringDiagram } from "@/views/centralMonitoring/_components";

const route = useRoute();
const siteId = computed(() => route.params.id || '');
const code = 'MainWiringDiagram';
</script>


<style scoped></style>