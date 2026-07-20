<template>
  <el-form ref="formRef" :model="user" :rules="rules" label-width="80px">
    <el-form-item :label="$t('user.index.098976-11')" prop="nickName">
      <el-input style="width: 60%" v-model="user.nickName" maxlength="30" />
    </el-form-item>
    <el-form-item :label="$t('user.index.098976-13')" prop="phonenumber">
      <el-input style="width: 60%" v-model="user.phonenumber" maxlength="11" />
    </el-form-item>
    <el-form-item :label="$t('user.index.098976-19')" prop="email">
      <el-input style="width: 60%" v-model="user.email" maxlength="50" />
    </el-form-item>
    <el-form-item :label="$t('user.userInfo.560923-1')">
      <el-radio-group v-model="user.sex">
        <el-radio value="0">{{ $t('user.userInfo.560923-2') }}</el-radio>
        <el-radio value="1">{{ $t('user.userInfo.560923-3') }}</el-radio>
      </el-radio-group>
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
import { updateUserProfile } from '@/api/system/user';

const props = defineProps({ user: { type: Object, default: () => ({}) } });
const { t } = useI18n();
const { proxy } = getCurrentInstance()!;
const formRef = ref();

const rules = reactive({
  nickName: [{ required: true, message: t('user.index.098976-33'), trigger: 'blur' }],
  email: [
    { required: true, message: t('user.userInfo.560923-0'), trigger: 'blur' },
    { type: 'email', message: t('user.index.098976-37'), trigger: ['blur', 'change'] },
  ],
  phonenumber: [
    { required: true, message: t('user.index.098976-38'), trigger: 'blur' },
    { pattern: /^1[3|4|5|6|7|8|9][0-9]\d{8}$/, message: t('user.index.098976-39'), trigger: 'blur' },
  ],
});

function submit() {
  formRef.value?.validate((valid: boolean) => {
    if (valid) {
      updateUserProfile(props.user).then(() => {
        (proxy as any).$modal.msgSuccess(t('updateSuccess'));
      });
    }
  });
}

function close() {
  (proxy as any).$tab.closePage();
}
</script>
