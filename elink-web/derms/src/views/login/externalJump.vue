<template>
  <div class="externalJump">
    <span class="alter_text">正在进行跳转...</span>
  </div>
</template>

<script lang="ts">
import {ElMessage} from "element-plus";
import {useRoute, useRouter} from "vue-router";
import {getToken, setToken} from "@/utils/auth";
import {getPasswordByAccount, login} from "@/api/login/login";
import {queryUserAuthorityIsHaveFun, selectTreeData} from "@/utils";
import {defineComponent, nextTick, onMounted, reactive, toRefs} from "vue";

export default defineComponent({
  name: "externalJump",
  setup() {
    const route = useRoute();
    const vueRouter = useRouter();

    const that = reactive({
      params: {}
    });

    // 开始跳转
    const startJumpRouteFun = () => {
      let isUserLogin = getToken(); // 查看用户是否进行登录
      let userLoginSign = localStorage.getItem("USER_LOGIN_SIGN"); //当前登录用户信息
      try {
        that.params = JSON.parse(decodeURIComponent(route.query?.params ?? {}));  // 解析路由参数
        if (!that.params.routeName) that.params.routeName = "/centralMonitoring/ChargingPileMonitoring"; // 默认进入电桩监控
      } catch (e) {
        ElMessage({message: "参数解析错误，请检查！", showClose: true, type: "error", duration: 5000});
        return;
      }
      // console.log(that.params);

      if (isUserLogin && isUserLogin === userLoginSign) {
        console.log("可以开始页面跳转");
        localStorage.removeItem("USER_LOGIN_SIGN"); // 清除本地登录标识
        setTimeout(() => nextTick(() => executeRouteJumpFun()), 50);
        return;
      }

      if (!that.params.account) {
        ElMessage({message: "未携带用户登录信息，请检查参数！", showClose: true, type: "error", duration: 5000});
        return;
      }

      // 根据用户账号获取密码
      getPasswordByAccount({ userAccount: that.params.account }).then(res=>{
        that.params.password = res.data;
        if(!that.params.password){
          ElMessage({message: "未获取到该账户密码，请检查账号是否正确！", showClose: true, type: "error", duration: 5000});
          return;
        }
        nextTick(() => handleLogin());   // 用户进行手动登录
      });
    };

    const handleLogin = () => {
      login({
        grant_type: "sys_pwd",
        client_id: "iems-client",
        client_secret: "iems-client",
        password: that.params.password,
        userAccount: that.params.account,
      }).then(result => {
        let returnDataInfo = result.data ? result.data : {};
        let userInfo = JSON.parse(JSON.stringify(returnDataInfo));
        delete userInfo.menuList;
        userInfo.userId = userInfo.id;
        setToken(userInfo.accessToken); //存储用户token
        localStorage.setItem("USER_INFO", JSON.stringify(userInfo)); //存储用户信息
        localStorage.setItem("USER_LOGIN_SIGN", userInfo.accessToken);
        localStorage.setItem("AUTH_ROUTER", JSON.stringify(returnDataInfo.menuList)); // 存储解析后的路由
        ElMessage({type: "success", showClose: true, message: "登录成功"});
        location.reload();
      });
    };

    // 开始进行路由跳转
    const executeRouteJumpFun = () => {
      // console.log(that.params);
      const isAuthority = queryUserAuthorityIsHaveFun(that.params?.routeName);
      if (!isAuthority) {
        ElMessage({type: "warning", showClose: true, message: "请联系管理员打开对应权限！"});
        return;
      }

      // 获取该路由的顶级权限名称
      let route_list = JSON.parse(localStorage.getItem("SIDEBAR"));
      const parentRouteList = selectTreeData(that.params?.routeName, 'path', route_list);
      // console.log(parentRouteList);

      // 进行路由跳转
      if (parentRouteList && parentRouteList.length) {
        vueRouter.replace({
          path: parentRouteList[0].path,
          state: {...that.params, routeList: parentRouteList[0].children}
        });
      }
    };

    onMounted(() => {
      // const params = encodeURIComponent(JSON.stringify({
      //   account: "smdl",
      //   siteId: "ff808081939ae2d30193b8a7cda9000c",
      // }))
      // console.log(params)
      // that.params = JSON.parse(decodeURIComponent(params));
      startJumpRouteFun();
    });

    return {...toRefs(that), route, startJumpRouteFun, handleLogin, executeRouteJumpFun};
  }
});

</script>

<style lang="scss" scoped>
.externalJump {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #000000;

  .alter_text {
    font-size: 16px;
    color: #FFFFFF;
  }
}
</style>