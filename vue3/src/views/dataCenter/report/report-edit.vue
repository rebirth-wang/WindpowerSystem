<template>
  <div class="edit-form">
    <div class="form">
      <div class="title">{{ reportName }}</div>

      <el-form ref="formRef" :rules="rules" :model="form" label-width="100px">
        <div class="base">
          <el-form-item :label="$t('dataCenter.report.451245-1')" prop="name">
            <el-input v-model="form.name" :placeholder="$t('dataCenter.report.451245-11')" style="width: 375px" />
          </el-form-item>

          <el-form-item :label="$t('product.product-select-template.318012-12')" prop="dataType">
            <el-select
              v-model="form.dataType"
              :placeholder="$t('product.product-things-model.142341-37')"
              style="width: 375px"
              clearable
            >
              <el-option
                v-for="dict in report_data_type"
                :key="dict.value"
                :label="dict.label"
                :value="Number(dict.value)"
              />
            </el-select>
          </el-form-item>

          <el-form-item :label="$t('dataCenter.report.451245-31')" prop="notifyUsers">
            <el-select
              v-model="notifyUsers"
              :placeholder="$t('dataCenter.report.451245-32')"
              style="width: 375px"
              multiple
              @change="handleNotifyUsers"
              clearable
            >
              <el-option
                v-for="user in userList"
                :key="user.userId"
                :value="user.userId"
                :label="user.userName"
              ></el-option>
            </el-select>
          </el-form-item>
        </div>
        <div class="time-cycle">
          <span class="label">{{ $t('device.device-modbus-task.384302-19') }}</span>
          <span class="tip">{{ $t('dataCenter.report.451245-12') }}</span>
          <div class="form">
            <el-form-item :label="$t('device.device-modbus-task.384302-19')" prop="cycleType">
              <el-radio-group v-model="form.cycleType">
                <el-radio v-for="dict in report_time_period" :value="Number(dict.value)" :key="dict.value">
                  {{ dict.label }}
                </el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item v-if="form.cycleType === 3" :label="$t('device.device-modbus-task.384302-46')" prop="cycle">
              <el-date-picker
                v-model="time"
                type="datetimerange"
                :range-separator="$t('dataCenter.report.451245-33')"
                :start-placeholder="$t('dataCenter.report.451245-34')"
                :end-placeholder="$t('dataCenter.report.451245-35')"
                value-format="YYYY-MM-DD HH:mm:ss"
                style="width: 105px"
              ></el-date-picker>
            </el-form-item>
            <el-form-item v-if="form.cycleType === 1" :label="$t('dataCenter.report.451245-13')" prop="cycle">
              <el-select style="width: 375px" v-model="cycles1[0].type" @change="handleCycleInterval">
                <el-option
                  v-for="dict in report_generation_cycle"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                ></el-option>
              </el-select>
            </el-form-item>
            <el-form-item
              v-if="form.cycleType === 1 && cycles1[0].type !== 'day'"
              :label="$t('dataCenter.report.451245-15')"
              prop="cycle"
            >
              <el-select v-if="cycles1[0].type === 'week'" style="width: 375px" v-model="cycles1[0].week">
                <el-option
                  v-for="dict in variable_operation_week"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                ></el-option>
              </el-select>
              <el-select v-if="cycles1[0].type === 'month'" style="width: 375px" v-model="cycles1[0].day">
                <el-option
                  v-for="dict in variable_operation_day"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                ></el-option>
              </el-select>
            </el-form-item>
            <el-form-item v-if="form.cycleType === 1" :label="$t('device.device-modbus-task.384302-46')">
              <el-select
                v-if="cycles1[0].type === 'day' || cycles1[0].type === 'week' || cycles1[0].type === 'month'"
                style="width: 375px"
                v-model="cycles1[0].time"
              >
                <el-option
                  v-for="dict in variable_operation_time"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                ></el-option>
              </el-select>
            </el-form-item>
          </div>
        </div>
        <div class="report-rule">
          <span class="label">{{ $t('dataCenter.report.451245-16') }}</span>
          <span class="tip">{{ $t('dataCenter.report.451245-17') }}</span>
          <div class="form">
            <el-form-item v-if="form.dataType === 2" :label="$t('dataCenter.report.451245-18')" prop="aggregateUnits">
              <el-select
                v-model="form.aggregateUnits"
                :placeholder="$t('dataCenter.report.451245-19')"
                style="width: 375px"
              >
                <el-option
                  v-for="dict in report_aggregation_unit"
                  :key="dict.value"
                  :value="dict.value"
                  :label="dict.label"
                  :disabled="cycles1[0].type !== 'month' && dict.value == '4'"
                ></el-option>
              </el-select>
            </el-form-item>
            <div class="top">
              <div>
                <el-form-item :label="$t('dataCenter.report.451245-6')" prop="dataDimension">
                  <el-radio-group v-model="form.dataDimension" @change="handleDimensionChange">
                    <el-radio v-for="dict in data_dimension" :key="dict.value" :value="Number(dict.value)">
                      {{ dict.label }}
                    </el-radio>
                  </el-radio-group>
                </el-form-item>
              </div>
              <div>
                <el-button size="small" type="primary" @click="handleAddList">
                  {{ $t('device.instruction-config.984980-5') }}
                </el-button>
              </div>
            </div>

            <div class="list" v-for="(item, index) in list" :key="index">
              <div class="Dimension">
                <div v-if="form.dataDimension === 1">
                  <el-form-item :label="$t('dataCenter.history.384934-0')" prop="device">
                    <el-select
                      size="small"
                      style="width: 255px"
                      v-model="item.cusSourceId"
                      :placeholder="$t('scene.edit.202832-15')"
                      filterable
                      @change="handleUpdateDeviceItem(item, $event)"
                    >
                      <el-option
                        v-for="(dItem, dIndex) in deviceList"
                        :key="dIndex"
                        :label="dItem.deviceName"
                        :value="dItem.deviceId"
                      ></el-option>
                    </el-select>
                  </el-form-item>
                </div>
                <div class="scene" v-if="form.dataDimension === 2">
                  <el-form-item :label="$t('dataCenter.history.384934-14')" prop="scene">
                    <el-select
                      style="width: 255px"
                      v-model="item.cusSourceId"
                      :placeholder="$t('sip.index.998533-25')"
                      filterable
                      @change="handleUpdateSceneItem(item, $event)"
                    >
                      <el-option
                        v-for="(sItem, sIndex) in sceneList"
                        :key="sIndex"
                        :label="sItem.sceneModelName"
                        :value="sItem.sceneModelId"
                      ></el-option>
                    </el-select>
                  </el-form-item>
                  <el-form-item v-if="item.cusSourceId" :label="$t('dataCenter.history.384934-0')" prop="sceneDevice">
                    <el-select
                      style="width: 255px"
                      v-model="item.sceneModelDevice"
                      :placeholder="$t('scene.edit.202832-15')"
                      filterable
                      value-key="id"
                      @change="handleUpdateModelDeviceItem(item, $event)"
                    >
                      <el-option
                        v-for="param in modelDeviceList"
                        :key="param.id"
                        :label="param.name"
                        :value="param"
                      ></el-option>
                    </el-select>
                  </el-form-item>
                </div>

                <div>
                  <el-button v-if="index !== 0" type="danger" size="small" @click="handleDeleteList(index)">
                    {{ $t('del') }}
                  </el-button>
                </div>
              </div>

              <el-form-item :label="$t('scene.edit.202832-66')" prop="model">
                <div class="var-wrap" v-if="form.dataDimension === 1">
                  <el-switch
                    class="switch"
                    :active-text="$t('dataCenter.report.451245-36')"
                    v-if="item.thingsModelList"
                    v-model="item.checkAll"
                    @change="handleCheckAllChange(item, index)"
                  />
                  <div class="multi">
                    <div :class="form.dataType === 2 ? 'checkBox-selected' : 'checkBox-noselected'">
                      <div
                        class="checkBox-input"
                        v-for="(param, paramIndex) in item.thingsModelList"
                        :key="param.modelId"
                      >
                        <input type="checkbox" v-model="param.checked" @change="handleCheckedChange(item, param)" />
                        <span>{{ param.modelName }}</span>
                        <el-select
                          v-if="form.dataType === 2"
                          style="width: 85px; margin-left: 5px"
                          size="small"
                          v-model="list[index].thingsModelList[paramIndex].operation"
                          :placeholder="$t('scene.edit.202832-69')"
                          @change="handleUpdateoperationItem(item, param, $event)"
                        >
                          <el-option
                            v-for="dict in report_rule_data_operation"
                            :key="dict.value"
                            :label="dict.label"
                            :value="parseInt(dict.value)"
                          ></el-option>
                        </el-select>
                      </div>
                    </div>
                  </div>
                </div>
                <div class="var-wrap" v-if="form.dataDimension === 2">
                  <el-switch
                    class="switch"
                    :active-text="$t('dataCenter.report.451245-36')"
                    v-if="item.variableList"
                    v-model="item.checkAll"
                    @change="handleCheckAllChange(item, index)"
                  />
                  <div class="multi">
                    <div :class="form.dataType === 2 ? 'checkBox-selected' : 'checkBox-noselected'">
                      <div class="checkBox-input" v-for="(param, paramIndex) in item.variableList" :key="param.id">
                        <input type="checkbox" v-model="param.checked" @change="handleCheckedChange(item, param)" />
                        <span>{{ param.sourceName }}</span>
                        <el-select
                          v-if="form.dataType === 2"
                          size="small"
                          style="width: 85px"
                          v-model="list[index].variableList[paramIndex].operation"
                          :placeholder="$t('scene.edit.202832-69')"
                          @change="handleUpdateoperationItem(item, param, $event)"
                        >
                          <el-option
                            v-for="dict in report_rule_data_operation"
                            :key="dict.value"
                            :label="dict.label"
                            :value="parseInt(dict.value)"
                          ></el-option>
                        </el-select>
                      </div>
                    </div>
                  </div>
                </div>
              </el-form-item>
            </div>
          </div>
        </div>
        <div class="report-structure">
          <span class="label">{{ $t('dataCenter.report.451245-29') }}</span>
          <span class="tip">{{ $t('dataCenter.report.451245-20') }}</span>
          <div class="form">
            <div class="format">
              <div
                v-if="list.length > 0 && list[0].reportRuleDataVOList && list[0].reportRuleDataVOList.length > 0"
                class="table"
              >
                <div v-if="form.dataType === 1">
                  <el-table :data="tableData">
                    <el-table-column
                      prop="data"
                      :label="$t('device.device-modbus-task.384302-46')"
                      width="100"
                    ></el-table-column>
                    <template v-for="(item, index) in list" :key="index">
                      <el-table-column
                        v-if="item.deviceName && form.dataDimension === 1"
                        min-width="180"
                        :label="item.deviceName"
                      >
                        <template v-for="(param, paramIndex) in item.reportRuleDataVOList" :key="paramIndex">
                          <el-table-column
                            v-if="param.modelName"
                            :label="param.modelName"
                            :show-overflow-tooltip="true"
                            prop="data"
                            min-width="180"
                          ></el-table-column>
                        </template>
                      </el-table-column>
                      <el-table-column
                        v-if="item.sceneName && form.dataDimension === 2"
                        min-width="180"
                        :label="item.sceneName"
                      >
                        <el-table-column v-if="item.deviceName && form.dataDimension === 2" :label="item.deviceName">
                          <template v-for="(param, paramIndex) in item.reportRuleDataVOList" :key="paramIndex">
                            <el-table-column
                              v-if="param.sourceName"
                              :label="param.sourceName"
                              :show-overflow-tooltip="true"
                              prop="data"
                              min-width="180"
                            ></el-table-column>
                          </template>
                        </el-table-column>
                      </el-table-column>
                    </template>
                  </el-table>
                </div>
                <div v-if="form.dataType === 2">
                  <table class="table">
                    <tr v-if="form.dataDimension === 2">
                      <th class="table-header">{{ $t('dataCenter.history.384934-14') }}</th>
                      <template v-for="(item, index) in list" :key="'s' + index">
                        <template
                          v-for="(param, paramIndex) in item.reportRuleDataVOList"
                          :key="`${index}_${paramIndex}`"
                        >
                          <td class="table-row">{{ item.sceneName }}</td>
                        </template>
                      </template>
                    </tr>
                    <tr>
                      <th class="table-header">{{ $t('dataCenter.history.384934-0') }}</th>
                      <template v-for="(item, index) in list" :key="'d' + index">
                        <template
                          v-for="(param, paramIndex) in item.reportRuleDataVOList"
                          :key="`${index}_${paramIndex}`"
                        >
                          <td class="table-row">{{ item.deviceName }}</td>
                        </template>
                      </template>
                    </tr>
                    <tr>
                      <th class="table-header">{{ $t('scene.edit.202832-66') }}</th>
                      <template v-for="(item, index) in list" :key="'v' + index">
                        <template
                          v-for="(param, paramIndex) in item.reportRuleDataVOList"
                          :key="`${index}_${paramIndex}`"
                        >
                          <td class="table-row">
                            <div class="operation">
                              <span v-if="form.dataDimension === 1">{{ param.modelName }}</span>
                              <span v-if="form.dataDimension === 2">{{ param.sourceName }}</span>
                              (
                              <dict-tag :options="report_rule_data_operation" :value="param.operation" size="small" />
                              )
                            </div>
                          </td>
                        </template>
                      </template>
                    </tr>
                    <tr>
                      <th class="table-header">{{ $t('device.device-modbus-task.384302-46') }}</th>
                      <template v-for="(item, index) in list" :key="'t' + index">
                        <template
                          v-for="(param, paramIndex) in item.reportRuleDataVOList"
                          :key="`${index}_${paramIndex}`"
                        >
                          <td class="table-row">--</td>
                        </template>
                      </template>
                    </tr>
                  </table>
                </div>
              </div>
              <el-empty v-else :image-size="120"></el-empty>
            </div>
          </div>
        </div>
      </el-form>
    </div>
    <div class="footer">
      <el-button type="primary" @click="saveForm" v-hasPermi="['dataCenter:report:add', 'dataCenter:report:edit']">
        {{ $t('save') }}
      </el-button>
      <el-button @click="cancel">{{ $t('cancel') }}</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, getCurrentInstance } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useI18n } from 'vue-i18n';
