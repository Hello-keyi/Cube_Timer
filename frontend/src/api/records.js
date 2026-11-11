// 接口层：所有和后端交互的 fetch 都集中在这里
const BASE_URL = '/api'

/**
 * 读取全部成绩（后端按 id 倒序返回：新 → 旧）
 * @returns {Promise<Array<{ id: number, timeMs: number, createdAt: string }>>}
 */
export async function fetchRecords() {
  const res = await fetch(BASE_URL + '/record/list')
  const json = await res.json()
  if (json.code !== 0) throw new Error(json.msg || "读取失败")
  return json.data
}

/**
 * 保存一条成绩（整数毫秒）
 * @param {number} timeMs
 */
export async function createRecord(timeMs) {
  const res = await fetch(BASE_URL + '/record', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ timeMs })
})
  const json = await res.json()
  if (json.code !== 0) throw new Error(json.msg || "保存失败")
  return json.data
}

export async function fetchRecordsPage(page = 1, size = 10) {
  const res = await fetch(BASE_URL + '/record/page?page=' + page + '&size=' + size)
  const json = await res.json()
  if (json.code !== 0) throw new Error(json.msg || '读取分页失败')
  return json.data
}

export async function searchRecords(minMs, maxMs) {
  // 可选参数：没填的就不要拼进 URL（否则后端拿 "undefined" 转 Integer 会报错）
  const params = new URLSearchParams()
  if (minMs !== '' && minMs !== null && minMs !== undefined) params.append('minMs', minMs)
  if (maxMs !== '' && maxMs !== null && maxMs !== undefined) params.append('maxMs', maxMs)
  const query = params.toString()

  const res = await fetch(BASE_URL + '/record/search' + (query ? '?' + query : ''))
  const json = await res.json()
  if (json.code !== 0) throw new Error(json.msg || '查询失败')
  return json.data

}

export async function deleteRecord(id) {
  const res = await fetch(BASE_URL + '/record/' + id, { method: 'DELETE' })
  const json = await res.json()
  if (json.code !== 0) throw new Error(json.msg || '删除失败')
  return json.data
}
/**
 * 读取聚合统计（后端 D12）：total / bestText / worstText / avg5Text / avg12Text / todayBestText
 */
export async function fetchStats() {
  const res = await fetch(BASE_URL + '/record/stats')
  const json = await res.json()
  if (json.code !== 0) throw new Error(json.msg || '读取统计失败')
  return json.data
}

/**
 * F7：读取单条成绩详情（含打乱列表）
 * TODO(F7-5)：参照 fetchRecords 的套路实现
 *   GET /record/{id} → 判 json.code !== 0 → throw new Error(json.msg || '读取详情失败') → return json.data
 */
export async function fetchRecordDetail(id) {
  const res = await fetch(BASE_URL + '/record/' + id)
  const json = await res.json()
  if (json.code !== 0) throw new Error(json.msg || '读取详情失败')
  return json.data
}

/**
 * F7：给某条成绩添加一条打乱
 * TODO(F7-6)：POST /record/{id}/scramble?scramble=...（query 参数，记得 encodeURIComponent）
 *   成功无 data；失败 throw new Error(json.msg || '添加打乱失败')
 */
export async function addScramble(id, scramble) {
  const res = await fetch(BASE_URL + '/record/' + id + '/scramble?scramble=' + encodeURIComponent(scramble), {
    method: 'POST'
  })
  const json = await res.json()
  if (json.code !== 0) throw new Error(json.msg || '添加打乱失败')
  return json.data
}
