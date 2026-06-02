<template>
  <div class="content_body">
    <template v-for="(item,index) in list" :key="index">
      <TitleView :title="item.title">
        <template v-slot:content>
          <div class="content_body_list" v-loading="listLoading">
            <el-row class="content_list" :gutter="12">
              <template v-for="(el,i) in item.children" :key="i">
                <el-col :xs="24" :md="12" :lg="8" >
                  <div class="content_list_li">
                    <div class="content_list_li_left">{{ el.name }}：</div>
                    <div class="content_list_li_right">
                      <template v-if="el.filterName"><span>{{ $filters[el.filterName](resData[el.fieldName]) }}</span></template>
                      <template v-else><span>{{ $filters.moreData(resData[el.fieldName]) }}</span></template>
                      <div class="copy_class" v-if="el.icCopy" @click="clickCopyBut(resData[el.fieldName])">复制</div>
                    </div>
                  </div>
                </el-col>
              </template>
            </el-row>
            <div class="content_bottom" >
              <template v-if="item.fieldName === 'businessInfo'">
                <div class="content_list_li_left">营业执照：</div>
                <div class="content_list_li_right">
                  <UploadPicturesCom ref="fileListRef" :isDisabled="!isEditCompany" isPreview :fileSize="fileSize" v-model:fileArray="fileListArray"
                                     fieldName="fileListArray" @changEvent="changEvent">
                    <template #tip_content>
                      <div class="alter_text">建议尺寸1600*1200px大小的营业执照原件JPG/PNG图片，上传1张，不超过5M</div>
                    </template>
                  </UploadPicturesCom>
                </div>
              </template>
<!--              <template v-if="item.fieldName === 'identifying'">-->
<!--                <div class="content_list_li_left">平台logo：</div>-->
<!--                <div class="content_list_li_right">-->
<!--                  <UploadPicturesCom ref="fileLogoListRef" :isDisabled="!isEditCompany" isPreview :fileSize="fileSize" v-model:fileArray="fileLogoListArray"-->
<!--                                     fieldName="fileLogoListArray" @changEvent="changEvent">-->
<!--                    <template #tip_content>-->
<!--                      <div class="alter_text">平台企业LOGO 建议尺寸 300*54px大小的JPG/PNG图片，此图用于SAAS的LOGO图片展示，上传1张，不超过5M</div>-->
<!--                    </template>-->
<!--                  </UploadPicturesCom>-->
<!--                </div>-->
<!--              </template>-->
            </div>
          </div>
        </template>
      </TitleView>
    </template>

    <!--    新增编辑租户信息-->
    <AddTenantDialog v-if="addTenantVisible" v-model:isVisible="addTenantVisible" :titleName="titleName" :formDialog="resData" isDetails
                     @changeEvent="getTenantDetails"></AddTenantDialog>
  </div>
</template>

<script>
import {ElMessage} from "element-plus";
import {clickCopyValue, nullToDelete} from "@/utils";
import {onMounted, reactive, toRefs, defineComponent} from "vue";
import {AddTenantDialog} from "@/views/tenantManagement/component";
import UploadPicturesCom from "@/components/component/UploadPicturesCom.vue";
import {findTenantDetailsById, saveOrUpdateTenantInfo} from "@/api/tenantManagement/tenantTabulation";

