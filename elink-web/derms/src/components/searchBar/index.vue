<template>
    <div class="w-full relative overflow-hidden">
        <el-form ref="formRef" :model="formData" :rules="rules" :inline="true" :status-icon="true"
            class="h-32px flex items-stretch  search-bar" @submit.native.prevent="onSearch">
            <template v-for="(item, index) in config" :key="index">
                <el-form-item v-if="item.type === 'select'" :label="item.title" class="h-full">
                    <el-select v-model="formData[item.key]" v-bind="item.props" class="search-bar-select">
                        <el-option v-for="optionData in item.props.options" :key="optionData.value"
                            :label="optionData.label" :value="optionData.value" />
                    </el-select>
                </el-form-item>

                <el-form-item v-if="item.type === 'treeSelect'" :label="item.title" class="h-full">
                    <el-tree-select v-model="formData[item.key]" v-bind="item.props" class="search-bar-tree-select" />
                </el-form-item>

                <el-form-item v-if="item.type === 'date'" :label="item.title" class="h-full">
                    <el-date-picker v-model="formData[item.key]" v-bind="item.props" class="search-bar-date-range" />
                </el-form-item>

                <el-form-item v-if="item.type === 'input'" :label="item.title" class="h-full">
                    <el-input v-model="formData[item.key]" v-bind="item.props" />
                </el-form-item>
                <el-form-item v-if="item.type === 'cascader'" :label="item.title" class="h-full">
                    <Cascader v-model="formData[item.key]" v-bind="item.props" />
                </el-form-item>

            </template>

            <el-form-item class="h-full flex items-center justify-center">
                <el-button type="primary" @click="onSearch">
                    <i class="iconfont icon-search text-18px w-16px text-white mr-6px" />
                    查询
                </el-button>
                <el-button @click="onReset"> <i
                        class="iconfont icon-reset text-16px w-16px text-white mr-6px" />重置</el-button>
                <el-button @click="onExport"> <i
                        class="iconfont icon-daochu text-16px w-16px text-white mr-6px" />导出</el-button>
            </el-form-item>
        </el-form>
    </div>
</template>

<script setup>
import { ref, unref } from 'vue';
import { ElMessage } from 'element-plus';
import Cascader from './cascader.vue';

/**
 * @description 表单配置项
 * @typedef {Object} FormConfig
 * @property {string} type - 表单项类型，如 'select', 'treeSelect', 'date', 'input'
 * @property {string} label - 表单项标签
 * @property {string} key - 表单项绑定的字段名
 * @property {Object} props - 表单项组件的props
 */
const props = defineProps({
    // 表单配置项
    config: {
        type: Array,
        default: () => [],
    },
    rules: {
        type: Object,
        default: () => { },
    },
    initData: {
        type: Object,
        default: () => ({}),
    },
});
const formData = defineModel({
    type: Object,
    required: true
});
const emit = defineEmits(['search', 'reset', 'export']);
const formRef = ref(null);

const onSearch = () => {
   setTimeout(() => {
      formRef.value.validate((valid) => {
        if (valid) {
            emit('search', formData);
        } else {
            ElMessage({ type: "error", showClose: true, message: "表单校验失败" });
            return false;
        }
    });
   }, 500);
};

const onReset = () => {
    
    Object.assign(formData.value, unref(props.initData));
    emit('reset', formData);
};
const onExport = () => {
    emit('export', formData);
};
// 暴露对外方法
defineExpose({
    onSearch,
    onReset,
    onExport,
});

</script>
<style lang="scss" scoped>
.search-bar {
    :deep(.el-form-item) {
        margin-right: 36px;

        .el-form-item__label {
            height: 100%;
            display: flex;
            align-items: center;
            justify-content: flex-start;
        }

        .el-form-item__content {
            height: 100%;
            line-height: 1;
            --font-size: 14px;
        }
    }
}

.search-bar-select {
    --el-select-width: 240px;
}

.search-bar-date-range {
    --el-date-editor-width: 360px;
}

.search-bar-tree-select {
    --el-select-width: 240px;

    :deep(.el-select__wrapper) {
        font-size: 14px;
        gap: 6px;
        line-height: 24px;
        min-height: 32px;
        padding: 4px 12px;
    }
}

.search-bar-input {
    --el-input-width: 240px;
}
</style>
