<template>
  <div class="notify-template">
    <el-card v-show="showSearch" style="margin-bottom: 15px; border-radius: 8px; width: 100%">
      <el-form
        @submit.prevent
        :model="queryParams"
        ref="queryRef"
        :inline="true"
        label-width="78px"
        style="margin-bottom: -18px; padding: 3px 0 0 0"
      >
        <el-form-item prop="name">
          <el-input
            v-model="queryParams.name"
            :placeholder="$t('notify.template.index.333542-1')"
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
            @keyup.enter="handleQuery"
          >
            <el-option
              v-for="option in channelTypeList"
              :key="option.value"
              :label="option.label"
              :value="option.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item prop="serviceCode">
          <el-select
            v-model="queryParams.serviceCode"
            :placeholder="$t('notify.template.index.333542-3')"
            clearable
            style="width: 192px"
            @keyup.enter="handleQuery"
          >
            <el-option
              v-for="dict in notify_service_code"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <div style="float: right">
          <el-button type="primary" :icon="Search" @click="handleQuery">{{ $t('search') }}</el-button>
          <el-button :icon="Refresh" @click="resetQuery">{{ $t('reset') }}</el-button>
        </div>
      </el-form>
    </el-card>

    <el-card style="border-radius: 8px">
      <el-row :gutter="10" style="margin-bottom: 16px">
        <el-col :span="1.5">
          <el-button type="primary" plain :icon="Plus" @click="handleAdd" v-hasPermi="['notify:template:add']">
            {{ $t('add') }}
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            plain
            :icon="Delete"
            :disabled="multiple"
            @click="handleDelete"
            v-hasPermi="['notify:template:remove']"
          >
            {{ $t('del') }}
          </el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
      </el-row>

      <el-table
        v-loading="loading"
        :data="templateList"
        :border="false"
        @selection-change="handleSelectionChange"
        ref="multipleTableRef"
        :row-key="getRowKeys"
      >
        <el-table-column :reserve-selection="true" type="selection" width="55" align="center" />
        <el-table-column :label="$t('notify.template.index.333542-35')" align="left" prop="id" width="80" />
        <el-table-column :label="$t('notify.template.index.333542-0')" align="left" prop="name" min-width="190" />
        <el-table-column :label="$t('notify.channel.index.333541-2')" align="center" prop="channelType" width="90">
          <template #default="scope">
            <dict-tag :options="notify_channel_type" :value="scope.row.channelType" />
          </template>
        </el-table-column>
        <el-table-column
          :label="$t('notify.template.index.333542-5')"
          align="left"
          prop="channelName"
          min-width="180"
        ></el-table-column>
        <el-table-column
          :label="$t('notify.channel.index.333541-7')"
          align="center"
          prop="provider"
          width="140"
        ></el-table-column>
        <el-table-column :label="$t('notify.template.index.333542-2')" align="center" prop="serviceCode" width="110">
          <template #default="scope">
            <dict-tag :options="notify_service_code" :value="scope.row.serviceCode" />
          </template>
        </el-table-column>
        <el-table-column :label="$t('notify.template.index.333542-6')" align="center" prop="status" width="90">
          <template #default="scope">
            <el-switch
              v-model="scope.row.status"
              @change="handleStatus(scope.row)"
              :active-value="1"
              :inactive-value="0"
            />
          </template>
        </el-table-column>
        <el-table-column :label="$t('template.index.891112-117')" align="left" prop="tenantName" min-width="180" />
        <el-table-column :label="$t('template.index.891112-118')" align="center" prop="createBy" width="150" />
        <el-table-column :label="$t('notify.channel.index.333541-8')" align="left" prop="createTime" min-width="160">
          <template #default="scope">
            <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d} {h}:{m}:{s}') }}</span>
          </template>
        </el-table-column>
        <el-table-column
          fixed="right"
          :label="$t('opation')"
          align="center"
          class-name="small-padding fixed-width"
          width="185"
        >
          <template #default="scope">
            <el-button
              size="small"
              :icon="Position"
              link
              @click="handleTest(scope.row)"
              :disabled="scope.row.channelType === 'mqtt'"
            >
              {{ $t('notify.template.index.333542-7') }}
            </el-button>
            <el-button
              size="small"
              :icon="View"
              link
              @click="handleUpdate(scope.row)"
              v-hasPermi="['notify:template:query']"
            >
              {{ $t('detail') }}
            </el-button>
            <el-button
              size="small"
              :icon="Delete"
              link
              @click="handleDelete(scope.row)"
              v-hasPermi="['notify:template:remove']"
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

    <!-- 添加或修改通知模版对话框 -->
    <el-dialog :title="title" v-model="open" width="610px" append-to-body>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="125px">
        <el-form-item :label="$t('notify.template.index.333542-0')" prop="name">
          <el-input v-model="form.name" :placeholder="$t('notify.template.index.333542-1')" style="width: 380px" />
        </el-form-item>
        <el-form-item :label="$t('notify.template.index.333542-2')" prop="serviceCode">
          <el-select
            v-model="form.serviceCode"
            :placeholder="$t('notify.template.index.333542-3')"
            style="width: 380px"
          >
            <el-option
              v-for="dict in notify_service_code"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item :label="$t('notify.template.index.333542-5')" prop="channelId">
          <el-input
            style="width: 380px"
            readonly
            v-model="selectedAccountName"
            :placeholder="$t('notify.template.index.333542-9')"
          >
            <template #append>
              <el-button @click="selectChannel(form.id ? true : false)">
                {{ $t('device.device-edit.148398-6') }}
              </el-button>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item
          :label="$t('notify.template.index.333542-10')"
          prop="msgType"
          v-if="
            form.channelType === 'wechat' &&
            (form.provider === 'wecom_apply' || form.provider === 'wecom_robot') &&
            !!form.channelId
          "
        >
          <el-select
            :placeholder="$t('notify.template.index.333542-11')"
            style="width: 380px"
            @change="getTemplateMsg"
            v-model="form.msgType"
          >
            <el-option
              v-for="dict in wecom_msg_type"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item
          :label="$t('views.iot.bridge.index.525282-28')"
          prop="method"
          v-if="form.channelType === 'http' && !!form.channelId"
        >
          <el-select
            v-model="form.httpform.method"
            :placeholder="$t('views.iot.bridge.index.525282-27')"
            style="width: 380px"
            @change="getTemplateMsg"
          >
            <el-option v-for="item in httpOptions" :key="item" :value="item"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item
          :label="$t('notify.template.index.333542-10')"
          prop="msgType"
          v-if="form.channelType === 'dingtalk' && !!form.channelId"
        >
          <el-select
            :placeholder="$t('notify.template.index.333542-12')"
            v-model="form.msgType"
            style="width: 380px"
            @change="getTemplateMsg"
          >
            <el-option
              v-for="dict in dingtalk_msg_type"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item v-for="(item, index) in configList" :key="index" :label="item.label">
          <el-input
            v-model="item.value"
            :placeholder="$t('notify.template.index.333542-13')"
            v-if="item.type == 'string' && !form.httpform.method"
            style="width: 380px"
          />
          <el-input
            v-model="item.value"
            :placeholder="$t('notify.template.index.333542-13')"
            type="number"
            v-if="item.type == 'int'"
            style="width: 380px"
          />
          <editor
            v-model="item.value"
            :min-height="192"
            v-if="item.type == 'text'"
            :url_type="url_type"
            style="width: 380px"
          />
          <el-switch
            v-model="item.value"
            active-color="#13ce66"
            inactive-color="#c0c0c0"
            v-if="item.type == 'boolean'"
          ></el-switch>
          <fileUpload
            ref="uploadFileRef"
            :value="form.filePath"
            :limit="1"
            :fileSize="10"
            :uploadFileUrl="uploadUrl"
            :fileType="['docx', 'xlsx', 'ppt', 'txt', 'pdf', 'zip', 'jpg', 'png']"
            @input="getFilePath($event)"
            v-if="item.type == 'file' && item.attribute == 'attachment'"
          />
          <fileUpload
            ref="uploadFileRef"
            :value="form.filePath"
            :limit="1"
            :fileSize="10"
            :uploadFileUrl="uploadUrl"
            :fileType="['jpg', 'png']"
            @input="getFilePath($event)"
            v-if="item.type == 'file' && item.attribute == 'picUrl'"
          />
          <template v-if="item.type === 'string' && form.httpform.method">
            <el-input
              v-if="index === 0"
              :placeholder="$t('views.iot.bridge.index.525282-26')"
              v-model="form.httpform.hostUrlbody"
              style="width: 380px"
            >
              <template #prepend>
                <el-select
                  v-model="hostUrlhead"
                  :placeholder="$t('views.iot.bridge.index.525282-27')"
                  style="width: 90px"
                >
                  <el-option v-for="item in urlOptions" :key="item" :value="item"></el-option>
                </el-select>
              </template>
            </el-input>
            <div v-if="item.attribute === 'requestHeaders'">
              <div v-for="(header, idx) in requestHeadersMap" :key="idx">
                <el-row>
                  <el-col :span="8">
                    <el-input size="small" :placeholder="$t('views.iot.bridge.index.525282-30')" v-model="header.key">
                      <template #prepend>{{ $t('views.iot.bridge.index.525282-32') }}:</template>
                    </el-input>
                  </el-col>
                  <el-col :span="8" style="margin: 0 15px 0 25px">
                    <el-input size="small" :placeholder="$t('views.iot.bridge.index.525282-31')" v-model="header.value">
                      <template #prepend>{{ $t('views.iot.bridge.index.525282-33') }}:</template>
                    </el-input>
                  </el-col>
                  <div>
                    <el-button
                      size="small"
                      plain
                      type="danger"
                      style="padding: 5px"
                      :icon="Delete"
                      @click="handleRemoveAction(idx)"
                    >
                      {{ $t('del') }}
                    </el-button>
                  </div>
                </el-row>
              </div>
              <div>
                +
                <a style="color: #486ff2" @click="handleAddAction()">{{ $t('views.iot.bridge.index.525282-98') }}</a>
              </div>
            </div>
            <div v-else-if="item.attribute === 'requestParams'">
              <div v-for="(param, idx) in requestQuerysMap" :key="idx">
                <el-row>
                  <el-col :span="8">
                    <el-input size="small" :placeholder="$t('views.iot.bridge.index.525282-30')" v-model="param.key">
                      <template #prepend>{{ $t('views.iot.bridge.index.525282-32') }}:</template>
                    </el-input>
                  </el-col>
                  <el-col :span="8" style="margin: 0 15px 0 25px">
                    <el-input size="small" :placeholder="$t('views.iot.bridge.index.525282-31')" v-model="param.value">
                      <template #prepend>{{ $t('views.iot.bridge.index.525282-33') }}:</template>
                    </el-input>
                  </el-col>
                  <div>
                    <el-button
                      size="small"
                      plain
                      type="danger"
                      style="padding: 5px"
                      :icon="Delete"
                      @click="handleRemoveQuerys(idx)"
                    >
                      {{ $t('del') }}
                    </el-button>
                  </div>
                </el-row>
              </div>
              <div>
                +
                <a style="color: #486ff2" @click="handleAddQuerys()">{{ $t('views.iot.bridge.index.525282-34') }}</a>
              </div>
            </div>
            <div v-else-if="item.attribute === 'requestBody'">
              <el-input
                v-model="form.httpform.requestBody"
                type="textarea"
                :rows="6"
                :placeholder="$t('views.iot.bridge.index.525282-37')"
                style="width: 380px"
                :autosize="{ minRows: 3, maxRows: 5 }"
              />
            </div>
          </template>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm" v-hasPermi="['notify:template:edit']" v-show="form.id">
            {{ $t('update') }}
          </el-button>
          <el-button type="primary" @click="submitForm" v-hasPermi="['notify:template:add']" v-show="!form.id">
            {{ $t('add') }}
          </el-button>
          <el-button @click="handleCancel">{{ $t('cancel') }}</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 测试按钮对话框 -->
    <el-dialog :title="title" v-model="isTestDialog" width="610px" append-to-body>
      <el-form ref="testFormRef" :model="testForm" :rules="testRules" label-width="125px">
        <el-form-item :label="$t('notify.template.index.333542-14')" prop="account" v-if="testForm.account != null">
          <el-input
            v-model="testForm.account"
            :placeholder="$t('notify.template.index.333542-15')"
            style="width: 380px"
          />
        </el-form-item>
        <div v-for="(item, index) in Object.keys(testForm.variables)" :key="index">
          <el-form-item
            :label="item"
            :prop="'variables.' + item"
            :rules="{ required: true, message: $t('notify.template.index.333542-16'), trigger: 'blur' }"
          >
            <el-input
              v-model="testForm.variables[item]"
              :placeholder="$t('notify.template.index.333542-16')"
              clearable
              style="width: 380px"
            ></el-input>
          </el-form-item>
        </div>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="handleTestConfirm">{{ $t('confirm') }}</el-button>
          <el-button @click="handleTestCancel">{{ $t('cancel') }}</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 选择渠道账号对话框 -->
    <el-dialog
      :title="$t('notify.template.index.333542-9')"
      v-model="openChannelForm"
      width="910px"
      append-to-body
      @close="resetChannelDialogFilters"
    >
      <el-form label-width="68px" :model="channelForm" :inline="true">
        <el-form-item>
          <el-select
            v-model="channelForm.channelType"
            :placeholder="$t('notify.channel.index.333541-3')"
            clearable
            @change="onDialogChannelTypeChange"
            style="width: 192px"
          >
            <el-option
              v-for="option in channelTypeList"
              :key="option.value"
              :label="option.label"
              :value="option.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-select
            v-model="channelForm.provider"
            :placeholder="$t('notify.channel.index.333541-10')"
            clearable
            :disabled="!dialogProvidersList.length"
            @change="onDialogProviderChange"
            style="width: 192px"
          >
            <el-option
              v-for="option in dialogProvidersList"
              :key="option.value"
              :label="option.label"
              :value="option.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="searchChannelAccounts">
            {{ $t('search') }}
          </el-button>
        </el-form-item>
        <el-form-item>
          <el-button :icon="Refresh" @click="resetChannelDialogFilters">{{ $t('reset') }}</el-button>
        </el-form-item>
        <el-table v-loading="loadingChannel" :data="accountList" @row-click="rowClick" size="small" :border="false">
          <el-table-column :label="$t('device.device-edit.148398-6')" width="80" align="center">
            <template #default="scope">
              <input
                type="radio"
                v-model="selectedChannelId"
                :value="scope.row.id || scope.row.value"
                name="channelSelection"
              />
            </template>
          </el-table-column>
          <el-table-column prop="name" :label="$t('notify.template.index.333542-5')" min-width="220" />
          <el-table-column
            prop="provider"
            :label="$t('notify.channel.index.333541-7')"
            min-width="160"
            align="center"
          />
        </el-table>
        <pagination
          style="margin-bottom: 20px"
          layout="prev, pager, next"
          v-show="channelTotal > 0"
          :total="channelTotal"
          v-model:page="channelQueryParams.pageNum"
          v-model:limit="channelQueryParams.pageSize"
          @pagination="handleChannelPagination"
        />
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" :disabled="!selectedChannelId" @click="confirmChannelSelection">
            {{ $t('confirm') }}
          </el-button>
          <el-button @click="cancelChannelDialog" type="info">{{ $t('device.product-list.058448-15') }}</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, getCurrentInstance, onMounted, nextTick } from 'vue';
