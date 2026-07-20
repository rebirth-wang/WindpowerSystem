import { useTagsViewStore } from '@/stores/modules/tagsView';
import router from '@/router';

export default {
  // 刷新当前tab页签
  refreshPage(obj?: any) {
    const tagsViewStore = useTagsViewStore();
    const { path, query, matched } = router.currentRoute.value;
    if (obj === undefined) {
      matched.forEach((m) => {
        if (m.components && m.components.default && (m.components.default as any).name) {
          if (!['Layout', 'ParentView'].includes((m.components.default as any).name)) {
            obj = { name: (m.components.default as any).name, path: path, query: query };
          }
        }
      });
    }
    return tagsViewStore.delCachedView(obj).then(() => {
      const { path, query } = obj;
      router.replace({
        path: '/redirect' + path,
        query: query,
      });
    });
  },
  // 关闭当前tab页签，打开新页签
  closeOpenPage(obj?: any) {
    const tagsViewStore = useTagsViewStore();
    tagsViewStore.delView(router.currentRoute.value);
    if (obj !== undefined) {
      return router.push(obj);
    }
  },
  // 关闭指定tab页签
  closePage(obj?: any) {
    const tagsViewStore = useTagsViewStore();
    if (obj === undefined) {
      return tagsViewStore.delView(router.currentRoute.value).then(({ lastPath }: any) => {
        return router.push(lastPath || '/');
      });
    }
    return tagsViewStore.delView(obj);
  },
  // 关闭所有tab页签
  closeAllPage() {
    const tagsViewStore = useTagsViewStore();
    return tagsViewStore.delAllViews();
  },
  // 关闭左侧tab页签
  closeLeftPage(obj?: any) {
    const tagsViewStore = useTagsViewStore();
    return tagsViewStore.delLeftTags(obj || router.currentRoute.value);
  },
  // 关闭右侧tab页签
  closeRightPage(obj?: any) {
    const tagsViewStore = useTagsViewStore();
    return tagsViewStore.delRightTags(obj || router.currentRoute.value);
  },
  // 关闭其他tab页签
  closeOtherPage(obj?: any) {
    const tagsViewStore = useTagsViewStore();
    return tagsViewStore.delOthersViews(obj || router.currentRoute.value);
  },
  // 添加tab页签
  openPage(title: string, url: string, params?: any) {
    const tagsViewStore = useTagsViewStore();
    const obj = { path: url, meta: { title: title } };
    tagsViewStore.addView(obj);
    return router.push({ path: url, query: params });
  },
  // 修改tab页签
  updatePage(obj: any) {
    const tagsViewStore = useTagsViewStore();
    return tagsViewStore.updateVisitedView(obj);
  },
};
