<template>
	<view class="FunctionOperate">
		<!-- <template v-for="(item, index) in funList">
			<template v-if="item.fieldName === 'contactService'">
				<button :key="index" class="card_li" open-type="contact" @contact="contactServiceFun">
					<view class="text_all">
						<view :class="['iconfont', item.icon]"></view>
						<view class="text_bottom">{{ item.name }}</view>
					</view>
				</button>
			</template>
			<template v-else>
				<view :key="index" class="card_li" @click="clickItemButFun(item)">
					<view class="text_all">
						<view :class="['iconfont', item.icon]"></view>
						<view class="text_bottom">{{ item.name }}</view>
					</view>
				</view>
			</template>
		</template> -->
		<template v-for="(item, index) in funList">
  <template v-if="item.fieldName === 'contactService'">
    <button :key="index" class="card_li" open-type="contact" @contact="contactServiceFun">
      <view class="text_all">
        <!-- 判断是图标还是图片 -->
        <image v-if="item.icon.startsWith('/')" :src="item.icon" style="width: 40rpx; height: 40rpx;" mode="aspectFit" class="iconfont"></image>
        <view v-else :class="['iconfont', item.icon]"></view>
        <view class="text_bottom">{{ item.name }}</view>
      </view>
    </button>
  </template>
  <template v-else>
    <view :key="index" class="card_li" @click="clickItemButFun(item)">
      <view class="text_all">
        <image v-if="item.icon.startsWith('/')" :src="item.icon" mode="aspectFit" class="iconfont"></image>
        <view v-else :class="['iconfont', item.icon]"></view>
        <view class="text_bottom">{{ item.name }}</view>
      </view>
    </view>
  </template>
</template>

	</view>
</template>

<script>
export default {
	name: "FunctionOperate",
	data () {
		return {
			funList: [{
				name: "充电订单",
				icon: "icon-chargeAndDisRecord",
				url: "/secondPackage/orderManagement/orderlist"
			}, {
				name: "充电开票",
				// icon: "icon-chargeAndDisRecord",
				icon: "/static/image/hetongdingdan.png", // 替换为图片路径
				url: "/secondPackage/orderManagement/chargeInvoice"
			},
			// {
			// 	name: "占位订单",
			// 	icon: "icon-zhanwei",
			// 	url: "/secondPackage/orderManagement/occupyOrderList"
			// },
			{
				name: "V2G",
				icon: "icon-sizhuangguanli",
				url: "/fourthPackage/pages/v2gDischarge"
			},
			// {
			// 	name: "我的车辆",
			// 	icon: "icon-cheduiguanli",
			// 	url: "/secondPackage/vehicleManagement/vehicleList"
			// },
			// {
			// 	name: "数据统计",
			// 	icon: "icon-shujutongji",
			// 	url: "/firstPackage/pages/dataStatistics"
			// },
			{
				name: "关于",
				nologin: true,
				icon: "icon-zc-tishi",
				url: "/thirdPackage/systemSettings/aboutUs"
			},
			{
				name: "设置",
				icon: "icon-shezhi",
				url: "/thirdPackage/systemSettings/setSystem"
			},
			{
				name: "联系客服",
				icon: "icon-lianxikefu",
				fieldName: "contactService"
			},
			]
		}
	},
	methods: {
		clickItemButFun (item) {
			if (item.url) {
				if (item.nologin) {
					uni.navigateTo({
						url: item.url
					});
				} else {
					if (this.$lockUserIfLogin(2)) {
						uni.navigateTo({
							url: item.url
						});
					}
				}

			}
		},
		// 联系客服
		contactServiceFun (e) {
			let {
				detail
			} = e;
			console.log(detail);
		}
	}
}
</script>

<style lang="scss" scoped>
.FunctionOperate {
	flex: 1;
	display: flex;
	flex-wrap: wrap;
	margin: 64rpx 32rpx;
	padding-top: 32rpx;
	box-sizing: border-box;
	background-color: #ffffff;
	border-radius: 24rpx;

	.card_li {
		width: 33.33%;
		display: flex;
		flex-direction: column;
		align-items: center;
		margin-bottom: 32rpx;
		// margin-bottom: 68rpx;

		.text_all {
			display: flex;
			flex-direction: column;
			align-items: center;

			.iconfont {
				color: #5498FC;
				font-size: 48rpx;
				margin-bottom: 12rpx;
				width: 48rpx;
				height: 48rpx;

			}

			.text_bottom {
				color: #242424;
				font-size: 24rpx;
			}
		}
	}

	button {
		margin: 0;
		padding: 0;
		line-height: initial;
		background-color: transparent;

		&::after {
			border: none;
		}
	}
}
</style>