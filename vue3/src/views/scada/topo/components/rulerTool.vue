<template>
  <div
    v-show="rulerToggle"
    :style="scaleBoxStyle"
    class="ScaleBox"
    @selectstart.prevent
  >
    <div id="levelRuler" class="ScaleRuler_h" @mousedown.stop="levelDragRuler">
      <span v-for="(item, index) in xScale" :key="index" :style="{ left: index * 50 + 2 + 'px' }" class="n">
        {{ item.id }}
      </span>
    </div>
    <div id="verticalRuler" class="ScaleRuler_v" @mousedown.stop="verticalDragRuler">
      <span v-for="(item, index) in yScale" :key="index" :style="{ top: index * 50 + 2 + 'px' }" class="n">
        {{ item.id }}
      </span>
    </div>
    <div id="levelDottedLine" :style="{ top: verticalDottedTop + 'px' }" class="RefDot_h" />
    <div id="verticalDottedLine" :style="{ left: levelDottedLeft + 'px' }" class="RefDot_v" />
    <div
      v-for="item in levelLineList"
      :id="item.id"
      :title="item.title"
      :style="{ top: item.top + 'px' }"
      :key="item.id"
      class="RefLine_h"
      @mousedown="dragLevelLine(item.id)"
    />
    <div
      v-for="item in verticalLineList"
      :id="item.id"
      :title="item.title"
      :style="{ left: item.left + 'px' }"
      :key="item.id"
      class="RefLine_v"
      @mousedown="dragVerticalLine(item.id)"
    />
    <div id="content" class="content" style="padding: 18px">
      <slot />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onBeforeUnmount, getCurrentInstance, nextTick } from 'vue';
import type { CSSProperties } from 'vue';

const props = defineProps({
  position: {
    type: String,
    default: 'relative',
    validator: (val: string) => ['absolute', 'fixed', 'relative', 'static', 'inherit'].indexOf(val) !== -1,
  },
  isHotKey: {
    type: Boolean,
    default: true,
  },
  isScaleRevise: {
    type: Boolean,
    default: false,
  },
  presetLine: {
    type: Array,
    default: () => [],
  },
  contentLayout: {
    type: Object,
    default: () => ({ top: 0, left: 0 }),
  },
  parent: {
    type: Boolean,
    default: false,
  },
});

const instance = getCurrentInstance();

const windowWidth = ref(0);
const windowHeight = ref(0);
const xScale = ref<any[]>([]);
const yScale = ref<any[]>([]);
const topSpacing = ref(0);
const leftSpacing = ref(0);
const isDrag = ref(false);
const dragFlag = ref('');
const levelLineList = ref<any[]>([]);
const verticalLineList = ref<any[]>([]);
const levelDottedLeft = ref(-999);
const verticalDottedTop = ref(-999);
const rulerWidth = ref(0);
const rulerHeight = ref(0);
const dragLineId = ref('');
const keyCode = { r: 82 };
const rulerToggle = ref(true);

const scaleBoxStyle = computed((): CSSProperties => ({
  width: `${windowWidth.value}px`,
  height: `${windowHeight.value}px`,
  position: props.position as CSSProperties['position'],
}));

let parentResizeObserver: ResizeObserver | null = null;

/** 从父节点或视口更新宽高与标尺辅助尺寸（须在 DOM 布局完成后调用） */
function measureDimensions() {
  const vr = document.getElementById('verticalRuler');
  const lr = document.getElementById('levelRuler');
  rulerWidth.value = vr?.clientWidth;
  rulerHeight.value = lr?.clientHeight;
  topSpacing.value = lr?.getBoundingClientRect().y ?? 0;
  leftSpacing.value = vr?.getBoundingClientRect().x ?? 0;

  if (props.parent) {
    const el = instance?.proxy?.$el as HTMLElement | undefined;
    const parent = el?.parentElement;
    if (parent) {
      windowWidth.value = parent.clientWidth;
      windowHeight.value = parent.clientHeight;
    }
  } else {
    windowWidth.value = document.documentElement.clientWidth - leftSpacing.value;
    windowHeight.value = document.documentElement.clientHeight - topSpacing.value;
  }
}

/** 在下一帧及再下一帧测量，避免侧栏 transition 未结束读到旧宽度 */
function scheduleMeasureAndScale() {
  nextTick(() => {
    requestAnimationFrame(() => {
      requestAnimationFrame(() => {
        measureDimensions();
        scaleCalc();
      });
    });
  });
}

function pushScalesFromContentOffset() {
  if (!props.isScaleRevise) return;
  const content = document.getElementById('content');
  const contentLeft = content?.offsetLeft;
  const contentTop = content?.offsetTop;
  for (let i = 0; i < (contentLeft ?? 0); i += 1) {
    if (i % 50 === 0 && i + 50 <= (contentLeft ?? 0)) {
      xScale.value.push({ id: i });
    }
  }
  for (let i = 0; i < (contentTop ?? 0); i += 1) {
    if (i % 50 === 0 && i + 50 <= (contentTop ?? 0)) {
      yScale.value.push({ id: i });
    }
  }
}

