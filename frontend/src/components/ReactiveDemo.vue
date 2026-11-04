<script setup>

import { ref, computed, watch } from 'vue'
import { formatMs } from '@/utils/format'

    const count = ref(0)
    const scores = ref([61234, 12345, 78210])
    const msg = ref("上次点击：o → n")

    const total = computed(() => {
        return scores.value.length
    })

    const average = computed(() => {
        return scores.value.reduce((a, b) => a + b, 0) / scores.value.length
    })

    const best = computed(() => {
        return Math.min(...scores.value)
    })

    const pushrandomScore = () => {
        const randomScore = Math.floor(Math.random() * 85000) + 5000
        scores.value.push(randomScore)
    }
    
    watch(count, (n, o) => { msg.value = `上次点击：${o} → ${n}` })

    
</script>

<template>
    <p>点击次数：{{ count }}</p>
    <button @click="count++">+1</button>
    <button @click="count--">-1</button>
    <button @click="count = 0 " >归零</button>

    <p>{{ msg }}</p>

    <p>总次数：{{ total }}</p>
    <p>平均分：{{ formatMs(average) }}</p>
    <p>最快：{{ formatMs(best) }}</p>
    <p>成绩列表：</p>
    <div v-for="(score, index) in scores" :key="index">
        <p>{{ formatMs(score) }}</p>
    </div>
    <button @click="pushrandomScore">加一条随机成绩</button>
</template>