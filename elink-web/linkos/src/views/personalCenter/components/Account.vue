<template>
  <div class="System white flex ai-center jc-center fd-column">
    <el-form ref="formInlineRef" :model="formInline" :rules="rules" label-position="right" label-width="100px" style="width: 360px">
      <div class="user_avatar">
        <el-upload :auto-upload="false" :on-change="uploadChange" :show-file-list="false" accept=".jpg, .jpeg, .png, .JPG, .JPEG" action="123" class="avatar-uploader">
          <div class="userAvater">
            <el-avatar :src="formInline.userProfile">{{ formInline.fullName }}</el-avatar>
            <div class="rightBttom">
              <div class="compileButton">
                <i class="iconfont icon-bianji"></i>
              </div>
            </div>
          </div>
        </el-upload>
      </div>
      <el-form-item label="用户账号:">
        <el-input v-model="formInline.userAccount" disabled type="text"></el-input>
      </el-form-item>
      <el-form-item label="姓名:" prop="fullName">
        <el-input v-model="formInline.fullName" type="text" @change="compileStatus = true"></el-input>
      </el-form-item>
      <el-form-item label="角色:">
        <el-select v-model="formInline.userRole" clearable disabled placeholder="请选择" style="width: 100%">
          <el-option v-for="item in userRoleArray" :key="item.id" :label="item.name" :value="item.id"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="手机号:" prop="phone">
        <el-input v-model="formInline.phone" maxlength="11" show-word-limit type="text" @change="compileStatus = true"></el-input>
      </el-form-item>
    </el-form>
    <div v-if="compileStatus" class="main-button">
      <el-button class="blackFontButtons" @click="getUserDetails">取消</el-button>
      <el-button class="whiteFontButtons" :loading="loading" @click="saveUserInfo">保存</el-button>
    </div>
  </div>
</template>

<script>
import {useStore} from 'vuex';
import {nullToDelete} from "@/utils";
import {ElMessage} from 'element-plus'
import {mobile, notCharmap} from "@/utils/validate"
import {reactive, toRefs, onMounted, computed, ref} from "vue"
import {findUserDetailsById, saveOrUpdateUserInfo} from "@/api/personalCenter/personalCenter";

export default {
  name: "Account",
  setup() {
    const validateFullName = (rule, value, callback) => {
      if (!notCharmap(value)) {
        callback(new Error("请输入正确的姓名"));
      } else {
        callback();
      }
    };

    const validatePhone = (rule, value, callback) => {
      if (!mobile(value)) {
        callback(new Error("请输入正确的手机号"));
      } else {
        callback();
      }
    };

    const store = useStore();
    const userInfo = computed(() => {
      return store.state.app.userInfo
    });

    const that = reactive({
      imgFile: null,
      loading: false,
      formInline: {},
      compileStatus: false,
      userRoleArray: [{id: 0, name: "平台管理员"}, {id: 1, name: "管理员"}, {id: 2, name: "普通用户"}],

      rules: {
        fullName: [{required: true, trigger: "change", validator: validateFullName}],
        phone: [{required: true, trigger: "change", validator: validatePhone}]
      }
    })

    const getUserDetails = () => {
      findUserDetailsById({ timer: new Date() }).then(result => {
        that.compileStatus = false;
        for (let key in result.data) result.data[key] = nullToDelete(result.data[key]);
        that.formInline = result.data;
        that.avater = that.formInline.userProfile;

        // 修改完成个人信息，同步更新  userInfo
        let userInfo = JSON.parse(localStorage.getItem('USER_INFO'));
        userInfo.phone = result.data.phone;
        userInfo.userName = result.data.fullName;
        userInfo.userProfile = result.data.userProfile;
        store.dispatch('updateUserInfo', userInfo);
        localStorage.setItem("USER_INFO", JSON.stringify(userInfo));
      });
    };

    // 确认保存
    const formInlineRef = ref(null);
    const saveUserInfo = () => {
      formInlineRef.value.validate((valid) => {
        if (valid) {
          that.loading = true;
          let formData = new FormData();
          let formInline = JSON.parse(JSON.stringify(that.formInline));
          for (let key in formInline) formData.append(key, formInline[key]);      //字段参数
          if (that.avater !== formInline.userProfile)formData.append("imageFile", that.imgFile);         //用户更改了头像
          saveOrUpdateUserInfo(formData).then(() => {
            getUserDetails();
            that.loading = false;
            ElMessage({type: "success", showClose: true, message: "保存成功！"});
          }).catch(() => {
            that.loading = false;
          })
        }
      });
    };

    // 上传图片改变
    const uploadChange = (file, fileList) => {
      const isIMAGE = file.type === "image/jpeg" || "image/jpg" || "image/png";
      const isLt1M = file.size / 1024 / 1024 < 2;
      if (!isIMAGE) {
        ElMessage({type: "error", showClose: true, message: "上传图片格式只能为jpg、png、jpeg!"});
        fileList.splice(fileList.length - 1, 1);
        return;
      }
      if (!isLt1M) {
        ElMessage({type: "error", showClose: true, message: "上传文件大小不能超过 2MB!"});
        fileList.splice(fileList.length - 1, 1);
        return;
      }
      that.imgFile = file.raw;
      that.compileStatus = true;
      that.formInline.userProfile = URL.createObjectURL(file.raw);
    }

    onMounted(() => {
      getUserDetails();
    });

    return {...toRefs(that), userInfo, getUserDetails, saveUserInfo, formInlineRef, uploadChange}
  }
};
</script>

<style lang="scss" scoped>
.System {
  flex: 1;

  .user_avatar {
    display: flex;
    justify-content: center;
    padding-bottom: 16px;
    box-sizing: border-box;

    .userAvater {
      width: 104px;
      height: 104px;
      border-radius: 50%;
      position: relative;

      .el-avatar {
        width: 100%;
        height: 100%;
        font-size: 20px;
        font-weight: 600;
      }

      .rightBttom {
        width: 32px;
        height: 32px;
        position: absolute;
        right: 0;
        bottom: 0;
        background: #FFFFFF;
        border-radius: 50%;
        padding: 2px;

        .compileButton {
          width: 100%;
          height: 100%;
          border-radius: 50%;
          font-size: 12px;
          color: #1F74E2;
          background: #E8F2FF;
          display: flex;
          justify-content: center;
          align-items: center;
          cursor: pointer;
        }
      }
    }
  }

  .main-button {
    display: flex;
    width: 300px;
    justify-content: space-around;
    margin-top: 48px;
  }
}
</style>
