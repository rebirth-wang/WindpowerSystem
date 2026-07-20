<template>
  <div class="scene-list-overview">
    <el-row>
      <el-col :span="12" class="card-box" style="padding-right: 7.5px; margin-top: 5px">
        <el-card>
          <template #header>
            <span>{{ $t('scene.overview.324354-0') }}</span>
          </template>
          <div class="el-table el-table--enable-row-hover el-table--medium">
            <div class="prop-box-info" id="scenePropBox">
              <div class="left-pic">
                <el-image
                  style="width: 100px; height: 100px"
                  :src="sceneModels.imgUrl ? baseUrl + sceneModels.imgUrl : sceneInfoImg"
                  fit="fill"
                ></el-image>
              </div>
              <div class="right-message">
                <div class="title">{{ sceneModels.sceneModelName }}</div>
                <div class="info-item">
                  <label>{{ $t('scene.overview.324354-1') }}</label>
                  <span>{{ sceneModels.deptName }}</span>
                </div>
                <div class="info-item">
                  <label>{{ $t('scene.overview.324354-2') }}</label>
                  <span v-if="sceneModels.cusDeviceList && sceneModels.cusDeviceList.length > 0">
                    <el-tag
                      class="tag-wrap"
                      size="small"
                      v-for="(item, index) in sceneModels.cusDeviceList"
                      :key="index"
                    >
                      {{ item.name }}
                    </el-tag>
                  </span>
                </div>
                <div class="info-item">
                  <label>{{ $t('scene.overview.324354-3') }}</label>
                  <span>{{ sceneModels.updateTime }}</span>
                </div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="12" class="card-box" style="padding-left: 7.5px; margin-top: 5px">
        <el-card>
          <template #header>
            <span>{{ $t('scene.overview.324354-4') }}</span>
          </template>
          <div class="el-table el-table--enable-row-hover el-table--medium" v-if="paramsList.length == 0">
            <div class="prop-box-attr" :style="{ height: scenePropBoxHeight + 'px' }">
              <div class="num-empty">
                {{ $t('scene.overview.324354-5') }}
                <br />
                <br />
                <br />
                <br />
                <el-image style="width: 493px; height: 29px" :src="sceneFlowImg" fit="fill"></el-image>
              </div>
            </div>
          </div>
          <div v-else>
            <div class="prop-box-attr" :style="{ height: scenePropBoxHeight + 'px' }">
              <div class="prop-list">
                <div class="prop-item" v-for="(item, index) in paramsList" :key="index">
                  {{ item.name }}: {{ item.defaultValue }} {{ item.unit }}
                </div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="24" class="card-box" style="margin-bottom: 15px">
        <el-card>
          <template #header>
            <span>{{ $t('scene.overview.324354-6') }}</span>
          </template>
          <div class="el-table--enable-row-hover el-table--medium">
            <el-form :model="queryParams" ref="queryFormRef" :inline="true" label-width="68px">
              <el-form-item prop="sceneModelDeviceId">
                <el-select
                  v-model="queryParams.sceneModelDeviceId"
                  :placeholder="$t('scene.overview.324354-8')"
                  clearable
                  style="width: 192px"
                >
                  <el-option
                    v-for="(item, index) in sceneModels.sceneModelDeviceVOList"
                    :key="index"
                    :label="item.name"
                    :value="item.id"
                  ></el-option>
                </el-select>
              </el-form-item>
              <el-form-item prop="sourceName">
                <el-input
                  v-model="queryParams.sourceName"
                  :placeholder="$t('scene.overview.324354-12')"
                  clearable
                  style="width: 192px"
                />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" :icon="Search" @click="handleQuery">{{ $t('search') }}</el-button>
                <el-button :icon="Refresh" @click="handleResetQuery">{{ $t('reset') }}</el-button>
              </el-form-item>
            </el-form>
            <el-table v-loading="loading" :data="variableList" style="width: 100%; margin-top: 10px" :border="false">
              <el-table-column prop="id" :label="$t('scene.overview.324354-13')" width="100"></el-table-column>
              <el-table-column
                prop="sceneModelDeviceName"
                :label="$t('scene.overview.324354-7')"
                align="left"
                min-width="140"
              ></el-table-column>
              <el-table-column
                prop="slaveName"
                :label="$t('scene.overview.324354-9')"
                align="left"
                min-width="120"
              ></el-table-column>
              <el-table-column
                prop="sourceName"
                :label="$t('scene.overview.324354-11')"
                align="left"
                min-width="130"
              ></el-table-column>
              <el-table-column
                prop="updateTime"
                :label="$t('scene.overview.324354-14')"
                align="center"
                width="175"
              ></el-table-column>
              <el-table-column prop="value" :label="$t('scene.overview.324354-15')" align="center" min-width="130">
                <template #default="scope">
                  <span>
                    {{ scope.row.valueName === '' || scope.row.valueName === null ? '-' : scope.row.valueName }}
                    {{ scope.row.value !== '' && scope.row.value !== null ? scope.row.unit : '' }}
                    <el-icon
                      v-if="scope.row.isReadonly === 0"
                      style="cursor: pointer; color: #1890ff"
                      v-hasPermi="['iot:service:invoke']"
                      @click="handleEditVariable(scope.row)"
                    >
                      <Edit />
                    </el-icon>
                  </span>
                </template>
              </el-table-column>
              <el-table-column :label="$t('opation')" align="center" width="100">
                <template #default="scope">
                  <el-button size="small" link @click="handleQueryHistory(scope.row)">
                    {{ $t('scene.overview.324354-16') }}
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
            <pagination
              v-show="total > 0"
              :total="total"
              v-model:page="queryParams.pageNum"
              v-model:limit="queryParams.pageSize"
              @pagination="getVariableList"
            />
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 设备类型下发弹框 -->
    <el-dialog :title="$t('device.realTime-status.099127-26')" v-model="dialogValue" width="480px">
      <el-form>
        <el-form-item v-for="(item, index) in opationList" :label="`${item.label}：`" :key="index" label-width="120px">
          <el-input
            v-model="funVal[item.key]"
            :precision="0"
            :controls="false"
            @input="justNumber(item)"
            type="number"
            v-if="
              item.dataTypeName == 'integer' ||
              item.dataTypeName == 'decimal' ||
              (item.dataTypeName == 'array' && item.arrayType == 'integer') ||
              (item.dataTypeName == 'array' && item.arrayType == 'decimal')
            "
            style="width: 200px"
          ></el-input>
          <el-input
            v-model="funVal[item.key]"
            :precision="0"
            :controls="false"
            :placeholder="$t('plzInput')"
            type="text"
            v-if="item.dataTypeName == 'string' || (item.dataTypeName == 'array' && item.arrayType == 'string')"
            style="width: 230px"
            @input="justNumber(item)"
          ></el-input>
          <el-select
            v-if="item.dataTypeName == 'enum' || item.dataTypeName == 'bool'"
            v-model="funVal[item.key]"
            @change="changeSelect()"
          >
            <el-option
              v-for="option in item.options"
              :key="option.value"
              :label="option.label"
              :value="option.value"
            ></el-option>
          </el-select>
          <span
            v-if="
              (item.dataTypeName == 'integer' ||
                item.dataTypeName == 'decimal' ||
                (item.dataTypeName == 'array' && item.arrayType == 'integer') ||
                (item.dataTypeName == 'array' && item.arrayType == 'decimal')) &&
              item.unit &&
              item.unit != 'un' &&
              item.unit != '/'
            "
          >
            ({{ item.unit }})
          </span>
          <span
            style="margin-left: 5px"
            v-if="
              item.dataTypeName == 'integer' ||
              item.dataTypeName == 'decimal' ||
              (item.dataTypeName == 'array' && item.arrayType == 'integer') ||
              (item.dataTypeName == 'array' && item.arrayType == 'decimal')
            "
          >
            ({{ item.min }} ~ {{ item.max }})
          </span>
        </el-form-item>
        <el-form-item style="display: none">
          <el-input v-model="functionName"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogValue = false">{{ $t('cancel') }}</el-button>
        <el-button type="primary" @click="sendService" :loading="btnLoading" :disabled="!canSend">
          {{ $t('confirm') }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onUnmounted, watch, getCurrentInstance, nextTick } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { Search, Refresh, Edit } from '@element-plus/icons-vue';
