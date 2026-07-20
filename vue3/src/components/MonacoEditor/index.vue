<template>
  <div ref="editorRef" :style="`width: 100%; height: ${height}`"></div>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount, watch } from 'vue';
import * as monaco from 'monaco-editor';
import editorWorker from 'monaco-editor/esm/vs/editor/editor.worker?worker';
import jsonWorker from 'monaco-editor/esm/vs/language/json/json.worker?worker';
import tsWorker from 'monaco-editor/esm/vs/language/typescript/ts.worker?worker';
import htmlWorker from 'monaco-editor/esm/vs/language/html/html.worker?worker';

const globalMonaco = self as any;
globalMonaco.MonacoEnvironment = {
  getWorker(_workerId: string, label: string) {
    if (label === 'json') {
      return new jsonWorker();
    }
    if (label === 'typescript' || label === 'javascript') {
      return new tsWorker();
    }
    if (label === 'html') {
      return new htmlWorker();
    }
    return new editorWorker();
  },
};

const props = defineProps({
  height: {
    type: String,
    default: '300px',
  },
  language: {
    type: String,
    default: 'javascript',
  },
  isMinimap: {
    type: Boolean,
    default: true,
  },
  isQuickSuggestions: {
    type: Boolean,
    default: true,
  },
  readOnly: {
    type: Boolean,
    default: false,
  },
  value: {
    type: String,
    default: '',
  },
});

const emit = defineEmits(['change']);

const editorRef = ref<HTMLElement | null>(null);
let monacoEditor: monaco.editor.IStandaloneCodeEditor | null = null;

function init() {
  if (!editorRef.value) return;
  monacoEditor = monaco.editor.create(editorRef.value, {
    theme: 'vs-dark',
    value: props.value || '',
    language: props.language,
    tabSize: 2,
    folding: true,
    foldingHighlight: true,
    foldingStrategy: 'indentation',
    showFoldingControls: 'always',
    disableLayerHinting: true,
    emptySelectionClipboard: false,
    selectionClipboard: false,
    automaticLayout: true,
    codeLens: false,
    scrollBeyondLastLine: false,
    colorDecorators: true,
    accessibilitySupport: 'off',
    lineNumbers: 'on',
    lineNumbersMinChars: 5,
    enableSplitViewResizing: false,
    readOnly: props.readOnly,
    minimap: {
      enabled: props.isMinimap,
    },
    quickSuggestions: props.isQuickSuggestions,
  });

  monacoEditor.onDidChangeModelContent(() => {
    emit('change', monacoEditor!.getValue());
  });
}

function setValue(val: string) {
  monacoEditor?.setValue(val);
}

watch(
  () => props.value,
  (val) => {
    if (monacoEditor && val !== monacoEditor.getValue()) {
      monacoEditor.setValue(val || '');
    }
  }
);

onMounted(() => {
  init();
});

onBeforeUnmount(() => {
  monacoEditor?.dispose();
});

defineExpose({ setValue });
</script>
