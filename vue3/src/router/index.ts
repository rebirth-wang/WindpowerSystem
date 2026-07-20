import { createRouter, createWebHistory } from 'vue-router';
import type { RouteRecordRaw } from 'vue-router';
import { i18n } from '@/lang';
import Layout from '@/layout/index.vue';

/** Extend Vue Router types for custom route properties used by RuoYi framework */
declare module 'vue-router' {
  interface RouteMeta {
    /** Page title shown in sidebar and breadcrumb */
    title?: string;
    /** SVG icon name for sidebar */
    icon?: string;
    /** If true, the page will not be cached by keep-alive */
    noCache?: boolean;
    /** If false, the route will not appear in breadcrumb */
    breadcrumb?: boolean;
    /** Highlight the specified sidebar menu */
    activeMenu?: string;
    /** If true, the tag cannot be closed */
    affix?: boolean;
    /** 0 = tab, 1 = new window */
    target?: number;
    /** Required permissions to access this route */
    permissions?: string[];
    /** Required roles to access this route */
    roles?: string[];
  }
}

/** Custom route properties (non-standard, used by RuoYi sidebar/permission) */
export interface AppRouteRecordRaw extends Omit<RouteRecordRaw, 'children'> {
  hidden?: boolean;
  alwaysShow?: boolean;
  children?: AppRouteRecordRaw[];
  permissions?: string[];
  roles?: string[];
  query?: string;
}

/** Static routes that do not require authentication */
export const constantRoutes: AppRouteRecordRaw[] = [
  {
    path: '/redirect',
    component: Layout,
    hidden: true,
    children: [
      {
        name: 'Redirect',
        path: '/redirect/:path(.*)',
        component: () => import('@/views/redirect.vue'),
      },
    ],
  },
  {
    name: 'FixedBigScreen',
    path: '/fixedBigScreen',
    component: () => import('@/views/fixedBigScreen/home.vue'),
    hidden: true,
  },
  {
    name: 'Login',
    path: '/login',
    component: () => import('@/views/login.vue'),
    hidden: true,
  },
  {
    name: 'License',
    path: '/license',
    component: () => import('@/views/license/index.vue'),
    hidden: true,
  },
  {
    name: 'Register',
    path: '/register',
    component: () => import('@/views/register.vue'),
    hidden: true,
  },
  {
    name: '404',
    path: '/404',
    component: () => import('@/views/error/404.vue'),
    hidden: true,
  },
  {
    name: '401',
    path: '/401',
    component: () => import('@/views/error/401.vue'),
    hidden: true,
  },
  {
    path: '/',
    component: Layout,
    redirect: 'index',
    children: [
      {
        name: 'Index',
        path: 'index',
        component: () => import('@/views/index.vue'),
        meta: { title: i18n.global.t('navbar.dashboard'), icon: 'home', affix: true },
      },
    ],
  },
  {
    path: '/user',
    component: Layout,
    hidden: true,
    children: [
      {
        name: 'Profile',
        path: 'profile',
        component: () => import('@/views/system/user/profile/index.vue'),
        meta: { title: i18n.global.t('navbar.personalCenter'), icon: 'user' },
      },
    ],
  },
  {
    name: 'VisualChartHome',
    path: '/visual/chart/home/:id(.*)*',
    component: () => import('@vb/views/chart/index.vue'),
    hidden: true,
  },
  {
    name: 'VisualChartPreview',
    path: '/visual/chart/preview/:id(.*)*',
    component: () => import('@vb/views/preview/index.vue'),
    hidden: true,
  },
  {
    name: 'VisualChartEdit',
    path: '/visual/chart/edit/:id(.*)*',
    component: () => import('@vb/views/edit/index.vue'),
    hidden: true,
  },
  {
    name: 'OauthLogin',
    path: '/oauthLogin',
    component: () => import('@/views/oauthLogin/index.vue'),
    hidden: true,
  },
  {
    name: 'Sso',
    path: '/sso',
    component: () => import('@/views/oauthLogin/oauth.vue'),
    hidden: true,
  },
  // 以下组态特有
  {
    name: 'ScadaFullscreen',
    path: '/scada/topo/fullscreen',
    component: () => import('@/views/scada/topo/fullscreen.vue'),
    hidden: true,
    permissions: ['scada:center:query'],
  },
  {
    name: 'ScadaEditor',
    path: '/scada/topo/editor',
    component: () => import('@/views/scada/topo/editor.vue'),
    hidden: true,
    permissions: ['scada:center:query'],
  },
  {
    name: 'ScadaShare',
    path: '/scada/topo/share',
    component: () => import('@/views/scada/topo/share.vue'),
    hidden: true,
    permissions: ['scada:center:query'],
  },
  // 以上组态特有
  {
    name: 'DragEditor',
    path: '/iot/dragEditor',
    component: () => import('@/views/dragEditor/index.vue'),
    hidden: true,
    permissions: ['iot:product:query'],
  },
  {
    name: 'RuleEditor',
    path: '/ruleengine/editor',
    component: () => import('@/views/ruleengine/editor/index.vue'),
    hidden: true,
    permissions: ['rule:el:query'],
  },
];

