<template>
  <div class="pelBindVariableListCom" v-loading="listLoading">
    <template
      v-if="(variableType === 1 && systemVarList.length) || (variableType === 2 && functionVarList.length) || (variableType === 3 && variableArray.length)">
      <el-radio-group v-model="variable_id" @change="changeVariableIdFun">
        <template v-if="variableType === 1">
          <el-radio v-for="item in systemVarList" :key="item.nodeCode" :value="item.nodeCode">{{
            item.nodeName }}</el-radio>
        </template>
        <template v-if="variableType === 2">
          <el-radio v-for="item in functionVarList" :key="item.functionLogo" :value="item.functionLogo">{{
            item.functionName }}
            <el-input style="width: 40%"
              v-if="(selectTypeData.dataType == 8 && selectTypeData.functionLogo === item.functionLogo) || (dataType == 8 && variable_id === item.functionLogo)"
              v-model="selectTypeDataValue" placeholder="请输入" @input="(val) => handleInput(val, item)"></el-input>
          </el-radio>
        </template>
        <template v-if="variableType === 3">
          <el-radio v-for="item in variableArray" :key="item" :value="item.name">{{ item.name }}</el-radio>
        </template>
      </el-radio-group>
    </template>
    <null-data v-else words="无变量列表数据"></null-data>
  </div>
</template>

<script lang="ts">
import { useMeta2dStore } from '@/stores/index';

import { getCurrentInstance, onMounted, reactive, ref, toRefs, watch, defineComponent, computed } from "vue";
import {
  findFunctionListByDeviceId,
  findGraphVariableByGraphId,
  findComputeNodeListByDeviceId
} from "@/api/2DVisualization/2d_artworkEditor";

export default defineComponent({
  name: "PelBindVariableListCom",
  props: {
    // 1 系统变量   2  设备功能点   3  自定义变量
    variableType: {
      type: Number,
      default: 1
    },
    // 站点或者设备id
    siteOrDeviceId: {
      type: [String, Number],
      default: ""
    },
    variableId: {
      type: [String, Number],
      default: ""
    },
    variableName: {
      type: String,
      default: ""
    },
    // 变量类型 1-设备类型 2-站点类型
    varType: {
      type: Number,
      default: 1
    },
    dataType: {
      type: String,
      default: ""
    }

  },
  emits: ["update:variableId", "update:variableName"],
  setup (props) {
    const meta2dStore = useMeta2dStore();
    const { emit } = getCurrentInstance();

    // 当前图纸数据
    const canvasMeta2dData = computed(() => {
      return meta2dStore.canvasMeta2dData;
    });
    console.log(props.variableId);
    const that = reactive({
      listLoading: false,
      variableArray: [], // 关联变量数据
      systemVarList: [], // 系统变量数据
      functionVarList: [], // 功能点数据
      dataType: props.dataType,
      variable_id: props.variableId.includes('@') ? props.variableId.split('@')[0] : props.variableId,
      selectTypeData: {},
      selectTypeDataValue: props.variableId.includes('@') ? props.variableId.split('@')[1] : "",
    })
    console.log(that.variable_id, props.siteOrDeviceId,props.dataType, '9999');


    // 输入框输入事件
    const handleInput = (val, item) => {
      console.log(val, item, 'val');
      that.selectTypeData = item;
      that.selectTypeDataValue = val.replace(/[^0-2]/g, '')
    }


    const changeVariableIdFun = () => {

      let variableName = "";
      that.selectTypeDataValue = "";
      let dataType = ''
      if (props.variableType === 1) {
        let findItem = that.systemVarList.find(item => item.nodeCode === that.variable_id);
        console.log(findItem);
        variableName = findItem?.nodeName;
      }

      if (props.variableType === 2) {
        let findItem = that.functionVarList.find(item => item.functionLogo === that.variable_id);
        variableName = findItem?.functionName;
        dataType = findItem?.dataType;
        that.selectTypeData = findItem;
         emit("update:dataType", dataType);

      }

      if (props.variableType === 3) {
        let findItem = that.variableArray.find(item => item.name === that.variable_id);
        variableName = findItem?.name;
      }

      emit("update:variableName", variableName);
      console.log(that.variable_id, 'variableName');
      emit("update:variableId", that.variable_id);
     
    }

    // 根据图模id查询关联变量数据
    const queryGraphVariableByGraphId = () => {

      findGraphVariableByGraphId({ graphId: canvasMeta2dData.value.id }).then(res => {

        that.variableArray = res.data ? res.data : [];
      })
    }

    // 根据站点/设备id查询系统变量列表
    const querySystemVarListByDeviceId = () => {
      that.listLoading = true;
      // queryType: props.varType,timer: new Date()
      findComputeNodeListByDeviceId({ deviceId: props.siteOrDeviceId }).then(res => {
        that.systemVarList = res.data ? res.data : [];
        that.listLoading = false;
      }).catch(() => {
        that.listLoading = false;
      })
    }

    // 根据站点/设备id查询功能点数据
    const queryFunctionListByDeviceId = () => {
      that.listLoading = true;

      findFunctionListByDeviceId({ deviceId: props.siteOrDeviceId, queryType: props.varType, timer: new Date() }).then(res => {

        that.functionVarList = res.data ? res.data : [];
        that.listLoading = false;
      }).catch(() => {
        that.listLoading = false;
      })
    }

    // 请求数据
    const queryVarListByDeviceIdFun = () => {
      if (props.variableType === 1) querySystemVarListByDeviceId();
      if (props.variableType === 2) queryFunctionListByDeviceId();

    }
    // emit("update:selectTypeDataValue", that.selectTypeDataValue);
    const watchselectType = watch(() => that.selectTypeDataValue, (newVal) => {
      emit("update:selectTypeDataValue", newVal);
    })

    // 获取系统变量列表
    const watchVariableId = watch(() => props.variableId, (newVariableId) => {
      that.variable_id = newVariableId;
    }, { deep: true });

    const watchSiteOrDeviceId = watch([() => props.siteOrDeviceId, () => props.variableType], ([newSiteOrDeviceId, newVariableType]) => {
      if (newSiteOrDeviceId) queryVarListByDeviceIdFun();
    }, { deep: true })

    onMounted(() => {
      queryGraphVariableByGraphId();
    })

    return {
      ...toRefs(that), queryGraphVariableByGraphId, changeVariableIdFun, watchSiteOrDeviceId, querySystemVarListByDeviceId,
      watchVariableId, watchselectType, handleInput, queryFunctionListByDeviceId, queryVarListByDeviceIdFun
    }
  }
})

</script>

<style lang="scss" scoped>
:deep(.el-radio) {
  width: 100%;
  margin-right: 0;
}
</style>