import { ElMessageBox } from 'element-plus';
import { getSceneModelDataList, getSceneModelTagList } from '@/api/scene/list.js';
import { serviceInvokeReply, serviceInvoke } from '@/api/iot/runstatus.js';
import { getOrderControl } from '@/api/iot/control';
import { useUserStore } from '@/stores/modules/user';
import dayjs from 'dayjs';
import sceneInfoImg from '@/assets/images/scene_info.png';
import sceneFlowImg from '@/assets/images/scene_basic_attr_flow.png';

const { proxy } = getCurrentInstance() as any;
const route = useRoute();
const router = useRouter();
const userStore = useUserStore();
const baseUrl = import.meta.env.VITE_APP_BASE_API;

const props = defineProps<{ sceneModels: any }>();

const queryFormRef = ref<any>(null);
const scenePropBoxHeight = ref(150);
const loading = ref(false);
const queryParams = reactive<any>({
  sceneModelId: null,
  sceneModelDeviceId: null,
  slaveName: '',
  sourceName: '',
  pageNum: 1,
  pageSize: 10,
});
const variableList = ref<any[]>([]);
const funVal = ref<any>({});
const chooseFun = ref<any>({});
const deviceInfo = ref<any>({});
const serialNumber = ref('');
const opationList = ref<any[]>([]);
const functionName = ref('');
const total = ref(0);
const canSend = ref(false);
const btnLoading = ref(false);
const dialogValue = ref(false);
const paramsList = ref<any[]>([]);
const inputVariableQueryParams = reactive<any>({
  sceneModelId: null,
  variableType: 2,
  name: '',
  pageNum: 1,
  pageSize: 10,
});

