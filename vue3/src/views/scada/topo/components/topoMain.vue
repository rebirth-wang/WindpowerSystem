<template>
  <!-- 组态主界面 -->
  <div class="topo-main-wrap">
    <div class="content-wrap" ref="rulerToolBox">
      <ruler-tool :parent="true" :is-scale-revise="true" ref="rulerToolRef" :is-hot-key="false">
        <div
          tabindex="0"
          id="surface-edit-layer"
          class="topo-layer"
          :class="{ 'topo-layer-view-selected': selectedIsLayer }"
          :style="layerStyle"
          @click="onLayerClick"
          @mouseup="onLayerMouseup($event)"
          @mousemove="onLayerMousemove($event)"
          @mousedown="onLayerMousedown($event)"
          @keyup.delete="removeItem()"
          @dragover.prevent
          @drop="onDrop"
          @contextmenu.prevent="onContextmenu"
          @keydown.ctrl.67.stop="copyItem"
          @keydown.ctrl.86.stop="pasteItem"
          @keydown.ctrl.90.stop="undo"
          @keydown.ctrl.89.stop="redo"
          @wheel="handelLayerWheel"
        >
          <template v-for="(component, index) in configData[selected][pageIndex].components" :key="index">
            <div
              tabindex="0"
              class="topo-layer-view"
              :class="{
                'topo-layer-view-selected': selectedComponentMap[component.identifier] == undefined ? false : true,
              }"
              v-if="component.style.visible == undefined ? true : component.style.visible"
              @click.stop="clickComponent(index, component, $event)"
              @mousedown.stop="handleMousedown(component, $event, index)"
              @keyup.delete="removeItem()"
              @keydown.up.exact.prevent="moveItems('up')"
              @keydown.right.exact.prevent="moveItems('right')"
              @keydown.down.exact.prevent="moveItems('down')"
              @keydown.left.exact.prevent="moveItems('left')"
              @keydown.ctrl.67.stop="copyItem"
              @keydown.ctrl.86.stop="pasteItem"
              @keydown.ctrl.90.stop="undo"
              @keydown.ctrl.89.stop="redo"
              :style="{
                left: component.style.position.x + 'px',
                top: component.style.position.y + 'px',
                width: component.style.position.w + 'px',
                height: component.style.position.h + 'px',
                backgroundColor:
                  component.type == 'flow-bar' || component.type == 'flow-bar-dynamic'
                    ? 'transparent'
                    : component.style.backColor,
                zIndex: getEditorZIndex(component),
                transform: component.style.transformType,
                color: component.style.foreColor,
                opacity: component.style.opacity,
                'border-radius': component.style.borderRadius + 'px',
                'box-shadow': '0 0 ' + component.style.boxShadowWidth + 'px 0 ' + component.style.boxShadowColor,
              }"
            >
              <component
                :is="parseView(component)"
                :detail="component"
                :editMode="true"
                :selected="selectedComponentMap[component.identifier] ? true : false"
                :ref="
                  (el) => {
                    if (el) compRefs[index] = el;
                  }
                "
              />
              <div
                class="resize-left-top"
                @mousedown.stop="resizeMousedown(component, $event, index, 'resize-lt')"
                v-show="selectedComponentMap[component.identifier]"
              ></div>
              <div
                class="resize-left-center"
                @mousedown.stop="resizeMousedown(component, $event, index, 'resize-lc')"
                v-show="selectedComponentMap[component.identifier]"
              ></div>
              <div
                class="resize-left-bottom"
                @mousedown.stop="resizeMousedown(component, $event, index, 'resize-lb')"
                v-show="selectedComponentMap[component.identifier]"
              ></div>
              <div
                class="resize-right-top"
                @mousedown.stop="resizeMousedown(component, $event, index, 'resize-rt')"
                v-show="selectedComponentMap[component.identifier]"
              ></div>
              <div
                class="resize-right-center"
                @mousedown.stop="resizeMousedown(component, $event, index, 'resize-rc')"
                v-show="selectedComponentMap[component.identifier]"
              ></div>
              <div
                class="resize-right-bottom"
                @mousedown.stop="resizeMousedown(component, $event, index, 'resize-rb')"
                v-show="selectedComponentMap[component.identifier]"
              ></div>
              <div
                class="resize-center-top"
                @mousedown.stop="resizeMousedown(component, $event, index, 'resize-ct')"
                v-show="selectedComponentMap[component.identifier]"
              ></div>
              <div
                class="resize-center-bottom"
                @mousedown.stop="resizeMousedown(component, $event, index, 'resize-cb')"
                v-show="selectedComponentMap[component.identifier]"
              ></div>
              <!-- 添加旋转手柄 -->
              <RotationHandle
                v-show="selectedComponentMap[component.identifier]"
                :angle="Number(component.style.transform ?? 0)"
                @rotate-start="onRotateStart(component, $event)"
                @rotating="onRotating(component, $event)"
                @rotate-end="onRotateEnd(component, $event)"
              />
            </div>
          </template>
          <!-- 点开空白选中多个组件 -->
          <div
            class="topo-frame-selection"
            :style="{
              width: frameSelectionDiv.width + 'px',
              height: frameSelectionDiv.height + 'px',
              top: frameSelectionDiv.top + 'px',
              left: frameSelectionDiv.left + 'px',
            }"
          ></div>
        </div>
      </ruler-tool>
    </div>
    <div class="footer-wrap">
      <div class="right-wrap" @click="handleBottomTabLeft">
        <el-icon><ArrowLeft /></el-icon>
      </div>
      <div class="right-wrap" @click="handleBottomTabRight">
        <el-icon><ArrowRight /></el-icon>
      </div>
      <div class="right-wrap" @click="handleBottomTabAdd">
        <el-icon><Plus /></el-icon>
      </div>
      <div class="left-wrap">
        <div class="tab" id="tab">
          <div
            class="tab-item"
            v-for="(item, index) in configData[selected]"
            :key="index"
            @click="handleBottomTabClick(Number(index))"
          >
            <el-dropdown @command="handleBottomTabCommand">
              <!-- 触发区域：默认插槽，不用写 #default -->
              <div class="tab-text" :class="pageIndex === Number(index) ? 'tab_active' : ''">
                <span>{{ item.name }}</span>
              </div>

              <!-- 下拉菜单：正确插槽 #dropdown -->
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item :command="{ type: 'rename', index: Number(index) }">
                    {{ $t('scada.topoMain.320129-28') }}
                  </el-dropdown-item>
                  <el-dropdown-item :command="{ type: 'delete', index: Number(index) }">
                    {{ $t('del') }}
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </div>
      </div>
    </div>
    <el-dialog
      :title="uploadImport.title"
      v-model="uploadImport.open"
      width="400px"
      append-to-body
      v-loading="importLoading"
      :element-loading-text="$t('scada.topoMain.320129-0')"
      element-loading-background="rgba(0, 0, 0, 0.8)"
    >
      <el-upload
        ref="uploadImportRef"
        :limit="1"
        accept=".json"
        :headers="uploadImport.headers"
        :action="uploadImport.url"
        :disabled="uploadImport.isUploading"
        :on-progress="handleFileUploadProgress"
        :on-success="handleFileSuccess"
        :auto-upload="false"
        drag
      >
        <el-icon class="el-icon--upload"><Upload /></el-icon>
        <div class="el-upload__text">
          {{ $t('dragFileTips') }}
          <em>{{ $t('clickFileTips') }}</em>
        </div>
        <template #tip>
          <div style="color: red; margin-top: 10px">{{ $t('scada.topoMain.320129-1') }}</div>
        </template>
      </el-upload>
      <template #footer>
        <el-button type="primary" @click="submitFileForm">{{ $t('confirm') }}</el-button>
        <el-button @click="uploadImport.open = false">{{ $t('cancel') }}</el-button>
      </template>
    </el-dialog>
    <el-dialog
      :title="$t('scada.topoMain.320129-28')"
      v-model="isRenameDialog"
      :close-on-press-escape="false"
      :show-close="false"
      width="400px"
    >
      <el-input v-model="renameValue.newName" :placeholder="$t('scada.topoMain.320129-3')" clearable></el-input>
      <template #footer>
        <el-button @click="cancelTabRename">{{ $t('cancel') }}</el-button>
        <el-button type="primary" @click="handleTabRename">{{ $t('confirm') }}</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, watch, onMounted, onBeforeUnmount, nextTick, getCurrentInstance, h } from 'vue';
