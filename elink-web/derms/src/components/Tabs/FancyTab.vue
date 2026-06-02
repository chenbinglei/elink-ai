<template>
    <div class="fancy-tab flex flex-col items-center">
        <div class="tabs flex ">
            <div v-for="item in tabs" :key="item.id"
                class="h-24px flex justify-center items-center cursor-pointer text-white" :class="{
                    'active': activeTab === item.id,
                    'with-bg': !justText,
                    'no-bg family-zihun': justText
                }" @click="onChange(item.id)">
                {{ item.name }}
            </div>
        </div>
    </div>
</template>

<script setup>

const props = defineProps({
    tabs: {
        type: Array,
        default: () => []
    },
    justText: {
        type: Boolean,
        default: false
    }
});
const activeTab = defineModel('activeTab');
const onChange = (id) => {
    activeTab.value = id;
};
</script>
<style lang="scss" scoped>
.no-bg {
    padding: 0 12px 0 12px;

    &.active {
        color: #00CCFF;
    }
}

.with-bg {
    --clip-var: polygon(20% 0, 100% 0, 80% 100%, 0 100%);
    --top-color: rgba(59, 170, 245, 0.32);
    --bottom-color: rgba(59, 170, 245, 0.2);
    position: relative;
    padding: 0 34px 0 34px;

    &:not(:first-child):not(:last-child) {
        margin: 0 -10px;
    }

    &::after {
        content: '';
        position: absolute;
        left: 0;
        top: 0;
        width: 100%;
        height: 100%;
        clip-path: var(--clip-var);
        background: linear-gradient(180deg, var(--top-color) 0%, rgba(59, 170, 245, 0) 49%, var(--bottom-color) 100%);
    }

    &:first-child {
        --clip-var: polygon(0 0, 100% 0, 76% 100%, 0 100%);
        padding: 0 34px 0 24px;

        &+ :last-child {
            margin-left: -10px;
        }

        &:is(:last-child) {
            --clip-var: polygon(0 0, 100% 0, 100% 100%, 0 100%);
            padding: 0 24px 0 24px;
        }
    }

    &:last-child {
        --clip-var: polygon(20% 0, 100% 0, 100% 100%, 0 100%);
        padding: 0 24px 0 34px;
    }

    &.active {
        --top-color: rgba(59, 170, 245, 0.62);
        --bottom-color: rgba(59, 170, 245, 0.72);
        color: #00CCFF;
    }
}
</style>
