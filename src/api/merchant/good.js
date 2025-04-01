import request from '@/utils/request'

// 查询仓库列表
export function listGood(query) {
  return request({
    url: '/merchant/good/list',
    method: 'get',
    params: query
  })
}

// 查询仓库详细
export function getGood(id) {
  return request({
    url: '/merchant/good/' + id,
    method: 'get'
  })
}

// 新增仓库
export function addGood(data) {
  return request({
    url: '/merchant/good',
    method: 'post',
    data: data
  })
}

// 修改仓库
export function updateGood(data) {
  return request({
    url: '/merchant/good',
    method: 'put',
    data: data
  })
}

// 删除仓库
export function delGood(id) {
  return request({
    url: '/merchant/good/' + id,
    method: 'delete'
  })
}
