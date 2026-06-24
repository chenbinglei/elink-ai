<template>
	<view class="content">
		<view class="stationMap">
			<map id="map" class="map" :latitude="latAndLongitude.centerLat" :longitude="latAndLongitude.centerLon"
				:markers="markers" @markertap="onMarkertap" :show-scale="optionsMap.showScale"
				:show-compass="optionsMap.showCompass" :enable-3D="optionsMap.enable3D" :show-location="optionsMap.showlocation"
				:enable-rotate="optionsMap.enableRotate" :enable-overlooking="optionsMap.enableOverlooking">
			</map>
		</view>
		<view class="station-list">
			<view class="station-data">
				<view class="station-item" v-for="item in detailArray?.inspectionSiteList" :key="item">
					<view class="station-item-title">
						<view class="station-item-title-text">
							<view class="station-item-title-text-title">{{ item.siteName }}</view>
							<!-- 未分配 -->
							<view class="station-item-title-text-distance" v-if="item.status === 1" @click="navigateToLocation(item)">
								导航</view>
						</view>
						<!-- 巡检中 -->
						<view class="station-item-title-content" v-if="item.status == 2">
							<u-button text="导航" shape="circle" @click="navigateToLocation(item)"></u-button>
							<u-button text="填写报告" shape="circle" @click="reportEvent(item)"></u-button>
							<u-button text="放弃巡检" shape="circle" @click="giveUpInspection(item)"></u-button>
							<!-- <u-button text="修改报告" shape="circle" @click="modifyReport(item)"></u-button>
							<u-button text="重新巡检" shape="circle" @click="reInspect(item)"></u-button> -->

						</view>
						<view class="station-item-title-content" v-if="item.status == 4 && detailArray.taskStatus === 3">
							<u-button text="导航" shape="circle" @click="navigateToLocation(item)"></u-button>
							<u-button text="重新巡检" shape="circle" @click="reInspect(item)"></u-button>
						</view>
						<view class="station-item-title-content" v-if="item.status == 3 && detailArray.taskStatus === 3">
							<u-button text="导航" shape="circle" @click="navigateToLocation(item)"></u-button>
							<u-button text="修改报告" shape="circle" @click="reportEvent(item)"></u-button>
						</view>
						<!-- 待验收 -->
						<view class="station-item-title-content" v-if="detailArray.taskStatus === 4">
							<u-button text="导航" shape="circle" @click="navigateToLocation(item)"></u-button>
							<u-button text="查看报告" shape="circle" @click="viewReport(item)" v-if="item.status === 3"></u-button>
						</view>

					</view>
					<view class="station-item-content line">
						<view class="station-item-content-label">任务时间</view>
						<view class="station-item-content-text">{{ $filters.moreData(item.finishTime) }}</view>
					</view>
					<view class="station-item-content line">
						<view class="station-item-content-label">巡检结果</view>
						<view class="station-item-content-text">{{ $filters.inspectHandResult(item.status) }}</view>
					</view>
					<view class="station-item-content">
						<view class="station-item-content-label">发现异常数</view>
						<view class="station-item-content-text">{{ item.exceptionNum ? item.exceptionNum : "0" }}</view>
					</view>
				</view>
			</view>
			<view class="station-button">
				<!--  -->

				<!-- 巡检中 -->
				<!-- <u-button type="primary" style="color: #FFFFFF;" shape="circle" text="完成巡检"
					@click="goToBackPage('finish')"></u-button> -->
				<!-- 待验收 -->
				<!-- <u-button text="退回" shape="circle" @click="goToBackPage('back')"></u-button>
				<u-button text="交接" shape="circle" @click="goToBackPage('handover')"></u-button>
				<u-button type="primary" style="color: #FFFFFF;" shape="circle" @click="goToBackPage('submit')" 
					text="确认完成"></u-button>-->
				<!-- 未开启 -->
				<u-button text="退回" shape="circle" style="background-color: #F3F3F3;" @click="goToBackPage('back')"
					v-if="detailArray.taskStatus !== 3"></u-button>
				<u-button text="交接" shape="circle" style="background-color: #F3F3F3;" @click="goToBackPage('handover')"
					v-if="detailArray.taskStatus !== 3"></u-button>
				<u-button type="primary" style="color: #FFFFFF;" shape="circle" text="开始巡检" @click="goToBackPage('start')"
					v-if="detailArray.taskStatus === 2"></u-button>
				<u-button type="primary" style="color: #FFFFFF;" shape="circle" text="确认完成" @click="goToBackPage('complete')"
					v-if="detailArray.taskStatus === 4"></u-button>
				<u-button type="primary" style="color: #FFFFFF;" shape="circle" text="完成巡检" @click="goToBackPage('finish')"
					v-if="detailArray.taskStatus === 3"></u-button>


			</view>
		</view>


	</view>
</template>

