<template>
	<view class="container">
		<view class="container_content">
			<view class="container_flex">
				<view class="container_top">
					<!-- <sm-card-list leftText="头像" :enableSlot="true" @cardEvent="chooseImage">
						<template v-slot:slotRight>
							<view class="avatarPath" @click.stop="previewAvatar"><image v-if="userInfoData.avatarPath" :src="userInfoData.avatarPath" ></image></view>
						</template>
					</sm-card-list> -->
					<sm-card-list leftText="昵称" :rightText="userInfoData.nickName" @cardEvent="setUserInfo('nickName', '昵称')"></sm-card-list>
					<sm-card-list leftText="邮箱" :rightText="userInfoData.mailbox" @cardEvent="setUserInfo('mailbox', '邮箱')"></sm-card-list>
					<sm-card-list leftText="更换手机号" :bottomLine="false" :rightText="userInfoData.phoneNum" @cardEvent="goToPage('/thirdPackage/accountSafe/sendVerCode?pageType=1')"></sm-card-list>
				</view>
				<view class="container_bottom"><view class="button" @click="clickLogOut">退出登录</view></view>
			</view>
		</view>
		<!-- <select-address ref="smSelectAddress" @modalEvent="modalEvent"></select-address>
		<sm-popup ref="smPopupRef" title="性别" :footerVisible="false">
			<template v-slot:content>
				<view class="gun_list_body">
					<sm-select-list textKey="name" onlyKey="id" :list="selectlist" :active="activeSex" selectIcon="checkbox" @changEvent="selectSexFun"></sm-select-list>
				</view>
			</template>
		</sm-popup> -->
		<sm-modal-input ref="smModalInput" :modalInfo="modalInfo" @modalEvent="modalEvent"></sm-modal-input>
	</view>
</template>

<script>
import { mapState } from 'vuex';
// import SelectAddress from "../components/select-address.vue";
import { notCharmap, isvalidEmail } from '@/common/validate.js';
import { queryAppletUserInfoById, saveOrUpdateMemberInfo, uploadMemberHeadByPhone} from '@/thirdPackage/api/index.js';

export default {
	name: 'personalData',
	options: { styleIsolation: 'shared' },//解决/deep/不生效**
	// components:{ SelectAddress },
	computed: {
		...mapState(['userInfo'])
	},
	data() {
		return {
			userInfoData: {},
			modalInfo: {
				title: '修改',
				editable: true,
				inputlength: 24,
				placeholderText: '请输入'
			},
			placeholderText: '',
			operationType: '',

			selectlist: [],
			activeSex: "",
			sexArray: [{ name: '男', id: 1 }, { name: '女', id: 2 }],
		};
	},
	onLoad() {
		this.getMemberInfoById();  // 根据会员id查询基本信息
	},
	methods: {
		// 根据会员id查询基本信息
		getMemberInfoById() {
			queryAppletUserInfoById({}).then(res => {
				let returnDataInfo = res.data ? res.data : {};
				
				for (let key in returnDataInfo) {
					if (JSON.stringify(returnDataInfo[key]) === 'null') returnDataInfo[key] = '';
				}
				let userInfo = uni.getStorageSync('USER_INFO');
				userInfo.nickName = returnDataInfo.nickName;
				userInfo.image = returnDataInfo.avatarPath;

				//修改缓存的用户数据
				this.$store.dispatch('alterUserInfo', userInfo);
				uni.setStorage({ key: 'USER_INFO', data: userInfo });
				this.userInfoData = JSON.parse(JSON.stringify(returnDataInfo));
			});
		},
		// 设置用户信息
		setUserInfo(operationType, placeholderText) {
			let inputlength = 24;
			this.operationType = operationType;
			this.placeholderText = placeholderText;
			this.modalInfo.title = `修改${placeholderText}`;
			this.modalInfo.placeholderText = `请输入${placeholderText}`;
			this.modalInfo.inputContent = this.userInfoData[this.operationType]; // 设置输入框值
			
			// 设置输入框输入长度
			if(operationType === "mailbox") inputlength = 999;
			this.modalInfo.inputlength = inputlength;

			this.$nextTick(() => {
				this.$refs.smModalInput.openModalInput();
			});
		},
		// 选择修改用户性别
		openSelectPopup(operationType, placeholderText) {
			let selectValue = '',perAddress = {};
			this.operationType = operationType;
			this.placeholderText = placeholderText;

			this.$nextTick(() => {
				
				// 打开选择地址框
				if (this.operationType === 'perAddress') {
					this.$refs.smSelectAddress.openPopup(perAddress);
					return
				}
				
				this.$refs.smPopupRef.openPopupFun();
			});
		},
		// 确定设置用户信息
		modalEvent(res) {
			let data = JSON.parse(JSON.stringify({ id: this.userInfo.memberId, ...this.userInfoData }));

			if (res.type === 'enter') {
				if (this.operationType === 'nickName') {
					if (!notCharmap(res.content)) {
						uni.showToast({
							icon: 'none',
							title: `请输入正确的${this.placeholderText}`
						});
						return;
					}
					data.nickName = res.content;
				}
				
				if (this.operationType === 'mailbox') {
					if (!isvalidEmail(res.content)) {
						uni.showToast({
							icon: 'none',
							title: `请输入正确的${this.placeholderText}`
						});
						return;
					}
					data.mailbox = res.content;
				}

				saveOrUpdateMemberInfo(data).then(res => {
					this.getMemberInfoById();
					this.$refs.smModalInput.closeModalPopup();
					uni.showToast({ icon: 'success', title: res.message });
				});
			}
		},
		//选择头像照片
		chooseImage() {
			uni.chooseImage({
				count: 1,
				sizeType: ['compressed'],
				sourceType: ['album', 'camera'],
				success: res => {
					let imgs = res.tempFilePaths.map((value, i) => {
						return {
							name: 'imageFile',
							uri: value
						};
					});
					uploadMemberHeadByPhone(imgs, { phone: this.userInfo.mobile }).then(res => {
						this.getMemberInfoById();
						uni.showToast({ icon: 'success', title: res.message });
					});
				},
				fail: err => {
					console.log('chooseImage fail', err);
				}
			});
		},
		selectSexFun(data){
			let res = { content: data.id,type: "enter" }
			this.modalEvent(res);
			this.$refs.smPopupRef.closePopupFun();
		},
		// 退出登录
		clickLogOut() {
			this.$clearUserInfo();
		},
		// 预览头像
		previewAvatar() {
			uni.previewImage({
				urls: [this.userInfoData.avatarPath]
			});
		},
		goToPage(pageName) {
			uni.navigateTo({ url: pageName });
		}
	}
};
</script>

<style scoped lang="scss">
.container_flex {
	.container_top {
		margin: 64rpx 32rpx 200rpx 32rpx;
		padding: 30rpx 0 10rpx 0;
		box-sizing: border-box;
		border-radius: 24rpx;
		background-color: #ffffff;
		
		/deep/ .cardlist {
			.leftText {
				color: #989898 !important;
			}
			
			.text {
				color: #242424 !important;
			}
		}
	}

	.container_bottom {
		display: flex;
		justify-content: center;
		padding: 0 40rpx 60rpx 40rpx;
		box-sizing: border-box;

		.button {
			width: 100%;
			height: 98rpx;
			border-radius: 12rpx;
			border: 2rpx solid #F83F30;
			text-align: center;
			line-height: 98rpx;
			font-size: 36rpx;
			font-weight: 500;
			color: #F83F30;
		}
	}

	.avatarPath {
		image {
			width: 100rpx;
			height: 100rpx;
			border-radius: 50%;
		}
	}
}
</style>
