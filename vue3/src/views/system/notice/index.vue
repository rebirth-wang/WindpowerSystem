<template>
  <div class="system-notice">
    <el-card v-show="showSearch" class="search-card">
      <el-form
        @submit.prevent
        :model="queryParams"
        ref="queryFormRef"
        :inline="true"
        label-width="68px"
        class="search-form"
      >
        <el-form-item prop="noticeTitle">
          <el-input
            v-model="queryParams.noticeTitle"
            :placeholder="$t('system.notice.670989-1')"
            clearable
            style="width: 192px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item prop="createBy">
          <el-input
            v-model="queryParams.createBy"
            :placeholder="$t('system.notice.670989-3')"
            clearable
            style="width: 192px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item prop="noticeType">
          <el-select
            v-model="queryParams.noticeType"
            :placeholder="$t('system.notice.670989-5')"
            clearable
            style="width: 192px"
          >
            <el-option
              v-for="item in dict.type.sys_notice_type"
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
          <el-button type="primary" plain :icon="Plus" @click="handleAdd" v-hasPermi="['system:notice:add']">
            {{ $t('add') }}
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button plain :icon="Edit" :disabled="single" @click="handleUpdate()" v-hasPermi="['system:notice:edit']">
            {{ $t('update') }}
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            plain
            :icon="Delete"
            :disabled="multiple"
            @click="handleDelete()"
            v-hasPermi="['system:notice:remove']"
          >
            {{ $t('del') }}
          </el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
      </el-row>

      <el-table
        v-loading="loading"
        :data="noticeList"
        :border="false"
        @selection-change="handleSelectionChange"
        ref="multipleTableRef"
        :row-key="getRowKeys"
      >
        <el-table-column :reserve-selection="true" type="selection" width="55" align="center" />
        <el-table-column :label="$t('system.cache.list.093478-1')" align="left" prop="noticeId" width="80" />
        <el-table-column
          :label="$t('system.notice.670989-0')"
          align="left"
          prop="noticeTitle"
          :show-overflow-tooltip="true"
          min-width="200"
        />
        <el-table-column :label="$t('system.notice.670989-5')" align="center" prop="noticeType" width="100">
          <template #default="scope">
            <dict-tag :options="dict.type.sys_notice_type" :value="scope.row.noticeType" />
          </template>
        </el-table-column>
        <el-table-column :label="$t('status')" align="center" prop="status" width="100">
          <template #default="scope">
            <dict-tag :options="dict.type.sys_notice_status" :value="scope.row.status" />
          </template>
        </el-table-column>
        <el-table-column :label="$t('system.notice.670989-2')" align="center" prop="createBy" width="100" />
        <el-table-column :label="$t('creatTime')" align="center" prop="createTime" width="100">
          <template #default="scope">
            <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d}') }}</span>
          </template>
        </el-table-column>
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
              v-hasPermi="['system:notice:edit']"
            >
              {{ $t('update') }}
            </el-button>
            <el-button
              size="small"
              link
              :icon="Delete"
              @click="handleDelete(scope.row)"
              v-hasPermi="['system:notice:remove']"
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

    <!-- 添加或修改公告对话框 -->
    <el-dialog :title="title" v-model="open" width="840px" append-to-body>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item :label="$t('system.notice.670989-0')" prop="noticeTitle">
              <el-input v-model="form.noticeTitle" :placeholder="$t('system.notice.670989-1')" style="width: 250px" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="$t('system.notice.670989-5')" prop="noticeType">
              <el-select v-model="form.noticeType" :placeholder="$t('system.notice.670989-6')" style="width: 250px">
                <el-option
                  v-for="item in dict.type.sys_notice_type"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item :label="$t('status')">
              <el-radio-group v-model="form.status">
                <el-radio v-for="item in dict.type.sys_notice_status" :key="item.value" :value="Number(item.value)">
                  {{ item.label }}
                </el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item :label="$t('system.notice.670989-7')">
              <editor v-model="form.noticeContent" :min-height="192" style="width: 659px" />
            </el-form-item>
          </el-col>
        </el-row>
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
import { ref, reactive, getCurrentInstance, onMounted } from 'vue';
import { useI18n } from 'vue-i18n';
import { Search, Refresh, Plus, Edit, Delete } from '@element-plus/icons-vue';
import { listNotice, getNotice, delNotice, addNotice, updateNotice } from '@/api/system/notice';
import { useDict } from '@/utils/dict/useDict';
import { parseTime } from '@/utils/ruoyi';

