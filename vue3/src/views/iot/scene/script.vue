<template>
  <div class="iot-scene-script">
    <el-card v-show="showSearch" class="search-card">
      <el-form :model="queryParams" ref="queryFormRef" :inline="true" class="search-form">
        <el-form-item prop="scriptId">
          <el-input
            v-model="queryParams.scriptId"
            :placeholder="$t('script.349087-1')"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item prop="scriptName">
          <el-input
            v-model="queryParams.scriptName"
            :placeholder="$t('script.349087-3')"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item prop="productName">
          <el-input
            v-model="queryParams.productName"
            :placeholder="$t('script.349087-5')"
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
      <el-row :gutter="10" style="margin-bottom: 15px">
        <el-col :span="1.5">
          <el-button type="primary" plain :icon="Plus" @click="handleAdd" v-hasPermi="['iot:script:add']">
            {{ $t('add') }}
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button plain :icon="Plus" @click="handleAllowPackage" v-hasPermi="['iot:script:add']">
            {{ $t('script.349087-43') }}
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button plain :icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['iot:script:remove']">
            {{ $t('del') }}
          </el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
      </el-row>

      <el-table
        v-loading="loading"
        :data="scriptList"
        :border="false"
        @selection-change="handleSelectionChange"
        ref="multipleTableRef"
        :row-key="getRowKeys"
      >
        <el-table-column :reserve-selection="true" type="selection" width="55" align="center" />
        <el-table-column :label="$t('script.349087-4')" align="left" prop="scriptName" min-width="210" />
        <el-table-column :label="$t('script.349087-5')" align="left" prop="productName" min-width="190" />
        <el-table-column :label="$t('script.349087-0')" align="center" prop="scriptId" width="190" />
        <el-table-column :label="$t('script.349087-6')" align="center" prop="status" width="110">
          <template #default="scope">
            <dict-tag :options="rule_script_event" :value="scope.row.scriptEvent" size="small" />
          </template>
        </el-table-column>
        <el-table-column :label="$t('script.349087-7')" align="center" prop="status" width="110">
          <template #default="scope">
            <dict-tag :options="rule_script_action" :value="scope.row.scriptAction" size="small" />
          </template>
        </el-table-column>
        <el-table-column :label="$t('script.349087-8')" align="center" prop="scriptLanguage" width="110" />
        <el-table-column :label="$t('alert.index.236501-44')" align="center" prop="enable" width="110">
          <template #default="scope">
            <el-switch
              v-model="scope.row.enable"
              :active-value="1"
              :inactive-value="0"
              @change="handleEnableChange(scope.row)"
              :disabled="isDisabled"
            ></el-switch>
          </template>
        </el-table-column>
        <el-table-column :label="$t('script.349087-9')" align="center" prop="scriptOrder" />
        <el-table-column :label="$t('template.index.891112-117')" align="left" prop="userName" min-width="150" />
        <el-table-column :label="$t('template.index.891112-118')" align="left" prop="createBy" min-width="130" />
        <el-table-column
          fixed="right"
          :label="$t('opation')"
          align="center"
          class-name="small-padding fixed-width"
          width="200"
        >
          <template #default="scope">
            <el-button
              size="small"
              link
              :icon="View"
              @click="handleUpdate(scope.row)"
              v-hasPermi="['iot:script:query']"
            >
              {{ $t('look') }}
            </el-button>
            <el-button
              size="small"
              link
              :icon="Calendar"
              @click="handleLog(scope.row.scriptId)"
              v-hasPermi="['iot:script:query']"
            >
              {{ $t('script.349087-36') }}
            </el-button>
            <el-button
              size="small"
              link
              :icon="Delete"
              @click="handleDelete(scope.row)"
              v-hasPermi="['iot:script:remove']"
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

    <!-- 添加或修改规则引擎脚本对话框 -->
    <el-dialog
      :title="title"
      v-model="open"
      width="830px"
      append-to-body
      :close-on-click-modal="false"
      :close-on-press-escape="false"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="110px">
        <el-row :gutter="10">
          <el-col :span="12">
            <el-form-item :label="$t('script.349087-4')" prop="scriptName">
              <el-input v-model="form.scriptName" :placeholder="$t('script.349087-3')" style="width: 250px" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="$t('script.349087-9')" prop="scriptOrder">
              <el-input-number
                v-model="form.scriptOrder"
                :placeholder="$t('script.349087-3')"
                controls-position="right"
                style="width: 250px"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="$t('script.349087-10')" prop="scriptEvent">
              <el-select v-model="form.scriptEvent" :placeholder="$t('script.349087-11')" style="width: 250px">
                <el-option
                  v-for="dict in rule_script_event"
                  :key="dict.label"
                  :label="dict.label"
                  :value="Number(dict.value)"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12" style="flex: 0 0 50%" v-if="form.scriptEvent === 5 || form.scriptEvent === 6">
            <el-form-item :label="$t('script.349087-32')" prop="bridgeName">
              <el-input readonly v-model="form.bridgeName" :placeholder="$t('script.349087-33')" style="width: 250px">
                <template #append>
                  <el-button @click="handleSelectBridge()">{{ $t('script.349087-34') }}</el-button>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12" v-if="form.scriptEvent !== 5 && form.scriptEvent !== 6">
            <el-form-item :label="$t('script.349087-5')" prop="productName">
              <el-input readonly v-model="form.productName" :placeholder="$t('script.349087-14')" style="width: 250px">
                <template #append>
                  <el-button @click="handleSelectProduct()">{{ $t('script.349087-34') }}</el-button>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item prop="scriptAction">
              <template #label>
                <span class="span-box">
                  <span>{{ $t('script.349087-7') }}</span>
                  <el-tooltip :content="$t('script.349087-41')" placement="top">
                    <el-icon style="margin-left: 3px"><QuestionFilled /></el-icon>
                  </el-tooltip>
                </span>
              </template>
              <el-select v-model="form.scriptAction" :placeholder="$t('script.349087-12')" style="width: 250px">
                <el-option
                  v-for="dict in rule_script_action"
                  :key="dict.label"
                  :label="dict.label"
                  :value="Number(dict.value)"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="$t('script.349087-13')" prop="enable">
              <el-switch v-model="form.enable" :active-value="1" :inactive-value="0" />
            </el-form-item>
          </el-col>
          <el-col :span="12" style="flex: 0 0 50%">
            <el-form-item :label="$t('script.349087-28')" v-show="form.scriptAction != 1 && form.scriptAction != 2">
              <el-button type="primary" v-if="form.scriptAction == 3" @click="handleSelectHttpClient()" size="small">
                {{ $t('script.349087-29') }}
              </el-button>
              <el-button type="primary" v-if="form.scriptAction == 4" @click="handleSelectMqttClient()" size="small">
                {{ $t('script.349087-30') }}
              </el-button>
              <el-button type="primary" v-if="form.scriptAction == 5" @click="handleSelectDbSave()" size="small">
                {{ $t('script.349087-31') }}
              </el-button>
            </el-form-item>
          </el-col>
          <el-col :span="12" style="float: right"></el-col>
        </el-row>
      </el-form>
      <div style="padding: 0px 10px" @click="editClick">
        <monaco-editor
          :key="editorKey"
          :value="form.scriptData"
          @change="handleEditorChange"
          style="margin-top: 10px"
        />
      </div>
      <div style="padding: 0 10px; margin: 10px 0">
        <el-alert
          :title="validateMsg"
          type="success"
          show-icon
          v-if="isValidate && validateMsg"
          :closable="false"
        ></el-alert>
        <el-alert
          :title="validateMsg"
          type="error"
          show-icon
          v-if="!isValidate && validateMsg"
          :closable="false"
        ></el-alert>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <span style="float: left">
            <el-link
              style="line-height: 40px; padding-left: 20px"
              underline="never"
              type="primary"
              href="https://fastbee.cn/doc/pages/rule_engine/"
              target="_blank"
            >
              {{ $t('script.349087-16') }}
            </el-link>
          </span>
          <el-button type="success" @click="handleValidate">{{ $t('script.349087-17') }}</el-button>
          <el-tooltip :content="$t('script.349087-42')" placement="top" v-if="!isValidate">
            <el-button
              type="primary"
              @click="submitForm"
              v-hasPermi="['iot:script:edit']"
              v-show="form.scriptId"
              :disabled="!isValidate"
            >
              {{ $t('update') }}
            </el-button>
          </el-tooltip>
          <el-button
            type="primary"
            @click="submitForm"
            v-hasPermi="['iot:script:edit']"
            v-show="form.scriptId"
            v-else
            :disabled="!isValidate"
          >
            {{ $t('update') }}
          </el-button>
          <el-tooltip :content="$t('script.349087-42')" placement="top" v-if="!isValidate">
            <el-button
              type="primary"
              @click="submitForm"
              v-hasPermi="['iot:script:add']"
              v-show="!form.scriptId"
              :disabled="!isValidate"
            >
              {{ $t('add') }}
            </el-button>
          </el-tooltip>
          <el-button
            type="primary"
            @click="submitForm"
            v-hasPermi="['iot:script:add']"
            v-show="!form.scriptId"
            v-else
            :disabled="!isValidate"
          >
            {{ $t('add') }}
          </el-button>
          <el-button @click="cancel">{{ $t('cancel') }}</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 日志弹窗 -->
    <el-dialog
      class="script-log-dialog"
      :title="title"
      v-model="openLog"
      width="700px"
      append-to-body
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      :fullscreen="isFullLogDialog"
    >
      <template #header>
        <div class="title">{{ title }}</div>
        <div class="menu" @click="isFullLogDialog = !isFullLogDialog">
          <el-icon><FullScreen /></el-icon>
        </div>
      </template>
      <div
        ref="logContainerRef"
        v-loading="logLoading"
        :element-loading-text="$t('script.349087-39')"
        style="
          border: 1px solid #ccc;
          border-radius: 4px;
          height: 450px;
          background-color: #181818;
          color: #fff;
          padding: 10px;
          line-height: 20px;
          overflow: auto;
        "
      >
        <pre style="white-space: pre-wrap; word-wrap: break-word">{{ logs }}</pre>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="cancelLog">{{ $t('script.349087-38') }}</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 引入包白名单弹窗 -->
    <el-dialog
      class="script-allowpackage-dialog"
      :title="$t('script.349087-43')"
      v-model="openAllowpackage"
      width="580px"
      append-to-body
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      :fullscreen="false"
    >
      <el-button
        type="primary"
        plain
        :icon="Plus"
        size="small"
        @click="handleAddAllowPackage"
        v-hasPermi="['iot:script:add']"
      >
        新增包白名单
      </el-button>
      <div style="padding: 10px">
        <el-table :data="allowPackage" :border="false">
          <el-table-column :label="$t('script.349087-44')" align="left" width="400">
            <template #default="scope">
              <span>{{ scope.row }}</span>
            </template>
          </el-table-column>
          <el-table-column :label="$t('opation')" align="center" width="120">
            <template #default="scope">
              <el-button
                link
                size="small"
                :icon="Delete"
                style="color: #f56c6c"
                @click="handleDelAllowPackage(scope.row, scope.$index)"
              >
                {{ $t('del') }}
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="cancelAllowpackage">{{ $t('cancel') }}</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 产品列表 -->
    <productList ref="productListRef" @productEvent="getSelectProduct"></productList>
    <!-- 客户端列表 -->
    <clientList ref="clientListRef" @clientEvent="getSelectClient"></clientList>
    <!-- 接入点列表 -->
    <bridgeList ref="bridgeListRef" @bridgeEvent="getSelectBridge"></bridgeList>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, nextTick, getCurrentInstance, onMounted } from 'vue';
