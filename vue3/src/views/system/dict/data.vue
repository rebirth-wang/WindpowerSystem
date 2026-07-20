<template>
  <div class="data-wrap">
    <el-card v-show="showSearch" class="search-card">
      <el-form
        @submit.prevent
        :model="queryParams"
        ref="queryFormRef"
        :inline="true"
        label-width="68px"
        class="search-form"
      >
        <el-form-item prop="dictType">
          <el-select v-model="queryParams.dictType" style="width: 200px">
            <el-option v-for="item in typeOptions" :key="item.dictId" :label="item.dictName" :value="item.dictType" />
          </el-select>
        </el-form-item>
        <el-form-item prop="dictLabel">
          <el-input
            v-model="queryParams.dictLabel"
            :placeholder="$t('system.dict.data.879098-2')"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item prop="status">
          <el-select
            v-model="queryParams.status"
            :placeholder="$t('system.dict.data.879098-3')"
            clearable
            style="width: 200px"
          >
            <el-option
              v-for="item in dict.type.sys_normal_disable"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
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
          <el-button type="primary" plain :icon="Plus" size="small" @click="handleAdd" v-hasPermi="['system:dict:add']">
            {{ $t('add') }}
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            plain
            :icon="Edit"
            size="small"
            :disabled="single"
            @click="handleUpdate()"
            v-hasPermi="['system:dict:edit']"
          >
            {{ $t('update') }}
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            plain
            :icon="Delete"
            size="small"
            :disabled="multiple"
            @click="handleDelete()"
            v-hasPermi="['system:dict:remove']"
          >
            {{ $t('del') }}
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button plain :icon="Download" size="small" @click="handleExport" v-hasPermi="['system:dict:export']">
            {{ $t('export') }}
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button plain :icon="Close" size="small" @click="handleClose">{{ $t('close') }}</el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
      </el-row>

      <el-table
        v-loading="loading"
        :data="dataList"
        @selection-change="handleSelectionChange"
        :border="false"
        ref="multipleTableRef"
        :row-key="getRowKeys"
      >
        <el-table-column :reserve-selection="true" type="selection" width="55" align="center" />
        <el-table-column :label="$t('system.dict.data.879098-4')" align="center" prop="dictCode" min-width="90" />
        <el-table-column :label="$t('system.dict.data.879098-5')" align="left" prop="dictLabel" min-width="180">
          <template #default="scope">
            <span v-if="scope.row.listClass == '' || scope.row.listClass == 'default'">{{ scope.row.dictLabel }}</span>
            <el-tag v-else :type="scope.row.listClass == 'primary' ? '' : scope.row.listClass">
              {{ scope.row.dictLabel }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column :label="$t('system.dict.data.879098-6')" align="left" prop="dictValue" min-width="150" />
        <el-table-column :label="$t('system.dict.data.879098-7')" align="center" prop="dictSort" min-width="90" />
        <el-table-column :label="$t('status')" align="center" prop="status" min-width="100">
          <template #default="scope">
            <dict-tag :options="dict.type.sys_normal_disable" :value="scope.row.status" />
          </template>
        </el-table-column>
        <el-table-column :label="$t('remark')" align="left" prop="remark" show-overflow-tooltip min-width="180" />
        <el-table-column :label="$t('creatTime')" align="center" prop="createTime" width="160">
          <template #default="scope">
            <span>{{ parseTime(scope.row.createTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column fixed="right" :label="$t('opation')" align="center" width="225">
          <template #default="scope">
            <el-button
              size="small"
              link
              :icon="Edit"
              @click="handleUpdate(scope.row)"
              v-hasPermi="['system:dict:edit']"
            >
              {{ $t('update') }}
            </el-button>
            <el-button
              size="small"
              link
              :icon="Delete"
              @click="handleDelete(scope.row)"
              v-hasPermi="['system:dict:remove']"
            >
              {{ $t('del') }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <pagination
        v-show="total > 0"
        :total="total"
        v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize"
        @pagination="getList"
      />

      <!-- 添加或修改参数配置对话框 -->
      <el-dialog :title="title" v-model="open" width="600px" append-to-body>
        <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
          <el-form-item :label="$t('system.dict.data.879098-8')">
            <el-input v-model="form.dictType" :disabled="true" style="width: 400px" />
          </el-form-item>
          <el-form-item :label="$t('system.dict.data.879098-5')" prop="dictLabel">
            <el-input v-model="form.dictLabel" :placeholder="$t('system.dict.data.879098-10')" style="width: 400px" />
          </el-form-item>
          <el-form-item :label="$t('system.dict.data.879098-29')" prop="dictLabel_en_US">
            <el-input
              v-model="form.dictLabel_en_US"
              :placeholder="$t('system.dict.data.879098-30')"
              style="width: 400px"
            />
          </el-form-item>
          <el-form-item :label="$t('system.dict.data.879098-6')" prop="dictValue">
            <el-input v-model="form.dictValue" :placeholder="$t('system.dict.data.879098-12')" style="width: 400px" />
          </el-form-item>
          <el-form-item :label="$t('system.dict.data.879098-13')" prop="cssClass">
            <el-input v-model="form.cssClass" :placeholder="$t('system.dict.data.879098-14')" style="width: 400px" />
          </el-form-item>
          <el-form-item :label="$t('system.dict.data.879098-15')" prop="dictSort">
            <el-input-number v-model="form.dictSort" controls-position="right" :min="0" style="width: 400px" />
          </el-form-item>
          <el-form-item :label="$t('system.dict.data.879098-16')" prop="listClass">
            <el-select v-model="form.listClass" style="width: 225px">
              <el-option
                v-for="item in listClassOptions"
                :key="item.value"
                :label="item.label + '(' + item.value + ')'"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
          <el-form-item :label="$t('status')" prop="status">
            <el-radio-group v-model="form.status">
              <el-radio v-for="item in dict.type.sys_normal_disable" :key="item.value" :value="Number(item.value)">
                {{ item.label }}
              </el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item :label="$t('remark')" prop="remark">
            <el-input
              v-model="form.remark"
              type="textarea"
              :placeholder="$t('plzInput')"
              :autosize="{ minRows: 3, maxRows: 5 }"
              style="width: 400px"
            />
          </el-form-item>
        </el-form>
        <template #footer>
          <div class="dialog-footer">
            <el-button type="primary" @click="submitForm">{{ $t('confirm') }}</el-button>
            <el-button @click="cancel">{{ $t('cancel') }}</el-button>
          </div>
        </template>
      </el-dialog>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, getCurrentInstance } from 'vue';
import { useRoute } from 'vue-router';
import { useI18n } from 'vue-i18n';
import { Search, Refresh, Plus, Delete, Edit, Download, Close } from '@element-plus/icons-vue';
import { listData, getData, delData, addData, updateData } from '@/api/system/dict/data';
import { optionselect as getDictOptionselect, getType } from '@/api/system/dict/type';
import { parseTime } from '@/utils/ruoyi';
import { useDict } from '@/utils/dict/useDict';

const { t } = useI18n();
const route = useRoute();
const { proxy } = getCurrentInstance()!;
const { dict } = useDict('sys_normal_disable');

const loading = ref(true);
const ids = ref<number[]>([]);
const single = ref(true);
const multiple = ref(true);
const showSearch = ref(true);
const total = ref(0);
const dataList = ref<any[]>([]);
const defaultDictType = ref('');
const title = ref('');
const open = ref(false);
const typeOptions = ref<any[]>([]);
const form = ref<any>({});

const queryFormRef = ref();
const formRef = ref();
const multipleTableRef = ref();

const listClassOptions = [
  { value: 'default', label: t('system.dict.data.879098-17') },
  { value: 'primary', label: t('system.dict.data.879098-18') },
  { value: 'success', label: t('system.dict.data.879098-19') },
  { value: 'info', label: t('system.dict.data.879098-20') },
  { value: 'warning', label: t('system.dict.data.879098-21') },
  { value: 'danger', label: t('system.dict.data.879098-22') },
];

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  dictName: undefined as string | undefined,
  dictType: undefined as string | undefined,
  dictLabel: undefined as string | undefined,
  status: undefined as string | undefined,
});

const rules = reactive({
  dictLabel: [{ required: true, message: t('system.dict.data.879098-23'), trigger: 'blur' }],
  dictLabel_en_US: [{ required: true, message: t('system.dict.data.879098-31'), trigger: 'blur' }],
  dictValue: [{ required: true, message: t('system.dict.data.879098-24'), trigger: 'blur' }],
  dictSort: [{ required: true, message: t('system.dict.data.879098-25'), trigger: 'blur' }],
});

onMounted(() => {
  const dictId = route.params?.dictId as string;
  loadType(dictId);
  getTypeList();
});

function loadType(dictId: string) {
  getType(dictId).then((res: any) => {
    queryParams.dictType = res.data.dictType;
    defaultDictType.value = res.data.dictType;
    getList();
  });
}

function getTypeList() {
  getDictOptionselect().then((res: any) => {
    typeOptions.value = res.data;
  });
}

function getList() {
  loading.value = true;
  listData(queryParams).then((res: any) => {
    dataList.value = res.rows;
    total.value = res.total;
    loading.value = false;
  });
}

function cancel() {
  open.value = false;
  reset();
}

function reset() {
  form.value = {
    dictCode: undefined,
    dictLabel: undefined,
    dictLabel_en_US: undefined,
    dictValue: undefined,
    cssClass: undefined,
    listClass: 'default',
    dictSort: 0,
    status: 0,
    remark: undefined,
  };
  formRef.value?.resetFields();
}

function handleQuery() {
  queryParams.pageNum = 1;
  getList();
}

function handleClose() {
  (proxy as any).$tab.closeOpenPage({ path: '/system/dict' });
}

function resetQuery() {
  queryFormRef.value?.resetFields();
  queryParams.dictType = defaultDictType.value;
  handleQuery();
}

function handleAdd() {
  reset();
  open.value = true;
  title.value = t('system.dict.data.879098-26');
  form.value.dictType = queryParams.dictType;
}

function handleSelectionChange(selection: any[]) {
  ids.value = selection.map((item) => item.dictCode);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

function handleUpdate(row?: any) {
  reset();
  const dictCode = row?.dictCode || ids.value;
  getData(dictCode).then((res: any) => {
    form.value = res.data;
    open.value = true;
    title.value = t('system.dict.data.879098-27');
  });
}

function submitForm() {
  formRef.value?.validate((valid: boolean) => {
    if (valid) {
      if (form.value.dictCode != undefined) {
        updateData(form.value).then(() => {
          (proxy as any).$modal.msgSuccess(t('updateSuccess'));
          open.value = false;
          getList();
        });
      } else {
        addData(form.value).then(() => {
          (proxy as any).$modal.msgSuccess(t('addSuccess'));
          open.value = false;
          getList();
        });
      }
    }
  });
}

function handleDelete(row?: any) {
  const dictCodes = row?.dictCode || ids.value;
  (proxy as any).$modal
    .confirm(t('system.dict.data.879098-28', [dictCodes]))
    .then(() => delData(dictCodes))
    .then(() => {
      getList();
      (proxy as any).$modal.msgSuccess(t('delSuccess'));
      multipleTableRef.value?.clearSelection();
    })
    .catch(() => {});
}

function handleExport() {
  (proxy as any).download('system/dict/data/export', { ...queryParams }, `data_${new Date().getTime()}.xlsx`);
}

function getRowKeys(row: any) {
  return row.dictCode;
}
</script>

<style lang="scss" scoped>
.data-wrap {
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
