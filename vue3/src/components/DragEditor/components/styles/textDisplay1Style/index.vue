<template>
  <section class="text-display-1-style">
    <el-form class="style-form" label-width="80px" :model="datas">
      <el-form-item :label="$t('components.DragEditor.rightslider.756540-35')" prop="name" class="block-item">
        <el-button plain @click="handleFeatureSelection">
          {{ $t('components.DragEditor.rightslider.756540-36') }}
        </el-button>
      </el-form-item>
      <el-form-item
        :label="$t('components.DragEditor.rightslider.756540-37')"
        prop="name"
        class="block-item attribute-item"
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
            </el-descriptions>
          </el-collapse-item>
        </el-collapse>
      </el-form-item>
      <el-form-item :label="$t('components.DragEditor.rightslider.756540-41')" class="slider-item">
        <el-slider v-model="datas.setStyle.marginTop" :max="300" :min="-16" input-size="small" show-input></el-slider>
      </el-form-item>
      <el-form-item :label="$t('components.DragEditor.rightslider.756540-42')" class="slider-item">
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
      <el-form-item :label="$t('components.DragEditor.rightslider.756540-44')" class="between">
        <el-color-picker
          v-model="datas.setStyle.statusColor"
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
    <attribute-selector :data="selectFeature" type="textDisplay1Style"></attribute-selector>
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

defineOptions({ name: 'TextDisplay1Style' });
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
.text-display-1-style {
  width: 100%;
  padding: 20px 5px;
  box-sizing: border-box;
  overflow-x: hidden;

  .style-form {
    display: flex;
    flex-direction: column;
    gap: 18px;
    width: 100%;
    min-width: 0;

    :deep(.el-form-item) {
      display: grid;
      grid-template-columns: 80px minmax(0, 1fr);
      align-items: center;
      width: 100%;
      margin-bottom: 0;
    }

    :deep(.el-form-item__label) {
      width: 85px !important;
      justify-content: flex-start;
      color: #4f5b67;
      text-align: left;
      padding: 0;
    }

    :deep(.el-form-item__content) {
      min-width: 0;
      margin-left: 0 !important;
    }
  }

  .slider-item {
    :deep(.el-form-item__content) {
      display: flex;
      min-width: 0;
    }

    :deep(.el-slider) {
      width: 100%;
      min-width: 0;
    }

    :deep(.el-slider__input) {
      width: 130px;
    }
  }

  .block-item {
    grid-template-columns: 1fr !important;

    :deep(.el-form-item__label) {
      display: block;
      width: 100% !important;
      height: 24px;
      line-height: 24px;
    }

    :deep(.el-form-item__content) {
      display: block;
      grid-column: 1 / -1;
      margin-top: 8px;
    }
  }

  .collapse-wrap {
    margin-left: 5px;
    border-top: 1px solid #e5e7eb;
    border-bottom: none;

    .icon {
      cursor: pointer;
    }

    :deep(.el-collapse-item__header) {
      height: 42px;
      color: #323233;
      border-bottom: 1px solid #e5e7eb;
    }

    :deep(.el-descriptions-item__cell) {
      font-size: 13px;
    }

    :deep(.el-collapse-item__content) {
      padding-bottom: 10px;
    }

    :deep(.el-collapse-item__wrap) {
      border-bottom: none;
    }
  }

  .between {
    :deep(.el-form-item__content) {
      display: flex;
      justify-content: flex-end;
      min-width: 0;
    }

    :deep(.el-form-item__label) {
      text-align: left;
    }
  }

  .left-height {
    :deep(.el-form-item__label) {
      text-align: left;
      width: 50px;
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