import { useI18n } from 'vue-i18n';
import { ElMessageBox } from 'element-plus';
import { Search, Refresh, Plus, Delete, View, Calendar, FullScreen, QuestionFilled } from '@element-plus/icons-vue';
import { useDict } from '@/utils/dict';
import { checkPermi } from '@/utils/permission';
import {
  listScript,
  getScript,
  getScriptLog,
  delScript,
  addScript,
  updateScript,
  validateScript,
  getScriptAllowpackage,
  addScriptAllowpackage,
  delScriptAllowpackage,
} from '@/api/iot/script';
import MonacoEditor from '@/components/MonacoEditor/index.vue';
import productList from './product-list.vue';
import clientList from './client-list.vue';
import bridgeList from './bridge-list.vue';

const { t } = useI18n();
const { proxy } = getCurrentInstance() as any;
const { rule_script_type, rule_script_language, rule_script_event, rule_script_action } = useDict(
  'rule_script_type',
  'rule_script_language',
  'rule_script_event',
  'rule_script_action'
);

const queryFormRef = ref();
const formRef = ref();
const multipleTableRef = ref();
const logContainerRef = ref();
const productListRef = ref();
const clientListRef = ref();
const bridgeListRef = ref();
const editorKey = ref(0);

const logs = ref('');
const isValidate = ref(false);
const validateMsg = ref('');
const loading = ref(true);
const logLoading = ref(true);
const scriptIds = ref<any[]>([]);
const single = ref(true);
const multiple = ref(true);
const showSearch = ref(true);
const total = ref(0);
const scriptList = ref<any[]>([]);
const title = ref('');
const open = ref(false);
const openLog = ref(false);
const isDisabled = ref(false);
const openAllowpackage = ref(false);
const allowPackage = ref<any[]>([]);
const isFullLogDialog = ref(false);

