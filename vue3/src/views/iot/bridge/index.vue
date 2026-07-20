<template>
  <div class="iot-bridge">
    <el-card v-show="showSearch" class="search-card">
      <el-form @submit.prevent :model="queryParams" ref="queryRef" :inline="true" class="search-form">
        <el-form-item prop="name">
          <el-input
            v-model="queryParams.name"
            :placeholder="$t('views.iot.bridge.index.525282-1')"
            clearable
            style="width: 192px"
            @keyup.enter="handleQuery"
          />
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
          <el-button plain :icon="Plus" @click="handleAdd" v-hasPermi="['iot:bridge:add']">
            {{ $t('views.iot.bridge.index.525282-4') }}
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button plain :icon="Edit" :disabled="single" @click="handleUpdate" v-hasPermi="['iot:bridge:edit']">
            {{ $t('views.iot.bridge.index.525282-5') }}
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button plain :icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['iot:bridge:remove']">
            {{ $t('views.iot.bridge.index.525282-6') }}
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button plain :icon="Download" @click="handleExport" v-hasPermi="['iot:bridge:export']">
            {{ $t('views.iot.bridge.index.525282-7') }}
          </el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
      </el-row>

      <el-table
        v-loading="loading"
        :data="bridgeList"
        :border="false"
        @selection-change="handleSelectionChange"
        ref="multipleTableRef"
        :row-key="getRowKeys"
      >
        <el-table-column :reserve-selection="true" type="selection" width="55" align="center" />
        <el-table-column :label="$t('views.iot.bridge.index.525282-0')" align="left" prop="name" min-width="200" />
        <el-table-column :label="$t('views.iot.bridge.index.525282-8')" align="center" prop="enable" width="80">
          <template #default="scope">
            <el-switch
              v-model="scope.row.enable"
              active-value="1"
              inactive-value="0"
              @change="handleEnableChange(scope.row)"
              :disabled="isDisabled"
            ></el-switch>
          </template>
        </el-table-column>
        <el-table-column :label="$t('views.iot.bridge.index.525282-9')" align="center" prop="status" width="120">
          <template #default="scope">
            <dict-tag :options="bridge_status" :value="scope.row.status"></dict-tag>
          </template>
        </el-table-column>
        <el-table-column :label="$t('views.iot.bridge.index.525282-12')" align="center" prop="type" width="180">
          <template #default="scope">
            <dict-tag :options="bridge_type" :value="scope.row.type" />
          </template>
        </el-table-column>
        <el-table-column :label="$t('views.iot.bridge.index.525282-16')" align="center" prop="direction" width="80">
          <template #default="scope">
            <dict-tag :options="bridging_direction" :value="scope.row.direction"></dict-tag>
          </template>
        </el-table-column>
        <el-table-column :label="$t('template.index.891112-117')" align="center" prop="tenantName" width="150" />
        <el-table-column :label="$t('template.index.891112-118')" align="center" prop="createBy" width="150" />
        <el-table-column :label="$t('views.iot.bridge.index.525282-19')" align="left" prop="remark" min-width="150" />
        <el-table-column
          fixed="right"
          :label="$t('views.iot.bridge.index.525282-20')"
          align="center"
          class-name="small-padding fixed-width"
          width="210"
        >
          <template #default="scope">
            <el-button
              size="small"
              link
              v-show="scope.row.shows"
              :icon="Sort"
              @click="handleCommect(scope.row)"
              v-hasPermi="['iot:bridge:edit']"
            >
              {{
                scope.row.isConnect ? $t('views.iot.bridge.index.525282-110') : $t('views.iot.bridge.index.525282-21')
              }}
            </el-button>
            <el-button size="small" link :icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['iot:bridge:edit']">
              {{ $t('update') }}
            </el-button>
            <el-button
              size="small"
              link
              :icon="Delete"
              @click="handleDelete(scope.row)"
              v-hasPermi="['iot:bridge:remove']"
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

    <!-- 添加或修改数据桥接对话框 -->
    <el-dialog :title="title" v-model="open" width="620px" append-to-body :close-on-click-modal="false">
      <el-form
        ref="bridgeFormRef"
        :model="form.bridge"
        :rules="rules"
        label-width="120px"
        :validate-on-rule-change="false"
      >
        <el-form-item :label="$t('views.iot.bridge.index.525282-22')" prop="name">
          <el-input
            v-model="form.bridge.name"
            :placeholder="$t('views.iot.bridge.index.525282-23')"
            style="width: 400px"
          />
        </el-form-item>
        <el-form-item :label="$t('views.iot.bridge.index.525282-16')" prop="direction">
          <el-select
            v-model="form.bridge.direction"
            :placeholder="$t('views.iot.bridge.index.525282-24')"
            style="width: 400px"
            @change="clearconfig"
          >
            <el-option
              v-for="dict in bridging_direction"
              :key="dict.value"
              :label="dict.label"
              :value="Number(dict.value)"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item :label="$t('views.iot.bridge.index.525282-8')" prop="enable">
          <el-switch v-model="form.bridge.enable" active-value="1" inactive-value="0" />
        </el-form-item>
        <el-form-item class="label-top" :label="$t('views.iot.bridge.index.525282-12')" prop="type">
          <el-radio-group v-model="form.bridge.type" @change="clearconfig">
            <el-radio-button v-for="dict in bridge_type" :key="dict.value" :value="Number(dict.value)">
              {{ dict.label }}
            </el-radio-button>
          </el-radio-group>
        </el-form-item>
      </el-form>

      <!-- BEGIN:HTTP桥接 -->
      <el-form
        ref="httpFormRef"
        :model="form.httpform"
        :rules="httprules"
        label-width="120px"
        v-show="form.bridge.type == 3"
        :validate-on-rule-change="false"
      >
        <div v-show="form.bridge.direction === 2">
          <el-form-item :label="$t('views.iot.bridge.index.525282-25')" prop="hostUrlbody">
            <el-input
              :placeholder="$t('views.iot.bridge.index.525282-26')"
              v-model="form.httpform.hostUrlbody"
              style="width: 400px"
            >
              <template #prepend>
                <el-select
                  v-model="hostUrlhead"
                  :placeholder="$t('views.iot.bridge.index.525282-27')"
                  style="width: 90px"
                >
                  <el-option v-for="item in urlOptions" :key="item" :value="item"></el-option>
                </el-select>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item :label="$t('views.iot.bridge.index.525282-28')" prop="method">
            <el-select
              v-model="form.httpform.method"
              :placeholder="$t('views.iot.bridge.index.525282-27')"
              style="width: 400px"
            >
              <el-option v-for="item in options" :key="item" :value="item"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item :label="$t('views.iot.bridge.index.525282-29')">
            <div v-if="isShowHeader">
              <div v-for="(item, index) in requestHeadersMap" :key="index">
                <el-row>
                  <el-col :span="8">
                    <el-input size="small" :placeholder="$t('views.iot.bridge.index.525282-30')" v-model="item.key">
                      <template #prepend>{{ $t('views.iot.bridge.index.525282-32') }}:</template>
                    </el-input>
                  </el-col>
                  <el-col :span="8" style="margin: 0 15px 0 25px">
                    <el-input size="small" :placeholder="$t('views.iot.bridge.index.525282-31')" v-model="item.value">
                      <template #prepend>{{ $t('views.iot.bridge.index.525282-33') }}:</template>
                    </el-input>
                  </el-col>
                  <div class="delete-wrap">
                    <el-button
                      size="small"
                      plain
                      type="danger"
                      style="padding: 5px"
                      :icon="Delete"
                      @click="handleRemoveAction(index)"
                    >
                      {{ $t('del') }}
                    </el-button>
                  </div>
                </el-row>
              </div>
            </div>
            <div>
              +
              <a style="color: #486ff2" @click="handleAddAction()">{{ $t('views.iot.bridge.index.525282-98') }}</a>
            </div>
          </el-form-item>
          <el-form-item :label="$t('views.iot.bridge.index.525282-35')">
            <div v-for="(item, index) in requestQuerysMap" :key="index">
              <el-row v-if="isShowParams">
                <el-col :span="8">
                  <el-input size="small" :placeholder="$t('views.iot.bridge.index.525282-30')" v-model="item.key">
                    <template #prepend>{{ $t('views.iot.bridge.index.525282-32') }}:</template>
                  </el-input>
                </el-col>
                <el-col :span="8" style="margin: 0 15px 0 25px">
                  <el-input size="small" :placeholder="$t('views.iot.bridge.index.525282-31')" v-model="item.value">
                    <template #prepend>{{ $t('views.iot.bridge.index.525282-33') }}:</template>
                  </el-input>
                </el-col>
                <div class="delete-wrap">
                  <el-button
                    size="small"
                    plain
                    type="danger"
                    style="padding: 5px"
                    :icon="Delete"
                    @click="handleRemoveQuerys(index)"
                  >
                    {{ $t('del') }}
                  </el-button>
                </div>
              </el-row>
            </div>
            <div>
              +
              <a style="color: #486ff2" @click="handleAddQuerys()">{{ $t('views.iot.bridge.index.525282-34') }}</a>
            </div>
          </el-form-item>
          <el-form-item :label="$t('views.iot.bridge.index.525282-109')">
            <div v-if="isShowConfig">
              <div v-for="(item, index) in requestConfigMap" :key="index">
                <el-row>
                  <el-col :span="8">
                    <el-input size="small" :placeholder="$t('views.iot.bridge.index.525282-30')" v-model="item.key">
                      <template #prepend>{{ $t('views.iot.bridge.index.525282-32') }}:</template>
                    </el-input>
                  </el-col>
                  <el-col :span="8" style="margin: 0 15px 0 25px">
                    <el-input size="small" :placeholder="$t('views.iot.bridge.index.525282-31')" v-model="item.value">
                      <template #prepend>{{ $t('views.iot.bridge.index.525282-33') }}:</template>
                    </el-input>
                  </el-col>
                  <div class="delete-wrap">
                    <el-button
                      size="small"
                      plain
                      type="danger"
                      style="padding: 5px"
                      :icon="Delete"
                      @click="handleRemoveConfig(index)"
                    >
                      {{ $t('del') }}
                    </el-button>
                  </div>
                </el-row>
              </div>
            </div>
            <div>
              +
              <a style="color: #486ff2" @click="handleAddConfig()">{{ $t('views.iot.bridge.index.525282-99') }}</a>
            </div>
          </el-form-item>
          <el-form-item prop="requestBody">
            <template #label>
              <span class="span-box">
                <span>{{ $t('views.iot.bridge.index.525282-36') }}</span>
                <el-tooltip :content="$t('views.iot.bridge.index.525282-112')" placement="top">
                  <el-icon style="margin-left: 3px"><QuestionFilled /></el-icon>
                </el-tooltip>
              </span>
            </template>
            <el-input
              v-model="form.httpform.requestBody"
              type="textarea"
              :rows="6"
              :placeholder="$t('views.iot.bridge.index.525282-37')"
              style="width: 400px"
              :autosize="{ minRows: 3, maxRows: 5 }"
            />
          </el-form-item>
        </div>
        <div v-show="form.bridge.direction === 1">
          <el-form-item :label="$t('views.iot.bridge.index.525282-82')" prop="route">
            <el-select
              v-model="form.httpform.route"
              :placeholder="$t('views.iot.bridge.index.525282-82')"
              style="width: 400px"
            >
              <el-option
                v-for="dict in bridge_entry"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
              ></el-option>
            </el-select>
          </el-form-item>
        </div>
      </el-form>

      <!-- BEGIN:Mqtt桥接 -->
      <el-form
        ref="mqttFormRef"
        :model="form.mqttform"
        :rules="mqttrules"
        label-width="120px"
        v-show="form.bridge.type == 4"
        :validate-on-rule-change="false"
      >
        <el-form-item :label="$t('views.iot.bridge.index.525282-38')" prop="hostUrlbody">
          <el-input
            :placeholder="$t('views.iot.bridge.index.525282-39')"
            v-model="form.mqttform.hostUrlbody"
            style="width: 400px"
          >
            <template #prepend>tcp://</template>
          </el-input>
        </el-form-item>
        <el-form-item :label="$t('views.iot.bridge.index.525282-40')" prop="clientId">
          <el-input
            v-model="form.mqttform.clientId"
            :placeholder="$t('views.iot.bridge.index.525282-41')"
            style="width: 400px"
          />
        </el-form-item>
        <el-form-item :label="$t('views.iot.bridge.index.525282-42')" prop="username">
          <el-input
            v-model="form.mqttform.username"
            :placeholder="$t('views.iot.bridge.index.525282-43')"
            style="width: 400px"
          />
        </el-form-item>
        <el-form-item :label="$t('views.iot.bridge.index.525282-44')" prop="password">
          <el-input
            v-model="form.mqttform.password"
            :placeholder="$t('views.iot.bridge.index.525282-45')"
            type="password"
            style="width: 400px"
          />
        </el-form-item>
        <el-form-item :label="$t('views.iot.bridge.index.525282-100')" prop="route" v-if="form.bridge.direction === 1">
          <el-input
            v-model="form.mqttform.route"
            :placeholder="$t('views.iot.bridge.index.525282-101')"
            style="width: 400px"
          />
        </el-form-item>
        <el-form-item prop="route" v-else>
          <template #label>
            <span class="span-box">
              <span>{{ $t('views.iot.bridge.index.525282-102') }}</span>
              <el-tooltip :content="$t('views.iot.bridge.index.525282-111')" placement="top">
                <el-icon style="margin-left: 3px"><QuestionFilled /></el-icon>
              </el-tooltip>
            </span>
          </template>
          <el-input
            v-model="form.mqttform.route"
            :placeholder="$t('views.iot.bridge.index.525282-103')"
            style="width: 400px"
          />
        </el-form-item>
      </el-form>
      <div class="advanced-header" @click="isExpanded = !isExpanded" v-show="form.bridge.type == 4">
        <span>{{ $t('views.iot.bridge.index.525282-104') }}</span>
        <el-icon :class="{ 'rotate-180': isExpanded }"><ArrowDown /></el-icon>
      </div>
      <el-collapse-transition v-show="form.bridge.type == 4">
        <div v-show="isExpanded && form.bridge.type == 4">
          <el-form :model="form.mqttform" label-width="120px" style="margin-top: 10px">
            <el-form-item :label="$t('views.iot.bridge.index.525282-105')">
              <el-select v-model="form.mqttform.version" style="width: 400px">
                <el-option
                  v-for="dict in mqtt_version"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                ></el-option>
              </el-select>
            </el-form-item>
            <el-form-item :label="$t('views.iot.bridge.index.525282-106')">
              <el-input
                v-model="form.mqttform.timeout"
                type="number"
                :placeholder="$t('views.iot.bridge.index.525282-108')"
                style="width: 400px"
              ></el-input>
            </el-form-item>
            <el-form-item label="Keep Alive">
              <el-input
                v-model="form.mqttform.keepalive"
                type="number"
                :placeholder="$t('views.iot.bridge.index.525282-108')"
                style="width: 400px"
              ></el-input>
            </el-form-item>
            <el-form-item :label="$t('views.iot.bridge.index.525282-107')">
              <el-switch v-model="form.mqttform.automaticReconnect"></el-switch>
            </el-form-item>
            <el-form-item label="Clean Session">
              <el-switch v-model="form.mqttform.cleanSession"></el-switch>
            </el-form-item>
          </el-form>
        </div>
      </el-collapse-transition>

      <!-- BEGIN:数据存储 -->
      <el-form
        ref="dbFormRef"
        :model="form.dbform"
        :rules="dbrules"
        label-width="120px"
        v-show="form.bridge.type == 5"
        :validate-on-rule-change="false"
      >
        <el-form-item :label="$t('views.iot.bridge.index.525282-48')" prop="type">
          <el-select
            v-model="form.dbform.type"
            :placeholder="$t('views.iot.bridge.index.525282-49')"
            style="width: 400px"
          >
            <el-option
              v-for="dict in database_type"
              :key="dict.value"
              :value="dict.label"
              :disabled="dict.value == 2"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item :label="$t('views.iot.bridge.index.525282-50')" prop="databaseSource">
          <el-select
            v-model="form.dbform.databaseSource"
            :placeholder="$t('views.iot.bridge.index.525282-51')"
            style="width: 400px"
          >
            <el-option v-for="item in dbOptions" :key="item" :value="item"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item :label="$t('views.iot.bridge.index.525282-52')" prop="host">
          <el-input
            v-model="form.dbform.host"
            :placeholder="$t('views.iot.bridge.index.525282-81')"
            style="width: 400px"
          />
        </el-form-item>
        <el-form-item :label="$t('views.iot.bridge.index.525282-42')" prop="username">
          <el-input
            v-model="form.dbform.username"
            :placeholder="$t('views.iot.bridge.index.525282-43')"
            style="width: 400px"
          />
        </el-form-item>
        <el-form-item :label="$t('views.iot.bridge.index.525282-44')" prop="password">
          <el-input
            v-model="form.dbform.password"
            :placeholder="$t('views.iot.bridge.index.525282-45')"
            type="password"
            style="width: 400px"
          />
        </el-form-item>
        <el-form-item :label="$t('views.iot.bridge.index.525282-53')" prop="dataBaseName">
          <el-input
            v-model="form.dbform.dataBaseName"
            :placeholder="$t('views.iot.bridge.index.525282-54')"
            style="width: 400px"
          />
        </el-form-item>
        <el-form-item label="SQL" prop="sql">
          <el-input
            v-model="form.dbform.sql"
            type="textarea"
            :placeholder="$t('views.iot.bridge.index.525282-55')"
            :rows="3"
            style="width: 400px"
            :autosize="{ minRows: 3, maxRows: 5 }"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <div class="dialog-footer">
          <el-button type="success" @click="test" v-show="testshow">
            {{ $t('views.iot.bridge.index.525282-56') }}
          </el-button>
          <el-button @click="submitForm" type="primary">{{ $t('views.iot.bridge.index.525282-57') }}</el-button>
          <el-button @click="cancel">{{ $t('views.iot.bridge.index.525282-58') }}</el-button>
        </div>
      </template>
      <div
        v-show="isShown"
        style="
          border: 1px solid #ccc;
          border-radius: 5px;
          height: 150px;
          background-color: #eef3f7;
          padding: 0 5px;
          line-height: 20px;
          overflow: auto;
        "
        v-loading="bridgeloading"
      >
        <pre>
          {{ $t('views.iot.bridge.index.525282-97') }}
          {{ response }}
        </pre>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, getCurrentInstance, onMounted, watch, nextTick } from 'vue';
