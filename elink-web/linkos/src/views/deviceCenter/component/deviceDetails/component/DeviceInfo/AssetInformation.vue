<template>
  <el-row :gutter="12">
    <template v-for="(item,index) in list" :key="index">
      <el-col :xs="24" :md="12" :lg="8">
        <div class="content_list">
          <div class="content_list_left">{{ item.name }}</div>
          <div class="content_list_right">
            <span v-if="item.filterName">{{ $filters[item.filterName](deviceInfo[item.fieldName]) }}</span>
            <span v-else>
              <span>{{ $filters.moreData(deviceInfo[item.fieldName]) }}</span>
              <span v-if="item.nextFieldName">，{{ $filters.moreData(deviceInfo[item.nextFieldName]) }}</span>
            </span>
          </div>
        </div>
      </el-col>
    </template>
  </el-row>
</template>

<script lang="ts">
import {reactive, toRefs, defineComponent} from "vue";

export default defineComponent({
  name: "AssetInformation",
  props: {
    deviceInfo: {
      type: Object,
      default: () => {
        return {}
      }
    }
  },
  setup() {

    const that = reactive({
      list:[
        {name:"所属场站：",fieldName:"siteName"},
        {name:"站点ID：",fieldName:"siteId"},
      ]
    })

    return {...toRefs(that)}
  }
})
</script>

<style scoped lang="scss">
.content_list{
  margin-bottom: 12px;
  display: flex;
  align-items: center;

  .content_list_left{
    font-size: 14px;
    color: #BBBBBB;
  }
  .content_list_right{
    font-size: 14px;
    color: #242424;
  }
}
</style>
