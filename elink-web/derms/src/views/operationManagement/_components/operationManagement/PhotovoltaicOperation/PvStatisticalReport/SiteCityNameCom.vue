<template>
  <div class="siteCityNameCom">
    <span>{{ $filters.moreData(returnDataInfo.province) }}</span>
    <span>/</span>
    <span>{{ $filters.moreData(returnDataInfo.city) }}</span>
  </div>
</template>

<script>
import {onMounted, reactive, ref, toRefs, defineComponent} from "vue";

export default defineComponent({
  name: 'SiteCityNameCom',
  props: {
    returnDataInfo: {
      type: Object,
      default: () => {
        return {};
      }
    }
  },
  setup(props) {
    const that = reactive({});

    return {...toRefs(that),};
    }
});
</script>

<style lang="scss" scoped>

</style>