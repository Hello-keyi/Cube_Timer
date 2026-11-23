import { ref } from 'vue'
import { fetchRecords, createRecord } from '@/api/records'

// 模块级的单例状态：所有组件共用同一份成绩数据
const records = ref([])
const loading = ref(false)
const error = ref('')

let loaded = false

export function useRecords() {
  // 读取成绩；force = true 时强制重新请求
  async function load(force = false) {
    if (loaded && !force) return
    loading.value = true
    error.value = ''
    try {
      records.value = await fetchRecords()
      loaded = true
    } catch (e) {
      error.value = '读取失败：' + e.message
    } finally {
      loading.value = false
    }
  }

  // 保存一条成绩，成功后重新拉取列表
  async function add(timeMs) {
    error.value = ''
    try {
      await createRecord(timeMs)
      await load(true)
    } catch (e) {
      error.value = '保存失败：' + e.message
      throw e
    }
  }

  // 退出登录时调用：清空数据，避免下一个账号看到上一个账号的缓存
  function reset() {
    records.value = []
    loading.value = false
    error.value = ''
    loaded = false
  }

  return { records, loading, error, load, add, reset }
}
