<template>
	<view class="fastAndSlowCharging">
		<template v-if="pileInfo[fieldName] && pileInfo[fieldName].length">
			<template v-for="(item,index) in pileInfo[fieldName]">
				<view class="pileCard" :key="index">
					<!-- 电桩信息卡片 -->
					<PileInfoCard ref="PileInfoCardRef" :pileInfo="item"></PileInfoCard>
				</view>
			</template>
		</template>

		<view class="null-data" v-else>
			<sm-null-data slot="empty"></sm-null-data>
		</view>
	</view>
</template>

<script>
	import PileInfoCard from './FastOrSlowCharge/PileInfoCard.vue'
	export default {
		name: "FastAndSlowCharging",
		components: {
			PileInfoCard
		},
		props: {
			pileInfo: {
				type: Object,
				default: () => {
					return {}
				}
			},
			tabsIndex: {
				type: [Number, String],
				default: 2
			},
		},
		watch: {
			tabsIndex: {
				deep: true,
				immediate: true,
				handler(newVal, oldVal) {
					this.fieldName = this.tabsIndex == 2 ? "dcGunDataList" : "acGunDataList";
				}
			}
		},
		data() {
			return {
				fieldName: "dcGunDataList"
			}
		}
	}
</script>

<style scoped lang="scss">
	.fastAndSlowCharging {
		max-height: 1000rpx;
		overflow-y: auto;

		.pileCard {
			width: 100%;
			padding: 28rpx 0;
			border-bottom: 2rpx solid #f5f5f7;
			box-sizing: border-box;

			&:last-child {
				padding-bottom: 0;
				border-bottom: none;
			}
		}

		/deep/ .nullData {
			.nullDataImage {
				width: 202rpx;
				height: 202rpx;
			}
		}
	}
</style>