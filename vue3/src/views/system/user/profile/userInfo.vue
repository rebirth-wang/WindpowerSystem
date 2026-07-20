<template>
  <div class="user-info">
    <el-descriptions :column="3">
      <el-descriptions-item :label="$t('user.profile.index.894502-1')">{{ userInfo.userName }}</el-descriptions-item>
      <el-descriptions-item :label="$t('user.index.098976-13')">{{ userInfo.phonenumber }}</el-descriptions-item>
      <el-descriptions-item :label="$t('user.profile.index.894502-2')">{{ userInfo.email }}</el-descriptions-item>
      <el-descriptions-item :label="$t('user.profile.index.894502-3')">
        {{
          posts
            ? `${userInfo.dept ? userInfo.dept.deptName : ''} / ${posts}`
            : `${userInfo.dept ? userInfo.dept.deptName : ''}`
        }}
      </el-descriptions-item>
      <el-descriptions-item :label="$t('user.profile.index.894502-4')">{{ roles }}</el-descriptions-item>
      <el-descriptions-item :label="$t('user.profile.index.894502-5')">{{ userInfo.createTime }}</el-descriptions-item>
      <el-descriptions-item :label="$t('user.profile.index.894502-6')">
        <el-button v-if="wxBind" type="warning" size="small" @click="handleBindWeChat">
          {{ $t('user.profile.index.894502-7') }}
        </el-button>
        <el-button v-else type="primary" size="small" @click="handleBindWeChat">
          {{ $t('user.profile.index.894502-8') }}
        </el-button>
      </el-descriptions-item>
    </el-descriptions>
    <!-- 绑定微信 -->
    <el-dialog :title="$t('user.profile.index.894502-14')" v-model="isBindWeChat" width="500px" append-to-body>
      <div class="bindWeChatDialog">
        <div class="dec">{{ $t('user.profile.index.894502-13') }}</div>
        <div class="weChat">
          <div id="weChatLogin" style="height: 200px"></div>
        </div>
      </div>
      <template #footer>
        <el-button type="primary" size="small" @click="isBindWeChat = false">{{ $t('close') }}</el-button>
      </template>
    </el-dialog>
    <!-- 解除微信绑定 -->
    <el-dialog :title="$t('user.profile.index.894502-11')" v-model="isUnBindWeChat" width="600px" append-to-body>
      <div class="unBindWeChatDialog">
        <el-form label-width="150px">
          <el-form-item :label="$t('user.profile.index.894502-12')" prop="password">
            <el-input style="width: 80%" v-model="unBindWeChat.password" :type="pwdtype">
              <template #suffix>
                <el-icon style="margin-right: 8px; cursor: pointer" @click="pwdTypeChange"><View /></el-icon>
              </template>
            </el-input>
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button size="small" @click="isUnBindWeChat = false">{{ $t('close') }}</el-button>
          <el-button size="small" type="primary" @click="confirmUnBindWeChat">{{ $t('confirm') }}</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, getCurrentInstance } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute } from 'vue-router';
import { getLoginParam, secureBind, getUserProfile } from '@/api/system/user';
import { getWxBindMsg } from '@/api/login';
import { View } from '@element-plus/icons-vue';

declare const WxLogin: any;

const { t } = useI18n();
const { proxy } = getCurrentInstance()!;
const route = useRoute();

const props = defineProps<{
  user: any;
  postGroup: string;
  roleGroup: string;
  wxbind: boolean;
}>();

const isBindWeChat = ref(false);
const isUnBindWeChat = ref(false);
const pwdtype = ref('password');
const wxBind = ref(props.wxbind);
const userInfo = ref(props.user);
const posts = ref(props.postGroup);
const roles = ref(props.roleGroup);
const unBindWeChat = reactive({
  password: '',
  verifyType: 1,
});

onMounted(() => {
  getUser();
  const wxBindMsgId = route.query.wxBindMsgId as string;
  if (wxBindMsgId) {
    getWeChatBindMsg();
  }
  const script = document.createElement('script');
  script.type = 'text/javascript';
  script.src = 'https://res.wx.qq.com/connect/zh_CN/htmledition/js/wxLogin.js';
  document.body.appendChild(script);
});

/** 绑定微信 */
function handleBindWeChat() {
  getLoginParam().then((response: any) => {
    const s = document.createElement('script');
    s.type = 'text/javascript';
    s.src = 'https://res.wx.qq.com/connect/zh_CN/htmledition/js/wxLogin.js';
    const wxElement = document.body.appendChild(s);
    wxElement.onload = function () {
      new WxLogin({
        self_redirect: false,
        id: 'weChatLogin',
        appid: response.data.appid,
        scope: response.data.scope,
        redirect_uri: response.data.redirectUri,
        state: response.data.state,
        style: 'black',
        href: 'data:text/css;base64,LmltcG93ZXJCb3ggLnRpdGxlIHsKIGRpc3BsYXk6IG5vbmU7Cn0KLmltcG93ZXJCb3ggLnN0YXR1cy5zdGF0dXNfYnJvd3NlciB7CiBkaXNwbGF5OiBub25lOwp9Ci5pbXBvd2VyQm94IC5xcmNvZGUgewogYm9yZGVyOm5vbmU7CiB3aWR0aDogMjAwcHg7CiBoZWlnaHQ6IDIwMHB4OwogbWFyZ2luOjAgYXV0bzsKfQouaW1wb3dlckJveCAuc3RhdHVzewogZGlzcGxheTogbm9uZQp9',
      });
    };
  });
  isBindWeChat.value = true;
}

/** 解除微信绑定 */
function handleUnBindWeChat() {
  isUnBindWeChat.value = true;
}

function pwdTypeChange() {
  pwdtype.value = pwdtype.value === 'password' ? 'text' : 'password';
}

/** 确认解除微信绑定 */
function confirmUnBindWeChat() {
  secureBind({ ...unBindWeChat }).then((res: any) => {
    if (res.code === 200) {
      getUser();
      (proxy as any).$modal.msgSuccess(res.msg);
    } else {
      (proxy as any).$modal.msgError(res.msg);
    }
    isUnBindWeChat.value = false;
  });
}

/** 获取用户信息 */
function getUser() {
  getUserProfile().then((res: any) => {
    if (res.code === 200) {
      userInfo.value = res.data;
      wxBind.value = res.wxBind;
      roles.value = res.roleGroup;
      posts.value = res.postGroup;
    }
  });
}

/** 获取微信绑定返回结果信息 */
function getWeChatBindMsg() {
  const wxBindMsgId = route.query.wxBindMsgId as string;
  getWxBindMsg(wxBindMsgId).then((res: any) => {
    if (res.code === 200) {
      (proxy as any).$modal.msgSuccess(res.msg);
    } else {
      (proxy as any).$modal.msgError(res.msg);
    }
  });
}
</script>

<style lang="scss" scoped>
.user-info {
  :deep(.el-descriptions__cell) {
    padding-bottom: 18px !important;
  }

  :deep(.el-button--small) {
    padding: 5px 15px;
  }
}

.bindWeChatDialog {
  .dec {
    font-size: 14px;
  }

  .weChat {
    width: 100%;
    display: flex;
    flex-direction: column;
    align-items: center;
    margin-top: 15px;
  }
}
</style>
