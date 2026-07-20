<template>
  <div class="app-content">
    <el-card v-show="showSearch" class="search-card">
      <el-form class="search-form" :model="queryParams" ref="queryFormRef" :inline="true">
        <el-form-item prop="iccid">
          <el-input
            v-model="queryParams.iccid"
            :placeholder="$t('card.sim.please_enter_iccid')"
            clearable
            @keyup.enter="handleQuery"
            @clear="resetQuery"
          />
        </el-form-item>
        <el-form-item prop="imsi">
          <el-input
            v-model="queryParams.imsi"
            :placeholder="$t('card.sim.please_enter_imsi')"
            clearable
            @keyup.enter="handleQuery"
            @clear="resetQuery"
          />
        </el-form-item>
        <el-form-item prop="msisdn">
          <el-input
            v-model="queryParams.msisdn"
            :placeholder="$t('card.sim.please_enter_card_number')"
            clearable
            @keyup.enter="handleQuery"
            @clear="resetQuery"
          />
        </el-form-item>
        <el-form-item prop="cardStatus" v-if="searchShow">
          <el-select
            v-model="queryParams.cardStatus"
            :placeholder="$t('card.sim.please_select_card_status')"
            clearable
            @clear="resetQuery"
          >
            <el-option v-for="dict in iot_card_status" :key="dict.value" :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <el-form-item prop="cardPlatformId" v-if="searchShow">
          <el-select
            v-model="queryParams.cardPlatformId"
            :placeholder="$t('card.sim.please_select_card_platform')"
            clearable
            @clear="resetQuery"
          >
            <el-option v-for="item in cardPlatformList" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item prop="operator" v-if="searchShow">
          <el-select
            v-model="queryParams.operator"
            :placeholder="$t('card.sim.please_select_operator')"
            clearable
            @clear="resetQuery"
          >
            <el-option v-for="dict in iot_card_operator" :key="dict.value" :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <div style="float: right">
          <el-button type="primary" :icon="Search" @click="handleQuery">{{ $t('card.platform.search') }}</el-button>
          <el-button :icon="Refresh" @click="resetQuery">{{ $t('card.platform.reset') }}</el-button>
          <el-button type="primary" link @click="searchChange">
            <span style="color: #486ff2; margin-left: 14px">
              {{ searchShow ? $t('template.index.891112-113') : $t('template.index.891112-112') }}
            </span>
            <el-icon style="color: #486ff2; margin-left: 10px">
              <ArrowUp v-if="searchShow" />
              <ArrowDown v-else />
            </el-icon>
          </el-button>
        </div>
      </el-form>
    </el-card>

    <el-card>
      <el-row :gutter="10" style="margin-bottom: 16px">
        <el-col :span="1.5">
          <el-button type="primary" plain :icon="Plus" @click="handleAdd" v-hasPermi="['iot:card:add']">
            {{ $t('card.platform.add') }}
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button plain :icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['iot:card:remove']">
            {{ $t('card.platform.delete') }}
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button plain :icon="Download" @click="handleExport" v-hasPermi="['iot:card:export']">
            {{ $t('card.platform.export') }}
          </el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList">
          <div class="button-group">
            <el-tooltip effect="dark" :content="$t('device.index.105953-67')" placement="top">
              <el-button
                size="small"
                @click="handleShowCard"
                :class="['toggle-button card-button', { active: isListView }]"
              >
                <el-icon><Document /></el-icon>
              </el-button>
            </el-tooltip>
            <div class="separator"></div>
            <el-tooltip effect="dark" :content="$t('device.index.105953-68')" placement="top">
              <el-button
                size="small"
                @click="handleShowList"
                :class="['toggle-button list-button', { active: !isListView }]"
              >
                <el-icon><Grid /></el-icon>
              </el-button>
            </el-tooltip>
          </div>
        </right-toolbar>
      </el-row>

      <!-- list形式 -->
      <div v-if="showType == 'list'" style="margin-top: 16px">
        <el-table v-loading="loading" :data="cardList" :border="false" @selection-change="handleSelectionChange">
          <el-table-column type="selection" align="center" />
          <el-table-column :label="$t('card.sim.id')" align="center" prop="id" width="60px">
            <template #default="scope">
              <span>{{ scope.row.id ?? '--' }}</span>
            </template>
          </el-table-column>
          <el-table-column :label="$t('card.sim.iccid')" align="center" prop="iccid" width="200px">
            <template #default="scope">
              <span>{{ scope.row.iccid || scope.row.iccid === 0 ? scope.row.iccid : '--' }}</span>
            </template>
          </el-table-column>
          <el-table-column :label="$t('card.sim.imsi')" align="center" prop="imsi" width="200px">
            <template #default="scope">
              <span>{{ scope.row.imsi || scope.row.imsi === 0 ? scope.row.imsi : '--' }}</span>
            </template>
          </el-table-column>
          <el-table-column :label="$t('card.sim.card_number')" align="center" prop="msisdn" width="200px">
            <template #default="scope">
              <span>{{ scope.row.msisdn || scope.row.msisdn === 0 ? scope.row.msisdn : '--' }}</span>
            </template>
          </el-table-column>
          <el-table-column :label="$t('card.sim.access_number')" align="center" prop="accessNumber" width="200px">
            <template #default="scope">
              <span>{{ scope.row.accessNumber || scope.row.accessNumber === 0 ? scope.row.accessNumber : '--' }}</span>
            </template>
          </el-table-column>
          <el-table-column :label="$t('card.sim.operator')" align="center" prop="operator" width="100px">
            <template #default="scope"><dict-tag :options="iot_card_operator" :value="scope.row.operator" /></template>
          </el-table-column>
          <el-table-column :label="$t('card.sim.card_status')" align="center" prop="cardStatus" width="100px">
            <template #default="scope"><dict-tag :options="iot_card_status" :value="scope.row.cardStatus" /></template>
          </el-table-column>
          <el-table-column :label="$t('card.sim.total_data')" align="center" prop="totalData" width="120px">
            <template #default="scope">
              <span>
                {{ scope.row.totalData || scope.row.totalData === 0 ? Number(scope.row.totalData).toFixed(2) : '--' }}
              </span>
            </template>
          </el-table-column>
          <el-table-column :label="$t('card.sim.data_used')" align="center" prop="dataUsed" width="120px">
            <template #default="scope">
              <span>
                {{ scope.row.dataUsed || scope.row.dataUsed === 0 ? Number(scope.row.dataUsed).toFixed(2) : '--' }}
              </span>
            </template>
          </el-table-column>
          <el-table-column :label="$t('card.sim.data_remaining')" align="center" prop="dataRemaining" width="120px">
            <template #default="scope">
              <span>
                {{
                  scope.row.dataRemaining || scope.row.dataRemaining === 0
                    ? Number(scope.row.dataRemaining).toFixed(2)
                    : '--'
                }}
              </span>
            </template>
          </el-table-column>
          <el-table-column
            :label="$t('card.sim.data_alert_threshold')"
            align="center"
            prop="dataAlertThreshold"
            width="150px"
          >
            <template #default="scope">
              <span>
                {{
                  scope.row.dataAlertThreshold || scope.row.dataAlertThreshold === 0
                    ? scope.row.dataAlertThreshold
                    : '--'
                }}
              </span>
            </template>
          </el-table-column>
          <el-table-column :label="$t('card.sim.card_platform')" align="center" prop="cardPlatformName" width="120px">
            <template #default="scope">
              <span>{{ getPlatformValue(scope.row.cardPlatformId) || '--' }}</span>
            </template>
          </el-table-column>
          <el-table-column
            fixed="right"
            :label="$t('card.platform.operation')"
            align="center"
            class-name="small-padding fixed-width"
          >
            <template #default="scope">
              <el-button size="small" type="primary" link :icon="Refresh" @click="handleSyncInfo(scope.row)">
                {{ $t('card.platform.synchronize') }}
              </el-button>
              <el-button size="small" type="primary" link :icon="View" @click="handleView(scope.row)">
                {{ $t('card.platform.view') }}
              </el-button>
              <el-button
                size="small"
                type="primary"
                link
                :icon="Delete"
                @click="handleDelete(scope.row)"
                v-hasPermi="['iot:card:remove']"
              >
                {{ $t('card.platform.delete') }}
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        <pagination
          v-show="total > 0"
          :total="total"
          v-model:page="queryParams.pageNum"
          v-model:limit="queryParams.pageSize"
          :pageSizes="[12, 24, 36, 60]"
          @pagination="getList"
        />
      </div>

      <!-- 卡片形式 -->
      <div v-if="showType == 'card'" style="margin-top: 16px">
        <el-row :gutter="20" v-loading="loading">
          <el-col
            :xs="24"
            :sm="12"
            :md="12"
            :lg="8"
            :xl="6"
            v-for="(item, index) in cardList"
            :key="index"
            class="card-item"
          >
            <el-card :body-style="{ padding: '0px' }" shadow="always" class="card-item">
              <div class="item-title">
                <div>
                  <el-image class="img" :src="getOperatorImage(item.operator)" fit="cover"></el-image>
                </div>
                <div class="title">
                  <div class="name">{{ item.msisdn || item.msisdn === 0 ? item.msisdn : '--' }}</div>
                  <div class="tag-wrap">
                    <el-tag type="info" size="small">{{ item.tenantName ?? '--' }}</el-tag>
                    <el-tag style="margin-left: 6px" v-if="item.cardPlatformId" type="info" size="small">
                      {{ getPlatformValue(item.cardPlatformId) || '--' }}
                    </el-tag>
                  </div>
                </div>
                <div class="status">
                  <div class="icon-wrap">
                    <dict-tag :options="iot_card_status" :value="item.cardStatus" style="line-height: 15px" />
                  </div>
                </div>
              </div>
              <el-row :gutter="10">
                <el-col :span="14" style="padding: 0px 0 0 30px; margin-bottom: -10px">
                  <el-descriptions
                    :column="1"
                    size="small"
                    style="margin-top: 10px; white-space: nowrap; overflow: hidden"
                  >
                    <el-descriptions-item :label="$t('card.sim.iccid')">{{ item.iccid || '--' }}</el-descriptions-item>
                  </el-descriptions>
                </el-col>
                <el-col :span="10" style="padding: 0px 30px 0 0px; margin-bottom: -10px">
                  <el-descriptions :column="1" size="small" style="margin-top: 10px; white-space: nowrap">
                    <el-descriptions-item :label="$t('card.sim.total_data')">
                      {{ item.totalData || item.totalData === 0 ? Number(item.totalData).toFixed(2) : '--' }} MB
                    </el-descriptions-item>
                  </el-descriptions>
                </el-col>
              </el-row>
              <el-row :gutter="10">
                <el-col :span="14" style="padding: 0px 0 0 30px; margin-bottom: -10px">
                  <el-descriptions :column="1" size="small" style="margin-top: 10px; white-space: nowrap">
                    <el-descriptions-item :label="$t('card.sim.activate_time')">
                      {{ item.activateTime ? item.activateTime : '--' }}
                    </el-descriptions-item>
                  </el-descriptions>
                </el-col>
                <el-col :span="10" style="padding: 0px 30px 0 0px; margin-bottom: -10px">
                  <el-descriptions :column="1" size="small" style="margin-top: 10px; white-space: nowrap">
                    <el-descriptions-item :label="$t('card.sim.data_used')">
                      {{ item.dataUsed || item.dataUsed === 0 ? Number(item.dataUsed).toFixed(2) : '--' }} MB
                    </el-descriptions-item>
                  </el-descriptions>
                </el-col>
              </el-row>
              <el-row :gutter="10">
                <el-col :span="14" style="padding: 0px 0 0 30px">
                  <el-descriptions :column="1" size="small" style="margin-top: 10px; white-space: nowrap">
                    <el-descriptions-item :label="$t('card.sim.data_alert_threshold')">
                      {{
                        item.dataAlertThreshold || item.dataAlertThreshold === 0 ? item.dataAlertThreshold + '%' : '--'
                      }}
                    </el-descriptions-item>
                  </el-descriptions>
                </el-col>
                <el-col :span="10" style="padding: 0px 30px 0 0px">
                  <el-descriptions :column="1" size="small" style="margin-top: 10px; white-space: nowrap">
                    <el-descriptions-item :label="$t('card.sim.data_remaining')">
                      {{
                        item.dataRemaining || item.dataRemaining === 0 ? Number(item.dataRemaining).toFixed(2) : '--'
                      }}
                      MB
                    </el-descriptions-item>
                  </el-descriptions>
                </el-col>
              </el-row>
              <el-divider class="divider" />
              <div class="action-row">
                <el-button size="small" link :icon="View" @click="handleView(item)">
                  {{ $t('card.platform.view') }}
                </el-button>
                <span class="action-separator">|</span>
                <el-button size="small" link :icon="Refresh" @click="handleSyncInfo(item)">
                  {{ $t('card.platform.synchronize') }}
                </el-button>
                <span style="width: 1px; margin: 0px 10px; font-size: 16px; color: #dcdfe6">|</span>
                <el-button
                  size="small"
                  link
                  :icon="Delete"
                  @click="handleDelete(item)"
                  v-hasPermi="['iot:card:remove']"
                >
                  {{ $t('card.platform.delete') }}
                </el-button>
              </div>
            </el-card>
          </el-col>
        </el-row>
        <el-empty :description="$t('card.sim.none')" v-if="total == 0"></el-empty>
        <pagination
          style="margin: 0 0 20px 0"
          v-show="total > 0"
          :total="total"
          v-model:page="queryParams.pageNum"
          v-model:limit="queryParams.pageSize"
          :pageSizes="[12, 24, 36, 60]"
          @pagination="getList"
        />
      </div>
    </el-card>

    <!-- 添加联网卡对话框 -->
    <el-dialog :title="title" v-model="dialogOpen" width="600px" append-to-body>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item :label="$t('card.sim.card_platform')" prop="cardPlatformId">
          <el-select
            v-model="form.cardPlatformId"
            :placeholder="$t('card.sim.please_select_card_platform')"
            style="width: 400px"
          >
            <el-option v-for="item in cardPlatformList" :key="item.id" :label="item.name" :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item :label="$t('card.sim.iccid')" prop="iccid">
          <el-input
            v-model="form.iccid"
            :placeholder="$t('card.sim.please_enter_iccid')"
            style="width: 400px"
            @input="(val) => handleInput(val, 'iccid')"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button type="primary" @click="handleSubmit">{{ $t('card.platform.ok') }}</el-button>
        <el-button @click="handleCancel">{{ $t('card.platform.cancel') }}</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, getCurrentInstance, type ComponentInternalInstance } from 'vue';
