// 毫秒 → 秒，保留 3 位小数
export function formatMs(ms) {
  return (ms / 1000).toFixed(3)
}
