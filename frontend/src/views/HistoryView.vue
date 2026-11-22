<script setup>
import RecordList from '@/components/RecordList.vue'
import { fetchRecordsPage, deleteRecord } from '@/api/records'
import { computed, onBeforeUnmount, onMounted, ref, watch } from 'vue'
// ===== 分页状态 =====
const SIZE = 5
const page = ref(1)
const total = ref(0)
const rows = ref([])
const loading = ref(false)
const errorMsg = ref('')

// ===== 筛选状态（F4 加分项）=====
const minSec = ref('')   // 用户输入的是"秒"
const maxSec = ref('')
const filtered = ref(false)
// 真正传给后端的筛选条件（毫秒）；undefined 表示不传
const filters = ref({ minMs: undefined, maxMs: undefined })

const totalPages = computed(() => Math.max(1, Math.ceil(total.value / SIZE)))
const canPrev = computed(() => page.value > 1)
const canNext = computed(() => page.value < totalPages.value)

// ===== 加载当前页 =====
async function loadPage() {
  loading.value = true
  errorMsg.value = ''
  try {
    const data = await fetchRecordsPage(page.value, SIZE, filters.value) // { total, records }
    rows.value = data.records
    total.value = data.total
    if (page.value > totalPages.value) page.value = totalPages.value
  } catch (e) {
    errorMsg.value = '加载失败：' + e.message
  } finally {
    loading.value = false
  }
}

// 秒 → 毫秒；空字符串返回 undefined（后端可选参数）
function toMs(text, label) {
  if (text === '' || text === null || text === undefined) return undefined
  const n = Number(text)
  if (Number.isNaN(n) || n < 0) throw new Error(label + ' 必须是非负数字')
  return Math.round(n * 1000)
}

// ===== 执行筛选：条件交给分页接口（后端 /record/page?minMs=&maxMs=） =====
async function applyFilter() {
  try {
    const minMs = toMs(minSec.value, '最小秒数')
    const maxMs = toMs(maxSec.value, '最大秒数')
    if (minMs === undefined && maxMs === undefined) {
      throw new Error('至少填一个筛选条件')
    }
    if (minMs !== undefined && maxMs !== undefined && minMs > maxMs) {
      throw new Error('最小值不能大于最大值')
    }
    filters.value = { minMs, maxMs }
    filtered.value = true
    page.value = 1
    await loadPage()          // page 已经是 1 时 watch 不触发，这里手动加载
  } catch (e) {
    errorMsg.value = '筛选失败：' + e.message
  }
}

function clearFilter() {
  minSec.value = ''
  maxSec.value = ''
  filtered.value = false
  filters.value = { minMs: undefined, maxMs: undefined }
  page.value = 1
  loadPage()
}

// ===== 删除 =====
async function handleDelete(id) {
  if (!window.confirm('确定删除这条成绩吗？')) return
  try {
    await deleteRecord(id)
    await loadPage()        // 带着当前筛选条件重拉
  } catch (e) {
    errorMsg.value = '删除失败：' + e.message
  }
}

function onNewRecord() {
  if (!loading.value) loadPage()
}

onMounted(() => {
  loadPage()
  window.addEventListener('cube:new-record', onNewRecord)
})

onBeforeUnmount(() => {
  window.removeEventListener('cube:new-record', onNewRecord)
})

watch(page, loadPage)
</script>

<template>
  <section class="history-view">
    <h2 class="section-title">
      历史成绩
      <span class="section-count">{{ filtered ? '筛选结果' : '共' }} {{ total }} 条</span>
    </h2>

    <!-- 筛选栏 -->
    <div class="filter-bar">
      <input v-model="minSec" class="filter-input" type="number" min="0" step="0.001" placeholder="最小秒数" />
      <span class="filter-sep">~</span>
      <input v-model="maxSec" class="filter-input" type="number" min="0" step="0.001" placeholder="最大秒数" />
      <button class="btn" :disabled="loading" @click="applyFilter">筛选</button>
      <button v-if="filtered" class="btn" :disabled="loading" @click="clearFilter">清除</button>
    </div>

    <!-- 三态显示 -->
    <p v-if="loading" class="history-msg">加载中…</p>
    <p v-else-if="errorMsg" class="history-msg history-msg-error">{{ errorMsg }}</p>
    <RecordList
      v-else
      class="record-list-wide"
      :records="rows"
      :show-date="true"
      :show-delete="true"
      :empty-text="filtered ? '没有符合条件的成绩' : '本页没有数据'"
      :linkable="true"
      @delete="handleDelete"
    />

    <!-- 分页按钮（筛选状态下也可以翻页） -->
    <div class="pager">
      <button class="btn" :disabled="!canPrev || loading" @click="page = page - 1">上一页</button>
      <span class="pager-info">第 {{ page }} / {{ totalPages }} 页</span>
      <button class="btn" :disabled="!canNext || loading" @click="page = page + 1">下一页</button>
    </div>
  </section>
</template>

<style scoped>
.history-msg {
  color: #888;
  font-size: 14px;
  padding: 12px;
}
.history-msg-error {
  color: #c05050;
}
.pager {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-top: 12px;
}
.pager-info {
  color: #888;
  font-size: 13px;
}
.filter-bar {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 8px 0 4px;
}
.filter-input {
  width: 110px;
  padding: 4px 8px;
  font-size: 13px;
  color: #ddd;
  background: #262626;
  border: 1px solid #3a3a3a;
  border-radius: 4px;
}
.filter-sep {
  color: #666;
}
</style>