import {
  Search,
  Refresh,
  Plus,
  Edit,
  Delete,
  Download,
  Sort,
  QuestionFilled,
  ArrowDown,
} from '@element-plus/icons-vue';
import { listBridge, getBridge, delBridge, addBridge, updateBridge, connectBridge } from '@/api/iot/bridge';
import { checkPermi } from '@/utils/permission';
import { useDict } from '@/utils/dict/useDict';

const { mqtt_version, bridge_entry, bridging_direction, bridge_type, bridge_status, database_type } = useDict(
  'mqtt_version',
  'bridge_entry',
  'bridging_direction',
  'bridge_type',
  'bridge_status',
  'database_type'
);
const { proxy } = getCurrentInstance() as any;

const loading = ref(true);
const ids = ref<any[]>([]);
const single = ref(true);
const multiple = ref(true);
const showSearch = ref(true);
const total = ref(0);
const bridgeList = ref<any[]>([]);
const title = ref('');
const open = ref(false);
const isDisabled = ref(false);
const isExpanded = ref(false);
const isShown = ref(false);
const bridgeloading = ref(false);
const response = ref<any>(null);
const testshow = ref(false);
const isShowHeader = ref(false);
const isShowParams = ref(false);
const isShowConfig = ref(false);
const hostUrlhead = ref('http://');
const multipleTableRef = ref<any>(null);
const bridgeFormRef = ref<any>(null);
const httpFormRef = ref<any>(null);
const mqttFormRef = ref<any>(null);
const dbFormRef = ref<any>(null);
const queryRef = ref<any>(null);

