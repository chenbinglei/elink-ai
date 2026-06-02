<template>
  <div class="app-container">
    <TopToolbarCom ref="topToolbarComRef"></TopToolbarCom>
    <div v-loading="listLoading" class="app-container-bottom">
      <Meta2dEditorCom ref="meta2dEditorComRef"></Meta2dEditorCom>
    </div>
  </div>
</template>

<script>
import {useStore} from "vuex";
import {useRoute} from "vue-router";
import {findGraphById} from "@/api/2DVisualization/2d_artworkEditor";
import {reactive, toRefs, defineComponent, ref, onMounted, nextTick} from "vue";
import {Meta2dEditorCom, TopToolbarCom} from "@/views/2DVisualization/components";

export default defineComponent({
  name: '2d_artworkEditor',
  components: {Meta2dEditorCom, TopToolbarCom},
  setup() {

    const route = useRoute();
    const store = useStore();
    const topToolbarComRef = ref(null);
    const meta2dEditorComRef = ref(null);

    const that = reactive({
      listLoading: false,
      activeGraphId: route.query.id, // 当前图模id
    })

    // 根据图模id查询图模文件数据
    const queryGraphById = () => {
      that.listLoading = true;
      findGraphById({id: that.activeGraphId }).then(res => {
        let canvasMeta2dData = res.data ? res.data : {};

        // if(history.state.copy) delete canvasMeta2dData.id; // 复制一张图
        store.dispatch('updateCanvasMeta2dData', canvasMeta2dData);
        nextTick(()=>{
          meta2dEditorComRef.value.activeRegistrationMeta2dFun(); // 注册画布
          that.listLoading = false;
        })
      }).catch(() => {
        that.listLoading = false;
      })
    }

    onMounted(() => {
      queryGraphById();
    })

    return {...toRefs(that), meta2dEditorComRef, topToolbarComRef, queryGraphById}
  }
})
</script>

<style lang="scss" scoped>
.app-container {
  min-width: 1200px;
  flex-direction: column;

  --color: #737A8A;
  --color-gray: #bfbfbf;
  --color-title: #666D79;
  --color-border: #f0f1f2;
  --color-primary: #4583ff;
  --color-background: #ffffff;
  --color-border-input: #e5e5e5;

  .app-container-bottom {
    flex: 1;
    height: 2px;
  }
}
</style>