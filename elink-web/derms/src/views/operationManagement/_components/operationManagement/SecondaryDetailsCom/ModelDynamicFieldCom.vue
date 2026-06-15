<template>
  <div class="modelDynamicFieldCom">
    <el-form ref="formDialogRef" :model="formDialog" :rules="rules" label-width="150px">
      <el-row :gutter="10">
        <el-col :span="12" v-if="sourceType === 1 && scenarioType !== 0">
          <el-form-item label="能源系统名称：" prop="systemName">
            <el-input v-model="formDialog.systemName" maxlength="32" placeholder="请输入能源系统名称" show-word-limit/>
          </el-form-item>
        </el-col>
        <template v-if="(model_field_array && model_field_array.length) || scenarioType !== 0">
          <template v-for="(item,index) in model_field_array" :key="index">
            <!--                位置-->
            <template v-if="item.reaType === 4">
              <el-col :span="24" style="margin-bottom: 18px">
                <el-form-item :label="item.reaName + '：'" :required="item.required">
                  <el-col :span="10">
                    <el-form-item :prop="item.fieldName + '.longitude'">
                      <el-input v-model="formDialog[item.fieldName]['longitude']" placeholder="经度" @change="queryAreaAddressByCoordinates(item.fieldName)" />
                    </el-form-item>
                  </el-col>
                  <el-col :span="10">
                    <el-form-item :prop="item.fieldName + '.latitude'">
                      <el-input v-model="formDialog[item.fieldName]['latitude']" placeholder="纬度" @change="queryAreaAddressByCoordinates(item.fieldName)" />
                    </el-form-item>
                  </el-col>
                  <el-col :span="4">
                    <div class="flex jc-end">
                      <el-button type="primary" @click="clickCoordinatePicking(1,item.fieldName)">坐标拾取</el-button>
                    </div>
                  </el-col>
                </el-form-item>
              </el-col>
              <el-col :span="24" style="margin-bottom: 18px">
                <el-form-item label="所在城市：">
                  <el-col :span="8">
                    <el-input v-model="formDialog[item.fieldName]['province']" disabled placeholder="省" />
                  </el-col>
                  <el-col :span="8">
                    <el-input v-model="formDialog[item.fieldName]['city']" disabled placeholder="市" />
                  </el-col>
                  <el-col :span="8">
                    <el-input v-model="formDialog[item.fieldName]['county']" disabled placeholder="区" />
                  </el-col>
                </el-form-item>
              </el-col>
              <el-col :span="24" style="margin-bottom: 18px">
                <el-form-item label="详细地址：" :prop="item.fieldName + '.address'">
                  <el-input v-model="formDialog[item.fieldName]['address']" :placeholder="'请输入' + item.reaName + '的详细地址'" type="textarea" :rows="2"></el-input>
                </el-form-item>
              </el-col>
            </template>
            <el-col :span="24" style="margin-bottom: 0px" v-else>
              <el-form-item :label="item.reaName + '：'" :prop="item.fieldName">
                <!--              数值-->
                <template v-if="item.reaType === 1">
                  <el-input-number v-model="formDialog[item.fieldName]" :placeholder="'请输入' + item.reaName" controls-position="right"
                                   :min="item.extraValue.minValue" :max="item.extraValue.maxValue"/>
                </template>
                <!--                文字-->
                <template v-if="item.reaType === 2">
                  <el-input v-model="formDialog[item.fieldName]" :placeholder="'请输入' + item.reaName" type="text"></el-input>
                </template>
                <!--                选项-->
                <template v-if="item.reaType === 3">
                  <el-select v-model="formDialog[item.fieldName]" :placeholder="'请选择' + item.reaName" :multiple="item.extraValue.multiple" collapse-tags :max-collapse-tags="1">
                    <el-option v-for="itam in item.extraValue.enumArray" :key="itam.id" :label="itam.name" :value="itam.id"></el-option>
                  </el-select>
                </template>

                <!--                开关-->
                <template v-if="item.reaType === 5">
                  <el-switch v-model="formDialog[item.fieldName]" :active-text="item.extraValue.trueValue" :inactive-text="item.extraValue.falseValue"/>
                </template>
                <!--                时间-->
                <template v-if="item.reaType === 6">
                  <el-date-picker :type="setDatePickTypeFun(item.extraValue)" v-model="formDialog[item.fieldName]" :placeholder="'请选择' + item.reaName"
                                  :value-format="setDatePickFormatFun(item.extraValue)" :format="setDatePickFormatFun(item.extraValue)" />
                    <!--                  :disabled-date="(time)=> pickerOptions.disabledDate(time,formDialog[item.fieldName])"-->
                    <!--                  :disabled-hours="pickerOptions.disabledHours" :disabled-minutes="pickerOptions.disabledMinutes"-->
                    <!--                  :disabled-seconds="pickerOptions.disabledSeconds"-->
                </template>
                <!--                文本-->
                <template v-if="item.reaType === 7">
                  <el-input v-model="formDialog[item.fieldName]" :placeholder="'请输入' + item.reaName" type="textarea" :rows="2"></el-input>
                </template>
              </el-form-item>
            </el-col>
          </template>
        </template>
        <template v-else><null-data words="无扩展属性"></null-data></template>
      </el-row>
    </el-form>

    <LocationAndRegionDialog v-if="locationAndRegionVisible" v-model:isVisible="locationAndRegionVisible" :latAndLongitude="latAndLongitude" @mapEvents="mapEvents"/>
  </div>
