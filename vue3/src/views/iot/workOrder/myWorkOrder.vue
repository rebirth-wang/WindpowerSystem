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
          <el-button plain :icon="Download" @click="handleExport" v-hasPermi="['iot:workOrder:export']">
            {{ $t('export') }}
          </el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
      </el-row>

      <el-table v-loading="loading" :data="workOrderList" :border="false" ref="multipleTableRef">
        <el-table-column :label="$t('workOrder.index.748855-3')" align="center" prop="name" width="100" />
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
            <span>{{ parseTime(scope.row.endTime, '{y}-{m}-{d}') }}</span>
          </template>
        </el-table-column>
        <el-table-column :label="$t('workOrder.index.748855-11')" align="left" prop="description" width="200" />
        <el-table-column :label="$t('workOrder.index.748855-12')" align="center" prop="tenantName" />
        <el-table-column :label="$t('workOrder.index.748855-13')" align="center" prop="createBy" />
        <el-table-column :label="$t('workOrder.index.748855-14')" align="center" prop="createTime" width="180">
          <template #default="scope">
            <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d}') }}</span>
          </template>
        </el-table-column>

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
              :icon="Edit"
              @click="handleUpdate(scope.row)"
              v-hasPermi="['iot:workOrder:query']"
            >
              {{ $t('look') }}
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

    <!-- 结单 -->
    <el-dialog :title="statementTitle" v-model="statementOpen" width="680px" append-to-body class="custom-steps">
      <el-form ref="statementFormRef" :model="result" :rules="statementRules" label-width="100px">
        <el-form-item :label="$t('workOrder.index.748855-50')" prop="text">
          <el-input
            v-model="result.text"
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

    <!-- 查看我的工单对话框 -->
    <el-dialog :title="title" v-model="open" width="900px" append-to-body class="workorder-two-col-dialog">
      <el-form ref="formRef" :model="form" label-width="88px" disabled class="workorder-form-grid">
        <el-form-item class="col-half" :label="$t('workOrder.index.748855-3')" prop="name">
          <el-input v-model="form.name" :placeholder="$t('workOrder.index.748855-0')" style="width: 430px" />
        </el-form-item>

        <el-form-item class="col-half" :label="$t('workOrder.index.748855-5')" prop="type">
          <el-select v-model="form.type" :placeholder="$t('workOrder.index.748855-2')" style="width: 430px">
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
            <el-form ref="detailsFormRef" :model="details" label-width="50px" disabled>
              <el-form-item :label="$t('workOrder.index.748855-54')" prop="description">
                <el-input
                  v-model="details.description"
                  :placeholder="$t('workOrder.index.748855-55')"
                  style="width: 300px"
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
                  change-on-select
                  style="width: 300px"
                  clearable
                  filterable
                ></el-cascader>
              </el-form-item>
              <el-form-item :label="$t('workOrder.index.748855-52')" prop="image" style="margin-top: 20px">
                <imageUpload
                  ref="detailImageRef"
                  :modelValue="details.image"
                  :limit="5"
                  :fileSize="10"
                  @update:modelValue="getImagePathDetails($event)"
                  :action="imgUrl"
                />
              </el-form-item>
            </el-form>
          </el-card>
        </el-form-item>
        <el-form-item class="col-half" :label="$t('workOrder.index.748855-50')" v-if="form.id && (form.status == 1 || form.status == 5)">
          <el-card shadow="never" class="detail-card" style="width: 430px">
            <el-form ref="resultFormRef" :model="result" label-width="50px" disabled>
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
          <el-button @click="cancel">{{ $t('cancel') }}</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, getCurrentInstance } from 'vue';
import { Search, Refresh, Download, Edit, EditPen } from '@element-plus/icons-vue';
import { myWorkOrderList, getWorkOrder, optionWorkOrder } from '@/api/iot/workOrder';
import { listUser } from '@/api/system/user';
import { listDeviceShort } from '@/api/iot/device';
import imageUpload from '@/components/ImageUpload/index.vue';
import { regionData } from 'element-china-area-data';
import { parseTime } from '@/utils/ruoyi';
import { useDict } from '@/utils/dict';

const { work_order_type, work_order_status } = useDict('work_order_type', 'work_order_status');
const proxy = getCurrentInstance()?.proxy as any;

const loading = ref(true);
const showSearch = ref(true);
const total = ref(0);
const workOrderList = ref<any[]>([]);
const title = ref('');
const open = ref(false);
const userList = ref<any[]>([]);
const deviceList = ref<any[]>([]);
const cityOptions = regionData as any[];
const statementOpen = ref(false);
const statementTitle = ref('');
const imgUrl = import.meta.env.VITE_APP_BASE_API + '/common/upload?path=iot/workOrder';

const queryFormRef = ref<any>(null);
const formRef = ref<any>(null);
const statementFormRef = ref<any>(null);
const multipleTableRef = ref<any>(null);

const queryParams = reactive<any>({
  pageNum: 1,
  pageSize: 10,
  name: null,
  status: null,
  type: null,
  showOwner: true,
});
const form = ref<any>({});
const result = ref<any>({
  description: '',
  image: [],
  text: '',
});
const details = ref<any>({
  description: '',
  image: [],
});

const statementRules = reactive<any>({
  text: [{ required: true, message: proxy?.$t('workOrder.index.748855-51'), trigger: 'blur' }],
});
const paramsRules = reactive<any>({
  description: [{ required: true, message: proxy?.$t('workOrder.index.748855-51'), trigger: 'blur' }],
  location: [{ required: true, message: proxy?.$t('workOrder.index.748855-58'), trigger: 'change' }],
});

onMounted(() => {
  statementTitle.value = proxy?.$t('workOrder.index.748855-50');
  getList();
  getDeviceList();
  getUserList();
});

function getList() {
  loading.value = true;
  myWorkOrderList(queryParams).then((response: any) => {
    workOrderList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}

function getUserList() {
  listUser(queryParams).then((response: any) => {
    if (response.code === 200) {
      userList.value = response.rows.map((item: any) => ({
        value: item.userId,
        label: item.userName,
      }));
    }
  });
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

function handleOptionWorkOrder(type: number, row: any) {
  form.value = row;
  if (type == 2) {
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
    result.value = { description: '', image: [], text: '' };
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
    tenantId: null,
    tenantName: null,
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

function handleUpdate(row: any) {
  reset();
  const id = row.id;
  getWorkOrder(id).then((response: any) => {
    form.value = response.data;
    open.value = true;
    title.value = proxy?.$t('workOrder.index.748855-46');
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

function handleExport() {
  proxy?.download('iot/workOrder/export', { ...queryParams }, `workOrder_${new Date().getTime()}.xlsx`);
}

function getImagePath(data: any) {
  result.value.image = (data || '').split(',').filter((url: string) => url && url.trim() !== '');
}

function getImagePathDetails(data: any) {
  details.value.image = (data || '').split(',').filter((url: string) => url && url.trim() !== '');
}
</script>

<style scoped lang="scss">
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
</style>
