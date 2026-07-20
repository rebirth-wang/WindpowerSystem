import { defineStore } from 'pinia';
import { ref } from 'vue';
import router, { constantRoutes, dynamicRoutes } from '@/router';
import type { AppRouteRecordRaw } from '@/router';
import { getRouters } from '@/api/menu';
import auth from '@/plugins/auth';

const Layout = () => import('@/layout/index.vue');
const ParentView = () => import('@/components/ParentView/index.vue');
const InnerLink = () => import('@/layout/components/InnerLink/index.vue');

export const usePermissionStore = defineStore('permission', () => {
  const routes = ref<AppRouteRecordRaw[]>([]);
  const addRoutes = ref<AppRouteRecordRaw[]>([]);
  const defaultRoutes = ref<AppRouteRecordRaw[]>([]);
  const topbarRouters = ref<AppRouteRecordRaw[]>([]);
  const sidebarRouters = ref<AppRouteRecordRaw[]>([]);

  // SET_ROUTES mutation equivalent
  function setRoutes(newRoutes: AppRouteRecordRaw[]) {
    addRoutes.value = newRoutes;
    routes.value = constantRoutes.concat(newRoutes);
  }

  // SET_DEFAULT_ROUTES mutation equivalent
  function setDefaultRoutes(newRoutes: AppRouteRecordRaw[]) {
    defaultRoutes.value = constantRoutes.concat(newRoutes);
  }

  // SET_TOPBAR_ROUTES mutation equivalent
  function setTopbarRouters(newRoutes: AppRouteRecordRaw[]) {
    topbarRouters.value = newRoutes;
  }

  // SET_SIDEBAR_ROUTERS mutation equivalent
  function setSidebarRouters(newRoutes: AppRouteRecordRaw[]) {
    sidebarRouters.value = newRoutes;
  }

  // 生成路由
  function GenerateRoutes() {
    return new Promise((resolve) => {
      // 向后端请求路由数据
      getRouters().then((res) => {
        const sdata = JSON.parse(JSON.stringify(res.data));
        const rdata = JSON.parse(JSON.stringify(res.data));
        const sidebarRoutes = filterAsyncRouter(sdata);
        const rewriteRoutes = filterAsyncRouter(rdata, false, true);
        const asyncRoutes = filterDynamicRoutes(dynamicRoutes);
        rewriteRoutes.push({ path: '/:pathMatch(.*)*', redirect: '/404', hidden: true });
        // Vue3 使用 router.addRoute 代替 Vue2 的 router.addRoutes
        asyncRoutes.forEach((route: any) => {
          router.addRoute(route);
        });
        setRoutes(rewriteRoutes);
        setSidebarRouters(constantRoutes.concat(sidebarRoutes));
        setDefaultRoutes(sidebarRoutes);
        setTopbarRouters(sidebarRoutes);
        resolve(rewriteRoutes);
      });
    });
  }

  return {
    routes,
    addRoutes,
    defaultRoutes,
    topbarRouters,
    sidebarRouters,
    GenerateRoutes,
    setRoutes,
    setDefaultRoutes,
    setTopbarRouters,
    setSidebarRouters,
  };
});

// 遍历后台传来的路由字符串，转换为组件对象
function filterAsyncRouter(asyncRouterMap: any[], lastRouter = false, type = false) {
  return asyncRouterMap.filter((route) => {
    if (type && route.children) {
      route.children = filterChildren(route.children);
    }
    if (route.component) {
      // Layout ParentView 组件特殊处理
      if (route.component === 'Layout') {
        route.component = Layout;
      } else if (route.component === 'ParentView') {
        route.component = ParentView;
      } else if (route.component === 'InnerLink') {
        route.component = InnerLink;
      } else {
        route.component = loadView(route.component);
      }
    }
    if (route.children != null && route.children && route.children.length) {
      route.children = filterAsyncRouter(route.children, route, type);
    } else {
      delete route['children'];
      delete route['redirect'];
    }
    return true;
  });
}

function filterChildren(childrenMap: any[], lastRouter: any = false) {
  let children: any[] = [];
  childrenMap.forEach((el, index) => {
    if (el.children && el.children.length) {
      if (el.component === 'ParentView' && !lastRouter) {
        el.children.forEach((c) => {
          c.path = el.path + '/' + c.path;
          if (c.children && c.children.length) {
            children = children.concat(filterChildren(c.children, c));
            return;
          }
          children.push(c);
        });
        return;
      }
    }
    if (lastRouter) {
      el.path = lastRouter.path + '/' + el.path;
    }
    children = children.concat(el);
  });
  return children;
}

// 动态路由遍历，验证是否具备权限
export function filterDynamicRoutes(routes: any[]) {
  const res: any[] = [];
  routes.forEach((route) => {
    if (route.permissions) {
      if (auth.hasPermiOr(route.permissions)) {
        res.push(route);
      }
    } else if (route.roles) {
      if (auth.hasRoleOr(route.roles)) {
        res.push(route);
      }
    }
  });
  return res;
}

// 在文件顶部预加载所有视图组件
const viewModules = import.meta.glob('@/views/**/*.vue');

export function loadView(view: string) {
  // vite 中对于含变量的路径不处理@
  const path = `/src/views/${view}.vue`;

  if (viewModules[path]) {
    return viewModules[path];
  } else {
    return () => import('@/views/error/404.vue');
  }
}

export default usePermissionStore;
