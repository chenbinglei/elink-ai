<template>
	<view class="container">
		<view class="content_top">
			<view class="logoImage">
				<image class="logo_img" :src="appConfig.appletLogo"></image>
				<view class="logo_text">{{ appConfig.appletName }}</view>
			</view>
		</view>

		<view class="content_bottom">
			<view class="logBtn">
				
				<template v-if="isAgreeWithAgre">
					<!-- #ifdef MP-WEIXIN -->
					<button class="btn-round" open-type="getPhoneNumber" @getphonenumber="auth">
						<text>手机号快捷登录</text>
					</button>
					<!-- #endif -->
					<!-- #ifdef MP-ALIPAY -->
					<button class="btn-round alipayBtn" open-type="getPhoneNumber" @getphonenumber="alipayAuth">
						<text>手机号快捷登录</text>
					</button>
					<!-- #endif -->
				</template>
				<template v-else>
					<!-- #ifdef MP-WEIXIN -->
					<button class="btn-round" @click="clickLoginBut">
						<text>手机号快捷登录</text>
					</button>
					<!-- #endif -->
					<!-- #ifdef MP-ALIPAY -->
					<button class="btn-round alipayBtn"  @click="clickLoginBut">
						<text>手机号快捷登录</text>
					</button>
					<!-- #endif -->
				</template>
				
				<!-- <view class="logBtn_text" @click="goToPage('/thirdPackage/login/mobilelogin')">其它手机号登录/注册</view> -->
			</view>
			<view class="bottom_agreement flex-center align-center">
				<view class="agreement_left" @click="isAgreeWithAgre = !isAgreeWithAgre">
					<radio style="transform: scale(0.7);" :checked="isAgreeWithAgre" color="#476AE2" />
				</view>
				<view class="agreement_center">
					<text class="normal">已阅读并同意</text>
					<text class="fontColor" @click="goToPage('/thirdPackage/login/userAgreement')">《用户协议》</text>
					<text class="normal">、</text>
					<text class="fontColor" @click="goToPage('/thirdPackage/login/privacyAgreement')">《隐私协议》</text>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	import { mapState } from 'vuex';
	import Crypto from '@/utils/crypto.js';
	import { login } from '@/thirdPackage/api/login.js';
	// console.log(Crypto.CBC_encrypt(53))
	
	export default {
		name: 'login',
		computed: {
			...mapState(['appConfig'])
		},
		data() {
			return {
				iv: '',
				code: '',
				loginType: '',
				encryptedData: '',
				authCode: '', // 支付宝认证code
				isAgreeWithAgre: false, // 是否确认同意协议
			};
		},
		onShow() {
			this.getloginCode();
		},
		methods: {
			clickLoginBut(){
				//需先同意协议
				if (!this.isAgreeWithAgre) {
					uni.showToast({ icon: 'none', title: '请先阅读并同意相关协议！' });
					return;
				}
			},
			// 获取登录code码
			getloginCode() {
				// #ifdef MP-WEIXIN
				uni.getProvider({
					service: 'oauth',
					success: res => {
						if (res.provider.indexOf('weixin') !== -1) this.loginType = 'weixin';
						if (res.provider.indexOf('apple') !== -1) this.loginType = 'apple';

						if (this.loginType) {
							uni.login({
								provider: this.loginType,
								onlyAuthorize: true,
								success: result => {
									// 	console.log(result);
									this.code = result.code;
								}
							});
						}
					}
				});
				// #endif
				// #ifdef MP-ALIPAY
				console.log('我是支付宝-登录！');
				// #endif
			},
			// 微信- 获取用户手机号并登录
			auth(e) {
				// console.log(e);
				//用户拒绝授权
				if (!e.detail.iv) {
					uni.showToast({ icon: 'none', title: '获取失败！' });
					return;
				}
				
				this.iv = e.detail.iv;
				this.encryptedData = e.detail.encryptedData;
				this.continueloginFun();
			},
			// 支付宝登录
			alipayAuth(e) {
				// console.log(e);

				//需先同意协议
				if (!this.isAgreeWithAgre) {
					uni.showToast({ icon: 'none', title: '请先阅读并同意相关协议！' });
					return;
				}

				let { encryptedData, errMsg } = e.detail;
				if (errMsg.indexOf("getPhoneNumber:fail") !== -1) {
					uni.showModal({
						title: "提示",
						cancelText: "我知道了",
						confirmText: "重新获取",
						content: "取消获取，可能会使部分服务无法使用，或页面信息显示不完整！"
					})
				}
				this.encryptedData = encryptedData;
				this.continueloginFun();
				// console.log(this.encryptedData);
			},
			continueloginFun() {
				login({
					// #ifdef MP-WEIXIN
					iv: this.iv,
					code: this.code,
					grant_type: 'applet',
					encryptedData: this.encryptedData,	
					// #endif
					// #ifdef MP-ALIPAY
					grant_type: 'alipay',
					authCode: this.authCode,
					encryptData: this.encryptedData,
					// #endif
					client_id: 'personality',
					client_secret: 'personality'
				}).then(res => {
					let loginData = res.data ? res.data : {};
					loginData.memberId = loginData.id; // 用户ID
					loginData.nickName = loginData.fullName; // 用户ID

					if (!loginData.phone) {
						this.getloginCode();
						uni.showToast({ icon: 'none', title: '登录失败，请重试！' });
						return;
					}
					
					let mobile = Crypto.CBC_decrypt(loginData.phone);
					loginData.mobile = mobile.substring(0, 11);
					uni.setStorage({ key: 'USER_INFO', data: loginData });
					this.$store.dispatch('alterUserInfo', loginData);
					uni.showToast({
						icon: 'none',
						title: '登录成功',
						success: () => {
							// 登录成功回退到上一页
							uni.navigateBack({ delta: 1 });
						}
					});
				}).catch(() => {
					this.getloginCode();
				});
			},
			goToPage(pageName) {
				uni.navigateTo({ url: pageName });
			}
		}
	};
</script>

<style lang="scss" scoped>
	.container {
		background-color: #ffffff;

		.content_top {
			padding-top: 138rpx;
			box-sizing: border-box;

			.logoImage {
				width: 100%;
				display: flex;
				flex-direction: column;
				align-items: center;
				justify-content: center;

				.logo_img {
					width: 160rpx;
					height: 160rpx;
					border-radius: 24rpx;
				}

				.logo_text {
					color: #242424;
					font-size: 32rpx;
					font-weight: bold;
					margin-top: 18rpx;
				}
			}
		}

		.content_bottom {
			flex: 1;
			padding-bottom: 60rpx;
			box-sizing: border-box;
			display: flex;
			flex-direction: column;
			justify-content: space-between;

			.logBtn {
				flex: 1;
				display: flex;
				flex-direction: column;
				align-items: center;
				justify-content: center;
				padding: 0 46rpx 0 44rpx;
				box-sizing: border-box;

				.btn-round {
					width: 100%;
					height: 96rpx;
					font-size: 34rpx;
					font-weight: 500;
					color: #FFFFFF;
					background-color: #476AE2;
					margin-bottom: 46rpx;

					display: flex;
					align-items: center;
					justify-content: center;

					image {
						width: 44rpx;
						height: 38rpx;
						margin-right: 20rpx;
					}
				}

				.alipayBtn {
					background-color: #476AE2;
				}

				.logBtn_text {
					color: #ffffff;
					font-size: 32rpx;
				}
			}

			.bottom_agreement {
				width: 100%;
				font-size: 26rpx;
				color: rgba(0,0,0,0.8);

				.fontColor {
					color: #476AE2;
				}
			}
		}
	}
</style>