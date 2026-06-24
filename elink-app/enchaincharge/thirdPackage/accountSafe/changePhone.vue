<template>
	<view class="container">
		<view class="container_content">
			<CustomHead title="更换手机号"></CustomHead>
			<view class="container_flex">
				<view class="header_title">
					<view class="title">绑定手机</view>
					<view class="phoneNumber">为了您的账号安全，请绑定手机号</view>
				</view>
				<view class="centent">
					<view class="centent_top">
						<uni-easyinput
							class="uni-input"
							type="number"
							v-model="phoneNumber"
							placeholder="请输入手机号"
							:maxlength="11"
							:inputBorder="false"
							:styles="inputStyle"
						></uni-easyinput>
					</view>
					<view class="centent_bottom"><view class="button" @click="goToPage">获取验证码</view></view>
				</view>
			</view>
		</view>
		<!-- <view class="bgImage"><image src="@/static/image/pageBgImage.png" ></image></view> -->
	</view>
</template>

<script>
import Crypto from '@/utils/crypto.js';
import { mobile } from '@/common/validate.js';
import { updateAppletUserPhoneById } from '@/thirdPackage/api/index.js';

export default {
	name: 'changePhone',
	data() {
		return {
			inputStyle: {
				height: '96rpx',
				color: '#454545',
				backgroundColor: '#FFFFFF',
				borderColor: '#FFFFFF',
				borderRadius: '6px',
				fontSize: '32rpx'
			},

			phoneNumber: ''
		};
	},
	methods: {
		goToPage() {
			if (!mobile(this.phoneNumber)) {
				uni.showToast({ icon: 'none', title: '请输入正确的手机号！' });
				return;
			}

			uni.navigateTo({ url: '/thirdPackage/accountSafe/sendVerCode?pageType=2&phoneNumber=' + this.phoneNumber });
		},
		// 验证码 验证成功回调函数
		sendVerCodeEvent(data) {
			let phoneNumber = Crypto.CBC_encrypt(this.phoneNumber);
			updateAppletUserPhoneById({ phoneNum: phoneNumber }).then(res => {
				//修改缓存的用户数据
				let userInfo = uni.getStorageSync('USER_INFO');
				
				userInfo.phone = phoneNumber;
				userInfo.mobile = this.phoneNumber;
				this.$store.dispatch('alterUserInfo', userInfo);
				uni.setStorage({ key: 'USER_INFO', data: userInfo });
				uni.showToast({
					icon: 'success',
					title: '手机更换成功',
					success: () => {
						// uni.navigateBack({ delta: 3 });
						uni.switchTab({ url:"/pages/workbench/index" });
					}
				});
			});
		}
	}
};
</script>

<style scoped lang="scss">
.container_flex {
	padding: 100rpx 40rpx 20rpx 40rpx;
	box-sizing: border-box;

	.header_title {
		.title {
			color: #242424;
			font-size: 58rpx;
			font-weight: 500;
		}

		.phoneNumber {
			color: #a7a9ad;
			font-size: 26rpx;
			margin-top: 8rpx;
		}
	}

	.centent {
		margin-top: 150rpx;

		.centent_bottom {
			margin-top: 32rpx;

			.button {
				height: 98rpx;
				background: #15e8af;
				border-radius: 12rpx;
				font-size: 36rpx;
				font-weight: 600;
				color: #083329;
				text-align: center;
				line-height: 98rpx;
			}
		}
	}
}
</style>
