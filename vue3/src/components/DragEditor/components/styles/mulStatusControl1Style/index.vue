<template>
  <section class="mul-status-control-1-style">
    <el-form label-width="80px" :model="datas" size="small">
      <el-form-item :label="$t('components.DragEditor.rightslider.756540-35')" prop="name">
        <el-button plain @click="handleFeatureSelection">
          {{ $t('components.DragEditor.rightslider.756540-36') }}
        </el-button>
      </el-form-item>
      <el-form-item
        :label="$t('components.DragEditor.rightslider.756540-37')"
        prop="name"
        v-if="datas.setConfig.attributes.length !== 0"
      >
        <el-collapse class="collapse-wrap">
          <el-collapse-item
            v-for="item in datas.setConfig.attributes"
            :key="item.templateId"
            :title="item.templateName"
            :name="item.templateId"
          >
            <el-descriptions direction="vertical" :column="1" :colon="false" :size="`small`">
              <el-descriptions-item :label="$t('components.DragEditor.rightslider.756540-38')">
                <el-input
                  style="width: 93%"
                  v-model="item.templateName"
                  :placeholder="$t('components.DragEditor.rightslider.756540-39')"
                  size="small"
                ></el-input>
              </el-descriptions-item>
            </el-descriptions>
          </el-collapse-item>
        </el-collapse>
      </el-form-item>
      <el-form-item :label="$t('components.DragEditor.rightslider.756540-41')" class="left-height">
        <el-slider v-model="datas.setStyle.marginTop" :max="300" :min="-16" input-size="small" show-input></el-slider>
      </el-form-item>
      <el-form-item :label="$t('components.DragEditor.rightslider.756540-42')" class="left-height">
        <el-slider v-model="datas.setStyle.marginBottom" :max="300" :min="-16" input-size="small" show-input></el-slider>
      </el-form-item>
      <el-form-item :label="$t('components.DragEditor.rightslider.756540-51')">
        <el-radio-group v-model="datas.setStyle.rowNum">
          <el-radio :label="1">{{ $t('components.DragEditor.rightslider.756540-52') }}</el-radio>
          <el-radio :label="2">{{ $t('components.DragEditor.rightslider.756540-53') }}</el-radio>
          <el-radio :label="3">{{ $t('components.DragEditor.rightslider.756540-54') }}</el-radio>
          <el-radio :label="4">{{ $t('components.DragEditor.rightslider.756540-55') }}</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item :label="$t('components.DragEditor.rightslider.756540-56')">
        <el-slider v-model="datas.setStyle.fontSize" :max="30" :min="12"></el-slider>
      </el-form-item>
      <el-form-item :label="$t('components.DragEditor.rightslider.756540-57')">
        <el-slider v-model="datas.setStyle.figureHeight" :max="40" :min="20"></el-slider>
      </el-form-item>
      <el-form-item :label="$t('components.DragEditor.rightslider.756540-58')">
        <el-slider v-model="datas.setStyle.borderRadius" :max="20" :min="0" :step="0.5"></el-slider>
      </el-form-item>
      <el-form-item :label="$t('components.DragEditor.rightslider.756540-12')" class="between">
        <el-color-picker
          v-model="datas.setStyle.backColor"
          show-alpha
          class="picke"
          :predefine="predefineColors"
        ></el-color-picker>
      </el-form-item>
      <el-form-item :label="$t('components.DragEditor.rightslider.756540-43')" class="between">
        <el-color-picker
          v-model="datas.setStyle.nameColor"
          show-alpha
          class="picke"
          :predefine="predefineColors"
        ></el-color-picker>
      </el-form-item>
      <el-form-item :label="$t('components.DragEditor.rightslider.756540-59')" class="between">
        <el-color-picker
          v-model="datas.setStyle.noColor"
          show-alpha
          class="picke"
          :predefine="predefineColors"
        ></el-color-picker>
      </el-form-item>
      <el-form-item :label="$t('components.DragEditor.rightslider.756540-60')" class="between">
        <el-color-picker
          v-model="datas.setStyle.offColor"
          show-alpha
          class="picke"
          :predefine="predefineColors"
        ></el-color-picker>
      </el-form-item>
      <el-form-item :label="$t('components.DragEditor.rightslider.756540-61')" class="between">
        <el-color-picker
          v-model="datas.setStyle.noBackColor"
          show-alpha
          class="picke"
          :predefine="predefineColors"
        ></el-color-picker>
      </el-form-item>
      <el-form-item :label="$t('components.DragEditor.rightslider.756540-62')" class="between">
        <el-color-picker
          v-model="datas.setStyle.offBackColor"
          show-alpha
          class="picke"
          :predefine="predefineColors"
        ></el-color-picker>
      </el-form-item>
    </el-form>
    <attribute-selector :data="selectFeature" type="mulStatusControl1Style"></attribute-selector>
  </section>
</template>

<script setup lang="ts">
import { reactive } from 'vue';
import AttributeSelector from '../../../attributeSelector/index.vue';
import { predefineColors } from '../../../constants';

defineOptions({ name: 'MulStatusControl1Style' });
defineProps<{ datas: any }>();

const selectFeature = reactive({ show: false });
function handleFeatureSelection() {
  selectFeature.show = true;
}
</script>

<style lang="scss" scoped>
.mul-status-control-1-style {
  width: 100%;
  padding: 20px 5px;
  box-sizing: border-box;

  .collapse-wrap {
    margin-left: 5px;

    :deep(.el-collapse-item__header) {
      height: 42px;
      color: #606266;
    }

    :deep(.el-descriptions-item__cell) {
      font-size: 13px;
    }

    :deep(.el-collapse-item__content) {
      padding-bottom: 10px;
    }
  }

  .between {
    :deep(.el-form-item__label) {
      text-align: left;
    }
    :deep(.el-form-item__content) {
      text-align: right;
      float: right;
    }
  }

  .left-height {
    :deep(.el-form-item__label) {
      text-align: left;
      width: 80px;
      float: left;
      padding: 0;
      line-height: 38px;
    }

    :deep(.el-form-item__content) {
      margin-left: 80px;
    }
  }
}
</style>
