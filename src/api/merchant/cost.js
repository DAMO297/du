import request from '@/utils/request'

// 查询邮费列表
export function listCost(query) {
  return request({
    url: '/merchant/cost/list',
    method: 'get',
    params: query
  })
}

// 查询邮费详细
export function getCost(id) {
  return request({
    url: '/merchant/cost/' + id,
    method: 'get'
  })
}

// 新增邮费
export function addCost(data) {
  return request({
    url: '/merchant/cost',
    method: 'post',
    data: data
  })
}

// 修改邮费
export function updateCost(data) {
  return request({
    url: '/merchant/cost',
    method: 'put',
    data: data
  })
}

// 删除邮费
export function delCost(id) {
  return request({
    url: '/merchant/cost/' + id,
    method: 'delete'
  })
}
