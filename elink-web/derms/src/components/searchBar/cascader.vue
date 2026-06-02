<template>
    <el-popover ref="popover" placement="bottom" trigger="click" popper-class="w-auto! p-0!"
        @before-leave="onBeforeLeave">
        <div class="w-auto min-w-140px h-460px flex justify-start items-stretch cascader-box">
            <div class="py-15px my-15px  flex flex-col justify-start items-stretch cascader-menu overflow-y-auto overflow-x-hidden" 
                v-for="(levelData, index) in levelList" :key="index">
                <!-- 类别 -->
                <template v-if="index < 2 && levelData.length > 0">
                    <div v-for="item in levelData" :key="item.value"
                        class="min-w-140px h-32px flex justify-start items-center cursor-pointer pl-20px pr-24px box-border hover:bg-[#0080bf4d]"
                        :class="{
                            'bg-[#0080bf4d]': isActive(index, item),
                        }">
                        <CheckBox v-model="checkMap[item.value]" :disabled="!(item.children?.length > 0)"
                            @change="onCheckBoxChange(item)" class="w-full">
                            <div class="w-full flex justify-start items-center text-14px lh-19px text-[#FFFFFF] whitespace-nowrap"
                                @click.stop="showChildren(item, index)">
                                <span class="flex-1">{{ item.label }}</span>
                                <i class="iconfont icon-youdanjiantou text-12px ml-20px"
                                    v-if="item.children?.length > 0" />
                            </div>
                        </CheckBox>
                    </div>
                </template>
                <!-- 字段 -->
                <template v-if="index === 2 && levelData.length > 0">
                    <div class="w-100% flex flex-col justify-start items-start px-24px box-border overflow-hidden">
                        <el-input v-model="filterText" class="w-263px! h-32px! mb-10px!" placeholder="请输入关键字" />
                        <div class="w-full flex-1 overflow-y-auto overflow-x-hidden">
                            <!-- 字段选择 -->
                            <ul>
                                <li v-for="(field, fieldIndex) in levelData" :key="field.value" :class="{
                                    'mt-14px': fieldIndex > 0,
                                    'hidden': isHiddenField(field)
                                }">
                                    <CheckBox v-model="checkMap[field.value]" @change="onCheckBoxChange(field)"
                                        class="w-full">
                                        <div
                                            class="w-full flex flex-col justify-start items-start text-14px lh-19px text-[#FFFFFF] whitespace-nowrap">
                                            <span class="flex-1">{{ field.label }}</span>
                                            <!-- 下标 -->
                                            <ul v-if="field.children?.length > 0">
                                                <li v-for="indexData in field.children" :key="indexData.value" :class="{
                                                    'mt-14px': fieldIndex > 0
                                                }">
                                                    <CheckBox v-model="checkMap[indexData.value]"
                                                        @change="onCheckBoxChange(indexData)" class="w-full">
                                                        <div
                                                            class="w-full flex justify-start items-center text-14px lh-19px text-[#FFFFFF] whitespace-nowrap">
                                                            <span class="flex-1">{{ indexData.label }}</span>
                                                        </div>
                                                    </CheckBox>
                                                </li>
                                            </ul>
                                        </div>
                                    </CheckBox>
                                </li>
                            </ul>
                        </div>
                    </div>
                </template>
            </div>
        </div>
        <template #reference>
            <div>
                <el-popover placement="bottom" popper-class="w-auto!  overflow-hidden" teleported="true"
                    :trigger="displayText.length > 0 ? 'hover' : 'contextmenu'">
                    <div class="w-full max-h-560px overflow-auto">
                        <ul>
                            <li class="text-14px text-white family-fb" v-for="(txt, index) in displayText.split(',')"
                                :key="index">{{ txt }}</li>
                        </ul>
                    </div>
                    <template #reference>
                        <el-input type="text" v-model="displayText" readonly class="w-263px!" placeholder="请选择数据" />
                    </template>
                </el-popover>
            </div>

        </template>
    </el-popover>
</template>

<script setup>
import { ref,watch, watchEffect, reactive } from 'vue';
import CheckBox from './checkBox.vue';
import { computed } from 'vue';

