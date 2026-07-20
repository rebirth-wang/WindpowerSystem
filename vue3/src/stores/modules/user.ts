import { defineStore } from 'pinia';
import { ref } from 'vue';
import { login, logout, getInfo } from '@/api/login';
import { getToken, setToken, removeToken, setUserId, removeUserId } from '@/utils/auth';

/** Credentials submitted by the login form */
export interface LoginForm {
  username: string;
  password: string;
  code: string;
  uuid: string;
}

/** Department info attached to user */
export interface DeptInfo {
  deptId: number;
  deptName: string;
  [key: string]: any;
}

export const useUserStore = defineStore('user', () => {
  const token = ref(getToken());
  const name = ref('');
  const avatar = ref('');
  const roles = ref<string[]>([]);
  const permissions = ref<string[]>([]);
  const dept = ref<DeptInfo | null>(null);
  const mqtt = ref(false);
  const dataScope = ref('');

  /** Login with credentials and store token */
  async function Login(userInfo: LoginForm) {
    const username = userInfo.username.trim();
    const password = userInfo.password;
    const code = userInfo.code;
    const uuid = userInfo.uuid;
    const sourceType = 1;
    const res = await login(username, password, code, uuid, sourceType);
    setToken(res.token, res.expireTime);
    token.value = res.token;
    return res;
  }

  /** Fetch current user info, roles, and permissions */
  async function GetInfo() {
    const res = await getInfo();
    const user = res.user;
    const avatarUrl =
      user.avatar === '' || user.avatar == null
        ? '@/assets/images/profile.jpg'
        : import.meta.env.VITE_APP_BASE_API + user.avatar;
    if (res.roles && res.roles.length > 0) {
      roles.value = res.roles;
      permissions.value = res.permissions;
    } else {
      roles.value = ['ROLE_DEFAULT'];
    }
    name.value = user.userName;
    avatar.value = avatarUrl;
    dept.value = user.dept;
    mqtt.value = !!res.mqtt;
    dataScope.value = res.dataScope || '';
    setUserId(user.userId);
    return res;
  }

  /** Server-side logout — clear token and user state */
  async function LogOut() {
    await logout(token.value);
    token.value = '';
    roles.value = [];
    permissions.value = [];
    dept.value = null;
    dataScope.value = '';
    removeToken();
    removeUserId();
  }

  /** Client-side only logout — just clear the token */
  function FedLogOut() {
    token.value = '';
    removeToken();
  }

  return {
    token,
    name,
    avatar,
    roles,
    permissions,
    dept,
    mqtt,
    dataScope,
    Login,
    GetInfo,
    LogOut,
    FedLogOut,
  };
});