import { useI18n } from 'vue-i18n';
import { Search, Refresh, Plus, Delete, View, Position } from '@element-plus/icons-vue';
import {
  listTemplate,
  getTemplate,
  delTemplate,
  addTemplate,
  updateTemplate,
  getUsableTempate,
  updateState,
  notifyTestTemplate,
  templateParams,
  getVariablesList,
} from '@/api/notify/template';
import { listChannel, getChannelMessage } from '@/api/notify/channel';
import { useDict } from '@/utils/dict/useDict';
import { parseTime } from '@/utils/ruoyi';

const { notify_channel_type, notify_service_code, dingtalk_msg_type, wecom_msg_type } = useDict(
  'notify_channel_type',
  'notify_service_code',
  'dingtalk_msg_type',
  'wecom_msg_type'
);
const { proxy } = getCurrentInstance() as any;
const { t } = useI18n();

const loading = ref(true);
const loadingChannel = ref(true);
const ids = ref<any[]>([]);
const single = ref(true);
const multiple = ref(true);
const showSearch = ref(true);
const total = ref(0);
const templateList = ref<any[]>([]);
const channelMsgList = ref<any[]>([]);
const url_type = ref(1);
const title = ref('');
const open = ref(false);
const isTestDialog = ref(false);
const openChannelForm = ref(false);
const uploadUrl = ref(import.meta.env.VITE_APP_BASE_API + '/common/upload');
const requestHeadersMap = ref<any[]>([{ key: '', value: '' }]);
const requestQuerysMap = ref<any[]>([{ key: '', value: '' }]);
const httpOptions = ref(['post', 'put', 'get']);
const hostUrlhead = ref('http://');
const urlOptions = ref(['http://', 'https://']);
const configList = ref<any[]>([]);
const notifyTestId = ref('');
const channelTypeList = ref<any[]>([]);
const selectedAccountName = ref('');
const accountList = ref<any[]>([]);
const channelTotal = ref(0);
const selectedChannelId = ref<any>(null);
const dialogProvidersList = ref<any[]>([]);
const multipleTableRef = ref<any>(null);
const formRef = ref<any>(null);
const testFormRef = ref<any>(null);
const queryRef = ref<any>(null);

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  name: null as any,
  serviceCode: null as any,
  channelType: null as any,
});
const channelQueryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  channelType: null as any,
  provider: null as any,
});

