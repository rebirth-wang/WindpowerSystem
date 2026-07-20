<template>
  <div class="panel-wrap" v-if="flowVisible">
    <!--顶部工具菜单-->
    <el-row>
      <el-col :span="24">
        <div class="tooltar">
          <div class="header-left">
            <img style="margin-right: 10px" width="25px" height="25px" src="@/assets/images/drools.png" />
            <span style="color: #878787">{{ nodesDetail.elName }}</span>
            <div class="tips-text" :class="{ enable: nodesDetail.enable === 1, forbid: nodesDetail.enable === 2 }">
              {{
                nodesDetail.enable === 2 ? t('ruleengine.editor.index.807357-2') : t('ruleengine.editor.index.807357-3')
              }}
            </div>
          </div>
          <div class="header-center">
            <el-button type="primary" link @click="handleDeleteNode" :disabled="!activeElement.type">
              <div class="btn-wrap">
                <el-icon class="icon"><Delete /></el-icon>
                <div class="name">{{ $t('del') }}</div>
              </div>
            </el-button>
            <el-divider direction="vertical"></el-divider>
            <el-button type="primary" link @click="downloadData">
              <div class="btn-wrap">
                <el-icon class="icon"><Download /></el-icon>
                <div class="name">{{ $t('export') }}</div>
              </div>
            </el-button>
            <el-divider direction="vertical"></el-divider>
            <el-button type="primary" link @click="handleZoomAdd">
              <div class="btn-wrap">
                <el-icon class="icon"><Plus /></el-icon>
                <div class="name">{{ $t('zoomIn') }}</div>
              </div>
            </el-button>
            <el-button type="primary" link @click="handleZoomSub">
              <div class="btn-wrap">
                <el-icon class="icon"><Minus /></el-icon>
                <div class="name">{{ $t('zoomOut') }}</div>
              </div>
            </el-button>
          </div>
          <div class="header-right">
            <el-button type="primary" link @click="handleNodeSave">
              <div class="btn-wrap">
                <el-icon class="icon"><FolderChecked /></el-icon>
                <div class="name">{{ $t('save') }}</div>
              </div>
            </el-button>
            <el-button type="primary" link @click="handleNodeRelease">
              <div class="btn-wrap">
                <el-icon class="icon"><Position /></el-icon>
                <div class="name">{{ $t('release') }}</div>
              </div>
            </el-button>
            <el-button type="primary" link @click="handleViewLog">
              <div class="btn-wrap">
                <el-icon class="icon"><Calendar /></el-icon>
                <div class="name">{{ $t('ruleengine.editor.index.807357-17') }}</div>
              </div>
            </el-button>
            <el-button type="primary" link @click="handleDataInfo">
              <div class="btn-wrap">
                <el-icon class="icon"><Document /></el-icon>
                <div class="name">{{ $t('ruleengine.editor.index.807357-0') }}</div>
              </div>
            </el-button>
            <el-button type="primary" link @click="handleCase">
              <div class="btn-wrap">
                <el-icon class="icon"><Coin /></el-icon>
                <div class="name">{{ $t('ruleengine.editor.index.807357-1') }}</div>
              </div>
            </el-button>
            <el-button type="primary" link @click="handleOpenHelp">
              <div class="btn-wrap">
                <el-icon class="icon"><WarningFilled /></el-icon>
                <div class="name">{{ $t('help') }}</div>
              </div>
            </el-button>
          </div>
        </div>
      </el-col>
    </el-row>
    <div class="content">
      <div class="menu-wrap">
        <node-menu @addNode="handleAddNode" ref="nodeMenuRef"></node-menu>
      </div>
      <div class="container-wrap" ref="fContainerWrapRef" @click="handlePanel" v-flow-drag>
        <!-- style="transform: scale(1)" 为了避免线乱画 -->
        <div class="node-wrap" ref="fContainerRef" style="transform: scale(1)">
          <flow-node
            v-for="node in data.nodes"
            :key="node.id"
            :id="node.id"
            :node="node"
            :activeElement="activeElement"
            @changeNodeSite="handleChangeNodeSite"
            @clickNode="handleClickNode"
          ></flow-node>
          <!-- 给画布一个默认的宽度和高度 -->
          <div style="position: absolute; top: 2000px; left: 2000px">画布</div>
        </div>
      </div>
      <!-- 右侧表单 -->
      <div class="form-wrap" :class="{ 'aside-close': !formVisible }">
        <flow-node-form
          ref="nodeFormRef"
          :id="nodeFormId"
          :activeElement="activeElement"
          @delete="handleDeleteNode"
        ></flow-node-form>
      </div>
    </div>
    <el-dialog
      :title="title"
      v-model="openLog"
      width="800px"
      append-to-body
      :close-on-click-modal="false"
      :close-on-press-escape="false"
    >
      <div
        ref="logContainerRef"
        v-loading="logLoading"
        :element-loading-text="$t('script.349087-39')"
        element-loading-background="rgba(0, 0, 0, 0.8)"
        style="
          border: 1px solid #ccc;
          border-radius: 4px;
          height: 480px;
          background-color: #181818;
          color: #fff;
          padding: 10px;
          line-height: 20px;
          overflow: auto;
        "
      >
        <pre style="white-space: pre-wrap; word-wrap: break-word" v-if="resultMsg">{{ resultMsg }}</pre>
        <el-empty v-if="!resultMsg" :description="$t('ruleengine.editor.components.debugLog.807357-0')" />
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="cancelLog">{{ $t('script.349087-38') }}</el-button>
        </div>
      </template>
    </el-dialog>
    <!-- 流程数据详情 -->
    <flow-info v-if="flowInfoVisible" ref="flowInfoRef" :data="data"></flow-info>
    <flow-help v-if="flowHelpVisible" ref="flowHelpRef"></flow-help>
    <flow-case v-if="flowCaseVisible" ref="flowCaseRef" @cell-click="handlCaseCellClick"></flow-case>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, watch, nextTick, onMounted, onUnmounted, getCurrentInstance } from 'vue';
