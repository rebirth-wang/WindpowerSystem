<template>
  <div class="iot-protocol">
    <el-card v-show="showSearch" style="margin-bottom: 15px; width: 100%">
      <el-form
        :model="queryParams"
        ref="queryFormRef"
        :inline="true"
        label-width="68px"
        style="margin-bottom: -18px; padding: 3px 0 0 0"
      >
        <el-form-item prop="protocolName">
          <el-input
            v-model="queryParams.protocolName"
            :placeholder="$t('protocol.index.111542-1')"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item prop="protocolCode">
          <el-input
            v-model="queryParams.protocolCode"
            :placeholder="$t('protocol.index.111542-3')"
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

    <el-card style="border-radius: 8px">
      <el-row :gutter="10" style="margin-bottom: 15px">
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
      </el-row>

      <el-table v-loading="loading" :data="protocolList" :border="false">
        <el-table-column :label="$t('protocol.index.111542-0')" align="left" prop="protocolName" min-width="210" />
        <el-table-column :label="$t('protocol.index.111542-2')" align="left" prop="protocolCode" min-width="180" />
        <el-table-column :label="$t('protocol.index.111542-4')" align="left" prop="jarSign" min-width="220" />
        <el-table-column :label="$t('protocol.index.111542-5')" align="left" prop="protocolFileUrl" min-width="210" />
        <el-table-column :label="$t('protocol.index.111542-6')" align="center" prop="protocolType" min-width="80" />
        <el-table-column :label="$t('protocol.index.111542-7')" align="center" prop="display" min-width="90">
          <template #default="scope">
            <el-switch
              v-model="scope.row.display"
              :active-value="1"
              :inactive-value="0"
              @change="handleStatusChange(scope.row)"
              :disabled="!isEnableSwitch || !isAdmin"
            ></el-switch>
          </template>
        </el-table-column>
        <el-table-column
          fixed="right"
          :label="$t('opation')"
          align="center"
          class-name="small-padding fixed-width"
          width="75"
        >
          <template #default="scope">
            <el-button
              size="small"
              link
              :icon="Document"
              @click="handleDetails(scope.row)"
              v-hasPermi="['iot:protocol:query']"
            >
              {{ $t('detail') }}
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

    <!-- 添加或修改协议对话框 -->
    <el-dialog :title="title" v-model="open" width="600px" append-to-body>
      <el-form ref="formRef" :model="form" label-width="100px">
        <div style="padding: 0px 10px">
          <el-input v-model="form.dataFormat" type="textarea" :rows="18" style="width: 100%; font-family: monospace" />
        </div>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submit" :disabled="!isAdmin" v-hasPermi="['iot:protocol:edit']">
            {{ $t('update') }}
          </el-button>
          <el-button @click="cancel">{{ $t('cancel') }}</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, getCurrentInstance, onMounted } from 'vue';
import { Search, Refresh, Document } from '@element-plus/icons-vue';
import { listProtocol, getProtocol, updateProtocol } from '@/api/iot/protocol';
import { useUserStore } from '@/stores/modules/user';
import { checkPermi } from '@/utils/permission';

const { proxy } = getCurrentInstance() as any;
const userStore = useUserStore();

const loading = ref(true);
const isAdmin = ref(false);
const ids = ref<any[]>([]);
const single = ref(true);
const showSearch = ref(true);
const total = ref(0);
const protocolList = ref<any[]>([]);
const title = ref('');
const open = ref(false);
const isEnableSwitch = ref(false);
const queryFormRef = ref<any>(null);
const formRef = ref<any>(null);

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  protocolCode: null as any,
  protocolName: null as any,
  protocolFileUrl: null as any,
  protocolType: null as any,
  jarSign: null as any,
  protocolStatus: null as any,
});

const form = ref<any>({});

onMounted(() => {
  if (userStore.roles.includes('admin')) {
    isAdmin.value = true;
  }
  getList();
  if (checkPermi(['iot:protocol:edit'])) {
    isEnableSwitch.value = true;
  }
});

function getList() {
  loading.value = true;
  listProtocol(queryParams).then((response: any) => {
    protocolList.value = response.rows;
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
    protocolCode: null,
    protocolName: null,
    protocolFileUrl: null,
    protocolType: null,
    jarSign: null,
    createTime: null,
    updateTime: null,
    protocolStatus: 0,
    delFlag: null,
    dataFormat: '',
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

function handleDetails(row: any) {
  reset();
  const id = row.id || ids.value;
  getProtocol(id).then((response: any) => {
    form.value = response.data;
    open.value = true;
    title.value = proxy.$t('protocol.index.111542-11');
  });
}

function handleStatusChange(row: any) {
  const params = { id: row.id, display: row.display };
  updateProtocol(params).then((response: any) => {
    if (response.code == 200) {
      proxy.$modal.msgSuccess(proxy.$t('protocol.index.111542-12'));
    } else {
      proxy.$modal.msgError(response.msg);
    }
  });
}

function submit() {
  const params = { id: form.value.id, dataFormat: form.value.dataFormat };
  updateProtocol(params).then((response: any) => {
    if (response.code == 200) {
      proxy.$modal.msgSuccess(proxy.$t('protocol.index.111542-12'));
      open.value = false;
    } else {
      proxy.$modal.msgError(response.msg);
    }
  });
}

/** 导出按钮操作 */
function handleExport() {
  proxy?.download('iot/protocol/export', { ...queryParams }, `protocol_${new Date().getTime()}.xlsx`);
}
</script>

<style lang="scss" scoped>
.iot-protocol {
  padding: 20px;
}
</style>
