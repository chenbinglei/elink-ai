<template>
  <div class="operationPasswordInput">
    <div class="alter_title">请输入四位操作密码</div>
    <div class="passwordInput">
      <VerificationCode :unit="form_dialog.unit" ontime @changeEvent="changeEvent"></VerificationCode>
    </div>
  </div>
</template>

<script lang="ts">
import {defineComponent, reactive, toRefs} from "vue";
import VerificationCode from "@/components/component/VerificationCode.vue";

export default defineComponent({
  name: "OperationPasswordInput",
  components: {VerificationCode},
  setup() {

    const that = reactive({
      form_dialog:{ unit: 4 }
    });

    const changeEvent = (data) => {
      that.form_dialog.password = data;
    };

    return {...toRefs(that), changeEvent};
  }
});
</script>

<style lang="scss" scoped>
.operationPasswordInput {
  padding: 32px 16px;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  align-items: center;

  .passwordInput {
    width: 240px;
    margin-top: 24px;
  }
}
</style>