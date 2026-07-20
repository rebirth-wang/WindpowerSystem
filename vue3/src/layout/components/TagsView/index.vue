<template>
  <div id="tags-view-container" class="tags-view-container">
    <scroll-pane ref="scrollPane" class="tags-view-wrapper" @scroll="handleScroll">
      <router-link
        v-for="tag in visitedViews"
        :ref="
          (el) => {
            if (el) tagRefs[tag.path] = el;
          }
        "
        :key="tag.path"
        :class="isActive(tag) ? 'active' : ''"
        :to="{ path: tag.path, query: tag.query }"
        custom
        v-slot="{ navigate }"
      >
        <span
          class="tags-view-item"
          :style="activeStyle(tag)"
          @click="navigate"
          @click.middle="!isAffix(tag) ? closeSelectedTag(tag) : ''"
          @contextmenu.prevent="openMenu(tag, $event)"
        >
          {{ tag.title }}
          <span v-if="!isAffix(tag)" class="el-icon-close" @click.prevent.stop="closeSelectedTag(tag)">
            <el-icon :size="12"><Close /></el-icon>
          </span>
        </span>
      </router-link>
    </scroll-pane>
    <ul v-show="visible" :style="{ left: left + 'px', top: top + 'px' }" class="contextmenu">
      <li @click="refreshSelectedTag(selectedTag)">刷新页面</li>
      <li v-if="!isAffix(selectedTag)" @click="closeSelectedTag(selectedTag)">关闭当前</li>
      <li @click="closeOthersTags">关闭其他</li>
      <li @click="closeAllTags(selectedTag)">全部关闭</li>
    </ul>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, nextTick, reactive } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { Close } from '@element-plus/icons-vue';
import { useTagsViewStore } from '@/stores/modules/tagsView';
import { usePermissionStore } from '@/stores/modules/permission';
import { useSettingsStore } from '@/stores/modules/settings';
import ScrollPane from './ScrollPane.vue';

const route = useRoute();
const router = useRouter();
const tagsViewStore = useTagsViewStore();
const permissionStore = usePermissionStore();
const settingsStore = useSettingsStore();

const visible = ref(false);
const top = ref(0);
const left = ref(0);
const selectedTag = ref<any>({});
const affixTags = ref<any[]>([]);
const tagRefs = reactive<Record<string, any>>({});
const scrollPane = ref<any>(null);

const visitedViews = computed(() => tagsViewStore.visitedViews);
const routes = computed(() => permissionStore.routes);
const theme = computed(() => settingsStore.theme);

watch(route, () => {
  addTags();
});

watch(visible, (value) => {
  if (value) {
    document.body.addEventListener('click', closeMenu);
  } else {
    document.body.removeEventListener('click', closeMenu);
  }
});

onMounted(() => {
  initTags();
  addTags();
});

function isActive(tag: any) {
  return tag.path === route.path;
}

function activeStyle(tag: any) {
  if (!isActive(tag)) return {};
  return {
    'background-color': theme.value,
    'border-color': theme.value,
  };
}

function isAffix(tag: any) {
  return tag.meta && tag.meta.affix;
}

function filterAffixTags(routes: any[], basePath = '/') {
  let tags: any[] = [];
  routes.forEach((r) => {
    if (r.meta && r.meta.affix) {
      const tagPath = basePath + '/' + r.path;
      tags.push({
        fullPath: tagPath.replace('//', '/'),
        path: tagPath.replace('//', '/'),
        name: r.name,
        meta: { ...r.meta },
      });
    }
    if (r.children) {
      const tempTags = filterAffixTags(r.children, r.path);
      if (tempTags.length >= 1) {
        tags = [...tags, ...tempTags];
      }
    }
  });
  return tags;
}

function initTags() {
  const tags = (affixTags.value = filterAffixTags(routes.value));
  for (const tag of tags) {
    if (tag.name) {
      tagsViewStore.addVisitedView(tag);
    }
  }
}

function addTags() {
  const { name } = route;
  if (name) {
    tagsViewStore.addView(route);
  }
  return false;
}

function refreshSelectedTag(view: any) {
  tagsViewStore.delCachedView(view).then(() => {
    const { fullPath } = view;
    nextTick(() => {
      router.replace({ path: '/redirect' + fullPath });
    });
  });
}

function closeSelectedTag(view: any) {
  tagsViewStore.delView(view).then(({ visitedViews }: any) => {
    if (isActive(view)) {
      toLastView(visitedViews, view);
    }
  });
}

function closeOthersTags() {
  router.push(selectedTag.value).catch(() => {});
  tagsViewStore.delOthersViews(selectedTag.value);
}

function closeAllTags(view: any) {
  tagsViewStore.delAllViews().then(({ visitedViews }: any) => {
    if (affixTags.value.some((tag) => tag.path === route.path)) {
      return;
    }
    toLastView(visitedViews, view);
  });
}

function toLastView(visitedViews: any[], view: any) {
  const latestView = visitedViews.slice(-1)[0];
  if (latestView) {
    router.push(latestView.fullPath);
  } else {
    if (view.name === 'Dashboard') {
      router.replace({ path: '/redirect' + view.fullPath });
    } else {
      router.push('/');
    }
  }
}

function openMenu(tag: any, e: MouseEvent) {
  const menuMinWidth = 105;
  const offsetLeft = (document.getElementById('tags-view-container') as HTMLElement).getBoundingClientRect().left;
  const offsetWidth = (document.getElementById('tags-view-container') as HTMLElement).offsetWidth;
  const maxLeft = offsetWidth - menuMinWidth;
  const l = e.clientX - offsetLeft + 15;

  if (l > maxLeft) {
    left.value = maxLeft;
  } else {
    left.value = l;
  }

  top.value = e.clientY;
  visible.value = true;
  selectedTag.value = tag;
}

function closeMenu() {
  visible.value = false;
}

function handleScroll() {
  closeMenu();
}
</script>

<style lang="scss" scoped>
.tags-view-container {
  height: 34px;
  width: 100%;
  background: #fff;
  border-bottom: 1px solid #d8dce5;
  box-shadow:
    0 1px 3px 0 rgba(0, 0, 0, 0.12),
    0 0 3px 0 rgba(0, 0, 0, 0.04);
  .tags-view-wrapper {
    .tags-view-item {
      display: inline-block;
      position: relative;
      cursor: pointer;
      height: 26px;
      line-height: 26px;
      border: 1px solid #d8dce5;
      color: #495060;
      background: #fff;
      padding: 0 8px;
      font-size: 12px;
      margin-left: 5px;
      margin-top: 4px;
      &:first-of-type {
        margin-left: 15px;
      }
      &:last-of-type {
        margin-right: 15px;
      }
      &.active {
        background-color: #42b983;
        color: #fff;
        border-color: #42b983;
        &::before {
          content: '';
          background: #fff;
          display: inline-block;
          width: 8px;
          height: 8px;
          border-radius: 50%;
          position: relative;
          margin-right: 2px;
        }
      }
    }
  }
  .contextmenu {
    margin: 0;
    background: #fff;
    z-index: 3000;
    position: absolute;
    list-style-type: none;
    padding: 5px 0;
    border-radius: 4px;
    font-size: 12px;
    font-weight: 400;
    color: #333;
    box-shadow: 2px 2px 3px 0 rgba(0, 0, 0, 0.3);
    li {
      margin: 0;
      padding: 7px 16px;
      cursor: pointer;
      &:hover {
        background: #eee;
      }
    }
  }
}
</style>
