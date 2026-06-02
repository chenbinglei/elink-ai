<script setup lang="js">
import { computed } from 'vue';
import { EventLevelList } from "@/common/enum";
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
const imgPath = computed(() => { 
    let path = ''
    EventLevelList.forEach(item => { 
        if (item.value == levelNum.value) {
            path = item.icon;
        }
    });
    return path;
});
</script>
<template>
    <img :src="imgPath" alt="" width="50" v-if="imgPath">
    <span v-else>--</span>
</template>+9
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