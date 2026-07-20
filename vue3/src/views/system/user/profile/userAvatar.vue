<template>
  <div>
    <div class="user-info-head" @click="editCropper()">
      <img :src="options.img" :title="$t('user.resetPwd.450986-11')" class="img-circle img-lg" />
    </div>
    <el-dialog :title="title" v-model="open" width="800px" append-to-body @opened="modalOpened" @close="closeDialog">
      <el-row>
        <el-col :xs="24" :md="12" :style="{ height: '350px' }">
          <vue-cropper
            ref="cropperRef"
            :img="options.img"
            :info="true"
            :autoCrop="options.autoCrop"
            :autoCropWidth="options.autoCropWidth"
            :autoCropHeight="options.autoCropHeight"
            :fixedBox="options.fixedBox"
            :outputType="options.outputType"
            @realTime="realTime"
            v-if="visible"
          />
        </el-col>
        <el-col :xs="24" :md="12" :style="{ height: '350px' }">
          <div class="avatar-upload-preview">
            <img :src="previews.url" :style="previews.img" />
          </div>
        </el-col>
      </el-row>
      <br />
      <el-row>
        <el-col :lg="2" :sm="3" :xs="3">
          <el-upload action="#" :http-request="requestUpload" :show-file-list="false" :before-upload="beforeUpload">
            <el-button size="small">
              {{ $t('select') }}
              <el-icon class="el-icon--right"><Upload /></el-icon>
            </el-button>
          </el-upload>
        </el-col>
        <el-col :lg="{ span: 1, offset: 2 }" :sm="2" :xs="2">
          <el-button :icon="Plus" size="small" @click="changeScale(1)" />
        </el-col>
        <el-col :lg="{ span: 1, offset: 1 }" :sm="2" :xs="2">
          <el-button :icon="Minus" size="small" @click="changeScale(-1)" />
        </el-col>
        <el-col :lg="{ span: 1, offset: 1 }" :sm="2" :xs="2">
          <el-button :icon="RefreshLeft" size="small" @click="rotateLeft()" />
        </el-col>
        <el-col :lg="{ span: 1, offset: 1 }" :sm="2" :xs="2">
          <el-button :icon="RefreshRight" size="small" @click="rotateRight()" />
        </el-col>
        <el-col :lg="{ span: 2, offset: 6 }" :sm="2" :xs="2">
          <el-button type="primary" size="small" @click="uploadImg()">{{ $t('submit') }}</el-button>
        </el-col>
      </el-row>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, getCurrentInstance } from 'vue';
import { useI18n } from 'vue-i18n';
import { VueCropper } from 'vue-cropper';
import { uploadAvatar } from '@/api/system/user';
import { debounce } from '@/utils';
import { useUserStore } from '@/stores/modules/user';
import { Plus, Minus, RefreshLeft, RefreshRight, Upload } from '@element-plus/icons-vue';

const { t } = useI18n();
const { proxy } = getCurrentInstance()!;
const userStore = useUserStore();

const props = defineProps<{ user: any }>();

const open = ref(false);
const visible = ref(false);
const title = ref(t('user.resetPwd.450986-12'));
const cropperRef = ref<any>(null);
const previews = ref<any>({});
let resizeHandler: (() => void) | null = null;

const options = reactive({
  img: userStore.avatar,
  autoCrop: true,
  autoCropWidth: 200,
  autoCropHeight: 200,
  fixedBox: true,
  outputType: 'png' as const,
});

/** 编辑头像 */
function editCropper() {
  open.value = true;
}

/** 打开弹出层结束时的回调 */
function modalOpened() {
  visible.value = true;
  if (!resizeHandler) {
    resizeHandler = debounce(() => {
      refresh();
    }, 100);
  }
  window.addEventListener('resize', resizeHandler);
}

/** 刷新组件 */
function refresh() {
  cropperRef.value?.refresh();
}

/** 覆盖默认上传行为 */
function requestUpload() {
  return Promise.resolve();
}

/** 向左旋转 */
function rotateLeft() {
  cropperRef.value?.rotateLeft();
}

/** 向右旋转 */
function rotateRight() {
  cropperRef.value?.rotateRight();
}

/** 图片缩放 */
function changeScale(num: number) {
  num = num || 1;
  cropperRef.value?.changeScale(num);
}

/** 上传预处理 */
function beforeUpload(file: File) {
  if (file.type.indexOf('image/') === -1) {
    (proxy as any).$modal.msgError(t('user.resetPwd.450986-13'));
  } else {
    const reader = new FileReader();
    reader.readAsDataURL(file);
    reader.onload = () => {
      options.img = reader.result as string;
    };
  }
}

/** 上传图片 */
function uploadImg() {
  cropperRef.value?.getCropBlob((data: Blob) => {
    const formData = new FormData();
    formData.append('avatarfile', data);
    uploadAvatar(formData).then((response: any) => {
      open.value = false;
      options.img = import.meta.env.VITE_APP_BASE_API + response.imgUrl;
      userStore.avatar = options.img;
      (proxy as any).$modal.msgSuccess(t('updateSuccess'));
      visible.value = false;
    });
  });
}

/** 实时预览 */
function realTime(data: any) {
  previews.value = data;
}

/** 关闭窗口 */
function closeDialog() {
  options.img = userStore.avatar;
  visible.value = false;
  if (resizeHandler) {
    window.removeEventListener('resize', resizeHandler);
  }
}
</script>

<style scoped lang="scss">
.user-info-head {
  position: relative;
  display: inline-block;
  height: 110px;
}

.user-info-head:hover:after {
  content: '+';
  position: absolute;
  left: 0;
  right: 0;
  top: 0;
  bottom: 0;
  color: #eee;
  background: rgba(0, 0, 0, 0.5);
  font-size: 24px;
  font-style: normal;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  cursor: pointer;
  line-height: 110px;
  border-radius: 50%;
}
</style>
