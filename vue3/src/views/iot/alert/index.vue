<template>
  <div class="iot-alert">
    <el-card v-show="showSearch" class="search-card">
      <el-form @submit.prevent :model="queryParams" ref="queryFormRef" :inline="true" class="search-form">
        <el-form-item prop="alertName">
          <el-input
            v-model="queryParams.alertName"
            :placeholder="$t('alert.log.491272-1')"
            clearable
            style="width: 192px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item prop="alertLevel">
          <el-select
            v-model="queryParams.alertLevel"
            :placeholder="$t('alert.index.236501-3')"
            clearable
            @keyup.enter="handleQuery"
            style="width: 192px"
          >
            <el-option v-for="dict in iot_alert_level" :key="dict.value" :label="dict.label" :value="dict.value" />
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
          <el-button type="primary" plain :icon="Plus" @click="handleAdd" v-hasPermi="['iot:alert:add']">
            {{ $t('add') }}
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button plain :icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['iot:alert:remove']">
            {{ $t('del') }}
          </el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
      </el-row>

      <el-table
        v-loading="loading"
        :data="alertList"
        :border="false"
        @selection-change="handleSelectionChange"
        ref="multipleTableRef"
        :row-key="getRowKeys"
      >
        <el-table-column :reserve-selection="true" type="selection" width="55" align="center" />
        <el-table-column :label="$t('alert.index.236501-0')" align="left" prop="alertName" min-width="200" />
        <el-table-column :label="$t('alert.index.236501-44')" align="center" prop="status" width="100">
          <template #default="scope">
            <el-switch
              v-model="scope.row.status"
              :active-value="1"
              :inactive-value="2"
              @change="handleEnableChange(scope.row)"
              :disabled="isDisabled"
            ></el-switch>
          </template>
        </el-table-column>
        <el-table-column :label="$t('alert.index.236501-2')" align="center" prop="alertLevel" width="100">
          <template #default="scope">
            <dict-tag :options="iot_alert_level" :value="scope.row.alertLevel" />
          </template>
        </el-table-column>
        <el-table-column :label="$t('template.index.891112-117')" align="center" prop="tenantName" width="150" />
        <el-table-column :label="$t('template.index.891112-118')" align="center" prop="createBy" width="150" />
        <el-table-column :label="$t('sip.user-list.558539-5')" align="center" prop="createTime" width="150">
          <template #default="scope">
            <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d}') }}</span>
          </template>
        </el-table-column>
        <el-table-column :label="$t('alert.index.236501-11')" align="left" prop="remark" min-width="200" />
        <el-table-column fixed="right" :label="$t('opation')" align="center" width="150">
          <template #default="scope">
            <el-button size="small" link :icon="View" @click="handleUpdate(scope.row)" v-hasPermi="['iot:alert:query']">
              {{ $t('look') }}
            </el-button>
            <el-button
              size="small"
              link
              :icon="Delete"
              @click="handleDelete(scope.row)"
              v-hasPermi="['iot:alert:remove']"
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

    <!-- 添加或修改设备告警对话框 -->
    <el-dialog
      :title="title"
      v-model="open"
      width="900px"
      append-to-body
      :close-on-click-modal="false"
      :close-on-press-escape="false"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="50">
          <el-col :span="12">
            <el-form-item :label="$t('alert.index.236501-0')" prop="alertName">
              <el-input v-model="form.alertName" :placeholder="$t('alert.log.491272-1')" style="width: 290px" />
            </el-form-item>
            <el-form-item :label="$t('alert.index.236501-2')" prop="alertLevel">
              <el-select v-model="form.alertLevel" :placeholder="$t('alert.index.236501-3')" style="width: 290px">
                <el-option
                  v-for="dict in iot_alert_level"
                  :key="dict.value"
                  :label="dict.label"
                  :value="parseInt(dict.value)"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-row>
              <el-col :span="12">
                <el-form-item :label="$t('alert.index.236501-16')">
                  <el-switch v-model="form.status" :active-value="1" :inactive-value="2" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item :label="$t('alert.index.236501-47')">
                  <el-switch v-model="form.generateWorkOrder" :active-value="1" :inactive-value="0" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-form-item :label="$t('alert.index.236501-11')" prop="remark">
              <el-input
                v-model="form.remark"
                type="textarea"
                :placeholder="$t('product.category.142342-3')"
                :autosize="{ minRows: 3, maxRows: 5 }"
                style="width: 290px"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>

      <el-tabs v-model="activeName" style="padding: 10px">
        <el-tab-pane :label="$t('alert.index.236501-17')" name="relateScene">
          <el-table :data="form.scenes" v-loading="sceneLoading" size="small" :border="false">
            <el-table-column
              prop="sceneName"
              align="left"
              :label="$t('alert.index.236501-18')"
              min-width="180"
            ></el-table-column>
            <el-table-column :label="$t('alert.index.236501-44')" align="center" prop="enable" min-width="80">
              <template #default="scope">
                <dict-tag :options="scene_status" :value="scope.row.enable"></dict-tag>
              </template>
            </el-table-column>
            <el-table-column :label="$t('alert.index.236501-19')" align="center" prop="cond" min-width="100">
              <template #default="scope">
                <dict-tag :options="trigger_condition" :value="scope.row.cond" />
              </template>
            </el-table-column>
            <el-table-column :label="$t('alert.index.236501-23')" align="center" prop="executeMode" width="80">
              <template #default="scope">
                <dict-tag :options="execution_method" :value="scope.row.executeMode" />
              </template>
            </el-table-column>
            <el-table-column :label="$t('opation')" align="center" width="100">
              <template #default="scope">
                <el-button
                  size="small"
                  link
                  :icon="Delete"
                  @click="handleAlertSceneRemove(scope.row)"
                  v-hasPermi="['iot:alert:remove']"
                >
                  {{ $t('alert.index.236501-26') }}
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
        <el-tab-pane :label="$t('alert.index.236501-27')" name="notify">
          <el-table :data="form.notifyTemplateList" v-loading="notifyLoading" size="small" :border="false">
            <el-table-column
              prop="name"
              align="left"
              :label="$t('alert.index.236501-28')"
              min-width="190"
            ></el-table-column>
            <el-table-column :label="$t('alert.index.236501-44')" align="center" prop="status" min-width="80">
              <template #default="scope">
                <el-tag type="success" size="small" v-if="scope.row.status == '1'">
                  {{ $t('alert.index.236501-45') }}
                </el-tag>
                <el-tag type="danger" size="small" v-if="scope.row.status == '0'">
                  {{ $t('alert.index.236501-46') }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column :label="$t('alert.index.236501-29')" align="center" prop="channelType" min-width="100">
              <template #default="scope">
                <dict-tag :options="notify_channel_type" :value="scope.row.channelType" />
              </template>
            </el-table-column>
            <el-table-column
              :label="$t('alert.index.236501-30')"
              align="center"
              prop="channelName"
              min-width="200"
            ></el-table-column>
            <el-table-column
              :label="$t('alert.index.236501-31')"
              align="left"
              prop="provider"
              min-width="120"
            ></el-table-column>
            <el-table-column :label="$t('opation')" align="center" width="100">
              <template #default="scope">
                <el-button size="small" link :icon="Delete" @click="handleAlertNotifyTempRemove(scope.row)">
                  {{ $t('alert.index.236501-26') }}
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
        <!-- 用于设置间距 -->
        <el-tab-pane disabled>
          <template #label>
            <div style="margin-left: 533px"></div>
          </template>
        </el-tab-pane>
        <!-- 按钮 tabs -->
        <el-tab-pane name="sceneButton" disabled v-if="activeName == 'relateScene'">
          <template #label>
            <el-button
              style="height: 32px; width: 32px; padding: 0"
              :icon="Plus"
              size="small"
              @click="addAlertScenes"
              v-hasPermi="['iot:alert:add']"
            ></el-button>
            <el-button
              style="height: 32px; width: 32px; padding: 0"
              :icon="Refresh"
              size="small"
              @click="getScenesByAlertIdFn"
              v-hasPermi="['iot:alert:add']"
            ></el-button>
          </template>
        </el-tab-pane>
        <el-tab-pane name="notifyButton" disabled v-else>
          <template #label>
            <el-button
              style="height: 32px; width: 32px; padding: 0"
              :icon="Plus"
              size="small"
              @click="addAlertNotifyTemp"
              v-hasPermi="['iot:alert:add']"
            ></el-button>
            <el-button
              style="height: 32px; width: 32px; padding: 0"
              :icon="Refresh"
              size="small"
              @click="getNotifyTempsByAlertIdFn"
              v-hasPermi="['iot:alert:add']"
            ></el-button>
          </template>
        </el-tab-pane>
      </el-tabs>

      <template #footer>
        <el-button
          type="primary"
          @click="handleSubmitForm"
          :disabled="updateBtnDisabled"
          :loading="confirmLoading"
          v-hasPermi="['iot:alert:edit']"
          v-show="form.alertId"
        >
          {{ $t('update') }}
        </el-button>
        <el-button
          type="primary"
          @click="handleSubmitForm"
          :disabled="updateBtnDisabled"
          :loading="confirmLoading"
          v-hasPermi="['iot:alert:add']"
          v-show="!form.alertId"
        >
          {{ $t('add') }}
        </el-button>
        <el-button @click="handleCancel">{{ $t('cancel') }}</el-button>
      </template>
    </el-dialog>

    <!-- 选择场景对话框 -->
    <scene-list ref="sceneListRef" @sceneEvent="getSceneData($event)" />
    <!-- 选择通知模板 -->
    <notify-temp-list ref="notifyTempListRef" @notifyEvent="getNotifyTempData($event)" />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, getCurrentInstance, nextTick } from 'vue';
