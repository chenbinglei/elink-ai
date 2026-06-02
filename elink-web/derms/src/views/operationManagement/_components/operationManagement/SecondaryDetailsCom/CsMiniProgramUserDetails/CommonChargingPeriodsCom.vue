<template>
  <TitleView title="常用充电时段">
    <template #content>
      <div v-loading="listLoading" class="content_body"></div>
    </template>
  </TitleView>
</template>

<script>
import {defineComponent, onMounted, reactive, toRefs} from "vue";

export default defineComponent({
  name: "CommonChargingPeriodsCom",
  props: {
    routeInfo: {
      type: Object,
      default: () => {
        return {};
      }
    }
  },
  setup(props) {

    const that = reactive({
      listLoading: false,
    });

    return {...toRefs(that)};
  }
});
</script>

<style lang="scss" scoped>

</style>