<script setup lang="js">
import { computed,reactive } from 'vue';

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
const state = reactive({
  1: '生效中',
  2: '已过期',
});
const key = computed(() => props.row[props.column.key]);
const otherProp = computed(() => props.row[props.column.otherProp]);
</script>
<template>
  <div>
    <p v-if="key && key != 'null'">{{ key}}</p>
    <p v-else style="font-size: 20px;">∞</p>
    <p v-if="key && key != 'null'"><span class="rounded-4px relative square" :class="`status-${otherProp}`">{{ state[otherProp] }}</span></p>
  </div>
</template>
<style scoped lang="scss">
.square {
    font-size: 12px;
    color: var(--color);
    padding: 3px 10px 7px;
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

.status-2 {
    --color: #FF0000;
}

.status-1 {
    --color: #34E800;
}
</style>