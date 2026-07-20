<template>
  <section class="num-control-1-style">
    <el-form label-position="top" label-width="80px" :model="datas" size="small">
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
            <el-descriptions direction="vertical" :column="2" :colon="false" :size="`small`">
              <el-descriptions-item :label="$t('components.DragEditor.rightslider.756540-38')">
                <el-input
                  style="width: 80%"
                  v-model="item.templateName"
                  :placeholder="$t('components.DragEditor.rightslider.756540-39')"
                  size="small"
                ></el-input>
              </el-descriptions-item>
              <el-descriptions-item :label="$t('components.DragEditor.rightslider.756540-40')">
                <drag-icon class="icon" :name="item.icon" @click="handleSelectIcon(item)" />
              </el-descriptions-item>
              <el-descriptions-item :label="$t('components.DragEditor.rightslider.756540-49')">
                <el-input-number
                  style="width: 80%"
                  v-model="item.min"
                  controls-position="right"
                  :min="-999999"
                  :max="999999"
                  size="small"
                ></el-input-number>
              </el-descriptions-item>
              <el-descriptions-item :label="$t('components.DragEditor.rightslider.756540-50')">
                <el-input-number
                  style="width: 80%"
                  v-model="item.max"
                  controls-position="right"
                  :min="-999999"
                  :max="999999"
                  size="small"
                ></el-input-number>
              </el-descriptions-item>
            </el-descriptions>
          </el-collapse-item>
        </el-collapse>
      </el-form-item>
      <el-form-item :label="$t('components.DragEditor.rightslider.756540-41')" class="left-height">
        <el-slider v-model="datas.setStyle.marginTop" :max="300" :min="-16" input-size="small" show-input></el-slider>
      </el-form-item>
      <el-form-item :label="$t('components.DragEditor.rightslider.756540-42')" class="left-height">
        <el-slider
          v-model="datas.setStyle.marginBottom"
          :max="300"
          :min="-16"
          input-size="small"
          show-input
        ></el-slider>
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
      <el-form-item :label="$t('components.DragEditor.rightslider.756540-48')" class="between">
        <el-color-picker
          v-model="datas.setStyle.numColor"
          show-alpha
          class="picke"
          :predefine="predefineColors"
        ></el-color-picker>
      </el-form-item>
      <el-form-item :label="$t('components.DragEditor.rightslider.756540-45')" class="between">
        <el-switch v-model="datas.setStyle.showIcon"></el-switch>
      </el-form-item>
      <el-form-item :label="$t('components.DragEditor.rightslider.756540-46')" class="between">
        <el-color-picker
          v-model="datas.setStyle.iconColor"
          show-alpha
          class="picke"
          :predefine="predefineColors"
        ></el-color-picker>
      </el-form-item>
      <el-form-item :label="$t('components.DragEditor.rightslider.756540-47')" class="between">
        <el-color-picker
          style="float: right"
          v-model="datas.setStyle.iconBackColor"
          show-alpha
          class="picke"
          :predefine="predefineColors"
        ></el-color-picker>
      </el-form-item>
    </el-form>
    <attribute-selector :data="selectFeature" type="numControl1Style"></attribute-selector>
    <icon-panel
      v-if="icon !== ''"
      :data="selectIcon"
      :icon="icon"
      :templateId="templateId"
      @select="handleIconSelected"
    ></icon-panel>
  </section>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue';
import AttributeSelector from '../../../attributeSelector/index.vue';
import DragIcon from '@/components/DragEditor/DragIcon.vue';
import IconPanel from '../../../iconPanel/index.vue';
import { predefineColors } from '../../../constants';

defineOptions({ name: 'NumControl1Style' });
defineProps<{ datas: any }>();

const selectFeature = reactive({ show: false });
const selectIcon = reactive({ show: false });
const icon = ref('');
const templateId = ref<string | number | null>(null);
const currentIconTarget = ref<any>(null);

function handleFeatureSelection() {
  selectFeature.show = true;
}

function handleSelectIcon(item: any) {
  currentIconTarget.value = item;
  icon.value = item.icon || 'Edit';
  templateId.value = item.templateId;
  selectIcon.show = true;
}

function handleIconSelected(selectedIcon: string) {
  if (currentIconTarget.value) {
    currentIconTarget.value.icon = selectedIcon;
  }
  icon.value = selectedIcon;
}
</script>

<style lang="scss" scoped>
.num-control-1-style {
  width: 100%;
  padding: 20px 5px;
  box-sizing: border-box;

  .collapse-wrap {
    margin-left: 5px;
    .icon {
      cursor: pointer;
    }

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
