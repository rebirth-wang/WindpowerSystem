<template>
  <div class="system-dict">
    <el-card v-show="showSearch" class="search-card">
      <div class="form-wrap">
        <el-form @submit.prevent :model="queryParams" ref="queryFormRef" :inline="true" label-width="68px">
          <el-form-item prop="dictName">
            <el-input
              v-model="queryParams.dictName"
              :placeholder="$t('system.dict.index.880996-0')"
              clearable
              style="width: 192px"
              @keyup.enter="handleQuery"
            />
          </el-form-item>
          <el-form-item prop="dictType">
            <el-input
              v-model="queryParams.dictType"
              :placeholder="$t('system.dict.index.880996-1')"
              clearable
              style="width: 192px"
              @keyup.enter="handleQuery"
            />
          </el-form-item>
          <el-form-item prop="status">
            <el-select
              v-model="queryParams.status"
              :placeholder="$t('system.dict.index.880996-2')"
              clearable
              style="width: 192px"
            >
              <el-option
                v-for="item in dict.type.sys_normal_disable"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
          <template v-if="searchShow">
            <el-form-item>
              <el-date-picker
                v-model="dateRange"
                style="width: 240px"
                value-format="YYYY-MM-DD"
                type="daterange"
                range-separator="-"
                :start-placeholder="$t('system.dict.index.880996-3')"
                :end-placeholder="$t('system.dict.index.880996-4')"
              />
            </el-form-item>
          </template>
        </el-form>
        <div class="search-btn-group">
          <el-button type="primary" :icon="Search" @click="handleQuery">{{ $t('search') }}</el-button>
          <el-button :icon="Refresh" @click="resetQuery">{{ $t('reset') }}</el-button>
          <el-button type="primary" link @click="searchChange">
            <span style="color: #486ff2; margin-left: 14px">
              {{ searchShow ? $t('template.index.891112-113') : $t('template.index.891112-112') }}
            </span>
            <el-icon style="color: #486ff2; margin-left: 10px">
              <ArrowUp v-if="searchShow" />
              <ArrowDown v-else />
            </el-icon>
          </el-button>
        </div>
      </div>
    </el-card>

    <el-card>
      <el-row :gutter="10" style="margin-bottom: 16px">
        <el-col :span="1.5">
          <el-button type="primary" plain :icon="Plus" @click="handleAdd" v-hasPermi="['system:dict:add']">
            {{ $t('add') }}
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button plain :icon="Edit" :disabled="single" @click="handleUpdate()" v-hasPermi="['system:dict:edit']">
            {{ $t('update') }}
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            plain
            :icon="Delete"
            :disabled="multiple"
            @click="handleDelete()"
            v-hasPermi="['system:dict:remove']"
          >
            {{ $t('del') }}
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button plain :icon="Download" @click="handleExport" v-hasPermi="['system:dict:export']">
            {{ $t('export') }}
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button plain :icon="Refresh" @click="handleRefreshCache" v-hasPermi="['system:dict:refresh']">
            {{ $t('system.dict.index.880996-5') }}
          </el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
      </el-row>

      <el-table
        v-loading="loading"
        :data="typeList"
        :border="false"
        @selection-change="handleSelectionChange"
        ref="multipleTableRef"
        :row-key="getRowKeys"
      >
        <el-table-column :reserve-selection="true" type="selection" width="55" align="center" />
        <el-table-column :label="$t('system.dict.index.880996-6')" align="center" prop="dictId" width="80" />
        <el-table-column
          :label="$t('system.dict.data.879098-0')"
          align="left"
          prop="dictName"
          show-overflow-tooltip
          min-width="180"
        />
        <el-table-column :label="$t('system.dict.data.879098-8')" align="left" show-overflow-tooltip min-width="180">
          <template #default="scope">
            <router-link :to="'/system/dictData/index/' + scope.row.dictId" class="link-type">
              <span>{{ scope.row.dictType }}</span>
            </router-link>
          </template>
        </el-table-column>
        <el-table-column :label="$t('status')" align="center" prop="status" width="100">
          <template #default="scope">
            <dict-tag :options="dict.type.sys_normal_disable" :value="scope.row.status" />
          </template>
        </el-table-column>
        <el-table-column :label="$t('creatTime')" align="center" prop="createTime" width="180">
          <template #default="scope">
            <span>{{ parseTime(scope.row.createTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column :label="$t('remark')" align="left" prop="remark" show-overflow-tooltip min-width="200" />
        <el-table-column
          fixed="right"
          :label="$t('opation')"
          align="center"
          class-name="small-padding fixed-width"
          width="150"
        >
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
        style="margin-bottom: 20px"
        v-show="total > 0"
        :total="total"
        v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize"
        @pagination="getList"
      />
    </el-card>

    <!-- 添加或修改参数配置对话框 -->
    <el-dialog :title="title" v-model="open" width="600px" append-to-body>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item :label="$t('system.dict.data.879098-0')" prop="dictName">
          <el-input v-model="form.dictName" :placeholder="$t('system.dict.index.880996-0')" style="width: 400px" />
        </el-form-item>
        <el-form-item :label="$t('system.dict.index.880996-13')" prop="dictName_en_US">
          <el-input
            v-model="form.dictName_en_US"
            :placeholder="$t('system.dict.index.880996-14')"
            style="width: 400px"
          />
        </el-form-item>
        <el-form-item :label="$t('system.dict.data.879098-8')" prop="dictType">
          <el-input v-model="form.dictType" :placeholder="$t('system.dict.index.880996-1')" style="width: 400px" />
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
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, getCurrentInstance } from 'vue';
import { useI18n } from 'vue-i18n';
import { Search, Refresh, Plus, Delete, Edit, Download, ArrowDown, ArrowUp } from '@element-plus/icons-vue';
import { listType, getType, delType, addType, updateType, refreshCache } from '@/api/system/dict/type';
import { parseTime, addDateRange } from '@/utils/ruoyi';
import { useDict } from '@/utils/dict/useDict';

const { t } = useI18n();
const { proxy } = getCurrentInstance()!;
const { dict } = useDict('sys_normal_disable');

const loading = ref(true);
const ids = ref<number[]>([]);
const single = ref(true);
const multiple = ref(true);
const showSearch = ref(true);
const searchShow = ref(false);
const total = ref(0);
const typeList = ref<any[]>([]);
const title = ref('');
const open = ref(false);
const dateRange = ref<string[]>([]);
const form = ref<any>({});

const queryFormRef = ref();
const formRef = ref();
const multipleTableRef = ref();

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  dictName: undefined as string | undefined,
  dictType: undefined as string | undefined,
  status: undefined as string | undefined,
});

