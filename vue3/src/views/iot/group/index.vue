<template>
  <div class="iot-group">
    <el-card v-show="showSearch" style="margin-bottom: 15px; border-radius: 8px; width: 100%">
      <el-form
        ref="queryRef"
        :model="queryParams"
        :inline="true"
        @submit.prevent
        label-width="68px"
        style="margin-bottom: -18px; padding: 3px 0 0 0"
      >
        <el-form-item prop="groupName">
          <el-input
            v-model="queryParams.groupName"
            :placeholder="$t('iot.group.index.637432-1')"
            clearable
            style="width: 192px"
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
      <el-row :gutter="10" style="margin-bottom: 15px">
        <el-col :span="1.5">
          <el-button type="primary" plain :icon="Plus" @click="handleAdd" v-hasPermi="['iot:group:add']">
            {{ $t('iot.group.index.637432-5') }}
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button plain :icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['iot:group:remove']">
            {{ $t('del') }}
          </el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
      </el-row>

      <el-table
        v-loading="loading"
        :data="groupList"
        ref="multipleTableRef"
        :border="false"
        @selection-change="handleSelectionChange"
        :row-key="getRowKeys"
      >
        <el-table-column :reserve-selection="true" type="selection" width="55" align="center" />
        <el-table-column :label="$t('iot.group.index.637432-0')" align="left" prop="groupName" min-width="210" />
        <el-table-column :label="$t('iot.group.index.637432-6')" align="center" prop="groupOrder" width="100" />
        <el-table-column :label="$t('template.index.891112-118')" align="center" prop="createBy" min-width="120" />
        <el-table-column :label="$t('iot.group.index.637432-7')" align="center" prop="createTime" width="160">
          <template #default="scope">
            <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d}') }}</span>
          </template>
        </el-table-column>
        <el-table-column :label="$t('iot.group.index.637432-8')" align="center" prop="userName" width="120" />
        <el-table-column :label="$t('iot.group.index.637432-9')" align="left" prop="remark" min-width="180" />
        <el-table-column
          fixed="right"
          :label="$t('iot.group.index.637432-10')"
          align="center"
          class-name="small-padding fixed-width"
          width="300"
        >
          <template #default="scope">
            <el-button
              size="small"
              link
              :icon="Search"
              @click="handleViewDevice(scope.row.groupId)"
              v-hasPermi="['iot:device:list']"
            >
              {{ $t('iot.group.index.637432-11') }}
            </el-button>
            <el-button size="small" link :icon="Plus" @click="selectDevice(scope.row)" v-hasPermi="['iot:group:add']">
              {{ $t('iot.group.index.637432-12') }}
            </el-button>
            <el-button size="small" link :icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['iot:group:query']">
              {{ $t('update') }}
            </el-button>
            <el-button
              size="small"
              link
              :icon="Delete"
              @click="handleDelete(scope.row)"
              v-hasPermi="['iot:group:remove']"
            >
              {{ $t('iot.group.index.637432-14') }}
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

    <!-- 分组设备列表 -->
    <deviceList ref="groupDeviceListRef" :group="group"></deviceList>

    <!-- 添加或修改设备分组对话框 -->
    <el-dialog :title="title" v-model="open" width="600px" append-to-body>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item :label="$t('iot.group.index.637432-0')" prop="groupName">
          <el-input v-model="form.groupName" :placeholder="$t('iot.group.index.637432-1')" style="width: 400px" />
        </el-form-item>
        <el-form-item :label="$t('iot.group.index.637432-6')" prop="groupOrder">
          <el-input
            v-model="form.groupOrder"
            type="number"
            :placeholder="$t('iot.group.index.637432-15')"
            style="width: 400px"
          />
        </el-form-item>
        <el-form-item :label="$t('iot.group.index.637432-9')" prop="remark">
          <el-input
            v-model="form.remark"
            type="textarea"
            :placeholder="$t('iot.group.index.637432-16')"
            :autosize="{ minRows: 3, maxRows: 5 }"
            style="width: 400px"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm" v-show="form.groupId" v-hasPermi="['iot:group:edit']">
            {{ $t('update') }}
          </el-button>
          <el-button type="primary" @click="submitForm" v-hasPermi="['iot:group:add']" v-show="!form.groupId">
            {{ $t('add') }}
          </el-button>
          <el-button @click="cancel">{{ $t('iot.group.index.637432-19') }}</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, getCurrentInstance, onMounted, nextTick } from 'vue';
