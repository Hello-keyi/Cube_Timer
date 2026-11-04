<script setup>

import { ref } from 'vue'

const score = ref('')
const error = ref('')
const emit = defineEmits(['add'])

function validate() {
  const n = Number(score.value)
  if (score.value === '' || Number.isNaN(n) || n <= 0) {
    error.value = 'Score must be a positive number.'
    return false
  }
  error.value = ''
  return true
}

function clearScore() {
  score.value = ''
  error.value = ''
}

function submitScore() {
  if (!validate()) return
  emit('add', Math.round(Number(score.value) * 1000))
  clearScore()
}

</script>

<template
>
  <div class="score-input">
    <label for="score">Score:</label>
    <input type="number" id="score" v-model="score" />
    <p v-if="error" class="error">{{ error }}</p>
    <button @click="submitScore">Add Score</button>
  </div>



</template>