import {
  Search,
  Refresh,
  Plus,
  Delete,
  Download,
  View,
  Document,
  Grid,
  ArrowUp,
  ArrowDown,
} from '@element-plus/icons-vue';
import { useRouter } from 'vue-router';
import { listCard, delCard, addCard } from '@/api/iot/card';
import { listCardPlatform } from '@/api/iot/cardPlatform';
import { useDict } from '@/utils/dict';
import mobileTitleImg from '@/assets/images/mobile_title.png';
import unicomTitleImg from '@/assets/images/unicom_title.png';
import telecomTitleImg from '@/assets/images/telecom_title.png';

const { proxy } = getCurrentInstance() as any;
const router = useRouter();
const { iot_card_operator, iot_card_status } = useDict('iot_card_operator', 'iot_card_status');

const loading = ref(true);
const ids = ref<any[]>([]);
const single = ref(true);
const multiple = ref(true);
const showSearch = ref(true);
const total = ref(0);
const cardList = ref<any[]>([]);
const cardPlatformList = ref<any[]>([]);
const title = ref('');
const dialogOpen = ref(false);
const showType = ref('card');
const isListView = ref(true);
const searchShow = ref(false);

const queryFormRef = ref<any>(null);
const formRef = ref<any>(null);

const queryParams = reactive({
  pageNum: 1,
  pageSize: 12,
  iccid: null as any,
  imsi: null as any,
  msisdn: null as any,
  cardStatus: null as any,
  operator: null as any,
  cardPlatformId: null as any,
});

