<template>
  <el-dialog
    class="scada-share-dialog"
    :title="$t('scada.common.scadaShareDialog.842076-0')"
    v-model="visible"
    width="680px"
    append-to-body
    @open="dialogOpen"
    :before-close="dialogBeforeClose"
  >
    <div class="device-list" v-show="isOpenShare">
      <p>{{ $t('scada.common.scadaShareDialog.842076-1') }}</p>
      <el-form class="query-from" :model="queryParams" ref="queryForm" :inline="true" label-width="68px">
        <el-form-item prop="deviceName">
          <el-input
            v-model="queryParams.deviceName"
            :placeholder="$t('scada.common.scadaShareDialog.842076-3')"
            clearable
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleQuery">
            {{ $t('scada.common.scadaShareDialog.842076-4') }}
          </el-button>
          <el-button :icon="Refresh" @click="handleResetQuery">
            {{ $t('scada.common.scadaShareDialog.842076-5') }}
          </el-button>
        </el-form-item>
      </el-form>
      <div class="tools-wrap">
        <el-checkbox v-model="queryParams.showChild" @change="handleQuery">
          {{ $t('scada.common.scadaShareDialog.842076-6') }}
        </el-checkbox>
        <el-tooltip :content="$t('scada.common.scadaShareDialog.842076-7')" placement="top">
          <el-icon class="question-icon"><QuestionFilled /></el-icon>
        </el-tooltip>
      </div>
      <el-table ref="table" v-loading="loading" :data="deviceList" :border="false">
        <el-table-column :label="$t('scada.common.scadaShareDialog.842076-2')" align="left" prop="deviceName" />
        <el-table-column
          :label="$t('scada.common.scadaShareDialog.842076-8')"
          align="center"
          prop="opation"
          width="100"
        >
          <template #default="scope">
            <el-button size="small" @click="handleShare(scope.row)">
              {{ $t('scada.common.scadaShareDialog.842076-0') }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <pagination
        v-show="total > 0"
        layout="prev, pager, next"
        :total="total"
        :page="queryParams.pageNum"
        @update:page="queryParams.pageNum = $event"
        :limit="queryParams.pageSize"
        @update:limit="queryParams.pageSize = $event"
        @pagination="getDeviceList"
      />
    </div>

    <div class="open-share" v-show="!isOpenShare && !isShare">
      <div class="open-img">
        <img src="@/assets/images/scada/scada_share.png" />
      </div>
      <el-button class="open-btn" type="primary" @click="handleOpenShare">
        {{ $t('scada.common.scadaShareDialog.842076-10') }}
      </el-button>
    </div>

    <div class="share-content" v-show="!isOpenShare && isShare">
      <div class="share">
        <span>{{ $t('scada.common.scadaShareDialog.842076-15') }}</span>
        <span class="close-share" @click="handeleCloseshare">
          <el-icon><Link /></el-icon>
          {{ $t('scada.common.scadaShareDialog.842076-16') }}
        </span>
      </div>
      <div class="share">
        <el-input v-model="link" :disabled="true"></el-input>
        <el-button v-if="!isCheckedPass" type="primary" style="width: 163px" @click="handleCopyUrl">
          {{ $t('scada.common.scadaShareDialog.842076-17') }}
        </el-button>
        <el-button v-else type="primary" style="width: 163px" @click="handleCopyUrlAndPass">
          {{ $t('scada.common.scadaShareDialog.842076-18') }}
        </el-button>
      </div>
      <el-checkbox style="margin-bottom: 3px" v-model="isCheckedPass" @change="handleNeedPassCheckChange">
        {{ $t('scada.common.scadaShareDialog.842076-19') }}
      </el-checkbox>
      <div class="share" v-show="isCheckedPass">
        <el-input v-model="shareDatas.sharePass" :disabled="true"></el-input>
        <el-button type="primary" style="width: 163px" @click="handleResetPass">
          {{ $t('scada.common.scadaShareDialog.842076-20') }}
        </el-button>
      </div>

      <div class="images-link" v-show="shareDatas.shareShortUrl">
        <p>{{ $t('scada.common.scadaShareDialog.842076-21') }}</p>
        <div class="link">
          <div class="qrcode">
            <vue-qr ref="vueQrRef" :text="shareDatas.shareShortUrl || ''" :size="120" :margin="0"></vue-qr>
          </div>
          <div>
            <p>
              {{ $t('scada.common.scadaShareDialog.842076-22') }}
              <br />
              {{ $t('scada.common.scadaShareDialog.842076-23') }}
            </p>
            <el-button style="margin: 10px" :icon="Download" @click="handleDownLoadQrCode">
              {{ $t('scada.common.scadaShareDialog.842076-24') }}
            </el-button>
          </div>
        </div>
      </div>
      <el-button
        v-if="type === 1"
        style="margin-bottom: 10px"
        type="text"
        :icon="Back"
        @click="handeleBackDeviceList"
      >
        {{ $t('scada.common.scadaShareDialog.842076-25') }}
      </el-button>
    </div>
  </el-dialog>
</template>
<script setup lang="ts">
import { ref, reactive, computed, getCurrentInstance, nextTick } from 'vue';
import { ElMessage } from 'element-plus';
import { Search, Refresh, Download, Back, Link, QuestionFilled } from '@element-plus/icons-vue';
import vueQr from 'vue-qr';
import { listDeviceShort } from '@/api/iot/device';
import { getShare, editShare } from '@/api/scada/center';

const props = defineProps({
  visible: {
    type: Boolean,
    required: true,
    default: false,
  },
  guid: {
    type: String,
    required: true,
    default: '',
  },
  tenantId: {
    type: Number,
    default: null,
  },
  createBy: {
    type: String,
    default: '',
  },
  type: {
    type: Number,
    required: true,
    default: 1,
  },
  productId: {
    type: Number,
    default: null,
  },
});

const emit = defineEmits(['update:visible']);

const visible = computed({
  get: () => props.visible,
  set: (value) => emit('update:visible', value),
});

const { proxy } = getCurrentInstance() as any;

const loading = ref(true);
const deviceList = ref<any[]>([]);
const total = ref(0);
const isOpenShare = ref(false);
const isShare = ref(false);
const isCheckedPass = ref(false);
const link = ref('');

const queryParams = reactive({
  pageNum: 1,
  pageSize: 5,
  deviceName: null as string | null,
  productId: null as number | null,
  showChild: false,
});

const shareDatas = reactive({
  isShare: null as number | null,
  shareShortUrl: '',
  sharePass: '',
}) as any;

const queryFormRef = ref<any>(null);
const vueQrRef = ref<any>(null);

function getDeviceList() {
  loading.value = true;
  listDeviceShort(queryParams)
    .then((res: any) => {
      if (res.code === 200) {
        deviceList.value = res.rows;
        total.value = res.total;
      }
    })
    .catch(() => {
      // Error handled silently, let UI handle empty state
    })
    .finally(() => {
      loading.value = false;
    });
}

function handleQuery() {
  queryParams.pageNum = 1;
  getDeviceList();
}

function handleResetQuery() {
  queryFormRef.value?.resetFields();
  handleQuery();
}

function handleShare(row: any) {
  const params = {
    guid: props.guid,
    type: props.type,
    serialNumber: row.serialNumber,
  };
  getShare(params)
    .then((res: any) => {
      const { code, data } = res;
      if (code === 200) {
        Object.assign(shareDatas, data);
        isCheckedPass.value = !!data.sharePass;
        link.value = data.sharePass
          ? data.shareShortUrl + '#(' + proxy.$t('scada.common.scadaShareDialog.842076-30') + ':' + data.sharePass + ')'
          : data.shareShortUrl;
        if (data.isShare === 1) {
          isShare.value = true;
          isOpenShare.value = false;
        } else {
          isShare.value = false;
          isOpenShare.value = false;
        }
      }
    })
    .catch(() => {
      // Error handled silently
    });
}

function handleOpenShare() {
  (proxy as any).$modal
    .confirm(proxy.$t('scada.common.scadaShareDialog.842076-12'), proxy.$t('scada.common.scadaShareDialog.842076-11'), {
      cancelButtonText: proxy.$t('scada.common.scadaShareDialog.842076-13'),
      confirmButtonText: proxy.$t('scada.common.scadaShareDialog.842076-14'),
      type: 'warning',
    })
    .then(() => {
      const params = {
        ...shareDatas,
        isShare: 1,
        tenantId: props.tenantId,
        createBy: props.createBy,
      };
      return editShare(params);
    })
    .then((res: any) => {
      const { code, data } = res;
      if (code === 200) {
        Object.assign(shareDatas, data);
        isShare.value = true;
        isCheckedPass.value = !!data.sharePass;
        link.value = data.sharePass
          ? data.shareShortUrl + '#(' + proxy.$t('scada.common.scadaShareDialog.842076-30') + ':' + data.sharePass + ')'
          : data.shareShortUrl;
        ElMessage.success(proxy.$t('scada.common.scadaShareDialog.842076-26'));
      }
    })
    .catch((err: unknown) => {
      if (err === 'cancel' || err === 'close') {
        ElMessage.info(proxy.$t('scada.common.scadaShareDialog.842076-27'));
      }
    });
}

function handeleCloseshare() {
  (proxy as any).$modal
    .confirm(proxy.$t('scada.common.scadaShareDialog.842076-31'), proxy.$t('scada.common.scadaShareDialog.842076-11'), {
      cancelButtonText: proxy.$t('scada.common.scadaShareDialog.842076-13'),
      confirmButtonText: proxy.$t('scada.common.scadaShareDialog.842076-14'),
      type: 'warning',
    })
    .then(() => {
      const params = {
        ...shareDatas,
        isShare: 0,
        tenantId: props.tenantId,
        createBy: props.createBy,
      };
      return editShare(params);
    })
    .then((res: any) => {
      const { code } = res;
      if (code === 200) {
        isShare.value = false;
        ElMessage.success(proxy.$t('scada.common.scadaShareDialog.842076-28'));
      }
    })
    .catch((err: unknown) => {
      if (err === 'cancel' || err === 'close') {
        ElMessage.info(proxy.$t('scada.common.scadaShareDialog.842076-27'));
      }
    });
}

function copyText(text: string) {
  if (navigator.clipboard) {
    navigator.clipboard.writeText(text).then(() => {
      ElMessage.success(proxy.$t('scada.common.scadaShareDialog.842076-29'));
    });
  } else {
    const element = document.createElement('SPAN') as any;
    element.textContent = text;
    document.body.appendChild(element);
    if ((document as any).selection) {
      const range = (document.body as any).createTextRange();
      range.moveToElementText(element);
      range.select();
    } else if (window.getSelection) {
      const range = document.createRange();
      range.selectNode(element);
      window.getSelection()!.removeAllRanges();
      window.getSelection()!.addRange(range);
    }
    document.execCommand('copy');
    element.remove ? element.remove() : element.removeNode(true);
    ElMessage.success(proxy.$t('scada.common.scadaShareDialog.842076-29'));
  }
}

function handleCopyUrl() {
  copyText(shareDatas.shareShortUrl);
}

function handleCopyUrlAndPass() {
  const text =
    shareDatas.shareShortUrl +
    '#(' +
    proxy.$t('scada.common.scadaShareDialog.842076-30') +
    ':' +
    shareDatas.sharePass +
    ')';
  copyText(text);
}

function handleNeedPassCheckChange(value: boolean) {
  const params = {
    ...shareDatas,
    sharePassStatus: value ? 1 : 0,
    tenantId: props.tenantId,
    createBy: props.createBy,
  };
  editShare(params)
    .then((res: any) => {
      const { code, data } = res;
      if (code === 200) {
        Object.assign(shareDatas, data);
        link.value = data.sharePass
          ? data.shareShortUrl + '#(' + proxy.$t('scada.common.scadaShareDialog.842076-30') + ':' + data.sharePass + ')'
          : data.shareShortUrl;
      }
    })
    .catch(() => {
      // Error handled silently
    });
}

function handleResetPass() {
  const params = {
    ...shareDatas,
    sharePassStatus: 1,
    tenantId: props.tenantId,
    createBy: props.createBy,
  };
  editShare(params)
    .then((res: any) => {
      const { code, data } = res;
      if (code === 200) {
        Object.assign(shareDatas, data);
      }
    })
    .catch(() => {
      // Error handled silently
    });
}

function handleDownLoadQrCode() {
  const qrInstance = vueQrRef.value;
  const image = qrInstance.$el;
  const url = image.currentSrc;
  const a = document.createElement('a');
  a.href = url;
  a.download = 'share_code.png';
  a.click();
  URL.revokeObjectURL(a.href);
}

function handeleBackDeviceList() {
  isShare.value = false;
  isOpenShare.value = true;
}

function dialogOpen() {
  if (props.type === 1) {
    queryParams.productId = props.productId;
    nextTick(() => {
      getDeviceList();
    });
    isOpenShare.value = true;
  } else {
    const params = {
      guid: props.guid,
      type: props.type,
    };
    getShare(params)
      .then((res: any) => {
        const { code, data } = res;
        if (code === 200) {
          Object.assign(shareDatas, data);
          isCheckedPass.value = !!data.sharePass;
          link.value = data.sharePass
            ? data.shareShortUrl +
              '#(' +
              proxy.$t('scada.common.scadaShareDialog.842076-30') +
              ':' +
              data.sharePass +
              ')'
            : data.shareShortUrl;
          nextTick(() => {
            if (data.isShare === 1) {
              isShare.value = true;
              isOpenShare.value = false;
            } else {
              isShare.value = false;
              isOpenShare.value = false;
            }
          });
        }
      })
      .catch(() => {
        // Error handled silently
      });
  }
}

function dialogBeforeClose() {
  emit('update:visible', false);
}
</script>

<style lang="scss" scoped>
.scada-share-dialog {
  .device-list {
    width: 100%;

    p {
      padding: 0;
      margin: 0;
    }

    .query-from {
      margin-top: 10px;

      :deep(.el-form-item) {
        margin-bottom: 10px;
      }
    }

    .tools-wrap {
      display: flex;
      flex-direction: row;
      justify-content: flex-end;
      align-items: center;
      margin-bottom: 12px;

      .question-icon {
        margin-left: 5px;
      }
    }
  }

  .open-share {
    width: 100%;
    text-align: center;

    .open-img {
      width: 100%;

      img {
        margin: 50px 0;
      }
    }

    .open-btn {
      width: 260px;
      margin-bottom: 60px;
    }
  }

  .share-content {
    .share {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 10px;

      .close-share {
        display: flex;
        align-items: center;
        cursor: pointer;
      }

      .el-input {
        width: 72%;
      }
    }

    :deep(.el-checkbox__label) {
      font-size: 12px;
    }

    .images-link {
      margin: 30px 0;

      .link {
        display: flex;
        align-items: center;
        margin: 20px 0;

        .qrcode {
          margin-right: 20px;
        }
      }
    }
  }
}
</style>
