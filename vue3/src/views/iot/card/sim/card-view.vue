<template>
  <div class="device-edit">
    <el-card class="top-card" :body-style="{ padding: '26px 20px' }">
      <div class="title-wrap">
        <el-button class="top-button" type="info" size="small" @click="goBack()">
          <el-icon><ArrowLeft /></el-icon>
          {{ $t('product.product-edit.473153-44') }}
        </el-button>
        <span class="info-item">{{ $t('card.sim.card_number') }} ：{{ form.msisdn || '--' }}</span>
        <span class="info-item">
          {{ $t('card.sim.operator') }} ：
          <div class="dict-tag">{{ form.operator }}</div>
        </span>
        <span class="info-item">{{ $t('card.sim.card_platform') }} ：{{ form.cardPlatformName || '--' }}</span>
        <span class="info-item">
          {{ $t('card.sim.card_status') }}：
          <dict-tag :options="iot_card_status" :value="form.cardStatus" style="margin-top: 5px" />
        </span>
        <el-icon
          style="cursor: pointer; font-size: 20px; margin-left: 20px"
          @click="refreshCardInfo"
          v-hasPermi="['iot:card:edit']"
        >
          <Refresh />
        </el-icon>
      </div>
    </el-card>

    <el-card style="padding-bottom: 30px">
      <el-tabs
        id="cardDetailTab"
        class="custom-tabs"
        v-model="activeName"
        tab-position="top"
        style="min-height: 400px"
        lazy
      >
        <el-tab-pane name="basic" :label="$t('card.sim.basic_info')">
          <el-form class="basic-span" ref="formRef" :model="form" label-width="120px">
            <span style="margin-bottom: 20px" class="main-title">{{ $t('card.sim.sim_card_info') }} ：</span>
            <div style="display: flex; gap: 20%; margin-left: 30px">
              <div class="display-row">
                <div class="form-text">
                  <div class="form-title">{{ $t('card.sim.iccid') }} ：</div>
                  <div>{{ form.iccid || '--' }}</div>
                </div>
                <div class="form-text">
                  <div class="form-title">{{ $t('card.sim.imel') }} ：</div>
                  <div>{{ form.imei || '--' }}</div>
                </div>
                <div class="form-text">
                  <div class="form-title">{{ $t('card.sim.data_plan') }} ：</div>
                  <div>{{ form.dataPlan || '--' }}</div>
                </div>
                <div class="form-text">
                  <div class="form-title">{{ $t('card.sim.data_used') }} ：</div>
                  <div style="display: flex; flex-direction: row; align-items: center; gap: 8px">
                    <div>
                      <span style="color: #13ce66">
                        {{ form.dataUsed !== null && form.dataUsed !== undefined ? form.dataUsed : '--' }}
                      </span>
                      &nbsp;MB
                    </div>
                    <el-icon
                      style="cursor: pointer; font-size: 20px"
                      @click="refreshUsage"
                      v-hasPermi="['iot:card:edit']"
                    >
                      <Refresh />
                    </el-icon>
                  </div>
                </div>
                <div class="form-text">
                  <div class="form-title">{{ $t('card.sim.activate_time') }} ：</div>
                  <div>{{ form.activateTime || '--' }}</div>
                </div>
                <div class="form-text">
                  <div class="form-title">{{ $t('card.sim.down_time') }} ：</div>
                  <div>{{ form.downTime || '--' }}</div>
                </div>
              </div>
              <div class="display-row">
                <div class="form-text">
                  <div>{{ $t('card.sim.imsi') }} ：</div>
                  <div>{{ form.imsi || '--' }}</div>
                </div>
                <div class="form-text">
                  <div class="form-title">{{ $t('card.sim.tenant_name') }} ：</div>
                  <div>{{ form.tenantName || '--' }}</div>
                </div>
                <div class="form-text">
                  <div class="form-title">{{ $t('card.sim.total_data') }} ：</div>
                  <div>
                    <span style="color: #4949ff">
                      {{ form.totalData !== null && form.totalData !== undefined ? form.totalData : '--' }}
                    </span>
                    &nbsp;MB
                  </div>
                </div>
                <div class="form-text">
                  <div class="form-title">{{ $t('card.sim.data_remaining') }} ：</div>
                  <div>
                    <span style="color: #ff4949">
                      {{ form.dataRemaining !== null && form.dataRemaining !== undefined ? form.dataRemaining : '--' }}
                    </span>
                    &nbsp;MB
                  </div>
                </div>
                <div class="form-text">
                  <div class="form-title">{{ $t('card.sim.expire_time') }} ：</div>
                  <div>{{ form.expireTime || '--' }}</div>
                </div>
                <div class="form-text">
                  <div class="form-title">{{ $t('card.sim.open_date') }} ：</div>
                  <div>{{ form.openDate || '--' }}</div>
                </div>
              </div>
            </div>

            <div class="main-title" style="margin-top: 15px">{{ $t('card.sim.alarm_settings') }} ：</div>
            <div style="display: flex; gap: 20%; margin-left: 30px">
              <div class="display-row">
                <div class="form-text">
                  <div style="min-width: 140px">{{ $t('card.sim.data_alert_threshold') }} ：</div>
                  <div style="display: flex; flex-direction: row; align-items: center; gap: 8px">
                    <el-tooltip :content="$t('card.sim.data_alert_threshold_tip')" placement="top">
                      <el-icon style="cursor: pointer"><QuestionFilled /></el-icon>
                    </el-tooltip>
                    <el-input
                      v-model="form.dataAlertThreshold"
                      @input="handleDataAlertThresholdInput"
                      :placeholder="$t('card.sim.data_alert_threshold_placeholder')"
                    />
                  </div>
                </div>
              </div>
              <div class="display-row">
                <div class="form-text">
                  <div style="min-width: 120px">{{ $t('card.sim.notify_users') }} ：</div>
                  <el-select
                    v-model="notifyUsers"
                    :placeholder="$t('card.sim.notify_users_placeholder')"
                    multiple
                    @change="handleNotifyUsers"
                  >
                    <el-option
                      v-for="user in userList"
                      :key="user.userId"
                      :value="user.userId"
                      :label="user.userName"
                    ></el-option>
                  </el-select>
                </div>
              </div>
            </div>

            <div class="main-title" style="margin-top: 15px">{{ $t('card.sim.device_blind_info') }} ：</div>
            <div style="display: flex; gap: 20%; margin-left: 30px">
              <div class="display-row">
                <div class="form-text">
                  <div class="form-title">{{ $t('card.sim.device_id') }} ：</div>
                  <div>
                    <el-input v-model="form.serialNumber" :placeholder="$t('card.sim.device_id_placeholder')">
                      <template #append>
                        <el-button @click="openDeviceDialog">{{ $t('card.sim.select') }}</el-button>
                      </template>
                    </el-input>
                  </div>
                </div>
                <div class="form-text">
                  <div class="form-title">{{ $t('card.sim.product') }} ：</div>
                  <div>{{ form.productName || '--' }}</div>
                </div>
              </div>
              <div class="display-row">
                <div class="form-text">
                  <div class="form-title">{{ $t('card.sim.device_name') }} ：</div>
                  <div>{{ form.deviceName || '--' }}</div>
                </div>
              </div>
            </div>
            <el-button type="primary" style="float: right" @click="submitForm">
              {{ $t('card.platform.edit') }}
            </el-button>
          </el-form>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- 设备选择对话框 -->
    <device-selector
      v-model:visible="deviceDialogVisible"
      @device-selected="handleDeviceSelected"
      @close="deviceDialogVisible = false"
    ></device-selector>
  </div>
