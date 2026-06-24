<template>
	<view class="container">
		<view class="container_content">
			<view class="container_flex">
				<image class="logo_img" :src="appConfig.appletLogo" ></image>
				<view class="logo_text">{{ appConfig.appletName }}</view>
				<view class="versionNo">V{{ appConfig.versionNum | moreData }}</view>
			
				<view class="list_card">
					<sm-card-list leftText="用户协议" @cardEvent="goToPage('/thirdPackage/login/userAgreement')"></sm-card-list>
					<sm-card-list leftText="隐私协议" @cardEvent="goToPage('/thirdPackage/login/privacyAgreement')"></sm-card-list>
					<sm-card-list leftText="联系我们" :bottomLine="false" @cardEvent="goToPage('/thirdPackage/login/contactUs')"></sm-card-list>
				</view>
				
			</view>
		</view>
	</view>
</template>

<script>
import { mapState } from 'vuex';
export default {
	name: 'aboutUs',
	computed: {
		...mapState(['appConfig'])
	},
	data() {
		return {};
	},
	methods: {
		goToPage(pageName) {
			if (pageName) {
				uni.navigateTo({ url: pageName });
			}
		}
	}
};
</script>

<style scoped lang="scss">
.container_flex {
	padding: 94rpx 40rpx 20rpx 40rpx;
	box-sizing: border-box;
	align-items: center;
	.logo_img {
		width: 140rpx;
		height: 140rpx;
		border-radius: 24rpx;
		margin-bottom: 42rpx;
	}
	.logo_text {
		color: #242424;
		font-size: 38rpx;
		font-weight: 600;
		margin-bottom: 6rpx;
	}
	.versionNo {
		color: #242424;
		font-size: 28rpx;
		margin-bottom: 48rpx;
	}
	
	.list_card{
		width: 100%;
		background-color: #ffffff;
		margin: 64rpx 32rpx 200rpx 32rpx;
		padding: 30rpx 0 10rpx 0;
		box-sizing: border-box;
		border-radius: 24rpx;
	}
}
</style>