import { useRoute } from 'vue-router';
import { useI18n } from 'vue-i18n';
import { storeToRefs } from 'pinia';
import lodash, { cloneDeep } from 'lodash';
import {
  Delete,
  Download,
  Plus,
  Minus,
  FolderChecked,
  Position,
  Calendar,
  Document,
  Coin,
  WarningFilled,
} from '@element-plus/icons-vue';

import { useRuleEditorStore } from '@/stores/modules/ruleEditor';
import NodeMenu from './components/node-menu.vue';
import FlowNodeForm from './components/node-form.vue';
import FlowNode from './components/node.vue';
import FlowInfo from './components/info.vue';
import FlowHelp from './components/help.vue';
import FlowCase from './components/case.vue';
import { jsplumbSetting, jsplumbConnectOptions, jsplumbSourceOptions, jsplumbTargetOptions } from './js/mixins';
import { jsPlumb } from 'jsplumb';
import { parseViewName, getNodeId } from '@/utils/rule';

import { getDataSwitch } from './data/data_switch';
import { getDataIf } from './data/data_if';
import { getDataIfSingle } from './data/data_ifSingle';
import { getDataTimer } from './data/data_timer';
import { getDataCustom } from './data/data_custom';
import { getDataMultiple } from './data/data_multiple';

import { getRuleElDetail, updateRuleEl, publishRuleEl, getRuleElLog } from '@/api/rule/el.js';

const { proxy } = getCurrentInstance() as any;
const { t } = useI18n();
const route = useRoute();
const ruleEditorStore = useRuleEditorStore();
const { data, curNode, curLine } = storeToRefs(ruleEditorStore);

// 案例数据映射
const caseDataMap: Record<string, () => any> = {
  Switch: getDataSwitch,
  If: getDataIf,
  IfSingle: getDataIfSingle,
  Timer: getDataTimer,
  Custom: getDataCustom,
  Multiple: getDataMultiple,
};

// refs
const nodeMenuRef = ref();
const nodeFormRef = ref();
const fContainerWrapRef = ref();
const fContainerRef = ref();
const logContainerRef = ref();
const flowInfoRef = ref();
const flowHelpRef = ref();
const flowCaseRef = ref();

// data
let jsPlumbInstance: any = null;
const flowVisible = ref(true);
const flowInfoVisible = ref(false);
const flowHelpVisible = ref(false);
const flowCaseVisible = ref(false);
const formVisible = ref(false);
const loadEasyFlowFinish = ref(false);
const activeElement = reactive<any>({
  type: undefined,
  nodeId: undefined,
  sourceId: undefined,
  targetId: undefined,
});
const zoom = ref(1);
const nodeFormId = ref('');
const logLoading = ref(true);
const openLog = ref(false);
const title = ref('');
const resultMsg = ref('');
const logList = ref<any[]>([]);
const nodesDetail = ref<any>({});

