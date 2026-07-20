<template>
  <div class="config-wrap">
    <el-card class="card-wrap">
      <el-row :gutter="10" style="margin-bottom: 16px">
        <el-col :span="1.5">
          <el-button type="primary" plain :icon="Plus" @click="handleAdd" v-hasPermi="['oss:config:add']">
            {{ $t('system.oss.config.185269-0') }}
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button plain :icon="Edit" :disabled="single" @click="handleUpdate" v-hasPermi="['oss:config:edit']">
            {{ $t('system.oss.config.185269-1') }}
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button plain :icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['oss:config:remove']">
            {{ $t('system.oss.config.185269-2') }}
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button :icon="Refresh" @click="getList()">
            {{ $t('refresh') }}
          </el-button>
        </el-col>
      </el-row>

      <el-table
        v-loading="loading"
        :data="configList"
        @selection-change="handleSelectionChange"
        :border="false"
        ref="multipleTableRef"
        :row-key="getRowKeys"
      >
        <el-table-column :reserve-selection="true" type="selection" width="55" align="center" />
        <el-table-column :label="$t('system.oss.config.185269-3')" align="left" prop="configKey" min-width="180" />
        <el-table-column :label="$t('system.oss.config.185269-4')" align="left" prop="endpoint" min-width="220" />
        <el-table-column :label="$t('system.oss.config.185269-5')" align="left" prop="domainAlias" min-width="210" />
        <el-table-column :label="$t('system.oss.config.185269-6')" align="left" prop="bucketName" min-width="180" />
        <el-table-column :label="$t('system.oss.config.185269-9')" align="center" prop="accessPolicy" min-width="90">
          <template #default="scope">
            <el-tag type="warning" v-if="scope.row.accessPolicy === '0'">private</el-tag>
            <el-tag type="success" v-if="scope.row.accessPolicy === '1'">public</el-tag>
            <el-tag type="info" v-if="scope.row.accessPolicy === '2'">custom</el-tag>
          </template>
        </el-table-column>
        <el-table-column :label="$t('system.oss.config.185269-10')" align="center" prop="status" min-width="90">
          <template #default="scope">
            <el-switch
              v-model="scope.row.status"
              :active-value="0"
              :inactive-value="1"
              @change="handleStatusChange(scope.row)"
            />
          </template>
        </el-table-column>
        <el-table-column
          fixed="right"
          :label="$t('system.oss.config.185269-11')"
          align="center"
          class-name="small-padding fixed-width"
          width="125"
        >
          <template #default="scope">
            <el-button size="small" link :icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['oss:config:edit']">
              {{ $t('system.oss.config.185269-1') }}
            </el-button>
            <el-button
              size="small"
              link
              :icon="Delete"
              @click="handleDelete(scope.row)"
              v-hasPermi="['oss:config:remove']"
            >
              {{ $t('system.oss.config.185269-2') }}
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

      <!-- 添加或修改文件存储配置对话框 -->
      <el-dialog :title="title" v-model="open" width="620px" append-to-body>
        <el-form ref="formRef" :model="form" :rules="rules" label-width="120px">
          <el-form-item :label="$t('system.oss.config.185269-12')" prop="configKey">
            <el-input v-model="form.configKey" :placeholder="$t('system.oss.config.185269-13')" style="width: 400px" />
          </el-form-item>
          <el-form-item :label="$t('system.oss.config.185269-14')" prop="endpoint">
            <el-input v-model="form.endpoint" :placeholder="$t('system.oss.config.185269-15')" style="width: 400px" />
          </el-form-item>
          <el-form-item :label="$t('system.oss.config.185269-16')" prop="domainAlias">
            <el-input
              v-model="form.domainAlias"
              :placeholder="$t('system.oss.config.185269-17')"
              style="width: 400px"
            />
          </el-form-item>
          <el-form-item label="accessKey" prop="accessKey">
            <el-input v-model="form.accessKey" :placeholder="$t('system.oss.config.185269-18')" style="width: 400px" />
          </el-form-item>
          <el-form-item label="secretKey" prop="secretKey">
            <el-input v-model="form.secretKey" :placeholder="$t('system.oss.config.185269-19')" style="width: 400px" />
          </el-form-item>
          <el-form-item :label="$t('system.oss.config.185269-20')" prop="bucketName">
            <el-input v-model="form.bucketName" :placeholder="$t('system.oss.config.185269-21')" style="width: 400px" />
          </el-form-item>
          <el-form-item :label="$t('system.oss.config.185269-22')" prop="isHttps">
            <el-radio-group v-model="form.isHttps">
              <el-radio v-for="dict in sys_yes_no" :key="dict.value" :value="dict.value">{{ dict.label }}</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item :label="$t('system.oss.config.185269-23')">
            <el-radio-group v-model="form.accessPolicy">
              <el-radio value="0">private</el-radio>
              <el-radio value="1">public</el-radio>
              <el-radio value="2">custom</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item :label="$t('system.oss.config.185269-24')" prop="prefix">
            <el-input v-model="form.prefix" :placeholder="$t('system.oss.config.185269-25')" style="width: 400px" />
          </el-form-item>
          <el-form-item :label="$t('system.oss.config.185269-26')" prop="region">
            <el-input v-model="form.region" :placeholder="$t('system.oss.config.185269-27')" style="width: 400px" />
          </el-form-item>
        </el-form>
        <template #footer>
          <div class="dialog-footer">
            <el-button type="primary" @click="submitForm">{{ $t('system.oss.config.185269-28') }}</el-button>
            <el-button @click="cancel">{{ $t('system.oss.config.185269-29') }}</el-button>
          </div>
        </template>
      </el-dialog>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, getCurrentInstance } from 'vue';