const defaultScriptData = `import cn.hutool.json.JSONArray;\nimport cn.hutool.json.JSONObject;\nimport cn.hutool.json.JSONUtil;\nimport cn.hutool.core.util.NumberUtil;\n\n// 1. 获取主题和内容(必要)\nString topic = msgContext.getTopic();\nString payload = msgContext.getPayload();\n\n// 2. 数据转换(自己处理)\nmsgContext.logger.info("数据转换处理")\nString NewTopic = topic;\nString NewPayload = payload;\n\n// 3. 返回新的数据（必要）\nmsgContext.setTopic(NewTopic);\nmsgContext.setPayload(NewPayload);`;

const form = reactive<any>({
  id: null,
  applicationName: 'fastbee',
  scriptId: null,
  productId: null,
  productName: '',
  bridgeName: '',
  scriptName: null,
  scriptType: 'script',
  remark: null,
  scriptLanguage: 'groovy',
  enable: 1,
  scriptPurpose: 1,
  scriptOrder: 1,
  scriptAction: 1,
  scriptEvent: 1,
  sceneId: 0,
  scriptData: defaultScriptData,
});

const queryParams = reactive<any>({
  pageNum: 1,
  pageSize: 10,
  scriptPurpose: 1,
  scriptId: null,
  scriptName: null,
  scriptData: null,
  scriptType: null,
  scriptLanguage: null,
  enable: null,
  productName: null,
});

