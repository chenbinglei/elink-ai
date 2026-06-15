<template>
  <el-popover :width="320" placement="bottom" trigger="click">
    <template #reference>
      <el-icon class="pointer" color="#079CEB" size="16"><Filter/></el-icon>
    </template>
    <div class="stationMenuFilterCom">
      <el-form :model="formInline">
        <el-form-item label="站点状态：">
          <el-select v-model="formInline.siteState" :teleported="false" placeholder="请选择">
            <el-option v-for="item in siteStateArray" :key="item.id" :label="item.name" :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="能源场景：">
          <el-select v-model="formInline.scenarioTypes" :teleported="false" multiple placeholder="请选择">
            <el-option v-for="item in scenarioTypeArray" :key="item.id" :label="item.name" :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="所属区域：">
          <el-col :span="8">
            <el-select v-model="formInline.areaType" placeholder="请选择" :teleported="false" @change="areaTypeChangeFun">
              <el-option v-for="(item,index) in areaTypeArray" :key="index" :label="item.name" :value="item.id"></el-option>
            </el-select>
          </el-col>
          <el-col :span="16">
            <el-cascader v-model="formInline.areaValue" filterable clearable placeholder="全部" :teleported="false" :props="propsAreaValueConfig"
                         :options="optionsDataArray"></el-cascader>
          </el-col>
        </el-form-item>

        <el-form-item>
          <div class="flex jc-end" style="width: 100%;">
            <el-button @click="clickResetBut">重置</el-button>
            <el-button type="primary" @click="clickSearchBut">查询</el-button>
          </div>
        </el-form-item>
      </el-form>
    </div>
  </el-popover>
</template>

<script lang="ts">
import { useAppStore } from '@/stores/index';

import pinyin from "tiny-pinyin";
import {Filter} from "@element-plus/icons-vue";
import {area_type_array} from "@/utils/setVariate";
import {reactive, defineComponent, toRefs, onMounted, getCurrentInstance, computed} from "vue";
import {findSiteBasicInfoByTenantId} from "@/api/operationManagement/CsStationManagement";
import {setTreeData} from "@/utils";

export default defineComponent({
  name: "StationMenuFilterCom",
  components: {Filter},
  emits: ["changeEvent"],
  setup() {
    const appStore = useAppStore();
    const {emit} = getCurrentInstance();
    const userInfo = computed(() => {
      return appStore.userInfo;
    });

    const that = reactive({
      formInline: {},
      oldFormInline: {},

      cityArray: [],
      countyArray: [],
      provinceArray: [],
      optionsDataArray: [],
      propsAreaValueConfig: {label: "name",value:"name"},
      areaTypeArray: [...area_type_array,{id: 3, name: "区"}],
      siteStateArray: [{id: 1, name: "正常"}, {id: 3, name: "通信异常"}, {id: 4, name: "故障"}],  // {id: 2, name: "预警"},
      scenarioTypeArray: [{id: 1, name: "光伏"}, {id: 2, name: "储能"}, {id: 3, name: "充电桩"}, {id: 4, name: "用能"}, {id: 5, name: "配电"}],
    });

    const clickResetBut = () => {
      that.formInline = JSON.parse(JSON.stringify(that.oldFormInline));
    };

    const clickSearchBut = () => {
      emit("changeEvent", that.formInline);
    };

    // 查询站点下拉列表
    const querySiteBasicInfoByTenantId = () => {
      findSiteBasicInfoByTenantId({tenantId: userInfo.value.tenantId}).then(res => {
        let list = res.data ? res.data : [];
        let provinceArray = [], cityArray = [], countyArray = [];

        for (let i = 0; i < list.length; i++) {

          // 读写类型字段
          if(list[i].siteReadwriteObject) list[i].siteReadwriteObject = JSON.parse(list[i].siteReadwriteObject);
          let location_info = list[i]?.siteReadwriteObject?.location ?? {};

          //省份处理
          if(location_info.province){

            let province_id = pinyin.convertToPinyin(location_info.province);
            let findProvince = provinceArray.find(item => item.id === province_id);
            if (!findProvince) provinceArray.push({name: location_info.province, id: province_id });

            // 市区处理
            let city_id = pinyin.convertToPinyin(location_info.city);
            let findCity = cityArray.find(item => item.name === location_info.city);
            if (!findCity) cityArray.push({id: city_id,name: location_info.city, parentId: province_id });

            //区域处理
            let county_id = pinyin.convertToPinyin(location_info.county);
            let findCounty = countyArray.find(item => item.name === location_info.county);
            if (!findCounty) countyArray.push({id: county_id,name: location_info.county, parentId: city_id });
          }
        }
        // console.log(provinceArray);
        // console.log(cityArray);
        that.provinceArray = JSON.parse(JSON.stringify(provinceArray));
        that.cityArray = setTreeData([...provinceArray,...cityArray]);
        that.countyArray = setTreeData([...provinceArray,...cityArray,...countyArray]);
      });
    };

    const areaTypeChangeFun = ()=>{
      let optionsDataArray = [];
      that.formInline.areaValue = "";
      if(that.formInline.areaType === 1) optionsDataArray = that.provinceArray;
      if(that.formInline.areaType === 2) optionsDataArray = that.cityArray;
      if(that.formInline.areaType === 3) optionsDataArray = that.countyArray;
      that.optionsDataArray = JSON.parse(JSON.stringify(optionsDataArray));
    };

    onMounted(() => {
      that.oldFormInline = JSON.parse(JSON.stringify(that.formInline));
      querySiteBasicInfoByTenantId();
    });

    return {...toRefs(that), clickResetBut, clickSearchBut, querySiteBasicInfoByTenantId, areaTypeChangeFun};
  }
});
</script>

<style lang="scss" scoped>
.stationMenuFilterCom {
  width: 100%;
}

</style>