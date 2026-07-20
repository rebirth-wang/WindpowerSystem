<template>
  <div class="login-wrap">
    <div class="logo-wrap">
      <img class="icon" :src="systemForm.logo" v-if="systemForm.logo" />
      <img class="icon" src="@/assets/images/logo_blue.png" v-else />
      <span class="text" v-if="systemForm.systemName">{{ systemForm.systemName }}</span>
      <span class="text" v-else>{{ $t('login.989807-37') }}</span>
    </div>
    <pre class="introduce-text" v-if="systemForm.description">{{ systemForm.description }}</pre>
    <pre class="introduce-text" v-else>{{ $t('login.989807-38') }}</pre>
    <div class="img-wrap">
      <img style="width: 100%; height: 100%" :src="systemForm.imgUrl" v-if="systemForm.imgUrl" />
      <img style="width: 100%; height: 100%" src="@/assets/images/cover.png" v-else />
    </div>
    <div class="box-wrap">
      <!-- 账号登录 -->
      <div class="form-box" v-show="!isHaveBind && !errorText && formIndex === 1">
        <div class="title-wrap">
          <div v-if="!bindId" class="name">{{ $t('login.989807-1') }}</div>
          <div v-else class="name">{{ $t('login.989807-36') }}</div>
          <langSelect></langSelect>
        </div>
        <pre class="demo-account" v-if="systemForm.accountTip">{{ $t('login.989807-33') }}</pre>
        <el-form class="form-wrap" ref="loginFormRef" :model="loginForm" :rules="loginRules">
          <el-form-item prop="username">
            <div class="item-wrap">
              <div class="input-wrap">
                <input
                  class="inner"
                  v-model="loginForm.username"
                  autocomplete="off"
                  type="text"
                  :placeholder="$t('login.989807-4')"
                />
              </div>
            </div>
          </el-form-item>
          <el-form-item prop="password">
            <div class="item-wrap">
              <div class="input-wrap">
                <input
                  class="inner"
                  v-model="loginForm.password"
                  autocomplete="off"
                  :type="pwdtype"
                  :placeholder="$t('login.989807-5')"
                />
                <el-icon class="icon" @click="handlePasswordTypeChange()"><View /></el-icon>
              </div>
            </div>
          </el-form-item>
          <el-form-item v-if="captchaOnOff" prop="code">
            <div class="item-wrap">
              <div class="input-wrap">
                <input
                  class="inner"
                  v-model="loginForm.code"
                  autocomplete="off"
                  type="text"
                  :placeholder="$t('login.989807-6')"
                />
              </div>
              <img class="input-append" :src="codeUrl" @click="getCode" />
            </div>
          </el-form-item>
          <el-form-item>
            <div class="item-wrap">
              <el-checkbox v-model="loginForm.rememberMe">
                {{ $t('login.989807-7') }}
              </el-checkbox>
              <div class="forget-pwd-link" @click="handleForgetPassword">{{ $t('login.989807-43') }}</div>
            </div>
          </el-form-item>

          <div v-if="!bindId">
            <el-button class="btn" :loading="loading" type="primary" @click.prevent="handleLogin">
              <span v-if="!loading">{{ $t('login.989807-3') }}</span>
              <span v-else>{{ $t('login.989807-13') }}</span>
            </el-button>
          </div>
          <div v-else>
            <el-button class="btn" :loading="loading" type="primary" @click.prevent="handleBindLogin">
              <span v-if="!loading">{{ $t('login.989807-15') }}</span>
              <span v-else>{{ $t('login.989807-16') }}</span>
            </el-button>
            <el-button style="margin: 16px 0 0 0" class="btn" @click.prevent="handleBackLogin">
              <span>{{ $t('login.989807-60') }}</span>
            </el-button>
          </div>
          <div class="other-link">
            <span class="text" @click="handleRegister">{{ $t('login.989807-18') }}</span>
            <el-divider direction="vertical" v-if="systemForm.document"></el-divider>
            <span class="text" @click="handleGotoDoc" v-if="systemForm.document">{{ $t('login.989807-40') }}</span>
            <el-divider direction="vertical" v-if="systemForm.website"></el-divider>
            <span class="text" @click="handleGotoOS" v-if="systemForm.website">{{ $t('login.989807-41') }}</span>
          </div>
          <div class="other-login">
            <span class="text">{{ $t('login.989807-42') }}</span>
            <svg-icon class="icon" icon-class="wechat" style="color: #07c160" @click="handleGotoWeChatLogin" />
            <svg-icon
              class="icon"
              icon-class="envelope"
              style="color: var(--el-color-primary)"
              @click="handleGotoSmsLogin"
            />
          </div>
        </el-form>
      </div>

      <!-- 短信登录 -->
      <div class="form-box" v-show="!isHaveBind && !errorText && formIndex === 2">
        <div class="title-wrap">
          <div class="name">{{ $t('login.989807-2') }}</div>
          <langSelect></langSelect>
        </div>
        <el-form
          style="margin-top: 40px"
          class="form-wrap"
          ref="smsLoginFormRef"
          :model="smsLoginForm"
          :rules="smsLoginRules"
        >
          <el-form-item prop="phonenumber">
            <div class="item-wrap">
              <div class="input-wrap">
                <input
                  class="inner"
                  v-model="smsLoginForm.phonenumber"
                  autocomplete="off"
                  type="text"
                  maxlength="11"
                  :placeholder="$t('login.989807-8')"
                />
              </div>
            </div>
          </el-form-item>
          <el-form-item prop="invitationCode">
            <div class="item-wrap">
              <div class="input-wrap">
                <input
                  class="inner"
                  v-model="smsLoginForm.invitationCode"
                  autocomplete="off"
                  type="text"
                  maxlength="11"
                  :placeholder="$t('login.989807-61')"
                />
              </div>
            </div>
          </el-form-item>
          <el-form-item prop="smsCode">
            <div class="item-wrap">
              <div class="input-wrap">
                <input
                  class="inner"
                  v-model="smsLoginForm.smsCode"
                  autocomplete="off"
                  type="text"
                  :placeholder="$t('login.989807-9')"
                />
              </div>
              <el-button class="input-append" :disabled="isLoginSmsBtn" @click.prevent="getLoginSmsCode()">
                {{ loginSmsBtnText }}
              </el-button>
            </div>
          </el-form-item>
          <div>
            <el-button class="btn" :loading="smsLoading" type="primary" @click.prevent="handleSmsLogin">
              <span v-if="!smsLoading">{{ $t('login.989807-3') }}</span>
              <span v-else>{{ $t('login.989807-13') }}</span>
            </el-button>
          </div>
          <div>
            <el-button style="margin-top: 16px" class="btn" @click="handleGotoLogin">
              <span>{{ $t('login.989807-44') }}</span>
            </el-button>
          </div>
        </el-form>
      </div>

      <!-- 忘记密码 -->
      <div class="form-box" v-show="!isHaveBind && !errorText && formIndex === 3">
        <div class="title-wrap">
          <div class="name">{{ $t('login.989807-43') }}</div>
          <langSelect></langSelect>
        </div>
        <el-form style="margin-top: 40px" class="form-wrap" ref="fpFormRef" :model="fpForm" :rules="fpRules">
          <el-form-item prop="phoneNumber">
            <div class="item-wrap">
              <div class="input-wrap">
                <input
                  class="inner"
                  v-model="fpForm.phoneNumber"
                  autocomplete="off"
                  type="text"
                  maxlength="11"
                  :placeholder="$t('login.989807-8')"
                />
              </div>
            </div>
          </el-form-item>
          <el-form-item prop="code">
            <div class="item-wrap">
              <div class="input-wrap">
                <input
                  class="inner"
                  v-model="fpForm.code"
                  autocomplete="off"
                  type="text"
                  :placeholder="$t('login.989807-9')"
                />
              </div>
              <el-button class="input-append" :disabled="isFpSmsBtn" @click.prevent="getFpSmsCode()">
                {{ fpSmsBtnText }}
              </el-button>
            </div>
          </el-form-item>
          <el-form-item prop="password">
            <div class="item-wrap">
              <div class="input-wrap">
                <input class="inner" v-model="fpForm.password" autocomplete="off" :placeholder="$t('login.989807-5')" />
              </div>
            </div>
          </el-form-item>
          <el-form-item prop="confirmPassword">
            <div class="item-wrap">
              <div class="input-wrap">
                <input
                  class="inner"
                  v-model="fpForm.confirmPassword"
                  autocomplete="off"
                  :placeholder="$t('login.989807-47')"
                />
              </div>
            </div>
          </el-form-item>
          <div>
            <el-button class="btn" :loading="fpLoading" type="primary" @click.prevent="handleChangePassword">
              <span v-if="!fpLoading">{{ $t('login.989807-45') }}</span>
              <span v-else>{{ $t('login.989807-46') }}</span>
            </el-button>
          </div>
          <div>
            <el-button style="margin-top: 16px" class="btn" @click="handleGotoLogin">
              <span>{{ $t('login.989807-44') }}</span>
            </el-button>
          </div>
        </el-form>
      </div>

      <!-- 账号注册 -->
      <div class="form-box" v-show="!isHaveBind && !errorText && formIndex === 4">
        <div class="title-wrap">
          <div v-if="!bindId" class="name">{{ $t('login.989807-52') }}</div>
          <div v-else class="name">{{ $t('login.989807-59') }}</div>
          <langSelect></langSelect>
        </div>
        <el-form
          style="margin-top: 40px"
          class="form-wrap"
          ref="registFormRef"
          :model="registForm"
          :rules="registRules"
        >
          <el-form-item prop="username">
            <div class="item-wrap">
              <div class="input-wrap">
                <input
                  class="inner"
                  v-model="registForm.username"
                  autocomplete="off"
                  type="text"
                  :placeholder="$t('login.989807-4')"
                />
              </div>
            </div>
          </el-form-item>
          <el-form-item prop="phonenumber">
            <div class="item-wrap">
              <div class="input-wrap">
                <input
                  class="inner"
                  v-model="registForm.phonenumber"
                  autocomplete="off"
                  type="text"
                  maxlength="11"
                  :placeholder="$t('login.989807-8')"
                />
              </div>
            </div>
          </el-form-item>
          <el-form-item prop="password">
            <div class="item-wrap">
              <div class="input-wrap">
                <input
                  class="inner"
                  v-model="registForm.password"
                  autocomplete="off"
                  :placeholder="$t('login.989807-5')"
                />
              </div>
            </div>
          </el-form-item>
          <el-form-item prop="confirmPassword">
            <div class="item-wrap">
              <div class="input-wrap">
                <input
                  class="inner"
                  v-model="registForm.confirmPassword"
                  autocomplete="off"
                  :placeholder="$t('login.989807-47')"
                />
              </div>
            </div>
          </el-form-item>
          <el-form-item prop="invitationCode">
            <div class="item-wrap">
              <div class="input-wrap">
                <input
                  class="inner"
                  v-model="registForm.invitationCode"
                  autocomplete="off"
                  type="text"
                  maxlength="11"
                  :placeholder="$t('login.989807-61')"
                />
              </div>
            </div>
          </el-form-item>
          <el-form-item v-if="captchaOnOff" prop="code">
            <div class="item-wrap">
              <div class="input-wrap">
                <input
                  class="inner"
                  v-model="registForm.code"
                  autocomplete="off"
                  type="text"
                  :placeholder="$t('login.989807-6')"
                />
              </div>
              <img class="input-append" :src="codeUrl" @click="getCode" />
            </div>
          </el-form-item>
          <el-form-item>
            <div class="item-wrap" style="justify-content: flex-start">
              <el-checkbox v-model="registForm.agree">{{ $t('login.989807-53') }}</el-checkbox>
              <span class="primary-link" @click="handleGotoPrivacyPolicy">{{ $t('login.989807-54') }}</span>
            </div>
          </el-form-item>
          <div>
            <el-button class="btn" v-if="!bindId" :loading="registLoading" type="primary" @click.prevent="handleRegist">
              <span v-if="!registLoading">{{ $t('login.989807-55') }}</span>
              <span v-else>{{ $t('login.989807-56') }}</span>
            </el-button>
            <el-button class="btn" v-else :loading="registLoading" type="primary" @click.prevent="handleRegistBind">
              <span v-if="!registLoading">{{ $t('login.989807-57') }}</span>
              <span v-else>{{ $t('login.989807-16') }}</span>
            </el-button>
          </div>
          <div>
            <el-button style="margin-top: 16px" class="btn" @click="handleGotoLogin">
              <span>{{ $t('login.989807-44') }}</span>
            </el-button>
          </div>
        </el-form>
      </div>

      <!-- 绑定账号提示 -->
      <div class="form-box" v-if="isHaveBind">
        <div class="mess-text">
          <el-icon style="font-size: 16px"><WarningFilled /></el-icon>
          <span>&nbsp;{{ $t('login.989807-10') }}</span>
          <span class="primary-link" @click="handleRegister">&nbsp;{{ $t('login.989807-12') }}</span>
        </div>
        <el-button style="margin-top: 40px" class="btn" @click="handleGotoLogin">
          <span>{{ $t('login.989807-44') }}</span>
        </el-button>
      </div>

      <!-- 错误信息提示 -->
      <div class="form-box" v-if="errorText">
        <div class="mess-text">
          <el-icon style="font-size: 16px"><WarningFilled /></el-icon>
          <span>{{ errorText }}</span>
        </div>
        <el-button style="margin-top: 40px" class="btn" @click="handleGotoLogin">
          <span>{{ $t('login.989807-44') }}</span>
        </el-button>
      </div>

      <div class="copyright-wrap" v-if="systemForm.copyRight">
        <span>
          Copyright &copy; 2021-2025
          <a target="_blank" href="http://fastbee.cn">FastBee</a>
          All Rights Reserved.
        </span>
        <a target="_blank" href="https://beian.miit.gov.cn/">滇ICP备2023000466号-6</a>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, getCurrentInstance } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useI18n } from 'vue-i18n';
