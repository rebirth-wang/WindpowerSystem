/**
 * v-clipboard 文字复制剪贴
 * Vue 3 版本
 */
import Clipboard from 'clipboard';
import type { Directive, DirectiveBinding } from 'vue';

const clipboard: Directive = {
  mounted(el: any, binding: DirectiveBinding) {
    switch (binding.arg) {
      case 'success':
        el._vClipBoard_success = binding.value;
        break;
      case 'error':
        el._vClipBoard_error = binding.value;
        break;
      default: {
        const clip = new Clipboard(el, {
          text: () => binding.value,
          action: () => (binding.arg === 'cut' ? 'cut' : 'copy'),
        });
        clip.on('success', (e: any) => {
          const callback = el._vClipBoard_success;
          callback && callback(e);
        });
        clip.on('error', (e: any) => {
          const callback = el._vClipBoard_error;
          callback && callback(e);
        });
        el._vClipBoard = clip;
      }
    }
  },
  updated(el: any, binding: DirectiveBinding) {
    if (binding.arg === 'success') {
      el._vClipBoard_success = binding.value;
    } else if (binding.arg === 'error') {
      el._vClipBoard_error = binding.value;
    } else {
      el._vClipBoard.text = function () {
        return binding.value;
      };
      el._vClipBoard.action = () => (binding.arg === 'cut' ? 'cut' : 'copy');
    }
  },
  unmounted(el: any, binding: DirectiveBinding) {
    if (!el._vClipBoard) return;
    if (binding.arg === 'success') {
      delete el._vClipBoard_success;
    } else if (binding.arg === 'error') {
      delete el._vClipBoard_error;
    } else {
      el._vClipBoard.destroy();
      delete el._vClipBoard;
    }
  },
};

export default clipboard;
