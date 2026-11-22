import { onBeforeUnmount, onMounted } from 'vue'
import { useRecords } from '@/composables/useRecords'

// 全局 WebSocket：收到 NEW_RECORD 就刷新数据
export function useRecordSocket() {
  let socket = null
  let reconnectTimer = null
  let closedByUs = false

  function connect() {
    // 随便一个唯一 id，后端只用作 session 标识
    const sid = Math.random().toString(36).slice(2)
    const scheme = location.protocol === 'https:' ? 'wss' : 'ws'
    socket = new WebSocket(`${scheme}://${location.host}/ws/${sid}`)

    socket.onmessage = (event) => {
      let msg = null
      try {
        msg = JSON.parse(event.data)
      } catch {
        return // 非 JSON 消息忽略
      }
      if (msg.type === 'NEW_RECORD') {
        // 1) 刷新共享的成绩列表（计时页"最近 5 次"、统计会自动跟着变）
        const { load } = useRecords()
        load(true)
        // 2) 通知其他页面（如历史页，它自己管理分页数据）
        window.dispatchEvent(new CustomEvent('cube:new-record', { detail: msg }))
      }
    }

    socket.onclose = () => {
      if (!closedByUs) {
        reconnectTimer = setTimeout(connect, 3000) // 断线 3 秒后重连
      }
    }

    socket.onerror = () => {
      if (socket) socket.close()
    }
  }

  onMounted(connect)

  onBeforeUnmount(() => {
    closedByUs = true
    clearTimeout(reconnectTimer)
    if (socket) socket.close()
  })
}