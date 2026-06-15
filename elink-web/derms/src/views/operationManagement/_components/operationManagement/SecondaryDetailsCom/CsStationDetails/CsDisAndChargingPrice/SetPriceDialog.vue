<template>
  <Dialog v-model:isVisible="dialog_visible" :listLoading="listLoading" :manualEnterClose="false" :title="titleName" disabledLoading width="70vw" @confirm="clickConfirmBut">
    <template v-slot:content>
      <div class="dialog-main">
        <el-form ref="formDialogRef" :model="formDialog" :rules="rules" label-width="130px">
          <el-form-item label="下发时间：" prop="takeType">
            <el-radio-group v-model="formDialog.takeType">
              <template v-for="(item,index) in takeTypeArray" :key="index">
                <el-radio :value="item.id">{{ item.name }}</el-radio>
              </template>
            </el-radio-group>
          </el-form-item>
          <template v-if="formDialog.takeType === 2">
            <el-form-item prop="takeTime">
              <el-date-picker v-model="formDialog.takeTime" type="datetime" value-format="YYYY-MM-DD HH:mm" format="YYYY-MM-DD HH:mm" style="max-width: 380px"
                              :disabled-date="(time)=> pickerOptions.disabledDate(time,formDialog.takeTime)" :disabled-hours="pickerOptions.disabledHours"
                              :disabled-minutes="pickerOptions.disabledMinutes" :disabled-seconds="pickerOptions.disabledSeconds" placeholder="请选择时间"/>
            </el-form-item>
          </template>
          <el-form-item label="设备：" prop="deviceType">
            <buttons-tabs customClass="customBcAndColor" :tabsArray="deviceTypeArray" v-model:tabsIndex="formDialog.deviceType"></buttons-tabs>
          </el-form-item>
          <el-form-item label="定价方式：" prop="fixedType">
            <buttons-tabs customClass="connectClass" :tabsArray="fixedTypeArray" v-model:tabsIndex="formDialog.fixedType"></buttons-tabs>
          </el-form-item>

          <template v-if="formDialog.fixedType === 1">
            <el-form-item label="配置价格：" required>
              <div class="content_config" v-if="chargingType === 1">
                <el-col :span="11">
                  <el-form-item prop="electMoney">
                    <el-input v-model="formDialog.electMoney" placeholder="请输入电费"/>
                  </el-form-item>
                </el-col>
                <el-col :span="2">
                  <div class="flex-jc-ai-center">+</div>
                </el-col>
                <el-col :span="11">
                  <el-form-item prop="serviceMoney">
                    <el-input v-model="formDialog.serviceMoney" placeholder="请输入服务费">
                      <template #suffix>
                        <span class="unit_text">元/度</span>
                      </template>
                    </el-input>
                  </el-form-item>
                </el-col>
              </div>
              <el-form-item v-else prop="electMoney">
                <el-input v-model="formDialog.electMoney" placeholder="请输入电费">
                  <template #suffix>
                    <span class="unit_text">元/度</span>
                  </template>
                </el-input>
              </el-form-item>
            </el-form-item>
          </template>
          <template v-if="formDialog.fixedType === 2">
            <el-form-item label="配置价格：" prop="periodTimeList">
              <div class="content_table">
                <div class="content_table_top">
                  <DayPartingSelect v-model:periodTimeList="formDialog.periodTimeList" ref="dayPartingSelectRef"></DayPartingSelect>
                </div>
                <div class="content_table_bottom">
                  <el-table :data="formDialog.periodTimeList" :max-height="tableMaxHeight">
                    <el-table-column label="时段" width="110">
                      <template #default="{ row }">
                        <div class="period_type" :class="'period_type' + row.periodType">
                          <span>{{ $filters.periodType(row.periodType) }}</span>
                        </div>
                      </template>
                    </el-table-column>
                    <el-table-column label="时间">
                      <template #default="{ row }">
                        <template v-if="row.children && row.children.length">
                          <template v-for="(item,index) in row.children" :key="index">
                            <span>{{ item.startTime}}</span>
                            <span>~</span>
                            <span>{{ item.actualEndTime }}</span>
                            <span v-if="index + 1 < row.children.length">、</span>
                          </template>
                        </template>
                        <template v-else>--</template>
                      </template>
                    </el-table-column>
                    <el-table-column :label="chargingType === 1 ? '电费/服务费' : '电费' " width="380">
                      <template #default="{ row }">
                        <div class="content_config" v-if="chargingType === 1">
                          <el-col :span="11">
                            <el-input v-model="row.electMoney" placeholder="请输入电费"/>
                          </el-col>
                          <el-col :span="2">
                            <div class="flex-jc-ai-center">+</div>
                          </el-col>
                          <el-col :span="11">
                            <el-input v-model="row.serviceMoney" placeholder="请输入服务费">
                              <template #suffix>
                                <span class="unit_text">元/度</span>
                              </template>
                            </el-input>
                          </el-col>
                        </div>
                        <template v-else>
                          <el-input v-model="row.electMoney" placeholder="请输入电费">
                            <template #suffix>
                              <span class="unit_text">元/度</span>
                            </template>
                          </el-input>
                        </template>
                      </template>
                    </el-table-column>
                  </el-table>
                </div>
              </div>
            </el-form-item>
          </template>
        </el-form>
      </div>
    </template>
  </Dialog>
