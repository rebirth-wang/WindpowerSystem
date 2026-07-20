<template>
  <div :class="{ show: show }" class="header-search">
    <svg-icon class-name="search-icon" icon-class="search" @click="onClick" />
    <el-select
      ref="headerSearchSelectRef"
      v-model="search"
      :remote-method="querySearch"
      filterable
      default-first-option
      remote
      placeholder="Search"
      class="header-search-select"
      @change="change"
    >
      <el-option
        v-for="option in options"
        :key="option.item.path"
        :value="option.item"
        :label="option.item.title.join(' > ')"
      />
    </el-select>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import Fuse from 'fuse.js';
import { usePermissionStore } from '@/stores/modules/permission';
import { isExternal } from '@/utils/validate';

const router = useRouter();
const permissionStore = usePermissionStore();

const search = ref('');
const options = ref<any[]>([]);
const searchPool = ref<any[]>([]);
const show = ref(false);
const fuse = ref<any>(undefined);
const headerSearchSelectRef = ref();

const routes = computed(() => permissionStore.routes);

function resolvePath(basePath: string, routePath: string): string {
  if (isExternal(routePath)) return routePath;
  if (isExternal(basePath)) return basePath;
  if (routePath.startsWith('/')) return routePath;
  return basePath.replace(/\/+$/, '') + '/' + routePath.replace(/^\/+/, '');
}

function generateRoutes(routeList: any[], basePath = '/', prefixTitle: string[] = []): any[] {
  let res: any[] = [];
  for (const r of routeList) {
    if (r.hidden) continue;
    const data: any = {
      path: !isExternal(r.path) ? resolvePath(basePath, r.path) : r.path,
      title: [...prefixTitle],
    };
    if (r.meta && r.meta.title) {
      data.title = [...data.title, r.meta.title];
      if (r.redirect !== 'noRedirect') {
        res.push(data);
      }
    }
    if (r.children) {
      const tempRoutes = generateRoutes(r.children, data.path, data.title);
      if (tempRoutes.length >= 1) {
        res = [...res, ...tempRoutes];
      }
    }
  }
  return res;
}

function initFuse(list: any[]) {
  fuse.value = new Fuse(list, {
    shouldSort: true,
    threshold: 0.4,
    location: 0,
    distance: 100,
    minMatchCharLength: 1,
    keys: [
      { name: 'title', weight: 0.7 },
      { name: 'path', weight: 0.3 },
    ],
  });
}

function onClick() {
  show.value = !show.value;
  if (show.value) {
    headerSearchSelectRef.value?.focus();
  }
}

function close() {
  headerSearchSelectRef.value?.blur();
  options.value = [];
  show.value = false;
}

function change(val: any) {
  const path = val.path;
  if (isExternal(path)) {
    window.open(path, '_blank');
  } else {
    router.push(path);
  }
  search.value = '';
  options.value = [];
  show.value = false;
}

function querySearch(query: string) {
  if (query !== '') {
    options.value = fuse.value?.search(query) || [];
  } else {
    options.value = [];
  }
}

watch(routes, () => {
  searchPool.value = generateRoutes(routes.value);
});

watch(searchPool, (list) => {
  initFuse(list);
});

watch(show, (value) => {
  if (value) {
    document.body.addEventListener('click', close);
  } else {
    document.body.removeEventListener('click', close);
  }
});

onMounted(() => {
  searchPool.value = generateRoutes(routes.value);
});
</script>

<style lang="scss" scoped>
.header-search {
  font-size: 0 !important;

  .search-icon {
    cursor: pointer;
    font-size: 18px;
    vertical-align: middle;
  }

  .header-search-select {
    font-size: 18px;
    transition: width 0.2s;
    width: 0;
    overflow: hidden;
    background: transparent;
    border-radius: 0;
    display: inline-block;
    vertical-align: middle;

    :deep(.el-input__inner) {
      border-radius: 0;
      border: 0;
      padding-left: 0;
      padding-right: 0;
      box-shadow: none !important;
      border-bottom: 1px solid #d9d9d9;
      vertical-align: middle;
    }
  }

  &.show {
    .header-search-select {
      width: 210px;
      margin-left: 10px;
    }
  }
}
</style>
