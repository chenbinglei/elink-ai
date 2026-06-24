<template>
	<view class="occupyOrder" v-if="occupyArray&&occupyArray.length" @click="clickOccupy">
		<uni-icons type="sound" size="20"></uni-icons>
		<view class="center_text">
			<text>您有{{ occupyArray ? occupyArray.length : 0 }}笔占用订单未支付，</text>
			<text class="underline">点击查看</text>
		</view>
	</view>
</template>

<script>
	import { findAppInHandOrderListByMemberId } from "@/api/home.js";
	
	export default{
		name: "OccupyOrder",
		props:{
			occupyOrderNum:{
				type: [Number,String],
				default: 0
			}
		},
		data(){
			return{
				occupyArray:[],  // 占用订单
			}
		},
		methods:{
			queryAppInHandOrderListByMemberId(){
				findAppInHandOrderListByMemberId({ orderType: 3 }).then(res=>{
					this.occupyArray = res.data ? res.data : [];
				})
			},
			clickOccupy(){
				uni.navigateTo({
					url: "/secondPackage/orderManagement/occupyOrderList"
				})
			}
		}
	}
</script>

<style scoped lang="scss">
	.occupyOrder{
		height: 76rpx;
		display: flex;
		align-items: center;
		padding: 0 32rpx;
		box-sizing: border-box;
		transition: all .28s;
		background: rgba(248, 213, 188, 1);
		
		.center_text{
			color: #030303;
			font-size: 26rpx;
			margin-left: 16rpx;
			
			.underline{
				text-decoration: underline;
			}
		}
	}
</style>