// computed
const newCurLine = computed(() => JSON.stringify(curLine.value));

// watch curLine changes
let oldCurLineStr = '{}';
watch(
  newCurLine,
  (newVal) => {
    const oldVal = oldCurLineStr;
    oldCurLineStr = newVal;
    try {
      const newObj = JSON.parse(newVal);
      const oldObj = JSON.parse(oldVal);
      if (newObj.from === oldObj.from && newObj.to === oldObj.to) {
        if (newObj.label !== oldObj.label || newObj.value !== oldObj.value) {
          const { from, to, label } = newObj;
          const conn = jsPlumbInstance.getConnections({ source: from, target: to })[0];
          if (!label || label === '') {
            conn.removeClass('flowLabel');
            conn.addClass('emptyFlowLabel');
            conn.setLabel({ label: '' });
          } else {
            conn.removeClass('emptyFlowLabel');
            conn.addClass('flowLabel');
            // 首次设置标签时显式指定 cssClass，避免只渲染 aLabel 无边框
            conn.setLabel({ label, cssClass: 'aLabel flowLabel' });
          }
          ruleEditorStore.executeLine({ op: 'update', com: newObj });
        }
        if (newObj.value !== oldObj.value || newObj.type !== oldObj.type) {
          const { from, to, type, value } = newObj;
          const conn = jsPlumbInstance.getConnections({ source: from, target: to })[0];
          conn.setParameters({ type, value });
          ruleEditorStore.executeLine({ op: 'update', com: newObj });
        }
      }
    } catch (e) {
      // ignore parse errors on first watch
    }
  },
  { deep: true }
);

// 自定义指令
const vFlowDrag = {
  mounted(el: HTMLElement, binding: any) {
    if (!binding) return;
    el.onmousedown = (e: MouseEvent) => {
      if (e.button === 2) return;
      let disX = e.clientX;
      let disY = e.clientY;
      el.style.cursor = 'move';
      document.onmousemove = function (e: MouseEvent) {
        e.preventDefault();
        const left = e.clientX - disX;
        disX = e.clientX;
        el.scrollLeft += -left;
        const top = e.clientY - disY;
        disY = e.clientY;
        el.scrollTop += -top;
      };
      document.onmouseup = function () {
        el.style.cursor = 'auto';
        document.onmousemove = null;
        document.onmouseup = null;
      };
    };
  },
};

// 加载流程图
function dataReload(reloadData: any) {
  flowVisible.value = false;
  data.value.nodes = [];
  data.value.lines = [];
  nextTick(() => {
    reloadData = lodash.cloneDeep(reloadData);
    flowVisible.value = true;
    ruleEditorStore.setData(reloadData);
    nextTick(() => {
      jsPlumbInstance = jsPlumb.getInstance();
      nextTick(() => {
        jsPlumbInit();
      });
    });
  });
}

