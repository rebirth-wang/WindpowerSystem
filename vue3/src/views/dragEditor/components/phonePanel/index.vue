<template>
  <div class="panel-wrap">
    <div class="phone-wrap">
      <div class="phone-all" ref="phoneAllRef" id="phoneAll">
        <img class="status-bar" src="@/assets/images/drag/phone_top.png" alt="" />
        <!-- 头部导航 -->
        <headerTop :pageSetup="dragStore.pageSetup" @click="handleHeadTop" />
        <!-- 主体内容 -->
        <div
          class="phone-container"
          :style="{
            'background-color': dragStore.pageSetup.bgColor,
            backgroundImage: 'url(' + dragStore.pageSetup.bgImg + ')',
          }"
          @drop="handleDrop($event)"
          @dragover="handleAllowDrop($event)"
          @dragleave="handleDragleaves"
        >
          <div :class="pointer.show ? 'pointer-events' : ''">
            <template v-for="(item, index) in dragStore.pageComponents" :key="index">
              <component
                v-if="!preview.show"
                :is="dragComponentRegistry[item.component] || item.component"
                :datas="item"
                @click="handleActiveComponent(item, index)"
                :class="['components-class', item.active ? 'edit-seled' : '']"
                :data-type="item.type"
              >
                <template #deles>
                  <div class="deles" @click.stop="handleDeleteCmp(index)">
                    <drag-icon class="caret-left" name="CaretLeft" :size="12" />
                    <div class="title">{{ $t(item.text) }}</div>
                    <drag-icon name="Delete" :size="12" />
                  </div>
                </template>
              </component>
            </template>
          </div>
        </div>
        <div v-show="dragStore.pageComponents.length === 0" class="app-empty-tips">
          <el-icon><Coin /></el-icon>
          {{ $t('dragEditor.565720-18') }}
        </div>
        <div class="phoneSize">{{ $t('dragEditor.565720-19') }}</div>
      </div>
    </div>
    <!-- 页面设置tab -->
    <div class="decorate-tab">
      <span :class="dragStore.rightcom === 'decorate' ? 'active' : ''" @click="dragStore.setRightcom('decorate')">
        <el-icon>
          <Iphone />
        </el-icon>
        {{ $t('dragEditor.565720-6') }}
      </span>
      <span
        :class="dragStore.rightcom === 'componenmanagement' ? 'active' : ''"
        @click="dragStore.setRightcom('componenmanagement')"
      >
        <el-icon>
          <Postcard />
        </el-icon>
        {{ $t('dragEditor.565720-7') }}
      </span>
      <span class="active" v-if="dragStore.rightcom != 'componenmanagement' && dragStore.rightcom != 'decorate'">
        <el-icon>
          <Coin />
        </el-icon>
        {{ $t('dragEditor.565720-8') }}
      </span>
    </div>
    <realTimeView
      :datas="preview"
      :val="{
        id: dragStore.id,
        name: dragStore.pageSetup.name,
        templateJson: JSON.stringify(dragStore.pageSetup),
        component: JSON.stringify(dragStore.pageComponents),
      }"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, getCurrentInstance } from 'vue';
import { useDragEditorStore } from '@/stores/modules/dragEditor';
import HeaderTop from './headerTop.vue';
import RealTimeView from './realTimeView.vue';
import DragIcon from '@/components/DragEditor/DragIcon.vue';
import utils from '@/utils/index';
import componentProperties from '@/utils/dragEditor';
import { dragComponentRegistry } from '../componentRegistry';
import { Iphone, Postcard, Coin, Delete } from '@element-plus/icons-vue';

const { proxy } = getCurrentInstance() as any;
const dragStore = useDragEditorStore();

const props = defineProps<{ preview: any; pointer: any }>();

const phoneAllRef = ref<any>(null);
const currentIndex = ref<any>('');
const onlyOne = ['1-5', '1-16'];
const offsetY = ref(0);

onMounted(() => {
  phoneAllDrag();
  phoneAllZoom();
});

/** 监听整个手机的移动事件 */
function phoneAllDrag() {
  let isDragging = false;
  let x = 0,
    y = 0;
  const element = document.getElementById('phoneAll');
  if (!element) return;
  element.addEventListener('mousedown', function (e) {
    isDragging = true;
    x = e.pageX - element.offsetLeft;
    y = e.pageY - element.offsetTop;
  });
  document.addEventListener('mousemove', function (e) {
    if (isDragging) {
      element.style.left = e.pageX - x + 'px';
      element.style.top = e.pageY - y + 'px';
    }
  });
  document.addEventListener('mouseup', function () {
    isDragging = false;
  });
}

