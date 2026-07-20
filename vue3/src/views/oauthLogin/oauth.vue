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
        <el-tabs class="form" style="float: none" model-value="uname">
          <el-tab-pane :label="$t('oauthLogin.oauth.989807-0') + '（' + client.name + ')'" name="uname" />
        </el-tabs>
        <el-form ref="loginFormRef" :model="loginForm">
          {{ $t('oauthLogin.oauth.989807-1') }}
          <el-form-item prop="scopes">
            <el-checkbox-group v-model="loginForm.scopes">
              <el-checkbox
                v-for="scope in params.scopes"
                :value="scope"
                :key="scope"
                style="display: block; margin-bottom: -10px"
              >
                {{ formatScope(scope) }}
              </el-checkbox>
            </el-checkbox-group>
          </el-form-item>
          <el-form-item style="width: 100%">
            <el-button
              :loading="loading"
              size="default"
              type="primary"
              style="width: 50%"
              @click.prevent="handleAuthorize(true)"
            >
              <span v-if="!loading">{{ $t('oauthLogin.oauth.989807-2') }}</span>
              <span v-else>{{ $t('oauthLogin.oauth.989807-3') }}</span>
            </el-button>
            <el-button size="default" style="width: 36%" @click.prevent="handleAuthorize(false)">
              {{ $t('oauthLogin.oauth.989807-4') }}
            </el-button>
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
import { useRoute } from 'vue-router';
import { useI18n } from 'vue-i18n';
import { authorize, getAuthorize } from '@/api/login';
import type { FormInstance } from 'element-plus';

const route = useRoute();
const { t } = useI18n();

const loginFormRef = ref<FormInstance>();
const loading = ref(false);

const loginForm = reactive({
  scopes: [] as string[],
});

const params = reactive({
  responseType: undefined as string | undefined,
  clientId: undefined as string | undefined,
  redirectUri: undefined as string | undefined,
  state: undefined as string | undefined,
  scopes: [] as string[],
});

const client = reactive({
  name: '',
  logo: '',
});

function doAuthorize(autoApprove: boolean, checkedScopes: string[], uncheckedScopes: string[]) {
  return authorize(
    params.responseType,
    params.clientId,
    params.redirectUri,
    params.state,
    autoApprove,
    checkedScopes,
    uncheckedScopes
  );
}

function getAuthorizeInfo() {
  getAuthorize(params.clientId).then((res: any) => {
    client.name = res.data.client.name;
    client.logo = res.data.client.logo;
    let scopes: any[];
    if (params.scopes.length > 0) {
      scopes = [];
      for (const scope of res.data.scopes) {
        if (params.scopes.indexOf(scope.key) >= 0) {
          scopes.push(scope);
        }
      }
    } else {
      scopes = res.data.scopes;
      for (const scope of scopes) {
        params.scopes.push(scope.key);
      }
    }
    for (const scope of scopes) {
      if (scope.value) {
        loginForm.scopes.push(scope.key);
      }
    }
  });
}

function handleAuthorize(approved: boolean) {
  loginFormRef.value?.validate((valid: boolean) => {
    if (!valid) return;
    loading.value = true;
    let checkedScopes: string[];
    let uncheckedScopes: string[];
    if (approved) {
      checkedScopes = loginForm.scopes;
      uncheckedScopes = params.scopes.filter((item) => checkedScopes.indexOf(item) === -1);
    } else {
      checkedScopes = [];
      uncheckedScopes = params.scopes;
    }
    doAuthorize(false, checkedScopes, uncheckedScopes)
      .then((res: any) => {
        const href = res.data;
        if (!href) return;
        location.href = href;
      })
      .finally(() => {
        loading.value = false;
      });
  });
}

function formatScope(scope: string) {
  switch (scope) {
    case 'user.read':
      return t('oauthLogin.oauth.989807-5');
    case 'user.write':
      return t('oauthLogin.oauth.989807-6');
    default:
      return scope;
  }
}

onMounted(() => {
  params.responseType = route.query.responseType as string;
  params.clientId = route.query.clientId as string;
  params.redirectUri = route.query.redirectUri as string;
  params.state = route.query.state as string;
  if (route.query.scope) {
    params.scopes = (route.query.scope as string).split(' ');
  }
  if (params.scopes.length > 0) {
    doAuthorize(true, params.scopes, []).then((res: any) => {
      const href = res.data;
      if (!href) {
        console.log('自动授权未通过！');
        return;
      }
      location.href = href;
    });
  }
  getAuthorizeInfo();
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
