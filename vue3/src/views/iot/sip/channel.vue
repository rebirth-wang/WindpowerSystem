<template>
  <div class="sip-channel">
    <el-row :gutter="10" class="mb8">
      <right-toolbar v-model:showSearch="showSearch" :search="false" @queryTable="getList" />
    </el-row>
    <el-table v-loading="loading" :data="channelList" :border="false">
      <el-table-column :label="$t('sip.channel.998532-1')" align="left" prop="channelId" min-width="180" />
      <el-table-column :label="$t('sip.channel.998532-3')" align="left" prop="channelName" min-width="150" />
      <el-table-column :label="$t('sip.channel.998532-19')" align="left" prop="manufacture" min-width="130" />
      <el-table-column :label="$t('sip.channel.998532-5')" align="center" prop="status" width="80">
        <template #default="scope">
          <dict-tag :options="sip_gen_status" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column :label="$t('sip.channel.998532-2')" align="center" min-width="120">
        <template #default="scope">
          <el-image
            v-if="isVideoChannel(scope.row)"
            :src="getSnap(scope.row)"
            :preview-src-list="getBigSnap(scope.row)"
            fit="cover"
            style="width: 48px; height: 48px"
          >
            <template #error>
              <div class="image-slot">
                <el-icon><Picture /></el-icon>
              </div>
            </template>
          </el-image>
        </template>
      </el-table-column>
      <el-table-column :label="$t('sip.channel.998532-9')" align="center" prop="streamPush" min-width="100">
        <template #default="scope">
          <el-switch
            v-model="scope.row.streamPush"
            @change="startPushStream(scope.row)"
            :active-value="1"
            :inactive-value="0"
            :disabled="scope.row.status !== 3"
          />
        </template>
      </el-table-column>
      <el-table-column :label="$t('sip.channel.998532-12')" align="center" prop="streamRecord" min-width="110">
        <template #default="scope">
          <el-tag type="info" v-if="scope.row.streamRecord === 0">{{ $t('sip.channel.998532-13') }}</el-tag>
          <el-tag v-if="scope.row.streamRecord === 1">{{ $t('sip.channel.998532-14') }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column
        fixed="right"
        :label="$t('opation')"
        align="center"
        width="300"
        class-name="small-padding fixed-width"
      >
        <template #default="scope">
          <el-tooltip :content="$t('sip.channel.998532-20')" placement="top">
            <el-button
              size="small"
              type="primary"
              link
              :disabled="scope.row.status !== 3 || !scope.row.streamPush"
              @click="handleViewLive(scope.row)"
            >
              <el-icon><VideoPlay /></el-icon>
              {{ $t('sip.channel.998532-24') }}
            </el-button>
          </el-tooltip>
          <el-button size="small" type="primary" link @click="handleViewVideo(scope.row)">
            <el-icon><VideoPause /></el-icon>
            {{ $t('sip.channel.998532-25') }}
          </el-button>
          <el-tooltip :content="$t('sip.channel.998532-21')" placement="top">
            <el-button
              size="small"
              type="primary"
              link
              @click="handleShareLink(scope.row)"
              :disabled="scope.row.status !== 3 || !scope.row.streamPush"
            >
              <el-icon><Promotion /></el-icon>
              {{ $t('sip.channel.998532-26') }}
            </el-button>
          </el-tooltip>
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

    <!-- 直播 -->
    <el-dialog :title="liveTitle" v-model="openLive" width="800px" :before-close="handleCloseLive">
      <div style="position: relative">
        <div style="position: absolute; top: 10px; right: 10px; z-index: 99">
          <el-button
            @click="startPlayRecordAction"
            :disabled="channelId === ''"
            style="
              background: #000;
              color: #f0f0f0;
              border: none;
              border-radius: 2px;
              padding: 5px 10px;
              margin: 0 5px;
              font-size: 14px;
            "
          >
            {{ streamRecord === 1 ? $t('sip.channel.998532-34') : $t('sip.channel.998532-33') }}
          </el-button>
        </div>
        <player
          ref="playerRef"
          :playerinfo="playinfo"
          class="custom-player"
          :deviceId="deviceId"
          :channelId="channelId"
          :type="videoType"
        />
      </div>
    </el-dialog>

    <!-- 录像回放 -->
    <el-dialog :title="videoTitle" v-model="openVideo" width="1100px" :before-close="handleCloseVideo">
      <!-- placeholder: template kept for script logic -->
      <el-row :gutter="0">
        <el-col :span="17">
          <div
            class="player-wrapper"
            v-loading="playerLoading"
            :element-loading-text="$t('sip.channel.998532-38')"
            element-loading-background="rgba(0, 0, 0, 0.7)"
            style="width: 100%; height: 100%; min-height: 400px"
          >
            <player
              ref="playbackerRef"
              :playerinfo="playinfo"
              :videoUrl="videoUrl"
              class="custom-player"
              :type="videoType"
              :streamId="streamId"
              :deviceId="deviceId"
              :channelId="channelId"
              v-if="currentType === 'local'"
            />
            <player ref="playerRef" :playerinfo="playinfo" :videoUrl="videoUrl" class="custom-player" v-else />
          </div>
        </el-col>
        <el-col :span="7" style="padding-left: 0px">
          <div class="record-type-wrapper">
            <el-radio-group v-model="currentType" size="small" @change="handleTypeChange" class="custom-radio-group">
              <el-radio value="local" border class="custom-radio-item">
                <div class="radio-content">
                  <el-icon><FolderOpened /></el-icon>
                  <span class="radio-text">{{ $t('sip.channel.998532-22') }}</span>
                </div>
              </el-radio>
              <el-radio value="cloud" border class="custom-radio-item">
                <div class="radio-content">
                  <el-icon><Upload /></el-icon>
                  <span class="radio-text">{{ $t('sip.channel.998532-23') }}</span>
                </div>
              </el-radio>
            </el-radio-group>
          </div>
          <!-- 服务器选择 -->
          <div style="padding: 0 15px 10px; font-weight: bold" v-if="currentType === 'cloud'">
            <el-select
              @change="handleMediaChange"
              v-model="mediaServerId"
              :placeholder="$t('record.239091-1')"
              style="width: 100%"
              clearable
            >
              <el-option
                v-for="item in mediaServerList"
                :key="item.serverId"
                :label="item.serverId"
                :value="item.serverId"
              />
            </el-select>
          </div>
          <!-- 年月选择 -->
          <div style="padding: 0 15px 10px; display: flex; gap: 10px">
            <el-select v-model="currentYear" @change="handleYearChange">
              <el-option
                v-for="year in [2024, 2025, 2026, 2027, 2028]"
                :key="year"
                :label="`${year}年`"
                :value="year"
              />
            </el-select>
            <el-select v-model="currentMonth" @change="handleMonthChange">
              <el-option v-for="month in 12" :key="month" :label="`${month}月`" :value="month" />
            </el-select>
          </div>
          <el-calendar
            v-model="selectedDate"
            :first-day-of-week="1"
            style="border: 1px solid #e6e6e6; margin: 0 15px"
            @input="handleDateChange"
          >
            <template #date-cell="{ data }">
              <div
                class="calendar-cell"
                :class="{ 'disabled-date': disabledDate(data.date) }"
                @click="handleCellClick(data.date)"
              >
                {{ data.day.split('-').pop() }}
              </div>
            </template>
          </el-calendar>
          <!-- 转存按钮 -->
          <el-button
            type="primary"
            plain
            size="small"
            style="font-size: 14px; margin: 10px 15px"
            @click="selectDownload()"
            v-if="currentType === 'local'"
          >
            <el-icon><Download /></el-icon>
            {{ $t('views.components.player.deviceVideo.808340-12') }}
          </el-button>
          <!-- 云端视频列表 -->
          <div
            class="node-data"
            style="margin: 10px 15px; height: 150px; overflow: auto; border: 1px solid #ccc; padding: 10px"
            v-if="currentType === 'cloud'"
          >
            <ul class="cloud-file-list">
              <li v-for="(item, index) in detailFiles" :key="index" class="cloud-file-item">
                <el-tag v-if="choosedFile !== item" @click="handleChooseFile(item)" class="cloud-file-tag">
                  <span class="cloud-file-tag-content">
                    <el-icon><VideoCamera /></el-icon>
                    <span>{{ item.substring(0, 17) }}</span>
                  </span>
                </el-tag>
                <el-tag type="danger" v-if="choosedFile === item" class="cloud-file-tag">
                  <span class="cloud-file-tag-content">
                    <el-icon><VideoCamera /></el-icon>
                    <span>{{ item.substring(0, 17) }}</span>
                  </span>
                </el-tag>
                <a
                  :href="`${getFileBasePath()}/download.html?url=file/download/rtp/${stream}/${formatDate(selectedDate)}/${item}`"
                  target="_blank"
                  class="cloud-file-action"
                >
                  <el-icon><Download /></el-icon>
                </a>
                <a @click="uploadOss(item)" class="cloud-file-action">
                  <el-icon><Upload /></el-icon>
                </a>
              </li>
            </ul>
            <el-empty
              v-if="detailFiles.length === 0"
              class="no-data"
              :image-size="100"
              :description="$t('sip.mediaServer.998535-6')"
            />
          </div>
          <!-- 分页 -->
          <pagination
            style="bottom: 1px; right: 3px; background: transparent; padding-right: 15px"
            v-show="recordTotal > recordParams.pageSize"
            :total="recordTotal"
            v-model:page="recordParams.pageNum"
            v-model:limit="recordParams.pageSize"
            small
            layout="prev, pager, next"
            @pagination="queryRecordDetails"
            :pager-count="5"
            v-if="currentType === 'cloud'"
          />
        </el-col>
      </el-row>
    </el-dialog>

    <!-- 转存时间选择 -->
    <el-dialog
      :title="$t('views.components.player.deviceVideo.808340-12')"
      v-model="dialogVisible"
      width="350px"
      :before-close="handleCloseSave"
    >
      <el-time-picker
        is-range
        v-model="timeRange"
        value-format="YYYY-MM-DD HH:mm:ss"
        :range-separator="$t('views.components.player.deviceVideo.808340-7')"
        :start-placeholder="$t('views.components.player.deviceVideo.808340-8')"
        :end-placeholder="$t('views.components.player.deviceVideo.808340-9')"
        :placeholder="$t('views.components.player.deviceVideo.808340-10')"
        @change="timePickerChange"
        style="width: 210px; margin-right: 10px"
      />
      <template #footer>
        <el-button @click="handleCloseSave">{{ $t('cancel') }}</el-button>
        <el-button type="primary" @click="downloadRecord">{{ $t('confirm') }}</el-button>
      </template>
    </el-dialog>

    <!-- 分享链接 -->
    <el-dialog :title="sharTitle" v-model="openShare" width="50%" :before-close="handleCloseShare">
      <el-table
        v-loading="shareLoading"
        ref="singleTableRef"
        :data="streamUrlTableData"
        highlight-current-row
        size="small"
        :border="false"
        height="600px"
      >
        <el-table-column :label="$t('sip.channel.998532-28')" align="center" prop="fieldName" min-width="50" />
        <el-table-column :label="$t('sip.channel.998532-29')" align="left" prop="fieldValue" min-width="300" />
        <el-table-column
          fixed="right"
          :label="$t('opation')"
          align="center"
          class-name="small-padding fixed-width"
          width="120"
        >
          <template #default="scope">
            <el-button size="small" link @click="handleCopyLink(scope.row)">
              <el-icon><DocumentCopy /></el-icon>
              {{ $t('sip.channel.998532-30') }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, onMounted, getCurrentInstance } from 'vue';
import {
  Picture,
  VideoPlay,
  VideoPause,
  Promotion,
  Download,
  Upload,
  FolderOpened,
  VideoCamera,
  DocumentCopy,
} from '@element-plus/icons-vue';
import dayjs from 'dayjs';
import { ElMessage } from 'element-plus';
import player from '@/views/components/player/player.vue';
import { listmediaServer } from '@/api/iot/media/mediaServer';
import { listComChannel } from '@/api/iot/media/channel';
import { playCom, playbackCom, closeStream, listShareLink, closeStreamDirect } from '@/api/iot/media/player';
import {
  startPlayRecord,
  getDevRecord,
  startDownloadRecord,
  uploadRecord,
  getServerRecordByChannel,
} from '@/api/iot/media/record';
import { useDict } from '@/utils/dict';

const { proxy } = getCurrentInstance() as any;
const { sip_gen_status } = useDict('sip_gen_status');

const props = defineProps({
  device: { type: Object, default: null },
});

const loading = ref(true);
const shareLoading = ref(true);
const showSearch = ref(true);
const total = ref(0);
const channelList = ref<any[]>([]);
const queryParams = ref({ pageNum: 1, pageSize: 10, deviceId: null as string | null });
const recordParams = ref({ pageNum: 1, pageSize: 10 });
const mediaServerId = ref<string | null>(null);
const mediaServerList = ref<any[]>([]);

const openLive = ref(false);
const openVideo = ref(false);
const openShare = ref(false);
const sharTitle = ref('');
const liveTitle = ref('');
const videoTitle = ref('');
const playinfo = ref<any>({ playtype: '' });
const channelId = ref('');
const deviceId = ref('');
const streamId = ref('');
const streamRecord = ref(0);
const currentType = ref('local');
const selectedDate = ref<any>(new Date());
const dialogVisible = ref(false);
const timeRange = ref<any>(null);
const startTime = ref<any>(null);
const endTime = ref<any>(null);
const detailFiles = ref<any[]>([]);
const choosedFile = ref<string | null>(null);
const videoUrl = ref<string | null>(null);
const recordTotal = ref(0);
const currentYear = ref(new Date().getFullYear());
const currentMonth = ref(new Date().getMonth() + 1);
const vodData = ref<any>({});
const hisData = ref<any[]>([]);
const playing = ref(false);
const playrecording = ref(false);
const playerLoading = ref(false);
const videoType = ref(0);
const streamUrlTableData = ref<any[]>([]);
const stream = ref('');
const ssrc = ref('');
const playurl = ref('');

const playerRef = ref<any>(null);
const playbackerRef = ref<any>(null);
const singleTableRef = ref<any>(null);

const deviceInfo = ref<any>({});

watch(
  () => props.device,
  (newVal) => {
    deviceInfo.value = newVal;
    if (deviceInfo.value && deviceInfo.value.deviceId != 0) {
      queryParams.value.deviceId = deviceInfo.value.serialNumber;
    }
  }
);

function formatDate(date: any) {
  return dayjs(date).format('YYYY-MM-DD');
}

function getList() {
  loading.value = true;
  queryParams.value.deviceId = props.device?.serialNumber;
  listComChannel(queryParams.value).then((response: any) => {
    channelList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}

function timePickerChange(val: any) {
  if (val) {
    startTime.value = val[0];
    endTime.value = val[1];
    timeRange.value = [val[0], val[1]];
  }
}

// 直播
function handleViewLive(row: any) {
  openLive.value = true;
  playinfo.value.playtype = 'play';
  streamRecord.value = row.streamRecord;
  playCom(row.deviceId, row.channelId, false).then((response: any) => {
    const res = response.data;
    deviceId.value = res.deviceId;
    channelId.value = res.channelId;
    streamId.value = res.streamId;
    playurl.value = res.playurl;
    playerRef.value?.play(playurl.value);
    playing.value = true;
    playrecording.value = false;
    videoType.value = 1;
  });
}

function handleYearChange() {
  const d = new Date(selectedDate.value);
  d.setFullYear(currentYear.value);
  selectedDate.value = d;
}

function handleMonthChange() {
  const d = new Date(selectedDate.value);
  d.setMonth(currentMonth.value - 1);
  selectedDate.value = d;
}

function handleTypeChange(val: string) {
  videoType.value = 0;
  currentType.value = val;
  detailFiles.value = [];
  choosedFile.value = null;
  videoUrl.value = '';
  if (currentType.value === 'cloud') {
    getMediaServerList();
    queryRecordDetails();
  } else {
    selectedDate.value = formatDate(selectedDate.value);
    handleDateChange(selectedDate.value);
  }
}

function startPlayRecordAction() {
  streamRecord.value = streamRecord.value === 1 ? 0 : 1;
  const status = streamRecord.value === 1 ? false : true;
  closeDestroy(status);
  setTimeout(() => {
    startPlayer();
  }, 500);
}

function handleViewVideo(row: any) {
  videoType.value = 0;
  openVideo.value = true;
  playinfo.value.playtype = 'playback';
  deviceId.value = row.deviceId;
  channelId.value = row.channelId;
  currentType.value = 'local';
  selectedDate.value = formatDate(new Date());
  handleDateChange(selectedDate.value);
}

function handleCloseVideo() {
  openVideo.value = false;
  closeStreamAction();
}

function startPlayer() {
  if (playing.value) closeDestroy(false);
  playinfo.value.playtype = 'play';

  if (streamRecord.value === 1) {
    startPlayRecord(deviceId.value, channelId.value, true).then((response: any) => {
      const res = response.data;
      streamId.value = res.streamId;
      playurl.value = res.playurl;
      playing.value = true;
      playrecording.value = true;
      playerRef.value?.play(playurl.value);
    });
  } else {
    playCom(deviceId.value, channelId.value, false).then((response: any) => {
      const res = response.data;
      deviceId.value = res.deviceId;
      channelId.value = res.channelId;
      streamId.value = res.streamId;
      playurl.value = res.playurl;
      playing.value = true;
      playrecording.value = false;
      videoType.value = 1;
      playerRef.value?.play(playurl.value);
    });
  }
}

function disabledDate(date: Date) {
  const today = new Date();
  today.setHours(0, 0, 0, 0);
  const td = new Date(date);
  td.setHours(0, 0, 0, 0);
  return td > today;
}

function handleCellClick(date: Date) {
  if (disabledDate(date)) return;
  selectedDate.value = date;
}

function handleDateChange(val: any) {
  if (disabledDate(val)) {
    selectedDate.value = new Date(new Date().setHours(0, 0, 0, 0));
    proxy.$message.warning(proxy.$t('views.components.player.deviceVideo.808340-16'));
    return;
  }
  if (currentType.value === 'cloud') {
    selectedDate.value = val;
    if (mediaServerId.value) queryRecordDetails();
    return;
  }
  if (deviceId.value && channelId.value) {
    const date = getBeijingTime(val);
    const start = date / 1000;
    const end = Math.floor((date + 24 * 60 * 60 * 1000 - 1) / 1000);
    vodData.value = { start, end, base: start };
    playerLoading.value = true;
    getDevRecord(deviceId.value, channelId.value, { start, end })
      .then((res: any) => {
        if (res.code === 200 && res.data?.recordItems) {
          hisData.value = res.data.recordItems;
          if (hisData.value.length > 0) {
            if (hisData.value[0].start < start) {
              hisData.value[0].start = start;
              vodData.value.start = start;
            } else {
              vodData.value.start = hisData.value[0].start;
            }
            if (hisData.value[0].end !== 0 && hisData.value[0].end < end) {
              vodData.value.end = hisData.value[hisData.value.length - 1].end;
            }
            playbackAction();
          } else {
            proxy.$message.warning(proxy.$t('views.components.player.deviceVideo.808340-14'));
          }
        } else {
          proxy.$message.warning(proxy.$t('views.components.player.deviceVideo.808340-14'));
        }
      })
      .catch((error: any) => {
        console.error('本地录像查询接口异常：', error);
      })
      .finally(() => {
        playerLoading.value = false;
      });
  }
}

function getBeijingTime(queryDate: any) {
  const offset = 8 * 60 * 60 * 1000;
  return new Date(new Date(queryDate).getTime() - offset).getTime();
}

function playbackAction() {
  const query = { start: vodData.value.start, end: vodData.value.end };
  if (streamId.value) {
    console.log(deviceId.value);
    closeStream(deviceId.value, channelId.value, streamId.value).then(() => {
      playbackCom(deviceId.value, channelId.value, query)
        .then((res: any) => {
          initUrl(res.data);
        })
        .finally(() => {
          triggerPlay(hisData.value);
        });
    });
  } else {
    playbackCom(deviceId.value, channelId.value, query)
      .then((res: any) => {
        initUrl(res.data);
      })
      .finally(() => {
        triggerPlay(hisData.value);
      });
  }
}

function initUrl(data: any) {
  if (data) {
    streamId.value = data.streamId;
    ssrc.value = data.ssrc;
    playurl.value = data.playurl;
  } else {
    streamId.value = '';
    ssrc.value = '';
    playurl.value = '';
  }
}

function triggerPlay(playTimes: any[]) {
  playing.value = true;
  playbackerRef.value?.playbackCom(playurl.value, playTimes);
  playbackerRef.value?.setPlaybackStartTime(vodData.value.start);
}

function selectDownload() {
  dialogVisible.value = true;
}

function getMediaServerList() {
  listmediaServer({ pageNum: 1, pageSize: 10 }).then((res: any) => {
    if (res.code === 200) {
      mediaServerList.value = res.rows;
      if (mediaServerList.value.length > 0) {
        mediaServerId.value = mediaServerList.value[0].serverId;
        getRecordApi(mediaServerId.value!);
      }
    }
  });
}

const recordApi = ref('');
function getRecordApi(serverId: string) {
  const item = mediaServerList.value.find((s: any) => s.serverId === serverId);
  if (item) {
    recordApi.value =
      item.protocol === 'https'
        ? 'https://' + item.domainAlias + ':' + item.recordPort
        : 'http://' + item.ip + ':' + item.recordPort;
  }
}

function handleMediaChange(val: string) {
  recordTotal.value = 0;
  getRecordApi(val);
  setTimeout(() => {
    queryRecordDetails();
  }, 500);
}

function queryRecordDetails() {
  playerLoading.value = true;
  const sd = dayjs(selectedDate.value);
  startTime.value = sd.startOf('day').format('YYYY-MM-DD HH:mm:ss');
  endTime.value = sd.endOf('day').format('YYYY-MM-DD HH:mm:ss');
  const params = {
    serverId: mediaServerId.value,
    channelId: channelId.value,
    deviceId: deviceId.value,
    startTime: startTime.value,
    endTime: endTime.value,
    pageNum: recordParams.value.pageNum,
    pageSize: recordParams.value.pageSize,
  };
  getServerRecordByChannel(params).then((res: any) => {
    if (res.code === 200) {
      recordTotal.value = res.data.total;
      detailFiles.value = res.data.list;
      if (detailFiles.value.length > 0) videoType.value = 2;
    }
    playerLoading.value = false;
  });
}

function handleChooseFile(file: string) {
  choosedFile.value = file;
  selectedDate.value = formatDate(selectedDate.value);
  stream.value = 'gb_playrecord_' + deviceId.value + '_' + channelId.value;
  videoUrl.value = `${getFileBasePath()}/file/download/rtp/${stream.value}/${formatDate(selectedDate.value)}/${choosedFile.value}`;
}

function getFileBasePath() {
  return recordApi.value;
}

function uploadOss(item: string) {
  const params = {
    recordApi: recordApi.value,
    file: '/rtp/' + stream.value + '/' + formatDate(selectedDate.value) + '/' + item,
  };
  uploadRecord(params).then((res: any) => {
    if (res.code === 200) proxy.$modal.msgSuccess(proxy.$t('record.record-oss.80878-4'));
  });
}

function handleCloseSave() {
  dialogVisible.value = false;
}

function downloadRecord() {
  const start = new Date(startTime.value).getTime() / 1000;
  const end = new Date(endTime.value).getTime() / 1000;
  startDownloadRecord(deviceId.value, channelId.value, { startTime: start, endTime: end, speed: '4' }).then(
    (res: any) => {
      if (res.code === 200) {
        proxy.$message.success(proxy.$t('views.components.player.deviceVideo.808340-15'));
        dialogVisible.value = false;
      }
    }
  );
}

async function startPushStream(row: any) {
  deviceId.value = row.deviceId;
  channelId.value = row.channelId;
  streamId.value = 'gb_play_' + deviceId.value + '_' + channelId.value;
  if (row.streamPush === 1) {
    await startPlayer();
  } else {
    await closeStreamAction(false);
  }
}

function closeDestroy(force: boolean) {
  closeStreamAction(force);
  destroy();
}

function destroy() {
  playerRef.value?.destroy?.();
  playbackerRef.value?.destroy?.();
}

function handleCloseLive() {
  openLive.value = false;
  getList();
}

async function closeStreamAction(force?: boolean) {
  if (force) {
    streamId.value = 'gb_playrecord_' + deviceId.value + '_' + channelId.value;
    if (playing.value && streamId.value) {
      await closeStreamDirect(deviceId.value, channelId.value, streamId.value);
      streamId.value = '';
      ssrc.value = '';
      playurl.value = '';
      playing.value = false;
      playrecording.value = false;
    }
  } else {
    if (playrecording.value) return;
    if (playing.value && streamId.value) {
      closeStream(deviceId.value, channelId.value, streamId.value).then(() => {
        streamId.value = '';
        ssrc.value = '';
        playurl.value = '';
      });
      playing.value = false;
      playrecording.value = false;
    }
  }
}

function handleShareLink(row: any) {
  openShare.value = true;
  handleShareList(row);
}

function handleShareList(row: any) {
  streamUrlTableData.value = [];
  shareLoading.value = true;
  listShareLink(row.deviceId, row.channelId)
    .then((response: any) => {
      if (response.code === 200) {
        const obj = response.data || {};
        Object.entries(obj).forEach(([fieldName, fieldValue]) => {
          streamUrlTableData.value.push({ fieldName, fieldValue: fieldValue || '-' });
        });
      } else {
        proxy.$message.error(response.msg);
      }
    })
    .catch((err: any) => {
      console.error('请求异常：', err);
    })
    .finally(() => {
      shareLoading.value = false;
    });
}

function handleCopyLink(scopeRow: any) {
  navigator.clipboard
    .writeText(scopeRow.fieldValue)
    .then(() => {
      ElMessage.success(proxy.$t('sip.channel.998532-36'));
    })
    .catch(() => {
      ElMessage.error(proxy.$t('sip.channel.998532-37'));
    });
}

function handleCloseShare() {
  openShare.value = false;
  streamUrlTableData.value = [];
}

function getSnap(row: any) {
  return import.meta.env.VITE_APP_BASE_API + '/profile/snap/' + row.deviceId + '_' + row.channelId + '.jpg';
}

function getBigSnap(row: any) {
  return [getSnap(row)];
}

function isVideoChannel(row: any) {
  const channelId = row?.channelId;
  if (!channelId || typeof channelId !== 'string' || channelId.length < 13) {
    return false;
  }
  const ct = channelId.substring(10, 13);
  return ['111', '112', '118', '131', '132'].includes(ct);
}

onMounted(() => {
  sharTitle.value = proxy.$t('sip.channel.998532-27');
  liveTitle.value = proxy.$t('sip.channel.998532-31');
  videoTitle.value = proxy.$t('sip.channel.998532-32');
  getList();
  getMediaServerList();
});

defineExpose({ getList, closeDestroy });
</script>

<style lang="scss" scoped>
.sip-channel {
  padding-bottom: 20px;

  .el-image {
    border-radius: 4px;
    background-color: #dcdcdc;
    overflow: clip;

    :deep(.el-image__inner) {
      transition: all 0.3s;
      cursor: pointer;
      &:hover {
        transform: scale(1.2);
      }
    }

    :deep(.image-slot) {
      display: flex;
      justify-content: center;
      align-items: center;
      width: 100%;
      height: 100%;
      color: #909399;
      font-size: 30px;
    }
  }
}
.el-dialog__body .custom-player :deep(#container) {
  width: 750px !important;
  max-width: 1200px;
  margin: 0 auto;
}
:deep(.el-calendar-table .el-calendar-day) {
  height: 30px;
  font-size: 12px;
  padding: 5px 0;
  text-align: center;
  border-radius: 4px;
}
:deep(.el-calendar__title) {
  width: 150px;
  font-size: 12px;
  margin-left: -10px;
}
:deep(.el-calendar__button-group) {
  width: 300px;
  margin-left: 10px;
  margin-right: -30px;
}
:deep(.el-button-group > .el-button) {
  width: 50px;
  font-size: 10px;
  display: flex;
  justify-content: center;
}
.record-type-wrapper {
  padding: 0 15px 10px;
}
.custom-radio-group {
  display: flex;
  flex-wrap: nowrap;
  gap: 10px;
  width: 100%;
}
:deep(.custom-radio-item) {
  flex: 1;
  min-width: 0;
  height: 40px;
  border: 1px solid #ddd;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  padding: 0;
  margin: 0;
}
:deep(.custom-radio-item .el-radio__input) {
  display: none;
}
:deep(.custom-radio-item .el-radio__label) {
  margin: 0;
  display: inherit;
}
.radio-content {
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 14px;
  gap: 4px;
}
:deep(.custom-radio-item.is-checked) {
  border-color: #486ff2;
}
.cloud-file-list {
  padding: 0;
  margin: 0;
  list-style: none;
}
.cloud-file-item {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
  white-space: nowrap;
}
.cloud-file-tag {
  width: 150px;
  max-width: 150px;
  flex: 0 0 150px;
  cursor: pointer;
}
:deep(.cloud-file-tag .el-tag__content) {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  min-width: 0;
}
.cloud-file-tag-content {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
  min-width: 0;
  line-height: 1;
  white-space: nowrap;
}
.cloud-file-tag-content .el-icon {
  flex-shrink: 0;
}
.cloud-file-action {
  display: inline-flex;
  align-items: center;
  flex-shrink: 0;
  color: #666;
  cursor: pointer;
}
</style>
