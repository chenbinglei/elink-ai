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
const levelNum = computed(() => props.row[props.column.key]);
const text = computed(() => props.column.formatter(levelNum.value));   
</script>
<template>
    <span class="px-12px py-6px rounded-4px relative square" :class="`level-${levelNum}`">{{ text }}</span>
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

.level-0 {
    --color: #34E800;
}

.level-1 {
    --color: #3BAAF5;
}

.level-2 {
    --color: #FFBB00;
}

.level-3 {
    --color: #FF0000;
}
</style>