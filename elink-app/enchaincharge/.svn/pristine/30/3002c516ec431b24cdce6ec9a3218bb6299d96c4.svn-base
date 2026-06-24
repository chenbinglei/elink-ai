<template>
	<view class="carListCard" @click="clickCarCard">
		
		<view class="car_top">
			<view class="car_top_left"></view>
			
			<view class="numberPlate">{{ carInfo.numberPlate | moreData }}</view>
			<view class="iconfont icon-shanchu" @click.stop="unbindVehicle"></view>
		</view>
		<view class="car_center">
			<image class="car_image" src="@/secondPackage/static/image/carExample.png"></image>
		</view>
		<view class="car_bottom">
			<view class="" v-if="carInfo.seriesName">{{ carInfo.seriesName | moreData }}</view>
			<view class="" v-else>{{ carInfo.brandName | moreData }}</view>
		</view>
		
		<view :class="['authentication', 'authentication' + carInfo.vehicleStatus]">{{ carInfo.vehicleStatus | vehicleStatus }}</view>
		
	</view>
</template>

<script>
	import { memberCarUnbind } from "@/secondPackage/api/index.js";
	export default {
		name: "CarListCard",
		props: {
			carInfo: {
				type: Object,
				default: () => {
					return {}
				}
			}
		},
		data() {
			return {
				
			}
		},
		methods: {
			clickCarCard() {
				uni.navigateTo({
					url: `/secondPackage/vehicleManagement/addVehicle?data=${encodeURIComponent(JSON.stringify(this.carInfo))}`
				});
			},
			// 解绑车辆
			unbindVehicle(){
				uni.showModal({
					title: '即将解绑您的爱车',
					content: `${ this.carInfo.brandName } ${ this.carInfo.seriesName } ${ this.carInfo.typeName ? this.carInfo.typeName : '' }`,
					confirmText: '解绑',
					confirmColor: '#FD393A',
					success: res => {
						if (res.confirm) {
							memberCarUnbind({ carId: this.carInfo.id }).then(res=>{
								uni.showToast({
									icon:'none',
									title: "车辆解绑成功！",
									success: () => {
										this.$emit('refresh');
									}
								})
							})
						}
					}
				});
			},
		}
	}
</script>

<style scoped lang="scss">
	.carListCard {
		padding: 12rpx 16rpx;
		border-radius: 12rpx;
		box-sizing: border-box;
		background-color: #ffffff;
		margin-bottom: 20rpx;
		position: relative;
		
		.car_top {
			display: flex;
			align-items: center;
			justify-content: space-between;
			
			.car_top_left{
				width: 110rpx;
			}
			
			.iconfont {
				color: #FD393A;
				margin-right: 12rpx;
			}
			
			.numberPlate {
				color: #c7c7c7;
			}
		}
		
		.car_center {
			display: flex;
			justify-content: center;
			margin: 24rpx 0;
			
			.car_image {
				width: 280rpx;
				height: 120rpx;
			}
		}
		
		.car_bottom {
			display: flex;
			justify-content: center;
			padding: 0 16rpx;
			box-sizing: border-box;
		}
		
		
		.authentication {
			width: 110rpx;
			color: #ffffff;
			text-align: center;
			padding: 4rpx 10rpx;
			box-sizing: border-box;
			background-color: #E2E2E2;
			border-top-left-radius: 12rpx;
			border-bottom-right-radius: 12rpx;
			position: absolute;
			left: 0;
			top: 0;
		}
		
		.authentication1 {
			background-color: #476AE2;
		}
	}
</style>