// 初始化连接线
function jsPlumbInit() {
  jsPlumbInstance.ready(() => {
    jsPlumbInstance.importDefaults(jsplumbSetting);
    jsPlumbInstance.setSuspendDrawing(false, true);
    loadEasyFlow();
    // 单点击了连接线
    jsPlumbInstance.bind('click', (conn: any, e: Event) => {
      activeElement.type = 'line';
      activeElement.sourceId = conn.sourceId;
      activeElement.targetId = conn.targetId;
      jsPlumbInstance.select().setPaintStyle({ stroke: '#E0E3E7', outlineStroke: 'transparent' });
      conn.setPaintStyle({ stroke: '#1879ff', outlineStroke: '#transparent' });
      e.stopPropagation();
      const params = conn.getParameters();
      const line = {
        from: conn.sourceId,
        to: conn.targetId,
        label: conn.getLabel(),
        ...params,
      };
      ruleEditorStore.setCurLine(line);
      nodeFormId.value = 'FlowLine';
      formVisible.value = true;
    });
    // 连线
    jsPlumbInstance.bind('connection', (evt: any) => {
      console.log('连线：');
      const from = evt.sourceId || evt.source.id;
      const to = evt.targetId || evt.target.id;
      if (loadEasyFlowFinish.value) {
        evt.connection.setParameters({ type: 'common', value: '' });
        ruleEditorStore.executeLine({
          op: 'add',
          com: { from, to, label: '', type: 'common', value: '' },
        });
      }
    });
    // 删除连线回调
    jsPlumbInstance.bind('connectionDetached', (evt: any) => {
      deleteLine(evt.sourceId, evt.targetId);
    });
    // 改变线的连接节点
    jsPlumbInstance.bind('connectionMoved', (evt: any) => {
      changeLine(evt.originalSourceId, evt.originalTargetId);
    });
    // 连线右击
    jsPlumbInstance.bind('contextmenu', (evt: any) => {
      console.log('contextmenu', evt);
    });
    // 连线
    jsPlumbInstance.bind('beforeDrop', (evt: any) => {
      const from = evt.sourceId;
      const to = evt.targetId;
      if (from === to) {
        proxy.$modal.msgError(t('ruleengine.editor.index.807357-4'));
        return false;
      }
      if (hasLine(from, to)) {
        proxy.$modal.msgError(t('ruleengine.editor.index.807357-5'));
        return false;
      }
      if (hashOppositeLine(from, to)) {
        proxy.$modal.msgError(t('ruleengine.editor.index.807357-6'));
        return false;
      }
      if (hasEndNodeLine(from, to)) {
        proxy.$modal.msgError(t('ruleengine.editor.index.807357-8'));
        return false;
      }
      if (hasStartNodeLine(from, to)) {
        proxy.$modal.msgError(t('ruleengine.editor.index.807357-9'));
        return false;
      }
      if (acceptMulLine(from, to)) {
        proxy.$modal.msgError(t('ruleengine.editor.index.807357-10'));
        return false;
      }
      if (acceptCommentLine(from, to)) {
        proxy.$modal.msgError(t('ruleengine.editor.index.807357-19'));
        return false;
      }
      proxy.$modal.msgSuccess(t('ruleengine.editor.index.807357-11'));
      return true;
    });
    // beforeDetach
    jsPlumbInstance.bind('beforeDetach', (evt: any) => {
      console.log('beforeDetach', evt);
    });
    jsPlumbInstance.setContainer(fContainerRef.value);
  });
}

// 是否具有该线
function hasLine(from: string, to: string) {
  for (let i = 0; i < data.value.lines.length; i++) {
    const line = data.value.lines[i];
    if (line.from === from && line.to === to) return true;
  }
  return false;
}

// 是否含有相反的线
function hashOppositeLine(from: string, to: string) {
  return hasLine(to, from);
}

// 结束节点连线
function hasEndNodeLine(from: string, _to: string) {
  return from.includes('end_');
}

// 开始节点连线
function hasStartNodeLine(_from: string, to: string) {
  return to.includes('start_');
}

// 不接受多条连线
function acceptMulLine(_from: string, to: string) {
  for (let i = 0; i < data.value.lines.length; i++) {
    const line = data.value.lines[i];
    if (line.to === to && to.includes('condition_')) return true;
  }
  return false;
}

// 不接受连线-注释
function acceptCommentLine(_from: string, to: string) {
  return to.includes('comment_');
}

// 加载流程图
function loadEasyFlow() {
  for (let i = 0; i < data.value.nodes.length; i++) {
    const node = data.value.nodes[i];
    jsPlumbInstance.makeSource(node.id, lodash.merge(jsplumbSourceOptions, {}));
    jsPlumbInstance.makeTarget(node.id, jsplumbTargetOptions);
    if (!node.viewOnly) {
      jsPlumbInstance.draggable(node.id, {
        containment: 'parent',
        filter: '.flow-node-drag',
        filterExclude: true,
        stop: function (el: any) {
          console.log('拖拽结束: ', el);
        },
      });
    }
  }
  for (let i = 0; i < data.value.lines.length; i++) {
    const line = data.value.lines[i];
    const connParam = {
      source: line.from,
      target: line.to,
      label: line.label ? line.label : '',
      parameters: {
        type: line.type ? line.type : 'common',
        value: line.value ? line.value : '',
      },
      connector: line.connector ? line.connector : '',
      anchors: line.anchors ? line.anchors : undefined,
      paintStyle: line.paintStyle ? line.paintStyle : undefined,
    };
    jsPlumbInstance.connect(connParam, jsplumbConnectOptions);
  }
  nextTick(() => {
    loadEasyFlowFinish.value = true;
  });
}

// 删除线
function deleteLine(from: string, to: string) {
  data.value.lines = data.value.lines.filter((line: any) => {
    return !(line.from === from && line.to === to);
  });
}

// 改变连线
function changeLine(oldFrom: string, oldTo: string) {
  deleteLine(oldFrom, oldTo);
}

// 流程数据信息
function handleDataInfo() {
  flowInfoVisible.value = true;
  nextTick(() => {
    flowInfoRef.value?.init();
  });
}

