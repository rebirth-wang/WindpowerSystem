<template>
  <div class="app-container">
    <el-card v-show="showSearch" style="margin-bottom: 15px; border-radius: 8px; width: 100%">
      <el-form
        @submit.prevent
        :model="queryParams"
        ref="queryFormRef"
        :inline="true"
        label-width="46px"
        style="margin-bottom: -18px; padding: 3px 0 0 0"
      >
        <el-form-item prop="name">
          <el-input
            v-model="queryParams.name"
            :placeholder="$t('workOrder.index.748855-0')"
            clearable
            @keyup.enter="handleQuery"
            style="width: 192px"
          />
        </el-form-item>
        <el-form-item prop="status">
          <el-select
            v-model="queryParams.status"
            :placeholder="$t('workOrder.index.748855-1')"
            clearable
            @keyup.enter="handleQuery"
            style="width: 192px"
          >
            <el-option v-for="dict in work_order_status" :key="dict.value" :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <el-form-item prop="type">
          <el-select
            v-model="queryParams.type"
            :placeholder="$t('workOrder.index.748855-2')"
            clearable
            style="width: 192px"
          >
            <el-option v-for="dict in work_order_type" :key="dict.value" :label="dict.label" :value="dict.value" />
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
          <el-button type="primary" plain :icon="Plus" @click="handleAdd" v-hasPermi="['iot:workOrder:add']">
            {{ $t('add') }}
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            plain
            :icon="Delete"
            :disabled="multiple"
            @click="handleDelete"
            v-hasPermi="['iot:workOrder:remove']"
          >
            {{ $t('del') }}
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button plain :icon="Download" @click="handleExport" v-hasPermi="['iot:workOrder:export']">
            {{ $t('export') }}
          </el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
      </el-row>

      <el-table
        v-loading="loading"
        :data="workOrderList"
        :border="false"
        @selection-change="handleSelectionChange"
        ref="multipleTableRef"
        :row-key="getRowKeys"
      >
        <el-table-column
          :reserve-selection="true"
          type="selection"
          width="55"
          align="center"
          :selectable="(row: any) => row.status !== 4"
        />
        <el-table-column :label="$t('workOrder.index.748855-3')" align="center" prop="name" width="200" />
        <el-table-column :label="$t('workOrder.index.748855-4')" align="center" prop="status">
          <template #default="scope">
            <dict-tag :options="work_order_status" :value="scope.row.status" />
          </template>
        </el-table-column>
        <el-table-column :label="$t('workOrder.index.748855-5')" align="center" prop="type" width="100">
          <template #default="scope">
            <dict-tag :options="work_order_type" :value="scope.row.type" />
          </template>
        </el-table-column>
        <el-table-column :label="$t('workOrder.index.748855-6')" align="center" prop="number" width="180" />
        <el-table-column :label="$t('workOrder.index.748855-7')" align="center" prop="userName" />
        <el-table-column :label="$t('workOrder.index.748855-8')" align="center" prop="userPhone" width="180" />
        <el-table-column :label="$t('workOrder.index.748855-9')" align="center" prop="deviceName" width="180" />
        <el-table-column :label="$t('workOrder.index.748855-10')" align="center" prop="endTime" width="180">
          <template #default="scope">
            <span>{{ parseTime(scope.row.endTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
          </template>
        </el-table-column>
        <el-table-column :label="$t('workOrder.index.748855-11')" align="left" prop="description" width="200" />
        <el-table-column :label="$t('workOrder.index.748855-12')" align="center" prop="tenantName" />
        <el-table-column :label="$t('workOrder.index.748855-13')" align="center" prop="createBy" />
        <el-table-column :label="$t('workOrder.index.748855-14')" align="center" prop="createTime" width="180">
          <template #default="scope">
            <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
          </template>
        </el-table-column>
        <el-table-column
          fixed="right"
          :label="$t('opation')"
          align="center"
          class-name="small-padding fixed-width"
          width="370"
        >
          <template #default="scope">
            <el-button
              size="small"
              link
              :icon="EditPen"
              @click="handleOptionWorkOrder(1, scope.row)"
              :disabled="scope.row.status != 2"
              v-hasPermi="['iot:workOrder:edit']"
            >
              {{ $t('workOrder.index.748855-15') }}
            </el-button>
            <el-button
              size="small"
              link
              :icon="EditPen"
              @click="handleOptionWorkOrder(2, scope.row)"
              :disabled="!scope.row.canReceived"
              v-hasPermi="['iot:workOrder:edit']"
            >
              {{ $t('workOrder.index.748855-16') }}
            </el-button>
            <el-button
              size="small"
              link
              :icon="EditPen"
              @click="handleOptionWorkOrder(3, scope.row)"
              :disabled="scope.row.status !== 4"
              v-hasPermi="['iot:workOrder:edit']"
            >
              {{ $t('workOrder.index.748855-17') }}
            </el-button>
            <el-button
              size="small"
              link
              :icon="Edit"
              @click="handleUpdate(scope.row)"
              v-hasPermi="['iot:workOrder:query']"
            >
              {{ $t('look') }}
            </el-button>
            <el-button
              size="small"
              link
              :icon="Delete"
              @click="handleDelete(scope.row)"
              :disabled="scope.row.status == 4"
              v-hasPermi="['iot:workOrder:remove']"
            >
              {{ $t('del') }}
            </el-button>
            <el-button
              size="small"
              link
              :icon="Tickets"
              @click="handleRecord(scope.row)"
              v-hasPermi="['iot:workOrderLog:log']"
            >
              {{ $t('workOrder.index.748855-18') }}
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

    <!-- 添加或修改工单管理对话框 -->
    <el-dialog :title="title" v-model="open" width="900px" append-to-body class="workorder-two-col-dialog">
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="88px"
        :disabled="!canEdit"
        class="workorder-form-grid"
      >
        <el-form-item class="col-half" :label="$t('workOrder.index.748855-3')" prop="name">
          <el-input v-model="form.name" :placeholder="$t('workOrder.index.748855-0')" style="width: 430px" />
        </el-form-item>
        <el-form-item class="col-half" :label="$t('workOrder.index.748855-5')" prop="type">
          <el-select
            v-model="form.type"
            :placeholder="$t('workOrder.index.748855-2')"
            :validate-event="false"
            style="width: 430px"
            @change="changeType"
          >
            <el-option
              v-for="dict in work_order_type"
              :key="dict.value"
              :label="dict.label"
              :value="parseInt(dict.value)"
              :disabled="dict.value == '4' || dict.value == '5'"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item class="col-half" :label="$t('workOrder.index.748855-7')" prop="userId">
          <el-select
            v-model="form.userId"
            :placeholder="$t('workOrder.index.748855-20')"
            style="width: 430px"
            :filterable="true"
            :validate-event="false"
            clearable
          >
            <el-option
              v-for="option in userList"
              :key="option.value"
              :label="option.label"
              :value="parseInt(option.value)"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item class="col-half" :label="$t('workOrder.index.748855-21')" prop="deviceId">
          <el-select
            v-model="form.deviceId"
            :placeholder="$t('workOrder.index.748855-22')"
            style="width: 430px"
            :filterable="true"
            :validate-event="false"
            clearable
          >
            <el-option
              v-for="option in deviceList"
              :key="option.value"
              :label="option.label"
              :value="parseInt(option.value)"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item class="col-half" :label="$t('workOrder.index.748855-23')" prop="endTime">
          <el-date-picker
            v-model="form.endTime"
            type="datetime"
            :placeholder="$t('workOrder.index.748855-24')"
            value-format="YYYY-MM-DD HH:mm:ss"
            :validate-event="false"
            style="width: 430px"
          ></el-date-picker>
        </el-form-item>
        <el-form-item class="col-half" :label="$t('workOrder.index.748855-25')" prop="remark">
          <el-input
            v-model="form.remark"
            :placeholder="$t('workOrder.index.748855-26')"
            :autosize="{ minRows: 3, maxRows: 5 }"
            style="width: 430px"
            type="textarea"
          />
        </el-form-item>
        <el-form-item class="col-half" :label="$t('workOrder.index.748855-53')" v-if="form.type !== null">
          <el-card shadow="never" class="detail-card" style="width: 430px">
            <el-form ref="detailsFormRef" :model="details" :rules="paramsRules" label-width="50px" :disabled="!canEdit">
              <el-form-item :label="$t('workOrder.index.748855-54')" prop="description">
                <el-input
                  v-model="details.description"
                  :placeholder="$t('workOrder.index.748855-55')"
                  style="width: 320px"
                  type="textarea"
                  :autosize="{ minRows: 3, maxRows: 5 }"
                />
              </el-form-item>
              <el-form-item
                :label="$t('workOrder.index.748855-56')"
                prop="location"
                style="margin-top: 20px"
                v-if="form.type === 3"
              >
                <el-cascader
                  :options="cityOptions"
                  v-model="details.location"
                  :validate-event="false"
                  @change="changeProvince"
                  change-on-select
                  style="width: 300px"
                  clearable
                  filterable
                ></el-cascader>
              </el-form-item>
              <el-form-item :label="$t('workOrder.index.748855-52')" prop="image" style="margin-top: 20px">
                <imageUpload
                  ref="imageUploadRef"
                  :modelValue="details.image"
                  :limit="5"
                  :fileSize="10"
                  @update:modelValue="getImagePathDetails($event)"
                ></imageUpload>
              </el-form-item>
            </el-form>
          </el-card>
        </el-form-item>
        <el-form-item
          class="col-half"
          :label="$t('workOrder.index.748855-50')"
          v-if="form.id && (form.status == 1 || form.status == 5)"
        >
          <el-card shadow="never" class="detail-card" style="width: 430px">
            <el-form ref="resultFormRef" :model="result" :rules="paramsRules" label-width="50px" disabled>
              <el-form-item :label="$t('workOrder.index.748855-54')" prop="description">
                <el-input
                  v-model="result.description"
                  :placeholder="$t('workOrder.index.748855-55')"
                  style="width: 320px"
                  type="textarea"
                  :autosize="{ minRows: 3, maxRows: 5 }"
                />
              </el-form-item>
              <el-form-item :label="$t('workOrder.index.748855-52')" prop="image" style="margin-top: 20px">
                <imageUpload
                  ref="resultImageRef"
                  :modelValue="result.image"
                  :limit="5"
                  :fileSize="10"
                  @update:modelValue="getImagePath($event)"
                  :action="imgUrl"
                ></imageUpload>
              </el-form-item>
            </el-form>
          </el-card>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm" v-if="form.id && canEdit" v-hasPermi="['iot:workOrder:edit']">
            {{ $t('update') }}
          </el-button>
          <el-button type="primary" @click="submitForm" v-if="!form.id" v-hasPermi="['iot:workOrder:add']">
            {{ $t('add') }}
          </el-button>
          <el-button @click="cancel">{{ $t('cancel') }}</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 派单选择联系人 -->
    <el-dialog :title="slectUserTitle" v-model="openSelectUser" width="800px" append-to-body>
      <el-form ref="formParamsRef" :model="formParams" :rules="rulesParams" label-width="80px">
        <el-form-item :label="$t('workOrder.index.748855-23')" prop="endTime">
          <el-date-picker
            v-model="formParams.endTime"
            type="datetime"
            :placeholder="$t('workOrder.index.748855-24')"
            value-format="YYYY-MM-DD HH:mm:ss"
            :validate-event="false"
            style="width: 300px"
          ></el-date-picker>
        </el-form-item>
      </el-form>
      <el-table
        v-loading="loading"
        :data="selectUserList"
        highlight-current-row
        size="small"
        :border="false"
        @row-click="rowClick"
      >
        <el-table-column :label="$t('device.device-edit.148398-6')" width="60" align="center">
          <template #default="scope">
            <input type="radio" :checked="scope.row.isSelect" name="user" />
          </template>
        </el-table-column>
        <el-table-column
          :label="$t('device.user-list.041943-5')"
          align="center"
          key="userId"
          prop="userId"
          width="120"
        />
        <el-table-column :label="$t('device.user-list.041943-6')" align="center" key="userName" prop="userName" />
        <el-table-column
          :label="$t('device.user-list.041943-1')"
          align="center"
          key="phonenumber"
          prop="phonenumber"
          width="120"
        />
      </el-table>
      <pagination
        v-show="userTotal > 0"
        small
        :total="userTotal"
        v-model:page="formParams.pageNum"
        v-model:limit="formParams.pageSize"
        @pagination="getUserList"
      />
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitUpdate">{{ $t('confirm') }}</el-button>
          <el-button @click="closeSelectUser">{{ $t('device.user-list.041943-10') }}</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 记录弹框 -->
    <el-dialog :title="recordTitle" v-model="openRecord" width="680px" append-to-body class="custom-steps">
      <div v-if="recordList.length > 0" class="record-timeline">
        <div v-for="(item, index) in recordList" :key="index" class="record-item" :class="{ latest: index === 0 }">
          <div class="timeline-axis">
            <span class="timeline-dot" :class="{ latest: index === 0 }">✓</span>
            <span class="timeline-line" v-if="index !== recordList.length - 1"></span>
          </div>
          <div class="record-content">
            <div class="record-time">{{ item.createTime }}</div>
            <div class="custom-description">
              <div>
                {{ $t('workOrder.index.748855-13') }}:
                <span class="creator">{{ item.createBy }}</span>
              </div>
              <div v-if="item.content" class="remark" style="margin-top: 5px">
                {{ $t('workOrder.index.748855-27') }}: {{ item.content }}
              </div>
            </div>
          </div>
        </div>
      </div>
      <el-empty :description="$t('workOrder.index.748855-28')" v-else></el-empty>
    </el-dialog>

    <!-- 结单 -->
    <el-dialog :title="statementTitle" v-model="statementOpen" width="680px" append-to-body class="custom-steps">
      <el-form ref="statementFormRef" :model="result" :rules="statementRules" label-width="100px">
        <el-form-item :label="$t('workOrder.index.748855-50')" prop="description">
          <el-input
            v-model="result.description"
            :placeholder="$t('workOrder.index.748855-51')"
            style="width: 430px"
            type="textarea"
            :autosize="{ minRows: 3, maxRows: 5 }"
          />
        </el-form-item>
        <el-form-item :label="$t('workOrder.index.748855-52')" prop="image">
          <imageUpload
            ref="statementImageRef"
            :modelValue="result.image"
            :limit="10"
            :fileSize="10"
            :type="1"
            @update:modelValue="getImagePath($event)"
            :action="imgUrl"
          ></imageUpload>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitFormResult">{{ $t('confirm') }}</el-button>
          <el-button @click="cancelResult">{{ $t('cancel') }}</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, getCurrentInstance, nextTick } from 'vue';
import { Search, Refresh, Plus, Delete, Download, Edit, EditPen, Tickets } from '@element-plus/icons-vue';
import {
  listWorkOrder,
  getWorkOrder,
  delWorkOrder,
  addWorkOrder,
  updateWorkOrder,
  optionWorkOrder,
  workOrderRecordList,
} from '@/api/iot/workOrder';
import { listUser } from '@/api/system/user';
import { listDeviceShort } from '@/api/iot/device';
import imageUpload from '@/components/ImageUpload/index.vue';
import { regionData, codeToText } from 'element-china-area-data';
import { parseTime } from '@/utils/ruoyi';
import { useDict } from '@/utils/dict';

const { work_order_type, work_order_status } = useDict('work_order_type', 'work_order_status');
const proxy = getCurrentInstance()?.proxy as any;

const loading = ref(true);
const ids = ref<any[]>([]);
const single = ref(true);
const multiple = ref(true);
const showSearch = ref(true);
const total = ref(0);
const workOrderList = ref<any[]>([]);
const title = ref('');
const open = ref(false);
const userList = ref<any[]>([]);
const userTotal = ref(0);
const deviceList = ref<any[]>([]);
const openSelectUser = ref(false);
const slectUserTitle = ref('');
const recordTitle = ref('');
const openRecord = ref(false);
const cityOptions = regionData as any[];
const selectUserList = ref<any[]>([]);
const recordList = ref<any[]>([]);
const userId = ref(0);
const user = ref<any>({});
const statementOpen = ref(false);
const statementTitle = ref('');
const canEdit = ref(true);
const imgUrl = import.meta.env.VITE_APP_BASE_API + '/common/upload?path=iot/workOrder';

const queryFormRef = ref<any>(null);
const formRef = ref<any>(null);
const detailsFormRef = ref<any>(null);
const formParamsRef = ref<any>(null);
const statementFormRef = ref<any>(null);
const multipleTableRef = ref<any>(null);

const queryParams = reactive<any>({
  pageNum: 1,
  pageSize: 10,
  name: null,
  status: null,
  type: null,
});
const form = ref<any>({});
const formParams = reactive<any>({
  pageNum: 1,
  pageSize: 10,
  endTime: null,
});
const result = ref<any>({
  description: '',
  image: [],
});
const details = ref<any>({
  description: '',
  image: [],
});

function requiredRule(message: string, trigger: 'blur' | 'change') {
  return {
    trigger,
    validator: (_rule: any, value: any, callback: (error?: Error) => void) => {
      if (value === null || value === undefined || value === '') {
        callback(new Error(message));
        return;
      }
      callback();
    },
  };
}

const rules = reactive<any>({
  name: [requiredRule(proxy?.$t('workOrder.index.748855-29'), 'blur')],
  description: [requiredRule(proxy?.$t('workOrder.index.748855-31'), 'blur')],
  type: [requiredRule(proxy?.$t('workOrder.index.748855-32'), 'change')],
  userId: [requiredRule(proxy?.$t('workOrder.index.748855-33'), 'change')],
  deviceId: [requiredRule(proxy?.$t('workOrder.index.748855-35'), 'change')],
  endTime: [
    requiredRule(proxy?.$t('workOrder.index.748855-36'), 'change'),
    {
      validator: (rule: any, value: string, callback: Function) => {
        if (!value) return callback();
        if (new Date(value).getTime() <= Date.now()) {
          callback(new Error(proxy?.$t('workOrder.index.748855-57')));
        } else {
          callback();
        }
      },
      trigger: 'change',
    },
  ],
});
const rulesParams = reactive<any>({
  endTime: [
    requiredRule(proxy?.$t('workOrder.index.748855-36'), 'change'),
    {
      validator: (rule: any, value: string, callback: Function) => {
        if (!value) return callback();
        if (new Date(value).getTime() <= Date.now()) {
          callback(new Error(proxy?.$t('workOrder.index.748855-57')));
        } else {
          callback();
        }
      },
      trigger: 'change',
    },
  ],
});
const statementRules = reactive<any>({
  description: [requiredRule(proxy?.$t('workOrder.index.748855-51'), 'blur')],
});
const paramsRules = reactive<any>({
  description: [requiredRule(proxy?.$t('workOrder.index.748855-51'), 'blur')],
  location: [requiredRule(proxy?.$t('workOrder.index.748855-58'), 'change')],
});

onMounted(() => {
  statementTitle.value = proxy?.$t('workOrder.index.748855-50');
  getList();
  getDeviceList();
  getUserList();
});

function getList() {
  loading.value = true;
  listWorkOrder(queryParams).then((response: any) => {
    if (response.code === 200) {
      workOrderList.value = response.rows;
      total.value = response.total;
      loading.value = false;
    }
  });
}

function changeType(value: number) {
  const selectedItem = work_order_type.value?.find((item: any) => parseInt(item.value) === value);
  if (selectedItem) {
    details.value = JSON.parse(selectedItem.raw.remark);
  }
}

function changeProvince(data: any) {
  if (data && data[0] != null && data[1] != null && data[2] != null) {
    const str = codeToText[data[0]] + '/' + codeToText[data[1]] + '/' + codeToText[data[2]];
  }
}

function getUserList() {
  loading.value = true;
  listUser(formParams).then((response: any) => {
    if (response.code === 200) {
      selectUserList.value = response.rows;
      userTotal.value = response.total;
      for (let i = 0; i < response.rows.length; i++) {
        response.rows[i].isSelect = false;
      }
      userList.value = response.rows.map((item: any) => ({
        value: item.userId,
        label: item.userName,
      }));
      if (form.value.userId != 0) {
        setRadioSelected(form.value.userId);
      }
      loading.value = false;
    } else {
      loading.value = false;
    }
  });
}

function rowClick(u: any) {
  if (u != null) {
    setRadioSelected(u.userId);
    user.value = u;
    form.value.userId = u.userId;
  }
}

function setRadioSelected(uid: any) {
  for (let i = 0; i < selectUserList.value.length; i++) {
    selectUserList.value[i].isSelect = selectUserList.value[i].userId == uid;
    if (selectUserList.value[i].userId == uid) {
      user.value = selectUserList.value[i];
    }
  }
}

function closeSelectUser() {
  openSelectUser.value = false;
  resetQuery();
}

function getDeviceList() {
  listDeviceShort({ showChild: true, pageNum: 1, pageSize: 9999 }).then((res: any) => {
    if (res.code === 200) {
      deviceList.value = res.rows.map((item: any) => ({
        value: item.deviceId,
        label: item.deviceName,
      }));
    }
  });
}

function getImagePath(data: any) {
  result.value.image = (data || '').split(',').filter((url: string) => url && url.trim() !== '');
}

function getImagePathDetails(data: any) {
  details.value.image = (data || '').split(',').filter((url: string) => url && url.trim() !== '');
}

function handleOptionWorkOrder(type: number, row: any) {
  form.value.id = row.id;
  if (type == 1) {
    openSelectUser.value = true;
    formParams.endTime = null;
    form.value.userId = null;
    formParams.pageNum = 1;
    formParams.pageSize = 10;
    slectUserTitle.value = proxy?.$t('workOrder.index.748855-37');
    getUserList();
  } else if (type == 2) {
    proxy?.$modal
      .confirm(proxy?.$t('workOrder.index.748855-38'))
      .then(() => {
        optionWork(row);
      })
      .then(() => {
        getList();
      })
      .catch(() => {});
  } else if (type == 3) {
    statementOpen.value = true;
    result.value = { description: '', image: [] };
  }
}

function optionWork(row: any) {
  optionWorkOrder({ id: row.id, status: 4 }).then((res: any) => {
    if (res.code === 200) {
      getList();
      proxy?.$modal.msgSuccess(res.msg);
    }
  });
}

async function submitUpdate() {
  const valid = await formParamsRef.value?.validate().catch(() => false);
  if (!valid) return;

  if (!form.value.userId) {
    proxy?.$modal.msgError(proxy?.$t('workOrder.index.748855-49'));
    return;
  }
  optionWorkOrder({
    id: form.value.id,
    status: 3,
    userId: form.value.userId,
    endTime: formParams.endTime,
  }).then((res: any) => {
    if (res.code === 200) {
      proxy?.$modal.msgSuccess(proxy?.$t('workOrder.index.748855-44'));
      openSelectUser.value = false;
      getList();
    } else {
      openSelectUser.value = false;
    }
  });
}

function submitFormResult() {
  statementFormRef.value?.validate((valid: boolean) => {
    if (valid) {
      optionWorkOrder({
        id: form.value.id,
        status: 1,
        result: JSON.stringify(result.value),
      }).then((res: any) => {
        if (res.code === 200) {
          getList();
          proxy?.$modal.msgSuccess(res.msg);
        }
      });
      statementOpen.value = false;
    }
  });
}

function cancelResult() {
  statementOpen.value = false;
}

function getRowKeys(row: any) {
  return row.id;
}

function cancel() {
  open.value = false;
  reset();
}

function reset() {
  form.value = {
    id: null,
    name: null,
    description: null,
    status: null,
    type: null,
    number: null,
    userId: null,
    deviceId: null,
    endTime: null,
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
  canEdit.value = true;
  title.value = proxy?.$t('workOrder.index.748855-45');
  nextTick(() => {
    formRef.value?.clearValidate();
  });
}

function handleUpdate(row: any) {
  reset();
  const id = row.id || ids.value;
  canEdit.value = row.canEdit;
  getWorkOrder(id).then((response: any) => {
    form.value = response.data;
    open.value = true;
    title.value = proxy?.$t('workOrder.index.748855-46');
    nextTick(() => {
      formRef.value?.clearValidate();
    });
    const filterEmptyImage = (obj: any) => {
      if (obj?.image) {
        obj.image = (obj.image || []).filter((url: string) => url && url.trim() !== '');
      }
    };
    if (form.value.details) {
      details.value = JSON.parse(form.value.details);
      filterEmptyImage(details.value);
    }
    if (form.value.result) {
      result.value = JSON.parse(form.value.result);
      filterEmptyImage(result.value);
    }
    if (details.value.location) {
      const addressArray = details.value.location.split('/');
      const selectedValues = addressArray
        .map((address: string) => {
          for (const province of cityOptions) {
            if (province.label === address) return province.value;
            for (const city of (province as any).children || []) {
              if (city.label === address) return city.value;
              for (const district of city.children || []) {
                if (district.label === address) return district.value;
              }
            }
          }
          return null;
        })
        .filter((val: any) => val);
      details.value.location = selectedValues;
    }
  });
}

async function submitForm() {
  const formValid = await formRef.value?.validate().catch(() => false);
  if (!formValid) return;

  const detailsValid = await detailsFormRef.value?.validate().catch(() => false);
  if (!detailsValid) return;

  form.value.details = JSON.stringify(details.value);
  if (form.value.id) {
    updateWorkOrder(form.value).then((res: any) => {
      if (res.code === 200) {
        proxy?.$modal.msgSuccess(proxy?.$t('updateSuccess'));
        getList();
      }
      open.value = false;
    });
  } else {
    addWorkOrder(form.value).then((res: any) => {
      if (res.code === 200) {
        proxy?.$modal.msgSuccess(proxy?.$t('addSuccess'));
        getList();
      }
      open.value = false;
    });
  }
}

function handleDelete(row?: any) {
  const delIds = row?.id || ids.value;
  proxy?.$modal
    .confirm(proxy?.$t('workOrder.index.748855-47', [delIds]))
    .then(() => {
      return delWorkOrder(delIds);
    })
    .then(() => {
      getList();
      proxy?.$modal.msgSuccess(proxy?.$t('delSuccess'));
      multipleTableRef.value?.clearSelection();
    })
    .catch(() => {});
}

function handleExport() {
  proxy?.download('iot/workOrder/export', { ...queryParams }, `workOrder_${new Date().getTime()}.xlsx`);
}

function handleRecord(row: any) {
  recordTitle.value = proxy?.$t('workOrder.index.748855-48');
  openRecord.value = true;
  workOrderRecordList(row.id).then((response: any) => {
    if (response.code === 200) {
      recordList.value = response.rows;
    }
  });
}
</script>

<style scoped lang="scss">
.record-timeline {
  padding: 2px 4px 4px;
}

.record-item {
  position: relative;
  display: flex;
  align-items: stretch;

  &.latest {
    .record-time {
      color: #3468ff;
      font-weight: 500;
    }
  }
}

.timeline-axis {
  width: 26px;
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-right: 8px;
  padding-top: 2px;
  align-self: stretch;
}

.timeline-dot {
  width: 20px;
  height: 20px;
  border-radius: 50%;
  border: 2px solid #c0c4cc;
  color: #c0c4cc;
  font-size: 12px;
  line-height: 16px;
  text-align: center;
  background: #fff;

  &.latest {
    border-color: #3468ff;
    color: #3468ff;
  }
}

.timeline-line {
  width: 1px;
  background: #cfd3dc;
  flex: 1;
  min-height: 0;
  margin-top: 3px;
  margin-bottom: -2px;
}

.record-content {
  flex: 1;
  min-width: 0;
  padding-bottom: 10px;
}

.record-time {
  font-size: 14px;
  line-height: 22px;
  color: #303133;
  font-weight: 500;
}

.custom-description {
  padding: 6px 0 0;
  line-height: 1.7;
  font-size: 15px;
}

.creator {
  color: #3498db;
  font-weight: 500;
  font-size: 16px;
}

.remark {
  color: #7f8c8d;
  background: #f5f7fb;
  padding: 10px 15px;
  border-radius: 6px;
  margin-top: 8px;
  border-left: 3px solid #2ecc71;
  font-size: 14px;
}

.workorder-two-col-dialog :deep(.el-dialog__body) {
  padding-top: 12px;
}

.workorder-form-grid {
  display: flex;
  flex-wrap: wrap;
  column-gap: 24px;
}

.workorder-form-grid .col-half {
  width: calc(50% - 12px);
  margin-right: 0;
}

.workorder-form-grid .col-half :deep(.el-input),
.workorder-form-grid .col-half :deep(.el-select),
.workorder-form-grid .col-half :deep(.el-date-editor),
.workorder-form-grid .col-half :deep(.el-textarea),
.workorder-form-grid .col-half .detail-card {
  width: 100% !important;
}

.detail-card {
  margin-top: 10px;
}

.detail-card :deep(.el-card__header) {
  background-color: #f5f7fa;
  padding: 10px 15px;
  font-weight: bold;
}

.detail-card :deep(.el-card__body) {
  padding: 15px;
}

.time {
  font-size: 14px;
  color: #95a5a6;
  margin-top: 4px;
}
</style>
