<template>
  <div
    :id="node.id"
    ref="nodeRef"
    :style="nodeContainerStyle"
    :class="nodeContainerClass"
    @click="handleClickNode"
    @mouseup="changeNodeSite"
    v-if="!node.id.includes('comment')"
  >
    <!-- 最左侧的那条竖线 -->
    <div class="node-left"></div>
    <!-- 节点类型的图标 -->
    <div class="node-left-ico flow-node-drag">
      <el-icon><component :is="resolveIcon(node.ico)" /></el-icon>
    </div>
    <!-- 节点名称 -->
    <el-tooltip :content="node.name" placement="top" v-if="node.type == 'bridge'">
      <div class="node-text" :show-overflow-tooltip="true">
        {{ node.name }}
      </div>
    </el-tooltip>
    <div class="node-text" :show-overflow-tooltip="true" v-else>
      {{ node.name }}
    </div>
    <!-- 节点状态图标 -->
    <div class="node-right-ico">
      <el-icon class="el-node-state-success" v-show="node.state === 'success'"><CircleCheck /></el-icon>
      <el-icon class="el-node-state-error" v-show="node.state === 'error'"><CircleClose /></el-icon>
      <el-icon class="el-node-state-warning" v-show="node.state === 'warning'"><WarningFilled /></el-icon>
      <el-icon class="el-node-state-running" v-show="node.state === 'running'"><Loading /></el-icon>
    </div>
  </div>
  <div
    :id="node.id"
    ref="nodeRef"
    :style="nodeContainerStyle"
    :class="nodeContainerClass"
    @click="handleClickNode"
    @mouseup="changeNodeSite"
    v-else
  >
    <!-- 节点类型的图标 -->
    <div class="node-left-ico flow-node-drag">
      <el-icon><component :is="resolveIcon(node.ico)" /></el-icon>
    </div>
    <!-- 节点名称 -->
    <div class="node-text" :show-overflow-tooltip="true">
      {{ node.name }}
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue';
import * as ElIcons from '@element-plus/icons-vue';
import { CircleCheck, CircleClose, WarningFilled, Loading } from '@element-plus/icons-vue';

const props = defineProps<{
  node: any;
  activeElement: any;
}>();

const emit = defineEmits(['clickNode', 'changeNodeSite']);

const nodeRef = ref<HTMLElement>();

const nodeContainerClass = computed(() => ({
  'node-container': true,
  'node-active': props.activeElement.type == 'node' ? props.activeElement.nodeId === props.node.id : false,
}));

const nodeContainerStyle = computed(() => ({
  top: props.node.top,
  left: props.node.left,
}));

const resolveIcon = (iconName: string) => {
  return (ElIcons as any)[iconName] || ElIcons.Menu;
};

const handleClickNode = (e: MouseEvent) => {
  const target = e.target as HTMLElement;
  if (target.closest('.flow-node-drag')) {
    return;
  }
  e.stopPropagation();
  emit('clickNode', props.node);
};

const changeNodeSite = () => {
  if (!nodeRef.value) return;
  if (props.node.left == nodeRef.value.style.left && props.node.top == nodeRef.value.style.top) {
    return;
  }
  emit('changeNodeSite', {
    nodeId: props.node.id,
    left: nodeRef.value.style.left,
    top: nodeRef.value.style.top,
  });
};
</script>

<style lang="scss" scoped>
/*节点的最外层容器*/
.node-container {
  position: absolute;
  display: flex;
  width: 166px;
  height: 33px;
  border: 1px solid #e0e3e7;
  border-radius: 5px;
  background-color: #fff;

  .node-left {
    width: 4px;
    background-color: #1879ff;
    border-radius: 4px 0 0 4px;
  }

  .node-left-ico {
    display: flex;
    align-items: center;
    align-self: stretch;
    line-height: 32px;
    padding: 0 4px;
    margin-left: 4px;
    color: #1879ff;
    cursor: crosshair;

    // 让连线事件命中 .flow-node-drag 容器，避免图标内部元素拦截鼠标事件
    :deep(.el-icon),
    :deep(svg),
    :deep(path) {
      pointer-events: none;
    }
  }

  .node-text {
    color: #565758;
    font-size: 12px;
    line-height: 32px;
    margin-left: 8px;
    width: 100px;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    text-align: center;
  }

  .node-right-ico {
    line-height: 32px;
    position: absolute;
    right: 8px;
    top: 8px;

    color: #84cf65;
    cursor: default;

    .el-node-state-success {
      line-height: 32px;
      position: absolute;
      right: 5px;
      color: #84cf65;
      cursor: default;
    }

    .el-node-state-error {
      line-height: 32px;
      position: absolute;
      right: 5px;
      color: #f56c6c;
      cursor: default;
    }

    .el-node-state-warning {
      line-height: 32px;
      position: absolute;
      right: 5px;
      color: #e6a23c;
      cursor: default;
    }

    .el-node-state-running {
      line-height: 32px;
      position: absolute;
      right: 5px;
      color: #84cf65;
      cursor: default;
    }
  }
}

.node-container:hover {
  cursor: move;
  background-color: #f0f7ff;
  border: 1px dashed #1879ff;
}

// 节点激活样式
.node-active {
  background-color: #f0f7ff;
  border: 1px solid #1879ff;
}
</style>