// 拖拽结束后添加新的节点
function handleAddNode(evt: any, nodeMenu: any, _mousePosition: any) {
  const screenX = evt.originalEvent.clientX;
  const screenY = evt.originalEvent.clientY;
  const fContainerWrap = fContainerWrapRef.value;
  const containerRect = fContainerWrap.getBoundingClientRect();
  let left = screenX;
  let top = screenY;
  if (
    left < containerRect.x ||
    left > containerRect.width + containerRect.x ||
    top < containerRect.y ||
    containerRect.y > containerRect.y + containerRect.height
  ) {
    proxy.$modal.msgError(t('ruleengine.editor.index.807357-12'));
    return;
  }
  left = left - containerRect.x + fContainerWrap.scrollLeft;
  top = top - containerRect.y + fContainerWrap.scrollTop;
  left -= 85;
  top -= 16;
  const uuid = getNodeId(nodeMenu.type);
  const nodeId = uuid;
  const origName = nodeMenu.name;
  let nodeName = origName;
  let index = 1;
  while (index < 10000) {
    let repeat = false;
    for (let i = 0; i < data.value.nodes.length; i++) {
      const node = data.value.nodes[i];
      if (node.name === nodeName) {
        nodeName = origName + index;
        repeat = true;
      }
    }
    if (repeat) {
      index++;
      continue;
    }
    break;
  }
  const node = {
    ...nodeMenu,
    id: nodeId,
    name: nodeName,
    left: left + 'px',
    top: top + 'px',
    state: 'success',
  };
  const isHavePlay = data.value.nodes.some((item: any) => item.type === 'start' && node.type === 'start');
  const isHavePause = data.value.nodes.some((item: any) => item.type === 'end' && node.type === 'end');
  if (isHavePlay) {
    proxy.$modal.msgWarning(t('ruleengine.editor.index.807357-13'));
    return;
  } else if (isHavePause) {
    proxy.$modal.msgWarning(t('ruleengine.editor.index.807357-14'));
    return;
  } else {
    ruleEditorStore.executeNode({ op: 'add', com: node });
  }
  nextTick(() => {
    jsPlumbInstance.makeSource(nodeId, jsplumbSourceOptions);
    jsPlumbInstance.makeTarget(nodeId, jsplumbTargetOptions);
    jsPlumbInstance.draggable(nodeId, {
      containment: 'parent',
      filter: '.flow-node-drag',
      filterExclude: true,
      stop: function (el: any) {
        console.log('拖拽结束: ', el);
      },
    });
  });
}

// 画布点击
function handlePanel() {
  formVisible.value = false;
}

// 点击节点
function handleClickNode(node: any) {
  const { id, type } = node || {};
  activeElement.type = 'node';
  activeElement.nodeId = id;
  jsPlumbInstance.select().setPaintStyle({ stroke: '#E0E3E7', outlineStroke: 'transparent' });
  if (type === 'start' || type === 'end') {
    formVisible.value = false;
    return;
  }
  getLineList(id);
  const conditionComNum = getConditionComNum(node);
  const isCom = conditionComNum > 0;
  activeElement.isCom = isCom;
  if (isCom && !node.data.cond) {
    node.data.cond = 1;
  }
  ruleEditorStore.setCurNode(node);
  nodeFormId.value = parseViewName(node);
  formVisible.value = true;
}

// 获取条件组件连过去的线
function getLineList(id: string) {
  const { lines } = data.value;
  const lin = cloneDeep(lines);
  const count = lin.filter((item: any) => item.to === id).length;
  activeElement.isShowExecute = count > 1;
}

// 改变节点的位置
function handleChangeNodeSite(siteData: any) {
  for (let i = 0; i < data.value.nodes.length; i++) {
    const node = data.value.nodes[i];
    if (node.id === siteData.nodeId) {
      node.left = siteData.left;
      node.top = siteData.top;
    }
  }
}

// 重置激活的元素
function resetActiveElement() {
  if (activeElement.type) {
    activeElement.type = undefined;
    activeElement.nodeId = undefined;
    activeElement.sourceId = undefined;
    activeElement.targetId = undefined;
  }
}

