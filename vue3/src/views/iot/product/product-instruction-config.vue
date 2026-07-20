<template>
  <div class="modbus-task">
    <el-row :gutter="10" style="margin-bottom: 8px">
      <el-col :span="1.5">
        <div class="btn-group">
          <el-button
            type="primary"
            plain
            :icon="Edit"
            size="small"
            @click="setSlave"
            v-if="!enableSetSlave && product.status == 1"
          >
            {{ $t('product.product-modbus.562372-2') }}
          </el-button>
          <el-button
            type="primary"
            plain
            :icon="Edit"
            size="small"
            @click="saveSlave"
            v-if="enableSetSlave"
            v-hasPermi="['modbus:config:edit']"
          >
            {{ $t('product.product-modbus.562372-3') }}
          </el-button>
          <el-button type="info" plain :icon="Close" size="small" @click="cancelSlave" v-if="enableSetSlave">
            {{ $t('product.product-modbus.562372-4') }}
          </el-button>
        </div>
      </el-col>
    </el-row>
    <el-table v-loading="loading" :data="commandList" :border="false">
      <el-table-column :label="$t('product.product-modbus-task.894593-71')" align="center" prop="address" width="200px">
        <template #default="scope">
          <el-input-number
            style="width: 100%"
            :disabled="!enableSetSlave"
            v-model="scope.row.address"
            :min="0"
            :max="400000"
            :label="$t('product.product-modbus.562372-30')"
          ></el-input-number>
        </template>
      </el-table-column>
      <el-table-column
        :label="$t('product.product-modbus-task.894593-72')"
        align="center"
        prop="register"
        width="200px"
      >
        <template #default="scope">
          <el-input-number
            style="width: 100%"
            :disabled="!enableSetSlave"
            v-model="scope.row.register"
            :min="0"
            :max="400000"
            :label="$t('product.product-modbus.562372-30')"
          ></el-input-number>
        </template>
      </el-table-column>
      <el-table-column
        :label="$t('product.product-modbus-task.894593-73')"
        align="center"
        prop="functioncode"
        width="260px"
      >
        <template #default="scope">
          <el-select
            v-model="scope.row.code"
            style="width: 100%"
            :disabled="!enableSetSlave"
            @change="changeFunctionCode(scope.row)"
          >
            <el-option
              v-for="dict in product_command_function_code"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            ></el-option>
          </el-select>
        </template>
      </el-table-column>
      <el-table-column :label="$t('device.instruction-config.984980-0')" align="center" prop="quantity" width="200px">
        <template #default="scope">
          <el-input-number
            style="width: 100%"
            :disabled="!enableSetSlave || ['05', '06'].includes(scope.row.code)"
            v-model="scope.row.quantity"
            :min="0"
            @change="changeQuentity(scope.row)"
          ></el-input-number>
        </template>
      </el-table-column>
      <el-table-column :label="$t('device.instruction-config.984980-1')" align="center" prop="values" width="300px">
        <template #default="scope">
          <template v-if="['05', '06', '15', '16'].includes(scope.row.code)">
            {{ scope.row.values ? scope.row.values : '' }}
            <el-button
              :icon="Edit"
              type="primary"
              link
              size="small"
              :disabled="!enableSetSlave"
              @click="openEdit(scope.row, scope.$index, commandList)"
            ></el-button>
          </template>
          <template v-else>-</template>
        </template>
      </el-table-column>
      <el-table-column
        :label="$t('product.product-modbus.562372-34')"
        align="center"
        class-name="small-padding fixed-width"
        fixed="right"
      >
        <template #default="scope">
          <el-button
            size="small"
            type="primary"
            link
            :icon="Delete"
            v-if="enableSetSlave"
            @click="handleDelete(scope.row, scope.$index, commandList)"
            v-hasPermi="['iot:config:remove']"
          >
            {{ $t('product.product-modbus.562372-35') }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-row :gutter="10" class="mb8" style="margin-top: 20px">
      <el-col :span="1.5">
        <el-button type="primary" plain :icon="Plus" size="small" @click="handleAddData" v-if="enableSetSlave">
          {{ $t('device.instruction-config.984980-5') }}
        </el-button>
      </el-col>
    </el-row>

    <el-dialog :title="$t('device.instruction-config.984980-4')" v-model="editDialog" width="600px">
      <div class="dialog-content">
        <el-form :model="createForm" label-position="top">
          <el-row :gutter="40">
            <!-- 写值 code=05 -->
            <el-form-item prop="setValue" v-show="['05'].includes(createForm.code)">
              <template #label>
                <div class="form-item-label">
                  <div style="margin-right: auto">{{ $t('product.product-modbus-task.894593-18') }}</div>
                </div>
              </template>
              <el-switch v-model="createForm.setValueSwitch1" active-value="1" inactive-value="0" />
            </el-form-item>
            <!-- 写值 code=06 -->
            <el-form-item prop="setValue" v-show="['06'].includes(createForm.code)">
              <template #label>
                <div class="form-item-label">
                  <el-tooltip :content="createForm.setValueSwitch" placement="top">
                    <el-switch
                      v-model="createForm.setValueSwitch"
                      size="small"
                      active-color="#13ce66"
                      inactive-color="#ff4949"
                      active-value="Dec"
                      inactive-value="Hex"
                    />
                  </el-tooltip>
                </div>
              </template>
              <el-input
                v-model="createForm.setValue"
                type="number"
                v-show="createForm.setValueSwitch == 'Dec'"
                @change="
                  () => {
                    createForm.setValue16 = int2hex(createForm.setValue);
                  }
                "
                @input="
                  () => {
                    createForm.setValue16 = int2hex(createForm.setValue);
                  }
                "
              >
                <template #append>0x{{ createForm.setValue16 }}</template>
              </el-input>
              <el-input
                v-model="createForm.setValue16"
                v-show="createForm.setValueSwitch != 'Dec'"
                @input="
                  () => {
                    createForm.setValue = hex2int(createForm.setValue16);
                  }
                "
              >
                <template #append>{{ createForm.setValue }}</template>
              </el-input>
            </el-form-item>
            <!-- 批量写寄存器值 -->
            <el-col
              :span="12"
              v-for="(item, index) in registerValList"
              :key="'register' + index"
              v-show="createForm.code == '16'"
            >
              <el-form-item prop="registerValList">
                <template #label>
                  <div class="form-item-label">
                    <div style="margin-right: auto">#{{ index }} {{ $t('product.product-modbus-task.894593-17') }}</div>
                    <el-tooltip :content="item.switch" placement="top">
                      <el-switch
                        v-model="item.switch"
                        size="small"
                        active-color="#13ce66"
                        @change="
                          () => {
                            refreshRegisterInputs(item, index);
                          }
                        "
                        inactive-color="#ff4949"
                        active-value="Dec"
                        inactive-value="Hex"
                      />
                    </el-tooltip>
                  </div>
                </template>
                <el-input
                  v-model="item.value"
                  type="number"
                  v-show="item.switch == 'Dec'"
                  :min="0"
                  @change="
                    () => {
                      item.value16 = int2hex(item.value);
                      refreshRegisterInputs(item, index);
                    }
                  "
                  @input="
                    () => {
                      item.value16 = int2hex(item.value);
                      refreshRegisterInputs(item, index);
                    }
                  "
                >
                  <template #append>0x{{ item.value16 }}</template>
                </el-input>
                <el-input
                  v-model="item.value16"
                  v-show="item.switch != 'Dec'"
                  @input="
                    () => {
                      item.value = hex2int(item.value16);
                      refreshRegisterInputs(item, index);
                    }
                  "
                >
                  <template #append>{{ item.value }}</template>
                </el-input>
              </el-form-item>
            </el-col>
            <!-- 批量写线圈值 -->
            <el-col :span="6" v-for="(item, index) in IOValList" :key="'IO' + index" v-show="createForm.code == '15'">
              <el-form-item prop="registerValList">
                <template #label>
                  <div class="form-item-label">
                    <div style="margin-right: auto">#{{ index }} {{ $t('product.product-modbus-task.894593-18') }}</div>
                  </div>
                </template>
                <el-switch
                  v-model="item.value"
                  active-value="1"
                  inactive-value="0"
                  @change="
                    () => {
                      refreshIOInputs(item, index);
                    }
                  "
                />
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
      </div>
      <template #footer>
        <div class="dialog-btn">
          <el-button @click="editDialog = false">{{ $t('product.product-modbus-task.894593-27') }}</el-button>
          <el-button type="primary" @click="handleAdd" v-hasPermi="['productModbus:job:add']">
            {{ $t('confirm') }}
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, watch, onMounted, getCurrentInstance } from 'vue';
import { Edit, Close, Plus, Delete } from '@element-plus/icons-vue';
import { hex2int as hex2intFn, int2hex as int2hexFn } from '@/utils/common';
import { listProductJob, delProductJob, updateProductJob } from '@/api/iot/modbusJob';
import { checkPermi } from '@/utils/permission';
import useDict from '@/utils/dict/useDict';

