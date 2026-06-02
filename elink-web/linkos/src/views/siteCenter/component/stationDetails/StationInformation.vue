<template>
  <div class="tableContent scrollbarStyle">
    <template v-for="(item, index) in list" :key="index">
      <TitleView :title="item.name">
        <template #headerRight>
          <template v-if="item.isEditBut">
            <el-button class="blackFontButtons" :icon="Edit" @click="clickEditButton(item.fieldName)">编辑</el-button>
          </template>
        </template>
        <template #content>
          <div class="content_list" v-loading="listLoading">
            <el-row :gutter="12" class="content_list">
              <template v-for="(child, i) in item.children" :key="i">
                <el-col :sm="12" :lg="8" :xl="8" class="info_li">
                  <div class="flex_li_left">{{ child.reaName }}：</div>
                  <div class="flex_li_right">
                    <span v-if="child.filterName">{{ $filters[child.filterName](returnDataInfo[child.fieldName])
                    }}</span>
                    <span v-else>{{ $filters.moreData(extendedAttributeFun(child)) }}</span>
                    <span class="unit" v-if="child.unit">{{ child.unit }}</span>
                    <span class="copy" v-if="child.isCopy"
                      @click="clickCopyBut(returnDataInfo[child.fieldName])">复制</span>
                  </div>
                </el-col>
              </template>
            </el-row>
          </div>
        </template>
      </TitleView>
    </template>

    <CreateSiteDialog v-if="createSiteVisible" v-model:isVisible="createSiteVisible" :titleName="titleName"
      :activeSiteId="siteRecordsId" @changeEvent="querySiteInfoById" />
  </div>
</template>

<script>
import { clickCopyValue } from "@/utils";
import { Edit } from "@element-plus/icons-vue";
import { setTimerSplitTallyFun } from "@/utils/dateTime";
import { CreateSiteDialog } from "@/views/siteCenter/component";
import { findSiteInfoById } from "@/api/siteCenter/stationDetails";
import { onMounted, reactive, toRefs, defineComponent } from "vue";

