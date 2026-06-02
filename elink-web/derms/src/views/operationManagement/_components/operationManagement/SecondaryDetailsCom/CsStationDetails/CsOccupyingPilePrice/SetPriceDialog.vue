<template>
  <Dialog v-model:isVisible="dialog_visible" :listLoading="listLoading" :manualEnterClose="false" :title="titleName" disabledLoading width="60vw" @confirm="clickConfirmBut">
    <template v-slot:content>
      <div class="dialog-main">
        <el-form ref="formDialogRef" :model="formDialog" :rules="rules" label-width="130px">
          <el-form-item label="设备：" prop="deviceType">
            <ButtonsTabs :tabsArray="deviceTypeArray" v-model:tabsIndex="formDialog.deviceType"></ButtonsTabs>
          </el-form-item>
          <el-form-item label="免占桩时长：" prop="avoidDuration">
            <el-input v-model="formDialog.avoidDuration" placeholder="请输入">
              <template #suffix>
                <span class="unit_text">分钟</span>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item label="定价方式：" prop="configType">
            <ButtonsTabs :tabsArray="configTypeArray" v-model:tabsIndex="formDialog.configType"></ButtonsTabs>
          </el-form-item>

          <template v-if="formDialog.configType === 1">
            <el-form-item prop="chargePrice">
              <el-input v-model="formDialog.chargePrice" placeholder="请输入费用">
                <template #suffix>
                  <span class="unit_text">元</span>
                </template>
              </el-input>
            </el-form-item>
          </template>
          <template v-if="formDialog.configType === 2">
            <el-form-item prop="configPriceInfoStr">
              <div class="content_table">
                <el-table :data="formDialog.configPriceInfoStr" :max-height="tableMaxHeight">
                  <el-table-column label="超时时长">
                    <template #default="{ row,$index }">
                      <template v-if="$index === 0">超时大于等于0分钟</template>
                      <template v-else>
                        <div class="flex-ai-center">
                          <el-col :span="6"><span class="text">超时大于等于</span></el-col>
                          <el-col :span="18">
                            <el-input-number v-model="row.timeoutDuration" placeholder="请输入" controls-position="right"
                                             :min="formDialog.configPriceInfoStr[$index - 1]&&formDialog.configPriceInfoStr[$index - 1].timeoutDuration ? formDialog.configPriceInfoStr[$index - 1].timeoutDuration : -Infinity"
                                             :max="formDialog.configPriceInfoStr[$index + 1] ? formDialog.configPriceInfoStr[$index + 1].timeoutDuration - 1 : Infinity"></el-input-number>
                          </el-col>
                        </div>
                      </template>
                    </template>
                  </el-table-column>
                  <el-table-column label="收费价格">
                    <template #default="{ row }">
                      <el-input class="selectAndInput" v-model="row.chargePrice" placeholder="请输入">
                        <template #prefix>
                          <el-select v-model="row.chargeType" placeholder="请选择" style="width: 110px;">
                            <template v-for="item in chargeTypeArray" :key="item.id" >
                              <el-option :label="item.name" :value="item.id"></el-option>
                            </template>
                          </el-select>
                        </template>
                        <template #suffix>
                          <span class="unit_text" v-if="row.chargeType === 1">元/分钟</span>
                          <span class="unit_text" v-else>元</span>
                        </template>
                      </el-input>
                    </template>
                  </el-table-column>
                  <el-table-column width="120">
                    <template #header>
                      <span class="header_text" @click="clickAddLadderBut">+添加阶梯</span>
                    </template>
                    <template #default="{ $index }">
                      <el-link :underline="false" @click="clickDeleteBut($index)">删除</el-link>
                    </template>
                  </el-table-column>
                </el-table>
              </div>
            </el-form-item>
          </template>
        </el-form>
      </div>
    </template>
  </Dialog>
</template>
<script>
import {ElMessage} from "element-plus";
import {getNowDateAll} from "@/utils/dateTime";
import {num0to9999999dot, onlyNum} from "@/utils/validate";
import {saveOccupyPilePriceInfo} from "@/api/operationManagement/CsStationDetails";
import {getCurrentInstance, reactive, toRefs, watch, defineComponent, ref, onMounted} from "vue";

