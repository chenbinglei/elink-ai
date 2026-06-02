<template>
  <el-form :model="active_pel_date" class="content_item_body">
    <el-collapse-item name="3" title="图片属性">
      <el-form-item>
        <div class="content_list_image">
          <el-image :src="active_pel_date.image"></el-image>
        </div>
      </el-form-item>
      <el-form-item>
        <div class="flex-ai-center">
          <el-checkbox v-model="active_pel_date.imageRatio" @change="setValueCanvasMeta2dPenFun">固定比例</el-checkbox>
          <el-checkbox v-model="active_pel_date.toGif" @change="setValueCanvasMeta2dPenFun">转动图</el-checkbox>
        </div>
      </el-form-item>
      <el-form-item>
        <el-col :span="11">
          <el-form-item>
            <el-input v-model="active_pel_date.iconWidth" placeholder="自适应" size="small" type="number" @change="setValueCanvasMeta2dPenFun">
              <template #prefix>
                <div class="innerText">W</div>
              </template>
            </el-input>
          </el-form-item>
        </el-col>
        <el-col :span="2"></el-col>
        <el-col :span="11">
          <el-form-item>
            <el-input v-model="active_pel_date.iconHeight" placeholder="自适应" size="small" @change="setValueCanvasMeta2dPenFun">
              <template #prefix>
                <div class="innerText" type="number">H</div>
              </template>
            </el-input>
          </el-form-item>
        </el-col>
      </el-form-item>
      <el-form-item label="URL">
        <el-input v-model="active_pel_date.image" placeholder="URL" size="small" type="textarea" @change="setValueCanvasMeta2dPenFun"></el-input>
      </el-form-item>
    </el-collapse-item>
  </el-form>
</template>

<script>
import {useStore} from "vuex";
import {reactive, toRefs, defineComponent, computed, watch, getCurrentInstance} from "vue";

export default defineComponent({
  name: "PenImageGifConfigCom",
  props: {
    activePelDate: {
      type: Object,
      default: () => {
        return {}
      }
    }
  },
  emits: ["update:activePelDate", "changeEvent"],
  setup(props) {

    const store = useStore();
    const {emit} = getCurrentInstance();

    const canvasMeta2d = computed(() => {
      return store.state.meta2d.canvasMeta2d;
    });

    const that = reactive({
      active_pel_date: {},
    })

    const setValueCanvasMeta2dPenFun = () => {
      emit("update:activePelDate", that.active_pel_date);
      emit("changeEvent");
    }

    const watchActivePelDate = watch(() => props.activePelDate, (newActivePelDate) => {
      if(JSON.stringify(that.active_pel_date) !== JSON.stringify(newActivePelDate)){
        that.active_pel_date = JSON.parse(JSON.stringify(newActivePelDate));
      }
    }, {deep: true,immediate: true})

    return {...toRefs(that), canvasMeta2d, watchActivePelDate, setValueCanvasMeta2dPenFun}
  }
})
</script>

<style lang="scss" scoped>
.el-form-item {
  margin-bottom: 4px;

  .el-checkbox {
    --el-checkbox-font-size: 12px;
    --el-checkbox-text-color: var(--color);
  }

  .content_list_image {
    width: 100%;
    padding: 12px 10px;
    box-sizing: border-box;
    display: flex;
    align-items: center;
    justify-content: center;

    .el-image {
      width: 98px;
      height: 98px;
    }
  }
}
</style>