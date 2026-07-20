<template>
  <div class="modbus-task">
    <el-row :gutter="10" style="margin-bottom: 8px">
      <el-col :span="1.5">
        <div class="btn-group">
          <el-button type="primary" plain :icon="Edit" size="small" @click="setSlave" v-if="!enableSetSlave">
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
      <div style="float: right; margin-right: 10px">
        <el-tooltip :content="$t('device.device-edit.148398-107')" placement="top">
          <el-button
            type="primary"
            plain
            :icon="Edit"
            size="small"
            @click="handleInstruction"
            :disabled="device.status !== 3"
          >
            {{ $t('device.instruction-parsing.830424-56') }}
          </el-button>
        </el-tooltip>
      </div>
    </el-row>
    <el-table v-loading="loading" :data="commandList" :border="false" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
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
            type="danger"
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
            <el-form-item prop="setValueSwitch1" v-show="['05'].includes(createForm.code)">
              <template #label>
                <div class="form-item-label">
                  <div style="margin-right: auto">{{ $t('product.product-modbus-task.894593-18') }}</div>
                </div>
              </template>
              <el-switch v-model="createForm.setValueSwitch1" active-value="1" inactive-value="0" />
            </el-form-item>
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
                    createForm.setValue16 = int2hexFn(createForm.setValue);
                  }
                "
                @input="
                  () => {
                    createForm.setValue16 = int2hexFn(createForm.setValue);
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
                    createForm.setValue = hex2intFn(createForm.setValue16);
                  }
                "
              >
                <template #append>{{ createForm.setValue }}</template>
              </el-input>
            </el-form-item>
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
                      item.value16 = int2hexFn(item.value);
                      refreshRegisterInputs(item, index);
                    }
                  "
                  @input="
                    () => {
                      item.value16 = int2hexFn(item.value);
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
                      item.value = hex2intFn(item.value16);
                      refreshRegisterInputs(item, index);
                    }
                  "
                >
                  <template #append>{{ item.value }}</template>
                </el-input>
              </el-form-item>
            </el-col>
            <el-col :span="6" v-for="(item, index) in dataList" :key="'IO' + index" v-show="createForm.code == '15'">
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
          <el-button type="primary" @click="handleAdd" v-hasPermi="['modbus:job:add']">{{ $t('confirm') }}</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, watch, getCurrentInstance, onMounted } from 'vue';
import { Edit, Close, Plus, Delete } from '@element-plus/icons-vue';
import { hex2int, int2hex } from '@/utils/common';
import { listJob, batchIntruction, delJob, addJob, updateJob } from '@/api/iot/modbusJob';
import { useDict } from '@/utils/dict/useDict';

const { proxy } = getCurrentInstance() as any;
const { product_command_function_code } = useDict('product_command_function_code');

const props = defineProps({
  device: { type: Object, default: null },
});

const loading = ref(false);
const editDialog = ref(false);
const enableSetSlave = ref(false);
const ids = ref<any[]>([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const deviceInfo = ref<any>({});
const jobList = ref<any[]>([]);
const commandList = ref<any[]>([]);
const selectDatas = ref<any[]>([]);
const registerValList = ref<any[]>([]);
const dataList = ref<any[]>([]);
const currentEditIndex = ref(0);
const delDataIds = ref<any[]>([]);

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  deviceId: null as any,
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
  taskId: null as any,
  setValueSwitch1: '0',
  setValue: 0,
  setValue16: '0000',
  setValueSwitch: 'Dec',
  startPath: 0,
  startPath16: '0000',
  startPathSwitch: 'Dec',
  path: '1',
  values: [],
});

function int2hexFn(str: any) {
  return int2hex(str);
}
function hex2intFn(str: any) {
  return hex2int(str);
}

watch(
  () => props.device,
  (newVal: any, oldVal: any) => {
    if (newVal?.deviceId && newVal.deviceId !== oldVal?.deviceId) {
      queryParams.deviceId = newVal.deviceId;
      deviceInfo.value = newVal;
      getList();
    }
  }
);

