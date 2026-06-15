<template>
  <Dialog v-model:isVisible="dialog_visible" :listLoading="listLoading" :manualEnterClose="false" :title="titleName" disabledLoading width="720" @confirm="clickConfirmBut">
    <template v-slot:content>
      <div class="dialog-main">
        <el-form ref="formDialogRef" :model="formDialog" :rules="rules" label-width="130px">
          <el-form-item label="营业时间：" prop="businessHours">
            <div class="content_body">
              <div class="alter_text">每周营业时间</div>
              <el-checkbox-group v-model="formDialog.businessHours.week">
                <el-checkbox-button v-for="item in weekArray" :key="item.id" :value="item.id">
                  <span>{{ item.name }}</span>
                </el-checkbox-button>
              </el-checkbox-group>

              <div class="alter_text marginBottom">
                <div class="left_text">每天营业时段</div>
                <div class="right_text" @click="clickAddBusinessHours">
                  <el-icon><Plus /></el-icon>
                  <span class="text">新增时段</span>
                </div>
              </div>
              <div class="sl_time_list scrollbarStyle">
                <template v-if="formDialog.businessHours.timeList && formDialog.businessHours.timeList.length">
                  <template v-for="(item,index) in formDialog.businessHours.timeList" :key="index">
                    <el-row class="sl_time_li" >
                      <el-col :span="10">
                        <el-form-item :rules="{required: true,message: '请选择',trigger: 'change'}" :prop="'businessHours.timeList.' + index + '.startTime'">
                          <el-time-select v-model="item.startTime" placeholder="请选择" start="00:00" step="01:00" end="24:00"/>
                        </el-form-item>
                      </el-col>
                      <el-col :span="1"></el-col>
                      <el-col :span="10">
                        <el-form-item :rules="{required: true,message: '请选择',trigger: 'change'}" :prop="'businessHours.timeList.' + index + '.endTime'">
                          <el-time-select v-model="item.endTime" placeholder="请选择" start="00:00" step="01:00" end="24:00"/>
                        </el-form-item>
                      </el-col>
                      <el-col :span="3">
                        <div class="delete_icon pointer flex-jc-ai-center" @click="clickDeleteTime(index)">
                          <el-icon size="16" color="#FF1515"><Delete /></el-icon>
                        </div>
                      </el-col>
                    </el-row>
                  </template>
                </template>
              </div>

            </div>
          </el-form-item>
          <el-form-item label="开放类型：" prop="openType">
            <TabBackground customClass="customBcAndColor" :tabsArray="openTypeArray" v-model:tabsIndex="formDialog.openType"></TabBackground>
          </el-form-item>

          <el-form-item label="站点状态：" prop="siteStatus">
            <el-select v-model="formDialog.siteStatus" placeholder="请选择站点状态">
              <el-option v-for="item in site_status_array" :key="item.id" :label="item.name" :value="item.id"/>
            </el-select>
          </el-form-item>

          <el-form-item label="建筑场所：" prop="buildSite">
            <el-select v-model="formDialog.buildSite" placeholder="请选择建筑场所">
              <el-option v-for="item in build_site_array" :key="item.id" :label="item.name" :value="item.id"/>
            </el-select>
          </el-form-item>
          <el-form-item label="经度：" prop="longitude">
            <el-input v-model="formDialog.longitude" placeholder="请输入经度" @change="queryAreaAddressByCoordinates" />
          </el-form-item>
          <el-form-item label="纬度：" prop="latitude">
            <el-input v-model="formDialog.latitude" placeholder="请输入纬度" @change="queryAreaAddressByCoordinates" />
          </el-form-item>
          <el-form-item label="所在城市：">
            <el-col :span="8">
              <el-input v-model="formDialog.province" disabled placeholder="所在省份"/>
            </el-col>
            <el-col :span="8">
              <el-input v-model="formDialog.city" disabled placeholder="所在市"/>
            </el-col>
            <el-col :span="8">
              <el-input v-model="formDialog.county" disabled placeholder="所在区县"/>
            </el-col>
          </el-form-item>
          <el-form-item label="详细地址：">
            <el-input v-model="formDialog.address" placeholder="请输入详细地址"/>
          </el-form-item>
          <el-form-item label="联系人：" prop="contacts">
            <el-input v-model="formDialog.contacts" placeholder="请输入联系人"/>
          </el-form-item>
          <el-form-item label="联系电话：" prop="phone">
            <el-input v-model="formDialog.phone" maxlength="11" show-word-limit placeholder="请输入联系电话"/>
          </el-form-item>
          <el-form-item label="停车费描述：">
            <el-input v-model="formDialog.parkCostDesc" :rows="3" type="textarea" placeholder="请输入停车费描述" maxlength="200" show-word-limit />
          </el-form-item>
        </el-form>
      </div>
    </template>
  </Dialog>
</template>
<script lang="ts">
import {ElMessage} from "element-plus";
import {mobile} from "@/utils/validate";
import {Plus,Delete} from '@element-plus/icons-vue';
import {build_site_array,site_status_array} from "@/utils/setVariate";
import {getCurrentInstance, onMounted, reactive, ref, toRefs, watch, defineComponent} from "vue";
import {getAreaAddressByCoordinates, saveOrUpdateSiteInfo} from "@/api/operationManagement/CsStationDetails";

