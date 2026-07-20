<template>
  <div
    style="
      border: 0px solid #ebebeb;
      overflow: hidden;
      border-radius: 6px;
      background-color: #ebebeb;
      padding: 8px 5px 8px 0;
    "
  >
    <div ref="editorContainer" :style="{ width: width, height: height }"></div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, onMounted, onBeforeUnmount } from 'vue';
import * as ace from 'ace-builds';
import 'ace-builds/src-noconflict/mode-groovy';
import 'ace-builds/src-noconflict/mode-mysql';
import 'ace-builds/src-noconflict/mode-json';
import 'ace-builds/src-noconflict/theme-chrome';
import 'ace-builds/src-noconflict/snippets/groovy';
import 'ace-builds/src-noconflict/snippets/json';
import 'ace-builds/src-noconflict/ext-language_tools';
import 'ace-builds/src-noconflict/ext-beautify';

interface Props {
  width?: string;
  height?: string;
  content: string;
  lang?: string;
  readOnly?: boolean;
  codeStyle?: string;
}

const props = withDefaults(defineProps<Props>(), {
  width: '100%',
  height: '500px',
  lang: 'groovy',
  readOnly: false,
  codeStyle: 'chrome',
});

const emit = defineEmits<{
  'update:content': [value: string];
}>();

const editorContainer = ref<HTMLElement | null>(null);
let editor: ace.Ace.Editor | null = null;

onMounted(() => {
  if (!editorContainer.value) return;
  editor = ace.edit(editorContainer.value, {
    mode: `ace/mode/${props.lang}`,
    theme: `ace/theme/${props.codeStyle}`,
    readOnly: props.readOnly,
    enableBasicAutocompletion: true,
    enableLiveAutocompletion: true,
    enableSnippets: true,
    showPrintMargin: false,
    fontSize: 13,
  });
  editor.setValue(props.content ?? '', -1);
  editor.on('change', () => {
    emit('update:content', editor!.getValue());
  });
});

onBeforeUnmount(() => {
  if (editor) {
    editor.destroy();
    editor = null;
  }
});

watch(
  () => props.content,
  (val) => {
    if (editor && val !== editor.getValue()) {
      editor.setValue(val ?? '', -1);
    }
  }
);

function format() {
  if (!editor) return;
  const beautify = ace.require('ace/ext/beautify');
  beautify.beautify(editor.session);
}

defineExpose({ format });
</script>
