<template>
  <el-form :model="formInline">
    <el-form-item label="App展示：">
      <el-radio-group v-model="formInline.appShow" @change="changeAppShowFun">
        <el-radio v-for="item in appShowArray" :key="item.id" :label="item.id">{{ item.name }}</el-radio>
      </el-radio-group>
    </el-form-item>
  </el-form>
</template>

<script>
import {ElMessage, ElMessageBox} from "element-plus";
import {updateSiteSetUp} from "@/api/siteCenter/stationDetails";
import {reactive, toRefs, defineComponent, watch, getCurrentInstance } from "vue";

export default defineComponent({
  name: "OtherSetting",
  props: {
    returnDataInfo: {
      type: Object,
      default: () => {
        return {}
      }
    }
  },
  emits: ["changEvent"],
  setup(props) {
    const {emit} = getCurrentInstance();

    const that = reactive({
      formInline: {},
      appShowArray: [{id: 1, name: "展示"}, {id: 2, name: "不展示"}]
    })

    const changeAppShowFun = (val)=>{
      let highlightText = val === 1 ? "展示" : "不展示";
      ElMessageBox.confirm(`确定更改状态为（<span class="deleteName">${ highlightText }</span>）吗？`, "提示", {
        dangerouslyUseHTMLString: true,confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning',
        customClass: "deleteMsgBoxClass", showClose:false, closeOnClickModal: false,
        beforeClose: (action, instance, done)=>{
          if (action === 'confirm') {
            instance.confirmButtonLoading = true;
            instance.confirmButtonText = '正在更改...';
            updateSiteSetUp({ ...that.formInline }).then(()=>{
              done();
              instance.confirmButtonLoading = false;
            }).catch(() => {
              instance.confirmButtonText = '确定';
              instance.confirmButtonLoading = false;
            });
          } else {
            done();
          }
        }
      }).then(() => {
        emit("changEvent");
        ElMessage({ type: "success", showClose: true, message: "更改成功！" });
      }).catch(() => {
        that.formInline = JSON.parse(JSON.stringify(props.returnDataInfo ?? {}));
        console.log("取消删除！");
      });
    }

    const watchReturnDataInfo = watch(() => props.returnDataInfo, (newReturnDataInfo) => {
      that.formInline = JSON.parse(JSON.stringify(newReturnDataInfo ?? {}));
    }, {deep: true, immediate: true})

    return {...toRefs(that), watchReturnDataInfo, changeAppShowFun}
  }
})
</script>

<style lang="scss" scoped>
</style>