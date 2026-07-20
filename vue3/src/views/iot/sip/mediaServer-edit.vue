<template>
  <el-dialog
    :title="dialogTitle"
    v-model="open"
    :width="step === 1 ? '600px' : '1000px'"
    top="2rem"
    append-to-body
    :close-on-click-modal="false"
    @close="handleClose"
  >
    <div id="formStep" style="margin-top: 1rem; margin-right: 20px">
      <!-- Step 1 -->
      <el-form v-if="step === 1" ref="checkFormRef" :rules="checkRules" :model="checkForm" label-width="125px">
        <el-form-item :label="$t('sip.mediaServerEdit.998534-1')" prop="ip">
          <el-input v-model="checkForm.ip" :placeholder="$t('sip.mediaServerEdit.998534-2')" style="width: 400px" />
        </el-form-item>
        <el-form-item :label="$t('sip.mediaServerEdit.998534-3')" prop="portHttp">
          <el-input
            v-model="checkForm.portHttp"
            :placeholder="$t('sip.mediaServerEdit.998534-4')"
            style="width: 400px"
          />
        </el-form-item>
        <el-form-item :label="$t('sip.mediaServerEdit.998534-5')" prop="secret">
          <el-input v-model="checkForm.secret" :placeholder="$t('sip.mediaServerEdit.998534-6')" style="width: 400px" />
        </el-form-item>
        <el-form-item>
          <div style="float: right; font-size: 28px">
            <el-button type="primary" @click="handleCheck" :loading="checking">
              {{ $t('notify.template.index.333542-7') }}
            </el-button>
            <el-button type="primary" v-if="serverCheck === 1" @click="step = 2">{{ $t('next') }}</el-button>
            <el-button @click="open = false">{{ $t('cancel') }}</el-button>
          </div>
        </el-form-item>
      </el-form>

      <!-- Step 2/3 -->
      <el-row :gutter="24" v-if="step === 2">
        <el-col :span="12">
          <el-form
            class="left-edit-form"
            ref="editFormRefLeft"
            :rules="editRules"
            :model="form"
            label-width="130px"
            :disabled="isView"
          >
            <el-form-item :label="$t('sip.mediaServerEdit.998534-7')" prop="serverId">
              <el-input style="width: 320px" v-model="form.serverId" />
            </el-form-item>
            <el-form-item :label="$t('sip.mediaServerEdit.998534-8')" prop="ip">
              <el-input style="width: 320px" v-model="form.ip" />
            </el-form-item>
            <el-form-item :label="$t('sip.mediaServerEdit.998534-9')" prop="protocol">
              <el-select style="width: 320px" v-model="form.protocol">
                <el-option label="http" value="http" />
                <el-option label="https" value="https" />
              </el-select>
            </el-form-item>
            <el-form-item class="hook-item" label="HookUrl" prop="hookurl">
              <el-input style="width: 320px" v-model="form.hookurl" placeholder="HookUrl" />
            </el-form-item>
            <el-form-item class="port-row" :label="$t('sip.mediaServerEdit.998534-10')" prop="portHttp">
              <el-input style="width: 320px" v-model="form.portHttp" />
            </el-form-item>
            <el-form-item class="port-row" :label="$t('sip.mediaServerEdit.998534-11')" prop="portHttps">
              <el-input
                style="width: 320px"
                v-model="form.portHttps"
                :placeholder="$t('sip.mediaServerEdit.998534-11')"
              />
            </el-form-item>
            <el-form-item class="port-row" :label="$t('sip.mediaServerEdit.998534-12')" prop="portRtsp">
              <el-input style="width: 320px" v-model="form.portRtsp" />
            </el-form-item>
          </el-form>
        </el-col>
        <el-col :span="12">
          <el-form ref="editFormRefRight" :rules="editRules" :model="form" label-width="120px" :disabled="isView">
            <el-form-item :label="$t('sip.mediaServerEdit.998534-13')" prop="secret">
              <el-input style="width: 320px" v-model="form.secret" disabled />
            </el-form-item>
            <el-form-item :label="$t('sip.mediaServerEdit.998534-14')" prop="domainAlias">
              <el-input style="width: 320px" v-model="form.domainAlias" />
            </el-form-item>
            <el-form-item :label="$t('sip.mediaServerEdit.998534-15')">
              <el-switch v-model="form.autoConfig" />
            </el-form-item>
            <el-form-item :label="$t('sip.mediaServerEdit.998534-16')">
              <el-radio-group v-model="form.rtpEnable" @change="portRangeChange">
                <el-radio :value="false">{{ $t('sip.mediaServerEdit.998534-18') }}</el-radio>
                <el-radio :value="true">{{ $t('sip.mediaServerEdit.998534-17') }}</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item v-if="!form.rtpEnable" :label="$t('sip.mediaServerEdit.998534-19')" prop="rtpProxyPort">
              <el-input style="width: 320px" v-model="form.rtpProxyPort" />
            </el-form-item>
            <el-form-item v-else :label="$t('sip.mediaServerEdit.998534-19')">
              <el-input style="width: 155px" v-model="rtpPortRange1" @change="portRangeChange" />
              <el-input style="width: 155px; margin-left: 10px" v-model="rtpPortRange2" @change="portRangeChange" />
            </el-form-item>
            <el-form-item :label="$t('sip.mediaServerEdit.998534-22')" prop="portRtmp">
              <el-input style="width: 320px" v-model="form.portRtmp" />
            </el-form-item>
            <el-form-item :label="$t('sip.mediaServerEdit.998534-23')" prop="recordPort">
              <el-input style="width: 320px" v-model="form.recordPort" />
            </el-form-item>
            <el-form-item>
              <div style="float: right">
                <el-button type="primary" @click="submitForm" v-if="!isView">{{ $t('confirm') }}</el-button>
                <el-button @click="open = false" v-if="!isView">{{ $t('close') }}</el-button>
              </div>
            </el-form-item>
          </el-form>
        </el-col>
      </el-row>
    </div>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, computed, getCurrentInstance } from 'vue';
