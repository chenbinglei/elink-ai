<template>
  <Dialog class="clear_padding_class" append-to-body id="customPelDialog" v-model:isVisible="dialog_visible" :manualEnterClose="false" title="自定义图元" width="75vw" @confirm="clickEnterBut">
    <template v-slot:content>
      <div class="dialog-main" v-loading="listLoading">
        <Meta2dEditorCom ref="meta2dEditorComRef" modeType="module" :canvasAutoInit="false"></Meta2dEditorCom>
      </div>
    </template>
  </Dialog>
</template>

<script>
import { useMeta2dStore } from '@/stores/index';

import Meta2dEditorCom from "@/views/2DVisualization/components/2d_artworkEditor/Meta2dEditorCom.vue";
import {reactive, toRefs, watch, getCurrentInstance, ref, defineComponent, onMounted, nextTick} from 'vue';

export default defineComponent({
  name: "CustomPelDialog",
  components: {Meta2dEditorCom},
  props: {
    isVisible: {
      type: Boolean,
      default: false,
    },
    codeValue: {
      type: String,
      default: "",
    },
  },
  emits: ["update:isVisible","update:codeValue"],
  setup(props) {
    const meta2dStore = useMeta2dStore();
    const {emit} = getCurrentInstance();
    const meta2dEditorComRef = ref(null);

    const that = reactive({
      listLoading: false,
      dialog_visible: props.isVisible,
    })

    const clickEnterBut = ()=>{
      // that.listLoading = true;
      let componentData = meta2dEditorComRef.value.canvasMeta2dToComponentFun();
      emit("update:codeValue",componentData);
      that.dialog_visible = false;
      emit("changEvent");
    }

    const watchVisible = watch([() => props.isVisible], ([newVisible]) => {
      that.dialog_visible = newVisible;
    })

    const watchDialogVisible = watch([() => that.dialog_visible], ([newDialogVisible]) => {
      emit("update:isVisible", newDialogVisible);
    })

    onMounted(()=>{
      meta2dStore.updateCanvasMeta2dData({ fileData: props.codeValue });
      nextTick(()=> meta2dEditorComRef.value.activeRegistrationMeta2dFun()); // 打开组件
    })

    return {...toRefs(that), watchVisible, watchDialogVisible, meta2dEditorComRef, clickEnterBut}
  }
})
</script>

<style scoped lang="scss">
.dialog-main{
  height: 65vh;
}
</style>