/** 通过滚轮进行缩放 */
function phoneAllZoom() {
  const element = document.getElementById('phoneAll');
  if (!element) return;
  let scale = 1;
  const zoomSpeed = 0.1;
  element.addEventListener('wheel', function (event) {
    event.preventDefault();
    const direction = event.deltaY > 0 ? -1 : 1;
    scale += direction * zoomSpeed;
    scale = Math.max(0.6, Math.min(scale, 1.4));
    element.style.transform = `scale(${scale})`;
  });
}

/** 放置目标元素上 */
function handleDrop(event: DragEvent) {
  event.preventDefault();
  const componentName = event.dataTransfer?.getData('componentName') || event.dataTransfer?.getData('text/plain') || '';
  if (!componentName) {
    handleDragleaves();
    return;
  }

  const source = componentProperties.get(componentName);
  if (!source) {
    handleDragleaves();
    return;
  }

  const data = utils.deepClone(source);

  const someOne = dragStore.pageComponents.some((item: any, index: number) => {
    return item.component === 'placementarea' && index === 0 && onlyOne.includes(data.type);
  });
  if (someOne) {
    proxy.$modal.msgWarning(proxy.$t('dragEditor.565720-20'));
    handleDragleaves();
    return;
  }

  const someResult = dragStore.pageComponents.some((item: any) => {
    return onlyOne.includes(item.type) && item.component === componentName;
  });
  if (someResult) {
    proxy.$modal.msgWarning(proxy.$t('dragEditor.565720-21'));
    handleDragleaves();
    return;
  }

  const list = dragStore.pageComponents.map((item: any) => ({ ...item, active: false }));
  const placementIndex = list.findIndex((item: any) => item.component === 'placementarea');

  if (placementIndex >= 0) {
    list.splice(placementIndex, 1, data);
    currentIndex.value = placementIndex;
  } else {
    list.push(data);
    currentIndex.value = list.length - 1;
  }

  dragStore.setPageComponents(list);
  dragStore.setRightcom(data.style);
  dragStore.setCurrentproperties(data);
}

/** 拖拽经过 */
function handleAllowDrop(event: DragEvent) {
  event.preventDefault();
  let eventoffset = (event as any).offsetY;
  if (offsetY.value === eventoffset) return;
  offsetY.value = eventoffset;
  const childrenObject = (event.target as HTMLElement).children[0];
  if (!childrenObject) return;
  if (dragStore.pageComponents.length) {
    if (dragStore.pageComponents.length === 1 && dragStore.pageComponents[0].type === 0) return;
    if (childrenObject.children[0] && eventoffset < (childrenObject.children[0] as HTMLElement).clientHeight / 2) {
      if (dragStore.pageComponents[0].type === 0) return;
      let list = dragStore.pageComponents.filter((res: any) => res.component !== 'placementarea') || [];
      list.unshift({ component: 'placementarea', type: 0 });
      dragStore.setPageComponents(list);
      return;
    }
    const childOff = (childrenObject as HTMLElement).offsetTop;
    if (
      eventoffset > (childrenObject as HTMLElement).clientHeight ||
      (childrenObject.lastChild as HTMLElement).offsetTop -
        childOff +
        (childrenObject.lastChild as HTMLElement).clientHeight / 2 <
        eventoffset
    ) {
      if (dragStore.pageComponents[dragStore.pageComponents.length - 1].type === 0) return;
      let list = dragStore.pageComponents.filter((res: any) => res.component !== 'placementarea') || [];
      list.push({ component: 'placementarea', type: 0 });
      dragStore.setPageComponents(list);
      return;
    }
    const childrens = childrenObject.children;
    for (let i = 0, l = childrens.length; i < l; i++) {
      const childoffset = (childrens[i] as HTMLElement).offsetTop - childOff;
      if (childoffset + (childrens[i] as HTMLElement).clientHeight / 2 > (event as any).offsetY) {
        if (dragStore.pageComponents[i].type === 0) break;
        if (dragStore.pageComponents[i - 1]?.type === 0) break;
        let list = dragStore.pageComponents.filter((res: any) => res.component !== 'placementarea') || [];
        list.splice(i, 0, { component: 'placementarea', type: 0 });
        dragStore.setPageComponents(list);
        break;
      } else if (childoffset + (childrens[i] as HTMLElement).clientHeight > (event as any).offsetY) {
        if (dragStore.pageComponents[i].type === 0) break;
        if (!dragStore.pageComponents[i + 1] || dragStore.pageComponents[i + 1].type === 0) break;
        let list = dragStore.pageComponents.filter((res: any) => res.component !== 'placementarea') || [];
        list.splice(i, 0, { component: 'placementarea', type: 0 });
        dragStore.setPageComponents(list);
        break;
      }
    }
  } else {
    let list = [...dragStore.pageComponents];
    list.push({ component: 'placementarea', type: 0 });
    dragStore.setPageComponents(list);
  }
}

