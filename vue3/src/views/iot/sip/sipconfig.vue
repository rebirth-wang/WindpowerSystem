<template>
  <div class="sip-config">
    <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
      <el-row :gutter="100">
        <el-col :xs="24" :sm="24" :md="12" :lg="12" :xl="8">
          <el-form-item :label="$t('sip.sipConfig.998537-1')" prop="ip">
            <el-input style="width: 100%" v-model="form.ip" disabled />
          </el-form-item>
          <el-form-item :label="$t('sip.sipConfig.998537-2')" prop="domainAlias">
            <el-input style="width: 100%" v-model="form.domainAlias" />
          </el-form-item>
          <el-form-item :label="$t('sip.sipConfig.998537-3')" prop="password">
            <el-input style="width: 100%" v-model="form.password" :placeholder="$t('sip.sipConfig.998537-4')" />
          </el-form-item>
          <el-form-item :label="$t('sip.sipConfig.998537-0')" prop="isdefault">
            <el-switch v-model="form.isdefault" :active-value="1" :inactive-value="0" />
          </el-form-item>
        </el-col>
        <el-col :xs="24" :sm="24" :md="12" :lg="12" :xl="8">
          <el-form-item :label="$t('sip.sipConfig.998537-6')" prop="port">
            <el-input style="width: 100%" v-model="form.port" type="number" disabled />
          </el-form-item>
          <el-form-item :label="$t('sip.sipConfig.998537-7')" prop="serverSipid">
            <el-input style="width: 100%" v-model="form.serverSipid" />
          </el-form-item>
          <el-form-item :label="$t('sip.sipConfig.998537-5')">
            <el-input style="width: 100%" v-model="accessWay" disabled></el-input>
          </el-form-item>
        </el-col>
        <el-col :xs="23" :sm="23" :md="23" :lg="23" :xl="15">
          <el-form-item style="text-align: center; margin-top: 20px">
            <el-button
              v-show="form.id && productInfo.status === 1"
              v-hasPermi="['iot:video:edit']"
              type="primary"
              @click="submitForm"
            >
              {{ $t('update') }}
            </el-button>
            <el-button
              v-show="!form.id && productInfo.status === 1"
              v-hasPermi="['iot:video:add']"
              type="primary"
              @click="submitForm"
            >
              {{ $t('add') }}
            </el-button>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, getCurrentInstance } from 'vue';
import { getSipconfig, addSipconfig, updateSipconfig } from '@/api/iot/sipConfig';

const { proxy } = getCurrentInstance() as any;

const props = defineProps({
  product: { type: Object, default: null },
});

const productInfo = ref<any>({});
const formRef = ref<any>(null);
const form = ref<any>({});
const accessWay = ref<string>('国标GB28181');
const rules = ref<any>({
  domainAlias: [{ required: true, message: proxy.$t('sip.sipConfig.998537-8'), trigger: 'blur' }],
  serverSipid: [{ required: true, message: proxy.$t('sip.sipConfig.998537-9'), trigger: 'blur' }],
  password: [{ required: true, message: proxy.$t('sip.sipConfig.998537-10'), trigger: 'blur' }],
});

/** 获取产品下第一条SIP配置 */
function getSipConfig() {
  getSipconfig(productInfo.value.productId).then((response: any) => {
    form.value = response.data || {};
  });
}

/** 提交按钮 */
function submitForm() {
  formRef.value?.validate((valid: boolean) => {
    if (valid) {
      form.value.productId = props.product.productId;
      if (form.value.isdefault == null) {
        form.value.isdefault = 0;
      }
      if (form.value.id != null) {
        updateSipconfig(form.value).then(() => {
          proxy.$modal.msgSuccess('修改成功');
        });
      } else {
        addSipconfig(form.value).then(() => {
          proxy.$modal.msgSuccess('新增成功');
          getSipConfig();
        });
      }
    }
  });
}

watch(
  () => props.product,
  (newVal) => {
    productInfo.value = { ...newVal };
    if (productInfo.value && !!productInfo.value.productId) {
      if (!form.value.id) {
        getSipConfig();
      }
    }
  },
  { immediate: true }
);
</script>

<style lang="scss" scoped>
.sip-config {
  margin-top: 16px;
}
</style>
