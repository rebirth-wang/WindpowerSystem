import { ref, reactive } from 'vue';
import i18n from '@/lang';
import router from '@/router';
import { httpErrorHandle, getUUID } from '@vb/utils';
import { ElMessage, ElMessageBox } from 'element-plus';
import { projectListApi, deleteProjectApi, changeProjectReleaseApi, createProjectApi } from '@vb/api/path/project.api';
import { Chartype, ChartList } from '../../../index.d';
import { ResultEnum } from '@vb/enums/httpEnum';

const PROJECT_ITEMS_MESSAGE_CLASS = 'go-items-static-message';

// 数据初始化
export const useDataListInit = () => {
  const loading = ref(true);

  const queryParams = reactive({
    keyword: '',
  });

  const paginat = reactive({
    // 当前页数
    page: 1,
    // 每页值
    limit: 12,
    // 总数
    count: 10,
  });

  const list = ref<ChartList>([]);
  const releaseDialogFocus = () => {
    const activeEl = document.activeElement as HTMLElement | null;
    activeEl?.blur?.();
  };
  const showItemsMessage = (type: 'success' | 'error', message: string) => {
    ElMessage({
      type,
      message,
      customClass: PROJECT_ITEMS_MESSAGE_CLASS,
    });
  };

  // 数据请求
  const fetchList = async () => {
    loading.value = true;
    const requestParams: Record<string, any> = {
      pageNum: paginat.page,
      pageSize: paginat.limit,
    };
    if (queryParams.keyword?.trim()) {
      requestParams.projectName = queryParams.keyword;
    }
    const res = (await projectListApi(requestParams)) as any;
    if (res.rows) {
      const { total } = res;
      paginat.count = total;

      list.value = res.rows.map((e: any) => {
        const { id, projectName, state, createTime, updateTime, indexImage, createBy } = e;
        return {
          id: id,
          title: projectName,
          createId: createBy,
          time: createTime,
          updateTime: updateTime,
          image: `${import.meta.env.VITE_APP_BASE_API}${indexImage}`,
          release: state !== 0,
        };
      });
      setTimeout(() => {
        loading.value = false;
      }, 500);
      return;
    }
    httpErrorHandle();
  };

  // 修改页数
  const changePage = (_page: number) => {
    paginat.page = _page;
    fetchList();
  };

  // 修改大小
  const changeSize = (_size: number) => {
    paginat.limit = _size;
    fetchList();
  };

  // 搜索
  const handleSearch = () => {
    fetchList();
  };

  // 重置
  const handleReset = () => {
    queryParams.keyword = '';
    paginat.page = 1;
    fetchList();
  };

  // 新建项目（与子项目一致：携带默认字段后创建，再进入编辑页）
  const handleAdd = async () => {
    try {
      const res = (await createProjectApi({
        indexImage: null,
        projectName: getUUID(),
        remarks: null,
      })) as any;
      if (res && res.code === ResultEnum.SUCCESS) {
        const id = res.data?.id || res.data || res.id;
        if (id) {
          const routeUrl = router.resolve(`/visual/chart/home/${id}`);
          window.open(routeUrl.href, '_blank');
          return;
        }
      }
      httpErrorHandle(i18n.global.t('visualBigScreen.management-674210-22'));
    } catch {
      httpErrorHandle(i18n.global.t('visualBigScreen.management-674210-22'));
    }
  };

  // 删除处理
  const deleteHandle = async (cardData: Chartype | Array<string | number>) => {
    const deleteIds = Array.isArray(cardData) ? cardData : [cardData.id];
    releaseDialogFocus();
    try {
      await ElMessageBox.confirm(i18n.global.t('visualBigScreen.management-674210-14'), i18n.global.t('tips'), {
        confirmButtonText: i18n.global.t('visualBigScreen.management-674210-110'),
        cancelButtonText: i18n.global.t('visualBigScreen.management-674210-111'),
        type: 'warning',
        autofocus: false,
      });

      const res = (await deleteProjectApi(deleteIds.join(','))) as any;
      if (res && res.code === ResultEnum.SUCCESS) {
        showItemsMessage('success', i18n.global.t('visualBigScreen.management-674210-10'));
        fetchList();
        return true;
      }
      showItemsMessage('error', i18n.global.t('http.error_message'));
    } catch {
      // 用户取消删除
    }
    return false;
  };

  // 发布处理
  const releaseHandle = async (cardData: Chartype) => {
    const { id, release } = cardData;
    const res = await changeProjectReleaseApi({
      id: id,
      // [0未发布, 1发布]
      state: !release ? 1 : 0,
    });
    if (res && res.code === ResultEnum.SUCCESS) {
      list.value = [];
      fetchList();
      // 发布 -> 未发布
      if (release) {
        showItemsMessage('success', i18n.global.t('visualBigScreen.management-674210-12'));
        return;
      }
      // 未发布 -> 发布
      showItemsMessage('success', i18n.global.t('visualBigScreen.management-674210-11'));
      return;
    }
    showItemsMessage('error', i18n.global.t('http.error_message'));
  };

  // 立即请求
  fetchList();

  return {
    loading,
    queryParams,
    paginat,
    list,
    fetchList,
    handleSearch,
    handleReset,
    handleAdd,
    releaseHandle,
    changeSize,
    changePage,
    deleteHandle,
  };
};