const requestHeadersMap = ref<any[]>([{ key: '', value: '' }]);
const requestQuerysMap = ref<any[]>([{ key: '', value: '' }]);
const requestConfigMap = ref<any[]>([{ key: '', value: '' }]);

const options = ['POST', 'PUT', 'GET'];
const urlOptions = ['http://', 'https://'];
const dbOptions = ['MySQL', 'SQLServer', 'Oracle', 'PostgreSQL'];

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  name: null as any,
  enable: null as any,
  status: null as any,
  type: null as any,
  direction: 1,
});

const createBridgeForm = () => ({
  enable: 1,
  direction: 1,
});

const createHttpForm = () => ({
  name: 'Http推送',
  method: 'POST',
  hostUrl: null,
  hostUrlbody: '',
  route: null,
  requestHeaders: '',
  requestQuerys: '',
  requestConfig: '',
});

const createMqttForm = () => ({
  name: 'Mqtt桥接',
  hostUrl: null,
  hostUrlbody: '',
  clientId: null,
  username: null,
  password: null,
  route: null,
  version: 0,
  keepalive: null,
  timeout: null,
  automaticReconnect: false,
  cleanSession: false,
});

const createDbForm = () => ({
  name: '数据库存储',
  type: null,
  databaseSource: null,
  port: null,
  dataBaseName: null,
  sql: null,
  host: null,
  username: null,
  password: null,
});

