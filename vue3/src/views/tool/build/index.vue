<template>
  <div class="container">
    <div class="left-board">
      <div class="logo-wrapper">
        <div class="logo">
          <img :src="logo" alt="logo" />
          <span class="label">Form Generator</span>
        </div>
      </div>
      <div class="left-scrollbar">
        <div class="components-list">
          <div class="components-title">
            <svg-icon icon-class="input" />
            {{ $t('build.index.2090840-0') }}
          </div>
          <draggable
            class="components-draggable"
            :list="inputComponents"
            :group="{ name: 'componentsGroup', pull: 'clone', put: false, revertClone: true }"
            :clone="cloneComponent"
            draggable=".components-item"
            :sort="false"
            @end="onEnd"
            :item-key="getComponentKey"
          >
            <template #item="{ element, index }">
              <div class="components-item" @click="addComponent(element)">
                <div class="components-body">
                  <svg-icon :icon-class="element.tagIcon" />
                  <span class="label">{{ element.label }}</span>
                </div>
              </div>
            </template>
          </draggable>
          <div class="components-title">
            <svg-icon icon-class="coverage" size="28" />
            {{ $t('build.index.2090840-1') }}
          </div>
          <draggable
            class="components-draggable"
            :list="selectComponents"
            :group="{ name: 'componentsGroup', pull: 'clone', put: false, revertClone: true }"
            :clone="cloneComponent"
            draggable=".components-item"
            :sort="false"
            @end="onEnd"
            :item-key="getComponentKey"
          >
            <template #item="{ element, index }">
              <div class="components-item" @click="addComponent(element)">
                <div class="components-body">
                  <svg-icon :icon-class="element.tagIcon" />
                  <span class="label">{{ element.label }}</span>
                </div>
              </div>
            </template>
          </draggable>
          <div class="components-title">
            <svg-icon icon-class="layout" />
            {{ $t('build.index.2090840-2') }}
          </div>
          <draggable
            class="components-draggable"
            :list="layoutComponents"
            :group="{ name: 'componentsGroup', pull: 'clone', put: false }"
            :clone="cloneComponent"
            draggable=".components-item"
            :sort="false"
            @end="onEnd"
            :item-key="getComponentKey"
          >
            <template #item="{ element, index }">
              <div class="components-item" @click="addComponent(element)">
                <div class="components-body">
                  <svg-icon :icon-class="element.tagIcon" />
                  <span class="label">{{ element.label }}</span>
                </div>
              </div>
            </template>
          </draggable>
        </div>
      </div>
    </div>

    <div class="center-board">
      <div class="collapse" @click="toggleCollapse">
        <el-icon><Fold /></el-icon>
      </div>
      <div class="action-bar">
        <el-button :icon="Download" type="primary" link @click="download">
          {{ $t('build.index.2090840-3') }}
        </el-button>
        <el-button class="copy-btn-main" :icon="CopyDocument" type="primary" link @click="copy">
          {{ $t('build.index.2090840-4') }}
        </el-button>
        <el-button class="delete-btn" :icon="Delete" type="danger" link @click="empty">
          {{ $t('build.index.2090840-5') }}
        </el-button>
      </div>
      <div class="center-scrollbar">
        <el-row class="center-board-row" :gutter="formConf.gutter">
          <el-col :span="24">
            <el-form
              :size="normalizeElementSize(formConf.size)"
              :label-position="formConf.labelPosition"
              :disabled="formConf.disabled"
              :label-width="formConf.labelWidth + 'px'"
            >
              <draggable
                class="drawing-board"
                :list="drawingList"
                :fallback-on-body="true"
                :animation="340"
                group="componentsGroup"
                item-key="formId"
              >
                <template #item="{ element, index }">
                  <draggable-item
                    :drawing-list="drawingList"
                    :element="element"
                    :index="index"
                    :active-id="activeId"
                    :form-conf="formConf"
                    @activeItem="activeFormItem"
                    @copyItem="drawingItemCopy"
                    @deleteItem="drawingItemDelete"
                  />
                </template>
              </draggable>
              <div v-show="!drawingList.length" class="empty-info">
                {{ $t('build.index.2090840-6') }}
              </div>
            </el-form>
          </el-col>
        </el-row>
      </div>
    </div>
    <div class="right-board" :class="rightCollapse ? '' : 'right-board-collapse'">
      <right-panel
        :active-data="activeData"
        :form-conf="formConf"
        :show-field="!!drawingList.length"
        @tag-change="tagChange"
      />
    </div>

    <code-type-dialog
      v-model:visible="dialogVisible"
      :title="$t('build.index.2090840-7')"
      :show-file-name="showFileName"
      @confirm="generate"
    />
    <input id="copyNode" type="hidden" />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, watch, onMounted, nextTick, getCurrentInstance } from 'vue';