const rules = reactive({
  dictName: [{ required: true, message: t('system.dict.index.880996-7'), trigger: 'blur' }],
  dictType: [{ required: true, message: t('system.dict.index.880996-8'), trigger: 'blur' }],
  dictName_en_US: [{ required: true, message: t('system.dict.index.880996-15'), trigger: 'blur' }],
});

onMounted(() => {
  getList();
});

function getList() {
  loading.value = true;
  listType(addDateRange(queryParams, dateRange.value)).then((res: any) => {
    typeList.value = res.rows;
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
    dictId: undefined,
    dictName: undefined,
    dictName_en_US: undefined,
    dictType: undefined,
    status: 0,
    remark: undefined,
  };
  formRef.value?.resetFields();
}

function handleQuery() {
  queryParams.pageNum = 1;
  getList();
}

function resetQuery() {
  dateRange.value = [];
  queryFormRef.value?.resetFields();
  handleQuery();
}

function handleAdd() {
  reset();
  open.value = true;
  title.value = t('system.dict.index.880996-9');
}

function handleSelectionChange(selection: any[]) {
  ids.value = selection.map((item) => item.dictId);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

function handleUpdate(row?: any) {
  reset();
  const dictId = row?.dictId || ids.value;
  getType(dictId).then((res: any) => {
    form.value = res.data;
    open.value = true;
    title.value = t('system.dict.index.880996-10');
  });
}

function submitForm() {
  formRef.value?.validate((valid: boolean) => {
    if (valid) {
      if (form.value.dictId != undefined) {
        updateType(form.value).then(() => {
          (proxy as any).$modal.msgSuccess(t('updateSuccess'));
          open.value = false;
          getList();
        });
      } else {
        addType(form.value).then(() => {
          (proxy as any).$modal.msgSuccess(t('addSuccess'));
          open.value = false;
          getList();
        });
      }
    }
  });
}

function handleDelete(row?: any) {
  const dictIds = row?.dictId || ids.value;
  (proxy as any).$modal
    .confirm(t('system.dict.index.880996-11', [dictIds]))
    .then(() => delType(dictIds))
    .then(() => {
      getList();
      (proxy as any).$modal.msgSuccess(t('delSuccess'));
      multipleTableRef.value?.clearSelection();
    })
    .catch(() => {});
}

function handleExport() {
  (proxy as any).download('system/dict/type/export', { ...queryParams }, `type_${new Date().getTime()}.xlsx`);
}

function handleRefreshCache() {
  refreshCache().then(() => {
    (proxy as any).$modal.msgSuccess(t('system.dict.index.880996-12'));
  });
}

function searchChange() {
  searchShow.value = !searchShow.value;
}

function getRowKeys(row: any) {
  return row.dictId;
}
</script>

<style lang="scss" scoped>
.system-dict {
  padding: 20px;

  .search-card {
    margin-bottom: 15px;
    padding: 3px 0;

    .form-wrap {
      display: flex;
      flex-direction: row;
      justify-content: space-between;
      align-items: flex-end;
      margin-bottom: -22.5px;

      .search-btn-group {
        display: flex;
        flex-direction: row;
        margin-bottom: 22px;
      }
    }
  }
}
</style>
