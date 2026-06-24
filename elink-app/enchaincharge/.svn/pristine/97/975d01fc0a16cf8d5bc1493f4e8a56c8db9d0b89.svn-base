<template>
	<view class="container">
		<view class="header_title">
			<view class="title">请输入短信验证码</view>
			<view class="phoneNumber">短信验证码已发送至 {{ phoneNumber }}</view>
			<view class="timer">
				<text v-if="codeState">{{ countDown }}后可重新发送</text>
			</view>
		</view>
		<view class="centent">
			<view class="centent_top"><smh-verification-code :unit="6" :focus="true" @change="handleCode"></smh-verification-code></view>
			<view class="centent_bottom flex-between">
				<view class="bottom_left" v-if="!codeState" @click="sendCode">重新发送</view>
				<view class="" v-else></view>
				<view class="bottom_right">收不到验证码?</view>
			</view>
		</view>
	</view>
</template>

<script>
import { mapState } from 'vuex';
import { sendSecurityCode, checkSecurityCode } from '@/thirdPackage/api/login.js';

export default {
	name: 'sendVerCode',
	computed: {
		...mapState(['userInfo'])
	},
	data() {
		return {
			timer: null,
			countDown: 59,
			vertificode: '',
			phoneNumber: '', // 当前手机号
			codeState: false, // 是否可获取验证码

			pageType: '', // 来自那个页面
			customHeadText: '发送验证码'
		};
	},
	onLoad(options) {
		this.pageType = options.pageType;
		this.phoneNumber = this.userInfo.mobile; // 当前登录手机号

		// 1：更换手机号（第一次校验）
		if (this.pageType == 1) {
			this.customHeadText = '更换手机号';
		}

		// 2: 更换新手机号验证
		if (this.pageType == 2) {
			this.customHeadText = '更换手机号';
			this.phoneNumber = options.phoneNumber;
		}

		// 3: 注销账号
		if (this.pageType == 3) {
			this.customHeadText = '注销账号';
		}
		
		uni.setNavigationBarTitle({ title: this.customHeadText });
		this.sendCode();
	},
	methods: {
		//获取验证码
		sendCode() {
			if (this.codeState) return;

			this.codeState = true;
			sendSecurityCode({ mobile: this.phoneNumber }).then(res => {
				uni.showToast({ title: res.message, icon: 'none' });
				this.timer = setInterval(() => {
					this.countDown--;
					if (this.countDown <= 0) this.clearCodeTimer();
				}, 1000);
			}).catch(() => {
				this.clearCodeTimer();
			});
		},
		handleCode(code) {
			this.vertificode = code;
			// console.log(this.vertificode);
			checkSecurityCode({ mobile: this.phoneNumber, smscode: this.vertificode }).then(res => {
				uni.showToast({
					icon: 'none',
					duration: 2500,
					title: '校验成功',
					success: () => {
						let pages = getCurrentPages(); //当前页面栈
						let prePage = pages[pages.length - 2];
						
						// 更换手机号功能跳转过来的，成功后更换手机号页面
						if (this.pageType == 1) {
							uni.navigateTo({ url: "/thirdPackage/accountSafe/changePhone" });
							return
						}
						
						prePage.$vm.sendVerCodeEvent({ operationType: this.pageType });
					}
				});
			});
		},
		clearCodeTimer() {
			this.codeState = false;
			this.codeText = '获取验证码';
			clearInterval(this.codeTimer);
		},
		goToPage(pageName) {
			if (pageName) {
				uni.navigateTo({ url: pageName });
			}
		}
	}
};
</script>

<style scoped lang="scss">
.container {
	padding-top: 260rpx;
	box-sizing: border-box;
	background-color: #ffffff;

	.header_title {
		display: flex;
		flex-direction: column;
		align-items: center;
		.title {
			font-size: 58rpx;
			font-weight: 500;
			color: #242424;
		}
		.phoneNumber {
			font-size: 26rpx;
			color: #a7a9ad;
			margin-top: 8rpx;
			margin-bottom: 56rpx;
		}
		.timer {
			font-size: 32rpx;
			color: #476ae2;
		}
	}

	.centent {
		padding: 0 40rpx;
		box-sizing: border-box;
		margin-top: 92rpx;

		.centent_bottom {
			margin-top: 30rpx;
			font-size: 30rpx;
			color: #242424;
		}
	}
}
</style>
