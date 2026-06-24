<template>
	<view class="container">
		<view class="text">{{ appConfig.appletName }}用户注册服务协议</view>
		<view class="text">晟曼电力科技有限公司（以下简称“我公司”或“我们”）根据您的充电需求向您提供电动汽车充电服务（以下简称“服务”），您访问和使用有关服务、应用程序提供的电动汽车充电服务适用本用户服务协议（以下简称“协议”）。</view>
		<view class="text">在您注册、使用有关应用程序及接受我公司提供的电动汽车充电服务之前，请您认真阅读本协议。您选择并使用服务即视为您已充分阅读并接受本协议的所有条款，本协议对双方具有法律约束力。</view>
		<view class="text">第一条用户注册</view>
		<view class="text textIndent">1.1 为使用我公司服务，您须在移动设备上下载“{{ appConfig.appletName }}”应用程序并进行注册，注册时您必须保证提供信息、移动电话号码等属于本人并始终保持真实有效，如因注册信息不真实、更新不及时或因其他个人原因遗失或灭失而引发的相关问题，将由您自行承担相应责任。您知晓并同意，您一经成为{{ appConfig.appletName }}注册用户，将会默认开通我公司服务账户，账号及密码默认为{{ appConfig.appletName }}账户的账号及密码，该账号及相应的密码由您负责保管，请勿转让、出售或借予他人使用，您应当对该账号进行的所有活动和事件承担法律责任。如您发现账号遭他人非法使用，请您立即通知我们。</view>
		<view class="text textIndent">1.2 如果您是个人签订本协议，您应具有完全民事行为能力；如果您是代表组织实体签订本协议，您应获得授权并遵守本《用户服务协议》（并约束该组织实体）。</view>
		<view class="text">第二条服务内容</view>
		<view class="text textIndent">您申请我公司向您提供电动汽车充电服务。您可通过下载并安装到移动设备上的“{{ appConfig.appletName }}”应用程序选择并使用上述服务。</view>
		<view class="text">第三条合同订立</view>
		<view class="text textIndent">您理解并同意，您通过{{ appConfig.appletName }}选择并使用我们的服务，即视为接受本《用户服务协议》并依据本协议与我公司达成了合约（以下简称“合约”）。</view>
		<view class="text">第四条服务使用</view>
		<view class="text textIndent">4.1 您可以通过{{ appConfig.appletName }}使用电动汽车充电服务。</view>
		<view class="text textIndent">4.2 我公司将做出合理的努力，让您获得服务。但这受制于您请求服务之时所在位置周边是否有可提供服务的充电站。</view>
		<view class="text">第五条担保及承诺</view>
		<view class="text textIndent">5.1 您保证，您向我们提供的信息真实、准确、完整。我们在任何时候都有权验证您所提供的信息，如我们发现您所提供的信息并非真实、准确和完整的，有权拒绝向您提供服务或拒绝您使用有关服务、应用程序。</view>
		<view class="text textIndent">5.2 您使用我公司服务或{{ appConfig.appletName }}，即表示您还同意以下事项：</view>
		<view class="text textIndent">a.您出于您个人用途使用服务或下载应用程序，并且不会转售给第三方。</view>
		<view class="text textIndent">b.您不会将服务或应用程序用于非法目的，包括（但不限于）发送或存储任何非法资料或者用于欺诈目的。</view>
		<view class="text textIndent">c.您不会利用服务或应用程序骚扰、妨碍他人或造成不便。</view>
		<view class="text textIndent">d.您不会影响网络的正常运行。</view>
		<view class="text textIndent">e.您不会尝试危害服务或应用程序。</view>
		<view class="text textIndent">f.当我们提出合理请求时，您会提供身份证明。</view>
		<view class="text textIndent">g.您将遵守国家/地区以及您在使用应用程序或服务时所处国家</view>
	</view>
</template>

<script>
import { mapState } from 'vuex';
export default {
	name: 'privacyAgreement',
	computed: {
		...mapState(['appConfig'])
	},
	data() {
		return {};
	},
	methods: {}
};
</script>

<style scoped lang="scss">
.container {
	width: 100%;
	background-color: #ffffff;
	padding: 10rpx 20rpx 100rpx 20rpx;
	box-sizing: border-box;

	.text {
		color: #242424;
		font-size: 24rpx;
	}

	.textIndent {
		text-indent: 2em;
	}
}
</style>
