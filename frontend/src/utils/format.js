// 毫秒 → 秒，保留 3 位小数
export function formatMs(ms) {
  return (ms / 1000).toFixed(3)
}

// "2026-10-25T18:27:49" → "2026-10-25 18:27:49"
export function formatDate(createdAt) {
  return createdAt ? String(createdAt).replace('T', ' ') : ''
}

// 最近 n 条成绩的平均值（毫秒）；没有数据返回 null
export function averageMs(records, n) {
  const list = records.slice(0, n)
  if (list.length === 0) return null
  return list.reduce((sum, r) => sum + r.timeMs, 0) / list.length
}