import type { Component } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import FileSaver from 'file-saver';
import html2canvas from 'html2canvas';

import uid from '@/utils/uid';
import { deepCopy } from '@/utils/index';
import { parseViewName } from '@/utils/topo/topoUtil';
import { checkByRectCollisionDetection } from '@/utils/topo/index';
import { getToken } from '@/utils/auth';
import { getByGuid, saveDetailData } from '@/api/scada/topo';
import { listCenter, updateCenter } from '@/api/scada/center';
import { useTopoEditorStore } from '@/stores/modules/topoEditor';
import { useAppStore } from '@/stores/modules/app';
import {
  ArrowLeft,
  ArrowRight,
  ArrowUp,
  ArrowDown,
  Plus,
  Minus,
  CircleClose,
  CopyDocument,
  Document,
  Delete,
  Top,
  Bottom,
  Refresh,
  DArrowLeft,
  DArrowRight,
  Edit,
  Operation,
  Sort,
  Money,
  Connection,
  Link,
  Lock,
  Unlock,
  Picture,
  View,
  Star,
  FullScreen,
  Upload,
} from '@element-plus/icons-vue';
import { ElIcon, ElMessage } from 'element-plus';

function ctxMenuIcon(comp: Component) {
  return h(ElIcon, { size: 16 }, { default: () => h(comp) });
}

import RulerTool from './rulerTool.vue';
import RotationHandle from './RotationHandle.vue';
import {
  ViewPanel,
  ViewText,
  ViewVarCalc,
  ViewNumPanel,
  ViewImageSwitch,
  ViewBtn,
  ViewFlowBarDynamic,
  ViewVideo,
  ViewVideoMp4,
  ViewVideoPlay,
  ViewInlineVideo,
  ViewTimer,
  ViewMap,
  ViewWeather,
  ViewWarn,
  ViewOrder,
  View3DModel,
  ViewVR,
  ViewTimeTable,
  ViewFlowBar,
  ViewHistory,
  ViewSelect,
  ViewTriangle,
  ViewRect,
  ViewCircular,
  ViewLine,
  ViewLineSegment,
  ViewBizierCurve,
  ViewLineWave,
  ViewChartWater,
  ViewChart,
  ViewChartPie,
  ViewChartGauge,
  ViewChartTemp,
  ViewChartMap,
  ViewChartWrapper,
  ViewImage,
  ViewUsrTable,
  ViewRealData,
  ViewComponent,
  ViewKnobSwitch,
  ViewLuckDraw,
  ViewSvgImage,
  ViewSvgStatic,
  ViewThreeJs,
  ViewReliefBall,
} from './topoBase';
import {
  applyComponentRotation,
  getRouteQueryString,
  getScadaRouteType,
  normalizeTopoIndex,
  reorderComponentsByStackEdge,
  toggleComponentMirror,
} from '@/utils/topo/topoUtil';

const { proxy } = getCurrentInstance() as any;
const route = useRoute();
const router = useRouter();
const store = useTopoEditorStore();
const appStore = useAppStore();

// store 状态
const selectedComponents = computed(() => store.selectedComponents);
const selectedComponentMap = computed(() => store.selectedComponentMap);
const configData = computed(() => store.topoData);
const selectedIsLayer = computed(() => store.selectedIsLayer);
const copySrcItems = computed(() => store.copySrcItems);
const undoStack = computed(() => store.undoStack);
const redoStack = computed(() => store.redoStack);
const selected = computed(() => store.selected);
const pageIndex = computed(() => store.pageIndex);

// computed
const layerStyle = computed(() => {
  const scale = zoom.value / 100;
  const styles = [`transform:scale(${scale})`];
  if (configData.value[selected.value][pageIndex.value].layer.backColor) {
    styles.push(`background-color: ${configData.value[selected.value][pageIndex.value].layer.backColor}`);
  }
  if (configData.value[selected.value][pageIndex.value].layer.backgroundImage) {
    styles.push(
      `background-image: url("${baseApi + configData.value[selected.value][pageIndex.value].layer.backgroundImage}")`
    );
  }
  if (configData.value[selected.value][pageIndex.value].layer.width > 0) {
    styles.push(`width: ${configData.value[selected.value][pageIndex.value].layer.width}px`);
  }
  if (configData.value[selected.value][pageIndex.value].layer.height > 0) {
    styles.push(`height: ${configData.value[selected.value][pageIndex.value].layer.height}px`);
  }
  styles.push('overflow:hidden');
  styles.push('top:0;left:0;right:0;margin:0 auto');
  return styles.join(';');
});
const canUndo = computed(() => undoStack.value.length > 0);
const canRedo = computed(() => redoStack.value.length > 0);

// data
const baseApi = import.meta.env.VITE_APP_BASE_API;
const rulerToolRef = ref<any>(null);
const uploadImportRef = ref<any>(null);
const compRefs = ref<any[]>([]);
const selectComponent = ref<any>(null);
const selectIndx = ref(0);
const newestConfigData = ref({});
const deviceZtRow = ref<any>({});
const ztList = ref<any[]>([]);
const frameSelectionDiv = reactive({
  startX: 0,
  startY: 0,
  startPageX: 0,
  startPageY: 0,
  width: 0,
  height: 0,
  top: 0,
  left: 0,
});
const moveItem = reactive({ startX: 0, startY: 0 });
const resizeItem = reactive({ startPx: 0, startPy: 0, x: 0, y: 0, w: 0, h: 0 });
const curControl = ref<any>({});
const curIndex = ref(0);
const flag = ref('');
const zoom = ref(100);
const guid = getRouteQueryString(route.query, 'guid');
const importLoading = ref(false);
const uploadImport = reactive({
  title: '',
  open: false,
  url: '',
  headers: { Authorization: 'Bearer ' + getToken() },
  isUploading: false,
});
const renameValue = reactive({ index: 0, edit: false, newName: '', oldName: '' });
const isRenameDialog = ref(false);
const tabIndex = ref('0');
const rotateStartAngle = ref(0);
const initialRotateStates = ref<any>({});
const operateFlag = ref(0);
const isMultiple = ref(false);
const lastAlignCommand = ref('');

// store 方法
const {
  setSelectedComponent,
  addSelectedComponent,
  setLayerSelected,
  setCopySrcItems,
  execute,
  undo,
  redo,
  setPageIndex,
  clearSelectedComponent,
  removeSelectedComponent,
  clearUndoRedoStack,
  loadDefaultTopoData,
} = store;

// watch
watch(
  () => appStore.sidebar.opened,
  () => {
    setTimeout(() => {
      initRuler();
    }, 500);
  }
);
watch(
  () => store.selected,
  () => {
    tabIndex.value = pageIndex.value.toLocaleString();
  }
);
watch(
  undoStack,
  () => {
    emit('revokeFlagClick', undoStack.value.length === 0);
  },
  { deep: true }
);
watch(
  redoStack,
  () => {
    emit('recoveryFlagClick', redoStack.value.length === 0);
  },
  { deep: true }
);

// emits
const emit = defineEmits([
  'revokeFlagClick',
  'recoveryFlagClick',
  'menuClick',
  'lockStatusChange',
  'selectComponentChange',
  'componentStyleChange',
  'onLayerWheel',
]);

onMounted(() => {
  document.addEventListener('keydown', handleKeyDown, false);
  initZtTab();
  getZtCenter();
});
onBeforeUnmount(() => {
  document.removeEventListener('keydown', handleKeyDown, false);
});

