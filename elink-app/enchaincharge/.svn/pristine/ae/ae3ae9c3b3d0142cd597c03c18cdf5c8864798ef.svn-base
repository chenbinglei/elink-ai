<template>
	<view class="popups">
		<!-- 底部最后一项为取消按钮 -->
		<uni-popup ref="popup" type="bottom">
			<view class="popup-box">
				<view class="list">
					<view class="li" v-for="(item, index) in list" :key="index" @click="select(item.id)">{{ item.name }}</view>
					<view class="li cencal" @click="close">取消</view>
				</view>
			</view>
		</uni-popup>
	</view>
</template>

<script>
/* 
	    @list 需要渲染的列表   {id，name} 两个字段
		@confirm   确定返回的方法
	 */
export default {
	name: 'BottomPopupCancel',
	props: {
		// 需要渲染的列表   {id，name} 两个字段
		list: {
			type: Array,
			default: []
		}
	},
	data() {
		return {
			activeIndex: ''
		};
	},
	methods: {
		open() {
			this.$refs.popup.open();
		},
		close() {
			this.$refs.popup.close();
		},
		select(id) {
			this.activeIndex = id;
			//当前选择的第几个
			this.$emit('confirm', this.activeIndex);
			this.close();
		}
	}
};
</script>

<style lang="scss" scoped>
.popup-box {
	z-index: 10000;
	width: 100%;
	padding: 0 32upx;
	box-sizing: border-box;
	background: #ffffff;
	border-radius: 40upx 40upx 0px 0px;

	.list {
		width: 100%;

		.li {
			width: 100%;
			height: 104upx;
			line-height: 104upx;
			text-align: center;
			font-size: 28upx;
			font-weight: 500;
			color: rgba(0, 0, 0, 0.8);
			border-bottom: 2upx solid #e6e6e6;
		}
		.cencal {
			color: #e6e6e6 !important;
			border: none;
		}
	}
}
</style>
