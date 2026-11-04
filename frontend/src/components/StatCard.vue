<script setup>
defineProps({
  label: { type: String, required: true },
  value: { type: String, default: '--' },
})
</script>

<template>
  <div class="stat">
    <div class="stat-label">{{ label }}</div>
    <div class="stat-value">{{ value }}</div>
  </div>
</template>
