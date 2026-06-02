<template>
  <div class="accountPasswordLogin flex-jc-ai-center">
    <div class="login_logo">欢迎登录</div>
    <el-form ref="formDialogRef" v-loading.fullscreen.lock="fullscreenLoading" :model="loginForm" :rules="rules">
      <el-form-item prop="username">
        <el-input v-model="loginForm.username" clearable name="username" placeholder="请输入账号" type="text">
          <template #prefix>
            <span class="iconfont icon-denglu-zhanghao"></span>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item prop="password">
        <el-input v-model="loginForm.password" placeholder="请输入密码" show-password type="password" @keyup.enter="handleLogin">
          <template #prefix>
            <span class="iconfont icon-denglu-mima"></span>
          </template>
        </el-input>
      </el-form-item>
      <div class="displayFlex">
        <el-checkbox v-model="remember" label="记住密码" size="large"/>
        <div class="forget" @click="setPassword">忘记密码</div>
      </div>
      <el-button :loading="loading" class="loginBut" type="primary" @click.prevent="handleLogin">登 录</el-button>
    </el-form>
  </div>
</template>

<script>
import {ElMessage} from "element-plus";
import Crypto from '@/common/crypto.js';
import {login} from "@/api/login/login";
import {isValidPassword} from "@/utils/validate";
import {setToken, getToken, removeToken} from "@/utils/auth";
import {onMounted, toRefs, reactive, ref, defineComponent} from "vue";
// import userInfoJson from "@/router/route.json";

export default defineComponent({
  name: "AccountPasswordLogin",
  setup() {

    const validateUsername = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请输入用户账号"));
      } else {
        callback();
      }
    };

    const validatePassword = (rule, value, callback) => {
      if (!isValidPassword(value)) {
        callback(new Error("请输入正确的密码"));
      } else {
        callback();
      }
    };

    const that = reactive({
      loginForm: {},
      loading: false, //登录按钮选择状态
      remember: false,  // 是否记住密码
      fullscreenLoading: false, //表单禁用状态，
      rules:{
        username: [{required: true, trigger: "change", validator: validateUsername}],
        password: [{required: true, trigger: "change", validator: validatePassword}],
      }
    });

    //登录
    const formDialogRef = ref(null);
    const handleLogin = () => {
      formDialogRef.value.validate((valid) => {
        if (valid) {
          that.loading = true; //开始登录
          that.fullscreenLoading = true;

          login({
            grant_type: "sys_pwd",
            client_id: "tycvs-client",
            client_secret: "tycvs-client",
            userAccount: that.loginForm.username,
            password: Crypto.CBC_encrypt(that.loginForm.password)
          }).then(result => {
            let userInfo = JSON.parse(JSON.stringify(result.data));
            delete userInfo.menuList;
            userInfo.userId = userInfo.id;
            setToken(userInfo.accessToken); //存储用户token
            localStorage.setItem("USER_INFO", JSON.stringify(userInfo)); //存储用户信息
            ElMessage({type: "success", showClose: true, message: "登录成功"});
            // 使用cookies 记住密码，保存7天
            that.remember ? setToken(JSON.stringify(that.loginForm), "remember", 7) : removeToken("remember");
            getRoutes(result.data.menuList); // 处理页面权限
            // getRoutes(userInfoJson.menuList); // 处理页面权限
          }).catch(() => {
            that.loading = false;
            that.fullscreenLoading = false;
          });
        }
      })
    };

    const getRoutes = (menuList = []) => {
      try {
        let routeList = [];
        if(menuList && menuList.length){
          //获取路由权限树数据，转换格式后存储至localStorage
          let auths = JSON.parse(JSON.stringify(menuList));

          for (let i = 0; i < auths.length; i++) {
            routeList[i] = {};
            routeList[i].id = auths[i].id; //表示路由id
            routeList[i].name = auths[i].name; //表示路由名称
            routeList[i].router_path = auths[i].url;
            routeList[i].router_icon = auths[i].iconPath; //表示路由图标
            routeList[i].router_layout = auths[i].isLayout * 1; //表示路由有渲染界面
            routeList[i].router_hidden = auths[i].isHidden * 1; //表示路由是否显示
            routeList[i].router_order =  Number(auths[i].directoryDesc); //表示路由列表排序
            routeList[i].parent_id = auths[i].parentId ? auths[i].parentId : "root"; //表示路由父级id
          }

          // 数据库索引排序
          routeList = routeList.sort( (a, b)=> {
            return a.router_order > b.router_order ? -1 : a.router_order < b.router_order ? 1 : 0;
          });
        }
        that.loading = false;
        that.fullscreenLoading = false;
        localStorage.setItem(`AUTH_ROUTER`, JSON.stringify(routeList)); // 存储解析后的路由
        //这个reload的非常关键，它会让整个APP重启，从而重新加载路由（这个时候从缓存中取出登录成功后获得的路由权限数据）
        location.reload();
      } catch (e) {
        console.log(e)
      }
    };

    //忘记密码按钮
    const setPassword = () => {
      ElMessage({type: "warning", showClose: true, message: "请联系超级管理员重置密码"});
    };

    onMounted(() => {
      // console.log(Crypto.CBC_encrypt("sm123456"))
      //  查看是是否记密码 ， 吧密码填入框
      if (getToken("remember")) {
        that.remember = true;
        that.loginForm = JSON.parse(getToken("remember"));
      }
    });

    return {...toRefs(that), setPassword, getRoutes, handleLogin, formDialogRef};
  }
})
</script>

<style lang="scss" scoped>
.accountPasswordLogin {
  border-radius: 21px;
  background: #FFFFFF;
  flex-direction: column;
  box-sizing: border-box;
  padding: 62px 48px 62px 48px;
  box-shadow: 0 2px 12px 0 rgba(40,77,155,0.1);

  .login_logo {
    color: #121C3F;
    font-size: 32px;
    text-align: center;
    font-weight: 500;
    margin-bottom: 48px;
  }

  :deep(.el-input) {
    --el-input-width: 420px;
    --el-input-height: 54px;
    --el-font-size-base: 16px;
    --el-input-border-radius: 8px;
    --el-input-placeholder-color: rgba(0, 0, 0, .5);

    .el-input__suffix .el-icon {
      font-size: var(--el-font-size-base);
    }
  }

  .displayFlex {
    width: 100%;
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 58px;

    .el-checkbox {
      --el-checkbox-font-size: 14px;
      --el-checkbox-input-height: 16px;
      --el-checkbox-input-width: 16px;
    }

    .forget {
      color: #1F74E2;
      font-size: 14px;
      cursor: pointer;
    }
  }

  .loginBut {
    width: 100%;
    height: 54px;
    color: #FFFFFF;
    font-size: 24px;
    border-radius: 8px;
    background: #007FEB;
  }
}

@media (max-width: 768px) {
  .accountPasswordLogin {
    width: 85vw;
    padding: 16px 0;

    .login_logo {
      font-size: 24px;
    }

    :deep(.el-input) {
      --el-input-width: 100%;
      --el-input-height: 36px;
    }

    .displayFlex {
      margin-bottom: 16px;
    }

    .loginBut {
      height: 38px;
      font-size: 16px;
    }
  }
}
</style>
