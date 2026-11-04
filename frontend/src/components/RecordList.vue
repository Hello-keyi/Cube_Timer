<script setup>

// 复用的成绩列表：计时页用它显示"最近 5 次"，历史页用它显示全部
defineProps({
  records: { type: Array, default: () => [] },
  showDate: { type: Boolean, default: false },
  emptyText: { type: String, default: '还没有成绩' },
  showDelete: { type: Boolean, default: false }
})
const emit = defineEmits(['delete'])

</script>


<template>
  <div class="record-list">
    <div v-for="item in records" :key="item.id" class="record-item">
      <span class="record-time">{{ item.timeText }}</span>
      <span v-if="showDate" class="record-date">{{ item.createdText }}</span>
      <button v-if="showDelete" class="delete-btn" @click="emit('delete', item.id)">删除</button>
    </div>
    <p v-if="records.length === 0" class="record-empty">{{ emptyText }}</p>
  </div>
</template>
