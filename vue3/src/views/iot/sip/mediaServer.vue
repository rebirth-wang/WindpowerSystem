<template>
  <div class="media-server">
    <el-card>
      <el-row :gutter="10" style="margin-bottom: 16px">
        <el-col :span="1.5">
          <el-button type="primary" plain :icon="Plus" @click="handleAdd" v-hasPermi="['iot:video:add']">
            {{ $t('sip.mediaServer.998535-0') }}
          </el-button>
        </el-col>
      </el-row>
      <el-row :gutter="20" v-loading="loading">
        <el-col :xs="24" :sm="12" :md="8" :lg="6" v-for="item in serverList" :key="item.id" class="server-item">
          <el-card :body-style="{ padding: '0px' }" shadow="always" class="card-item">
            <div class="item-title">
              <div>
                <el-image :src="zlmLogo" fit="contain" class="img" />
              </div>
              <div class="title">
                <div class="name">{{ item.serverId }}</div>
                <div class="tag">
                  <el-tag type="info" size="small">{{ item.protocol }}</el-tag>
                </div>
              </div>
            </div>

            <el-row :gutter="10">
              <el-col :span="12" class="card-item-desc" style="padding-left: 24px">
                <div class="card-item-desc-item">
                  <span class="desc-label">{{ $t('sip.mediaServer.998535-13') }}：</span>
                  <span class="desc-content">{{ item.portRtsp }}</span>
                </div>
              </el-col>
              <el-col :span="12" class="card-item-desc" style="padding-right: 24px">
                <div class="card-item-desc-item">
                  <span class="desc-label">{{ $t('sip.mediaServer.998535-14') }}：</span>
                  <span class="desc-content">{{ item.portRtmp }}</span>
                </div>
              </el-col>
            </el-row>
            <el-row :gutter="10">
              <el-col :span="12" class="card-item-desc" style="padding-left: 24px">
                <div class="card-item-desc-item">
                  <span class="desc-label">{{ $t('sip.mediaServer.998535-2') }}：</span>
                  <span class="desc-content">{{ item.ip }}</span>
                </div>
              </el-col>
              <el-col :span="12" class="card-item-desc" style="padding-right: 24px">
                <div class="card-item-desc-item">
                  <span class="desc-label">{{ $t('sip.mediaServer.998535-4') }}：</span>
                  <span class="desc-content">{{ parseTime(item.createTime, '{y}-{m}-{d} {h}:{i}') }}</span>
                </div>
              </el-col>
            </el-row>
            <el-row :gutter="10">
              <el-col :span="12" class="card-item-desc" style="padding-left: 24px">
                <div class="card-item-desc-item">
                  <span class="desc-label">{{ $t('template.index.891112-117') }}：</span>
                  <span class="desc-content">{{ item.tenantName }}</span>
                </div>
              </el-col>
              <el-col :span="12" class="card-item-desc" style="padding-right: 24px">
                <div class="card-item-desc-item">
                  <span class="desc-label">{{ $t('template.index.891112-118') }}：</span>
                  <span class="desc-content">{{ item.createBy }}</span>
                </div>
              </el-col>
            </el-row>

            <el-divider class="divider" />

            <div class="card-item-btns">
              <el-button size="small" link @click="handleView(item)" v-hasPermi="['iot:video:query']">
                {{ $t('sip.mediaServer.998535-9') }}
              </el-button>
              <span class="btn-item-line" v-hasPermi="['iot:video:edit']">|</span>
              <el-button size="small" link @click="handleEdit(item)" v-hasPermi="['iot:video:edit']">
                {{ $t('sip.mediaServer.998535-10') }}
              </el-button>
              <span class="btn-item-line" v-hasPermi="['iot:video:edit']">|</span>
              <el-button size="small" link @click="configManage(item)" v-hasPermi="['iot:video:edit']">
                {{ $t('sip.mediaServer.998535-11') }}
              </el-button>
              <span class="btn-item-line" v-hasPermi="['iot:video:remove']">|</span>
              <el-button size="small" link @click="handleDelete(item)" v-hasPermi="['iot:video:remove']">
                {{ $t('sip.mediaServer.998535-12') }}
              </el-button>
            </div>
          </el-card>
        </el-col>
        <el-col :span="24" v-if="serverList.length === 0 && !loading">
          <el-empty :description="$t('sip.mediaServer.998535-6')" />
        </el-col>
      </el-row>
      <pagination
        style="margin: 0 0 20px 0"
        v-show="total > 0"
        :total="total"
        :page.sync="queryParams.pageNum"
        :limit.sync="queryParams.pageSize"
        @pagination="getServerList"
      />
    </el-card>

    <!-- 新增/编辑 -->
    <media-server-edit ref="mediaServerEditRef" @success="getServerList" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, getCurrentInstance } from 'vue';
