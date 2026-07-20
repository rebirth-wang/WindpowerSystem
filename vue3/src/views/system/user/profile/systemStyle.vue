<template>
  <el-form ref="formRef" :model="systemForm" label-width="120px">
    <el-form-item :label="$t('user.profile.systemStyle.080498-1')" prop="systemName">
      <div class="field-wrap">
        <el-input
          class="field-input"
          v-model="systemForm.systemName"
          :placeholder="$t('user.profile.systemStyle.080498-2')"
        />
        <div class="field-tip">{{ $t('user.profile.systemStyle.080498-3') }}</div>
      </div>
    </el-form-item>
    <el-form-item label="LOGO" prop="logo">
      <template #label>
        <span class="label-wrap">
          LOGO
          <el-tooltip effect="dark" :content="$t('user.profile.systemStyle.080498-23')" placement="top">
            <el-icon><QuestionFilled /></el-icon>
          </el-tooltip>
        </span>
      </template>
      <image-upload
        :model-value="systemForm.logo"
        :limit="1"
        :fileSize="1"
        :type="1"
        @update:model-value="getImagePath"
      />
    </el-form-item>
    <el-form-item :label="$t('user.profile.systemStyle.080498-4')" prop="description">
      <div class="field-wrap">
        <el-input
          class="field-input"
          v-model="systemForm.description"
          :placeholder="$t('user.profile.systemStyle.080498-5')"
          type="textarea"
        />
        <div class="field-tip">{{ $t('user.profile.systemStyle.080498-6') }}</div>
      </div>
    </el-form-item>

    <el-form-item :label="$t('user.profile.systemStyle.080498-7')" prop="imgUrl">
      <template #label>
        <span class="label-wrap">
          {{ $t('user.profile.systemStyle.080498-7') }}
          <el-tooltip effect="dark" :content="$t('user.profile.systemStyle.080498-23')" placement="top">
            <el-icon><QuestionFilled /></el-icon>
          </el-tooltip>
        </span>
      </template>
      <image-upload
        :model-value="systemForm.imgUrl"
        :limit="1"
        :fileSize="5"
        :type="1"
        @update:model-value="getImagePathImg"
      />
    </el-form-item>
    <el-form-item :label="$t('user.profile.systemStyle.080498-8')" prop="remark">
      <el-tooltip :content="$t('user.profile.systemStyle.080498-9')" placement="top">
        <el-checkbox v-model="systemForm.accountTip" :value="true">
          {{ $t('user.profile.systemStyle.080498-10') }}
        </el-checkbox>
      </el-tooltip>
      <el-tooltip :content="$t('user.profile.systemStyle.080498-11')" placement="top">
        <el-checkbox v-model="systemForm.document" :value="true">
          {{ $t('user.profile.systemStyle.080498-12') }}
        </el-checkbox>
      </el-tooltip>
      <el-tooltip :content="$t('user.profile.systemStyle.080498-13')" placement="top">
        <el-checkbox v-model="systemForm.website" :value="true">
          {{ $t('user.profile.systemStyle.080498-14') }}
        </el-checkbox>
      </el-tooltip>
      <el-tooltip :content="$t('user.profile.systemStyle.080498-15')" placement="top">
        <el-checkbox v-model="systemForm.copyRight" :value="true">
          {{ $t('user.profile.systemStyle.080498-16') }}
        </el-checkbox>
      </el-tooltip>
      <el-tooltip :content="$t('user.profile.systemStyle.080498-17')" placement="top">
        <el-checkbox v-model="systemForm.isShowPhone" :value="true">
          {{ $t('user.profile.systemStyle.080498-18') }}
        </el-checkbox>
      </el-tooltip>
      <el-tooltip :content="$t('user.profile.systemStyle.080498-19')" placement="top">
        <el-checkbox v-model="systemForm.isDoc" :value="true">
          {{ $t('user.profile.systemStyle.080498-20') }}
        </el-checkbox>
      </el-tooltip>
    </el-form-item>
    <el-form-item>
      <el-button type="primary" plain @click="submit">
        {{ $t('user.profile.systemStyle.080498-21') }}
      </el-button>
      <el-button @click="close">{{ $t('close') }}</el-button>
    </el-form-item>
  </el-form>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, getCurrentInstance } from 'vue';
import { useI18n } from 'vue-i18n';
import { listConfig, addConfig, updateConfig } from '@/api/system/config';
import { QuestionFilled } from '@element-plus/icons-vue';

const { t } = useI18n();
const { proxy } = getCurrentInstance()!;

const formRef = ref<any>(null);
const configId = ref<number | undefined>(undefined);
const baseUrl = import.meta.env.VITE_APP_BASE_API;

const systemForm = reactive<any>({
  name: undefined,
  newPassword: undefined,
  confirmPassword: undefined,
  copyRight: true,
  logo: undefined,
  imgUrl: undefined,
  accountTip: true,
  document: true,
  website: true,
  isShowPhone: true,
  isDoc: true,
});

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  configKey: 'sys.logo.config',
});

onMounted(() => {
  getList();
});

/** 查询参数列表 */
function getList() {
  listConfig(queryParams).then((response: any) => {
    const configList = response.rows;
    if (response.total > 0) {
      if (configList[0].configValue !== '') {
        const parsed = JSON.parse(configList[0].configValue);
        Object.assign(systemForm, parsed);
        if (systemForm.logo !== '' && systemForm.logo !== undefined) {
          const matchResult = systemForm.logo.match(/\/profile\/upload\/.*/);
          systemForm.logo = matchResult ? baseUrl + matchResult[0] : systemForm.logo;
        }
        if (systemForm.imgUrl !== '' && systemForm.imgUrl !== undefined) {
          const matchResultUrl = systemForm.imgUrl.match(/\/profile\/upload\/.*/);
          systemForm.imgUrl = matchResultUrl ? baseUrl + matchResultUrl[0] : systemForm.imgUrl;
        }
      }
      configId.value = configList[0].configId;
    } else {
      configId.value = undefined;
    }
  });
}

/** 提交 */
function submit() {
  if (configId.value === undefined) {
    const form = {
      configName: t('user.profile.systemStyle.080498-22'),
      configKey: 'sys.logo.config',
      configValue: JSON.stringify(systemForm),
      configType: 'N',
      remark: undefined,
    };
    addConfig(form).then((response: any) => {
      if (response.code === 200) {
        (proxy as any).$modal.msgSuccess(response.msg);
        getList();
      } else {
        (proxy as any).$modal.msgError(response.msg);
      }
    });
  } else {
    const form = {
      configId: configId.value,
      configValue: JSON.stringify(systemForm),
    };
    updateConfig(form).then((response: any) => {
      (proxy as any).$modal.msgSuccess(response.msg);
      getList();
    });
  }
}

/** 获取上传Logo的路径 */
function getImagePath(data: string) {
  const matchResult = data.match(/\/profile\/upload\/.*/);
  systemForm.logo = matchResult ? baseUrl + matchResult[0] : data;
}

/** 获取上传图片的路径 */
function getImagePathImg(data: string) {
  const matchResult = data.match(/\/profile\/upload\/.*/);
  systemForm.imgUrl = matchResult ? baseUrl + matchResult[0] : data;
}

/** 关闭 */
function close() {
  (proxy as any).$tab.closePage();
}
</script>

<style lang="scss" scoped>
.label-wrap {
  display: flex;
  flex-direction: row;
  align-items: center;
  gap: 4px;
}

.field-wrap {
  width: 60%;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.field-input {
  width: 100%;
}

.field-tip {
  font-size: 12px;
  color: #909399;
  line-height: 1.4;
}
</style>