import { useDict } from '@/utils/dict/useDict';
import { listDeviceShort, listThingsModel } from '@/api/iot/device';
import { getSceneModelList, getSceneModelDeviceList, getSceneModelDataListByType } from '@/api/scene/list.js';
import { getReport, addReport, updateReport } from '@/api/data/report';
import { listUser } from '@/api/system/user';

const { proxy } = getCurrentInstance() as any;
const { t } = useI18n();
const route = useRoute();
const router = useRouter();

const {
  variable_operation_interval,
  variable_operation_time,
  variable_operation_week,
  variable_operation_day,
  report_rule_data_operation,
  report_aggregation_unit,
  report_data_type,
  report_time_period,
  report_generation_cycle,
  data_dimension,
} = useDict(
  'variable_operation_interval',
  'variable_operation_time',
  'variable_operation_week',
  'variable_operation_day',
  'report_rule_data_operation',
  'report_aggregation_unit',
  'report_data_type',
  'report_time_period',
  'report_generation_cycle',
  'data_dimension'
);

const formRef = ref();
const reportName = ref(t('dataCenter.report.451245-21'));
const edit = ref(false);
const form = reactive<any>({
  dataDimension: 1,
  dataType: 1,
  reportRuleVOList: [],
  notifyUsers: '',
});
const notifyUsers = ref<number[]>([]);
const userList = ref<any[]>([]);
const time = ref<any[]>([]);
const cycles1 = reactive<any[]>([{ type: 'week', time: '', week: '', day: '' }]);
const list = ref<any[]>([
  {
    reportRuleDataVOList: [],
    cusSourceId: '',
    sceneModelDeviceId: '',
    checkAll: true,
  },
]);
const deviceList = ref<any[]>([]);
const sceneList = ref<any[]>([]);
const modelDeviceList = ref<any[]>([]);
const rules = reactive<any>({
  name: [{ required: true, message: t('dataCenter.report.451245-22'), trigger: 'blur' }],
  dataType: [{ required: true, message: t('product.product-things-model.142341-98'), trigger: 'change' }],
  cycleType: [{ required: true, message: t('dataCenter.report.451245-24'), trigger: 'change' }],
  dataDimension: [{ required: true, message: t('dataCenter.report.451245-23'), trigger: 'blur' }],
  aggregateUnits: [{ required: true, message: t('dataCenter.report.451245-25'), trigger: 'blur' }],
});
const tableData = ref([{ data: '--' }]);

