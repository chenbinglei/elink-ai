<template>
	<view class="sm-tabs">
		<template v-for="(item, index) in tabsArray">
			<view class="tabs_list" :key="index" :class="{ activeClass: item.id === activeIndex }" @click="clickTabs(item.id)">
				<text>{{ item.name }}</text>
			</view>
		</template>
	</view>
</template>

<script>
export default {
	name: 'sm-tabs',
	props: {
		tabsArray: {
			type: Array,
			default: () => []
		},
		// 默认 获取第一个 (数组id)
		tabsIndex: {
			type: [Number, String],
			default: 1
		}
	},
	data() {
		return {
			activeIndex: ''
		};
	},
	mounted() {
		this.activeIndex = this.tabsIndex;
	},
	methods: {
		clickTabs(tabsIndex){
			this.activeIndex = tabsIndex;
			this.$emit("changEvent",this.activeIndex);
		}
	}
};
</script>

<style scoped lang="scss">
.sm-tabs {
	display: flex;
	align-items: center;

	.tabs_list {
		flex: 1;
		height: 64rpx;
		background: #ffffff;
		border-radius: 8rpx;
		text-align: center;
		line-height: 64rpx;
		font-size: 24rpx;
		color: #242424;
	}

	.activeClass {
		color: #ffffff;
		font-weight: 500;
		background: #107be9;
	}
}
</style>
