
<script setup>

    import { ref, computed , watch} from 'vue'
    import {format} from './format.js'

    const count = ref(0)
    const scores = ref([61234, 12345, 78210])
    const msg = ref("上次点击：o → n")

    const total = computed(() => {
        return scores.value.length
    })

    const average = computed(() => {
        return total.value / scores.value.length
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
    <p>{{ msg }}</p>

    <p>总次数：{{ format(total) }}</p>
    <p>平均分：{{ format(average) }}</p>
    <p>最快：{{ format(best) }}</p>
    <p>成绩列表：</p>
    <div v-for="index in scores" :key="index">
        <p>{{ format(scores[index]) }}</p>
    </div>
    <button @click="pushrandomScore">加一条随机成绩</button>
</template>