<template>
  <div class="component-detail-wrap">
    <el-card class="top-card" :body-style="{ padding: '26px 20px' }">
      <div class="title-wrap">
        <el-button class="top-button" type="info" size="small" :icon="ArrowLeft" @click="handleGoBack()">
          {{ $t('product.product-edit.473153-44') }}
        </el-button>
        <span class="info-item">{{ $t('scada.echart.209302-8') }}：{{ form.componentName }}</span>
        <span class="info-item">
          {{ $t('scada.echart.209302-9') }}：
          <span style="color: #486ff2">
            {{ form.isShare === 0 ? $t('scada.component.302923-2') : $t('scada.component.302923-3') }}
          </span>
        </span>
        <span class="info-item">
          {{ $t('scada.echart.209302-10') }}： {{ parseTime(form.updateTime, '{y}-{m}-{d}') }}
        </span>
      </div>
    </el-card>

    <el-row :gutter="10">
      <el-col :span="12">
        <el-card class="main-card" :body-style="{ padding: '0px' }">
          <el-tabs class="tabs-wrap" type="border-card">
            <el-tab-pane label="Template">
              <monaco-editor
                ref="templateEditor"
                height="77vh"
                language="html"
                @change="handleTemplateEditorChange"
              ></monaco-editor>
            </el-tab-pane>
            <el-tab-pane label="CSS">
              <monaco-editor
                ref="styleEditor"
                height="77vh"
                language="css"
                @change="handleStyleEditorChange"
              ></monaco-editor>
            </el-tab-pane>
            <el-tab-pane label="Script">
              <monaco-editor
                ref="scriptEditor"
                language="javascript"
                height="77vh"
                @change="handleScriptEditorChange"
              ></monaco-editor>
            </el-tab-pane>
          </el-tabs>
          <div class="tools-wrap">
            <el-button class="item-btn" type="text" @click="handleSubmitForm" v-hasPermi="['scada:component:edit']">
              <svg-icon icon-class="save" class="item-btn__icon" />
              {{ $t('save') }}
            </el-button>
            <el-button class="item-btn" type="text" :icon="Refresh" @click="handleRun">
              {{ $t('scada.component.302923-0') }}
            </el-button>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="main-card">
          <template #header>
            <span>{{ $t('preview') }}</span>
          </template>
          <div ref="componentResult" style="height: 77vh; width: 100%; overflow-y: auto"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import * as VueRuntime from 'vue';
import { ref, onMounted, onBeforeUnmount, getCurrentInstance, createApp, defineComponent } from 'vue';
import { compile } from '@vue/compiler-dom';
import { useRoute } from 'vue-router';
import html2canvas from 'html2canvas';
import { getComponent, updateComponent } from '@/api/scada/component';
import { parseTime } from '@/utils/ruoyi';
import { ArrowLeft, Refresh } from '@element-plus/icons-vue';
import { getRouteQueryString } from '@/utils/topo/topoUtil';

const instance = getCurrentInstance() as any;
const { proxy } = instance;
const route = useRoute();

const templateEditor = ref<any>(null);
const styleEditor = ref<any>(null);
const scriptEditor = ref<any>(null);
const componentResult = ref<any>(null);
let previewApp: any = null;
let previewStyleEl: HTMLStyleElement | null = null;

const form = ref({
  componentTemplate: '',
  componentStyle: '',
  componentScript: '',
} as any);

function getDetail() {
  getComponent(getRouteQueryString(route.query, 'id')).then((res: any) => {
    if (res.code === 200) {
      form.value = res.data;
      templateEditor.value.setValue(form.value.componentTemplate);
      styleEditor.value.setValue(form.value.componentStyle);
      scriptEditor.value.setValue(form.value.componentScript);
      loadData();
    }
  });
}