// 删除激活的元素
function handleDeleteNode() {
  if (activeElement.type === 'node') {
    deleteNode(activeElement.nodeId);
  } else if (activeElement.type === 'line') {
    proxy.$modal
      .confirm(t('ruleengine.list.index.807357-10'), t('tips'), {
        confirmButtonText: t('confirm'),
        cancelButtonText: t('cancel'),
        type: 'warning',
      })
      .then(() => {
        const conn = jsPlumbInstance.getConnections({
          source: activeElement.sourceId,
          target: activeElement.targetId,
        })[0];
        jsPlumbInstance.deleteConnection(conn);
        nextTick(() => {
          resetActiveElement();
        });
        formVisible.value = false;
      });
  } else {
    proxy.$modal.msgWarning(t('ruleengine.list.index.807357-11'));
  }
}

// 删除节点
function deleteNode(nodeId: string) {
  proxy.$modal
    .confirm(t('ruleengine.list.index.807357-12', [nodeId]), t('tips'), {
      confirmButtonText: t('confirm'),
      cancelButtonText: t('cancel'),
      type: 'warning',
      closeOnClickModal: false,
    })
    .then(() => {
      ruleEditorStore.executeNode({ op: 'delete', com: { nodeId } });
      nextTick(() => {
        jsPlumbInstance.removeAllEndpoints(nodeId);
        resetActiveElement();
      });
      formVisible.value = false;
    })
    .catch(() => {});
  return true;
}

// 下载数据
function downloadData() {
  proxy.$modal
    .confirm(t('ruleengine.list.index.807357-13'), t('tips'), {
      confirmButtonText: t('confirm'),
      cancelButtonText: t('cancel'),
      type: 'warning',
      closeOnClickModal: false,
    })
    .then(() => {
      const datastr = 'data:text/json;charset=utf-8,' + encodeURIComponent(JSON.stringify(data.value, null, '\t'));
      const downloadAnchorNode = document.createElement('a');
      downloadAnchorNode.setAttribute('href', datastr);
      downloadAnchorNode.setAttribute('download', 'data.json');
      downloadAnchorNode.click();
      downloadAnchorNode.remove();
      proxy.$modal.msgSuccess(t('ruleengine.list.index.807357-14'));
    })
    .catch(() => {});
}

// 放大
function handleZoomAdd() {
  if (zoom.value >= 1.5) return;
  zoom.value = zoom.value + 0.1;
  fContainerRef.value.style.transform = `scale(${zoom.value})`;
  jsPlumbInstance.setZoom(zoom.value);
}

// 缩小
function handleZoomSub() {
  if (zoom.value <= 0.2) return;
  zoom.value = zoom.value - 0.1;
  fContainerRef.value.style.transform = `scale(${zoom.value})`;
  jsPlumbInstance.setZoom(zoom.value);
}

// 键盘按钮点击事件
function handleKeyDown(event: KeyboardEvent) {
  if (event.key === 'Delete' || event.keyCode === 46) {
    event.preventDefault();
    handleDeleteNode();
  }
}

// 案例dialog
function handleCase() {
  flowCaseVisible.value = true;
  nextTick(() => {
    flowCaseRef.value?.init();
  });
}

// 案例选择
function handlCaseCellClick(row: any) {
  const { id } = row;
  const fn = caseDataMap[id];
  if (fn) {
    dataReload(fn());
  }
}

function handleOpenHelp() {
  flowHelpVisible.value = true;
  nextTick(() => {
    flowHelpRef.value?.init();
  });
}

// 获取详情
function getNodeDetail() {
  let { id } = route.query;
  const numId = Number(id);
  getRuleElDetail(numId).then((res: any) => {
    if (res.code === 200) {
      nodesDetail.value = res.data;
      const source = JSON.parse(nodesDetail.value.sourceJson);
      nextTick(() => {
        if (source) {
          dataReload(source);
        } else {
          dataReload({ nodes: [], lines: [] });
        }
      });
    }
  });
}