</template>

<script lang="ts">
import {Location} from '@element-plus/icons-vue';
import {pickerOptionsMinutesTimer} from "@/utils/dateTime";
import {commonCharName,hasWhiteSpace} from "@/utils/validate";
import {onMounted, reactive, ref, toRefs, defineComponent, watch} from "vue";
import LocationAndRegionDialog from "@/components/component/LocationAndRegionDialog.vue";
import {getAreaAddressByCoordinates} from "@/api/operationManagement/CsStationDetails";

export default defineComponent({
  name: "ModelDynamicFieldCom",
  components: {LocationAndRegionDialog},
  props: {
    // 0-电站属性 1-光伏属性 2-储能属性 3-充电桩属性 4-用能属性 5-配电属性
    scenarioType: {
      type: Number,
      default: 0
    },
    // 1:  来自新增站点弹框  2：新增能源信息弹框  3: 新增设备弹框
    sourceType: {
      type: Number,
      default: 1
    },
    // 动态列表数据
    modelFieldArray:{
      type: Array,
      default: []
    }
  },
  setup(props) {

    const validateSystemName = (rule, value, callback) => {
      if (!value || !commonCharName(value)) {
        callback(new Error("请输入正确的能源系统名称"));
      } else {
        callback();
      }
    };

    const that = reactive({
      Location,
      formDialog: {},
      model_field_array: [],
      pickerOptions: pickerOptionsMinutesTimer(),

      // 地图选点使用 ------》
      operateType: 1, // 1: 选择经纬度  2：选择区域
      activeFieldName: "",
      latAndLongitude: "",
      locationAndRegionVisible: false,
      //《 ------ 地图选点使用

      rules: {
        systemName: [{required: true, trigger: "change", validator: validateSystemName }],
      }
    });

    // 根据模型id获取模型编辑字段列表
    const queryModelFieldUpdateListByModelId = (model_field_array = [])=>{
      let newModelFieldArray = [];
      for(let i = 0;i < model_field_array.length;i++){
        if(model_field_array[i].fieldName && !that.rules[model_field_array[i].fieldName]){
          if(model_field_array[i].required){

            // 位置信息字段校验方式
            if(model_field_array[i].reaType === 4){
              that.rules[model_field_array[i].fieldName + '.longitude'] = locationInfoVerificationFun(model_field_array[i],'经度');
              that.rules[model_field_array[i].fieldName + '.latitude'] = locationInfoVerificationFun(model_field_array[i],'纬度');
              that.rules[model_field_array[i].fieldName + '.address'] = locationInfoVerificationFun(model_field_array[i],'详细地址');
            } else {
              // 默认字段校验方式
              that.rules[model_field_array[i].fieldName] = [{
                trigger: "change",
                required: model_field_array[i].required,
                validator: (rule, value, callback)=>{
                  if ((!value && value !== 0) || (hasWhiteSpace(value) && model_field_array[i].reaType !== 6) ) {
                    callback(new Error(`请选择或输入正确的${ model_field_array[i].reaName }`));
                  } else {
                    callback();
                  }
                }
              }];
            }
          }
        }

        // 位置信息字段
        if(model_field_array[i].reaType === 4 && !that.formDialog[model_field_array[i].fieldName]){
          that.formDialog[model_field_array[i].fieldName] = {};
        }

        // 额外属性解析
        model_field_array[i].extraValue = model_field_array[i].extraValue ? JSON.parse(model_field_array[i].extraValue) : {};
        newModelFieldArray.push(model_field_array[i]);
      }

      // console.log(newModelFieldArray);
      that.model_field_array = JSON.parse(JSON.stringify(newModelFieldArray));
      // that.listLoading = false;
    };
    
    // 位置信息校验
    const locationInfoVerificationFun = (data,text)=>{
      return [{
        required: true, 
        trigger: "change", 
        validator: (rule, value, callback) => {
          if (!value) {
            callback(new Error(`请输入${ data.reaName }的${ text }`));
          } else {
            callback();
          }
        }
      }];
    };

    // 清除字段属性值
    const clearFormDialogValueFun = ()=>{
      if(that.model_field_array && that.model_field_array.length){
        for(let i = 0;i < that.model_field_array.length;i++){
          if(that.model_field_array[i].fieldName){
            delete that.rules[that.model_field_array[i].fieldName];
            delete that.formDialog[that.model_field_array[i].fieldName];
          }
        }
      }
      that.model_field_array = [];
    };

    // 打开地图选点弹框
    const clickCoordinatePicking = (operateType,fieldName)=>{
      let latAndLongitude = "";
      that.operateType = operateType;
      that.activeFieldName = fieldName;
      if(that.formDialog[fieldName]['longitude'] && that.formDialog[fieldName]['latitude']){
        latAndLongitude = that.formDialog[fieldName]['longitude'] + ',' + that.formDialog[fieldName]['latitude'];
      }
      that.latAndLongitude = latAndLongitude;
      that.locationAndRegionVisible = true;
    };

    const mapEvents = (data)=>{
      if(that.operateType === 1){
        let latAndLongitude = data.latAndLongitude.split(',');
        that.formDialog[that.activeFieldName]['latitude'] = latAndLongitude[1];
        that.formDialog[that.activeFieldName]['longitude'] = latAndLongitude[0];
        that.formDialog[that.activeFieldName]['longitudeAndLatitude'] = data.latAndLongitude;
        queryAreaAddressByCoordinates(that.activeFieldName); // 根据坐标获取区域地址
        that.locationAndRegionVisible = false;
      }
    };

    const formDialogRef = ref(null);
    const formSubmitFun = ()=>{
      return new Promise((resolve, reject)=>{
        formDialogRef.value.validate((valid) => {
          if(valid){
            let formDialog = JSON.parse(JSON.stringify(that.formDialog)); // 扩展属性字段
            if(props.scenarioType === 0){
              resolve({scenarioType: props.scenarioType,readwriteObject:  formDialog});// 电站扩展属性
            } else {
              let readwriteObject = JSON.parse(JSON.stringify(formDialog));
              delete readwriteObject.systemName;
              resolve({modelId: props.modelId, scenarioType: props.scenarioType, readwriteObject: readwriteObject, systemName: formDialog.systemName});
            }
          } else {
            reject({scenarioType: props.scenarioType,code: 50000,msg: "请确保数据填写完整！"});
          }
        });
      });
    };

    // 根据坐标获取区域地址
    const queryAreaAddressByCoordinates = (fieldName)=>{
      if(!that.formDialog[fieldName]['longitude'] || !that.formDialog[fieldName]['latitude']){
        return;
      }
      let coordinates = that.formDialog[fieldName]['longitude'] + ',' + that.formDialog[fieldName]['latitude'];
      getAreaAddressByCoordinates({ coordinates: coordinates }).then(res=>{
        let returnDataInfo = res.data ? res.data : {};
        that.formDialog[fieldName]['city'] = returnDataInfo.cityName;
        that.formDialog[fieldName]['county'] = returnDataInfo.areaName;
        that.formDialog[fieldName]['address'] = returnDataInfo.address;
        that.formDialog[fieldName]['province'] = returnDataInfo.provinceName;
      });
    };

    // 返回时间选择的类型
    const setDatePickTypeFun = (extraValue)=>{
      let dateType = "date";
      if(extraValue.timeFormat && extraValue.timeFormat.indexOf('HH') !== -1)dateType = "datetime";
      return  dateType;
    };

    // 设置时间选择展示类型
    const setDatePickFormatFun = (extraValue)=>{
      let dateType = "date",dateFormat = "YYYY-MM-DD";
      if(extraValue.timeFormat && extraValue.timeFormat.indexOf('HH') !== -1){
        dateType = "datetime";
        dateFormat = "YYYY-MM-DD HH:mm:ss";
      }

      switch (extraValue.timeFormat) {
        case "yyyy-MM-dd":
          dateFormat = "YYYY-MM-DD";
          break;
        case "yyyy-MM-dd HH:mm":
          dateFormat = "YYYY-MM-DD HH:mm";
          break;
        case "yyyy-MM-dd HH:mm:ss":
          dateFormat = "YYYY-MM-DD HH:mm:ss";
          break;
        default:
          return dateFormat;
      }
      return dateFormat;
    };
    
    const watchModelFieldArray = watch(()=>props.modelFieldArray,(newModelFieldArray)=>{
      let model_field_array = JSON.parse(JSON.stringify(newModelFieldArray));
      queryModelFieldUpdateListByModelId(model_field_array);
    },{ deep: true,immediate: true });

    onMounted(()=>{});

    return {...toRefs(that), clearFormDialogValueFun, clickCoordinatePicking, mapEvents, setDatePickTypeFun, setDatePickFormatFun, formDialogRef,
      formSubmitFun, queryAreaAddressByCoordinates, locationInfoVerificationFun, queryModelFieldUpdateListByModelId, watchModelFieldArray};
  }
});
</script>

<style lang="scss" scoped>
.modelDynamicFieldCom{
  margin-bottom: 10px;

  :deep(.nullData){
    .noCartImages {
      max-width: 65px;
    }
  }
}
</style>