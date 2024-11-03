import request from '@/utils/request'

// 查询账单列表
export function listBill(query) {
  return request({
    url: '/merchant/bill/list',
    method: 'get',
    params: query
  })
}

// 查询账单详细
export function getBill(id) {
  return request({
    url: '/merchant/bill/' + id,
    method: 'get'
  })
}

// 新增账单
export function addBill(data) {
  return request({
    url: '/merchant/bill',
    method: 'post',
    data: data
  })
}

// 修改账单
export function updateBill(data) {
  return request({
    url: '/merchant/bill',
    method: 'put',
    data: data
  })
}

// 删除账单
export function delBill(id) {
  return request({
    url: '/merchant/bill/' + id,
    method: 'delete'
  })
}
