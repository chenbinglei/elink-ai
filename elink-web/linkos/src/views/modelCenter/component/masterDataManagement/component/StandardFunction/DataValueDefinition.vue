<template>
  <div class="dataValueDefinition">
    <template v-if="dataType < 5 || dataType === 8">--</template>
    <template v-if="dataType === 5">
      <template v-if="data_object.enumArray && data_object.enumArray.length">
        <template v-for="(item,index) in data_object.enumArray" :key="index">
          <span class="text">{{ $filters.moreData(item.id) }}：</span>
          <span class="text">{{ $filters.moreData(item.name) }}</span>
          <span v-if="index + 1 < data_object.enumArray.length">，</span>
        </template>
      </template>
      <template v-else>--</template>
    </template>
    <template v-if="dataType === 6">
      <span class="text">true：</span>
      <span>{{ data_object.trueValue }}</span>
      <span>，</span>
      <span class="text">false：</span>
      <span>{{ data_object.falseValue }}</span>
    </template>
    <template v-if="dataType === 9">整数类型Int64的UTC时间戳(毫秒)</template>
  </div>
</template>

<script>
import {reactive, toRefs, watch, defineComponent} from "vue";

export default defineComponent({
  name: "DataValueDefinition",
  props: {
    dataType: {
      type: [Number, String],
      default: ""
    },
    dataObject: {
      type: String,
      default: ""
    }
  },
  setup(props) {

    const that = reactive({
      data_object: {}
    })

    const watchDataObject = watch([() => props.dataObject], ([newDataObject]) => {
      try {
        that.data_object = JSON.parse(newDataObject);
      } catch (e) {
        that.data_object = {};
      }
    }, {deep: true, immediate: true})

    return {...toRefs(that), watchDataObject}
  }
})
</script>

<style lang="scss" scoped>
.dataValueDefinition {

}
</style>
