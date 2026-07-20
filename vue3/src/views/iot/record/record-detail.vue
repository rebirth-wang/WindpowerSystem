<template>
  <div class="iot-record-detail">
    <el-card class="top-card" :body-style="{ padding: '26px 20px' }">
      <div class="title-wrap">
        <el-button class="top-button" type="info" size="small" @click="goBack()">
          <el-icon><ArrowLeft /></el-icon>
          {{ $t('product.product-edit.473153-44') }}
        </el-button>
        <span class="info-item">{{ $t('record.239091-2') }}：{{ recordFile.app }}</span>
        <span class="info-item">{{ $t('record.239091-3') }}：{{ recordFile.stream }}</span>
      </div>
    </el-card>
    <el-card class="main-card" :body-style="{ padding: '20px' }">
      <el-row>
        <el-col :span="6">
          <div class="left-wrap">
            <el-date-picker
              class="record-date-picker"
              style="width: 100%; margin-bottom: 10px"
              v-model="chooseDate"
              type="date"
              value-format="YYYY-MM-DD"
              :placeholder="$t('record.239091-6')"
              :cell-class-name="getDateCellClass"
              @change="handleDateChange()"
            ></el-date-picker>
            <div class="node-data">
              <div class="list-body">
                <ul v-if="detailFiles.length > 0" class="file-list">
                  <li
                    v-for="(item, index) in detailFiles"
                    :key="index"
                    class="file-row"
                    :class="{ active: choosedFile == item }"
                  >
                    <button class="file-btn" type="button" @click="handleChooseFile(item)">
                      <el-icon class="video-icon"><VideoCamera /></el-icon>
                      <span class="time-text">{{ item.substring(0, 17) }}</span>
                    </button>
                    <a
                      class="icon"
                      :href="`${getFileBasePath()}/download.html?url=file/download/${recordFile.app}/${recordFile.stream}/${chooseDate}/${item}`"
                      target="_blank"
                    >
                      <el-icon><Download /></el-icon>
                    </a>
                    <a class="icon" @click="uploadOss(item)" :title="$t('upload')">
                      <el-icon><UploadFilled /></el-icon>
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
              <div class="list-pager" v-show="chooseDate != null && total > queryParams.pageSize">
                <pagination
                  :total="total"
                  v-model:page="queryParams.pageNum"
                  v-model:limit="queryParams.pageSize"
                  small
                  layout="prev, pager, next"
                  @pagination="queryRecordDetails"
                  :pager-count="5"
                />
              </div>
            </div>
          </div>
        </el-col>
        <el-col :span="18">
          <div class="right-wrap">
            <player ref="playerRef" :playerinfo="playinfo" :videoUrl="videoUrl" class="custom-player"></player>
          </div>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, getCurrentInstance, onMounted } from 'vue';
import { ArrowLeft, Download, UploadFilled, VideoCamera } from '@element-plus/icons-vue';
import { getServerRecordByDate, getServerRecordByFile, uploadRecord } from '@/api/iot/media/record';
// @ts-ignore
import player from '@/views/components/player/player.vue';

const { proxy } = getCurrentInstance() as any;
const emit = defineEmits(['back']);

const props = defineProps({
  recordFile: { type: Object, default: () => ({}) },
  mediaServerId: { type: String, default: '' },
  recordApi: { type: String, default: '' },
});

const playinfo = reactive({ playtype: 'playback' });
const dateFilesObj = ref<any>({});
const detailFiles = ref<any[]>([]);
const chooseDate = ref('');
const videoUrl = ref<any>(null);
const choosedFile = ref<any>(null);
const total = ref(0);
const queryParams = reactive({ pageNum: 1, pageSize: 8 });

function handleDateChange() {
  detailFiles.value = [];
  choosedFile.value = null;
  videoUrl.value = '';
  queryParams.pageNum = 1;
  queryRecordDetails();
}

function getDateCellClass(cellDate: Date) {
  const yyyy = cellDate.getFullYear();
  const mm = `${cellDate.getMonth() + 1}`.padStart(2, '0');
  const dd = `${cellDate.getDate()}`.padStart(2, '0');
  const key = `${yyyy}-${mm}-${dd}`;
  return dateFilesObj.value[key] ? 'data-picker-true' : '';
}

function queryRecordDetails() {
  if (chooseDate.value) {
    const params = {
      recordApi: props.recordApi,
      app: props.recordFile.app,
      stream: props.recordFile.stream,
      startTime: chooseDate.value + ' 00:00:00',
      endTime: chooseDate.value + ' 23:59:59',
      pageNum: queryParams.pageNum,
      pageSize: queryParams.pageSize,
    };
    getServerRecordByFile(params).then((res: any) => {
      if (res.code === 200) {
        total.value = res.data.total;
        detailFiles.value = res.data.list;
      }
    });
  }
}