// 初始化卡尺
function initRuler() {
  nextTick(() => {
    rulerToolRef.value?.init();
  });
}
// 调整视图大小
function handleZoom(value: number) {
  zoom.value = value;
}
// 导入组态json
function handleImport() {
  uploadImport.title = proxy.$t('scada.topoMain.320129-4');
  uploadImport.open = true;
  uploadImport.url = `${baseApi}/scada/center/importJson?guid=${guid}`;
}
// 文件上传中处理
function handleFileUploadProgress() {
  uploadImport.isUploading = true;
}
// 文件上传成功处理
function handleFileSuccess(res: any) {
  uploadImport.open = false;
  uploadImport.isUploading = false;
  uploadImportRef.value?.clearFiles();
  proxy.$modal.alert(res.msg, proxy.$t('scada.topoMain.320129-5'), { dangerouslyUseHTMLString: true });
  importLoading.value = false;
  getZtDetails();
}
// 提交上传文件
function submitFileForm() {
  uploadImportRef.value?.submit();
  importLoading.value = true;
}
// 导出组态json
function handleDownLoad() {
  let jsonDatas = { ...deviceZtRow.value };
  const compJson = JSON.stringify(configData.value);
  jsonDatas = { ...jsonDatas, scadaData: compJson };
  const data = JSON.stringify(jsonDatas);
  const blob = new Blob([data], { type: '' });
  FileSaver.saveAs(blob, document.title + '.json');
}
// 撤销
function handleRevoke() {
  undo();
  emit('revokeFlagClick', !canUndo.value);
  emit('recoveryFlagClick', false);
}
// 恢复
function handleRecovery() {
  redo();
  emit('recoveryFlagClick', !canRedo.value);
  emit('revokeFlagClick', false);
}
// 右击菜单
function onContextmenu(event: MouseEvent) {
  let isDisabled = false;
  if (selectedComponents.value.length > 0) {
    if (selectedComponentMap.value && selectedComponentMap.value[selectedComponents.value as any]?.type === 'VR')
      return;
  } else {
    isDisabled = true;
  }
  proxy.$contextmenu({
    items: [
      { label: proxy.$t('scada.topo.editor.023345-41'), icon: ctxMenuIcon(CircleClose), onClick: () => {} },
      {
        label: proxy.$t('scada.topo.editor.023345-0'),
        icon: ctxMenuIcon(CopyDocument),
        disabled: isDisabled,
        onClick: () => {
          copyItem();
        },
      },
      {
        label: proxy.$t('scada.topo.editor.023345-44'),
        icon: ctxMenuIcon(Document),
        disabled: !(copySrcItems.value && copySrcItems.value.length > 0),
        onClick: () => {
          pasteItem();
        },
      },
      {
        label: proxy.$t('del'),
        icon: ctxMenuIcon(Delete),
        disabled: isDisabled,
        onClick: () => {
          removeItem();
        },
      },
      {
        label: proxy.$t('scada.topo.editor.023345-1'),
        icon: ctxMenuIcon(Top),
        disabled: isDisabled,
        onClick: () => {
          emit('menuClick', proxy.$t('scada.topo.editor.023345-1'));
        },
      },
      {
        label: proxy.$t('scada.topo.editor.023345-2'),
        icon: ctxMenuIcon(Bottom),
        disabled: isDisabled,
        onClick: () => {
          emit('menuClick', proxy.$t('scada.topo.editor.023345-2'));
        },
      },
      {
        label: proxy.$t('scada.topo.editor.023345-3'),
        icon: ctxMenuIcon(Refresh),
        minWidth: 0,
        disabled: isDisabled,
        children: [
          {
            label: proxy.$t('scada.topo.editor.023345-4'),
            icon: ctxMenuIcon(Plus),
            onClick: () => {
              emit('menuClick', proxy.$t('scada.topo.editor.023345-4'));
            },
          },
          {
            label: proxy.$t('scada.topo.editor.023345-5'),
            icon: ctxMenuIcon(Minus),
            onClick: () => {
              emit('menuClick', proxy.$t('scada.topo.editor.023345-5'));
            },
          },
          {
            label: proxy.$t('scada.topo.editor.023345-6'),
            icon: ctxMenuIcon(DArrowLeft),
            onClick: () => {
              emit('menuClick', proxy.$t('scada.topo.editor.023345-6'));
            },
          },
          {
            label: proxy.$t('scada.topo.editor.023345-7'),
            icon: ctxMenuIcon(DArrowRight),
            onClick: () => {
              emit('menuClick', proxy.$t('scada.topo.editor.023345-7'));
            },
          },
          {
            label: proxy.$t('scada.topo.editor.023345-8'),
            icon: ctxMenuIcon(Edit),
            onClick: () => {
              emit('menuClick', proxy.$t('scada.topo.editor.023345-8'));
            },
          },
        ],
      },
      {
        label: proxy.$t('scada.topo.editor.023345-9'),
        icon: ctxMenuIcon(Operation),
        minWidth: 0,
        disabled: isDisabled,
        children: [
          { label: proxy.$t('scada.topo.editor.023345-10'), icon: ctxMenuIcon(ArrowLeft), command: 'left_align' },
          { label: proxy.$t('scada.topo.editor.023345-11'), icon: ctxMenuIcon(ArrowRight), command: 'right_align' },
          { label: proxy.$t('scada.topo.editor.023345-12'), icon: ctxMenuIcon(ArrowUp), command: 'top_align' },
          { label: proxy.$t('scada.topo.editor.023345-13'), icon: ctxMenuIcon(ArrowDown), command: 'bottom_align' },
          {
            label: proxy.$t('scada.topo.editor.023345-14'),
            icon: ctxMenuIcon(DArrowLeft),
            command: 'horizontal_space',
          },
          { label: proxy.$t('scada.topo.editor.023345-15'), icon: ctxMenuIcon(DArrowRight), command: 'vertical_space' },
          { label: proxy.$t('scada.topo.editor.023345-38'), icon: ctxMenuIcon(Sort), command: 'center_horizontal' },
          { label: proxy.$t('scada.topo.editor.023345-39'), icon: ctxMenuIcon(Sort), command: 'center_vertical' },
        ],
      },
      {
        label: proxy.$t('scada.topo.editor.023345-16'),
        icon: ctxMenuIcon(Money),
        minWidth: 0,
        disabled: isDisabled,
        children: [
          {
            label: proxy.$t('scada.topo.editor.023345-16'),
            icon: ctxMenuIcon(Connection),
            onClick: () => {
              makeUpClick(proxy.$t('scada.topo.editor.023345-16'));
            },
          },
          {
            label: proxy.$t('scada.topo.editor.023345-17'),
            icon: ctxMenuIcon(Link),
            onClick: () => {
              makeUpClick(proxy.$t('scada.topo.editor.023345-17'));
            },
          },
        ],
      },
      {
        label: proxy.$t('scada.topo.editor.023345-19'),
        icon: ctxMenuIcon(Lock),
        minWidth: 0,
        disabled: isDisabled,
        children: [
          {
            label: proxy.$t('scada.topo.editor.023345-19'),
            icon: ctxMenuIcon(Lock),
            onClick: () => {
              handleLock(proxy.$t('scada.topo.editor.023345-19'));
            },
          },
          {
            label: proxy.$t('scada.topo.editor.023345-18'),
            icon: ctxMenuIcon(Unlock),
            onClick: () => {
              handleLock(proxy.$t('scada.topo.editor.023345-18'));
            },
          },
        ],
      },
      {
        label: proxy.$t('scada.topo.editor.023345-20'),
        divided: true,
        icon: ctxMenuIcon(Picture),
        onClick: () => {
          emit('menuClick', proxy.$t('scada.topo.editor.023345-20'));
        },
      },
      {
        label: proxy.$t('preview'),
        divided: false,
        icon: ctxMenuIcon(View),
        onClick: () => {
          emit('menuClick', proxy.$t('preview'));
        },
      },
      {
        label: proxy.$t('save'),
        divided: false,
        icon: ctxMenuIcon(Star),
        onClick: () => {
          emit('menuClick', proxy.$t('save'));
        },
      },
      {
        label: proxy.$t('scada.topoMain.320129-7'),
        divided: false,
        icon: ctxMenuIcon(Refresh),
        onClick: () => {
          router.go(0);
        },
      },
    ],
    x: event.clientX,
    y: event.clientY,
    customClass: 'custom-class',
    zIndex: 9999,
    minWidth: 230,
  });
  return true;
}
function onTabContextmenu(event: MouseEvent) {
  proxy.$contextmenu({
    items: [
      {
        label: proxy.$t('scada.topoMain.320129-6'),
        icon: ctxMenuIcon(FullScreen),
        onClick: () => {
          screenPreview();
        },
      },
      {
        label: proxy.$t('scada.topoMain.320129-7'),
        divided: true,
        icon: ctxMenuIcon(Refresh),
        onClick: () => {
          router.go(0);
        },
      },
    ],
    x: event.clientX,
    y: event.clientY,
    customClass: 'custom-class',
    zIndex: 9999,
    minWidth: 230,
  });
  return true;
}
// 鼠标按下事件
function handleMousedown(component: any, event: MouseEvent, index: number | string) {
  if (event.ctrlKey) return;
  const idx = normalizeTopoIndex(index);
  flag.value = 'move';
  curControl.value = component;
  clickItem(component, idx);
  moveItem.startX = event.pageX;
  moveItem.startY = event.pageY;
  for (var key in selectedComponentMap.value) {
    var comp = selectedComponentMap.value[key];
    comp.style.temp = {};
    comp.style.temp.position = {};
    comp.style.temp.position.x = comp.style.position.x;
    comp.style.temp.position.y = comp.style.position.y;
  }
}
function resizeMousedown(component: any, event: MouseEvent, index: number | string, f: string) {
  const idx = normalizeTopoIndex(index);
  flag.value = f;
  curControl.value = component;
  curIndex.value = idx;
  clickItem(component, idx);
  resizeItem.startPx = event.clientX;
  resizeItem.startPy = event.clientY;
  resizeItem.x = curControl.value.style.position.x;
  resizeItem.y = curControl.value.style.position.y;
  resizeItem.w = curControl.value.style.position.w;
  resizeItem.h = curControl.value.style.position.h;
}
function onLayerMousemove(event: MouseEvent) {
  if (event.which != 1) {
    flag.value = '';
    return;
  }
  if (flag.value == '') return;
  const scale = zoom.value / 100;
  if (flag.value.startsWith('resize')) {
    var dx = (event.clientX - resizeItem.startPx) / scale,
      dy = (event.clientY - resizeItem.startPy) / scale;
    switch (flag.value) {
      case 'resize-lt':
        curControl.value.style.position.x = resizeItem.x + dx;
        curControl.value.style.position.y = resizeItem.y + dy;
        dx = -dx;
        dy = -dy;
        break;
      case 'resize-lc':
        curControl.value.style.position.x = resizeItem.x + dx;
        dy = 0;
        dx = -dx;
        break;
      case 'resize-lb':
        curControl.value.style.position.x = resizeItem.x + dx;
        dx = -dx;
        break;
      case 'resize-rt':
        curControl.value.style.position.y = resizeItem.y + dy;
        dy = -dy;
        break;
      case 'resize-rc':
        dy = 0;
        break;
      case 'resize-rb':
        break;
      case 'resize-ct':
        curControl.value.style.position.y = resizeItem.y + dy;
        dx = 0;
        dy = -dy;
        break;
      case 'resize-cb':
        dx = 0;
        break;
    }
    if (resizeItem.w + dx > 20) curControl.value.style.position.w = resizeItem.w + dx;
    if (resizeItem.h + dy > 20) curControl.value.style.position.h = resizeItem.h + dy;
    nextTick(() => {
      compRefs.value[curIndex.value]?.onResize?.();
    });
  } else if (flag.value == 'move') {
    var dx2 = (event.pageX - moveItem.startX) / scale,
      dy2 = (event.pageY - moveItem.startY) / scale;
    for (var key in selectedComponentMap.value) {
      var comp = selectedComponentMap.value[key];
      if (!comp.isLock) {
        comp.style.position.x = comp.style.temp.position.x + dx2;
        comp.style.position.y = comp.style.temp.position.y + dy2;
      }
    }
  } else if (flag.value == 'frame_selection') {
    onFrameSelection(event);
  }
}
function onLayerMouseup(event: MouseEvent) {
  if (flag.value.startsWith('resize')) {
    compRefs.value[curIndex.value]?.onResize?.();
    if (curControl.value?.style?.position) {
      curControl.value.style.position.x = Math.round(curControl.value.style.position.x);
      curControl.value.style.position.y = Math.round(curControl.value.style.position.y);
      curControl.value.style.position.w = Math.round(curControl.value.style.position.w);
      curControl.value.style.position.h = Math.round(curControl.value.style.position.h);
    }
  } else if (flag.value == 'frame_selection') {
    onFrameSelection(event);
    frameSelectionDiv.width = 0;
    frameSelectionDiv.height = 0;
    frameSelectionDiv.top = 0;
    frameSelectionDiv.left = 0;
  } else if (flag.value == 'move') {
    const scale = zoom.value / 100;
    var dx = (event.pageX - moveItem.startX) / scale;
    var dy = (event.pageY - moveItem.startY) / scale;
    for (var key in selectedComponentMap.value) {
      var comp = selectedComponentMap.value[key];
      comp.style.position.x = comp.style.position.x - dx;
      comp.style.position.y = comp.style.position.y - dy;
    }
    const finalDx = Math.round(dx),
      finalDy = Math.round(dy);
    execute({ op: 'move', dx: finalDx, dy: finalDy, items: selectedComponentMap.value });
    for (var k in selectedComponentMap.value) {
      var c = selectedComponentMap.value[k];
      if (c?.style?.position) {
        c.style.position.x = Math.round(c.style.position.x);
        c.style.position.y = Math.round(c.style.position.y);
      }
    }
  }
  flag.value = '';
}
function onLayerMousedown(event: MouseEvent) {
  flag.value = 'frame_selection';
  const scale = zoom.value / 100;
  const layerEl = document.getElementById('surface-edit-layer');
  const rect = layerEl ? layerEl.getBoundingClientRect() : { left: 0, top: 0 };
  frameSelectionDiv.startX = (event.clientX - rect.left) / scale;
  frameSelectionDiv.startY = (event.clientY - rect.top) / scale;
  frameSelectionDiv.startPageX = event.clientX;
  frameSelectionDiv.startPageY = event.clientY;
  isMultiple.value = true;
}
function onLayerClick() {
  if (isMultiple.value == false) {
    clearSelectedComponent();
    selectComponent.value = null;
    setLayerSelected(true);
  } else {
    isMultiple.value = false;
  }
}
function onFrameSelection(event: MouseEvent) {
  const scale = zoom.value / 100;
  var dx = (event.clientX - frameSelectionDiv.startPageX) / scale;
  var dy = (event.clientY - frameSelectionDiv.startPageY) / scale;
  frameSelectionDiv.width = Math.round(Math.abs(dx));
  frameSelectionDiv.height = Math.round(Math.abs(dy));
  if (dx > 0 && dy > 0) {
    frameSelectionDiv.top = Math.round(frameSelectionDiv.startY);
    frameSelectionDiv.left = Math.round(frameSelectionDiv.startX);
  } else if (dx > 0 && dy < 0) {
    frameSelectionDiv.top = Math.round(frameSelectionDiv.startY + dy);
    frameSelectionDiv.left = Math.round(frameSelectionDiv.startX);
  } else if (dx < 0 && dy > 0) {
    frameSelectionDiv.top = Math.round(frameSelectionDiv.startY);
    frameSelectionDiv.left = Math.round(frameSelectionDiv.startX + dx);
  } else if (dx < 0 && dy < 0) {
    frameSelectionDiv.top = Math.round(frameSelectionDiv.startY + dy);
    frameSelectionDiv.left = Math.round(frameSelectionDiv.startX + dx);
  }
  var rect = {
    x: frameSelectionDiv.left,
    y: frameSelectionDiv.top,
    width: frameSelectionDiv.width,
    height: frameSelectionDiv.height,
  };
  var components = configData.value[selected.value][pageIndex.value].components;
  components.forEach((component: any) => {
    var itemRect = {
      x: component.style.position.x,
      y: component.style.position.y,
      width: component.style.position.w,
      height: component.style.position.h,
    };
    if (checkByRectCollisionDetection(rect, itemRect)) {
      addSelectedComponent(component);
    } else {
      removeSelectedComponent(component);
    }
  });
  if (selectedComponents.value.length > 0) {
    setLayerSelected(false);
  } else {
    setLayerSelected(true);
  }
}
function onDrop(event: DragEvent) {
  event.preventDefault();
  const transfer = event.dataTransfer as DataTransfer;
  const infoJson = transfer.getData('my-info') || transfer.getData('text/plain');
  let component = JSON.parse(infoJson);
  if (component.type == 'weather') {
    var components = configData.value[selected.value][pageIndex.value].components;
    let isExist = false;
    components.forEach((c: any) => {
      if (c.type == 'weather') isExist = true;
    });
    if (isExist) {
      ElMessage({ message: proxy.$t('scada.topoMain.320129-8'), type: 'warning' });
      return;
    }
  }
  if (checkAddComponent(component) == false) return;
  const scale = zoom.value / 100;
  if ((event.target as HTMLElement).id == 'surface-edit-layer') {
    component.style.position.x = Math.round(event.offsetX / scale);
    component.style.position.y = Math.round(event.offsetY / scale);
  } else {
    var layer = event.currentTarget as HTMLElement;
    var position = layer.getBoundingClientRect();
    var x = event.clientX - position.left;
    var y = event.clientY - position.top;
    component.style.position.x = Math.round(x / scale);
    component.style.position.y = Math.round(y / scale);
  }
  execute({ op: 'add', component: component });
  clickItem(component, configData.value[selected.value][pageIndex.value].components.length - 1);
}
function moveItems(direction: string) {
  var dx = 0,
    dy = 0;
  if (direction == 'up') dy = -5;
  else if (direction == 'right') dx = 5;
  else if (direction == 'down') dy = 5;
  else if (direction == 'left') dx = -5;
  execute({ op: 'move', dx, dy, items: selectedComponentMap.value });
}
function checkAddComponent(info: any) {
  if (info == null) {
    proxy.$q?.notify({ type: 'negative', position: 'bottom-right', message: 'This component not surpport.' });
    return false;
  }
  return true;
}
const viewMap: Record<string, any> = {
  'view-panel': ViewPanel,
  'view-text': ViewText,
  'view-var-calc': ViewVarCalc,
  'view-num-panel': ViewNumPanel,
  'view-image-switch': ViewImageSwitch,
  'view-btn': ViewBtn,
  'view-flow-bar-dynamic': ViewFlowBarDynamic,
  'view-video': ViewVideo,
  'view-video-mp4': ViewVideoMp4,
  'view-video-play': ViewVideoPlay,
  'view-inline-video': ViewInlineVideo,
  'view-timer': ViewTimer,
  'view-map': ViewMap,
  'view-weather': ViewWeather,
  'view-warn': ViewWarn,
  'view-order': ViewOrder,
  'view-3d-model': View3DModel,
  'view-vr': ViewVR,
  'view-time-table': ViewTimeTable,
  'view-flow-bar': ViewFlowBar,
  'view-history': ViewHistory,
  'view-select': ViewSelect,
  'view-triangle': ViewTriangle,
  'view-rect': ViewRect,
  'view-circular': ViewCircular,
  'view-line': ViewLine,
  'view-line-segment': ViewLineSegment,
  'view-bizier-curve': ViewBizierCurve,
  'view-line-wave': ViewLineWave,
  'view-chart-water': ViewChartWater,
  'view-chart': ViewChart,
  'view-chart-pie': ViewChartPie,
  'view-chart-gauge': ViewChartGauge,
  'view-chart-temp': ViewChartTemp,
  'view-chart-map': ViewChartMap,
  'view-chart-wrapper': ViewChartWrapper,
  'view-image': ViewImage,
  'view-usr-table': ViewUsrTable,
  'view-real-data': ViewRealData,
  'view-component': ViewComponent,
  'view-knob-switch': ViewKnobSwitch,
  'view-luck-draw': ViewLuckDraw,
  'view-svg-image': ViewSvgImage,
  'view-svg-static': ViewSvgStatic,
  'view-three-js': ViewThreeJs,
  'view-relief-ball': ViewReliefBall,
};

