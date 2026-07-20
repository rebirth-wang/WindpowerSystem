<template>
  <div class="view-component" :id="detail.identifier" ref="xcomp">
    <div v-if="statusText" class="view-component__placeholder" :class="{ 'is-error': loadError }">
      {{ statusText }}
    </div>
    <div v-show="false">{{ animateChange }}</div>
  </div>
</template>

<script lang="ts">
import * as VueRuntime from 'vue';
import { compile } from '@vue/compiler-dom';
import { createApp, defineComponent, getCurrentInstance } from 'vue';
import ElementPlus from 'element-plus';
import dataV from '@kjgl77/datav-vue3';
import { useRoute } from 'vue-router';
import { useTopoEditorStore } from '@/stores/modules/topoEditor';

import request from '@/utils/request';
import BaseView from '../View.vue';
import { getScadaRouteType, getRouteQueryString } from '@/utils/topo/topoUtil';
import { getComponent } from '@/api/scada/component';

export default {
  name: 'ViewComponent',
  extends: BaseView,
  setup() {
    const route = useRoute();
    const topoStore = useTopoEditorStore();
    const instance = getCurrentInstance();
    return { route, topoStore, parentAppContext: instance?.appContext };
  },
  data() {
    return {
      loading: false,
      loadError: '',
      dynamicApp: null as any,
      styleEl: null as HTMLStyleElement | null,
      data: {
        componentTemplate: '',
        componentStyle: '',
        componentScript: '',
        id: '',
      },
    };
  },
  computed: {
    mqttData() {
      return this.topoStore?.mqttData || {};
    },
    statusText() {
      if (this.loading) return '组件加载中...';
      if (this.loadError) return this.loadError;
      if (!this.detail?.dataBind?.id) return '请选择自定义组件';
      if (!this.data.componentTemplate) return '暂无组件内容';
      return '';
    },
    animateChange() {
      try {
        if (Object.keys(this.mqttData).length === 0) return '';

        const type = getScadaRouteType(this.route.query);
        let mqttNum = this.mqttData.serialNumber;
        let actionNum = this.detail?.dataAction?.serialNumber;

        if (type === 1) {
          actionNum = getRouteQueryString(this.route.query, 'serialNumber');
        }
        if (type === 2 && this.detail?.dataAction?.variableType && this.detail.dataAction.variableType !== 1) {
          actionNum = this.detail.dataAction.sceneModelDeviceId;
          mqttNum = this.mqttData.sceneModelDeviceId;
        }

        const action = this.detail?.dataAction || {};
        const messages = Array.isArray(this.mqttData.message) ? this.mqttData.message : [];
        if (
          actionNum &&
          this.isSameValue(actionNum, mqttNum) &&
          action.identifier &&
          action.paramJudge &&
          (action.paramJudgeData !== '' ||
            (action.paramJudgeDatarangeMin !== '' && action.paramJudgeDatarangeMax !== ''))
        ) {
          const item = messages.find((msg: any) => action.identifier == msg?.id);
          if (item) this.animatePlay(item.value);
        }
      } catch (e) {
        console.warn('ViewComponent animateChange failed', e);
      }
      return '';
    },
  },
  watch: {
    'detail.dataBind.id'() {
      this.initEchart();
    },
  },
  mounted() {
    this.initEchart();
    if (!this.editMode) {
      this.initAnimate();
    }
  },
  beforeUnmount() {
    this.disposeDynamicComponent();
  },
  methods: {
    initEchart() {
      this.loadError = '';
      const id = this.detail?.dataBind?.id;
      if (!id) {
        this.disposeDynamicComponent();
        return;
      }
      this.getComponentDataById(id);
    },
    async getComponentDataById(id: string | number) {
      this.loading = true;
      this.loadError = '';
      try {
        const res: any = await getComponent(id);
        if (res.code === 200 && res.data) {
          this.data = res.data;
          this.loadData();
        } else {
          this.loadError = res?.msg || '获取组件失败';
        }
      } catch (err: any) {
        console.error('获取组件失败', err);
        this.loadError = err?.message || '获取组件失败';
      } finally {
        this.loading = false;
      }
    },
    loadData() {
      try {
        this.disposeDynamicComponent();
        const template = this.data.componentTemplate;
        if (!template) return;

        this.mountStyle(this.data.componentStyle);
        const obj = this.createComponentOptions(template, this.data.componentScript);
        const componentDef = defineComponent({
          ...obj,
          data() {
            const originData = typeof obj.data === 'function' ? obj.data.call(this) || {} : {};
            return {
              ...originData,
              row: originData.row || {},
              rows: originData.rows || [],
              item: originData.item || {},
              data: originData.data || {},
              form: originData.form || {},
              scope: originData.scope || { row: {} },
              currentRow: originData.currentRow || {},
              record: originData.record || {},
            };
          },
          created() {
            this.row ||= {};
            this.rows ||= [];
            this.item ||= {};
            this.data ||= {};
            this.form ||= {};
            this.scope ||= { row: {} };
            this.currentRow ||= {};
            this.record ||= {};
          },
        });

        const container = this.$refs.xcomp as HTMLElement;
        if (!container) throw new Error('组件容器初始化失败');

        const mountHost = document.createElement('div');
        mountHost.className = 'view-component__mount';
        mountHost.style.width = '100%';
        mountHost.style.height = '100%';
        container.appendChild(mountHost);

        this.dynamicApp = createApp(componentDef);
        this.installDynamicAppContext(this.dynamicApp);
        this.dynamicApp.mount(mountHost);
        this.loadError = '';
      } catch (err: any) {
        console.error('自定义组件加载失败', err);
        this.disposeDynamicComponent();
        this.loadError = err?.message || '组件加载失败';
      }
    },
    createComponentOptions(template: string, script?: string) {
      const normalizedScript = this.normalizeComponentScript(script);
      let obj: any = {};
      try {
        obj = new Function(normalizedScript)() || {};
      } catch (err: any) {
        throw new Error(`组件脚本执行失败：${err?.message || err}`);
      }

      try {
        const { code } = compile(template, { mode: 'function' });
        obj.render = new Function('Vue', `${code}`)(VueRuntime);
        delete obj.template;
      } catch (err: any) {
        throw new Error(`组件模板编译失败：${err?.message || err}`);
      }

      if (!obj.methods) obj.methods = {};
      if (!obj.computed) obj.computed = {};
      if (!obj.data) obj.data = () => ({});
      return obj;
    },
    normalizeComponentScript(script?: string) {
      let nextScript = script || 'export default {}';
      nextScript = String(nextScript)
        .replace(/^\uFEFF/, '')
        .replace(/```[a-zA-Z]*\n?/g, '')
        .replace(/```/g, '')
        .replace(/<script[^>]*>/gi, '')
        .replace(/<\/script>/gi, '')
        .trim();
      nextScript = nextScript.replace(/^\s*export\s+default/, 'return');
      if (!/^\s*return\b/.test(nextScript)) {
        nextScript = `return (${nextScript});`;
      }
      return nextScript;
    },
    mountStyle(styleCss?: string) {
      if (!styleCss) return;
      this.styleEl = document.createElement('style');
      this.styleEl.setAttribute('data-scada-component-style', String(this.detail?.identifier || this.data.id || ''));
      this.styleEl.textContent = styleCss;
      document.head.appendChild(this.styleEl);
    },
    installDynamicAppContext(app: any) {
      const appContext: any = this.parentAppContext;

      app.use(ElementPlus);
      app.use(dataV);

      Object.assign(app.config.globalProperties, appContext?.config?.globalProperties || {}, {
        $request: request,
      });
      Object.assign(app._context.provides, appContext?.provides || {});

      Object.entries(appContext?.components || {}).forEach(([name, component]: [string, any]) => {
        app.component(name, component);
      });
      Object.entries(appContext?.directives || {}).forEach(([name, directive]: [string, any]) => {
        app.directive(name, directive);
      });
    },
    disposeDynamicComponent() {
      if (this.dynamicApp) {
        this.dynamicApp.unmount();
        this.dynamicApp = null;
      }
      if (this.styleEl?.parentNode) {
        this.styleEl.parentNode.removeChild(this.styleEl);
        this.styleEl = null;
      }

      const container = this.$refs.xcomp as HTMLElement;
      if (!container) return;
      Array.from(container.children).forEach((child: Element) => {
        if (!child.classList.contains('view-component__placeholder')) {
          child.remove();
        }
      });
    },
    initAnimate() {
      try {
        const value = !this.detail?.dataAction?.modelValue ? 0 : this.detail.dataAction.modelValue;
        this.animatePlay(value);
      } catch (e) {
        console.warn('ViewComponent initAnimate failed', e);
      }
    },
    animatePlay(val: any) {
      try {
        this.applyAnimationState(val);
      } catch (e) {
        console.warn('ViewComponent animatePlay failed', e);
      }
    },
  },
};
</script>

<style lang="scss">
.view-component {
  position: relative;
  width: 100%;
  height: 100%;
  box-sizing: border-box;
  overflow: hidden;
}

.view-component__mount {
  width: 100%;
  height: 100%;
  box-sizing: border-box;
  overflow: hidden;
}

.view-component__mount > * {
  max-width: 100%;
  box-sizing: border-box;
}

.view-component__placeholder {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 10px;
  color: #909399;
  font-size: 14px;
  text-align: center;
  background: rgba(255, 255, 255, 0.6);
  pointer-events: none;
}

.view-component__placeholder.is-error {
  color: #f56c6c;
}
</style>
