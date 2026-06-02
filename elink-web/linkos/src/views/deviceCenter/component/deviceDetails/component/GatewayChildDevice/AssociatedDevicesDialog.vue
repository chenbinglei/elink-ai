<template>
  <Dialog v-model:isVisible="dialog_visible" :listLoading="listLoading" :manualEnterClose="false" :title="titleName" disabledLoading
          width="480" @confirm="clickConfirmBut">
    <template v-slot:content>
      <div class="content_body">
        <el-form ref="formDialogRef" :model="formDialog" :rules="rules" label-width="90px">
          <el-form-item label="设备：" prop="subDeviceIds">
            <el-select v-model="formDialog.subDeviceIds" multiple filterable max-collapse-tags="1" placeholder="请选择">
              <el-option v-for="item in deviceArray" :key="item.id" :label="item.deviceName" :value="item.id"/>
            </el-select>
          </el-form-item>
        </el-form>
      </div>
    </template>
  </Dialog>
</template>
<script>
import {ElMessage} from "element-plus";
import {findSiteSubDeviceList,batchUpdateGatewaySubDevice} from "@/api/deviceCenter/deviceList";
import {getCurrentInstance, onMounted, reactive, ref, toRefs, watch, defineComponent} from "vue";

export default defineComponent({
  name: "AssociatedDevicesDialog",
  props: {
    isVisible: {
      type: Boolean,
      default: false
    },
    activeDeviceId: {
      type: [Number, String],
      default: ""
    }
  },
  setup(props) {
    const {emit} = getCurrentInstance();

    const validateDeviceIds = (rule, value, callback) => {
      if (!value || !value.length) {
        callback(new Error("请选择关联设备"));
      } else {
        callback();
      }
    };

    const that = reactive({
      formDialog: {},
      deviceArray: [],
      listLoading: false,
      titleName: "关联设备",
      dialog_visible: props.isVisible,
      rules: {
        subDeviceIds: [{required: true, trigger: "change", validator: validateDeviceIds}],
      }
    })


    // 批量修改网关子设备数据
    const formDialogRef = ref(null);
    const clickConfirmBut = () => {
      formDialogRef.value.validate((valid) => {
        if (valid) {
          that.listLoading = true;
          batchUpdateGatewaySubDevice({gatewayId:props.activeDeviceId,type:1,...that.formDialog }).then(()=>{
            emit("changeEvent");
            that.dialog_visible = false;
            ElMessage({ type: "success", message: "操作成功", showClose: true });
          }).catch(()=>{
            that.listLoading = false;
          })
        }
      })
    }

    const initParamConfigFun = () => {
      findSiteSubDeviceList({ gatewayId: props.activeDeviceId }).then(res=>{
        that.deviceArray = res.data;
      })
    }

    const watchVisible = watch([() => props.isVisible], ([newVisible]) => {
      that.dialog_visible = newVisible;
    });

    const watchDialogVisible = watch([() => that.dialog_visible], ([newDialogVisible]) => {
      emit("update:isVisible", newDialogVisible);
    });

    onMounted(() => {
      initParamConfigFun();
    })

    return {...toRefs(that), watchDialogVisible, watchVisible, initParamConfigFun, formDialogRef, clickConfirmBut}
  }
})
</script>
<style lang="scss" scoped>

</style>