const { t } = useI18n();
const { proxy } = getCurrentInstance()!;
const { dict } = useDict('sys_notice_status', 'sys_notice_type');

const loading = ref(true);
const ids = ref<any[]>([]);
const single = ref(true);
const multiple = ref(true);
const showSearch = ref(true);
const total = ref(0);
const noticeList = ref<any[]>([]);
const title = ref('');
const open = ref(false);
const queryFormRef = ref();
const formRef = ref();
const multipleTableRef = ref();

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  noticeTitle: undefined as string | undefined,
  createBy: undefined as string | undefined,
  status: undefined as string | undefined,
  noticeType: undefined as string | undefined,
});
const form = ref<any>({});
const rules = reactive({
  noticeTitle: [{ required: true, message: t('system.notice.670989-9'), trigger: 'blur' }],
  noticeType: [{ required: true, message: t('system.notice.670989-10'), trigger: 'change' }],
});

/** 查询公告列表 */
function getList() {
  loading.value = true;
  listNotice(queryParams).then((response: any) => {
    noticeList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}

/** 取消按钮 */
function cancel() {
  open.value = false;
  reset();
}

/** 表单重置 */
function reset() {
  form.value = {
    noticeId: undefined,
    noticeTitle: undefined,
    noticeType: undefined,
    noticeContent: undefined,
    status: 0,
  };
  formRef.value?.resetFields();
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.pageNum = 1;
  getList();
}

/** 重置按钮操作 */
function resetQuery() {
  queryFormRef.value?.resetFields();
  handleQuery();
}

/** 多选框选中数据 */
function handleSelectionChange(selection: any[]) {
  ids.value = selection.map((item) => item.noticeId);
  single.value = selection.length !== 1;
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
function handleAdd() {
  reset();
  open.value = true;
  title.value = t('system.notice.670989-11');
}

/** 修改按钮操作 */
function handleUpdate(row?: any) {
  reset();
  const noticeId = row?.noticeId || ids.value;
  getNotice(noticeId).then((response: any) => {
    form.value = response.data;
    open.value = true;
    title.value = t('system.notice.670989-12');
  });
}

/** 提交按钮 */
function submitForm() {
  formRef.value?.validate((valid: boolean) => {
    if (valid) {
      if (form.value.noticeId != undefined) {
        updateNotice(form.value).then(() => {
          (proxy as any).$modal.msgSuccess(t('updateSuccess'));
          open.value = false;
          getList();
        });
      } else {
        addNotice(form.value).then(() => {
          (proxy as any).$modal.msgSuccess(t('addSuccess'));
          open.value = false;
          getList();
        });
      }
    }
  });
}

/** 删除按钮操作 */
function handleDelete(row?: any) {
  const noticeIds = row?.noticeId || ids.value;
  (proxy as any).$modal
    .confirm(t('system.notice.670989-13', [noticeIds]))
    .then(() => {
      return delNotice(noticeIds);
    })
    .then(() => {
      getList();
      (proxy as any).$modal.msgSuccess(t('delSuccess'));
      multipleTableRef.value?.clearSelection();
    })
    .catch(() => {});
}

function getRowKeys(row: any) {
  return row.noticeId;
}

onMounted(() => {
  getList();
});
</script>

<style lang="scss" scoped>
.system-notice {
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
