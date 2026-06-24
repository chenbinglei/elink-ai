<template>
	<view class="container">
		<view class="container_content">
			<CustomHead></CustomHead>
			<view class="container_flex">
				<view class="header_title">
					<view class="title">请输入短信验证码</view>
					<view class="phoneNumber">短信验证码已发送至 {{ phoneNumber }}</view>
					<view class="timer">
						<text v-if="!codeState">{{ countDown }}后重新发送</text>
					</view>
				</view>
				<view class="centent">
					<view class="centent_top"><smh-verification-code :unit="6" :focus="true" @change="handleCode"></smh-verification-code></view>
					<view class="centent_bottom flex-between">
						<view class="bottom_left" v-if="codeState" @click="sendCode">重新发送</view>
						<view class="" v-else></view>
						<view class="bottom_right">收不到验证码?</view>
					</view>
				</view>
			</view>
		</view>

		<view class="bgImage"><image src="@/static/image/pageBgImage.png" ></image></view>
	</view>
</template>

<script>
import { mobile } from '@/utils/validate.js';
import { sendSecurityCode, checkSecurityCode } from '@/thirdPackage/api/login.js';
export default {
	name: 'mobilelogin',
	data() {
		return {
			phoneNumber: '',
			vertificode: '',
			codeState: true, // 是否可获取验证码
			timer: null,
			countDown: 59
		};
	},
	onLoad(options) {
		this.phoneNumber = options.phoneNumber;
		this.sendCode();
	},
	methods: {
		//获取验证码
		sendCode() {
			if (!this.codeState) return;
			this.codeState = false;
			sendSecurityCode({ mobile: this.phoneNumber })
				.then(res => {
					uni.showToast({ title: res.message, icon: 'none' });
					this.timer = setInterval(() => {
						this.countDown--;
						if (this.countDown == 0) {
							clearInterval(this.timer);
							this.countDown = 59;
							this.codeState = true;
						}
					}, 1000);
				})
				.catch(() => {
					this.codeState = true;
				});
		},
		handleCode(code) {
			this.vertificode = code;
			console.log(this.vertificode);
		}
	}
};
</script>

<style scoped lang="scss">
.container_flex {
	padding-top: 260rpx;
	box-sizing: border-box;

	.header_title {
		display: flex;
		flex-direction: column;
		align-items: center;
		.title {
			font-size: 58rpx;
			font-weight: 500;
			color: #ffffff;
		}
		.phoneNumber {
			font-size: 26rpx;
			color: #a7a9ad;
			margin-top: 8rpx;
			margin-bottom: 56rpx;
		}
		.timer {
			font-size: 32rpx;
			color: #15e8af;
		}
	}

	.centent {
		padding: 0 40rpx;
		box-sizing: border-box;
		margin-top: 92rpx;

		.centent_bottom {
			margin-top: 30rpx;
			font-size: 30rpx;
			color: #ffffff;
		}
	}
}
</style>