import { checkmediaServer, addmediaServer, updatemediaServer } from '@/api/iot/mediaServer';

const { proxy } = getCurrentInstance() as any;
const emit = defineEmits(['success']);

const open = ref(false);
const step = ref(1);
const isEdit = ref(false);
const isView = ref(false);
const checking = ref(false);
const serverCheck = ref(0);
const checkFormRef = ref<any>(null);
const editFormRefLeft = ref<any>(null);
const editFormRefRight = ref<any>(null);

const checkForm = ref<any>({ ip: '', portHttp: '', secret: '' });
const form = ref<any>({});
const rtpPortRange1 = ref<any>(30000);
const rtpPortRange2 = ref<any>(30100);

const dialogTitle = computed(() =>
  isEdit.value ? proxy.$t('sip.mediaServerEdit.998534-0') : proxy.$t('sip.mediaServerEdit.998534-0')
);

const validateIp = (_rule: any, value: string, callback: any) => {
  const reg =
    /^(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])$/;
  if (!reg.test(value)) return callback(new Error(proxy.$t('sip.mediaServerEdit.998534-24')));
  callback();
};
const validatePort = (_rule: any, value: any, callback: any) => {
  const reg = /^(([0-9]|[1-9]\d{1,3}|[1-5]\d{4}|6[0-5]{2}[0-3][0-5]))$/;
  if (!reg.test(String(value ?? ''))) return callback(new Error(proxy.$t('sip.mediaServerEdit.998534-25')));
  callback();
};

const validateSecret = (_rule: any, value: any, callback: any) => {
  if (!value) return callback(new Error(proxy.$t('sip.mediaServer-edit.998539-6')));
  callback();
};

const checkRules = ref<any>({
  ip: [{ required: true, validator: validateIp, trigger: 'blur' }],
  portHttp: [{ required: true, validator: validatePort, trigger: 'blur' }],
  secret: [{ required: true, validator: validateSecret, trigger: 'blur' }],
});

const editRules = ref<any>({
  ip: [{ required: true, validator: validateIp, trigger: 'blur' }],
  portHttp: [{ required: true, validator: validatePort, trigger: 'blur' }],
  portHttps: [{ required: true, validator: validatePort, trigger: 'blur' }],
  portRtsp: [{ required: true, validator: validatePort, trigger: 'blur' }],
  portRtmp: [{ required: true, validator: validatePort, trigger: 'blur' }],
  recordPort: [{ required: true, validator: validatePort, trigger: 'blur' }],
  secret: [{ required: true, validator: validateSecret, trigger: 'blur' }],
  rtpProxyPort: [
    {
      validator: (_rule: any, value: any, callback: any) => {
        if (form.value.rtpEnable) return callback();
        return validatePort(_rule, value, callback);
      },
      trigger: 'blur',
    },
  ],
});

function openAdd() {
  isView.value = false;
  isEdit.value = false;
  step.value = 1;
  serverCheck.value = 0;
  checkForm.value = { ip: '', portHttp: '', secret: '' };
  form.value = {};
  open.value = true;
}

