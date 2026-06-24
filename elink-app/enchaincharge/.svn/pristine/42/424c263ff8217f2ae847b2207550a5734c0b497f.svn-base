<template>
	<view class="container">
		<view class="container_content">
			<view class="container_flex">
				<sm-white-card :leftTopText="titleText" leftBottomText="开启通知提醒，不错过重要消息">
					<template v-slot:slotRight>
						<sm-switch :checked="messagePush" @change="switchChange"></sm-switch>
					</template>
				</sm-white-card>
			</view>
		</view>
	</view>
</template>

<script>
import { queryPreferenceSettingsInfoByMemberId,updateMemberWechatNotice } from '@/thirdPackage/api/index.js';
export default {
	name: 'messagePush',
	data() {
		return {
			updateId: null,
			messagePush: false,
			// #ifdef MP-WEIXIN
			titleText:"接收微信服务号通知提醒",
			// #endif
			// #ifdef MP-ALIPAY
			titleText:"接收支付宝服务号通知提醒",
			// #endif
		};
	},
	onLoad() {
		this.getPreferenceSettingsInfoByMemberId();
	},
	methods: {
		getPreferenceSettingsInfoByMemberId(){
			queryPreferenceSettingsInfoByMemberId({}).then(res=>{
				//isWechatNotice 是否开启微信通知设置 0-不开启 1-开启
				this.updateId = res.data.id ? res.data.id : '';
				this.messagePush = res.data.isWechatNotice ? true : false;
			})
		},
		switchChange(data) {
			
			uni.showModal({
				title: "通知提醒",
				content: `即将${ this.messagePush ? "关闭" : "开启" }通知提醒`,
				confirmText: `${ this.messagePush ? "关闭" : "开启" }`,
				confirmColor: '#3C68F4',
				success: res => {
					if (res.confirm) {
						updateMemberWechatNotice({ id:this.updateId,isWechatNotice:data?1:0 }).then(res=>{
							uni.showToast({
								icon:'none',
								title:"设置成功",
								success: () => {
									this.messagePush = data;
								}
							})
						})
					}
				}
			});
			
		}
	}
};
</script>

<style scoped lang="scss">
.container_flex {
	padding: 32rpx 40rpx 40rpx 40rpx;
	box-sizing: border-box;

	.card-list {
		margin-top: 20rpx;
		transition: all 0.28s;
	}
}
</style>