/** 获取设备列表 */
function getDeviceList() {
  const params = { showChild: false, pageNum: 1, pageSize: 9999 };
  listDeviceShort(params).then((res: any) => {
    if (res.code === 200) {
      deviceList.value = res.rows;
    } else {
      proxy.$modal.msgError(res.msg);
    }
  });
}

/** 获取场景列表数据 */
function getSceneListDatas() {
  const params = { pageNum: 1, pageSize: 9999 };
  getSceneModelList(params).then((res: any) => {
    if (res.code === 200) {
      sceneList.value = res.rows;
    } else {
      proxy.$modal.msgError(res.msg);
    }
  });
}

/** 获取用户列表 */
function getUserList() {
  listUser().then((res: any) => {
    if (res.code === 200) {
      userList.value = res.rows;
    }
  });
}

/** 获取物模型列表 */
async function getThingsModels(id: any) {
  const params = { deviceId: id, pageSize: 9999, pageNum: 1 };
  const res = await listThingsModel(params);
  return res;
}

/** 获取场景设备 */
async function getSceneModelDevice(val: any) {
  const params = { sceneModelId: Number(val), pageNum: 1, pageSize: 9999 };
  const res = await getSceneModelDeviceList(params);
  return res;
}

/** 获取场景变量 */
async function getSceneModelDataListFn(item: any, val: any) {
  const params = {
    sceneModelId: item.cusSourceId,
    variableType: val.variableType,
    sceneModelDeviceId: Number(val.id),
    sourceName: '',
    pageNum: 1,
    pageSize: 9999,
  };
  const res = await getSceneModelDataListByType(params);
  return res;
}

