/**
 * v-hasRole 角色权限处理
 * Vue 3 版本
 */
import { useUserStore } from '@/stores/modules/user';
import type { Directive, DirectiveBinding } from 'vue';

const hasRole: Directive = {
  mounted(el: HTMLElement, binding: DirectiveBinding) {
    const { value } = binding;
    const super_admin = 'admin';
    const userStore = useUserStore();
    const roles = userStore.roles;

    if (value && value instanceof Array && value.length > 0) {
      const roleFlag = value;
      const hasRole = roles.some((role: string) => {
        return super_admin === role || roleFlag.includes(role);
      });

      if (!hasRole) {
        el.parentNode && el.parentNode.removeChild(el);
      }
    } else {
      throw new Error(`请设置角色权限标签值`);
    }
  },
};

export default hasRole;