import { useRouter } from 'vue-router';
import { Search, Refresh, Plus, Edit, Delete } from '@element-plus/icons-vue';
import { listGroup, getGroup, delGroup, addGroup, updateGroup } from '@/api/iot/group';
import { parseTime } from '@/utils/ruoyi';
import deviceList from './device-list.vue';
const formRef = ref<any>(null);
const { proxy } = getCurrentInstance() as any;
const router = useRouter();

const loading = ref(true);
const ids = ref<any[]>([]);
const single = ref(true);
const multiple = ref(true);
const showSearch = ref(true);
const total = ref(0);
const groupList = ref<any[]>([]);
const title = ref('');
const open = ref(false);
const group = ref<any>({});
const form = ref<any>({});
const multipleTableRef = ref<any>(null);
const groupDeviceListRef = ref<any>(null);
const queryRef = ref<any>(null);

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  groupName: null as any,
  userId: null as any,
});

const rules = reactive<any>({
  groupName: [{ required: true, message: proxy.$t('iot.group.index.637432-20'), trigger: 'blur' }],
  groupOrder: [
    {
      required: true,
      validator: (_rule: any, value: any, callback: any) => {
        if (value > 127) return callback(new Error('最大值为127'));
        return callback();
      },
      message: proxy.$t('iot.group.index.637432-21'),
      trigger: 'blur',
    },
  ],
});

function handleViewDevice(groupId: any) {
  router.push({ path: '/iot/device/list', query: { t: Date.now(), groupId, searchShow: true } as any });
}

function getList() {
  loading.value = true;
  listGroup(queryParams).then((response: any) => {
    groupList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}

function cancel() {
  open.value = false;
  reset();
}

function reset() {
  form.value = { groupId: null, groupName: null, groupOrder: null, userId: null, userName: null, remark: null };
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
  ids.value = selection.map((item) => item.groupId);
  single.value = selection.length !== 1;
  multiple.value = !selection.length;
}

function handleAdd() {
  reset();
  open.value = true;
  title.value = proxy.$t('iot.group.index.637432-22');
  nextTick(() => {
    formRef.value?.clearValidate();
  });
}

function handleUpdate(row: any) {
  reset();
  const groupId = row.groupId || ids.value;
  getGroup(groupId).then((response: any) => {
    form.value = response.data;
    open.value = true;
    title.value = proxy.$t('iot.group.index.637432-23');
    nextTick(() => {
      formRef.value?.clearValidate();
    });
  });
}

function selectDevice(row: any) {
  group.value = row;
  groupDeviceListRef.value.openDeviceList = true;
}

function submitForm() {
  proxy.$refs['formRef'].validate((valid: boolean) => {
    if (valid) {
      if (form.value.groupId != null) {
        updateGroup(form.value).then((res: any) => {
          if (res.code === 200) {
            proxy.$modal.msgSuccess(proxy.$t('iot.group.index.637432-24'));
            getList();
          }
          open.value = false;
        });
      } else {
        addGroup(form.value).then((res: any) => {
          if (res.code) {
            proxy.$modal.msgSuccess(proxy.$t('iot.group.index.637432-25'));
            getList();
          }
          open.value = false;
        });
      }
    }
  });
}

function handleDelete(row?: any) {
  const groupIds = row?.groupId || ids.value;
  proxy.$modal
    .confirm(proxy.$t('iot.group.index.637432-26', [groupIds]))
    .then(() => {
      return delGroup(groupIds);
    })
    .then(() => {
      getList();
      proxy.$modal.msgSuccess(proxy.$t('iot.group.index.637432-27'));
      multipleTableRef.value.clearSelection();
    })
    .catch(() => {});
}

function getRowKeys(row: any) {
  return row.groupId;
}

/** 导出按钮操作 */
function handleExport() {
  proxy?.download('iot/group/export', { ...queryParams }, `group_${new Date().getTime()}.xlsx`);
}

onMounted(() => {
  getList();
});
</script>

<style lang="scss" scoped>
.iot-group {
  padding: 20px;
}
:deep(.el-dialog__body) {
  box-sizing: border-box;
  padding: 20px;
}
</style>
