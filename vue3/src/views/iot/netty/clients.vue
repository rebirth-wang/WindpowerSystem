<template>
  <div class="iot-netty-clients">
    <el-card v-show="showSearch" class="search-card">
      <el-form
        @submit.prevent
        :model="queryParams"
        class="search-form"
        ref="queryRef"
        :inline="true"
        label-width="58px"
      >
        <el-form-item prop="clientId">
          <el-input
            v-model="queryParams.clientId"
            :placeholder="$t('netty.clients.654908-1')"
            clearable
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
      <el-row :gutter="10" style="margin-bottom: 10px">
        <el-col :span="1.5" style="line-height: 32px">
          <el-checkbox v-model="queryParams.isClient" true-value="1" false-value="0" @change="handleQuery">
            {{ $t('netty.clients.654908-2') }}
          </el-checkbox>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
      </el-row>
      <el-tabs v-model="serverType" @tab-click="handleClick" style="flex: 1; min-height: 720px; margin-bottom: 5px">
        <el-tab-pane :label="$t('netty.clients.654908-3')" name="MQTT">
          <el-table v-loading="loading" :data="clientList" :border="false">
            <el-table-column :label="$t('netty.clients.654908-4')" align="left" prop="clientId" min-width="180">
              <template #default="scope">
                <el-link
                  underline="never"
                  type="primary"
                  @click="handleOpen(scope.row)"
                  style="color: #000; font-weight: bold"
                >
                  {{ scope.row.clientId }}
                </el-link>
              </template>
            </el-table-column>
            <el-table-column :label="$t('netty.clients.654908-5')" align="center" prop="type" width="100">
              <template #default="scope">
                <el-tag type="danger" v-if="scope.row.clientId.indexOf('server') == 0">
                  {{ $t('netty.clients.654908-6') }}
                </el-tag>
                <el-tag type="success" v-else-if="scope.row.clientId.indexOf('web') == 0">
                  {{ $t('netty.clients.654908-7') }}
                </el-tag>
                <el-tag type="warning" v-else-if="scope.row.clientId.indexOf('phone') == 0">
                  {{ $t('netty.clients.654908-8') }}
                </el-tag>
                <el-tag type="info" v-else-if="scope.row.clientId.indexOf('test') == 0">
                  {{ $t('netty.clients.654908-9') }}
                </el-tag>
                <el-tag type="primary" v-else>{{ $t('netty.clients.654908-10') }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column :label="$t('netty.clients.654908-11')" align="center" prop="connected" width="100">
              <template #default="scope">
                <el-tag type="success" v-if="scope.row.connected">{{ $t('netty.clients.654908-12') }}</el-tag>
                <el-tag type="info" v-else>{{ $t('netty.clients.654908-13') }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column :label="$t('netty.clients.654908-14')" align="center" prop="keepAlive" width="100" />
            <el-table-column :label="$t('netty.clients.654908-15')" align="center" prop="username" width="100px" />
            <el-table-column :label="$t('netty.clients.654908-16')" align="center" prop="topicCount" width="110" />
            <el-table-column :label="$t('netty.clients.654908-17')" align="center" prop="connected_at" width="180" />
            <el-table-column fixed="right" :label="$t('opation')" align="center" width="80">
              <template #default="scope">
                <el-button
                  link
                  style="color: #ed2525"
                  size="small"
                  @click="clickClientOut(scope.row)"
                  v-hasPermi="['iot:emqx:client:remove']"
                >
                  <svg-icon icon-class="logout" />
                  <span style="margin-left: 5px">{{ $t('netty.clients.654908-18') }}</span>
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
          <el-dialog :title="title" v-model="open" width="800px" append-to-body>
            <el-tabs v-model="activeName" tab-position="top" style="padding: 10px">
              <el-tab-pane name="subscribe" :label="$t('netty.clients.654908-19')">
                <el-table :data="subscribeList" size="small">
                  <el-table-column :label="$t('netty.clients.654908-20')" align="center" prop="topicName" />
                  <el-table-column label="QoS" align="center" prop="qos" />
                </el-table>
              </el-tab-pane>
            </el-tabs>
          </el-dialog>
        </el-tab-pane>
        <el-tab-pane :label="$t('netty.clients.654908-21')" name="TCP">
          <el-table v-loading="loading" :data="clientList" :border="false">
            <el-table-column :label="$t('netty.clients.654908-4')" align="left" prop="clientId" min-width="180" />
            <el-table-column :label="$t('netty.clients.654908-5')" align="center" prop="type" width="100">
              <template #default="scope">
                <el-tag type="danger" v-if="scope.row.clientId.indexOf('server') == 0">
                  {{ $t('netty.clients.654908-6') }}
                </el-tag>
                <el-tag type="success" v-else-if="scope.row.clientId.indexOf('web') == 0">
                  {{ $t('netty.clients.654908-7') }}
                </el-tag>
                <el-tag type="warning" v-else-if="scope.row.clientId.indexOf('phone') == 0">
                  {{ $t('netty.clients.654908-8') }}
                </el-tag>
                <el-tag type="info" v-else-if="scope.row.clientId.indexOf('test') == 0">
                  {{ $t('netty.clients.654908-9') }}
                </el-tag>
                <el-tag type="primary" v-else>{{ $t('netty.clients.654908-10') }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column :label="$t('netty.clients.654908-11')" align="center" prop="connected" width="100">
              <template #default="scope">
                <dict-tag :options="client_connection_status" :value="normalizeConnectionStatus(scope.row.connected)" />
              </template>
            </el-table-column>
            <el-table-column :label="$t('netty.clients.654908-14')" align="center" prop="keepAlive" width="100" />
            <el-table-column :label="$t('netty.clients.654908-17')" align="center" prop="connected_at" width="180" />
            <el-table-column fixed="right" :label="$t('opation')" align="center" width="80">
              <template #default="scope">
                <el-button
                  link
                  style="color: #ed2525"
                  size="small"
                  @click="clickClientOut(scope.row)"
                  v-hasPermi="['iot:emqx:client:remove']"
                >
                  <svg-icon icon-class="logout" />
                  <span style="margin-left: 5px">{{ $t('netty.clients.654908-18') }}</span>
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
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, getCurrentInstance, onMounted } from 'vue';
import { Search, Refresh } from '@element-plus/icons-vue';
import { clientOut, listNettyMqttClient } from '@/api/iot/netty';
import { useDict } from '@/utils/dict/useDict';

const { client_connection_status } = useDict('client_connection_status');
const { proxy } = getCurrentInstance() as any;

const loading = ref(true);
const showSearch = ref(true);
const total = ref(0);
const clientList = ref<any[]>([]);
const title = ref('');
const open = ref(false);
const activeName = ref('subscribe');
const subscribeList = ref<any[]>([]);
const serverType = ref('MQTT');
const queryRef = ref<any>(null);

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  clientId: null as any,
  isClient: '0',
  serverCode: 'MQTT',
});

