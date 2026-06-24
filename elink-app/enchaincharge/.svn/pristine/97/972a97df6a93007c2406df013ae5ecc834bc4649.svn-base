<template>
	<view class="pileDetailsCard">
		<view class="card_left">
			<image class="pilePlaceholderImage" src="@/firstPackage/static/image/pilePlaceholderImage.png"></image>
			<text class="iconfont icon-qiehuan" v-if="pileData.gunDataList&&pileData.gunDataList.length>1"></text>
		</view>
		<view class="card_right">
			<view class="card_right_top">
				<view class="pileName twoShowText">{{ pileData.pileName | moreData }}</view>
				<view class="gunWorkState" :class="'gunWorkState' + activeGunData.gunWorkState">{{ activeGunData.gunWorkState | gunWorkState }}</view>
			</view>

			<view class="textClass">
				<text>枪：</text>
				<text>{{ activeGunData.gunName | moreData}}</text>
			</view>

			<view class="textClass">
				<text>编号：</text>
				<text>{{ pileData.pileCode | moreData }}-{{ activeGunData.gunCode | moreData }}</text>
			</view>

			<view class="lable_list">
				<view class="lable_li" v-if="activeGunData.electricPileType">
					<text>{{ activeGunData.electricPileType | electricPileType }}</text>
				</view>
				<view class="lable_li" v-if="activeGunData.power">
					<text>{{ activeGunData.power | moreData }}</text>
					<text>kw</text>
				</view>
				<view class="lable_li" v-if="activeGunData.nationalStandard">
					<text>国标</text>
					<text>{{ activeGunData.nationalStandard | nationalStandard }}</text>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	export default {
		name: "PileDetailsCard",
		props: {
			pileData: {
				type: Object,
				default: () => {}
			}
		},
		data() {
			return {
				activeGunData: {},
			}
		},
		methods: {
			switchActiveGunData(gunCode = "") {
				let activeGunData = {};
				if (this.pileData.gunDataList && this.pileData.gunDataList.length) {

					if (gunCode) {
						let findItem = this.pileData.gunDataList.find(item => item.gunCode == gunCode);
						if (findItem) activeGunData = JSON.parse(JSON.stringify(findItem));
					} else {
						activeGunData = JSON.parse(JSON.stringify(this.pileData.gunDataList[0]));
					}
				}
				
				// console.log(activeGunData);
				this.activeGunData = JSON.parse(JSON.stringify(activeGunData));
				this.$emit("changEvent",{ type:"PileDetailsCard",...this.activeGunData });
			}
		}
	}
</script>

<style scoped lang="scss">
	.pileDetailsCard {
		padding: 34rpx 32rpx;
		box-sizing: border-box;
		display: flex;
		justify-content: center;

		.card_left {
			margin-right: 22rpx;
			position: relative;

			.pilePlaceholderImage {
				width: 210rpx;
				height: 210rpx;
			}

			.iconfont {
				color: #242424;
				font-size: 32rpx;
				position: absolute;
				left: 0rpx;
				top: 0rpx;
			}
		}

		.card_right {
			flex: 1;
			display: flex;
			flex-direction: column;
			justify-content: space-between;

			.card_right_top {
				display: flex;
				align-items: center;

				.pileName {
					flex: 1;
					color: #030303;
					font-size: 34rpx;
					font-weight: 600;
					-webkit-line-clamp: 1;
				}

				.gunWorkState {
					color: #FFFFFF;
					font-weight: 500;
					font-size: 20rpx;
					padding: 6rpx 20rpx;
					margin-left: 6rpx;
					border-radius: 4rpx;
					background-color: rgba(109, 125, 145, 1);
				}
				
				.gunWorkState1{
					background-color: rgba(7, 156, 235, 1);
				}
				
				.gunWorkState2{
					 background-color: rgba(255, 248, 134, 1);
				}
				
				.gunWorkState4{
					background-color: rgba(255, 125, 30, 1);
				}
				
				.gunWorkState5{
					background-color: rgba(253, 57, 58, 1);
				}
				
			}

			.textClass {
				color: #7B7D7F;
				font-size: 24rpx;
			}

			.lable_list {
				display: flex;
				flex-wrap: wrap;

				.lable_li {
					border-radius: 4rpx;
					background: #E5F0FF;
					color: #8BB6EE;
					font-size: 24rpx;
					line-height: 32rpx;
					margin-right: 16rpx;
					padding: 6rpx 12rpx;
					box-sizing: border-box;
				}
			}
		}
	}
</style>