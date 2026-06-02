<template>
  <SecondGradeRouterCom ref="secondGradeRouterComRef" />
</template>

<script>
import {reactive, defineComponent, toRefs} from "vue";
import SecondGradeRouterCom from "@/components/component/SecondGradeRouterCom.vue";

export default defineComponent({
  name: "operationManagement",
  components: {SecondGradeRouterCom},
  setup() {
    const that = reactive({});
    return {...toRefs(that)};
  }
});
</script>

<style lang="scss" scoped></style>