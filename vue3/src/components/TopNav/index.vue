<template>
  <el-menu :default-active="activeMenu" mode="horizontal" @select="handleSelect">
    <template v-for="(item, index) in topMenus">
      <el-menu-item :style="{ '--theme': theme }" :index="item.path" :key="index" v-if="index < visibleNumber">
        <svg-icon :icon-class="item.meta.icon" />
        {{ item.meta.title }}
      </el-menu-item>
    </template>

    <!-- 顶部菜单超出数量折叠 -->
    <el-sub-menu :style="{ '--theme': theme }" index="more" v-if="topMenus.length > visibleNumber">
      <template #title>更多菜单</template>
      <template v-for="(item, index) in topMenus">
        <el-menu-item :index="item.path" :key="index" v-if="index >= visibleNumber">
          <svg-icon :icon-class="item.meta.icon" />
          {{ item.meta.title }}
        </el-menu-item>
      </template>
    </el-sub-menu>
  </el-menu>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onBeforeUnmount } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useAppStore } from '@/stores/modules/app';
import { usePermissionStore } from '@/stores/modules/permission';
import { constantRoutes } from '@/router';

const router = useRouter();
const route = useRoute();
const appStore = useAppStore();
const permissionStore = usePermissionStore();

// 隐藏侧边栏路由
const hideList = ['/index', '/user/profile'];

const visibleNumber = ref(5);
const currentIndex = ref<string | undefined>(undefined);

const theme = computed(() => appStore.theme);

// 顶部显示菜单
const topMenus = computed(() => {
  const menus: any[] = [];
  routers.value.forEach((menu: any) => {
    if (menu.hidden !== true) {
      if (menu.path === '/') {
        menus.push(menu.children[0]);
      } else {
        menus.push(menu);
      }
    }
  });
  return menus;
});

// 所有的路由信息
const routers = computed(() => permissionStore.topbarRouters);

// 设置子路由
const childrenMenus = computed(() => {
  const menus: any[] = [];
  routers.value.forEach((r: any) => {
    for (const item in r.children) {
      if (r.children[item].parentPath === undefined) {
        if (r.path === '/') {
          r.children[item].path = '/' + r.children[item].path;
        } else {
          if (!ishttp(r.children[item].path)) {
            r.children[item].path = r.path + '/' + r.children[item].path;
          }
        }
        r.children[item].parentPath = r.path;
      }
      menus.push(r.children[item]);
    }
  });
  return (constantRoutes as any[]).concat(menus);
});

// 默认激活的菜单
const activeMenu = computed(() => {
  const path = route.path;
  let activePath = path;
  if (path !== undefined && path.lastIndexOf('/') > 0 && hideList.indexOf(path) === -1) {
    const tmpPath = path.substring(1, path.length);
    activePath = '/' + tmpPath.substring(0, tmpPath.indexOf('/'));
    if (!route.meta.link) {
      appStore.toggleSideBarHide(false);
    }
  } else if (!(route as any).children) {
    activePath = path;
    appStore.toggleSideBarHide(true);
  }
  activeRoutes(activePath);
  return activePath;
});

// 根据宽度计算设置显示栏数
function setVisibleNumber() {
  const width = document.body.getBoundingClientRect().width / 3;
  visibleNumber.value = parseInt(String(width / 85));
}

// 菜单选择事件
function handleSelect(key: string, _keyPath: string[]) {
  currentIndex.value = key;
  const routeItem = routers.value.find((item: any) => item.path === key);
  if (ishttp(key)) {
    const target = topMenus.value.find((item: any) => item.path === key)?.meta?.target;
    window.open(key, target ? '_blank' : '_self');
  } else if (!routeItem || !routeItem.children) {
    router.push({ path: key });
    appStore.toggleSideBarHide(true);
  } else {
    activeRoutes(key);
    appStore.toggleSideBarHide(false);
  }
}

// 当前激活的路由
function activeRoutes(key: string) {
  const routes: any[] = [];
  if (childrenMenus.value && childrenMenus.value.length > 0) {
    childrenMenus.value.forEach((item: any) => {
      if (key === item.parentPath || (key === 'index' && '' === item.path)) {
        routes.push(item);
      }
    });
  }
  if (routes.length > 0) {
    permissionStore.SET_SIDEBAR_ROUTERS(routes);
  } else {
    appStore.toggleSideBarHide(true);
  }
}

function ishttp(url: string) {
  return url.indexOf('http://') !== -1 || url.indexOf('https://') !== -1;
}

onMounted(() => {
  window.addEventListener('resize', setVisibleNumber);
  setVisibleNumber();
});

onBeforeUnmount(() => {
  window.removeEventListener('resize', setVisibleNumber);
});
</script>

<style lang="scss">
.topmenu-container.el-menu--horizontal > .el-menu-item {
  float: left;
  height: 50px !important;
  line-height: 50px !important;
  color: #999093 !important;
  padding: 0 5px !important;
  margin: 0 10px !important;
}

.topmenu-container.el-menu--horizontal > .el-menu-item.is-active,
.el-menu--horizontal > .el-sub-menu.is-active .el-sub-menu__title {
  border-bottom: 2px solid #{'var(--theme)'} !important;
  color: #303133;
}

/* submenu item */
.topmenu-container.el-menu--horizontal > .el-sub-menu .el-sub-menu__title {
  float: left;
  height: 50px !important;
  line-height: 50px !important;
  color: #999093 !important;
  padding: 0 5px !important;
  margin: 0 10px !important;
}
</style>