function handleChooseFile(file: any) {
  choosedFile.value = file;
  videoUrl.value = file
    ? `${getFileBasePath()}/file/download/${props.recordFile.app}/${props.recordFile.stream}/${chooseDate.value}/${file}`
    : '';
}

function uploadOss(item: any) {
  const params = {
    recordApi: props.recordApi,
    file: '/' + props.recordFile.app + '/' + props.recordFile.stream + '/' + chooseDate.value + '/' + item,
  };
  uploadRecord(params).then((res: any) => {
    if (res.code === 200) proxy.$modal.msgSuccess(proxy.$t('uploadSuccess'));
  });
}

function getFileBasePath() {
  return props.recordApi;
}

function getDateInYear() {
  getServerRecordByDate({
    recordApi: props.recordApi,
    app: props.recordFile.app,
    stream: props.recordFile.stream,
  }).then((res: any) => {
    if (res.code === 200 && res.data?.length > 0) {
      res.data.forEach((d: string) => {
        dateFilesObj.value[d] = d;
      });
    }
  });
}

function goBack() {
  emit('back');
}

onMounted(() => {
  getDateInYear();
});
</script>

<style>
.data-picker-true .el-date-table-cell__text::after {
  content: '';
  position: absolute;
  width: 4px;
  height: 4px;
  border-radius: 50%;
  background-color: #606060;
  left: 50%;
  transform: translateX(-50%);
  bottom: 2px;
}
</style>
<style lang="scss" scoped>
.iot-record-detail {
  .top-card {
    margin-bottom: 10px;

    .title-wrap {
      display: flex;
      flex-direction: row;
      align-items: center;

      .top-button {
        height: 24px;
        color: #909399;
        background: #f4f5f7;
        padding: 0 8px;
        border: none;
      }

      .info-item {
        font-weight: normal;
        font-size: 13px;
        color: #333333;
        line-height: 20px;
        margin-left: 24px;
      }
    }
  }

  .main-card {
    .left-wrap {
      position: relative;
      width: 100%;
      padding-right: 14px;

      .record-date-picker {
        margin-bottom: 10px;
      }

      .no-data {
        position: absolute;
        left: 50%;
        top: 50%;
        transform: translate(-55%, -52%);
        height: 465px;
      }

      .node-data {
        border-radius: 4px;
        border: 1px solid #dcdfe6;
        height: 468px;
        background: #fff;
        display: flex;
        flex-direction: column;
        overflow: hidden;
      }

      .list-body {
        flex: 1;
        overflow: auto;
        padding: 8px;
      }

      .file-list {
        list-style: none;
        padding: 0;
        margin: 0;
      }

      .file-row {
        display: flex;
        align-items: center;
        gap: 8px;
        margin-bottom: 6px;

        .file-btn {
          border: 1px solid #dcdfe6;
          border-radius: 3px;
          background: #f4f6fb;
          height: 24px;
          line-height: 22px;
          padding: 0 8px;
          color: #4f6ef7;
          cursor: pointer;
          display: inline-flex;
          align-items: center;
        }

        .video-icon {
          margin-right: 5px;
          position: relative;
        }

        .time-text {
          font-size: 12px;
        }

        .icon {
          display: inline-flex;
          align-items: center;
          color: #4f6ef7;
          font-size: 14px;
        }

        &.active .file-btn {
          border-color: #f56c6c;
          background: #fdeceb;
          color: #f56c6c;
        }
      }

      .list-pager {
        border-top: none;
        padding: 6px 0;
        display: flex;
        justify-content: flex-end;
        background: #fff;
      }

      .list-pager :deep(.pagination-container) {
        margin: 0 !important;
        padding: 0 !important;
        background: transparent !important;
        width: 100%;
        display: flex;
        justify-content: flex-end;
      }

      .list-pager :deep(.el-pagination) {
        margin: 0;
      }
    }

    .right-wrap {
      height: 510px;
      width: 100%;
      border-radius: 4px;
      overflow: hidden;
      background: #61616d;
    }
  }
}

.custom-player {
  width: 100%;
  height: 510px;
  padding: 0;
  margin: 0;
}

.custom-player :deep(.root) {
  width: 100%;
  margin: 0;
  display: block;
}

.custom-player :deep(.container-shell) {
  position: relative;
  width: 100%;
  height: 510px;
  padding-top: 56.25%;
  border-radius: 0;
  overflow: hidden;
}

.custom-player :deep(#container) {
  position: absolute;
  top: 0;
  left: 0;
  width: 100% !important;
  height: 510px;
  background: rgba(13, 14, 27, 0.7);
  border-radius: 0;
  max-width: none !important;
}
</style>
