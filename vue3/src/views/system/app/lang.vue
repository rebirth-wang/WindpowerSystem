<template>
  <div class="system-app-lang">
    <el-card v-show="showSearch" class="search-card">
      <el-form
        @submit.prevent
        :model="queryParams"
        ref="queryRef"
        :inline="true"
        label-width="46px"
        class="search-form"
      >
        <el-form-item prop="langName">
          <el-input
            v-model="queryParams.langName"
            :placeholder="$t('app.lang.755172-14')"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item prop="country">
          <el-input
            v-model="queryParams.country"
            :placeholder="$t('app.lang.755172-12')"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <div style="float: right">
          <el-button type="primary" :icon="Search" @click="handleQuery">{{ $t('search') }}</el-button>
          <el-button :icon="Refresh" @click="resetQuery">{{ $t('reset') }}</el-button>
        </div>
      </el-form>
    </el-card>

    <el-card>
      <el-row :gutter="10" style="margin-bottom: 16px">
        <el-col :span="1.5">
          <el-button type="primary" plain :icon="Plus" @click="handleAdd" v-hasPermi="['app:language:add']">
            {{ $t('add') }}
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            plain
            :icon="Delete"
            :disabled="multiple"
            @click="handleDelete"
            v-hasPermi="['app:language:remove']"
          >
            {{ $t('del') }}
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button v-hasPermi="['app:language:export']" plain :loading="exportLoading" @click="handleExport">
            {{ $t('app.start.891644-43') }}
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-upload
            v-hasRole="['admin']"
            :show-file-list="false"
            ref="uploadRef"
            action=""
            :http-request="handleImport"
          >
            <el-button :loading="importLoading" plain>
              {{ $t('app.start.891644-44') }}
            </el-button>
          </el-upload>
        </el-col>
        <el-col :span="1.5">
          <el-dropdown v-if="checkRole(['admin'])" @command="(val: string) => handleExportBackendMenu(val, true)">
            <el-button plain>
              {{ $t('app.start.891644-47') }}
              <el-icon class="el-icon--right"><ArrowDown /></el-icon>
            </el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item
                  v-for="dict in international_configuration_template"
                  :key="dict.value"
                  :command="dict.value"
                >
                  {{ dict.label }}
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </el-col>
        <el-col :span="1.5">
          <el-dropdown v-if="checkRole(['admin'])" @command="(val: string) => handleExportBackendMenu(val, false)">
            <el-button plain>
              {{ $t('app.start.891644-45') }}
              <el-icon class="el-icon--right"><ArrowDown /></el-icon>
            </el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item
                  v-for="dict in international_configuration_template"
                  :key="dict.value"
                  :command="dict.value"
                >
                  {{ dict.label }}
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </el-col>
        <el-col :span="1.5">
          <el-dropdown v-if="checkRole(['admin'])" @command="handleImportTranslate">
            <el-button plain>
              {{ $t('app.start.891644-46') }}
              <el-icon class="el-icon--right"><ArrowDown /></el-icon>
            </el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item
                  v-for="dict in international_configuration_template"
                  :key="dict.value"
                  :command="dict.value"
                >
                  {{ dict.label }}
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
          <el-upload
            v-hasRole="['admin']"
            :show-file-list="false"
            ref="importTranslateRef"
            action=""
            :http-request="handleImportBackendMenu"
            style="display: none"
          />
        </el-col>
        <right-toolbar
          :showSearch="showSearch"
          @update:showSearch="(val: boolean) => (showSearch = val)"
          @queryTable="getList"
        ></right-toolbar>
      </el-row>

      <el-table v-loading="loading" :data="languageList" :border="false" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column :label="$t('app.lang.755172-4')" align="center" prop="id" min-width="80" />
        <el-table-column :label="$t('app.lang.755172-8')" align="left" prop="langName" min-width="180" />
        <el-table-column :label="$t('app.lang.755172-5')" align="center" prop="language" min-width="100" />
        <el-table-column :label="$t('app.lang.755172-6')" align="center" prop="country" min-width="120" />
        <el-table-column :label="$t('app.lang.755172-7')" align="center" prop="timeZone" min-width="100" />
        <el-table-column fixed="right" :label="$t('opation')" align="center" width="130">
          <template #default="scope">
            <el-button
              size="small"
              link
              :icon="Edit"
              @click="handleUpdate(scope.row)"
              v-hasPermi="['app:language:edit']"
            >
              {{ $t('update') }}
            </el-button>
            <el-button
              size="small"
              link
              :icon="Delete"
              @click="handleDelete(scope.row)"
              v-hasPermi="['app:language:remove']"
            >
              {{ $t('del') }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <pagination
        style="margin-bottom: 20px"
        v-show="total > 0"
        :total="total"
        :page="queryParams.pageNum"
        :limit="queryParams.pageSize"
        @update:page="(val: number) => (queryParams.pageNum = val)"
        @update:limit="(val: number) => (queryParams.pageSize = val)"
        @pagination="getList"
      />
    </el-card>

    <!-- 添加或修改app语言对话框 -->
    <el-dialog :title="title" v-model="open" width="560px" append-to-body>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="75px">
        <el-form-item :label="$t('app.lang.755172-8')" prop="langName">
          <el-input v-model="form.langName" :placeholder="$t('app.lang.755172-14')" style="width: 390px" />
        </el-form-item>
        <el-form-item :label="$t('app.lang.755172-5')" prop="language">
          <el-input v-model="form.language" :placeholder="$t('app.lang.755172-11')" style="width: 390px" />
        </el-form-item>
        <el-form-item :label="$t('app.lang.755172-6')" prop="country">
          <el-input v-model="form.country" :placeholder="$t('app.lang.755172-12')" style="width: 390px" />
        </el-form-item>
        <el-form-item :label="$t('app.lang.755172-7')" prop="timeZone">
          <el-input v-model="form.timeZone" :placeholder="$t('app.lang.755172-13')" style="width: 390px" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">{{ $t('confirm') }}</el-button>
          <el-button @click="cancel">{{ $t('cancel') }}</el-button>
        </div>
      </template>
    </el-dialog>

    <el-dialog :title="$t('app.lang.755172-22')" v-model="productModelVisible" width="500px" append-to-body>
      <el-form :model="form" label-width="80px">
        <el-form-item :label="$t('app.lang.755172-23')" prop="productId">
          <el-select v-model="productId" :placeholder="$t('pleaseSelect')">
            <el-option v-for="item in prodcutModels" :key="item.id" :label="item.name" :value="item.id"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitProdcutModel">{{ $t('confirm') }}</el-button>
          <el-button @click="closeProductModelDialog">{{ $t('cancel') }}</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, getCurrentInstance, onMounted } from 'vue';
