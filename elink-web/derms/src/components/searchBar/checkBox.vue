<template>
    <div class="flex justify-start items-start">
        <div class="w-16px h-16px flex justify-center items-center mr-8px checkbox" :class="{
            'active': model > 0,
            'disabled': disabled
        }" @click.stop="onClick">
            <span v-if="model === 2" class="w-8px h-8px rounded-1px inner"></span>
            <span v-if="model === 1" class="iconfont icon-duihao text-[#00CCFF]"></span>
        </div>
        <div class="flex-1 text-14px flex justify-center items-center"
            :class="model > 0 ? 'text-[#00CCFF]' : 'text-[#FFFFFF]'">
            <slot></slot>
        </div>
    </div>
</template>

<script setup>
import { nextTick } from 'vue';

const props = defineProps({
    disabled: {
        type: Boolean,
        default: false
    }
});
const emit = defineEmits(['change']);
const model = defineModel({
    type: Number,
    default: 0,  // 0: false, 1: true, 2: half
});
const onClick = () => {
    if (props.disabled) { return; }
    model.value = (model.value + 1) % 2;
    nextTick(() => {
        emit('change', model.value);
    })
}
</script>
<style scoped lang="scss">
.checkbox {
    width: 16px;
    height: 16px;
    border-radius: 2px;
    border: 1px solid #005B8A;


    &.disabled{
        border: 1px solid #595959;
        background: linear-gradient(180deg, #001319 0%, #595959 100%);
        cursor: not-allowed !important;
    }
    &:not(.disabled).active {
        background: linear-gradient(180deg, #001319 0%, #008FB3 100%);
        border: 1px solid #00CCFF;
    }

    .inner {
        background: linear-gradient(310deg, #00CCFF 0%, #00B6E3 100%);
    }
}
</style>