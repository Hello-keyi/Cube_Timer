<script setup>
// 成绩详情页：展示单条成绩 + 打乱公式，支持新增打乱
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { fetchRecordDetail, addScramble } from '@/api/records'

const route = useRoute()
const router = useRouter()

// 路由参数 /record/:id 里的 id 是字符串，转成数字用
const recordId = computed(() => Number(route.params.id))

const detail = ref(null)      // 详情数据 { id, timeMs, timeText, createdText, scrambles: [] }
const loading = ref(false)
const errorMsg = ref('')

const newScramble = ref('')   // 输入框内容
const submitting = ref(false)

// ===== 加载详情 =====
async function loadDetail() {
  loading.value = true
  errorMsg.value = ''
  try {
    detail.value = await fetchRecordDetail(recordId.value)
  } catch (e) {
    errorMsg.value = '加载失败：' + e.message
  } finally {
    loading.value = false
  }
}

// ===== 添加一条打乱 =====
async function submitScramble() {
  if (!newScramble.value.trim()) {
    errorMsg.value = '打乱公式不能为空'
    return
  }

  submitting.value = true

  try {
    await addScramble(recordId.value, newScramble.value)
    // 成功后：清空输入 + 刷新数据
    newScramble.value = ''
    await loadDetail()
  } catch (e) {
    errorMsg.value = '添加失败：' + e.message
  } finally {
    submitting.value = false
  }
}

// ===== 返回 =====
function goBack() {
  router.push('/history')
}

onMounted(loadDetail)
</script>

<template>
  <section class="detail-view">
    <h2 class="section-title">成绩详情</h2>

    <!-- 三态：加载中 / 出错 / 详情（骨架已经搭好，你不用改结构） -->
    <p v-if="loading" class="detail-msg">加载中…</p>
    <p v-else-if="errorMsg" class="detail-msg detail-msg-error">{{ errorMsg }}</p>

    <template v-else-if="detail">
      <div class="detail-card">
        <p class="detail-time">{{ detail.timeText }} 秒</p>
        <p class="detail-date">{{ detail.createdText }}</p>

        <h3 class="detail-subtitle">打乱记录（{{ detail.scrambles.length }}）</h3>

        <ul v-if="detail.scrambles.length" class="scramble-list">
          <li v-for="(s, i) in detail.scrambles" :key="i" class="scramble-item">{{ s }}</li>
        </ul>
        <p v-else class="detail-msg">还没有打乱记录</p>

        <div class="scramble-add">
          <input
            v-model="newScramble"
            class="scramble-input"
            placeholder="输入打乱公式，如 R U R' U'"
          />
          <button class="btn" :disabled="submitting" @click="submitScramble">添加打乱</button>
        </div>
      </div>

      <button class="btn" @click="goBack">返回历史</button>
    </template>
  </section>
</template>

<style scoped>
.detail-view {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding: 20px;
}
.detail-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  padding: 20px;
  background: #222;
  border-radius: 8px;
}
.detail-time {
  font-size: 28px;
  color: #fff;
}
.detail-date {
  font-size: 13px;
  color: #888;
}
.detail-subtitle {
  font-size: 14px;
  color: #aaa;
  margin-top: 8px;
}
.scramble-list {
  list-style: none;
  padding: 0;
  margin: 0;
}
.scramble-item {
  font-size: 14px;
  color: #bbb;
  padding: 4px 0;
}
.scramble-add {
  display: flex;
  gap: 8px;
  margin-top: 8px;
}
.scramble-input {
  padding: 4px 8px;
  font-size: 13px;
  color: #ddd;
  background: #262626;
  border: 1px solid #3a3a3a;
  border-radius: 4px;
  width: 220px;
}
.detail-msg {
  color: #888;
  font-size: 14px;
  padding: 12px;
}
.detail-msg-error {
  color: #c05050;
}
</style>
