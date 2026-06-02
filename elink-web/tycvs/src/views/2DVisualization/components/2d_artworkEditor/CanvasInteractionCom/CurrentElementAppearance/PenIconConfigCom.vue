<template>
  <el-collapse-item name="10" title="图标设置">
    <el-form :model="active_pel_date" class="content_item_body">
      <el-form-item label="图标">
        <span :class="active_pel_date.iconFamily" class="icon_class">{{ active_pel_date.icon }}</span>
        <div style="width: 10px"></div>
        <el-button type="primary" link @click="penIconFamilyVisible = true">选择</el-button>
      </el-form-item>
    </el-form>

    <PenIconFamilyDialog v-if="penIconFamilyVisible" v-model:isVisible="penIconFamilyVisible" @changeEvent="changeEvent" />
  </el-collapse-item>
</template>

<script>
import {useStore} from "vuex";
import PenIconFamilyDialog from "./PenIconFamilyDialog.vue";
import {reactive, toRefs, defineComponent, computed, watch, getCurrentInstance} from "vue";

export default defineComponent({
  name: "PenIconConfigCom",
  components: {PenIconFamilyDialog},
  props: {
    activePelDate: {
      type: Object,
      default: () => {
        return {}
      }
    }
  },
  emits: ["update:activePelDate"],
  setup(props) {

    const store = useStore();
    const {emit} = getCurrentInstance();

    const canvasMeta2d = computed(() => {
      return store.state.meta2d.canvasMeta2d;
    });

    const that = reactive({
      active_pel_date: {},
      penIconFamilyVisible: false,
      updateFieldList: ["icon", "iconFamily"]
    })

    const changeEvent = (data)=>{
      // console.log(data);
      that.active_pel_date.icon = data.icon;
      that.active_pel_date.iconFamily = data.iconFamily;
      setValueCanvasMeta2dPenFun();
    }

    const setValueCanvasMeta2dPenFun = () => {
      let updateFieldObj = {};
      emit("update:activePelDate", that.active_pel_date);
      for (let key of that.updateFieldList) updateFieldObj[key] = that.active_pel_date[key];
      canvasMeta2d.value.setValue({id: that.active_pel_date.id, ...updateFieldObj});
    }

    const watchActivePelDate = watch(() => props.activePelDate, (newActivePelDate) => {
      if(JSON.stringify(that.active_pel_date) !== JSON.stringify(newActivePelDate)){
        that.active_pel_date = JSON.parse(JSON.stringify(newActivePelDate));
      }
    }, {deep: true,immediate: true})

    return {...toRefs(that), canvasMeta2d, watchActivePelDate, setValueCanvasMeta2dPenFun, changeEvent}
  }
})
</script>

<style lang="scss" scoped>
.el-form-item {
  margin-bottom: 4px;

  .icon_class{
    font-size: 32px;
    font-weight: bold;
  }
}
</style>