import { ElMessage, ElMessageBox } from 'element-plus';
import { WarningFilled, View } from '@element-plus/icons-vue';
import Cookies from 'js-cookie';
import {
  getCodeImg,
  checkBindId,
  getErrorMsg,
  socialLogin,
  bindLogin,
  getSmsLoginCaptcha,
  smsLogin,
  getSmsForgetPassword,
  forgetPwdReset,
  bindRegister,
} from '@/api/login';
import { register } from '@/api/iot/tool';
import { encrypt, decrypt } from '@/utils/jsencrypt';
import { setToken } from '@/utils/auth';
import { getConfigKey } from '@/api/system/config';
import { useUserStore } from '@/stores/modules/user';
import langSelect from '@/layout/components/LangSelect.vue';

const { t } = useI18n();
const route = useRoute();
const router = useRouter();
const userStore = useUserStore();

// ==================== 表单引用 ====================
const loginFormRef = ref();
const smsLoginFormRef = ref();
const fpFormRef = ref();
const registFormRef = ref();

// ==================== 通用状态 ====================
const formIndex = ref(1); // 1-账号登录 2-短信登录 3-忘记密码 4-注册
const pwdtype = ref('password');
const captchaOnOff = ref(true);
const codeUrl = ref('');
const uuid = ref('');
const bindId = ref('');
const isHaveBind = ref(false);
const errorText = ref('');