const resetReactiveObject = (target: Record<string, any>, source: Record<string, any>) => {
  Object.keys(target).forEach((key) => {
    if (!(key in source)) delete target[key];
  });
  Object.assign(target, source);
};

const form = reactive<any>({
  bridge: createBridgeForm(),
  httpform: createHttpForm(),
  mqttform: createMqttForm(),
  dbform: createDbForm(),
});

const rules = reactive<any>({
  name: [{ required: true, message: proxy.$t('views.iot.bridge.index.525282-61'), trigger: 'blur' }],
  type: [{ required: true, message: proxy.$t('views.iot.bridge.index.525282-62'), trigger: 'change' }],
  direction: [{ required: true, message: proxy.$t('views.iot.bridge.index.525282-63'), trigger: 'blur' }],
});

const httprules = reactive<any>({
  method: [{ required: true, message: proxy.$t('views.iot.bridge.index.525282-64'), trigger: 'blur' }],
  hostUrlbody: [{ required: true, message: proxy.$t('views.iot.bridge.index.525282-65'), trigger: 'blur' }],
  route: [{ required: true, message: proxy.$t('views.iot.bridge.index.525282-65'), trigger: 'blur' }],
});

const mqttrules = reactive<any>({
  hostUrlbody: [
    { required: true, message: proxy.$t('views.iot.bridge.index.525282-65'), trigger: 'blur' },
    { pattern: /^[\w.-]+:\d+$/, message: proxy.$t('views.iot.bridge.index.525282-69'), trigger: 'blur' },
  ],
  clientId: [{ required: true, message: proxy.$t('views.iot.bridge.index.525282-70'), trigger: 'blur' }],
  username: [{ required: true, message: proxy.$t('views.iot.bridge.index.525282-71'), trigger: 'blur' }],
  password: [{ required: true, message: proxy.$t('views.iot.bridge.index.525282-72'), trigger: 'blur' }],
  route: [{ required: true, message: proxy.$t('views.iot.bridge.index.525282-101'), trigger: 'blur' }],
});