function init() {
  xScale.value = [];
  yScale.value = [];
  pushScalesFromContentOffset();
  scheduleMeasureAndScale();
}

function scaleCalc() {
  for (let i = 0; i < windowWidth.value; i += 1) {
    if (i % 50 === 0) {
      xScale.value.push({ id: i });
    }
  }
  for (let i = 0; i < windowHeight.value; i += 1) {
    if (i % 50 === 0) {
      yScale.value.push({ id: i });
    }
  }
}

function newLevelLine() {
  isDrag.value = true;
  dragFlag.value = 'x';
}

function newVerticalLine() {
  isDrag.value = true;
  dragFlag.value = 'y';
}

function dottedLineMove($event: MouseEvent) {
  switch (dragFlag.value) {
    case 'x':
      if (isDrag.value) verticalDottedTop.value = $event.pageY - topSpacing.value;
      break;
    case 'y':
      if (isDrag.value) levelDottedLeft.value = $event.pageX - leftSpacing.value;
      break;
    case 'l':
      if (isDrag.value) verticalDottedTop.value = $event.pageY - topSpacing.value;
      break;
    case 'v':
      if (isDrag.value) levelDottedLeft.value = $event.pageX - leftSpacing.value;
      break;
  }
}

function dottedLineUp($event: MouseEvent) {
  if (isDrag.value) {
    isDrag.value = false;
    switch (dragFlag.value) {
      case 'x':
        levelLineList.value.push({
          id: 'levelLine' + levelLineList.value.length + 1,
          title: $event.pageY + 1 - topSpacing.value - 18 + 'px',
          top: $event.pageY - topSpacing.value + 1,
        });
        break;
      case 'y':
        verticalLineList.value.push({
          id: 'verticalLine' + verticalLineList.value.length + 1,
          title: $event.pageX + 1 - leftSpacing.value - 18 + 'px',
          left: $event.pageX - leftSpacing.value + 1,
        });
        break;
      case 'l': {
        if ($event.pageY - topSpacing.value < rulerHeight.value) {
          let Index: number = 0,
            id: string = '';
          levelLineList.value.forEach((item, index) => {
            if (item.id === dragLineId.value) {
              Index = index;
              id = item.id;
            }
          });
          levelLineList.value.splice(Index, 1, { id, title: -600 + 'px', top: -600 });
        } else {
          let Index: number = 0,
            id: string = '';
          levelLineList.value.forEach((item, index) => {
            if (item.id === dragLineId.value) {
              Index = index;
              id = item.id;
            }
          });
          levelLineList.value.splice(Index, 1, {
            id,
            title: $event.pageY + 1 - topSpacing.value - 18 + 'px',
            top: $event.pageY - topSpacing.value + 1,
          });
        }
        break;
      }
      case 'v': {
        if ($event.pageX - leftSpacing.value < rulerWidth.value) {
          let Index: number = 0,
            id: string = '';
          verticalLineList.value.forEach((item, index) => {
            if (item.id === dragLineId.value) {
              Index = index;
              id = item.id;
            }
          });
          verticalLineList.value.splice(Index, 1, { id, title: -600 + 'px', left: -600 });
        } else {
          let Index: number = 0,
            id: string = '';
          verticalLineList.value.forEach((item, index) => {
            if (item.id === dragLineId.value) {
              Index = index;
              id = item.id;
            }
          });
          verticalLineList.value.splice(Index, 1, {
            id,
            title: $event.pageX + 2 - leftSpacing.value - 18 + 'px',
            left: $event.pageX - leftSpacing.value + 1,
          });
        }
        break;
      }
    }
    verticalDottedTop.value = levelDottedLeft.value = -10;
  }
}

function levelDragRuler() {
  newLevelLine();
}
function verticalDragRuler() {
  newVerticalLine();
}

function dragLevelLine(id: string) {
  isDrag.value = true;
  dragFlag.value = 'l';
  dragLineId.value = id;
}

function dragVerticalLine(id: string) {
  isDrag.value = true;
  dragFlag.value = 'v';
  dragLineId.value = id;
}

function keyboard($event: KeyboardEvent) {
  if (props.isHotKey) {
    if ($event.keyCode === keyCode.r) {
      rulerToggle.value = !rulerToggle.value;
    }
  }
}

function quickGeneration(params: any[]) {
  if (params && params.length !== 0) {
    params.forEach((item) => {
      if (item.type === 'l') {
        levelLineList.value.push({
          id: 'levelLine' + levelLineList.value.length + 1,
          title: item.site + 'px',
          top: item.site,
        });
      } else if (item.type === 'v') {
        verticalLineList.value.push({
          id: 'verticalLine' + verticalLineList.value.length + 1,
          title: item.site + 'px',
          left: item.site,
        });
      }
    });
  }
}

function handleWindowResize() {
  init();
}

