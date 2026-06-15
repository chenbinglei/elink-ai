<template>
  <div class="content_body">
    <el-table v-loading="listLoading" :data="list" :max-height="contentMainMaxHeight" border stripe>
      <el-table-column align="center" label="序号" type="index" width="80"></el-table-column>
      <el-table-column align="center" label="属性名称">
        <template #default="{ row }">{{ $filters.moreData(row.reaName) }}</template>
      </el-table-column>
      <el-table-column align="center" label="属性类型">
        <template #default="{ row }">{{ $filters.reaType(row.reaType) }}</template>
      </el-table-column>
      <el-table-column align="center" label="值" show-overflow-tooltip>
        <template #default="{ row }">{{ $filters.moreData(filterValueFun(row)) }}</template>
      </el-table-column>
      <el-table-column align="center" label="单位">
        <template #default="{ row }">{{ $filters.moreData(row.unit) }}</template>
      </el-table-column>
      <el-table-column align="center" label="操作">
        <template #default="{ row }">
          <el-link :underline="false" @click="clickOperateBut(1, row)">编辑</el-link>
        </template>
      </el-table-column>
    </el-table>

    <add-device-dialog v-if="editDeviceInfoVisible" v-model:isVisible="editDeviceInfoVisible" :titleName="titleName" :activeDeviceId="activeDeviceId"
                       :fieldEditName="fieldEditName" :editingType="editingType" @changeEvent="queryDeviceReaListById"></add-device-dialog>
  </div>
</template>

<script lang="ts">
import { useAppStore } from '@/stores/index';

import {setTimerSplitTallyFun} from "@/utils/dateTime";
import {findDeviceReaListById} from "@/api/deviceCenter/deviceList";
import {computed, onMounted, reactive, toRefs, defineComponent} from "vue";
import AddDeviceDialog from "@/views/deviceCenter/component/deviceList/AddDeviceDialog.vue";

export default defineComponent({
  name: "ExtendedAttr",
  components: {AddDeviceDialog},
  props: {
    activeDeviceId: {
      type: [Number, String],
      default: ""
    }
  },
  setup(props) {

    const appStore = useAppStore();
    const contentMainMaxHeight = computed(() => {
      return appStore.contentMainMaxHeight;
    });

    const that = reactive({
      list: [],
      listLoading: false, // 表格加载
      tableMaxHeight: 300,

      editingType: 2,
      fieldEditName: "",
      titleName: "编辑设备信息",
      editDeviceInfoVisible: false,
    })

    const queryDeviceReaListById = ()=>{
      that.listLoading = true;
      findDeviceReaListById({ deviceId: props.activeDeviceId }).then(res=>{
        let list = res.data ? res.data : [];

        for(let i = 0;i < list.length;i++){
          list[i].extraValue = list[i].extraValue ? JSON.parse(list[i].extraValue) : {};
        }

        that.list = list;
        that.listLoading = false;
      }).catch(()=>{
        that.listLoading = false;
      })
    }

    const clickOperateBut = (operateType,row)=>{
      if(operateType === 1){
        that.fieldEditName = row.fieldName;
        that.editDeviceInfoVisible = true;
      }
    }

    const filterValueFun = (fieldInfo)=>{
      let showText = fieldInfo.value;

      // 选项
      if(fieldInfo.reaType === 3){
        if(fieldInfo.extraValue.enumArray && fieldInfo.extraValue.enumArray.length){
          // 多选
          if(fieldInfo.extraValue.multiple){
            //   console.log(fieldValue);
            //   console.log(fieldInfo.extraValue.enumArray);
            if(showText && showText.length){
              for(let i = 0;i < showText.length;i++){
                let findItem = fieldInfo.extraValue.enumArray.find(item=> item.id === showText[i]);
                if(findItem) showText[i] = findItem.name;
              }
              showText = showText.join('，');
            }
          } else {
            let findItem = fieldInfo.extraValue.enumArray.find(item => item.id === fieldInfo.value);
            if(findItem) showText = findItem.name;
          }
        }
      }

      // 位置
      if(fieldInfo.reaType === 4) showText = fieldInfo.value ? fieldInfo.value.address : "";

      // 开关
      if(fieldInfo.reaType === 5){
        showText = fieldInfo.value ? fieldInfo.extraValue.trueValue : fieldInfo.extraValue.falseValue;
      }

      // 时间
      if(fieldInfo.reaType === 6)showText = setTimerSplitTallyFun(fieldInfo.value,fieldInfo.extraValue.splitTally,fieldInfo.extraValue.timeFormat);

      return showText
    }

    onMounted(()=>{
      queryDeviceReaListById();
    })

    return {...toRefs(that), queryDeviceReaListById, filterValueFun, contentMainMaxHeight, clickOperateBut}

  }
})
</script>


<style lang="scss" scoped>
.content_body{
  height: 100%;
  padding: 16px 12px 0 12px;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;

  .tableContent{
    padding: 0 !important;
  }
}
</style>