watch(enableSetSlave, (n) => {
  if (!n) getList();
  delDataIds.value = [];
});

onMounted(() => {
  if (props.device?.deviceId) {
    queryParams.deviceId = props.device.deviceId;
    queryParams.commandType = 2;
    getList();
  }
  resetCreateForm();
});

function getList() {
  loading.value = true;
  commandList.value = [];
  listJob(queryParams).then((response: any) => {
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

function handleInstruction() {
  if (selectDatas.value.length === 0) {
    proxy?.$modal.msgError(proxy?.$t('device.instruction-config.984980-6'));
    return;
  }
  const params = { clientId: props.device?.serialNumber, commands: selectDatas.value };
  proxy?.$modal.confirm(proxy?.$t('device.instruction-config.984980-2')).then(() => {
    batchIntruction(params)
      .then((response: any) => {
        if (response.code === 200) {
          proxy?.$modal.msgSuccess(proxy?.$t('device.realTime-status.845353-6'));
        } else {
          proxy?.$modal.msgError(response.msg);
        }
      })
      .catch(() => {});
  });
}

function saveSlave() {
  const data = {
    taskId: createForm.taskId,
    deviceId: props.device?.deviceId,
    command: JSON.stringify(commandList.value),
    status: 0,
    commandType: 2,
    serialNumber: props.device?.serialNumber,
  };
  updateJob(data).then(() => {
    proxy?.$modal.msgSuccess(proxy?.$t('product.product-modbus.562372-53'));
    getList();
    setSlave();
  });
}

function handleSelectionChange(selection: any[]) {
  selectDatas.value = selection;
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

function cancelSlave() {
  enableSetSlave.value = !enableSetSlave.value;
}

function handleAddData() {
  commandList.value.push({ register: 0, address: 1, code: '03', quantity: 1, values: [] });
}

function handleDelete(row: any, index: number, list: any[]) {
  if (props.device?.protocolCode !== 'MODBUS-TCP') {
    const taskIds = row.taskId || ids.value;
    proxy?.$modal
      .confirm(proxy?.$t('device.instruction-config.984980-3', [taskIds]))
      .then(() => {
        return delJob(taskIds);
      })
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
      commandList.value[index].values = dataList.value.map((item: any) => item.value);
      break;
    case '16':
      commandList.value[index].values = registerValList.value.map((item: any) => item.value);
      break;
  }
  editDialog.value = false;
}

function openEdit(row: any, index: number, list: any[]) {
  editDialog.value = true;
  resetCreateForm();
  createForm.code = row.code;
  createForm.quantity = row.quantity;
  createForm.values = row.values;
  currentEditIndex.value = index;
  if (createForm.code == '15') {
    if (row.values && row.values.length > 0) {
      dataList.value = row.values.map((value: any) => ({ value }));
    } else {
      dataList.value = [];
      for (let i = 0; i < createForm.quantity; i++) {
        dataList.value.push({ value: '0' });
      }
    }
  } else if (createForm.code == '16') {
    if (row.values && row.values.length > 0) {
      registerValList.value = row.values.map((value: any) => ({
        value,
        value16: value.toString(16).padStart(4, '0'),
        switch: 'Dec',
      }));
    } else {
      registerValList.value = [];
      for (let i = 0; i < createForm.quantity; i++) {
        registerValList.value.push({ value: 0, value16: '0000', switch: 'Dec' });
      }
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

function refreshRegisterInputs(item: any, index: number) {
  registerValList.value[index] = { ...item };
}

function refreshIOInputs(item: any, index: number) {
  dataList.value[index] = { ...item };
}
</script>

<style lang="scss" scoped>
$border-color: #dcdfe6;
$right-btn-color: #1890ff;

:deep(.el-button--text .el-icon-edit) {
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
  .form-item-label {
    display: flex;
    align-items: center;
    width: 100%;
  }
}
</style>
