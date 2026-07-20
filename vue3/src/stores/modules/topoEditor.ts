import { defineStore } from 'pinia';
import { ref, reactive } from 'vue';

// UID 工具：生成唯一标识
function uid(): string {
  return Math.random().toString(36).substr(2, 9) + Date.now().toString(36);
}

function deepCopy<T>(obj: T): T {
  return JSON.parse(JSON.stringify(obj));
}

function createDefaultTopoData() {
  return {
    pcConfig: [
      {
        name: '--',
        layer: {
          backColor: '',
          foreColor: '',
          backgroundImage: '',
          widthHeightRatio: '',
          width: 1600,
          height: 900,
        },
        components: [] as any[],
        httpPublicSetting: [] as any[],
      },
    ],
    mdConfig: [
      {
        name: '--',
        layer: {
          backColor: '',
          foreColor: '',
          backgroundImage: '',
          widthHeightRatio: '',
          width: 375,
          height: 667,
        },
        components: [] as any[],
        httpPublicSetting: [] as any[],
      },
    ],
    pcChecked: true,
    mdChecked: true,
  };
}

export const useTopoEditorStore = defineStore('topoEditor', () => {
  const topoData = ref<any>(createDefaultTopoData());
  const selectedIsLayer = ref(true);
  const selectedComponent = ref<any>(null);
  const selectedComponents = ref<string[]>([]);
  const selectedComponentMap = reactive<Record<string, any>>({});
  const copySrcItems = ref<any[]>([]);
  const copyCount = ref(0);
  const undoStack = ref<any[]>([]);
  const redoStack = ref<any[]>([]);
  const mqttData = ref<Record<string, any>>({});
  const historyData = ref<any[]>([]);
  const deviceStatus = ref<Record<string, any>>({});
  const selected = ref('pcConfig');
  const pageIndex = ref(0);

  // -------- getters --------
  function getTopoConfigData() {
    return topoData.value;
  }
  function getSelectedComponentsList() {
    return selectedComponents.value;
  }

  // -------- actions --------
  function loadDefaultTopoData(jsonData: any) {
    topoData.value = jsonData;
  }

  function execute(command: any) {
    const components = topoData.value[selected.value][pageIndex.value].components;
    switch (command.op) {
      case 'add': {
        const component = command.component;
        component.identifier = uid();
        component.name = component.type + components.length;
        component.style.visible = true;
        component.style.transform = 0;
        component.style.borderWidth = component.style.borderWidth || 0;
        component.style.borderStyle = component.style.borderStyle || 'solid';
        component.style.borderColor = component.style.borderColor || '#ccccccff';
        components.push(component);
        break;
      }
      case 'del': {
        const deleted: { index: number; component: any }[] = [];
        for (let i = 0; i < components.length; i++) {
          if (selectedComponentMap[components[i].identifier] !== undefined) {
            deleted.push({ index: i, component: deepCopy(components[i]) });
          }
        }
        deleted.sort((a, b) => b.index - a.index);
        deleted.forEach((d) => components.splice(d.index, 1));
        command.deletedComponents = deleted;
        break;
      }
      case 'move': {
        const { dx, dy } = command;
        for (const key in command.items) {
          const comp = command.items[key];
          comp.style.position.x += dx;
          comp.style.position.y += dy;
        }
        break;
      }
      case 'copy-add':
        clearSelectedComponent();
        handleCopy(command, { crossPage: false });
        break;
      case 'cross-page-copy-add':
        clearSelectedComponent();
        handleCopy(command, { crossPage: true });
        break;
      case 'rotate': {
        const angle = command.newAngle;
        for (const key in command.initialStates) {
          const comp = selectedComponentMap[key];
          if (comp) {
            comp.style.transform = angle;
            comp.style.transformType = `rotate(${angle}deg)`;
          }
        }
        break;
      }
      default:
        console.warn('不支持的命令.');
        break;
    }
    undoStack.value.push(command);
  }

  function handleCopy(command: any, { crossPage = false } = {}) {
    const components = topoData.value[selected.value][pageIndex.value].components;
    const isGroupCopy = command.items.some(
      (item: any) => Array.isArray(item.identifiers) && item.identifiers.length > 1
    );
    const idMap: Record<string, string> = {};
    if (isGroupCopy) {
      command.items.forEach((item: any) => {
        idMap[item.identifier] = uid();
      });
    }
    command.items.forEach((t: any) => {
      const component = deepCopy(t);
      if (crossPage) delete component._sourcePage;
      component.identifier = isGroupCopy ? idMap[t.identifier] : uid();
      if (isGroupCopy && Array.isArray(component.identifiers)) {
        component.identifiers = component.identifiers.map((oldId: string) => idMap[oldId]).filter(Boolean);
      } else {
        delete component.identifiers;
      }
      component.name = component.type + components.length;
      component.style.visible = true;
      component.style.position.x += 25;
      component.style.position.y += 25;
      components.push(component);
      addSelectedComponent(component);
      increaseCopyCount();
    });
  }

  function undo() {
    const command = undoStack.value.pop();
    if (!command) return;
    const components = topoData.value[selected.value][pageIndex.value].components;
    switch (command.op) {
      case 'add':
        for (let i = components.length - 1; i >= 0; i--) {
          if (components[i].identifier === command.component.identifier) {
            components.splice(i, 1);
            break;
          }
        }
        break;
      case 'del':
        if (command.deletedComponents?.length) {
          command.deletedComponents
            .slice()
            .sort((a: any, b: any) => a.index - b.index)
            .forEach((item: any) => {
              components.splice(item.index, 0, item.component);
            });
        }
        break;
      case 'move': {
        const { dx, dy } = command;
        for (const key in selectedComponentMap) {
          selectedComponentMap[key].style.position.x -= dx;
          selectedComponentMap[key].style.position.y -= dy;
        }
        break;
      }
      case 'rotate':
        if (command.initialStates) {
          for (const id in command.initialStates) {
            const comp = components.find((c: any) => c.identifier === id);
            if (comp) {
              comp.style.transform = command.initialStates[id].style.transform;
              comp.style.transformType = command.initialStates[id].style.transformType;
            }
          }
        }
        break;
      default:
        break;
    }
    redoStack.value.push(command);
  }

  function redo() {
    const command = redoStack.value.pop();
    if (!command) return;
    if (command.op === 'rotate') {
      const angle = command.newAngle;
      const components = topoData.value[selected.value][pageIndex.value].components;
      for (const key in command.initialStates) {
        const comp = components.find((c: any) => c.identifier === key);
        if (comp) {
          comp.style.transform = angle;
          comp.style.transformType = `rotate(${angle}deg)`;
        }
      }
      undoStack.value.push(command);
    } else {
      execute(command);
    }
  }

  function setSelectedComponent(component: any) {
    if (!component.identifier) component.identifier = uid();
    selectedComponents.value = [component.identifier];
    Object.keys(selectedComponentMap).forEach((k) => delete selectedComponentMap[k]);
    selectedComponentMap[component.identifier] = component;
    selectedComponent.value = component;
  }

  function addSelectedComponent(component: any) {
    if (!component.identifier) component.identifier = uid();
    if (selectedComponentMap[component.identifier]) return;
    selectedComponents.value.push(component.identifier);
    selectedComponentMap[component.identifier] = component;
    selectedComponent.value = component;
  }

  function removeSelectedComponent(component: any) {
    if (!component.identifier) return;
    const index = selectedComponents.value.indexOf(component.identifier);
    if (index > -1) selectedComponents.value.splice(index, 1);
    delete selectedComponentMap[component.identifier];
    if (selectedComponent.value?.identifier === component.identifier) {
      selectedComponent.value = null;
    }
    if (selectedComponents.value.length === 1) {
      selectedComponent.value = selectedComponentMap[selectedComponents.value[0]];
    }
  }

  function clearSelectedComponent() {
    selectedComponents.value = [];
    Object.keys(selectedComponentMap).forEach((k) => delete selectedComponentMap[k]);
    selectedComponent.value = null;
  }

  function setLayerSelected(val: boolean) {
    selectedIsLayer.value = val;
  }

  function setCopySrcItems(items: any[]) {
    copySrcItems.value = items;
    copyCount.value = 0;
  }

  function increaseCopyCount() {
    copyCount.value++;
  }

  function setMqttData(data: Record<string, any>) {
    mqttData.value = data;
  }

  function setDeviceStatus(data: Record<string, any>) {
    deviceStatus.value = data;
  }

  function clearUndoRedoStack() {
    undoStack.value = [];
    redoStack.value = [];
  }

  function setSelected(val: string) {
    selected.value = val;
  }

  function resetSelected() {
    const savedCopySrcItems = copySrcItems.value;
    const savedCopyCount = copyCount.value;
    selectedIsLayer.value = true;
    selectedComponent.value = null;
    selectedComponents.value = [];
    Object.keys(selectedComponentMap).forEach((k) => delete selectedComponentMap[k]);
    undoStack.value = [];
    redoStack.value = [];
    mqttData.value = {};
    historyData.value = [];
    deviceStatus.value = {};
    copySrcItems.value = savedCopySrcItems;
    copyCount.value = savedCopyCount;
  }

  function setPageIndex(index: number) {
    pageIndex.value = index;
  }

  function setHttpPublicSetting(data: any[]) {
    topoData.value[selected.value][pageIndex.value].httpPublicSetting = data;
  }

  return {
    topoData,
    selectedIsLayer,
    selectedComponent,
    selectedComponents,
    selectedComponentMap,
    copySrcItems,
    copyCount,
    undoStack,
    redoStack,
    mqttData,
    historyData,
    deviceStatus,
    selected,
    pageIndex,
    getTopoConfigData,
    getSelectedComponentsList,
    loadDefaultTopoData,
    execute,
    undo,
    redo,
    setSelectedComponent,
    addSelectedComponent,
    removeSelectedComponent,
    clearSelectedComponent,
    setLayerSelected,
    setCopySrcItems,
    increaseCopyCount,
    setMqttData,
    setDeviceStatus,
    clearUndoRedoStack,
    setSelected,
    resetSelected,
    setPageIndex,
    setHttpPublicSetting,
  };
});
