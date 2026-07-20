<template>
  <div class="user-profile">
    <el-card :body-style="{ padding: '0px' }">
      <div class="card-body">
        <div class="left-wrap">
          <userAvatar :user="user" />
          <div class="user-name">{{ user.userName }}</div>
          <ul class="ul-wrap">
            <li :class="{ 'li-wrap': true, 'li-active': tabIndex === 0 }" @click="handleTabClick(0)">
              <el-icon class="icon"><User /></el-icon>
              <span class="title">{{ $t('user.index.098976-50') }}</span>
            </li>
            <li :class="{ 'li-wrap': true, 'li-active': tabIndex === 1 }" @click="handleTabClick(1)">
              <el-icon class="icon"><Postcard /></el-icon>
              <span class="title">{{ $t('user.index.098976-51') }}</span>
            </li>
            <li :class="{ 'li-wrap': true, 'li-active': tabIndex === 2 }" @click="handleTabClick(2)">
              <svg-icon icon-class="password" class="icon" />
              <span class="title">{{ $t('user.index.098976-52') }}</span>
            </li>
            <li v-if="isAdmin" :class="{ 'li-wrap': true, 'li-active': tabIndex === 3 }" @click="handleTabClick(3)">
              <svg-icon icon-class="system_style" class="icon" />
              <span class="title">{{ $t('user.profile.systemStyle.080498-0') }}</span>
            </li>
          </ul>
        </div>
        <div class="right-wrap">
          <div class="body-wrap" v-show="tabIndex === 0">
            <div class="header">{{ $t('user.index.098976-50') }}</div>
            <div class="content">
              <userInfo :user="user" :postGroup="postGroup" :roleGroup="roleGroup" :wxbind="wxbind" />
            </div>
          </div>
          <div class="body-wrap" v-show="tabIndex === 1">
            <div class="header">{{ $t('user.index.098976-51') }}</div>
            <div class="content">
              <baseInfo :user="user" />
            </div>
          </div>
          <div class="body-wrap" v-show="tabIndex === 2">
            <div class="header">{{ $t('user.index.098976-52') }}</div>
            <div class="content">
              <resetPwd />
            </div>
          </div>
          <div class="body-wrap" v-show="tabIndex === 3">
            <div class="header">{{ $t('user.profile.systemStyle.080498-0') }}</div>
            <div class="content">
              <systemStyle />
            </div>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useI18n } from 'vue-i18n';
import { User, Postcard } from '@element-plus/icons-vue';
import { getUserProfile } from '@/api/system/user';
import { useUserStore } from '@/stores/modules/user';
import userAvatar from './userAvatar.vue';
import userInfo from './userInfo.vue';
import baseInfo from './baseInfo.vue';
import resetPwd from './resetPwd.vue';
import systemStyle from './systemStyle.vue';

const { t } = useI18n();
const userStore = useUserStore();

const tabIndex = ref(0);
const user = ref<any>({});
const roleGroup = ref('');
const postGroup = ref('');
const wxbind = ref(false);
const isAdmin = ref(false);

function handleTabClick(index: number) {
  tabIndex.value = index;
}

function getUser() {
  getUserProfile().then((response: any) => {
    user.value = response.data;
    wxbind.value = response.wxBind;
    roleGroup.value = response.roleGroup;
    postGroup.value = response.postGroup;
  });
}

onMounted(() => {
  getUser();
  if (userStore.roles.includes('admin')) {
    isAdmin.value = true;
  }
});
</script>

<style lang="scss" scoped>
.user-profile {
  padding: 20px;

  .card-body {
    display: flex;
    flex-direction: row;

    .left-wrap {
      width: 230px;
      height: 860px;
      background-color: #f7f7f7;
      text-align: center;
      padding: 40px 20px;

      .user-name {
        margin-top: 10px;
      }

      .ul-wrap {
        list-style: none;
        padding: 0;
        margin: 0;

        .li-wrap {
          margin-top: 35px;
          display: flex;
          flex-direction: row;
          align-items: center;
          padding: 10px 49px;
          white-space: nowrap;
          box-sizing: border-box;
          transition: 0.1s;
          font-size: 14px;
          cursor: pointer;

          .icon {
            color: #1890ff;
          }

          .title {
            margin-left: 8px;
            font-size: 14px;
          }
        }

        .li-active {
          border-radius: 20px;
          color: #fff;
          background-color: #1890ff;
          border-color: #1890ff;

          .icon {
            color: #fff;
          }
        }
      }
    }

    .right-wrap {
      flex: 1;

      .body-wrap {
        width: 100%;
        height: 100%;
        padding: 20px;

        .header {
          padding: 10px 0;
          font-size: 15px;
          font-weight: bold;
          border-bottom: 1px solid #e6ebf5;
        }

        .content {
          padding: 20px 0;
        }
      }
    }
  }
}
</style>