function loadData() {
  const template = form.value.componentTemplate;
  console.log('template', template);
  if (!template || !componentResult.value) return;

  if (previewApp) {
    previewApp.unmount();
    previewApp = null;
  }
  if (previewStyleEl && previewStyleEl.parentNode) {
    previewStyleEl.parentNode.removeChild(previewStyleEl);
    previewStyleEl = null;
  }
  componentResult.value.innerHTML = '';

  const styleCss = form.value.componentStyle || '';
  previewStyleEl = document.createElement('style');
  previewStyleEl.setAttribute('data-preview-style', 'component-detail');
  previewStyleEl.textContent = styleCss;
  componentResult.value.appendChild(previewStyleEl);

  let script = form.value.componentScript || 'export default {}';
  // 兼容粘贴进来的 ```js 代码块、<script> 包裹和 BOM
  script = script
    .replace(/^\uFEFF/, '')
    .replace(/```[a-zA-Z]*\n?/g, '')
    .replace(/```/g, '')
    .replace(/<script[^>]*>/gi, '')
    .replace(/<\/script>/gi, '')
    .trim();

  // 将 export default 转成 return，便于 new Function 执行
  script = script.replace(/^\s*export\s+default/, 'return');
  if (!/^\s*return\b/.test(script)) {
    script = `return (${script});`;
  }

  let obj: any = {};
  try {
    obj = new Function(script)() || {};
  } catch (e: any) {
    console.error('component script eval error:', e, '\nscript=>\n', script);
    componentResult.value.innerHTML += `<div style="color:#f56c6c;padding:12px;white-space:pre-wrap;">${proxy.$t('scada.component.302923-17', [String(e?.message ?? e)])}</div>`;
    return;
  }

  try {
    const { code } = compile(template, { mode: 'function' });
    obj.render = new Function('Vue', `${code}`)(VueRuntime);
  } catch (e) {
    console.error('template compile error:', e);
    componentResult.value.innerHTML += `<div style="color:#f56c6c;padding:12px;">${proxy.$t('scada.component.302923-18')}</div>`;
    return;
  }
  delete obj.template;

  const ComponentDef = defineComponent(obj);

  const mountHost = document.createElement('div');
  mountHost.setAttribute('id', 'component-result');
  mountHost.style.width = '100%';
  mountHost.style.minHeight = '200px';
  componentResult.value.appendChild(mountHost);

  try {
    previewApp = createApp(ComponentDef);
    previewApp.config.globalProperties = { ...instance?.appContext?.config?.globalProperties };
    previewApp.mount(mountHost);
  } catch (e) {
    console.error('component render error:', e);
    componentResult.value.innerHTML += `<div style="color:#f56c6c;padding:12px;">${proxy.$t('scada.component.302923-19')}</div>`;
  }
}

function handleTemplateEditorChange(data: string) {
  form.value.componentTemplate = data;
}

function handleStyleEditorChange(data: string) {
  form.value.componentStyle = data;
}

function handleScriptEditorChange(data: string) {
  form.value.componentScript = data;
}

function handleRun() {
  loadData();
}

function handleSubmitForm() {
  proxy.$modal.loading(proxy.$t('scada.component.302923-1'));
  let canvasBox = componentResult.value;
  html2canvas(canvasBox).then((canvas: HTMLCanvasElement) => {
    form.value.base64 = canvas.toDataURL('image/png');
    updateComponent(form.value).then((res: any) => {
      if (res.code === 200) {
        proxy.$modal.msgSuccess(proxy.$t('updateSuccess'));
      }
      proxy.$modal.closeLoading();
    });
  });
}

function handleGoBack() {
  history.go(-1);
}

onMounted(() => {
  getDetail();
});
</script>

<style lang="scss" scoped>
.component-detail-wrap {
  padding: 20px;

  .top-card {
    margin-bottom: 10px;

    .title-wrap {
      display: flex;
      flex-direction: row;
      align-items: center;

      .top-button {
        height: 22px;
        color: #909399;
        background: #f4f5f7;
        padding: 0px 8px;
        border: none;
      }

      .info-item {
        font-weight: normal;
        font-size: 14px;
        color: #333333;
        line-height: 20px;
        margin-left: 36px;
      }
    }
  }

  .main-card {
    position: relative;

    .tabs-wrap {
      background: #ffffff;
      border: none;
      box-shadow: none;

      :deep(.el-tabs__header) {
        background-color: unset;
        border-bottom: 1px solid #e6ebf5;
      }

      :deep(.el-tabs__item) {
        height: 59px;
        line-height: 59px;
        font-size: 16px;
        font-weight: 400;
      }

      :deep(.el-tabs__item.is-active) {
        background-color: unset;
        border-right-color: transparent;
        border-left-color: transparent;
      }

      :deep(.el-tabs__content) {
        padding: 20px;
      }
    }

    .tools-wrap {
      position: absolute;
      right: 0;
      top: 10px;
      padding: 0 10px;
      display: flex;
      align-items: center;
      gap: 4px;

      .item-btn {
        padding: 0 5px;
        height: 40px;
        color: #606266;

        &:hover,
        &:focus {
          color: #486ff2;
        }

        /* 与 el-button :icon 默认间距（6px）对齐 */
        .item-btn__icon {
          width: 14px !important;
          height: 14px !important;
          margin-right: 6px;
          vertical-align: -0.15em;
          color: inherit;
        }

        :deep(.el-icon) {
          font-size: 14px;
          color: inherit;
        }
      }
    }
  }
}
</style>