const dbrules = reactive<any>({
  databaseSource: [{ required: true, message: proxy.$t('views.iot.bridge.index.525282-74'), trigger: 'blur' }],
  type: [{ required: true, message: proxy.$t('views.iot.bridge.index.525282-75'), trigger: 'blur' }],
  dataBaseName: [{ required: true, message: proxy.$t('views.iot.bridge.index.525282-77'), trigger: 'blur' }],
  sql: [{ required: true, message: proxy.$t('views.iot.bridge.index.525282-78'), trigger: 'blur' }],
  host: [
    { required: true, message: proxy.$t('views.iot.bridge.index.525282-79'), trigger: 'blur' },
    { pattern: /^[\w.-]+:\d+$/, message: proxy.$t('views.iot.bridge.index.525282-80'), trigger: 'blur' },
  ],
  username: [{ required: true, message: proxy.$t('views.iot.bridge.index.525282-71'), trigger: 'blur' }],
  password: [{ required: true, message: proxy.$t('views.iot.bridge.index.525282-72'), trigger: 'blur' }],
});

watch(
  () => form.bridge,
  (newVal) => {
    if (newVal.direction === 1) {
      testshow.value = newVal.type !== 3 && newVal.type !== '3';
    } else {
      testshow.value = true;
    }
    if (newVal.type === '3' && newVal.direction === 1) testshow.value = false;
    nextTick(() => {
      if (newVal.direction === 1) {
        httprules.method = [];
        httprules.hostUrlbody = [];
        httprules.route = [{ required: true, message: proxy.$t('views.iot.bridge.index.525282-65'), trigger: 'blur' }];
        if (mqttFormRef.value) mqttFormRef.value.clearValidate('route');
        mqttrules.route = [{ required: true, message: proxy.$t('views.iot.bridge.index.525282-101'), trigger: 'blur' }];
      } else {
        if (mqttFormRef.value) mqttFormRef.value.clearValidate('route');
        mqttrules.route = [];
        httprules.method = [{ required: true, message: proxy.$t('views.iot.bridge.index.525282-64'), trigger: 'blur' }];
        httprules.hostUrlbody = [
          { required: true, message: proxy.$t('views.iot.bridge.index.525282-65'), trigger: 'blur' },
        ];
        httprules.route = [];
      }
    });
  },
  { deep: true }
);

