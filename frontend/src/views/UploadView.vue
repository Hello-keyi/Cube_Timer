<script setup>
// F15 图片上传练习页：选择 → 预览 → 上传 → 显示后端返回的 URL
import { ref } from 'vue'
import { uploadImage } from '@/api/records'

const file = ref(null)
const previewUrl = ref('')     // 本地预览（URL.createObjectURL）
const uploadedUrl = ref('')    // 后端返回的 /uploads/xxx
const uploading = ref(false)
const errorMsg = ref('')

const MAX_SIZE = 5 * 1024 * 1024
const ALLOWED = ['image/jpeg', 'image/png', 'image/gif', 'image/webp']

function onFileChange(event) {
  errorMsg.value = ''
  uploadedUrl.value = ''
  const f = event.target.files && event.target.files[0]
  if (!f) return

  if (!ALLOWED.includes(f.type)) {
    errorMsg.value = '只支持 jpg/png/gif/webp 图片'
    file.value = null
    return
  }
  if (f.size > MAX_SIZE) {
    errorMsg.value = '图片不能超过 5MB'
    file.value = null
    return
  }

  file.value = f
  if (previewUrl.value) URL.revokeObjectURL(previewUrl.value)
  previewUrl.value = URL.createObjectURL(f)
}

async function submit() {
  if (!file.value) {
    errorMsg.value = '请先选择图片'
    return
  }
  uploading.value = true
  errorMsg.value = ''
  try {
    uploadedUrl.value = await uploadImage(file.value)
  } catch (e) {
    errorMsg.value = e.message
  } finally {
    uploading.value = false
  }
}
</script>

<template>
  <section class="upload-view">
    <h2 class="section-title">图片上传</h2>

    <input type="file" accept="image/jpeg,image/png,image/gif,image/webp" @change="onFileChange" />

    <p v-if="errorMsg" class="upload-error">{{ errorMsg }}</p>

    <div v-if="previewUrl" class="upload-preview">
      <p class="upload-label">本地预览：</p>
      <img :src="previewUrl" alt="预览" class="upload-img" />
    </div>

    <button class="btn" :disabled="uploading || !file" @click="submit">
      {{ uploading ? '上传中…' : '上传' }}
    </button>

    <div v-if="uploadedUrl" class="upload-result">
      <p class="upload-label">上传成功，后端返回：{{ uploadedUrl }}</p>
      <img :src="uploadedUrl" alt="已上传" class="upload-img" />
    </div>
  </section>
</template>

<style scoped>
.upload-view {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 14px;
  padding: 24px;
}
.upload-error {
  color: #c05050;
  font-size: 13px;
}
.upload-preview,
.upload-result {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}
.upload-label {
  color: #888;
  font-size: 13px;
}
.upload-img {
  max-width: 320px;
  max-height: 240px;
  border-radius: 6px;
  border: 1px solid #3a3a3a;
}
</style>
