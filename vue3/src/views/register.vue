<template>
  <div class="register">
    <div
      class="register-form"
      style="
        max-width: 500px;
        margin: 100px auto;
        padding: 40px;
        background: #fff;
        border-radius: 8px;
        box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
      "
    >
      <h2 style="text-align: center; margin-bottom: 30px">{{ $t('register.974236-0') }}</h2>
      <el-form ref="registerFormRef" :model="registerForm" :rules="registerRules">
        <el-form-item prop="username">
          <el-input v-model="registerForm.username" :placeholder="$t('login.989807-4')" prefix-icon="User" />
        </el-form-item>
        <el-form-item prop="phonenumber">
          <el-input v-model="registerForm.phonenumber" placeholder="手机号" prefix-icon="Phone" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="registerForm.password"
            type="password"
            :placeholder="$t('login.989807-5')"
            prefix-icon="Lock"
            show-password
          />
        </el-form-item>
        <el-form-item prop="confirmPassword">
          <el-input
            v-model="registerForm.confirmPassword"
            type="password"
            placeholder="确认密码"
            prefix-icon="Lock"
            show-password
          />
        </el-form-item>
        <el-form-item v-if="captchaOnOff" prop="code">
          <div style="display: flex; gap: 10px; width: 100%">
            <el-input v-model="registerForm.code" placeholder="验证码" style="flex: 1" />
            <img :src="codeUrl" @click="getCode" style="height: 38px; cursor: pointer" />
          </div>
        </el-form-item>
        <el-form-item>
          <el-button :loading="loading" type="primary" style="width: 100%" @click="handleRegister">
            <span v-if="!loading">注 册</span>
            <span v-else>注 册 中...</span>
          </el-button>
        </el-form-item>
        <div style="text-align: center">
          <router-link to="/login" style="color: #409eff">已有账号？去登录</router-link>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { ElMessage, type FormInstance, type FormRules } from 'element-plus';
import { getCodeImg, register } from '@/api/login';

const router = useRouter();
const route = useRoute();
const registerFormRef = ref<FormInstance>();
const loading = ref(false);
const codeUrl = ref('');
const captchaOnOff = ref(true);

const registerForm = reactive({
  username: '',
  phonenumber: '',
  password: '',
  confirmPassword: '',
  code: '',
  uuid: '',
});

const equalToPassword = (rule: any, value: any, callback: any) => {
  if (registerForm.password !== value) {
    callback(new Error('两次输入的密码不一致'));
  } else {
    callback();
  }
};

const registerRules = reactive<FormRules>({
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, max: 20, message: '用户名长度在 2 到 20 个字符', trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 5, max: 20, message: '密码长度在 5 到 20 个字符', trigger: 'blur' },
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    { validator: equalToPassword, trigger: 'blur' },
  ],
  code: [{ required: true, message: '请输入验证码', trigger: 'change' }],
});

function getCode() {
  getCodeImg().then((res: any) => {
    captchaOnOff.value = res.captchaOnOff !== undefined ? res.captchaOnOff : true;
    if (captchaOnOff.value) {
      codeUrl.value = 'data:image/gif;base64,' + res.img;
      registerForm.uuid = res.uuid;
    }
  });
}

function handleRegister() {
  registerFormRef.value?.validate(async (valid: boolean) => {
    if (valid) {
      loading.value = true;
      try {
        await register(registerForm);
        ElMessage.success('注册成功');
        router.push('/login');
      } catch (error: any) {
        loading.value = false;
        if (captchaOnOff.value) getCode();
      }
    }
  });
}

onMounted(() => {
  getCode();
});
</script>

<style scoped>
.register {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>
