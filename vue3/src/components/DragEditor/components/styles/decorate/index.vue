<template>
  <div class="decorate" v-if="datas">
    <el-form label-position="top" label-width="80px" :model="datas" :rules="rules">
      <el-form-item
        :label="$t('components.DragEditor.rightslider.756540-2')"
        :hide-required-asterisk="true"
        prop="name"
      >
        <el-input
          v-model="datas.name"
          :placeholder="$t('components.DragEditor.rightslider.756540-3')"
          maxlength="25"
          show-word-limit
        />
      </el-form-item>
      <el-form-item
        :label="$t('components.DragEditor.rightslider.756540-5')"
        :hide-required-asterisk="true"
        prop="details"
      >
        <el-input v-model="datas.details" :placeholder="$t('components.DragEditor.rightslider.756540-6')" />
      </el-form-item>
      <el-form-item :label="$t('components.DragEditor.rightslider.756540-8')" class="lef">
        {{
          datas.isBack
            ? $t('components.DragEditor.rightslider.756540-9')
            : $t('components.DragEditor.rightslider.756540-10')
        }}
        <el-checkbox style="margin-left: 196px" v-model="datas.isBack" />
      </el-form-item>
      <el-form-item :label="$t('components.DragEditor.rightslider.756540-11')" class="left-height">
        <el-slider v-model="datas.titleHeight" :max="100" :min="35" show-input></el-slider>
      </el-form-item>
      <el-form-item :label="$t('components.DragEditor.rightslider.756540-12')">
        <el-radio-group v-model="colourAction">
          <el-radio :label="1">{{ $t('components.DragEditor.rightslider.756540-13') }}</el-radio>
          <el-radio :label="2">{{ $t('components.DragEditor.rightslider.756540-14') }}</el-radio>
        </el-radio-group>
        <el-color-picker
          v-model="datas.bgColor"
          show-alpha
          class="picke"
          v-if="pickeShow"
          :predefine="predefineColors"
        ></el-color-picker>
      </el-form-item>
      <el-form-item :label="$t('components.DragEditor.rightslider.756540-15')">
        <div class="shop-head-pic" style="text-align: center">
          <img class="home-bg" :src="datas.bgImg" alt="" v-if="datas.bgImg" />
          <div class="shop-head-pic-btn" style="text-align: center">
            <el-button @click="showUpload('2')" class="uploadImg" type="primary" plain>
              <el-icon>
                <Plus />
              </el-icon>
              {{
                datas.bgImg === ''
                  ? $t('components.DragEditor.rightslider.756540-16')
                  : $t('components.DragEditor.rightslider.756540-17')
              }}
            </el-button>
            <el-button plain @click="clear()">
              <el-icon>
                <Delete />
              </el-icon>
              {{ $t('components.DragEditor.rightslider.756540-18') }}
            </el-button>
          </div>
        </div>
      </el-form-item>
    </el-form>

    <!-- 上传图片 -->
    <Uploadimg ref="upload" @uploadInformation="uploadInformation" />
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, watch } from 'vue';
import { useI18n } from 'vue-i18n';
import { Delete, Plus } from '@element-plus/icons-vue';
import { useDragEditorStore } from '@/stores/modules/dragEditor';
import Uploadimg from '../../../uploadImg.vue';
import { predefineColors } from '../../../constants';

const props = withDefaults(defineProps<{ datas: any }>(), {
  datas: () => ({}),
});

defineOptions({ name: 'Decorate' });

const { t } = useI18n();
const dragStore = useDragEditorStore();
const upload = ref<InstanceType<typeof Uploadimg> | null>(null);
const colourAction = ref(1);
const pickeShow = ref(false);
const uploadImgDataType = ref<string | null>(null);
const rules = reactive({
  name: [{ required: true, message: t('components.DragEditor.rightslider.756540-4'), trigger: 'blur' }],
  details: [{ required: true, message: t('components.DragEditor.rightslider.756540-7'), trigger: 'blur' }],
});

function showUpload(type: string) {
  uploadImgDataType.value = type;
  upload.value?.showUpload();
}

function uploadInformation(res: string) {
  if (uploadImgDataType.value === '2') {
    props.datas.bgImg = res;
  }
}

function clear() {
  props.datas.bgImg = '';
  dragStore.setPageSetup({ ...dragStore.pageSetup, bgImg: '' });
}

watch(colourAction, (data) => {
  if (data === 1) {
    props.datas.bgColor = 'rgba(241,243,249,1)';
    pickeShow.value = false;
    return;
  } else {
    pickeShow.value = true;
  }
});
</script>

<style lang="scss" scoped>
.decorate {
  width: 100%;
  height: 100%;
  padding: 0 10px;
  box-sizing: border-box;
  padding: 20px 5px;

  h2 {
    padding: 24px 16px 24px 0;
    margin-bottom: 15px;
    border-bottom: 1px solid #f2f4f6;
    font-size: 18px;
    font-weight: 600;
    color: #323233;
  }

  .ification {
    color: #155bd4;
    font-size: 14px;
    padding: 0 15px;
    cursor: pointer;
  }

  .picke {
    margin-left: 15px;
    vertical-align: top;
  }

  .home-bg {
    width: 100px;
    margin: 10px auto;
  }

  .lef {
    display: flex;

    :deep(.el-form-item__label) {
      text-align: left;
      margin-right: 16px;
      margin-top: 8px;
    }
  }

  .left-height {
    :deep(.el-form-item__label) {
      text-align: left;
      width: 80px;
      float: left;
      margin-top: 5px;
    }

    :deep(.el-form-item__content) {
      margin-left: 80px;
    }
  }

  // 底部logo
  .bottomLogo {
    display: flex;
    flex-direction: column;

    img {
      display: block;
      width: 220px;
      margin: 10px auto;
    }
  }

  // 店铺信息修改
  .shop-info {
    .shop-name {
      display: flex;
      flex-direction: row;
      color: #ababab;

      .el-input {
        flex: 1;
      }
    }

    .shop-head-pic {
      color: #ababab;
      display: flex;
      flex-direction: column;

      img {
        width: 70px;
        height: 70px;
        margin: 10px auto;
      }

      .shop-head-pic-btn {
        display: flex;
        flex-direction: row;

        .el-button {
          flex: 1;
        }
      }
    }
  }
}
</style>