// ==================== 系统配置 ====================
const systemForm = reactive<any>({
  copyRight: true,
  logo: undefined,
  imgUrl: undefined,
  accountTip: true,
  document: true,
  website: true,
  isShowPhone: true,
  isDoc: true,
});

// ==================== 账号登录 ====================
const loading = ref(false);
const loginForm = reactive({
  username: '',
  password: '',
  rememberMe: false,
  code: '',
  bindId: '',
});
const loginRules = reactive({
  username: [{ required: true, trigger: 'blur', message: () => t('login.989807-20') }],
  password: [{ required: true, trigger: 'blur', message: () => t('login.989807-21') }],
  code: [{ required: true, trigger: 'change', message: () => t('login.989807-22') }],
});

// ==================== 短信登录 ====================
const smsLoading = ref(false);
const smsLoginForm = reactive({ phonenumber: '', invitationCode: '', smsCode: '', sourceType: 1 });
const smsLoginRules: any = {
  phonenumber: [
    { required: true, message: () => t('login.989807-23'), trigger: 'blur' },
    { pattern: /^1[3456789]\d{9}$/, message: () => t('login.989807-24'), trigger: 'blur' },
  ],
  smsCode: [{ required: true, trigger: 'change', message: () => t('login.989807-25') }],
};
const isLoginSmsBtn = ref(false);
const loginSmsBtnText = ref('');
const loginSmsBtnSeconds = ref(60);
let loginSmsBtnTimer: any = null;

