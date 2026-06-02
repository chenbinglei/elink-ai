<template>
    <div class="flex items-end position-absolute left-50% bottom-0 transform-translate-x--50% monitor-tab-list"
        :class="[
            { 'pb-16px': notInFrame },
            justifyClass
        ]"
        v-if="filteredTabList.length > 0">
        <div class="flex flex-col items-stretch justify-end cursor-pointer select-none"
            :class="item.name.indexOf('Home') + 1 > 0 ? 'w-121px' : 'w-77px'"
            v-for="item in filteredTabList"
            :key="item.name"
            @click="handleTabClick(item)">
            <div class="flex items-end justify-center w-full transform-translate-y-2px">
                <img :src="item.img" alt="" class="w-full" />
            </div>
            <div class="flex items-center justify-center">
                <span class="text-center whitespace-nowrap text-14px h-24px lh-24px px-18px tab-name family-zihun"
                    :class="{ active: item.name === activeTab }">
                    {{ item.meta.title }}
                </span>
            </div>
        </div>
    </div>
    <div v-else class="w-full h-full flex items-center justify-center">
        <Empty />
    </div>
</template>

<script setup>
import emptyImg from "@/assets/image/empty.png";
import { ElMessage } from 'element-plus';
import { ref, computed, watch, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import Empty from "@/views/monitor/_slice/base/empty.vue";
const props = defineProps({
    authSearching: {
        type: Boolean,
        default: true
    }
});
const route = useRoute();
const router = useRouter();
const routePrefix = computed(() => `/monitor/${route.params.id}`);
const monitorRouter = router.options.routes.find((item) => item.name === "monitor");
// 根据菜单数量动态返回 justify 类名
const justifyClass = computed(() => {
    const len = filteredTabList.value.length;
    if (len === 1) return 'justify-center';
    if (len === 2) return 'justify-around';
    return 'justify-between'; // len >= 3
});
// 获取有效的权限数组
const getValidPermissions = () => {
    const stored = localStorage.getItem("StationRoulist");
    if (stored === null || stored === undefined || stored === '' || stored === '0' || stored === 0) {
        return null;
    }
    try {
        const parsed = JSON.parse(stored);
        if (!Array.isArray(parsed) || parsed.length === 0) {
            return null;
        }
        return parsed;
    } catch (e) {
        console.error("解析 StationRoulist 失败", e);
        return null;
    }
};

// 判断一级菜单是否显示
const shouldShowMenuItem = (menuItem, permissions) => {
    if (permissions.includes(menuItem.name)) return true;
    if (menuItem.children && menuItem.children.length) {
        return menuItem.children.some(child => permissions.includes(child.name));
    }
    return false;
};

// 过滤后的菜单列表
const filteredTabList = computed(() => {
    if (!monitorRouter) return [];
    const permissions = getValidPermissions();
    if (!permissions) return [];

    const allMenus = monitorRouter.children.map(({ name, meta, path }) => ({
        name,
        meta,
        path: `${routePrefix.value}${path ? `/${path}` : ''}`,
        img: `/img/monitor/${name}.webp`,
        children: monitorRouter.children.find(m => m.name === name)?.children || []
    }));
    return allMenus.filter(menu => shouldShowMenuItem(menu, permissions));
});

const notInFrame = computed(() => window.top === window);
const activeTab = ref('');

// 监听菜单列表变化，设置默认激活项（第一个）
watch(
    filteredTabList,
    (newList) => {
        if (newList.length > 0 && !activeTab.value) {
            activeTab.value = newList[0].name;
            // 如果当前路由不是有效的菜单项，则跳转到第一个菜单
            const currentRouteName = route.matched[1]?.name;
            const isValidRoute = newList.some(menu => menu.name === currentRouteName);
            if (!isValidRoute) {
                router.replace(newList[0].path);
            }
        }
    },
    { immediate: true }
);

// 监听路由变化，同步 activeTab
watch(
    () => route.matched[1]?.name,
    (newRouteName) => {
        if (newRouteName && filteredTabList.value.some(menu => menu.name === newRouteName)) {
            activeTab.value = newRouteName;
        }
    },
    { immediate: true }
);

// 跳转处理
const handleTabClick = (item) => {
    if (props.authSearching) {
        ElMessage.info(`页面全选获取中,请稍后再试!`);
        return;
    }
    activeTab.value = item.name;
    router.replace(item.path);
};
</script>

<style lang="scss" scoped>
.monitor-tab-list {
    width: 532px;
    height: 120px;

    .tab-name {
        color: #3BAAF5;

        &.active {
            color: #ffffff;
            background-image: url("/img/monitor/monitorMenuActive.webp");
            background-repeat: no-repeat;
            background-size: 100% 88%;
            background-position: center;
        }
    }
}
</style>