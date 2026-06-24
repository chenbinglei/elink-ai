import store from '@/store/index.js';
import { base_info } from './base_info.js';
import { $lockUserIfLogin, $clearUserInfo } from "@/common/common.js";

export function ajax(options) {
	return new Promise((resolve, reject) => {

		//无需登录可访问的接口
		if (!options.nologinRequired && !$lockUserIfLogin(2)) return
		
		//接口等待不转圈  showLoading 为true 不出现加载等待框
		if (options.showLoading === undefined || !options.showLoading) {
			uni.showLoading({
				mask: true,
				title: options.showLoadingText ? options.showLoadingText : "加载中.."
			});
		}

		// 合并对象
		options = Object.assign({method: 'POST',dataType: 'json',header: { 'content-type': 'application/x-www-form-urlencoded' } }, options);
		options.url = `${uni.getStorageSync('BASE_URL')}${options.url}`;
		options.data = { ...base_info(), ...options.data };
		
		options.success = ({ data, statusCode }) => {
			// console.log(data);
			
			if (options.showLoading === undefined || !options.showLoading){
				uni.hideLoading({ noConflict: true });
			}

			if (statusCode != 200) {
				let text = `服务器小哥状态 ${statusCode} 啦,请稍后再试`;
				uni.showModal({
					title: '提示',
					content: text,
					confirmColor: '#3296FA',
					showCancel: false,
					success: (res) => {
						if (res.confirm) {
							uni.stopPullDownRefresh();
							uni.$emit('z-paging-error-emit');
							reject(data)
						}
					}
				});
				return;
			}

			if (data.code) {
				if (data.code == 20000 || data.code == 200) {
					resolve(data)
				} else if (data.code == 9999) {
					// 登录失效
					$clearUserInfo(data.message || data.msg);
					reject(data)
				} else {
					if (options.showToast === undefined || !options.showToast) {
						uni.showToast({
							icon: 'none',
							duration: 3000,
							title: data.message || data.msg
						});
					}
					reject(data)
				}
			} else {
				// 没有code,直接返回data
				resolve(data)
			}
		}

		options.fail = (err) => {
			// console.log(err);
			uni.hideLoading({ noConflict: true });
			if (err.errMsg.indexOf("interrupted") == -1) {
				uni.showModal({
					title: '网络异常',
					content: '连接失败，请检查网络后重试',
					confirmColor: '#3296FA',
					showCancel: false,
					success: (res) => {
						if (res.confirm) {
							uni.$emit('z-paging-error-emit');
						}
					}
				});
				reject(err)
			}
		}
		uni.request(options)
	})
}

// 上传
export function dj_upload(options) {
	return new Promise((resolve, reject) => {

		//无需登录可访问的接口
		if (!options.nologinRequired) {
			if (!$lockUserIfLogin(2)) return
		}

		//接口等待不转圈  showLoading 为true 不出现加载等待框
		if (options.showLoading === undefined || !options.showLoading) {
			uni.showLoading({
				mask: true,
				title: "加载中"
			});
		}

		options.formData = {...options.formData,...base_info() };
		options.url = `${uni.getStorageSync('BASE_URL')}${ options.url }`;

		// #ifdef MP-WEIXIN
		options.name = options.files[0].name;
		options.filePath = options.files[0].uri;
		// #endif

		options.success = ({ data, statusCode }) => {
			
			if (options.showLoading === undefined || !options.showLoading){
				uni.hideLoading({ noConflict: true });
			}
			
			if (statusCode != 200) {
				uni.showModal({
					title: '提示',
					showCancel: false,
					confirmColor: '#3296FA',
					content: `服务器小哥状态 ${statusCode} 啦,请稍后再试`,
					success: (res) => {
						if (res.confirm) {
							reject(data)
						}
					}
				});
				return;
			}
			
			data = JSON.parse(data);
			
			if (data.code) {
				if (data.code == 20000 || data.code == 200) {
					resolve(data);
				} else if (data.code == 9999) {
					// 登录失效
					$clearUserInfo(data.message || data.msg);
					reject(data)
				} else {
					if (options.showToast === undefined || !options.showToast) {
						uni.showToast({
							icon: 'none',
							duration: 3000,
							title: data.message || data.msg
						});
					}
					reject(data)
				}
			} else {
				// 没有code,直接返回data
				resolve(data)
			}
		}

		options.fail = (err) => {
			// console.log(err);
			uni.hideLoading({ noConflict: true });
			uni.showModal({
				title: '网络异常',
				content: '连接失败，请检查网络后重试',
				confirmColor: '#3296FA',
				showCancel: false,
				success: (res) => {
					if (res.confirm) {
						// #ifdef APP-PLUS
						// plus.runtime.quit();
						// #endif
					}
				}
			});
			reject(err)
		}
		// console.log(options)
		uni.uploadFile(options)
	})
}