// 接口层：统一走 request（自动带 token、统一解析 Result、401 自动跳登录）
import { request, postJSON } from './request'

/** 读取全部成绩（后端按 id 倒序：新 → 旧） */
export function fetchRecords() {
  return request('/record/list')
}

/** 保存一条成绩（整数毫秒） */
export function createRecord(timeMs) {
  return postJSON('/record', { timeMs })
}

/** 分页查询：返回 { total, records } */
export function fetchRecordsPage(page = 1, size = 10) {
  return request('/record/page?page=' + page + '&size=' + size)
}

/** 按时间区间筛选（可选参数不传） */
export function searchRecords(minMs, maxMs) {
  const params = new URLSearchParams()
  if (minMs !== '' && minMs !== null && minMs !== undefined) params.append('minMs', minMs)
  if (maxMs !== '' && maxMs !== null && maxMs !== undefined) params.append('maxMs', maxMs)
  const query = params.toString()
  return request('/record/search' + (query ? '?' + query : ''))
}

/** 删除一条成绩 */
export function deleteRecord(id) {
  return request('/record/' + id, { method: 'DELETE' })
}

/** 聚合统计（AVG5/12、最佳、最差、今日最佳） */
export function fetchStats() {
  return request('/record/stats')
}

/** 单条成绩详情（含打乱列表） */
export function fetchRecordDetail(id) {
  return request('/record/' + id)
}

/** 给某条成绩添加打乱公式 */
export function addScramble(id, scramble) {
  return request('/record/' + id + '/scramble?scramble=' + encodeURIComponent(scramble), {
    method: 'POST',
  })
}
