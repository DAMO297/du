import request from '@/utils/request'

// 查询消息日志列表
export function listLog(query) {
  return request({
    url: '/system/message/log/list',
    method: 'get',
    params: query
  })
}

// 标记消息为已读
export function markAsRead(logId) {
  return request({
    url: '/system/message/log/markAsRead/' + logId,
    method: 'put'
  })
}

// 批量标记消息为已读
export function markAsReadBatch(logIds) {
  return request({
    url: '/system/message/log/markAsReadBatch',
    method: 'put',
    data: logIds
  })
}

// 导出消息日志
export function exportLog(query) {
  return request({
    url: '/system/message/log/export',
    method: 'get',
    params: query
  })
}

// 查询消息模板列表
export function listTemplate(query) {
  return request({
    url: '/system/message/template/list',
    method: 'get',
    params: query
  })
}

// 查询消息模板详细
export function getTemplate(templateId) {
  return request({
    url: '/system/message/template/' + templateId,
    method: 'get'
  })
}

// 新增消息模板
export function addTemplate(data) {
  return request({
    url: '/system/message/template',
    method: 'post',
    data: data
  })
}

// 修改消息模板
export function updateTemplate(data) {
  return request({
    url: '/system/message/template',
    method: 'put',
    data: data
  })
}

// 删除消息模板
export function delTemplate(templateId) {
  return request({
    url: '/system/message/template/' + templateId,
    method: 'delete'
  })
}

// 导出消息模板
export function exportTemplate(query) {
  return request({
    url: '/system/message/template/export',
    method: 'get',
    params: query
  })
}