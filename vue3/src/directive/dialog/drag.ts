/**
 * v-dialogDrag 弹窗拖拽
 * Vue 3 版本
 */
import type { Directive } from 'vue';

const dialogDrag: Directive = {
  mounted(el: HTMLElement, binding) {
    const value = binding.value;
    if (value === false) return;
    // 获取拖拽内容头部
    const dialogHeaderEl = el.querySelector('.el-dialog__header') as HTMLElement;
    const dragDom = el.querySelector('.el-dialog') as HTMLElement;
    if (!dialogHeaderEl || !dragDom) return;

    dialogHeaderEl.style.cursor = 'move';
    const sty = (dragDom as any).currentStyle || window.getComputedStyle(dragDom, null);
    dragDom.style.position = 'absolute';
    dragDom.style.marginTop = '0';
    let width = dragDom.style.width;
    if (width.includes('%')) {
      width = String(+document.body.clientWidth * (+width.replace(/%/g, '') / 100));
    } else {
      width = String(+width.replace(/px/g, ''));
    }
    dragDom.style.left = `${(document.body.clientWidth - +width) / 2}px`;

    dialogHeaderEl.onmousedown = (e: MouseEvent) => {
      const disX = e.clientX - dialogHeaderEl.offsetLeft;
      const disY = e.clientY - dialogHeaderEl.offsetTop;

      let styL: number, styT: number;
      if (sty.left.includes('%')) {
        styL = +document.body.clientWidth * (+sty.left.replace(/%/g, '') / 100);
        styT = +document.body.clientHeight * (+sty.top.replace(/%/g, '') / 100);
      } else {
        styL = +sty.left.replace(/px/g, '');
        styT = +sty.top.replace(/px/g, '');
      }

      document.onmousemove = function (e: MouseEvent) {
        const l = e.clientX - disX;
        const t = e.clientY - disY;
        const finallyL = l + styL;
        const finallyT = t + styT;
        dragDom.style.left = `${finallyL}px`;
        dragDom.style.top = `${finallyT}px`;
      };

      document.onmouseup = function () {
        document.onmousemove = null;
        document.onmouseup = null;
      };
    };
  },
};

export default dialogDrag;
