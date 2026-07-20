<template>
  <el-form ref="formRef" :model="user" :rules="rules" label-width="80px">
    <el-form-item :label="$t('user.resetPwd.450986-0')" prop="oldPassword">
      <el-input
        style="width: 60%"
        v-model="user.oldPassword"
        :placeholder="$t('user.resetPwd.450986-1')"
        type="password"
        show-password
      />
    </el-form-item>
    <el-form-item :label="$t('user.resetPwd.450986-2')" prop="newPassword">
      <el-input
        style="width: 60%"
        v-model="user.newPassword"
        :placeholder="$t('user.resetPwd.450986-3')"
        type="password"
        show-password
      />
    </el-form-item>
    <el-form-item :label="$t('user.resetPwd.450986-4')" prop="confirmPassword">
      <el-input
        style="width: 60%"
        v-model="user.confirmPassword"
        :placeholder="$t('user.resetPwd.450986-5')"
        type="password"
        show-password
      />
    </el-form-item>
    <el-form-item>
      <el-button type="primary" plain @click="submit">{{ $t('save') }}</el-button>
      <el-button @click="close">{{ $t('close') }}</el-button>
    </el-form-item>
  </el-form>
</template>

<script setup lang="ts">
import { reactive, ref, getCurrentInstance } from 'vue';
import { useI18n } from 'vue-i18n';
import { updateUserPwd } from '@/api/system/user';

const { t } = useI18n();
const { proxy } = getCurrentInstance()!;
const formRef = ref();

const user = reactive({
  oldPassword: undefined as string | undefined,
  newPassword: undefined as string | undefined,
  confirmPassword: undefined as string | undefined,
});

const equalToPassword = (_rule: any, value: any, callback: any) => {
  if (user.newPassword !== value) {
    callback(new Error(t('user.resetPwd.450986-6')));
  } else {
    callback();
  }
};

const rules = reactive({
  oldPassword: [{ required: true, message: t('user.resetPwd.450986-7'), trigger: 'blur' }],
  newPassword: [
    { required: true, message: t('user.resetPwd.450986-8'), trigger: 'blur' },
    { min: 6, max: 20, message: t('user.resetPwd.450986-9'), trigger: 'blur' },
    {
      trigger: 'blur',
      validator: (_rule: any, value: any, callback: any) => {
        const passwordreg = /(?![A-Z]*$)(?![a-z]*$)(?![0-9]*$)(?![^a-zA-Z0-9]*$)/;
        if (!passwordreg.test(value)) {
          callback(new Error(t('system.dept.780956-30')));
        } else {
          callback();
        }
      },
    },
  ],
  confirmPassword: [
    { required: true, message: t('user.resetPwd.450986-10'), trigger: 'blur' },
    { required: true, validator: equalToPassword, trigger: 'blur' },
  ],
});

function submit() {
  formRef.value?.validate((valid: boolean) => {
    if (valid) {
      updateUserPwd(user.oldPassword, user.newPassword).then(() => {
        (proxy as any).$modal.msgSuccess(t('updateSuccess'));
      });
    }
  });
}

function close() {
  (proxy as any).$tab.closePage();
}
</script>
