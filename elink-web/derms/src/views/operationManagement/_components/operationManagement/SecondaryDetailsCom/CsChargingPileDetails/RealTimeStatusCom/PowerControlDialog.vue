<template>
  <Dialog v-model:isVisible="dialog_visible" width="540" :manualEnterClose="false" :listLoading="listLoading" :title="titleName" disabledLoading @confirm="clickConfirmBut">
    <template v-slot:content>
      <div class="dialog_main">
        <el-form ref="formDialogRef" :model="formDialog" :rules="rules" label-width="130px" :disabled="listLoading">
          <el-form-item label="当前状态：">
            <div class="padding_class">
              <span class="gun_status" :class="'gun_status' + returnDataInfo.gunWorkState">
                <span>{{ $filters.gunWorkState(returnDataInfo.gunWorkState) }}</span>
              </span>
            </div>
          </el-form-item>
          <el-form-item label="功率控制：" required>
            <el-col :span="11">
              <el-form-item>
                <el-button-group>
                  <template v-for="(item,index) in powerTypeArray" :key="index">
                    <el-button :type="formDialog.powerType === item.id ? 'primary' : ''" @click="formDialog.powerType = item.id">
                      <span>{{ item.name }}</span>
                    </el-button>
                  </template>
                </el-button-group>
              </el-form-item>
            </el-col>
            <el-col :span="13">
              <el-form-item prop="power">
                <el-input :disabled="formDialog.powerType === 1" placeholder="请输入" v-model="formDialog.outPower" />
              </el-form-item>
            </el-col>
          </el-form-item>
        </el-form>
      </div>
    </template>
  </Dialog>
</template>

<script>
import {ElMessage} from "element-plus";
import {onlyNum} from "@/utils/validate";
import {pilePowerCtrl} from "@/api/operationManagement/CsPileGunRunningStatus";
import {getCurrentInstance, onMounted, reactive, toRefs, watch, defineComponent} from "vue";

export default defineComponent({
  name: "PowerControlDialog",
  props: {
    isVisible: {
      type: Boolean,
      default: false
    },
    titleName: {
      type: String,
      default: "功率控制"
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

    const validateOutPower = (rule, value, callback) => {
      if (!onlyNum(value)) {
        callback(new Error("请输入正确的功率值"));
      } else {
        callback();
      }
    };

    const that = reactive({
      listLoading: false,
      formDialog: { powerType: 1 },
      dialog_visible: props.isVisible,
      powerTypeArray:[{id:1,name:"默认值"},{id:2,name:"自定义"}],

      rules:{
        outPower: [{required: true, trigger: "change", validator: validateOutPower}],
      }
    });

    // 功率控制
    const clickConfirmBut = () => {
      that.listLoading = true;
      pilePowerCtrl({
        ctrlType: 0, // 控制类型 0-绝对控制 1-相对控制
        gunCode: props.returnDataInfo.gunCode, //充电桩枪编号
        pileCode: props.returnDataInfo.pileCode, //充电桩编号
        runMode: props.returnDataInfo.gunWorkState - 1, // 运行模式 0-充电模式 1-放电模式
        outPower: that.formDialog.powerType === 1 ? props.returnDataInfo.outPower : that.formDialog.outPower,//输出功率
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
.padding_class{
  padding-left: 8px;

  .gun_status{
    color: #E66AE3;
    font-size: 14px;
  }

  .gun_status1{
    color: #41CB4A;
  }
  .gun_status2{
    color: #FF9C02;
  }
  .gun_status3{
    color: #007FEB;
  }
  .gun_status4{
    color: #EDA300;
  }
  .gun_status5{
    color: #FF1515;
  }
  .gun_status6{
    color: #666666;
  }
}
</style>