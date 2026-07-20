<template>
  <div class="device-sub-container">
    <el-form :model="queryParams" ref="queryFormRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item>
        <el-button plain type="primary" :icon="Plus" size="small" @click="handleBindSubDevice">
          {{ $t('device.sub.083943-6') }}
        </el-button>
      </el-form-item>
      <el-form-item>
        <el-button plain type="primary" :icon="Plus" size="small" @click="handleAdd">
          {{ $t('device.sub.083943-0') }}
        </el-button>
      </el-form-item>
      <el-form-item>
        <el-button plain type="danger" :icon="Delete" size="small" @click="handleDelete()" :disabled="ids.length == 0">
          {{ $t('device.sub.083943-1') }}
        </el-button>
      </el-form-item>
      <el-form-item>
        <el-button :icon="Refresh" size="small" @click="resetQuery">{{ $t('refresh') }}</el-button>
        <span style="font-size: 12px; margin-left: 10px">{{ $t('device.device-sub.299018-28') }}</span>
      </el-form-item>
      <el-form-item v-if="!isSet" style="float: right">
        <el-button plain type="primary" :icon="Edit" size="small" @click="setSubDeviceAddress">
          {{ $t('device.sub.083943-2') }}
        </el-button>
      </el-form-item>
      <el-form-item v-if="isSet" style="float: right">
        <el-button plain type="primary" :icon="Check" size="small" @click="saveSetting">{{ $t('save') }}</el-button>
      </el-form-item>
      <el-form-item v-if="isSet" style="float: right">
        <el-button plain type="info" :icon="CircleClose" size="small" @click="cancelSetting">
          {{ $t('cancel') }}
        </el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="deviceList" @selection-change="handleSelectionChange" :border="false">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column :label="$t('device.device-edit.148398-1')" align="left" prop="subDeviceName" min-width="160px" />
      <el-table-column :label="$t('device.device-edit.148398-7')" align="left" prop="subClientId" min-width="140px" />
      <el-table-column :label="$t('device.sub.083943-3')" align="left" prop="address" width="200px">
        <template #default="scope">
          <el-input
            style="width: 100%; text-align: center"
            :disabled="!isSet"
            v-model="scope.row.address"
            size="small"
            :placeholder="$t('device.sub.083943-2')"
          ></el-input>
        </template>
      </el-table-column>
      <el-table-column :label="$t('creatTime')" align="center" prop="createTime" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total > 0"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加子设备 -->
    <addSubDevice ref="addSubDeviceRef" :gateway="gateway" @addSuccess="addSuccess"></addSubDevice>
    <!-- 绑定子设备 -->
    <bindSubDevice ref="bindSubDeviceRef" :gateway="gateway" @addSuccess="addSuccess"></bindSubDevice>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, watch, onMounted, getCurrentInstance } from 'vue';
import { Plus, Delete, Refresh, Edit, Check, CircleClose } from '@element-plus/icons-vue';
import { parseTime } from '@/utils/ruoyi';
import addSubDevice from './add-sub-device.vue';
import bindSubDevice from './bind-sub-device.vue';
import { listGateway, delGateway, editGatewayBatch } from '@/api/iot/gateway';

const { proxy } = getCurrentInstance()!;

const props = defineProps({
  device: {
    type: Object,
    default: null,
  },
});

// 遮罩层
const loading = ref(true);
// 选中数组
const ids = ref<number[]>([]);
// 非单个禁用
const single = ref(true);
// 非多个禁用
const multiple = ref(true);
// 显示搜索条件
const showSearch = ref(true);
// 总条数
const total = ref(0);
// 设备表格数据
const deviceList = ref<any[]>([]);
const deviceInfo = ref<any>({});
// 弹出层标题
const title = ref('');
// 是否显示弹出层
const open = ref(false);
// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  parentClientId: null as string | null,
});
// 表单参数
const form = ref<any>({});
const gateway = reactive<any>({});
//是否可设置子设备地址
const isSet = ref(false);

const queryFormRef = ref();
const addSubDeviceRef = ref();
const bindSubDeviceRef = ref();

watch(
  () => props.device,
  (newVal) => {
    deviceInfo.value = newVal;
    if (deviceInfo.value && deviceInfo.value.deviceId != 0) {
      queryParams.parentClientId = deviceInfo.value.serialNumber;
      gateway.parentClientId = deviceInfo.value.serialNumber;
      getList();
    }
  }
);

onMounted(() => {
  gateway.productId = props.device.productId;
  gateway.serialNumber = props.device.serialNumber;
  gateway.parentClientId = props.device.serialNumber;
  gateway.deviceId = props.device.deviceId;
  deviceInfo.value = props.device;
  if (deviceInfo.value && deviceInfo.value.deviceId != 0) {
    queryParams.parentClientId = deviceInfo.value.serialNumber;
    gateway.parentProductId = deviceInfo.value.productId;
    getList();
  }
});

/** 查询子设备列表 */
function getList() {
  queryParams.parentClientId = props.device.serialNumber;
  listGateway(queryParams).then((response: any) => {
    deviceList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}

// 取消按钮
function cancel() {
  open.value = false;
  reset();
}

// 表单重置
function reset() {
  form.value = {
    id: null,
    parentClientId: null,
    subDeviceId: null,
    address: null,
    createBy: null,
    createTime: null,
    updateBy: null,
    updateTime: null,
    remark: null,
  };
}

/** 重置按钮操作 */
function resetQuery() {
  proxy?.resetForm(queryFormRef.value);
  getList();
}

// 多选框选中数据
function handleSelectionChange(selection: any[]) {
  ids.value = selection.map((item) => item.id);
  single.value = selection.length !== 1;
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
function handleAdd() {
  addSubDeviceRef.value.open = true;
  addSubDeviceRef.value.getList();
}

//绑定子设备
function handleBindSubDevice() {
  bindSubDeviceRef.value.open = true;
  bindSubDeviceRef.value.getList();
}

/** 删除按钮操作 */
function handleDelete(row?: any) {
  const deviceIds = row?.deviceId || ids.value;
  proxy?.$modal
    .confirm(proxy?.$t('device.device-sub.299018-25', [deviceIds]))
    .then(function () {
      return delGateway(deviceIds);
    })
    .then(() => {
      getList();
      proxy?.$modal.msgSuccess(proxy?.$t('device.device-sub.299018-27'));
    })
    .catch(() => {});
}

/**设置子设备地址*/
function setSubDeviceAddress() {
  isSet.value = !isSet.value;
}

/**保存子设备地址设置*/
function saveSetting() {
  isSet.value = !isSet.value;
  editGatewayBatch(deviceList.value)
    .then((response: any) => {
      if (response.code == 200) {
        proxy?.$modal.msgSuccess(proxy?.$t('saveSuccess'));
        getList();
      }
    })
    .catch(() => {
      getList(); // 子设备地址还原修改之前的
    });
}

function cancelSetting() {
  isSet.value = !isSet.value;
}

/**添加成功 */
function addSuccess() {
  getList();
}

defineExpose({ getList });
</script>

<style lang="scss" scoped>
.device-sub-container {
  padding: 0px 20px 10px;
}
</style>