/** 获取详细信息 */
function getReportDetail(id: any) {
  getReport(id).then((res: any) => {
    Object.assign(form, res.data);
    notifyUsers.value = form.notifyUsers ? form.notifyUsers.split(',').map(Number) : [];
    if (form.cycleType === 1) {
      const cycle = JSON.parse(form.cycle);
      cycles1.splice(0, cycles1.length, ...cycle);
    }
    if (form.cycleType === 3) {
      const cycle = JSON.parse(form.cycle);
      time.value = [cycle[0].beginTime, cycle[0].endTime];
    }
    list.value = form.reportRuleVOList;
    if (form.dataDimension === 1) {
      list.value.forEach(async (item: any) => {
        const res: any = await getThingsModels(item.cusSourceId);
        let map = new Map();
        item.reportRuleDataVOList.forEach((param: any) => {
          map.set(param.cusDataId, param.operation);
        });
        item.thingsModelList = res.rows.map((row: any) => ({
          ...row,
          operation: map.has(row.modelId) ? map.get(row.modelId) : 1,
          checked: map.has(row.modelId),
        }));
        const modelMap = new Map();
        item.thingsModelList.forEach((param: any) => {
          modelMap.set(param.modelId, param.modelName);
        });
        item.reportRuleDataVOList.forEach((row: any) => {
          row.modelName = modelMap.get(row.cusDataId);
        });
        if (item.thingsModelList && item.thingsModelList.length > 0) {
          item.deviceName = item.thingsModelList[0].deviceName;
          item.checkAll =
            item.reportRuleDataVOList.length === item.thingsModelList.length && item.thingsModelList.length > 0;
        }
      });
    }
    if (form.dataDimension === 2) {
      list.value.forEach(async (item: any) => {
        const deviceRes: any = await getSceneModelDevice(item.cusSourceId);
        modelDeviceList.value = deviceRes.rows;
        item.sceneModelDevice = modelDeviceList.value.find((e: any) => e.id === item.sceneModelDeviceId);
        const selected = sceneList.value.find((s: any) => s.sceneModelId === item.cusSourceId);
        item.sceneName = selected?.sceneModelName;
        const res: any = await getSceneModelDataListFn(item, item.sceneModelDevice);
        let map = new Map();
        item.reportRuleDataVOList.forEach((param: any) => {
          map.set(param.cusDataId, param.operation);
        });
        item.variableList = res.rows.map((row: any) => ({
          ...row,
          operation: map.has(row.id) ? map.get(row.id) : 1,
          checked: map.has(row.id),
        }));
        const modelMap = new Map();
        item.variableList.forEach((param: any) => {
          modelMap.set(param.id, param.sourceName);
        });
        item.reportRuleDataVOList.forEach((row: any) => {
          row.sourceName = modelMap.get(row.cusDataId);
        });
        if (item.variableList && item.variableList.length > 0) {
          item.deviceName = item.variableList[0].sceneModelDeviceName;
          item.checkAll = item.reportRuleDataVOList.length === item.variableList.length && item.variableList.length > 0;
        }
      });
    }
  });
}

