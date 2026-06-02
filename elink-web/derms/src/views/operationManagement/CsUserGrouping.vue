<template>
  <!-- 用户分组 -->
  <div class="app-container">
    <CsUserGrouping ref="secondGradeRouterComRef" />
  </div>
</template>

<script>
import { reactive, defineComponent, toRefs } from "vue";
import CsUserGrouping from "./_components/operationManagement/ChargingStationOperation/CsUserGrouping.vue";

export default defineComponent({
  name: "",
  components: { CsUserGrouping },
  setup () {
    const that = reactive({});
    return { ...toRefs(that) };
  }
});
</script>

<style lang="scss" scoped></style>