</template>

<script setup lang="ts">
import { ref, getCurrentInstance } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ArrowLeft, Refresh, QuestionFilled } from '@element-plus/icons-vue';
import { getCard, updateCard, updateUsage, syncCardInfo } from '@/api/iot/card';
import { listUser } from '@/api/system/user';
import DeviceSelector from '@/components/DeviceSelector/index.vue';
import { useDict } from '@/utils/dict';

const { proxy } = getCurrentInstance() as any;
const route = useRoute();
const router = useRouter();
const { iot_card_operator, iot_card_status, iot_card_platform } = useDict(
  'iot_card_operator',
  'iot_card_status',
  'iot_card_platform'
);

const activeName = ref('basic');
const form = ref<any>({});
const deviceDialogVisible = ref(false);
const notifyUsers = ref<number[]>([]);
const userList = ref<any[]>([]);

const getCardInfo = () => {
  const id = route.query?.id;
  if (id) {
    getCard(id).then((response: any) => {
      form.value = response.data;
      notifyUsers.value = form.value.notifyUsers ? form.value.notifyUsers.split(',').map(Number) : [];
      form.value.dataUsed = Number(form.value.dataUsed).toFixed(2);
      form.value.dataRemaining = Number(form.value.dataRemaining).toFixed(2);
      form.value.totalData = Number(form.value.totalData).toFixed(2);
    });
  }
};

const goBack = () => {
  router.push({ path: '/iot/card/sim', query: { t: Date.now(), pageNum: route.query.pageNum as any } });
};