/** 时间周期-周期循环 */
function handleCycleInterval(val: string) {
  if (val === 'hour') {
    cycles1.splice(0, 1, { type: val, time: '', week: '', day: '' });
  } else if (val === 'day') {
    cycles1.splice(0, 1, { type: val, time: '01', week: '', day: '' });
  } else if (val === 'week') {
    cycles1.splice(0, 1, { type: val, time: '01', week: '1', day: '' });
  } else if (val === 'month') {
    cycles1.splice(0, 1, { type: val, time: '01', week: '', day: '1' });
  } else {
    cycles1.splice(0, 1, { type: val, time: '', week: '', day: '' });
  }
}

/** 修改数据维度 */
function handleDimensionChange(val: number) {
  proxy.$modal
    .confirm(t('dataCenter.report.451245-28'))
    .then(() => {
      resetList();
    })
    .catch(() => {
      form.dataDimension = val === 1 ? 2 : 1;
    });
}

/** 重置列表 */
function resetList() {
  list.value = [
    {
      reportRuleDataVOList: [],
      cusSourceId: '',
      sceneModelDeviceId: '',
      checkAll: true,
    },
  ];
}

/** 增加列表 */
function handleAddList() {
  list.value.push({
    reportRuleDataVOList: [],
    cusSourceId: '',
    sceneModelDeviceId: '',
    checkAll: true,
  });
}