const rules = reactive<any>({
  scriptId: [{ required: true, message: t('script.349087-19'), trigger: 'blur' }],
  productName: [{ required: true, message: t('script.349087-20'), trigger: 'blur' }],
  bridgeName: [{ required: true, message: t('script.349087-35'), trigger: 'blur' }],
  scriptName: [{ required: true, message: t('script.349087-21'), trigger: 'blur' }],
  scriptType: [{ required: true, message: t('script.349087-22'), trigger: 'change' }],
  scriptLanguage: [{ required: true, message: t('script.349087-23'), trigger: 'change' }],
  scriptEvent: [{ required: true, message: '', trigger: 'change' }],
  scriptAction: [{ required: true, message: '', trigger: 'change' }],
  scriptOrder: [{ required: true, message: '', trigger: 'change' }],
  enable: [{ required: true, message: t('script.349087-24'), trigger: 'blur' }],
});

/** 查询规则引擎脚本列表 */
function toNumberOrNull(value: any) {
  if (value === null || value === undefined || value === '') return null;
  const numberValue = Number(value);
  return Number.isNaN(numberValue) ? null : numberValue;
}

function getList() {
  loading.value = true;
  listScript(queryParams).then((response: any) => {
    scriptList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}

/** 取消按钮 */
function cancel() {
  open.value = false;
  reset();
}

/** 取消日志按钮 */
function cancelLog() {
  logs.value = '';
  openLog.value = false;
}

/** 编辑器内容变化 */
function handleEditorChange(data: string) {
  form.scriptData = data;
}

/** 状态切换 */
function handleEnableChange(row: any) {
  let hasPermission = checkPermi(['iot:script:edit']);
  if (!hasPermission) {
    isDisabled.value = true;
    return;
  }
  isDisabled.value = true;
  setTimeout(() => {
    isDisabled.value = false;
  }, 1000);
  const id = row.scriptId;
  getScript(id).then((res: any) => {
    if (res.code === 200) {
      const data = res.data;
      data.enable = data.enable == 1 ? 0 : 1;
      updateScript(data).then((chilRes: any) => {
        if (chilRes.code === 200) {
          getList();
          proxy.$modal.msgSuccess(t('views.iot.bridge.index.525282-93'));
        }
      });
    }
  });
}

/** 表单重置 */
function reset() {
  validateMsg.value = '';
  isValidate.value = false;
  Object.assign(form, {
    id: null,
    applicationName: 'fastbee',
    scriptId: null,
    productId: null,
    productName: '',
    bridgeName: '',
    scriptName: null,
    scriptType: 'script',
    remark: null,
    scriptLanguage: 'groovy',
    enable: 1,
    scriptPurpose: 1,
    scriptOrder: 1,
    scriptAction: 1,
    scriptEvent: 1,
    sceneId: 0,
    scriptData: defaultScriptData,
  });
  form.scriptOrder = toNumberOrNull(form.scriptOrder);
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
  scriptIds.value = selection.map((item) => item.scriptId);
  single.value = selection.length !== 1;
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
function handleAdd() {
  reset();
  open.value = true;
  title.value = t('script.349087-25');
  editorKey.value++;
  nextTick(() => {
    formRef.value?.clearValidate();
  });
}

/** 引入包白名单 */
function handleAllowPackage() {
  openAllowpackage.value = true;
  getScriptAllowpackage().then((response: any) => {
    allowPackage.value = response.data;
  });
}

/** 新增白名单 */
function handleAddAllowPackage() {
  ElMessageBox.prompt(t('script.349087-45'), t('edit'), {
    confirmButtonText: t('confirm'),
    cancelButtonText: t('cancel'),
    inputPattern: /^import.*;$/,
    inputErrorMessage: t('script.349087-46'),
  })
    .then(({ value }: any) => {
      addScriptAllowpackage(value).then((res: any) => {
        if (res.code === 200) {
          handleAllowPackage();
        }
      });
    })
    .catch((err) => {
      console.error(err);
    });
}

function cancelAllowpackage() {
  openAllowpackage.value = false;
}

/** 删除白名单 */
function handleDelAllowPackage(row: any, index: number) {
  delScriptAllowpackage(row).then(() => {
    proxy.$modal.msgSuccess(t('delSuccess'));
    allowPackage.value.splice(index, 1);
  });
}

/** 修改按钮操作 */
function handleUpdate(row: any) {
  reset();
  const scriptId = row.scriptId || scriptIds.value;
  getScript(scriptId).then((response: any) => {
    Object.assign(form, response.data);
    form.scriptOrder = toNumberOrNull(form.scriptOrder);
    editorKey.value++;
    open.value = true;
    title.value = t('script.349087-26');
  });
}

/** 日志按钮操作 */
function handleLog(scriptId: any) {
  logLoading.value = true;
  getScriptLog(scriptId).then((res: any) => {
    if (res.code === 200) {
      logs.value = res.msg;
      form.scriptId = scriptId;
      openLog.value = true;
      isFullLogDialog.value = false;
      title.value = t('script.349087-40');
      logLoading.value = false;
      nextTick(() => {
        const messageContent = logContainerRef.value;
        if (messageContent) {
          messageContent.scroll({ top: messageContent.scrollHeight });
        }
      });
    }
  });
}

/** 选择产品 */
function handleSelectProduct() {
  productListRef.value.queryParams.pageNum = 1;
  productListRef.value.open = true;
  productListRef.value.selectProductId = form.productId;
  productListRef.value.getList();
}

function getSelectProduct(data: any) {
  form.productId = data.productId;
  form.productName = data.productName;
}

/** 选择桥接接入点 */
function handleSelectBridge() {
  bridgeListRef.value.queryParams.pageNum = 1;
  if (form.scriptEvent === 5) {
    bridgeListRef.value.queryParams.type = 3;
  } else if (form.scriptEvent === 6) {
    bridgeListRef.value.queryParams.type = 4;
  }
  bridgeListRef.value.openBridge = true;
  bridgeListRef.value.selectBridgeId = form.bridgeId;
  bridgeListRef.value.getList();
}

function getSelectBridge(data: any) {
  form.bridgeId = data.id;
  form.bridgeName = data.name;
}

/** 选择HTTP客户端 */
function handleSelectHttpClient() {
  clientListRef.value.queryParams.pageNum = 1;
  clientListRef.value.queryParams.type = 3;
  clientListRef.value.open = true;
  clientListRef.value.getList();
}

/** 选择MQTT客户端 */
function handleSelectMqttClient() {
  clientListRef.value.queryParams.pageNum = 1;
  clientListRef.value.queryParams.type = 4;
  clientListRef.value.open = true;
  clientListRef.value.getList();
}

/** 选择数据库存储 */
function handleSelectDbSave() {
  clientListRef.value.queryParams.pageNum = 1;
  clientListRef.value.queryParams.type = 5;
  clientListRef.value.open = true;
  clientListRef.value.getList();
}

/** 返回客户端数据 */
function getSelectClient(data: any) {
  let scriptaction = '\r\n// 执行Action动作参数(脚本由系统自动生成)\r\n';
  if (form.scriptAction === 3) {
    scriptaction += 'msgContext.setData("httpBridgeID", ' + data.id + ');';
  } else if (form.scriptAction === 4) {
    scriptaction += 'msgContext.setData("mqttBridgeID", ' + data.id + ');';
  } else if (form.scriptAction === 5) {
    scriptaction += 'msgContext.setData("databaseBridgeID", ' + data.id + ');';
  }
  form.scriptData += scriptaction;
  editorKey.value++;
}

/** 提交按钮 */
function submitForm() {
  formRef.value.validate((valid: boolean) => {
    if (valid) {
      if (form.scriptId != null) {
        updateScript(form).then(() => {
          proxy.$modal.msgSuccess(t('updateSuccess'));
          open.value = false;
          getList();
        });
      } else {
        addScript(form).then(() => {
          proxy.$modal.msgSuccess(t('addSuccess'));
          open.value = false;
          getList();
        });
      }
    }
  });
}

/** 删除按钮操作 */
function handleDelete(row?: any) {
  const ids = row?.scriptId || scriptIds.value;
  proxy.$modal
    .confirm(t('script.349087-27', [ids]))
    .then(() => {
      return delScript(ids);
    })
    .then(() => {
      getList();
      proxy.$modal.msgSuccess(t('delSuccess'));
      multipleTableRef.value.clearSelection();
    })
    .catch(() => {});
}

/** 验证按钮操作 */
function handleValidate() {
  validateMsg.value = '';
  isValidate.value = false;
  validateScript(form).then((response: any) => {
    isValidate.value = response.data;
    validateMsg.value = response.msg;
  });
}

/** 编辑器点击事件 */
function editClick() {
  validateMsg.value = '';
  isValidate.value = false;
}

function getRowKeys(row: any) {
  return row.scriptId;
}

onMounted(() => {
  getList();
  let hasPermission = checkPermi(['iot:script:edit']);
  if (!hasPermission) {
    isDisabled.value = true;
  }
});
</script>

<style lang="scss" scoped>
.iot-scene-script {
  padding: 20px;

  .search-card {
    margin-bottom: 15px;
    padding: 3px 0;
  }

  .search-form {
    margin-bottom: -22.5px;
  }
}

.script-log-dialog {
  width: 100%;

  .title {
    line-height: 24px;
    font-size: 18px;
    color: #303133;
    word-wrap: break-word;
  }

  .menu {
    position: absolute;
    top: 13px;
    right: 44px;
    width: 20px;
    height: 20px;
    display: inline-flex;
    align-items: center;
    justify-content: center;
    padding: 0;
    margin: 0;
    line-height: 1;
    background: transparent;
    border: none;
    outline: none;
    cursor: pointer;
    font-size: 14px;
    color: #989399;
  }

  .menu :deep(svg) {
    display: block;
  }
}
</style>
