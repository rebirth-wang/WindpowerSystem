import { useUserStore } from '@/stores/modules/user';

function authPermission(permission: string): boolean {
  const all_permission = '*:*:*';
  const userStore = useUserStore();
  const permissions = userStore.permissions;
  if (permission && permission.length > 0) {
    return permissions.some((v: string) => {
      return all_permission === v || v === permission;
    });
  } else {
    return false;
  }
}

function authRole(role: string): boolean {
  const super_admin = 'admin';
  const userStore = useUserStore();
  const roles = userStore.roles;
  if (role && role.length > 0) {
    return roles.some((v: string) => {
      return super_admin === v || v === role;
    });
  } else {
    return false;
  }
}

export default {
  // 验证用户是否具备某权限
  hasPermi(permission: string) {
    return authPermission(permission);
  },
  // 验证用户是否含有指定权限，只需包含其中一个
  hasPermiOr(permissions: string[]) {
    return permissions.some((item: string) => {
      return authPermission(item);
    });
  },
  // 验证用户是否含有指定权限，必须全部拥有
  hasPermiAnd(permissions: string[]) {
    return permissions.every((item: string) => {
      return authPermission(item);
    });
  },
  // 验证用户是否具备某角色
  hasRole(role: string) {
    return authRole(role);
  },
  // 验证用户是否含有指定角色，只需包含其中一个
  hasRoleOr(roles: string[]) {
    return roles.some((item: string) => {
      return authRole(item);
    });
  },
  // 验证用户是否含有指定角色，必须全部拥有
  hasRoleAnd(roles: string[]) {
    return roles.every((item: string) => {
      return authRole(item);
    });
  },
};
