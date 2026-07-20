<template>
  <div class="notify-channel">
    <el-card v-show="showSearch" class="top-card-wrap">
      <el-form
        @submit.prevent
        :model="queryParams"
        ref="queryFormRef"
        :inline="true"
        label-width="78px"
        style="margin-bottom: -18px; padding: 3px 0 0 0"
      >
        <el-form-item prop="name">
          <el-input
            v-model="queryParams.name"
            :placeholder="$t('notify.channel.index.333541-1')"
            clearable
            style="width: 192px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item prop="channelType">
          <el-select
            v-model="queryParams.channelType"
            :placeholder="$t('notify.channel.index.333541-3')"
            clearable
            style="width: 192px"
          >
            <el-option
              v-for="option in channelTypeList"
              :key="option.value"
              :label="option.label"
              :value="option.value"
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
      <el-row :gutter="10" style="margin-bottom: 15px">
        <el-col :span="1.5">
          <el-button type="primary" plain :icon="Plus" @click="handleAdd" v-hasPermi="['notify:channel:add']">
            {{ $t('add') }}
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            plain
            :icon="Delete"
            :disabled="multiple"
            @click="handleDelete"
            v-hasPermi="['notify:channel:remove']"
          >
            {{ $t('del') }}
          </el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
      </el-row>

      <el-table
        v-loading="loading"
        :data="channelList"
        :border="false"
        @selection-change="handleSelectionChange"
        ref="multipleTableRef"
        :row-key="getRowKeys"
      >
        <el-table-column :reserve-selection="true" type="selection" width="55" align="center" />
        <el-table-column :label="$t('notify.channel.index.333541-18')" align="center" prop="id" width="60" />
        <el-table-column :label="$t('notify.channel.index.333541-0')" align="left" prop="name" min-width="180" />
        <el-table-column :label="$t('notify.channel.index.333541-6')" align="center" prop="channelType" width="110">
          <template #default="scope">
            <dict-tag :options="notify_channel_type" :value="scope.row.channelType" />
          </template>
        </el-table-column>
        <el-table-column :label="$t('notify.channel.index.333541-7')" align="center" prop="provider" width="160" />
        <el-table-column :label="$t('template.index.891112-117')" align="center" prop="tenantName" width="150" />
        <el-table-column :label="$t('template.index.891112-118')" align="center" prop="createBy" width="150" />
        <el-table-column :label="$t('notify.channel.index.333541-8')" align="left" prop="createTime" min-width="190">
          <template #default="scope">
            <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d} {h}:{m}:{s}') }}</span>
          </template>
        </el-table-column>
        <el-table-column
          fixed="right"
          :label="$t('opation')"
          align="center"
          class-name="small-padding fixed-width"
          width="120"
        >
          <template #default="scope">
            <el-button
              size="small"
              :icon="View"
              link
              @click="handleUpdate(scope.row)"
              v-hasPermi="['notify:channel:query']"
            >
              {{ $t('detail') }}
            </el-button>
            <el-button
              size="small"
              :icon="Delete"
              link
              @click="handleDelete(scope.row)"
              v-hasPermi="['notify:channel:remove']"
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

    <!-- 添加或修改通知渠道对话框 -->
    <el-dialog :title="title" v-model="open" width="610px" append-to-body>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="130px">
        <el-form-item :label="$t('notify.channel.index.333541-0')" prop="name">
          <el-input v-model="form.name" :placeholder="$t('notify.channel.index.333541-1')" style="width: 390px" />
        </el-form-item>
        <el-form-item :label="$t('notify.channel.index.333541-2')" prop="channelType">
          <el-select
            v-model="form.channelType"
            :placeholder="$t('notify.channel.index.333541-3')"
            clearable
            style="width: 390px"
            @change="changeChannel"
          >
            <el-option
              v-for="option in channelTypeList"
              :key="option.value"
              :label="option.label"
              :value="option.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item :label="$t('notify.channel.index.333541-7')" prop="provider">
          <el-select
            :placeholder="$t('notify.channel.index.333541-10')"
            clearable
            style="width: 390px"
            v-model="form.provider"
            :disabled="form.channelType == null"
            @change="changeService"
          >
            <el-option v-for="provider in list" :key="provider.value" :label="provider.label" :value="provider.value" />
          </el-select>
        </el-form-item>
        <el-form-item v-for="(item, index) in configList" :key="index" :label="item.label">
          <el-input
            v-model="item.value"
            :placeholder="$t('notify.channel.index.333541-11')"
            v-if="item.type == 'string'"
            :autosize="{ minRows: 3, maxRows: 5 }"
            style="width: 390px"
            type="textarea"
          />
          <el-input
            v-model="item.value"
            :placeholder="$t('notify.channel.index.333541-11')"
            type="number"
            v-if="item.type == 'int'"
            style="width: 390px"
          />
          <editor v-model="item.value" :min-height="192" v-if="item.type == 'text'" />
          <el-switch
            v-model="item.value"
            active-color="#13ce66"
            inactive-color="#c0c0c0"
            v-if="item.type == 'boolean'"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm" v-hasPermi="['notify:channel:edit']" v-show="form.id">
            {{ $t('update') }}
          </el-button>
          <el-button type="primary" @click="submitForm" v-hasPermi="['notify:channel:add']" v-show="!form.id">
            {{ $t('add') }}
          </el-button>
          <el-button @click="cancel">{{ $t('cancel') }}</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, getCurrentInstance, nextTick } from 'vue';