<script setup>
import { ref, computed } from 'vue';
import { useStore } from 'vuex';
import { onLoad } from '@dcloudio/uni-app'
import { findInspectionTaskDetailById, updateInspectionTask, updateInspectSite, findInspectionItemListBySiteId } from '@/firstPackage/api/inspection.js'
const store = useStore();
// 生命周期钩子
onLoad((options) => {
	// 这里可以放置页面加载时的逻辑
	const data = JSON.parse(decodeURIComponent(options.data));
	inspectionTask.value = data
	getOperationDetail(data.id);
	 uni.setStorageSync('operationsCurrentIndex', 1)
})
// 标记点数据
const markers = ref([
]);
// 详细信息
const detailArray = ref({});
// 传递过来的巡检任务
const inspectionTask = ref({})
// 地图配置信息
const optionsMap = ref({
	scale: 11,
	minScale: 5, //最小缩放级别
	enable3D: true, //	是否显示3D楼块
	showScale: false, // 显示比例尺。
	showCompass: false, //是否显示指南针
	enableRotate: true, //是否支持旋转
	showlocation: true, //显示带有方向的当前定位点
	enableOverlooking: true,// 是否开启俯视
});

const getOperationDetail = (val) => {
	findInspectionTaskDetailById({ id: val }).then(res => {
		detailArray.value = res.data;
		// 根据 inspectionSiteList 生成 markers
		markers.value = generateMarkers(detailArray.value.inspectionSiteList);
	})
}
// 生成 markers 数据
const generateMarkers = (sites) => {
	return sites.map((item, index) => ({
		id: index,
		latitude: item.latitude, // 假设 item 中有 lat 字段
		longitude: item.longitude, // 假设 item 中有 lon 字段
		name: item.siteName, // 点位名称
		iconPath: '/static/image/lat-bg.png', // 图标路径
		width: 28,
		height: 30,
		label: {
			content: item.siteName,
			color: '#000000',
			fontSize: 14,
			fontweight: 'bold',
			textAlign: 'center',
			padding: 10,
		}
	}));
};
const latAndLongitude = computed(() => store.state.latAndLongitude);
const navigateToLocation = (item) => {
	wx.openLocation({
		latitude: Number(item.latitude),     // 强制转为数字
		longitude: Number(item.longitude),
		name: item.siteName,
		address: item.siteName,
		scale: 18
	});
}
const goToBackPage = (val) => {
	if (val == 'back') {
		uni.navigateTo({
			url: `/firstPackage/pages/operations/operationDetails/backPage?data=${encodeURIComponent(JSON.stringify(inspectionTask.value))}`,
		});
	} else if (val == 'handover') {
		uni.navigateTo({
			url: `/firstPackage/pages/operations/operationDetails/handoverPage?data=${encodeURIComponent(JSON.stringify(inspectionTask.value))}`,
		});
	} else if (val == 'submit') {
		uni.navigateTo({
			url: '/firstPackage/pages/operations/operationDetails/submitPage',
		});
		// 完成巡检
	} else if (val == 'finish') {
		const hasSubmitted = detailArray.value?.inspectionSiteList.some(item => item.status === 2);

		if (hasSubmitted) {
			uni.showToast({
				title: '请先填写巡检报告',
				icon: 'none',
			});
		} else {
			uni.navigateTo({
				url: `/firstPackage/pages/operations/operationDetails/submitUser?data=${encodeURIComponent(JSON.stringify(inspectionTask.value))}`,
			});
		}
		//开始巡检
	} else if (val == 'start') {
 const userId = uni.getStorageSync('USER_ID')
		updateInspectionTask({
			operationType: 4,
			id: inspectionTask.value.id,
			userId: userId
		}).then(res => {
			if (res.success) {
				uni.showToast({
					title: '开始巡检成功',
					icon: 'success',
					duration: 2000
				})
			}
			uni.reLaunch({
				url: `/firstPackage/pages/operations/operationDetails/operationDetails?data=${encodeURIComponent(JSON.stringify(inspectionTask.value))}`,
				success: () => {
					console.log('跳转成功');
				},
				fail: (err) => {
					console.error('跳转失败:', err);
				}
			});
		});
	} else if (val == 'complete') {
		uni.navigateTo({
			url: `/firstPackage/pages/operations/operationDetails/submitPage?data=${encodeURIComponent(JSON.stringify(inspectionTask.value))}`,
		});
	}

}
const reInspect = (val) => {
	updateInspectSite({
		id: val.id,
		status: 2,
	}).then(res => {
		if (res.success) {
			uni.showToast({
				title: '开始巡检成功',
				icon: 'success',
				duration: 2000
			})
			// uni.reLaunch({
			// 	url: `/firstPackage/pages/operations/operationDetails/operationDetails?data=${encodeURIComponent(JSON.stringify(inspectionTask.value))}`,
			// });
			getOperationDetail(inspectionTask.value.id);
		}
	})
}