import { useI18n } from 'vue-i18n';
import { Search, Refresh, Plus, Delete, Edit, ArrowDown } from '@element-plus/icons-vue';
import {
  listLanguage,
  getLanguage,
  delLanguage,
  addLanguage,
  updateLanguage,
  exportTranslate,
  importTranslate,
} from '@/api/system/language';
import * as langTransformer from './script/langTransformer';
import * as xlsxHandler from './script/xlsx';
import * as jszip from './script/jszip';
import { downFileByBlob } from '@/utils/common';
import { listShortProduct } from '@/api/iot/product';
import { useDict } from '@/utils/dict/useDict';
import { checkRole } from '@/utils/permission';

const { international_configuration_template } = useDict('international_configuration_template');
const { proxy } = getCurrentInstance() as any;
const { t } = useI18n();

const loading = ref(true);
const ids = ref<any[]>([]);
const single = ref(true);
const multiple = ref(true);
const showSearch = ref(true);
const total = ref(0);
const languageList = ref<any[]>([]);
const title = ref('');
const open = ref(false);
const exportLoading = ref(false);
const importLoading = ref(false);
const currentTranslateModule = ref('');
const productModelVisible = ref(false);
const prodcutModels = ref<any[]>([]);
const productId = ref('');
const uploadRef = ref<any>(null);
const importTranslateRef = ref<any>(null);
const formRef = ref<any>(null);
const queryRef = ref<any>(null);
let callback: Function | null = null;

const queryParams = reactive({
  langName: '',
  country: '',
  pageNum: 1,
  pageSize: 10,
});

const form = ref<any>({});

const rules = reactive<any>({
  langName: [{ required: true, message: t('app.lang.755172-14'), trigger: 'blur' }],
  language: [{ required: true, message: t('app.lang.755172-11'), trigger: 'blur' }],
  country: [{ required: true, message: t('app.lang.755172-12'), trigger: 'blur' }],
});

