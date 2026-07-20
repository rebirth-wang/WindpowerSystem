<template>
  <div>
    <el-form :model="queryParams" ref="queryFormRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item prop="logType">
        <el-select
          v-model="queryParams.logType"
          :placeholder="$t('device.device-log.798283-1')"
          clearable
          @keyup.enter="handleQuery"
          style="width: 200px"
        >
          <el-option v-for="dict in iot_event_type" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item prop="identify">
        <el-input
          v-model="queryParams.identify"
          :placeholder="$t('device.device-log.798283-3')"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-date-picker
          v-model="daterangeTime"
          style="width: 240px"
          value-format="YYYY-MM-DD"
          type="daterange"
          range-separator="-"
          :start-placeholder="$t('device.device-log.798283-5')"
          :end-placeholder="$t('device.device-log.798283-6')"
        ></el-date-picker>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :icon="Search" @click="handleQuery">{{ $t('device.device-log.798283-7') }}</el-button>
        <el-button :icon="Refresh" @click="resetQuery">{{ $t('device.device-log.798283-8') }}</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="deviceLogList" :border="false">
      <el-table-column :label="$t('device.device-log.798283-9')" align="center" prop="logType" min-width="100">
        <template #default="scope">
          <dict-tag :options="iot_event_type" :value="scope.row.logType" />
        </template>
      </el-table-column>
      <el-table-column :label="$t('device.device-log.798283-10')" align="center" prop="logType" min-width="100">
        <template #default="scope">
          <dict-tag :options="device_mode" :value="scope.row.mode" />
        </template>
      </el-table-column>
      <el-table-column :label="$t('device.device-log.798283-14')" align="center" prop="createTime" min-width="155">
        <template #default="scope">
          <span>{{ scope.row.createTime }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('device.device-log.798283-2')" align="left" prop="identify" min-width="120" />
      <el-table-column :label="$t('device.device-log.798283-15')" align="left" prop="logValue" min-width="130">
        <template #default="scope">
          <div v-html="formatValueDisplay(scope.row)"></div>
        </template>
      </el-table-column>
      <el-table-column :label="$t('device.device-log.798283-16')" align="left" prop="remark" min-width="160">
        <template #default="scope">
          {{ scope.row.remark == null ? $t('device.device-log.798283-17') : scope.row.remark }}
        </template>
      </el-table-column>
    </el-table>
    <div style="height: 60px">
      <pagination
        v-show="total > 0"
        :total="total"
        v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize"
        @pagination="getList"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, watch, getCurrentInstance, onMounted } from 'vue';
import { Search, Refresh } from '@element-plus/icons-vue';
import { listEventLog } from '@/api/iot/eventLog';
import { useDict } from '@/utils/dict/useDict';
import { addDateRange } from '@/utils/ruoyi';

const { proxy } = getCurrentInstance() as any;

const { iot_event_type, device_mode } = useDict('iot_event_type', 'iot_yes_no', 'device_mode');

const props = defineProps({
  device: {
    type: Object,
    default: null,
  },
});

const loading = ref(true);
const showSearch = ref(true);
const total = ref(0);
const deviceLogList = ref<any[]>([]);
const queryFormRef = ref<any>(null);
const daterangeTime = ref<any[]>([]);
const thingsModel = ref<any>({});
const deviceInfo = ref<any>({});

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  logType: null as any,
  logValue: null as any,
  deviceId: null as any,
  serialNumber: null as any,
  deviceName: null as any,
  identify: null as any,
  isMonitor: null as any,
});

watch(
  () => props.device,
  (newVal) => {
    deviceInfo.value = newVal;
    if (newVal && newVal.deviceId != 0) {
      queryParams.serialNumber = newVal.serialNumber;
      getList();
      thingsModel.value = newVal.cacheThingsModel;
    }
  }
);

onMounted(() => {
  deviceInfo.value = props.device;
  if (props.device && props.device.deviceId != 0) {
    queryParams.serialNumber = props.device.serialNumber;
    getList();
    thingsModel.value = props.device.cacheThingsModel;
  }
});