const form = ref<any>({
  channelId: null,
  status: 1,
  filePath: '',
  httpform: { method: '', hostUrl: '', requestBody: '', requestHeaders: '', requestParams: '' },
});

const rules = reactive<any>({
  name: [{ required: true, message: t('notify.template.index.333542-17'), trigger: 'blur' }],
  serviceCode: [{ required: true, message: t('notify.template.index.333542-18'), trigger: 'blur' }],
  msgType: [{ required: true, message: t('notify.template.index.333542-10'), trigger: 'blur' }],
  channelId: [{ required: true, message: t('notify.template.index.333542-22'), trigger: 'change' }],
});

const testForm = ref<any>({ account: '', variables: {} });
const testRules = reactive<any>({
  account: [{ required: true, message: t('notify.template.index.333542-24'), trigger: 'blur' }],
});
const channelForm = reactive<any>({ channelType: '', provider: '', channelId: '' });

let callbackFn: Function | null = null;

function getList() {
  loading.value = true;
  listTemplate(queryParams).then((response: any) => {
    templateList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}

function handleSelectionChange(selection: any[]) {
  ids.value = selection.map((item) => item.id);
  single.value = selection.length !== 1;
  multiple.value = !selection.length;
}

function getInfo() {
  loadingChannel.value = true;
  getChannelMessage().then((response: any) => {
    channelMsgList.value = response.data;
    channelTypeList.value = response.data.map((item: any) => ({ value: item.channelType, label: item.channelName }));
  });
  loadingChannel.value = false;
}

function getTemplateMsg() {
  const { channelId, msgType, httpform } = form.value;
  const params = { channelId, msgType, method: httpform?.method };
  templateParams(params).then((res: any) => {
    if (res.code === 200) {
      configList.value = res.data.map((item: any) => ({
        value: item.value,
        label: item.name,
        attribute: item.attribute,
        type: item.type,
      }));
    }
  });
}

function handleCancel() {
  open.value = false;
  reset();
}

function reset() {
  form.value = {
    id: null,
    name: null,
    serviceCode: null,
    channelCode: null,
    msgContent: null,
    redirectUri: null,
    provider: null,
    msgParams: null,
    msgType: null,
    channelId: null,
    filePath: '',
    httpform: { method: '', hostUrl: '', requestBody: '', requestHeaders: '', requestParams: '' },
  };
  selectedAccountName.value = '';
  Object.assign(channelForm, { channelType: '', provider: '', channelId: '' });
  accountList.value = [];
  dialogProvidersList.value = [];
  configList.value = [];
  requestHeadersMap.value = [];
  requestQuerysMap.value = [];
  hostUrlhead.value = 'http://';
  formRef.value?.resetFields();
}

function handleQuery() {
  queryParams.pageNum = 1;
  getList();
}
function resetQuery() {
  queryRef.value.resetFields();
  handleQuery();
}

function handleAdd() {
  reset();
  open.value = true;
  title.value = t('notify.template.index.333542-25');
}

async function handleUpdate(row: any) {
  try {
    reset();
    const id = row.id || ids.value;
    open.value = true;
    title.value = t('notify.template.index.333542-26');
    const response: any = await getTemplate(id);
    const data = response.data || {};
    form.value = { ...form.value, ...data };
    form.value.httpform = JSON.parse(response.data.msgParams || '{}');
    if (!form.value.httpform) {
      form.value.httpform = { method: '', hostUrl: '', requestBody: '', requestHeaders: '', requestParams: '' };
    }
    if (form.value.httpform.hostUrl) {
      const parts = form.value.httpform.hostUrl.split('://');
      if (parts.length === 2) {
        hostUrlhead.value = parts[0] + '://';
        form.value.httpform.hostUrlbody = parts[1];
      }
    }
    requestHeadersMap.value = [];
    if (form.value.httpform.requestHeaders) {
      try {
        const obj = JSON.parse(form.value.httpform.requestHeaders);
        for (let key in obj) {
          if (Object.prototype.hasOwnProperty.call(obj, key)) requestHeadersMap.value.push({ key, value: obj[key] });
        }
      } catch (e) {
        requestHeadersMap.value = [];
      }
    }
    requestQuerysMap.value = [];
    if (form.value.httpform.requestParams) {
      try {
        const obj = JSON.parse(form.value.httpform.requestParams);
        for (let key in obj) {
          if (Object.prototype.hasOwnProperty.call(obj, key)) requestQuerysMap.value.push({ key, value: obj[key] });
        }
      } catch (e) {
        requestQuerysMap.value = [];
      }
    }
    if (form.value.channelId) {
      fetchChannelNameById(form.value.channelId);
      getTemplateMsgAndParams();
    } else {
      getParamsMsg();
    }
  } catch (error) {
    console.error('数据获取失败', error);
  }
}

function handleAddAction() {
  requestHeadersMap.value.push({ key: '', value: '' });
}
function handleAddQuerys() {
  requestQuerysMap.value.push({ key: '', value: '' });
}
function handleRemoveAction(index: number) {
  requestHeadersMap.value.splice(index, 1);
  refreshRequestHeaders();
}
function handleRemoveQuerys(index: number) {
  requestQuerysMap.value.splice(index, 1);
  refreshRequestQuerys();
}

function refreshRequestHeaders() {
  if (requestHeadersMap.value.length) {
    const obj = Object.fromEntries(requestHeadersMap.value.map((item: any) => [item.key, item.value]));
    form.value.httpform.requestHeaders = JSON.stringify(obj);
  } else {
    form.value.httpform.requestHeaders = '';
  }
}
function refreshRequestQuerys() {
  if (requestQuerysMap.value.length) {
    const obj = Object.fromEntries(requestQuerysMap.value.map((item: any) => [item.key, item.value]));
    form.value.httpform.requestParams = JSON.stringify(obj);
  } else {
    form.value.httpform.requestParams = '';
  }
}

function getConfigJson() {
  form.value.httpform.hostUrl = hostUrlhead.value + form.value.httpform.hostUrlbody;
  if (requestHeadersMap.value.length > 0) {
    const headerObj: any = {};
    requestHeadersMap.value.forEach((item: any) => {
      if (item.key?.trim()) headerObj[item.key.trim()] = item.value?.trim() || '';
    });
    form.value.httpform.requestHeaders = JSON.stringify(headerObj);
  } else {
    form.value.httpform.requestHeaders = '';
  }
  if (requestQuerysMap.value.length > 0) {
    const paramObj: any = {};
    requestQuerysMap.value.forEach((item: any) => {
      if (item.key?.trim()) paramObj[item.key.trim()] = item.value?.trim() || '';
    });
    form.value.httpform.requestParams = JSON.stringify(paramObj);
  } else {
    form.value.httpform.requestParams = '';
  }
  delete form.value.httpform.hostUrlbody;
  form.value.msgParams = JSON.stringify(form.value.httpform);
}

async function fetchChannelNameById(channelId: any) {
  try {
    const params: any = {};
    if (form.value.channelType) params.channelType = form.value.channelType;
    if (form.value.provider) params.provider = form.value.provider;
    let res: any = await listChannel(params);
    let found: any = null;
    if (res?.code === 200 && Array.isArray(res.rows))
      found = res.rows.find((r: any) => r.id == channelId || r.value == channelId);
    if (!found) {
      const res2: any = await listChannel({});
      if (res2?.code === 200 && Array.isArray(res2.rows))
        found = res2.rows.find((r: any) => r.id == channelId || r.value == channelId);
    }
    selectedAccountName.value = found?.name || found?.label || found?.channelName || '';
  } catch (error) {
    console.error('获取渠道名称失败:', error);
    selectedAccountName.value = '';
  }
}

function getParamsMsg() {
  if (form.value.msgParams != null) {
    try {
      const msgParamsList = JSON.parse(form.value.msgParams);
      form.value.httpform = { ...form.value.httpform, ...msgParamsList };
      if (
        form.value.channelType == 'dingtalk' ||
        (form.value.channelType == 'wechat' && form.value.provider != 'mini_program') ||
        (form.value.channelType == 'wechat' && form.value.provider != 'public_account')
      ) {
        form.value.msgType = msgParamsList.msgType;
        delete msgParamsList.msgType;
        nextTick(() => {
          getTemplateMsg();
        });
      }
      if (form.value.channelType == 'http') {
        getTemplateMsg();
        if (form.value.httpform.hostUrl) {
          const parts = form.value.httpform.hostUrl.split('://');
          if (parts.length === 2) {
            hostUrlhead.value = parts[0] + '://';
            form.value.httpform.hostUrlbody = parts[1];
          }
        }
        requestQuerysMap.value = [];
        if (form.value.httpform.requestParams) {
          const obj = JSON.parse(form.value.httpform.requestParams);
          for (let key in obj) {
            if (Object.prototype.hasOwnProperty.call(obj, key)) requestQuerysMap.value.push({ key, value: obj[key] });
          }
        }
      }
      setTimeout(() => {
        for (let j = 0; j < configList.value.length; j++) {
          for (const key in msgParamsList) {
            if (configList.value[j].attribute === 'attachment') form.value.filePath = configList.value[j].value;
            if (configList.value[j].attribute === 'picUrl') form.value.filePath = msgParamsList[key];
            if (configList.value[j].attribute == key) configList.value[j].value = msgParamsList[key];
          }
        }
      }, 500);
    } catch (e) {
      console.error('解析msgParams失败:', e);
      configList.value = [];
      form.value.filePath = '';
    }
  } else {
    configList.value = [];
    form.value.filePath = '';
  }
}

function getFilePath(data: string) {
  form.value.filePath = 'http://' + window.location.host + import.meta.env.VITE_APP_BASE_API + data;
  for (let i = 0; i < configList.value.length; i++) {
    if (configList.value[i].attribute === 'attachment' || configList.value[i].attribute === 'picUrl')
      configList.value[i].value = form.value.filePath;
  }
}

function submitForm() {
  if (form.value.channelType === 'http') {
    if (form.value.httpform.method) form.value.httpform.method = form.value.httpform.method.toLowerCase();
    getConfigJson();
  } else {
    const configMsgList = configList.value.map((item: any) => [item.attribute, item.value]);
    const object: any = Object.fromEntries(configMsgList);
    if (form.value.msgType != null) object.msgType = form.value.msgType;
    form.value.msgParams = JSON.stringify(object);
  }
  const { id, name, channelId, channelType, provider, serviceCode, msgParams, tenantId, createBy } = form.value;
  let query: any = { name, channelId, channelType, provider, serviceCode, msgParams };
  formRef.value?.validate((valid: boolean) => {
    if (valid) {
      if (!id) {
        addTemplate(query).then(() => {
          proxy.$modal.msgSuccess(t('addSuccess'));
          open.value = false;
          getList();
        });
      } else {
        query = { ...query, id, tenantId, createBy };
        updateTemplate(query).then(() => {
          proxy.$modal.msgSuccess(t('updateSuccess'));
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
    .confirm(t('notify.template.index.333542-27', [delIds]))
    .then(() => {
      return delTemplate(delIds);
    })
    .then(() => {
      getList();
      proxy.$modal.msgSuccess(t('delSuccess'));
      multipleTableRef.value?.clearSelection();
    })
    .catch(() => {});
}

function handleStatus(row: any) {
  if (row.status == 1) {
    getUsableTempate(row).then((response: any) => {
      if (response.data > 0) {
        proxy.$modal
          .confirm(t('notify.template.index.333542-28'))
          .then(() => {
            updateState(row).then((res: any) => {
              if (res.code == 200) {
                getList();
                proxy.$modal.msgSuccess(t('notify.template.index.333542-29'));
              } else proxy.$modal.msgWarning(t('notify.template.index.333542-30'));
            });
          })
          .catch(() => {
            row.status = false;
            proxy.$modal.msgInfo(t('notify.template.index.333542-31'));
          });
      } else {
        updateState(row).then((res: any) => {
          if (res.code == 200) {
            getList();
            proxy.$modal.msgSuccess(t('notify.template.index.333542-29'));
          } else proxy.$modal.msgWarning(t('notify.template.index.333542-30'));
        });
      }
    });
  } else {
    const data = { channelType: row.channelType, serviceCode: row.serviceCode, id: row.id, status: row.status };
    updateTemplate(data).then((response: any) => {
      if (response.code == 200) {
        getList();
        proxy.$modal.msgSuccess(t('notify.template.index.333542-32'));
      } else proxy.$modal.msgWarning(t('notify.template.index.333542-33'));
    });
  }
}

function handleTest(row: any) {
  testReset();
  const { id, channelType, provider } = row;
  notifyTestId.value = id || ids.value;
  getVariablesList(notifyTestId.value, channelType, provider).then((res: any) => {
    if (res.code == 200) {
      if (res.data == '') {
        sendTest();
      } else {
        testForm.value.account = res.data.sendAccount;
        testForm.value.variables = res.data.variables !== '' ? JSON.parse(res.data.variables) : {};
        title.value = t('notify.template.index.333542-7');
        isTestDialog.value = true;
      }
    } else {
      proxy.$modal.msgError(res.msg);
    }
  });
}

function handleTestConfirm() {
  testFormRef.value?.validate((valid: boolean) => {
    if (valid) sendTest();
  });
}

function sendTest() {
  const { account, variables } = testForm.value;
  let params: any = { sendAccount: account, id: notifyTestId.value };
  if (Object.keys(variables).length !== 0) params.variables = JSON.stringify(variables);
  notifyTestTemplate(params).then((res: any) => {
    if (res.code == 200) {
      proxy.$modal.msgSuccess(t('notify.template.index.333542-34'));
      isTestDialog.value = false;
    } else {
      proxy.$modal.msgError(res.msg);
      isTestDialog.value = false;
    }
  });
}

function handleTestCancel() {
  isTestDialog.value = false;
  testReset();
}
function testReset() {
  testForm.value = { account: null, variables: {} };
}
function getRowKeys(row: any) {
  return row.id;
}

function selectChannel(isEdit = false) {
  openChannelForm.value = true;
  channelQueryParams.pageNum = 1;
  channelQueryParams.channelType = null;
  channelQueryParams.provider = null;
  loadAllChannelAccounts();
  if (isEdit) {
    nextTick(() => {
      selectedChannelId.value = form.value.channelId;
    });
  }
}

function loadAllChannelAccounts() {
  loadingChannel.value = true;
  listChannel({
    pageNum: channelQueryParams.pageNum,
    pageSize: channelQueryParams.pageSize,
    channelType: channelQueryParams.channelType,
    provider: channelQueryParams.provider,
  }).then((res: any) => {
    if (res.code === 200) {
      accountList.value = res.rows;
      channelTotal.value = res.total;
    } else proxy.$modal.msgError(res.msg);
    loadingChannel.value = false;
  });
}

function rowClick(channel: any) {
  if (channel) selectedChannelId.value = channel.id || channel.value;
}

function searchChannelAccounts() {
  const { channelType, provider } = channelForm;
  if (!channelType && !provider) {
    proxy.$modal.msgWarning(t('notify.template.index.333542-35'));
    return;
  }
  loadingChannel.value = true;
  channelQueryParams.channelType = channelType;
  channelQueryParams.provider = provider;
  listChannel(channelQueryParams).then((res: any) => {
    if (res.code === 200) {
      accountList.value = res.rows;
      channelTotal.value = res.total;
    } else proxy.$modal.msgError(res.msg);
    loadingChannel.value = false;
  });
}

function onDialogChannelTypeChange() {
  channelForm.provider = '';
  channelForm.channelId = '';
  dialogProvidersList.value = [];
  accountList.value = [];
  channelTotal.value = 0;
  selectedChannelId.value = null;
  if (!channelForm.channelType) {
    resetChannelDialogFilters();
    return;
  }
  const matched = channelMsgList.value.find((c: any) => c.channelType === channelForm.channelType);
  if (matched?.providerList)
    dialogProvidersList.value = matched.providerList.map((p: any) => ({ value: p.provider, label: p.providerName }));
  loadingChannel.value = true;
  channelQueryParams.pageNum = 1;
  channelQueryParams.channelType = channelForm.channelType;
  channelQueryParams.provider = null;
  listChannel(channelQueryParams).then((res: any) => {
    if (res.code === 200) {
      accountList.value = res.rows;
      channelTotal.value = res.total;
    }
    loadingChannel.value = false;
  });
}

function onDialogProviderChange() {
  accountList.value = [];
  channelTotal.value = 0;
  selectedChannelId.value = null;
  const { channelType, provider } = channelForm;
  if (channelType || provider) {
    loadingChannel.value = true;
    channelQueryParams.pageNum = 1;
    channelQueryParams.channelType = channelType;
    channelQueryParams.provider = provider;
    listChannel(channelQueryParams).then((res: any) => {
      if (res.code === 200) {
        accountList.value = res.rows;
        channelTotal.value = res.total;
      }
      loadingChannel.value = false;
    });
  }
}

function cancelChannelDialog() {
  openChannelForm.value = false;
  resetChannelDialogFilters();
}
function handleChannelPagination() {
  loadAllChannelAccounts();
}

function confirmChannelSelection() {
  if (!selectedChannelId.value) {
    proxy.$modal.msgWarning(t('notify.template.index.333542-36'));
    return;
  }
  const selected = accountList.value.find((c: any) => (c.id || c.value) === selectedChannelId.value);
  if (!selected) {
    proxy.$modal.msgWarning(t('notify.template.index.333542-36'));
    return;
  }
  form.value.channelId = selectedChannelId.value;
  selectedAccountName.value = selected.name || selected.label;
  form.value.channelType = channelForm.channelType || selected.channelType || null;
  form.value.provider = channelForm.provider || selected.provider || null;
  form.value.msgType = null;
  configList.value = [];
  if (form.value.channelType && form.value.provider) getTemplateMsg();
  openChannelForm.value = false;
}

function resetChannelDialogFilters() {
  Object.assign(channelForm, { channelType: '', provider: '', channelId: '' });
  dialogProvidersList.value = [];
  accountList.value = [];
  channelTotal.value = 0;
  selectedChannelId.value = null;
  Object.assign(channelQueryParams, { pageNum: 1, pageSize: 10, channelType: null, provider: null });
  loadAllChannelAccounts();
}

function getTemplateMsgAndParams() {
  const { channelId, msgType } = form.value;
  templateParams({ channelId, msgType }).then((res: any) => {
    if (res.code === 200) {
      configList.value = res.data.map((item: any) => ({
        value: item.value,
        label: item.name,
        attribute: item.attribute,
        type: item.type,
      }));
      nextTick(() => {
        getParamsMsg();
      });
    }
  });
}

onMounted(() => {
  getList();
  getInfo();
});

function handleExport() {
  proxy?.download('notify/template/export', { ...queryParams }, `template_${new Date().getTime()}.xlsx`);
}
</script>

<style lang="scss" scoped>
.notify-template {
  padding: 20px;
}
</style>
