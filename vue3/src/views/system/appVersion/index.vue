<template>
  <div class="system-appVersion">
    <el-card v-show="showSearch" class="search-card">
      <div class="form-wrap">
        <el-form @submit.prevent :model="queryParams" ref="queryFormRef" :inline="true" label-width="68px">
          <el-form-item prop="versionName">
            <el-input
              v-model="queryParams.versionName"
              :placeholder="$t('appVersion.index.348485-0')"
              clearable
              @keyup.enter="handleQuery"
            />
          </el-form-item>
          <el-form-item prop="version">
            <el-input
              v-model="queryParams.version"
              :placeholder="$t('appVersion.index.348485-1')"
              clearable
              @keyup.enter="handleQuery"
            />
          </el-form-item>
        </el-form>
        <div class="search-btn-group">
          <el-button type="primary" :icon="Search" @click="handleQuery">{{ $t('search') }}</el-button>
          <el-button :icon="Refresh" @click="resetQuery">{{ $t('reset') }}</el-button>
        </div>
      </div>
    </el-card>
    <el-card shadow="hover">
      <el-row :gutter="10" class="mb8">
        <el-col :span="1.5">
          <el-button type="primary" plain :icon="Plus" @click="handleAdd" v-hasPermi="['iot:version:add']">
            {{ $t('add') }}
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button plain :icon="Edit" :disabled="single" @click="handleUpdate" v-hasPermi="['iot:version:edit']">
            {{ $t('update') }}
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            plain
            :icon="Delete"
            :disabled="multiple"
            @click="handleDelete"
            v-hasPermi="['iot:version:remove']"
          >
            {{ $t('del') }}
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button plain :icon="Download" @click="handleExport" v-hasPermi="['iot:version:export']">
            {{ $t('export') }}
          </el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
      </el-row>
      <el-table
        v-loading="loading"
        :data="versionList"
        :border="false"
        @selection-change="handleSelectionChange"
        :row-key="getRowKeys"
      >
        <el-table-column :reserve-selection="true" type="selection" width="55" align="center" />
        <el-table-column :label="$t('appVersion.index.348485-10')" prop="version" width="100" />
        <el-table-column :label="$t('appVersion.index.348485-11')" align="center" prop="versionName" width="100" />
        <el-table-column :label="$t('appVersion.index.348485-12')" align="center" prop="isLiveUpdate" width="100">
          <template #default="scope">
            <el-tag v-if="scope.row.isLiveUpdate == '1'" type="success">{{ $t('appVersion.index.348485-2') }}</el-tag>
            <el-tag v-else type="danger">{{ $t('appVersion.index.348485-3') }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column :label="$t('appVersion.index.348485-5')" align="center" prop="apk" min-width="250">
          <template #default="scope">
            <el-tooltip v-if="scope.row.apk" :content="getDownloadUrl(scope.row.apk)" placement="top" effect="dark">
              <div class="table-link-cell">
                <el-link class="link" :href="getDownloadUrl(scope.row.apk)" underline="never" type="primary">
                  {{ getDownloadUrl(scope.row.apk) }}
                </el-link>
              </div>
            </el-tooltip>
          </template>
        </el-table-column>
        <el-table-column :label="$t('appVersion.index.348485-6')" align="center" prop="wgt" min-width="250">
          <template #default="scope">
            <el-tooltip v-if="scope.row.wgt" :content="getDownloadUrl(scope.row.wgt)" placement="top" effect="dark">
              <div class="table-link-cell">
                <el-link class="link" :href="getDownloadUrl(scope.row.wgt)" underline="never" type="primary">
                  {{ getDownloadUrl(scope.row.wgt) }}
                </el-link>
              </div>
            </el-tooltip>
          </template>
        </el-table-column>
        <el-table-column :label="$t('appVersion.index.348485-7')" align="center" prop="updateContent" />
        <el-table-column :label="$t('appVersion.index.348485-8')" align="center" prop="createBy" width="100" />
        <el-table-column :label="$t('appVersion.index.348485-9')" align="center" prop="createTime" width="200" />
        <el-table-column fixed="right" :label="$t('opation')" align="center" width="130">
          <template #default="scope">
            <el-button
              size="small"
              link
              :icon="Edit"
              @click="handleUpdate(scope.row)"
              v-hasPermi="['manager:news:edit']"
            >
              {{ $t('update') }}
            </el-button>
            <el-button
              size="small"
              link
              :icon="Delete"
              @click="handleDelete(scope.row)"
              v-hasPermi="['manager:news:remove']"
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
    </el-card>
    <!-- 添加或修改app版本对话框 -->
    <el-dialog :title="title" v-model="open" width="620px" append-to-body>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item :label="$t('appVersion.index.348485-10')" prop="version">
          <el-input
            v-model="form.version"
            :placeholder="$t('appVersion.index.348485-1')"
            type="number"
            style="width: 400px"
          />
        </el-form-item>
        <el-form-item :label="$t('appVersion.index.348485-11')" prop="versionName">
          <el-input v-model="form.versionName" :placeholder="$t('appVersion.index.348485-0')" style="width: 400px" />
        </el-form-item>
        <el-form-item :label="$t('appVersion.index.348485-12')" prop="isLiveUpdate">
          <el-radio-group v-model="form.isLiveUpdate" class="ml-4" @change="changeIsLiveUpdate">
            <el-radio v-for="dict in iot_yes_no" :key="dict.value" :value="dict.value">
              {{ dict.label }}
            </el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item :label="$t('appVersion.index.348485-5')" prop="apk" v-if="form.isLiveUpdate == '0'">
          <fileUpload
            ref="fileUploadRef"
            :value="form.apk"
            :limit="1"
            :fileSize="30"
            :fileType="['apk']"
            @input="getFileApkPath($event)"
            style="width: 400px"
          />
        </el-form-item>
        <el-form-item :label="$t('appVersion.index.348485-6')" prop="wgt" v-if="form.isLiveUpdate == '1'">
          <fileUpload
            ref="fileUploadRef"
            :value="form.wgt"
            :limit="1"
            :fileSize="30"
            :fileType="['wgt']"
            @input="getFileWgtPath($event)"
            style="width: 400px"
          />
        </el-form-item>
        <el-form-item :label="$t('appVersion.index.348485-14')" prop="updateContent">
          <el-input
            v-model="form.updateContent"
            :placeholder="$t('appVersion.index.348485-15')"
            type="textarea"
            style="width: 400px"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">{{ $t('appVersion.index.348485-16') }}</el-button>
          <el-button @click="cancel">{{ $t('appVersion.index.348485-17') }}</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, getCurrentInstance, nextTick } from 'vue';
