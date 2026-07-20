import { ref } from 'vue';
import { ChartEnum } from '@vb/enums/pageEnum';
import { fetchPathByName, routerTurnByPath, openNewWindow } from '@vb/utils';
import { Chartype } from '../../../index.d';
export const useModalDataInit = () => {
  const modalShow = ref<boolean>(false);
  const modalData = ref<Chartype | null>(null);

  // 关闭 modal
  const closeModal = () => {
    modalShow.value = false;
    modalData.value = null;
  };

  // 缩放处理
  const resizeHandle = (cardData: Chartype) => {
    if (!cardData) return;
    modalShow.value = true;
    modalData.value = cardData;
  };

  // 编辑处理
  const editHandle = (cardData: Chartype) => {
    if (!cardData) return;
    const path = fetchPathByName(ChartEnum.CHART_HOME_NAME, 'href');
    routerTurnByPath(path, [cardData.id], undefined, true);
  };

  // 预览处理
  const previewHandle = (cardData: Chartype) => {
    if (!cardData?.id) return;
    openNewWindow(`/visual/chart/preview/${cardData.id}`);
  };

  return {
    modalData,
    modalShow,
    closeModal,
    resizeHandle,
    editHandle,
    previewHandle,
  };
};
