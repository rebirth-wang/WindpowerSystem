/**
 * v-dialogDragWidth 可拖动弹窗宽度（右侧边）
 * Vue 3 版本
 */
import type { Directive } from 'vue';

const dialogDragWidth: Directive = {
  mounted(el: HTMLElement) {
    const dragDom = el.querySelector('.el-dialog') as HTMLElement;
    if (!dragDom) return;
    const lineEl = document.createElement('div');
    lineEl.style.cssText =
      'width: 5px; background: inherit; height: 80%; position: absolute; right: 0; top: 0; bottom: 0; margin: auto; z-index: 1; cursor: w-resize;';
    lineEl.addEventListener(
      'mousedown',
      function (e: MouseEvent) {
        const disX = e.clientX - el.offsetLeft;
        const curWidth = dragDom.offsetWidth;
        document.onmousemove = function (e: MouseEvent) {
          e.preventDefault();
          const l = e.clientX - disX;
          dragDom.style.width = `${curWidth + l}px`;
        };
        document.onmouseup = function () {
          document.onmousemove = null;
          document.onmouseup = null;
        };
      },
      false
    );
    dragDom.appendChild(lineEl);
  },
};

export default dialogDragWidth;