function parseView(component: any) {
  return viewMap[parseViewName(component)] || ViewText;
}

function getEditorZIndex(component: any) {
  const zIndex = Number(component?.style?.zIndex);
  if (component?.type === '3D-model' && zIndex < 0) {
    return 0;
  }
  return Number.isFinite(zIndex) ? zIndex : component?.style?.zIndex;
}
function clickItem(component: any, index: number | string) {
  const idx = normalizeTopoIndex(index);
  operateFlag.value = operateFlag.value + 1;
  console.log('单击组件:', deepCopy(component));
  selectComponent.value = component;
  selectIndx.value = idx;
  setLayerSelected(false);
  setSelectedComponent(component);
  if (component.identifiers) {
    component.identifiers.forEach((element: string) => {
      configData.value[selected.value][pageIndex.value].components.forEach((ele: any) => {
        if (ele.identifier == element) {
          selectedComponentMap.value[element] = ele;
        }
      });
    });
  }
  emit('lockStatusChange', component.isLock);
  emit('selectComponentChange', { index: idx, component });
}
function copyItem() {
  var items: any[] = [];
  for (var key in selectedComponentMap.value) {
    var item = deepCopy(selectedComponentMap.value[key]);
    item._sourcePage = { configType: selected.value, pageIndex: pageIndex.value };
    items.push(item);
  }
  setCopySrcItems(items);
}
function pasteItem() {
  if (copySrcItems.value && copySrcItems.value.length > 0) {
    const sourcePage = copySrcItems.value[0]._sourcePage;
    const isCrossPage =
      sourcePage && (sourcePage.configType !== selected.value || sourcePage.pageIndex !== pageIndex.value);
    if (isCrossPage) {
      execute({ op: 'cross-page-copy-add', items: copySrcItems.value });
    } else {
      execute({ op: 'copy-add', items: copySrcItems.value });
    }
  }
}
function onRotateStart(component: any, startAngle: number) {
  rotateStartAngle.value = startAngle;
  initialRotateStates.value = {};
  for (let key in selectedComponentMap.value) {
    const comp = selectedComponentMap.value[key];
    initialRotateStates.value[key] = {
      ...comp,
      style: {
        ...comp.style,
        transform: comp.style.transform || 0,
        transformType: `rotate(${comp.style.transform || 0}deg)`,
      },
    };
  }
}
function onRotating(component: any, angle: number) {
  const transformType = `rotate(${angle}deg)`;
  for (var key in selectedComponentMap.value) {
    var comp = selectedComponentMap.value[key];
    comp.style.transform = angle;
    comp.style.transformType = transformType;
  }
  emit('componentStyleChange', component);
}
function onRotateEnd(component: any, { startAngle, endAngle }: { startAngle: number; endAngle: number }) {
  if (Math.abs(endAngle - rotateStartAngle.value) > 0.1) {
    execute({ op: 'rotate', newAngle: endAngle, initialStates: initialRotateStates.value });
  }
}
function removeItem() {
  execute({ op: 'del' });
  clearSelectedComponent();
  setLayerSelected(true);
}
function showMessage(type: string, msg: string) {
  ElMessage({ message: msg, type: type as any, duration: 1500 });
}
function handleVisibilityCommand(command: string) {
  toggleComponentVisibility(command === 'show');
}
function toggleComponentVisibility(visible: boolean) {
  if (selectedComponents.value.length > 0) {
    selectedComponents.value.forEach((id: string) => {
      const component = selectedComponentMap.value[id];
      if (component.identifiers && component.identifiers.length > 1) {
        component.identifiers.forEach((identifier: string) => {
          if (configData.value[selected.value][pageIndex.value].components) {
            const comp = configData.value[selected.value][pageIndex.value].components.find(
              (c: any) => c.identifier === identifier
            );
            if (comp) comp.style.visible = visible;
          }
        });
      } else {
        if (!component.style) component.style = {};
        if (!component.style.visible) component.style.visible = true;
        component.style.visible = visible;
      }
    });
  }
}
function handleLock(command: string) {
  if (command == proxy.$t('scada.topo.editor.023345-19')) {
    for (var key in selectedComponentMap.value) selectedComponentMap.value[key].isLock = true;
    emit('lockStatusChange', true);
  } else {
    for (var key in selectedComponentMap.value) selectedComponentMap.value[key].isLock = false;
    emit('lockStatusChange', false);
  }
}
function alignClick(command: string) {
  const LEFT_ALIGN = 'left_align',
    RIGHT_ALIGN = 'right_align',
    TOP_ALIGN = 'top_align',
    BOTTOM_ALIGN = 'bottom_align';
  const HORIZONTAL_SPACE = 'horizontal_space',
    VERTICAL_SPACE = 'vertical_space',
    CENTER_HORIZONTAL = 'center_horizontal',
    CENTER_VERTICAL = 'center_vertical';
  if ([LEFT_ALIGN, RIGHT_ALIGN, TOP_ALIGN, BOTTOM_ALIGN].includes(command)) lastAlignCommand.value = command;
  const scMap = selectedComponentMap.value;
  const scList = selectedComponents.value as string[];
  if (command === RIGHT_ALIGN) {
    let alignX = Math.max(...scList.map((id) => scMap[id].style.position.x + scMap[id].style.position.w));
    scList.forEach((id) => {
      scMap[id].style.position.x = alignX - scMap[id].style.position.w;
    });
  } else if (command === LEFT_ALIGN) {
    let alignX = Math.min(...scList.map((id) => scMap[id].style.position.x));
    scList.forEach((id) => {
      scMap[id].style.position.x = alignX;
    });
  } else if (command === TOP_ALIGN) {
    let alignY = Math.min(...scList.map((id) => scMap[id].style.position.y));
    scList.forEach((id) => {
      scMap[id].style.position.y = alignY;
    });
  } else if (command === BOTTOM_ALIGN) {
    let alignY = Math.max(...scList.map((id) => scMap[id].style.position.y + scMap[id].style.position.h));
    scList.forEach((id) => {
      scMap[id].style.position.y = alignY - scMap[id].style.position.h;
    });
  } else if (command === HORIZONTAL_SPACE) {
    if (scList.length < 2) return;
    const FIXED_SPACING = 20;
    let comps = scList.map((id) => scMap[id]);
    comps.sort((a, b) => a.style.position.x - b.style.position.x);
    let baseY =
      lastAlignCommand.value === TOP_ALIGN
        ? Math.min(...comps.map((c) => c.style.position.y))
        : lastAlignCommand.value === BOTTOM_ALIGN
          ? Math.max(...comps.map((c) => c.style.position.y + c.style.position.h))
          : comps[0].style.position.y;
    let currentX = comps[0].style.position.x;
    comps.forEach((c) => {
      c.style.position.x = currentX;
      c.style.position.y = baseY;
      currentX += c.style.position.w + FIXED_SPACING;
    });
  } else if (command === VERTICAL_SPACE) {
    if (scList.length < 2) return;
    const FIXED_SPACING = 20;
    let comps = scList.map((id) => scMap[id]);
    comps.sort((a, b) => a.style.position.y - b.style.position.y);
    let baseX =
      lastAlignCommand.value === LEFT_ALIGN
        ? Math.min(...comps.map((c) => c.style.position.x))
        : lastAlignCommand.value === RIGHT_ALIGN
          ? Math.max(...comps.map((c) => c.style.position.x + c.style.position.w))
          : comps[0].style.position.x;
    let currentY = comps[0].style.position.y;
    comps.forEach((c) => {
      c.style.position.y = currentY;
      c.style.position.x = baseX;
      currentY += c.style.position.h + FIXED_SPACING;
    });
  } else if (command === CENTER_HORIZONTAL) {
    let centerY =
      (Math.min(...scList.map((id) => scMap[id].style.position.y)) +
        Math.max(...scList.map((id) => scMap[id].style.position.y + scMap[id].style.position.h))) /
      2;
    scList.forEach((id) => {
      let c = scMap[id];
      c.style.position.y = centerY - c.style.position.h / 2;
    });
  } else if (command === CENTER_VERTICAL) {
    let centerX =
      (Math.min(...scList.map((id) => scMap[id].style.position.x)) +
        Math.max(...scList.map((id) => scMap[id].style.position.x + scMap[id].style.position.w))) /
      2;
    scList.forEach((id) => {
      let c = scMap[id];
      c.style.position.x = centerX - c.style.position.w / 2;
    });
  }
}