/** 获取窗体高度 */
function calculateScenePropBoxHeight() {
  setTimeout(() => {
    const el = document.getElementById('scenePropBox');
    if (el) {
      scenePropBoxHeight.value = parseFloat(String(el.offsetHeight));
    }
  }, 500);
}

/** 获取变量情况列表 */
function getVariableList() {
  loading.value = true;
  queryParams.sceneModelId = route.query.sceneModelId;
  let { sceneModelDeviceId, ...pres } = queryParams;
  if (sceneModelDeviceId === '-1') {
    sceneModelDeviceId = '';
  }
  const params = { sceneModelDeviceId, ...pres };
  getSceneModelDataList(params).then((res: any) => {
    if (res.code === 200) {
      variableList.value = res.rows.map((item: any) => {
        if (item.variableType == 1) {
          return {
            ...item,
            valueName: getValueName(item),
            dataTypeName: item.datatype?.type || '',
          };
        } else {
          return {
            ...item,
            valueName: getValueName(item),
          };
        }
      });
      total.value = res.total;
    }
    loading.value = false;
  });
}

/** 获取变量列表 */
function getInputVariableList() {
  inputVariableQueryParams.sceneModelId = Number(route.query.sceneModelId);
  inputVariableQueryParams.isSceneAttribute = 1;
  getSceneModelTagList(inputVariableQueryParams).then((res: any) => {
    if (res.code === 200) {
      paramsList.value = res.rows;
    } else {
      proxy.$modal.msgError(res.msg);
    }
  });
}

/** 判断输入是否超过范围 */
function justNumber(val: any) {
  canSend.value = true;
  opationList.value.some((item) => {
    if (item.max < funVal.value[item.key] || item.min > funVal.value[item.key]) {
      canSend.value = false;
      return true;
    }
  });
}

/** 下拉选择修改触发 */
function changeSelect() {
  // Vue 3 reactive, no need for $forceUpdate
}

