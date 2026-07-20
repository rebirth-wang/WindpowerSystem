import { defineStore } from 'pinia';
import { ref } from 'vue';

const defaultPageSetup = {
  name: '页面标题',
  details: '',
  isBack: true,
  titleHeight: 35,
  bgColor: 'rgba(241,243,249,1)',
  bgImg: '',
};

export const useDragEditorStore = defineStore('dragEditor', () => {
  const id = ref<any>(null);
  const rightcom = ref('');
  const pageSetup = ref<any>({ ...defaultPageSetup });
  const pageComponents = ref<any[]>([]);
  const currentproperties = ref<any>({ ...defaultPageSetup });
  const tableData = ref<any[]>([]);

  function setId(data: any) {
    id.value = data;
  }

  function setRightcom(data: string) {
    rightcom.value = data;
  }

  function setPageSetup(data: any) {
    pageSetup.value = data;
  }

  function setPageComponents(data: any[]) {
    pageComponents.value = data;
  }

  function setCurrentproperties(data: any) {
    currentproperties.value = null;
    currentproperties.value = data;
  }

  function setTableData(data: any[]) {
    tableData.value = data;
  }

  function resetPageComponents() {
    id.value = null;
    rightcom.value = '';
    pageSetup.value = { ...defaultPageSetup };
    currentproperties.value = null;
    pageComponents.value = [];
  }

  return {
    id,
    rightcom,
    pageSetup,
    pageComponents,
    currentproperties,
    tableData,
    setId,
    setRightcom,
    setPageSetup,
    setPageComponents,
    setCurrentproperties,
    setTableData,
    resetPageComponents,
  };
});
