<template>
  <Dialog v-model:isVisible="dialog_visible" :listLoading="listLoading" :manualEnterClose="false" :title="titleName" disabledLoading width="540" @confirm="clickConfirmBut">
    <template v-slot:content>
      <div class="dialog_main">
        <el-form ref="formDialogRef" :disabled="listLoading" :model="formDialog" :rules="rules" label-width="130px">
          <el-form-item label="启动方式：" required>
            <el-radio-group v-model="formDialog.type">
              <template v-for="(item,index) in typeArray" :key="index">
                <el-radio :value="item.id">{{ $filters.chargingDisText(item.name,operateType) }}</el-radio>
              </template>
            </el-radio-group>
          </el-form-item>
          <el-form-item v-if="formDialog.type === 1">
            <el-col :span="9">
              <el-form-item prop="timeType">
                <el-button-group>
                  <template v-for="(item,index) in timeTypeArray" :key="index">
                    <el-button :type="formDialog.timeType === item.id ? 'primary' : ''" @click="formDialog.timeType = item.id">
                      <span>{{ item.name }}</span>
                    </el-button>
                  </template>
                </el-button-group>
              </el-form-item>
            </el-col>
            <el-col :span="14">
              <el-form-item prop="time">
                <el-time-picker v-model="formDialog.time" :disabled="!formDialog.timeType" placeholder="请选择预约时间" format="HH:mm" value-format="HH:mm"
                                :disabled-hours="()=>pickerOptions.disabledHours(formDialog.timeType)"
                                :disabled-minutes="(hour)=>pickerOptions.disabledMinutes(hour,formDialog.timeType)"/>
              </el-form-item>
            </el-col>
          </el-form-item>
          <el-form-item label="启动策略：" prop="strategy">
            <template v-for="(item,index) in strategyArray" :key="index">
              <el-col :span="24">
                <el-form-item :prop="item.fieldName">
                  <div class="content_list" :class="{ active_class: formDialog.strategy === item.id }">
                    <el-input v-model="formDialog[item.fieldName]" :disabled="!item.id || formDialog.strategy !== item.id" :placeholder="item.placeholder">
                      <template #suffix>
                        <span class="unit" v-if="item.id >= 2">{{ item.unit }}</span>
                        <div v-else>
                          <span class="iconfont icon-duihao" v-if="formDialog.strategy === item.id"></span>
                        </div>
                      </template>
                      <template #prefix>
                        <div class="prefix pointer" @click.stop="formDialog.strategy = item.id">
                          <span>{{ $filters.chargingDisText(item.name,operateType) }}</span>
                        </div>
                      </template>
                    </el-input>
                  </div>
                </el-form-item>
              </el-col>
            </template>
          </el-form-item>
        </el-form>
      </div>
      <Loading v-if="loadingVisible" ref="loadingRef" title="正在启动中..." @changeEvent="changeEvent"></Loading>
    </template>
  </Dialog>
</template>

<script>
import {useStore} from "vuex";
import {ElMessage} from 'element-plus';
import Loading from "@/components/Dialog/Loading.vue";
import {integer0to100, integer0to9999999} from "@/utils/validate";
import {pileStartDisAndCharging} from "@/api/operationManagement/CsPileGunRunningStatus";
import {pickerOptionsHoursAndMinutesAndSeconds,getDaysFromCurrentTime} from "@/utils/dateTime";
import {getCurrentInstance, onMounted, reactive, toRefs, watch, defineComponent, ref, computed} from "vue";