/** 查询app语言列表 */
function getList() {
  loading.value = true;
  listLanguage(queryParams).then((response: any) => {
    languageList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}

function cancel() {
  open.value = false;
  reset();
}

function reset() {
  form.value = {
    id: null,
    language: null,
    country: null,
    timeZone: null,
    createBy: null,
    createTime: null,
    langName: null,
  };
  formRef.value?.resetFields();
}

function handleQuery() {
  queryParams.pageNum = 1;
  getList();
}

function resetQuery() {
  queryRef.value?.resetFields();
  handleQuery();
}

function handleSelectionChange(selection: any[]) {
  ids.value = selection.map((item) => item.id);
  single.value = selection.length !== 1;
  multiple.value = !selection.length;
}

function handleAdd() {
  reset();
  open.value = true;
  title.value = t('app.lang.755172-17');
}

function handleUpdate(row: any) {
  reset();
  const id = row.id || ids.value;
  getLanguage(id).then((response: any) => {
    form.value = response.data;
    open.value = true;
    title.value = t('app.lang.755172-18');
  });
}

function submitForm() {
  formRef.value?.validate((valid: boolean) => {
    if (valid) {
      if (form.value.id != null) {
        updateLanguage(form.value).then(() => {
          proxy.$modal.msgSuccess(t('updateSuccess'));
          open.value = false;
          getList();
        });
      } else {
        addLanguage(form.value).then(() => {
          proxy.$modal.msgSuccess(t('addSuccess'));
          open.value = false;
          getList();
        });
      }
    }
  });
}

function handleDelete(row?: any) {
  const delIds = row?.id || ids.value;
  proxy.$modal
    .confirm(t('app.lang.755172-21', [delIds]))
    .then(() => {
      return delLanguage(delIds);
    })
    .then(() => {
      getList();
      proxy.$modal.msgSuccess(t('delSuccess'));
    })
    .catch(() => {});
}

function handleExport() {
  if (languageList.value.length === 0) return;
  try {
    exportLoading.value = true;
    const langs = languageList.value.reduce((obj: any, item: any) => {
      obj[item.language] = item.country;
      return obj;
    }, {});
    const jsonMap = langTransformer.getLangJson();
    const excelData = langTransformer.transoformToExcel(jsonMap, langs);
    xlsxHandler.exportExcel(excelData, 'lang.xlsx');
  } finally {
    exportLoading.value = false;
  }
}

async function handleImport(fileInfo: any) {
  try {
    importLoading.value = true;
    const langs = languageList.value.reduce((obj: any, item: any) => {
      obj[item.country] = item.language;
      return obj;
    }, {});
    const data = await xlsxHandler.parseJson(fileInfo.file);
    const jsonData = jszip.parseJsonZipData(data, langs);
    const files = jszip.generateJsonZipFiles(jsonData);
    jszip.downloadFiles2Zip({ zipName: 'lang', files });
  } finally {
    importLoading.value = false;
  }
}

async function handleExportBackendMenu(value: string, isSource = false) {
  currentTranslateModule.value = value;
  const exportFn = () => {
    const sourceData = international_configuration_template.value;
    const isThingsModel = currentTranslateModule.value === 'things_model';
    exportTranslate(value, isSource, isThingsModel ? productId.value : null).then((response: any) => {
      let name = sourceData.find((item: any) => item.value === value)?.label || value;
      if (isThingsModel) {
        name += '_' + prodcutModels.value.find((item: any) => item.id === productId.value)?.name;
      }
      if (response.type === 'application/json') {
        proxy.$modal.msgError('导出异常');
        return;
      }
      const fileName = isSource ? `${name}原表数据.xlsx` : `${name}翻译数据.xlsx`;
      downFileByBlob(response, fileName);
      isThingsModel && closeProductModelDialog();
    });
  };
  if (value === 'things_model') {
    productModelVisible.value = true;
    callback = () => {
      exportFn();
    };
  } else {
    exportFn();
  }
}

async function handleImportBackendMenu(fileInfo: any) {
  let formData = new FormData();
  formData.append('file', fileInfo.file);
  const isThingsModel = currentTranslateModule.value === 'things_model';
  const pId = isThingsModel && productId.value ? productId.value : '';
  importTranslate(formData, currentTranslateModule.value, pId).then((res: any) => {
    if (res.code === 200) {
      proxy.$modal.msgSuccess('导入成功');
    } else {
      proxy.$modal.msgError(res.msg);
    }
    isThingsModel && closeProductModelDialog();
  });
}

function handleImportTranslate(value: string) {
  currentTranslateModule.value = value;
  if (value === 'things_model') {
    productModelVisible.value = true;
    callback = () => {
      importTranslateRef.value?.$el?.querySelector('input')?.click();
    };
  } else {
    importTranslateRef.value?.$el?.querySelector('input')?.click();
  }
}

async function getProductModels() {
  const params = { pageSize: 999, showSenior: true };
  const res: any = await listShortProduct(params);
  if (res.code === 200) {
    prodcutModels.value = res.data || [];
  }
}

function closeProductModelDialog() {
  productModelVisible.value = false;
  productId.value = '';
}

function submitProdcutModel() {
  if (!productId.value) {
    proxy.$modal.msgWarning('请选择产品后再确认');
    return;
  }
  callback && callback(productId.value);
}

onMounted(() => {
  getList();
  getProductModels();
});
</script>

<style lang="scss" scoped>
.system-app-lang {
  padding: 20px;

  .search-card {
    margin-bottom: 15px;
    padding: 3px 0;
  }

  .search-form {
    margin-bottom: -22.5px;
  }
}
</style>
