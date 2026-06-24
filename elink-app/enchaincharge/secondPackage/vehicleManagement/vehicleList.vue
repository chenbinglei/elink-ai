<template>
	<view class="container">
		<view class="container_content">
			<view class="car_list">
				<template v-if="dataList && dataList.length">
					<car-list-card v-for="(item, index) in dataList" :key="index" :carInfo="item" @refresh="findCarInfoByMemberId"></car-list-card>
				</template>
				<sm-null-data v-else slot="empty" title="当前暂无车辆"></sm-null-data>
			</view>
		</view>
		<view class="bottom_but">
			<view class="add_black" @click="goToPage">
				<text class="iconfont icon-tianjia"></text>
				<text class="text">添加车辆</text>
			</view>
		</view>
	</view>
</template>

<script>
	import { queryCarInfoByMemberId } from "@/secondPackage/api/index.js";
	import CarListCard from "@/secondPackage/components/vehicleList/CarListCard.vue";
	export default {
		name: "vehicleList",
		components:{ CarListCard },
		data() {
			return {
				dataList: [],
			}
		},
		onShow() {
			this.findCarInfoByMemberId();
		},
		methods: {
			findCarInfoByMemberId(){
				queryCarInfoByMemberId({}).then(res => {
					//请勿在网络请求回调中给dataList赋值！！只需要调用complete就可以了
					this.dataList = res.data;
				}).catch(e => {
					console.log(e);
				});
			},
			goToPage() {
				uni.navigateTo({
					url: `/secondPackage/vehicleManagement/addVehicle`
				});
			}
		}
	}
</script>

<style scoped lang="scss">
	.container_content{
		display: initial;
		// display: flex;
		// justify-content: center;
		// align-items: center;
		
		.car_list{
			width: 100%;
			height: 100%;
			padding: 24rpx 32rpx;
			box-sizing: border-box;
			overflow-y: auto;
		}
	}
	
	.bottom_but{
		width: 100%;
		padding: 12rpx 32rpx 40rpx;
		box-sizing: border-box;
		background-color: #FFFFFF;
		box-shadow: 0px 0px 16rpx 0px rgba(0, 0, 0, 0.1);
		
		.add_black{
			height: 88rpx;
			background: #476AE2;
			border-radius: 16rpx;
			display: flex;
			align-items: center;
			justify-content: center;
			
			.iconfont,.text {
				color: #FFFFFF;
				font-size: 36rpx;
				font-weight: 500;
			}
		}
	}
</style>