/** 置顶/置底：与右侧图层面板 draggable 一致——重排 components 并同步 style.zIndex */
function stackOrder(mode: '置顶' | '置底') {
  const map = selectedComponentMap.value as Record<string, any>;
  if (!map || Object.keys(map).length === 0) return;
  const arr = configData.value[selected.value][pageIndex.value].components as any[];
  if (!arr?.length) return;
  reorderComponentsByStackEdge(arr, map, mode, configData.value as any);
  const ids = selectedComponents.value;
  if (!ids.length) return;
  const newIdx = arr.findIndex((c: any) => c.identifier === ids[0]);
  if (newIdx >= 0) {
    selectIndx.value = newIdx;
    emit('selectComponentChange', { index: newIdx });
  }
}

function transform(command: string, value?: string) {
  const cw = proxy.$t('scada.topo.editor.023345-27');
  const ccw = proxy.$t('scada.topo.editor.023345-28');
  const mirrorX = proxy.$t('scada.topo.editor.023345-29');
  const mirrorY = proxy.$t('scada.topo.editor.023345-30');
  const custom = proxy.$t('scada.topo.editor.023345-35');

  const targets = Object.keys(selectedComponentMap.value);
  if (!targets.length) return;

  targets.forEach((id) => {
    const comp = selectedComponentMap.value[id];
    if (!comp?.style) return;
    const current = Number(comp.style.transform || 0);
    let next = current;

    if (command === cw) next = current + 90;
    else if (command === ccw) next = current - 90;
    else if (command === custom) next = Number(value || 0);
    else if (command === mirrorX) {
      toggleComponentMirror(comp.style, 'x');
      return;
    } else if (command === mirrorY) {
      toggleComponentMirror(comp.style, 'y');
      return;
    }

    applyComponentRotation(comp.style, next);
  });
}