// ==================== 忘记密码 ====================
const fpLoading = ref(false);
const fpForm = reactive({ phoneNumber: '', code: '', password: '', confirmPassword: '' });
const fpRules: any = {
  phoneNumber: [
    { required: true, message: () => t('login.989807-23'), trigger: 'blur' },
    { pattern: /^1[3456789]\d{9}$/, message: () => t('login.989807-24'), trigger: 'blur' },
  ],
  code: [{ required: true, trigger: 'change', message: () => t('login.989807-25') }],
  password: [{ required: true, trigger: 'blur', message: () => t('login.989807-21') }],
  confirmPassword: [
    { required: true, trigger: 'blur', message: () => t('login.989807-48') },
    {
      validator: (_rule: any, value: any, callback: any) => {
        if (fpForm.password !== value) callback(new Error(t('register.974236-12')));
        else callback();
      },
      trigger: 'blur',
    },
  ],
};
const isFpSmsBtn = ref(false);
const fpSmsBtnText = ref('');
const fpSmsBtnSeconds = ref(60);
let fpSmsBtnTimer: any = null;

// ==================== 账号注册 ====================
const registLoading = ref(false);
const registForm = reactive({
  username: '',
  phonenumber: '',
  password: '',
  invitationCode: '',
  confirmPassword: '',
  code: '',
  agree: false,
  uuid: '',
  bindId: '',
  sourceType: 1,
});
const registRules: any = {
  username: [
    { required: true, trigger: 'blur', message: () => t('login.989807-20') },
    { min: 2, max: 20, message: () => t('register.974236-13'), trigger: 'blur' },
  ],
  phonenumber: [
    { required: true, message: () => t('login.989807-23'), trigger: 'blur' },
    { pattern: /^1[3456789]\d{9}$/, message: () => t('login.989807-24'), trigger: 'blur' },
  ],
  code: [{ required: true, trigger: 'change', message: () => t('login.989807-25') }],
  password: [{ required: true, trigger: 'blur', message: () => t('login.989807-21') }],
  confirmPassword: [
    { required: true, trigger: 'blur', message: () => t('login.989807-48') },
    {
      validator: (_rule: any, value: any, callback: any) => {
        if (registForm.password !== value) callback(new Error(t('register.974236-12')));
        else callback();
      },
      trigger: 'blur',
    },
  ],
};

