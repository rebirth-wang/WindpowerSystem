<template>
  <div class="license-wrap">
    <div class="logo-wrap">
      <img class="icon" src="@/assets/images/logo_blue.png" />
      <span class="text">{{ $t('license.index.654891-0') }}</span>
    </div>
    <pre class="introduce-text">{{ $t('license.index.654891-1') }}</pre>
    <div class="img-wrap">
      <img style="width: 100%; height: 100%" src="@/assets/images/cover.png" />
    </div>
    <div class="box-wrap">
      <div class="form-box">
        <div class="title-wrap">
          <div class="name">{{ $t('license.index.654891-2') }}</div>
        </div>
        <div class="msg-wrap">{{ $t('license.index.654891-3') }}{{ message }}</div>
        <el-form ref="formRef" :model="form" label-width="0px" :rules="rules">
          <el-form-item label="" prop="type">
            <el-select
              v-model="form.type"
              :placeholder="$t('license.index.654891-4')"
              style="width: 100%"
              :clearable="true"
            >
              <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
          <el-form-item label="" prop="company">
            <el-input v-model.trim="form.company" :placeholder="$t('license.index.654891-5')" style="width: 100%" />
          </el-form-item>
          <el-form-item prop="fileName">
            <div class="file-item-wrap">
              <el-input v-model="form.subject" :placeholder="$t('license.index.654891-6')" :disabled="!isDisabled" />
              <el-upload
                :action="uploadImgUrl"
                :on-change="handleChange"
                :on-success="handleSuccess"
                :on-error="handleError"
                :show-file-list="false"
                :before-upload="handleBeforeUpload"
                accept=".lic"
              >
                <el-button class="input-append" :icon="Upload">{{ $t('license.index.654891-7') }}</el-button>
              </el-upload>
            </div>
          </el-form-item>
          <el-button type="primary" style="width: 100%; margin-top: 26px" @click="startInstallation" :loading="loading">
            {{ btnMsg }}
          </el-button>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useI18n } from 'vue-i18n';
import { Upload } from '@element-plus/icons-vue';
import { installLicense, getLicenseInfo } from '@/api/license';
import type { FormInstance } from 'element-plus';

const router = useRouter();
const { t } = useI18n();

const formRef = ref<FormInstance>();
const fileType = ['lic'];
const message = ref(t('license.index.654891-8'));
const btnMsg = ref(t('license.index.654891-9'));
const loading = ref(false);
const uploadImgUrl = import.meta.env.VITE_APP_BASE_API + '/license/upload';
const isDisabled = ref(false);

const options = [
  { value: 2, label: t('license.index.654891-10') },
  { value: 3, label: t('license.index.654891-11') },
  { value: 4, label: t('license.index.654891-12') },
];

const form = reactive({
  subject: '',
  type: 2,
  company: '',
  fileName: '',
  accessIp: '',
});

const rules = reactive({
  fileName: [{ required: true, message: t('license.index.654891-13'), trigger: 'blur' }],
  type: [{ required: true, message: t('license.index.654891-4'), trigger: 'blur' }],
  company: [{ required: true, message: t('license.index.654891-5'), trigger: 'blur' }],
  subject: [{ required: true, message: t('license.index.654891-6'), trigger: 'blur' }],
});

function getLicense() {
  getLicenseInfo()
    .then((res: any) => {
      if (res.code === 200 && res.data === true) {
        router.push('/login');
      }
    })
    .catch(() => {});
}

function handleBeforeUpload(file: any) {
  if (fileType.length) {
    const fileName = file.name.split('.');
    const fileExt = fileName[fileName.length - 1];
    const isTypeOk = fileType.indexOf(fileExt) >= 0;
    if (!isTypeOk) {
      message.value = t('license.index.654891-14');
      return false;
    }
    return true;
  }
}

function handleChange(file: any) {
  form.fileName = file.name;
  form.subject = extractCoreName(form.fileName);
  isDisabled.value = true;
}

function handleSuccess(response: any) {
  if (response.code === 200) {
    message.value = t('license.index.654891-15');
  } else {
    handleError(response);
  }
}

function handleError(error: any) {
  message.value = error.msg || t('license.index.654891-16');
}

function extractCoreName(name: string) {
  return name.replace(/-license.*/, '');
}

function startInstallation() {
  formRef.value?.validate((valid: boolean) => {
    if (valid) {
      loading.value = true;
      btnMsg.value = t('license.index.654891-17');
      form.accessIp = window.location.href;
      installLicense(form)
        .then((response: any) => {
          if (response.code === 200) {
            message.value = response.msg || t('license.index.654891-18');
            router.push('/login');
          } else {
            message.value = response.msg || t('license.index.654891-19');
          }
          loading.value = false;
          btnMsg.value = t('license.index.654891-9');
        })
        .catch((error: any) => {
          message.value = error.message || t('license.index.654891-20');
          loading.value = false;
          btnMsg.value = t('license.index.654891-9');
        });
    }
  });
}

onMounted(() => {
  getLicense();
});
</script>

<style lang="scss" scoped>
.license-wrap {
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
  }

  .img-wrap {
    flex: 1;
    background: #0f73ee;
  }

  .msg-wrap {
    padding: 8px;
    font-size: 12px;
    color: #486ff2;
    line-height: 18px;
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
      width: 320px;

      .title-wrap {
        display: flex;
        justify-content: center;
        align-items: center;
        margin-bottom: 30px;

        .name {
          font-weight: 600;
          font-size: 24px;
          color: #303133;
        }

        .lang {
          cursor: pointer;

          :deep(.el-dropdown) {
            font-weight: 400;
            font-size: 14px;
            color: #909399;
          }
        }
      }
    }
  }
}

.file-item-wrap {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  align-items: center;

  .input-append {
    width: 112px;
    height: 36px;
    margin-left: 8px;
    border-radius: 4px;
    cursor: pointer;
  }
}

@media screen and (min-width: 1920px) {
  .license-wrap .box-wrap {
    width: 811px;
  }
}

@media screen and (max-width: 1180px) {
  .license-wrap .box-wrap {
    width: 498px;

    .form-box {
      width: 246px;
    }
  }

  .license-wrap .logo-wrap {
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

  .license-wrap .introduce-text {
    font-size: 11px;
    top: 120px;
    left: 66px;
  }
}

@media screen and (max-width: 968px) {
  .license-wrap .img-wrap {
    display: none;
  }

  .license-wrap .logo-wrap {
    display: none;
  }

  .license-wrap .introduce-text {
    display: none;
  }

  .license-wrap .box-wrap {
    width: 100%;
  }
}

:deep(.el-input--medium .el-input__inner) {
  background: none;
}
</style>
