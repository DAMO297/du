import request from "@/utils/request";

// 查询邮费报告列表
export function listCostReport(query) {
  return request({
    url: "/merchant/costreport/list",
    method: "get",
    params: {
      ...query,
      userId: localStorage.getItem("userId"),
      deptId: localStorage.getItem("deptId"),
    },
  });
}

// 获取邮费报告详细信息
export function getCostReport(id) {
  return request({
    url: "/merchant/costreport/" + id,
    method: "get",
    params: {
      userId: localStorage.getItem("userId"),
      deptId: localStorage.getItem("deptId"),
    },
  });
}

// 新增邮费报告
export function addCostReport(data) {
  return request({
    url: "/merchant/costreport",
    method: "post",
    data: {
      ...data,
      userId: localStorage.getItem("userId"),
      deptId: localStorage.getItem("deptId"),
    },
  });
}

// 修改邮费报告
export function updateCostReport(data) {
  return request({
    url: "/merchant/costreport",
    method: "put",
    data: {
      ...data,
      userId: localStorage.getItem("userId"),
      deptId: localStorage.getItem("deptId"),
    },
  });
}

// 删除邮费报告
export function delCostReport(ids) {
  return request({
    url: "/merchant/costreport/" + ids,
    method: "delete",
    params: {
      userId: localStorage.getItem("userId"),
      deptId: localStorage.getItem("deptId"),
    },
  });
}

// 生成周期性邮费报告
export function generatePeriodicReport(reportType) {
  return request({
    url: "/merchant/costreport/generate-periodic",
    method: "post",
    params: {
      reportType: reportType,
      userId: localStorage.getItem("userId"),
      deptId: localStorage.getItem("deptId"),
    },
  });
}

// 生成自定义时间段的邮费报告
export function generateCustomReport(startDate, endDate) {
  // 转换日期字符串为时间戳
  const startTimestamp = new Date(startDate).getTime();
  const endTimestamp = new Date(endDate).getTime();

  const params = {
    startDate: startTimestamp,
    endDate: endTimestamp,
    userId: localStorage.getItem("userId"),
    deptId: localStorage.getItem("deptId"),
  };

  return request({
    url: "/merchant/costreport/generate-custom",
    method: "post",
    data: params,
  });
}

// 获取邮费优化建议
export function getOptimizationSuggestions(reportId) {
  return request({
    url: "/merchant/costreport/optimization-suggestions/" + reportId,
    method: "get",
    params: {
      userId: localStorage.getItem("userId"),
      deptId: localStorage.getItem("deptId"),
    },
  });
}

// 导出邮费报告
export function exportReport(reportId) {
  return request({
    url: "/merchant/costreport/export-report/" + reportId,
    method: "get",
    params: {
      userId: localStorage.getItem("userId"),
      deptId: localStorage.getItem("deptId"),
    },
  });
}

// 导出邮费报告列表
export function exportCostReport(query) {
  return request({
    url: "/merchant/costreport/export",
    method: "post",
    params: {
      ...query,
      userId: localStorage.getItem("userId"),
      deptId: localStorage.getItem("deptId"),
    },
  });
}

// 测试指定日期范围内是否有数据
export function testDataAvailability(startDate, endDate) {
  return request({
    url: "/merchant/costreport/test-data",
    method: "get",
    params: {
      startDate: new Date(startDate).getTime(),
      endDate: new Date(endDate).getTime(),
      userId: localStorage.getItem("userId"),
      deptId: localStorage.getItem("deptId"),
    },
  });
}

// 查看数据库表结构
export function checkTableStructure() {
  return request({
    url: "/merchant/costreport/check-structure",
    method: "get",
    params: {
      userId: localStorage.getItem("userId"),
      deptId: localStorage.getItem("deptId"),
    },
  });
}

// 获取所有成本记录（用于测试）
export function getAllCostRecords() {
  return request({
    url: "/merchant/costreport/all-records",
    method: "get",
    params: {
      userId: localStorage.getItem("userId"),
      deptId: localStorage.getItem("deptId"),
    },
  });
}
