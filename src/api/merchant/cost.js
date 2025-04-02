import request from "@/utils/request";

// 查询邮费列表
export function listCost(query) {
  return request({
    url: "/merchant/cost/list",
    method: "get",
    params: query,
  });
}

// 查询邮费详细
export function getCost(id) {
  return request({
    url: "/merchant/cost/" + id,
    method: "get",
  });
}

// 新增邮费
export function addCost(data) {
  return request({
    url: "/merchant/cost",
    method: "post",
    data: data,
  });
}

// 修改邮费
export function updateCost(data) {
  return request({
    url: "/merchant/cost",
    method: "put",
    data: data,
  });
}

// 删除邮费
export function delCost(id) {
  return request({
    url: "/merchant/cost/" + id,
    method: "delete",
  });
}

// 查询物流信息
export function queryLogisticsInfo(trackingNumber) {
  return request({
    url: "/merchant/express/query",
    method: "get",
    params: { trackingNumber },
  });
}
// OCR图片识别
export function recognizeImage(base64Image) {
  return request({
    url: "/merchant/ocr/recognize",
    method: "post",
    data: {
      file: base64Image,
    },
  });
}
