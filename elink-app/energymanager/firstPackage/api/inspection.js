import {
	ajax,
	dj_upload
} from '@/utils/request.js';

// 查询地图站点列表
export function findInspectionTaskDetailById (data) {
	return ajax({
		url: "/devops/inspect/findInspectionTaskDetailById",
		nologinRequired: true, // 请求接口不需要登录
		data
	})
};
// 修改巡检任务
export function updateInspectionTask (data) {
	return ajax({
		url: "/devops/inspect/updateInspectionTask",
		nologinRequired: true, // 请求接口不需要登录
		data
	})
};
// 根据用户id和节点类型查询巡检节点人员用户名称
export function findInspectionUserList (data) {
	return ajax({
		url: "/devops/inspect/findInspectionUserList",
		nologinRequired: true, // 请求接口不需要登录
		data
	})
};
// 更新巡检站点报表

// export function updateInspectSite (formData, files) {
// 	if (files && files.length) {
// 		return dj_upload({
// 			url: '/devops/inspect/updateInspectSite',
// 			nologinRequired: true,
// 			formData: formData,
// 			files: files,
// 		});
// 	} else {
// 		return ajax({
// 			url: '/devops/inspect/updateInspectSite',
// 			nologinRequired: true,
// 			data: formData
// 		});
// 	}

// }
export function uploadInspectSiteFile (formData, files) {
	return dj_upload({
		url: '/devops/inspect/uploadInspectSiteFile',
		nologinRequired: true,
		formData: formData,
		files: files,
	});

};
export function updateInspectSite (data) {
	return ajax({
		url: "/devops/inspect/updateInspectSite",
		nologinRequired: true, // 请求接口不需要登录
		data
	})
};

export function findInspectionItemListBySiteId (data) {

	return ajax({
		url: "/devops/inspect/findInspectionItemListBySiteId",
		nologinRequired: true, // 请求接口不需要登录
		data,


	})
};
// 根据巡检站点主键id查询巡检站点详情数据
export function findInspectionSiteListById (data) {
	return ajax({
		url: "/devops/inspect/findInspectionSiteListById",
		nologinRequired: true, // 请求接口不需要登录
		data
	})
};


