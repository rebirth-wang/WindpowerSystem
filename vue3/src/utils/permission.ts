import { useUserStore } from '@/stores/modules/user';

/**
 * 字符权限校验
 */
export function checkPermi(value: string[]): boolean {
  if (value && value instanceof Array && value.length > 0) {
    const userStore = useUserStore();
    const permissions = userStore.permissions;
    const permissionDatas = value;
    const all_permission = '*:*:*';

    const hasPermission = permissions.some((permission: string) => {
      return all_permission === permission || permissionDatas.includes(permission);
    });

    return hasPermission;
  } else {
    console.error(`need roles! Like checkPermi="['system:user:add','system:user:edit']"`);
    return false;
  }
}

/**
 * 角色权限校验
 */
export function checkRole(value: string[]): boolean {
  if (value && value instanceof Array && value.length > 0) {
    const userStore = useUserStore();
    const roles = userStore.roles;
    const permissionRoles = value;
    const super_admin = 'admin';

    const hasRole = roles.some((role: string) => {
      return super_admin === role || permissionRoles.includes(role);
    });

    return hasRole;
  } else {
    console.error(`need roles! Like checkRole="['admin','editor']"`);
    return false;
  }
}

/**
 * 是否为平台内置 admin 账号。
 */
export function isAdminAccount(): boolean {
  const userStore = useUserStore();
  return String(userStore.name || '').toLowerCase() === 'admin';
}