import { Search, Refresh, Plus, Delete, View } from '@element-plus/icons-vue';
import {
  listAlert,
  getAlert,
  delAlert,
  addAlert,
  updateAlert,
  getScenesByAlertId,
  listNotifyTemplate,
} from '@/api/iot/alert';
import { checkPermi } from '@/utils/permission';
import { parseTime } from '@/utils/ruoyi';
import { useDict } from '@/utils/dict';
import sceneList from './scene-list.vue';
import notifyTempList from './notify-temp-list.vue';

const proxy = getCurrentInstance()?.proxy as any;
const { iot_alert_level, notify_channel_type, trigger_condition, execution_method, scene_status } = useDict(
  'iot_alert_level',
  'notify_channel_type',
  'trigger_condition',
  'execution_method',
  'scene_status'
);

const activeName = ref('relateScene');
const notifyLoading = ref(false);
const updateBtnDisabled = ref(false);
const confirmLoading = ref(false);
const sceneLoading = ref(false);
const loading = ref(false);
const ids = ref<any[]>([]);
const single = ref(true);
const multiple = ref(true);
const showSearch = ref(true);
const total = ref(0);
const alertList = ref<any[]>([]);
const title = ref('');
const open = ref(false);
const isDisabled = ref(false);

const queryFormRef = ref<any>(null);
const formRef = ref<any>(null);
const multipleTableRef = ref<any>(null);
const sceneListRef = ref<any>(null);
const notifyTempListRef = ref<any>(null);

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  alertName: null as any,
  alertLevel: null as any,
});