function makeUpClick(command: string) {
  if (command == proxy.$t('scada.topo.editor.023345-16')) {
    for (var key in selectedComponentMap.value) selectedComponentMap.value[key].identifiers = selectedComponents.value;
  } else {
    for (var key in selectedComponentMap.value) selectedComponentMap.value[key].identifiers = [];
  }
  if (selectedComponents.value.length == 0) {
    showMessage('warning', proxy.$t('scada.topoMain.320129-9'));
  } else {
    clearSelectedComponent();
    clickItem(selectComponent.value, selectIndx.value);
  }
}
function getImgJsonItem(url: string) {
  return {
    type: 'image',
    componentShow: ['动画', '单击', '组件颜色', '滤镜渲染', '状态开关', '参数绑定'],
    action: ['click'],
    hdClassName: '',
    identifier: uid(),
    name: `image${configData.value[selected.value][pageIndex.value].components.length}`,
    img: { type: 'service', icon: url },
    dataBind: {
      dataType: 1,
      modelName: '',
      modelValue: '',
      identifier: '',
      simValue: '',
      simInterval: 1,
      filter: 0,
      funValue: '// 数据处理逻辑编辑\r\n// 以返回値作为绑定结果\r\nconst result = value;\r\nreturn result;',
      sceneModelId: '',
      sceneModelDeviceId: '',
      variableType: '',
      productId: '',
      serialNumber: '',
      shutImageUrl: '',
      openImageUrl: '',
      warnImageUrl: '',
      stateList: [],
      filterNewData: '',
      httpSetting: { type: 'get', time: 30, unit: 's', params: {}, body: { type: 1, value: '' }, headers: {} },
      httpFilter: '',
      httpSettingId: '',
    },
    dataEvent: {
      djAction: false,
      action: '1',
      sceneModelId: '',
      variableType: '',
      productId: '',
      serialNumber: '',
      slaveId: '',
      identifier: '',
      modelName: '',
      modelValue: '',
      redirectUrl: '',
      ztPage: '',
      djType: 1,
      modelTip: '',
      writeType: 1,
      funValue:
        "/*\r\nreturn 的値是脚本的最终输出。\r\n\r\n1. 返回节点数据，例如固定値：\r\nreturn 4\r\n\r\n2. 返回可变数据：\r\napp.$prompt('请输入', '温度', {\r\n    confirmButtonText: '确认',\r\n    cancelButtonText: '取消',\r\n    closeOnClickModal: false,\r\n    closeOnPressEscape: false,\r\n    inputValidator: (value) => {\r\n        if (!value) {\r\n            return '变量値不能为空';\r\n        }\r\n    },\r\n}).then(({ value }) => {\r\n    issord(value);\r\n});\r\n以上两种返回结果展示效果不同：\r\n- app 是应用实例，通过它可以对整个应用进行操作\r\n- issord 是下发函数，下发对应变量値\r\n- 对于固定値：发送値不显示\r\n- 对于输入値：根据代码弹框，并输入値发送\r\n*/\r\nreturn;",
      externalLink: '',
      scadaGuid: '',
      openModel: 1,
      windowWidth: '',
      windowHeight: '',
      switchControl: 1,
      scadaIndex: '',
      passwordEnabled: false,
      passwordType: 'user',
    },
    dataAction: {
      xyAction: false,
      xzAction: false,
      ssAction: false,
      hdAction: false,
      maAction: false,
      sceneModelId: '',
      sceneModelDeviceId: '',
      variableType: '',
      productId: '',
      serialNumber: '',
      identifier: '',
      modelName: '',
      paramJudge: '',
      paramJudgeData: '',
      paramJudgeDatarangeMin: '',
      paramJudgeDatarangeMax: '',
      rotationSpeed: '中',
      duration: '',
      translateList: [],
    },
    style: {
      position: { x: 200 + randomNumBoth(0, 30), y: 200 + randomNumBoth(0, 30), w: 100, h: 100 },
      backColor: 'rgba(255,255,255,0)',
      foreColor: 'rgba(255,255,255,0)',
      zIndex: 1,
      transform: 0,
      url: url,
      transformType: 'rotate(0deg)',
      isFilter: false,
      visible: true,
    },
  };
}
function randomNumBoth(Min: number, Max: number) {
  var Range = Max - Min;
  var Rand = Math.random();
  return Min + Math.round(Rand * Range);
}
function addImageData(url: string) {
  let data = getImgJsonItem(url);
  configData.value[selected.value][pageIndex.value].components.push(data);
}
function clickComponent(index: number | string, component: any, event: MouseEvent) {
  console.log('点击组件');
  if (event.ctrlKey == true) {
    setLayerSelected(false);
    if (selectedComponentMap.value[component.identifier] == undefined) {
      addSelectedComponent(component);
    } else {
      removeSelectedComponent(component);
    }
  }
}
function getZtDetails() {
  const params = { guid: guid };
  getByGuid(params).then((res: any) => {
    deviceZtRow.value = res.data;
    document.title = deviceZtRow.value.pageName;
    if (deviceZtRow.value.scadaData) {
      let cfgData = JSON.parse(deviceZtRow.value.scadaData);
      if (cfgData?.pcConfig && cfgData?.mdConfig) {
        loadDefaultTopoData(cfgData);
      }
      if (!cfgData?.pcConfig) {
        const data = {
          pcConfig: [{ ...cfgData }],
          mdConfig: [
            {
              name: '--',
              layer: {
                backColor: '',
                backgroundImage: '',
                widthHeightRatio: '',
                width: 375,
                height: 667,
                dragZoom: false,
              },
              components: [],
              httpPublicSetting: [],
            },
          ],
          pcChecked: true,
          mdChecked: false,
        };
        loadDefaultTopoData(data);
      }
    } else {
      let cfgData = {
        pcConfig: [
          {
            name: proxy.$t('scada.topoMain.320129-32', [1]),
            layer: {
              backColor: '',
              backgroundImage: '',
              widthHeightRatio: '',
              width: 1600,
              height: 900,
              dragZoom: false,
            },
            components: [],
            httpPublicSetting: [],
          },
        ],
        mdConfig: [
          {
            name: proxy.$t('scada.topoMain.320129-32', [1]),
            layer: {
              backColor: '',
              backgroundImage: '',
              widthHeightRatio: '',
              width: 375,
              height: 667,
              dragZoom: false,
            },
            components: [],
            httpPublicSetting: [],
          },
        ],
        pcChecked: true,
        mdChecked: true,
      };
      loadDefaultTopoData(cfgData);
    }
  });
}
function initZtTab() {
  getZtDetails();
  clearSelectedComponent();
  setLayerSelected(true);
  emit('recoveryFlagClick', true);
  emit('revokeFlagClick', true);
}
function getZtCenter() {
  return new Promise((resolve, reject) => {
    listCenter().then((res: any) => {
      if (res.code === 200) {
        ztList.value = res.rows;
        resolve(true);
      } else {
        reject(false);
      }
    });
  });
}
async function handleBottomTabClick(tab: number) {
  setPageIndex(tab);
}
function handleBottomTabCommand(command: { type: 'rename' | 'delete'; index: number }) {
  const { type, index } = command;
  if (type === 'rename') {
    isRenameDialog.value = true;
    Object.assign(renameValue, {
      index,
      edit: true,
      newName: configData.value[selected.value][index].name,
      oldName: configData.value[selected.value][index].name,
    });
  }
  if (type === 'delete') {
    if (configData.value[selected.value].length === 1) {
      ElMessage({ message: proxy.$t('scada.topoMain.320129-29'), type: 'warning' });
      return;
    }
    proxy
      .$confirm(proxy.$t('scada.topoMain.320129-19'), proxy.$t('scada.topoMain.320129-17'), {
        confirmButtonText: proxy.$t('del'),
        cancelButtonText: proxy.$t('cancel'),
        type: 'warning',
      })
      .then(() => {
        proxy.$modal.loading(proxy.$t('scada.topoMain.320129-26'));
        configData.value[selected.value].splice(index, 1);
        setPageIndex(0);
        tabIndex.value = pageIndex.value.toLocaleString();
        proxy.$modal.closeLoading();
      });
  }
}
function handleTabRename() {
  if (
    configData.value[selected.value].some(
      (item: any, index: number) => item.name === renameValue.newName && index !== renameValue.index
    )
  ) {
    ElMessage({ message: proxy.$t('scada.topoMain.320129-30'), type: 'warning' });
    return;
  }
  configData.value[selected.value][renameValue.index].name = renameValue.newName;
  if (!renameValue.edit) setPageIndex(configData.value[selected.value].length - 1);
  isRenameDialog.value = false;
}
function cancelTabRename() {
  if (!renameValue.edit) {
    configData.value[selected.value].splice(renameValue.index, 1);
    isRenameDialog.value = false;
    return;
  }
  if (
    configData.value[selected.value].some(
      (item: any, index: number) => item.name === renameValue.oldName && index !== renameValue.index
    )
  ) {
    ElMessage({ message: proxy.$t('scada.topoMain.320129-31'), type: 'warning' });
    return;
  }
  isRenameDialog.value = false;
}
function handleBottomTabLeft() {
  const scroll = document.getElementById('tab') as HTMLElement;
  scroll.scrollLeft -= 200;
}
function handleBottomTabRight() {
  const scroll = document.getElementById('tab') as HTMLElement;
  scroll.scrollLeft += 200;
}
function handleBottomTabAdd() {
  if (selected.value === 'mdConfig') {
    configData.value.mdConfig.push({
      name: proxy.$t('scada.topoMain.320129-32', [configData.value.mdConfig.length + 1]),
      layer: { backColor: '', backgroundImage: '', widthHeightRatio: '', width: 375, height: 667, dragZoom: false },
      components: [],
    });
  }
  if (selected.value === 'pcConfig') {
    configData.value.pcConfig.push({
      name: proxy.$t('scada.topoMain.320129-32', [configData.value.pcConfig.length + 1]),
      layer: { backColor: '', backgroundImage: '', widthHeightRatio: '', width: 1600, height: 900, dragZoom: false },
      components: [],
    });
  }
  isRenameDialog.value = true;
  renameValue.index = configData.value[selected.value].length - 1;
  renameValue.edit = false;
  renameValue.newName = configData.value[selected.value][renameValue.index].name;
  renameValue.oldName = configData.value[selected.value][renameValue.index].name;
}
function handlePrintScadaData() {
  proxy.$modal.loading(proxy.$t('scada.topoMain.320129-22'));
  saveZtDatas().then((flag: any) => {
    if (flag) {
      proxy.$modal.closeLoading();
      clearUndoRedoStack();
      showMessage('success', proxy.$t('saveSuccess'));
    }
  });
}
function saveZtDatas() {
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      let canvasBox = document.querySelector('#surface-edit-layer') as HTMLElement;
      canvasBox.classList.add('none-topo-layer');
      const options = {
        backgroundColor: null,
        useCORS: true,
        width: configData.value[selected.value][pageIndex.value].layer.width,
        height: configData.value[selected.value][pageIndex.value].layer.height,
        windowWidth: document.body.scrollWidth,
        windowHeight: document.body.scrollHeight,
        x: window.pageXOffset,
        y: window.pageYOffset,
      };
      html2canvas(canvasBox, options).then((canvas: HTMLCanvasElement) => {
        const base64 = canvas.toDataURL('image/jpeg', 0.7);
        var json = JSON.stringify(configData.value);
        canvasBox.classList.remove('none-topo-layer');
        const params = { guid: guid, base64: base64, scadaData: json };
        saveDetailData(params).then((res: any) => {
          if (res.code === 200) {
            const id = getRouteQueryString(route.query, 'id');
            const resolution =
              configData.value[selected.value][pageIndex.value].layer.width +
              'x' +
              configData.value[selected.value][pageIndex.value].layer.height;
            const { tenantId, createBy } = deviceZtRow.value;
            const params2 = { id: id, pageResolution: resolution, tenantId: tenantId, createBy: createBy };
            updateCenter(params2).then((res2: any) => {
              if (res2.code === 200) resolve(true);
              else reject(false);
            });
          } else reject(false);
        });
      });
    }, 500);
  });
}
function screenPreview() {
  const type = getScadaRouteType(route.query);
  const sceneModelId = getRouteQueryString(route.query, 'sceneModelId');
  let query: any = { guid: guid, type: type };
  if (type === 2) query = { ...query, sceneModelId: sceneModelId };
  const routeUrl = router.resolve({ path: '/scada/topo/fullscreen', query });
  window.open(routeUrl.href, '_blank');
}
function handelLayerWheel(event: WheelEvent) {
  if (event.ctrlKey && event.metaKey) {
    emit('onLayerWheel', event);
    event.preventDefault();
  }
}
function handleKeyDown(event: KeyboardEvent) {
  if (event.key === 's' && event.ctrlKey) {
    proxy.$modal.loading(proxy.$t('scada.topoMain.320129-22'));
    saveZtDatas().then((flag: any) => {
      if (flag) {
        proxy.$modal.closeLoading();
        clearUndoRedoStack();
        showMessage('success', proxy.$t('saveSuccess'));
      }
    });
    event.preventDefault();
  }
}

