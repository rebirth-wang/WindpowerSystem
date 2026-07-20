/**
 * v-dialogDragHeight 可拖动弹窗高度（右下角）
 * Vue 3 版本
 */
import type { Directive } from 'vue';

const dialogDragHeight: Directive = {
  mounted(el: HTMLElement) {
    const dragDom = el.querySelector('.el-dialog') as HTMLElement;
    if (!dragDom) return;
    const lineEl = document.createElement('div');
    lineEl.style.cssText =
      'width: 6px; background: inherit; height: 10px; position: absolute; right: 0; bottom: 0; margin: auto; z-index: 1; cursor: nwse-resize;';
    lineEl.addEventListener(
      'mousedown',
      function (e: MouseEvent) {
        const disX = e.clientX - el.offsetLeft;
        const disY = e.clientY - el.offsetTop;
        const curWidth = dragDom.offsetWidth;
        const curHeight = dragDom.offsetHeight;
        document.onmousemove = function (e: MouseEvent) {
          e.preventDefault();
          const xl = e.clientX - disX;
          const yl = e.clientY - disY;
          dragDom.style.width = `${curWidth + xl}px`;
          dragDom.style.height = `${curHeight + yl}px`;
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

export default dialogDragHeight;
