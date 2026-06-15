<template>
  <Dialog v-model:isVisible="dialog_visible" :footerVisible="false" closeOnClickModal title="可使用站点" width="520">
    <template v-slot:content>
      <div class="content_body">
        <el-table :data="siteNameList" stripe :max-height="tableMaxHeight">
          <template #empty><null-data></null-data></template>
          <el-table-column label="序号" type="index" width="90"></el-table-column>
          <el-table-column label="站点名称" align="center" show-overflow-tooltip>
            <template #default="{ row }">{{ $filters.moreData(row) }}</template>
          </el-table-column>
        </el-table>
      </div>
    </template>
  </Dialog>
</template>

<script lang="ts">
import { reactive, toRefs, getCurrentInstance, onMounted, watch } from "vue";

export default {
  name: "CanBeUsedSiteDialog",
  props:{
    isVisible: {
      type: Boolean,
      default: false
    },
    siteNameList:{
      type:Array,
      default:()=>[]
    }
  },
  setup(props){
    const { emit } = getCurrentInstance();

    const that = reactive({
      tableMaxHeight: 380,
      dialog_visible: props.isVisible,
    });

    const watchVisible = watch([() => props.isVisible], ([newVisible]) => {
      that.dialog_visible = newVisible;
    });

    const watchDialogVisible = watch([() => that.dialog_visible], ([newDialogVisible]) => {
      emit("update:isVisible", newDialogVisible);
    });

    onMounted(() => {});

    return { ...toRefs(that),watchVisible,watchDialogVisible };
  }
};
</script>

<style scoped lang="scss"></style>
