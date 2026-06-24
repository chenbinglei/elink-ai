<template>
	<view class="container">
		<view class="container_content">
			<view class="container_flex">
				<view class="list_card">
					<!-- <sm-card-list leftText="消息推送" @cardEvent="goToPage('/thirdPackage/systemSettings/messagePush')"></sm-card-list> -->
					<sm-card-list leftText="账号注销" :bottomLine="false" @cardEvent="goToPage('/thirdPackage/accountSafe/accounCtancellation')"></sm-card-list>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
export default {
	name: 'personalMore', 
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
	
	.list_card{
		margin: 64rpx 32rpx 200rpx 32rpx;
		padding: 30rpx 0 10rpx 0;
		box-sizing: border-box;
		border-radius: 24rpx;
		background-color: #ffffff;
	}
}
</style>