const { product_command_function_code } = useDict(
  'sys_job_group',
  'sys_job_status',
  'variable_operation_interval',
  'variable_operation_time',
  'variable_operation_week',
  'variable_operation_day',
  'variable_operation_type',
  'product_command_function_code'
);
const { proxy } = getCurrentInstance() as any;

const props = defineProps({
  product: {
    type: Object,
    default: null,
  },
});

const loading = ref(false);
const editDialog = ref(false);
const delDataIds = ref<any[]>([]);
const total = ref(0);
const productInfo = ref<any>({});
const enableSetSlave = ref(false);
const jobList = ref<any[]>([]);
const commandList = ref<any[]>([]);
const registerValList = ref<any[]>([]);
const IOValList = ref<any[]>([]);
const currentEditIndex = ref(0);

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  productId: null as any,
  command: null as any,
  taskId: null as any,
  status: null as any,
  commandType: 2,
});

const createForm = reactive<any>({
  cycleType: 1,
  status: 0,
  register: 1,
  code: '03',
  address: 1,
  quantity: 1,
  taskId: null,
  setValueSwitch1: '0',
  setValueSwitch: 'Dec',
  setValue: 0,
  setValue16: '0000',
});

watch(
  () => props.product,
  (newVal, oldVal) => {
    if (newVal?.productId && newVal.productId !== oldVal?.productId) {
      queryParams.productId = newVal.productId;
      queryParams.commandType = 1;
      productInfo.value = newVal;
      getList();
    }
  }
);

