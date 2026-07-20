<template>
  <div class="component-upload-image">
    <el-upload
      ref="imageUpload"
      :action="action"
      list-type="picture-card"
      :on-success="handleUploadSuccess"
      :before-upload="handleBeforeUpload"
      :limit="limit"
      :multiple="multiple"
      :on-error="handleUploadError"
      :on-exceed="handleExceed"
      :on-remove="handleDelete"
      :show-file-list="true"
      :headers="headers"
      :file-list="fileList"
      :on-preview="handlePictureCardPreview"
      :class="{ hide: fileList.length >= limit }"
      :disabled="disabled"
    >
      <el-icon><Plus /></el-icon>
    </el-upload>

    <!-- 上传提示 -->
    <div class="el-upload__tip" v-if="showTip">
      {{ $t('components.FileUpload.index.232435-1') }}
      <template v-if="fileSize">
        {{ $t('components.FileUpload.index.232435-2') }}
        <b style="color: #f56c6c">{{ fileSize }}MB</b>
      </template>
      <template v-if="fileType">
        {{ $t('components.FileUpload.index.232435-3') }}
        <b style="color: #f56c6c">{{ fileType.join('/') }}</b>
      </template>
      {{ $t('components.FileUpload.index.232435-4') }}
    </div>

    <el-dialog v-model="dialogVisible" :title="$t('components.ImageUpload.384733-0')" width="800" append-to-body>
      <img :src="dialogImageUrl" style="display: block; max-width: 100%; margin: 0 auto" />
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, getCurrentInstance } from 'vue';
import { Plus } from '@element-plus/icons-vue';
import { getToken } from '@/utils/auth';

const { proxy } = getCurrentInstance()!;

const props = defineProps({
  value: [String, Object, Array],
  // 图片数量限制
  limit: {
    type: Number,
    default: 5,
  },
  // 大小限制(MB)
  fileSize: {
    type: Number,
    default: 5,
  },
  // 文件类型, 例如['png', 'jpg', 'jpeg']
  fileType: {
    type: Array,
    default: () => ['png', 'jpg', 'jpeg'],
  },
  // 是否显示提示
  isShowTip: {
    type: Boolean,
    default: true,
  },
  // 是否支持多选文件
  multiple: {
    type: Boolean,
    default: true,
  },
  // 是否禁止删除
  disabled: {
    type: Boolean,
    default: false,
  },
  action: {
    type: String,
    default: import.meta.env.VITE_APP_BASE_API + '/common/upload',
  },
});

const emit = defineEmits(['input']);

const number = ref(0);
const uploadList = ref<any[]>([]);
const dialogImageUrl = ref('');
const dialogVisible = ref(false);
const baseUrl = import.meta.env.VITE_APP_BASE_API;
const headers = ref({
  Authorization: 'Bearer ' + getToken(),
});
const fileList = ref<any[]>([]);
const imageUpload = ref<any>(null);

watch(
  () => props.value,
  (val: any) => {
    if (val) {
      const list = Array.isArray(val) ? val : (val as string).split(',');
      fileList.value = list.map((item: any) => {
        if (typeof item === 'string') {
          if (item.indexOf(baseUrl) === -1) {
            item = { name: baseUrl + item, url: baseUrl + item };
          } else {
            item = { name: item, url: item };
          }
        }
        return item;
      });
    } else {
      fileList.value = [];
    }
  },
  { deep: true, immediate: true }
);

const showTip = computed(() => {
  return props.isShowTip && (props.fileType || props.fileSize);
});

// 上传前loading加载
function handleBeforeUpload(file: File) {
  let isImg = false;
  const fileTypeArr = props.fileType as string[];
  if (fileTypeArr.length) {
    let fileExtension = '';
    if (file.name.lastIndexOf('.') > -1) {
      fileExtension = file.name.slice(file.name.lastIndexOf('.') + 1);
    }
    isImg = fileTypeArr.some((type) => {
      if (file.type.indexOf(type) > -1) return true;
      if (fileExtension && fileExtension.indexOf(type) > -1) return true;
      return false;
    });
  } else {
    isImg = file.type.indexOf('image') > -1;
  }

  if (!isImg) {
    (proxy as any).$modal.msgError(
      (proxy as any).$t('components.FileUpload.index.232435-6') +
        `${fileTypeArr.join('/')}` +
        (proxy as any).$t('components.FileUpload.index.232435-7')
    );
    return false;
  }
  if (props.fileSize) {
    const isLt = file.size / 1024 / 1024 < props.fileSize;
    if (!isLt) {
      (proxy as any).$modal.msgError((proxy as any).$t('components.ImageUpload.384733-1') + `${props.fileSize} MB!`);
      return false;
    }
  }
  (proxy as any).$modal.loading((proxy as any).$t('components.ImageUpload.384733-2'));
  number.value++;
}

// 文件个数超出
function handleExceed() {
  (proxy as any).$modal.msgError(
    (proxy as any).$t('components.ImageUpload.384733-3') + `${props.limit}` + (proxy as any).$t('components.ImageUpload.384733-4')
  );
}

// 上传成功回调
function handleUploadSuccess(res: any, file: any) {
  if (res.code === 200) {
    uploadList.value.push({ name: res.fileName, url: res.fileName });
    uploadedSuccessfully();
  } else {
    number.value--;
    (proxy as any).$modal.closeLoading();
    (proxy as any).$modal.msgError(res.msg);
    imageUpload.value?.handleRemove(file);
    uploadedSuccessfully();
  }
}

// 删除图片
function handleDelete(file: any) {
  const findex = fileList.value.map((f) => f.name).indexOf(file.name);
  if (findex > -1) {
    fileList.value.splice(findex, 1);
    emit('input', listToString(fileList.value));
  }
}

// 上传失败
function handleUploadError() {
  (proxy as any).$modal.msgError((proxy as any).$t('components.ImageUpload.384733-5'));
  (proxy as any).$modal.closeLoading();
}

// 上传结束处理
function uploadedSuccessfully() {
  if (number.value > 0 && uploadList.value.length === number.value) {
    fileList.value = fileList.value.concat(uploadList.value);
    uploadList.value = [];
    number.value = 0;
    emit('input', listToString(fileList.value));
    (proxy as any).$modal.closeLoading();
  }
}

// 预览
function handlePictureCardPreview(file: any) {
  dialogImageUrl.value = file.url;
  dialogVisible.value = true;
}

// 对象转成指定字符串分隔
function listToString(list: any[], separator?: string) {
  let strs = '';
  separator = separator || ',';
  for (const i in list) {
    if (list[i].url) {
      strs += list[i].url.replace(baseUrl, '') + separator;
    }
  }
  return strs !== '' ? strs.substr(0, strs.length - 1) : '';
}
</script>

<style lang="scss" scoped>
// .el-upload--picture-card 控制加号部分
:deep(.hide .el-upload--picture-card) {
  display: none;
}

// 去掉动画效果
:deep(.el-list-enter-active),
:deep(.el-list-leave-active) {
  transition: all 0s;
}

:deep(.el-list-enter),
.el-list-leave-active {
  opacity: 0;
  transform: translateY(0);
}
</style>