const form = ref<any>({});

const rules = reactive<any>({
  cardPlatformId: [{ required: true, message: proxy?.$t('card.sim.card_platform_not_empty'), trigger: 'change' }],
  iccid: [{ required: true, message: proxy?.$t('card.sim.iccid_not_empty'), trigger: 'blur' }],
});

const getList = () => {
  loading.value = true;
  listCard(queryParams)
    .then((res: any) => {
      if (res.code === 200) {
        cardList.value = res.rows;
        total.value = res.total;
      }
      loading.value = false;
    })
    .catch(() => {
      loading.value = false;
    });
};

const getOperatorImage = (operator: any) => {
  switch (String(operator)) {
    case 'CMCC':
      return mobileTitleImg;
    case 'CUCC':
      return unicomTitleImg;
    case 'CTCC':
      return telecomTitleImg;
    default:
      return mobileTitleImg;
  }
};

const handleInput = (value: string, field: string) => {
  const filtered = value.replace(/[\u4e00-\u9fa5\s]/g, '');
  if (filtered !== value) form.value[field] = filtered;
};

const getCardPlatfromList = () => {
  listCardPlatform()
    .then((res: any) => {
      if (res.code === 200) cardPlatformList.value = res.rows;
    })
    .catch((err: any) => {
      console.log(err);
    });
};

