<template>
  <el-dialog v-model="visible" title="上传图片" width="520px" append-to-body>
    <div style="display: flex; flex-direction: column; gap: 12px">
      <el-input v-model="imgUrl" placeholder="请输入图片 URL" clearable />
      <div v-if="imgUrl" style="text-align: center">
        <img :src="imgUrl" alt="preview" style="max-width: 100%; max-height: 220px" />
      </div>
      <el-upload
        :show-file-list="false"
        :auto-upload="false"
        accept="image/*"
        :on-change="handleFileChange"
      >
        <el-button type="primary" plain>本地选择图片</el-button>
      </el-upload>
    </div>
    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="confirmUpload">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import type { UploadFile } from 'element-plus';

defineOptions({ name: 'Uploadimg' });

const emit = defineEmits<{
  (event: 'uploadInformation', url: string): void;
}>();

const visible = ref(false);
const imgUrl = ref('');

function showUpload() {
  visible.value = true;
}

function handleFileChange(file: UploadFile) {
  const raw = file.raw;
  if (!raw) return;
  imgUrl.value = URL.createObjectURL(raw);
}

function confirmUpload() {
  if (!imgUrl.value) return;
  emit('uploadInformation', imgUrl.value);
  visible.value = false;
}

defineExpose({ showUpload });
</script>