function getList() {
  loading.value = true;
  listBridge(queryParams).then((res: any) => {
    bridgeList.value = res.rows;
    bridgeList.value.forEach((item: any) => {
      if (item.type === 4) {
        item.shows = true;
      } else if (item.type === 3 || item.type === 5) {
        item.shows = item.direction == 2;
      } else {
        item.shows = false;
      }
    });
    total.value = res.total;
    loading.value = false;
  });
}

function cancel() {
  open.value = false;
  resetBridge();
}

function resetBridge() {
  resetReactiveObject(form.bridge, {
    id: null,
    name: null,
    status: null,
    type: null,
    route: null,
    delFlag: null,
    createBy: null,
    createTime: null,
    updateBy: null,
    updateTime: null,
    remark: null,
    ...createBridgeForm(),
  });
  resetReactiveObject(form.httpform, createHttpForm());
  resetReactiveObject(form.mqttform, createMqttForm());
  resetReactiveObject(form.dbform, createDbForm());
  hostUrlhead.value = 'http://';
  isExpanded.value = false;
  isShown.value = false;
  testshow.value = false;
  isShowHeader.value = false;
  isShowParams.value = false;
  isShowConfig.value = false;
  requestHeadersMap.value = [];
  requestQuerysMap.value = [];
  requestConfigMap.value = [];
  response.value = null;
  bridgeFormRef.value?.clearValidate();
  httpFormRef.value?.clearValidate();
  mqttFormRef.value?.clearValidate();
  dbFormRef.value?.clearValidate();
}

function resetQueryForm() {
  form.httpform = { id: null, name: null, method: null, hostUrl: null };
  form.mqttform = {
    id: null,
    name: null,
    hostUrl: null,
    clientId: null,
    username: null,
    password: null,
    version: 0,
    keepalive: null,
    timeout: null,
    automaticReconnect: false,
    cleanSession: false,
  };
  form.dbform = {
    id: null,
    name: null,
    type: null,
    databaseSource: null,
    port: null,
    dataBaseName: null,
    sql: null,
    host: null,
    username: null,
    password: null,
  };
}

