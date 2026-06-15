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
            <span class="unit" v-if="item.unit">{{ item.unit }}</span>
          </div>
        </div>
      </el-col>
    </template>
  </el-row>
</template>

<script lang="ts">
import {reactive, toRefs, defineComponent} from "vue";

export default defineComponent({
  name: "DeviceInformation",
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
        {name:"设备ID：",fieldName:"id"},
        {name:"设备名称：",fieldName:"deviceName"},
        {name:"序列号：",fieldName:"deviceNumber"},
        {name:"通信状态：",fieldName:"txStatus",filterName:"deviceStatus"},
        {name:"接入类型：",fieldName:"accessType",filterName:"accessType"},
        {name:"运营状态：",fieldName:"operateStatus",filterName:"operateStatus"},
        {name:"父级节点：",fieldName:"parentName"},
        {name:"更新信息：",fieldName:"updateTime",nextFieldName:"updateName"},
        {name:"创建信息：",fieldName:"createTime",nextFieldName:"createName"},
        {name:"设备描述：",fieldName:"deviceDesc"},
      ]
    })

    return {...toRefs(that) }
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
