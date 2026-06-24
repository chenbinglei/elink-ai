<template>
	<view class="container">
		<view class="container_content">
			<CustomHead title="充电偏好设置"></CustomHead>

			<view class="container_flex">
				<view class="title">下方所设置的默认策略即为扫码充电/放电策略配置时默认的策略</view>

				<view class="content">
					<view class="content_card">
						<view class="card_title">
							<view class="card_title_left">默认充电策略</view>
							<view class="card_title_right">
								<text>{{ strategy | strategy }}</text>
								<text v-if="strategy != 0">({{ chargeValue }}%)</text>
							</view>
						</view>

						<view class="card_list">
							<view class="card_list_li" v-for="(item, index) in strategyArray" :key="index" @click.stop="clickStrategy(item.id)">
								<image class="selected" :src="item.id == strategy ? selectedImage : unSelectedImage" ></image>
								<view class="text">{{ item.name }}</view>
							</view>
						</view>

						<view class="slider_centent marginBottom" v-if="strategy == 1">
							<view class="sliderRange">
								<SliderRange :value="sliderChargeValue" :barHeight="11" backgroundColor="#DBD9D9" activeColor="#199D7C" @change="handleChargeChange"></SliderRange>
							</view>
							<view class="sliderValue">{{ chargeValue }}%</view>
						</view>

						<view class="slider_centent marginBottom" v-if="strategy == 3">
							<uni-easyinput v-model="strategyCfg" placeholder="请输入电量" type="number" :inputBorder="false" :styles="inputStyle" :maxlength="3">
								<template v-slot:right>
									<view class="unit">度</view>
								</template>
							</uni-easyinput>
						</view>

						<view class="card_title">
							<view class="card_title_left">默认放电策略</view>
							<view class="card_title_right">放至({{ disChargeValue }}%)</view>
						</view>

						<view class="slider_centent">
							<view class="sliderRange">
								<SliderRange
									:value="sliderDisChargeValue"
									:blockMin="30"
									:barHeight="11"
									backgroundColor="#DBD9D9"
									activeColor="#199D7C"
									@change="handleDisChargeChange"
								></SliderRange>
							</view>
							<view class="sliderValue">{{ disChargeValue }}%</view>
						</view>
					</view>
					<view class="content_button" @click="clickEnter">确认</view>
				</view>
			</view>
		</view>
		<!-- <view class="bgImage"><image src="@/static/image/pageBgImage.png" ></image></view> -->
	</view>
</template>

<script>
import { num1to9999 } from '@/utils/validate.js';
import { queryPreferenceSettingsInfoByMemberId, saveOrUpdatePreferenceSettings } from '@/thirdPackage/api/index.js';
export default {
	name: 'setPreferences',
	data() {
		return {
			updateId: null,
			strategy: 0,
			strategyArray: [{ id: 0, name: '自动充满' }, { id: 1, name: '定SOC' }, { id: 3, name: '定电量' }],
			chargeValue: 100,
			sliderChargeValue: [0, 100],

			strategyCfg: '',

			sliderDisChargeValue: [0, 30],
			disChargeValue: 30,

			selectedImage: require('@/static/image/selected.png'),
			unSelectedImage: require('@/static/image/unSelected.png'),

			inputStyle: {
				height: '88rpx',
				color: '#454545',
				backgroundColor: '#DBD9D9',
				borderColor: '#DBD9D9',
				borderRadius: '10rpx',
				fontSize: '28rpx'
			}
		};
	},
	onLoad() {
		this.getPreferenceSettingsInfoByMemberId();
	},
	methods: {
		// 根据会员id查询偏好设置
		getPreferenceSettingsInfoByMemberId() {
			queryPreferenceSettingsInfoByMemberId({}).then(res => {
				this.strategy = res.data.chargeStrategyType;
				this.updateId = res.data.id ? res.data.id : '';
				this.strategyCfg = res.data.chargeStrategyNum ? res.data.chargeStrategyNum : '';
				this.chargeValue = res.data.chargeStrategyNum ? res.data.chargeStrategyNum : '';
				this.disChargeValue = res.data.dischargeStrategyNum ? res.data.dischargeStrategyNum : '';
			});
		},
		// 保存或编辑偏好设置
		clickEnter() {
			if (this.strategy == 3 && !num1to9999(this.strategyCfg)) {
				uni.showToast({
					icon: 'none',
					title: '请输入正确的电量'
				});
				return;
			}
			saveOrUpdatePreferenceSettings({
				id: this.updateId,
				chargeStrategyType: this.strategy,
				chargeStrategyNum: this.strategy == 1 ? this.chargeValue : this.strategyCfg,
				dischargeStrategyNum: this.disChargeValue
			}).then(res => {
				this.getPreferenceSettingsInfoByMemberId();
				uni.showToast({ icon: 'success', title: '保存成功！' });
			});
		},
		goToPage(pageName) {
			if (pageName) {
				uni.navigateTo({ url: pageName });
			}
		},
		clickStrategy(strategy) {
			this.strategy = strategy;
			this.sliderChargeValue = [0, this.chargeValue];
		},
		handleChargeChange(e) {
			// console.log(e);
			this.chargeValue = e[1];
		},
		handleDisChargeChange(e) {
			// console.log(e);
			this.disChargeValue = e[1];
		}
	}
};
</script>

<style scoped lang="scss">
.container_flex {
	padding-top: 32rpx;

	.title {
		height: 68rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		margin-bottom: 58rpx;
		font-size: 24rpx;
		color: rgba(255, 255, 255, .6);
		background: rgba(236, 141, 74, .6);
	}

	.content {
		padding: 0 40rpx;
		box-sizing: border-box;
		.content_card {
			background: #ffffff;
			border-radius: 10rpx;
			margin-bottom: 100rpx;
			padding: 46rpx 20rpx 46rpx 40rpx;
			box-sizing: border-box;

			.card_title {
				display: flex;
				align-items: center;
				justify-content: space-between;
				margin-bottom: 30rpx;

				.card_title_left {
					font-size: 32rpx;
					font-weight: 500;
					color: #454545;
				}
				.card_title_right {
					font-size: 32rpx;
					font-weight: 400;
					color: #a1a1a1;
				}
			}

			.card_list {
				display: flex;
				align-items: center;
				justify-content: space-between;
				margin-bottom: 36rpx;

				.card_list_li {
					display: flex;
					align-items: center;
					.selected {
						width: 20rpx;
						height: 20rpx;
						margin-right: 8rpx;
						vertical-align: middle;
					}
					.text {
						font-size: 24rpx;
						color: #030303;
					}
				}
			}

			.slider_centent {
				display: flex;
				align-items: center;
				justify-content: space-between;
				margin-bottom: 10rpx;

				.sliderRange {
					flex: 1;
				}

				.sliderValue {
					width: 90rpx;
					text-align: center;
					font-size: 36rpx;
					font-weight: bold;
					color: #454545;
					margin-left: 10rpx;
				}

				.unit {
					color: #454545;
					font-size: 30rpx;
					padding-right: 30rpx;
				}
			}
			
			.marginBottom{
				margin-bottom: 60rpx !important;
			}
			
		}
		.content_button {
			height: 98rpx;
			background: #15e8af;
			border-radius: 12rpx;
			font-size: 36rpx;
			font-weight: 600;
			color: #083329;
			display: flex;
			align-items: center;
			justify-content: center;
		}
	}
}
</style>