import { Search, Refresh, Plus, Edit, Delete, Download } from '@element-plus/icons-vue';
import { useDict } from '@/utils/dict/useDict';
import { listVersion, getVersion, delVersion, addVersion, updateVersion } from '@/api/system/appVersion';

const { proxy } = getCurrentInstance() as any;
const { iot_yes_no } = useDict('iot_yes_no');

const queryFormRef = ref();
const formRef = ref();
const fileUploadRef = ref();

const loading = ref(false);
const ids = ref<any[]>([]);
const single = ref(true);
const multiple = ref(true);
const showSearch = ref(true);
const total = ref(0);
const versionList = ref<any[]>([]);
const open = ref(false);
const title = ref('');
const form = ref<any>({});

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  versionName: undefined as any,
  version: undefined as any,
  isLiveUpdate: null as any,
});

const validateInteger = (rule: any, value: any, callback: any) => {
  if (value === undefined || value === null || value === '') return callback(new Error('请输入版本号'));
  if (!/^[+-]?\d+$/.test(value.toString())) return callback(new Error('版本号必须为整数'));
  callback();
};

const rules = reactive<any>({
  version: [{ required: true, validator: validateInteger, trigger: 'blur' }],
  versionName: [{ required: true, message: proxy?.$t('appVersion.index.348485-0'), trigger: 'blur' }],
  wgt: [{ required: true, message: proxy?.$t('appVersion.index.348485-20'), trigger: 'change' }],
  apk: [{ required: true, message: proxy?.$t('appVersion.index.348485-19'), trigger: 'change' }],
  updateContent: [{ required: true, message: proxy?.$t('appVersion.index.348485-21'), trigger: 'blur' }],
});

onMounted(() => {
  getList();
});

function getList() {
  loading.value = true;
  listVersion(queryParams).then((response: any) => {
    versionList.value = response.rows;
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
    version: null,
    versionName: null,
    isLiveUpdate: '1',
    apk: null,
    wgt: null,
    updateContent: null,
    delFlag: null,
    createBy: null,
    createTime: null,
    updateBy: null,
    updateTime: null,
    remark: null,
  };
  formRef.value?.resetFields();
}

function handleQuery() {
  queryParams.pageNum = 1;
  getList();
}
function resetQuery() {
  queryFormRef.value?.resetFields();
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
  title.value = proxy?.$t('appVersion.index.348485-22');
}

function handleUpdate(row: any) {
  reset();
  const id = row.id || ids.value;
  getVersion(id).then((response: any) => {
    if (response.code === 200) {
      form.value = response.data;
      open.value = true;
      title.value = proxy?.$t('appVersion.index.348485-23');
    } else {
      proxy?.$modal.msgError(response.msg);
    }
  });
}

function submitForm() {
  formRef.value?.validate((valid: boolean) => {
    if (valid) {
      if (form.value.id != null) {
        updateVersion(form.value).then((response: any) => {
          if (response.code === 200) {
            proxy?.$modal.msgSuccess(proxy?.$t('appVersion.index.348485-24'));
            open.value = false;
            getList();
          } else proxy?.$modal.msgError(response.msg);
        });
      } else {
        addVersion(form.value).then((response: any) => {
          if (response.code === 200) {
            proxy?.$modal.msgSuccess(proxy?.$t('appVersion.index.348485-25'));
            open.value = false;
            getList();
          } else proxy?.$modal.msgError(response.msg);
        });
      }
    }
  });
}

function getFileApkPath(data: any) {
  form.value.apk = data;
  nextTick(() => {
    formRef.value?.clearValidate('apk');
  });
}

function getFileWgtPath(data: any) {
  form.value.wgt = data;
  nextTick(() => {
    formRef.value?.clearValidate('wgt');
  });
}

function changeIsLiveUpdate() {
  form.value.apk = null;
  form.value.wgt = null;
}

function handleDelete(row: any) {
  const delIds = row.id || ids.value;
  proxy?.$modal
    .confirm(proxy?.$t('appVersion.index.348485-27', [delIds]))
    .then(() => {
      return delVersion(delIds);
    })
    .then(() => {
      getList();
      proxy?.$modal.msgSuccess(proxy?.$t('appVersion.index.348485-26'));
    })
    .catch(() => {});
}

function handleExport() {
  proxy?.download('iot/version/export', { ...queryParams }, `version_${new Date().getTime()}.xlsx`);
}

function getDownloadUrl(path: string) {
  return window.location.origin + import.meta.env.VITE_APP_BASE_API + path;
}

function getRowKeys(row: any) {
  return row.version;
}
</script>

<style lang="scss" scoped>
.system-appVersion {
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

  .table-link-cell {
    width: 100%;
    min-width: 0;
    overflow: hidden;

    .link {
      display: block;
      width: 100%;
      min-width: 0;

      :deep(.el-link__inner) {
        display: block;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }
    }
  }
}
</style>
