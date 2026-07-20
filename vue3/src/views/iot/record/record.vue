<template>
  <div class="iot-record">
    <div v-if="!isRecordDetail">
      <el-card v-show="showSearch" style="margin-bottom: 15px; width: 100%">
        <el-form
          ref="queryFormRef"
          :inline="true"
          label-width="88px"
          style="margin-bottom: -18px; padding: 3px 0 0 0"
          @submit.prevent
        >
          <el-form-item prop="serverId">
            <el-select
              @change="handleMediaChange"
              v-model="mediaServerId"
              :placeholder="$t('record.239091-1')"
              :disabled="isRecordDetail"
              style="width: 192px"
            >
              <el-option
                v-for="item in mediaServerList"
                :key="item.serverId"
                :label="item.serverId"
                :value="item.serverId"
              ></el-option>
            </el-select>
          </el-form-item>

          <div style="float: right">
            <el-button v-if="!isRecordDetail" :icon="Search" :loading="loading" type="primary" @click="getRecordList()">
              {{ $t('search') }}
            </el-button>
          </div>
        </el-form>
      </el-card>
      <el-card>
        <el-row :gutter="10" style="margin-bottom: 15px">
          <right-toolbar v-model:showSearch="showSearch" @queryTable="getRecordList"></right-toolbar>
        </el-row>

        <el-table :data="recordList" :border="false">
          <el-table-column prop="app" :label="$t('record.239091-2')" align="left" min-width="150"></el-table-column>
          <el-table-column prop="stream" :label="$t('record.239091-3')" align="left" min-width="460"></el-table-column>
          <el-table-column prop="time" :label="$t('record.239091-4')" align="center" width="180"></el-table-column>
          <el-table-column :label="$t('opation')" width="100" fixed="right" align="center">
            <template #default="scope">
              <el-button
                size="small"
                :icon="FolderOpened"
                link
                @click="handleRecordDetailClick(scope.row)"
                v-hasPermi="['iot:sip:record:query']"
              >
                {{ $t('look') }}
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        <pagination
          style="margin-bottom: 20px"
          v-show="total > 0"
          :total="total"
          v-model:page="queryRecord.pageNum"
          v-model:limit="queryRecord.pageSize"
          @pagination="getRecordList"
        />
      </el-card>
    </div>
    <record-detail
      ref="cloudRecordDetailRef"
      v-if="isRecordDetail"
      :recordFile="chooseRecord"
      :mediaServerId="mediaServerId"
      :recordApi="recordApi"
      @back="handleBack"
    ></record-detail>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, getCurrentInstance } from 'vue';
import { useI18n } from 'vue-i18n';
import { Search, FolderOpened } from '@element-plus/icons-vue';
import { listmediaServer } from '@/api/iot/media/mediaServer';
import { getServerRecord } from '@/api/iot/media/record';
import recordDetail from '@/views/iot/record/record-detail.vue';

const { proxy } = getCurrentInstance() as any;
const { t } = useI18n();

const queryFormRef = ref();
const cloudRecordDetailRef = ref();

const showSearch = ref(true);
const mediaServerList = ref<any[]>([]);
const mediaServerId = ref<any>(null);
const recordApi = ref<any>(null);
const recordList = ref<any[]>([]);
const chooseRecord = ref<any>(null);
const total = ref(0);
const loading = ref(false);
const isRecordDetail = ref(false);

const queryRecord = reactive({
  pageNum: 1,
  pageSize: 10,
  recordApi: null as any,
});

/** 获取媒体节点列表 */
function getMediaServerList() {
  const params = { pageNum: 1, pageSize: 9999 };
  listmediaServer(params).then((res: any) => {
    if (res.code === 200) {
      mediaServerList.value = res.rows;
    }
  });
}

/** 获取录像列表 */
function getRecordList() {
  if (!recordApi.value) {
    proxy.$modal.alertError(t('record.239091-5'));
    return;
  }
  loading.value = true;
  queryRecord.recordApi = recordApi.value;
  getServerRecord(queryRecord)
    .then((res: any) => {
      if (res.code === 200) {
        recordList.value = res.data.list;
        total.value = res.data.total;
      }
      loading.value = false;
    })
    .catch(() => {
      loading.value = false;
    });
}

/** 选择标识符 */
function handleMediaChange(val: any) {
  total.value = 0;
  recordList.value = [];
  getRecordApiUrl(val);
  getRecordList();
}

/** 生成api地址 */
function getRecordApiUrl(serverId: any) {
  const item = mediaServerList.value.find((m: any) => m.serverId === serverId);
  if (!item) return;
  if (item.protocol === 'http') {
    recordApi.value = 'http://' + item.ip + ':' + item.recordPort;
  } else if (item.protocol === 'https') {
    recordApi.value = 'https://' + item.domainAlias + ':' + item.recordPort;
  }
}

/** 查看详情 */
function handleRecordDetailClick(row: any) {
  isRecordDetail.value = true;
  chooseRecord.value = row;
}

/** 返回列表 */
function handleBack() {
  isRecordDetail.value = false;
}

onMounted(() => {
  getMediaServerList();
});
</script>

<style lang="scss" scoped>
.iot-record {
  padding: 20px;
}
</style>
