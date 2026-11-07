<script setup>
import { RouterLink } from 'vue-router'
</script>

<template>
  <section class="not-found">
    <h2 class="section-title">404</h2>
    <p class="not-found-text">这个页面不存在</p>
    <RouterLink class="btn" to="/">回到计时页</RouterLink>
  </section>
</template>

<style scoped>
.not-found {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
  padding: 40px;
}
.not-found-text {
  color: #888;
}
</style>
