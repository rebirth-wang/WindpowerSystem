<template>
  <div class="login-wrap">
    <div class="logo-wrap">
      <img class="icon" src="@/assets/images/logo_blue.png" />
      <span class="text">{{ $t('login.989807-37') }}</span>
    </div>
    <pre class="introduce-text">{{ $t('login.989807-38') }}</pre>

    <div class="img-wrap">
      <img style="width: 100%; height: 100%" src="@/assets/images/cover.png" />
    </div>
    <div class="box-wrap">
      <div class="form-box">
        <el-form ref="loginFormRef" :model="loginForm" :rules="loginRules" class="login-form">
          <el-form-item prop="username">
            <el-input v-model="loginForm.username" type="text" auto-complete="off" :placeholder="$t('login.989807-4')">
              <template #prefix>
                <svg-icon icon-class="user" class="input-icon" />
              </template>
            </el-input>
          </el-form-item>
          <el-form-item prop="password">
            <el-input
              v-model="loginForm.password"
              type="password"
              auto-complete="off"
              :placeholder="$t('login.989807-5')"
              @keyup.enter="handleLogin"
            >
              <template #prefix>
                <svg-icon icon-class="password" class="input-icon" />
              </template>
            </el-input>
          </el-form-item>
          <el-form-item style="width: 100%">
            <div style="margin-bottom: 10px">
              <el-button :loading="loading" type="primary" style="width: 100%" @click.prevent="handleLogin">
                <span v-if="!loading">{{ $t('login.989807-3') }}</span>
                <span v-else>{{ $t('login.989807-13') }}</span>
              </el-button>
            </div>
          </el-form-item>
        </el-form>
      </div>

      <div class="copyright-wrap">
        <span>
          Copyright © 2021-2025
          <a target="_blank" href="http://fastbee.cn">FastBee</a>
          All Rights Reserved.
        </span>
        <a target="_blank" href="https://beian.miit.gov.cn/">滇ICP备2023000466号-6</a>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useI18n } from 'vue-i18n';
import { oauthLogin } from '@/api/login';
import { setToken } from '@/utils/auth';
import type { FormInstance } from 'element-plus';

const router = useRouter();
const route = useRoute();
const { t } = useI18n();

const loginFormRef = ref<FormInstance>();
const loading = ref(false);

const loginForm = reactive({
  username: '',
  password: '',
});

const params = reactive({
  responseType: '' as string,
  clientId: '' as string,
  redirectUri: '' as string,
  state: '' as string,
});

const loginRules = reactive({
  username: [{ required: true, trigger: 'blur', message: t('login.989807-20') }],
  password: [{ required: true, trigger: 'blur', message: t('login.989807-21') }],
});

function handleLogin() {
  loginFormRef.value?.validate((valid: boolean) => {
    if (valid) {
      loading.value = true;
      oauthLogin(loginForm)
        .then((res: any) => {
          setToken(res.token || res.data, res.expireTime);
          router
            .push({
              path: '/sso',
              query: {
                clientId: route.query.client_id as string,
                redirectUri: route.query.redirect_uri as string,
                responseType: route.query.response_type as string,
                state: route.query.state as string,
              },
            })
            .catch(() => {});
        })
        .finally(() => {
          loading.value = false;
        });
    }
  });
}

onMounted(() => {
  params.responseType = (route.query.response_type as string) || '';
  params.clientId = (route.query.client_id as string) || '';
  params.redirectUri = (route.query.redirect_uri as string) || '';
  params.state = (route.query.state as string) || '';
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
      color: #486ff2;
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

      .form-wrap {
        margin-top: 24px;

        :deep(.el-form-item) {
          margin-bottom: 21px;

          .el-form-item__content {
            line-height: 14px;
          }
        }
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
      color: #486ff2;
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
