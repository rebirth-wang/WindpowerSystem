<template>
  <div class="upload-file">
    <el-upload
      multiple
      :accept="getAccept()"
      :action="uploadFileUrl"
      :before-upload="handleBeforeUpload"
      :file-list="fileList"
      :limit="limit"
      :on-error="handleUploadError"
      :on-exceed="handleExceed"
      :on-success="handleUploadSuccess"
      :show-file-list="false"
      :headers="headers"
      class="upload-file-uploader"
      ref="fileUpload"
    >
      <!-- 上传按钮 -->
      <el-button size="small" type="primary">{{ $t('components.FileUpload.index.232435-0') }}</el-button>
      <!-- 上传提示 -->
      <template #tip>
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
      </template>
    </el-upload>

    <!-- 文件列表 -->
    <transition-group class="upload-file-list el-upload-list el-upload-list--text" name="el-fade-in-linear" tag="ul">
      <li :key="file.url" class="el-upload-list__item ele-upload-list__item-content" v-for="(file, index) in fileList">
        <el-link :href="`${baseUrl}${file.url}`" underline="never" target="_blank">
          <span class="el-icon-document">{{ getFileName(file.name) }}</span>
        </el-link>
        <div class="ele-upload-list__item-content-action">
          <el-link underline="never" @click="handleDelete(index)" type="danger">
            {{ $t('components.FileUpload.index.232435-5') }}
          </el-link>
        </div>
      </li>
    </transition-group>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, getCurrentInstance } from 'vue';
import { getToken } from '@/utils/auth';

const { proxy } = getCurrentInstance()!;

const props = defineProps({
  // 值
  value: [String, Object, Array],
  // 数量限制
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
    default: () => ['doc', 'xlsx', 'xls', 'ppt', 'txt', 'pdf'],
  },
  // 是否显示提示
  isShowTip: {
    type: Boolean,
    default: true,
  },
  uploadFileUrl: {
    type: String,
    default: import.meta.env.VITE_APP_BASE_API + '/iot/tool/upload',
  },
  type: {
    type: Number,
    default: 0, // 0: 使用默认路径, 1: 使用全路径
  },
});

const emit = defineEmits(['input']);

const number = ref(0);
const uploadList = ref<any[]>([]);
const baseUrl = import.meta.env.VITE_APP_BASE_API;
const headers = ref({
  Authorization: 'Bearer ' + getToken(),
});
const fileList = ref<any[]>([]);
const fileUpload = ref<any>(null);

watch(
  () => props.value,
  (val: any) => {
    if (val) {
      let temp = 1;
      const list = Array.isArray(val) ? val : (val as string).split(',');
      fileList.value = list.map((item: any) => {
        if (typeof item === 'string') {
          item = { name: item, url: item };
        }
        item.uid = item.uid || new Date().getTime() + temp++;
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

// 上传前校检格式和大小
function handleBeforeUpload(file: File) {
  if (props.fileType) {
    const fileName = file.name.split('.');
    const fileExt = fileName[fileName.length - 1];
    const isTypeOk = (props.fileType as string[]).indexOf(fileExt) >= 0;
    if (!isTypeOk) {
      (proxy as any).$modal.msgError(
        (proxy as any).$t('components.FileUpload.index.232435-6') +
          (props.fileType as string[]).join('/') +
          (proxy as any).$t('components.FileUpload.index.232435-7')
      );
      return false;
    }
  }
  if (props.fileSize) {
    const isLt = file.size / 1024 / 1024 < props.fileSize;
    if (!isLt) {
      (proxy as any).$modal.msgError((proxy as any).$t('components.FileUpload.index.232435-8') + `${props.fileSize} MB!`);
      return false;
    }
  }
  (proxy as any).$modal.loading((proxy as any).$t('components.FileUpload.index.232435-9'));
  number.value++;
  return true;
}

// 文件个数超出
function handleExceed() {
  (proxy as any).$modal.msgError((proxy as any).$t('components.FileUpload.index.232435-10') + `${props.limit} 个!`);
}

// 上传失败
function handleUploadError(_err: any) {
  (proxy as any).$modal.msgError((proxy as any).$t('components.FileUpload.index.232435-11'));
  (proxy as any).$modal.closeLoading();
}

// 上传成功回调
function handleUploadSuccess(res: any, file: any) {
  if (res.code === 200) {
    if (props.type === 1) {
      uploadList.value.push({ name: res.fileName, url: res.url });
    } else {
      uploadList.value.push({ name: res.fileName, url: res.fileName });
    }
    uploadedSuccessfully();
  } else {
    number.value--;
    (proxy as any).$modal.closeLoading();
    (proxy as any).$modal.msgError('上传文件失败，请检查相关配置');
    console.log(res.msg);
    fileUpload.value?.handleRemove(file);
    uploadedSuccessfully();
  }
}

// 删除文件
function handleDelete(index: number) {
  fileList.value.splice(index, 1);
  emit('input', listToString(fileList.value));
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

// 获取文件名称
function getFileName(name: string) {
  if (name.lastIndexOf('/') > -1) {
    return name.slice(name.lastIndexOf('/') + 1);
  } else {
    return '';
  }
}

// 对象转成指定字符串分隔
function listToString(list: any[], separator?: string) {
  let strs = '';
  separator = separator || ',';
  for (const i in list) {
    strs += list[i].url + separator;
  }
  return strs !== '' ? strs.substr(0, strs.length - 1) : '';
}

// 获取接受上传的文件类型
function getAccept() {
  if (props.fileType !== 0) {
    const list = (props.fileType as string[]).map((item) => `.${item}`);
    return list.join(',');
  }
}
</script>

<style scoped lang="scss">
.upload-file-uploader {
  margin-bottom: 5px;
}

.upload-file-list .el-upload-list__item {
  border: 1px solid #e4e7ed;
  line-height: 2;
  margin-bottom: 10px;
  position: relative;
}

.upload-file-list .ele-upload-list__item-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: inherit;
  background: transparent;
}

.ele-upload-list__item-content .el-icon-document:before {
  margin-right: 5px;
}

.ele-upload-list__item-content-action .el-link {
  margin-right: 10px;
}

:deep(.upload-file-list .el-upload-list__item) {
  border: none;
}
</style>