const handleDataAlertThresholdInput = (value: string) => {
  if (value === '') {
    form.value.dataAlertThreshold = '';
    return;
  }
  let val = value.replace(/[^\d.]/g, '').replace(/\.{2,}/g, '.');
  val = val.replace(/(\..*)\./g, '$1');
  let [intPart, decPart] = val.split('.');
  if (intPart) intPart = intPart.replace(/^0+(?!$)/, '');
  if (decPart) decPart = decPart.substring(0, 2);
  val = decPart !== undefined ? `${intPart}.${decPart}` : intPart;
  form.value.dataAlertThreshold = val;
};

const openDeviceDialog = () => {
  deviceDialogVisible.value = true;
};

const handleDeviceSelected = (device: any) => {
  form.value.deviceId = device.deviceId;
  form.value.productId = device.productId;
  form.value.serialNumber = device.serialNumber;
  form.value.deviceName = device.deviceName;
  form.value.productName = device.productName;
};

const submitForm = () => {
  form.value.notifyUsers = notifyUsers.value.join(',');
  const { id, deviceId, dataAlertThreshold, notifyUsers: nu, tenantId, createBy } = form.value;
  const params = { id, deviceId, dataAlertThreshold, notifyUsers: nu, tenantId, createBy };
  updateCard(params).then((response: any) => {
    if (response.code === 200) {
      proxy?.$modal.msgSuccess(proxy?.$t('updateSuccess'));
      getCardInfo();
    } else {
      proxy?.$modal.msgError(response.msg);
    }
  });
};

const getUserList = () => {
  listUser().then((res: any) => {
    if (res.code === 200) userList.value = res.rows;
  });
};

const handleNotifyUsers = (val: number[]) => {
  form.value.notifyUsers = val.join(',');
};

const refreshUsage = () => {
  const params = { iccid: form.value.iccid, cardPlatformId: form.value.cardPlatformId };
  updateUsage(params)
    .then((response: any) => {
      if (response.code === 200) {
        form.value.dataUsed = Number(response.data.dataUsed).toFixed(2);
        form.value.dataRemaining = Number(response.data.dataRemaining).toFixed(2);
        form.value.totalData = Number(response.data.totalData).toFixed(2);
        proxy?.$modal.msgSuccess(proxy?.$t('card.platform.synchronize_success'));
      }
    })
    .catch((error: any) => {
      console.error('刷新流量信息失败:', error);
    });
};

const refreshCardInfo = () => {
  const params = { iccid: form.value.iccid, cardPlatformId: form.value.cardPlatformId };
  syncCardInfo(params)
    .then((response: any) => {
      if (response.code === 200) {
        form.value = response.data;
        proxy?.$modal.msgSuccess(proxy?.$t('card.sim.refresh_successful'));
      }
    })
    .catch((error: any) => {
      console.error('同步物联网卡信息:', error);
    });
};

getCardInfo();
getUserList();
</script>

<style lang="scss" scoped>
.device-edit {
  padding: 20px;
  .top-card {
    margin-bottom: 10px;
    .title-wrap {
      display: flex;
      flex-direction: row;
      align-items: center;
      flex-wrap: wrap;
      .top-button {
        height: 22px;
        color: #909399;
        background: #f4f5f7;
        padding: 0px 8px;
        border: none;
      }
      .info-item {
        font-weight: normal;
        font-size: 14px;
        color: #333333;
        line-height: 20px;
        margin-left: 36px;
        display: flex;
        align-items: center;
        :deep(.el-tag) {
          margin-left: 5px;
        }
      }
    }
  }
  .custom-tabs {
    .basic-span {
      margin-top: 16px;
    }
    :deep(.el-card__body) {
      padding: 0 20px;
    }
    :deep(.el-tabs__active-bar) {
      background-color: transparent;
    }
    :deep(.el-tabs__nav) {
      margin-bottom: 12px;
    }
    :deep(.el-tabs__item) {
      padding: 0px 25px !important;
      box-sizing: border-box;
      display: inline-block;
      list-style: none;
      font-size: 14px;
      font-weight: 500;
      color: #333333;
      position: relative;
    }
    :deep(.el-tabs__item.is-active) {
      color: #fff;
      background-color: #486ff2;
      border-radius: 4px;
      height: 32px;
      line-height: 34px;
    }
  }
  .form-text {
    display: flex;
    align-items: center;
    font-size: 14px;
    color: #606266;
    padding-bottom: 15px;
    margin-top: 15px;
  }
  .display-row {
    width: 30%;
    display: flex;
    flex-direction: column;
  }
  .form-title {
    min-width: 90px;
  }
  .main-title {
    font-size: 15px;
    color: #606061;
    margin-left: 30px;
    font-weight: bolder;
  }
}
</style>