import { Plus, Edit, Delete, Refresh } from '@element-plus/icons-vue';
import { useDict } from '@/utils/dict/useDict';
import {
  listConfig,
  getConfig,
  delConfig,
  addConfig,
  updateConfig,
  changeOssConfigStatus,
} from '@/api/system/ossConfig';

const { proxy } = getCurrentInstance() as any;
const { sys_yes_no } = useDict('sys_yes_no');

const formRef = ref();
const multipleTableRef = ref();

const loading = ref(true);
const ids = ref<any[]>([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const configList = ref<any[]>([]);
const title = ref('');
const open = ref(false);
const form = ref<any>({});

const queryParams = reactive({ pageNum: 1, pageSize: 10 });

const rules = reactive<any>({
  configKey: [{ required: true, message: proxy?.$t('system.oss.config.185269-30'), trigger: 'blur' }],
  accessKey: [{ required: true, message: proxy?.$t('system.oss.config.185269-31'), trigger: 'blur' }],
  secretKey: [{ required: true, message: proxy?.$t('system.oss.config.185269-32'), trigger: 'blur' }],
  bucketName: [{ required: true, message: proxy?.$t('system.oss.config.185269-33'), trigger: 'blur' }],
  endpoint: [{ required: true, message: proxy?.$t('system.oss.config.185269-34'), trigger: 'blur' }],
  isHttps: [{ required: true, message: proxy?.$t('system.oss.config.185269-35'), trigger: 'blur' }],
  accessPolicy: [{ required: true, message: proxy?.$t('system.oss.config.185269-36'), trigger: 'blur' }],
});

onMounted(() => {
  getList();
});

function getList() {
  loading.value = true;
  listConfig(queryParams).then((response: any) => {
    configList.value = response.rows;
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
    configKey: null,
    accessKey: null,
    secretKey: null,
    bucketName: null,
    prefix: null,
    endpoint: null,
    domainAlias: null,
    isHttps: null,
    region: null,
    accessPolicy: null,
    status: null,
    ext1: null,
  };
  formRef.value?.resetFields();
}

function handleSelectionChange(selection: any[]) {
  ids.value = selection.map((item) => item.id);
  single.value = selection.length !== 1;
  multiple.value = !selection.length;
}

function handleAdd() {
  reset();
  open.value = true;
  title.value = proxy?.$t('system.oss.config.185269-37');
}

function handleUpdate(row: any) {
  reset();
  const id = row.id || ids.value;
  getConfig(id).then((response: any) => {
    form.value = response.data;
    open.value = true;
    title.value = proxy?.$t('system.oss.config.185269-38');
  });
}

function submitForm() {
  formRef.value?.validate((valid: boolean) => {
    if (valid) {
      if (form.value.id != null) {
        updateConfig(form.value).then(() => {
          proxy?.$modal.msgSuccess(proxy?.$t('system.oss.config.185269-39'));
          open.value = false;
          getList();
        });
      } else {
        addConfig(form.value).then(() => {
          proxy?.$modal.msgSuccess(proxy?.$t('system.oss.config.185269-40'));
          open.value = false;
          getList();
        });
      }
    }
  });
}

function handleDelete(row: any) {
  const delIds = row.id || ids.value;
  proxy?.$modal
    .confirm(proxy?.$t('system.oss.config.185269-44', [delIds]))
    .then(() => {
      return delConfig(delIds);
    })
    .then(() => {
      getList();
      proxy?.$modal.msgSuccess(proxy?.$t('system.oss.config.185269-41'));
      multipleTableRef.value?.clearSelection();
    })
    .catch(() => {});
}

function handleStatusChange(row: any) {
  if (row.status === 1) {
    proxy?.$modal.msgError(proxy?.$t('system.oss.config.185269-42'));
    return;
  }
  proxy?.$modal
    .confirm(proxy?.$t('system.oss.config.185269-45', [row.id]))
    .then(() => {
      return changeOssConfigStatus(row);
    })
    .then(() => {
      getList();
      proxy?.$modal.msgSuccess(proxy?.$t('system.oss.config.185269-43'));
    })
    .catch(() => {});
}

function getRowKeys(row: any) {
  return row.id;
}

/** 导出按钮操作 */
function handleExport() {
  proxy?.download('iot/config/export', { ...queryParams }, `config_${new Date().getTime()}.xlsx`);
}
</script>

<style lang="scss" scoped>
.config-wrap {
  padding: 20px;
  .card-wrap {
    border-radius: 8px;
  }
}
</style>
