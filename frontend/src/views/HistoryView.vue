<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import RecordList from '@/components/RecordList.vue'
import { fetchRecordsPage } from '@/api/records'
import { deleteRecord } from '@/api/records'

// ===== 分页要存的 5 样东西 =====
const SIZE = 5          // 每页几条（先写死，够用）
const page = ref(1)     // 当前第几页
const total = ref(0)    // 一共多少条
const rows = ref([])    // 当前这一页的数据
const loading = ref(false)
const errorMsg = ref('')

// ===== 派生值：用 computed 算，不额外存 =====
const totalPages = computed(() => Math.max(1, Math.ceil(total.value / SIZE)))
const canPrev = computed(() => page.value > 1)
const canNext = computed(() => page.value < totalPages.value)

// ===== 加载当前页 =====
async function loadPage() {
  loading.value = true
  errorMsg.value = ''
  try {
    const data = await fetchRecordsPage(page.value, SIZE) // { total, records }
    rows.value = data.records
    total.value = data.total
    // 兜底：删数据后当前页可能超出总页数，自动回到最后一页
    if (page.value > totalPages.value) page.value = totalPages.value
  } catch (e) {
    errorMsg.value = '加载失败：' + e.message
  } finally {
    loading.value = false
  }
}

// ===== 删除记录 =====
async function handleDelete(id) {
  if (!window.confirm('确定删除这条成绩吗？')) return
  try {
    await deleteRecord(id)
    // 删除成功后，重新加载当前页
    await loadPage()
  } catch (e) {
    errorMsg.value = '删除失败：' + e.message
  }
}

onMounted(loadPage)     // 进入页面加载第 1 页
watch(page, loadPage)   // page 变化自动重新加载
</script>

<template>
  <section class="history-view">
    <h2 class="section-title">
      历史成绩 <span class="section-count">共 {{ total }} 条</span>
    </h2>

    <!-- 三种状态：加载中 / 出错 / 正常列表 -->
    <p v-if="loading" class="history-msg">加载中…</p>
    <p v-else-if="errorMsg" class="history-msg history-msg-error">{{ errorMsg }}</p>
    <RecordList
      v-else
      class="record-list-wide"
      :records="rows"
      :show-date="true"
      :show-delete="true"
      empty-text="本页没有数据"
      @delete="handleDelete"
    />

    <!-- 分页按钮 -->
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
</style>
