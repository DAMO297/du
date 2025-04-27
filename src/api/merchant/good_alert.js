import request from '@/utils/request'

// 预警配置
export function getConfigList(params) {
  return request({
    url: '/merchant/alert/config/list',
    method: 'get',
    params
  })
}

export function addConfig(data) {
  return request({
    url: '/merchant/alert/config',
    method: 'post',
    data
  })
}

export function updateConfig(data) {
  return request({
    url: '/merchant/alert/config',
    method: 'put',
    data
  })
}

export function deleteConfig(ids) {
  return request({
    url: `/merchant/alert/config/${ids}`,
    method: 'delete'
  })
}

// 预警记录
export function getRecordList(params) {
  return request({
    url: '/merchant/alert/record/list',
    method: 'get',
    params
  })
}

export function handleRecord(id, handler, handleRemark) {
  return request({
    url: `/merchant/alert/record/handle/${id}`,
    method: 'put',
    params: { handler, handleRemark }
  })
}

export function deleteRecord(ids) {
  return request({
    url: `/merchant/alert/record/${ids}`,
    method: 'delete'
  })
}

// 预警检查
export function checkAlerts() {
  return request({
    url: '/merchant/alert/check',
    method: 'post'
  })
}