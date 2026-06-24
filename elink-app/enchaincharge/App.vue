<script>
import store from '@/store' //引入vuex
import { findAppletByAppletCode } from "@/api/home.js";
	
export default {
	onLaunch: function() {
		console.log('App Launch');
		this.queryAppletByAppletCode();
	},
	onShow: function() {
		this.autoUpdate();
		console.log('App Show');
	},
	onHide: function() {
		console.log('App Hide');
	},
	data(){
		return{
			chargeTmplIds: [], // 开始充电需调用的消息通知模板
			disChargeTmplIds: [], // 开始放电需调用的消息通知模板
		}
	},
	methods: {
		autoUpdate() {
			let self = this;
			// 获取小程序更新机制兼容
			if (uni.canIUse('getUpdateManager')) {
				const updateManager = uni.getUpdateManager();
				//1. 检查小程序是否有新版本发布
				updateManager.onCheckForUpdate(function(res) {
					// 请求完新版本信息的回调
					if (res.hasUpdate) {
						//检测到新版本，需要更新，给出提示
						uni.showModal({
							title: '更新提示',
							content: '检测到新版本，是否下载新版本并重启小程序？',
							success: function(res) {
								if (res.confirm) {
									//2. 用户确定下载更新小程序，小程序下载及更新静默进行
									self.downLoadAndUpdate(updateManager);
								} else if (res.cancel) {
									//用户点击取消按钮的处理，如果需要强制更新，则给出二次弹窗，如果不需要，则这里的代码都可以删掉了
									uni.showModal({
										title: '温馨提示~',
										content: '本次版本更新涉及到新的功能添加，旧版本无法正常访问的哦~',
										showCancel: false, //隐藏取消按钮
										confirmText: '确定更新', //只保留确定更新按钮
										success: function(res) {
											if (res.confirm) {
												//下载新版本，并重新应用
												self.downLoadAndUpdate(updateManager);
											}
										}
									});
								}
							}
						});
					}
				});
			} else {
				// 如果希望用户在最新版本的客户端上体验您的小程序，可以这样子提示
				uni.showModal({
					title: '提示',
					content: '当前微信版本过低，无法使用该功能，请升级到最新微信版本后重试。'
				});
			}
		},
		/**
		 * 下载小程序新版本并重启应用
		 */
		downLoadAndUpdate(updateManager) {
			uni.showLoading();
			//静默下载更新小程序新版本
			updateManager.onUpdateReady(function() {
				uni.hideLoading();
				//新的版本已经下载好，调用 applyUpdate 应用新版本并重启
				updateManager.applyUpdate();
			});
			updateManager.onUpdateFailed(function() {
				// 新的版本下载失败
				uni.showModal({
					title: '已经有新版本了哟~',
					content: '新版本已经上线啦~，请您删除当前小程序，重新搜索打开哟~'
				});
			});
		},
		queryAppletByAppletCode(){
			let accountInfo = uni.getAccountInfoSync();
			// //  2：晟曼e充 wx6c2157bc4d08d531
			// if (accountInfo.miniProgram.appId === "wx6c2157bc4d08d531") {
			// 	// 开始充电需调用的消息通知模板
			// 	this.chargeTmplIds = ['SV314ziuH3jhcXj4oOlr5LBY9FnuLkJSsYcQez8fF-I', 'xkG_vMRjC73l8wtrbGqAvnD5TWYbpAYBoSlXbvtOwiE','e7FXpw9lq4hl7-vi5T4R7qV-tJt3gzd9IoJCYQOTr8U'];
			// 	// 开始放电需调用的消息通知模板
			// 	this.disChargeTmplIds = ['4kHapzr1t9CrZ3O2WzrjFegF4XxZHN2j0R04kYenU6k','onv7Pt4KdpNRiagP511J8NgRGsnQtDollGPe9wY2KhU','e7FXpw9lq4hl7-vi5T4R7qV-tJt3gzd9IoJCYQOTr8U']; 
			// }
			findAppletByAppletCode({ appletCode: accountInfo.miniProgram.appId }).then(res=>{
				let config = res.data ? res.data : {};
				config.chargeTmplIds = this.chargeTmplIds;
				config.disChargeTmplIds = this.disChargeTmplIds;
				config.versionNum = accountInfo.miniProgram.version; // 当前小程序版本号
				if(config.email)config.email = JSON.parse(config.email);
				if(!config.platformLogo) config.platformLogo = "999999"; // 平台标识
				store.dispatch('alterAppConfig', config);
			}).catch(()=>{
				let config = {
					platformLogo: "999999",
					chargeTmplIds: this.chargeTmplIds,
					disChargeTmplIds: this.disChargeTmplIds,
					versionNum: accountInfo.miniProgram.version // 当前小程序版本号
				};
				store.dispatch('alterAppConfig', config);
			})
		}
	}
};
</script>

<style rel="stylesheet/scss" lang="scss">
@import '@/static/styles/uni.scss';
@import '@/static/styles/common.scss';
@import '@/static/iconfont/iconfont.css';
@import '@/static/styles/displayFlex.scss';
</style>
