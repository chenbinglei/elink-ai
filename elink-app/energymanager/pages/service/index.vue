<template>
	<view class="content">
		<image class="content-bgc" src="/static/image/bgc.png"></image>
		<view class="container">
			<view class="tabs">服务</view>
			<view class="serves-list">
				<view v-for="(item, index) in list" :key="index">
					<view class="tab-servers">{{ item.label }}</view>
					<view class="servers-list">
						<view class="servers-item" v-for="(items, indexs) in item.children" :key="indexs" @click="goOthers(items)">
							<view :class="items.img" :style="{ color: item.label === '常用应用' ? '#388BFF' : '#09B9B5' }">
							</view>
							<view class="servers-item-text">{{ items.label }}</view>
						</view>
					</view>
				</view>
			</view>
		</view>
	</view>
</template>



<script setup>
import { ref } from 'vue'
const list = [
	{
		label: "常用应用",
		children: [
			{
				label: "充电订单",
				img: "iconfont icon-chongdiandingdan",
				path: "chargingOrder"
			},
			{
				label: "V2G订单",
				img: "iconfont icon-fangdiandingdan",
				path: "disChargingOrder"
			},
			{
				label: "交易明细",
				img: "iconfont icon-jiaoyimingxi"
			},
			{
				label: "发票管理",
				img: "iconfont icon-fapiaoguanli"
			}, {
				label: "经营分析",
				img: "iconfont icon-jiankongshuju",
				path: "CsBusinessAnalysis"
			},
		]
	}, {
		label: "储能运营",
		children: [
			{
				label: "生产统计",
				img: "iconfont icon-shebeirizhi"
			},
			{
				label: "储能报表",
				img: "iconfont icon-dingdanguanli"
			},
		]
	}
]
const goOthers = (item) => {
	if (item.path) {
		uni.navigateTo({
			url: '/fourthPackage/pages/' + item.path
		})
	} else {
		uni.showToast({
			title: '暂未开放',
			icon: 'none'
		})
	}
	// uni.showToast({
	// 		title: '暂未开放',
	// 		icon: 'none'
	// 	})

}

</script>

<style lang="scss" scoped>
.content {
	position: relative;
	width: 100%;
	height: 100%;
}

.content-bgc {
	position: absolute;
	width: 100%;
	height: 100%;
}

.container {
	width: 90%;
	margin: 0 auto;
}

.tabs {
	padding-top: 16%;
	width: 100%;
	margin: 0 auto;
	display: flex;
	font-weight: 500;
	font-size: 36rpx;
	color: #000000;
}

.serves-list {
	margin-top: 30rpx;

	.tab-servers {
		font-weight: 600;
		font-size: 28rpx;
		color: #606163;
		padding: 30rpx 0 13rpx 0;
	}

	/* ========== 修复在这里 ========== */
	.servers-list {
		margin-top: 14rpx;
		background: #fff;
		padding: 28rpx 20rpx;
		border-radius: 30rpx;
		display: flex;
		flex-wrap: wrap;
		gap: 10rpx;
		justify-content: flex-start;

		.servers-item {
			width: calc(25% - 10rpx);
			text-align: center;
			box-sizing: border-box;
			display: flex;
			flex-direction: column;
			align-items: center;
			padding: 10rpx 0;

			.iconfont {
				font-size: 40rpx;
			}

			.servers-item-text {
				margin-top: 8rpx;
				font-weight: 400;
				font-size: 22rpx;
				color: #1A1A1A;
				text-align: center;
			}
		}
	}
}
</style>