// 放弃巡检
const giveUpInspection = (val) => {
	findInspectionItemListBySiteId({
		siteId: val.siteId
	}).then(res => {
		const newSelectedItems = {};
		res.data.forEach(section => {
			newSelectedItems[section.id] = '1'; // 默认选中 '未检查'

		});
		updateInspectSite({
			id: val.id,
			itemStates: JSON.stringify(newSelectedItems),
			status: 4,
		}).then(res => {
			if (res.success) {
				uni.showToast({
					title: '放弃巡检成功',
					icon: 'success',
					duration: 2000
				})
				// uni.reLaunch({
				// 	url: `/firstPackage/pages/operations/operationDetails/operationDetails?data=${encodeURIComponent(JSON.stringify(inspectionTask.value))}`,
				// });
				getOperationDetail(inspectionTask.value.id);
			}
		})
	})

	// console.log('val', val);
	// // 构造 itemStates 字符串
	// const itemStatesStr = JSON.stringify({ [val.id]: '1' }); // 格式: {"巡检项id": "1"}
	// console.log(val, itemStatesStr, inspectionTask.value, '***9999 ***');
	// updateInspectSite({
	// 	id: inspectionTask.value.id,
	// 	itemStates: itemStatesStr,
	// 	status: 4,
	// }).then(res => {
	// 	if (res.success) {
	// 		uni.showToast({
	// 			title: '放弃巡检成功',
	// 			icon: 'success',
	// 			duration: 2000
	// 		})
	// 		// uni.reLaunch({
	// 		// 	url: `/firstPackage/pages/operations/operationDetails/operationDetails?data=${encodeURIComponent(JSON.stringify(inspectionTask.value))}`,
	// 		// });
	// 	}
	// })
}
// 报告事件
const reportEvent = (val) => {
	uni.navigateTo({
		url: '/firstPackage/pages/operations/reports/addReport?siteName=' + encodeURIComponent(JSON.stringify(val))
	});
};
// 查看报告
const viewReport = (val) => {
	uni.navigateTo({
		url: '/firstPackage/pages/operations/reports/checkReport?siteName=' + encodeURIComponent(JSON.stringify(val))
	});
};


</script>

<style lang="scss" scoped>
.content {
	width: 100%;
	height: 100%;
	overflow: hidden;

	.stationMap {
		height: 33%;
		width: 100%;
		overflow: hidden;

		.map {
			width: 100%;
			height: 108%;
		}
	}

	.station-list {
		height: 67%;
		width: 100%;
		border-radius: 20rpx;

		.station-data {
			width: 100%;
			height: 82%;
			overflow: auto;

		}

		.station-item {
			padding: 0 43rpx;

			.station-item-title {
				width: 100%;
				border-bottom: 0.5rpx solid rgba(203, 203, 203, .6);
				padding: 47rpx 0 33rpx 0;

				.station-item-title-text {
					display: flex;
					justify-content: space-between;
				}

				.station-item-title-content {
					display: flex;
					justify-content: space-between;
					gap: 30rpx;
					margin-top: 27rpx;

					.u-button {
						padding: 8rpx 0;
						color: rgba(0, 92, 220, 1);
						background: rgba(243, 243, 243, 1);
						border: none;
					}

					.station-item-button {
						padding: 8rpx 54rpx;
						font-size: 24rpx;
						color: rgba(0, 92, 220, 1);
						font-weight: 600;
						background: rgba(243, 243, 243, 1);
						border-radius: 30rpx;

					}
				}

				.station-item-title-text-title {
					font-weight: 600;
					font-size: 26rpx;
					color: #000000;
					text-align: left;
				}

				.station-item-title-text-distance {
					font-size: 24rpx;
					color: #005CDC;
					padding: 8rpx 30rpx;
					background: rgba(243, 243, 243, 1);
					border-radius: 49rpx 49rpx 49rpx 49rpx;

				}
			}

			.line {
				border-bottom: 0.5rpx solid rgba(203, 203, 203, .6);
			}

			.station-item-content {
				width: 100%;

				padding: 30rpx 0 26rpx 0;
				display: flex;
				justify-content: space-between;

				.station-item-content-label {
					font-weight: 600;
					font-size: 26rpx;
					color: #000000;
					text-align: left;
				}

				.station-item-content-text {
					font-weight: 400;
					font-size: 26rpx;
					color: #676767;
				}
			}

		}
	}

	.station-button {
		height: 7%;
		width: 100%;
		display: flex;
		margin-top: 4%;
		justify-content: space-between;
		gap: 20rpx;
		padding: 0 43rpx;

		color: rgba(0, 92, 220, 1);

		.u-button {
			font-weight: 600;
			color: #005CDC;


		}
	}

	.map-picker {
		position: fixed;
		bottom: 0;
		left: 0;
		right: 0;
		background: #fff;
		border-top: 1rpx solid #ccc;
		z-index: 999;
	}

	.map-option {
		padding: 20rpx;
		text-align: center;
		border-bottom: 1rpx solid #eee;
	}

	.map-close {
		padding: 20rpx;
		text-align: center;
		color: #888;
	}
}
</style>