/** 封装操作列表 */
function getOpationList(item: any) {
  opationList.value = [];
  let options: any[] = [];
  funVal.value = {};
  const datatype = item.datatype;
  if (datatype.type == 'enum') {
    options =
      datatype.enumList?.map((option: any) => ({
        label: option.text,
        value: option.value + '',
      })) || [];
  }
  if (datatype.type == 'bool') {
    options = [
      { label: datatype.falseText || '', value: '0' },
      { label: datatype.trueText || '', value: '1' },
    ];
  }
  opationList.value.push({
    dataTypeName: datatype.type,
    arrayType: datatype.arrayType,
    label: item.sourceName,
    key: item.identifier,
    max: parseInt(datatype?.max || 100),
    min: parseInt(datatype?.min || -100),
    options: options,
    value: item.value,
  });
  opationList.value.forEach((op: any) => {
    let value = op.value;
    if (
      op.dataTypeName == 'integer' ||
      op.dataTypeName == 'decimal' ||
      (op.dataTypeName == 'array' && op.arrayType == 'integer') ||
      (op.dataTypeName == 'array' && op.arrayType == 'decimal')
    ) {
      value = parseInt(value);
    }
    funVal.value[op.key] = value;
  });
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.pageNum = 1;
  getVariableList();
}

/** 重置按钮操作 */
function handleResetQuery() {
  queryFormRef.value?.resetFields();
  handleQuery();
}

/** 编辑变量(设备类型) */
async function editVariable(item: any) {
  const userName = userStore.dept?.userName;
  if (userName !== item.createBy) {
    const params = { deviceId: item.deviceId, modelId: item.datasourceId };
    const response: any = await getOrderControl(params);
    if (response.code != 200) {
      proxy.$modal.msgWarning(response.msg);
      return;
    }
  }
  serialNumber.value = item.serialNumber;
  let title = '';
  if (item.status !== 3) {
    if (item.status === 1) {
      title = proxy.$t('device.device-variable.930930-0');
    } else if (item.status === 2) {
      title = proxy.$t('device.device-variable.930930-1');
    } else {
      title = proxy.$t('device.device-variable.930930-2');
    }
    proxy.$modal.msgWarning(title);
    return;
  }
  dialogValue.value = true;
  canSend.value = true;
  funVal.value = {};
  chooseFun.value = item;
  getOpationList(item);
}

/** 编辑变量值 */
async function handleEditVariable(item: any) {
  if (item.variableType !== 1) {
    ElMessageBox.prompt(proxy.$t('scene.overview.324354-17'), proxy.$t('edit'), {
      confirmButtonText: proxy.$t('confirm'),
      cancelButtonText: proxy.$t('cancel'),
      inputPattern: /\S/,
      inputErrorMessage: proxy.$t('scene.overview.324354-17'),
      inputValue: item.value,
    })
      .then(({ value }: any) => {
        const command: any = {};
        command[item.identifier] = value;
        const data = {
          sceneModelId: item.sceneModelId,
          variableType: item.variableType,
          serialNumber: item.serialNumber,
          productId: item.productId,
          remoteCommand: command,
          identifier: item.identifier,
          modelName: item.modelName,
          isShadow: item.status != 3,
          type: item.type,
        };
        serviceInvoke(data).then((res: any) => {
          if (res.code === 200) {
            item.updateTime = dayjs().format('YYYY-MM-DD HH:mm:ss');
            item.value = value;
          }
        });
      })
      .catch(() => {});
  } else {
    editVariable(item);
  }
}

/** 发送指令 */
async function sendService() {
  try {
    let params = funVal.value;
    const pas = {
      serialNumber: serialNumber.value,
      identifier: chooseFun.value.identifier,
      remoteCommand: params,
    };
    btnLoading.value = true;
    if (
      deviceInfo.value.protocolCode === 'MODBUS-TCP-OVER-RTU' ||
      deviceInfo.value.protocolCode === 'MODBUS-TCP' ||
      deviceInfo.value.protocolCode === 'MODBUS-RTU'
    ) {
      const response: any = await serviceInvokeReply(pas);
      if (response.code === 200) {
        proxy.$modal.msgSuccess(proxy.$t('device.running-status.866086-25'));
      } else {
        proxy.$modal.msgError(response.msg);
      }
    } else {
      const response: any = await serviceInvoke(pas);
      if (response.code === 200) {
        proxy.$modal.msgSuccess(proxy.$t('device.running-status.866086-25'));
      } else {
        proxy.$modal.msgError(response.msg);
      }
    }
    for (let i = 0; i < variableList.value.length; i++) {
      if (variableList.value[i].identifier == chooseFun.value.identifier) {
        const variable = Object.values(funVal.value)[0];
        variableList.value[i].value = variable;
        variableList.value[i].valueName = getValueName(variableList.value[i]);
        break;
      }
    }
  } finally {
    btnLoading.value = false;
    dialogValue.value = false;
  }
}

