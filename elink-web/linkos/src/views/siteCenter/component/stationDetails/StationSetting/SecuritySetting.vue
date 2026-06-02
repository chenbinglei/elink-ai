<template>
  <div class="securitySetting">
    <el-form :model="formInline">
      <el-form-item label="操作密码：">
        <span v-if="showPassword">{{ $filters.moreData(formInline.operatePassword) }}</span>
        <span v-else>****</span>
        <span class="pointer" @click="showPassword = !showPassword">
        <span :class=" showPassword ? 'icon-yanjing_yincang_o' : 'icon-yanjing_xianshi_o'" class="iconfont"></span>
      </span>
        <span class="alter_title">（初始密码0000）</span>
      </el-form-item>
    </el-form>

    <OperationPasswordDialog v-if="operationPasswordVisible" v-model:isVisible="operationPasswordVisible" :formDialog="formInline" @changEvent="changEvent"></OperationPasswordDialog>
  </div>
</template>

<script>
import OperationPasswordDialog from "./OperationPasswordDialog.vue";
import {reactive, toRefs, defineComponent, watch, getCurrentInstance} from "vue";

export default defineComponent({
  name: "SecuritySetting",
  components: {OperationPasswordDialog},
  props: {
    returnDataInfo: {
      type: Object,
      default: () => {
        return {}
      }
    }
  },
  setup(props) {
    const {emit} = getCurrentInstance();

    const that = reactive({
      formInline: {},
      showPassword: false,
      operationPasswordVisible: false,
    })

    const clickEditButtonFun = () => {
      that.operationPasswordVisible = true;
    }

    const changEvent = (data)=>{
      emit("changEvent",data);
    }

    const watchReturnDataInfo = watch(() => props.returnDataInfo, (newReturnDataInfo) => {
      that.formInline = JSON.parse(JSON.stringify(newReturnDataInfo ?? {}));
    }, {deep: true, immediate: true})

    return {...toRefs(that), watchReturnDataInfo, clickEditButtonFun,changEvent}
  }
})
</script>

<style lang="scss" scoped>
.iconfont {
  margin: 0 12px;
  font-size: 18px;
  font-weight: bold;
}

.alter_title {
  color: #cccccc;
  font-size: 12px;
}
</style>