/** 拖拽离开 */
function handleDragleaves() {
  const list = dragStore.pageComponents.filter((res: any) => res.component !== 'placementarea');
  dragStore.setPageComponents(list);
}

/** 删除组件 */
function handleDeleteCmp(index: number) {
  dragStore.pageComponents.splice(index, 1);
  if (currentIndex.value === index) dragStore.setRightcom('decorate');
  if (index < currentIndex.value) currentIndex.value = currentIndex.value - 1;
}

/** 选择组件 */
function handleActiveComponent(res: any, index: number) {
  currentIndex.value = index;
  dragStore.setRightcom(res.style);
  dragStore.setCurrentproperties(res);
  utils.forEach(dragStore.pageComponents, (item: any) => {
    if (item.active === true) item.active = false;
  });
  res.active = true;
}

/** 标题切换 */
function handleHeadTop() {
  dragStore.setRightcom('decorate');
  utils.forEach(dragStore.pageComponents, (res: any) => {
    if (res.active === true) res.active = false;
  });
}
</script>

<style lang="scss" scoped>
.panel-wrap {
  width: 100%;
  height: 100%;
  position: relative;

  .phone-wrap {
    width: 100%;
    height: 100%;
    background: #f7f8fa;
    position: relative;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    overflow-y: scroll;
    &::-webkit-scrollbar {
      width: 0px;
    }

    .phone-all {
      display: flex;
      flex-direction: column;
      width: 375px;
      min-height: 660px;
      box-shadow: 0 0 14px 0 rgba(0, 0, 0, 0.1);
      position: absolute;

      .phoneSize {
        position: absolute;
        left: -137px;
        top: 640px;
        font-size: 12px;
        color: #a2a2a2;
        border-bottom: 1px solid #dedede;
        width: 130px;
        height: 21px;
        line-height: 21px;
      }
      .status-bar {
        width: 100%;
        display: block;
      }
      .phone-container {
        flex: 1;
        box-sizing: border-box;
        cursor: pointer;
        width: 100%;
        position: relative;
        background-repeat: no-repeat;
        background-size: cover;
        background-position: center center;
        .pointer-events {
          pointer-events: none;
        }
        .components-class {
          border: 1px solid transparent;
          &:hover {
            border: 1px dashed #155bd4c4;
          }
          &:hover :deep(.deles) {
            display: block;
          }
        }
        .edit-seled {
          outline: 2px dashed #155bd4c4;
          outline-offset: -1px;
          z-index: 1;
          // background: rgba(21, 91, 212, 0.04);
          // box-shadow: 0 4px 12px rgba(0, 0, 0, 0.12);
          // transition:
          //   box-shadow 0.2s ease,
          //   background 0.2s ease;
        }
      }
      .app-empty-tips {
        display: flex;
        flex-direction: column;
        align-items: center;
        gap: 20px;
        color: #c0c4cc;
        font-size: 16px;
        position: absolute;
        top: 200px;
        left: 0;
        right: 0;
      }
    }
  }

  .deles {
    position: absolute;
    min-width: 80px;
    text-align: center;
    line-height: 25px;
    background: #fff;
    color: #555;
    height: 25px;
    font-size: 12px;
    left: 103%;
    top: 50%;
    transform: translateY(-50%);
    .el-icon-caret-left {
      position: absolute;
      left: -11px;
      color: #fff;
      font-size: 12px;
      top: 50%;
      transform: translateY(-50%);
    }
    .title {
      width: 90px;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
      padding: 0 4px;
    }
    &:hover {
      i {
        display: block;
        position: absolute;
        left: 0;
        font-size: 16px;
        top: 0;
        text-align: center;
        line-height: 25px;
        width: 100%;
        color: #fff;
        height: 100%;
        z-index: 10;
        background: rgba(0, 0, 0, 0.3);
      }
      .el-icon-caret-left {
        color: rgba(0, 0, 0, 0.5);
      }
    }
    i {
      display: none;
    }
  }

  .decorate-tab {
    position: absolute;
    display: flex;
    right: 0;
    top: 0px;
    flex-direction: column;
    span {
      width: 100px;
      height: 36px;
      background-color: #fff;
      display: inline-block;
      text-align: center;
      line-height: 36px;
      transition: all 0.8s;
      cursor: pointer;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
      font-size: 14px;
      padding: 0 5px;
      &:not(:last-child) {
        border-bottom: 1px solid #f2f4f6;
      }
      &.active {
        color: #155bd4;
      }
      i {
        font-size: 12px;
        margin-right: 3px;
      }
    }
  }
}
</style>