// 节点验证逻辑 (保存和发布共用)
function validateNodes(): boolean {
  const nodeList = data.value.nodes;
  if (!nodeList || !Array.isArray(nodeList) || nodeList.length === 0) return false;

  // 设备触发
  const devTriggers = nodeList.filter((item: any) => item.id?.includes('devTrigger_'));
  for (const item of devTriggers) {
    if (!item?.data) {
      proxy.$modal.msgError(t('ruleengine.editor.index.807357-22'));
      return false;
    }
    if (!(item.data.modelId || item.data.deviceId)) {
      proxy.$modal.msgError(t('ruleengine.editor.index.807357-23'));
      return false;
    }
  }

  // 设备执行
  const devExcute = nodeList.filter((item: any) => item.id?.includes('devExecute_'));
  for (const item of devExcute) {
    if (!item?.data) {
      proxy.$modal.msgError(t('ruleengine.editor.index.807357-24'));
      return false;
    }
    if (!(item.data.modelId || item.data.deviceId)) {
      proxy.$modal.msgError(t('ruleengine.editor.index.807357-25'));
      return false;
    }
  }

  // 产品触发
  const productTriggers = nodeList.filter((item: any) => item.id?.includes('productTrigger_'));
  for (const item of productTriggers) {
    if (!(item.data.modelId || item.data.productId)) {
      proxy.$modal.msgError(t('ruleengine.editor.index.807357-26'));
      return false;
    }
  }

  // 产品执行
  const productExcutes = nodeList.filter((item: any) => item.id?.includes('productExecute_'));
  for (const item of productExcutes) {
    if (!(item.data.productId || item.data.type || item.data.modelId || item.data.value)) {
      proxy.$modal.msgError(t('ruleengine.editor.index.807357-27'));
      return false;
    }
  }

  // 定时触发
  const timers = nodeList.filter((item: any) => item.id?.includes('scheduledTrigger_'));
  for (const item of timers) {
    if (!item.data.timerTimeValue) {
      proxy.$modal.msgError(t('ruleengine.editor.index.807357-28'));
      return false;
    }
  }

  // 告警组件
  const alarms = nodeList.filter((item: any) => item.id?.includes('alarm_'));
  for (const item of alarms) {
    if (!item.data.source) {
      proxy.$modal.msgError(t('ruleengine.editor.index.807357-29'));
      return false;
    }
    if (item.data.source === 4 && !item.data.notifyCount) {
      proxy.$modal.msgError(t('ruleengine.editor.index.807357-30'));
      return false;
    }
    if (item.data.source === 5 && !item.data.id) {
      proxy.$modal.msgError(t('ruleengine.editor.index.807357-31'));
      return false;
    }
  }

  // 条件组件判断
  const conditionList = nodeList.filter((item: any) => item.id?.includes('condition_'));
  for (const item of conditionList) {
    if (!item?.data) {
      proxy.$modal.msgError(t('ruleengine.editor.index.807357-32'));
      return false;
    }
    for (const expr of item.data.expressions) {
      if (!expr.value || expr.value.trim() === '') {
        proxy.$modal.msgError(t('ruleengine.editor.index.807357-33'));
        return false;
      }
      if (expr.value.indexOf('~') !== -1) {
        if (expr.valueA === '' || expr.valueB === '') {
          proxy.$modal.msgError(t('scene.index.670805-68'));
          return false;
        }
      }
    }
  }

  // 脚本组件
  const scriptNodeArr = nodeList.filter((item: any) => item.id?.includes('script_'));
  for (const item of scriptNodeArr) {
    if (!item?.data) {
      proxy.$modal.msgError(t('ruleengine.editor.index.807357-20'));
      return false;
    }
    if (!item.data.scriptName || item.data.scriptName.trim() === '') {
      proxy.$modal.msgError(t('ruleengine.editor.index.807357-15'));
      return false;
    }
    if (!item.data.scriptData || item.data.scriptData.trim() === '') {
      proxy.$modal.msgError(t('ruleengine.editor.index.807357-16'));
      return false;
    }
  }

  return true;
}

// 保存
function handleNodeSave() {
  if (!validateNodes()) return;
  const params = {
    ...nodesDetail.value,
    sourceJson: JSON.stringify(data.value),
  };
  updateRuleEl(params).then((res: any) => {
    if (res.code === 200) {
      proxy.$modal.msgSuccess(t('saveSuccess'));
      nodesDetail.value = res.data;
    }
  });
}

// 查看日志
function handleViewLog() {
  logLoading.value = true;
  let { id } = route.query;
  const numId = Number(id);
  getRuleElLog(numId).then((res: any) => {
    if (res.code === 200) {
      logList.value = res.data;
      let formattedResult = '';
      logList.value.forEach((item: any, index: number) => {
        const logOrderText = t('ruleengine.editor.index.807357-34');
        const logItemText = t('ruleengine.editor.index.807357-35');
        formattedResult += `===== ${logOrderText} ${index + 1} ${logItemText}（ID: ${item.id}） =====\n`;
        const execTimeText = t('ruleengine.editor.index.807357-36');
        const noInfoText = t('ruleengine.editor.index.807357-37');
        formattedResult += `${execTimeText}：${item.createTime || noInfoText}\n`;
        const execStatusText = t('ruleengine.editor.index.807357-38');
        const successText = t('ruleengine.editor.index.807357-39');
        const failureText = t('ruleengine.editor.index.807357-40');
        const statusValue = item.status === 1 ? successText : failureText;
        formattedResult += `${execStatusText}：${statusValue}\n`;
        const resultInfoText = t('ruleengine.editor.index.807357-42');
        const noResultInfoText = t('ruleengine.editor.index.807357-41');
        const msg = item.resultMsg || noResultInfoText;
        const formattedMsg = msg.replace(/\r\n/g, '\n');
        formattedResult += `${resultInfoText}：\n${formattedMsg}\n\n`;
      });
      resultMsg.value = formattedResult;
      openLog.value = true;
      title.value = t('script.349087-40');
      logLoading.value = false;
      nextTick(() => {
        const messageContent = logContainerRef.value;
        messageContent?.scroll({ top: messageContent.scrollHeight });
      });
    }
  });
}