const getPlatformValue = (cardPlatformId: any) => {
  if (!cardPlatformId) return '';
  const platform = cardPlatformList.value.find((item: any) => item.id === cardPlatformId);
  return platform ? platform.name : '';
};

const searchChange = () => {
  searchShow.value = !searchShow.value;
};

const reset = () => {
  form.value = { id: null, iccid: null, cardPlatformId: null };
  proxy?.resetForm('formRef');
};

const handleQuery = () => {
  queryParams.pageNum = 1;
  getList();
};
const resetQuery = () => {
  queryFormRef.value?.resetFields();
  queryParams.pageNum = 1;
  queryParams.pageSize = 12;
  handleQuery();
};

const handleSelectionChange = (selection: any[]) => {
  ids.value = selection.map((item: any) => item.id);
  single.value = selection.length !== 1;
  multiple.value = !selection.length;
};

const handleAdd = () => {
  reset();
  dialogOpen.value = true;
  title.value = proxy?.$t('card.sim.add_title');
};

const handleSubmit = () => {
  const params = { cardPlatformId: form.value.cardPlatformId, iccid: form.value.iccid };
  formRef.value?.validate((valid: boolean) => {
    if (valid) {
      addCard(params).then((res: any) => {
        if (res.code === 200) {
          proxy?.$modal.msgSuccess(proxy?.$t('card.platform.add_success'));
          dialogOpen.value = false;
          getList();
        }
      });
    }
  });
};

