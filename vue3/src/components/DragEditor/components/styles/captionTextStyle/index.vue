<template>
  <section class="caption-text-style">
    <el-form label-width="80px" :model="datas">
      <el-form-item :label="$t('components.DragEditor.rightslider.756540-19')">
        <el-input
          v-model="datas.setStyle.name"
          :placeholder="$t('components.DragEditor.rightslider.756540-20')"
          show-word-limit
        />
      </el-form-item>
      <el-form-item :label="$t('components.DragEditor.rightslider.756540-21')">
        <el-input
          type="textarea"
          v-model="datas.setStyle.description"
          :placeholder="$t('components.DragEditor.rightslider.756540-22')"
          maxlength="100"
        />
      </el-form-item>
      <el-form-item :label="$t('components.DragEditor.rightslider.756540-23')">
        <div class="weiz">
          <i
            :class="datas.setStyle.positions === 'left' ? 'active' : ''"
            class="iconfont icon-horizontal-left"
            @click="datas.setStyle.positions = 'left'"
          />
          <i
            :class="datas.setStyle.positions === 'center' ? 'active' : ''"
            class="iconfont icon-juzhong"
            @click="datas.setStyle.positions = 'center'"
          />
          <i
            :class="datas.setStyle.positions === 'right' ? 'active' : ''"
            class="iconfont icon-juyou"
            @click="datas.setStyle.positions = 'right'"
          />
        </div>
      </el-form-item>
      <el-form-item :label="$t('components.DragEditor.rightslider.756540-24')" prop="wordSize" class="left">
        <el-input
          type="number"
          v-model.number="datas.setStyle.wordSize"
          :placeholder="$t('components.DragEditor.rightslider.756540-25')"
          :maxlength="2"
        />
      </el-form-item>
      <el-form-item :label="$t('components.DragEditor.rightslider.756540-26')" prop="descriptionSize" class="left">
        <el-input
          type="number"
          v-model.number="datas.setStyle.descriptionSize"
          :placeholder="$t('components.DragEditor.rightslider.756540-27')"
        />
      </el-form-item>
      <el-form-item :label="$t('components.DragEditor.rightslider.756540-28')" prop="wordWeight" class="left">
        <el-input
          type="number"
          v-model.number="datas.setStyle.wordWeight"
          :placeholder="$t('components.DragEditor.rightslider.756540-29')"
        />
      </el-form-item>
      <el-form-item :label="$t('components.DragEditor.rightslider.756540-30')" prop="descriptionWeight" class="left">
        <el-input
          type="number"
          v-model.number="datas.setStyle.descriptionWeight"
          :placeholder="$t('components.DragEditor.rightslider.756540-31')"
        />
      </el-form-item>
      <el-form-item :label="$t('components.DragEditor.rightslider.756540-32')" class="left-height">
        <el-slider v-model="datas.setStyle.wordHeight" :max="100" :min="20" input-size="small" show-input></el-slider>
      </el-form-item>
      <el-form-item :label="$t('components.DragEditor.rightslider.756540-41')" class="left-height">
        <el-slider v-model="datas.setStyle.marginTop" :max="300" :min="-16" input-size="small" show-input></el-slider>
      </el-form-item>
      <el-form-item :label="$t('components.DragEditor.rightslider.756540-42')" class="left-height">
        <el-slider v-model="datas.setStyle.marginBottom" :max="300" :min="-16" input-size="small" show-input></el-slider>
      </el-form-item>
      <el-form-item :label="$t('components.DragEditor.rightslider.756540-33')" class="between">
        <el-color-picker
          v-model="datas.setStyle.wordColor"
          show-alpha
          class="picke"
          :predefine="predefineColors"
        ></el-color-picker>
      </el-form-item>
      <el-form-item :label="$t('components.DragEditor.rightslider.756540-34')" class="between">
        <el-color-picker
          v-model="datas.setStyle.descriptionColor"
          show-alpha
          class="picke"
          :predefine="predefineColors"
        ></el-color-picker>
      </el-form-item>
      <el-form-item :label="$t('components.DragEditor.rightslider.756540-12')" class="between">
        <el-color-picker
          v-model="datas.setStyle.backColor"
          show-alpha
          class="picke"
          :predefine="predefineColors"
        ></el-color-picker>
      </el-form-item>
      <!-- <el-form-item label="底部分割线" class="between">
                {{ datas.setStyle.borderBott ? '显示' : '隐藏' }}
                <el-checkbox v-model="datas.setStyle.borderBott" />
            </el-form-item>
            <el-form-item label="查看更多" class="between">
                {{ datas.setStyle.more.show ? '显示' : '隐藏' }}
                <el-checkbox v-model="datas.setStyle.more.show" />
            </el-form-item> -->
      <!-- 更多样式选择 -->
    </el-form>
  </section>
</template>

<script setup lang="ts">
import { predefineColors } from '../../../constants';

defineOptions({ name: 'captionTextStyle' });
defineProps<{ datas: any }>();
</script>

<style lang="scss" scoped>
.caption-text-style {
  width: 100%;
  padding: 20px 5px;
  box-sizing: border-box;

  .weiz {
    i {
      padding: 5px 14px;
      margin-right: 10px;
      border-radius: 0;
      border: 1px solid #ebedf0;
      font-size: 20px;
      font-weight: 500;
      cursor: pointer;

      &:last-child {
        font-size: 22px;
      }

      &.active {
        color: #155bd4;
        border: 1px solid #155bd4;
        background: #e0edff;
      }
    }
  }

  .left {
    :deep(.el-form-item__label) {
      text-align: left;
      width: 80px;
      float: left;
      padding: 0;
    }

    :deep(.el-form-item__content) {
      margin-left: 80px;
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

  .between {
    :deep(.el-form-item__label) {
      text-align: left;
    }

    :deep(.el-form-item__content) {
      text-align: right;
      float: right;
    }
  }
}
</style>
