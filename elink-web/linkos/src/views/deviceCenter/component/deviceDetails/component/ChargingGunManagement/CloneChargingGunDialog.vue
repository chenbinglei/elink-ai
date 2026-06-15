<template>
  <Dialog v-model:isVisible="dialog_visible" :listLoading="listLoading" :manualEnterClose="false" :title="titleName" disabledLoading width="580" @confirm="saveDialog">
    <template v-slot:content>
      <div v-loading="listLoading" class="dialog-main">
        <el-form ref="formDialogRef" :model="formDialog" :rules="rules" label-width="110px">
          <el-form-item label="枪数据：" prop="deviceGunIds">
            <el-select v-model="formDialog.deviceGunIds" placeholder="请选择枪数据" multiple filterable collapse-tags>
              <el-option v-for="item in deviceGunList" :key="item.id" :label="item.gunName" :value="item.id"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="目标设备：" prop="deviceIds">
            <el-select v-model="formDialog.deviceIds" placeholder="请选择枪数据" multiple filterable collapse-tags>
              <template v-for="item in deviceIdList" :key="item.id">
                <el-option :label="item.name" :value="item.id" :disabled="activeDeviceId === item.id"></el-option>
              </template>
            </el-select>
          </el-form-item>
        </el-form>
      </div>
    </template>
  </Dialog>
</template>

<script lang="ts">
import {ElMessage} from "element-plus";
import {getSiteDeviceTreeList} from "@/api/deviceCenter/deviceList";
import {saveAllDeviceGun, findDeviceGunListByDeviceId} from "@/api/deviceCenter/deviceDetails";
import {getCurrentInstance, reactive, ref, toRefs, watch, defineComponent, onMounted} from "vue";

export default defineComponent({
  name: "CloneChargingGunDialog",
  props: {
    isVisible: {
      type: Boolean,
      default: false
    },
    activeDeviceId: {
      type: [String, Number],
      default: ''
    }
  },
  setup(props) {
    const {emit} = getCurrentInstance();

    const validateDeviceGunIds = (rule, value, callback) => {
      if (!value || !value.length) {
        callback(new Error("请选择需要克隆的枪数据"));
      } else {
        callback();
      }
    };

    const validateDeviceIds = (rule, value, callback) => {
      if (!value || !value.length) {
        callback(new Error("请选择目标设备"));
      } else {
        callback();
      }
    };

    const that = reactive({
      formDialog: {},
      deviceIdList: [],
      deviceGunList: [],
      listLoading: false,
      titleName: "克隆枪数据",
      dialog_visible: props.isVisible,
      rules: {
        deviceGunIds: [{required: true, trigger: "change", validator: validateDeviceGunIds }],
        deviceIds: [{required: true, trigger: "change", validator: validateDeviceIds }],
      }
    })

    const formDialogRef = ref(null);
    const saveDialog = () => {
      formDialogRef.value.validate((valid) => {
        if (valid) {
          that.listLoading = true;
          let formDialog = JSON.parse(JSON.stringify(that.formDialog));
          saveAllDeviceGun({ ...formDialog }).then(()=>{
            that.dialog_visible = false;
            ElMessage({ type: "success", showClose: true, message: "操作成功！" });
          }).catch(()=>{
            that.listLoading = false;
          })
        }
      })
    }

    // 根据模型id查询模型拓扑节点列表
    const queryDeviceGunListByDeviceId = ()=>{
      findDeviceGunListByDeviceId({ deviceId: props.activeDeviceId,timer: new Date() }).then(res=>{
        that.deviceGunList = res.data ? res.data : [];
      })
    }

    // 获取站点设备树形结构
    const querySiteDeviceTreeList = ()=>{
      getSiteDeviceTreeList({ type: 0,timer: new Date() }).then(res=>{
        let newDeviceGunList = [];
        let deviceGunList = res.data ? res.data : [];
        for(let i = 0;i < deviceGunList.length;i++){
          if(deviceGunList[i].typeId >= 28 && deviceGunList[i].typeId <= 30){
            if(props.activeDeviceId !== deviceGunList[i].id) newDeviceGunList.push(deviceGunList[i]);
          }
        }
        that.deviceIdList = JSON.parse(JSON.stringify(newDeviceGunList));
      })
    }

    const watchVisible = watch([() => props.isVisible], ([newVisible]) => {
      that.dialog_visible = newVisible;
    });

    const watchDialogVisible = watch([() => that.dialog_visible], ([newDialogVisible]) => {
      emit("update:isVisible", newDialogVisible);
    });

    onMounted(()=>{
      queryDeviceGunListByDeviceId();
      querySiteDeviceTreeList();
    })

    return {...toRefs(that), watchVisible, watchDialogVisible, formDialogRef, saveDialog, queryDeviceGunListByDeviceId, querySiteDeviceTreeList}
  }
})
</script>

<style lang="scss" scoped>

</style>