import { Search, Refresh, Plus, Delete, View } from '@element-plus/icons-vue';
import { useDict } from '@/utils/dict/useDict';
import { parseTime } from '@/utils/ruoyi';
import {
  listChannel,
  getChannel,
  delChannel,
  addChannel,
  getConfigContent,
  updateChannel,
  getChannelMessage,
} from '@/api/notify/channel';

const { proxy } = getCurrentInstance() as any;
const { notify_channel_type } = useDict('notify_channel_type');

const queryFormRef = ref();
const formRef = ref();
const multipleTableRef = ref();

const loading = ref(true);
const ids = ref<any[]>([]);
const single = ref(true);
const multiple = ref(true);
const showSearch = ref(true);
const total = ref(0);
const channelList = ref<any[]>([]);
const channelTypeList = ref<any[]>([]);
const title = ref('');
const open = ref(false);
const configList = ref<any[]>([]);
const channelMsgList = ref<any[]>([]);
const channelChildren = ref<any[]>([]);
const list = ref<any[]>([]);
const form = ref<any>({});

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  name: null as any,
  channelType: null as any,
  provider: null as any,
  configContent: null as any,
});

const rules = reactive({
  name: [{ required: true, message: proxy?.$t('notify.channel.index.333541-12'), trigger: 'blur' }],
  channelType: [{ required: true, message: proxy?.$t('notify.channel.index.333541-13'), trigger: 'change' }],
  provider: [{ required: true, message: proxy?.$t('notify.channel.index.333541-14'), trigger: 'blur' }],
});

onMounted(() => {
  getList();
  getInfo();
});

/** 查询通知渠道列表 */
function getList() {
  loading.value = true;
  listChannel(queryParams).then((response: any) => {
    channelList.value = response.rows;
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
    name: null,
    channelType: null,
    provider: null,
    configContent: null,
    createBy: null,
    createTime: null,
    updateBy: null,
    updateTime: null,
    delFlag: null,
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
  title.value = proxy?.$t('notify.channel.index.333541-15');
  configList.value = [];
}

async function handleUpdate(row: any) {
  reset();
  const id = row.id || ids.value;
  nextTick(() => {
    form.value.channelType = row.channelType;
    form.value.provider = row.provider;
    getConfig();
  });
  setTimeout(() => {
    getChannel(id).then((response: any) => {
      form.value = response.data;
      open.value = true;
      title.value = proxy?.$t('notify.channel.index.333541-16');
      getServiceList();
      if (form.value.configContent != '') {
        const List = JSON.parse(form.value.configContent);
        for (let j = 0; j < configList.value.length; j++) {
          for (const key in List) {
            if (configList.value[j].attribute == key) {
              configList.value[j].value = List[key];
            }
          }
        }
      }
    });
  }, 500);
}

function getInfo() {
  loading.value = true;
  getChannelMessage().then((response: any) => {
    channelMsgList.value = response.data;
    channelTypeList.value = response.data.map((item: any) => {
      return { value: item.channelType, label: item.channelName };
    });
  });
  loading.value = false;
}

function changeChannel() {
  getServiceList();
  form.value.provider = '';
  configList.value = [];
}

function getServiceList() {
  const selectedChannel = form.value.channelType;
  channelChildren.value = channelMsgList.value
    .filter((item: any) => selectedChannel.includes(item.channelType))
    .map((item: any) => item.providerList);
  for (let i = 0; i < channelChildren.value.length; i++) {
    list.value = channelChildren.value[i].map((item: any) => {
      return { value: item.provider, label: item.providerName, config: item.configContent };
    });
  }
}

function changeService() {
  getServiceList();
  getConfig();
}

function getConfig() {
  getConfigContent(form.value.provider, form.value.channelType).then((res: any) => {
    configList.value = res.data.map((item: any) => {
      return { value: item.value, label: item.name, attribute: item.attribute, type: item.type };
    });
    for (let i = 0; i < configList.value.length; i++) {
      if (configList.value[i].type === 'boolean') {
        configList.value[i].value = Boolean(configList.value[i].value);
      }
    }
  });
}

function submitForm() {
  const configUseList = configList.value.map((item: any) => [item.attribute, item.value]);
  const object = Object.fromEntries(configUseList);
  const json = JSON.stringify(object);
  const { id, name, channelType, provider, tenantId, createBy } = form.value;
  let query: any = { name, channelType, provider, configContent: json };
  formRef.value?.validate((valid: boolean) => {
    if (valid) {
      if (id === undefined || id === null || id === '') {
        addChannel(query).then(() => {
          proxy?.$modal.msgSuccess(proxy?.$t('addSuccess'));
          open.value = false;
          getList();
        });
      } else {
        query = { ...query, id, tenantId, createBy };
        updateChannel(query).then(() => {
          proxy?.$modal.msgSuccess(proxy?.$t('updateSuccess'));
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
    .confirm(proxy?.$t('notify.channel.index.333541-17', [delIds]))
    .then(() => {
      return delChannel(delIds);
    })
    .then(() => {
      getList();
      proxy?.$modal.msgSuccess(proxy?.$t('delSuccess'));
      multipleTableRef.value?.clearSelection();
    })
    .catch(() => {});
}

function getRowKeys(row: any) {
  return row.id;
}

function handleExport() {
  proxy?.download('notify/channel/export', { ...queryParams }, `channel_${new Date().getTime()}.xlsx`);
}
</script>

<style lang="scss" scoped>
.notify-channel {
  padding: 20px;
  .top-card-wrap {
    margin-bottom: 15px;
    border-radius: 8px;
    width: 100%;
  }
  .card-wrap {
    padding-bottom: 100px;
  }
}
</style>