const handleCancel = () => {
  dialogOpen.value = false;
  reset();
};

const handleDelete = (row: any) => {
  const delIds = row.id || ids.value;
  proxy?.$modal
    .confirm(proxy?.$t('card.sim.confirm_delete', { ids: delIds }))
    .then(() => {
      return delCard(delIds);
    })
    .then(() => {
      getList();
      proxy?.$modal.msgSuccess(proxy?.$t('card.platform.delete_success'));
    })
    .catch(() => {});
};

const handleExport = () => {
  proxy?.download('iot/card/export', { ...queryParams }, `card_${new Date().getTime()}.xlsx`);
};

const handleView = (row: any) => {
  router.push({ path: '/iot/card/sim/view', query: { id: row.id } });
};

const handleSyncInfo = (row: any) => {
  const params = { cardPlatformId: row.cardPlatformId, iccid: row.iccid };
  addCard(params).then((res: any) => {
    if (res.code === 200) {
      proxy?.$modal.msgSuccess(proxy?.$t('card.platform.synchronize_success'));
      getList();
    }
  });
};

const handleShowList = () => {
  isListView.value = false;
  showType.value = 'list';
};
const handleShowCard = () => {
  isListView.value = true;
  showType.value = 'card';
};

/** 切换显示方式 */
const handleChangeShowType = () => {
  showType.value = showType.value == 'card' ? 'list' : 'card';
  isListView.value = showType.value == 'card';
};