export default defineComponent({
  name: "StartDisAndChargingDialog",
  components: {Loading},
  props: {
    isVisible: {
      type: Boolean,
      default: false
    },
    // 1： 充 2：放
    operateType: {
      type: [Number, String],
      default: 1
    },
    returnDataInfo: {
      type: Object,
      default: ()=>{
         return { };
      }
    },
  },
  setup(props) {
    const store = useStore();
    const {emit} = getCurrentInstance();

    const userInfo = computed(() => {
      return store.state.app.userInfo;
    });

    const validateStrategy = (rule, value, callback) => {
      if (!value && value !== 0) {
        callback(new Error("请选择启动策略"));
      } else {
        callback();
      }
    };

    const validateStrategySoc = (rule, value, callback) => {
      if (!integer0to100(value) && that.formDialog.strategy === 2) {
        callback(new Error("请输入正确的Soc"));
      } else {
        callback();
      }
    };

    const validateStrategyEcy = (rule, value, callback) => {
      if (!integer0to9999999(value) && that.formDialog.strategy === 3) {
        callback(new Error("请输入正确的电量"));
      } else {
        callback();
      }
    };

    const validateTimeType = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请选择"));
      } else {
        callback();
      }
    };

    const validateTime = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请选择预约时间"));
      } else {
        callback();
      }
    };

    const that = reactive({
      listLoading: false,
      titleName: "启动充电",
      loadingVisible: false,
      dialog_visible: props.isVisible,
      formDialog: { type: 0,strategy: 0 },
      timeTypeArray: [{id: 1, name: "今日"}, {id: 2, name: "次日"}],
      pickerOptions: pickerOptionsHoursAndMinutesAndSeconds(3),
      typeArray: [{id: 0, name: "立即充/放电"}, {id: 1, name: "预约充/放电"}],
      strategyArray: [
        {id: 0, name: "自动充满",unit:"",placeholder:""},
        {id: 2, name: "定Soc",unit:"%",placeholder:"请输入0～100",fieldName:"strategySoc"},
        {id: 3, name: "定电量",unit:"度",placeholder:"请输入",fieldName:"strategyEcy"}
      ],

      rules: {
        strategy: [{required: true, trigger: "change", validator: validateStrategy}],
        strategySoc: [{required: true, trigger: "change", validator: validateStrategySoc}],
        strategyEcy: [{required: true, trigger: "change", validator: validateStrategyEcy}],
        timeType: [{required: true, trigger: "change", validator: validateTimeType}],
        time: [{required: true, trigger: "change", validator: validateTime}],
      }
    });

    const loadingRef = ref(null);
    const formDialogRef = ref(null);
    const clickConfirmBut = () => {
      formDialogRef.value.validate((valid) => {
        if (valid) {
          that.listLoading = true;
          that.loadingVisible = true;
          let form_dialog = JSON.parse(JSON.stringify(that.formDialog));
          if(form_dialog.strategy === 0)form_dialog.strategyCfg = 100;
          if(form_dialog.strategy === 2)form_dialog.strategyCfg = form_dialog.strategySoc;
          if(form_dialog.strategy === 3)form_dialog.strategyCfg = form_dialog.strategyEcy;

          // 预约充放电
          if(form_dialog.type === 1){
            let timer = getDaysFromCurrentTime(form_dialog.timeType - 1);
            form_dialog.clockingTime = timer + ' ' + form_dialog.time + ':00'; // 获取当前预约时间
          }

          // console.log(form_dialog);
          pileStartDisAndCharging({...form_dialog,
            starter: 2, // 发起者 1-App 2-第三方平台 3-电卡 4-VIN码 5-电桩屏幕强制启动 6-有序控制
            isStore: true, //是否存控制记录
            accountType: 3, //账户类型 1-充/放电卡 2-VIN码 3-手机号
            runMode: props.operateType - 1, // 运行模式 -1-未知 0-充电模式 1-放电模式
            gunCode: props.returnDataInfo.gunCode, //充电桩枪编号
            pileCode: props.returnDataInfo.pileCode, //充电桩编号
            accountData: userInfo.value.phone || "18888888888", //账户数据
          }).then(res=>{
            let returnDataInfo = res.data ? res.data : {};
            if(returnDataInfo.result !== 0){
              that.listLoading = false;
              that.loadingVisible = false;
              ElMessage({ type: "warning", message: returnDataInfo.failDetailReason ?? "启动失败，请检查设备。", showClose: true });
              return;
            }
            // 启动成功
            loadingRef.value.setLoadingFun(returnDataInfo);
          }).catch(()=>{
            that.listLoading = false;
            that.loadingVisible = false;
            emit("close");
          });
        }
      });
    };

    // 启动成功回调
    const changeEvent = ()=>{
      that.dialog_visible = false;
      ElMessage({ type: "success", message: "操作成功", showClose: true });
    };

    const initParamConfigFun = () => {
      that.titleName = props.operateType === 1 ? '启动充电' : '启动放电';
    };

    const watchVisible = watch([() => props.isVisible], ([newVisible]) => {
      that.dialog_visible = newVisible;
    });

    const watchDialogVisible = watch([() => that.dialog_visible], ([newDialogVisible]) => {
      emit("update:isVisible", newDialogVisible);
    });

    onMounted(() => {
      initParamConfigFun();
    });

    return {...toRefs(that), watchDialogVisible, watchVisible, initParamConfigFun, clickConfirmBut, formDialogRef, userInfo, changeEvent, loadingRef};
  }
});
</script>

<style lang="scss" scoped>
.el-col{
  margin-bottom: 16px;

  .content_list{
    width: 100%;
    //background: #F1F3FA;

    .prefix{
      color: #FFFFFF;
      font-size: 14px;
      background: #081A30;
      padding: 0 19px 0 12px;
      border-radius: var(--el-border-radius-base);;
    }

    .icon-duihao{
      color: #007FEB;
    }

    :deep(.el-input) {
      --el-input-bg-color: none;

      .el-input__wrapper{
        padding: 1px 11px 1px 1px;
      }
    }
  }

  .active_class{
    :deep(.el-input) {
      --el-border-color: #007FEB;
      //--el-input-bg-color: #FFFFFF;
      //--el-border-color-hover: #007FEB;
      //--el-disabled-bg-color: #FFFFFF;
      //--el-disabled-border-color: #007FEB;

      .prefix{
        background: rgba(7, 156, 235,0.6);
      }
    }
  }

  &:first-child{
    .prefix{
      background: none;
    }
  }
}
</style>