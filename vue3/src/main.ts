import { createApp, h, Transition, TransitionGroup } from 'vue';
import App from './App.vue';
import router from './router';
import { createPinia } from 'pinia';
// Element Plus
import 'element-plus/dist/index.css';
import { ElLoading } from 'element-plus';
import i18n from './lang';
import * as echarts from 'echarts';

// Core modules
import '@/permission';
import directive from '@/directive';
import plugins from '@/plugins';
import { setupNaive, setupDirectives as setupVBDirectives, setupCustomComponents, initFunction } from '@vb/plugins';

// Global components
import Pagination from '@/components/Pagination/index.vue';
import RightToolbar from '@/components/RightToolbar/index.vue';
import Editor from '@/components/Editor/index.vue';
import FileUpload from '@/components/FileUpload/index.vue';
import ImageUpload from '@/components/ImageUpload/index.vue';
import ImagePreview from '@/components/ImagePreview/index.vue';
import DictTag from '@/components/DictTag/index.vue';
import DictData from '@/components/DictData/index.vue';
import MonacoEditor from '@/components/MonacoEditor/index.vue';
import SvgIcon from '@/components/SvgIcon/index.vue';

// Global properties
import busEvent from '@/utils/busEvent';
import request, { download } from '@/utils/request';
import {
  parseTime,
  resetForm,
  clearValidate,
  addDateRange,
  selectDictLabel,
  selectDictLabels,
  handleTree,
} from '@/utils/ruoyi';
import mqttTool from '@/utils/mqttTool';
import { setAppInstance } from '@vb/utils/runtimeContext';
import { getDicts } from '@/api/system/dict/data';
import { getConfigKey } from '@/api/system/config';

// Styles
import '@/assets/styles/index.scss';
import '@/assets/styles/ruoyi.scss';
import '@/assets/styles/element-variables.scss';
import '@/assets/styles/common.scss';
import '@/assets/styles/jsplumb.scss';
// virtual:svg-icons-register 必须在 @/assets/icons 之前
import 'virtual:svg-icons-register';
import '@/assets/icons';

import dataV from '@kjgl77/datav-vue3';
import Contextmenu from '@imengyu/vue3-context-menu';
import '@imengyu/vue3-context-menu/lib/vue3-context-menu.css';
import Print from 'vue3-print-nb';
import 'animate.css/animate.min.css'; // 引入动画
import 'vue3-sketch-ruler/lib/style.css'; // 引入标尺
import { addCollection } from 'iconify-icon';
import uimIcons from '@iconify/json/json/uim.json';
import lineMdIcons from '@iconify/json/json/line-md.json';
import wiIcons from '@iconify/json/json/wi.json';

// 注册图标 - 移到 app 创建之前
addCollection(uimIcons);
addCollection(lineMdIcons);
addCollection(wiIcons);

// BigScreen global components
import ItemWrap from '@/views/fixedBigScreen/components/item-wrap/item-wrap.vue';
import Message from '@/views/fixedBigScreen/components/message/message.vue';
import Reacquire from '@/views/fixedBigScreen/components/reacquire/reacquire.vue';

const app = createApp(App);

// Register framework plugins
app.use(createPinia());

// visualBigScreen 组件注册依赖
setAppInstance(app as any);
setupNaive(app as any);
setupVBDirectives(app as any);
setupCustomComponents(app as any);
initFunction();
router.beforeEach((to, from, next) => {
  const activeEl = document.activeElement as HTMLElement | null;
  activeEl?.blur?.();
  next();
});
app.use(router);
app.use(i18n);
app.use(directive);
app.use(plugins);
app.use(dataV);
app.use(Contextmenu);
app.use(Print);

// Register Element Plus directives
app.directive('loading', ElLoading.directive);

// Vue3 compatibility for vue3-treeselect's legacy render helpers
app.component('transition', Transition);
app.component('transition-group', TransitionGroup);

// Register global components
app.component('Pagination', Pagination);
app.component('RightToolbar', RightToolbar);
app.component('Editor', Editor);
app.component('FileUpload', FileUpload);
app.component('ImageUpload', ImageUpload);
app.component('ImagePreview', ImagePreview);
app.component('DictTag', DictTag);
app.component('DictData', DictData);
app.component('MonacoEditor', MonacoEditor);
app.component('SvgIcon', SvgIcon);
app.component('svg-icon', SvgIcon);
app.component('ItemWrap', ItemWrap);
app.component('Message', Message);
app.component('Reacquire', Reacquire);

// Register global properties
app.config.globalProperties.$busEvent = busEvent;
app.config.globalProperties.download = download;
app.config.globalProperties.$request = request;
app.config.globalProperties.parseTime = parseTime;
app.config.globalProperties.resetForm = resetForm;
app.config.globalProperties.clearValidate = clearValidate;
app.config.globalProperties.addDateRange = addDateRange;
app.config.globalProperties.selectDictLabel = selectDictLabel;
app.config.globalProperties.selectDictLabels = selectDictLabels;
app.config.globalProperties.handleTree = handleTree;
app.config.globalProperties.$mqttTool = mqttTool;
app.config.globalProperties.getDicts = getDicts;
app.config.globalProperties.getConfigKey = getConfigKey;
app.config.globalProperties.$echarts = echarts;
app.config.globalProperties.$createElement = h;

app.mount('#app');