function getValueName(item: any) {
  let res = item.value || '-';
  if (item.datatype) {
    switch (item.datatype.type) {
      case 'bool':
        if (0 == item.value) res = item.datatype.falseText;
        if (1 == item.value) res = item.datatype.trueText;
        break;
      case 'enum':
        item.datatype.enumList?.some((enumOpt: any) => {
          if (enumOpt.value == item.value) {
            res = enumOpt.text;
            return true;
          }
        });
        break;
    }
  }
  return res;
}

/** 查询历史数据 */
function handleQueryHistory(item: any) {
  router.push({
    path: '/dataCenter/history',
    query: {
      sceneModelId: item.sceneModelId,
      sceneModelDeviceId: item.sceneModelDeviceId,
      identifier: item.identifier,
      activeName: 'scene',
    },
  });
}

/** 连接Mqtt消息服务器 */
async function connectMqtt() {
  if (proxy?.$mqttTool.client == null) {
    await proxy?.$mqttTool.connect();
  }
  proxy?.$mqttTool.client.removeAllListeners('message');
  mqttCallback();
}

/** Mqtt回调处理 */
function mqttCallback() {
  proxy?.$mqttTool.client.on('message', (topic: string, message: any) => {
    let topics = topic.split('/');
    let deviceNum = topics[2];
    message = JSON.parse(message.toString());
    if (!message) return;
    console.log('接收到【物模型】主题：', topic);
    console.log('接收到【物模型】内容：', message);
    if (topic.endsWith('ws/service')) {
      message = message.message;
      message.forEach((mes: any) => {
        variableList.value.forEach((vari: any) => {
          proxy?.$busEvent.$emit('updateData', {
            serialNumber: topics[2],
            productId: vari.productId,
            data: message,
          });
          if (vari.serialNumber === deviceNum) {
            if (vari.identifier === mes.id) {
              vari.value = mes.value;
              vari.valueName = getValueName(vari);
              vari.updateTime = dayjs().format('YYYY-MM-DD HH:mm:ss');
            }
          }
        });
      });
    }
    if (topic.endsWith('scene/report')) {
      message.forEach((mes: any) => {
        variableList.value.forEach((vari: any) => {
          if (vari.identifier === mes.id) {
            vari.value = mes.value;
            vari.valueName = getValueName(vari);
            vari.updateTime = dayjs().format('YYYY-MM-DD HH:mm:ss');
          }
        });
      });
    }
  });
}

onMounted(() => {
  getVariableList();
  calculateScenePropBoxHeight();
  window.addEventListener('resize', calculateScenePropBoxHeight, true);
  connectMqtt();
  getInputVariableList();
});

onUnmounted(() => {
  window.removeEventListener('resize', calculateScenePropBoxHeight, true);
});
</script>

<style lang="scss" scoped>
.scene-list-overview {
  width: 100%;

  .card-box {
    padding: 0;
    margin: 15px 0 0;
  }

  .prop-box-info {
    display: flex;

    .left-pic {
      margin-right: 20px;
    }

    .right-message {
      width: 100%;

      .title {
        font-size: 12px;
        color: #333;
        font-weight: 600;
        margin-bottom: 20px;
      }

      .info-item {
        display: flex;
        align-items: flex-start;
        margin-bottom: 15px;
        font-size: 12px;
        font-weight: 400;
        color: #333;

        label {
          color: #666;
          width: 80px;
          flex-shrink: 0;
          font-weight: normal;
        }

        .tag-wrap {
          margin-bottom: 5px;
          margin-right: 5px;
          cursor: pointer;
        }
      }
    }
  }

  .prop-box-attr {
    width: 100%;
    overflow: auto;

    .num-empty {
      width: 100%;
    }
  }

  /* Grid 布局容器 */
  .prop-list {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 10px;
  }

  .prop-item {
    box-sizing: border-box;
    padding: 8px;
  }
}
</style>
