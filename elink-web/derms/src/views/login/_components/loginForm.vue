<template>
  <div class="box-border login-form" v-loading="loading">
    <div
      class="w-full py-35px flex justify-center items-center text-24px text-white"
    >
      账号登录
    </div>
    <el-form
      :model="formData"
      :rules="rules"
      ref="loginForm"
      hide-required-asterisk
    >
      <el-form-item label="账号" prop="username" class="login-input-item">
        <template #label>
          <div class="w-122px h-52px flex justify-center items-center">
            <img
              src="/img/login/loginFormAccountLogo.webp"
              alt="账号"
              class="w-122px h-52px"
            />
            <span class="absolute pl-10px text-20px">账号</span>
          </div>
        </template>
        <el-input
          class="login-form-input w-361px h-52px"
          v-model="formData.username"
          placeholder="请输入账号"
        />
      </el-form-item>
      <el-form-item label="密码" prop="password" class="login-input-item">
        <template #label>
          <div class="w-122px h-52px flex justify-center items-center">
            <img
              src="/img/login/loginFormPasswordLogo.webp"
              alt="密码"
              class="w-122px h-52px"
            />
            <span class="absolute pl-10px text-20px">密码</span>
          </div>
        </template>
        <el-input
          class="login-form-input w-361px h-52px"
          v-model="formData.password"
          type="password"
          placeholder="请输入密码"
        />
      </el-form-item>
      <el-form-item>
        <el-checkbox class="fancy-checkbox" v-model="formData.remember"
          >记住密码</el-checkbox
        >
      </el-form-item>
      <el-form-item>
        <div class="w-full flex justify-center items-center">
          <el-button
            type="default"
            class="text-22px font-bold fancy-submit"
            @click="handleLogin"
            >登录</el-button
          >
        </div>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from "vue";
import { useRouter, useRoute } from "vue-router";
import { ElMessage } from "element-plus";
import Crypto from "@/common/crypto.js";
import { login } from "@/api/login/login";
import { isValidPassword } from "@/utils/validate";
import { setToken, getToken, removeToken } from "@/utils/auth";

const props = defineProps({
  clientId: {
    type: String,
    default: "iems-client",
  },
});
const router = useRouter();
const route = useRoute();
const { query } = route;
const loading = ref(false); //登录按钮状态
const formData = reactive({
  username: "",
  password: "",
  remember: false,
});

const validatePassword = (rule, value, callback) => {
  if (!isValidPassword(value)) {
    callback(new Error("请输入正确的密码"));
  } else {
    callback();
  }
};
const rules = {
  username: [{ required: true, message: "请输入账号", trigger: "blur" }],
  password: [
    { required: true, message: "请输入密码", trigger: "blur" },
    {
      message: "请输入正确的密码",
      validator: validatePassword,
      trigger: "change",
    },
  ],
};

const loginForm = ref(null);

const handleLogin = () => {
  loginForm.value.validate((valid) => {
    if (valid) {
      loading.value = true;
      login({
        grant_type: "sys_pwd",
        client_id: props.clientId,
        client_secret: props.clientId,
        userAccount: formData.username,
        password: Crypto.CBC_encrypt(formData.password),
      })
        .then((result) => {
          let returnDataInfo = result.data ? result.data : {};
          let userInfo = JSON.parse(JSON.stringify(returnDataInfo));
          delete userInfo.menuList;
          userInfo.userId = userInfo.id;
          setToken(userInfo.accessToken); //存储用户token
          localStorage.setItem("USER_INFO", JSON.stringify(userInfo)); //存储用户信息
          console.log(returnDataInfo.menuList,'returnDataInfo.menuList')
          localStorage.setItem(
            "AUTH_ROUTER",
            JSON.stringify(returnDataInfo.menuList)
          ); // 存储解析后的路由
          formData.remember
            ? setToken(JSON.stringify(formData), "remember", 7)
            : removeToken("remember"); // 使用cookies 记住密码，保存7天
          //这个reload的非常关键，它会让整个APP重启，从而重新加载路由（这个时候从缓存中取出登录成功后获得的路由权限数据）
          ElMessage({ type: "success", showClose: true, message: "登录成功" });
          //单站点直接进系统监控
          if (props.clientId === "microgrid-client") {
            router.replace(`/monitor/${query.id}`);
          } else {
            location.reload();
          }
        })
        .finally(() => {
          loading.value = false; //登录完成
        });
    }
  });
};
//

onMounted(() => {
  //  查看是是否记密码 ， 吧密码填入框
  if (getToken("remember")) {
    const rememberedForm = JSON.parse(getToken("remember")) || {};
    const { username, password } = rememberedForm;
    Object.assign(formData, {
      username,
      password,
      remember: true,
    });
  }
});
</script>

<style lang="scss" scoped>
@keyframes autofill {
  to {
    background: transparent;
  }
}

.login-form {
  width: 683px;
  height: 556px;
  background-image: url("/img/login/loginFormBg.webp");
  background-repeat: no-repeat;
  background-size: 100% 100%;
  padding: 55px 86px;
  box-sizing: border-box;
}

.login-input-item {
  :deep(label) {
    padding: 0 4px 0 0;
  }
}

.login-form-input {
  background-image: url("/img/login/loginInputBg.webp");
  background-repeat: no-repeat;
  background-size: 100% 100%;
  --el-input-bg-color: transparent;
  --el-input-border-color: transparent;

  :deep(input) {
    // 设计自动填充时背景色
    &:-webkit-autofill {
      -webkit-text-fill-color: #ffffff !important;
      -webkit-box-shadow: 0 0 0px 1000px transparent inset !important;
      transition: background-color 5000s ease-in-out 0s !important;
      background-color: transparent !important;
    }
  }
}

.fancy-checkbox {
  --el-checkbox-bg-color: transparent;
  --el-checkbox-checked-bg-color: transparent;
  --el-checkbox-checked-input-border-color: #018eb5;
  --el-checkbox-input-border: 1px solid #018eb5;
  --el-checkbox-input-height: 22px;
  --el-checkbox-input-width: 22px;

  :deep(.is-checked) {
    .el-checkbox__inner {
      display: flex;
      justify-content: center;
      align-items: center;

      &::after {
        content: "";
        position: relative;
        border: none;
        left: auto !important;
        top: auto !important;
        width: 13px;
        height: 13px;
        background-image: url("/img/login/loginRemember.webp");
        background-repeat: no-repeat;
        background-size: 100% 100%;
        transform: none;
      }
    }
  }

  :deep(.el-checkbox__label) {
    color: #00abd7;
  }
}

.fancy-submit {
  width: 310px;
  height: 50px !important;
  background-color: transparent;
  border: none;
  background-image: url("/img/login/loginBtnBg.webp");
  background-repeat: no-repeat;
  background-size: 100% 100%;
  --el-button-hover-text-color: #ffffff;

  :deep(span) {
    font-size: 22px;
  }

  &:hover {
    filter: brightness(1.2);
  }
}
</style>