import { Plus } from '@element-plus/icons-vue';
import { listmediaServer, delmediaServer } from '@/api/iot/media/mediaServer';
import mediaServerEdit from './mediaServer-edit.vue';
import zlmLogo from '@/assets/images/zlm_logo.png';
import { parseTime } from '@/utils/ruoyi';

const { proxy } = getCurrentInstance() as any;

const loading = ref(false);
const serverList = ref<any[]>([]);
const mediaServerEditRef = ref<any>(null);
const queryParams = ref<any>({
  serverId: '',
  pageNum: 1,
  pageSize: 10,
});

const total = ref(0);

function getServerList() {
  loading.value = true;
  const params = { ...queryParams.value };
  listmediaServer(params).then((response: any) => {
    serverList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}

function handleAdd() {
  mediaServerEditRef.value?.openAdd();
}

function handleView(item: any) {
  mediaServerEditRef.value?.openView(item);
}

function handleEdit(item: any) {
  mediaServerEditRef.value?.openEdit(item);
}

function configManage(item: any) {
  if (item.protocol === 'https') {
    const url = 'https://' + item.domainAlias + ':' + item.portHttps + '/webassist/?secret=' + item.secret;
    window.open(url, '_blank');
  } else if (item.protocol === 'http') {
    const url = 'http://' + item.ip + ':' + item.portHttp + '/webassist/?secret=' + item.secret;
    window.open(url, '_blank');
  } else {
    proxy.$modal.msgError('暂无该播放协议管理功能');
  }
}

function handleDelete(item: any) {
  proxy.$modal
    .confirm(proxy.$t('sip.mediaServer.998535-7', [item.serverId]))
    .then(() => {
      return delmediaServer(item.id);
    })
    .then(() => {
      getServerList();
      proxy.$modal.msgSuccess(proxy.$t('delSuccess'));
    })
    .catch(() => {});
}

onMounted(() => {
  getServerList();
});

// 搜索按钮操作
function handleQuery() {
  getServerList();
}

defineExpose({ getServerList });
</script>

<style lang="scss" scoped>
.media-server {
  padding: 20px;
  overflow-x: hidden;

  .server-item {
    margin-bottom: 20px;
    text-align: center;

    .card-item {
      background: #ffffff;
      border-radius: 8px;
      border: 1px solid #dcdfe6;
      overflow: hidden;
      :deep(.el-card__body) {
        overflow: hidden;
      }

      .item-title {
        display: flex;
        flex-direction: row;
        align-items: center;
        position: relative;
        padding: 18px;

        .img {
          height: 58px;
          width: 68px;
          border-radius: 10px;
        }

        .title {
          flex: 1;
          min-width: 0;
          text-align: left;
          margin-left: 16px;

          .name {
            font-weight: 500;
            font-size: 16px;
            color: #303133;
            line-height: 22px;
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
          }

          .tag {
            margin-top: 5px;
            max-width: 100%;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
            :deep(.el-tag) {
              max-width: 100%;
              overflow: hidden;
              text-overflow: ellipsis;
              white-space: nowrap;
              .el-tag__content {
                overflow: hidden;
                text-overflow: ellipsis;
                white-space: nowrap;
              }
            }
            span {
              font-weight: 200;
              border-radius: 2px;
              border: 1px solid #c0c4cc;
              background: #ffffff;
            }
          }
        }
      }

      .card-item-desc {
        display: flex;
        align-items: center;
        margin-bottom: 6px;
        .card-item-desc-item {
          display: flex;
          align-items: center;
          width: 100%;
          min-width: 0;
          .desc-label {
            flex-shrink: 0;
            white-space: nowrap;
            font-size: 12px;
            color: #909399;
          }
          .desc-content {
            min-width: 0;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
            font-size: 12px;
            color: #606266;
          }
        }
      }

      .divider {
        margin: 10px 0 0;
        height: 1px;
        background: #dcdfe6;
      }

      .card-item-btns {
        height: 40px;
        padding: 0 20px;
        display: flex;
        align-items: center;
        justify-content: space-around;

        .btn-item-line {
          width: 1px;
          margin: 0px 10px;
          font-size: 16px;
          color: #dcdfe6;
        }
      }
    }
  }
}

.divider {
  margin: 10px 0 0;
  height: 1px;
  background: #dcdfe6;
}
</style>