defineExpose({
  handleZoom,
  handleImport,
  handleRevoke,
  handleRecovery,
  handleDownLoad,
  handlePrintScadaData,
  screenPreview,
  alignClick,
  handleVisibilityCommand,
  handleLock,
  addImageData,
  initZtTab,
  handleBottomTabClick,
  handleBottomTabCommand,
  handleBottomTabAdd,
  handleBottomTabLeft,
  handleBottomTabRight,
  copyItem,
  pasteItem,
  removeItem,
  makeUpClick,
  transform,
  clickItem,
  initRuler,
  stackOrder,
});
</script>

<style lang="scss" scoped>
.topo-main-wrap {
  position: relative;
  width: 100%;
  height: 100%;
  background-color: #ffffff;
  overflow-x: hidden;
  overflow-y: hidden;
  .content-wrap {
    height: calc(100% - 41px);
    width: 100%;
    .topo-layer {
      width: 100%;
      height: 100%;
      position: absolute;
      transform-origin: left top;
      overflow: auto;
      background-color: #f2f3f4;
      background-clip: padding-box;
      background-origin: padding-box;
      background-repeat: no-repeat;
      background-size: 100% 100%;
      .topo-frame-selection {
        background-color: #8787e7;
        opacity: 0.3;
        border: #0000ff solid 1px;
        position: absolute;
        z-index: 999;
      }
      .topo-layer-view-selected-line {
        outline: 1px solid #0cf;
      }
      .topo-layer-view {
        position: absolute;
        height: 100px;
        width: 100px;
        background-color: #999;
        cursor: move;
        .resize-left-top,
        .resize-left-center,
        .resize-left-bottom,
        .resize-right-top,
        .resize-right-center,
        .resize-right-bottom,
        .resize-center-top,
        .resize-center-bottom {
          z-index: 3;
        }
        .resize-left-top {
          position: absolute;
          height: 10px;
          width: 10px;
          border-radius: 50%;
          background-color: #486ff2e6;
          border: 1px solid #486ff2;
          left: -5px;
          top: -5px;
          cursor: nwse-resize;
        }
        .resize-left-center {
          position: absolute;
          height: 10px;
          width: 10px;
          border-radius: 50%;
          background-color: #486ff2e6;
          border: 1px solid #486ff2;
          left: -5px;
          top: 50%;
          margin-top: -5px;
          cursor: ew-resize;
        }
        .resize-left-bottom {
          position: absolute;
          height: 10px;
          width: 10px;
          border-radius: 50%;
          background-color: #486ff2e6;
          border: 1px solid #486ff2;
          left: -5px;
          bottom: -5px;
          cursor: nesw-resize;
        }
        .resize-right-top {
          position: absolute;
          height: 10px;
          width: 10px;
          border-radius: 50%;
          background-color: #486ff2e6;
          border: 1px solid #486ff2;
          right: -5px;
          top: -5px;
          cursor: nesw-resize;
        }
        .resize-right-center {
          position: absolute;
          height: 10px;
          width: 10px;
          border-radius: 50%;
          background-color: #486ff2e6;
          border: 1px solid #486ff2;
          right: -5px;
          top: 50%;
          margin-top: -5px;
          cursor: ew-resize;
        }
        .resize-right-bottom {
          position: absolute;
          height: 10px;
          width: 10px;
          border-radius: 50%;
          background-color: #486ff2e6;
          border: 1px solid #486ff2;
          right: -5px;
          bottom: -5px;
          cursor: nwse-resize;
        }
        .resize-center-top {
          position: absolute;
          height: 10px;
          width: 10px;
          border-radius: 50%;
          background-color: #486ff2e6;
          border: 1px solid #486ff2;
          top: -5px;
          left: 50%;
          margin-left: -5px;
          cursor: ns-resize;
        }
        .resize-center-bottom {
          position: absolute;
          height: 10px;
          width: 10px;
          border-radius: 50%;
          background-color: #486ff2e6;
          border: 1px solid #486ff2;
          bottom: -5px;
          left: 50%;
          margin-left: -5px;
          cursor: ns-resize;
        }
      }
      .topo-layer.topo-layer-view-selected {
        outline: 1.5px solid #a2b4f3;
      }
      .topo-layer-view.topo-layer-view-selected {
        border: none;
      }
      .topo-layer-view.topo-layer-view-selected::after {
        position: absolute;
        inset: -1.5px;
        z-index: 2;
        content: '';
        border: 1.5px solid #a2b4f3;
        border-radius: inherit;
        pointer-events: none;
      }
      .topo-layer-view.topo-layer-view-selected:hover::after {
        border-style: dashed;
      }
    }
    .topo-layer::before,
    .topo-layer::after {
      position: fixed;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      content: '';
      background-repeat: repeat;
      pointer-events: none;
    }
    .topo-layer::before {
      background-image:
        linear-gradient(to right, black 1px, transparent 1px, transparent 10px),
        linear-gradient(to bottom, black 1px, transparent 1px, transparent 10px);
      background-size: 10px 10px;
      opacity: 0.05;
    }
    .topo-layer::after {
      background-image:
        linear-gradient(to right, black 1px, transparent 1px, transparent 50px),
        linear-gradient(to bottom, black 1px, transparent 1px, transparent 50px);
      background-size: 50px 50px;
      opacity: 0.1;
    }
    .none-topo-layer::before,
    .none-topo-layer::after {
      position: unset;
    }
  }
  .footer-wrap {
    width: 100%;
    position: absolute;
    height: 42px;
    border-top: 1px solid #dcdfe6;
    background-color: #ffffff;
    display: flex;
    flex-direction: row;
    align-items: center;
    .left-wrap {
      flex: 1;
      overflow: hidden;
      margin-left: 5px;
      display: flex;
      height: 100%;
      .tab {
        width: 100%;
        height: 100%;
        overflow-y: auto;
        display: flex;
        border-left: 1px solid #dfe4ed;
        .tab-item {
          width: 115px;
          height: 100%;
          display: flex;
          align-items: center;
          justify-content: center;
          border-right: 1px solid #dfe4ed;
          flex-shrink: 0;
          cursor: pointer;
          .tab-text {
            width: 115px;
            font-size: 11px;
            display: flex;
            align-items: center;
            justify-content: center;
            span {
              padding: 5px;
              white-space: nowrap;
              overflow: hidden;
              text-overflow: ellipsis;
            }
          }
        }
        .tab_active {
          color: #0000ff;
        }
      }
      :deep(.el-tabs__nav) {
        border: unset;
        border-left: 1px solid #dfe4ed;
        border-right: 1px solid #dfe4ed;
        border-radius: unset;
        .is-active {
          background-color: #fff;
        }
      }
      :deep(.is-focus) {
        box-shadow: unset;
        border-radius: unset;
      }
    }
    .right-wrap {
      height: 30px;
      width: 30px;
      line-height: 30px;
      text-align: center;
      cursor: pointer;
      font-size: 18px;
      color: #333;
      font-weight: 700;
    }
    .left-disabled,
    .right-disabled {
      color: #dcdfe6;
      cursor: not-allowed;
    }
  }
  ::-webkit-scrollbar-thumb {
    background-color: #e1e1e1;
  }
  ::-webkit-scrollbar-thumb:hover {
    background-color: #a5a2a2;
  }
  ::-webkit-scrollbar {
    width: 5px;
    height: 5px;
    position: absolute;
  }
  ::-webkit-scrollbar-track {
    background-color: #fff;
  }
}
</style>