// 取消日志按钮
function cancelLog() {
  resultMsg.value = '';
  openLog.value = false;
}

// 发布
function handleNodeRelease() {
  if (!validateNodes()) return;
  const params = {
    ...nodesDetail.value,
    enable: 1,
  };
  publishRuleEl(params).then((res: any) => {
    if (res.code === 200) {
      proxy.$modal.msgSuccess(t('releaseSuccess'));
      nodesDetail.value.enable = 1;
    }
  });
}

// 获取条件节点id
function getConditionComNum(node: any) {
  const { lines, nodes } = data.value;
  const { id } = node;
  const alines = lines?.filter((item: any) => item.to === id);
  const anodes = alines.map((item: any) => {
    return nodes?.some((so: any) => so.id === item.from && so.type === 'condition');
  });
  return anodes.filter((item: any) => item).length;
}

// lifecycle
onMounted(() => {
  window.addEventListener('keydown', handleKeyDown, true);
  jsPlumbInstance = (window as any).jsPlumb.getInstance();
  getNodeDetail();
});

onUnmounted(() => {
  window.removeEventListener('keydown', handleKeyDown, true);
});
</script>

<style lang="scss" scoped>
.panel-wrap {
  width: 100%;
  height: calc(100vh);

  .tooltar {
    display: flex;
    align-items: center;
    width: 100%;
    padding: 0 20px;
    height: 59px;
    border-bottom: 1px solid #eee;
    background-color: #fff;
    color: #333;
    font-size: 14px;
    box-sizing: border-box;

    .header-left {
      width: 30%;
      display: flex;
      align-items: center;

      .tips-text {
        margin-left: 20px;
        margin-right: 20px;
        padding-left: 12px;
        position: relative;

        &::before {
          content: '';
          position: absolute;
          left: 0;
          width: 6px;
          height: 6px;
          border-radius: 50%;
          top: calc(50% - 2px);
        }
      }

      .enable {
        color: #486ff2;

        &::before {
          background-color: #486ff2;
        }
      }

      .forbid {
        color: #ff4949;

        &::before {
          background-color: #ff4949;
        }
      }
    }

    .header-center,
    .header-right {
      display: flex;
      flex-direction: row;
      align-items: center;

      .btn-wrap {
        display: flex;
        flex-direction: column;
        align-items: center;
        padding: 0 7px;
        color: #565758;

        .icon {
          font-size: 21px;
        }

        .name {
          font-weight: 400;
          font-size: 14px;
          margin-top: 5px;
        }
      }
    }

    .header-center {
      width: 40%;
      justify-content: center;
    }

    .header-right {
      width: 30%;
      justify-content: flex-end;
    }
  }

  .content {
    display: flex;
    height: calc(100% - 60px);
    overflow: hidden;

    .menu-wrap {
      width: 207px;
      background-color: #fff;
      border-right: 1px solid #dce3e8;
    }

    .container-wrap {
      position: relative;
      overflow: scroll;
      flex: 1;
      background-color: #f4f7f9;

      .node-wrap {
        width: 100%;
        height: 100%;
      }
    }

    .form-wrap {
      width: 360px;
      background-color: #fff;
      border-left: 1px solid #dce3e8;
      transition: width 0.5s;
    }

    .aside-close {
      width: 0px !important;
    }
  }

  ::-webkit-scrollbar {
    width: 4px;
    height: 4px;
  }

  ::-webkit-scrollbar-thumb {
    border-radius: 4px;
    background: #e0e3e7;
    height: 20px;
  }

  ::-webkit-scrollbar-track {
    background-color: transparent;
  }
}
</style>
