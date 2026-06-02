<script setup lang="js">
import { computed } from 'vue';

const props = defineProps({
    column: {
        type: Object,
        default: ''
    },
    row: {
        type: Object,
        default: ''
    },
});
const text = computed(() => props.row[props.column.key]);
</script>
<template>
    <span class="px-12px py-6px rounded-4px relative square" :class="text == '操作成功' ? 'status-1' : 'status-0'">{{ text }}</span>
</template>
<style scoped lang="scss">
.square {
    color: var(--color);

    &::after {
        content: '';
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background-color: var(--color);
        opacity: 0.2;
    }
}

.status-0 {
    --color: #FF0000;
}

.status-1 {
    --color: #34E800;
}
</style>