const form = ref<any>({});

const rules = reactive<any>({
  alertName: [{ required: true, message: proxy?.$t('alert.index.236501-39'), trigger: 'blur' }],
  alertLevel: [{ required: true, message: proxy?.$t('alert.index.236501-40'), trigger: 'change' }],
});

/** 查询设备告警列表 */
const getList = () => {
  loading.value = true;
  listAlert(queryParams).then((response: any) => {
    alertList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
};

/** 表单重置 */
const reset = () => {
  form.value = {
    alertId: null,
    alertName: null,
    alertLevel: 1,
    productId: null,
    productName: null,
    remark: null,
    status: 1,
    scenes: [],
    notifyTemplateList: [],
    generateWorkOrder: 0,
  };
  activeName.value = 'relateScene';
  proxy?.resetForm('formRef');
};

/** 搜索 */
const handleQuery = () => {
  queryParams.pageNum = 1;
  getList();
};

/** 重置 */
const resetQuery = () => {
  queryFormRef.value?.resetFields();
  handleQuery();
};

/** 多选 */
const handleSelectionChange = (selection: any[]) => {
  ids.value = selection.map((item: any) => item.alertId);
  single.value = selection.length !== 1;
  multiple.value = !selection.length;
};

const getRowKeys = (row: any) => row.alertId;

/** 新增 */
const handleAdd = () => {
  reset();
  open.value = true;
  title.value = proxy?.$t('alert.index.236501-41');
  nextTick(() => {
    formRef.value?.clearValidate();
  });
};

/** 修改 */
const handleUpdate = (row: any) => {
  reset();
  const alertId = row.alertId || ids.value;
  getAlert(alertId).then((response: any) => {
    form.value = response.data;
    open.value = true;
    title.value = proxy?.$t('alert.index.236501-42');
  });
};

/** 删除 */
const handleDelete = (row: any) => {
  const alertIds = row.alertId || ids.value;
  proxy?.$modal
    .confirm(proxy?.$t('alert.index.236501-43', [alertIds]))
    .then(() => {
      return delAlert(alertIds);
    })
    .then(() => {
      getList();
      proxy?.$modal.msgSuccess(proxy?.$t('delSuccess'));
      multipleTableRef.value?.clearSelection();
    })
    .catch(() => {});
};

/** 取消 */
const handleCancel = () => {
  open.value = false;
  reset();
  nextTick(() => {
    formRef.value?.clearValidate();
  });
};

/** 状态切换 */
const handleEnableChange = (row: any) => {
  isDisabled.value = true;
  setTimeout(() => {
    isDisabled.value = false;
  }, 1000);
  const id = row.alertId;
  getAlert(id).then((res: any) => {
    if (res.code === 200) {
      const data = res.data;
      data.status = data.status == 1 ? 2 : 1;
      updateAlert(data).then((chilRes: any) => {
        if (chilRes.code === 200) {
          getList();
          proxy?.$modal.msgSuccess(proxy?.$t('updateSuccess'));
        }
      });
    }
  });
};

/** 提交 */
const handleSubmitForm = () => {
  formRef.value?.validate((valid: boolean) => {
    if (valid) {
      if (form.value.alertId != null) {
        updateAlert(form.value).then((res: any) => {
          if (res.code === 200) {
            proxy?.$modal.msgSuccess(proxy?.$t('updateSuccess'));
            getList();
          }
          open.value = false;
        });
      } else {
        addAlert(form.value).then((res: any) => {
          if (res.code === 200) {
            proxy?.$modal.msgSuccess(proxy?.$t('addSuccess'));
            getList();
          }
          open.value = false;
        });
      }
    }
  });
};

/** 移除告警场景 */
const handleAlertSceneRemove = (row: any) => {
  form.value.scenes = form.value.scenes.filter((item: any) => item.sceneId !== row.sceneId);
};

/** 获取告警关联场景 */
const getScenesByAlertIdFn = () => {
  if (form.value.alertId) {
    sceneLoading.value = true;
    getScenesByAlertId(form.value.alertId).then((response: any) => {
      form.value.scenes = response.rows;
      sceneLoading.value = false;
    });
  }
};

/** 添加场景 */
const addAlertScenes = () => {
  sceneListRef.value?.openDialog();
  if (form.value.scenes) {
    const list = JSON.parse(JSON.stringify(form.value.scenes));
    sceneListRef.value.selectScenes = list;
    sceneListRef.value.ids = list.map((item: any) => item.sceneId);
  }
};

/** 获取场景数据 */
const getSceneData = (data: any) => {
  form.value.scenes = data;
};

/** 移除通知模板 */
const handleAlertNotifyTempRemove = (row: any) => {
  form.value.notifyTemplateList = form.value.notifyTemplateList.filter((item: any) => item.id !== row.id);
};

/** 获取通知模板 */
const getNotifyTempsByAlertIdFn = () => {
  if (form.value.alertId) {
    notifyLoading.value = true;
    listNotifyTemplate(form.value.alertId).then((response: any) => {
      form.value.notifyTemplateList = response.rows;
      notifyLoading.value = false;
    });
  }
};

/** 添加通知模板 */
const addAlertNotifyTemp = () => {
  notifyTempListRef.value?.openDialog();
  if (form.value.notifyTemplateList) {
    const list = JSON.parse(JSON.stringify(form.value.notifyTemplateList));
    notifyTempListRef.value.selectNotifyTemps = list;
    notifyTempListRef.value.ids = list.map((item: any) => item.id);
  }
};

/** 获取通知模板数据 */
const getNotifyTempData = (data: any) => {
  form.value.notifyTemplateList = data;
};

// 初始化
getList();
const hasPermission = checkPermi(['iot:alert:edit']);
if (!hasPermission) {
  isDisabled.value = true;
}
</script>

<style lang="scss" scoped>
.iot-alert {
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
