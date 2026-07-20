<template>
  <div class="record-oss">
    <el-row>
      <el-col :span="6">
        <div class="left-wrap">
          <div class="search-wrap">
            <el-select
              v-model="channelId"
              :placeholder="$t('record.record-oss.80878-3')"
              @change="changeChannel"
              style="width: 100%"
            >
              <el-option
                v-for="option in channelList"
                :key="option.value"
                :label="option.label"
                :value="option.value"
              ></el-option>
            </el-select>
            <el-date-picker
              style="width: 100%"
              v-model="chooseDate"
              type="date"
              value-format="YYYY-MM-DD"
              :placeholder="$t('record.record-oss.80878-0')"
              @change="changeDate()"
            ></el-date-picker>
          </div>
          <div class="node-data">
            <ul v-if="detailFiles.length > 0">
              <li v-for="(item, index) in detailFiles" :key="index">
                <el-tag v-if="choosedFile != item" @click="chooseFile(item)">
                  <el-icon><VideoCamera /></el-icon>
                  {{ item.substring(0, 17) }}
                </el-tag>
                <el-tag type="danger" v-if="choosedFile == item">
                  <el-icon><VideoCamera /></el-icon>
                  {{ item.substring(0, 17) }}
                </el-tag>
                <a
                  class="icon"
                  :href="`${getFileBasePath()}/download.html?url=file/download/rtp/${stream}/${chooseDate}/${item}`"
                  target="_blank"
                >
                  <el-icon><Download /></el-icon>
                </a>
                <a class="icon" @click="uploadOss(item)">
                  <el-icon><Upload /></el-icon>
                </a>
              </li>
            </ul>
            <el-empty
              v-if="detailFiles.length == 0"
              class="no-data"
              :image-size="100"
              :description="$t('sip.mediaServer.998535-6')"
            ></el-empty>
          </div>
          <pagination
            style="bottom: 63px; right: 10px"
            v-show="total > queryParams.pageSize"
            :total="total"
            v-model:page="queryParams.pageNum"
            v-model:limit="queryParams.pageSize"
            small
            layout="prev, pager, next"
            @pagination="queryRecordDetails"
            :pager-count="5"
            v-if="chooseDate !== null"
          />
        </div>
      </el-col>
      <el-col :span="18">
        <div class="right-wrap">
          <!-- @ts-ignore -->
          <!--                    <player ref="recordVideoPlayer" :videoUrl="videoUrl" :height="true"></player>-->
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, watch, onMounted, getCurrentInstance, type ComponentInternalInstance } from 'vue';
import { VideoCamera, Download, Upload } from '@element-plus/icons-vue';
import { getServerRecordByDevice, uploadRecord } from '@/api/iot/media/record';
import { listComChannel } from '@/api/iot/media/channel';
// @ts-ignore
// import player from '@/components/player';

const { proxy } = getCurrentInstance() as any;

const props = defineProps<{
  device: any;
}>();

const detailFiles = ref<any[]>([]);
const chooseDate = ref<string>('');
const videoUrl = ref<string | null>(null);
const choosedFile = ref<string | null>(null);
const deviceId = ref('');
const channelId = ref('');
const channelList = ref<any[]>([]);
const stream = ref('');
const total = ref(0);
const recordApi = ref('');
const queryParams = reactive({
  pageNum: 1,
  pageSize: 12,
  deviceId: null as string | null,
});

watch(
  () => props.device,
  (newVal) => {
    if (newVal && newVal.deviceId !== 0) {
      deviceId.value = newVal.serialNumber;
    }
  }
);

onMounted(() => {
  deviceId.value = props.device.serialNumber;
  getList();
});

/** 查询监控设备通道信息列表 */
function getList() {
  queryParams.deviceId = deviceId.value;
  listComChannel(queryParams).then((response: any) => {
    channelList.value = response.rows.map((item: any) => {
      return { value: item.channelId, label: item.channelName };
    });
  });
}

function changeDate() {
  detailFiles.value = [];
  if (channelId.value !== '') {
    queryRecordDetails();
  }
}

function changeChannel() {
  detailFiles.value = [];
  stream.value = 'gb_playrecord_' + deviceId.value + '_' + channelId.value;
  if (chooseDate.value !== '') {
    queryRecordDetails();
  }
}

function queryRecordDetails() {
  const query = {
    deviceId: deviceId.value,
    channelId: channelId.value,
    startTime: chooseDate.value + ' 00:00:00',
    endTime: chooseDate.value + ' 23:59:59',
    pageNum: queryParams.pageNum,
    pageSize: queryParams.pageSize,
  };
  getServerRecordByDevice(query).then((res: any) => {
    if (res.code === 200) {
      total.value = res.data.total;
      detailFiles.value = res.data.list;
      recordApi.value = res.data.recordApi;
    }
  });
}

function chooseFile(file: string) {
  choosedFile.value = file;
  if (file == null) {
    videoUrl.value = '';
  } else {
    videoUrl.value = `${getFileBasePath()}/file/download/rtp/${stream.value}/${chooseDate.value}/${choosedFile.value}`;
  }
}

function uploadOss(item: string) {
  const query = {
    recordApi: recordApi.value,
    file: '/rtp/' + stream.value + '/' + chooseDate.value + '/' + item,
  };
  uploadRecord(query).then((res: any) => {
    if (res.code === 200) {
      proxy?.$modal.msgSuccess(proxy?.$t('record.record-oss.80878-4'));
    }
  });
}

function getFileBasePath() {
  return recordApi.value;
}
</script>

<style>
.data-picker-true:after {
  content: '';
  position: absolute;
  width: 4px;
  height: 4px;
  background-color: #606060;
  border-radius: 4px;
  left: 45%;
  top: 74%;
}
</style>
<style lang="scss" scoped>
.record-oss {
  .left-wrap {
    position: relative;
    flex-direction: column;
    width: 100%;
    padding-right: 20px;

    .search-wrap {
      display: flex;
      flex-direction: row;
      justify-content: space-between;
      align-items: center;
      gap: 6px;
    }

    .no-data {
      position: absolute;
      left: 50%;
      top: 50%;
      transform: translate(-55%, -52%);
      height: 517px;
    }

    .node-data {
      color: #486ff2;
      border-radius: 4px;
      border: 1px solid #dcdfe6;
      margin-top: 10px;
      padding: 10px;
      height: 517px;

      ul {
        list-style-type: none;
        padding: 0;
        margin: 0;

        li {
          margin-bottom: 10px;
          cursor: pointer;

          .icon {
            margin-left: 10px;
          }
        }
      }
    }
  }

  .right-wrap {
    height: 559px;
    width: 100%;
  }
}
</style>