/** Dynamic routes loaded based on user permissions */
export const dynamicRoutes: AppRouteRecordRaw[] = [
  {
    path: '/system/roleAuth',
    component: Layout,
    hidden: true,
    permissions: ['system:role:edit'],
    children: [
      {
        path: 'user/:roleId(\\d+)',
        component: () => import('@/views/system/role/authUser.vue'),
        name: 'AuthUser',
        meta: { title: i18n.global.t('navbar.assignUsers'), activeMenu: '/enterprise/role' },
      },
    ],
  },
  {
    path: '/system/dictData',
    component: Layout,
    hidden: true,
    permissions: ['system:dict:list'],
    children: [
      {
        name: 'DictData',
        path: 'index/:dictId(\\d+)',
        component: () => import('@/views/system/dict/data.vue'),
        meta: { title: i18n.global.t('navbar.dictionaryData'), activeMenu: '/system/dict' },
      },
    ],
  },
  {
    path: '/system/userManage',
    component: Layout,
    hidden: true,
    permissions: ['system:user:edit'],
    children: [
      {
        name: 'DeptUser',
        path: 'user/:deptId(\\d+)',
        component: () => import('@/views/system/user/index.vue'),
        meta: { title: i18n.global.t('navbar.userManagement'), activeMenu: '/enterprise/dept' },
      },
    ],
  },
  {
    path: '/system/roleManage',
    component: Layout,
    hidden: true,
    permissions: ['system:role:edit'],
    children: [
      {
        name: 'DeptRole',
        path: 'role/:deptId(\\d+)',
        component: () => import('@/views/system/role/index.vue'),
        meta: { title: i18n.global.t('navbar.roleManagement'), activeMenu: '/enterprise/dept' },
      },
    ],
  },
  {
    path: '/monitor/jobLog',
    component: Layout,
    hidden: true,
    permissions: ['monitor:job:list'],
    children: [
      {
        name: 'JobLog',
        path: 'index/:jobId(\\d+)',
        component: () => import('@/views/monitor/job/log.vue'),
        meta: { title: i18n.global.t('navbar.dispatchingLog'), activeMenu: '/monitor/job' },
      },
    ],
  },
  {
    path: '/tool/genEdit',
    component: Layout,
    hidden: true,
    permissions: ['tool:gen:edit'],
    children: [
      {
        name: 'GenEdit',
        path: 'index/:tableId(\\d+)',
        component: () => import('@/views/tool/gen/editTable.vue'),
        meta: { title: i18n.global.t('navbar.modifyBuildConfiguration'), activeMenu: '/tool/gen' },
      },
    ],
  },
  {
    path: '/system/ossConfig',
    component: Layout,
    hidden: true,
    permissions: ['oss:config:list'],
    children: [
      {
        name: 'OssConfig',
        path: 'index',
        component: () => import('@/views/system/oss/config.vue'),
        meta: { title: i18n.global.t('navbar.configurationManagement'), activeMenu: '/video/detail' },
      },
    ],
  },
  {
    path: '/iot/product',
    component: Layout,
    hidden: true,
    permissions: ['iot:product:query'],
    children: [
      {
        name: 'ProductEdit',
        path: 'edit',
        component: () => import('@/views/iot/product/product-edit.vue'),
        meta: { title: i18n.global.t('navbar.editingProduct'), activeMenu: '/iot/product/list', noCache: true },
      },
    ],
  },
  {
    path: '/iot/device',
    component: Layout,
    hidden: true,
    permissions: ['iot:device:list'],
    children: [
      {
        name: 'DeviceEdit',
        path: 'edit',
        component: () => import('@/views/iot/device/device-edit.vue'),
        meta: { title: i18n.global.t('navbar.editingEquipment'), activeMenu: '/iot/device/list', noCache: true },
      },
      {
        name: 'DeviceSelectAllot',
        path: 'selectAllot',
        component: () => import('@/views/iot/device/device-select-allot.vue'),
        meta: { title: i18n.global.t('navbar.selectiveAllocation'), activeMenu: '/iot/device/list', noCache: true },
      },
      {
        name: 'DeviceUserAllot',
        path: 'userAllot',
        component: () => import('@/views/iot/device/device-user-allot.vue'),
        meta: { title: i18n.global.t('navbar.userAllocation'), activeMenu: '/iot/device/list', noCache: true },
      },
      {
        name: 'DeviceRecycling',
        path: 'recycle',
        component: () => import('@/views/iot/device/device-recycle.vue'),
        meta: { title: i18n.global.t('navbar.recoveryPlant'), activeMenu: '/iot/device/list', noCache: true },
      },
    ],
  },
  {
    path: '/iot/firmware',
    component: Layout,
    hidden: true,
    permissions: ['iot:firmware:query'],
    children: [
      {
        name: 'FirmwareTask',
        path: 'task',
        component: () => import('@/views/iot/firmware/firmware-task.vue'),
        meta: { title: i18n.global.t('navbar.firmwareDetails'), activeMenu: '/operation/firmware/list' },
      },
    ],
  },
  {
    path: '/iot/card',
    component: Layout,
    hidden: true,
    permissions: ['iot:card:query'],
    children: [
      {
        name: 'CardView',
        path: 'sim/view',
        component: () => import('@/views/iot/card/sim/card-view.vue'),
        meta: { title: i18n.global.t('card.sim.view_title'), activeMenu: '/iot/card/sim', noCache: true },
      },
      {
        name: 'CardSim',
        path: 'sim',
        component: () => import('@/views/iot/card/sim/index.vue'),
        meta: { title: i18n.global.t('card.sim.title'), activeMenu: '/iot/card/sim', noCache: true },
      },
    ],
  },
  {
    path: '/scene/list',
    component: Layout,
    hidden: true,
    permissions: ['scene:model:list'],
    children: [
      {
        name: 'ListDetail',
        path: 'detail',
        component: () => import('@/views/scene/list/detail/index.vue'),
        meta: { title: i18n.global.t('navbar.sceneDetails'), activeMenu: '/scene/list/index', noCache: true },
      },
      {
        name: 'ListEdit',
        path: 'edit',
        component: () => import('@/views/scene/list/edit.vue'),
        meta: { title: i18n.global.t('navbar.sceneDditing'), activeMenu: '/scene/list/index', noCache: true },
      },
    ],
  },
  {
    path: '/dataCenter/report',
    component: Layout,
    hidden: true,
    permissions: ['dataCenter:report:query'],
    children: [
      {
        name: 'ReportEdit',
        path: 'edit',
        component: () => import('@/views/dataCenter/report/report-edit.vue'),
        meta: {
          title: i18n.global.t('dataCenter.report.451245-26'),
          activeMenu: '/dataCenter/report/list',
          noCache: true,
        },
      },
    ],
  },
  // 以下组态特有
  {
    path: '/scada/echart',
    component: Layout,
    hidden: true,
    permissions: ['scada:echart:query'],
    children: [
      {
        name: 'EchartDetail',
        path: 'detail',
        component: () => import('@/views/scada/echart/detail.vue'),
        meta: { title: i18n.global.t('scada.echart.209302-11'), activeMenu: '/scada/echart/list', noCache: true },
      },
    ],
  },
  {
    path: '/scada/component',
    component: Layout,
    hidden: true,
    permissions: ['scada:component:query'],
    children: [
      {
        name: 'ComponentEditor',
        path: 'detail',
        component: () => import('@/views/scada/component/detail.vue'),
        meta: { title: i18n.global.t('scada.component.302923-13'), activeMenu: '/scada/component/list', noCache: true },
      },
    ],
  },
  // 以上组态特有
];

const router = createRouter({
  history: createWebHistory('/'),
  scrollBehavior: () => ({ top: 0 }),
  routes: constantRoutes as RouteRecordRaw[],
});

/** Suppress duplicate navigation errors on repeated clicks */
const originalPush = router.push;
router.push = function push(location) {
  return originalPush.call(this, location).catch((err: any) => err);
};

export default router;