function getList() {
  loading.value = true;
  listNettyMqttClient(queryParams).then((response: any) => {
    clientList.value = response.data;
    total.value = response.total;
    loading.value = false;
  });
}

function clickClientOut(row: any) {
  const id = row.clientId;
  proxy.$modal
    .confirm(proxy.$t('netty.clients.654908-31', [id]))
    .then(() => {
      return clientOut({ clientId: id });
    })
    .then((res: any) => {
      if (res.code === 200) {
        getList();
        proxy.$modal.msgSuccess(proxy.$t('netty.clients.654908-32'));
      } else proxy.$modal.msgError(proxy.$t('netty.clients.654908-33'));
    })
    .catch(() => {});
}

function handleClick() {
  queryParams.serverCode = serverType.value;
  getList();
}
function handleQuery() {
  queryParams.pageNum = 1;
  getList();
}
function resetQuery() {
  queryRef.value?.resetFields();
  handleQuery();
}

function normalizeConnectionStatus(value: unknown) {
  if (typeof value === 'boolean') {
    return String(value);
  }
  return value == null ? '' : (value as string | number | any[]);
}

function handleOpen(row: any) {
  open.value = true;
  title.value = proxy.$t('detail');
  subscribeList.value = row.topics;
}

onMounted(() => {
  getList();
});

/** 断开客户端连接 */
function handleDelete(row: any) {
  clickClientOut(row);
}
</script>

<style lang="scss" scoped>
.iot-netty-clients {
  padding: 20px;
  .search-card {
    margin-bottom: 15px;
    padding: 3px 0;
  }
  .search-form {
    margin-bottom: -22.5px;
  }
}
</style>
