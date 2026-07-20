<template>
  <div style="padding: 6px">
    <el-row :gutter="5">
      <el-col :xs="24" :sm="12" :md="12" :lg="7" :xl="6">
        <el-card style="min-height: 800px; margin-bottom: 5px">
          <el-form :model="queryParams" ref="queryRef" :inline="true" style="margin-bottom: -10px">
            <el-form-item prop="deviceName">
              <el-input
                v-model="queryParams.deviceName"
                :placeholder="$t('simulate.index.111543-0')"
                clearable
                @keyup.enter="handleQuery"
                style="width: 150px"
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :icon="Search" @click="handleQuery">{{ $t('search') }}</el-button>
            </el-form-item>
          </el-form>
          <el-table v-loading="loading" :data="deviceList" @row-click="rowClick" highlight-current-row size="small">
            <el-table-column :label="$t('simulate.index.111543-1')" width="60" align="center">
              <template #default="scope">
                <input type="radio" :checked="scope.row.isSelect" />
              </template>
            </el-table-column>
            <el-table-column
              :label="$t('simulate.index.111543-2')"
              align="center"
              header-align="center"
              prop="deviceName,serialNumber"
            >
              <template #default="scope">
                {{ scope.row.deviceName }}
                <br />
                ({{ scope.row.serialNumber }})
              </template>
            </el-table-column>
            <el-table-column :label="$t('simulate.index.111543-3')" align="center" prop="subDeviceCount" width="60">
              <template #default="scope">{{ scope.row.subDeviceCount }}</template>
            </el-table-column>
            <el-table-column :label="$t('simulate.index.111543-4')" align="center" prop="status" width="140">
              <template #default="scope">
                <el-switch
                  v-model="scope.row.status"
                  :active-value="3"
                  :inactive-value="4"
                  @change="handleStatusChange(scope.row)"
                  :disabled="!editPermission"
                ></el-switch>
              </template>
            </el-table-column>
          </el-table>
          <pagination
            v-show="total > 0"
            small
            style="margin: 0 0 10px"
            layout="total,prev, pager, next"
            :total="total"
            v-model:page="queryParams.pageNum"
            v-model:limit="queryParams.pageSize"
            @pagination="getList"
          />
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="12" :lg="9" :xl="8">
        <el-card style="min-height: 800px; margin-bottom: 5px; padding: 0 10px">
          <div class="phone">
            <div class="phone-container">
              <div class="phone-title">{{ $t('simulate.index.111543-5') }}</div>
              <div class="messageContent" ref="messageContentRef">
                <div v-for="(item, index) in messageList" :key="index">
                  <div :class="item.direction == 'receive' ? 'messageReceive' : 'messageSend'">
                    <div style="display: flex; align-items: center; justify-content: space-between">
                      <div style="width: 190px">
                        <span style="font-weight: 600; line-height: 24px">{{ $t('simulate.index.111543-6') }}</span>
                        {{ item.ts }}
                      </div>
                      <div style="width: 50px">Qos: {{ item.qos }}</div>
                    </div>
                    <div>
                      <span style="font-weight: 600; line-height: 24px">{{ $t('simulate.index.111543-7') }}</span>
                      {{ item.topic }}
                    </div>
                    <div>
                      <span style="font-weight: 600; line-height: 24px">{{ $t('simulate.index.111543-8') }}</span>
                      {{ item.data }}
                    </div>
                  </div>
                </div>
                <div style="height: 200px; display: flex; width: 100%"></div>
              </div>
              <div class="messageBottom">
                <el-form :inline="true" size="small">
                  <el-form-item style="width: 100%" class="adaptWidth">
                    <el-row :gutter="10">
                      <el-col :span="8">
                        <el-select
                          v-model="simulateForm.payloadType"
                          :placeholder="$t('simulate.index.111543-9')"
                          style="width: 100%"
                          @change="payloadTypeChange"
                        >
                          <el-option label="JSON" value="json"></el-option>
                          <el-option label="Hex" value="hex"></el-option>
                          <el-option label="Base64" value="base64"></el-option>
                          <el-option label="Plaintext" value="plaintext"></el-option>
                        </el-select>
                      </el-col>
                      <el-col :span="8">
                        <el-select
                          v-model="simulateForm.qos"
                          :placeholder="$t('simulate.index.111543-10')"
                          style="width: 100%"
                        >
                          <el-option :label="$t('simulate.index.111543-11')" value="0"></el-option>
                          <el-option :label="$t('simulate.index.111543-12')" value="1"></el-option>
                          <el-option :label="$t('simulate.index.111543-13')" value="2"></el-option>
                        </el-select>
                      </el-col>
                    </el-row>
                  </el-form-item>
                  <el-form-item style="margin-top: -10px; width: 100%" class="adaptWidth">
                    <el-row :gutter="10">
                      <el-col :span="16">
                        <el-select
                          v-model="simulateForm.topicSuffix"
                          style="width: 100%"
                          @change="topicChange"
                          :placeholder="$t('simulate.index.111543-14')"
                        >
                          <el-option
                            v-for="topic in topics"
                            :key="topic.topicName"
                            :label="topic.desc"
                            :value="topic.topicName"
                          ></el-option>
                        </el-select>
                      </el-col>
                      <el-col :span="7">
                        <el-button type="primary" @click="enDecode">{{ $t('simulate.index.111543-15') }}</el-button>
                      </el-col>
                    </el-row>
                  </el-form-item>
                  <el-form-item style="margin-top: -10px; width: 100%" class="adaptWidth">
                    <el-row :gutter="10">
                      <el-col :span="16">
                        <el-input
                          disabled
                          type="textarea"
                          :rows="3"
                          :placeholder="$t('simulate.index.111543-16')"
                          resize="none"
                          v-model="simulateForm.data"
                        ></el-input>
                      </el-col>
                      <el-col :span="7">
                        <el-button
                          type="success"
                          style="margin-top: 2px; width: 92px; height: 62px"
                          @click="simulateSend"
                        >
                          {{ $t('simulate.index.111543-17') }}
                        </el-button>
                      </el-col>
                    </el-row>
                  </el-form-item>
                </el-form>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="24" :md="24" :lg="8" :xl="10">
        <el-tabs
          type="border-card"
          v-model="thingsType"
          @tab-click="handleClick"
          style="flex: 1; height: 800px; margin-bottom: 5px"
        >
          <el-tab-pane :label="$t('simulate.index.111543-18')" name="prop">
            <el-main v-loading="loading" style="position: relative" class="H100">
              <el-row :gutter="20" class="row-list">
                <el-col
                  :xs="24"
                  :sm="12"
                  :md="12"
                  :lg="12"
                  :xl="8"
                  v-for="(item, index) in runningData"
                  :key="index"
                  style="margin-bottom: 10px"
                >
                  <el-card shadow="hover" class="elcard" style="padding: 0px; height: auto">
                    <div class="head">
                      <div class="title">{{ item.name }}</div>
                      <div class="name">
                        <span style="color: #0f73ee">{{ item.value }}</span>
                        <span v-if="item.value">{{ item.datatype.unit || item.datatype.unitName }}</span>
                      </div>
                    </div>
                    <div>{{ item.ts }}</div>
                    <div v-if="item.value == null || item.value == ''" style="height: 20px; width: 10px"></div>
                    <div v-if="item.ts == null" style="height: 10px; width: 10px"></div>
                  </el-card>
                </el-col>
              </el-row>
            </el-main>
          </el-tab-pane>
          <el-tab-pane :label="$t('simulate.index.111543-19')" name="function">
            <el-main v-loading="loading" style="position: relative" class="H100">
              <el-row :gutter="20" class="row-list">
                <el-col
                  :xs="24"
                  :sm="12"
                  :md="12"
                  :lg="12"
                  :xl="8"
                  v-for="(item, index) in runningData"
                  :key="index"
                  style="margin-bottom: 10px"
                >
                  <el-card shadow="hover" class="elcard" style="height: auto">
                    <div class="head">
                      <div class="title">{{ item.name }}</div>
                      <div class="name">
                        <span style="color: #0f73ee">{{ item.value }}</span>
                        <span v-if="item.value">{{ item.datatype.unit }}</span>
                        <el-button
                          type="primary"
                          plain
                          :icon="Promotion"
                          size="small"
                          style="float: right; margin-right: -5px; padding: 3px 5px"
                          @click.stop="editFunc(item)"
                        >
                          发送
                        </el-button>
                      </div>
                    </div>
                    <div class="card-bottom">
                      <span>{{ item.ts }}</span>
                    </div>
                    <div v-if="item.value == null || item.value == ''" style="height: 20px; width: 10px"></div>
                    <div v-if="item.ts == null" style="height: 10px; width: 10px"></div>
                  </el-card>
                </el-col>
                <el-empty :description="$t('simulate.index.111543-20')" v-show="runningData.length == 0"></el-empty>
              </el-row>
            </el-main>
          </el-tab-pane>
          <el-tab-pane disabled name="slave">
            <template #label>
              <el-select
                v-model="params.slaveId"
                :placeholder="$t('simulate.index.111543-21')"
                @change="selectSlave"
                size="small"
              >
                <el-option
                  v-for="slave in slaveList"
                  :key="slave.slaveId"
                  :label="`${slave.deviceName}   (${slave.slaveId})`"
                  :value="slave.slaveId"
                ></el-option>
              </el-select>
            </template>
          </el-tab-pane>
        </el-tabs>
      </el-col>
    </el-row>

    <!-- 指令下发弹窗 -->
    <el-dialog style="float: right" :title="$t('simulate.index.111543-22')" v-model="dialogValue" width="30%">
      <el-form size="small" style="height: 100%; padding: 0 20px">
        <el-form-item :label="fromData.name" label-width="180px">
          <el-input
            v-model="fromData.value"
            type="number"
            @input="justicNumber()"
            v-if="fromData.datatype.type == 'integer' || fromData.datatype.type == 'decimal'"
            style="width: 50%"
          ></el-input>
          <el-select v-if="fromData.datatype.type == 'enum'" v-model="fromData.value" @change="changeSelect()">
            <el-option
              v-for="option in fromData.datatype.enumList"
              :key="option.value"
              :label="option.text"
              :value="option.value"
            ></el-option>
          </el-select>
          <el-switch v-if="fromData.datatype.type === 'bool'" v-model="fromData.value" inline-prompt />
          <div class="range" v-if="fromData.datatype.type == 'integer' || fromData.datatype.type == 'decimal'">
            ({{ fromData.datatype.min }} ~ {{ fromData.datatype.max }})
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogValue = false">{{ $t('cancel') }}</el-button>
          <el-button type="primary" @click="sendService" :loading="btnLoading" :disabled="!canSend">
            {{ $t('confirm') }}
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 编解码弹窗 -->
    <el-dialog :title="$t('simulate.index.111543-15')" v-model="encodeDiaLog" width="50%">
      <el-form>
        <el-form-item :label="$t('simulate.index.111543-24')">
          <el-select
            disabled
            v-model="codeFrom.protocolCode"
            :placeholder="$t('simulate.index.111543-25')"
            style="width: 150px"
          >
            <el-option
              v-for="p in protocolList"
              :key="p.protocolCode"
              :label="p.protocolName"
              :value="p.protocolCode"
            />
          </el-select>
          <span style="color: #ffba00; margin-left: 20px">{{ $t('simulate.index.111543-26') }}</span>
        </el-form-item>
      </el-form>
      <el-tabs type="border-card" value="first" @tab-click="encodeTagClick">
        <el-tab-pane :label="$t('simulate.index.111543-27')" name="first">
          <el-form :inline="true" :model="codeFrom" label-width="70px">
            <el-form-item>
              <el-input
                type="textarea"
                :rows="5"
                style="width: 600px"
                v-model="codeFrom.payload"
                :placeholder="$t('simulate.index.111543-28')"
              ></el-input>
            </el-form-item>
            <el-form-item style="display: block">
              <el-button style="width: 120px" type="primary" @click="onSubmit(1, false)">
                {{ $t('simulate.index.111543-29') }}
              </el-button>
            </el-form-item>
            <el-form-item v-if="codeFrom.result">
              <div style="width: 700px; color: #00bb00" v-html="codeFrom.result"></div>
            </el-form-item>
          </el-form>
        </el-tab-pane>
        <el-tab-pane :label="$t('simulate.index.111543-30')" name="second">
          <el-form label-width="85px">
            <el-form-item :label="$t('simulate.index.111543-31')" prop="slaveId" size="small">
              <el-input style="width: 250px" v-model="codeFrom.slaveId" :placeholder="$t('simulate.index.111543-32')" />
            </el-form-item>
            <el-form-item :label="$t('simulate.index.111543-33')" prop="address" size="small">
              <el-input style="width: 250px" v-model="codeFrom.address" :placeholder="$t('simulate.index.111543-33')" />
            </el-form-item>
            <el-form-item :label="$t('simulate.index.111543-34')" prop="code" size="small">
              <el-select style="width: 250px" v-model="codeFrom.code" :placeholder="$t('simulate.index.111543-35')">
                <el-option :label="$t('simulate.index.111543-36')" value="3" />
                <el-option :label="$t('simulate.index.111543-37')" value="4" />
              </el-select>
            </el-form-item>
            <el-form-item :label="$t('simulate.index.111543-38')" prop="count" size="small">
              <el-input style="width: 250px" v-model="codeFrom.count" :placeholder="$t('simulate.index.111543-38')" />
            </el-form-item>
            <el-form-item style="display: block">
              <el-button style="width: 100px" type="primary" @click="onSubmit(2, false)">
                {{ $t('simulate.index.111543-39') }}
              </el-button>
              <el-button style="width: 200px; margin-left: 20px" type="success" @click="onSubmit(2, true)">
                {{ $t('simulate.index.111543-40') }}
              </el-button>
            </el-form-item>
            <el-form-item v-if="codeFrom.result">
              <div>
                {{ $t('simulate.index.111543-41') }}
                <span style="color: #00bb00">{{ codeFrom.result }}</span>
              </div>
            </el-form-item>
          </el-form>
        </el-tab-pane>
        <el-tab-pane :label="$t('simulate.index.111543-42')" name="third">
          <el-form label-width="85px">
            <el-form-item :label="$t('simulate.index.111543-31')" prop="slaveId" size="small">
              <el-input style="width: 250px" v-model="codeFrom.slaveId" :placeholder="$t('simulate.index.111543-32')" />
            </el-form-item>
            <el-form-item :label="$t('simulate.index.111543-33')" prop="address" size="small">
              <el-input style="width: 250px" v-model="codeFrom.address" :placeholder="$t('simulate.index.111543-33')" />
            </el-form-item>
            <el-form-item :label="$t('simulate.index.111543-43')" prop="value" size="small">
              <el-input
                style="width: 250px"
                v-model="codeFrom.writeData"
                :placeholder="$t('simulate.index.111543-44')"
              />
            </el-form-item>
            <el-form-item :label="$t('simulate.index.111543-34')" size="small">
              <div style="color: #0f73ee">06</div>
            </el-form-item>
            <el-form-item style="display: block">
              <el-button style="width: 100px" type="primary" @click="onSubmit(3, false)">
                {{ $t('simulate.index.111543-41') }}
              </el-button>
              <el-button style="width: 200px; margin-left: 20px" type="success" @click="onSubmit(3, true)">
                {{ $t('simulate.index.111543-40') }}
              </el-button>
            </el-form-item>
            <el-form-item v-if="codeFrom.result">
              <div style="width: 200px; height: 40px">
                {{ $t('simulate.index.111543-41') }}
                <span style="color: #00bb00">{{ codeFrom.result }}</span>
              </div>
            </el-form-item>
          </el-form>
        </el-tab-pane>
        <el-tab-pane :label="$t('simulate.index.111543-45')" name="four">
          <el-form :inline="true" :model="codeFrom" label-width="70px">
            <el-form-item>
              <el-input
                type="textarea"
                :rows="5"
                style="width: 600px"
                v-model="codeFrom.payload"
                :placeholder="$t('simulate.index.111543-46')"
              ></el-input>
            </el-form-item>
            <el-form-item style="display: block">
              <el-button style="width: 120px" type="primary" @click="onSubmit(4, false)">
                {{ $t('simulate.index.111543-39') }}
              </el-button>
              <el-button style="width: 120px; margin-left: 20px" type="success" @click="onSubmit(5, false)">
                {{ $t('simulate.index.111543-47') }}
              </el-button>
            </el-form-item>
            <el-form-item v-if="codeFrom.result">
              <div>
                {{ $t('simulate.index.111543-48') }}
                <span style="color: #0f73ee" v-html="codeFrom.result"></span>
              </div>
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, getCurrentInstance, onMounted, nextTick } from 'vue';
import { Search, Promotion } from '@element-plus/icons-vue';
import { listDeviceShort, getDevice, updateDevice, getDeviceRunningStatus } from '@/api/iot/device';
import { listSimulateLog } from '@/api/iot/simulate';
import { listProtocol } from '@/api/iot/protocol';
import { decode, getTopics, simulateDown } from '@/api/iot/tool';
import { checkPermi } from '@/utils/permission';