import { useI18n } from 'vue-i18n';
import draggable from 'vuedraggable';
import beautifier from 'js-beautify';
import ClipboardJS from 'clipboard';
import RightPanel from './right-panel.vue';
import CodeTypeDialog from './code-type-dialog.vue';
import DraggableItem from './draggable-item.vue';
import { inputComponents, selectComponents, layoutComponents, formConf } from '@/utils/generator/config';
import { beautifierConf, titleCase } from '@/utils/index';
import { makeUpHtml, vueTemplate, vueScript, cssStyle } from '@/utils/generator/html';
import { makeUpJs } from '@/utils/generator/js';
import { makeUpCss } from '@/utils/generator/css';
import drawingDefault from '@/utils/generator/drawingDefault';
import logo from '@/assets/images/logo_blue.png';
import { Delete, Download, CopyDocument, Fold } from '@element-plus/icons-vue';

const { proxy } = getCurrentInstance() as any;
const { t } = useI18n();

let oldActiveId: any;
let tempActiveData: any;

const idGlobal = ref(100);
const drawingList = ref<any[]>(drawingDefault);
const drawingData = ref<any>({});
const activeId = ref(drawingDefault[0]?.formId);
const drawerVisible = ref(false);
const formData = ref<any>({});
const dialogVisible = ref(false);
const generateConf = ref<any>(null);
const showFileName = ref(false);
const activeData = ref<any>(drawingDefault[0]);
const rightCollapse = ref(true);
const operationType = ref('');

// 防止 firefox 下 拖拽 会新打开一个选项卡
document.body.ondrop = (event: any) => {
  event.preventDefault();
  event.stopPropagation();
};

watch(
  () => activeData.value?.label,
  (val, oldVal) => {
    if (activeData.value?.placeholder === undefined || !activeData.value?.tag || oldActiveId !== activeId.value) return;
    activeData.value.placeholder = activeData.value.placeholder.replace(oldVal, '') + val;
  }
);

watch(
  activeId,
  (val) => {
    oldActiveId = val;
  },
  { immediate: true }
);

onMounted(() => {
  const clipboard = new ClipboardJS('#copyNode', {
    text: () => {
      const codeStr = generateCode();
      proxy.$notify({
        title: t('success'),
        message: t('build.index.2090840-8'),
        type: 'success',
      });
      return codeStr;
    },
  });
  clipboard.on('error', () => {
    proxy.$modal.msgError(t('build.index.2090840-9'));
  });
});

function toggleCollapse() {
  rightCollapse.value = !rightCollapse.value;
}

function activeFormItem(element: any) {
  activeData.value = element;
  activeId.value = element.formId;
}

function normalizeElementSize(size: string) {
  if (size === 'medium') return 'default';
  if (size === 'mini') return 'small';
  return size;
}

function getComponentKey(element: any, index: number) {
  return `${element?.type || element?.label || 'component'}_${index}`;
}

function onEnd(obj: any) {
  if (obj.from !== obj.to) {
    activeData.value = tempActiveData;
    activeId.value = idGlobal.value;
  }
}

function addComponent(item: any) {
  const clone = cloneComponent(item);
  drawingList.value.push(clone);
  activeFormItem(clone);
}

function cloneComponent(origin: any) {
  const clone = JSON.parse(JSON.stringify(origin));
  clone.formId = ++idGlobal.value;
  clone.renderKey = +new Date();
  if (!clone.layout) clone.layout = 'colFormItem';
  if (clone.layout === 'colFormItem') {
    clone.vModel = `field${idGlobal.value}`;
    clone.placeholder !== undefined && (clone.placeholder += clone.label);
    tempActiveData = clone;
  } else if (clone.layout === 'rowFormItem') {
    delete clone.label;
    clone.componentName = `row${idGlobal.value}`;
    clone.gutter = formConf.gutter;
    tempActiveData = clone;
  }
  return tempActiveData;
}

function AssembleFormData() {
  formData.value = {
    fields: JSON.parse(JSON.stringify(drawingList.value)),
    ...formConf,
  };
}

function generate(data: any) {
  const funcMap: any = {
    run: execRun,
    download: execDownload,
    copy: execCopy,
  };
  generateConf.value = data;
  const func = funcMap[operationType.value];
  func && func(data);
}

function execRun(data: any) {
  AssembleFormData();
  drawerVisible.value = true;
}