export default defineComponent({
  name: "SetPriceDialog",
  props: {
    isVisible: {
      type: Boolean,
      default: false
    },
    siteId: {
      type: [Number, String],
      default: ""
    },
    activeFormDialog: {
      type: Object,
      default: ()=>{
        return { };
      }
    },
  },
  setup(props) {

    const {emit} = getCurrentInstance();
    const validateDeviceType = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请选择下发设备类型"));
      } else {
        callback();
      }
    };

    const validateConfigType = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请选择定价方式"));
      } else {
        callback();
      }
    };

    const validateAvoidDuration = (rule, value, callback) => {
      if (value && !num0to9999999dot(value)) {
        callback(new Error("请输入正确的免占桩时长"));
      } else {
        callback();
      }
    };

    const validateChargePrice = (rule, value, callback) => {
      if (!num0to9999999dot(value)) {
        callback(new Error("请输入正确的数值"));
      } else {
        callback();
      }
    };

    const validateConfigPriceInfoStr = (rule, value, callback) => {
      if (!value || !value.length) {
        callback(new Error("请添加计费策略"));
      } else {
        callback();
      }
    };

    const that = reactive({
      listLoading: false,
      tableMaxHeight: 280,
      titleName: "设置占桩价格",
      dialog_visible: props.isVisible,
      formDialog: { configType: 1,deviceType: 3 },
      chargeTypeArray: [{id:1,name:"分钟计费"},{id:2,name:"固定金额"}],
      configTypeArray: [{id:1,name:"固定价格"},{id:2,name:"阶梯价格"}],
      deviceTypeArray: [{id:3,name:"全部"},{id:1,name:"直流"},{id:2,name:"交流"}],

      rules:{
        deviceType: [{required: true, trigger: "change", validator: validateDeviceType}],
        configType: [{required: true, trigger: "change", validator: validateConfigType}],
        chargePrice: [{required: true, trigger: "change", validator: validateChargePrice}],
        avoidDuration: [{required: false, trigger: "change", validator: validateAvoidDuration}],
        configPriceInfoStr: [{required: true, trigger: "change", validator: validateConfigPriceInfoStr}],
      }
    });

    // 添加修改占桩价格信息
    const formDialogRef = ref(null);
    const clickConfirmBut = () => {
      formDialogRef.value.validate((valid) => {
        if (valid) {
          that.listLoading = true;
          let form_dialog = JSON.parse(JSON.stringify(that.formDialog));
          form_dialog.priceType = props.chargingType; //价格类型 1-充电 2-放电
          if(form_dialog.takeTime)form_dialog.takeTime = getNowDateAll(form_dialog.takeTime,{ isSs: false });

          // 固定价格
          if(form_dialog.configType === 1)form_dialog.configPriceInfoStr = [{chargePrice:form_dialog.chargePrice}];

          // 阶梯价格
          if(form_dialog.configType === 2){

            for(let i = 0;i < form_dialog.configPriceInfoStr.length;i++){
              if( i === 0)form_dialog.configPriceInfoStr[i].timeoutDuration = 0;

              if(!onlyNum(form_dialog.configPriceInfoStr[i].timeoutDuration)){
                ElMessage({type: "error", showClose: true, message: `请输入正确的时长(第${i + 1}行)`});
                that.listLoading = false;
                return;
              }

              if(!form_dialog.configPriceInfoStr[i].chargeType){
                ElMessage({type: "error", showClose: true, message: `请选择收费类型(第${i + 1}行)`});
                that.listLoading = false;
                return;
              }

              if(!num0to9999999dot(form_dialog.configPriceInfoStr[i].chargePrice)){
                ElMessage({type: "error", showClose: true, message: `请输入正确的价格(第${i + 1}行)`});
                that.listLoading = false;
                return;
              }
            }
          }

          saveOccupyPilePriceInfo({ ...form_dialog,siteId: props.siteId }).then(() => {
            emit("changeEvent");
            that.dialog_visible = false;
            ElMessage({type: "success", showClose: true, message: "操作成功！"});
          }).catch(() => {
            that.listLoading = false;
          });
        }
      });
    };

    const clickAddLadderBut = ()=>{
      if(!that.formDialog.configPriceInfoStr)that.formDialog.configPriceInfoStr = [];

      // 获取最后一项数据
      let timeoutDuration = 0;
      let findLastItem = that.formDialog.configPriceInfoStr[that.formDialog.configPriceInfoStr.length - 1];
      if(findLastItem){
        timeoutDuration = findLastItem.timeoutDuration + 1;
        if(that.formDialog.configPriceInfoStr.length > 1 && !onlyNum(findLastItem.timeoutDuration)){
          ElMessage({type: "error", showClose: true, message: `请先正确的输入最后一行超时时长!`});
          return;
        }
      }

      that.formDialog.configPriceInfoStr.push({ timeoutDuration: timeoutDuration });
    };

    // 删除某一项费用
    const clickDeleteBut = (index)=>{
      that.formDialog.configPriceInfoStr.splice(index,1);
    };

    const initParamConfigFun = ()=>{
      if(props.activeFormDialog.id){
        let formDialog = JSON.parse(JSON.stringify(props.activeFormDialog));

        // 固定价格
        if(formDialog.configType === 1){
          formDialog.chargePrice = formDialog.configPriceInfoList[0].chargePrice;
        }

        for(let key in formDialog)if(formDialog[key])that.formDialog[key] = formDialog[key];
        // that.formDialog = JSON.parse(JSON.stringify(formDialog));

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

    return {...toRefs(that), watchDialogVisible, watchVisible, clickConfirmBut, formDialogRef, initParamConfigFun,clickAddLadderBut,clickDeleteBut};

  }
});
</script>

<style lang="scss" scoped>
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

  .header_text{
    color: #007FEB;
    cursor: pointer;
  }
}
</style>