watch(enableSetSlave, (n) => {
  if (!n) getList();
  delDataIds.value = [];
});

onMounted(() => {
  const { productId } = props.product || {};
  if (productId) {
    queryParams.productId = productId;
    getList();
  }
  resetCreateForm();
});

/** 查询任务列表 */
function getList() {
  loading.value = true;
  commandList.value = [];
  queryParams.commandType = 2;
  listProductJob(queryParams).then((response: any) => {
    jobList.value = response.rows;
    if (response.rows.length > 0) {
      if (response.rows[0].command.includes('{"register":')) {
        commandList.value = JSON.parse(response.rows[0].command);
      }
      createForm.taskId = response.rows[0].taskId;
    } else {
      createForm.taskId = '';
    }
    total.value = response.total;
    loading.value = false;
  });
}

function setSlave() {
  enableSetSlave.value = !enableSetSlave.value;
}

function saveSlave() {
  const data = {
    taskId: createForm.taskId,
    productId: props.product.productId,
    command: JSON.stringify(commandList.value),
    status: 0,
    commandType: 2,
  };
  updateProductJob(data).then(() => {
    proxy?.$modal.msgSuccess(proxy?.$t('product.product-modbus.562372-53'));
    getList();
    setSlave();
  });
}

function cancelSlave() {
  enableSetSlave.value = !enableSetSlave.value;
}

function handleAddData() {
  commandList.value.push({ register: 0, address: 1, code: '03', quantity: 1, values: [] });
}

function handleDelete(row: any, index: number, list: any[]) {
  if (props.product.protocolCode !== 'MODBUS-TCP') {
    const taskIds = row.taskId;
    proxy?.$modal
      .confirm(proxy?.$t('device.instruction-config.984980-3', [taskIds]))
      .then(() => delProductJob(taskIds))
      .then(() => {
        getList();
        proxy?.$modal.msgSuccess(proxy?.$t('product.product-modbus-task.894593-65'));
      })
      .catch(() => {});
  } else {
    list.splice(index, 1);
    delDataIds.value.push(row.id);
  }
}

function handleAdd() {
  const index = currentEditIndex.value;
  switch (createForm.code) {
    case '05':
      commandList.value[index].values = [createForm.setValueSwitch1];
      break;
    case '06':
      commandList.value[index].values = [createForm.setValue];
      break;
    case '15':
      commandList.value[index].values = IOValList.value.map((item: any) => item.value);
      break;
    case '16':
      commandList.value[index].values = registerValList.value.map((item: any) => item.value);
      break;
  }
  editDialog.value = false;
}