function execDownload(data: any) {
  const codeStr = generateCode();
  const blob = new Blob([codeStr], { type: 'text/plain;charset=utf-8' });
  const link = document.createElement('a');
  link.href = URL.createObjectURL(blob);
  link.download = data.fileName;
  link.click();
  URL.revokeObjectURL(link.href);
}

function execCopy(data: any) {
  document.getElementById('copyNode')?.click();
}

function empty() {
  proxy.$modal.confirm(t('build.index.2090840-10')).then(() => {
    drawingList.value = [];
  });
}

function drawingItemCopy(item: any, parent: any) {
  let clone = JSON.parse(JSON.stringify(item));
  clone = createIdAndKey(clone);
  parent.push(clone);
  activeFormItem(clone);
}

function createIdAndKey(item: any) {
  item.formId = ++idGlobal.value;
  item.renderKey = +new Date();
  if (item.layout === 'colFormItem') {
    item.vModel = `field${idGlobal.value}`;
  } else if (item.layout === 'rowFormItem') {
    item.componentName = `row${idGlobal.value}`;
  }
  if (Array.isArray(item.children)) {
    item.children = item.children.map((childItem: any) => createIdAndKey(childItem));
  }
  return item;
}

function drawingItemDelete(index: number, parent: any) {
  parent.splice(index, 1);
  nextTick(() => {
    const len = drawingList.value.length;
    if (len) {
      activeFormItem(drawingList.value[len - 1]);
    }
  });
}

function generateCode() {
  const { type } = generateConf.value;
  AssembleFormData();
  const script = vueScript(makeUpJs(formData.value, type));
  const html = vueTemplate(makeUpHtml(formData.value, type));
  const css = cssStyle(makeUpCss(formData.value));
  return beautifier.html(html + script + css, beautifierConf.html);
}

function download() {
  dialogVisible.value = true;
  showFileName.value = true;
  operationType.value = 'download';
}

function copy() {
  dialogVisible.value = true;
  showFileName.value = false;
  operationType.value = 'copy';
}

function tagChange(newTag: any) {
  newTag = cloneComponent(newTag);
  newTag.vModel = activeData.value.vModel;
  newTag.formId = activeId.value;
  newTag.span = activeData.value.span;
  delete activeData.value.tag;
  delete activeData.value.tagIcon;
  delete activeData.value.document;
  Object.keys(newTag).forEach((key: string) => {
    if (activeData.value[key] !== undefined && typeof activeData.value[key] === typeof newTag[key]) {
      newTag[key] = activeData.value[key];
    }
  });
  activeData.value = newTag;
  updateDrawingList(newTag, drawingList.value);
}

function updateDrawingList(newTag: any, list: any[]) {
  const index = list.findIndex((item: any) => item.formId === activeId.value);
  if (index > -1) {
    list.splice(index, 1, newTag);
  } else {
    list.forEach((item: any) => {
      if (Array.isArray(item.children)) updateDrawingList(newTag, item.children);
    });
  }
}
</script>

<style lang="scss" scoped>
$selectedColor: #fff;
$lighterBlue: #409eff;