/** 删除列表 */
function handleDeleteList(index: number) {
  list.value.splice(index, 1);
}

/** 选择设备 */
async function handleUpdateDeviceItem(item: any, val: any) {
  const res: any = await getThingsModels(val);
  item.thingsModelList = res.rows.map((row: any) => ({
    ...row,
    operation: 1,
    checked: true,
  }));
  item.reportRuleDataVOList = [];
  item.thingsModelList.forEach((element: any) => {
    item.reportRuleDataVOList.push({
      cusDataId: element.modelId,
      operation: element.operation,
      modelName: element.modelName,
    });
  });
  item.deviceName = res?.rows?.[0]?.deviceName || null;
  item.checkAll = true;
}

/** 改变场景选择 */
async function handleUpdateSceneItem(item: any, val: any) {
  item.sceneModelDeviceId = '';
  item.sceneModelDevice = null;
  item.variableList = [];
  item.reportRuleDataVOList = [];
  const res: any = await getSceneModelDevice(val);
  modelDeviceList.value = res.rows;
  const selected = sceneList.value.find((s: any) => s.sceneModelId === val);
  item.sceneName = selected?.sceneModelName;
}

/** 改变场景关联设备选择 */
async function handleUpdateModelDeviceItem(item: any, val: any) {
  const res: any = await getSceneModelDataListFn(item, val);
  item.sceneModelDeviceId = val.id;
  item.reportRuleDataVOList = [];
  item.variableList = res.rows.map((row: any) => ({
    ...row,
    operation: 1,
    checked: true,
  }));
  if (item.variableList.length > 0) {
    item.variableList.forEach((element: any) => {
      item.reportRuleDataVOList.push({
        cusDataId: element.id,
        operation: element.operation,
        sourceName: element.sourceName,
      });
    });
    item.deviceName = res?.rows?.[0]?.sceneModelDeviceName || null;
    item.checkAll = true;
  } else {
    item.checkAll = false;
  }
}

