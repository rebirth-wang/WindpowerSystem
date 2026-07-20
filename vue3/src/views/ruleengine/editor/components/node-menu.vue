<template>
  <div class="flow-menu" ref="tool">
    <div v-for="menu in menuListData" :key="menu.id">
      <span class="node-pmenu" @click="menu.open = !menu.open">
        <i :class="{ 'el-icon-caret-bottom': menu.open, 'el-icon-caret-right': !menu.open }"></i>
        <span style="margin-left: 8px">{{ menu.name }}</span>
      </span>
      <ul v-show="menu.open" class="node-menu-ul" v-if="menu.type == 'bridge'">
        <draggable
          :list="menu.children"
          item-key="id"
          @end="handleEnd"
          @start="handleMove"
          :sort="false"
          :forceFallback="true"
          :ghostClass="'tt'"
        >
          <template #item="{ element: subMenu }">
            <li class="node-menu-li" :data-id="subMenu.id" :type="subMenu.type" draggable="true">
              <el-icon class="menu-icon"><component :is="resolveIcon(subMenu.ico)" /></el-icon>
              {{ subMenu.name }}
            </li>
          </template>
        </draggable>
      </ul>
      <ul v-show="menu.open" class="node-menu-ul" v-else>
        <draggable
          :list="menu.children"
          item-key="id"
          @end="handleEnd"
          @start="handleMoveComp"
          :sort="false"
          :forceFallback="true"
          :ghostClass="'tt'"
        >
          <template #item="{ element: subMenu }">
            <li class="node-menu-li" :type="subMenu.type">
              <el-icon class="menu-icon"><component :is="resolveIcon(subMenu.ico)" /></el-icon>
              {{ subMenu.name }}
            </li>
          </template>
        </draggable>
      </ul>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import draggable from 'vuedraggable';
import { cloneDeep } from 'lodash-es';
import * as ElIcons from '@element-plus/icons-vue';
import { menuList } from '../js/menu.js';

const mousePosition = reactive({
  left: -1,
  top: -1,
});

const emit = defineEmits(['addNode']);

const menuListData = reactive(cloneDeep(menuList));
const nodeMenu = ref<any>({});
const draggedNode = ref<any>(null);

const resolveIcon = (iconName: string) => {
  return (ElIcons as any)[iconName] || ElIcons.Menu;
};

/** 根据类型获取左侧菜单对象 */
const getMenuByType = (type: string) => {
  for (let i = 0; i < menuListData.length; i++) {
    const children = menuListData[i].children;
    for (let j = 0; j < children.length; j++) {
      if (children[j].type === type) {
        return children[j];
      }
    }
  }
};

const getMenuByDataType = (dataType: number) => {
  for (let i = 0; i < menuListData.length; i++) {
    const children = menuListData[i].children;
    for (let j = 0; j < children.length; j++) {
      if ((children[j] as any).data?.type === dataType) {
        return children[j];
      }
    }
  }
};

const getMenuById = (id: string) => {
  for (let i = 0; i < menuListData.length; i++) {
    const children = menuListData[i].children;
    for (let j = 0; j < children.length; j++) {
      if (children[j].id === id) {
        return children[j];
      }
    }
  }
};

/** 拖拽开始时触发（bridge类型节点使用，存储当前被拖拽的节点配置） */
const handleDragStart = (subMenu: any, event: DragEvent) => {
  draggedNode.value = subMenu;
  event.dataTransfer?.setData('nodeId', subMenu.id);
};

/** 拖拽开始时触发 */
const handleMoveComp = (evt: any) => {
  const type = evt.item.attributes.type?.nodeValue;
  if (type) {
    nodeMenu.value = getMenuByType(type);
  }
};

const handleMove = (evt: any) => {
  const nodeId = evt.item.getAttribute('data-id');
  const currentNode = getMenuById(nodeId) as any;
  if (currentNode) {
    nodeMenu.value = getMenuByDataType(currentNode.data.type);
  }
};

/** 拖拽结束时触发 */
const handleEnd = (evt: any) => {
  const node = cloneDeep(nodeMenu.value);
  emit('addNode', evt, node, mousePosition);
};

/** 是否是火狐浏览器 */
const isFirefox = () => {
  return navigator.userAgent.indexOf('Firefox') > -1;
};

onMounted(() => {
  if (isFirefox()) {
    document.body.ondrop = function (event: DragEvent) {
      mousePosition.left = (event as any).layerX;
      mousePosition.top = event.clientY - 50;
      event.preventDefault();
      event.stopPropagation();
    };
  }
});

defineExpose({ getMenuByType, getMenuByDataType, getMenuById });
</script>

<style lang="scss" scoped>
.flow-menu {
  width: 100%;
  height: 100%;
  margin-top: 8px;
  overflow-y: auto;

  .node-pmenu {
    cursor: pointer;
    height: 32px;
    line-height: 32px;
    font-size: 14px;
    width: 225px;
    display: block;
    font-weight: bold;
    color: #4a4a4a;
    padding-left: 5px;
  }

  .node-menu-ul {
    list-style: none;
    padding-left: 20px;
    margin: 10px 0;
    font-size: 13px;

    .node-menu-li {
      color: #565758;
      width: 166px;
      border: 1px dashed #e0e3e7;
      margin: 5px 0 5px 0;
      padding: 5px;
      border-radius: 5px;
      padding-left: 8px;
      user-select: none;
      display: flex;
      align-items: center;
      gap: 6px;

      .menu-icon {
        font-size: 14px;
      }
    }

    .node-menu-li:hover {
      cursor: move;
      background-color: #f0f7ff;
      border: 1px dashed #1879ff;
      border-left: 4px solid #1879ff;
      padding-left: 5px;
    }
  }
}
</style>
