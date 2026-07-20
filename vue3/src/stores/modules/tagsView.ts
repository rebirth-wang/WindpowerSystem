import { defineStore } from 'pinia';
import { ref } from 'vue';
import type { RouteLocationNormalized } from 'vue-router';

/** Lightweight route snapshot used by the tags-view bar */
export interface TagView {
  path: string;
  name?: string | symbol;
  fullPath?: string;
  title?: string;
  meta: {
    title?: string;
    icon?: string;
    noCache?: boolean;
    affix?: boolean;
    link?: string;
    [key: string]: any;
  };
  query?: Record<string, any>;
  params?: Record<string, any>;
  [key: string]: any;
}

export const useTagsViewStore = defineStore('tagsView', () => {
  const visitedViews = ref<TagView[]>([]);
  const cachedViews = ref<string[]>([]);
  const iframeViews = ref<TagView[]>([]);

  function addView(view: RouteLocationNormalized) {
    addVisitedView(view);
    addCachedView(view);
  }

  function addIframeView(view: RouteLocationNormalized) {
    if (iframeViews.value.some((v) => v.path === view.path)) return;
    iframeViews.value.push({
      ...view,
      title: (view.meta.title as string) || 'no-name',
    } as TagView);
  }

  function addVisitedView(view: RouteLocationNormalized) {
    if (visitedViews.value.some((v) => v.path === view.path)) return;
    visitedViews.value.push({
      ...view,
      title: (view.meta.title as string) || 'no-name',
    } as TagView);
  }

  function addCachedView(view: RouteLocationNormalized) {
    const viewName = view.name as string | undefined;
    if (!viewName || cachedViews.value.includes(viewName)) return;
    if (view.meta && !view.meta.noCache) {
      cachedViews.value.push(viewName);
    }
  }

  function delView(view: TagView) {
    return new Promise<{ visitedViews: TagView[]; cachedViews: string[] }>((resolve) => {
      delVisitedView(view);
      delCachedView(view);
      resolve({
        visitedViews: [...visitedViews.value],
        cachedViews: [...cachedViews.value],
      });
    });
  }

  function delVisitedView(view: TagView) {
    return new Promise<TagView[]>((resolve) => {
      for (const [i, v] of visitedViews.value.entries()) {
        if (v.path === view.path) {
          visitedViews.value.splice(i, 1);
          break;
        }
      }
      iframeViews.value = iframeViews.value.filter((item) => item.path !== view.path);
      resolve([...visitedViews.value]);
    });
  }

  function delIframeView(view: TagView) {
    return new Promise<TagView[]>((resolve) => {
      iframeViews.value = iframeViews.value.filter((item) => item.path !== view.path);
      resolve([...iframeViews.value]);
    });
  }

  function delCachedView(view: TagView) {
    return new Promise<string[]>((resolve) => {
      const index = cachedViews.value.indexOf(view.name as string);
      if (index > -1) {
        cachedViews.value.splice(index, 1);
      }
      resolve([...cachedViews.value]);
    });
  }

  function delOthersViews(view: TagView) {
    return new Promise<{ visitedViews: TagView[]; cachedViews: string[] }>((resolve) => {
      delOthersVisitedViews(view);
      delOthersCachedViews(view);
      resolve({
        visitedViews: [...visitedViews.value],
        cachedViews: [...cachedViews.value],
      });
    });
  }

  function delOthersVisitedViews(view: TagView) {
    return new Promise<TagView[]>((resolve) => {
      visitedViews.value = visitedViews.value.filter((v) => {
        return v.meta.affix || v.path === view.path;
      });
      iframeViews.value = iframeViews.value.filter((item) => item.path === view.path);
      resolve([...visitedViews.value]);
    });
  }

  function delOthersCachedViews(view: TagView) {
    return new Promise<string[]>((resolve) => {
      const index = cachedViews.value.indexOf(view.name as string);
      if (index > -1) {
        cachedViews.value = cachedViews.value.slice(index, index + 1);
      } else {
        cachedViews.value = [];
      }
      resolve([...cachedViews.value]);
    });
  }

  function delAllViews(_view?: TagView) {
    return new Promise<{ visitedViews: TagView[]; cachedViews: string[] }>((resolve) => {
      delAllVisitedViews(_view);
      delAllCachedViews(_view);
      resolve({
        visitedViews: [...visitedViews.value],
        cachedViews: [...cachedViews.value],
      });
    });
  }

  function delAllVisitedViews(_view?: TagView) {
    return new Promise<TagView[]>((resolve) => {
      const affixTags = visitedViews.value.filter((tag) => tag.meta.affix);
      visitedViews.value = affixTags;
      iframeViews.value = [];
      resolve([...visitedViews.value]);
    });
  }

  function delAllCachedViews(_view?: TagView) {
    return new Promise<string[]>((resolve) => {
      cachedViews.value = [];
      resolve([...cachedViews.value]);
    });
  }

  function delLeftTags(view: TagView) {
    return new Promise<TagView[]>((resolve) => {
      const index = visitedViews.value.findIndex((v) => v.path === view.path);
      if (index === -1) return;
      visitedViews.value = visitedViews.value.filter((item, idx) => {
        if (idx >= index || (item.meta && item.meta.affix)) return true;
        return false;
      });
      resolve([...visitedViews.value]);
    });
  }

  function delRightTags(view: TagView) {
    return new Promise<TagView[]>((resolve) => {
      const index = visitedViews.value.findIndex((v) => v.path === view.path);
      if (index === -1) return;
      visitedViews.value = visitedViews.value.filter((item, idx) => {
        if (idx <= index || (item.meta && item.meta.affix)) return true;
        return false;
      });
      resolve([...visitedViews.value]);
    });
  }

  function updateVisitedView(view: TagView) {
    for (let v of visitedViews.value) {
      if (v.path === view.path) {
        v = Object.assign(v, view);
        break;
      }
    }
  }

  return {
    visitedViews,
    cachedViews,
    iframeViews,
    addView,
    addIframeView,
    addVisitedView,
    addCachedView,
    delView,
    delVisitedView,
    delIframeView,
    delCachedView,
    delOthersViews,
    delOthersVisitedViews,
    delOthersCachedViews,
    delAllViews,
    delAllVisitedViews,
    delAllCachedViews,
    delLeftTags,
    delRightTags,
    updateVisitedView,
  };
});