export default defineComponent({
  name: "OperationsInfoDialog",
  components:{Plus,Delete},
  props: {
    isVisible: {
      type: Boolean,
      default: false
    },
    returnDataInfo: {
      type: Object,
      default: () => {
        return {};
      }
    }
  },
  setup(props) {
    const {emit} = getCurrentInstance();

    const validateOpenType = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请选择开放类型"));
      } else {
        callback();
      }
    };

    const validateSiteStatus = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请选择站点状态"));
      } else {
        callback();
      }
    };

    const validatePhone = (rule, value, callback) => {
      if (!value && !mobile(value)) {
        callback(new Error("请输入正确的联系电话"));
      } else {
        callback();
      }
    };

    const validateContacts = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请输入联系人"));
      } else {
        callback();
      }
    };

    const validateLongitude = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请输入经度"));
      } else {
        callback();
      }
    };

    const validateLatitude = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请输入纬度"));
      } else {
        callback();
      }
    };

    const validateBuildSite = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请选择建筑场所"));
      } else {
        callback();
      }
    };

    const validateBusinessHours = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请选择营业时间"));
      } else {
        if((value.timeList && value.timeList.length) && (value.week && value.week.length)){
          callback();
        } else {
          if(!value.week || !value.week.length){
            callback(new Error("请选择每周营业时间"));
          }

          if(!value.timeList || !value.timeList.length){
            callback(new Error("请填加每天营业时段"));
          }
        }
      }
    };

    const that = reactive({
      formDialog: {},
      build_site_array,
      site_status_array,
      listLoading: false,
      titleName: "编辑运营信息",
      dialog_visible: props.isVisible,
      openTypeArray: [{id: 1, name: "对外开放"}, {id: 2, name: "专用站点"}],
      weekArray: [
        {id: 1, name: "星期一"},
        {id: 2, name: "星期二"},
        {id: 3, name: "星期三"},
        {id: 4, name: "星期四"},
        {id: 5, name: "星期五"},
        {id: 6, name: "星期六"},
        {id: 7, name: "星期日"}
      ],

      rules: {
        openType: [{required: true, trigger: "change", validator: validateOpenType}],
        siteStatus: [{required: true, trigger: "change", validator: validateSiteStatus}],
        buildSite: [{required: true, trigger: "change", validator: validateBuildSite}],
        longitude: [{required: true, trigger: "change", validator: validateLongitude}],
        latitude: [{required: true, trigger: "change", validator: validateLatitude}],
        contacts: [{required: true, trigger: "change", validator: validateContacts}],
        phone: [{required: true, trigger: "change", validator: validatePhone}],
        businessHours: [{required: true, trigger: "change", validator: validateBusinessHours}],
      }
    });

    const formDialogRef = ref(null);
    const clickConfirmBut = () => {
      formDialogRef.value.validate((valid) => {
        if (valid) {
          that.listLoading = true;
          let form_dialog = JSON.parse(JSON.stringify(that.formDialog));

          saveOrUpdateSiteInfo({ ...form_dialog, isDelete: 1 }).then(() => {
            emit("changeEvent");
            that.dialog_visible = false;
            ElMessage({type: "success", showClose: true, message: "操作成功！"});
          }).catch(() => {
            that.listLoading = false;
          });
        }
      });
    };

    // 根据坐标获取区域地址
    const queryAreaAddressByCoordinates = () => {
      if (!that.formDialog.longitude || !that.formDialog.latitude) return;
      let longitudeAndLatitude = that.formDialog.longitude + ',' + that.formDialog.latitude;
      getAreaAddressByCoordinates({coordinates: longitudeAndLatitude}).then(res => {
        let addressInfo = res.data ? res.data : {};
        that.formDialog.province = addressInfo.provinceName;
        that.formDialog.city = addressInfo.cityName;
        that.formDialog.county = addressInfo.areaName;
        that.formDialog.address = addressInfo.address;
      });
    };

    // 添加营业时段
    const clickAddBusinessHours = ()=>{
      if(!that.formDialog.businessHours.timeList)that.formDialog.businessHours.timeList = [];

      let findLastItem = that.formDialog.businessHours.timeList[that.formDialog.businessHours.timeList.length - 1];
      if(findLastItem && findLastItem.endTime === "00:00"){
        ElMessage({type: "error", showClose: true, message: "全天时段以包含！"});
        return;
      }

      that.formDialog.businessHours.timeList.push({});
    };

    // 删除时段
    const clickDeleteTime = (index)=>{
      that.formDialog.businessHours.timeList.splice(index,1);
    };

    const initParamConfigFun = () => {
      let formDialog = JSON.parse(JSON.stringify(props.returnDataInfo));
      delete formDialog.imagePath;

      if(!formDialog.businessHours)formDialog.businessHours = {};

      for (let key in formDialog) {
        if (formDialog[key]) that.formDialog[key] = formDialog[key];
      }
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

    return {...toRefs(that), watchDialogVisible, watchVisible, initParamConfigFun, clickConfirmBut, formDialogRef, queryAreaAddressByCoordinates,
      clickAddBusinessHours,clickDeleteTime};

  }
});
</script>
<style lang="scss" scoped>

.alter_text{
  width: 100%;
  font-size: 14px;
  display: flex;
  align-items: center;
  justify-content: space-between;

  .right_text{
    color: #007FEB;
    display: flex;
    align-items: center;
    cursor: pointer;

    .text{
      margin-left: 2px;
    }
  }
}

.marginBottom{
  margin-top: 10px;
}

.sl_time_list{
  width: 100%;
  max-height: 210px;
  overflow-y: auto;

  .sl_time_li{
    margin-bottom: 16px;

    .delete_icon{
      height: 100%;
    }
  }
}
</style>