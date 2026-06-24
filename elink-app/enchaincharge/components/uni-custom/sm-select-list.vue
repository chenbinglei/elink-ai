<template>
	<view class="list">
		<template v-for="(item, index) in list">
			<view class="select_li" :class="{ activeClass: item[onlyKey] === active }" :key="index" @click="clickItemButton(item)">
				<view class="select_li_left">{{ item[textKey] }}</view>
				<view class="select_li_right">
					<template v-if="selectIcon === 'iconfont'">
						<text class="iconfont icon-duihao select_icon" v-if="item[onlyKey] === active"></text>
					</template>
					<template v-else>
						<sm-checkbox  type="radio" :checked="item[onlyKey] === active" :isClick="false"></sm-checkbox>
					</template>
				</view>
			</view>
		</template>
	</view>
</template>

<script>
	export default {
		name: "sm-select-list",
		props: {
			list: {
				type: Array,
				default: () => []
			},
			active: {
				type: [Number, String],
				default: ""
			},
			textKey:{
				type: String,
				default: "name"
			},
			onlyKey:{
				type: String,
				default: "id"
			},
			selectIcon:{
				type: String,
				default: "iconfont"   // iconfont  checkbox
			}
		},
		data() {
			return {

			}
		},
		methods: {
			clickItemButton(item) {
				this.$emit("changEvent", item);
			}
		}
	}
</script>

<style scoped lang="scss">
	.select_li {
		height: 80rpx;
		display: flex;
		align-items: center;
		padding: 0 16rpx;
		box-sizing: border-box;
	
		.select_li_left {
			flex: 1;
			color: #2a2a2a;
			font-size: 28rpx;
		}
	
		.select_li_right {
			width: 32rpx;
			margin-left: 16rpx;
			box-sizing: border-box;
	
			.select_icon {
				color: #107be9;
			}
		}
	}
	
	.activeClass {
		border-radius: 12rpx;
		transition: all 0.28s;
		background: rgba(16, 123, 233, 0.08);
		.select_li_left {
			color: #107be9;
		}
	}
</style>