export default defineComponent({
  name: "StationInformation",
  components: { CreateSiteDialog },
  props: {
    siteRecordsId: {
      type: [String, Number],
      default: ""
    }
  },
  setup (props) {

    const that = reactive({
      Edit,
      returnDataInfo: {},
      listLoading: false,
      list: [
        {
          name: "基本信息",
          isEditBut: true,
          fieldName: "BasicInfo",
          children: [
            { reaName: "站点ID", fieldName: "id", filterName: "", unit: "", isCopy: true },
            { reaName: "站点名称", fieldName: "siteName", filterName: "", unit: "" },
            { reaName: "站点编码", fieldName: "siteCode", filterName: "", unit: "" },
            { reaName: "所属租户", fieldName: "tenantName", filterName: "", unit: "" },
            { reaName: "站点状态", fieldName: "siteStatus", filterName: "siteStatus", unit: "" },
            { reaName: "创建信息", fieldName: "createInfo", filterName: "", unit: "" },
            { reaName: "更新信息", fieldName: "updateInfo", filterName: "", unit: "" },
            { reaName: "站点描述", fieldName: "siteDescribe", filterName: "", unit: "" },
          ]
        },
        {
          name: "扩展信息",
          isEditBut: false,
          fieldName: "ExtendedInfo",
          children: []
        }
      ],

      titleName: "编辑详情",
      createSiteVisible: false,
    })

    const querySiteInfoById = () => {
      that.listLoading = true;
      findSiteInfoById({ id: props.siteRecordsId, timer: new Date() }).then(res => {
        let returnDataInfo = res.data ? res.data : {};

        // 处理扩展属性返回值
        if (returnDataInfo.siteReadwriteObject) {
          returnDataInfo.siteReadwriteObject = JSON.parse(returnDataInfo.siteReadwriteObject);
          // 如果 location 不存在，先创建
          if (!returnDataInfo.siteReadwriteObject.location.longitudeAndLatitude) {
            returnDataInfo.siteReadwriteObject.location.longitudeAndLatitude =
              `${returnDataInfo.siteReadwriteObject.location.longitude},${returnDataInfo.siteReadwriteObject.location.latitude}`;
          }

          returnDataInfo = Object.assign({}, returnDataInfo, returnDataInfo.siteReadwriteObject);
        }

        // 扩展属性处理
        if (returnDataInfo.siteReaList && returnDataInfo.siteReaList.length) {
          for (let i = 0; i < returnDataInfo.siteReaList.length; i++) {
            if (returnDataInfo.siteReaList[i].extraValue) {
              returnDataInfo.siteReaList[i].extraValue = JSON.parse(returnDataInfo.siteReaList[i].extraValue);
            }

            // 位置信息处理
            if (returnDataInfo.siteReaList[i].reaType === 4) {
              let activeDataInfo = JSON.parse(JSON.stringify(returnDataInfo.siteReaList[i]));
              console.log(activeDataInfo, '站点文职');
              returnDataInfo.siteReaList[i].fieldName = returnDataInfo.siteReaList[i].fieldName + ".longitudeAndLatitude";
              // returnDataInfo.siteReaList[i].fieldName = [];
              let active_obj = JSON.parse(JSON.stringify(activeDataInfo));
              returnDataInfo.siteReaList.splice(i + 1, 0, { ...active_obj, reaName: activeDataInfo.reaName + "-具体地址", fieldName: activeDataInfo.fieldName + ".address" });
              returnDataInfo.siteReaList.splice(i + 1, 0, { ...active_obj, reaName: activeDataInfo.reaName + "-所在城市", fieldName: activeDataInfo.fieldName + ".city" });
              i += 2;
            }
          }

          console.log(returnDataInfo.siteReaList);
          let findIndex = that.list.findIndex(item => item.fieldName === "ExtendedInfo");
          that.list[findIndex].children = JSON.parse(JSON.stringify(returnDataInfo.siteReaList));
        }

        returnDataInfo.createInfo = returnDataInfo.createName + "," + returnDataInfo.createTime;
        returnDataInfo.updateInfo = returnDataInfo.updateName + "," + returnDataInfo.updateTime;
        that.returnDataInfo = JSON.parse(JSON.stringify(returnDataInfo));
        // console.log(that.returnDataInfo);
        that.listLoading = false;
      }).catch(() => {
        that.listLoading = false;
      })
    }

    const clickEditButton = () => {
      that.titleName = "编辑站点信息";
      that.createSiteVisible = true;
    }

    // 复制
    const clickCopyBut = (value) => {
      clickCopyValue(value);
    }

    // 扩展属性值处理
    const extendedAttributeFun = (fieldInfo) => {
      let fieldNameArray = fieldInfo.fieldName.split('.');
      let fieldValue = that.returnDataInfo[fieldNameArray[0]];
      if (fieldNameArray.length >= 2) {
        for (let i = 1; i < fieldNameArray.length; i++) {
          if (fieldValue) fieldValue = fieldValue[fieldNameArray[i]];
        }
      }

      if (fieldInfo.extraValue) {

        //  扩展属性 ----》 下拉选择
        if (fieldInfo.reaType === 3) {
          if (fieldInfo.extraValue?.enumArray && fieldInfo.extraValue?.enumArray.length) {
            // 多选
            if (fieldInfo.extraValue.multiple) {
              // console.log(fieldValue);
              // console.log(fieldInfo.extraValue.enumArray);
              if (fieldValue && fieldValue.length) {
                for (let i = 0; i < fieldValue.length; i++) {
                  let findItem = fieldInfo.extraValue.enumArray.find(item => item.id === fieldValue[i]);
                  if (findItem) fieldValue[i] = findItem.name;
                }
                fieldValue = fieldValue.join('，');
              }
            } else {
              let findItem = fieldInfo.extraValue.enumArray.find(item => item.id === fieldValue);
              if (findItem) fieldValue = findItem.name;
            }
          }
        }

        //  扩展属性 ----》 开关
        if (fieldInfo.reaType === 5) {
          fieldValue = fieldValue ? fieldInfo.extraValue.trueValue : fieldInfo.extraValue.falseValue;
        }

        //  扩展属性 ----》 时间
        if (fieldInfo.reaType === 6) {
          fieldValue = setTimerSplitTallyFun(fieldValue, fieldInfo.extraValue?.splitTally, fieldInfo.extraValue?.timeFormat);
        }
      }
      return fieldValue
    }

    onMounted(() => {
      querySiteInfoById();
    })

    return { ...toRefs(that), querySiteInfoById, clickCopyBut, extendedAttributeFun, clickEditButton }
  }
})
</script>


<style lang="scss" scoped>
.el-row {
  width: 100%;
  margin-bottom: 11px;

  .info_li {
    display: flex;
    align-items: center;
    margin-bottom: 18px;

    .flex_li_left {
      color: #666666;
      font-size: 14px;
      white-space: nowrap;
    }

    .flex_li_right {
      color: #121C3F;
      font-size: 14px;
      display: flex;
      align-items: center;

      .unit {
        margin-left: 2px;
        font-weight: bold;
      }

      .copy {
        color: #1F74E2;
        cursor: pointer;
        margin-left: 12px;
      }
    }
  }
}
</style>