// ==================== 方法 ====================

// 显示|隐藏密码
function handlePasswordTypeChange() {
  pwdtype.value = pwdtype.value === 'password' ? 'text' : 'password';
}

// 获取验证码
function getCode() {
  getCodeImg().then((res: any) => {
    captchaOnOff.value = res.captchaEnabled !== undefined ? res.captchaEnabled : true;
    if (captchaOnOff.value) {
      codeUrl.value = 'data:image/gif;base64,' + res.img;
      uuid.value = res.uuid;
    }
  });
}

// 获取Cookie
function getCookie() {
  const username = Cookies.get('username');
  const password = Cookies.get('password');
  const rememberMe = Cookies.get('rememberMe');
  loginForm.username = username === undefined ? loginForm.username : (username as string);
  loginForm.password = password === undefined ? loginForm.password : (decrypt(password) as string);
  loginForm.rememberMe = rememberMe === undefined ? false : Boolean(rememberMe);
}

// 验证手机号
function validatePhoneNumber(number: string) {
  return /^1[3456789]\d{9}$/.test(number);
}

// 重置表单
function resetForm(formRef: any) {
  formRef?.value?.resetFields();
}

// 清除校验
function clearValidate(formRef: any) {
  formRef?.value?.clearValidate();
}

// ========== 账号登录 ==========
function handleLogin() {
  const redirect = route.query.redirect as string;
  loginFormRef.value?.validate(async (valid: boolean) => {
    if (valid) {
      loading.value = true;
      if (loginForm.rememberMe) {
        Cookies.set('username', loginForm.username, { expires: 30 });
        Cookies.set('password', encrypt(loginForm.password) as string, { expires: 30 });
        Cookies.set('rememberMe', String(loginForm.rememberMe), { expires: 30 });
      } else {
        Cookies.remove('username');
        Cookies.remove('password');
        Cookies.remove('rememberMe');
      }
      try {
        const params = { ...loginForm, uuid: uuid.value };
        await userStore.Login(params);
        await router.push({ path: redirect || '/' });
      } catch (error: any) {
        console.error('登录或跳转失败:', error);
        if (error && error.message && error.message.includes('503')) {
          setTimeout(() => {
            router.push({ path: '/license' });
          }, 2000);
        }
        if (captchaOnOff.value) getCode();
      } finally {
        loading.value = false;
      }
    }
  });
}

