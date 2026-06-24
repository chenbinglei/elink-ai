<template>
	<view class="site_li" @click="clickSite">
		<view class="li_title">
			<view class="li_title_left twoShowText">
				<u-parse :content="siteInfo.siteName ? siteInfo.siteName : '--'"></u-parse>
			</view>
			<view class="li_title_right" @click.stop="openLocationMap">
				<uni-icons type="paperplane-filled" size="12" color="#333333"></uni-icons>
				<view class="km_n">{{ siteInfo.distance | moreData }}km</view>
			</view>
		</view>
		<view class="site_lable">
			<view class="site_lable_li" v-if="siteInfo.parkCostType">{{ siteInfo.parkCostType | parkCostType }}</view>
			<template v-if="siteInfo.siteLabel && siteInfo.siteLabel.length">
				<view v-for="(item,index) in siteInfo.siteLabel" :key="index" class="site_lable_li">{{ item | moreData }}</view>
			</template>
		</view>
		
		<view class="site_address" v-if="siteInfo.stationAddress">{{ siteInfo.stationAddress | moreData }}</view>

		<view class="site_bottom">
			<view class="bottom_left">
				<view class="number">{{ siteInfo.chargePrice | moneyTwoNum }}</view>
				<view class="unit">元/度起</view>
			</view>
			<view v-if="!isShowRight" class="bottom_right">
				<view class="r_left">
					<view class="unit">
						<text class="text">快</text>
					</view>
					<view class="number">
						<view class="left_n">{{ siteInfo.fastIdleNum | moreData }}</view>
						<view class="right_n">/{{ siteInfo.fastTotalNum | moreData }}</view>
					</view>
				</view>
				<view class="r_left">
					<view class="unit">
						<text class="text">慢</text>
					</view>
					<view class="number">
						<view class="left_n">{{ siteInfo.slowIdleNum | moreData }}</view>
						<view class="right_n">/{{ siteInfo.slowTotalNum | moreData }}</view>
					</view>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
import uParse from '@/components/u-parse/u-parse.vue'
export default {
	name: 'SiteListCard',
	components:{uParse},
	props: {
		siteInfo: {
			type: Object,
			default: () => {
				return { };
			}
		},
		// 是否可点击
		isClick: {
			type: Boolean,
			default: true
		},
		// 右下角是否展示
		isShowRight: {
			type: Boolean,
			default: false
		}
	},
	data() {
		return {};
	},
	methods: {
		clickSite() {
			if(this.isClick && this.$lockUserIfLogin(2)){
				uni.navigateTo({
					url: `/firstPackage/pages/stationDetails?siteRecordId=${this.siteInfo.siteId}&distance=${this.siteInfo.distance}`,
					success: () => {
						this.$emit('siteListsEvent', this.siteInfo.siteId);
					}
				});
			}
		},
		openLocationMap(){
			// console.log(this.siteInfo);
			if(!this.siteInfo.location){
				uni.showToast({ icon:'none', title:"该站点未配置位置信息！" });
				return
			}
			
			try{
				let locationInfo = JSON.parse(this.siteInfo.location);
				// console.log(locationInfo);
				
				uni.openLocation({
					name: locationInfo.address,
					address: locationInfo.address,
					latitude: Number(locationInfo.latitude),
					longitude: Number(locationInfo.longitude),
					success: function () {
						console.log('success');
					},
					fail: function (err) {
						console.log(err);
						uni.showToast({ icon:'none', title:"打开地图失败！" });
					},
				});
				
			}catch(e){
				//TODO handle the exception
				uni.showToast({ icon:'none', title:"位置信息解析错误！" });
			}
		}
	}
};
</script>

<style scoped lang="scss">
.site_li {
	background: #ffffff;
	border-radius: 10rpx;
	margin-bottom: 20rpx;
	padding: 32rpx 38rpx 26rpx 36rpx;
	box-sizing: border-box;
	transition: all 0.28s;

	.li_title {
		display: flex;
		align-items: center;

		.li_title_left {
			flex: 1;
			-webkit-line-clamp: 1;
			font-size: 34rpx;
			font-weight: 600;
			color: #030303;
		}

		.li_title_right {
			display: flex;
			align-items: center;
			margin-left: 28rpx;
			
			.km_n{
				margin-left: 4rpx;
			}
		}
	}

	.site_lable {
		display: flex;
		flex-wrap: wrap;
		align-items: center;
		margin-top: 4rpx;
		
		.site_lable_li {
			height: 28rpx;
			line-height: 28rpx;
			color: #476AE2;
			font-size: 20rpx;
			padding: 0 12rpx;
			margin-right: 8rpx;
			border-radius: 4rpx;
			background: rgba(71, 106, 226, .13);
			
			&:last-child{
				margin-right: 0;
			}
		}
	}
	
	.site_address{
		color: #000000;
		font-size: 28rpx;
		margin-top: 4rpx;
	}

	.site_bottom {
		display: flex;
		align-items: center;
		justify-content: space-between;
		margin-top: 30rpx;

		.bottom_left {
			font-size: 20rpx;
			color: #7b7d7f;
			display: flex;
			align-items: flex-end;

			.number {
				font-size: 56rpx;
				font-weight: 500;
				color: #f3615b;
				margin-left: 4rpx;
			}
		}

		.bottom_right {
			display: flex;
			align-items: center;

			.r_left {
				display: flex;
				height: 42rpx;
				align-items: center;
				margin-right: 12rpx;
				border-radius: 6rpx;
				border: 2rpx solid #ec8d4a;
				box-sizing: border-box;
				transform: skewX(-15deg);
				position: relative;

				.unit {
					width: 52rpx;
					height: 42rpx;
					background: #ec8d4a;
					border-radius: 6rpx;
					display: flex;
					align-items: center;
					justify-content: center;
					
					.text{
						font-size: 22rpx;
						font-weight: 600;
						color: #ffffff;
						transform: skewX(15deg);
					}
				}

				.number {
					display: flex;
					align-items: center;
					transform: skewX(15deg);
					padding: 0 10rpx 0 6rpx;
					font-size: 22rpx;
					color: #666c70;

					.left_n {
						font-size: 28rpx;
						font-weight: 500;
						color: #ec8d4a;
					}
				}
			}

			.r_left:last-child {
				margin-right: 0;
				border: 1px solid #199d7c;

				.unit {
					background: #199d7c;
				}
				.number {
					.left_n {
						color: #199d7c;
					}
				}
			}
		}
	}
}
</style>