</template>
<script lang="ts">
import {ElMessage} from "element-plus";
import {num0to9999999dot} from "@/utils/validate";
import DayPartingSelect from "./SetPriceDialog/DayPartingSelect.vue";
import {getNowDateAll, pickerOptionsDHSMTimer} from "@/utils/dateTime";
import {findPriceDetailsById, saveChargerPriceInfo} from "@/api/operationManagement/CsStationDetails";
import {getCurrentInstance, reactive, toRefs, watch, defineComponent, ref, onMounted, nextTick} from "vue";

export default defineComponent({
  name: "SetPriceDialog",
  components:{ DayPartingSelect },
  props: {
    isVisible: {
      type: Boolean,
      default: false
    },
    // 1： 充   2；放
    chargingType: {
      type: [String, Number],
      default: 1
    },
    siteId: {
      type: [Number, String],
      default: ""
    },
    // 复制时段使用
    activePricingId: {
      type: [Number, String],
      default: ""
    },
  },
  setup(props) {

    const {emit} = getCurrentInstance();
    const validateTakeType = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请选择下发时间类型"));
      } else {
        callback();
      }
    };

    const validateTakeTime = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请选择下发时间"));
      } else {
        callback();
      }
    };

    const validateDeviceType = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请选择下发设备类型"));
      } else {
        callback();
      }
    };

    const validateFixedType = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请选择定价方式"));
      } else {
        callback();
      }
    };

    const validateElectMoney = (rule, value, callback) => {
      if (!num0to9999999dot(value)) {
        callback(new Error("请输入正确的电费"));
      } else {
        callback();
      }
    };

    const validateServiceMoney = (rule, value, callback) => {
      if (!num0to9999999dot(value)) {
        callback(new Error("请输入正确的服务费"));
      } else {
        callback();
      }
    };

    const validatePeriodTimeList = (rule, value, callback) => {
      if (!value || !value.length) {
        callback(new Error("请添加分时段配置"));
      } else {
        callback();
      }
    };

    const that = reactive({
      listLoading: false,
      titleName: "添加价格",
      dialog_visible: props.isVisible,
      pickerOptions: pickerOptionsDHSMTimer(3),
      formDialog: { fixedType: 1,deviceType: 3,takeType: 1 },
      takeTypeArray: [{id:1,name:"立即生效"},{id:2,name:"自定义时间"}],
      fixedTypeArray: [{id:1,name:"全天同价"},{id:2,name:"分时段定价"}],
      deviceTypeArray: [{id:3,name:"全部"},{id:1,name:"直流"},{id:2,name:"交流"}],

      tableMaxHeight: 320,
      rules:{
        takeType: [{required: true, trigger: "change", validator: validateTakeType}],
        takeTime: [{required: true, trigger: "change", validator: validateTakeTime}],
        deviceType: [{required: true, trigger: "change", validator: validateDeviceType}],
        fixedType: [{required: true, trigger: "change", validator: validateFixedType}],
        electMoney: [{required: true, trigger: "change", validator: validateElectMoney}],
        serviceMoney: [{required: true, trigger: "change", validator: validateServiceMoney}],
        periodTimeList: [{required: true, trigger: "change", validator: validatePeriodTimeList}],
      }
    });

    //
    const formDialogRef = ref(null);
    const clickConfirmBut = () => {
      formDialogRef.value.validate((valid) => {
        if (valid) {
          that.listLoading = true;
          let chargerPriceDtos = [];
          let form_dialog = JSON.parse(JSON.stringify(that.formDialog));
          form_dialog.priceType = props.chargingType; //价格类型 1-充电 2-放电
          if(form_dialog.takeTime)form_dialog.takeTime = getNowDateAll(form_dialog.takeTime,{ isSs: false });

          // 全天同价
          if(form_dialog.fixedType === 1){
            let chargerPriceConfig = {
              periodType:6,
              endTime:"23:59",
              startTime: "00:00",
              electMoney:form_dialog.electMoney,
              serviceMoney:form_dialog.serviceMoney
            };
            chargerPriceDtos.push(chargerPriceConfig);
          }

          // 分时段定价
          if(form_dialog.fixedType === 2){

            // if(!that.periodTimeList || !that.periodTimeList.length){
            //   ElMessage({type: "error", showClose: true, message: "请添加分时段配置！"});
            //   that.listLoading = false;
            //   return
            // }

            for(let i = 0;i < form_dialog.periodTimeList.length;i++){

              if(!num0to9999999dot(form_dialog.periodTimeList[i].electMoney)){
                ElMessage({type: "error", showClose: true, message: `请输入正确的电费！（第${ i + 1 }行）`});
                that.listLoading = false;
                return;
              }

              if(props.chargingType === 1){
                if(!num0to9999999dot(form_dialog.periodTimeList[i].serviceMoney)){
                  ElMessage({type: "error", showClose: true, message: `请输入正确的服务费！（第${ i + 1 }行）`});
                  that.listLoading = false;
                  return;
                }
              }

              for(let j = 0;j < form_dialog.periodTimeList[i].children.length;j++){
                chargerPriceDtos.push({
                  periodType: form_dialog.periodTimeList[i].periodType,
                  electMoney: form_dialog.periodTimeList[i].electMoney,
                  serviceMoney: form_dialog.periodTimeList[i].serviceMoney,
                  startTime: form_dialog.periodTimeList[i].children[j].startTime,
                  endTime: form_dialog.periodTimeList[i].children[j].actualEndTime,
                });
              }
            }
          }

          delete form_dialog.periodTimeList;
          saveChargerPriceInfo({ ...form_dialog,chargerPriceDtos: chargerPriceDtos,siteId: props.siteId }).then(() => {
            emit("changeEvent");
            that.dialog_visible = false;
            ElMessage({type: "success", showClose: true, message: "操作成功！"});
          }).catch(() => {
            that.listLoading = false;
          });
        }
      });
    };

    const dayPartingSelectRef = ref(null);
    const initParamConfigFun = ()=>{
      if(props.chargingType === 2){
        that.formDialog.deviceType = 1;
        that.deviceTypeArray =  [{id:1,name:"直流"}];  // {id:3,name:"全部"},
      }

      // 获取时段计费信息配置
      if(props.activePricingId){
        that.listLoading = true;
        findPriceDetailsById({ pirceId:props.activePricingId }).then(res=>{
          let returnDataInfo = res.data ? res.data : {};
          // console.log(returnDataInfo);

          let form_dialog = JSON.parse(JSON.stringify(that.formDialog));
          form_dialog.fixedType = returnDataInfo.fixedType;
          form_dialog.deviceType = returnDataInfo.deviceType;
          that.formDialog = JSON.parse(JSON.stringify(form_dialog));

          nextTick(()=>{
            // 全天同价
            if(that.formDialog.fixedType === 1){
              if(returnDataInfo.priceConfigList && returnDataInfo.priceConfigList.length){
                that.formDialog.electMoney = returnDataInfo.priceConfigList[0].electMoney;
                that.formDialog.serviceMoney = returnDataInfo.priceConfigList[0].serviceMoney;
              }
            }

            // 分时段定价
            if(that.formDialog.fixedType === 2){
              if(returnDataInfo.priceConfigList && returnDataInfo.priceConfigList.length){
                dayPartingSelectRef.value.echoPeriodTimeConfigFun(returnDataInfo.priceConfigList);
              }
            }
            that.listLoading = false;
          });

        }).catch(()=>{
          that.listLoading = false;
        });
      }
    };

    const watchVisible = watch([() => props.isVisible], ([newVisible]) => {
      that.dialog_visible = newVisible;
    });

    const watchDialogVisible = watch([() => that.dialog_visible], ([newDialogVisible]) => {
      emit("update:isVisible", newDialogVisible);
    });

    onMounted(()=>{
      initParamConfigFun();
    });

    return {...toRefs(that), watchDialogVisible, watchVisible, clickConfirmBut, formDialogRef, initParamConfigFun, dayPartingSelectRef};
  }
});
</script>

<style lang="scss" scoped>
.content_config{
  display: flex;
  align-items: center;
  border-radius: 8px;

  .el-input{
    --el-border-color: none !important;
  }
}

.unit_text{
  color: #FFFFFF;
  font-size: 14px;
}

.content_table{
  width: 100%;
  padding: 16px;
  border-radius: 8px;
  box-sizing: border-box;
  background: rgba(0, 67, 125, 0.15);

  .period_type{
    width: fit-content;
    height: 32px;
    border-radius: 8px;
    background: #36CFC2;
    padding: 0 16px;
    line-height: 32px;
    font-size: 14px;
    color: #FFFFFF;
  }

  .period_type1{
    background: #FB6868;
  }
  .period_type2{
    background: #FD9449;
  }
  .period_type3{
    background: #56ADF7;
  }
  .period_type4{
    background: #6DCF36;
  }
}
</style>