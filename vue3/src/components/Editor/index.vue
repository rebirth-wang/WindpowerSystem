<template>
  <div>
    <el-upload
      :action="uploadUrl"
      :before-upload="handleBeforeUpload"
      :on-success="handleUploadSuccess"
      :on-error="handleUploadError"
      name="file"
      :show-file-list="false"
      :headers="headers"
      style="display: none"
      ref="upload"
      v-if="type == 'url'"
    ></el-upload>
    <div class="editor" ref="editor" :style="styles"></div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, onBeforeUnmount, getCurrentInstance } from 'vue';
import Quill from 'quill';
import 'quill/dist/quill.core.css';
import 'quill/dist/quill.snow.css';
import 'quill/dist/quill.bubble.css';
import { getToken } from '@/utils/auth';
import { ElMessage } from 'element-plus';

const { proxy } = getCurrentInstance()!;

const props = defineProps({
  /* 编辑器的内容 */
  value: {
    type: String,
    default: '',
  },
  /* 高度 */
  height: {
    type: Number,
    default: null,
  },
  /* 最小高度 */
  minHeight: {
    type: Number,
    default: null,
  },
  /* 只读 */
  readOnly: {
    type: Boolean,
    default: false,
  },
  // 上传文件大小限制(MB)
  fileSize: {
    type: Number,
    default: 5,
  },
  /* 类型（base64格式、url格式） */
  type: {
    type: String,
    default: 'url',
  },
  url_type: {
    default: 0,
  },
});

const emit = defineEmits(['input', 'on-change', 'on-text-change', 'on-selection-change', 'on-editor-change']);

const uploadUrl = import.meta.env.VITE_APP_BASE_API + '/common/upload';
const headers = ref({
  Authorization: 'Bearer ' + getToken(),
});
const quillInstance = ref<any>(null);
const currentValue = ref('');
const editor = ref<HTMLElement | null>(null);
const upload = ref<any>(null);

const styles = computed(() => {
  const style: Record<string, string> = {};
  if (props.minHeight) {
    style.minHeight = `${props.minHeight}px`;
  }
  if (props.height) {
    style.height = `${props.height}px`;
  }
  return style;
});

watch(
  () => props.value,
  (val) => {
    if (val !== currentValue.value) {
      currentValue.value = val === null ? '' : val;
      if (quillInstance.value && quillInstance.value.clipboard) {
        quillInstance.value.clipboard.dangerouslyPasteHTML(currentValue.value);
      }
    }
  },
  { immediate: true }
);

function init() {
  const editorEl = editor.value;
  if (!editorEl) return;
  const options = {
    theme: 'snow',
    bounds: document.body,
    debug: 'warn',
    modules: {
      toolbar: [
        ['bold', 'italic', 'underline', 'strike'],
        ['blockquote', 'code-block'],
        [{ list: 'ordered' }, { list: 'bullet' }],
        [{ indent: '-1' }, { indent: '+1' }],
        [{ size: ['small', false, 'large', 'huge'] }],
        [{ header: [1, 2, 3, 4, 5, 6, false] }],
        [{ color: [] }, { background: [] }],
        [{ align: [] }],
        ['clean'],
        ['link', 'image', 'video'],
      ],
    },
    placeholder: (proxy as any)?.$t?.('plzInput') ?? '请输入内容',
    readOnly: props.readOnly,
  };
  quillInstance.value = new Quill(editorEl, options);
  if (props.type === 'url') {
    const toolbar = quillInstance.value.getModule('toolbar');
    toolbar.addHandler('image', (value: boolean) => {
      if (value) {
        (upload.value as any)?.$children?.[0]?.$refs?.input?.click();
      } else {
        quillInstance.value.format('image', false);
      }
    });
  }
  if (quillInstance.value.clipboard) {
    quillInstance.value.clipboard.dangerouslyPasteHTML(currentValue.value);
  }
  quillInstance.value.on('text-change', (_delta: any, _oldDelta: any, _source: any) => {
    const html = (editorEl.children[0] as HTMLElement).innerHTML;
    const text = quillInstance.value.getText();
    const quill = quillInstance.value;
    currentValue.value = html;
    emit('input', html);
    emit('on-change', { html, text, quill });
  });
  quillInstance.value.on('text-change', (delta: any, oldDelta: any, source: any) => {
    emit('on-text-change', delta, oldDelta, source);
  });
  quillInstance.value.on('selection-change', (range: any, oldRange: any, source: any) => {
    emit('on-selection-change', range, oldRange, source);
  });
  quillInstance.value.on('editor-change', (eventName: string, ...args: any[]) => {
    emit('on-editor-change', eventName, ...args);
  });
}

