import { ElMessage } from 'element-plus';
import NProgress from 'nprogress';
import 'nprogress/nprogress.css';

import router from '@/router';
import { useUserStore } from '@/stores/modules/user';
import { usePermissionStore } from '@/stores/modules/permission';
import { useSettingsStore } from '@/stores/modules/settings';
import { getToken } from '@/utils/auth';
import { isRelogin } from '@/utils/request';

NProgress.configure({ showSpinner: false });

const whiteList = ['/license', '/login', '/auth-redirect', '/bind', '/register', '/oauthLogin'];

router.beforeEach((to, from, next) => {
  NProgress.start();
  const userStore = useUserStore();
  const permissionStore = usePermissionStore();
  const settingsStore = useSettingsStore();

  if (getToken()) {
    to.meta.title && settingsStore.setTitle(to.meta.title);
    /* has token*/
    if (to.path === '/login') {
      next({ path: '/' });
      NProgress.done();
    } else {
      if (userStore.roles.length === 0) {
        isRelogin.show = true;
        // 判断当前用户是否已拉取完user_info信息
        userStore
          .GetInfo()
          .then(() => {
            isRelogin.show = false;
            permissionStore.GenerateRoutes().then((accessRoutes: any) => {
              // 根据roles权限生成可访问的路由表
              // Vue3 使用 router.addRoute 代替 Vue2 的 router.addRoutes
              accessRoutes.forEach((route: any) => {
                if (route.path.startsWith('/')) {
                  router.addRoute(route);
                }
              });
              // 如果是访问根路径，明确重定向到首页
              if (to.path === '/') {
                next('/index');
              } else {
                next({ ...to, replace: true }); // hack方法 确保addRoutes已完成
              }
            });
          })
          .catch((err: any) => {
            userStore.LogOut().then(() => {
              ElMessage.error(err?.messagerr);
              // 如果在异常情况下重定向到根路径，改为重定向到首页
              if (to.path === '/') {
                next('/index');
              } else {
                next({ path: '/' });
              }
            });
          });
      } else {
        // 如果用户已登录且访问根路径，重定向到首页
        if (to.path === '/') {
          next('/index');
        } else {
          next();
        }
      }
    }
  } else {
    // 没有token
    if (whiteList.indexOf(to.path) !== -1) {
      // 在免登录白名单，直接进入
      next();
    } else {
      const { share } = to.query;
      if (share) {
        next();
        return;
      }
      next(`/login?redirect=${to.fullPath}`); // 全部重定向到登录页
      NProgress.done();
    }
  }
});

router.afterEach(() => {
  NProgress.done();
});
