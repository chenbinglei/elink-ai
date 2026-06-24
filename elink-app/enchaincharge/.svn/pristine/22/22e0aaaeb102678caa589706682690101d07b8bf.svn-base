<template>
	<view class="container">
		<view class="container_content">
			<view class="container_flex">
				<view class="content_top">
					<view class="title">申请注销账号</view>
					<view class="alter_text">你提交的注销申请生效前，{{ appConfig.appletName }}团队将进行审核，以保证你的账号、权益以及资产安全。</view>
					<view class="list_text">
						<view class="list_li" v-for="(item, index) in listTextArray" :key="index">{{ item }}</view>
					</view>
				</view>
				<view class="content_bottom">
					<view class="bottom_agreement flex-center align-center">
						<!-- <image class="agreement_left" src="@/thirdPackage/static/image/check_yes.png" ></image> -->
						<view class="agreement_left" @click="isAgreeWithAgre = !isAgreeWithAgre">
							<radio style="transform: scale(0.7);width: 30rpx;height: 30rpx;" :checked="isAgreeWithAgre" color="#476AE2" />
						</view>
						<view class="agreement_center">
							<text class="normal">点击“申请注销”，代表您已同意</text>
							<text class="fontColor" @click="goToPage('/thirdPackage/accountSafe/cancellationAgreement')">《注销账号协议》</text>
						</view>
					</view>

					<view class="button" @click="applyCancellation">申请注销</view>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
import { mapState } from 'vuex';
import { submitAppletCancel } from '@/thirdPackage/api/index.js';

export default {
	name: 'accounCtancellation',
	computed: {
		...mapState(['appConfig', 'userInfo'])
	},
	data() {
		return {
			isAgreeWithAgre: false, // 是否确认申请注销
			listTextArray: ['1、账号没有进行中或未结算的订单', '2、积分等虚拟资产注销后将失效', '3、相关权益注销后将失效']
		};
	},
	methods: {
		goToPage(pageName) {
			if (pageName) {
				uni.navigateTo({ url: pageName });
			}
		},
		// 申请注销
		applyCancellation() {
			//需先同意协议
			if (!this.isAgreeWithAgre) {
				uni.showToast({ icon: 'none', title: '请先阅读并同意相关协议！' });
				return;
			}
			uni.showModal({
				title: '申请注销',
				content: `即将注销账号`,
				confirmText: '注销',
				confirmColor: '#939599',
				success: res => {
					if (res.confirm) {
						uni.navigateTo({ url: '/thirdPackage/accountSafe/sendVerCode?pageType=3' });
					}
				}
			});
		},
		// 验证码 验证成功回调函数
		sendVerCodeEvent(data) {
			submitAppletCancel({ appletName: this.appConfig.appletName }).then(res => {
				this.$clearUserInfo("账户已申请注销");
			});
		}
	}
};
</script>

<style scoped lang="scss">
.container_flex {
	padding: 30rpx 40rpx 50rpx 40rpx;
	box-sizing: border-box;
	background-color: #fff;

	.content_top {
		flex: 1;
		overflow: auto;

		.title {
			color: #000;
			font-size: 36rpx;
			font-weight: bold;
		}

		.alter_text {
			color: #a7a9ad;
			font-size: 24rpx;
			margin-top: 8rpx;
		}

		.list_text {
			margin-top: 48rpx;

			.list_li {
				color: #000;
				font-size: 28rpx;
				margin-bottom: 10rpx;
			}
		}
	}

	.content_bottom {
		margin-top: 20rpx;

		.bottom_agreement {
			width: 100%;
			color: #939599;
			font-size: 26rpx;
			margin-bottom: 12rpx;

			.agreement_left {
				// width: 30rpx;
				// height: 30rpx;
				vertical-align: middle;
				margin-right: 20rpx;
			}
			.fontColor {
				color: #1C7CE6;
			}
		}

		.button {
			height: 98rpx;
			background: #1C7CE6;
			border-radius: 49rpx;
			font-size: 36rpx;
			color: #ffffff;
			text-align: center;
			line-height: 98rpx;
		}
	}
}
</style>
