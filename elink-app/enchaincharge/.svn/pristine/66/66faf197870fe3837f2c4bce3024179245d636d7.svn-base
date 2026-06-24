<template>
	<view class="container">
		<view class="container_content">
			<CustomHead title="账号安全"></CustomHead>
			<view class="container_flex">
				<sm-card-list leftText="更换手机" @cardEvent="goToPage('/thirdPackage/accountSafe/sendVerCode?pageType=1')"></sm-card-list>
				<sm-card-list leftText="注销账户" @cardEvent="goToPage('/thirdPackage/accountSafe/accounCtancellation')"></sm-card-list>
			</view>
		</view>
		<!-- <view class="bgImage"><image src="@/static/image/pageBgImage.png" ></image></view> -->
	</view>
</template>

<script>
export default {
	name: 'accountSafe',
	data() {
		return {};
	},
	methods: {
		goToPage(pageName) {
			if (pageName) {
				uni.navigateTo({ url: pageName });
			}
		},
		// 验证码 验证成功回调函数
		sendVerCodeEvent(data){
			
			// 更换手机号,输入新手机号
			if(data.operationType == 1){
				uni.redirectTo({ url: '/thirdPackage/accountSafe/changePhone' });
			}
			
		}
	}
};
</script>

<style scoped lang="scss">
.container_flex {
	padding: 30rpx 40rpx 10rpx 40rpx;
	box-sizing: border-box;
}
</style>