/** 控制全选 */
function handleCheckAllChange(item: any, index: number) {
  let selectList: any[] = [];
  if (form.dataDimension === 1) {
    item.thingsModelList.forEach((element: any) => {
      element.checked = item.checkAll;
      selectList.push({
        cusDataId: element.modelId,
        operation: element.operation,
        modelName: element.modelName,
      });
    });
    item.reportRuleDataVOList = item.checkAll ? selectList : [];
  }
  if (form.dataDimension === 2) {
    item.variableList.forEach((element: any) => {
      element.checked = item.checkAll;
      selectList.push({
        cusDataId: element.id,
        operation: element.operation,
        sourceName: element.sourceName,
      });
    });
    item.reportRuleDataVOList = item.checkAll ? selectList : [];
  }
}

/** 改变数据 */
function handleCheckedChange(item: any, param: any) {
  switch (form.dataDimension) {
    case 1:
      if (param.checked) {
        item.reportRuleDataVOList.push({
          cusDataId: param.modelId,
          operation: param.operation,
          modelName: param.modelName,
        });
      } else {
        item.reportRuleDataVOList = item.reportRuleDataVOList.filter((i: any) => i.cusDataId != param.modelId);
      }
      item.checkAll =
        item.reportRuleDataVOList.length === item.thingsModelList.length && item.thingsModelList.length > 0;
      break;
    case 2:
      if (param.checked) {
        item.reportRuleDataVOList.push({
          cusDataId: param.id,
          operation: param.operation,
          sourceName: param.sourceName,
        });
      } else {
        item.reportRuleDataVOList = item.reportRuleDataVOList.filter((i: any) => i.cusDataId != param.id);
      }
      item.checkAll = item.reportRuleDataVOList.length === item.variableList.length && item.variableList.length > 0;
      break;
  }
}

/** 改变数据操作 */
function handleUpdateoperationItem(item: any, param: any, val: any) {
  switch (form.dataDimension) {
    case 1:
      item.reportRuleDataVOList.forEach((element: any) => {
        if (element.cusDataId == param.modelId) {
          element.operation = val;
        }
      });
      break;
    case 2:
      item.reportRuleDataVOList.forEach((element: any) => {
        if (element.cusDataId == param.id) {
          element.operation = val;
        }
      });
      break;
  }
}

/** 选择通知用户 */
function handleNotifyUsers(val: number[]) {
  form.notifyUsers = val.join(',');
}

