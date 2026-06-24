<template>
	<view class="container">
		<view class="container_content">
			<CustomHead></CustomHead>

			<view class="container_flex">
				<view class="title">手机号登录/注册</view>
				<view class="centent">
					<view class="centent_input">
						<uni-easyinput class="uni-input" type="number" v-model="phoneNumber" placeholder="请输入手机号" :maxlength="11" :inputBorder="false" :styles="inputStyle"></uni-easyinput>
					</view>

					<view class="bottom_agreement uni-flex">
						<image class="agreement_left" src="@/thirdPackage/static/image/check_yes.png" ></image>
						<view class="agreement_center">
							<text class="normal">登录代表您已同意</text>
							<text class="fontColor">用户协议、</text>
							<text class="fontColor">隐私协议</text>
						</view>
					</view>

					<view class="logBtn_text" @click="getVerCode">获取验证码</view>
				</view>
			</view>
		</view>

		<view class="bgImage"><image src="@/static/image/pageBgImage.png" ></image></view>
	</view>
</template>

<script>
import { mobile } from '@/utils/validate.js';
export default {
	name: 'mobilelogin',
	data() {
		return {
			phoneNumber: '',
			inputStyle: {
				fontSize: '28rpx',
				color: '#727272',
				backgroundColor: 'rgba(0,0,0,0)'
			}
		};
	},
	methods: {
		getVerCode() {
			if (!mobile(this.phoneNumber)) {
				uni.showToast({ icon: 'none', title: '请输入正确的手机号' });
				return;
			}

			uni.navigateTo({
				url: `/thirdPackage/login/loginVerCode?phoneNumber=${this.phoneNumber}`
			});
		}
	}
};
</script>

<style scoped lang="scss">
.container_flex {
	padding-top: 260rpx;
	box-sizing: border-box;

	.title {
		font-size: 58rpx;
		font-weight: 500;
		color: #ffffff;
		padding-left: 54rpx;
		box-sizing: border-box;
		margin-bottom: 144rpx;
	}

	.centent {
		flex: 1;
		padding: 0 44rpx;
		box-sizing: border-box;

		.centent_input {
			border-bottom: 2rpx dotted #e0e0e0;
			padding-bottom: 20rpx;
		}

		.bottom_agreement {
			width: 100%;
			color: #939599;
			font-size: 26rpx;
			margin-top: 42rpx;

			.agreement_left {
				width: 30rpx;
				height: 32rpx;
				vertical-align: middle;
				margin-right: 28rpx;
			}
			.fontColor {
				color: #199d7c;
			}
		}

		.logBtn_text {
			height: 96rpx;
			line-height: 96rpx;
			font-size: 32rpx;
			font-weight: 500;
			color: #ffffff;
			background-color: #15e8af;
			border-radius: 6rpx;
			margin-top: 124rpx;
			text-align: center;
		}
	}
}
</style>
