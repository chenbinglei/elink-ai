<template>
  <div class="selectSiteAndDevice">
    <el-form ref="formDialogRef" :model="formDialog" :rules="rules" label-width="100px">

      <!--      计算节点-->
      <template v-if="activeVarInfo.dataSource === 1">
        <el-form-item v-if="activeVarInfo.varType === 1" label="选择设备:" prop="deviceId">
          <el-tree-select v-model="formDialog.deviceId" :data="deviceArray" :indent="0" :props="deviceProps" :render-after-expand="false"
                          check-strictly class="leftArrowClass" default-expand-all filterable node-key="id" placeholder="请选择设备">
          </el-tree-select>
        </el-form-item>

        <el-form-item v-if="activeVarInfo.varType === 2" label="选择站点:" prop="deviceId">
          <el-select v-model="formDialog.deviceId" clearable placeholder="请选择站点">
            <el-option v-for="item in deviceArray" :key="item.id" :label="item.name" :value="item.id"></el-option>
          </el-select>
        </el-form-item>
      </template>

      <!--      模型功能点-->
      <template v-if="activeVarInfo.dataSource === 2">
        <el-form-item label="设备分类：" prop="typeId">
          <el-tree-select v-model="formDialog.typeId" :data="handleMenuArray" :indent="0" :props="treeProps" :render-after-expand="false"
                          class="leftArrowClass" default-expand-all filterable @change="queryModelNameListByTypeId" />
        </el-form-item>
        <el-form-item label="选择模型：" prop="deviceId">
          <el-select v-model="formDialog.deviceId" :disabled="!formDialog.typeId" placeholder="请选择选择模型">
            <el-option v-for="item in modelArray" :key="item.id" :label="item.modelName" :value="item.id"></el-option>
          </el-select>
        </el-form-item>
      </template>

    </el-form>
  </div>
</template>

<script>
import {setTreeData} from "@/utils";
import {getAssetTypeList} from "@/api/modelCenter/modelManagement";
import {getSiteDeviceTreeList} from "@/api/deviceCenter/deviceList";
import {getModelNameListByTypeId} from "@/api/deviceCenter/deviceList";
import {reactive, toRefs, ref, onMounted, defineComponent} from "vue";

export default defineComponent({
  name: "SelectSiteAndDevice",
  props: {
    formDialog: {
      type: Object,
      default: () => {
        return {}
      }
    },
    activeVarInfo: {
      type: Object,
      default: () => {
        return {}
      }
    },
  },
  setup(props) {

    const formDialogRef = ref(null);
    const validateDeviceId = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请选择关联数据"));
      } else {
        callback();
      }
    };

    const validateTypeId = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请选择设备分类"));
      } else {
        callback();
      }
    };

    const that = reactive({
      modelArray: [],
      deviceArray: [],
      handleMenuArray: [],
      formDialog: props.formDialog,
      deviceProps: {value: "id", label: "name", children: "children"},
      treeProps: {value: "id", label: "typeName", children: "children"},
      rules: {
        typeId: [{required: true, trigger: "change", validator: validateTypeId}],
        deviceId: [{required: true, trigger: "change", validator: validateDeviceId}]
      }
    });

    // 获取站点设备树形结构
    const findSiteDeviceTreeList = () => {
      getSiteDeviceTreeList({type: props.activeVarInfo.varType === 2 ? 1 : 0}).then(res => {
        let data_array = res.data ? res.data : [];
        if (props.activeVarInfo.varType === 2) that.deviceArray = data_array;
        if (props.activeVarInfo.varType === 1) {
          for (let i = 0; i < data_array.length; i++) data_array[i].disabled = data_array[i].type === 1;
          that.deviceArray = setTreeData(data_array);
        }
      })
    }

    // 获取设备类型列表
    const queryAssetTypeList = ()=>{
      getAssetTypeList({ timer: new Date(),pageName: "AddDeviceDialog" }).then(res=>{
        let assetTypeList = res.data ? res.data : [];
        that.handleMenuArray = setTreeData(assetTypeList);
        if(that.formDialog.typeId)queryModelNameListByTypeId(false);
      })
    }

    // 根据分类id获取模型名称列表
    const queryModelNameListByTypeId = (isClearId = true)=>{
      getModelNameListByTypeId({ typeId: that.formDialog.typeId }).then(res=>{
        that.modelArray = res.data;
        if(isClearId) that.formDialog.deviceId = "";
      }).catch(()=>{
        if(isClearId) that.formDialog.deviceId = "";
      })
    }

    onMounted(() => {
      props.activeVarInfo.dataSource === 1 ? findSiteDeviceTreeList() : queryAssetTypeList();
    })

    return {...toRefs(that), formDialogRef, findSiteDeviceTreeList, queryAssetTypeList,queryModelNameListByTypeId}
  }
})
</script>

<style lang="scss" scoped>
.selectSiteAndDevice {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>
