<template>
  <Dialog v-model:isVisible="dialog_visible" :title="titleName" width="720" @confirm="clickEnterBut">
    <template v-slot:content>
      <div class="dialog-main scrollbarStyle">
        <el-row :gutter="12">
          <template v-if="iconFontList && iconFontList.length">
            <template v-for="(item,index) in iconFontList" :key="index">
              <el-col :span="4">
                <div class="icon_list" @click="clickItemIconFun(item)" :class="{ active_class:activeIconUniCode === item.unicode }">
                  <span class="iconName" :class="[fontFamily,`${ cssPrefixText }${ item.font_class }`]" ></span>
                  <span class="icon_list_text">{{ item.name }}</span>
                </div>
              </el-col>
            </template>
          </template>
        </el-row>
      </div>
    </template>
  </Dialog>
</template>

<script>
import iconfont from "@/assets/iconfont/iconfont.json";
import {reactive, toRefs, watch, getCurrentInstance, defineComponent, onMounted} from 'vue';

export default defineComponent({
  name: "PenIconFamilyDialog",
  props: {
    isVisible: {
      type: Boolean,
      default: false,
    }
  },
  emits: ["update:isVisible","changeEvent"],
  setup(props) {

    const {emit} = getCurrentInstance();

    const that = reactive({
      listLoading: false,
      titleName: "选择ICON",
      iconFontList: iconfont.glyphs,
      dialog_visible: props.isVisible,
      fontFamily: iconfont.font_family,
      cssPrefixText: iconfont.css_prefix_text,

      activeIconUniCode: "",
    })

    const clickItemIconFun = (data) => {
      that.activeIconUniCode = data.unicode;
    }

    const clickEnterBut = () => {
      emit("changeEvent",{ type: "penIconFamilyDialog",icon: unescape("%u" + that.activeIconUniCode), iconFamily: that.fontFamily });
    }

    const watchVisible = watch(() => props.isVisible, (newVisible) => {
      that.dialog_visible = newVisible;
    })

    const watchDialogVisible = watch(() => that.dialog_visible, (newDialogVisible) => {
      emit("update:isVisible", newDialogVisible);
    })

    onMounted(() => {})

    return {...toRefs(that), watchVisible, watchDialogVisible, clickEnterBut, clickItemIconFun}
  }
})
</script>

<style lang="scss" scoped>
.dialog-main{
  max-height: 520px;
  overflow-y: auto;

  .icon_list{
    width: 100%;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    margin-bottom: 12px;
    padding: 12px 10px;
    box-sizing: border-box;
    border: 1px solid transparent;
    overflow: hidden;

    .iconName{
      font-size: 28px;
      margin-bottom: 10px;
    }

    .icon_list_text{
      font-size: 12px;
    }

    &:hover{
      color: #4B92FB;
      cursor: pointer;
      border-radius: 4px;
      border: 1px solid #4583ff;
    }
  }

  .active_class{
    color: #4B92FB;
    border-radius: 4px;
    border: 1px solid #4583ff;
  }
}
</style>