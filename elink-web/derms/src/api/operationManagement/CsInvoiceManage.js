import request from '@/utils/request';

// 统计运营总览数据
export function queryInvoiceList (data) {
  return request({
    url: '/together/invoice/queryInvoiceList',
    portNum: 60009,
    method: 'post',
    data: data
  });
}
// 查询操作记录列表
export function queryInvoiceRecordList (data) {
  return request({
    url: '/together/invoice/queryInvoiceRecordList',
    portNum: 60009,
    method: 'post',
    data: data
  });
}
// 根据发票申请单号查询发票订单详情
export function findInvoiceOrderById (data) {
  return request({
    url: '/together/invoice/findInvoiceOrderById',
    portNum: 60009,
    method: 'post',
    data: data
  });
}
// 修改发票状态
export function updateInvoiceStatus (data) {
  return request({
    url: '/together/invoice/updateInvoiceStatus',
    portNum: 60009,
    method: 'post',
    data: data
  });
}
// 根据发票申请单号查询发票详情
export function findInvoiceDetailById (data) {
  return request({
    url: '/together/invoice/findInvoiceDetailById',
    portNum: 60009,
    method: 'post',
    data: data
  });
}