function handleQuery() {
  queryParams.pageNum = 1;
  getList();
}
function resetQuery() {
  queryRef.value?.resetFields();
  handleQuery();
}

function handleSelectionChange(selection: any[]) {
  ids.value = selection.map((item) => item.id);
  single.value = selection.length !== 1;
  multiple.value = !selection.length;
}

function handleAdd() {
  resetBridge();
  open.value = true;
  title.value = proxy.$t('views.iot.bridge.index.525282-86');
  form.bridge.type = 3;
  testshow.value = false;
}

function handleCommect(row: any) {
  row.isConnect = true;
  connectBridge(row)
    .then((res: any) => {
      if (res.code == 200) {
        getList();
        proxy.$modal.msgSuccess(proxy.$t('views.iot.bridge.index.525282-87'));
      }
      row.isConnect = false;
    })
    .catch(() => {
      row.isConnect = false;
    });
}

function handleUpdate(row?: any) {
  resetBridge();
  const id = row?.id || ids.value;
  getBridge(id).then((res: any) => {
    if (res.data.type == 3) {
      resetReactiveObject(form.bridge, res.data);
      resetReactiveObject(form.httpform, JSON.parse(res.data.configJson));
      open.value = true;
      title.value = proxy.$t('views.iot.bridge.index.525282-89');
      if (form.httpform.hostUrl) {
        const parts = form.httpform.hostUrl.split('://');
        if (parts.length === 2) {
          hostUrlhead.value = parts[0] + '://';
          form.httpform.hostUrlbody = parts[1];
        }
      }
      isShowHeader.value = true;
      isShowConfig.value = true;
      isShowParams.value = true;
      requestHeadersMap.value = [];
      if (form.httpform.requestHeaders) {
        const rh = JSON.parse(form.httpform.requestHeaders);
        for (let key in rh) {
          if (rh.hasOwnProperty(key)) requestHeadersMap.value.push({ key, value: rh[key] });
        }
      }
      requestQuerysMap.value = [];
      if (form.httpform.requestQuerys) {
        const rq = JSON.parse(form.httpform.requestQuerys);
        for (let key in rq) {
          if (rq.hasOwnProperty(key)) requestQuerysMap.value.push({ key, value: rq[key] });
        }
      }
      requestConfigMap.value = [];
      if (form.httpform.requestConfig) {
        const rc = JSON.parse(form.httpform.requestConfig);
        for (let key in rc) {
          if (rc.hasOwnProperty(key)) requestConfigMap.value.push({ key, value: rc[key] });
        }
      }
    }
    if (res.data.type == 4) {
      resetReactiveObject(form.bridge, res.data);
      resetReactiveObject(form.mqttform, JSON.parse(res.data.configJson));
      if (form.mqttform.hostUrl) {
        const parts = form.mqttform.hostUrl.split('://');
        if (parts.length == 2) form.mqttform.hostUrlbody = parts[1];
      }
      open.value = true;
      title.value = proxy.$t('views.iot.bridge.index.525282-89');
    }
    if (res.data.type == 5) {
      resetReactiveObject(form.bridge, res.data);
      resetReactiveObject(form.dbform, JSON.parse(res.data.configJson));
      open.value = true;
      title.value = proxy.$t('views.iot.bridge.index.525282-89');
    }
  });
}

function getConfigJson() {
  if (form.bridge.type == 3) {
    form.httpform.name = form.bridge.name;
    form.httpform.hostUrl = hostUrlhead.value + form.httpform.hostUrlbody;
    if (requestHeadersMap.value.length)
      form.httpform.requestHeaders = JSON.stringify(
        Object.fromEntries(requestHeadersMap.value.map((i: any) => [i.key, i.value]))
      );
    if (requestQuerysMap.value.length)
      form.httpform.requestQuerys = JSON.stringify(
        Object.fromEntries(requestQuerysMap.value.map((i: any) => [i.key, i.value]))
      );
    if (requestConfigMap.value.length)
      form.httpform.requestConfig = JSON.stringify(
        Object.fromEntries(requestConfigMap.value.map((i: any) => [i.key, i.value]))
      );
    form.bridge.configJson = JSON.stringify(form.httpform);
    form.bridge.route = form.httpform.route;
  }
  if (form.bridge.type == 4) {
    form.mqttform.name = form.bridge.name;
    form.mqttform.hostUrl = 'tcp://' + form.mqttform.hostUrlbody;
    form.bridge.configJson = JSON.stringify(form.mqttform);
    form.bridge.route = form.mqttform.route;
  }
  if (form.bridge.type == 5) {
    form.dbform.name = form.bridge.name;
    form.bridge.configJson = JSON.stringify(form.dbform);
  }
}