// ========== 绑定登录 ==========
function handleBindLogin() {
  loginFormRef.value?.validate((valid: boolean) => {
    if (valid) {
      loading.value = true;
      if (loginForm.rememberMe) {
        Cookies.set('username', loginForm.username, { expires: 30 });
        Cookies.set('password', encrypt(loginForm.password) as string, { expires: 30 });
        Cookies.set('rememberMe', String(loginForm.rememberMe), { expires: 30 });
      } else {
        Cookies.remove('username');
        Cookies.remove('password');
        Cookies.remove('rememberMe');
      }
      const params = { ...loginForm, uuid: uuid.value, bindId: bindId.value };
      bindLogin(params)
        .then((res: any) => {
          setToken(res.token, res.expireTime);
          router.push({ path: '/' }).catch(() => {});
        })
        .catch(() => {
          loading.value = false;
          if (captchaOnOff.value) getCode();
        });
    }
  });
}

// ========== 返回登录 ==========
function handleBackLogin() {
  bindId.value = '';
  router.push('/login');
}

function handleGotoLogin() {
  if (captchaOnOff.value) getCode();
  clearValidate(loginFormRef);
  formIndex.value = 1;
  isHaveBind.value = false;
  errorText.value = '';
}

// ========== 忘记密码 ==========
function handleForgetPassword() {
  resetForm(fpFormRef);
  formIndex.value = 3;
}

// ========== 注册 ==========
function handleRegister() {
  if (captchaOnOff.value) getCode();
  resetForm(registFormRef);
  formIndex.value = 4;
  isHaveBind.value = false;
  errorText.value = '';
}

// ========== 跳转链接 ==========
function handleGotoDoc() {
  window.open('https://fastbee.cn/doc', '_blank');
}
function handleGotoOS() {
  window.open('https://fastbee.cn', '_blank');
}
function handleGotoPrivacyPolicy() {
  window.open('https://fastbee.cn/privacy-policy.html', '_blank');
}

// ========== 微信登录 ==========
function handleGotoWeChatLogin() {
  const baseUrl = import.meta.env.VITE_APP_BASE_API;
  window.location.href = baseUrl + '/auth/render/wechat_open_web';
}

// ========== 短信登录 ==========
function handleGotoSmsLogin() {
  resetForm(smsLoginFormRef);
  formIndex.value = 2;
}

function getLoginSmsCode() {
  if (validatePhoneNumber(smsLoginForm.phonenumber)) {
    getSmsLoginCaptcha(smsLoginForm.phonenumber).then((res: any) => {
      if (res.code == 200) {
        ElMessage.success(t('login.989807-26'));
        loginSmsBtnTimer = setInterval(() => {
          if (loginSmsBtnSeconds.value > 0) {
            loginSmsBtnSeconds.value--;
            loginSmsBtnText.value = `${loginSmsBtnSeconds.value}秒后获取`;
          } else {
            clearInterval(loginSmsBtnTimer);
            loginSmsBtnText.value = t('login.989807-27');
            isLoginSmsBtn.value = false;
          }
        }, 1000);
        isLoginSmsBtn.value = true;
      } else {
        ElMessage.warning(t('login.989807-28'));
      }
    });
  } else {
    ElMessage.warning(t('login.989807-29'));
  }
}

function handleSmsLogin() {
  smsLoginFormRef.value?.validate((valid: boolean) => {
    if (valid) {
      smsLoading.value = true;
      smsLogin(smsLoginForm).then((res: any) => {
        setToken(res.token || res.data, res.expireTime);
        router.push({ path: '/' });
      });
    }
  });
}