export default defineComponent({
  name: "CompanyInfo",
  components: { AddTenantDialog, UploadPicturesCom },
  props: {
    tenantId: {
      type: [String, Number],
      default: ""
    },
    isEditCompany:{
      type: Boolean,
      default: false
    }
  },
  setup(props){

    const that = reactive({
      resData: {},
      listLoading: false,
      tenantId: props.tenantId,

      titleName: "编辑租户",
      addTenantVisible: false,

      fileSize: 5,
      fileListArray: [],
      fileLogoListArray: [],

      list:[
        {
          title: "租户信息",
          fieldName: "tenantInfo",
          children:[
            {name: "租户id", fieldName: "id",icCopy: true},
            {name: "租户状态", fieldName: "tenantState",filterName:"tenantState"},
            {name: "描述", fieldName: "refer"},
            {name: "创建信息", fieldName: "createInfo"},
            {name: "更新信息", fieldName: "updateInfo"}
          ]
        },
        {
          title: "租户信息",
          fieldName: "businessInfo",
          children:[
            {name: "企业名称", fieldName: "tenantName"},
            {name: "地址", fieldName: "address"},
            {name: "组织机构代码", fieldName: "organizationCode"},
          ]
        },
        // {
        //   title: "标识",
        //   fieldName: "identifying",
        //   children:[]
        // }
      ]
    })

    // 获取租户详情信息
    const getTenantDetails = () => {
      that.listLoading = true;
      findTenantDetailsById({ id: that.tenantId }).then( res=> {
        let resData = res.data ? res.data : {};
        resData.repeatPassword = resData.password;
        if(resData.createUserName && resData.createTime){
          resData.createInfo = `${ resData.createUserName }，${ resData.createTime }`;
        }
        if(resData.updateUserName && resData.updateTime){
          resData.updateInfo = `${ resData.updateUserName }，${ resData.updateTime }`;
        }
        // that.fileLogoListArray = resData.logo && resData.logo !== "null" ? [{ url: resData.logo }] : [];
        that.fileListArray = resData.businessLicense && resData.businessLicense !== "null" ? [{ url: resData.businessLicense }] : [];
        for(let key in resData)resData[key] = nullToDelete(resData[key]);
        that.resData = JSON.parse(JSON.stringify(resData));
        that.listLoading = false;
      }).catch(()=>{
        that.listLoading = false;
      })
    }

    // 编辑企业信息
    const clickButtonFun = () => {
      that.addTenantVisible = true;
    }

    const changEvent = (data)=>{
      let formData = new FormData();
      let formDialog = JSON.parse(JSON.stringify(that.resData));

      // console.log(that[data.fieldName]);
      if(that[data.fieldName] && that[data.fieldName].length){
        for (let i = 0; i < that[data.fieldName].length; i++) {
          if (that[data.fieldName][i].raw) formData.append("businessLicenseFile", that[data.fieldName][i].raw);
        }
      } else {
        // if(data.fieldName === "fileLogoListArray")formDialog.logo = "";
        if(data.fieldName === "fileListArray")formDialog.businessLicense = "";
      }

      for (let key in formDialog) formData.append(key, formDialog[key]);
      // 保存或编辑租户信息
      saveOrUpdateTenantInfo(formData).then(()=>{
        getTenantDetails();
        ElMessage({ type: "success", showClose: true, message: "操作成功！" });
      }).catch(()=>{
        getTenantDetails();
      })
    }

    // 复制
    const clickCopyBut = (value)=>{
      clickCopyValue(value);
    }

    onMounted(() =>{
      getTenantDetails();
    })

    return { ...toRefs(that), clickButtonFun, getTenantDetails,clickCopyBut, changEvent }
  }
})
</script>

<style scoped lang="scss">
.content_body{
  padding: 12px 16px;
  box-sizing: border-box;

  .content_body_list{
    margin-bottom: 16px;

    .content_list_li{
      display: flex;
      align-items: center;
      margin-bottom: 16px;

      .content_list_li_left{
        color: #242424;
        font-size: 14px;
      }

      .content_list_li_right{
        display: flex;
        align-items: center;
        color: #BBBBBB;
        font-size: 14px;

        .copy_class {
          color: #1F74E2;
          cursor: pointer;
          margin-left: 16px;
        }
      }
    }

    .content_bottom{
      display: flex;
      align-items: center;
      margin-top: 16px;

      :deep(.uploadPicturesCom){

        .el-upload--picture-card{
          --el-upload-picture-card-size: 120px;
          --el-upload-list-picture-card-size: 120px;

          .uploader-icon{
            font-size: 28px;
          }
        }

        .el-upload-list__item{
          --el-upload-list-picture-card-size: 120px;
        }
      }
    }
  }
}
</style>