function openEdit(row: any, index: number, list: any[]) {
  editDialog.value = true;
  createForm.code = row.code;
  createForm.quantity = row.quantity;
  createForm.values = row.values;
  currentEditIndex.value = index;
  if (createForm.code == '15') {
    if (row.values && row.values.length > 0) {
      IOValList.value = row.values.map((value: any) => ({ value }));
    } else {
      const arr: any[] = [];
      for (let i = 0; i < createForm.quantity; i++) {
        arr.push({ value: '0' });
      }
      IOValList.value = arr;
    }
  } else if (createForm.code == '16') {
    if (row.values && row.values.length > 0) {
      registerValList.value = row.values.map((value: any) => ({
        value,
        value16: value.toString(16).padStart(4, '0'),
        switch: 'Dec',
      }));
    } else {
      const arr: any[] = [];
      for (let i = 0; i < createForm.quantity; i++) {
        arr.push({ value: 0, value16: '0000', switch: 'Dec' });
      }
      registerValList.value = arr;
    }
  } else if (createForm.code == '05') {
    createForm.setValueSwitch1 = row.values && row.values.length > 0 ? row.values[0] : '0';
  } else if (createForm.code == '06') {
    createForm.setValue = row.values && row.values.length > 0 ? row.values[0] : 0;
  }
}

function changeFunctionCode(row: any) {
  row.quantity = 1;
  if (['05', '06', '15', '16'].includes(row.code)) {
    row.values = [];
  } else {
    commandList.value.forEach((item: any) => {
      delete item.values;
    });
  }
}

function changeQuentity(row: any) {
  if (['05', '06', '15', '16'].includes(row.code)) {
    row.values = [];
  } else {
    commandList.value.forEach((item: any) => {
      delete item.values;
    });
  }
}

function resetCreateForm() {
  Object.assign(createForm, {
    path: '1',
    code: '01',
    startPath: 0,
    startPath16: '0000',
    quantity: 1,
    startPathSwitch: 'Dec',
    setValue: 0,
    setValue16: '0000',
    setValueSwitch: 'Dec',
    setValueSwitch1: '0',
    status: 0,
    cycleType: 1,
  });
}

function int2hex(str: any) {
  return int2hexFn(str);
}

function hex2int(str: any) {
  return hex2intFn(str);
}

function refreshRegisterInputs(item: any, index: number) {
  registerValList.value[index] = { ...item };
}

function refreshIOInputs(item: any, index: number) {
  IOValList.value[index] = { ...item };
}

// 搜索按钮操作
function handleQuery() {
  getList();
}
</script>

<style lang="scss" scoped>
$border-color: #dcdfe6;
$right-btn-color: #1890ff;

:deep(.el-button--text i) {
  color: #409eff;
}

.modbus-task {
  width: 100%;
  padding: 10px;
}

.dialog-content {
  width: 100%;
  box-sizing: border-box;
  padding: 0px 20px;
  overflow: auto;

  .input-item {
    width: 250px;
  }

  .create-title {
    display: flex;
    line-height: 36px;
    margin-bottom: 16px;
    .title-right {
      margin-left: auto;
    }
  }

  .create-code {
    font-size: 18px;
    line-height: 36px;
    font-weight: 800;
  }

  .form-item-label {
    display: flex;
    align-items: center;
    width: 100%;
    :deep(.el-form-item__label) {
      width: 100%;
    }
  }

  .timer-wrap {
    .timer-period {
      display: inline-block;
      margin-left: 30px;
      color: #000000;
      font-size: 12px;
      font-weight: normal;
    }
    .timer-custom {
      display: block;
      margin-top: 12px;
      color: #000000;
      font-size: 12px;
      font-weight: normal;
    }
  }

  .comp-add-edit {
    display: flex;
    flex-direction: column;
    :deep(.el-form-item__content) {
      margin-left: 0 !important;
    }
    .comput-formula-box {
      padding: 20px 0;
      border: 1px solid #e7e9f1;
      margin-left: 50px;
      display: flex;
      .title {
        text-align: right;
        width: 96px;
        padding-right: 16px;
      }
      .content {
        font-size: 12px;
        line-height: 32px;
        .alias-wrap {
          width: 28px;
          height: 28px;
          background-image: linear-gradient(180deg, #6fb0ff, #3c78ff);
          box-shadow: 0 0 4px 0 rgba(0, 0, 0, 0.2);
          font-size: 12px;
          font-weight: 400;
          line-height: 28px;
          text-align: center;
          margin-top: 2px;
          color: #fff;
        }
      }
    }
  }
}
</style>