function getList() {
  loading.value = true;
  listEventLog(addDateRange(queryParams, daterangeTime.value)).then((response: any) => {
    deviceLogList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}

function handleQuery() {
  queryParams.pageNum = 1;
  getList();
}

function resetQuery() {
  proxy?.resetForm(queryFormRef.value);
  daterangeTime.value = [];
  handleQuery();
}

/** 导出按钮操作 */
function handleExport() {
  proxy?.download('iot/event/export', { ...queryParams }, `eventLog_${new Date().getTime()}.xlsx`);
}

function formatValueDisplay(row: any) {
  if (row.logType == 1) {
    let propertyItem = getThingsModelItem(1, row.identify);
    if (propertyItem != '') {
      return (
        (propertyItem.parentName
          ? '[' + propertyItem.parentName + (propertyItem.arrayIndex ? propertyItem.arrayIndex : '') + '] '
          : '') +
        propertyItem.name +
        '： <span style="color:#486FF2;">' +
        getThingsModelItemValue(propertyItem, row.logValue) +
        ' ' +
        (propertyItem.datatype.unit != undefined ? propertyItem.datatype.unit : '') +
        '</span>'
      );
    }
  } else if (row.logType == 2) {
    let functionItem = getThingsModelItem(2, row.identify);
    if (functionItem != '') {
      return (
        (functionItem.parentName
          ? '[' + functionItem.parentName + (functionItem.arrayIndex ? functionItem.arrayIndex : '') + '] '
          : '') +
        functionItem.name +
        '： <span style="color:#486FF2">' +
        getThingsModelItemValue(functionItem, row.logValue) +
        ' ' +
        (functionItem.datatype.unit != undefined ? functionItem.datatype.unit : '') +
        '</span>'
      );
    }
  } else if (row.logType == 3) {
    let eventItem = getThingsModelItem(3, row.identify);
    if (eventItem != '') {
      return (
        (eventItem.parentName
          ? '[' + eventItem.parentName + (eventItem.arrayIndex ? eventItem.arrayIndex : '') + '] '
          : '') +
        eventItem.name +
        '： <span style="color:#486FF2">' +
        getThingsModelItemValue(eventItem, row.logValue) +
        ' ' +
        (eventItem.datatype.unit != undefined ? eventItem.datatype.unit : '') +
        '</span>'
      );
    } else {
      return row.logValue;
    }
  } else if (row.logType == 4) {
    return '<span>设备升级</span>';
  } else if (row.logType == 5) {
    return '<span>设备上线</span>';
  } else if (row.logType == 6) {
    return '<span>设备离线</span>';
  }
  return '';
}

function getThingsModelItemValue(item: any, oldValue: any) {
  if (item.datatype.type == 'bool') {
    if (oldValue == '0') {
      return item.datatype.falseText;
    } else if (oldValue == '1') {
      return item.datatype.trueText;
    }
  } else if (item.datatype.type == 'enum') {
    for (let i = 0; i < item.datatype.enumList.length; i++) {
      if (oldValue == item.datatype.enumList[i].value) {
        return item.datatype.enumList[i].text;
      }
    }
  }
  return oldValue;
}

function getThingsModelItem(type: number, identify: string): any {
  if (type == 1 && thingsModel.value.properties) {
    for (let i = 0; i < thingsModel.value.properties.length; i++) {
      if (thingsModel.value.properties[i].id == identify) {
        return thingsModel.value.properties[i];
      }
      if (thingsModel.value.properties[i].datatype.type == 'object') {
        for (let j = 0; j < thingsModel.value.properties[i].datatype.params.length; j++) {
          if (thingsModel.value.properties[i].datatype.params[j].id == identify) {
            thingsModel.value.properties[i].datatype.params[j].parentName = thingsModel.value.properties[i].name;
            return thingsModel.value.properties[i].datatype.params[j];
          }
        }
      }
      if (
        thingsModel.value.properties[i].datatype.type == 'array' &&
        thingsModel.value.properties[i].datatype.arrayType
      ) {
        if (thingsModel.value.properties[i].datatype.arrayType == 'object') {
          let realIdentity = identify;
          let arrayIndex: any = 0;
          if (identify.indexOf('array_') > -1) {
            arrayIndex = identify.substring(6, 8);
            realIdentity = identify.substring(9);
          }
          for (let j = 0; j < thingsModel.value.properties[i].datatype.params.length; j++) {
            if (thingsModel.value.properties[i].datatype.params[j].id == realIdentity) {
              thingsModel.value.properties[i].datatype.params[j].arrayIndex = Number(arrayIndex) + 1;
              thingsModel.value.properties[i].datatype.params[j].parentName = thingsModel.value.properties[i].name;
              return thingsModel.value.properties[i].datatype.params[j];
            }
          }
        } else {
          let realIdentity = identify;
          let arrayIndex: any = 0;
          if (identify.indexOf('array_') > -1) {
            arrayIndex = identify.substring(6, 8);
            realIdentity = identify.substring(9);
          }
          if (thingsModel.value.properties[i].datatype.arrayCount) {
            for (let j = 0; j < thingsModel.value.properties[i].datatype.arrayCount.length; j++) {
              if (thingsModel.value.properties[i].id == realIdentity) {
                thingsModel.value.properties[i].arrayIndex = Number(arrayIndex) + 1;
                thingsModel.value.properties[i].parentName = proxy?.$t('device.device-log.798283-21');
                return thingsModel.value.properties[i];
              }
            }
          }
        }
      }
    }
  } else if (type == 2 && thingsModel.value.functions) {
    for (let i = 0; i < thingsModel.value.functions.length; i++) {
      if (thingsModel.value.functions[i].id == identify) {
        return thingsModel.value.functions[i];
      }
      if (thingsModel.value.functions[i].datatype.type == 'object') {
        for (let j = 0; j < thingsModel.value.functions[i].datatype.params.length; j++) {
          if (thingsModel.value.functions[i].datatype.params[j].id == identify) {
            thingsModel.value.functions[i].datatype.params[j].parentName = thingsModel.value.functions[i].name;
            return thingsModel.value.functions[i].datatype.params[j];
          }
        }
      }
      if (
        thingsModel.value.functions[i].datatype.type == 'array' &&
        thingsModel.value.functions[i].datatype.arrayType
      ) {
        let realIdentity = identify;
        let arrayIndex: any = 0;
        if (identify.indexOf('array_') > -1) {
          arrayIndex = identify.substring(6, 8);
          realIdentity = identify.substring(9);
        }
        if (thingsModel.value.functions[i].datatype.arrayType == 'object') {
          for (let j = 0; j < thingsModel.value.functions[i].datatype.params.length; j++) {
            if (thingsModel.value.functions[i].datatype.params[j].id == realIdentity) {
              thingsModel.value.functions[i].datatype.params[j].arrayIndex = Number(arrayIndex) + 1;
              thingsModel.value.functions[i].datatype.params[j].parentName = thingsModel.value.functions[i].name;
              return thingsModel.value.functions[i].datatype.params[j];
            }
          }
        } else {
          if (thingsModel.value.functions[i].datatype.arrayCount) {
            for (let j = 0; j < thingsModel.value.functions[i].datatype.arrayCount.length; j++) {
              if (thingsModel.value.functions[i].id == realIdentity) {
                thingsModel.value.functions[i].arrayIndex = Number(arrayIndex) + 1;
                thingsModel.value.functions[i].parentName = proxy?.$t('device.device-log.798283-21');
                return thingsModel.value.functions[i];
              }
            }
          }
        }
      }
    }
  } else if (type == 3 && thingsModel.value.events) {
    for (let i = 0; i < thingsModel.value.events.length; i++) {
      if (thingsModel.value.events[i].id == identify) {
        return thingsModel.value.events[i];
      }
    }
  }
  return '';
}
</script>
