<template>
	<view class="operateList">
		<view class="content_body">
			<view class="card_li" v-for="(item,index) in iconList" :key="index" @click="goToPage(item)">
				<view :class="['iconfont', item.icon]"></view>
				<view class="text">{{ item.name }}</view>
			</view>
		</view>
	</view>
</template>

<script>
	export default {
		name: "OperateList",
		data(){
			return{
				iconList: [{
						name: "扫码放电",
						icon: "icon-saoma",
						pageName: "/fourthPackage/pages/scanCode?startMode=2"
					},
					{
						name: "放电订单",
						icon: "icon-chargeAndDisRecord",
						pageName: "/secondPackage/orderManagement/orderlist?orderType=2"
					}
				]
			}
		},
		methods:{
			goToPage(item) {
				uni.navigateTo({ url: item.pageName });
			}
		}
	}
</script>

<style lang="scss" scoped>
	.operateList {
		padding: 24rpx 32rpx 0 32rpx;
		box-sizing: border-box;

		.content_body {
			display: flex;
			flex-wrap: wrap;
			border-radius: 12rpx;
			box-sizing: border-box;
			background-color: #FFFFFF;
			padding: 24rpx 32rpx;
			
			.card_li {
				width: 25%;
				display: flex;
				flex-direction: column;
				align-items: center;
			
				.iconfont {
					color: #476AE2;
					font-size: 48rpx;
					margin-bottom: 12rpx;
				}
			
				.text {
					font-size: 24rpx;
					color: rgba(0, 0, 0, 0.8);
				}
			}
		}
	}
</style>