const { proxy } = getCurrentInstance() as any;

const loading = ref(false);
const total = ref(0);
const deviceList = ref<any[]>([]);
const editPermission = ref(false);
const messageList = ref<any[]>([]);
const simulateForm = reactive<any>({ payloadType: 'hex', qos: '0' });
const selectDevice = ref<any>({});
const dialogValue = ref(false);
const encodeDiaLog = ref(false);
const runningData = ref<any[]>([]);
const serialNumber = ref('');
const params = reactive<any>({ serialNumber: undefined, type: 1, slaveId: '' });
const slaveList = ref<any[]>([]);
const thingsType = ref('prop');
const canSend = ref(false);
const btnLoading = ref(false);
const fromData = ref<any>({ datatype: { type: '' } });
const topics = ref<any[]>([]);
const codeFrom = reactive<any>({ protocolCode: 'MODBUS' });
const protocolList = ref<any[]>([]);
const device = ref<any>({});
const messageContentRef = ref<any>(null);

const queryParams = reactive({
  pageNum: 1,
  pageSize: 12,
  deviceName: null as any,
  productId: null as any,
  groupId: null as any,
  productName: null as any,
  isSimulate: 1,
});

function getList() {
  loading.value = true;
  (queryParams as any).params = {};
  listDeviceShort(queryParams).then((response: any) => {
    for (let i = 0; i < response.rows.length; i++) response.rows[i].isSelect = false;
    deviceList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}

function handleQuery() {
  queryParams.pageNum = 1;
  getList();
  runningData.value.length = 0;
  slaveList.value = [];
  params.slaveId = '';
}

function getDownTopics() {
  getTopics({ isSimulate: true }).then((response: any) => {
    topics.value = response.data;
  });
}

async function rowClick(row: any) {
  if (row) {
    setRadioSelected(row.deviceId);
    selectDevice.value = row;
    serialNumber.value = row.serialNumber;
    params.productId = row.productId;
    params.deviceId = row.deviceId;
    device.value = await getDeviceDetail();
    slaveList.value = device.value.subDeviceList || [];
    params.serialNumber = row.serialNumber + '_' + device.value.slaveId;
    params.slaveId = device.value.slaveId;
    getRuntimeStatus();
    getSimulateLog();
    mqttSubscribe(device.value);
  }
}

function setRadioSelected(deviceId: any) {
  deviceList.value.forEach((d) => {
    d.isSelect = d.deviceId == deviceId;
  });
}

function getSimulateLog() {
  listSimulateLog({ serialNumber: params.serialNumber }).then((response: any) => {
    messageList.value = response.rows;
    nextTick(() => scrollBottom());
  });
}

function simulateSend() {
  if (!params.serialNumber || !params.productId) {
    proxy.$modal.alert(proxy.$t('simulate.index.111543-49'));
    return;
  }
  if (!simulateForm.topicSuffix) {
    proxy.$modal.alert(proxy.$t('simulate.index.111543-50'));
    return;
  }
  if (!simulateForm.data) {
    proxy.$modal.alert(proxy.$t('simulate.index.111543-51'));
    return;
  }
  simulateForm.topic = '/' + params.productId + '/' + params.serialNumber + simulateForm.topicSuffix;
  simulateForm.direction = 'send';
  simulateForm.ts = getTime();
  messageList.value.push(JSON.parse(JSON.stringify(simulateForm)));
  scrollBottom();
  simulateDown({ topic: simulateForm.topic, message: simulateForm.data, qos: simulateForm.qos }).then(() => {
    proxy.$modal.msgSuccess(proxy.$t('simulate.index.111543-52'));
  });
}

function scrollBottom() {
  if (messageContentRef.value)
    messageContentRef.value.scroll({ top: messageContentRef.value.scrollHeight, behavior: 'smooth' });
}

function payloadTypeChange() {}
function topicChange() {}

function enDecode() {
  encodeDiaLog.value = true;
  resetCode();
}

function getTime() {
  const d = new Date();
  const pad = (n: number) => (n < 10 ? '0' + n : '' + n);
  return (
    d.getFullYear() +
    '-' +
    pad(d.getMonth() + 1) +
    '-' +
    pad(d.getDate()) +
    ' ' +
    pad(d.getHours()) +
    ':' +
    d.getMinutes() +
    ':' +
    d.getSeconds()
  );
}

function getRuntimeStatus() {
  getDeviceRunningStatus(params).then((response: any) => {
    runningData.value = response.data.thingsModels;
  });
}

function getDeviceDetail(): Promise<any> {
  return new Promise((resolve, reject) => {
    getDevice(params.deviceId)
      .then((res: any) => resolve(res.data))
      .catch(reject);
  });
}

function selectSlave() {
  params.serialNumber = serialNumber.value + '_' + params.slaveId;
  getRuntimeStatus();
}

function handleClick() {
  if (!selectDevice.value) return;
  params.type = thingsType.value === 'prop' ? 1 : 2;
}

function editFunc(item: any) {
  dialogValue.value = true;
  canSend.value = true;
  item.id = parseInt(item.id);
  fromData.value = item;
}

function sendService() {
  try {
    const sendParams = {
      topic: '/' + params.productId + '/' + params.serialNumber + '/property/set/simulate',
      slaveId: params.slaveId,
      value: fromData.value.value,
      address: fromData.value.id,
    };
    simulateDown(sendParams).then((response: any) => {
      if (response.code == 200) proxy.$modal.msgSuccess(proxy.$t('simulate.index.111543-53'));
    });
  } finally {
    dialogValue.value = false;
  }
}

function changeSelect() {}

function justicNumber() {
  canSend.value = true;
  if (fromData.value.datatype.max < fromData.value.value || fromData.value.datatype.min > fromData.value.value) {
    canSend.value = false;
  }
}

function handleStatusChange(row: any) {
  let text = row.status === 3 ? proxy.$t('simulate.index.111543-54') : proxy.$t('simulate.index.111543-55');
  updateDevice({
    deviceId: row.deviceId,
    status: row.status,
    serialNumber: row.serialNumber,
    productId: row.productId,
  }).then(() => {
    proxy.$modal.msgSuccess(text + proxy.$t('simulate.index.111543-56'));
  });
}

function encodeTagClick() {
  resetCode();
}

function onSubmit(type: number, copy: boolean) {
  codeFrom.type = type;
  if (type == 2) codeFrom.writeData = 0;
  else if (type == 3) {
    codeFrom.code = 6;
    codeFrom.count = 0;
  }
  decode(codeFrom).then((response: any) => {
    codeFrom.result = response.msg;
    if (copy) simulateForm.data = codeFrom.result;
  });
}

function getProtocol() {
  listProtocol({ status: 1 }).then((res: any) => {
    protocolList.value = res.rows;
  });
}

function resetCode() {
  codeFrom.payload = null;
  codeFrom.slaveId = undefined;
  codeFrom.address = null;
  codeFrom.code = null;
  codeFrom.count = null;
  codeFrom.writeData = null;
  codeFrom.type = null;
  codeFrom.result = null;
  codeFrom.protocolCode = 'MODBUS';
}

function mqttSubscribe(dev: any) {
  if (proxy.$mqttTool?.client) {
    let topicList: string[] = [];
    const topicSimulateLog = '/' + dev.productId + '/' + dev.serialNumber + '/ws/post/simulate';
    if (dev.subDeviceList?.length > 0) {
      dev.subDeviceList.forEach((v: any) => {
        topicList.push('/' + dev.productId + '/' + v.serialNumber + '/ws/service');
      });
    }
    topicList.push(topicSimulateLog);
    proxy.$mqttTool.subscribe(topicList);
  }
}

async function connectMqtt() {
  if (proxy.$mqttTool) {
    if (proxy.$mqttTool.client == null) await proxy.$mqttTool.connect();
    proxy.$mqttTool.client?.removeAllListeners('message');
    proxy.$mqttTool.client?.on('message', (_topic: string, message: any) => {
      message = JSON.parse(message.toString());
      if (!message) return;
      if (_topic.endsWith('ws/service')) {
        /* updateData */
      }
      if (_topic.endsWith('ws/post/simulate')) {
        messageList.value.push(...message);
        messageList.value.splice(0, message.length - 1);
        if (messageList.value.length > 30) messageList.value.splice(0, 10);
        scrollBottom();
      }
    });
  }
  getList();
  getDownTopics();
  getProtocol();
}

onMounted(() => {
  if (checkPermi(['iot:device:edit'])) editPermission.value = true;
  connectMqtt();
});
</script>

<style>
.specsColor {
  background-color: #fcfcfc;
}
</style>

<style lang="scss" scoped>
.adaptWidth :deep(.el-form-item__content) {
  width: 100%;
}
.phone {
  height: 729px;
  width: 100%;
  background-image: url('../../../assets/images/phone.png');
  background-size: 100% 100%;
}
.phone-container {
  height: 618px;
  width: 94%;
  position: relative;
  top: 46px;
  left: 2.5%;
  background-color: #fff;
  .phone-title {
    line-height: 40px;
    color: #fff;
    background-color: #007aff;
    text-align: center;
  }
  .messageContent {
    height: 440px;
    overflow-y: scroll;
    word-wrap: break-word;
    padding: 6px 0;
    color: #fff;
  }
  .messageBottom {
    height: 150px;
    position: absolute;
    bottom: 0;
    width: 100%;
    background-color: #eef3f7;
    padding: 5px;
    border-top: 1px solid #d2dae1;
  }
  .messageReceive {
    float: left;
    background-color: #409eff;
    border-radius: 6px;
    padding: 10px;
    width: 70%;
    font-size: 12px;
    margin-bottom: 15px;
    border-style: dotted;
  }
  .messageSend {
    float: right;
    background-color: #13ce66;
    border-radius: 10px;
    padding: 10px;
    width: 70%;
    font-size: 12px;
    margin-bottom: 15px;
    border-right-style: double;
  }
}
.H100 {
  overflow: hidden;
  margin-left: 10px;
}
.row-list {
  height: 700px;
  overflow: auto;
  margin: -20px -30px -30px -30px !important;
  font-size: 12px;
  line-height: 20px;
}
.head {
  .title {
    height: 15px;
    color: rgb(80, 93, 92, 0.85);
    line-height: 15px;
    font-weight: 600;
    font-size: 12px;
  }
  .name {
    height: 28px;
    margin-top: 5px;
    overflow: hidden;
    color: rgba(0, 0, 0, 0.85);
    font-size: 12px;
    line-height: 20px;
    white-space: nowrap;
    text-overflow: ellipsis;
  }
}
.card-bottom {
  color: darkgrey;
  font-size: 10px;
  margin-top: 12px;
  display: inline;
}
</style>
