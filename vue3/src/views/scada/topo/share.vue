<template>
  <div class="scada-share">
    <div class="pass-wrap" v-if="shareDatas.isShare && shareDatas.sharePass">
      <div class="title-wrap">{{ $t('scada.topo.share.842076-0') }}</div>
      <div class="input-wrap">
        <el-input
          v-model="password"
          :placeholder="$t('scada.topo.share.842076-1')"
          type="password"
          show-password
        ></el-input>
        <el-button type="primary" style="width: 100px; margin-left: 5px" @click="handleConfirmPass">
          {{ $t('confirm') }}
        </el-button>
      </div>
    </div>
    <div class="pass-wrap" v-if="!shareDatas.isShare">
      <div style="line-height: 300px; font-size: 26px">{{ $t('scada.topo.share.842076-2') }}</div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, getCurrentInstance } from 'vue';
import { useRoute } from 'vue-router';
import { getShare } from '@/api/scada/center';
import { getScadaRouteType, getRouteQueryString } from '@/utils/topo/topoUtil';

const { proxy } = getCurrentInstance() as any;
const route = useRoute();

const shareDatas = ref<any>({
  isShare: 1,
  shareUrl: '',
  sharePass: '',
});
const password = ref(''); // 密码

function getShareDetail() {
  // 查询分享接口
  const params = {
    guid: getRouteQueryString(route.query, 'guid'),
    type: getScadaRouteType(route.query),
    serialNumber: getRouteQueryString(route.query, 'serialNumber'),
  };
  getShare(params).then((res: any) => {
    const { code, data } = res;
    if (code === 200) {
      shareDatas.value = data;
      if (data.isShare && !data.sharePass) {
        window.location.href = data.shareUrl;
      }
    }
  });
}

// 确认密码
function handleConfirmPass() {
  if (password.value === shareDatas.value.sharePass) {
    window.location.href = shareDatas.value.shareUrl;
  } else {
    proxy.$message({
      type: 'error',
      message: proxy.$t('scada.topo.share.842076-3'),
    });
  }
}

onMounted(() => {
  getShareDetail();
});
</script>

<style lang="scss" scoped>
.scada-share {
  width: 100%;
  height: 100%;

  .pass-wrap {
    width: 500px;
    height: 300px;
    position: relative;
    top: 50%;
    margin: 0 auto;
    transform: translateY(-50%);
    border-radius: 4px;
    background-color: #ffffff;
    box-shadow: 0 2px 14px 0 rgba(0, 0, 0, 0.08);
    text-align: center;

    .title-wrap {
      font-size: 26px;
      color: #333333;
      padding-top: 88px;
    }

    .input-wrap {
      display: flex;
      justify-content: center;
      align-items: center;
      margin-bottom: 10px;
      margin-top: 50px;

      .el-input {
        width: 200px;
      }
    }
  }
}
</style>