function openEdit(item: any) {
  isView.value = false;
  isEdit.value = true;
  step.value = 2;
  form.value = {
    ...item,
    autoConfig: item.autoConfig === 1,
    rtpEnable: item.rtpEnable === 1,
  };
  if (form.value.rtpPortRange) {
    const arr = String(form.value.rtpPortRange).split(',');
    if (arr.length > 1) {
      rtpPortRange1.value = arr[0];
      rtpPortRange2.value = arr[1];
    }
  }
  open.value = true;
}

function openView(item: any) {
  openEdit(item);
  isView.value = true;
}

function handleCheck() {
  checkFormRef.value?.validate((valid: boolean) => {
    if (!valid) return;
    checking.value = true;
    serverCheck.value = 0;
    checkmediaServer({ ip: checkForm.value.ip, port: checkForm.value.portHttp, secret: checkForm.value.secret })
      .then((response: any) => {
        checking.value = false;
        if (response?.data) {
          form.value = {
            ...response.data,
            ip: checkForm.value.ip,
            portHttp: checkForm.value.portHttp,
            secret: checkForm.value.secret,
            autoConfig: true,
            rtpEnable: true,
            protocol: 'http',
            domainAlias: 'fastbee.com',
            enabled: 1,
            serverId: 'fastbee',
            hookurl: 'java:8080',
            portHttps: 8443,
            recordPort: 18081,
            portRtmp: 1935,
            portRtsp: 554,
            rtpProxyPort: '',
          };
          rtpPortRange1.value = 30000;
          rtpPortRange2.value = 30100;
          serverCheck.value = 1;
          proxy.$modal.alertSuccess(proxy.$t('sip.mediaServerEdit.998534-26'));
        } else {
          serverCheck.value = -1;
          proxy.$modal.alertError(proxy.$t('sip.mediaServerEdit.998534-27'));
        }
      })
      .catch(() => {
        checking.value = false;
        serverCheck.value = -1;
        proxy.$modal.alertError(proxy.$t('sip.mediaServerEdit.998534-27'));
      });
  });
}

function portRangeChange() {
  if (form.value.rtpEnable) {
    form.value.rtpPortRange = `${rtpPortRange1.value},${rtpPortRange2.value}`;
  }
}

function submitForm() {
  const payload = {
    ...form.value,
    autoConfig: form.value.autoConfig ? 1 : 0,
    rtpEnable: form.value.rtpEnable ? 1 : 0,
  };
  if (payload.id) {
    updatemediaServer(payload).then(() => {
      proxy.$modal.msgSuccess(proxy.$t('updateSuccess'));
      open.value = false;
      emit('success');
    });
  } else {
    portRangeChange();
    addmediaServer(payload).then(() => {
      proxy.$modal.msgSuccess(proxy.$t('addSuccess'));
      open.value = false;
      emit('success');
    });
  }
}

function handleClose() {
  step.value = 1;
  serverCheck.value = 0;
  checkForm.value = { ip: '', portHttp: '', secret: '' };
  form.value = {};
  rtpPortRange1.value = 30000;
  rtpPortRange2.value = 30100;
}

defineExpose({ openAdd, openEdit, openView });
</script>

<style scoped lang="scss">
:deep(.el-dialog__body) {
  padding-top: 8px;
  padding-bottom: 12px;
}

#formStep {
  margin-top: 8px !important;
  margin-right: 12px !important;
}

:deep(.el-form-item) {
  margin-bottom: 14px;
}

:deep(.el-form-item__label) {
  color: #303133;
  white-space: nowrap;
  text-align: right;
  padding-right: 12px;
  box-sizing: border-box;
}

:deep(.el-form-item.is-required .el-form-item__label::before) {
  margin-right: 4px;
}

:deep(.el-input__wrapper),
:deep(.el-textarea__inner),
:deep(.el-select__wrapper) {
  border-radius: 4px;
}

:deep(.el-dialog__footer) {
  padding-top: 8px;
}

.dialog-footer,
:deep(.el-form-item:last-child .el-form-item__content) {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

:deep(.left-edit-form .el-form-item) {
  display: flex;
  align-items: center;
}

:deep(.left-edit-form .el-form-item__label) {
  width: 130px !important;
  flex: 0 0 130px !important;
  line-height: 32px !important;
}

:deep(.left-edit-form .el-form-item__content) {
  margin-left: 0 !important;
  width: 320px;
  flex: 0 0 320px;
  line-height: 32px;
}

:deep(.left-edit-form .port-row .el-input__wrapper) {
  min-height: 32px;
  height: 32px;
}
</style>
