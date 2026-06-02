<script setup>
import { ref, computed, watch } from 'vue';
import { useRoute, useRouter } from "vue-router";
import { useStore } from 'vuex';

const route = useRoute();
const router = useRouter();
const store = useStore();
const activeTab = ref('');

const parentRoute = computed(() => {
    const matched = route.matched;
    return matched[1];
});
const authList = computed(() => store.state.monitor.authMenuList);
const children = computed(() => {
    return parentRoute.value.children.filter(({ name }) => !authList.value || authList.value?.length === 0 || authList.value?.includes(name)).map(({ name, path, meta }) => ({ name, path, meta }));
});

// 切换tab
const switchTab = (item) => {
    const parentPath = parentRoute.value.path.replace(":id", route.params.id);
    activeTab.value = item.name;
    router.replace(`${parentPath}/${item.path}`);
}

watch(children, () => {
    if (children.value.length > 0) {
        // 如果是以二级路由进入 跳转第一个子路由
        children.value[0] && switchTab(children.value[0]);
    }
});

</script>

<template>
    <div class="flex flex-col w-36px position-absolute top-29px left-0 z-100">
        <div class="w-full h-128px  font-bold text-14px text-white py-33px pl-12px pr-10px box-border relative cursor-pointer monitor-tab"
            v-for="child in children" :key="child.name" :class="{
                active: child.name === activeTab,
                'text-white': child.name === activeTab,
                'color-#9FDEFF': child.name !== activeTab
            }" @click="switchTab(child)">
            {{ child.meta.title }}
        </div>
    </div>
</template>

<style lang="scss" scoped>
.monitor-tab {
    &::before {
        content: '';
        position: absolute;
        width: 100%;
        top: 0;
        left: 0;
        height: 142px;
        background-image: url("/img/monitor/monitorTab.png");
        background-repeat: no-repeat;
        background-size: cover;
        z-index: -1;
    }


    &.active {
        &::before {
            background-image: url("/img/monitor/monitorTabActive.png");
        }
    }

    &:not(.active) {
        &:hover {
            filter: brightness(1.2);
        }
    }
}
</style>