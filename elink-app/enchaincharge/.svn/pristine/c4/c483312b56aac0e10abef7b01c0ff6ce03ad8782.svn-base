<template>
	<view class="switchTableCard">
		<view class="switch_list">
			<template v-for="(item,index) in tabsArray">
				<view class="switch_li" :class="{ activeClass: tabsIndex === item.id }" :key="index" @click="selectIndex(item.id)">
					<view class="textClass"><u-parse :content="item.name"></u-parse></view>
					<view class="white_bg_color"></view>
				</view>
			</template>
		</view>
		<view class="switch_content" v-if="slotContent">
			<slot name="slotContent"></slot>
		</view>
	</view>
</template>

<script>
	export default {
		name: "SwitchTableCard",
		props: {
			tabsArray: {
				type: Array,
				default: () => []
			},
			// 默认 获取第一个 (数组id)
			tabsIndex: {
				type: [Number, String],
				default: 1
			},
			slotContent: {
				type: Boolean,
				default: true
			},
		},
		data() {
			return {

			}
		},
		methods: {
			selectIndex(tabsIndex) {
				if (!this.tabsArray.length) return;
				this.$emit("changEvent", { type: "SwitchTableCard", tabsIndex: tabsIndex });
			}
		}
	}
</script>

<style scoped lang="scss">
	.switchTableCard {
		width: 100%;

		.switch_list {
			display: flex;
			align-items: center;
			border-radius: 24rpx 24rpx 0 0;
			background: rgba(31, 116, 226, 0.2);

			.switch_li {
				flex: 1;
				height: 88rpx;
				padding: 0 12rpx;
				box-sizing: border-box;
				display: flex;
				align-items: center;
				justify-content: center;
				position: relative;
				
				.textClass {
					color: #242424;
					font-size: 24rpx;
					position: absolute;
					z-index: 100;
					
					.tabs_li{
						display: flex;
						flex-direction: column;
						align-items: center;
						
						.text {
							color: #989898;
							padding-right: 12rpx;
							box-sizing: border-box;
						}
					}
				}

				.white_bg_color {
					width: 100%;
					height: 100%;
					position: absolute;
					left: 0;
					top: 0;
					display: none;
					transition: all .28s;
					box-sizing: border-box;
					background-color: #ffffff;
					border-radius: 24rpx 24rpx 0 0;
				}
			}

			.activeClass {

				.white_bg_color {
					display: block;
				}

			}

		}

		.switch_content {
			padding: 24rpx 32rpx;
			box-sizing: border-box;
			background-color: #ffffff;
			border-radius: 0 0 24rpx 24rpx;
		}
	}
</style>