const model = defineModel({
    type: Object,
    default: () => ({
        functions: [],
        nodes: [],
        timeInterval: '1m',
        fieldCount: 0
    }),
});
const props = defineProps({
    data: {
        type: Array,
        default: () => [],
    },
    max: {
        type: Number,
        default: 6
    }
});
const nodeMap = computed(() => {
    const map = {};
    let queue = [...props.data];
    while (queue.length > 0) {
        const target = queue.shift();
        map[target.value] = target;
        if (target.children?.length > 0) {
            queue = queue.concat(target.children);
        }
    }
    return map;
});
const displayText = ref('');
const filterText = ref('');
const levelList = ref([]);
const checkMap = reactive({});
const levelActiveList = ref([]);
// 检查给定元素的子元素 判断当前元素的check值应该是多少
const checkItemValue = (item) => {
    const checkedNum = item.children.filter((child) => checkMap[child.value] === 1).length;
    if (checkedNum === item.children.length) { return 1; }
    const noCheckedNum = item.children.filter((child) => checkMap[child.value] === 0 || checkMap[child.value] === undefined).length;
    if (noCheckedNum === item.children.length) { return 0; }
    return 2;
}
const cloneDeep = (data) => JSON.parse(JSON.stringify(data));
const filterTree = (_list, displayPrefix) => {
    let timeIntervalSet = new Set();
    const displayList = [];
    const list = _list.filter((item) => {
        const keep = checkMap[item.value] > 0;
        if (keep) {
            item.children = item.children.filter((field) => {
                const fieldKeep = checkMap[field.value] > 0;
                if (fieldKeep) {
                    if (field.children.length > 0) {
                        field.indexes = field.children.filter(({ value }) => {
                            return checkMap[value] > 0;
                        }).map(({ index }) => {
                            displayList.push(`${displayPrefix}/${item.label}/${field.label}/${index}`);
                            return index;
                        });
                    } else {
                        displayList.push(`${displayPrefix}/${item.label}/${field.label}`);
                    }
                    timeIntervalSet.add(field.raw.timeInterval);
                }
                return fieldKeep;
            }).map(({ raw, indexes }) => ({
                code: raw.fieldCode,
                indexes
            }))
        }
        return keep;
    }).map(({ raw, children }) => ({
        id: raw.id,
        type: raw.type,
        fields: children
    }));

    return {
        list,
        displayList,
        timeIntervalSet
    }
};
const intervalSort = ['s', 'm', 'h', 'd', 'n', 'y'];
// 隐藏前去更新model
const onBeforeLeave = () => {
    const { list: functions, displayList, timeIntervalSet } = filterTree(cloneDeep(nodeMap.value['function']?.children ?? []), '功能点');
    const { list: nodes, displayList: displayListCompute, timeIntervalSet: timeIntervalSetCompute } = filterTree(cloneDeep(nodeMap.value['compute']?.children ?? []), '计算点');
    const sortList = [...timeIntervalSet, ...timeIntervalSetCompute];
    sortList.sort((a, b) => {
        const aUnit = a[a.length - 1];
        const bUnit = b[b.length - 1];
        return intervalSort.indexOf(aUnit) - aUnit.indexOf(bUnit);
    });
    displayText.value = displayList.concat(displayListCompute).join(",");
    model.value = {
        functions,
        nodes,
        timeInterval: sortList[0] ?? '1m',
        fieldCount: displayList.length + displayListCompute.length,
    }
}
//点击checkbox
const onCheckBoxChange = (item) => {
    const targetValue = checkMap[item.value];
    //设置子
    let children = item.children ? [...item.children] : [];
    while (children.length > 0) {
        const child = children.shift();
        checkMap[child.value] = targetValue;
        if (child.children?.length > 0) {
            children = children.concat(child.children);
        }
    }
    //回溯父节点
    let parentId = item.parentId;
    while (parentId) {
        const parent = nodeMap.value[parentId];
        if (!parent) { return; }
        checkMap[parentId] = checkItemValue(parent);
        parentId = parent.parentId;
    }
};
//展开子节点
const showChildren = (item, index) => {
    levelList.value = levelList.value.slice(0, index + 1).concat([item.children]);
    levelActiveList.value = levelActiveList.value.slice(0, index).concat([item.value])
}
const isActive = (index, item) => {
    return levelActiveList.value[index] === item.value;
}

const isHiddenField = (field) => {
    return filterText.value && field.label.indexOf(filterText.value) + 1 === 0
}

watchEffect(() => {
    levelList.value = [props.data];
});

watch(model,()=>{
    console.log(model.value)
    if(model.value.fieldCount===0){
        displayText.value = '';
        for(let key in checkMap){
            checkMap[key] = false;
        }
    }
})



</script>
<style scoped lang="scss">
.cascader-box {
    background: #001929;
    box-shadow: inset 0px 0px 20px 1px #007CAB;
    border-radius: 4px 4px 4px 4px;
    border: 1px solid #0071A4;

    .cascader-menu {

        &:not(:first-child) {
            border-left: 1px solid #0071A4;
        }

        :deep(.el-tree) {
            .el-icon {
                display: none;
            }
        }
    }
}
</style>