getList();
getCardPlatfromList();
</script>

<style lang="scss" scoped>
.app-content {
  padding: 20px;
  .search-card {
    margin-bottom: 15px;
    padding: 3px 0;
  }
  .search-form {
    margin-bottom: -22.5px;
  }
  .card-item {
    margin-bottom: 20px;
    text-align: center;
    .card-item {
      background: #ffffff;
      border-radius: 8px;
      border: 1px solid #dcdfe6;

      :deep(.el-card__body) {
        overflow: hidden;
      }

      .item-title {
        display: -webkit-box;
        flex-direction: row;
        align-items: center;
        position: relative;
        padding: 18px;
        .img {
          height: 58px;
          width: 58px;
          border-radius: 10px;
        }
        .title {
          flex: 1;
          text-align: left;
          margin-left: 16px;
          .name {
            font-weight: 500;
            font-size: 16px;
            color: #303133;
            line-height: 22px;
          }
          .tag-wrap {
            margin-top: 10px;
            display: flex;
            flex-direction: row;
          }
        }
        .status {
          position: absolute;
          right: -1px;
          top: 24px;
          .icon-wrap {
            display: flex;
            flex-direction: row;
            align-items: center;
            padding-right: 15px;
          }
        }
      }
    }
  }
}
.divider {
  margin: 20px 0 0;
  height: 1px;
  background: #dcdfe6;
}

.action-row {
  height: 39px;
  padding: 0 10px;
  display: flex;
  align-items: center;
  justify-content: space-evenly;
  overflow: hidden;
  white-space: nowrap;
}

.action-row :deep(.el-button) {
  margin: 0;
  padding: 0 4px;
  min-width: 0;
  white-space: nowrap;
}

.action-separator {
  width: 1px;
  margin: 0 8px;
  font-size: 14px;
  color: #dcdfe6;
  line-height: 1;
  flex-shrink: 0;
}
.button-group {
  width: 65px;
  height: 32.5px;
  display: flex;
  align-items: center;
  margin-right: 12px;
  background: #ffffff;
  .toggle-button {
    width: 32.5px;
    height: 32.5px;
    display: flex;
    justify-content: center;
    align-items: center;
    border: 1px solid #dcdfe6;
    background: #fff;
  }
  :deep(.el-button:hover),
  .el-button:focus {
    color: unset;
  }
  .active {
    background-color: #e0e0e0;
    border-color: #dcdfe6;
  }
  .card-button {
    border-radius: 4px 0px 0px 4px !important;
  }
  .list-button {
    border-radius: 0px 4px 4px 0px !important;
  }
  .separator {
    width: 1px;
    height: 32px;
    background: #dcdfe6;
  }
  :deep(::-webkit-scrollbar) {
    width: 0;
    height: 0;
  }
}
</style>