// ========== 忘记密码短信 ==========
function getFpSmsCode() {
  if (validatePhoneNumber(fpForm.phoneNumber)) {
    getSmsForgetPassword(fpForm.phoneNumber).then((res: any) => {
      if (res.code == 200) {
        ElMessage.success(t('login.989807-26'));
        fpSmsBtnTimer = setInterval(() => {
          if (fpSmsBtnSeconds.value > 0) {
            fpSmsBtnSeconds.value--;
            fpSmsBtnText.value = `${fpSmsBtnSeconds.value}秒后获取`;
          } else {
            clearInterval(fpSmsBtnTimer);
            fpSmsBtnText.value = t('login.989807-27');
            isFpSmsBtn.value = false;
          }
        }, 1000);
        isFpSmsBtn.value = true;
      } else {
        ElMessage.warning(t('login.989807-28'));
      }
    });
  } else {
    ElMessage.warning(t('login.989807-29'));
  }
}

function handleChangePassword() {
  fpFormRef.value?.validate((valid: boolean) => {
    if (valid) {
      fpLoading.value = true;
      const { confirmPassword, ...params } = fpForm;
      forgetPwdReset(params)
        .then((res: any) => {
          if (res.code === 200) {
            ElMessage.success(t('login.989807-50'));
            handleGotoLogin();
          } else {
            ElMessage.error(t('login.989807-51'));
          }
        })
        .catch(() => {
          fpLoading.value = false;
        });
    }
  });
}

// ========== 注册 ==========
function handleRegist() {
  if (registForm.agree) {
    registFormRef.value?.validate((valid: boolean) => {
      if (valid) {
        registLoading.value = true;
        const params = { ...registForm, uuid: uuid.value };
        register(params)
          .then((res: any) => {
            registLoading.value = false;
            if (res.code === 200) registerAlert();
          })
          .catch(() => {
            registLoading.value = false;
            if (captchaOnOff.value) getCode();
          });
      }
    });
  } else {
    ElMessage.warning(t('login.989807-58'));
  }
}

function handleRegistBind() {
  if (registForm.agree) {
    registFormRef.value?.validate((valid: boolean) => {
      if (valid) {
        registLoading.value = true;
        const params = { ...registForm, uuid: uuid.value, bindId: bindId.value };
        bindRegister(params)
          .then((res: any) => {
            registLoading.value = false;
            if (res.code === 200) registerAlert();
          })
          .catch(() => {
            registLoading.value = false;
            if (captchaOnOff.value) getCode();
          });
      }
    });
  } else {
    ElMessage.warning(t('login.989807-58'));
  }
}

function registerAlert() {
  const queryBindId = route.query.bindId;
  const { username } = registForm;
  ElMessageBox.alert(
    "<font color='red'> " + t('register.974236-18') + username + t('register.974236-19') + ' </font>',
    t('register.974236-20'),
    { dangerouslyUseHTMLString: true, type: 'success' }
  ).then(() => {
    if (queryBindId) handleBackLogin();
    else handleGotoLogin();
  });
}

// ========== 检测绑定/错误/重定向 ==========
function checkBind() {
  const queryBindId = route.query.bindId as string;
  if (queryBindId) {
    checkBindId(queryBindId).then((res: any) => {
      isHaveBind.value = res.code === 200;
      if (isHaveBind.value) {
        bindId.value = queryBindId;
      } else {
        bindId.value = '';
        router.push({ query: {} });
      }
    });
  }
}

function checkError() {
  const errorId = route.query.errorId as string;
  if (errorId) {
    getErrorMsg(errorId).then((res: any) => {
      errorText.value = res.code === 200 ? res.msg : '';
    });
  }
}

function redirectLogin(loginId: string) {
  socialLogin(loginId).then((res: any) => {
    setToken(res.token, res.expireTime);
    router.push({ path: (route.query.redirect as string) || '/' });
  });
}

// ==================== 初始化 ====================
onMounted(() => {
  loginSmsBtnText.value = t('login.989807-27');
  fpSmsBtnText.value = t('login.989807-27');

  const loginId = route.query.loginId as string;
  if (!loginId) {
    getCode();
    getCookie();
    checkBind();
    checkError();
    // 获取系统配置
    getConfigKey('sys.logo.config').then((response: any) => {
      if (response.code === 200 && response.msg !== '' && response.msg !== '{}') {
        Object.assign(systemForm, JSON.parse(response.msg));
      }
    });
  } else {
    redirectLogin(loginId);
  }
});
</script>