// 上传前校检格式和大小
function handleBeforeUpload(file: File) {
  if (props.fileSize) {
    const isLt = file.size / 1024 / 1024 < props.fileSize;
    if (!isLt) {
      ElMessage.error(`上传文件大小不能超过 ${props.fileSize} MB!`);
      return false;
    }
  }
  return true;
}

function handleUploadSuccess(res: any, file: any) {
  const quill = quillInstance.value;
  if (res.code === 200) {
    const length = quill.getSelection().index;
    if (props.url_type === 0) {
      quill.insertEmbed(length, 'image', import.meta.env.VITE_APP_BASE_API + res.fileName);
    } else {
      const baseUrl = 'http://' + window.location.host + import.meta.env.VITE_APP_BASE_API;
      quill.insertEmbed(length, 'image', baseUrl + res.fileName);
    }
    quill.setSelection(length + 1);
  } else {
    ElMessage.error('图片插入失败');
  }
}

function handleUploadError() {
  ElMessage.error('图片插入失败');
}

onMounted(() => {
  init();
});

onBeforeUnmount(() => {
  quillInstance.value = null;
});
</script>

<style>
.editor,
.ql-toolbar {
  white-space: pre-wrap !important;
  line-height: normal !important;
}

.quill-img {
  display: none;
}

.ql-snow .ql-tooltip[data-mode='link']::before {
  content: '请输入链接地址:';
}

.ql-snow .ql-tooltip.ql-editing a.ql-action::after {
  border-right: 0px;
  content: '保存';
  padding-right: 0px;
}

.ql-snow .ql-tooltip[data-mode='video']::before {
  content: '请输入视频地址:';
}

.ql-snow .ql-picker.ql-size .ql-picker-label::before,
.ql-snow .ql-picker.ql-size .ql-picker-item::before {
  content: '14px';
}

.ql-snow .ql-picker.ql-size .ql-picker-label[data-value='small']::before,
.ql-snow .ql-picker.ql-size .ql-picker-item[data-value='small']::before {
  content: '10px';
}

.ql-snow .ql-picker.ql-size .ql-picker-label[data-value='large']::before,
.ql-snow .ql-picker.ql-size .ql-picker-item[data-value='large']::before {
  content: '18px';
}

.ql-snow .ql-picker.ql-size .ql-picker-label[data-value='huge']::before,
.ql-snow .ql-picker.ql-size .ql-picker-item[data-value='huge']::before {
  content: '32px';
}

.ql-snow .ql-picker.ql-header .ql-picker-label::before,
.ql-snow .ql-picker.ql-header .ql-picker-item::before {
  content: '文本';
}

.ql-snow .ql-picker.ql-header .ql-picker-label[data-value='1']::before,
.ql-snow .ql-picker.ql-header .ql-picker-item[data-value='1']::before {
  content: '标题1';
}

.ql-snow .ql-picker.ql-header .ql-picker-label[data-value='2']::before,
.ql-snow .ql-picker.ql-header .ql-picker-item[data-value='2']::before {
  content: '标题2';
}

.ql-snow .ql-picker.ql-header .ql-picker-label[data-value='3']::before,
.ql-snow .ql-picker.ql-header .ql-picker-item[data-value='3']::before {
  content: '标题3';
}

.ql-snow .ql-picker.ql-header .ql-picker-label[data-value='4']::before,
.ql-snow .ql-picker.ql-header .ql-picker-item[data-value='4']::before {
  content: '标题4';
}

.ql-snow .ql-picker.ql-header .ql-picker-label[data-value='5']::before,
.ql-snow .ql-picker.ql-header .ql-picker-item[data-value='5']::before {
  content: '标题5';
}

.ql-snow .ql-picker.ql-header .ql-picker-label[data-value='6']::before,
.ql-snow .ql-picker.ql-header .ql-picker-item[data-value='6']::before {
  content: '标题6';
}

.ql-snow .ql-picker.ql-font .ql-picker-label::before,
.ql-snow .ql-picker.ql-font .ql-picker-item::before {
  content: '标准字体';
}

.ql-snow .ql-picker.ql-font .ql-picker-label[data-value='serif']::before,
.ql-snow .ql-picker.ql-font .ql-picker-item[data-value='serif']::before {
  content: '衬线字体';
}

.ql-snow .ql-picker.ql-font .ql-picker-label[data-value='monospace']::before,
.ql-snow .ql-picker.ql-font .ql-picker-item[data-value='monospace']::before {
  content: '等宽字体';
}
</style>
