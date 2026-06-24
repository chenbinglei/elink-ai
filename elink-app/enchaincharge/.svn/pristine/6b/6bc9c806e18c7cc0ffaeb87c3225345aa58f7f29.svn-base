<template>
	<view class="PileInfoCard">
		<view class="pile_left">
			<!-- 枪状态 圆环 -->
			<view class="gun_ring">
				<GunRing :gunWorkState="pileInfo.gunWorkState" :batterySOC="pileInfo.batterySOC"></GunRing>
			</view>
			<view class="li" v-if="pileInfo.pileTypeId">
				<text class="text" >{{ pileInfo.pileTypeId | electricPileType }}</text>
			</view>
			<view class="li">
				<text class="text">国标</text>
				<text>{{ pileInfo.nationalStandard | nationalStandard }}</text>
			</view>
		</view>
		<view class="pile_right">
			<template v-for="(item,index) in rightList">
				<view class="card_li" :key="index">
					<view class="card_left"> {{ item.name }} </view>
					<view class="card_center">
						<text>{{ pileInfo[item.fieldName] | moreData }}</text>
						<text class="unit" v-if="item.unit">{{ item.unit }}</text>
						<template v-if="item.nextFieldName">
							<text>{{ item.connector }}</text>
							<text>{{ pileInfo[item.nextFieldName] | moreData }}</text>
							<text class="unit" v-if="item.unit">{{ item.unit }}</text>
						</template>
					</view>
					<view class="copy_but" v-if="item.copy" @click="copyValue(item)">
						<text>复制</text>
					</view>
				</view>
			</template>
		</view>
	</view>
</template>

<script>
	import GunRing from './GunRing.vue';
	export default {
		name: "PileInfoCard",
		components: { GunRing },
		props: {
			pileInfo: {
				type: Object,
				default: () => {
					return {}
				}
			}
		},
		data() {
			return {
				rightList: [
					{ name: "枪编号", fieldName: "pileCode",nextFieldName: "gunCode",copy: true,connector:"-" },
					{ name: "最大功率", fieldName: "power", unit: "kW" },
					{ name: "额定电流", fieldName: "ratedCurrent", unit: "A" },
					{ name: "额定电压", fieldName: "voltageLowerLimits", nextFieldName: "voltageUpperLimits", unit: "V",connector:"~" }
				]
			}
		},
		methods: {
			// 点击复制
			copyValue(item){
				let copyValue = this.pileInfo[item.fieldName];
				if(item.nextFieldName)copyValue = copyValue + item.connector + this.pileInfo[item.nextFieldName];
				uni.setClipboardData({
					data: copyValue,
				});
			},
		}
	}
</script>

<style scoped lang="scss">
.PileInfoCard {
	display: flex;
	justify-content: space-between;
	
	.pile_left {
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		
		.gun_ring {
			margin-bottom: 12rpx;
		}
		
		.li {
			font-size: 24rpx;
			text-align: center;
			
			.text {
				margin-right: 16rpx;
			}
		}
	}
	
	.pile_right {
		flex: 1;
		margin-left: 24rpx;
		
		.card_li {
			display: flex;
			align-items: center;
			font-size: 24rpx;
			padding-bottom: 16rpx;
			box-sizing: border-box;
			
			.card_left{
				color: #989898;
				margin-right: 10rpx;
			}
			
			.card_center {
				flex: 1;
				color: #242424;
				
				.unit{
					margin-left: 4rpx;
				}
			}
			
			.copy_but {
				color: #476ae2;
				margin-left: 10rpx;
				padding: 4rpx 12rpx;
				border-radius: 6rpx;
				border: 2rpx solid #476ae2;
			}
		}
	}
}
</style>