onMounted(() => {
  document.documentElement.addEventListener('mousemove', dottedLineMove, true);
  document.documentElement.addEventListener('mouseup', dottedLineUp, true);
  document.documentElement.addEventListener('keyup', keyboard, true);
  window.addEventListener('resize', handleWindowResize);
  init();
  quickGeneration(props.presetLine as any[]);

  if (props.parent && typeof ResizeObserver !== 'undefined') {
    const el = instance?.proxy?.$el as HTMLElement | undefined;
    const parent = el?.parentElement;
    if (parent) {
      let resizeTick = 0;
      parentResizeObserver = new ResizeObserver(() => {
        window.cancelAnimationFrame(resizeTick);
        resizeTick = window.requestAnimationFrame(() => {
          xScale.value = [];
          yScale.value = [];
          pushScalesFromContentOffset();
          measureDimensions();
          scaleCalc();
        });
      });
      parentResizeObserver.observe(parent);
    }
  }
});

onBeforeUnmount(() => {
  document.documentElement.removeEventListener('mousemove', dottedLineMove, true);
  document.documentElement.removeEventListener('mouseup', dottedLineUp, true);
  document.documentElement.removeEventListener('keyup', keyboard, true);
  window.removeEventListener('resize', handleWindowResize);
  parentResizeObserver?.disconnect();
  parentResizeObserver = null;
});

defineExpose({ init });
</script>

<style scoped>
.ScaleBox {
  left: 0;
  top: 0;
  z-index: 999;
  overflow: hidden;
  user-select: none;
}

.ScaleRuler_h,
.ScaleRuler_v,
.RefLine_v,
.RefLine_h,
.RefDot_h,
.RefDot_v {
  position: absolute;
  left: 0;
  top: 0;
  overflow: hidden;
  z-index: 999;
}

.ScaleRuler_h {
  width: calc(100% - 18px);
  height: 18px;
  left: 18px;
  opacity: 0.6;
  background: url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADIAAAASCAMAAAAuTX21AAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAAlQTFRFMzMzAAAA////BqjYlAAAACNJREFUeNpiYCAdMDKRCka1jGoBA2JZZGshiaCXFpIBQIABAAplBkCmQpujAAAAAElFTkSuQmCC)
    repeat-x;
  /*./image/ruler_h.png*/
}

.ScaleRuler_v {
  width: 18px;
  height: calc(100% - 18px);
  top: 18px;
  opacity: 0.6;
  background: url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABIAAAAyCAMAAABmvHtTAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAAlQTFRFMzMzAAAA////BqjYlAAAACBJREFUeNpiYGBEBwwMTGiAakI0NX7U9aOuHyGuBwgwAH6bBkAR6jkzAAAAAElFTkSuQmCC)
    repeat-y;
  /*./image/ruler_v.png*/
}

.ScaleRuler_v .n,
.ScaleRuler_h .n {
  position: absolute;
  font:
    10px/1 Arial,
    sans-serif;
  color: #333;
  cursor: default;
}

.ScaleRuler_v .n {
  width: 8px;
  left: 3px;
  word-wrap: break-word;
}

.ScaleRuler_h .n {
  top: 1px;
}

.RefLine_v,
.RefLine_h,
.RefDot_h,
.RefDot_v {
  z-index: 998;
}

.RefLine_h {
  width: 100%;
  height: 3px;
  background: url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAgAAAABCAMAAADU3h9xAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAAZQTFRFSv//AAAAH8VRuAAAAA5JREFUeNpiYIACgAADAAAJAAE0lmO3AAAAAElFTkSuQmCC)
    repeat-x left center;
  /*./image/line_h.png*/
  cursor: n-resize;
  /*url(./image/cur_move_h.cur), move*/
}

.RefLine_v {
  width: 3px;
  height: 100%;
  _height: 9999px;
  background: url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAAICAMAAAAPxGVzAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAAZQTFRFSv//AAAAH8VRuAAAAA5JREFUeNpiYEAFAAEGAAAQAAGePof9AAAAAElFTkSuQmCC)
    repeat-y center top;
  /*./image/line_v.png*/
  cursor: w-resize;
  /*url(./image/cur_move_v.cur), move*/
}

.RefDot_h {
  width: 100%;
  height: 3px;
  background: url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAIAAAACCAMAAABFaP0WAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAAZQTFRFf39/////F3PnHQAAAAJ0Uk5T/wDltzBKAAAAEElEQVR42mJgYGRgZAQIMAAADQAExkizYQAAAABJRU5ErkJggg==)
    repeat-x left 1px;
  /*./image/line_dot.png*/
  cursor: n-resize;
  /*url(./image/cur_move_h.cur), move*/
  top: -10px;
}

.RefDot_v {
  width: 3px;
  height: 100%;
  _height: 9999px;
  background: url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAIAAAACCAMAAABFaP0WAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAAZQTFRFf39/////F3PnHQAAAAJ0Uk5T/wDltzBKAAAAEElEQVR42mJgYGRgZAQIMAAADQAExkizYQAAAABJRU5ErkJggg==)
    repeat-y 1px top;
  /*./image/line_dot.png*/
  cursor: w-resize;
  /*url(./image/cur_move_v.cur), move*/
  left: -10px;
}

#content {
  position: absolute;
  height: 100%;
  width: 100%;
  overflow: auto;
  top: 0;
  left: 50%;
  transform: translate(-50%, 0);
}
</style>
