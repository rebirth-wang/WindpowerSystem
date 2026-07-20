<template>
  <div class="echart-detail-wrap">
    <el-card class="top-card" :body-style="{ padding: '26px 20px' }">
      <div class="title-wrap">
        <el-button class="top-button" type="info" size="small" @click="handleGoBack()">
          <i class="el-icon-arrow-left"></i>
          {{ $t('product.product-edit.473153-44') }}
        </el-button>
        <span class="info-item">{{ $t('scada.echart.209302-8') }}：{{ form.echartName }}</span>
        <span class="info-item">
          {{ $t('scada.echart.209302-9') }}：
          <span style="color: #486ff2">{{ form.echartType }}</span>
        </span>
        <span class="info-item">
          {{ $t('scada.echart.209302-10') }}： {{ parseTime(form.updateTime, '{y}-{m}-{d}') }}
        </span>
      </div>
    </el-card>

    <el-row :gutter="10">
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <div class="card-header__title">
                <span>{{ $t('scada.echart.209302-3') }}&nbsp;-&nbsp;</span>
                <a href="https://www.isqqw.com/" target="_blank" class="card-header__link">
                  {{ $t('scada.echart.209302-4') }}
                </a>
              </div>
              <div class="card-header__tools">
                <el-button class="item-btn" type="text" @click="handleSubmitForm" v-hasPermi="['scada:echart:edit']">
                  <svg-icon icon-class="save" class="item-btn__icon" />
                  {{ $t('save') }}
                </el-button>
                <el-button class="item-btn" type="text" :icon="Refresh" @click="loadEchartDatas">
                  {{ $t('scada.component.302923-0') }}
                </el-button>
              </div>
            </div>
          </template>
          <monaco-editor ref="editor" height="75vh" @change="handleEditorChange"></monaco-editor>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span class="card-header__title">{{ $t('scada.echart.209302-5') }}</span>
              <div class="card-header__tools">
                <el-button class="item-btn" type="text" :icon="Download" @click="handleDownloadImage">
                  {{ $t('scada.echart.209302-6') }}
                </el-button>
              </div>
            </div>
          </template>
          <div ref="echartRef" style="height: 75vh; width: 100%"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, getCurrentInstance } from 'vue';
import { useRoute } from 'vue-router';
import html2canvas from 'html2canvas';
import { parseTime } from '@/utils/ruoyi';
import { getEchart, updateEchart } from '@/api/scada/echart';
import { getRouteQueryString } from '@/utils/topo/topoUtil';
import { Refresh, Download } from '@element-plus/icons-vue';

const { proxy } = getCurrentInstance() as any;
const route = useRoute();

const editor = ref<any>(null);
const echartRef = ref<any>(null);

const form = ref({ echartData: '' } as any);
let echart: any = null;

function getDetail() {
  getEchart(getRouteQueryString(route.query, 'id')).then((res: any) => {
    if (res.code === 200) {
      form.value = res.data;
      editor.value.setValue(form.value.echartData);
      loadEchartDatas();
    }
  });
}

function loadEchartDatas() {
  if (echart) echart.dispose();
  let funStr = getFun(form.value.echartData);
  let fun = eval('(' + funStr + ')');
  let option = fun((proxy as any).$echarts);
  let view = echartRef.value;
  echart = (proxy as any).$echarts.init(view, 'light');
  echart.setOption(option);
}

function getFun(optionStr: string) {
  let body = typeof optionStr === 'string' ? optionStr : '';
  body = body.replace(/\b(let|const|var)\s+option\s*=/g, 'option =');
  body = body.replace(/\b(let|const|var)\s+option\s*;/g, '');
  return (
    'function (echarts) {\n' +
    'var option;\n' +
    body +
    '\nreturn option;\n' +
    '}'
  );
}

function handleEditorChange(data: string) {
  form.value.echartData = data;
}

function handleSubmitForm() {
  let canvasBox = echartRef.value;
  proxy.$modal.loading(proxy.$t('scada.component.302923-1'));
  html2canvas(canvasBox).then((canvas: HTMLCanvasElement) => {
    form.value.base64 = canvas.toDataURL('image/png');
    updateEchart(form.value).then((res: any) => {
      if (res.code === 200) {
        proxy.$modal.msgSuccess(proxy.$t('updateSuccess'));
      }
      proxy.$modal.closeLoading();
    });
  });
}

function handleDownloadImage() {
  let canvasBox = echartRef.value;
  html2canvas(canvasBox).then((canvas: HTMLCanvasElement) => {
    var img = canvas.toDataURL('image/png').replace('image/png', 'image/octet-stream');
    var creatIMg = document.createElement('a');
    creatIMg.download = `${form.value.echartName}.png`;
    creatIMg.href = img;
    document.body.appendChild(creatIMg);
    creatIMg.click();
    creatIMg.remove();
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
.echart-detail-wrap {
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

  .card-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 12px;

    &__title {
      flex: 1;
      min-width: 0;
    }

    &__link {
      color: #486ff2;
    }

    &__tools {
      flex-shrink: 0;
      display: flex;
      align-items: center;
      gap: 10px;
    }
  }

  .item-btn {
    padding: 3px 0;
    color: #606266;

    &:hover,
    &:focus {
      color: #486ff2;
    }

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
</style>
