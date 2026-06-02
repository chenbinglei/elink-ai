<template>
  <Dialog v-model:isVisible="dialog_visible" width="540" :manualEnterClose="false" :listLoading="listLoading" :title="titleName" disabledLoading @confirm="clickConfirmBut">
    <template v-slot:content>
      <div class="dialog_main" v-loading="listLoading">
        <div class="alter_icon"><span class="iconfont icon-zc-tishi"></span></div>
        <div class="alter_text">确定结束当前进行中的订单吗？</div>
      </div>
    </template>
  </Dialog>
</template>

<script>
import {ElMessage} from "element-plus";
import {pileStopDisAndCharging} from "@/api/operationManagement/CsPileGunRunningStatus";
import {getCurrentInstance, onMounted, reactive, toRefs, watch, defineComponent} from "vue";

export default defineComponent({
  name: "EndChargingDialog",
  props: {
    isVisible: {
      type: Boolean,
      default: false
    },
    titleName: {
      type: String,
      default: "提示"
    },
    returnDataInfo: {
      type: Object,
      default: ()=>{
        return { };
      }
    },
  },
  setup(props) {
    const {emit} = getCurrentInstance();

    const that = reactive({
      listLoading: false,
      dialog_visible: props.isVisible,
    });

    // 停止充放电/停止预约
    const clickConfirmBut = () => {
      that.listLoading = true;
      pileStopDisAndCharging({
        isStore: true, //是否存控制记录
        gunCode: props.returnDataInfo.gunCode, //充电桩枪编号
        pileCode: props.returnDataInfo.pileCode, //充电桩编号
        serialNum: props.returnDataInfo.serialNum,
        type: props.returnDataInfo.gunWorkState === 8 ? 2 : 1, // 停止方式 1-停止充/放电 2-取消预约
      }).then(()=>{
        that.dialog_visible = false;
        ElMessage({ type: "success", message: "操作成功", showClose: true });
        emit("close");
      }).catch(()=>{
        that.listLoading = false;
      });
    };

    const initParamConfigFun = () => {};

    const watchVisible = watch([() => props.isVisible], ([newVisible]) => {
      that.dialog_visible = newVisible;
    });

    const watchDialogVisible = watch([() => that.dialog_visible], ([newDialogVisible]) => {
      emit("update:isVisible", newDialogVisible);
    });

    onMounted(() => {});

    return {...toRefs(that), watchDialogVisible, watchVisible, initParamConfigFun, clickConfirmBut};
  }
});
</script>

<style lang="scss" scoped>
.dialog_main{
  display: flex;
  flex-direction: column;
  align-items: center;

  .iconfont{
    color: #007FEB;
    font-size: 28px;
  }

  .alter_text{
    color: #FFFFFF;
    font-size: 14px;
    margin-top: 16px;
  }
}
</style>