.container {
  position: relative;
  width: 100%;
  height: calc(100vh - 50px);
  overflow: hidden;
  display: flex;
  flex-direction: row;
  background: #f5f7fa;
  border-top: 1px solid #ebeef5;

  .left-board {
    width: 250px;
    background: #fff;
    height: 100vh;
    border-right: 1px solid #ebeef5;
    .logo-wrapper {
      position: relative;
      height: 42px;
      padding: 0 15px;
      border-bottom: 1px solid #f1e8e8;
      .logo {
        display: flex;
        align-items: center;
        height: 100%;
        color: #0e74ee;
        font-weight: 600;
        font-size: 17px;
        white-space: nowrap;
        .label {
          margin-left: 5px;
        }
        > img {
          width: 30px;
          height: 30px;
          vertical-align: top;
        }
      }
    }
    .left-scrollbar {
      width: 100%;
      overflow: hidden;
      background: #fff;
      .components-list {
        padding: 13px;
        height: 100%;
        .components-item {
          display: inline-block;
          width: 48%;
          margin: 1%;
          transition: transform 0ms !important;
        }
        .components-draggable {
          padding-bottom: 20px;
        }
        .components-title {
          font-size: 14px;
          color: #222;
          margin: 6px 2px;
          .svg-icon {
            color: #666;
            font-size: 18px;
            margin-right: 3px;
          }
        }
      }
    }
  }

  .center-board {
    flex: 1;
    display: flex;
    flex-direction: column;
    position: relative;
    background: transparent;
    .collapse {
      position: absolute;
      right: 0;
      top: calc(50% - 40px);
      width: 20px;
      height: 80px;
      background: #fff;
      border-radius: 10px 0 0 10px;
      display: flex;
      align-items: center;
      justify-content: center;
      opacity: 0.5;
      cursor: pointer;
      z-index: 2;
      &:hover {
        opacity: 1;
      }
    }
    .action-bar {
      position: relative;
      height: 42px;
      text-align: right;
      background: #fff;
      padding: 0 12px;
      border-bottom: 1px solid #ebeef5;
      line-height: 42px;
      .delete-btn {
        color: #f56c6c;
      }
      & .el-button + .el-button {
        margin-left: 15px;
      }
      & i {
        font-size: 16px;
        vertical-align: middle;
        position: relative;
        top: -1px;
      }
    }
    .center-scrollbar {
      flex: 1;
      overflow-y: auto;
      width: 100%;
      overflow-x: hidden;
      .center-board-row {
        padding: 15px 12px;

        :deep(.el-form) {
          height: calc(100vh - 120px);
          background: transparent;
          border: 0;
          padding: 0;
        }

        .drawing-board {
          width: 100%;
          height: 100%;
          position: relative;
          display: block;

          :deep(.drawing-item) {
            width: 100%;
            max-width: 100%;
          }

          :deep(.drawing-item > .el-form-item) {
            margin: 0 0 10px;
            padding: 6px 10px;
            background: #fff;
            border: 1px solid #ebeef5;
            border-radius: 2px;
          }

          :deep(.drawing-item > .el-form-item .el-form-item__label) {
            width: 110px !important;
            color: #606266;
            padding-right: 12px;
            line-height: 32px;
            font-size: 12px;
            box-sizing: border-box;
            white-space: nowrap;
          }

          :deep(.drawing-item > .el-form-item .el-form-item__content) {
            width: calc(100% - 110px);
            margin-left: 110px !important;
            min-height: 32px;
            line-height: 32px;
          }

          :deep(.drawing-item .el-input),
          :deep(.drawing-item .el-select),
          :deep(.drawing-item .el-input-number),
          :deep(.drawing-item .el-date-editor),
          :deep(.drawing-item textarea) {
            width: 100% !important;
          }
          .components-body {
            padding: 0;
            margin: 0;
            font-size: 0;
          }
          .sortable-ghost {
            position: relative;
            display: block;
            overflow: hidden;
            opacity: 0.8;
            color: #fff;
            background: #fff;
          }
          .sortable-chosen {
            border: 2px dashed #486ff2;
          }
          .components-item.sortable-ghost {
            width: 100%;
            height: 60px;
            background-color: rgba(72, 111, 242, 0.2);
            border: 2px dashed #486ff2;
            &::before {
              height: 60px;
              content: '组件放置区域';
              color: #fff;
              display: flex;
              align-items: center;
              justify-content: center;
              z-index: 2;
            }
          }
          :deep(.el-col),
          :deep(.el-form-item),
          :deep(.el-form-item__content) {
            width: 100%;
            max-width: 100%;
          }

          :deep(.drawing-item .el-form-item) {
            margin-bottom: 10px;
            border-radius: 4px;
            background: #fff;
            border: 1px solid #ebeef5;
            padding: 10px 12px;
          }

          :deep(.drawing-item .el-form-item__label) {
            width: 110px !important;
            color: #606266;
            line-height: 32px;
          }

          :deep(.drawing-item .el-form-item__content) {
            margin-left: 110px !important;
          }

          .el-form-item {
            margin-bottom: 12px;
          }
        }
        .empty-info {
          position: absolute;
          top: 46%;
          left: 0;
          right: 0;
          text-align: center;
          font-size: 18px;
          color: #486ff2;
          opacity: 0.6;
          letter-spacing: 4px;
        }
      }
    }
  }

  .right-board {
    width: 350px;
    height: calc(100vh - 50px);
    overflow: auto;
    transition: all 0.3s ease-in-out;
    background: #fff;
    border-left: 1px solid #ebeef5;
  }
  .right-board-collapse {
    width: 0;
    overflow: hidden;
  }
  .components-body {
    padding: 8px 10px;
    background: $selectedColor;
    font-size: 13px;
    cursor: move;
    border: 1px dashed $selectedColor;
    border-radius: 3px;
    display: flex;
    align-items: center;
    .label {
      margin-left: 10px;
    }
    .svg-icon {
      color: #777;
      font-size: 15px;
    }
    &:hover {
      border: 1px dashed #486ff2;
      color: #486ff2;
      .svg-icon {
        color: #486ff2;
      }
    }
  }
}
</style>
