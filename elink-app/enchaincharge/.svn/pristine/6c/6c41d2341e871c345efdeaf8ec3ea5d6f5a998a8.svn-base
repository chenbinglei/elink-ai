# smh-verification-code
# ### 安装方式

本组件符合[easycom](https://uniapp.dcloud.io/collocation/pages?id=easycom)规范，`HBuilderX 2.5.5`起，只需将本组件导入项目，在页面`template`中即可直接使用，无需在页面中`import`和注册`components`

### 基本用法

在 ``template`` 中的使用
```html
<!-- 基本用法 -->
<smh-verification-code :unit="4" :focus="true" :ontime="true" @change="handle"></smh-verification-code>
<!-- 属性说明 -->
unit:验证码的位数
focus:是否自动打开键盘，可点击验证码区域手动打开
ontime:是否实时返回数据，如果不设置，则是到验证码输入完成后返回，如设置为true，则每次输入都会返回数据

<!-- 事件用法 -->
@change对应的事件则是返回的验证码
methods: {
	handle(code){
		console.log(code)//code就是验证码
	}
}