<style lang="scss" scoped>
.login-wrap {
  user-select: none;
  height: 100vh;
  max-width: 100%;
  width: 100vw;
  display: flex;

  .logo-wrap {
    position: absolute;
    top: 80px;
    left: 80px;
    display: flex;
    flex-direction: row;
    align-items: center;

    .icon {
      width: 42px;
      height: 46px;
    }

    .text {
      font-size: 36px;
      font-weight: 500;
      margin-left: 12px;
      color: var(--el-color-primary);
    }
  }

  .introduce-text {
    position: absolute;
    font-weight: 400;
    font-size: 14px;
    color: #909399;
    line-height: 20px;
    text-align: left;
    font-style: normal;
    top: 146px;
    left: 80px;
    width: 500px;
    white-space: pre-wrap;
    word-wrap: break-word;
  }

  .img-wrap {
    flex: 1;
    background: #0f73ee;
  }

  .box-wrap {
    position: relative;
    width: 608px;
    align-items: center;
    display: flex;
    flex-direction: column;
    overflow: hidden;
    justify-content: center;

    .form-box {
      margin-top: -8%;
      width: 300px;

      .title-wrap {
        display: flex;
        justify-content: space-between;
        align-items: center;

        .name {
          font-weight: 600;
          font-size: 24px;
          color: #303133;
        }
      }

      .demo-account {
        font-weight: 400;
        font-size: 14px;
        color: #909399;
        margin: 40px 0 0;
      }

      .form-wrap {
        margin-top: 24px;

        .item-wrap {
          width: 100%;
          display: flex;
          flex-direction: row;
          justify-content: space-between;
          align-items: center;

          .input-wrap {
            display: flex;
            flex-direction: row;
            align-items: center;
            justify-content: space-between;
            border-radius: 4px;
            border: 1px solid #dcdfe6;
            padding: 3px 16px;
            width: 100%;

            .inner {
              background: none;
              border: none;
              box-sizing: border-box;
              flex-grow: 1;
              font-size: inherit;
              outline: none;
              padding: 0;
              width: 100%;
              color: #303133;
              line-height: 30px;
            }

            .icon {
              color: #303133;
              cursor: pointer;
            }
          }

          .input-append {
            width: 112px;
            height: 40px;
            margin-left: 8px;
            border-radius: 4px;
            cursor: pointer;
          }
        }

        .other-link {
          margin-top: 17px;
          display: flex;
          flex-direction: row;
          justify-content: space-between;
          align-items: center;
        }

        .text {
          color: #909399;
          font-size: 14px;
          text-align: left;
          white-space: nowrap;
          cursor: pointer;
        }

        .other-login {
          margin-top: 40px;
          display: flex;
          flex-direction: row;
          align-items: center;

          .icon {
            width: 16px;
            height: 16px;
            margin: 0 12px 0 8px;
            cursor: pointer;
          }
        }

        .forget-pwd-link {
          font-weight: 400;
          font-size: 14px;
          color: var(--el-color-primary);
          cursor: pointer;
        }

        .primary-link {
          color: var(--el-color-primary);
          cursor: pointer;
        }
      }

      .btn {
        margin-top: 26px;
        width: 100%;
        height: 40px;
      }

      .mess-text {
        font-weight: 400;
        font-size: 14px;
        line-height: 24px;
        color: #ed2525;
      }
    }

    .copyright-wrap {
      position: absolute;
      bottom: 24px;
      font-weight: 400;
      font-size: 12px;
      color: #909399;
      line-height: 20px;
    }
  }
}

@media screen and (min-width: 1920px) {
  .login-wrap .box-wrap {
    width: 811px;
  }
}

@media screen and (max-width: 1180px) {
  .login-wrap .box-wrap {
    width: 498px;

    .form-box {
      width: 246px;
    }
  }

  .login-wrap .logo-wrap {
    top: 66px;
    left: 66px;

    .icon {
      width: 34px;
      height: 38px;
    }

    .text {
      font-size: 29px;
      margin-left: 10px;
      color: var(--el-color-primary);
    }
  }

  .login-wrap .introduce-text {
    font-size: 11px;
    top: 120px;
    left: 66px;
  }
}

@media screen and (max-width: 968px) {
  .login-wrap .img-wrap {
    display: none;
  }

  .login-wrap .logo-wrap {
    display: none;
  }

  .login-wrap .introduce-text {
    display: none;
  }

  .login-wrap .box-wrap {
    width: 100%;

    .copyright-wrap {
      position: absolute;
      bottom: 10px;
      padding: 0 10px;
      text-align: center;
    }
  }
}
</style>
