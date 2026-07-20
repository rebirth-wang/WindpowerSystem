<template>
  <div class="json-editor">
    <textarea ref="textarea" />
  </div>
</template>

<script setup lang="ts">
import { ref, watch, onMounted } from 'vue';
import CodeMirror from 'codemirror';
import 'codemirror/lib/codemirror.css';
import 'codemirror/mode/sql/sql.js';
import 'codemirror/theme/idea.css';
import 'codemirror/addon/hint/show-hint.css';
import 'codemirror/addon/hint/show-hint.js';
import 'codemirror/addon/hint/anyword-hint.js';
import 'codemirror/mode/javascript/javascript.js';
import 'codemirror/addon/fold/foldgutter.css';
import 'codemirror/addon/fold/foldcode.js';
import 'codemirror/addon/fold/foldgutter.js';
import 'codemirror/addon/fold/brace-fold.js';
import 'codemirror/addon/fold/comment-fold.js';
import 'codemirror/addon/edit/matchbrackets.js';
import 'codemirror/addon/edit/closebrackets.js';
import 'codemirror/addon/comment/comment.js';
import 'codemirror/addon/lint/lint.css';
import 'codemirror/addon/lint/lint.js';

interface Props {
  value?: string;
  height: string;
  myMode: string;
}

const props = defineProps<Props>();

const textarea = ref<HTMLTextAreaElement | null>(null);
let editor: CodeMirror.Editor | null = null;

watch(
  () => props.value,
  (value) => {
    if (!editor) return;
    const editorValue = editor.getValue();
    if (value !== editorValue) {
      editor.setValue(typeof value !== 'undefined' ? value : '');
    }
  }
);

onMounted(() => {
  if (!textarea.value) return;
  editor = CodeMirror.fromTextArea(textarea.value, {
    mode: props.myMode,
    smartIndent: true,
    styleActiveLine: true,
    lineNumbers: true,
    indentUnit: 2,
    gutters: ['CodeMirror-linenumbers', 'CodeMirror-foldgutter', 'CodeMirror-lint-markers'],
    lint: true,
    matchBrackets: true,
    autoCloseBrackets: true,
    readOnly: false,
    foldGutter: true,
  } as any);
  editor.on('inputRead', () => {
    (editor as any).showHint();
  });
  editor.setSize('auto', props.height);
  editor.setValue(typeof props.value !== 'undefined' ? props.value : '');
});

function getValue(): string {
  return editor ? editor.getValue() : '';
}

defineExpose({ getValue });
</script>

<style scoped>
.json-editor {
  height: 100%;
}

.json-editor :deep(.CodeMirror) {
  font-size: 14px;
  font-weight: normal;
}
</style>
