// 接口层：统一走 request（自动带 token、统一解析 Result、401 自动跳登录）
import { clearToken, getToken, postJSON, request } from './request'

/** 读取全部成绩（后端按 id 倒序：新 → 旧） */
export function fetchRecords() {
  return request('/record/list')
}

/** 保存一条成绩（整数毫秒） */
export function createRecord(timeMs) {
  return postJSON('/record', { timeMs })
}

/** 分页查询（支持 minMs/maxMs 条件）：返回 { total, records } */
export function fetchRecordsPage(page = 1, size = 10, filters = {}) {
  const params = new URLSearchParams({ page, size })
  if (filters.minMs !== undefined && filters.minMs !== null) params.append('minMs', filters.minMs)
  if (filters.maxMs !== undefined && filters.maxMs !== null) params.append('maxMs', filters.maxMs)
  return request('/record/page?' + params.toString())
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

/** 批量录入：一次提交「成绩 + 多条打乱」（后端 D16 事务） */
export function createRecordFull({ timeMs, scrambles }) {
  return postJSON('/record/full', { timeMs, scrambles })
}

/** 导出当前用户的成绩 Excel（返回二进制 Blob） */
export async function exportRecords() {
  const token = getToken()
  const res = await fetch('/api/record/export', {
    headers: token ? { Authorization: 'Bearer ' + token } : {},
  })
  if (res.status === 401) {
    clearToken()
    window.location.href = '/login'
    throw new Error('登录已过期，请重新登录')
  }
  if (!res.ok) throw new Error('导出失败（HTTP ' + res.status + '）')
  return res.blob()
}

/** 上传图片（jpg/png/gif/webp，≤5MB），返回可访问的 URL：/uploads/xxx */
export async function uploadImage(file) {
  const token = getToken()
  const form = new FormData()
  form.append('file', file)

  const res = await fetch('/api/file/upload', {
    method: 'POST',
    headers: token ? { Authorization: 'Bearer ' + token } : {},
    body: form,   // 不要手动设置 Content-Type，浏览器会自动加 boundary
  })

  if (res.status === 401) {
    clearToken()
    window.location.href = '/login'
    throw new Error('登录已过期，请重新登录')
  }
  const json = await res.json()
  if (json.code !== 0) throw new Error(json.msg || '上传失败')
  return json.data
}
