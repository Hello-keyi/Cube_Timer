<script setup>
// F8 批量录入页（骨架 —— 你来实现 TODO）
// 契约：POST /record/full  body:{timeMs, scrambles?:string[]}  → Result<Void>（后端事务，失败整体回滚）
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { createRecordFull } from '@/api/records'

const router = useRouter()

const timeSec = ref('')          // 用户输入的是"秒"
const scrambles = ref([''])      // 多条打乱，初始一行
const submitting = ref(false)
const errorMsg = ref('')

function addScramble() {
  // TODO(F8-1)：给 scrambles 加一行空字符串
}

function removeScramble(index) {
  // TODO(F8-2)：删掉第 index 行（至少保留 1 行）
}

async function submit() {
  // TODO(F8-3)：
  //  1) 校验 timeSec：空 / 非数字 / <=0 → errorMsg 并 return
  //  2) 组装：timeMs = Math.round(Number(timeSec.value) * 1000)
  //  3) 过滤空白打乱：scrambles.value.map(s => s.trim()).filter(Boolean)
  //  4) submitting = true; errorMsg = ''
  //  5) await createRecordFull({ timeMs, scrambles: 过滤后的数组 })
  //  6) 成功 → router.push('/history')
  //  7) catch (e) { errorMsg.value = e.message } finally { submitting.value = false }
}
</script>

<template>
  <section class="new-record-view">
    <h2 class="section-title">手动录入成绩</h2>

    <form class="record-form" @submit.prevent="submit">
      <label class="form-row">
        <span class="form-label">成绩（秒）</span>
        <input
          v-model="timeSec"
          class="form-input"
          type="number"
          min="0"
          step="0.001"
          placeholder="例如 12.345"
        />
      </label>

      <div class="scramble-block">
        <div class="form-label">打乱公式（可多条）</div>
        <div v-for="(item, i) in scrambles" :key="i" class="scramble-row">
          <input v-model="scrambles[i]" class="form-input" placeholder="例如 R U R' U'" />
          <button
            type="button"
            class="mini-btn"
            :disabled="scrambles.length === 1"
            @click="removeScramble(i)"
          >
            删除
          </button>
        </div>
        <button type="button" class="btn" @click="addScramble">+ 加一条打乱</button>
      </div>

      <p v-if="errorMsg" class="form-error">{{ errorMsg }}</p>

      <button class="btn" type="submit" :disabled="submitting">
        {{ submitting ? '提交中…' : '提交' }}
      </button>
    </form>
  </section>
</template>

<style scoped>
.new-record-view {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 14px;
  padding: 20px;
}
.record-form {
  display: flex;
  flex-direction: column;
  gap: 14px;
  width: 360px;
}
.form-row {
  display: flex;
  align-items: center;
  gap: 10px;
}
.form-label {
  color: #aaa;
  font-size: 14px;
}
.form-input {
  flex: 1;
  padding: 6px 10px;
  font-size: 14px;
  color: #ddd;
  background: #262626;
  border: 1px solid #3a3a3a;
  border-radius: 4px;
}
.scramble-block {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.scramble-row {
  display: flex;
  align-items: center;
  gap: 8px;
}
.mini-btn {
  padding: 3px 8px;
  font-size: 12px;
  color: #fff;
  background: #8f2b2b;
  border: 1px solid #b23a3a;
  border-radius: 4px;
  cursor: pointer;
}
.mini-btn:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}
.form-error {
  color: #c05050;
  font-size: 13px;
}
</style>