/** 保存 */
function saveForm() {
  formRef.value?.validate((valid: boolean) => {
    if (valid) {
      let hasEmptyModel = false;
      for (const item of list.value) {
        if (!item.reportRuleDataVOList || item.reportRuleDataVOList.length === 0) {
          hasEmptyModel = true;
          proxy.$modal.msgError(t('dataCenter.report.451245-37'));
          break;
        }
      }
      if (hasEmptyModel) return;

      form.reportRuleVOList = [];
      list.value.forEach((item: any) => {
        form.reportRuleVOList.push({
          reportRuleDataVOList: item.reportRuleDataVOList,
          cusSourceId: item.cusSourceId,
          sceneModelDeviceId: item.sceneModelDeviceId,
        });
      });

      const cycle = [{ beginTime: time.value[0], endTime: time.value[1] }];
      switch (form.cycleType) {
        case 3:
          form.cycle = JSON.stringify(cycle);
          break;
        case 1:
          form.cycle = JSON.stringify(cycles1);
          break;
      }
      if (edit.value) {
        form.notifyUsers = notifyUsers.value.join(',');
        updateReport(form).then((res: any) => {
          if (res.code === 200) {
            proxy.$modal.msgSuccess(t('views.iot.bridge.index.525282-93'));
            router.push({ path: '/dataCenter/report/list' });
          }
        });
      } else {
        addReport(form).then((res: any) => {
          if (res.code === 200) {
            proxy.$modal.msgSuccess(t('notify.template.index.333542-32'));
            router.push({ path: '/dataCenter/report/list' });
          }
        });
      }
    }
  });
}

/** 取消 */
function cancel() {
  router.push({ path: '/dataCenter/report/list' });
}

// 初始化
getDeviceList();
getSceneListDatas();
getUserList();

onMounted(() => {
  const id = route.query.id;
  if (id) {
    getReportDetail(id);
    reportName.value = t('dataCenter.report.451245-26');
    edit.value = true;
  }
});
</script>

<style lang="scss" scoped>
.edit-form {
  padding: 20px;
  .form {
    padding: 20px;
    background: #fff;
    .title {
      font-size: 16px;
      font-weight: 800;
      padding-bottom: 10px;
      border-bottom: 1px solid #ebeef5;
      margin-bottom: 20px;
    }
    .base {
      padding: 0 30px;
    }
    .time-cycle,
    .report-rule,
    .report-structure {
      .label {
        font-weight: 800;
      }
      .tip {
        color: #ff3b30;
        margin-left: 20px;
      }
      .form {
        margin-top: 20px;
        padding: 0 30px;
        .top {
          display: flex;
          justify-content: space-between;
        }
        .list {
          margin-left: 100px;
          margin-bottom: 20px;
          background-color: #f5f7fa;
          padding: 20px 20px 10px 20px;
          border-radius: 10px;
          .Dimension {
            display: flex;
            justify-content: space-between;
            .scene {
              display: flex;
            }
          }
          .var-wrap {
            display: flex;
            flex-direction: column;
            gap: 10px;
            width: 800px;
            .switch {
              margin-left: auto;
            }
            .multi {
              height: 140px;
              overflow: auto;
              background: #fff;
              border: 1px solid #dcdfe6;
              border-radius: 5px;
              padding: 10px;
              .checkBox-noselected {
                display: grid;
                grid-template-columns: repeat(3, 1fr);
                grid-gap: 10px;
              }
              .checkBox-selected {
                display: grid;
                grid-template-columns: repeat(2, 1fr);
                grid-gap: 10px;
              }
              .checkBox-input {
                display: flex;
                flex-shrink: 0;
                gap: 5px;
                min-width: 200px;
                cursor: pointer;
                input[type='checkbox'] {
                  accent-color: #486ff2;
                  cursor: pointer;
                }
              }
            }
          }
        }
        .format {
          margin-left: 100px;
          border-radius: 10px;
          border: 1px solid #dcdfe6;
          padding: 10px;
          .table {
            width: 100%;
            overflow: auto;
            table-layout: fixed;
            border-collapse: collapse;
            .table-header {
              width: 200px;
              min-width: 200px;
              border: 1px solid #dddee0;
              background: #f5f7fa;
              text-align: left;
              padding: 8px;
            }
            .table-row {
              width: 200px;
              min-width: 200px;
              padding: 8px;
              border: 1px solid #dddee0;
              text-align: left;
              .operation {
                display: flex;
                flex-wrap: wrap;
              }
            }
          }
          :deep(.el-table td.el-table__cell) {
            border-bottom: none;
          }
        }
      }
    }
  }
  .footer {
    text-align: center;
    padding: 20px 20px 0;
  }
}
</style>
