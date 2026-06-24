<template>
	<view class="lxy-steps">
		<view v-for="(item, index) in list" :key="index" :class="['steps_li', item.id < activeIndexId ? 'pre_steps_li' : '', item.id === activeIndexId ? 'active_steps_li' : '']">
			<view class="li_top">
				<view class="li_top_left line"></view>
				<view class="li_top_cnter">
					<text class="center_text">{{ index + 1 }}</text>
				</view>
				<view class="li_top_right line"></view>
			</view>
			<view class="li_bottom">{{ item.name }}</view>
		</view>
	</view>
</template>

<script>
export default {
	name: 'lxy-steps',
	props: {
		activeIndex: {
			type: [String, Number],
			default: 1
		}
	},
	data() {
		return {
			activeIndexId: 1,
			list: [{ name: '品牌', id: 1 }, { name: '车系', id: 2 }, { name: '车型', id: 3 }]
		};
	},
	mounted() {
		this.activeIndexId = this.activeIndex;
	},
	methods: {}
};
</script>

<style scoped lang="scss">
.lxy-steps {
	width: 100%;
	display: flex;
	align-items: center;
	padding: 0 28rpx;
	box-sizing: border-box;
	
	
	// 已经完成的步骤
	.pre_steps_li{
		.li_top{
			.line{
				background: #15E8AF !important;
			}
			.li_top_cnter{
				background-size: 100% 100%;
				background-repeat: no-repeat;
				background-color: transparent !important;
				background-image: url('~@/secondPackage/static/image/succecc.png');

				.center_text{
					display: none;
				}
			}
		}
		
		.li_bottom{
			color: #15E8AF !important;
		}
	}
	
	// 当前需要完成的步骤
	.active_steps_li{
		.li_top{
			.li_top_left{
				background: #15E8AF !important;
			}
			.li_top_cnter{
				color: #FFFFFF !important;
				height: 46rpx !important;
				background-size: 100% 100%;
				background-repeat: no-repeat;
				background-color: transparent !important;
				background-image: url('~@/secondPackage/static/image/completing.png');
			}
		}
	}

	.steps_li {
		flex: 1;
		.li_top {
			width: 100%;
			display: flex;
			align-items: center;
			margin-bottom: 16rpx;
			box-sizing: border-box;

			.line {
				flex: 1;
				height: 2rpx;
				background: #f2f6f7;
			}
			.li_top_cnter {
				width: 40rpx;
				height: 40rpx;
				font-size: 26rpx;
				font-weight: bold;
				color: #b4b6b7;
				text-align: center;
				line-height: 40rpx;
				border-radius: 50%;
				background-color: #f2f6f7;
			}
		}

		.li_bottom {
			font-size: 26rpx;
			font-weight: 600;
			color: #a4a5a6;
			text-align: center;
		}
	}

	.steps_li:first-child {
		.li_top {
			padding-left: 6rpx;

			.li_top_left {
				display: none;
			}
		}
		.li_bottom {
			text-align: left;
		}
	}

	.steps_li:last-child {
		.li_top {
			padding-right: 6rpx;
			.li_top_right {
				display: none;
			}
		}
		.li_bottom {
			text-align: right;
		}
	}
}
</style>