function test() {
  bridgeFormRef.value.validate((valid: boolean) => {
    if (!valid) return;
    const formRef = form.bridge.type == 3 ? httpFormRef : form.bridge.type == 4 ? mqttFormRef : dbFormRef;
    formRef.value.validate((v: boolean) => {
      if (v) {
        bridgeloading.value = true;
        isShown.value = true;
        getConfigJson();
        testConnect();
      }
    });
  });
}

function testConnect() {
  connectBridge(form.bridge)
    .then((res: any) => {
      bridgeloading.value = false;
      response.value = res;
      if (res.code == 200) proxy.$modal.msgSuccess(proxy.$t('views.iot.bridge.index.525282-90'));
      else if (res.code == 500) proxy.$modal.msgWarning(proxy.$t('views.iot.bridge.index.525282-91'));
      getList();
    })
    .catch(() => {
      bridgeloading.value = false;
      response.value = proxy.$t('views.iot.bridge.index.525282-92');
    });
}

function submitForm() {
  bridgeFormRef.value.validate((valid: boolean) => {
    if (!valid) return;
    const formRef = form.bridge.type == 3 ? httpFormRef : form.bridge.type == 4 ? mqttFormRef : dbFormRef;
    formRef.value.validate((v: boolean) => {
      if (v) addOrUpdate();
    });
  });
}

function addOrUpdate() {
  getConfigJson();
  if (form.bridge.id != null) {
    updateBridge(form.bridge).then(() => {
      proxy.$modal.msgSuccess(proxy.$t('views.iot.bridge.index.525282-93'));
      open.value = false;
      getList();
    });
  } else {
    addBridge(form.bridge).then(() => {
      proxy.$modal.msgSuccess(proxy.$t('views.iot.bridge.index.525282-94'));
      open.value = false;
      getList();
    });
  }
}

function clearconfig() {
  isShown.value = false;
  response.value = null;
  resetQueryForm();
}

async function handleEnableChange(row: any) {
  let hasPermission = checkPermi(['iot:bridge:edit']);
  if (!hasPermission) {
    proxy.$modal.alertError(proxy.$t('product.index.091251-31'));
    return;
  }
  isDisabled.value = true;
  setTimeout(() => {
    isDisabled.value = false;
  }, 1000);
  const id = row.id || ids.value;
  getBridge(id).then((res: any) => {
    if (res.code === 200) {
      const data = res.data;
      data.enable = data.enable == 1 ? 0 : 1;
      updateBridge(data).then((r: any) => {
        if (r.code === 200) {
          getList();
          proxy.$modal.msgSuccess(proxy.$t('views.iot.bridge.index.525282-93'));
        }
      });
    }
  });
}

function handleDelete(row?: any) {
  const deleteIds = row?.id || ids.value;
  proxy.$modal
    .confirm(proxy.$t('views.iot.bridge.index.525282-96', [deleteIds]))
    .then(() => {
      return delBridge(deleteIds);
    })
    .then(() => {
      getList();
      proxy.$modal.msgSuccess(proxy.$t('views.iot.bridge.index.525282-95'));
      multipleTableRef.value.clearSelection();
    })
    .catch(() => {});
}

function handleExport() {
  proxy.download('iot/bridge/export', { ...queryParams }, `bridge_${new Date().getTime()}.xlsx`);
}
function handleAddAction() {
  isShowHeader.value = true;
  requestHeadersMap.value.push({ key: '', value: '' });
}
function handleAddQuerys() {
  isShowParams.value = true;
  requestQuerysMap.value.push({ key: '', value: '' });
}
function handleAddConfig() {
  isShowConfig.value = true;
  requestConfigMap.value.push({ key: '', value: '' });
}
function handleRemoveAction(i: number) {
  requestHeadersMap.value.splice(i, 1);
}
function handleRemoveQuerys(i: number) {
  requestQuerysMap.value.splice(i, 1);
}
function handleRemoveConfig(i: number) {
  requestConfigMap.value.splice(i, 1);
}
function getRowKeys(row: any) {
  return row.id;
}

onMounted(() => {
  getList();
});
</script>

<style lang="scss" scoped>
.iot-bridge {
  padding: 20px;
  .search-card {
    margin-bottom: 15px;
    padding: 3px 0;
  }
  .search-form {
    margin-bottom: -22.5px;
  }
}
.advanced-header {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 10px 0;
  color: #409eff;
  border-bottom: 1px solid #eee;
  margin-left: 30px;
  i,
  .el-icon {
    margin-left: 8px;
    transition: transform 0.3s;
  }
}
.rotate-180 {
  transform: rotate(180deg);
}
</style>
