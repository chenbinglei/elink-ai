<template>
  <el-dialog v-model="dialog_visible" width='48%' :manual-enter-close="false" disabledLoading class="operationLogDialog"
    @close="saveDialog()">
    <template #header>
      <TableHeaderTitle title="节点人员设置"></TableHeaderTitle>
    </template>
    <div class="dialog-main">
      <nodeTask :selectedNode="selectedNode" :selectType="selectType"></nodeTask>
    </div>
  </el-dialog>
</template>

<script setup>
import { ref, onMounted, nextTick, onUnmounted } from 'vue'
import nodeTask from './nodeTask.vue'
const props = defineProps({
  isVisible: {
    type: Boolean,
    default: false
  },
})
const dialog_visible = ref(props.isVisible)
const emit = defineEmits(['saveDialog'])
const selectedNode = ref(true)
const selectType = ref('')  
const saveDialog = () => {
  emit('saveDialog')
}

</script>

<style lang="scss" scoped></style>